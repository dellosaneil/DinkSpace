import SwiftUI
import Shared
import FirebaseCore

@main
struct iOSApp: App {
    init() {
        FirebaseApp.configure()
        KoinIosKt.startKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
