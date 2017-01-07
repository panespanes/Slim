package panes.slim.bundle.task

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.tasks.TaskAction

class FillTask extends DefaultTask {
    @TaskAction
    void fill() {
        def sources = ['app', 'bundle']
        def modules = []
        def exclude = project.Slim.source.exclude
        def mainNames = project.Slim.source.main
        def mainFiles = []
        def mainDirs = []
        println("exclude: ${exclude}")
        project.rootProject.allprojects.each {Project module ->
            def mainMatch = mainNames.find{
                module.name.equals(it)
            }
            if (mainMatch){
                mainFiles << module
            }
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
//            mainNames = addDirs(srcDirs, mainNames, "main")
//            if (mainNames){
//                resDirs = addDirs(mainNames, resDirs, "res")
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
        def projectToDir = {def projects ->
            def dirs = []
            projects.each {
                Project module ->
                    ExtensionContainer container = module.extensions
                    def android = container.findByName("android")
                    if (android) {
                        srcDirs = android.sourceSets.main.res.srcDirs
                        srcDirs.each { File file ->
                            dirs << file
                            println "${module.name}: ${file.absolutePath}"
                        }
                    }
            }
            return dirs
        }
        fromDirs = projectToDir(modules)
        mainDirs = projectToDir(mainFiles)
//        modules.each { Project module ->
//            ExtensionContainer container = module.extensions
//            def android = container.findByName("android")
//            if (android) {
//                srcDirs = android.sourceSets.main.res.srcDirs
//                srcDirs.each { File file ->
//                    assert module
//                    fromDirs << file
//                    println "${module.name}: ${file.absolutePath}"
//                }
//            }
//        }
        println "fromDirs"
        fromDirs.each { File file->
            println file.absolutePath
        }
        println "to"
        File to = project.extensions.android.sourceSets.main.res.srcDirs[0] // only exist one res folder in Android project
        println "${project.name}: ${to.absolutePath}"
        FileListTask fileListTask = project.task('fileListTask',type: FileListTask)
        assert fromDirs instanceof ArrayList<File>
        fileListTask.srcDirs = fromDirs
        assert mainFiles instanceof ArrayList<File>
        fileListTask.main = mainDirs
        fileListTask.execute()
        println "conflict:"
        fileListTask.conflicts.each {File f->
            println "${f.absolutePath}"
        }
        println "flat:"
        fileListTask.flat.each {
            println "${it.key.absolutePath}"
        }
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