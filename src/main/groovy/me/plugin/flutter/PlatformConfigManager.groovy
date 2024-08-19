package me.plugin.flutter

import org.gradle.api.Project

class PlatformConfigManager {
    private final Project project

    PlatformConfigManager(Project project) {
        this.project = project
    }

    void applyConfigToPlatforms(Properties properties) {
        print('开始设置平台属性值')
        //全平台属性
        String applicationId = properties.getProperty("applicationId")?.trim()
        String applicationName = properties.getProperty("applicationVersionCode")?.trim()
        String applicationVersionCode = properties.getProperty("applicationVersionCode")?.trim()
        String applicationVersionName = properties.getProperty("applicationVersionName")?.trim()

        //Android平台
        String applicationIdAndroid = properties.getProperty("applicationIdAndroid")?.trim()
        String applicationNameAndroid = properties.getProperty("applicationVersionCodeAndroid")?.trim()
        String applicationVersionCodeAndroid = properties.getProperty("applicationVersionCodeAndroid")?.trim()
        String applicationVersionNameAndroid = properties.getProperty("applicationVersionNameAndroid")?.trim()
        //IOS平台
        String applicationIdIOS = properties.getProperty("applicationIdIOS")?.trim()
        String applicationNameIOS = properties.getProperty("applicationVersionCodeIOS")?.trim()
        String applicationVersionCodeIOS = properties.getProperty("applicationVersionCodeIOS")?.trim()
        String applicationVersionNameIOS = properties.getProperty("applicationVersionNameIOS")?.trim()
        //Web平台
        String applicationIdWeb = properties.getProperty("applicationIdWeb")?.trim()
        String applicationNameWeb = properties.getProperty("applicationVersionCodeWeb")?.trim()
        String applicationVersionCodeWeb = properties.getProperty("applicationVersionCodeWeb")?.trim()
        String applicationVersionNameWeb = properties.getProperty("applicationVersionNameWeb")?.trim()
        //Windows平台
        String applicationIdWindows = properties.getProperty("applicationIdWindows")?.trim()
        String applicationNameWindows = properties.getProperty("applicationVersionCodeWindows")?.trim()
        String applicationVersionCodeWindows = properties.getProperty("applicationVersionCodeWindows")?.trim()
        String applicationVersionNameWindows = properties.getProperty("applicationVersionNameWindows")?.trim()
        //MacOs平台
        String applicationIdMacOs = properties.getProperty("applicationIdMacOs")?.trim()
        String applicationNameMacOs = properties.getProperty("applicationVersionCodeMacOs")?.trim()
        String applicationVersionCodeMacOs = properties.getProperty("applicationVersionCodeMacOs")?.trim()
        String applicationVersionNameMacOs = properties.getProperty("applicationVersionNameMacOs")?.trim()
        //Linux平台
        String applicationIdLinux = properties.getProperty("applicationIdLinux")?.trim()
        String applicationNameLinux = properties.getProperty("applicationVersionCodeLinux")?.trim()
        String applicationVersionCodeLinux = properties.getProperty("applicationVersionCodeLinux")?.trim()
        String applicationVersionNameLinux = properties.getProperty("applicationVersionNameLinux")?.trim()
    }
}