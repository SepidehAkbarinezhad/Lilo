import SwiftUI
import FirebaseCore
import FirebaseCrashlytics
import FirebaseCore
import FirebaseAnalytics



@main
struct iOSApp: App {
    
    init() {
            FirebaseApp.configure()
        // Force immediate Analytics verification
        Analytics.logEvent("debug_connection_test", parameters: [
            "test_id": "ios_\(UUID().uuidString)",
            "timestamp": Date().timeIntervalSince1970
        ])

        print("🔵 DEBUG: Analytics event logged at \(Date())")

    

        // Check if FirebaseApp has Analytics
        if let app = FirebaseApp.app(), app.isDataCollectionDefaultEnabled {
            print("✅ Firebase data collection ENABLED")
        } else {
            print("❌ Firebase data collection DISABLED")
        }
        }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
