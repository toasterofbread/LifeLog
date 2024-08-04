package dev.toastbits.lifelog.specification.impl.converter.usercontent

import dev.toastbits.lifelog.specification.converter.error.LogParseAlert
import dev.toastbits.lifelog.specification.model.UserContent
import dev.toastbits.lifelog.specification.model.reference.LogEntityReference
import dev.toastbits.lifelog.specification.model.reference.LogEntityReferenceParser
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.MarkdownFlavourDescriptor
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser

class MarkdownUserContentParser: UserContentParser {
    override fun parseUserContent(
        markdownText: String,
        referenceParser: LogEntityReferenceParser,
        onAlert: (LogParseAlert) -> Unit
    ): UserContent {
        val parts: MutableList<UserContent.Part> = mutableListOf()

        val nodes: MutableList<ASTNode> =
            mutableListOf(
                MarkdownParser(getFlavour()).buildMarkdownTreeFromString(markdownText)
            )

        fun getNodeParts(node: ASTNode): List<UserContent.Part> {
            when (node.type.name) {
                "PARAGRAPH" -> return node.children.flatMap { getNodeParts(it) }
                "TEXT", "WHITE_SPACE" -> {
                    val nodeText: String = node.getTextInNode(markdownText).toString()
                    return listOf(UserContent.Part.Single(nodeText))
                }
                "EOL" -> return listOf(UserContent.Part.Single("\n"))
                "INLINE_LINK" -> {
                    var linkTextParts: List<UserContent.Part>? = null
                    var linkReference: LogEntityReference<*>? = null

                    for (linkChild in node.children) {
                        when (linkChild.type.name) {
                            "LINK_TEXT" -> {
                                val linkTextNodes: List<ASTNode> = linkChild.children.drop(1).dropLast(1)
                                linkTextParts = linkTextNodes.flatMap { getNodeParts(it) }
                            }
                            "LINK_DESTINATION" -> {
                                linkReference = referenceParser.parseReference(linkChild.getTextInNode(markdownText).toString(), onAlert = onAlert)
                            }
                            "(", ")" -> {}
                            else -> onAlert(LogParseAlert.UnhandledMarkdownNodeType(linkChild.type.name, "LINK"))
                        }
                    }

                    val referenceModifier: UserContent.Modifier? = linkReference?.let { UserContent.Modifier.Reference(it) }
                    return listOf(UserContent.Part.Composite(linkTextParts.orEmpty(), listOfNotNull(referenceModifier)))
                }
                else -> {
                    if (node.type.name.length == 1) {
                        val nodeText: String = node.getTextInNode(markdownText).toString()
                        return listOf(UserContent.Part.Single(nodeText))
                    }

                    onAlert(LogParseAlert.UnhandledMarkdownNodeType(node.type.name, "TOP"))
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

    private fun getFlavour(): MarkdownFlavourDescriptor = CommonMarkFlavourDescriptor()
}
