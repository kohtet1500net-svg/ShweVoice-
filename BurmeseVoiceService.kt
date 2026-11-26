package com.shwevoice.launcher.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.core.app.NotificationCompat
import com.shwevoice.launcher.model.CommandAction
import com.shwevoice.launcher.nlp.BurmeseCommandParser
import java.util.Locale

class BurmeseVoiceService : Service(), TextToSpeech.OnInitListener {

    companion object {
        const val ACTION_START_LISTENING = "com.shwevoice.launcher.START_LISTENING"
        const val ACTION_STOP_LISTENING = "com.shwevoice.launcher.STOP_LISTENING"
        const val BROADCAST_TRANSCRIPT = "com.shwevoice.launcher.BROADCAST_TRANSCRIPT"
        const val EXTRA_TRANSCRIPT = "extra_transcript"
        const val EXTRA_FEEDBACK = "extra_feedback"
        private const val CHANNEL_ID = "shwevoice_listener_channel"
        private const val NOTIFICATION_ID = 1001
    }

    private var speechRecognizer: SpeechRecognizer? = null
    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification("မြန်မာအသံဖြင့် App ထိန်းချုပ်ရန် အသင့်ဖြစ်နေပါပြီ"))
        tts = TextToSpeech(this, this)
        initSpeechRecognizer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_LISTENING -> startListening()
            ACTION_STOP_LISTENING -> stopListening()
        }
        return START_STICKY
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val myanmarLocale = Locale("my", "MM")
            val result = tts?.setLanguage(myanmarLocale)
            isTtsReady = result != TextToSpeech.LANG_MISSING_DATA &&
                result != TextToSpeech.LANG_NOT_SUPPORTED
        }
    }

    private fun initSpeechRecognizer() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) return
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onError(error: Int) {}
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val spoken = matches?.firstOrNull().orEmpty()
                    if (spoken.isNotBlank()) {
                        handleBurmeseTranscript(spoken)
                    }
                }
            })
        }
    }

    private fun startListening() {
        val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "my-MM")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "my-MM")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        }
        speechRecognizer?.startListening(recognizerIntent)
    }

    private fun stopListening() {
        speechRecognizer?.stopListening()
    }

    private fun handleBurmeseTranscript(transcript: String) {
        val parsed = BurmeseCommandParser.parse(transcript)
        val accessibility = AppControlAccessibilityService.instance

        when (parsed.action) {
            CommandAction.OPEN_APP -> {
                val target = parsed.targetApp
                if (target != null) {
                    val launched = accessibility?.launchApp(target.packageName) ?: run {
                        val intent = packageManager.getLaunchIntentForPackage(target.packageName)
                        if (intent != null) {
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            true
                        } else {
                            false
                        }
                    }
                    if (!launched) {
                        speakFeedback("${target.burmeseName} App ကို ဖုန်းထဲတွင် မတွေ့ပါ။")
                        broadcastUpdate(transcript, "${target.burmeseName} ကို ဖုန်းထဲတွင် မတွေ့ပါ။")
                        return
                    }
                }
            }
            CommandAction.CLOSE_APP -> {
                accessibility?.closeCurrentOrTargetApp(parsed.targetApp?.packageName)
            }
            CommandAction.CLOSE_ALL_APPS -> {
                accessibility?.closeAllRecentApps()
            }
            CommandAction.GO_HOME -> {
                accessibility?.goHome()
            }
            CommandAction.UNKNOWN -> {}
        }

        speakFeedback(parsed.spokenFeedback)
        broadcastUpdate(transcript, parsed.spokenFeedback)
    }

    private fun speakFeedback(text: String) {
        if (isTtsReady) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "shwevoice_reply")
        }
    }

    private fun broadcastUpdate(transcript: String, feedback: String) {
        sendBroadcast(Intent(BROADCAST_TRANSCRIPT).apply {
            putExtra(EXTRA_TRANSCRIPT, transcript)
            putExtra(EXTRA_FEEDBACK, feedback)
        })
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "ShweVoice Launcher Voice Service",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun buildNotification(text: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("ShweVoice Launcher")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer?.destroy()
        tts?.shutdown()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
