package panes.slim.bundle

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.tasks.TaskAction

class FillTask extends DefaultTask {
    @TaskAction
    void fill() {
        def sources = ['app', 'bundle']
        def modules = Project[]
        def exclude = project.Slim.source.exclude
        println("exclude: ${exclude}, ${project.name}")
        project.rootProject.allprojects.each {Project module
            if (!module.equals(project)) {
                def excludeRst = exclude.find{String excludeName ->
                    !excludeName.equals(module.name)
                }
                if (!excludeRst){
                    modules << module
                }
            }
        }
        modules.each {
            print "${it.name}; "
            assert it instanceof Project
        }
        println "exclude: ${project.name}"

        def needCopy = []
        def srcDirs = []
        def mainDirs = []
        def resDirs = []
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
        modules.each { Project module ->
            ExtensionContainer container = module.extensions
            def android = container.findByName("android")
            if (android) {
                srcDirs = android.sourceSets.main.res.srcDirs
                srcDirs.each { File file ->
                    println "${module.name}: ${file.absolutePath}"
                }
            }

        }
    }

    def addDirs(def dirs, def destList, String destName) {
        def ret = []
        dirs.each { File root ->
            if (root.exists() && root.isDirectory()) {
                root.listFiles().each { File file ->
                    if (destName.equals(file.name)) {
                        ret << file
                    }
                }
            }
        }
        return ret

    }
}