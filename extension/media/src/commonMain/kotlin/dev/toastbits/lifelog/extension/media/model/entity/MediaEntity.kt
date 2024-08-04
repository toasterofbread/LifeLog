package dev.toastbits.lifelog.extension.media.model.entity

import dev.toastbits.lifelog.extension.media.util.MediaStringId
import dev.toastbits.lifelog.specification.model.entity.LogEntity
import dev.toastbits.lifelog.specification.model.entity.LogEntityProperty
import kotlin.time.Duration

sealed interface MediaEntity: LogEntity {
    val iteration: LogEntityProperty<Int, MediaStringId>
}
