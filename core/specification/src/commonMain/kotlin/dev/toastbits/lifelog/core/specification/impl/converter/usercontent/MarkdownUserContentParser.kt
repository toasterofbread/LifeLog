package dev.toastbits.lifelog.core.specification.impl.converter.usercontent

import dev.toastbits.lifelog.core.specification.converter.alert.LogParseAlert
import dev.toastbits.lifelog.core.specification.model.UserContent
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReference
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceParser
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.MarkdownFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser

class MarkdownUserContentParser: UserContentParser {
    override fun parseUserContent(
        text: String,
        referenceParser: LogEntityReferenceParser,
        onAlert: (alert: LogParseAlert, line: Int) -> Unit
    ): UserContent {
        val parts: MutableList<UserContent.Part> = mutableListOf()

        val nodes: MutableList<ASTNode> =
            mutableListOf(
                MarkdownParser(getFlavour()).buildMarkdownTreeFromString(text)
            )

        var currentLine: Int = 0

        fun getNodeParts(node: ASTNode): List<UserContent.Part> {
            fun List<ASTNode>.getParts(): List<UserContent.Part> = flatMap { getNodeParts(it) }

            when (node.type.name) {
                "PARAGRAPH" -> return node.children.getParts()
                "TEXT", "WHITE_SPACE", "CODE_FENCE_CONTENT" -> {
                    val nodeText: String = node.getTextInNode(text).toString()
                    return listOf(UserContent.Part.Single(nodeText))
                }
                "EOL" -> {
                    currentLine++
                    return listOf(UserContent.Part.Single("\n"))
                }
                "BR" -> {
                    currentLine += 2
                    return listOf(UserContent.Part.Single("\n\n"))
                }
                "EMPH", "STRONG", "CODE_SPAN" -> {
                    var children: List<ASTNode> = node.children

                    val modifier: UserContent.Modifier =
                        when (node.type.name) {
                            "EMPH" -> UserContent.Modifier.Italic
                            "STRONG" -> UserContent.Modifier.Bold
                            "CODE_SPAN" -> {
                                children = children.removeSides("BACKTICK", "BACKTICK")
                                UserContent.Modifier.Code
                            }
                            else -> throw IllegalStateException(node.type.name)
                        }
                    return listOf(UserContent.Part.Composite(children.getParts(), setOf(modifier)))
                }
                "STRIKETHROUGH" -> {
                    val children: List<ASTNode> = node.children.removeSides("~", "~").removeSides("~", "~")
                    return listOf(UserContent.Part.Composite(children.flatMap { getNodeParts(it) }, setOf(UserContent.Modifier.Strikethrough)))
                }
                "CODE_FENCE" -> {
                    val children: List<ASTNode> = node.children.removeSides("CODE_FENCE_START", "CODE_FENCE_END").removeSides("EOL", "EOL")
                    return listOf(UserContent.Part.Composite(children.getParts(), setOf(UserContent.Modifier.CodeBlock)))
                }
                "INLINE_LINK" -> {
                    var linkTextParts: List<UserContent.Part>? = null
                    var linkReference: LogEntityReference? = null

                    for (linkChild in node.children) {
                        when (linkChild.type.name) {
                            "LINK_TEXT" -> {
                                val linkTextNodes: List<ASTNode> = linkChild.children.drop(1).dropLast(1)
                                linkTextParts = linkTextNodes.flatMap { getNodeParts(it) }
                            }
                            "LINK_DESTINATION" -> {
                                linkReference = referenceParser.parseReference(linkChild.getTextInNode(text).toString(), onAlert = { onAlert(it, currentLine) })
                            }
                            "(", ")" -> {}
                            else -> onAlert(node.toUnhandledAlert("LINK", text), currentLine)
                        }
                    }

                    val referenceModifier: UserContent.Modifier? = linkReference?.let { UserContent.Modifier.Reference(it) }
                    return listOf(UserContent.Part.Composite(linkTextParts.orEmpty(), setOfNotNull(referenceModifier)))
                }
                else -> {
                    if (node.type.name.length == 1) {
                        val nodeText: String = node.getTextInNode(text).toString()
                        return listOf(UserContent.Part.Single(nodeText))
                    }

                    onAlert(node.toUnhandledAlert("TOP", text), currentLine)
                    return emptyList()
                }
            }
        }

        while (nodes.isNotEmpty()) {
            val parent: ASTNode = nodes.removeLast()
            for (child in parent.children) {
                parts.addAll(getNodeParts(child))
            }
        }

        return UserContent(parts).normalised()
    }

    private fun ASTNode.toUnhandledAlert(scope: String, markdownText: String): LogParseAlert =
        LogParseAlert.UnhandledMarkdownNodeType(type.name, startOffset, endOffset, scope, getTextInNode(markdownText).toString())

    private fun List<ASTNode>.removeSides(startType: String, endType: String): List<ASTNode> {
        if (firstOrNull()?.type?.name != startType) {
            return this
        }

        return drop(1).run {
            if (lastOrNull()?.type?.name == endType) dropLast(1) else this
        }
    }

    private fun getFlavour(): MarkdownFlavourDescriptor = GFMFlavourDescriptor()
}
