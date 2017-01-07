package panes.slim.bundle.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class FileListTask extends DefaultTask {
    def main = [] //File
    def srcDirs = [] //File
    def conflicts = [] //File that exist conflicts
    def flat = [:] // [File : rootDir]
    String priority = project.Slim.priority
    def modes = project.Slim.mode
    @TaskAction
    void addConflicts(){
        if (main && main.size() > 0){
            def temp = srcDirs - main
            srcDirs = main + temp
        }
        println "sort ${srcDirs}"
        srcDirs.each {File src->
            findDirs(src.absolutePath, src.absolutePath)
        }
        println "priority: ${priority}"
        println "will copy ${modes} folders"
    }

    void findDirs (String path, String root){
        File file = new File(path)
        if (file.exists()){
            def files = file.listFiles()
            files.each {File f->
                if (f.isDirectory()){
                    findDirs(f.absolutePath, root)
                } else {
//                    def find = flat.find{File flatFile ->
//                        flatFile.absolutePath.replace(root, "").equals(f.absolutePath)
//                    }
                    def find = flat.find {
                        File key= it.key
                        String value = it.value
                        key.absolutePath.replace(value, "").equals(f.absolutePath.replace(root, ""))
                    }
                    if (find){
                        conflicts << f
                    } else {
                        flat.put(f, root)
                    }
                }
            }
        }
    }

}