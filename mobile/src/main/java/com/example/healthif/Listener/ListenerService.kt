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
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

class ListenerService : WearableListenerService() {

    override fun onMessageReceived(event: MessageEvent) {
        when (event.path) {
            "/healthif/find_phone" -> alertFindPhone()
        }
    }

    override fun onDataChanged(events: DataEventBuffer) {
        for (event in events) {
            if (event.type == DataEvent.TYPE_CHANGED &&
                event.dataItem.uri.path == "/healthif/sync"
            ) {
                try {
                    saveHealthSnapshot(DataMapItem.fromDataItem(event.dataItem).dataMap)
                } catch (error: Exception) {
                    Log.e("WEAR", "Não foi possível processar o snapshot de saúde", error)
                }
            }
        }
    }

    private fun saveHealthSnapshot(snapshot: DataMap) {
        val steps = snapshot.optionalLong("steps")
        val calories = snapshot.optionalInt("calories")
        val distanceMeters = snapshot.optionalDouble("distanceMeters")
        val heartRate = snapshot.optionalInt("heartRate")
        val collectedAt = snapshot.getLong("collectedAt", 0L)
        val measuredAt = snapshot.getLong("measuredAt", collectedAt)

        if (steps?.let { it < 0 } == true ||
            calories?.let { it < 0 } == true ||
            distanceMeters?.let { !it.isFinite() || it < 0 } == true ||
            heartRate?.let { it !in 1..300 } == true ||
            collectedAt <= 0
        ) {
            Log.w("WEAR", "Snapshot de saúde fora dos limites")
            return
        }
        val dailyActivity = DailyActivity(
            LocalDate.now().toEpochDay(),
            steps = steps?.toInt(),
            calories = calories,
            distance = distanceMeters?.toInt(),
            collectedAt,
        )

        runBlocking {
            (applicationContext as MeuApp).dailyActivityRepository
                .adicionarAtividadeDiaria(dailyActivity)
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
    }

    private fun DataMap.optionalLong(name: String): Long? =
        if (containsKey(name)) getLong(name) else null

    private fun DataMap.optionalInt(name: String): Int? =
        if (containsKey(name)) getInt(name) else null

    private fun DataMap.optionalDouble(name: String): Double? =
        if (containsKey(name)) getDouble(name) else null

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
