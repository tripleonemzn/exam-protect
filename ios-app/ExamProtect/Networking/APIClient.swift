import Foundation

struct SignedConfigResponse: Codable {
    let examId: String
    let jws: String
}

final class APIClient {
    let baseURL: URL?
    let urlSession: URLSession

    init(baseURL: URL?, session: URLSession = .shared) {
        self.baseURL = baseURL
        self.urlSession = session
    }

    func fetchPolicy(examId: String) async throws -> ExamPolicy {
        if baseURL == nil {
            return try await loadLocalPolicy()
        }
        guard let baseURL else { throw URLError(.badURL) }
        let url = baseURL.appendingPathComponent("/v1/exams/\(examId)/config")
        var req = URLRequest(url: url)
        req.httpMethod = "GET"
        let (data, resp) = try await urlSession.data(for: req)
        guard let http = resp as? HTTPURLResponse, http.statusCode == 200 else {
            return try await loadLocalPolicy()
        }
        // Catatan: JWS verification belum diimplementasi. Di MVP, kita asumsikan jws memuat payload JSON langsung.
        let cfg = try JSONDecoder().decode(SignedConfigResponse.self, from: data)
        let payload = Data(cfg.jws.utf8)
        return try JSONDecoder().decode(ExamPolicy.self, from: payload)
    }

    private func loadLocalPolicy() async throws -> ExamPolicy {
        guard let url = Bundle.main.url(forResource: "sample-policy", withExtension: "json") else {
            return ExamPolicy.sample()
        }
        let data = try Data(contentsOf: url)
        return try JSONDecoder().decode(ExamPolicy.self, from: data)
    }
}
