package com.shwevoice.launcher

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.shwevoice.launcher.nlp.BurmeseCommandParser
import com.shwevoice.launcher.service.AppControlAccessibilityService
import com.shwevoice.launcher.service.BurmeseVoiceService

class MainActivity : ComponentActivity() {

    private val micPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }

        setContent {
            MaterialTheme {
                ShweVoiceHomeScreen(
                    onStartVoiceCommand = {
                        val intent = Intent(this, BurmeseVoiceService::class.java).apply {
                            action = BurmeseVoiceService.ACTION_START_LISTENING
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(intent)
                        } else {
                            startService(intent)
                        }
                    },
                    onOpenAccessibilitySettings = {
                        startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                    },
                    isAccessibilityEnabled = {
                        AppControlAccessibilityService.instance != null
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShweVoiceHomeScreen(
    onStartVoiceCommand: () -> Unit,
    onOpenAccessibilitySettings: () -> Unit,
    isAccessibilityEnabled: () -> Boolean
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var lastTranscript by remember { mutableStateOf("အသံဖြင့် အမိန့်ပေးရန် ခလုတ်ကို နှိပ်ပါ") }
    var lastFeedback by remember { mutableStateOf("ဥပမာ - \"YouTube ဖွင့်ပေးပါ\" သို့မဟုတ် \"Facebook ပိတ်လိုက်\"") }
    var accessibilityReady by remember { mutableStateOf(isAccessibilityEnabled()) }

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                lastTranscript = intent?.getStringExtra(BurmeseVoiceService.EXTRA_TRANSCRIPT) ?: lastTranscript
                lastFeedback = intent?.getStringExtra(BurmeseVoiceService.EXTRA_FEEDBACK) ?: lastFeedback
            }
        }
        val filter = IntentFilter(BurmeseVoiceService.BROADCAST_TRANSCRIPT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(receiver, filter)
        }
        onDispose { context.unregisterReceiver(receiver) }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("ရွှေအသံ App ထိန်းချုပ်စနစ်") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (accessibilityReady) "Accessibility Service: ဖွင့်ထားပြီး" else "Accessibility Service ဖွင့်ရန် လိုအပ်ပါသည်",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        onOpenAccessibilitySettings()
                        accessibilityReady = isAccessibilityEnabled()
                    }) {
                        Text("Accessibility ဆက်တင်ဖွင့်ရန်")
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = lastTranscript, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = lastFeedback, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Button(
                onClick = onStartVoiceCommand,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("မြန်မာအသံဖြင့် အမိန့်ပေးရန် နှိပ်ပါ (ဟေ့ ရွှေအသံ)")
            }

            Text("သတ်မှတ်ထားသော App စာရင်း", style = MaterialTheme.typography.titleSmall)

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(BurmeseCommandParser.defaultCatalog) { app ->
                    ListItem(
                        headlineContent = { Text("${app.burmeseName} (${app.name})") },
                        supportingContent = { Text(app.packageName) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
