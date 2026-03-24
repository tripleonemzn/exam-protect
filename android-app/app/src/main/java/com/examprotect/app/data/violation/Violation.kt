package com.examprotect.app.data.violation

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "violations")
data class ViolationEvent(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val type: String,
  val detail: String?,
  val ts: Long
)

@Dao
interface ViolationDao {
  @Insert
  suspend fun insert(e: ViolationEvent)

  @Query("SELECT * FROM violations ORDER BY ts DESC")
  fun list(): Flow<List<ViolationEvent>>

  @Query("DELETE FROM violations")
  suspend fun clear()
}

@Database(entities = [ViolationEvent::class], version = 1)
abstract class ViolationDb : RoomDatabase() {
  abstract fun dao(): ViolationDao
}
