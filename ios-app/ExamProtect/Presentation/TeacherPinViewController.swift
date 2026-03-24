import UIKit

final class TeacherPinViewController: UIViewController {
    var onVerified: (() -> Void)?
    private let field = UITextField()
    private let button = UIButton(type: .system)

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .systemBackground
        field.placeholder = "PIN 6 digit"
        field.isSecureTextEntry = true
        field.borderStyle = .roundedRect
        button.setTitle("OK", for: .normal)
        button.addTarget(self, action: #selector(okTapped), for: .touchUpInside)
        let stack = UIStackView(arrangedSubviews: [field, button])
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

    @objc private func okTapped() {
        if field.text == "000000" {
            onVerified?()
        } else {
            let alert = UIAlertController(title: "PIN salah", message: nil, preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "OK", style: .default))
            present(alert, animated: true)
        }
    }
}
