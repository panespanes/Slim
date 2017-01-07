package panes.slim.bundle.task

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

class PrebuildTask extends DefaultTask{
    @TaskAction
    void prebuild(){
        if (!project.plugins.hasPlugin('com.android.application')) {
            throw new GradleException("'${project.name}' module must apply plugin 'com.android.application'")
        }
    }
}