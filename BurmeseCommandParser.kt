package com.shwevoice.launcher.nlp

import com.shwevoice.launcher.model.AppItem
import com.shwevoice.launcher.model.CommandAction
import com.shwevoice.launcher.model.ParsedBurmeseCommand

object BurmeseCommandParser {

    val defaultCatalog: List<AppItem> = listOf(
        AppItem(
            id = "facebook",
            name = "Facebook",
            burmeseName = "ဖေ့စ်ဘွတ်ခ်",
            packageName = "com.facebook.katana",
            aliases = listOf("ဖေ့စ်ဘွတ်ခ်", "ဖေ့ဘွတ်", "အက်ဖ်ဘီ", "fb", "facebook")
        ),
        AppItem(
            id = "youtube",
            name = "YouTube",
            burmeseName = "ယူကျု့",
            packageName = "com.google.android.youtube",
            aliases = listOf("ယူကျု့", "ယူကျု", "ဗီဒီယို", "youtube", "yt")
        ),
        AppItem(
            id = "tiktok",
            name = "TikTok",
            burmeseName = "တစ်တော့",
            packageName = "com.zhiliaoapp.musically",
            aliases = listOf("တစ်တော့", "တစ်တော့ခ်", "tiktok")
        ),
        AppItem(
            id = "messenger",
            name = "Messenger",
            burmeseName = "မက်ဆင်ဂျာ",
            packageName = "com.facebook.orca",
            aliases = listOf("မက်ဆင်ဂျာ", "စာပို့", "messenger")
        ),
        AppItem(
            id = "telegram",
            name = "Telegram",
            burmeseName = "တယ်လီဂရမ်",
            packageName = "org.telegram.messenger",
            aliases = listOf("တယ်လီဂရမ်", "တီဂျီ", "telegram", "tg")
        ),
        AppItem(
            id = "kbzpay",
            name = "KBZPay",
            burmeseName = "ကေပေး",
            packageName = "com.kbzbank.kbzpay",
            aliases = listOf("ကေပေး", "ကေဘီဇက်ပေး", "kbzpay", "kpay")
        ),
        AppItem(
            id = "wavepay",
            name = "WavePay",
            burmeseName = "ဝေ့ဖ်ပေး",
            packageName = "mm.com.wavemoney.wavepay",
            aliases = listOf("ဝေ့ဖ်ပေး", "ဝေ့ပေး", "wavepay", "wave")
        ),
        AppItem(
            id = "chrome",
            name = "Chrome",
            burmeseName = "ခရုမ်း",
            packageName = "com.android.chrome",
            aliases = listOf("ခရုမ်း", "ဘရောက်ဇာ", "အင်တာနက်", "chrome", "browser")
        ),
        AppItem(
            id = "maps",
            name = "Google Maps",
            burmeseName = "မြေပုံ",
            packageName = "com.google.android.apps.maps",
            aliases = listOf("မြေပုံ", "လမ်းညွှန်", "maps", "google maps")
        ),
        AppItem(
            id = "camera",
            name = "Camera",
            burmeseName = "ကင်မရာ",
            packageName = "com.android.camera2",
            aliases = listOf("ကင်မရာ", "ဓာတ်ပုံ", "camera")
        ),
        AppItem(
            id = "phone",
            name = "Phone",
            burmeseName = "ဖုန်း",
            packageName = "com.google.android.dialer",
            aliases = listOf("ဖုန်း", "ဖုန်းခေါ်", "phone", "dialer")
        ),
        AppItem(
            id = "settings",
            name = "Settings",
            burmeseName = "ဆက်တင်",
            packageName = "com.android.settings",
            aliases = listOf("ဆက်တင်", "အပြင်အဆင်", "settings")
        ),
        AppItem(
            id = "calculator",
            name = "Calculator",
            burmeseName = "ဂဏန်းပေါင်းစက်",
            packageName = "com.google.android.calculator",
            aliases = listOf("ဂဏန်းပေါင်းစက်", "တွက်စက်", "calculator")
        ),
        AppItem(
            id = "clock",
            name = "Clock",
            burmeseName = "နာရီ",
            packageName = "com.google.android.deskclock",
            aliases = listOf("နာရီ", "နှိုးစက်", "clock", "alarm")
        )
    )

    private val openKeywords = listOf(
        "ဖွင့်", "ဖွင့်", "ဖွင့်ပေး", "ဖွင့်ပေး", "တင်", "ဝင်", "open", "launch", "start"
    )

    private val closeKeywords = listOf(
        "ပိတ်", "ပိတ်ပေး", "ပိတ်လိုက်", "ထွက်", "ရပ်", "close", "stop", "exit", "kill"
    )

    private val closeAllKeywords = listOf(
        "အားလုံးပိတ်", "အားလုံး ပိတ်", "အကုန်ပိတ်", "အကုန် ပိတ်", "close all", "clear all"
    )

    private val homeKeywords = listOf(
        "ပင်မစာမျက်နှာ", "အိမ်ပြန်", "home", "launcher"
    )

    fun parse(transcript: String, catalog: List<AppItem> = defaultCatalog): ParsedBurmeseCommand {
        val normalized = transcript.trim().lowercase()
        if (normalized.isEmpty()) {
            return ParsedBurmeseCommand(
                action = CommandAction.UNKNOWN,
                targetApp = null,
                spokenFeedback = "အသံမကြားရပါ။ ထပ်မံပြောကြားပေးပါ။",
                rawTranscript = transcript
            )
        }

        if (closeAllKeywords.any { normalized.contains(it) }) {
            return ParsedBurmeseCommand(
                action = CommandAction.CLOSE_ALL_APPS,
                targetApp = null,
                spokenFeedback = "ဖွင့်ထားသော App များအားလုံးကို ပိတ်ပေးနေပါပြီ။",
                rawTranscript = transcript
            )
        }

        if (homeKeywords.any { normalized.contains(it) }) {
            return ParsedBurmeseCommand(
                action = CommandAction.GO_HOME,
                targetApp = null,
                spokenFeedback = "ပင်မစာမျက်နှာသို့ ပြန်သွားနေပါပြီ။",
                rawTranscript = transcript
            )
        }

        val matchedApp = findBestAppMatch(normalized, catalog)
        val isClose = closeKeywords.any { normalized.contains(it) }
        val isOpen = openKeywords.any { normalized.contains(it) }

        if (matchedApp != null && isClose) {
            return ParsedBurmeseCommand(
                action = CommandAction.CLOSE_APP,
                targetApp = matchedApp,
                spokenFeedback = "${matchedApp.burmeseName} ကို ပိတ်လိုက်ပါပြီ။",
                rawTranscript = transcript
            )
        }

        if (matchedApp != null && (isOpen || !isClose)) {
            return ParsedBurmeseCommand(
                action = CommandAction.OPEN_APP,
                targetApp = matchedApp,
                spokenFeedback = "${matchedApp.burmeseName} ကို ဖွင့်ပေးနေပါပြီ။",
                rawTranscript = transcript
            )
        }

        if (isClose) {
            return ParsedBurmeseCommand(
                action = CommandAction.CLOSE_APP,
                targetApp = null,
                spokenFeedback = "လက်ရှိ App ကို ပိတ်ပေးနေပါပြီ။",
                rawTranscript = transcript
            )
        }

        return ParsedBurmeseCommand(
            action = CommandAction.UNKNOWN,
            targetApp = null,
            spokenFeedback = "အမိန့်ကို နားမလည်လိုက်ပါဘူး။ ဥပမာ - YouTube ဖွင့်ပေးပါ ဟု ပြောကြည့်ပါ။",
            rawTranscript = transcript
        )
    }

    private fun findBestAppMatch(normalizedInput: String, catalog: List<AppItem>): AppItem? {
        for (app in catalog) {
            if (normalizedInput.contains(app.name.lowercase()) ||
                normalizedInput.contains(app.burmeseName.lowercase())
            ) {
                return app
            }
            if (app.aliases.any { alias -> normalizedInput.contains(alias.lowercase()) }) {
                return app
            }
        }
        return null
    }
}
