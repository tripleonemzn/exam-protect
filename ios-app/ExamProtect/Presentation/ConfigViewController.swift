import UIKit

final class ConfigViewController: UIViewController {
    private let urlField = UITextField()
    private let examIdField = UITextField()
    private let button = UIButton(type: .system)

    override func viewDidLoad() {
        super.viewDidLoad()
        title = "Konfigurasi"
        view.backgroundColor = .systemBackground
        urlField.placeholder = "Launch URL (opsional)"
        urlField.borderStyle = .roundedRect
        examIdField.placeholder = "Exam ID"
        examIdField.borderStyle = .roundedRect
        examIdField.text = "sample-exam-001"
        button.setTitle("Lanjut", for: .normal)
        button.addTarget(self, action: #selector(nextTapped), for: .touchUpInside)
        let stack = UIStackView(arrangedSubviews: [urlField, examIdField, button])
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

    @objc private func nextTapped() {
        navigationController?.pushViewController(ChecklistViewController(), animated: true)
    }
}
