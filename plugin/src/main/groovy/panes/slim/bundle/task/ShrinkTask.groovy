package panes.slim.bundle.task

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.DependencySet
import org.gradle.api.tasks.TaskAction

class ShrinkTask extends DefaultTask {
    @TaskAction
    void shrink() {
        String currentPath = project.getProjectDir()
        println "project.path = ${currentPath}"
        def android = project.extensions.android
        def srcFiles = android.sourceSets.main.res.srcDirs
        def javaFiles = android.sourceSets.main.java.srcDirs
        def testFiles = android.sourceSets.androidTest
        def javatest = android.sourceSets.test
        println "src = ${srcFiles}; java = ${javaFiles}; test = ${testFiles}; javatest = ${javatest}"
        srcFiles.each{
            it.delete()
        }
        javaFiles.each{
            it.delete()
        }
        testFiles.metaClass.properties.each {
            MetaBeanProperty property = it
            println property.name
        }
//        testFiles.each{
//            it.delete()
//        }
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

        DependencySet compilesDependencies = project.configurations.compile.dependencies
        project.configurations.testCompile.each {
            println "test: ${it.name}"
        }

        project.configurations.compile.each {
            println "compile: ${it.name}"
        }
/*
        test: junit-4.12.jar
        test: hamcrest-core-1.3.jar
        compile: appcompat-v7-24.1.1.aar
        compile: animated-vector-drawable-24.1.1.aar
        compile: support-v4-24.1.1.aar
        compile: support-vector-drawable-24.1.1.aar
        compile: support-annotations-24.1.1.jar
*/
    }

}