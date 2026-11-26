# ShweVoice Launcher (ရွှေအသံ App ထိန်းချုပ်စနစ်)

မြန်မာဘာသာစကားဖြင့် အသံသုံး၍ Android App များကို ဖွင့်ခြင်း၊ ပိတ်ခြင်း ပြုလုပ်နိုင်သော Android Studio Kotlin Project ဖြစ်ပါသည်။

## APK ထုတ်ယူနည်း ၂ နည်း

### နည်းလမ်း ၁ — Android Studio ဖြင့် Build လုပ်ခြင်း
1. ဤ Folder ကို **Android Studio** ဖြင့် Open လုပ်ပါ။
2. Gradle Sync ပြီးဆုံးသည်အထိ စောင့်ပါ။
3. Menu bar မှ **Build > Build Bundle(s) / APK(s) > Build APK(s)** ကို နှိပ်ပါ။
4. ရရှိလာသော `app-debug.apk` ကို Android ဖုန်းထဲသို့ ထည့်သွင်းပါ။

### နည်းလမ်း ၂ — GitHub Actions ဖြင့် Cloud ပေါ်တွင် APK ထုတ်ယူခြင်း (PC မလိုပါ)
1. ဤ Project ဖိုင်များကို GitHub Repository အသစ်တစ်ခုထဲသို့ Upload တင်ပါ။
2. **Actions** Tab သို့သွား၍ **Build Android APK > Run workflow** ကို နှိပ်ပါ။
3. စက္ကန့်အနည်းငယ်အတွင်း `app-debug.apk` ကို Download ဆွဲယူနိုင်ပါမည်။

## ဖုန်းတွင် အသုံးပြုပုံ
1. App ကို ဖွင့်ပြီး **Microphone (မိုက်ခရိုဖုန်း)** ခွင့်ပြုချက်ပေးပါ။
2. **Accessibility ဆက်တင်ဖွင့်ရန်** ခလုတ်ကို နှိပ်၍ `ShweVoice Launcher App Controller` ကို On ပေးပါ။
3. ထို့နောက် **"YouTube ဖွင့်ပေးပါ"**, **"ဖေ့စ်ဘွတ်ခ် ပိတ်လိုက်"**, **"အားလုံးပိတ်ပေးပါ"** စသည်ဖြင့် မြန်မာလို အမိန့်ပေးနိုင်ပါပြီ။
