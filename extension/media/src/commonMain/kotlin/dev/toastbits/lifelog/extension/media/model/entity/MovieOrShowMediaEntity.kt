package dev.toastbits.lifelog.extension.media.model.entity

import dev.toastbits.lifelog.extension.media.util.MediaStringId
import dev.toastbits.lifelog.specification.model.entity.LogEntity
import dev.toastbits.lifelog.specification.model.entity.LogEntityCompanion
import kotlin.time.Duration

interface MovieOrShowMediaEntity: MediaEntity {
    val runtime: Duration?
    val partCount: Int?

    companion object: LogEntityCompanion<MovieOrShowMediaEntity>(MediaEntity) {
        override fun getAllProperties(): List<LogEntity.Property<*, *>> =
            listOf(
                MediaStringId.Property.MediaEntityMovieOrShow.RUNTIME.property { runtime },
                MediaStringId.Property.MediaEntityMovieOrShow.PART_COUNT.property { partCount }
            )
    }
}
