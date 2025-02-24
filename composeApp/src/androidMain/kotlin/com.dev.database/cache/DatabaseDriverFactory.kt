package com.dev.database.cache

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

class AndroidDatabaseDriverFactory(private val context: Context) : DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        //context.deleteDatabase("GatorField.db") //add this to clear db
        return AndroidSqliteDriver(AppDatabase.Schema, context, "GatorField.db")
    }
}