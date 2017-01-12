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
    static def ALLMODES = [ 'mdpi','hdpi','xhdpi','xxhdpi','xxxhdpi']
//    def ALLMODES = [ 'xxhdpi', 'xhdpi', 'hdpi', 'mdpi','xxxhdpi', 'xxhdpi', 'xhdpi', 'hdpi', 'mdpi']
    def xxxh = [] //File
    def xxh = []
    def xh = []
    def h = []
    def m = []
    def remain = []
    def delete = []
    def excludes = []
    int modeInt
    String priority = project.Slim.priority
    String mode
    @TaskAction
    void addConflicts(){
        flat = [:]
        conflicts = []
        xxxh = []
        xxh = []
        xh = []
        h = []
        m = []
        remain = []
        delete = []
        excludes = []
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
        delete = dpiAll()
        if ('size'.equals(priority)){
            applyNeighborDpi()
        } else {
            applyHigherDpi()
        }
    }
    // delete duplicate
    void applyModes (int current){
        def temp = []
        modeIndexToList(current).each {File f ->
            (delete - modeIndexToList(current)).each {File all ->
                if (f.name.equals(all.name)){

                    temp << all
                    excludes << all
                } else {
                    remain << all
                }
            }
        }
        delete = delete - temp
        [m, h, xh, xxh, xxxh].each {
             it.removeAll(temp)
        }
    }

    void applyNeighborDpi(){
        for (int offset=0; offset<ALLMODES.size() - 1; offset++){
            if (modeInt + offset <ALLMODES.size()){
                applyModes(modeInt + offset)
            }
            if (modeInt - offset > -1 && offset!=0){
                applyModes(modeInt - offset)
            }
        }
    }

    void applyHigherDpi(){
        for (int offset = 0; offset<ALLMODES.size() -1; offset++){
            if (modeInt + offset < ALLMODES.size()){
                applyModes(modeInt + offset)
            }
        }
        for (int offset =1; offset<ALLMODES.size() -1; offset++){
            if (modeInt - offset >=0){
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
        for (int i=0; i<ALLMODES.size(); i++){
            if (name.contains("-" << ALLMODES[i])){
                modeIndexToList(i) << file
            }
        }
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
    def dpiAll (){
        return m + h + xh + xxh + xxxh
    }

    int modeToIndex (){
        return ALLMODES.indexOf(mode)
    }
}