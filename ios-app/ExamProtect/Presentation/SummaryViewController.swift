import UIKit

final class SummaryViewController: UIViewController {
    override func viewDidLoad() {
        super.viewDidLoad()
        title = "Ringkasan"
        view.backgroundColor = .systemBackground
        let label = UILabel()
        label.text = "Ringkasan pelanggaran akan ditampilkan di sini."
        label.translatesAutoresizingMaskIntoConstraints = false
        let exitButton = UIButton(type: .system)
        exitButton.setTitle("Keluar", for: .normal)
        exitButton.addTarget(self, action: #selector(exitTapped), for: .touchUpInside)
        exitButton.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(label)
        view.addSubview(exitButton)
        NSLayoutConstraint.activate([
            label.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            label.centerYAnchor.constraint(equalTo: view.centerYAnchor),
            exitButton.topAnchor.constraint(equalTo: label.bottomAnchor, constant: 16),
            exitButton.centerXAnchor.constraint(equalTo: view.centerXAnchor)
        ])
    }

    @objc private func exitTapped() {
        navigationController?.popToRootViewController(animated: true)
    }
}
