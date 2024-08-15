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
        println("我是自定义 Gradle的中 FlutterConfigAssetsManager --> allTasks")
        // 整理 Flutter 项目：执行 flutter clean 和 flutter pub get
        cleanAndFetchFlutterDependencies(project)
        //创建配置文件
        createOrCheckConfigFile(project)
        println("开始执行别的任务")
        //最后继续执行一遍flutter pub get
        flutterPubGet(project)
    }
    // 整理 Flutter 项目：执行 flutter clean 和 flutter pub get
    void cleanAndFetchFlutterDependencies(Project project) {
        String osName = System.getProperty("os.name").toLowerCase()
        // 判断操作系统类型
        if (osName.contains("windows")) {
            // Windows 系统
            project.exec {
                commandLine 'cmd', '/c', 'flutter clean'
            }
            project.exec {
                commandLine 'cmd', '/c', 'flutter pub get'
            }
        } else {
            // macOS 或 Linux 系统
            project.exec {
                commandLine 'sh', '-c', 'flutter clean'
            }
            project.exec {
                commandLine 'sh', '-c', 'flutter pub get'
            }
        }
    }

    // 检查并创建配置文件函数
    void createOrCheckConfigFile(Project project) {
        // 获取 Flutter 项目根目录的上上级目录
        File upperDirectory = project.rootDir.parentFile

        // 定义配置文件的路径（假设文件名为 config.properties）
        File configFile = new File(upperDirectory, "config.properties")

        if (configFile.exists()) {
            println("配置文件已存在: ${configFile.absolutePath}")
        } else {
            // 创建一个新的配置文件
            try {
                configFile.createNewFile()
                configFile << "# 这是一个新的配置文件\n"
                configFile << "key1=value1\n"
                configFile << "key2=value2\n"
                println("已创建新的配置文件: ${configFile.absolutePath}")
            } catch (IOException e) {
                println("创建配置文件失败: ${e.message}")
            }
        }
    }

    void flutterPubGet(Project project) {
        String osName = System.getProperty("os.name").toLowerCase()
        // 判断操作系统类型
        if (osName.contains("windows")) {
            project.exec {
                commandLine 'cmd', '/c', 'flutter pub get'
            }
        } else {
            // macOS 或 Linux 系统
            project.exec {
                commandLine 'sh', '-c', 'flutter pub get'
            }
        }
    }
}