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
            // prebuild
            PrebuildTask prebuildTask = project.task('prebuild', type:PrebuildTask, group:"Slim", description: "check environment")
            // shrink
            ShrinkTask shrinkTask = project.task('shrink', type: ShrinkTask, group: "Slim", description: "decrease file size of bundle.apk")
            shrinkTask.dependsOn prebuildTask

            // genSlimBundle
            GenBundleTask genBundleTask = project.task('genSlimBundle', type: GenBundleTask, group: "Slim", description: "generate bundle.apk")
//            genBundleTask.dependsOn shrinkTask
            // fill
            FillTask fillTask = project.task('fill', type:FillTask, group: "Slim", description: "delete and copy res")
            def assembleSlim = project.task('assembleSlim', type: AssembleTask, group:"Slim")
            def checkDuplicate = project.task('checkDuplicate', type:CheckDuplicateTask, group:'Slim', description: "check duplicate name of modules' res")
            def android = project.extensions.android
            android.applicationVariants.all { variant ->
//            variant.mergedFlavor.versionCode =
                variant.outputs.each { output ->
                File origin = output.outputFile
                String dir = output.outputFile.getAbsolutePath()
                println "dir = ${dir}"
                File parent = new File ("${origin.getParentFile().getParentFile().getParentFile().getAbsolutePath()}\\Slim\\output")
                if (!parent.exists()){
                    parent.mkdirs()
                }
                String path = genPath(parent.absolutePath, variant.versionName)
                println "path in SlimPlugin = ${path}"
                output.outputFile = new File(path)
//                output.outputFile = new File("D:\\demo\\1.apk")
                }
            }
            assembleSlim.doLast{
                def assembleDebug = project.tasks.findByName('assembleDebug')
                assembleDebug.execute()
            }
        }
    }
    String genPath (String parent, String versionName){
        //\test\build\Slim\output\SlimBundle_v1.0.3_201701111648.apk
        return parent << "\\" << "SlimBundle" <<  "_v" << versionName <<"_" <<new Date().format("yyyyMMddHHmm")<<".apk"
    }
}