import Foundation
import UIKit
import Darwin

enum EnvironmentChecks {
    static func isDebuggerAttached() -> Bool {
        var info = kinfo_proc()
        var mib = [CTL_KERN, KERN_PROC, KERN_PROC_PID, getpid()]
        var size = MemoryLayout<kinfo_proc>.stride
        let result = sysctl(&mib, u_int(mib.count), &info, &size, nil, 0)
        return result == 0 && (info.kp_proc.p_flag & P_TRACED) != 0
    }

    static func isJailbreakSuspected() -> Bool {
        let suspicious = [
            "/Applications/Cydia.app",
            "/usr/sbin/sshd",
            "/bin/bash",
            "/private/var/lib/apt/"
        ]
        if suspicious.contains(where: { FileManager.default.fileExists(atPath: $0) }) {
            return true
        }
        if fopen("/bin/bash", "r") != nil {
            fclose(fopen("/bin/bash", "r"))
            return true
        }
        return false
    }
}
