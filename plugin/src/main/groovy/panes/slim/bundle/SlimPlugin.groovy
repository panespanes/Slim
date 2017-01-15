package panes.slim.bundle

import org.gradle.api.Plugin
import org.gradle.api.Project
import panes.slim.bundle.extension.OutPutExtension
import panes.slim.bundle.extension.SourceExtension
import panes.slim.bundle.task.*

public class SlimPlugin implements Plugin<Project> {

    void apply(Project project) {
        project.extensions.create("Slim", panes.slim.bundle.extension.SlimExtension)
        project.Slim.extensions.create('source', SourceExtension)
        project.Slim.extensions.create('output', OutPutExtension)
        project.logger.error("----------------------Slim  ------------------------------------")
        project.afterEvaluate {
            PrebuildTask prebuildTask = project.task('prebuild', type:PrebuildTask, group:"Slim", description: "check environment")
            ShrinkTask shrinkTask = project.task('shrink', type: ShrinkTask, group: "Slim", description: "decrease file size of bundle.apk")
            shrinkTask.dependsOn prebuildTask


            def checkDuplicate = project.task('checkDuplicate', type:CheckDuplicateTask, group:'Slim', description: "check duplicate name of modules' res")
            def assembleDebug = project.tasks.findByName('assembleDebug')

            def modes = project.Slim.output.mode
            def findMatch = modes.find{String mode->
                mode.equals('all')
            }
            if (findMatch){
                modes = FileListTask.ALLMODES
            }
            FileListTask.ALLMODES.each {
                // fill
                FillTask fillTask = project.task("fill${it}", type:FillTask, group: "Slim", description: "delete and copy res")
                fillTask.dependsOn shrinkTask
                fillTask.mode = it
                AssembleTask assembleSlim = project.task("assemble${it}", type: AssembleTask, group:"Slim")
                assembleSlim.finalizedBy(assembleDebug)
                assembleSlim.mode = it
                assembleSlim.dependsOn fillTask
                project.task("${it}", group: "Slim", dependsOn: assembleSlim)
            }
            def allDpiTask = project.task("alldpi", group:"Slim")
            allDpiTask.dependsOn FileListTask.ALLMODES
        }
    }
}