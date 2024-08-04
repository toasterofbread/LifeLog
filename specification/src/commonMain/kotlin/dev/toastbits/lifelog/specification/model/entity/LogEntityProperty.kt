package dev.toastbits.lifelog.specification.model.entity

import dev.toastbits.lifelog.specification.util.StringId

interface LogEntityProperty<T, S: StringId> {
    var value: T
    val name: S
}
