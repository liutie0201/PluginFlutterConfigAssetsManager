package me.plugin.flutter

import groovy.xml.Namespace
import org.gradle.api.Project
import org.yaml.snakeyaml.Yaml

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
        File pubspecYamlFile = new File(currentDirPath, "pubspec.yaml")
        def pubspecYamlData
        if (pubspecYamlFile.exists()) {
            pubspecYamlData = new Yaml().load(pubspecYamlFile.text)
        }

        String appId = properties.getProperty("applicationIdAndroid")?.trim() ?: applicationId
        String appName = properties.getProperty("applicationNameAndroid")?.trim() ?: applicationName ?: pubspecYamlData?.name
        String appVersionCode = properties.getProperty("applicationVersionCodeAndroid")?.trim() ?: applicationVersionCode ?: "flutter.versionCode"
        String appVersionName = properties.getProperty("applicationVersionNameAndroid")?.trim() ?: applicationVersionName ?: "flutter.versionName"

        try {
            File manifestFile = new File(currentDirPath, "android/app/src/main/AndroidManifest.xml")
            if (manifestFile.exists()) {
                def androidNamespace = new Namespace("http://schemas.android.com/apk/res/android", "android")
                def manifest = new XmlSlurper().parse(manifestFile)
                def applicationNode = manifest.application[0]
                if (applicationNode != null) {
                    applicationNode.@(androidNamespace.label) = appName
                    manifestFile.withWriter('UTF-8') { writer ->
                        XmlUtil.serialize(manifest, writer)
                    }
                } else {
                    println "Application node not found!"
                }
            } else {
                println "AndroidManifest.xml file not found!"
            }
        } catch (Exception e) {
            println "Error updating AndroidManifest.xml: ${e.message}"
        }

        try {
            // 更新 build.gradle 中的 applicationId、versionCode、versionName
            File buildGradleFile = new File(currentDirPath, 'android/app/build.gradle')
            if (buildGradleFile.exists()) {
                def lines = buildGradleFile.readLines()
                buildGradleFile.withWriter('UTF-8') { writer ->
                    lines.each { line ->
                        if (line.contains("applicationId")) {
                            writer.writeLine("        applicationId '${appId}'")
                        } else if (line.contains("versionCode")) {
                            writer.writeLine("        versionCode = ${appVersionCode}")
                        } else if (line.contains("versionName")) {
                            if (appVersionName != "flutter.versionName") {
                                appVersionName = "'$appVersionName'"
                            }
                            writer.writeLine("        versionName = ${appVersionName}")
                        } else {
                            writer.writeLine(line)
                        }
                    }
                }
            } else {
                println("build.gradle file not found!")
            }
        } catch (Exception e) {
            println "Error updating build.gradle: ${e.message}"
        }
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
