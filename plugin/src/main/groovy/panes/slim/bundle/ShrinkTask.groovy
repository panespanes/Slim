package panes.slim.bundle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class ShrinkTask extends DefaultTask {
    @TaskAction
    void shrink() {
        String currentPath = project.getProjectDir()
        println "project.path = ${currentPath}"
        String[] keepDirs = ["${currentPath}\\build", "${currentPath}\\src"];
        def argDirs = project.extensions.Slim.keepDir.collect{
            println "kepp dirs: ${it}"
            it = "${currentPath}\\${it}"
        }
        keepDirs = keepDirs + argDirs
        keepDirs.each {
            println "keep: ${it}"
        }

        def files = project.getProjectDir().listFiles()
        def dirs = []
        files.each {
            if (it.isDirectory()){
                dirs.add(it.getAbsolutePath())
            }
        }
//        files.each {File file ->
//            String fileName = file.getName()
//            if (file.isDirectory()){
//                keepDirs.find {String keepDir ->
//                    if (fileName.equals(keepDir)){
//                        dirsToDelete.add(fileName)
//                    }
//                }
//            }
//        }
        dirs.removeAll(keepDirs)
        dirs.each {
            println it
        }
    }

}