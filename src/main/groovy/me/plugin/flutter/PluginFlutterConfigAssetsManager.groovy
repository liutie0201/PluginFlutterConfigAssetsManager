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
        // 创建配置文件
        createConfigFile(project)
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

    void createConfigFile(Project project) {
        //先获取config.properties
        File currentDirPath = project.rootDir.parentFile
        File configFile = new File(currentDirPath, "config.properties")
        if (!configFile.exists()) {
            try {
                configFile.createNewFile()
                initializeConfigFile(configFile)
            } catch (IOException e) {
                println("创建配置文件失败: ${e.message}")
            }
        } else {
            //读取config.properties里面的内容
            Properties properties = new Properties()
            configFile.withInputStream { stream ->
                properties.load(stream)
            }

            // 检查是否有属性真正有值（非空且非空字符串）
            boolean hasValue = properties.any { key, value -> value?.trim() }

            if (!hasValue) {
                println("config.properties 中没有任何已赋值的属性，未生成 generate 文件夹。")
                return
            }
            File appBuildConfigFile = new File(currentDirPath, 'lib/generate/app_build_config.dart')
            if (!appBuildConfigFile.exists()) {
                //创建generate文件夹
                appBuildConfigFile.getParentFile().mkdirs()
                //创建app_build_config.dart
                appBuildConfigFile.createNewFile()
            }
            //读取config.properties中已经赋值的属性


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
}
