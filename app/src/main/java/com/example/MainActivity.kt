package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppScreen
import com.example.data.model.BibleBook
import com.example.ui.BibleViewModel
import com.example.ui.components.AudioPlayerBar
import com.example.ui.components.BookSelectorDialog
import com.example.ui.components.HtmlExportDialog
import com.example.ui.components.SettingsDialog
import com.example.ui.components.StatsDialog
import com.example.ui.components.VerseItem
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.BibliaTheme
import com.example.ui.theme.LocalBibleCustomColors

class MainActivity : ComponentActivity() {

    private val viewModel: BibleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.themeMode.collectAsState()

            BibliaTheme(themeMode = themeMode) {
                BibleAppMain(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BibleAppMain(viewModel: BibleViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentChapter by viewModel.currentChapter.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val fontSize by viewModel.fontSize.collectAsState()
    val audioSpeed by viewModel.audioSpeed.collectAsState()
    val voiceMode by viewModel.voiceMode.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentVerseIndex by viewModel.currentVerseIndex.collectAsState()
    val stats by viewModel.readingStats.collectAsState()
    val downloadedBooks by viewModel.downloadedBooks.collectAsState()
    val downloadingBookId by viewModel.downloadingBookId.collectAsState()
    val downloadingChapterKey by viewModel.downloadingChapterKey.collectAsState()
    val downloadProgress by viewModel.downloadProgress.collectAsState()

    var showBookSelectorDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showStatsDialog by remember { mutableStateOf(false) }
    var showHtmlExportDialog by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val customColors = LocalBibleCustomColors.current

    // BackHandler: Returns to Home if user is in any secondary screen (READER, BOOKS, STATS)
    BackHandler(enabled = currentScreen != AppScreen.HOME) {
        viewModel.navigateBack()
    }

    // Auto-scroll to active playing verse in reader
    LaunchedEffect(currentVerseIndex) {
        if (currentScreen == AppScreen.READER && currentVerseIndex >= 0 && currentVerseIndex < currentChapter.verses.size) {
            listState.animateScrollToItem((currentVerseIndex + 2).coerceAtLeast(0))
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    when (currentScreen) {
                        AppScreen.HOME -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "1611",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Bíblia 1611 Bilíngue",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        AppScreen.READER -> {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                                    .clickable { showBookSelectorDialog = true }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                    .testTag("book_chapter_selector_button"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Book,
                                    contentDescription = "Selecionar Livro",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${currentChapter.bookNamePt} ${currentChapter.chapter}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        AppScreen.BOOKS -> {
                            Text(
                                text = "Índice dos 66 Livros",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        AppScreen.STATS -> {
                            Text(
                                text = "Progresso de Leitura",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        AppScreen.SETTINGS -> {
                            Text(
                                text = "Configurações",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                navigationIcon = {
                    // Functional BACK BUTTON when not on Home screen
                    if (currentScreen != AppScreen.HOME) {
                        IconButton(
                            onClick = { viewModel.navigateBack() },
                            modifier = Modifier.testTag("back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Voltar para Início",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                actions = {
                    if (currentScreen == AppScreen.READER) {
                        IconButton(
                            onClick = { viewModel.previousChapter() },
                            modifier = Modifier.testTag("prev_chapter_header_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Capítulo Anterior",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        IconButton(
                            onClick = { viewModel.nextChapter() },
                            modifier = Modifier.testTag("next_chapter_header_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Próximo Capítulo",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = { showHtmlExportDialog = true },
                        modifier = Modifier.testTag("github_html_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = "Versão HTML para GitHub",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = { showSettingsDialog = true },
                        modifier = Modifier.testTag("settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configurações"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Column {
                // Floating Audio Control Bar
                AnimatedVisibility(
                    visible = isPlaying || currentVerseIndex >= 0,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    AudioPlayerBar(
                        isPlaying = isPlaying,
                        currentVerseIndex = currentVerseIndex,
                        totalVerses = currentChapter.verses.size,
                        currentSpeed = audioSpeed,
                        currentVoiceMode = voiceMode,
                        onTogglePlay = { viewModel.toggleAudio() },
                        onNext = { viewModel.nextVerse() },
                        onPrevious = { viewModel.previousVerse() },
                        onStop = { viewModel.stopAudio() },
                        onChangeSpeed = { viewModel.setAudioSpeed(it) },
                        onChangeVoiceMode = { viewModel.setVoiceMode(it) }
                    )
                }

                // Bottom Navigation Bar
                NavigationBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp
                ) {
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.HOME,
                        onClick = { viewModel.navigateTo(AppScreen.HOME) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Início") },
                        label = { Text("Início") },
                        modifier = Modifier.testTag("nav_home")
                    )
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.READER,
                        onClick = { viewModel.navigateTo(AppScreen.READER) },
                        icon = { Icon(Icons.Default.MenuBook, contentDescription = "Leitor") },
                        label = { Text("Leitor") },
                        modifier = Modifier.testTag("nav_reader")
                    )
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.BOOKS,
                        onClick = { viewModel.navigateTo(AppScreen.BOOKS) },
                        icon = { Icon(Icons.Default.Book, contentDescription = "66 Livros") },
                        label = { Text("Livros") },
                        modifier = Modifier.testTag("nav_books")
                    )
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.STATS,
                        onClick = { viewModel.navigateTo(AppScreen.STATS) },
                        icon = { Icon(Icons.Default.CheckCircle, contentDescription = "Progresso") },
                        label = { Text("Progresso") },
                        modifier = Modifier.testTag("nav_stats")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.HOME -> {
                    HomeScreen(
                        readingStats = stats,
                        dailyVerse = viewModel.dailyVerse,
                        allBooks = viewModel.allBooks,
                        downloadedCount = downloadedBooks.size,
                        onContinueReading = { viewModel.continueReading() },
                        onOpenBookChapter = { book, ch -> viewModel.openReaderFor(book, ch) },
                        onOpenBooksCatalog = { viewModel.navigateTo(AppScreen.BOOKS) },
                        onOpenStats = { viewModel.navigateTo(AppScreen.STATS) },
                        onOpenSettings = { showSettingsDialog = true },
                        onOpenHtmlExport = { showHtmlExportDialog = true },
                        onPlayDailyVerse = { dv ->
                            val verse = com.example.data.model.BibleVerse(dv.verseNumber, dv.textEn, dv.textPt)
                            viewModel.playSingleVerse(verse)
                        }
                    )
                }

                AppScreen.READER -> {
                    // Bilingual Verse Reader Screen
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item(key = "header_title") {
                            Spacer(modifier = Modifier.height(6.dp))
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${currentChapter.bookNamePt} (Chapter ${currentChapter.chapter})",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "The Authorized King James Version 1611 • Bilíngue",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = { viewModel.toggleAudio() },
                                        shape = RoundedCornerShape(20.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                        ),
                                        modifier = Modifier.testTag("listen_chapter_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isPlaying) "Pausar" else "Ouvir",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = { viewModel.markCurrentChapterCompleted() },
                                        shape = RoundedCornerShape(20.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = if (viewModel.isCurrentChapterCompleted()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (viewModel.isCurrentChapterCompleted()) "Lido ✓" else "Marcar Lido"
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val currentBookObj = viewModel.allBooks.find { it.id == currentChapter.bookId } ?: viewModel.allBooks.first()
                                    val chapterKey = "${currentChapter.bookId}_${currentChapter.chapter}"
                                    val isDownloadingThisChapter = downloadingChapterKey == chapterKey
                                    val isChapterSaved = viewModel.isChapterDownloaded(currentChapter.bookId, currentChapter.chapter)

                                    // Single chapter download button
                                    OutlinedButton(
                                        onClick = { viewModel.downloadSingleChapter(currentBookObj, currentChapter.chapter) },
                                        enabled = !isDownloadingThisChapter && downloadingBookId == null,
                                        shape = RoundedCornerShape(20.dp),
                                        modifier = Modifier.testTag("download_chapter_button")
                                    ) {
                                        if (isDownloadingThisChapter) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(14.dp),
                                                strokeWidth = 2.dp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Baixando...", style = MaterialTheme.typography.labelSmall)
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.Download,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = if (isChapterSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isChapterSaved) "Capítulo Salvo ✓" else "Baixar Capítulo",
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    }

                                    // Whole book download button
                                    val isDownloadingThisBook = downloadingBookId == currentChapter.bookId
                                    val isBookSaved = downloadedBooks.contains(currentChapter.bookId)

                                    OutlinedButton(
                                        onClick = { viewModel.downloadBook(currentBookObj) },
                                        enabled = !isDownloadingThisBook && downloadingChapterKey == null,
                                        shape = RoundedCornerShape(20.dp),
                                        modifier = Modifier.testTag("download_book_button")
                                    ) {
                                        if (isDownloadingThisBook) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(14.dp),
                                                strokeWidth = 2.dp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("${(downloadProgress * 100).toInt()}%", style = MaterialTheme.typography.labelSmall)
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.Download,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = if (isBookSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isBookSaved) "Livro Salvo ✓" else "Baixar Livro Inteiro",
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // The Argument 1611 Card
                        item(key = "the_argument_1611") {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                                        shape = RoundedCornerShape(16.dp)
                                    ),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = customColors.argumentBackground
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primary)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "THE ARGUMENT (1611 PREFACE)",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = currentChapter.argument1611,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontFamily = FontFamily.Serif,
                                        fontStyle = FontStyle.Italic,
                                        lineHeight = 22.sp,
                                        color = customColors.argumentText
                                    )
                                }
                            }
                        }

                        // Verses List
                        itemsIndexed(
                            items = currentChapter.verses,
                            key = { _, verse -> "verse_${currentChapter.bookId}_${currentChapter.chapter}_${verse.number}" }
                        ) { index, verse ->
                            val isCurrent = isPlaying && currentVerseIndex == index
                            val isFav = viewModel.isFavorite(verse.number)

                            VerseItem(
                                verse = verse,
                                bookNameEn = currentChapter.bookNameEn,
                                bookNamePt = currentChapter.bookNamePt,
                                chapterNumber = currentChapter.chapter,
                                fontSize = fontSize,
                                isCurrentPlaying = isCurrent,
                                isFavorite = isFav,
                                onPlaySingle = { viewModel.playSingleVerse(verse) },
                                onToggleFavorite = { viewModel.toggleFavorite(verse.number) }
                            )
                        }

                        // Footer Navigation between chapters
                        item(key = "footer_nav") {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 80.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.previousChapter() },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Anterior")
                                }

                                Button(
                                    onClick = { viewModel.nextChapter() },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Próximo")
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                AppScreen.BOOKS -> {
                    // Full Books Catalog Screen
                    BookSelectorDialog(
                        books = viewModel.allBooks,
                        currentBookId = currentChapter.bookId,
                        currentChapter = currentChapter.chapter,
                        completedChapters = stats.chaptersCompleted,
                        downloadedBooks = downloadedBooks,
                        downloadingBookId = downloadingBookId,
                        downloadProgress = downloadProgress,
                        onDownloadBook = { viewModel.downloadBook(it) },
                        onDeleteDownloadedBook = { viewModel.deleteDownloadedBook(it) },
                        onSelectBookAndChapter = { book, ch ->
                            viewModel.openReaderFor(book, ch)
                        },
                        onDismiss = { viewModel.navigateBack() }
                    )
                }

                AppScreen.STATS -> {
                    // Full Stats Screen
                    StatsDialog(
                        stats = stats,
                        onMarkCurrentChapterComplete = { viewModel.markCurrentChapterCompleted() },
                        isCurrentChapterCompleted = viewModel.isCurrentChapterCompleted(),
                        onResetProgress = { viewModel.resetProgress() },
                        onDismiss = { viewModel.navigateBack() }
                    )
                }

                AppScreen.SETTINGS -> {
                    // Settings Screen
                    SettingsDialog(
                        currentTheme = themeMode,
                        currentFontSize = fontSize,
                        currentVoiceMode = voiceMode,
                        currentSpeed = audioSpeed,
                        onSelectTheme = { viewModel.setTheme(it) },
                        onChangeFontSize = { viewModel.setFontSize(it) },
                        onChangeVoiceMode = { viewModel.setVoiceMode(it) },
                        onChangeSpeed = { viewModel.setAudioSpeed(it) },
                        onDismiss = { viewModel.navigateBack() }
                    )
                }
            }
        }
    }

    // Secondary dialogs (opened from header actions)
    if (showBookSelectorDialog) {
        BookSelectorDialog(
            books = viewModel.allBooks,
            currentBookId = currentChapter.bookId,
            currentChapter = currentChapter.chapter,
            completedChapters = stats.chaptersCompleted,
            downloadedBooks = downloadedBooks,
            downloadingBookId = downloadingBookId,
            downloadProgress = downloadProgress,
            onDownloadBook = { viewModel.downloadBook(it) },
            onDeleteDownloadedBook = { viewModel.deleteDownloadedBook(it) },
            onSelectBookAndChapter = { book, ch ->
                viewModel.selectBookAndChapter(book, ch)
                showBookSelectorDialog = false
            },
            onDismiss = { showBookSelectorDialog = false }
        )
    }

    if (showSettingsDialog) {
        SettingsDialog(
            currentTheme = themeMode,
            currentFontSize = fontSize,
            currentVoiceMode = voiceMode,
            currentSpeed = audioSpeed,
            onSelectTheme = { viewModel.setTheme(it) },
            onChangeFontSize = { viewModel.setFontSize(it) },
            onChangeVoiceMode = { viewModel.setVoiceMode(it) },
            onChangeSpeed = { viewModel.setAudioSpeed(it) },
            onDismiss = { showSettingsDialog = false }
        )
    }

    if (showStatsDialog) {
        StatsDialog(
            stats = stats,
            onMarkCurrentChapterComplete = { viewModel.markCurrentChapterCompleted() },
            isCurrentChapterCompleted = viewModel.isCurrentChapterCompleted(),
            onResetProgress = { viewModel.resetProgress() },
            onDismiss = { showStatsDialog = false }
        )
    }

    if (showHtmlExportDialog) {
        HtmlExportDialog(
            onDismiss = { showHtmlExportDialog = false }
        )
    }
}
