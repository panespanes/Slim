package panes.slim.bundle.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class ConflictsTask extends DefaultTask {
    def main = [] //File
    def srcDirs = [] //File
    def conflicts = [] //File that exist conflicts
    def flat = [] // all files
    @TaskAction
    void addConflicts(){
        if (main && main.size() > 0){
            def temp = srcDirs - main
            srcDirs = main + temp
        }
        println( "sort: ${srcDirs}")
        srcDirs.each {File src->
            findDirs(src.absolutePath)
        }
    }

    void findDirs (String path){
        File file = new File(path)
        if (file.exists()){
            File files = file.listFiles()
            files.each {File f->
                if (f.isDirectory()){
                    findDirs(f.absolutePath)
                } else {
                    def find = flat.find{File flatFile ->
                        flatFile.name.equals(f.name)
                    }
                    if (find){
                        conflicts << f
                    } else {
                        flat << f
                    }
                }
            }
        }
    }

}