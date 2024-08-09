package dev.toastbits.lifelog.core.specification.impl.converter.usercontent

import dev.toastbits.lifelog.core.specification.converter.alert.LogGenerateAlert
import dev.toastbits.lifelog.core.specification.converter.alert.LogParseAlert
import dev.toastbits.lifelog.core.specification.model.UserContent
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReference
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceGenerator
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceParser
import dev.toastbits.lifelog.core.specification.model.sorted
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.MarkdownFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser

class MarkdownUserContentGenerator: UserContentGenerator {
    override fun generateUserContent(
        content: UserContent,
        referenceGenerator: LogEntityReferenceGenerator,
        onAlert: (alert: LogGenerateAlert, line: Int) -> Unit
    ): String = buildString {
        val builder: StringBuilderWrapper = StringBuilderWrapper(this, referenceGenerator, onAlert)
        for (part in content.parts) {
            builder.addPart(part)
        }
    }

    private class StringBuilderWrapper(
        private val stringBuilder: StringBuilder,
        private val referenceGenerator: LogEntityReferenceGenerator,
        private val onAlert: (alert: LogGenerateAlert, line: Int) -> Unit
    ) {
        var currentLine: Int = 0

        fun append(string: String) {
            stringBuilder.append(string)
            currentLine += string.count { it == '\n' }
        }

        fun addPart(part: UserContent.Part) {
            inModifiers(part.modifiers) {
                when (part) {
                    is UserContent.Part.Single -> append(part.text)
                    is UserContent.Part.Composite -> {
                        for (subPart in part.parts) {
                            addPart(subPart)
                        }
                    }
                }
            }
        }

        private fun inModifiers(
            modifiers: Iterable<UserContent.Modifier>,
            block: () -> Unit
        ) {
            val sorted: List<UserContent.Modifier> = modifiers.sorted()
            for (modifier in sorted) {
                append(modifier.getStart())
            }

            block()

            for (modifier in sorted.asReversed()) {
                append(modifier.getEnd())
            }
        }

        private fun UserContent.Modifier.getStart(): String =
            when (this) {
                UserContent.Modifier.Bold -> "**"
                UserContent.Modifier.Code -> "`"
                UserContent.Modifier.CodeBlock -> "```\n"
                UserContent.Modifier.Italic -> "*"
                UserContent.Modifier.Strikethrough -> "~~"
                is UserContent.Modifier.Reference -> {
                    referenceGenerator.generateReferencePath(reference, { onAlert(it, currentLine) }).toString()
                }
            }

        private fun UserContent.Modifier.getEnd(): String =
            when (this) {
                UserContent.Modifier.CodeBlock -> "\n```"
                else -> getStart()
            }
    }
}
