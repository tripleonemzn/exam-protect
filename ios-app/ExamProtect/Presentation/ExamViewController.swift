import UIKit
import WebKit

final class ExamViewController: UIViewController, WKNavigationDelegate, WKUIDelegate {
    private var webView: WKWebView!
    private var policy: ExamPolicy = .sample()
    private var violationCount = 0
    private var heartbeatTimer: Timer?

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .black

        let contentController = WKUserContentController()
        if !policy.enableCopyPaste {
            let css = "html, body, * { -webkit-user-select: none !important; user-select: none !important; -webkit-touch-callout: none !important; }"
            let js = "var style = document.createElement('style'); style.innerHTML = `\(css)`; document.head.appendChild(style);"
            let script = WKUserScript(source: js, injectionTime: .atDocumentStart, forMainFrameOnly: true)
            contentController.addUserScript(script)
        }
        let config = WKWebViewConfiguration()
        config.defaultWebpagePreferences.allowsContentJavaScript = true
        config.websiteDataStore = .default()
        config.userContentController = contentController
        webView = WKWebView(frame: .zero, configuration: config)
        webView.translatesAutoresizingMaskIntoConstraints = false
        webView.navigationDelegate = self
        webView.uiDelegate = self
        webView.allowsLinkPreview = false
        view.addSubview(webView)
        NSLayoutConstraint.activate([
            webView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            webView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            webView.topAnchor.constraint(equalTo: view.topAnchor),
            webView.bottomAnchor.constraint(equalTo: view.bottomAnchor)
        ])

        // Best-effort: nonaktifkan selection/copy via CSS injection
        if !policy.enableCopyPaste {
            let css = "html, body, * { -webkit-user-select: none !important; user-select: none !important; -webkit-touch-callout: none !important; }"
            let js = "var style = document.createElement('style'); style.innerHTML = `\(css)`; document.head.appendChild(style);"
            webView.evaluateJavaScript(js, completionHandler: nil)
        }

        loadExam()
        startMonitoring()
        startHeartbeat()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        navigationController?.setNavigationBarHidden(true, animated: animated)
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        navigationController?.setNavigationBarHidden(false, animated: animated)
        heartbeatTimer?.invalidate()
    }

    private func loadExam() {
        if let url = URL(string: policy.launchUrl) {
            webView.load(URLRequest(url: url))
        }
    }

    private func isAllowed(_ url: URL) -> Bool {
        guard let host = url.host else { return false }
        return policy.allowedDomains.contains(where: { host == $0 || host.hasSuffix(".\($0)") })
    }

    private func incrViolation(_ type: ViolationType, detail: String? = nil) {
        violationCount += 1
        if violationCount >= policy.violationThreshold {
            presentSummary()
        }
    }

    private func presentSummary() {
        let vc = SummaryViewController()
        navigationController?.setViewControllers([vc], animated: true)
    }

    // MARK: - Monitoring
    private func startMonitoring() {
        NotificationCenter.default.addObserver(self, selector: #selector(appWillResignActive), name: UIApplication.willResignActiveNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(appDidEnterBackground), name: UIApplication.didEnterBackgroundNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(screenshotTaken), name: UIApplication.userDidTakeScreenshotNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(screenCaptureChanged), name: UIScreen.capturedDidChangeNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(screenChanged), name: UIScreen.didConnectNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(screenChanged), name: UIScreen.didDisconnectNotification, object: nil)
    }

    @objc private func appWillResignActive() {
        incrViolation(.appInactive)
    }
    @objc private func appDidEnterBackground() {
        incrViolation(.appBackground)
    }
    @objc private func screenshotTaken() {
        incrViolation(.screenshot)
    }
    @objc private func screenCaptureChanged() {
        if UIScreen.main.isCaptured {
            incrViolation(.screenCaptured)
        }
    }
    @objc private func screenChanged() {
        if UIScreen.screens.count > 1 {
            incrViolation(.externalDisplay)
        }
    }

    private func startHeartbeat() {
        heartbeatTimer?.invalidate()
        heartbeatTimer = Timer.scheduledTimer(withTimeInterval: TimeInterval(policy.heartbeatSeconds), repeats: true) { [weak self] _ in
            guard let self else { return }
            // TODO: kirim heartbeat ke backend (public API). Di MVP, skip network call.
            _ = self.violationCount
        }
    }

    // MARK: - WKNavigationDelegate
    func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
        guard let url = navigationAction.request.url else {
            decisionHandler(.cancel); return
        }
        let scheme = url.scheme?.lowercased() ?? ""
        let blockedSchemes = ["tel", "sms", "mailto", "itms-apps", "itms", "maps", "file", "data", "blob", "content", "about"]
        if blockedSchemes.contains(scheme) {
            incrViolation(.externalNavAttempt, detail: scheme)
            decisionHandler(.cancel); return
        }
        if (scheme == "http" || scheme == "https") && isAllowed(url) {
            decisionHandler(.allow)
        } else {
            incrViolation(.externalNavAttempt, detail: url.absoluteString)
            decisionHandler(.cancel)
        }
    }

    // MARK: - WKUIDelegate
    func webView(_ webView: WKWebView, createWebViewWith configuration: WKWebViewConfiguration, for navigationAction: WKNavigationAction, windowFeatures: WKWindowFeatures) -> WKWebView? {
        // Blokir pop-up/window.open kecuali diizinkan oleh kebijakan
        return nil
    }
}
