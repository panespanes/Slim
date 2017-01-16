package panes.slim.bundle.task

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.CopySpec
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.tasks.AbstractCopyTask
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskAction

import java.lang.reflect.Type

class FillTask extends DefaultTask {
    String mode
    @TaskAction
    void fill() {
        def modules = []
        def exclude = project.Slim.source.exclude
        def mainNames = project.Slim.source.main
        def mainFiles = []
        def mainDirs = []

        println("exclude: ${exclude}")
        project.rootProject.allprojects.each {Project module ->
            // use another loop instead of leftshift in order to maintain the original order of mainNames

//            def mainMatch = mainNames.find{
//                module.name.equals(it)
//            }
//            if (mainMatch){
//                mainFiles << module
//            }
            if (!module.equals(project)) {
                def excludeRst = exclude.find { String excludeName ->
                    assert module
                    excludeName.equals(module.name)
                }
                if (!excludeRst) {
                    modules << module
                }
            }

        }
        mainNames.each{
            def find = project.rootProject.allprojects.find{Project module ->
                module.name.equals(it)
            }
            if (find){
                mainFiles << find
            }
        }
        modules.sort{a,b->
            b.projectDir.absolutePath.size() <=> a.projectDir.absolutePath.size()
        }
        println "res will be detected in:"
        modules.each {
            print "${it.name}; "
            assert it instanceof Project
        }
        println("\n")

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
        println "fromDirs"
        fromDirs.each { File file->
            println file.absolutePath
        }
        FileListTask fileListTask = project.task("fileListTask${mode}",type: FileListTask)
        assert fromDirs instanceof ArrayList<File>
        fileListTask.srcDirs = fromDirs
        assert mainFiles instanceof ArrayList<File>
        fileListTask.main = mainDirs

        println 'apply mode: ' << mode
        fileListTask.mode = mode
        fileListTask.addConflicts()
        println 'excludes: '
        fileListTask.excludes.each { File f ->
            println f.absolutePath
        }

        ((fileListTask.flat.keySet() as List) - fileListTask.excludes).each {File file ->
            AbstractCopyTask copyTask = project.task("copy${mode}${file.absolutePath}", type: Copy, group: "Slim")
            String old = file.parentFile.absolutePath
            def moduleMatch = modules.findAll{Project module ->
                old.contains(module.projectDir.absolutePath)
            }
            String dest = old.replace(moduleMatch[0].projectDir.absolutePath, project.projectDir.absolutePath)
            println "dest = ${dest}"
            copyTask.from(file.absolutePath)
            copyTask.into(dest)
            copyTask.execute()
        }

    }


}