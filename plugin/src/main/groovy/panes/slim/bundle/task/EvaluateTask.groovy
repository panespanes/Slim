package panes.slim.bundle

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.tasks.TaskAction
import panes.slim.bundle.task.FileListTask
import panes.slim.bundle.utils.SlimConstant

class EvaluateTask extends DefaultTask{
    @TaskAction
    void evaluate(){
        project.tasks.slimPreBuild.execute()

        def modules = []
        def exclude = project.Slim.source.exclude
        def mainNames = SlimConstant.main
        def mainFiles = []
        def mainDirs = []
        project.rootProject.allprojects.each { Project module ->
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
                if (!excludeRst) {
                    modules << module
                }
            }

        }
        modules.sort{a,b->
            b.projectDir.absolutePath.size() <=> a.projectDir.absolutePath.size()
        }
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
        File txt = new File(SlimConstant.backupFolder + File.separator + "evaluate.txt")
        PrintWriter pw = txt.newPrintWriter()
        FileListTask.ALLMODES.each {
            FileListTask fileListTask = project.task("evaluateFileListTask$it",type: FileListTask)
            assert fromDirs instanceof ArrayList<File>
            fileListTask.srcDirs = fromDirs
            assert mainFiles instanceof ArrayList<File>
            fileListTask.main = mainDirs
            project.logger.error('-------------------------------------------')
            println "Applying $it resource..."
            pw.write("\n\nApplying $it resource...\n")
            println "following files will be removed:"
            pw.write("following files will be removed:\n")
            fileListTask.mode = it
            fileListTask.addConflicts()
            fileListTask.excludes.each { File f ->
                println f.absolutePath
                pw.write(f.absolutePath)
                pw.write("\n")
            }
        }
        pw.flush()
        pw.close()

    }
}