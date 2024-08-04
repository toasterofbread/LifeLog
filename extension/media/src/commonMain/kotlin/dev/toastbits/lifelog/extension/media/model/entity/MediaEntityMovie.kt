package dev.toastbits.lifelog.extension.media.model.entity

import dev.toastbits.lifelog.extension.media.util.MediaStringId
import dev.toastbits.lifelog.specification.model.entity.LogEntityProperty
import kotlin.time.Duration

interface MediaEntityMovie: MediaEntity {
    val runtime: LogEntityProperty<Duration?, MediaStringId>
}
