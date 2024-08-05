package dev.toastbits.lifelog.extension.media.model.entity

import dev.toastbits.lifelog.specification.model.entity.LogEntity
import dev.toastbits.lifelog.specification.model.entity.LogEntityCompanion

sealed interface MediaEntity: LogEntity {
    companion object: LogEntityCompanion<MediaEntity>(LogEntity) {
        override fun getAllProperties(): List<LogEntity.Property<*, *>> = emptyList()
    }
}
