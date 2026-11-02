package com.example.healthif.Listener

import android.util.Log
import com.example.healthif.MeuApp
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.runBlocking

//1- O relógio envia uma mensagem contendo o BPM.
//2- O onMessageReceived() recebe e valida o valor.
//3- O listener obtém a instância compartilhada do Repository.
//4- O runBlocking permite executar repository.adicionarBatimentos(bpm).
//5- O callback aguarda a conclusão da inserção no Room.
//6- O Flow do Room notifica o ViewModel, que atualiza a interface.

class ListenerService : WearableListenerService() {

    override fun onMessageReceived(event: MessageEvent) {
        if (event.path != "/batimentos") return

        val bpm = event.data.toString(Charsets.UTF_8)
            .toIntOrNull() ?: return

        if (bpm !in 1..300) return

        val repository =
            (applicationContext as MeuApp).batimentosRepository

        //runBlocking aguarda a coroutine terminar e
        // mantém o callback em execução durante a inserção
        runBlocking {
            try{
                repository.adicionarBatimentos(bpm)

                Log.d(
                    "WEAR",
                    "Batimento salvo: $bpm BPM"
                )
            } catch(e: Exception){
                Log.e(
                    "WEAR",
                    "Erro ao salvar batimento",
                    e
                )
            }
        }
    }
}