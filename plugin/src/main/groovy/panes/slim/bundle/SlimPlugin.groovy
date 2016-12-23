package panes.slim.bundle

import org.gradle.api.Plugin
import org.gradle.api.Project

public class SlimPlugin implements Plugin<Project> {

    void apply(Project project) {
        def num = 3.14
        println "${num}"
        println('-----------------------------')
        println('-----------------------------')
        println('-----------------------------')
        project.task('testTask') << {
            println "${num}"
        }
    }
}