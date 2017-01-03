package panes.slim.bundle

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

public class SlimPlugin implements Plugin<Project> {

    void apply(Project project) {
        if (!project.plugins.hasPlugin('com.android.application')) {
            throw new GradleException('error: Android Application plugin required')
        }
        project.extensions.create("Slim", SlimExtension)
//        project.task('myCustomTask', type: MyCustomTask, dependsOn: 'clean', group: "slim", description: "slim description")
        project.task('shrink', type: ShrinkTask, group:"slim", description: "decrease file size of bundle.apk")
        project.logger.error("----------------------slim build warning ------------------------------------")

    }
}