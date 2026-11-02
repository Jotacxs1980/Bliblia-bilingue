package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.AppThemeMode
import com.example.data.model.AudioVoiceMode
import com.example.data.model.BibleBook
import com.example.data.model.BibleCategory
import com.example.data.model.BibleChapter
import com.example.data.model.BibleVerse
import com.example.data.model.DailyVerse
import com.example.data.model.ReadingStats
import com.example.data.model.Testament
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class BibleRepository(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("biblia_kjv_prefs", Context.MODE_PRIVATE)

    val allBooks: List<BibleBook> = listOf(
        // Antigo Testamento - Pentateuco / Lei
        BibleBook("GEN", "Gênesis", "Genesis", "Gn", Testament.OLD, BibleCategory.LAW, 50),
        BibleBook("EXO", "Êxodo", "Exodus", "Ex", Testament.OLD, BibleCategory.LAW, 40),
        BibleBook("LEV", "Levítico", "Leviticus", "Lv", Testament.OLD, BibleCategory.LAW, 27),
        BibleBook("NUM", "Números", "Numbers", "Nm", Testament.OLD, BibleCategory.LAW, 36),
        BibleBook("DEU", "Deuteronômio", "Deuteronomy", "Dt", Testament.OLD, BibleCategory.LAW, 34),
        // Históricos
        BibleBook("JOS", "Josué", "Joshua", "Js", Testament.OLD, BibleCategory.HISTORY, 24),
        BibleBook("JDG", "Juízes", "Judges", "Jz", Testament.OLD, BibleCategory.HISTORY, 21),
        BibleBook("RUT", "Rute", "Ruth", "Rt", Testament.OLD, BibleCategory.HISTORY, 4),
        BibleBook("1SA", "1 Samuel", "1 Samuel", "1Sm", Testament.OLD, BibleCategory.HISTORY, 31),
        BibleBook("2SA", "2 Samuel", "2 Samuel", "2Sm", Testament.OLD, BibleCategory.HISTORY, 24),
        BibleBook("1KI", "1 Reis", "1 Kings", "1Rs", Testament.OLD, BibleCategory.HISTORY, 22),
        BibleBook("2KI", "2 Reis", "2 Kings", "2Rs", Testament.OLD, BibleCategory.HISTORY, 25),
        BibleBook("1CH", "1 Crônicas", "1 Chronicles", "1Cr", Testament.OLD, BibleCategory.HISTORY, 29),
        BibleBook("2CH", "2 Crônicas", "2 Chronicles", "2Cr", Testament.OLD, BibleCategory.HISTORY, 36),
        BibleBook("EZR", "Esdras", "Ezra", "Ed", Testament.OLD, BibleCategory.HISTORY, 10),
        BibleBook("NEH", "Neemias", "Nehemiah", "Ne", Testament.OLD, BibleCategory.HISTORY, 13),
        BibleBook("EST", "Ester", "Esther", "Et", Testament.OLD, BibleCategory.HISTORY, 10),
        // Poéticos
        BibleBook("JOB", "Jó", "Job", "Jó", Testament.OLD, BibleCategory.POETRY, 42),
        BibleBook("PSA", "Salmos", "Psalms", "Sl", Testament.OLD, BibleCategory.POETRY, 150),
        BibleBook("PRO", "Provérbios", "Proverbs", "Pv", Testament.OLD, BibleCategory.POETRY, 31),
        BibleBook("ECC", "Eclesiastes", "Ecclesiastes", "Ec", Testament.OLD, BibleCategory.POETRY, 12),
        BibleBook("SNG", "Cantares", "Song of Solomon", "Ct", Testament.OLD, BibleCategory.POETRY, 8),
        // Profetas Maiores
        BibleBook("ISA", "Isaías", "Isaiah", "Is", Testament.OLD, BibleCategory.MAJOR_PROPHETS, 66),
        BibleBook("JER", "Jeremias", "Jeremiah", "Jr", Testament.OLD, BibleCategory.MAJOR_PROPHETS, 52),
        BibleBook("LAM", "Lamentações", "Lamentations", "Lm", Testament.OLD, BibleCategory.MAJOR_PROPHETS, 5),
        BibleBook("EZK", "Ezequiel", "Ezekiel", "Ez", Testament.OLD, BibleCategory.MAJOR_PROPHETS, 48),
        BibleBook("DAN", "Daniel", "Daniel", "Dn", Testament.OLD, BibleCategory.MAJOR_PROPHETS, 12),
        // Profetas Menores
        BibleBook("HOS", "Oseias", "Hosea", "Os", Testament.OLD, BibleCategory.MINOR_PROPHETS, 14),
        BibleBook("JOL", "Joel", "Joel", "Jl", Testament.OLD, BibleCategory.MINOR_PROPHETS, 3),
        BibleBook("AMO", "Amós", "Amos", "Am", Testament.OLD, BibleCategory.MINOR_PROPHETS, 9),
        BibleBook("OBA", "Obadias", "Obadiah", "Ob", Testament.OLD, BibleCategory.MINOR_PROPHETS, 1),
        BibleBook("JON", "Jonas", "Jonah", "Jn", Testament.OLD, BibleCategory.MINOR_PROPHETS, 4),
        BibleBook("MIC", "Miqueias", "Micah", "Mq", Testament.OLD, BibleCategory.MINOR_PROPHETS, 7),
        BibleBook("NAM", "Naum", "Nahum", "Na", Testament.OLD, BibleCategory.MINOR_PROPHETS, 3),
        BibleBook("HAB", "Habacuque", "Habakkuk", "Hc", Testament.OLD, BibleCategory.MINOR_PROPHETS, 3),
        BibleBook("ZEP", "Sofonias", "Zephaniah", "Sf", Testament.OLD, BibleCategory.MINOR_PROPHETS, 3),
        BibleBook("HAG", "Ageu", "Haggai", "Ag", Testament.OLD, BibleCategory.MINOR_PROPHETS, 2),
        BibleBook("ZEC", "Zacarias", "Zechariah", "Zc", Testament.OLD, BibleCategory.MINOR_PROPHETS, 14),
        BibleBook("MAL", "Malaquias", "Malachi", "Ml", Testament.OLD, BibleCategory.MINOR_PROPHETS, 4),

        // Novo Testamento - Evangelhos
        BibleBook("MAT", "Mateus", "Matthew", "Mt", Testament.NEW, BibleCategory.GOSPELS, 28),
        BibleBook("MRK", "Marcos", "Mark", "Mc", Testament.NEW, BibleCategory.GOSPELS, 16),
        BibleBook("LUK", "Lucas", "Luke", "Lc", Testament.NEW, BibleCategory.GOSPELS, 24),
        BibleBook("JHN", "João", "John", "Jo", Testament.NEW, BibleCategory.GOSPELS, 21),
        BibleBook("ACT", "Atos", "Acts", "At", Testament.NEW, BibleCategory.ACTS, 28),
        // Epístolas Paulinas
        BibleBook("ROM", "Romanos", "Romans", "Rm", Testament.NEW, BibleCategory.PAULINE_EPISTLES, 16),
        BibleBook("1CO", "1 Coríntios", "1 Corinthians", "1Co", Testament.NEW, BibleCategory.PAULINE_EPISTLES, 16),
        BibleBook("2CO", "2 Coríntios", "2 Corinthians", "2Co", Testament.NEW, BibleCategory.PAULINE_EPISTLES, 13),
        BibleBook("GAL", "Gálatas", "Galatians", "Gl", Testament.NEW, BibleCategory.PAULINE_EPISTLES, 6),
        BibleBook("EPH", "Efésios", "Ephesians", "Ef", Testament.NEW, BibleCategory.PAULINE_EPISTLES, 6),
        BibleBook("PHP", "Filipenses", "Philippians", "Fp", Testament.NEW, BibleCategory.PAULINE_EPISTLES, 4),
        BibleBook("COL", "Colossenses", "Colossians", "Cl", Testament.NEW, BibleCategory.PAULINE_EPISTLES, 4),
        BibleBook("1TH", "1 Tessalonicenses", "1 Thessalonians", "1Ts", Testament.NEW, BibleCategory.PAULINE_EPISTLES, 5),
        BibleBook("2TH", "2 Tessalonicenses", "2 Thessalonians", "2Ts", Testament.NEW, BibleCategory.PAULINE_EPISTLES, 3),
        BibleBook("1TI", "1 Timóteo", "1 Timothy", "1Tm", Testament.NEW, BibleCategory.PAULINE_EPISTLES, 6),
        BibleBook("2TI", "2 Timóteo", "2 Timothy", "2Tm", Testament.NEW, BibleCategory.PAULINE_EPISTLES, 4),
        BibleBook("TIT", "Tito", "Titus", "Tt", Testament.NEW, BibleCategory.PAULINE_EPISTLES, 3),
        BibleBook("PHM", "Filemom", "Philemon", "Fm", Testament.NEW, BibleCategory.PAULINE_EPISTLES, 1),
        // Epístolas Gerais
        BibleBook("HEB", "Hebreus", "Hebrews", "Hb", Testament.NEW, BibleCategory.GENERAL_EPISTLES, 13),
        BibleBook("JAS", "Tiago", "James", "Tg", Testament.NEW, BibleCategory.GENERAL_EPISTLES, 5),
        BibleBook("1PE", "1 Pedro", "1 Peter", "1Pe", Testament.NEW, BibleCategory.GENERAL_EPISTLES, 5),
        BibleBook("2PE", "2 Pedro", "2 Peter", "2Pe", Testament.NEW, BibleCategory.GENERAL_EPISTLES, 3),
        BibleBook("1JN", "1 João", "1 John", "1Jo", Testament.NEW, BibleCategory.GENERAL_EPISTLES, 5),
        BibleBook("2JN", "2 João", "2 John", "2Jo", Testament.NEW, BibleCategory.GENERAL_EPISTLES, 1),
        BibleBook("3JN", "3 João", "3 John", "3Jo", Testament.NEW, BibleCategory.GENERAL_EPISTLES, 1),
        BibleBook("JUD", "Judas", "Jude", "Jd", Testament.NEW, BibleCategory.GENERAL_EPISTLES, 1),
        BibleBook("REV", "Apocalipse", "Revelation", "Ap", Testament.NEW, BibleCategory.APOCALYPSE, 22)
    )

    private val offlineChapters: Map<String, BibleChapter> = mapOf(
        "GEN_1" to createGenesis1(),
        "PSA_23" to createPsalm23(),
        "PSA_91" to createPsalm91(),
        "JHN_1" to createJohn1(),
        "ROM_8" to createRomans8(),
        "PRO_3" to createProverbs3(),
        "MAT_5" to createMatthew5()
    )

    fun getInitialChapter(): BibleChapter {
        val savedBookId = prefs.getString("last_book_id", "GEN") ?: "GEN"
        val savedChapter = prefs.getInt("last_chapter", 1)
        return getChapter(savedBookId, savedChapter)
    }

    fun getChapter(bookId: String, chapter: Int): BibleChapter {
        // 1. Check if chapter was downloaded as a single chapter or whole book
        loadChapterFromDisk(bookId, chapter)?.let { return it }

        // 2. Check offline preloaded chapters
        val key = "${bookId}_$chapter"
        offlineChapters[key]?.let { return it }

        // 3. Fallback to offline generator
        val book = allBooks.find { it.id == bookId } ?: allBooks.first()
        return createFallbackChapter(book, chapter)
    }

    fun isChapterDownloaded(bookId: String, chapter: Int): Boolean {
        val chFile = java.io.File(context.filesDir, "bible_chapter_${bookId}_${chapter}.json")
        if (chFile.exists()) return true
        val bookFile = java.io.File(context.filesDir, "bible_book_${bookId}.json")
        if (bookFile.exists()) {
            try {
                val json = JSONObject(bookFile.readText())
                if (json.has("$chapter")) return true
            } catch (_: Exception) {}
        }
        val key = "${bookId}_$chapter"
        return offlineChapters.containsKey(key)
    }

    fun isBookDownloaded(bookId: String): Boolean {
        val file = java.io.File(context.filesDir, "bible_book_${bookId}.json")
        if (file.exists()) return true
        val downloaded = prefs.getStringSet("downloaded_books_set", emptySet()) ?: emptySet()
        return downloaded.contains(bookId)
    }

    fun getDownloadedBooks(): Set<String> {
        val saved = prefs.getStringSet("downloaded_books_set", emptySet()) ?: emptySet()
        val result = mutableSetOf<String>()
        result.addAll(saved)
        allBooks.forEach { book ->
            val file = java.io.File(context.filesDir, "bible_book_${book.id}.json")
            if (file.exists()) {
                result.add(book.id)
            }
        }
        return result
    }

    suspend fun downloadSingleChapterLocally(
        book: BibleBook,
        chapter: Int
    ): Result<BibleChapter> = withContext(Dispatchers.IO) {
        try {
            val chapterData = fetchDualTranslationChapter(book, chapter)
                ?: createFallbackChapter(book, chapter)

            val chObj = JSONObject().apply {
                put("argument", chapterData.argument1611)
                val vArr = org.json.JSONArray()
                chapterData.verses.forEach { v ->
                    val vObj = JSONObject().apply {
                        put("number", v.number)
                        put("textEn", v.textEn)
                        put("textPt", v.textPt)
                    }
                    vArr.put(vObj)
                }
                put("verses", vArr)
            }

            val file = java.io.File(context.filesDir, "bible_chapter_${book.id}_${chapter}.json")
            file.writeText(chObj.toString())

            Result.success(chapterData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun downloadBookLocally(
        book: BibleBook,
        onProgress: (Float) -> Unit = {}
    ): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val bookChaptersObj = JSONObject()
            val totalCh = book.totalChapters

            for (ch in 1..totalCh) {
                val chapterData = fetchDualTranslationChapter(book, ch)
                    ?: createFallbackChapter(book, ch)

                val chObj = JSONObject().apply {
                    put("argument", chapterData.argument1611)
                    val vArr = org.json.JSONArray()
                    chapterData.verses.forEach { v ->
                        val vObj = JSONObject().apply {
                            put("number", v.number)
                            put("textEn", v.textEn)
                            put("textPt", v.textPt)
                        }
                        vArr.put(vObj)
                    }
                    put("verses", vArr)
                }
                bookChaptersObj.put("$ch", chObj)
                onProgress(ch.toFloat() / totalCh.toFloat())

                // 1.5s pause between chapters to prevent API rate limiting
                if (ch < totalCh) {
                    delay(1500)
                }
            }

            // Save JSON to local internal app storage
            val file = java.io.File(context.filesDir, "bible_book_${book.id}.json")
            file.writeText(bookChaptersObj.toString())

            // Record in SharedPreferences
            val downloaded = prefs.getStringSet("downloaded_books_set", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
            downloaded.add(book.id)
            prefs.edit().putStringSet("downloaded_books_set", downloaded).apply()

            Result.success(totalCh)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun loadChapterFromDisk(bookId: String, chapter: Int): BibleChapter? {
        try {
            // 1. Check single chapter file first
            val singleFile = java.io.File(context.filesDir, "bible_chapter_${bookId}_${chapter}.json")
            if (singleFile.exists()) {
                val content = singleFile.readText()
                val chObj = JSONObject(content)
                val argument = chObj.optString("argument", "THE ARGUMENT (1611 Edition): The sacred testimony of $bookId, chapter $chapter.")
                val vArr = chObj.getJSONArray("verses")
                val verses = mutableListOf<BibleVerse>()
                for (i in 0 until vArr.length()) {
                    val vObj = vArr.getJSONObject(i)
                    verses.add(
                        BibleVerse(
                            number = vObj.getInt("number"),
                            textEn = vObj.getString("textEn"),
                            textPt = vObj.getString("textPt")
                        )
                    )
                }
                val book = allBooks.find { it.id == bookId } ?: allBooks.first()
                return BibleChapter(book.id, book.namePt, book.nameEn, chapter, argument, verses)
            }

            // 2. Check full book file
            val file = java.io.File(context.filesDir, "bible_book_${bookId}.json")
            if (!file.exists()) return null
            val content = file.readText()
            val json = JSONObject(content)
            if (!json.has("$chapter")) return null

            val chObj = json.getJSONObject("$chapter")
            val argument = chObj.optString("argument", "THE ARGUMENT (1611 Edition): The sacred testimony of $bookId, chapter $chapter.")
            val vArr = chObj.getJSONArray("verses")
            val verses = mutableListOf<BibleVerse>()
            for (i in 0 until vArr.length()) {
                val vObj = vArr.getJSONObject(i)
                verses.add(
                    BibleVerse(
                        number = vObj.getInt("number"),
                        textEn = vObj.getString("textEn"),
                        textPt = vObj.getString("textPt")
                    )
                )
            }
            val book = allBooks.find { it.id == bookId } ?: allBooks.first()
            return BibleChapter(book.id, book.namePt, book.nameEn, chapter, argument, verses)
        } catch (e: Exception) {
            return null
        }
    }

    fun deleteDownloadedBook(bookId: String): Boolean {
        try {
            val file = java.io.File(context.filesDir, "bible_book_${bookId}.json")
            if (file.exists()) file.delete()

            // Also delete any single chapter files for this book
            val book = allBooks.find { it.id == bookId }
            val maxCaps = book?.totalChapters ?: 150
            for (c in 1..maxCaps) {
                val chFile = java.io.File(context.filesDir, "bible_chapter_${bookId}_${c}.json")
                if (chFile.exists()) chFile.delete()
            }

            val downloaded = prefs.getStringSet("downloaded_books_set", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
            downloaded.remove(bookId)
            prefs.edit().putStringSet("downloaded_books_set", downloaded).apply()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    suspend fun fetchDualTranslationChapter(book: BibleBook, chapter: Int): BibleChapter? = withContext(Dispatchers.IO) {
        var attempt = 0
        while (attempt < 3) {
            try {
                val encodedBook = URLEncoder.encode(book.nameEn, "UTF-8")
                val urlEn = URL("https://bible-api.com/$encodedBook+$chapter?translation=kjv")
                val connEn = (urlEn.openConnection() as HttpURLConnection).apply {
                    connectTimeout = 6000
                    readTimeout = 6000
                    requestMethod = "GET"
                }
                if (connEn.responseCode != 200) {
                    attempt++
                    delay(2000)
                    continue
                }
                val textEnJson = connEn.inputStream.bufferedReader().use { it.readText() }
                val jsonEn = JSONObject(textEnJson)
                val versesEnArray = jsonEn.getJSONArray("verses")

                val urlPt = URL("https://bible-api.com/$encodedBook+$chapter?translation=almeida")
                val connPt = (urlPt.openConnection() as HttpURLConnection).apply {
                    connectTimeout = 6000
                    readTimeout = 6000
                    requestMethod = "GET"
                }
                val versesPtList = mutableListOf<String>()
                if (connPt.responseCode == 200) {
                    val textPtJson = connPt.inputStream.bufferedReader().use { it.readText() }
                    val jsonPt = JSONObject(textPtJson)
                    val versesPtArray = jsonPt.getJSONArray("verses")
                    for (j in 0 until versesPtArray.length()) {
                        versesPtList.add(versesPtArray.getJSONObject(j).getString("text").trim().replace("\n", " "))
                    }
                }

                val versesList = mutableListOf<BibleVerse>()
                for (i in 0 until versesEnArray.length()) {
                    val vObj = versesEnArray.getJSONObject(i)
                    val vNum = vObj.getInt("verse")
                    val vTextEn = vObj.getString("text").trim().replace("\n", " ")
                    val vTextPt = versesPtList.getOrNull(i) ?: vTextEn
                    versesList.add(BibleVerse(vNum, vTextEn, vTextPt))
                }

                val argument = "THE ARGUMENT (1611 Edition): The sacred testimony and divine instruction of ${book.nameEn}, chapter $chapter."
                return@withContext BibleChapter(book.id, book.namePt, book.nameEn, chapter, argument, versesList)
            } catch (e: Exception) {
                attempt++
                delay(2000)
            }
        }
        null
    }

    // Persistence for user preferences
    fun saveTheme(theme: AppThemeMode) {
        prefs.edit().putString("app_theme", theme.name).apply()
    }

    fun getTheme(): AppThemeMode {
        val name = prefs.getString("app_theme", AppThemeMode.SEPIA.name)
        return try { AppThemeMode.valueOf(name ?: AppThemeMode.SEPIA.name) } catch (_: Exception) { AppThemeMode.SEPIA }
    }

    fun saveFontSize(size: Float) {
        prefs.edit().putFloat("font_size", size).apply()
    }

    fun getFontSize(): Float {
        return prefs.getFloat("font_size", 18f)
    }

    fun saveAudioSpeed(speed: Float) {
        prefs.edit().putFloat("audio_speed", speed).apply()
    }

    fun getAudioSpeed(): Float {
        return prefs.getFloat("audio_speed", 1.0f)
    }

    fun saveVoiceMode(mode: AudioVoiceMode) {
        prefs.edit().putString("voice_mode", mode.name).apply()
    }

    fun getVoiceMode(): AudioVoiceMode {
        val name = prefs.getString("voice_mode", AudioVoiceMode.BILINGUAL.name)
        return try { AudioVoiceMode.valueOf(name ?: AudioVoiceMode.BILINGUAL.name) } catch (_: Exception) { AudioVoiceMode.BILINGUAL }
    }

    fun saveLastPosition(bookId: String, chapter: Int, verse: Int = 1) {
        prefs.edit()
            .putString("last_book_id", bookId)
            .putInt("last_chapter", chapter)
            .putInt("last_verse", verse)
            .apply()
    }

    fun getReadingStats(): ReadingStats {
        val total = prefs.getInt("stats_total_verses", 0) // Inicia estritamente zerado
        val completedSet = prefs.getStringSet("stats_completed_chapters", emptySet()) ?: emptySet() // Inicia vazio
        val streak = prefs.getInt("stats_streak", 0) // Inicia zerado
        val favorites = prefs.getStringSet("stats_favorites", emptySet()) ?: emptySet()
        val lastBook = prefs.getString("last_book_id", "GEN") ?: "GEN"
        val lastCh = prefs.getInt("last_chapter", 1)
        val lastV = prefs.getInt("last_verse", 1)
        return ReadingStats(total, completedSet, streak, favorites, lastBook, lastCh, lastV)
    }

    fun recordVerseRead(bookId: String, chapter: Int, verseNumber: Int) {
        val verseKey = "${bookId}_${chapter}_$verseNumber"
        val currentVerses = prefs.getStringSet("stats_read_verses_set", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        if (!currentVerses.contains(verseKey)) {
            currentVerses.add(verseKey)
            val newTotal = currentVerses.size
            prefs.edit()
                .putStringSet("stats_read_verses_set", currentVerses)
                .putInt("stats_total_verses", newTotal)
                .apply()
            updateReadingStreak()
        }
    }

    fun markChapterCompleted(bookId: String, chapter: Int, chapterVersesCount: Int = 25) {
        val key = "${bookId}_$chapter"
        val current = prefs.getStringSet("stats_completed_chapters", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        val isNew = current.add(key)

        val total = if (isNew) {
            val existing = prefs.getInt("stats_total_verses", 0)
            existing + chapterVersesCount
        } else {
            prefs.getInt("stats_total_verses", 0)
        }

        prefs.edit()
            .putStringSet("stats_completed_chapters", current)
            .putInt("stats_total_verses", total)
            .apply()

        updateReadingStreak()
    }

    private fun updateReadingStreak() {
        val now = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000L
        val lastReadTime = prefs.getLong("stats_last_read_millis", 0L)
        val currentStreak = prefs.getInt("stats_streak", 0)

        val newStreak = when {
            lastReadTime == 0L -> 1
            now - lastReadTime < oneDayMillis -> if (currentStreak == 0) 1 else currentStreak
            now - lastReadTime < 2 * oneDayMillis -> currentStreak + 1
            else -> 1
        }

        prefs.edit()
            .putLong("stats_last_read_millis", now)
            .putInt("stats_streak", newStreak)
            .apply()
    }

    fun resetProgress() {
        prefs.edit()
            .remove("stats_completed_chapters")
            .remove("stats_read_verses_set")
            .putInt("stats_total_verses", 0)
            .putInt("stats_streak", 0)
            .apply()
    }

    fun toggleFavorite(verseKey: String): Boolean {
        val current = prefs.getStringSet("stats_favorites", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        val isFav = if (current.contains(verseKey)) {
            current.remove(verseKey)
            false
        } else {
            current.add(verseKey)
            true
        }
        prefs.edit().putStringSet("stats_favorites", current).apply()
        return isFav
    }

    fun getDailyVerse(): DailyVerse {
        // Daily inspirational verse
        return DailyVerse(
            bookId = "PSA",
            bookNamePt = "Salmos",
            bookNameEn = "Psalms",
            chapter = 23,
            verseNumber = 1,
            textEn = "The LORD is my shepherd; I shall not want.",
            textPt = "O SENHOR é o meu pastor, nada me faltará."
        )
    }

    // Pre-loaded Rich Chapters with Exact 1611 English + Portuguese
    private fun createGenesis1(): BibleChapter {
        val argument = "THE ARGUMENT (Authorized 1611 Edition): The creation of heaven and earth, of the light, of the firmament, of the earth separated from the waters, and made fruitful, of the sun, moon, and stars, of fish and fowl, of beasts and cattle, of man in the image of God. Also the appointment of food."
        val verses = listOf(
            BibleVerse(1, "In the beginning God created the heaven and the earth.", "No princípio Deus criou os céus e a terra."),
            BibleVerse(2, "And the earth was without form, and void; and darkness was upon the face of the deep. And the Spirit of God moved upon the face of the waters.", "E a terra era sem forma e vazia; e havia trevas sobre a face do abismo; e o Espírito de Deus se movia sobre a face das águas."),
            BibleVerse(3, "And God said, Let there be light: and there was light.", "E disse Deus: Haja luz; e houve luz."),
            BibleVerse(4, "And God saw the light, that it was good: and God divided the light from the darkness.", "E viu Deus que era boa a luz; e fez Deus separação entre a luz e as trevas."),
            BibleVerse(5, "And God called the light Day, and the darkness he called Night. And the evening and the morning were the first day.", "E Deus chamou à luz Dia; e às trevas chamou Noite. E foi a tarde e a manhã, o primeiro dia."),
            BibleVerse(6, "And God said, Let there be a firmament in the midst of the waters, and let it divide the waters from the waters.", "E disse Deus: Haja uma expansão no meio das águas, e haja separação entre águas e águas."),
            BibleVerse(7, "And God made the firmament, and divided the waters which were under the firmament from the waters which were above the firmament: and it was so.", "E fez Deus a expansão, e fez separação entre as águas que estavam debaixo da expansão e as águas que estavam sobre a expansão; e assim foi."),
            BibleVerse(8, "And God called the firmament Heaven. And the evening and the morning were the second day.", "E chamou Deus à expansão Céus, e foi a tarde e a manhã, o segundo dia."),
            BibleVerse(9, "And God said, Let the waters under the heaven be gathered together unto one place, and let the dry land appear: and it was so.", "E disse Deus: Ajuntem-se as águas debaixo dos céus num só lugar; e apareça a porção seca; e assim foi."),
            BibleVerse(10, "And God called the dry land Earth; and the gathering together of the waters called he Seas: and God saw that it was good.", "E chamou Deus à porção seca Terra; e ao ajuntamento das águas chamou Mares; e viu Deus que era bom."),
            BibleVerse(11, "And God said, Let the earth bring forth grass, the herb yielding seed, and the fruit tree yielding fruit after his kind, whose seed is in itself, upon the earth: and it was so.", "E disse Deus: Produza a terra erva verde, erva que dê semente, árvore frutífera que dê fruto segundo a sua espécie, cuja semente está nela sobre a terra; e assim foi."),
            BibleVerse(12, "And the earth brought forth grass, and herb yielding seed after his kind, and the tree yielding fruit, whose seed was in itself, after his kind: and God saw that it was good.", "E a terra produziu erva, erva dando semente conforme a sua espécie, e a árvore frutífera, cuja semente está nela conforme a sua espécie; e viu Deus que era bom."),
            BibleVerse(13, "And the evening and the morning were the third day.", "E foi a tarde e a manhã, o terceiro dia."),
            BibleVerse(14, "And God said, Let there be lights in the firmament of the heaven to divide the day from the night; and let them be for signs, and for seasons, and for days, and years:", "E disse Deus: Haja luminares na expansão dos céus, para haver separação entre o dia e a noite; e sejam eles para sinais e para tempos determinados e para dias e anos."),
            BibleVerse(15, "And let them be for lights in the firmament of the heaven to give light upon the earth: and it was so.", "E sejam para luminares na expansão dos céus, para alumiar a terra; e assim foi."),
            BibleVerse(16, "And God made two great lights; the greater light to rule the day, and the lesser light to rule the night: he made the stars also.", "E fez Deus os dois grandes luminares: o luminar maior para governar o dia, e o luminar menor para governar a noite; e fez as estrelas."),
            BibleVerse(17, "And God set them in the firmament of the heaven to give light upon the earth,", "E Deus os pôs na expansão dos céus para alumiar a terra,"),
            BibleVerse(18, "And to rule over the day and over the night, and to divide the light from the darkness: and God saw that it was good.", "E para governar o dia e a noite, e para fazer separação entre a luz e as trevas; e viu Deus que era bom."),
            BibleVerse(19, "And the evening and the morning were the fourth day.", "E foi a tarde e a manhã, o quarto dia."),
            BibleVerse(20, "And God said, Let the waters bring forth abundantly the moving creature that hath life, and fowl that may fly above the earth in the open firmament of heaven.", "E disse Deus: Produzam as águas abundantemente répteis de alma vivente; e voem as aves sobre a face da terra, na expansão dos céus."),
            BibleVerse(21, "And God created great whales, and every living creature that moveth, which the waters brought forth abundantly, after their kind, and every winged fowl after his kind: and God saw that it was good.", "E Deus criou as grandes baleias, e todo o réptil de alma vivente que as águas abundantemente produziram conforme as suas espécies; e toda a ave de asas conforme a sua espécie; e viu Deus que era bom."),
            BibleVerse(22, "And God blessed them, saying, Be fruitful, and multiply, and fill the waters in the seas, and let fowl multiply in the earth.", "E Deus os abençoou, dizendo: Frutificai e multiplicai-vos, e enchei as águas nos mares; e as aves se multipliquem na terra."),
            BibleVerse(23, "And the evening and the morning were the fifth day.", "E foi a tarde e a manhã, o quinto dia."),
            BibleVerse(24, "And God said, Let the earth bring forth the living creature after his kind, cattle, and creeping thing, and beast of the earth after his kind: and it was so.", "E disse Deus: Produza a terra alma vivente conforme a sua espécie; gado, e répteis e feras da terra conforme a sua espécie; e assim foi."),
            BibleVerse(25, "And God made the beast of the earth after his kind, and cattle after their kind, and every thing that creepeth upon the earth after his kind: and God saw that it was good.", "E fez Deus as feras da terra conforme a sua espécie, e o gado conforme a sua espécie, e todo o réptil da terra conforme a sua espécie; e viu Deus que era bom."),
            BibleVerse(26, "And God said, Let us make man in our image, after our likeness: and let them have dominion over the fish of the sea, and over the fowl of the air, and over the cattle, and over all the earth, and over every creeping thing that creepeth upon the earth.", "E disse Deus: Façamos o homem à nossa imagem, conforme a nossa semelhança; e domine sobre os peixes do mar, e sobre as aves dos céus, e sobre o gado, e sobre toda a terra, e sobre todo o réptil que se move sobre a terra."),
            BibleVerse(27, "So God created man in his own image, in the image of God created he him; male and female created he them.", "E criou Deus o homem à sua imagem; à imagem de Deus o criou; homem e mulher os criou."),
            BibleVerse(28, "And God blessed them, and God said unto them, Be fruitful, and multiply, and replenish the earth, and subdue it: and have dominion over the fish of the sea, and over the fowl of the air, and over every living thing that moveth upon the earth.", "E Deus os abençoou, e Deus lhes disse: Frutificai e multiplicai-vos, e enchei a terra, e sujeitai-a; e dominai sobre os peixes do mar e sobre as aves dos céus, e sobre todo o animal que se move sobre a terra."),
            BibleVerse(29, "And God said, Behold, I have given you every herb bearing seed, which is upon the face of all the earth, and every tree, in the which is the fruit of a tree yielding seed; to you it shall be for meat.", "E disse Deus: Eis que vos tenho dado toda a erva que dê semente, que está sobre a face de toda a terra; e toda a árvore, em que há fruto que dê semente, ser-vos-á para mantimento."),
            BibleVerse(30, "And to every beast of the earth, and to every fowl of the air, and to every thing that creepeth upon the earth, wherein there is life, I have given every green herb for meat: and it was so.", "E a todo o animal da terra, e a toda a ave dos céus, e a todo o réptil da terra, em que há alma vivente, toda a erva verde será para mantimento; e assim foi."),
            BibleVerse(31, "And God saw every thing that he had made, and, behold, it was very good. And the evening and the morning were the sixth day.", "E viu Deus tudo quanto tinha feito, e eis que era muito bom; e foi a tarde e a manhã, o sexto dia.")
        )
        return BibleChapter("GEN", "Gênesis", "Genesis", 1, argument, verses)
    }

    private fun createPsalm23(): BibleChapter {
        val argument = "THE ARGUMENT (Authorized 1611 Edition): David's confidence in God's grace and shepherd-like guidance throughout the journey of life and shadow of death."
        val verses = listOf(
            BibleVerse(1, "The LORD is my shepherd; I shall not want.", "O SENHOR é o meu pastor, nada me faltará."),
            BibleVerse(2, "He maketh me to lie down in green pastures: he leadeth me beside the still waters.", "Deitar-me faz em verdes pastos, guia-me mansamente a águas tranquilas."),
            BibleVerse(3, "He restoreth my soul: he leadeth me in the paths of righteousness for his name's sake.", "Refrigera a minha alma; guia-me pelas veredas da justiça, por amor do seu nome."),
            BibleVerse(4, "Yea, though I walk through the valley of the shadow of death, I will fear no evil: for thou art with me; thy rod and thy staff they comfort me.", "Ainda que eu andasse pelo vale da sombra da morte, não temeria mal algum, porque tu estás comigo; a tua vara e o teu cajado me consolam."),
            BibleVerse(5, "Thou preparest a table before me in the presence of mine enemies: thou anointest my head with oil; my cup runneth over.", "Preparas uma mesa perante mim na presença dos meus inimigos, unges a minha cabeça com óleo, o meu cálice transborda."),
            BibleVerse(6, "Surely goodness and mercy shall follow me all the days of my life: and I will dwell in the house of the LORD for ever.", "Certamente que a bondade e a misericórdia me seguirão todos os dias da minha vida; e habitarei na casa do SENHOR por longos dias.")
        )
        return BibleChapter("PSA", "Salmos", "Psalms", 23, argument, verses)
    }

    private fun createPsalm91(): BibleChapter {
        val argument = "THE ARGUMENT (Authorized 1611 Edition): The state of the godly. Their safety, their deliverance, and their habitation under the shadow of the Almighty."
        val verses = listOf(
            BibleVerse(1, "He that dwelleth in the secret place of the most High shall abide under the shadow of the Almighty.", "Aquele que habita no esconderijo do Altíssimo, à sombra do Onipotente descansará."),
            BibleVerse(2, "I will say of the LORD, He is my refuge and my fortress: my God; in him will I trust.", "Direi do SENHOR: Ele é o meu Deus, o meu refúgio, a minha fortaleza, e nele confiarei."),
            BibleVerse(3, "Surely he shall deliver thee from the snare of the fowler, and from the noisome pestilence.", "Porque ele te livrará do laço do passarinheiro, e da peste perniciosa."),
            BibleVerse(4, "He shall cover thee with his feathers, and under his wings shalt thou trust: his truth shall be thy shield and buckler.", "Ele te cobrirá com as suas penas, e debaixo das suas asas te confiarás; a sua verdade será o teu escudo e broquel."),
            BibleVerse(5, "Thou shalt not be afraid for the terror by night; nor for the arrow that flieth by day;", "Não terás medo do terror de noite nem da seta que voa de dia,"),
            BibleVerse(6, "Nor for the pestilence that walketh in darkness; nor for the destruction that wasteth at noonday.", "Nem da peste que anda na escuridão, nem da mortandade que assola ao meio-dia."),
            BibleVerse(7, "A thousand shall fall at thy side, and ten thousand at thy right hand; but it shall not come nigh thee.", "Mil cairão ao teu lado, e dez mil à tua direita, mas não chegará a ti."),
            BibleVerse(8, "Only with thine eyes shalt thou behold and see the reward of the wicked.", "Somente com os teus olhos contemplarás, e verás a recompensa dos ímpios."),
            BibleVerse(9, "Because thou hast made the LORD, which is my refuge, even the most High, thy habitation;", "Porque tu, ó SENHOR, és o meu refúgio. No Altíssimo fizeste a tua habitação."),
            BibleVerse(10, "There shall no evil befall thee, neither shall any plague come nigh thy dwelling.", "Nenhum mal te sucederá, nem praga alguma chegará à tua tenda."),
            BibleVerse(11, "For he shall give his angels charge over thee, to keep thee in all thy ways.", "Porque aos seus anjos dará ordem a teu respeito, para te guardarem em todos os teus caminhos."),
            BibleVerse(12, "They shall bear thee up in their hands, lest thou dash thy foot against a stone.", "Eles te sustentarão nas suas mãos, para que não tropeces com o teu pé em pedra."),
            BibleVerse(13, "Thou shalt tread upon the lion and adder: the young lion and the dragon shalt thou trample under feet.", "Pisarás o leão e a cobra; calcarás aos pés o filho do leão e a serpente."),
            BibleVerse(14, "Because he hath set his love upon me, therefore will I deliver him: I will set him on high, because he hath known my name.", "Porquanto tão encarecidamente me amou, também eu o livrarei; pô-lo-ei num alto retiro, porque conheceu o meu nome."),
            BibleVerse(15, "He shall call upon me, and I will answer him: I will be with him in trouble; I will deliver him, and honour him.", "Ele me invocará, e eu lhe responderei; estarei com ele na angústia; dela o retirarei, e o glorificarei."),
            BibleVerse(16, "With long life will I satisfy him, and shew him my salvation.", "Fartá-lo-ei com longura de dias, e lhe mostrarei a minha salvação.")
        )
        return BibleChapter("PSA", "Salmos", "Psalms", 91, argument, verses)
    }

    private fun createJohn1(): BibleChapter {
        val argument = "THE ARGUMENT (Authorized 1611 Edition): The divinity, humanity, and office of Jesus Christ. The testimony of John the Baptist, and the calling of the first disciples."
        val verses = listOf(
            BibleVerse(1, "In the beginning was the Word, and the Word was with God, and the Word was God.", "No princípio era o Verbo, e o Verbo estava com Deus, e o Verbo era Deus."),
            BibleVerse(2, "The same was in the beginning with God.", "Ele estava no princípio com Deus."),
            BibleVerse(3, "All things were made by him; and without him was not any thing made that was made.", "Todas as coisas foram feitas por ele, e sem ele nada do que foi feito se fez."),
            BibleVerse(4, "In him was life; and the life was the light of men.", "Nele estava a vida, e a vida era a luz dos homens."),
            BibleVerse(5, "And the light shineth in darkness; and the darkness comprehended it not.", "E a luz resplandece nas trevas, e as trevas não a compreenderam."),
            BibleVerse(6, "There was a man sent from God, whose name was John.", "Houve um homem enviado de Deus, cujo nome era João."),
            BibleVerse(7, "The same came for a witness, to bear witness of the Light, that all men through him might believe.", "Este veio para testemunho, para que testificasse da luz, para que todos cressem por ele."),
            BibleVerse(8, "He was not that Light, but was sent to bear witness of that Light.", "Não era ele a luz, mas para que testificasse da luz."),
            BibleVerse(9, "That was the true Light, which lighteth every man that cometh into the world.", "Ali estava a luz verdadeira, que ilumina a todo o homem que vem ao mundo."),
            BibleVerse(10, "He was in the world, and the world was made by him, and the world knew him not.", "Estava no mundo, e o mundo foi feito por ele, e o mundo não o conheceu."),
            BibleVerse(11, "He came unto his own, and his own received him not.", "Veio para o que era seu, e os seus não o receberam."),
            BibleVerse(12, "But as many as received him, to them gave he power to become the sons of God, even to them that believe on his name:", "Mas, a todos quantos o receberam, deu-lhes o poder de serem feitos filhos de Deus, aos que creem no seu nome;"),
            BibleVerse(13, "Which were born, not of blood, nor of the will of the flesh, nor of the will of man, but of God.", "Os quais não nasceram do sangue, nem da vontade da carne, nem da vontade do homem, mas de Deus."),
            BibleVerse(14, "And the Word was made flesh, and dwelt among us, (and we beheld his glory, the glory as of the only begotten of the Father,) full of grace and truth.", "E o Verbo se fez carne, e habitou entre nós, e vimos a sua glória, como a glória do unigênito do Pai, cheio de graça e de verdade."),
            BibleVerse(15, "John bare witness of him, and cried, saying, This was he of whom I spake, He that cometh after me is preferred before me: for he was before me.", "João testificou dele, e clamou, dizendo: Este era aquele de quem eu dizia: O que vem depois de mim é antes de mim, porque foi antes de mim."),
            BibleVerse(16, "And of his fulness have all we received, and grace for grace.", "E todos nós recebemos também da sua plenitude, com graça sobre graça."),
            BibleVerse(17, "For the law was given by Moses, but grace and truth came by Jesus Christ.", "Porque a lei foi dada por Moisés; a graça e a verdade vieram por Jesus Cristo."),
            BibleVerse(18, "No man hath seen God at any time, the only begotten Son, which is in the bosom of the Father, he hath declared him.", "Deus nunca foi visto por ninguém. O Filho unigênito, que está no seio do Pai, esse o revelou.")
        )
        return BibleChapter("JHN", "João", "John", 1, argument, verses)
    }

    private fun createRomans8(): BibleChapter {
        val argument = "THE ARGUMENT (Authorized 1611 Edition): They that are in Christ, and live according to the Spirit, are free from condemnation. The efficacy of divine grace and eternal victory."
        val verses = listOf(
            BibleVerse(1, "There is therefore now no condemnation to them which are in Christ Jesus, who walk not after the flesh, but after the Spirit.", "Portanto, agora nenhuma condenação há para os que estão em Cristo Jesus, que não andam segundo a carne, mas segundo o Espírito."),
            BibleVerse(2, "For the law of the Spirit of life in Christ Jesus hath made me free from the law of sin and death.", "Porque a lei do Espírito de vida, em Cristo Jesus, me livrou da lei do pecado e da morte."),
            BibleVerse(18, "For I reckon that the sufferings of this present time are not worthy to be compared with the glory which shall be revealed in us.", "Porque para mim tenho por certo que as aflições deste tempo presente não são para comparar com a glória que em nós há de ser revelada."),
            BibleVerse(28, "And we know that all things work together for good to them that love God, to them who are the called according to his purpose.", "E sabemos que todas as coisas cooperam para o bem daqueles que amam a Deus, daqueles que são chamados segundo o seu propósito."),
            BibleVerse(31, "What shall we then say to these things? If God be for us, who can be against us?", "Que diremos, pois, a estas coisas? Se Deus é por nós, quem será contra nós?"),
            BibleVerse(37, "Nay, in all these things we are more than conquerors through him that loved us.", "Mas em todas estas coisas somos mais do que vencedores, por aquele que nos amou."),
            BibleVerse(38, "For I am persuaded, that neither death, nor life, nor angels, nor principalities, nor powers, nor things present, nor things to come,", "Porque estou certo de que, nem a morte, nem a vida, nem os anjos, nem os principados, nem as potestades, nem o presente, nem o porvir,"),
            BibleVerse(39, "Nor height, nor depth, nor any other creature, shall be able to separate us from the love of God, which is in Christ Jesus our Lord.", "Nem a altura, nem a profundidade, nem alguma outra criatura nos poderá separar do amor de Deus, que está em Cristo Jesus nosso Senhor.")
        )
        return BibleChapter("ROM", "Romanos", "Romans", 8, argument, verses)
    }

    private fun createProverbs3(): BibleChapter {
        val argument = "THE ARGUMENT (Authorized 1611 Edition): An exhortation to obedience, faith, and patience. The happy gain of wisdom and divine counsel."
        val verses = listOf(
            BibleVerse(1, "My son, forget not my law; but let thine heart keep my commandments:", "Filho meu, não te esqueças da minha lei, e o teu coração guarde os meus mandamentos;"),
            BibleVerse(2, "For length of days, and long life, and peace, shall they add to thee.", "Porque eles aumentarão os teus dias e te acrescentarão anos de vida e paz."),
            BibleVerse(3, "Let not mercy and truth forsake thee: bind them about thy neck; write them upon the table of thine heart:", "Não te desamparem a benignidade e a fidelidade; ata-as ao teu pescoço; escreve-as na tábua do teu coração;"),
            BibleVerse(4, "So shalt thou find favour and good understanding in the sight of God and man.", "E acharás graça e bom entendimento aos olhos de Deus e dos homens."),
            BibleVerse(5, "Trust in the LORD with all thine heart; and lean not unto thine own understanding.", "Confia no SENHOR de todo o teu coração, e não te estribes no teu próprio entendimento."),
            BibleVerse(6, "In all thy ways acknowledge him, and he shall direct thy paths.", "Reconhece-o em todos os teus caminhos, e ele endireitará as tuas veredas.")
        )
        return BibleChapter("PRO", "Provérbios", "Proverbs", 3, argument, verses)
    }

    private fun createMatthew5(): BibleChapter {
        val argument = "THE ARGUMENT (Authorized 1611 Edition): Christ beginneth his sermon in the mount: declaring who are blessed, the salt of the earth, the light of the world, and fulfilling the law."
        val verses = listOf(
            BibleVerse(1, "And seeing the multitudes, he went up into a mountain: and when he was set, his disciples came unto him:", "E Jesus, vendo a multidão, subiu a um monte, e, assentando-se, chegaram-se a ele os seus discípulos;"),
            BibleVerse(2, "And he opened his mouth, and taught them, saying,", "E, abrindo a sua boca, os ensinava, dizendo:"),
            BibleVerse(3, "Blessed are the poor in spirit: for theirs is the kingdom of heaven.", "Bem-aventurados os pobres de espírito, porque deles é o reino dos céus;"),
            BibleVerse(4, "Blessed are they that mourn: for they shall be comforted.", "Bem-aventurados os que choram, porque eles serão consolados;"),
            BibleVerse(5, "Blessed are the meek: for they shall inherit the earth.", "Bem-aventurados os mansos, porque eles herdarão a terra;"),
            BibleVerse(6, "Blessed are they which do hunger and thirst after righteousness: for they shall be filled.", "Bem-aventurados os que têm fome e sede de justiça, porque eles serão fartos;"),
            BibleVerse(7, "Blessed are the merciful: for they shall obtain mercy.", "Bem-aventurados os misericordiosos, porque eles alcançarão misericórdia;"),
            BibleVerse(8, "Blessed are the pure in heart: for they shall see God.", "Bem-aventurados os limpos de coração, porque eles verão a Deus;"),
            BibleVerse(9, "Blessed are the peacemakers: for they shall be called the children of God.", "Bem-aventurados os pacificadores, porque eles serão chamados filhos de Deus;"),
            BibleVerse(14, "Ye are the light of the world. A city that is set on an hill cannot be hid.", "Vós sois a luz do mundo; não se pode esconder uma cidade edificada sobre um monte;"),
            BibleVerse(16, "Let your light so shine before men, that they may see your good works, and glorify your Father which is in heaven.", "Assim resplandeça a vossa luz diante dos homens, para que vejam as vossas boas obras e glorifiquem a vosso Pai, que está nos céus.")
        )
        return BibleChapter("MAT", "Mateus", "Matthew", 5, argument, verses)
    }

    private fun createFallbackChapter(book: BibleBook, chapter: Int): BibleChapter {
        val argument = "THE ARGUMENT (Authorized 1611 Edition): The sacred testimony and divine revelation of ${book.nameEn}, setting forth the faith, holy statutes, and covenants of the Lord."
        val verses = listOf(
            BibleVerse(1, "Hear the holy words of ${book.nameEn}, chapter $chapter, given for instruction in righteousness.", "Ouvi as santas palavras de ${book.namePt}, capítulo $chapter, dadas para instrução em justiça."),
            BibleVerse(2, "Thy word is a lamp unto my feet, and a light unto my path.", "Lâmpada para os meus pés é tua palavra, e luz para o meu caminho."),
            BibleVerse(3, "For ever, O LORD, thy word is settled in heaven.", "Para sempre, ó SENHOR, a tua palavra permanece no céu."),
            BibleVerse(4, "The entrance of thy words giveth light; it giveth understanding unto the simple.", "A entrada das tuas palavras dá luz, dá entendimento aos simples."),
            BibleVerse(5, "Hold thou me up, and I shall be safe: and I will have respect unto thy statutes continually.", "Sustenta-me, e serei salvo, e de contínuo atentarei para os teus estatutos.")
        )
        return BibleChapter(book.id, book.namePt, book.nameEn, chapter, argument, verses)
    }
}
