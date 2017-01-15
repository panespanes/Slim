package panes.slim.bundle.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class AssembleSpecificTask extends DefaultTask {
    String mode
    @TaskAction
    void call (){
        println "assembleSpecific: ${mode}"
        if ("specific".equals(mode)){
            FileListTask.ALLMODES.intersect(project.Slim.output.mode).each {
                executeAssemble(it)
            }
        } else if ('all'.equals(mode)){
            FileListTask.ALLMODES.each {
                executeAssemble(it)
            }
        } else {
            executeAssemble(mode)
        }
    }

    void executeAssemble(String mode){
        println "starting assemble $mode ..."
        project.tasks.assemblehdpi.execute()

        FillTask fillTask = project.task("fill${it}", type:FillTask, group: "Slim", description: "delete and copy res")
        fillTask.dependsOn shrinkTask
        fillTask.mode = it
        AssembleTask assembleSlim = project.task("assemble${it}", type: AssembleTask, group:"Slim")
        assembleSlim.finalizedBy(assembleDebug)
        assembleSlim.mode = it
        assembleSlim.dependsOn fillTask
        AssembleSpecificTask dpiTask = project.task("${it}",type:AssembleSpecificTask, group: "Slim")
        dpiTask.mode = it
    }
}