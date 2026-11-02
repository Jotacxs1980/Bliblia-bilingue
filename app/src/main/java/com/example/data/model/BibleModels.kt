package com.example.data.model

enum class Testament {
    OLD, NEW
}

enum class BibleCategory(val labelPt: String) {
    LAW("Lei / Pentateuco"),
    HISTORY("Históricos"),
    POETRY("Poéticos & Sabedoria"),
    MAJOR_PROPHETS("Profetas Maiores"),
    MINOR_PROPHETS("Profetas Menores"),
    GOSPELS("Evangelhos"),
    ACTS("Histórico NT"),
    PAULINE_EPISTLES("Epístolas Paulinas"),
    GENERAL_EPISTLES("Epístolas Gerais"),
    APOCALYPSE("Profecia")
}

data class BibleBook(
    val id: String,
    val namePt: String,
    val nameEn: String,
    val abbreviation: String,
    val testament: Testament,
    val category: BibleCategory,
    val totalChapters: Int
)

data class BibleVerse(
    val number: Int,
    val textEn: String,
    val textPt: String
)

data class BibleChapter(
    val bookId: String,
    val bookNamePt: String,
    val bookNameEn: String,
    val chapter: Int,
    val argument1611: String, // The Argument of 1611
    val verses: List<BibleVerse>
)

enum class AppThemeMode {
    SEPIA, DARK, LIGHT
}

enum class AudioVoiceMode(val labelPt: String) {
    BILINGUAL("Bilíngue (EN + PT)"),
    ENGLISH_ONLY("Somente Inglês (KJV)"),
    PORTUGUESE_ONLY("Somente Português")
}

enum class AppScreen {
    HOME,
    READER,
    BOOKS,
    STATS,
    SETTINGS
}

data class ReadingStats(
    val totalVersesRead: Int = 0,
    val chaptersCompleted: Set<String> = emptySet(), // Format: "GEN_1"
    val currentStreakDays: Int = 0,
    val favoriteVerses: Set<String> = emptySet(),
    val lastBookId: String = "GEN",
    val lastChapter: Int = 1,
    val lastVerse: Int = 1
)

data class DailyVerse(
    val bookId: String,
    val bookNamePt: String,
    val bookNameEn: String,
    val chapter: Int,
    val verseNumber: Int,
    val textEn: String,
    val textPt: String
)
