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
        } else {
            Properties properties = new Properties()
            configFile.withReader("UTF-8") { reader -> properties.load(reader) }

            boolean hasValue = properties.any { key, value -> value?.trim() }
            if (hasValue) {
                if (!generateFileDir.exists()) {
                    generateFileDir.mkdirs()
                }
                // 判断是否需要创建或删除文件
                manageConfigFiles(properties)
            } else {
                if (generateFileDir.exists()) {
                    generateFileDir.deleteDir()
                }
            }
        }
    }

    void manageConfigFiles(Properties properties) {
        int propertiesSize = properties.findAll { key, value -> value?.trim() }.size()
        String loadAssetsName = properties.getProperty("loadAssetsName")?.trim()

        boolean shouldCreateBuildFile = propertiesSize != 1 || !loadAssetsName
        boolean shouldCreateImageFile = loadAssetsName

        File currentDirPath = project.rootDir.parentFile
        File appBuildConfigFile = new File(currentDirPath, 'lib/generate/app_build_config.dart')
        File appImageConfigFile = new File(currentDirPath, 'lib/generate/app_image_config.dart')

        if (shouldCreateBuildFile) {
            if (!appBuildConfigFile.exists()) {
                appBuildConfigFile.createNewFile()
            }
            //app_build_config 文件写入
            def configBuildManager = new ConfigBuildManager(project)
            configBuildManager.writeAppBuildConfig(appBuildConfigFile, properties)
        } else {
            if (appBuildConfigFile.exists()) {
                appBuildConfigFile.delete()
            }
        }
        if (shouldCreateImageFile) {
            if (!appImageConfigFile.exists()) {
                appImageConfigFile.createNewFile()
            }
            //app_image_config 文件写入
            def configAssetsManager = new ConfigAssetsManager(project)
            configAssetsManager.writeAppImageConfig(currentDirPath, appImageConfigFile, properties)
        } else {
            if (appImageConfigFile.exists()) {
                appImageConfigFile.delete()
            }
        }

    }

    void initializeConfigFile(File configFile) {
        configFile.withWriter("UTF-8") { writer ->
            // 添加全局注释
            writer.println("## 以下字段是自动生成的，删除或者为空都不影响应用的真实值，但是如果一旦赋值，将会以下面字段的值为准")
            writer.println("## config.properties不仅仅只要有这些字段，还有以平台开头的值，比如applicationIdAndroid、applicationIdIOS、applicationIdWeb、applicationIdWindows、applicationIdMacOs、applicationNameIOS、applicationVersionCodeWeb、applicationVersionNameWindows、等等")
            writer.println("## 如果将applicationVersionCode(仅举例) 赋值：applicationVersionCode = 101 ，而applicationVersionCodeAndroid = 105，那么其他平台(IOS、Web、Windows、MacOs、Linux)的applicationVersionCode都是101，而Android的applicationVersionCode则是105\n")

            // 全局属性
            writer.println("## 全局属性")
            writer.println("applicationId=")
            writer.println("applicationName=")
            writer.println("applicationVersionCode=")
            writer.println("applicationVersionName=")
            writer.println("#loadAssetsName=")

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

        }
    }
}
