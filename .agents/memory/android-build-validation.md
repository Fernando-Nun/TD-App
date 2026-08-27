---
name: Android build validation
description: Environment-specific constraint for validating the native Android project inside Replit.
---

Replit's available Java and Gradle tools do not include a configured Android SDK, and the managed package index may not expose the Android SDK package.

**Why:** Gradle can load the project but Android tasks stop with “SDK location not found” until an SDK is available.

**How to apply:** For Replit-only validation, use an uncommitted temporary official Android command-line SDK and keep `local.properties` ignored. Android Studio users should rely on the SDK configured by Android Studio.