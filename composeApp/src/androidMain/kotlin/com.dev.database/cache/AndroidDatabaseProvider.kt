package com.dev.database.cache

import android.content.Context

class AndroidDatabaseProvider(private val context: Context) : DatabaseProvider {
    override val database: Database by lazy {
        val driverFactory = AndroidDatabaseDriverFactory(context)
        Database(driverFactory)
    }

    companion object {
        private var instance: AndroidDatabaseProvider? = null

        fun initialize(context: Context) {
            if (instance == null) {
                instance = AndroidDatabaseProvider(context)
            }
        }
    }
}