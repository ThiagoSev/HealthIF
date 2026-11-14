package com.example.wearos.healthif.data.sync

import android.content.Context
import com.example.wearos.healthif.data.model.HealthReadings
import com.google.android.gms.wearable.Wearable
import org.json.JSONObject

class PhoneSyncDataSource(context: Context) {
    private val appContext = context.applicationContext

    fun syncHealthReadings(readings: HealthReadings, onStatus: (String) -> Unit) {
        val payload = JSONObject()
            .put("steps", readings.steps ?: JSONObject.NULL)
            .put("calories", readings.calories ?: JSONObject.NULL)
            .put("distanceMeters", readings.distanceMeters ?: JSONObject.NULL)
            .put("heartRate", readings.heartRate ?: JSONObject.NULL)
            .put("collectedAt", System.currentTimeMillis())
            .toString()
            .toByteArray(Charsets.UTF_8)

        sendToConnectedPhones(SYNC_PATH, payload, "Dados sincronizados", onStatus)
    }

    fun findPhone(onStatus: (String) -> Unit) {
        sendToConnectedPhones(
            FIND_PHONE_PATH,
            byteArrayOf(),
            "Sinal enviado ao telefone",
            onStatus,
        )
    }

    private fun sendToConnectedPhones(
        path: String,
        payload: ByteArray,
        successMessage: String,
        onStatus: (String) -> Unit,
    ) {
        onStatus("Conectando ao telefone...")
        Wearable.getNodeClient(appContext).connectedNodes
            .addOnSuccessListener { nodes ->
                if (nodes.isEmpty()) {
                    onStatus("Nenhum telefone conectado")
                    return@addOnSuccessListener
                }

                var remaining = nodes.size
                var anySuccess = false
                nodes.forEach { node ->
                    Wearable.getMessageClient(appContext)
                        .sendMessage(node.id, path, payload)
                        .addOnCompleteListener { task ->
                            anySuccess = anySuccess || task.isSuccessful
                            remaining--
                            if (remaining == 0) {
                                onStatus(
                                    if (anySuccess) successMessage
                                    else "Falha ao enviar para o telefone",
                                )
                            }
                        }
                }
            }
            .addOnFailureListener {
                onStatus("Não foi possível localizar o telefone")
            }
    }

    private companion object {
        const val SYNC_PATH = "/healthif/sync"
        const val FIND_PHONE_PATH = "/healthif/find_phone"
    }
}
