package dev.toastbits.lifelog.specification.model

import dev.toastbits.lifelog.specification.model.UserContent.Modifier
import dev.toastbits.lifelog.specification.model.UserContent.Part
import dev.toastbits.lifelog.specification.model.reference.LogEntityReference

data class UserContent(
    val parts: List<Part>
) {
    sealed interface Part {
        val parts: List<Part>
        val modifiers: List<Modifier>

        fun asText(): String

        data class Single(
            val text: String,
            override val modifiers: List<Modifier> = emptyList()
        ): Part {
            override val parts: List<Part> get() = listOf(this)
            override fun asText(): String = text
        }

        data class Composite(
            override val parts: List<Part>,
            override val modifiers: List<Modifier> = emptyList()
        ): Part {
            override fun asText(): String = parts.joinToString("") { it.asText() }
        }
    }

    sealed interface Modifier {
        data object Italic: Modifier
        data object Bold: Modifier
        data class Reference(val reference: LogEntityReference<*>): Modifier
    }

    fun asText(): String = parts.joinToString("") { it.asText() }

    fun normalised(): UserContent {
        val newParts: MutableList<Part> = mutableListOf()
        for (part in parts) {
            val previous: Part? = newParts.lastOrNull()
            if (previous != null && part.modifiers.matches(previous.modifiers)) {
                newParts[newParts.size - 1] = part.appendTo(previous, part.modifiers)
            }
            else {
                newParts.add(part)
            }
        }
        return UserContent(newParts)
    }
}

private fun Part.appendTo(other: Part, newModifiers: List<Modifier>): Part {
    if (this is Part.Single && other is Part.Single) {
        return Part.Single(other.text + text, newModifiers)
    }

    return Part.Composite(other.parts + parts, newModifiers)
}

private fun List<Modifier>.matches(other: List<Modifier>): Boolean =
    size == other.size && sorted() == other.sorted()

private fun List<Modifier>.sorted(): List<Modifier> =
    sortedBy {
        when (it) {
            Modifier.Bold -> 0
            Modifier.Italic -> 1
            is Modifier.Reference -> 2
        }
    }
