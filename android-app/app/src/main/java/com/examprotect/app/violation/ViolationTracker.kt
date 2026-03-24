package com.examprotect.app.violation

import com.examprotect.app.data.violation.ViolationDao
import com.examprotect.app.data.violation.ViolationEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger

class ViolationTracker(private val dao: ViolationDao) {
  private val scope = CoroutineScope(Dispatchers.IO)
  private val counter = AtomicInteger(0)

  fun countFlow(): Flow<Int> = dao.list().map { events ->
    counter.set(events.size)
    events.size
  }

  fun log(type: String, detail: String? = null) {
    scope.launch {
      dao.insert(ViolationEvent(type = type, detail = detail, ts = System.currentTimeMillis()))
    }
  }

  fun reset() {
    scope.launch { dao.clear() }
    counter.set(0)
  }
}
