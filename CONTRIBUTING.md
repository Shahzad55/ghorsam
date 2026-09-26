# مشارکت در قرصام | Contributing to Ghorsam

از اینکه می‌خواهید به پروژه **قرصام (Ghorsam)** کمک کنید متشکریم! 💊

Thank you for your interest in contributing to **Ghorsam**

---

## 🇮🇷 راهنمای مشارکت (فارسی)

### قبل از شروع

1. یک Issue باز کنید یا روی Issueهای موجود با برچسب `good first issue` یا `help wanted` کار کنید.
2. پروژه را Fork کنید.
3. یک Branch جدید بسازید:
   ```bash
   git checkout -b feature/my-feature
   ```

### پیش‌نیازها

- **JDK 17 یا بالاتر** (اجباری)
- Android Studio (پیشنهادی)
- Android SDK با minSdk 24

```bash
git clone https://github.com/YOUR_USERNAME/ghorsam.git
cd ghorsam
./gradlew clean assembleDebug
```

### قوانین کدنویسی

- از Kotlin و Jetpack Compose استفاده کنید.
- کد را خوانا و با نام‌گذاری واضح بنویسید.
- برای قابلیت‌های جدید، در صورت امکان تست اضافه کنید.
- پیام‌های کامیت را واضح بنویسید (به انگلیسی ترجیحاً).

### ارسال Pull Request

1. تغییرات را Push کنید.
2. از Branch خود به `main` یک Pull Request باز کنید.
3. در توضیحات PR بنویسید چه تغییری داده‌اید و چرا.
4. اگر Issue مرتبطی وجود دارد، آن را ذکر کنید (`Fixes #123`).

---

## 🇬🇧 Contributing Guide (English)

### Getting started

1. Open an issue or pick an existing one labeled `good first issue` or `help wanted`.
2. Fork the repository.
3. Create a feature branch:
   ```bash
   git checkout -b feature/my-feature
   ```

### Requirements

- **JDK 17 or later** (required)
- Android Studio (recommended)
- Android SDK (minSdk 24)

```bash
git clone https://github.com/YOUR_USERNAME/ghorsam.git
cd ghorsam
./gradlew clean assembleDebug
```

### Coding guidelines

- Use Kotlin and Jetpack Compose.
- Keep code readable with clear naming.
- Add tests when reasonable for new features.
- Write clear commit messages (preferably in English).

### Pull Requests

1. Push your changes.
2. Open a Pull Request against `main`.
3. Describe what you changed and why.
4. Reference related issues (`Fixes #123`).

---

## Ideas for contribution

- Improve elderly-mode accessibility
- Real scheduled notifications (AlarmManager / WorkManager)
- Better Persian calendar support
- Unit tests for ViewModel and Repository
- UI polish and screenshots
- Documentation improvements
- Bug fixes

---

## Code of Conduct

Please read and follow our [Code of Conduct](CODE_OF_CONDUCT.md).

سوالی دارید؟ یک Issue باز کنید. خوشحال می‌شویم کمکتان کنیم.
