package panes.slim.bundle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class GenBundleTask extends DefaultTask {
    @TaskAction
    void genBundle(){
        project.logger.error("start generate bundle.apk")
    }
}