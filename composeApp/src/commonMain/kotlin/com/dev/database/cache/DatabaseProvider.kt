package com.dev.database.cache

interface DatabaseProvider {
    val database: Database

    companion object {
        private var instance: DatabaseProvider? = null

        fun initialize(provider: DatabaseProvider) {
            instance = provider
        }

        fun getInstance(): DatabaseProvider {
            return instance ?: throw IllegalStateException("DatabaseProvider must be initialized first")
        }
    }
}