import Foundation

struct Branding: Codable {
    let appName: String?
    let primaryColor: String?
    let logoUrl: String?
}

struct ExamPolicy: Codable {
    let examId: String
    let launchUrl: String
    let allowedDomains: [String]
    let blockExternalNavigation: Bool
    let enableCopyPaste: Bool
    let enableDownloads: Bool
    let enableUploads: Bool
    let heartbeatSeconds: Int
    let teacherPinRequired: Bool
    let maxBackgroundSeconds: Int
    let violationThreshold: Int
    let managedMode: Bool
    let branding: Branding?
}

enum DeviceMode {
    case byod
    case supervised
}

enum ViolationType: String {
    case appBackground = "APP_BACKGROUND"
    case appInactive = "APP_INACTIVE"
    case screenshot = "SCREENSHOT_TAKEN"
    case screenCaptured = "SCREEN_CAPTURED"
    case externalDisplay = "EXTERNAL_DISPLAY"
    case externalNavAttempt = "EXTERNAL_NAV_ATTEMPT"
}

extension ExamPolicy {
    static func sample() -> ExamPolicy {
        return ExamPolicy(
            examId: "sample-exam-001",
            launchUrl: "https://example.com/exam",
            allowedDomains: ["example.com", "docs.google.com", "forms.gle", "accounts.google.com"],
            blockExternalNavigation: true,
            enableCopyPaste: false,
            enableDownloads: false,
            enableUploads: false,
            heartbeatSeconds: 15,
            teacherPinRequired: true,
            maxBackgroundSeconds: 3,
            violationThreshold: 3,
            managedMode: false,
            branding: Branding(appName: "EXAM-PROTECT", primaryColor: nil, logoUrl: nil)
        )
    }
}
