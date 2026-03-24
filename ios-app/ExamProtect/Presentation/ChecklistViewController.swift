import UIKit

final class ChecklistViewController: UIViewController {
    private let label = UILabel()
    private let button = UIButton(type: .system)

    override func viewDidLoad() {
        super.viewDidLoad()
        title = "Checklist"
        view.backgroundColor = .systemBackground
        label.numberOfLines = 0
        label.text = """
        • Aktifkan Guided Access (best-effort BYOD)
        • Matikan notifikasi (Focus Mode)
        • Pastikan internet stabil
        """
        button.setTitle("Mulai Ujian", for: .normal)
        button.addTarget(self, action: #selector(startExam), for: .touchUpInside)
        let stack = UIStackView(arrangedSubviews: [label, button])
        stack.axis = .vertical
        stack.spacing = 12
        stack.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(stack)
        NSLayoutConstraint.activate([
            stack.leadingAnchor.constraint(equalTo: view.layoutMarginsGuide.leadingAnchor),
            stack.trailingAnchor.constraint(equalTo: view.layoutMarginsGuide.trailingAnchor),
            stack.centerYAnchor.constraint(equalTo: view.centerYAnchor)
        ])
    }

    @objc private func startExam() {
        GuidedAccessHelper.requestEnable { _ in
            DispatchQueue.main.async {
                self.navigationController?.pushViewController(ExamViewController(), animated: true)
            }
        }
    }
}
