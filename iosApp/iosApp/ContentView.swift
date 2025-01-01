import UIKit
import SwiftUI
import shared

struct ComposeView: UIViewControllerRepresentable {
    
    func makeUIViewController(context: Context) -> UIViewController {
        //         Main_iosKt.debugBuild()
        let vc:UIViewController = Main_iosKt.MainViewController()
        return vc
        
    }
    
 
    
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView().ignoresSafeArea() // Compose has own keyboard handler
    }
}



