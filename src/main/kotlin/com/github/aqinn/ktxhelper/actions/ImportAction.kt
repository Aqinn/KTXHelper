package com.github.aqinn.ktxhelper.actions

import com.github.aqinn.ktxhelper.common.Utils
import com.intellij.codeInsight.CodeInsightActionHandler
import com.intellij.codeInsight.generation.actions.BaseGenerateAction
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElementFactory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.util.PsiUtilBase
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtImportList
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.resolve.ImportPath

/**
 * @Author Aqinn
 * @Date 2021/6/3 5:06 下午
 */
class ImportAction @JvmOverloads constructor(handler: CodeInsightActionHandler? = null) : BaseGenerateAction(handler) {

    lateinit var layoutName: String
    lateinit var fqName: String
    lateinit var importList: KtImportList
    lateinit var ktFile: KtFile


    override fun isValidForFile(project: Project, editor: Editor, file: PsiFile): Boolean {

        val fileType = file.fileType.name
        if (fileType != "Kotlin") {
            return false
        }

        val layout = Utils.getLayoutFileFromCaret(editor, file) ?: return false

        // get Full Qualified Name
        layoutName = findLayoutName(project, editor) ?: return false
        fqName = "kotlinx.android.synthetic.main.${layoutName}"

        ktFile = file as KtFile

        return true
    }

    private fun findLayoutName(project: Project, editor: Editor): String? {
        val file: PsiFile = PsiUtilBase.getPsiFileInEditor(editor, project)!!
        val offset = editor.caretModel.offset
        val candidate = file.findElementAt(offset) ?: file.findElementAt(offset - 1) ?: return null
        return candidate.text
    }

    private fun isAlreadyImportedOrError(): Boolean {
        importList = ktFile.importList ?: return true
        for (import in importList.imports) {
            val impQualifiedName = import.importedFqName!!.asString()
            if (fqName == impQualifiedName) {
                return true  // Already imported so nothing neede
            }
        }
        return false
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(PlatformDataKeys.PROJECT)!!
        val editor = e.getData(PlatformDataKeys.EDITOR)!!

        actionPerformImpl(project, editor)
    }

    private fun actionPerformImpl(project: Project, editor: Editor) {
        // check if already imported
        if (!isAlreadyImportedOrError()) {
            // import
            WriteCommandAction.runWriteCommandAction(project) {
                //do something
                @Suppress("MISSING_DEPENDENCY_CLASS")
                val factory = KtPsiFactory(project)
                val importPsi = factory.createImportDirective(
                    ImportPath(FqName(fqName), isAllUnder = true)
                )
                importList.add(importPsi)
                Utils.showInfoNotification(project, "Import successfully.")
            }
        } else {
            Utils.showInfoNotification(project, "Already import.")
        }

    }

    private fun addImportInJava(file: PsiFile, elementFactory: PsiElementFactory, fullyQualifiedName: String) {
        val targetFile = file as PsiJavaFile
        val importList = targetFile.importList ?: return

        // Check if already imported
        for (`is` in importList.allImportStatements) {
            val impQualifiedName = `is`.importReference!!.qualifiedName
            if (fullyQualifiedName == impQualifiedName) {
                return  // Already imported so nothing neede
            }
        }

        // Not imported yet so add it
        WriteCommandAction.runWriteCommandAction(file.project) {
            //do something
            importList.add(elementFactory.createImportStatementOnDemand(fullyQualifiedName))
        }
    }

}