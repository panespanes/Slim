package panes.slim.bundle.task

import org.codehaus.groovy.util.StringUtil
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import org.gradle.util.TextUtil
import panes.slim.bundle.utils.SlimConstant

class PrebuildTask extends DefaultTask{
    @TaskAction
    void prebuild(){
        // generate main module list
        if (!project.plugins.hasPlugin('com.android.application')) {
            throw new GradleException("'${project.name}' module must apply plugin 'com.android.application'")
        }
        def mainExt = project.Slim.source.main
        if (mainExt.size() >= 1){
            def allNames = project.rootProject.allprojects.collect{
                it.name
            }
            if (!allNames.intersect(mainExt).equals(mainExt)){
                throw new GradleException("there's something wrong with 'slim/main' configure in build.gradle")
            }
            setMain(mainExt)
        } else {
            def hasPlugin = project.rootProject.allprojects.findAll{
                !it.equals(project) && it.plugins.hasPlugin('com.android.application')
            }
            if (hasPlugin.size() > 1){
                throw new GradleException("'slim/main' must be configured in build.gradle")
            } else {
                setMain(hasPlugin[0].name)
            }
        }
        // generate backup folder
        String backupExt = project.Slim.output.backup
        if (!backupExt || backupExt?.isEmpty()){
            backupExt = project.rootProject.getProjectDir().absolutePath
        }
        String backup = backupExt + File.separator + "Slim" + File.separator + "backup"
        println "backup path: $backup"
        File backupFile = new File(backup)
        if (!backupFile.exists()){
            new File(backup).mkdirs()
        } else {
            if (!backupFile.isDirectory()){
                throw new GradleException("$backup already exist, consider modify Slim/output/backup in build.gradle")
            }
        }
        setBackup(backup)
        println "prebuild done"
    }

    void setMain(def main){
        println "set main: ${main}"
        SlimConstant.main = main
    }

    void setBackup (String dir){
        SlimConstant.backupFolder = dir
    }
}