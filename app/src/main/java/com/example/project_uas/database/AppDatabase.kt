package com.example.project_uas.database

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.project_uas.model.User
import com.example.project_uas.model.Budget
import com.example.project_uas.model.Expense

@Database(
    entities = [User::class, Budget::class, Expense::class],
    version = 5, // ✅ naikkan versi ke 5
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun budgetDao(): BudgetDao
    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        // MIGRATION 1 ➜ 2 (kosong)
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Tidak digunakan
            }
        }

        // MIGRATION 2 ➜ 3 (rename nominal ➜ amount + tambah date + foreign key)
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE budgets_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        amount INTEGER NOT NULL
                    )
                """.trimIndent())

                database.execSQL("""
                    INSERT INTO budgets_new (id, name, amount)
                    SELECT id, name, nominal FROM budgets
                """.trimIndent())

                database.execSQL("DROP TABLE budgets")
                database.execSQL("ALTER TABLE budgets_new RENAME TO budgets")

                database.execSQL("""
                    CREATE TABLE expenses_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        budgetId INTEGER NOT NULL,
                        description TEXT NOT NULL,
                        amount INTEGER NOT NULL,
                        date INTEGER NOT NULL DEFAULT 0,
                        FOREIGN KEY(budgetId) REFERENCES budgets(id) ON DELETE CASCADE
                    )
                """.trimIndent())

                database.execSQL("""
                    INSERT INTO expenses_new (id, budgetId, description, amount, date)
                    SELECT id, budgetId, description, amount, 0 FROM expenses
                """.trimIndent())

                database.execSQL("DROP TABLE expenses")
                database.execSQL("ALTER TABLE expenses_new RENAME TO expenses")
            }
        }

        // MIGRATION 3 ➜ 4 (tambah kolom userId di budgets & expenses)
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE users_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        username TEXT NOT NULL,
                        firstName TEXT NOT NULL,
                        lastName TEXT NOT NULL,
                        password TEXT NOT NULL
                    )
                """.trimIndent())

                database.execSQL("""
                    INSERT INTO users_new (id, username, firstName, lastName, password)
                    SELECT NULL, username, firstName, lastName, password FROM User
                """.trimIndent())

                database.execSQL("DROP TABLE User")
                database.execSQL("ALTER TABLE users_new RENAME TO User")

                database.execSQL("""
                    CREATE TABLE budgets_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        amount INTEGER NOT NULL,
                        userId INTEGER NOT NULL,
                        FOREIGN KEY(userId) REFERENCES User(id) ON DELETE CASCADE
                    )
                """.trimIndent())

                database.execSQL("""
                    INSERT INTO budgets_new (id, name, amount, userId)
                    SELECT id, name, amount, 0 FROM budgets
                """.trimIndent())

                database.execSQL("DROP TABLE budgets")
                database.execSQL("ALTER TABLE budgets_new RENAME TO budgets")

                database.execSQL("""
                    CREATE TABLE expenses_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        budgetId INTEGER NOT NULL,
                        description TEXT NOT NULL,
                        amount INTEGER NOT NULL,
                        date INTEGER NOT NULL DEFAULT 0,
                        userId INTEGER NOT NULL,
                        FOREIGN KEY(budgetId) REFERENCES budgets(id) ON DELETE CASCADE,
                        FOREIGN KEY(userId) REFERENCES User(id) ON DELETE CASCADE
                    )
                """.trimIndent())

                database.execSQL("""
                    INSERT INTO expenses_new (id, budgetId, description, amount, date, userId)
                    SELECT id, budgetId, description, amount, date, 0 FROM expenses
                """.trimIndent())

                database.execSQL("DROP TABLE expenses")
                database.execSQL("ALTER TABLE expenses_new RENAME TO expenses")
            }
        }

        // ✅ MIGRATION 4 ➜ 5 (dummy migration untuk perubahan return type Room)
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Tidak ada perubahan struktural, hanya agar Room update skema
            }
        }

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3,
                        MIGRATION_3_4,
                        MIGRATION_4_5 // ✅ Tambahkan ini
                    )
                    .build()
                    .also { instance = it }
            }
    }
}
