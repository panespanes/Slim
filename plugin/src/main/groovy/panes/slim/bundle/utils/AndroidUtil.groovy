package panes.slim.bundle.utils

import org.gradle.api.Project

class AndroidUtil {
    static def android (Project project){
        project.extensions.android
    }
    static File resFile (Project project){
       return project.extensions.android.sourceSets.main.res.srcDirs[0]
    }
    static String manifestPath (Project project){
        android(project).sourceSets.main.manifest.srcFile.absolutePath
    }
}