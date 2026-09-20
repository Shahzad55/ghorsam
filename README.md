# 💊 قرصام | Ghorsam

### Smart Medication Reminder & Personal Health Assistant for Android

**قرصام (Ghorsam)** یک اپلیکیشن اندرویدی برای **یادآوری مصرف دارو، ثبت سوابق مصرف، پایش اطلاعات سلامت و کمک به مراقبین خانواده** است که با **Kotlin و Jetpack Compose** توسعه داده شده است.

این پروژه با تمرکز بر تجربه‌ی ساده و قابل‌دسترس برای کاربران، به‌خصوص سالمندان، طراحی شده و قابلیت بررسی محلی اطلاعات ثبت‌شده‌ی داروها را نیز در اختیار دارد.

> ⚠️ این برنامه یک ابزار کمکی برای مدیریت و یادآوری دارو است و جایگزین پزشک، داروساز یا تشخیص پزشکی حرفه‌ای نیست.

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

بررسی شامل مواردی مانند:

- نام دارو
- مقدار مصرف
- زمان مصرف
- تعداد دفعات مصرف
- توضیحات دارو
- شناسایی برخی موارد مشکوک یا نیازمند بررسی بیشتر

نتیجه به زبان فارسی و با پیام ساده برای کاربر نمایش داده می‌شود.

> بررسی محلی صرفاً یک قابلیت کمکی است و نباید به‌عنوان تأیید پزشکی یا تجویز دارو در نظر گرفته شود.

### 🔊 Persian Text-to-Speech

برنامه دارای دستیار صوتی مبتنی بر Android Text-to-Speech است.

قابلیت‌ها:

- اعلام زمان مصرف دارو
- اعلام وضعیت عملیات
- اعلام هشدار
- اعلام تأیید مصرف
- پشتیبانی از زبان فارسی در صورت وجود موتور TTS مناسب

### 👵 Elderly Mode

برای کاربران سالمند یک حالت دسترسی ویژه در نظر گرفته شده است.

در این حالت:

- اندازه فونت افزایش پیدا می‌کند
- رابط کاربری خواناتر می‌شود
- پیام‌های صوتی فعال‌تر می‌شوند
- عناصر اصلی برای استفاده ساده‌تر طراحی شده‌اند

همچنین امکان تغییر بین حالت کاربری عادی و سالمندان وجود دارد.

### ❤️ Health Tracking

کاربر می‌تواند اطلاعات روزانه سلامت خود را ثبت کند، از جمله:

- فشار خون سیستولیک
- فشار خون دیاستولیک
- ضربان قلب
- وضعیت عمومی
- یادداشت روزانه سلامت

سوابق در دیتابیس محلی برنامه ذخیره می‌شوند.

### 📋 Medication Intake History

هر بار مصرف دارو می‌تواند در قالب یک `IntakeLog` ثبت شود.

وضعیت‌های موجود:

- `TAKEN`
- `MISSED`
- `SNOOZED`

همچنین امکان ثبت عوارض جانبی مرتبط با مصرف دارو وجود دارد.

### 👨‍👩‍👧 Caregiver Support

برنامه امکان تعریف مخاطبین مراقب را فراهم می‌کند.

برای هر مراقب می‌توان موارد زیر را ذخیره کرد:

- نام
- شماره تلفن
- فعال یا غیرفعال بودن هشدار اضطراری

در صورت عدم پاسخ کاربر به هشدار دارو، برنامه وضعیت هشدار مراقب را ثبت و نمایش می‌دهد.

> در نسخه فعلی، ارسال واقعی SMS یا پیام اضطراری به عنوان یک سرویس مخابراتی مستقل پیاده‌سازی نشده و مکانیزم هشدار در سطح برنامه مدیریت می‌شود.

---

## 🏗️ Architecture

ساختار اصلی برنامه بر اساس معماری تفکیک‌شده‌ی UI، ViewModel و Data Layer طراحی شده است.

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
│                             │
│  Medications                │
│  Intake Logs                │
│  Health Notes               │
│  Caregiver Contacts         │
└─────────────────────────────┘
```

---

## 🧱 Project Structure

```
ghorsam/
│
├── app/
│   ├── build.gradle.kts
│   │
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   │
│       │   ├── java/com/example/
│       │   │   ├── MainActivity.kt
│       │   │   │
│       │   │   ├── data/
│       │   │   │   ├── AppDatabase.kt
│       │   │   │   ├── Entities.kt
│       │   │   │   ├── MedicationDao.kt
│       │   │   │   └── MedicationRepository.kt
│       │   │   │
│       │   │   └── ui/
│       │   │       ├── MainScreen.kt
│       │   │       ├── MedicationViewModel.kt
│       │   │       └── theme/
│       │   │           ├── Color.kt
│       │   │           ├── Theme.kt
│       │   │           └── Type.kt
│       │   │
│       │   └── res/
│       │
│       ├── test/
│       └── androidTest/
│
├── gradle/
│   └── libs.versions.toml
│
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── .env.example
└── README.md
```

---

## 🛠️ Technology Stack

| Technology | Usage |
|---|---|
| **Kotlin** | Main programming language |
| **Jetpack Compose** | Modern Android UI |
| **Material 3** | UI components and design system |
| **Room** | Local database |
| **ViewModel** | UI state and business logic |
| **Kotlin Coroutines** | Asynchronous operations |
| **KSP** | Code generation |
| **Retrofit** | HTTP/API communication |
| **OkHttp** | Network layer |
| **Moshi** | JSON serialization |
| **Local Verification** | Local medication safety checks |
| **Android TTS** | Voice assistance |
| **Robolectric** | Unit testing |
| **Roborazzi** | Screenshot/UI testing |
| **Gradle Version Catalog** | Dependency management |

---

## 📱 Android Configuration

```
Language: Kotlin
minSdk: 24
targetSdk: 36
compileSdk: 36
Java Compatibility: Java 11
UI: Jetpack Compose
```

---

## 🚀 Build & Run

Clone the repository:

```bash
git clone https://github.com/Shahzad55/ghorsam.git
cd ghorsam
```

Open the project in **Android Studio**.

Then:

1. Configure the required Android SDK.
3. Sync Gradle.
4. Build the project.
5. Run it on an Android device or emulator.

---

## 🧪 Testing

The project contains both local and Android instrumentation tests.

```
app/src/test/
app/src/androidTest/
```

Testing technologies currently included:

- JUnit
- AndroidX Test
- Espresso
- Robolectric
- Roborazzi
- Compose UI Testing

Screenshot-based UI testing is also included.

---

## 🔄 Data Model

The application currently defines the following main entities:

### Medication

```
id
name
dose
time
frequency
notes
colorIndex
isActive
isVerified
verificationMsg
```

### IntakeLog

```
id
medId
medName
scheduledTime
actualTime
status
sideEffects
```

### DailyHealthNote

```
id
date
note
systolicBP
diastolicBP
heartRate
generalStatus
```

### CaregiverContact

```
id
name
phone
isEmergencyAlertEnabled
```

---

## 🧠 Medication Verification Architecture

The medication verification flow is fully local:

```
              Add Medication
                    │
                    ▼
          Medication Saved Locally
                    │
                    ▼
          Local Safety Verification
                    │
                    ▼
             Verification Result
                    │
                    ▼
              Stored in Room DB
```

This keeps the verification feature available without requiring an external service or API credentials.

---

## 🎨 User Experience

The UI is designed around a Persian-language user experience with emphasis on:

- Readability
- Large interactive elements
- Simple medication workflows
- Accessibility
- Voice feedback
- Elderly-friendly interaction
- Clear medication status
- Health information at a glance

---

## 🔒 Privacy & Security

The application is primarily designed around local storage using Room.

The medication verification feature runs locally and does not require sending medication information to an external service.

### Security recommendations

- Do not commit API keys.
- Use environment/secret management for production.
- Avoid storing unnecessary personal information.
- Protect caregiver contact information.
- Review network traffic before production deployment.
- Use a secure backend/proxy if exposing an AI API key directly from a distributed Android application is not acceptable.

---

## 🚧 Project Status

**Current version:** `1.3`  
**Version code:** `4`  
**Build:** Debug APK

The project is currently under active development.

Possible future improvements include:

- 🔔 Background/real scheduled notifications
- 📲 Android notification channels
- ⌚ Wear OS integration
- 📡 Real caregiver notifications
- 📱 SMS / messaging integration
- 📊 Health history charts
- 🗓️ Improved Persian calendar support
- 🔐 Stronger local data protection
- ☁️ Optional encrypted cloud synchronization
- 🧪 More advanced local medication-safety checks
- 🌍 Multi-language support

---

## ⚠️ Medical Disclaimer

**Ghorsam is a medication-management and reminder application, not a medical diagnostic system.**

Medication verification results must not be treated as medical advice.

Always consult a qualified physician or pharmacist before changing:

- Medication
- Dosage
- Frequency
- Treatment plan

Never stop or modify prescribed medication solely based on an application or AI-generated recommendation.

---

## 📄 License

No open-source license has currently been specified for this repository.

Unless a license is added, the source code should be treated as **all rights reserved**.

---

## 👨‍💻 Author

**Shahzad55**

GitHub:

https://github.com/Shahzad55

Project:

https://github.com/Shahzad55/ghorsam

---

### 💊 Ghorsam

**Remember your medication. Track your health. Stay connected.**

> Built with Kotlin • Jetpack Compose • Room
