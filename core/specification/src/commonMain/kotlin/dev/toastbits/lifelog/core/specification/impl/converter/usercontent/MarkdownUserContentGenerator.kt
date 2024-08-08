package dev.toastbits.lifelog.core.specification.impl.converter.usercontent

import dev.toastbits.lifelog.core.specification.converter.alert.LogGenerateAlert
import dev.toastbits.lifelog.core.specification.converter.alert.LogParseAlert
import dev.toastbits.lifelog.core.specification.model.UserContent
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReference
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceGenerator
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceParser
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
    ): String {
        TODO("Not yet implemented")
    }

    private fun getFlavour(): MarkdownFlavourDescriptor = GFMFlavourDescriptor()
}
