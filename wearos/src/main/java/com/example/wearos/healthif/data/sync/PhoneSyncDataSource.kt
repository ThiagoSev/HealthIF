package com.example.wearos.healthif.data.sync

import android.content.Context
import com.example.wearos.healthif.data.model.HealthReadings
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable

class PhoneSyncDataSource(context: Context) {
    private val appContext = context.applicationContext

    fun syncHealthReadings(readings: HealthReadings, onStatus: (String) -> Unit) {
        onStatus("Preparando sincronização...")
        sendHealthReadings(readings)
            .addOnSuccessListener {
                onStatus("Dados disponibilizados para sincronização")
            }
            .addOnFailureListener {
                onStatus("Falha ao disponibilizar dados para sincronização")
            }
    }

    private fun sendHealthReadings(readings: HealthReadings): Task<DataItem> {
        val request = PutDataMapRequest.create(SYNC_PATH).run {
            readings.steps?.let { dataMap.putLong("steps", it) }
            readings.calories?.let { dataMap.putInt("calories", it) }
            readings.distanceMeters?.let { dataMap.putDouble("distanceMeters", it) }
            readings.heartRate?.let { dataMap.putInt("heartRate", it) }

            dataMap.putLong("collectedAt", System.currentTimeMillis())

            asPutDataRequest().setUrgent()
        }

        return Wearable.getDataClient(appContext).putDataItem(request)
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
