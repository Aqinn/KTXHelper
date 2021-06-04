package com.github.aqinn.ktxhelper.actions

import com.github.aqinn.ktxhelper.common.Utils
import com.intellij.codeInsight.CodeInsightActionHandler
import com.intellij.codeInsight.generation.actions.BaseGenerateAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiUtilBase
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.resolve.ImportPath

/**
 * @Author Aqinn
 * @Date 2021/6/3 5:06 下午
 */
class ImportAction @JvmOverloads constructor(handler: CodeInsightActionHandler? = null) : BaseGenerateAction(handler) {

    private lateinit var layoutName: String
    private lateinit var fqName: String
    private lateinit var ktFile: KtFile

    override fun isValidForFile(project: Project, editor: Editor, file: PsiFile): Boolean {
        return when {
            file.fileType.name != "Kotlin" -> false
            Utils.getLayoutFileFromCaret(editor, file) == null -> false
            else -> {
                // get Full Qualified Name
                layoutName = findLayoutName(project, editor) ?: return false
                fqName = "kotlinx.android.synthetic.main.$layoutName"
                ktFile = file as KtFile
                true
            }
        }
    }

    private fun findLayoutName(project: Project, editor: Editor): String? {
        val file: PsiFile = PsiUtilBase.getPsiFileInEditor(editor, project)!!
        val offset = editor.caretModel.offset
        val candidate = file.findElementAt(offset) ?: file.findElementAt(offset - 1) ?: return null
        return candidate.text
    }

    private fun isAlreadyImportedOrError(): Boolean {
        return if (ktFile.importList == null) {
            true
        } else {
            for (import in ktFile.importList!!.imports) {
                val impQualifiedName = import.importedFqName!!.asString()
                if (fqName == impQualifiedName) {
                    return true // Already imported
                }
            }
            false
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(PlatformDataKeys.PROJECT)!!
        actionPerformImpl(project)
    }

    private fun actionPerformImpl(project: Project) {
        // check if already imported
        if (!isAlreadyImportedOrError()) {
            // import
            WriteCommandAction.runWriteCommandAction(project) {
                // do something
                @Suppress("MISSING_DEPENDENCY_CLASS")
                val factory = KtPsiFactory(project)
                val importPsi = factory.createImportDirective(
                    ImportPath(FqName(fqName), isAllUnder = true)
                )
                ktFile.importList!!.add(importPsi)
                Utils.showInfoNotification(project, "Import successfully.")
            }
        } else {
            Utils.showInfoNotification(project, "Already import.")
        }
    }
}
