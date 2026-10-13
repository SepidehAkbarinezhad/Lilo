package com.sepideh.lilo.password.data.local

import com.sepideh.lilo.core.domain.validation.requireUniqueName
import androidx.room.*
import androidx.sqlite.SQLiteConnection
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "password_services", indices = [Index(value = ["nameKey"], unique = true)])
data class PasswordServiceEntity(@PrimaryKey val id: String, val name: String, val nameKey: String, val isDefault: Boolean)

@Dao
abstract class PasswordServiceDao {
    @Query("SELECT * FROM password_services ORDER BY nameKey") abstract fun observe(): Flow<List<PasswordServiceEntity>>
    @Query("SELECT * FROM password_services WHERE id = :id") abstract suspend fun get(id: String): PasswordServiceEntity?
    @Insert abstract suspend fun insert(row: PasswordServiceEntity)
    @Query("UPDATE password_services SET name = :name, nameKey = :key WHERE id = :id AND isDefault = 0")
    abstract suspend fun rename(id: String, name: String, key: String)
    @Query("SELECT COUNT(*) FROM passwords WHERE serviceId = :id") abstract suspend fun usage(id: String): Int
    @Query("UPDATE passwords SET serviceId = :replacement WHERE serviceId = :id") protected abstract suspend fun move(id: String, replacement: String)
    @Query("DELETE FROM password_services WHERE id = :id") protected abstract suspend fun remove(id: String)
    @Query("SELECT * FROM password_services")
    abstract suspend fun getServiceSnapshot(): List<PasswordServiceEntity>

    private suspend fun checkName(name: String, excludingId: String? = null) {
        val existingNames = getServiceSnapshot().filter { it.id != excludingId }.flatMap {
            // Match both the stored name and the built-in name shown in the UI.
            if (it.isDefault && it.id == "google") listOf(it.name, "Gmail") else listOf(it.name)
        }
        requireUniqueName(name, existingNames)
    }

    @Transaction open suspend fun insertUnique(row: PasswordServiceEntity) {
        checkName(row.name)
        insert(row)
    }

    @Transaction open suspend fun renameUnique(id: String, name: String, key: String) {
        checkName(name, excludingId = id)
        rename(id, name, key)
    }

    @Transaction open suspend fun delete(id: String, replacement: String?) {
        val service = requireNotNull(get(id))
        require(!service.isDefault) { "Built-in services cannot be deleted" }
        if (usage(id) > 0) {
            require(replacement != null && replacement != id && get(replacement) != null)
            move(id, replacement)
        }
        remove(id)
    }
}

private val initialServices = listOf("apple" to "Apple", "github" to "GitHub", "google" to "Google", "linkedin" to "LinkedIn", "instagram" to "Instagram", "mobile-banking" to "Mobile banking", "yahoo" to "Yahoo")
fun seedPasswordServices(connection: SQLiteConnection) {
    connection.prepare("INSERT INTO password_services (id, name, nameKey, isDefault) VALUES (?, ?, ?, 1)").use { statement ->
        initialServices.forEach { (id, name) ->
            statement.bindText(1, id); statement.bindText(2, name); statement.bindText(3, name.lowercase())
            statement.step(); statement.reset()
        }
    }
}
object PasswordDefaultsCallback : RoomDatabase.Callback() {
    override fun onCreate(connection: SQLiteConnection) { seedPasswordServices(connection) }
}
