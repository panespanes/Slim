package panes.slim.bundle

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.tasks.TaskAction
import panes.slim.bundle.task.ConflictsTask

class FillTask extends DefaultTask {
    @TaskAction
    void fill() {
        def sources = ['app', 'bundle']
        def modules = []
        def exclude = project.Slim.source.exclude
        def mainDirs = project.Slim.source.main
        println("exclude: ${exclude}")
        project.rootProject.allprojects.each {Project module ->
                if (!module.equals(project)) {
                    def excludeRst = exclude.find { String excludeName ->
                        assert module
                        excludeName.equals(module.name)
                    }
                    assert module && "2"
                    if (!excludeRst) {
                        modules << module
                    }
                }

        }
        println "res will be detected in:"
        modules.each {
            print "${it.name}; "
            assert it instanceof Project
        }
        println("\n")

//            String resDir = "\\src\\main\\res"
//        srcDirs = addDirs(modules, srcDirs, "src")
//        if (srcDirs){
//            mainDirs = addDirs(srcDirs, mainDirs, "main")
//            if (mainDirs){
//                resDirs = addDirs(mainDirs, resDirs, "res")
//                resDirs.each {File resDir ->
//                    println resDir.absolutePath
//                    def android = project.extensions.android
//                    def manifestFile = android.sourceSets.main.manifest.srcFile
//                    def packageName = new XmlParser().parse(manifestFile).attribute('package')
//                    println packageName
//                    def dirs = android.sourceSets.main.res.srcDirs
//                    dirs.each { File dir ->
//                        println dir.absolutePath
//                    }
//                }
//            }
//        }
        def srcDirs = []
        def fromDirs = [] //files
        modules.each { Project module ->
            ExtensionContainer container = module.extensions
            def android = container.findByName("android")
            if (android) {
                srcDirs = android.sourceSets.main.res.srcDirs
                srcDirs.each { File file ->
                    assert module
                    fromDirs << file
                    println "${module.name}: ${file.absolutePath}"
                }
            }
        }
        println "fromDirs"
        fromDirs.each { File file->
            println file.absolutePath
        }
        println "to"
        File to = project.extensions.android.sourceSets.main.res.srcDirs[0] // only exist one res folder in Android project
        println "${project.name}: ${to.absolutePath}"
        ConflictsTask conflictsTask = project.task('conflictsTask',type: ConflictsTask)
        conflictsTask.srcDirs = fromDirs
        conflictsTask.main = mainDirs
        conflictsTask.execute()
        println "conflicts: ${conflictsTask.conflicts}; flat: ${conflictsTask.flat}"
//        for (int i=0; i<1;i++){
//            def copyTask = project.task("copy${i}To${project.name}", type: Copy){
//                into to.absolutePath
//                doLast {
//                    println "finish ${name}"
//                }
//            }
//            copyTask.from(fromDirs[i].absolutePath)
//            copyTask.con
//            copyTask.execute()
//        }
    }


}