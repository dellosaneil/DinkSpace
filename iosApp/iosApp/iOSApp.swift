import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        // TODO: Add the Firebase iOS SDK (Swift Package Manager), GoogleService-Info.plist,
        // then `import FirebaseCore` and call `FirebaseApp.configure()` here.
        KoinIosKt.startKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}