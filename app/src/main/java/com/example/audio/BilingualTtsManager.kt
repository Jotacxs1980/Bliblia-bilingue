package com.example.audio

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.data.model.AudioVoiceMode
import com.example.data.model.BibleVerse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class BilingualTtsManager(context: Context) : TextToSpeech.OnInitListener {

    private val TAG = "BilingualTtsManager"
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentVerseIndex = MutableStateFlow(-1)
    val currentVerseIndex: StateFlow<Int> = _currentVerseIndex.asStateFlow()

    private var currentPlaylist: List<BibleVerse> = emptyList()
    private var speedRate: Float = 1.0f
    private var voiceMode: AudioVoiceMode = AudioVoiceMode.BILINGUAL

    private val localeEn = Locale.US
    private val localePt = Locale("pt", "BR")

    init {
        tts = TextToSpeech(context.applicationContext, this, "com.google.android.tts")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts?.let { engine ->
                engine.setSpeechRate(speedRate)
                setupProgressListener(engine)
            }
            Log.d(TAG, "TextToSpeech inicializado com sucesso.")
        } else {
            // Fallback to default engine if com.google.android.tts isn't specified
            Log.w(TAG, "Tentando fallback com engine padrão...")
        }
    }

    private fun setupProgressListener(engine: TextToSpeech) {
        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                // When utterance starts, highlight
                utteranceId?.let { id ->
                    if (id.startsWith("verse_")) {
                        val parts = id.split("_")
                        val index = parts.getOrNull(1)?.toIntOrNull() ?: -1
                        _currentVerseIndex.value = index
                    }
                }
            }

            override fun onDone(utteranceId: String?) {
                utteranceId?.let { id ->
                    val parts = id.split("_")
                    val index = parts.getOrNull(1)?.toIntOrNull() ?: -1
                    val subPart = parts.getOrNull(2) // "en" or "pt"

                    // If bilingual mode and "en" finished, the queued "pt" will play next
                    // When the final subpart of verse finishes:
                    val isVerseFinished = when (voiceMode) {
                        AudioVoiceMode.BILINGUAL -> subPart == "pt"
                        AudioVoiceMode.ENGLISH_ONLY -> subPart == "en"
                        AudioVoiceMode.PORTUGUESE_ONLY -> subPart == "pt"
                    }

                    if (isVerseFinished) {
                        val nextIndex = index + 1
                        if (nextIndex < currentPlaylist.size && _isPlaying.value) {
                            speakVerseAt(nextIndex)
                        } else {
                            _isPlaying.value = false
                            _currentVerseIndex.value = -1
                        }
                    }
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                Log.e(TAG, "Erro no TTS na fala: $utteranceId")
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                Log.e(TAG, "Erro no TTS na fala: $utteranceId, código: $errorCode")
            }
        })
    }

    fun setSpeechRate(rate: Float) {
        speedRate = rate.coerceIn(0.5f, 2.0f)
        tts?.setSpeechRate(speedRate)
    }

    fun setVoiceMode(mode: AudioVoiceMode) {
        voiceMode = mode
    }

    fun play(verses: List<BibleVerse>, startIndex: Int = 0) {
        if (!isInitialized || verses.isEmpty()) return
        currentPlaylist = verses
        _isPlaying.value = true
        val validIndex = startIndex.coerceIn(0, verses.lastIndex)
        speakVerseAt(validIndex)
    }

    private fun speakVerseAt(index: Int) {
        val verse = currentPlaylist.getOrNull(index) ?: run {
            stop()
            return
        }
        _currentVerseIndex.value = index

        tts?.stop()

        when (voiceMode) {
            AudioVoiceMode.BILINGUAL -> {
                // Speak English first
                tts?.language = localeEn
                val paramsEn = Bundle().apply {
                    putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "verse_${index}_en")
                }
                tts?.speak(verse.textEn, TextToSpeech.QUEUE_FLUSH, paramsEn, "verse_${index}_en")

                // Enqueue Portuguese
                tts?.language = localePt
                val paramsPt = Bundle().apply {
                    putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "verse_${index}_pt")
                }
                tts?.speak(verse.textPt, TextToSpeech.QUEUE_ADD, paramsPt, "verse_${index}_pt")
            }
            AudioVoiceMode.ENGLISH_ONLY -> {
                tts?.language = localeEn
                val paramsEn = Bundle().apply {
                    putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "verse_${index}_en")
                }
                tts?.speak(verse.textEn, TextToSpeech.QUEUE_FLUSH, paramsEn, "verse_${index}_en")
            }
            AudioVoiceMode.PORTUGUESE_ONLY -> {
                tts?.language = localePt
                val paramsPt = Bundle().apply {
                    putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "verse_${index}_pt")
                }
                tts?.speak(verse.textPt, TextToSpeech.QUEUE_FLUSH, paramsPt, "verse_${index}_pt")
            }
        }
    }

    fun pause() {
        _isPlaying.value = false
        tts?.stop()
    }

    fun resume() {
        if (currentPlaylist.isNotEmpty()) {
            val resumeIndex = if (_currentVerseIndex.value >= 0) _currentVerseIndex.value else 0
            _isPlaying.value = true
            speakVerseAt(resumeIndex)
        }
    }

    fun nextVerse() {
        val next = _currentVerseIndex.value + 1
        if (next < currentPlaylist.size) {
            _isPlaying.value = true
            speakVerseAt(next)
        } else {
            stop()
        }
    }

    fun previousVerse() {
        val prev = (_currentVerseIndex.value - 1).coerceAtLeast(0)
        _isPlaying.value = true
        speakVerseAt(prev)
    }

    fun playSingleVerse(verse: BibleVerse) {
        currentPlaylist = listOf(verse)
        _isPlaying.value = true
        speakVerseAt(0)
    }

    fun stop() {
        _isPlaying.value = false
        _currentVerseIndex.value = -1
        tts?.stop()
    }

    fun release() {
        stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
