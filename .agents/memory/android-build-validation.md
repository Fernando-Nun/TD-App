---
name: Android build validation
description: Environment-specific constraint for validating the native Android project inside Replit.
---

Replit's available Java and Gradle tools do not include a configured Android SDK, and the managed package index may not expose the Android SDK package. The default GraalVM JDK 19 can also fail during Android's `JdkImageTransform`.

**Why:** Gradle can load the project but Android tasks stop with “SDK location not found” until an SDK is available. With the SDK installed, GraalVM's `jlink` can still fail while transforming Android platform modules.

**How to apply:** For Replit-only validation, use an uncommitted temporary official Android command-line SDK plus a temporary JDK 17, and keep `local.properties` ignored. Android Studio supplies both through its normal setup.