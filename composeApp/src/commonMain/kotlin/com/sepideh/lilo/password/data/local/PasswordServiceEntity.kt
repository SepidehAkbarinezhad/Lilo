package com.sepideh.lilo.password.data.local

import com.sepideh.lilo.core.domain.validation.requireUniqueName
import androidx.room.*
import androidx.sqlite.SQLiteConnection
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "password_services", indices = [Index(value = ["nameKey"], unique = true)])
data class PasswordServiceEntity(@PrimaryKey val id: String, val name: String, val nameKey: String, val isDefault: Boolean, val nameFa: String = name)

@Dao
abstract class PasswordServiceDao {
    @Query("SELECT * FROM password_services ORDER BY nameKey") abstract fun observe(): Flow<List<PasswordServiceEntity>>
    @Query("SELECT * FROM password_services WHERE id = :id") abstract suspend fun get(id: String): PasswordServiceEntity?
    @Insert abstract suspend fun insert(row: PasswordServiceEntity)
    @Query("UPDATE password_services SET name = :name, nameFa = :name, nameKey = :key WHERE id = :id AND isDefault = 0")
    abstract suspend fun rename(id: String, name: String, key: String)
    @Query("SELECT COUNT(*) FROM passwords WHERE serviceId = :id") abstract suspend fun usage(id: String): Int
    @Query("UPDATE passwords SET serviceId = :replacement WHERE serviceId = :id") protected abstract suspend fun move(id: String, replacement: String)
    @Query("DELETE FROM password_services WHERE id = :id") protected abstract suspend fun remove(id: String)
    @Query("SELECT * FROM password_services")
    abstract suspend fun getServiceSnapshot(): List<PasswordServiceEntity>

    private suspend fun checkName(name: String, excludingId: String? = null) {
        val existingNames = getServiceSnapshot().filter { it.id != excludingId }.flatMap {
            listOf(it.name, it.nameFa)
        }
        requireUniqueName(name, existingNames)
    }

    @Transaction open suspend fun insertUnique(row: PasswordServiceEntity) {
        checkName(row.name)
        checkName(row.nameFa)
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

internal val initialServices = listOf(
    PasswordServiceEntity("apple", "Apple", "apple", true, "اپل"),
    PasswordServiceEntity("github", "GitHub", "github", true, "گیت‌هاب"),
    PasswordServiceEntity("google", "Gmail", "gmail", true, "جیمیل"),
    PasswordServiceEntity("linkedin", "LinkedIn", "linkedin", true, "لینکدین"),
    PasswordServiceEntity("instagram", "Instagram", "instagram", true, "اینستاگرام"),
    PasswordServiceEntity("mobile-banking", "Mobile banking", "mobile banking", true, "همراه بانک"),
    PasswordServiceEntity("yahoo", "Yahoo", "yahoo", true, "یاهو"),
)
fun seedPasswordServices(connection: SQLiteConnection) {
    connection.prepare("INSERT INTO password_services (id, name, nameKey, isDefault, nameFa) VALUES (?, ?, ?, 1, ?)").use { statement ->
        initialServices.forEach { service ->
            statement.bindText(1, service.id)
            statement.bindText(2, service.name)
            statement.bindText(3, service.nameKey)
            statement.bindText(4, service.nameFa)
            statement.step()
            statement.reset()
        }
    }
}
object PasswordDefaultsCallback : RoomDatabase.Callback() {
    override fun onCreate(connection: SQLiteConnection) { seedPasswordServices(connection) }
}
