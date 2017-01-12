package panes.slim.bundle.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class AssembleSpecificTask extends DefaultTask {
    String mode
    @TaskAction
    void call (){
        println "assembleSpecific: ${mode}"
        if ("specific".equals(mode)){

        } else if ('all'.equals(mode)){

        }
        def assembleTask = project.tasks.findByName("assemble${mode}")
        assembleTask.execute()
    }
}