package com.example.healthif.listener

import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.example.healthif.MeuApp
import com.example.healthif.data.Entities.DailyActivity
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.runBlocking
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDate

class ListenerService : WearableListenerService() {

    override fun onMessageReceived(event: MessageEvent) {
        when (event.path) {
            "/healthif/find_phone" -> alertFindPhone()
            "/healthif/sync" -> saveHealthSnapshot(event.data.toString(Charsets.UTF_8))
        }
    }

    private fun saveHealthSnapshot(payload: String) {
        try {
            val snapshot = JSONObject(payload)
            val steps = snapshot.optionalLong("steps")
            val calories = snapshot.optionalInt("calories")
            val distanceMeters = snapshot.optionalDouble("distanceMeters")
            val heartRate = snapshot.optionalInt("heartRate")
            val collectedAt = snapshot.optLong("collectedAt", 0L)
            val measuredAt = snapshot.optLong("measuredAt", 0L)
            val receivedAt = snapshot.optLong("receivedAt", 0L)

            if (steps?.let { it < 0 } == true ||
                calories?.let { it < 0 } == true ||
                distanceMeters?.let { !it.isFinite() || it < 0 } == true ||
                heartRate?.let { it !in 1..300 } == true ||
                collectedAt <= 0
            ) {
                Log.w("WEAR", "Snapshot de saúde fora dos limites")
            }
            else{
                val dailyActivity = DailyActivity(
                    LocalDate.now().toEpochDay(),
                    steps = steps?.toInt(),
                    calories = calories,
                    distance = distanceMeters?.toInt(),
                    collectedAt)

                runBlocking {
                    (applicationContext as MeuApp).dailyActivityRepository
                        .adicionarAtividadeDiaria(dailyActivity)
                }

            }

            if (heartRate != null) {
                runBlocking {
                    try {
                        (applicationContext as MeuApp).batimentosRepository
                            .adicionarBatimentos(
                                bpm = heartRate,
                                measuredAt = measuredAt
                            )

                    } catch (error: Exception) {
                        Log.e("WEAR", "Snapshot recebido, mas não foi possível salvar o BPM", error)
                    }
                }
            } else {
                Log.d("WEAR", "Snapshot de saúde recebido")
            }
        } catch (error: JSONException) {
            Log.e("WEAR", "Snapshot de saúde inválido", error)
        }
    }

    private fun JSONObject.optionalLong(name: String): Long? =
        if (!has(name) || isNull(name)) null else getLong(name)

    private fun JSONObject.optionalInt(name: String): Int? =
        if (!has(name) || isNull(name)) null else getInt(name)

    private fun JSONObject.optionalDouble(name: String): Double? =
        if (!has(name) || isNull(name)) null else getDouble(name)

    private fun alertFindPhone() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSystemService(VibratorManager::class.java).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Vibrator::class.java)
        }
        vibrator?.vibrate(
            VibrationEffect.createWaveform(longArrayOf(0, 500, 300, 500, 300, 900), 0)
        )

        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        val ringtone = alarmUri?.let { RingtoneManager.getRingtone(this, it) }
        if (ringtone == null) {
            Log.w("WEAR", "Nenhum toque padrão disponível no telefone")
        } else {
            ringtone.audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) ringtone.isLooping = true
            ringtone.play()
        }
        Handler(Looper.getMainLooper()).postDelayed({
            if (ringtone?.isPlaying == true) ringtone.stop()
            vibrator?.cancel()
        }, 10_000)
    }
}