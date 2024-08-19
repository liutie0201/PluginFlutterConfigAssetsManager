package me.plugin.flutter

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.LinkedHashMap

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
        println("-------------文件任务开始------------")
        // 先获取 config.properties
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
            File appBuildConfigFile = new File(currentDirPath, 'lib/generate/app_build_config.dart')
            if (!appBuildConfigFile.exists()) {
                // 创建 generate 文件夹
                appBuildConfigFile.getParentFile().mkdirs()
                // 创建 app_build_config.dart
                appBuildConfigFile.createNewFile()
            }
            //读取config.properties里面的内容
            Properties properties = new Properties()

            configFile.withReader("UTF-8") { reader ->
                properties.load(reader)
            }

            // 写入 app_build_config.dart 文件
            writeAppBuildConfig(appBuildConfigFile, properties)
        }
    }

    void writeAppBuildConfig(File appBuildConfigFile, Properties properties) {
        // 检查是否有属性真正有值（非空且非空字符串）
        boolean hasValue = properties.any { key, value -> value?.trim() }

        if (!hasValue) {
            appBuildConfigFile.withWriter("UTF-8") { writer ->
                writer << "// 自动生成的配置文件\n"
                writer << "class AppBuildConfig {\n"
                writer << "}\n"
            }
            return
        }
        appBuildConfigFile.withWriter("UTF-8") { writer ->
            writer << "// 自动生成的配置文件\n"
            writer << "class AppBuildConfig {\n"

            properties.each { key, value ->
                if (value?.trim()) { // 仅写入有实际值的属性
                    def keys = key.substring(0, 1).toLowerCase() + key.substring(1, key.length())
                    writer << "  static const ${value.isInteger() ? "int" : "String"} ${key} = ${value.isInteger() ? value : "\"${value}\""};\n"
                }
            }

            writer << "}\n"
        }

    }


    void initializeConfigFile(File configFile) {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(configFile), "UTF-8"))

        // 添加全局注释
        writer.println("## 以下字段是自动生成的，删除或者为空都不影响应用的真实值，但是如果一旦赋值，将会以下面字段的值为准")
        writer.println("## config.properties不仅仅只要有这些字段，还有以平台开头的值，比如applicationIdAndroid、applicationIdIOS、applicationIdWeb、applicationIdWindows、applicationIdMacOs、applicationNameIOS、applicationVersionCodeWeb、applicationVersionNameWindows、等等")
        writer.println("## 如果将applicationVersionCode(仅举例) 赋值：applicationVersionCode = 101 ，而applicationVersionCodeAndroid = 105，那么其他平台(IOS、Web、Windows、MacOs、Linux)的applicationVersionCode都是101，而Android的applicationVersionCode则是105\n")

        // 全局属性部分
        writer.println("## 全局属性")
        writer.println("applicationId=")
        writer.println("applicationName=")
        writer.println("applicationVersionCode=")
        writer.println("applicationVersionName=")

        // Android平台属性
        writer.println("\n## Android")
        writer.println("#applicationIdAndroid=")
        writer.println("#applicationNameAndroid=")
        writer.println("#applicationVersionCodeAndroid=")
        writer.println("#applicationVersionNameAndroid=")

        // iOS平台属性
        writer.println("\n## IOS")
        writer.println("#applicationIdIOS=")
        writer.println("#applicationNameIOS=")
        writer.println("#applicationVersionCodeIOS=")
        writer.println("#applicationVersionNameIOS=")

        // Web平台属性
        writer.println("\n## Web")
        writer.println("#applicationIdWeb=")
        writer.println("#applicationNameWeb=")
        writer.println("#applicationVersionCodeWeb=")
        writer.println("#applicationVersionNameWeb=")

        // Windows平台属性
        writer.println("\n## Windows")
        writer.println("#applicationIdWindows=")
        writer.println("#applicationNameWindows=")
        writer.println("#applicationVersionCodeWindows=")
        writer.println("#applicationVersionNameWindows=")

        // MacOS平台属性
        writer.println("\n## MacOS")
        writer.println("#applicationIdMacOs=")
        writer.println("#applicationNameMacOs=")
        writer.println("#applicationVersionCodeMacOs=")
        writer.println("#applicationVersionNameMacOs=")

        // Linux平台属性
        writer.println("\n## Linux")
        writer.println("#applicationIdLinux=")
        writer.println("#applicationNameLinux=")
        writer.println("#applicationVersionCodeLinux=")
        writer.println("#applicationVersionNameLinux=")

        // 关闭 writer
        writer.close()
    }

}
