package dev.toastbits.lifelog.specification.model.reference

import dev.toastbits.lifelog.specification.database.LogDatabase
import dev.toastbits.lifelog.specification.model.entity.LogEntity

interface LogEntityReference<T: LogEntity> {
    fun getEntity(database: LogDatabase): T
}
