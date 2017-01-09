package panes.slim.bundle.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import panes.slim.bundle.utils.StringUtil

class FileListTask extends DefaultTask {
    def main = [] //File
    def srcDirs = [] //File
    def conflicts = [] //File that exist conflicts
    def flat = [:] // [File : rootDir]
//    def ALLMODES = [ 'xxxhdpi', 'xxhdpi', 'xhdpi', 'hdpi', 'mdpi']
    def ALLMODES = [ 'mdpi','hdpi','xhdpi','xxhdpi','xxxhdpi']
//    def ALLMODES = [ 'xxhdpi', 'xhdpi', 'hdpi', 'mdpi','xxxhdpi', 'xxhdpi', 'xhdpi', 'hdpi', 'mdpi']
    def xxxh = [] //File
    def xxh = []
    def xh = []
    def h = []
    def m = []
    def dpi = []
    def remain = []
    def delete = []
    def useless = []
    int modeInt
    String priority = project.Slim.priority
    String mode = project.Slim.mode
    @TaskAction
    void addConflicts(){
        mode = 'hdpi'
        modeInt = modeToIndex()
        if (main && main.size() > 0){
            def temp = srcDirs - main
            srcDirs = main + temp
        }
        println "sort ${srcDirs}"
        srcDirs.each {File src->
            findDirs(src.absolutePath, src.absolutePath)
        }
        println "priority: ${priority}"
        println "will copy ${mode} folders"
        delete = dpiExcept(modeIndexToList(modeInt))
//        applyModes(1)
        applyDpi()
        println "final delete size = ${delete.size()}"
//        println "delete duplicated remain result: "
//        remain.each { File f->
//            println f.absolutePath
//        }
        println "useless: "
        useless.each {File f->
            println f.absolutePath
        }
    }
    // delete duplicate
    void applyModes (int current){
        println "apply mode: " << current
        def apply = [] //File
//        if (mode.equals('all')){
//            return
//        }
        def temp = []
        modeIndexToList(current).each {File f ->
            println "h: ${f.absolutePath}"
            delete.each {File all ->
                if (f.name.equals(all.name)){
                    temp << all
                    useless << all
                    println "remove ${all.absolutePath}"
                } else {
                    remain << all
                }
            }
        }
        println "before delete size = ${delete.size()}"
        delete = delete - temp
    }

    void applyDpi (){
        for (int offset=0; offset<ALLMODES.size() - 1; offset++){
            if (modeInt + offset <ALLMODES.size()){
                applyModes(modeInt + offset)
            }
            if (modeInt - offset > -1 && offset!=0){
                applyModes(modeInt - offset)
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
                    def find = flat.find {
                        File key= it.key
                        String value = it.value
//                        key.absolutePath.replace(value, "").equals(f.absolutePath.replace(root, ""))
                        StringUtil.clearResRoot(key.absolutePath, value).equals(StringUtil.clearResRoot(f.absolutePath, root))
                    }
                    if (find){
                        conflicts << f
                    } else {
                        toFolder(f)
                        flat.put(f, root)
                    }
                }
            }
        }
    }

    void toFolder (File file){
         String name = file.getParent()
         if (name.contains("-" << ALLMODES[0])){
             m << file
         } else if (name.contains("-" << ALLMODES[1])){
             h << file
         } else if (name.contains("-" << ALLMODES[2])){
             xh << file
         } else if (name.contains("-" << ALLMODES[3])){
             xxh << file
         } else if (name.contains("-" << ALLMODES[4])){
             xxxh << file
         }
//        for (int i=0; i<ALLMODES.size(); i++){
//            modeIndexToList(i) << file
//        }
    }

    def modeIndexToList (int i){
        switch (i){
            case 0:
                return m
            case 1:
                return h
            case 2:
                return xh
            case 3:
                return xxh
            case 4:
                return xxxh
        }
    }
    def dpiExcept (def dpi){
        return m + h + xh + xxh +xxxh - dpi
    }

    int modeToIndex (){
        return ALLMODES.indexOf(mode)
    }
}