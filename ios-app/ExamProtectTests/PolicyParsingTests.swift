import XCTest
@testable import ExamProtect

final class PolicyParsingTests: XCTestCase {
    func testParseSample() throws {
        let bundle = Bundle(for: Self.self)
        guard let url = bundle.url(forResource: "sample-policy", withExtension: "json", subdirectory: "../ExamProtect/Resources") else {
            XCTFail("Missing sample-policy.json"); return
        }
        let data = try Data(contentsOf: url)
        let policy = try JSONDecoder().decode(ExamPolicy.self, from: data)
        XCTAssertEqual(policy.examId, "sample-exam-001")
        XCTAssertEqual(policy.teacherPinRequired, true)
    }

    func testAllowlist() {
        let p = ExamPolicy.sample()
        func isAllowed(_ url: URL) -> Bool {
            guard let host = url.host else { return false }
            return p.allowedDomains.contains(where: { host == $0 || host.hasSuffix(".\($0)") })
        }
        XCTAssertTrue(isAllowed(URL(string: "https://example.com/exam")!))
        XCTAssertTrue(isAllowed(URL(string: "https://sub.example.com/path")!))
        XCTAssertFalse(isAllowed(URL(string: "https://evil.com/")!))
    }
}
