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
        println("开始执行 PluginFlutterConfigAssetsManager 的任务...")
        // 整理 Flutter 项目：执行 flutter clean 和 flutter pub get
        flutterClean(project)
        flutterPubGet(project)
        // 创建配置文件
        createOrCheckConfigFile(project)
        println("配置文件生成完成，执行剩余任务...")
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

    void createOrCheckConfigFile(Project project) {
        File upperDirectory = project.rootDir.parentFile
        File configFile = new File(upperDirectory, "config.properties")

        if (configFile.exists()) {
            println("配置文件已存在: ${configFile.absolutePath}")
            configFile.delete()
            println("配置文件已删除")
        }

        try {
            println("配置文件准备创建...")
            configFile.createNewFile()
            initializeConfigFile(configFile)
            println("配置文件创建并初始化完成: ${configFile.absolutePath}")
        } catch (IOException e) {
            println("创建配置文件失败: ${e.message}")
        }
    }

    void initializeConfigFile(File configFile) {
        configFile << "## 以下字段是自动生成的，删除或者为空都不影响应用的真实值，但是如果一旦赋值，将会以下面字段的值为准\n"
        configFile << "## config.properties不仅仅只要有这些字段，还有以平台开头的值，比如AndroidApplicationId、IosApplicationId、WebApplicationId、WindowsApplicationId、MacOsApplicationId、IosName、WebVersionCode、WindowsVersionName、等等\n"
        configFile << "## 如果将VersionCode(仅举例) 赋值：VersionCode = 101 ，而AndroidVersionCode = 105，那么其他平台(IOS、Web、Windows、MacOs、Linux)的VersionCode都是101，而Android的VersionCode则是105\n\n"
        configFile << "ApplicationId=\n"
        configFile << "Name=\n"
        configFile << "VersionCode=\n"
        configFile << "VersionName=\n\n"
    }

    void applyConfigLogic(File configFile) {
        Properties properties = new Properties()
        configFile.withInputStream { stream ->
            properties.load(stream)
        }

        String versionCode = properties.getProperty("VersionCode", "")
        String versionName = properties.getProperty("VersionName", "")
        String applicationId = properties.getProperty("ApplicationId", "")
        String name = properties.getProperty("Name", "")

        String androidVersionCode = properties.getProperty("AndroidVersionCode", versionCode)
        String iosVersionCode = properties.getProperty("IosVersionCode", versionCode)
        String webVersionCode = properties.getProperty("WebVersionCode", versionCode)
        String windowsVersionCode = properties.getProperty("WindowsVersionCode", versionCode)
        String macOsVersionCode = properties.getProperty("MacOsVersionCode", versionCode)

        // 打印平台字段
        println "Android VersionCode: ${androidVersionCode}"
        println "iOS VersionCode: ${iosVersionCode}"
        println "Web VersionCode: ${webVersionCode}"
        println "Windows VersionCode: ${windowsVersionCode}"
        println "MacOs VersionCode: ${macOsVersionCode}"
    }
}
