package me.plugin.flutter

import org.gradle.api.Plugin
import org.gradle.api.Project

class PluginFlutterConfigAssetsManager implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.task('FlutterConfigAssetsManager') {
            try {
                allTasks(project)
            } catch (Exception e) {
                println("Error in FlutterConfigAssetsManager: ${e.message}")
            }
        }
    }

    void allTasks(Project project) {
        // 整理 Flutter 项目：执行 flutter clean 和 flutter pub get
        flutterClean(project)
        flutterPubGet(project)
        // 处理配置文件
        def configFileManager = new ConfigFileManager(project)
        configFileManager.createConfigFile()

        // 最后再次执行 flutter pub get
        flutterPubGet(project)
    }

    void flutterPubGet(Project project) {
        executeCommand(project, 'flutter pub get')
    }

    void flutterClean(Project project) {
        executeCommand(project, 'flutter clean')
    }

    void executeCommand(Project project, String command) {
        String osName = System.getProperty("os.name").toLowerCase()
        String cmd = osName.contains("windows") ? 'cmd' : 'sh'
        String cmdFlag = osName.contains("windows") ? '/c' : '-c'

        project.exec {
            commandLine cmd, cmdFlag, command
        }
    }
}
