package panes.slim.bundle.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class FileListTask extends DefaultTask {
    def main = [] //File
    def srcDirs = [] //File
    def conflicts = [] //File that exist conflicts
    def flat = [:] // [File : rootDir]
//    def ALLMODES = [ 'xxxhdpi', 'xxhdpi', 'xhdpi', 'hdpi', 'mdpi']
    def ALLMODES = [ 'mdpi','hdpi','xhdpi','xxhdpi','xxxhdpi', 'xxhdpi', 'xhdpi', 'hdpi', 'mdpi']
//    def ALLMODES = [ 'xxhdpi', 'xhdpi', 'hdpi', 'mdpi','xxxhdpi', 'xxhdpi', 'xhdpi', 'hdpi', 'mdpi']
    def xxxh = []
    def xxh = []
    def xh = []
    def h = []
    def m = []
    def dpi = []
    def dpiDelete  = []
    String priority = project.Slim.priority
    def modes = project.Slim.mode
    @TaskAction
    void addConflicts(){
        modes = ['hdpi']
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
        applyModes()
        println "delete duplicated name result: "
        println dpiDelete
    }
    // delete duplicate
    void applyModes (){
        def apply = [] //File
        def modesMatch = modes.find{String it ->
            it.equals('all')
        }
        if (!modesMatch){
                modes.each {String mode ->
                    for (int i = 4; i < ALLMODES.size() && i > -1; i++){
                        if (ALLMODES[i].equals(mode)){

                            if ("size222".equals(priority)){
                                // TODO
                                // 0 to 4
                                i--
                            } else {
                                // 4 to -1

                                modeIndexToList(i).each {File f ->
                                    def temp = dpiExcept(modeIndexToList(i))
                                    dpiDelete = temp
                                    temp.each {File all ->
                                        if (f.name.equals(all.name)){
                                            println "name = ${f.name}"
                                            if (flat.get(f).equals(flat.get(all))){
                                                dpiDelete.remove(all)
                                            }
                                        }
                                    }
                                }
//                                i++
                            }
//                            apply << file
                        }
                    }
                }
        }
    }

    void findDirs (String path, String root){
        File file = new File(path)
        if (file.exists()){
            def files = file.listFiles()
            files.each {File f->
                if (f.isDirectory()){
                    findDirs(f.absolutePath, root)
                } else {
                    toFolder(f)
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

    void toFolder (File file){
         String name = file.getParent()
         if (name.contains(ALLMODES[0])){
             m << file
         } else if (name.contains(ALLMODES[1])){
             h << file
         } else if (name.contains(ALLMODES[2])){
             xh << file
         } else if (name.contains(ALLMODES[3])){
             xxh << file
         } else if (name.contains(ALLMODES[4])){
             xxxh << file
         }
//        for (int i=0; i<ALLMODES.size(); i++){
//            modeIndexToList(i) << file
//        }
    }

    def modeIndexToList (int i){
        switch (i){
            case 0:
                println "m"
                return m
            case 1:
                println 'h'
                return h
            case 2:
                println 'xh'
                return xh
            case 3:
                println 'xxh'
                return xxh
            case 4:
                println 'xxxh'
                return xxxh
            case 5:
                println 'xxh'
                return xxh
            case 6:
                println 'xh'
                return xh
            case 7:
                println 'h'
                return h
            case 8:
                println 'm'
                return m
        }
    }
    def dpiExcept (def dpi){
        return m + h + xh + xxh +xxxh - dpi
    }
}