package panes.slim.bundle.task

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import panes.slim.bundle.extension.OutPutExtension

class AssembleTask extends DefaultTask{
    OutPutExtension outPutExtension
    @TaskAction
    void invoke(){
        String path
        def android = project.extensions.android
        outPutExtension = project.Slim.output
        android.applicationVariants.all { variant ->
//            variant.mergedFlavor.versionCode =
            variant.outputs.each { output ->
                File origin = output.outputFile
                println "task path = ${origin.absolutePath}"
            }
        }
//        println "output path = ${path}"
//        File destFile = new File(path)
//        if (destFile.exists()){
//            destFile.delete()
//        }

//        def assembleDebug = project.tasks.findByName('assembleDebug')
//        assembleDebug.execute()
//        println 'assemble finish.'

    }

    String genPath (String parent, String versionName){
        //\test\build\Slim\output\SlimBundle_v1.0.3_201701111648.apk
        return parent << "\\" << outPutExtension.fileName <<  "_v" << versionName <<"_" <<new Date().format("yyyyMMddHHmm")<<".apk"
    }
}