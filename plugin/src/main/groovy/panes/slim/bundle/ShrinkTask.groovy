package panes.slim.bundle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class ShrinkTask extends DefaultTask {
    @TaskAction
    void shrink() {
        String currentPath = project.getProjectDir()
        println "project.path = ${currentPath}"
        String[] keepDirAll = ["${currentPath}\\build", "${currentPath}\\src"];
        def keepDirExt = project.extensions.Slim.keepDir.collect{
            println "kepp : ${it}"
            it = "${currentPath}\\${it}"
        }
        keepDirAll += keepDirExt
        def files = project.getProjectDir().listFiles()
        def dirToDelete = []
        files.each {
            if (it.isDirectory()){
                dirToDelete.add(it.getAbsolutePath())
            }
        }
//        files.each {File file ->
//            String fileName = file.getName()
//            if (file.isDirectory()){
//                keepDirAll.find {String keepDir ->
//                    if (fileName.equals(keepDir)){
//                        dirsToDelete.add(fileName)
//                    }
//                }
//            }
//        }
        dirToDelete.removeAll(keepDirAll)
        dirToDelete.each {
            println "delete: ${it}"
            new File(it).deleteDir()
        }
    }

}