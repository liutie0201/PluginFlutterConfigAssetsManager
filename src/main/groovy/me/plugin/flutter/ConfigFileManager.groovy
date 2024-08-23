package me.plugin.flutter

import org.gradle.api.Project

class ConfigFileManager {

    private final Project project

    ConfigFileManager(Project project) {
        this.project = project
    }

    void createConfigFile() {
        File currentDirPath = project.rootDir.parentFile
        File configFile = new File(currentDirPath, "config.properties")
        File generateFileDir = new File(currentDirPath, 'lib/generate')

        if (!configFile.exists()) {
            try {
                configFile.createNewFile()
                initializeConfigFile(configFile)
            } catch (IOException e) {
                println("创建配置文件失败: ${e.message}")
            }
        }
        if (!generateFileDir.exists()) {
            generateFileDir.mkdirs()
        }
        File appBuildConfigFile = new File(currentDirPath, 'lib/generate/app_build_config.dart')
        File appImageConfigFile = new File(currentDirPath, 'lib/generate/app_image_config.dart')

        if (!appBuildConfigFile.exists()) {
            appBuildConfigFile.createNewFile()
        }
        if (!appImageConfigFile.exists()) {
            appImageConfigFile.createNewFile()
        }
        Properties properties = new Properties()
        configFile.withReader("UTF-8") { reader -> properties.load(reader) }

        def configBuildManager = new ConfigBuildManager(project)
        configBuildManager.writeAppBuildConfig(appBuildConfigFile, properties)

        def configAssetsManager = new ConfigAssetsManager(project)
        configAssetsManager.writeAppImageConfig(currentDirPath, appImageConfigFile, properties)

        def platformConfigManager = new PlatformConfigManager(project)
        platformConfigManager.applyConfigToPlatforms(properties)

    }


    void initializeConfigFile(File configFile) {
        configFile.withWriter("UTF-8") { writer ->
            // 添加全局注释
            writer.println("# If the comments are garbled, set the compiler to utf-8")
            writer.println("# 以下字段是自动生成的，删除或者为空都不影响应用的真实值，但是如果一旦赋值，将会以下面字段的值为准")
            writer.println("# config.properties不仅仅只要有这些字段，还有以平台结尾的值，比如applicationIdAndroid、applicationIdIOS、applicationIdWeb、applicationIdWindows、applicationIdMacOs、applicationNameIOS、applicationVersionCodeWeb、applicationVersionNameWindows、等等")
            writer.println("# 如果将applicationVersionCode(仅举例) 赋值：applicationVersionCode = 101 ，而applicationVersionCodeAndroid = 105，那么其他平台(IOS、Web、Windows、MacOs、)的applicationVersionCode都是101，而Android的applicationVersionCode则是105\n")

            // 全局属性
            writer.println("# 全局属性")
            writer.println("applicationId=")
            writer.println("applicationName=")
            writer.println("applicationVersionCode=")
            writer.println("applicationVersionName=")
            writer.println("#loadAssetsName默认assets")
            writer.println("loadAssetsName=assets")

//            // Android平台属性
//            writer.println("\n## Android")
//            writer.println("#applicationIdAndroid=")
//            writer.println("#applicationNameAndroid=")
//            writer.println("#applicationVersionCodeAndroid=")
//            writer.println("#applicationVersionNameAndroid=")
//
//            // iOS平台属性
//            writer.println("\n## IOS")
//            writer.println("#applicationIdIOS=")
//            writer.println("#applicationNameIOS=")
//            writer.println("#applicationVersionCodeIOS=")
//            writer.println("#applicationVersionNameIOS=")
//
//            // Web平台属性
//            writer.println("\n## Web")
//            writer.println("#applicationIdWeb=")
//            writer.println("#applicationNameWeb=")
//            writer.println("#applicationVersionCodeWeb=")
//            writer.println("#applicationVersionNameWeb=")
//
//            // Windows平台属性
//            writer.println("\n## Windows")
//            writer.println("#applicationIdWindows=")
//            writer.println("#applicationNameWindows=")
//            writer.println("#applicationVersionCodeWindows=")
//            writer.println("#applicationVersionNameWindows=")
//
//            // MacOS平台属性
//            writer.println("\n## MacOS")
//            writer.println("#applicationIdMacOs=")
//            writer.println("#applicationNameMacOs=")
//            writer.println("#applicationVersionCodeMacOs=")
//            writer.println("#applicationVersionNameMacOs=")
//
//            // Linux平台属性
//            writer.println("\n## Linux")
//            writer.println("#applicationIdLinux=")
//            writer.println("#applicationNameLinux=")
//            writer.println("#applicationVersionCodeLinux=")
//            writer.println("#applicationVersionNameLinux=")

        }
    }
}
