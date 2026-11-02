package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.BilingualTtsManager
import com.example.data.model.AppScreen
import com.example.data.model.AppThemeMode
import com.example.data.model.AudioVoiceMode
import com.example.data.model.BibleBook
import com.example.data.model.BibleChapter
import com.example.data.model.BibleVerse
import com.example.data.model.DailyVerse
import com.example.data.model.ReadingStats
import com.example.data.repository.BibleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BibleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BibleRepository(application)
    val ttsManager = BilingualTtsManager(application)

    val allBooks: List<BibleBook> = repository.allBooks

    // Navigation screen state - starts at Home screen!
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _currentChapter = MutableStateFlow(repository.getInitialChapter())
    val currentChapter: StateFlow<BibleChapter> = _currentChapter.asStateFlow()

    private val _themeMode = MutableStateFlow(repository.getTheme())
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _fontSize = MutableStateFlow(repository.getFontSize())
    val fontSize: StateFlow<Float> = _fontSize.asStateFlow()

    private val _audioSpeed = MutableStateFlow(repository.getAudioSpeed())
    val audioSpeed: StateFlow<Float> = _audioSpeed.asStateFlow()

    private val _voiceMode = MutableStateFlow(repository.getVoiceMode())
    val voiceMode: StateFlow<AudioVoiceMode> = _voiceMode.asStateFlow()

    private val _readingStats = MutableStateFlow(repository.getReadingStats())
    val readingStats: StateFlow<ReadingStats> = _readingStats.asStateFlow()

    val dailyVerse: DailyVerse = repository.getDailyVerse()

    private val _downloadedBooks = MutableStateFlow(repository.getDownloadedBooks())
    val downloadedBooks: StateFlow<Set<String>> = _downloadedBooks.asStateFlow()

    private val _downloadingBookId = MutableStateFlow<String?>(null)
    val downloadingBookId: StateFlow<String?> = _downloadingBookId.asStateFlow()

    private val _downloadingChapterKey = MutableStateFlow<String?>(null)
    val downloadingChapterKey: StateFlow<String?> = _downloadingChapterKey.asStateFlow()

    private val _downloadProgress = MutableStateFlow(0f)
    val downloadProgress: StateFlow<Float> = _downloadProgress.asStateFlow()

    val isPlaying: StateFlow<Boolean> = ttsManager.isPlaying
    val currentVerseIndex: StateFlow<Int> = ttsManager.currentVerseIndex

    init {
        ttsManager.setSpeechRate(_audioSpeed.value)
        ttsManager.setVoiceMode(_voiceMode.value)

        // Automatically track verse reads during speech synthesis
        viewModelScope.launch {
            currentVerseIndex.collect { index ->
                if (index >= 0 && isPlaying.value) {
                    val chapter = _currentChapter.value
                    val verse = chapter.verses.getOrNull(index)
                    if (verse != null) {
                        repository.recordVerseRead(chapter.bookId, chapter.chapter, verse.number)
                        repository.saveLastPosition(chapter.bookId, chapter.chapter, verse.number)
                        _readingStats.value = repository.getReadingStats()
                    }
                }
            }
        }
    }

    // Navigation controls with working Back Handler
    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        return if (_currentScreen.value != AppScreen.HOME) {
            _currentScreen.value = AppScreen.HOME
            true
        } else {
            false
        }
    }

    fun continueReading() {
        val stats = _readingStats.value
        val book = allBooks.find { it.id == stats.lastBookId } ?: allBooks.first()
        selectBookAndChapter(book, stats.lastChapter)
        navigateTo(AppScreen.READER)
    }

    fun openReaderFor(book: BibleBook, chapter: Int) {
        selectBookAndChapter(book, chapter)
        navigateTo(AppScreen.READER)
    }

    fun selectBookAndChapter(book: BibleBook, chapter: Int) {
        ttsManager.stop()
        repository.saveLastPosition(book.id, chapter, 1)
        _currentChapter.value = repository.getChapter(book.id, chapter)
        _readingStats.value = repository.getReadingStats()
    }

    fun nextChapter() {
        ttsManager.stop()
        val current = _currentChapter.value
        val book = allBooks.find { it.id == current.bookId } ?: allBooks.first()
        if (current.chapter < book.totalChapters) {
            selectBookAndChapter(book, current.chapter + 1)
        } else {
            val bookIndex = allBooks.indexOfFirst { it.id == book.id }
            if (bookIndex != -1 && bookIndex < allBooks.size - 1) {
                selectBookAndChapter(allBooks[bookIndex + 1], 1)
            }
        }
    }

    fun previousChapter() {
        ttsManager.stop()
        val current = _currentChapter.value
        val book = allBooks.find { it.id == current.bookId } ?: allBooks.first()
        if (current.chapter > 1) {
            selectBookAndChapter(book, current.chapter - 1)
        } else {
            val bookIndex = allBooks.indexOfFirst { it.id == book.id }
            if (bookIndex > 0) {
                val prevBook = allBooks[bookIndex - 1]
                selectBookAndChapter(prevBook, prevBook.totalChapters)
            }
        }
    }

    fun toggleAudio() {
        if (isPlaying.value) {
            ttsManager.pause()
        } else {
            val verses = _currentChapter.value.verses
            val startIndex = if (currentVerseIndex.value >= 0) currentVerseIndex.value else 0
            ttsManager.play(verses, startIndex)
        }
    }

    fun playSingleVerse(verse: BibleVerse) {
        val chapter = _currentChapter.value
        repository.recordVerseRead(chapter.bookId, chapter.chapter, verse.number)
        _readingStats.value = repository.getReadingStats()
        ttsManager.playSingleVerse(verse)
    }

    fun nextVerse() {
        ttsManager.nextVerse()
    }

    fun previousVerse() {
        ttsManager.previousVerse()
    }

    fun stopAudio() {
        ttsManager.stop()
    }

    fun setTheme(theme: AppThemeMode) {
        _themeMode.value = theme
        repository.saveTheme(theme)
    }

    fun setFontSize(size: Float) {
        _fontSize.value = size
        repository.saveFontSize(size)
    }

    fun setAudioSpeed(speed: Float) {
        _audioSpeed.value = speed
        repository.saveAudioSpeed(speed)
        ttsManager.setSpeechRate(speed)
    }

    fun setVoiceMode(mode: AudioVoiceMode) {
        _voiceMode.value = mode
        repository.saveVoiceMode(mode)
        ttsManager.setVoiceMode(mode)
    }

    fun toggleFavorite(verseNumber: Int) {
        val current = _currentChapter.value
        val key = "${current.bookId}_${current.chapter}_$verseNumber"
        repository.toggleFavorite(key)
        _readingStats.value = repository.getReadingStats()
    }

    fun isFavorite(verseNumber: Int): Boolean {
        val current = _currentChapter.value
        val key = "${current.bookId}_${current.chapter}_$verseNumber"
        return _readingStats.value.favoriteVerses.contains(key)
    }

    fun markCurrentChapterCompleted() {
        val current = _currentChapter.value
        repository.markChapterCompleted(current.bookId, current.chapter, current.verses.size)
        _readingStats.value = repository.getReadingStats()
    }

    fun isCurrentChapterCompleted(): Boolean {
        val current = _currentChapter.value
        val key = "${current.bookId}_${current.chapter}"
        return _readingStats.value.chaptersCompleted.contains(key)
    }

    fun resetProgress() {
        repository.resetProgress()
        _readingStats.value = repository.getReadingStats()
    }

    fun isBookDownloaded(bookId: String): Boolean {
        return _downloadedBooks.value.contains(bookId) || repository.isBookDownloaded(bookId)
    }

    fun isChapterDownloaded(bookId: String, chapter: Int): Boolean {
        return repository.isChapterDownloaded(bookId, chapter)
    }

    fun downloadSingleChapter(book: BibleBook, chapter: Int) {
        val key = "${book.id}_$chapter"
        if (_downloadingChapterKey.value != null || _downloadingBookId.value != null) return
        viewModelScope.launch {
            _downloadingChapterKey.value = key
            val result = repository.downloadSingleChapterLocally(book, chapter)
            result.onSuccess { downloadedChapter ->
                if (_currentChapter.value.bookId == book.id && _currentChapter.value.chapter == chapter) {
                    _currentChapter.value = downloadedChapter
                }
            }
            _downloadingChapterKey.value = null
            _downloadedBooks.value = repository.getDownloadedBooks()
        }
    }

    fun downloadBook(book: BibleBook) {
        if (_downloadingBookId.value != null) return
        viewModelScope.launch {
            _downloadingBookId.value = book.id
            _downloadProgress.value = 0f
            repository.downloadBookLocally(book) { progress ->
                _downloadProgress.value = progress
            }
            _downloadingBookId.value = null
            _downloadedBooks.value = repository.getDownloadedBooks()
            if (_currentChapter.value.bookId == book.id) {
                _currentChapter.value = repository.getChapter(book.id, _currentChapter.value.chapter)
            }
        }
    }

    fun deleteDownloadedBook(bookId: String) {
        repository.deleteDownloadedBook(bookId)
        _downloadedBooks.value = repository.getDownloadedBooks()
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.release()
    }
}
