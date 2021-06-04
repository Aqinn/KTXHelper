package com.github.aqinn.ktxhelper.common

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.wm.WindowManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.search.EverythingGlobalScope
import com.intellij.psi.search.FilenameIndex
import com.intellij.ui.awt.RelativePoint
import java.util.Locale

/**
 * @Author Aqinn
 * @Date 2021/6/3 6:42 下午
 */
object Utils {

    private const val fadeOutTime = 7500L

    /**
     * Try to find layout XML file in current source on cursor's position
     */
    fun getLayoutFileFromCaret(editor: Editor, file: PsiFile): PsiFile? {
        val offset = editor.caretModel.offset

//        val candidate = file.findElementAt(offset)
//        return findLayoutResource(candidate)

        val candidateA = file.findElementAt(offset)
        val candidateB = file.findElementAt(offset - 1)

        val layout: PsiFile? = findLayoutResource(candidateA)
        return layout ?: findLayoutResource(candidateB)
    }

    /**
     * Try to find layout XML file in selected element
     *
     * @param element
     * @return
     */
    fun findLayoutResource(element: PsiElement?): PsiFile? {
        println("Finding layout resource for element: " + element?.text)
        return when {
            element == null -> {
                null // nothing to be used
            }
            element.parent.parent.firstChild.text == null -> {
                null // no file to process
            }
            "R.layout" != element.parent.parent.firstChild.text -> {
                null // not layout file
            }
            else -> {
                val project = element.project
                val name = String.format(Locale.getDefault(), "%s.xml", element.text)
                return resolveLayoutResourceFile(element, project, name)
            }
        }
    }

    private fun resolveLayoutResourceFile(element: PsiElement, project: Project, name: String): PsiFile? {
        // restricting the search to the current module - searching the whole project could return wrong layouts
        val module = ModuleUtil.findModuleForPsiElement(element)
        var files: Array<PsiFile>? = null
        if (module != null) {
            // first omit libraries, it might cause issues like (#103)
            var moduleScope = module.moduleWithDependenciesScope
            files = FilenameIndex.getFilesByName(project, name, moduleScope)
            if (files == null || files.size <= 0) {
                // now let's do a fallback including the libraries
                moduleScope = module.getModuleWithDependenciesAndLibrariesScope(false)
                files = FilenameIndex.getFilesByName(project, name, moduleScope)
            }
        }
        if (files == null || files.size <= 0) {
            // fallback to search through the whole project
            // useful when the project is not properly configured - when the resource directory is not configured
            files = FilenameIndex.getFilesByName(project, name, EverythingGlobalScope(project))
            if (files.size <= 0) {
                return null // no matching files
            }
        }

        // TODO - we have a problem here - we still can have multiple layouts (some coming from a dependency)
        // we need to resolve R class properly and find the proper layout for the R class
        for (file in files!!) {
            println("Resolved layout resource file for name [" + name + "]: " + file.virtualFile)
        }
        return files[0]
    }

    /**
     * Display simple notification - information
     */
    fun showInfoNotification(project: Project, text: String) {
        showNotification(project, MessageType.INFO, text)
    }

    /**
     * Display simple notification of given type
     */
    fun showNotification(project: Project, type: MessageType, text: String) {
        val statusBar = WindowManager.getInstance().getStatusBar(project)
        JBPopupFactory.getInstance()
            .createHtmlTextBalloonBuilder(text, type, null)
            .setFadeoutTime(fadeOutTime)
            .createBalloon()
            .show(RelativePoint.getCenterOf(statusBar.component), Balloon.Position.atRight)
    }

    fun showMessage(project: Project, title: String, msg: String) {
        Messages.showMessageDialog(project, msg, title, Messages.getInformationIcon())
    }
}
