package dev.toastbits.lifelog.core.specification.model.reference

import dev.toastbits.lifelog.core.specification.database.LogDatabase
import dev.toastbits.lifelog.core.specification.model.entity.LogEntity

interface LogEntityReference {
    fun getEntity(database: LogDatabase): LogEntity
}
