import SwiftUI
import GoogleMaps

@main
struct iOSApp: App {
    init() {
        GMSServices.provideAPIKey("AIzaSyA-1BxSg2g9e82HnWkldWefXpVc1KXWzcw")
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}