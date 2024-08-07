package dev.toastbits.lifelog.specification.model

import dev.toastbits.lifelog.specification.model.UserContent.Modifier
import dev.toastbits.lifelog.specification.model.UserContent.Part
import dev.toastbits.lifelog.specification.model.reference.LogEntityReference

data class UserContent(
    val parts: List<Part>
) {
    sealed interface Part {
        val parts: List<Part>
        val modifiers: Set<Modifier>

        fun asText(): String
        fun withModifiers(modifiers: Set<Modifier>): Part
        fun isNotEmpty(): Boolean
        
        data class Single(
            val text: String,
            override val modifiers: Set<Modifier> = emptySet()
        ): Part {
            override val parts: List<Part> get() = listOf(this)
            override fun asText(): String = text
            override fun withModifiers(modifiers: Set<Modifier>): Part = copy(modifiers = modifiers)
            override fun isNotEmpty(): Boolean = text.isNotEmpty()
        }

        data class Composite(
            override val parts: List<Part>,
            override val modifiers: Set<Modifier> = emptySet()
        ): Part {
            override fun asText(): String = parts.joinToString("") { it.asText() }
            override fun withModifiers(modifiers: Set<Modifier>): Part = copy(modifiers = modifiers)
            override fun isNotEmpty(): Boolean = parts.any { it.isNotEmpty() }
        }
    }

    sealed interface Modifier {
        data object Italic: Modifier
        data object Bold: Modifier
        data object Strikethrough: Modifier
        data object Code: Modifier
        data object CodeBlock: Modifier
        data class Reference(val reference: LogEntityReference<*>): Modifier
    }

    fun asText(): String = parts.joinToString("") { it.asText() }
    
    fun normalised(): UserContent = UserContent(parts.normalised())

    fun isNotEmpty(): Boolean =
        parts.any { it.isNotEmpty() }

    companion object {
        fun single(text: String, modifiers: Set<Modifier> = emptySet()): UserContent =
            UserContent(listOf(Part.Single(text, modifiers)))
    }
}

private fun List<Part>.normalised(): List<Part> {
    val newParts: MutableList<Part> = mutableListOf()
    for (part in this) {
        val previous: Part? = newParts.lastOrNull()
        if (previous != null && part.modifiers.matches(previous.modifiers)) {
            newParts[newParts.size - 1] = part.appendTo(previous, part.modifiers)
        }
        else {
            newParts.add(part)
        }
    }

    val i: MutableListIterator<Part> = newParts.listIterator()
    while (i.hasNext()) {
        val part: Part = i.next()

        if (part !is Part.Composite) {
            continue
        }

        val normalisedParts: List<Part> = part.parts.normalised()
        if (normalisedParts.isEmpty()) {
            i.remove()
        }
        else if (normalisedParts.size == 1) {
            val singlePart: Part = normalisedParts.single()
            i.set(singlePart.withModifiers(singlePart.modifiers + part.modifiers))
        }
        else {
            i.set(part.copy(parts = normalisedParts))
        }
    }

    return newParts
}

private fun Part.appendTo(other: Part, newModifiers: Set<Modifier>): Part {
    if (this is Part.Single && other is Part.Single) {
        return Part.Single(other.text + text, newModifiers)
    }

    return Part.Composite(other.parts + parts, newModifiers)
}

private fun Set<Modifier>.matches(other: Set<Modifier>): Boolean =
    size == other.size && sorted() == other.sorted()

private fun Set<Modifier>.sorted(): List<Modifier> =
    sortedBy {
        when (it) {
            Modifier.Bold -> 0
            Modifier.Italic -> 1
            Modifier.Strikethrough -> 2
            Modifier.Code -> 3
            Modifier.CodeBlock -> 4
            is Modifier.Reference -> 5
        }
    }
