package panes.slim.bundle

import org.gradle.api.Plugin
import org.gradle.api.Project
import panes.slim.bundle.extension.SourceExtension
import panes.slim.bundle.task.CheckDuplicateTask
import panes.slim.bundle.task.FillTask
import panes.slim.bundle.task.GenBundleTask
import panes.slim.bundle.task.PrebuildTask
import panes.slim.bundle.task.ShrinkTask

public class SlimPlugin implements Plugin<Project> {

    void apply(Project project) {
        project.extensions.create("Slim", panes.slim.bundle.extension.SlimExtension)
        project.Slim.extensions.create('source', SourceExtension)
        project.logger.error("----------------------Slim  ------------------------------------")
        project.afterEvaluate {
            // prebuild
            PrebuildTask prebuildTask = project.task('prebuild', type:PrebuildTask, group:"Slim", description: "check environment")
            // shrink
            ShrinkTask shrinkTask = project.task('shrink', type: ShrinkTask, group: "Slim", description: "decrease file size of bundle.apk")
            shrinkTask.dependsOn prebuildTask
            // fill
            FillTask fillTask = project.task('fill', type:FillTask, group: "Slim", description: "delete and copy res")

            // genSlimBundle
            def assembleDebug = project.tasks.findByName('assembleDebug')
            GenBundleTask genBundleTask = project.task('genSlimBundle', type: GenBundleTask, group: "Slim", description: "generate bundle.apk")
            genBundleTask.dependsOn shrinkTask
//            genBundleTask.dependsOn assembleDebug
//            assembleDebug.mustRunAfter shrinkTask

            def checkDuplicate = project.task('checkDuplicate', type:CheckDuplicateTask, group:'Slim', description: "check duplicate name of modules' res")
        }
    }
}