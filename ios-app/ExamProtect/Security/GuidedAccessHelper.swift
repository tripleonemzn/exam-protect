import UIKit

enum GuidedAccessHelper {
    static func requestEnable(completion: ((Bool) -> Void)? = nil) {
        // Best effort: BYOD memerlukan persetujuan pengguna untuk Guided Access
        UIAccessibility.requestGuidedAccessSession(enabled: true) { success in
            completion?(success)
        }
    }
    static func requestDisable(completion: ((Bool) -> Void)? = nil) {
        UIAccessibility.requestGuidedAccessSession(enabled: false) { success in
            completion?(success)
        }
    }
}
