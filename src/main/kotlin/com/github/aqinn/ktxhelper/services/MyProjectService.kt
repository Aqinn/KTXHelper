package com.github.aqinn.ktxhelper.services

import com.github.aqinn.ktxhelper.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
