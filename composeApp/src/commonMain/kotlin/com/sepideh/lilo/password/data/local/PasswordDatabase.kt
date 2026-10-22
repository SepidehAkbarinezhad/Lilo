package com.sepideh.lilo.password.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName = "passwords",
    foreignKeys = [ForeignKey(
        entity = PasswordServiceEntity::class, parentColumns = ["id"], childColumns = ["serviceId"],
        onDelete = ForeignKey.RESTRICT,
    )],
    indices = [Index("serviceId")],
)
data class PasswordEntity(
    @PrimaryKey val id: String,
    val format: Int,
    val nonce: ByteArray,
    val ciphertext: ByteArray,
    @ColumnInfo(defaultValue = "X''") val wrappedKey: ByteArray = byteArrayOf(),
    @ColumnInfo(defaultValue = "''") val title: String = "",
    @ColumnInfo(defaultValue = "''") val username: String = "",
    @ColumnInfo(defaultValue = "''") val website: String = "",
    @ColumnInfo(defaultValue = "0") val createdAt: Long = 0,
    @ColumnInfo(defaultValue = "0") val updatedAt: Long = 0,
    @ColumnInfo(defaultValue = "NULL") val serviceId: String? = null,
)

data class PasswordMetadata(
    val id: String, val title: String, val username: String, val website: String,
    val createdAt: Long, val updatedAt: Long, val serviceId: String?,
)

@Dao
interface PasswordDao {
    @Query("SELECT id, title, username, website, createdAt, updatedAt, serviceId FROM passwords")
    fun observe(): Flow<List<PasswordMetadata>>
    @Query("SELECT * FROM passwords WHERE id = :id") suspend fun get(id: String): PasswordEntity?
    @Upsert suspend fun save(record: PasswordEntity)
    @Query("SELECT COUNT(*) FROM password_services WHERE id = :id") suspend fun serviceExists(id: String): Int
    @Transaction suspend fun saveWithService(record: PasswordEntity) {
        require(record.serviceId != null && serviceExists(record.serviceId) == 1)
        save(record)
    }
    @Query("UPDATE passwords SET title = :title, username = :username, website = :website, createdAt = :createdAt, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateMetadata(id: String, title: String, username: String, website: String, createdAt: Long, updatedAt: Long)
    @Query("DELETE FROM passwords WHERE id = :id") suspend fun delete(id: String)
}

@Database(entities = [PasswordEntity::class, PasswordServiceEntity::class], version = 1, exportSchema = true)
@ConstructedBy(PasswordDbConstructor::class)
abstract class PasswordDatabase : RoomDatabase() { abstract fun passwords(): PasswordDao; abstract fun services(): PasswordServiceDao }
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object PasswordDbConstructor : RoomDatabaseConstructor<PasswordDatabase> { override fun initialize(): PasswordDatabase }
