package panes.slim.bundle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class CheckDuplicateTask extends DefaultTask{
    @TaskAction
    void checkDuplicate(){

        def allProjects = project.rootProject.allprojects
        allProjects.each {
            println it.name
        }
    }
}