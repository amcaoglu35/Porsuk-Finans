package com.nexus.porsuk.ui.common

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import java.util.Locale

class VoiceInputManager(private val context: Context) {
    private var speechRecognizer: SpeechRecognizer? = null
    private val recognizerIntent: Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "tr-TR")
        putExtra(RecognizerIntent.EXTRA_PROMPT, "Dinliyorum...")
    }

    fun startListening(
        onResult: (String) -> Unit,
        onError: (String) -> Unit = {},
        onStateChange: (Boolean) -> Unit = {}
    ) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onError("Ses tanıma bu cihazda kullanılamıyor.")
            return
        }

        stopListening() // Reset previous instance

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    onStateChange(true)
                }

                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {
                    onStateChange(false)
                }

                override fun onError(error: Int) {
                    val message = when (error) {
                        SpeechRecognizer.ERROR_AUDIO -> "Ses hatası."
                        SpeechRecognizer.ERROR_CLIENT -> "İstemci hatası."
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Yetersiz izin."
                        SpeechRecognizer.ERROR_NETWORK -> "Ağ hatası."
                        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Ağ zaman aşımı."
                        SpeechRecognizer.ERROR_NO_MATCH -> "Eşleşme bulunamadı."
                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Servis meşgul."
                        SpeechRecognizer.ERROR_SERVER -> "Sunucu hatası."
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Ses girişi zaman aşımı."
                        else -> "Bilinmeyen hata."
                    }
                    onError(message)
                    onStateChange(false)
                }

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    onResult(matches?.firstOrNull() ?: "")
                    onStateChange(false)
                }

                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
            startListening(recognizerIntent)
        }
    }

    fun stopListening() {
        speechRecognizer?.apply {
            stopListening()
            cancel()
            destroy()
        }
        speechRecognizer = null
    }
}
