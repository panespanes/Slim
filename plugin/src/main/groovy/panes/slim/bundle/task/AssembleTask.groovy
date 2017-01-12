package panes.slim.bundle.task

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import panes.slim.bundle.extension.OutPutExtension

class AssembleTask extends DefaultTask{
    OutPutExtension outPutExtension
    String mode
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
                File parent = new File("${origin.getParentFile().getParentFile().getParentFile().getAbsolutePath()}\\Slim\\output")
                if (!parent.exists()) {
                    parent.mkdirs()
                }
                path = genPath(parent.absolutePath, variant.versionName)
                output.outputFile = new File(path)
            }
        println "output path: ${path}"
        File destFile = new File(path)
        if (destFile.exists()){
            destFile.delete()
        }
        }
    }

    String genPath (String parent, String versionName){
        //\test\build\Slim\output\bundle_xhdpi_v1.0.3_201701111648.apk
        return parent << "\\" << outPutExtension.fileName <<"_"<< mode <<  "_v" << versionName <<"_" <<new Date().format("yyyyMMddHHmm")<<".apk"
    }
}