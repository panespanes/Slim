package panes.slim.bundle.task

import groovy.util.slurpersupport.GPathResult
import groovy.xml.QName
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.DependencySet
import org.gradle.api.tasks.TaskAction
import panes.slim.bundle.utils.AndroidUtil

class ShrinkTask extends DefaultTask {
    @TaskAction
    void shrink() {
        String currentPath = project.getProjectDir()
        println "project.path = ${currentPath}"
        def android = AndroidUtil.android(project)
        def srcFiles = android.sourceSets.main.res.srcDirs
        def javaFiles = android.sourceSets.main.java.srcDirs
        def androidTestFiles = android.sourceSets.androidTest
        def testFiles = android.sourceSets.test
        println "src = ${srcFiles}; java = ${javaFiles}; test = ${androidTestFiles}; testFiles = ${testFiles}"
        srcFiles.each{
            it.deleteDir()
        }
        javaFiles.each{
            it.deleteDir()
        }
        androidTestFiles.java.srcDirs.each{File f->
            String path = f.parentFile.absolutePath
            if (path.endsWith('androidTest')){
                f.parentFile.deleteDir()
            }
        }
        testFiles.java.srcDirs.each{File f->
            String path = f.parentFile.absolutePath
            if (path.endsWith('test')){
                f.parentFile.deleteDir()
            }
        }
//        androidTestFiles.metaClass.properties.each {
//            MetaBeanProperty property = it
//            if (property.name.equals("root")){
//                println "root = " << property.getProperty()
//            }
//            println "${property.name}: ${property.type}"
//            if (property.name.equals('root')){
////                println "root = ${property.getProperty('root')}"
//            }
//        }
//        androidTestFiles.metaClass.getMetaPropertyValues().each {
//            println "${it.name}: ${it.value}"
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
        DependencySet testDependencies = project.configurations.testCompile.dependencies
        def deleteCompile = compilesDependencies.findAll {
            it.name.contains('appcompat')
        }
//        compilesDependencies.removeAll(deleteCompile)
        println "delete compile: " <<deleteCompile
        def deleteTest = testDependencies.findAll {
            it.name.equals('junit')
        }
//        testDependencies.removeAll(deleteTest)
        println "delete compile: " << deleteTest
/*
        test: junit-4.12.jar
        test: hamcrest-core-1.3.jar
        compile: appcompat-v7-24.1.1.aar
        compile: animated-vector-drawable-24.1.1.aar
        compile: support-v4-24.1.1.aar
        compile: support-vector-drawable-24.1.1.aar
        compile: support-annotations-24.1.1.jar
*/

        /**
         * <a  attr = 1>    .@attr / [@attr]
         *      <b>         .name().each / .b
         *
         *      </b>
         *
         * </a>
         */
//        GPathResult androidManifest = new XmlSlurper().parse(AndroidUtil.manifestPath(project))
        GPathResult androidManifest = new XmlSlurper().parse("D:\\tao.pan\\personal\\Slim\\Slim\\test\\src\\main\\test.xml")
//        println androidManifest['@android:versionName']
        println androidManifest.application.@'android:supportsRtl'
        println androidManifest.name()
        androidManifest.children().each {GPathResult r->
            println "body = $r"
            println r.getProperty("android:supportsRtl")
        }
//        androidManifest.application.replaceNode {
//            application(panes:"slim"){
//                test("title")
//            }
//        }
        def parser = new XmlParser().parse("D:\\tao.pan\\personal\\Slim\\Slim\\test\\src\\main\\test.xml")
//        parser.application.replaceNode {
//            application(panes:"slim"){
//                test("title")
//            }
//        }
        parser.appendNode(
                new QName("numberOfResults"),
                [:],
                "1"
        )
        println "qname = ${parser.numberOfResults.text()}"

        /**
         * def catcherInTheRye = response.value.books.'*'.find { node->
         /* node.@id == 2 could be expressed as node['@id'] == 2
                node.name() == 'book' && node.@id == '2'
         }
         */
        /**
         * response.value.books.book[0].replaceNode{
         book(id:"3"){
         title("To Kill a Mockingbird")
         author(id:"3","Harper Lee")
         }
         }

         */
    }

}