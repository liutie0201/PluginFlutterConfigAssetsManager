package me.plugin.flutter

import groovy.xml.Namespace
import org.gradle.api.Project

class PlatformConfigManager {
    private final Project project

    PlatformConfigManager(Project project) {
        this.project = project
    }

    void applyConfigToPlatforms(Properties properties) {
        File currentDirPath = project.rootDir.parentFile
        File androidDir = new File(currentDirPath, 'android')
        File iosDir = new File(currentDirPath, 'ios')
        File webDir = new File(currentDirPath, 'web')
        File windowsDir = new File(currentDirPath, 'windows')
        File macosDir = new File(currentDirPath, 'macos')
        File linuxDir = new File(currentDirPath, 'linux')

        // 全平台属性
        String applicationId = properties.getProperty("applicationId")?.trim()
        String applicationName = properties.getProperty("applicationName")?.trim()
        String applicationVersionCode = properties.getProperty("applicationVersionCode")?.trim()
        String applicationVersionName = properties.getProperty("applicationVersionName")?.trim()

        if (androidDir.exists()) {
            applyConfigToAndroid(currentDirPath, properties, applicationId, applicationName, applicationVersionCode, applicationVersionName)
        }
        if (iosDir.exists()) {
            applyConfigToIOS(currentDirPath, properties, applicationId, applicationName, applicationVersionCode, applicationVersionName)
        }
        if (webDir.exists()) {
            applyConfigToWeb(currentDirPath, properties, applicationId, applicationName, applicationVersionCode, applicationVersionName)
        }
        if (windowsDir.exists()) {
            applyConfigToWindows(currentDirPath, properties, applicationId, applicationName, applicationVersionCode, applicationVersionName)
        }
        if (macosDir.exists()) {
            applyConfigToMacOs(currentDirPath, properties, applicationId, applicationName, applicationVersionCode, applicationVersionName)
        }
        if (linuxDir.exists()) {
            applyConfigToLinux(currentDirPath, properties, applicationId, applicationName, applicationVersionCode, applicationVersionName)
        }
    }

    void applyConfigToAndroid(File currentDirPath, Properties properties, String applicationId, String applicationName, String applicationVersionCode, String applicationVersionName) {
        String applicationIdAndroid = properties.getProperty("applicationIdAndroid")?.trim() ?: applicationId
        String applicationNameAndroid = properties.getProperty("applicationNameAndroid")?.trim() ?: applicationName
        String applicationVersionCodeAndroid = properties.getProperty("applicationVersionCodeAndroid")?.trim() ?: applicationVersionCode
        String applicationVersionNameAndroid = properties.getProperty("applicationVersionNameAndroid")?.trim() ?: applicationVersionName

        if (!applicationIdAndroid || !applicationId) {
            File buildGradleFile = new File(currentDirPath, 'android/app/build.gradle')
            if (buildGradleFile.exists()) {


                if (!applicationIdAndroid) {

                } else {

                }
            }
        }
        if (applicationNameAndroid || applicationName) {
            File manifestFile = new File(currentDirPath, "android/app/src/main/AndroidManifest.xml")
            def androidNamespace = new Namespace("http://schemas.android.com/apk/res/android", "android")

            if (manifestFile.exists()) {
                def manifest = new XmlSlurper().parse(manifestFile)
                def applicationNode = manifest.application[0]
                if (applicationNode != null) {
                    def labelAttribute = applicationNode.@(androidNamespace.label)
                    if (labelAttribute != null) {
                        if (!applicationNameAndroid) {
                            applicationNode.@(androidNamespace.label) = applicationNameAndroid
                        } else {
                            applicationNode.@(androidNamespace.label) = applicationName
                        }
                        manifestFile.withWriter('UTF-8') { writer ->
                            XmlUtil.serialize(manifest, writer)
                        }
                    } else {
                        println "android:label attribute not found!"
                    }
                } else {
                    println "Application node not found!"
                }
            }
        }
        // 这里可以编写应用到Android平台的逻辑
    }

    void applyConfigToIOS(File currentDirPath, Properties properties, String applicationId, String applicationName, String applicationVersionCode, String applicationVersionName) {
        String applicationIdIOS = properties.getProperty("applicationIdIOS")?.trim() ?: applicationId
        String applicationNameIOS = properties.getProperty("applicationNameIOS")?.trim() ?: applicationName
        String applicationVersionCodeIOS = properties.getProperty("applicationVersionCodeIOS")?.trim() ?: applicationVersionCode
        String applicationVersionNameIOS = properties.getProperty("applicationVersionNameIOS")?.trim() ?: applicationVersionName

        // 这里可以编写应用到iOS平台的逻辑
    }

    void applyConfigToWeb(File currentDirPath, Properties properties, String applicationId, String applicationName, String applicationVersionCode, String applicationVersionName) {
        String applicationIdWeb = properties.getProperty("applicationIdWeb")?.trim() ?: applicationId
        String applicationNameWeb = properties.getProperty("applicationNameWeb")?.trim() ?: applicationName
        String applicationVersionCodeWeb = properties.getProperty("applicationVersionCodeWeb")?.trim() ?: applicationVersionCode
        String applicationVersionNameWeb = properties.getProperty("applicationVersionNameWeb")?.trim() ?: applicationVersionName

        // 这里可以编写应用到Web平台的逻辑
    }

    void applyConfigToWindows(File currentDirPath, Properties properties, String applicationId, String applicationName, String applicationVersionCode, String applicationVersionName) {
        String applicationIdWindows = properties.getProperty("applicationIdWindows")?.trim() ?: applicationId
        String applicationNameWindows = properties.getProperty("applicationNameWindows")?.trim() ?: applicationName
        String applicationVersionCodeWindows = properties.getProperty("applicationVersionCodeWindows")?.trim() ?: applicationVersionCode
        String applicationVersionNameWindows = properties.getProperty("applicationVersionNameWindows")?.trim() ?: applicationVersionName

        // 这里可以编写应用到Windows平台的逻辑
    }

    void applyConfigToMacOs(File currentDirPath, Properties properties, String applicationId, String applicationName, String applicationVersionCode, String applicationVersionName) {
        String applicationIdMacOs = properties.getProperty("applicationIdMacOs")?.trim() ?: applicationId
        String applicationNameMacOs = properties.getProperty("applicationNameMacOs")?.trim() ?: applicationName
        String applicationVersionCodeMacOs = properties.getProperty("applicationVersionCodeMacOs")?.trim() ?: applicationVersionCode
        String applicationVersionNameMacOs = properties.getProperty("applicationVersionNameMacOs")?.trim() ?: applicationVersionName

        // 这里可以编写应用到MacOS平台的逻辑
    }

    void applyConfigToLinux(File currentDirPath, Properties properties, String applicationId, String applicationName, String applicationVersionCode, String applicationVersionName) {
        String applicationIdLinux = properties.getProperty("applicationIdLinux")?.trim() ?: applicationId
        String applicationNameLinux = properties.getProperty("applicationNameLinux")?.trim() ?: applicationName
        String applicationVersionCodeLinux = properties.getProperty("applicationVersionCodeLinux")?.trim() ?: applicationVersionCode
        String applicationVersionNameLinux = properties.getProperty("applicationVersionNameLinux")?.trim() ?: applicationVersionName

        // 这里可以编写应用到Linux平台的逻辑
    }
}
