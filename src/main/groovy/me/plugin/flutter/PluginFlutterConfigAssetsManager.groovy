package me.plugin.flutter

import org.gradle.api.Plugin
import org.gradle.api.Project

class PluginFlutterConfigAssetsManager implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println("我是自定义 Gradle的 PluginFlutterConfigAssetsManager")

        try {
            allTasks(project)
        } catch (Exception e) {
            println("Error in getConfigProperties: ${e.message}")
        }

        project.afterEvaluate {
            project.tasks.findByName('preBuild')?.dependsOn 'allTasks'
        }
    }

    void allTasks(Project project) {

    }

}