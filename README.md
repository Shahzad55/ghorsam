# 💊 قرصام | Ghorsam

### Smart Medication Reminder & Personal Health Assistant for Android

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.x-blue.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-green.svg)](https://developer.android.com/jetpack/compose)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](CONTRIBUTING.md)

**قرصام (Ghorsam)** یک اپلیکیشن اندرویدی برای **یادآوری مصرف دارو، ثبت سوابق مصرف، پایش اطلاعات سلامت و کمک به مراقبین خانواده** است که با **Kotlin و Jetpack Compose** توسعه داده شده است.

این پروژه با تمرکز بر تجربه‌ی ساده و قابل‌دسترس برای کاربران، به‌خصوص سالمندان، طراحی شده و قابلیت بررسی محلی اطلاعات ثبت‌شده‌ی داروها را نیز در اختیار دارد.

> ⚠️ این برنامه یک ابزار کمکی برای مدیریت و یادآوری دارو است و جایگزین پزشک، داروساز یا تشخیص پزشکی حرفه‌ای نیست.

---

## 🤝 کمک می‌خواهیم! | We need contributors

پروژه متن‌باز است و از مشارکت شما استقبال می‌کند.

- بخوانید: [CONTRIBUTING.md](CONTRIBUTING.md)
- Issueهای مناسب شروع: برچسب **`good first issue`**
- منشور رفتاری: [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md)

This is an open-source project. PRs and issues are welcome!

---

## ⚠️ هشدار مهم | Important Safety Warning

### 🇮🇷 فارسی

**قرصام یک ابزار یادآوری و ثبت مصرف دارو است و جایگزین پزشک، داروساز یا مراقبت انسانی نیست.**

برخی سالمندان یا افراد دارای حواس‌پرتی، فراموشی یا مشکلات خفیف حافظه و شناختی ممکن است زمان مصرف دارو را فراموش کنند، دوز را اشتباه کنند یا یک دارو را بیش از مقدار تجویز‌شده مصرف کنند.

بنابراین، **برای سالمندان، افراد دارای مشکلات حافظه یا افرادی که چندین دارو را در طول روز مصرف می‌کنند، توصیه می‌شود یک فرد قابل اعتماد مانند عضو خانواده، مراقب یا پرستار بر روند مصرف دارو نظارت داشته باشد.**

این موضوع فقط مختص سالمندان نیست؛ **افراد جوان نیز ممکن است به دلیل مشغله، خستگی، حواس‌پرتی یا مصرف چند دارو در طول روز، دچار خطا در مصرف شوند.**

قرصام نباید به‌تنهایی برای اطمینان از مصرف صحیح دارو یا جلوگیری از مصرف بیش از حد استفاده شود.

**همیشه دستور پزشک یا داروساز را در اولویت قرار دهید. در صورت مصرف اشتباه یا بیش از حد دارو، با پزشک، داروساز، مرکز مسمومیت یا خدمات اورژانسی محل خود تماس بگیرید.**

### 🇬🇧 English

**Ghorsam is a medication reminder and tracking tool. It is not a substitute for a doctor, pharmacist, or human supervision.**

Some older adults and people experiencing distraction, forgetfulness, memory problems, or mild cognitive difficulties may forget to take their medication, take it at the wrong time, or accidentally take more than prescribed.

Therefore, **older adults, people with memory difficulties, and anyone taking multiple medications throughout the day should preferably have a trusted person—such as a family member, caregiver, or nurse—help monitor their medication routine.**

This is not limited to older adults. **Younger people can also make medication errors due to busy schedules, fatigue, distraction, or taking several medications during the day.**

Ghorsam should **not be used as the sole safeguard** to ensure correct medication use or to prevent accidental overdose.

**Always follow the instructions provided by your doctor or pharmacist. If medication is taken incorrectly or in excess, contact a doctor, pharmacist, poison control service, or local emergency service as appropriate.**

---

## ✨ Features

### 💊 Medication Management

- افزودن دارو
- تعیین مقدار مصرف
- تعیین زمان مصرف
- تعیین تعداد دفعات / فاصله مصرف
- ثبت توضیحات و یادداشت دارو
- فعال یا غیرفعال کردن دارو
- حذف دارو
- دسته‌بندی بصری داروها

اطلاعات داروها به صورت محلی در دیتابیس Room ذخیره می‌شوند.

### 🔔 Smart Medication Reminder

سیستم یادآوری دارو شامل:

- هشدار صوتی و تصویری
- نمایش داروی موردنظر
- نمایش مقدار مصرف
- تأیید مصرف دارو
- ثبت داروی مصرف‌شده
- امکان **Snooze**
- ثبت داروی فراموش‌شده
- ثبت زمان واقعی مصرف

برای تست تعاملی، مکانیزم Snooze در نسخه فعلی با تأخیر کوتاه شبیه‌سازی شده است.

### 🤖 Local Medication Verification

بررسی ایمنی دارو به‌صورت محلی روی دستگاه انجام می‌شود و برای این قابلیت به سرویس خارجی یا کلید API نیاز نیست.

### 🔊 Persian Text-to-Speech

برنامه دارای دستیار صوتی مبتنی بر Android Text-to-Speech است (پشتیبانی از زبان فارسی در صورت وجود موتور TTS مناسب).

### 👵 Elderly Mode

حالت دسترسی ویژه برای سالمندان: فونت بزرگ‌تر، رابط خواناتر، پیام‌های صوتی فعال‌تر.

### ❤️ Health Tracking

ثبت فشار خون، ضربان قلب، وضعیت عمومی و یادداشت روزانه سلامت.

### 📋 Medication Intake History

ثبت وضعیت‌های `TAKEN` / `MISSED` / `SNOOZED` و عوارض جانبی.

### 👨‍👩‍👧 Caregiver Support

تعریف مخاطبین مراقب و نمایش هشدار در صورت عدم پاسخ به یادآوری دارو.

---

## 🏗️ Architecture

```
┌─────────────────────────────┐
│        Jetpack Compose      │
│          UI Layer           │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│       ViewModel Layer       │
│   MedicationViewModel       │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│       Repository Layer      │
│    MedicationRepository     │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│         Room Database       │
└─────────────────────────────┘
```

---

## 🧱 Project Structure

```
ghorsam/
├── app/
│   └── src/main/java/com/shahzad55/ghorsam/
│       ├── MainActivity.kt
│       ├── data/
│       │   ├── AppDatabase.kt
│       │   ├── Entities.kt
│       │   ├── MedicationDao.kt
│       │   └── MedicationRepository.kt
│       └── ui/
│           ├── MainScreen.kt
│           ├── MedicationViewModel.kt
│           └── theme/
├── gradle/
├── LICENSE
├── CONTRIBUTING.md
├── CODE_OF_CONDUCT.md
└── README.md
```

---

## 🛠️ Technology Stack

| Technology | Usage |
|---|---|
| **Kotlin** | Main language |
| **Jetpack Compose** | UI |
| **Material 3** | Design system |
| **Room** | Local database |
| **ViewModel + Coroutines** | State & async |
| **KSP** | Code generation |
| **Android TTS** | Persian voice |
| **Robolectric / Roborazzi** | Testing |

---

## 📱 Android Configuration

```
Language: Kotlin
minSdk: 24
targetSdk: 36
compileSdk: 36
JDK: 17+ (required for Gradle)
UI: Jetpack Compose
Package: com.shahzad55.ghorsam
```

---

## 🚀 Build & Run

**Requirement: JDK 17 or later**

```bash
git clone https://github.com/Shahzad55/ghorsam.git
cd ghorsam
./gradlew clean assembleDebug
```

Or open the project in **Android Studio**, sync Gradle, and run on a device/emulator.

---

## 🧪 Testing

```
app/src/test/
app/src/androidTest/
```

JUnit, AndroidX Test, Espresso, Robolectric, Roborazzi, Compose UI Testing.

---

## 🚧 Project Status

**Current version:** under active development

Possible future improvements:

- Real scheduled notifications (AlarmManager / WorkManager)
- Wear OS integration
- Real caregiver SMS / messaging
- Health history charts
- Better Persian calendar support
- Multi-language support
- More tests and documentation

---

## ⚠️ Medical Disclaimer

**Ghorsam is a medication-management and reminder application, not a medical diagnostic system.**

Always consult a qualified physician or pharmacist before changing medication, dosage, or treatment plan.

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Author

**Shahzad55** — [GitHub](https://github.com/Shahzad55)  
Project: [https://github.com/Shahzad55/ghorsam](https://github.com/Shahzad55/ghorsam)

---

### 💊 Ghorsam

**Remember your medication. Track your health. Stay connected.**

> Built with Kotlin • Jetpack Compose • Room  
> Contributions welcome — see [CONTRIBUTING.md](CONTRIBUTING.md)
