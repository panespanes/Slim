package panes.slim.bundle

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.DependencySet
import org.gradle.api.tasks.TaskAction

class FillTask extends DefaultTask {
    @TaskAction
    void cut (){
        DependencySet compilesDependencies = project.configurations.compile.dependencies
        compilesDependencies.each {
            println it.name
        }
    }
}