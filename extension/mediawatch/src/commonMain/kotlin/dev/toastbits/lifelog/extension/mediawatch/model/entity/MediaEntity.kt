package dev.toastbits.lifelog.extension.mediawatch.model.entity

import dev.toastbits.lifelog.core.specification.model.entity.LogEntity
import dev.toastbits.lifelog.core.specification.model.entity.LogEntityCompanion

sealed interface MediaEntity: LogEntity {
    companion object: LogEntityCompanion<MediaEntity>(LogEntity) {
        override fun getAllProperties(): List<LogEntity.Property<*, *>> = emptyList()
    }
}
