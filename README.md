This is a Kotlin Multiplatform project targeting Android, iOS.

* [/iosApp](./iosApp/iosApp) contains an iOS application. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* [/shared](./shared/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./shared/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./shared/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./shared/src/jvmMain/kotlin)
    folder is the appropriate location.

### Running the apps

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and options:

- Android app: `./gradlew :androidApp:assembleDebug`
- iOS app: open the [/iosApp](./iosApp) directory in Xcode and run it from there.

### Running tests

Use the run button in your IDE's editor gutter, or run tests using Gradle tasks:

- Android tests: `./gradlew :shared:testAndroidHostTest`
- iOS tests: `./gradlew :shared:iosSimulatorArm64Test`

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…# DinkSpace


## Architecture

Clean Architecture + MVVM, organized by feature inside `shared/src/commonMain`:

```
core/
  domain/         Result, Error, DataError: shared by every feature
  data/           Room database (DinkSpaceDatabase, DatabaseFactory), Firestore error mapping
  presentation/   theme, shared composables, error → string mapping
feature/<name>/
  domain/         models, repository interfaces, use cases (pure Kotlin, no framework imports)
  data/           Room entities + DAOs, Firestore data sources + DTOs, mappers, repository impls
  presentation/   <Screen>State, <Screen>Action, <Screen>ViewModel, <Screen>Root + <Screen>Screen
di/               Koin modules (one per feature) and initKoin()
navigation/       type-safe routes, NavHost and bottom bar
```

- Dependencies point inwards: presentation → domain ← data. The domain layer knows nothing about Room, Firebase or Compose.
- Repositories are offline-first: Room is the single source of truth and Firestore only refreshes it.
- Each screen has a stateful `XRoot` that wires up the ViewModel and navigation, and a stateless `XScreen` that takes `state` and `onAction`, so it can be previewed.

Stack: Firebase (GitLive Kotlin SDK: Firestore, Auth), Room KMP with the bundled SQLite driver, Koin, Navigation Compose, Coil 3 (Ktor network fetcher) for images.

### Firebase setup

- **Android:** put `google-services.json` in `androidApp/`. The Google Services plugin is applied automatically once the file exists.
- **iOS:** add the Firebase iOS SDK with Swift Package Manager (FirebaseAuth and FirebaseFirestore), add `GoogleService-Info.plist`, and call `FirebaseApp.configure()` in `iOSApp.init()`.
