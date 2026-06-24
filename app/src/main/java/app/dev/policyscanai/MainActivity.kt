package app.dev.policyscanai

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import app.dev.policyscanai.data.local.ScanHistoryManager
import app.dev.policyscanai.data.model.ScanFinding
import app.dev.policyscanai.data.model.ScanHistoryItem
import app.dev.policyscanai.domain.AiFinding
import app.dev.policyscanai.domain.ScanReport
import app.dev.policyscanai.ui.components.UploadBottomSheet
import app.dev.policyscanai.ui.screens.*
import app.dev.policyscanai.ui.theme.PolicyScanTheme
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_PDF
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainApp()
        }
    }
}

sealed class Screen {
    data object Home : Screen()
    data object History : Screen()
    data object Laws : Screen()
    data object Settings : Screen()
    data object Upload : Screen()
    data object Paste : Screen()
    data object Scanning : Screen()
    data class Results(val report: ScanReport) : Screen()
}

@Composable
fun MainApp() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val historyManager = remember { ScanHistoryManager(context) }
    val textRecognizer = remember { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }
    
    // Theme Preference State
    val settingsPrefs = remember { context.getSharedPreferences("policy_scan_settings", Context.MODE_PRIVATE) }
    val systemInDarkTheme = isSystemInDarkTheme()
    var isDarkMode by remember { 
        mutableStateOf(settingsPrefs.getBoolean("dark_mode", systemInDarkTheme)) 
    }

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var uploadState by remember { mutableStateOf<UploadState>(UploadState.Idle) }
    var currentUri by remember { mutableStateOf<Uri?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    
    var pageTexts by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    var selectedPolicyType by remember { mutableStateOf("Privacy Policy") }
    var uploadedFileName by remember { mutableStateOf("Document") }

    // ML Kit Document Scanner
    val options = remember {
        GmsDocumentScannerOptions.Builder()
            .setGalleryImportAllowed(true)
            .setPageLimit(20)
            .setResultFormats(RESULT_FORMAT_JPEG, RESULT_FORMAT_PDF)
            .setScannerMode(SCANNER_MODE_FULL)
            .build()
    }
    val scanner = remember { GmsDocumentScanning.getClient(options) }
    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val scanningResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
            scanningResult?.let { res ->
                uploadedFileName = "Scanned Document"
                currentUri = res.pdf?.uri ?: res.pages?.firstOrNull()?.imageUri
                uploadState = UploadState.Processing
                currentScreen = Screen.Upload
                
                coroutineScope.launch {
                    val extracted = mutableMapOf<Int, String>()
                    val totalPages = res.pages?.size ?: 0
                    res.pages?.forEachIndexed { index, page ->
                        try {
                            val image = InputImage.fromFilePath(context, page.imageUri)
                            val visionText = textRecognizer.process(image).await()
                            if (visionText.text.isNotBlank()) {
                                extracted[index + 1] = visionText.text
                            }
                        } catch (e: Exception) {
                            // Skip page on error
                        }
                    }
                    
                    if (extracted.isEmpty()) {
                        uploadState = UploadState.Error("No text could be extracted from the scanned pages.")
                        Toast.makeText(context, "Text extraction failed", Toast.LENGTH_SHORT).show()
                    } else {
                        pageTexts = extracted
                        uploadState = UploadState.Preview(
                            fileName = uploadedFileName,
                            fileType = if (res.pdf != null) "PDF" else "IMAGE",
                            pageCount = totalPages,
                            fileSizeKb = 0L,
                            thumbnailUri = res.pages?.firstOrNull()?.imageUri
                        )
                        Toast.makeText(context, "Extracted text from $totalPages pages", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    val recentScans by remember(currentScreen) {
        mutableStateOf(historyManager.getAllScans().take(3).map {
            RecentScanData(it.fileName, it.policyType, it.overallRisk, it.findingsCount, it.scannedAtLabel)
        })
    }

    fun launchCamera() {
        val activity = context as? Activity
        if (activity != null) {
            scanner.getStartScanIntent(activity)
                .addOnSuccessListener { intentSender ->
                    scannerLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to start scanner: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Photo/Doc Launchers
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            currentUri = it
            uploadedFileName = app.dev.policyscanai.utils.getFileNameFromUri(context, it)
            uploadState = UploadState.Processing
            currentScreen = Screen.Upload
            
            coroutineScope.launch {
                try {
                    val image = InputImage.fromFilePath(context, it)
                    val visionText = textRecognizer.process(image).await()
                    if (visionText.text.isNotBlank()) {
                        pageTexts = mapOf(1 to visionText.text)
                        uploadState = UploadState.Preview(
                            fileName = uploadedFileName,
                            fileType = "IMAGE",
                            pageCount = 1,
                            fileSizeKb = app.dev.policyscanai.utils.getFileSizeKb(context, it),
                            thumbnailUri = it
                        )
                        Toast.makeText(context, "Text extracted successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        uploadState = UploadState.Error("No text found in the selected image.")
                    }
                } catch (e: Exception) {
                    uploadState = UploadState.Error("Failed to extract text: ${e.message}")
                }
            }
        }
    }

    val docPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            currentUri = it
            uploadedFileName = app.dev.policyscanai.utils.getFileNameFromUri(context, it)
            uploadState = UploadState.Processing
            currentScreen = Screen.Upload
            
            coroutineScope.launch {
                try {
                    val extracted = mutableMapOf<Int, String>()
                    val pfd: ParcelFileDescriptor? = context.contentResolver.openFileDescriptor(it, "r")
                    pfd?.let { fd ->
                        val renderer = PdfRenderer(fd)
                        val totalPages = renderer.pageCount
                        
                        for (i in 0 until totalPages) {
                            val page = renderer.openPage(i)
                            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                            val canvas = Canvas(bitmap)
                            canvas.drawColor(Color.WHITE)
                            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                            
                            val image = InputImage.fromBitmap(bitmap, 0)
                            val visionText = textRecognizer.process(image).await()
                            if (visionText.text.isNotBlank()) {
                                extracted[i + 1] = visionText.text
                            }
                            
                            page.close()
                        }
                        renderer.close()
                    }
                    pfd?.close()
                    
                    if (extracted.isEmpty()) {
                        uploadState = UploadState.Error("No text could be extracted from the PDF.")
                    } else {
                        pageTexts = extracted
                        uploadState = UploadState.Preview(
                            fileName = uploadedFileName,
                            fileType = "PDF",
                            pageCount = extracted.size,
                            fileSizeKb = app.dev.policyscanai.utils.getFileSizeKb(context, it),
                            thumbnailUri = null
                        )
                        Toast.makeText(context, "Extracted text from ${extracted.size} pages", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    uploadState = UploadState.Error("Failed to process PDF: ${e.message}")
                }
            }
        }
    }

    // Back Press Handling
    BackHandler(enabled = currentScreen != Screen.Home) {
        when (currentScreen) {
            Screen.Upload, Screen.Paste, Screen.History, Screen.Laws, Screen.Settings -> {
                currentScreen = Screen.Home
                uploadState = UploadState.Idle
                currentUri = null
            }
            Screen.Scanning -> {
                currentScreen = Screen.Upload
            }
            is Screen.Results -> {
                currentScreen = Screen.History
            }
            else -> {
                currentScreen = Screen.Home
            }
        }
    }

    PolicyScanTheme(darkTheme = isDarkMode) {
        // Screen Management
        when (val screen = currentScreen) {
            Screen.Home -> HomeScreen(
                onPickFile = { showBottomSheet = true },
                onOpenCamera = { launchCamera() },
                onNavigate = { route ->
                    currentScreen = when(route) {
                        "history" -> Screen.History
                        "laws" -> Screen.Laws
                        "settings" -> Screen.Settings
                        else -> Screen.Home
                    }
                },
                recentScans = recentScans
            )
            Screen.Upload -> UploadScreen(
                uploadState = uploadState,
                initialUri = currentUri,
                onBack = { 
                    currentScreen = Screen.Home
                    uploadState = UploadState.Idle
                    currentUri = null
                },
                onStartScan = { policyType ->
                    selectedPolicyType = policyType
                    currentScreen = Screen.Scanning
                }
            )
            Screen.Paste -> TextPasteScreen(
                onBack = { currentScreen = Screen.Home },
                onAnalyze = { text ->
                    if (text.isNotBlank()) {
                        coroutineScope.launch {
                            pageTexts = mapOf(1 to text)
                            uploadedFileName = "Pasted Text"
                            currentScreen = Screen.Upload
                            uploadState = UploadState.Preview(
                                fileName = uploadedFileName,
                                fileType = "TEXT",
                                pageCount = 1,
                                fileSizeKb = (text.length / 1024).toLong(),
                                thumbnailUri = null
                            )
                        }
                    }
                }
            )
            Screen.Scanning -> ScanningScreen(
                fileName = uploadedFileName,
                policyType = selectedPolicyType,
                pageTexts = pageTexts,
                onScanComplete = { report ->
                    // Save to history
                    val findings = report.findings.map { f ->
                        ScanFinding(
                            id = f.id,
                            title = f.title,
                            description = f.description,
                            riskLevel = f.riskLevel,
                            category = f.category,
                            pageNumber = f.page,
                            textSnippet = f.originalText
                        )
                    }
                    
                    val redCount = findings.count { it.riskLevel == "RED" }
                    val orangeCount = findings.count { it.riskLevel == "ORANGE" }
                    val yellowCount = findings.count { it.riskLevel == "YELLOW" }
                    
                    val newScan = ScanHistoryItem(
                        id = UUID.randomUUID().toString(),
                        fileName = uploadedFileName,
                        policyType = selectedPolicyType,
                        overallRisk = report.overallRisk,
                        findingsCount = findings.size,
                        redCount = redCount,
                        orangeCount = orangeCount,
                        yellowCount = yellowCount,
                        pageCount = pageTexts.size,
                        fileSizeKb = 0L,
                        scannedAtMillis = System.currentTimeMillis(),
                        scannedAtLabel = SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault()).format(Date()),
                        findings = findings,
                        summary = report.summaryPoints.joinToString("\n")
                    )
                    historyManager.saveScan(newScan)
                    
                    currentScreen = Screen.Results(report)
                },
                onCancelScan = {
                    currentScreen = Screen.Upload
                }
            )
            Screen.History -> HistoryScreen(
                onNavigate = { route ->
                    currentScreen = when(route) {
                        "home" -> Screen.Home
                        "laws" -> Screen.Laws
                        "settings" -> Screen.Settings
                        else -> Screen.History
                    }
                },
                onOpenScan = { scanId ->
                    val scan = historyManager.getScanById(scanId)
                    if (scan != null) {
                        // Convert ScanHistoryItem back to ScanReport
                        val report = ScanReport(
                            findings = scan.findings.map { f ->
                                AiFinding(
                                    id = f.id,
                                    title = f.title,
                                    description = f.description,
                                    originalText = f.textSnippet ?: "",
                                    simplified = "", // Not stored in history currently
                                    riskLevel = f.riskLevel,
                                    category = f.category,
                                    laws = emptyList(), // Not stored in history currently
                                    page = f.pageNumber,
                                    chunk = 0
                                )
                            },
                            summaryPoints = scan.summary.split("\n"),
                            overallRisk = scan.overallRisk,
                            headline = scan.fileName,
                            totalChunks = 0
                        )
                        currentScreen = Screen.Results(report)
                    }
                },
                onBack = {
                    currentScreen = Screen.Home
                }
            )
            Screen.Laws -> LawsScreen(
                onNavigate = { route ->
                    currentScreen = when(route) {
                        "home" -> Screen.Home
                        "history" -> Screen.History
                        "settings" -> Screen.Settings
                        else -> Screen.Laws
                    }
                },
                onBack = { currentScreen = Screen.Home }
            )
            Screen.Settings -> SettingsScreen(
                onNavigate = { route ->
                    currentScreen = when(route) {
                        "home" -> Screen.Home
                        "history" -> Screen.History
                        "laws" -> Screen.Laws
                        else -> Screen.Settings
                    }
                },
                onBack = { currentScreen = Screen.Home },
                isDarkMode = isDarkMode,
                onToggleDarkMode = { 
                    isDarkMode = it
                    settingsPrefs.edit().putBoolean("dark_mode", it).apply()
                }
            )
            is Screen.Results -> {
                ResultScreen(
                    report = screen.report,
                    onBack = { currentScreen = Screen.History },
                    onShare = { },
                    onNavigate = { route ->
                        currentScreen = when(route) {
                            "home" -> Screen.Home
                            "history" -> Screen.History
                            "laws" -> Screen.Laws
                            "settings" -> Screen.Settings
                            else -> Screen.Home
                        }
                    }
                )
            }
        }

        if (showBottomSheet) {
            UploadBottomSheet(
                onDismiss = { showBottomSheet = false },
                onPickImage = { 
                    showBottomSheet = false
                    photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) 
                },
                onPickDocument = { 
                    showBottomSheet = false
                    docPickerLauncher.launch("application/pdf") 
                },
                onPasteText = { 
                    showBottomSheet = false
                    currentScreen = Screen.Paste 
                }
            )
        }
    }
}
