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
                String dir = output.outputFile.getAbsolutePath()
                println "dir = ${dir}"
                File parent = new File("${origin.getParentFile().getParentFile().getParentFile().getAbsolutePath()}\\Slim\\output")
                if (!parent.exists()) {
                    parent.mkdirs()
                }
                path = genPath(parent.absolutePath, variant.versionName)
                println "path in SlimPlugin = ${path}"
                output.outputFile = new File(path)
//                output.outputFile = new File("D:\\demo\\1.apk")
            }
//        println "output path = ${path}"
//        File destFile = new File(path)
//        if (destFile.exists()){
//            destFile.delete()
//        }
        }
    }

    String genPath (String parent, String versionName){
        //\test\build\Slim\output\SlimBundle_v1.0.3_201701111648.apk
        return parent << "\\" << outPutExtension.fileName <<  "_v" << versionName <<"_" <<new Date().format("yyyyMMddHHmm")<<".apk"
    }
}