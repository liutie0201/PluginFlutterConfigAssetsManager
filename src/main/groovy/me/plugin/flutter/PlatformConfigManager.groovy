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
        File pubspecYamlFile = new File(currentDirPath, "pubspec.yaml")
        def pubspecYamlData
        if (pubspecYamlFile.exists()) {
            pubspecYamlData = new Yaml().load(pubspecYamlFile.text)
        }
        // 全平台属性
        String applicationId = properties.getProperty("applicationId")?.trim()
        String applicationName = properties.getProperty("applicationName")?.trim()
        String applicationVersionCode = properties.getProperty("applicationVersionCode")?.trim()
        String applicationVersionName = properties.getProperty("applicationVersionName")?.trim()

        if (androidDir.exists()) {
            applyConfigToAndroid(currentDirPath, properties, applicationId, applicationName, applicationVersionCode, applicationVersionName, pubspecYamlData)
        }
        if (iosDir.exists()) {
            applyConfigToIOS(currentDirPath, properties, applicationId, applicationName, applicationVersionCode, applicationVersionName, pubspecYamlData)
        }
        if (webDir.exists()) {
            applyConfigToWeb(currentDirPath, properties, applicationId, applicationName, applicationVersionCode, applicationVersionName, pubspecYamlData)
        }
        if (windowsDir.exists()) {
            applyConfigToWindows(currentDirPath, properties, applicationId, applicationName, applicationVersionCode, applicationVersionName, pubspecYamlData)
        }
        if (macosDir.exists()) {
            applyConfigToMacOs(currentDirPath, properties, applicationId, applicationName, applicationVersionCode, applicationVersionName, pubspecYamlData)
        }
        if (linuxDir.exists()) {
            applyConfigToLinux(currentDirPath, properties, applicationId, applicationName, applicationVersionCode, applicationVersionName, pubspecYamlData)
        }
    }

    void applyConfigToAndroid(File currentDirPath, Properties properties, String applicationId, String applicationName, String applicationVersionCode, String applicationVersionName, def pubspecYamlData) {
        String appId = properties.getProperty("applicationIdAndroid")?.trim() ?: applicationId
        String appName = properties.getProperty("applicationNameAndroid")?.trim() ?: applicationName ?: pubspecYamlData?.name
        String appVersionCode = properties.getProperty("applicationVersionCodeAndroid")?.trim() ?: applicationVersionCode ?: "flutter.versionCode"
        String appVersionName = properties.getProperty("applicationVersionNameAndroid")?.trim() ?: applicationVersionName ?: "flutter.versionName"

        File manifestFile = new File(currentDirPath, "android/app/src/main/AndroidManifest.xml")
        if (manifestFile.exists()) {
            def androidNamespace = new Namespace("http://schemas.android.com/apk/res/android", "android")
            def manifest = new XmlSlurper().parse(manifestFile)
            def applicationNode = manifest.application[0]
            if (applicationNode != null) {
                applicationNode.@(androidNamespace.label) = appName
                manifestFile.withWriter('UTF-8') { writer -> XmlUtil.serialize(manifest, writer)
                }
            } else {
                println "Application node not found!"
            }
        } else {
            println "AndroidManifest.xml file not found!"
        }

        // 更新 build.gradle 中的 applicationId、versionCode、versionName
        File buildGradleFile = new File(currentDirPath, 'android/app/build.gradle')
        if (buildGradleFile.exists()) {
            def lines = buildGradleFile.readLines()
            buildGradleFile.withWriter('UTF-8') { writer ->
                lines.each { line ->
                    if (line.contains("applicationId")) {
                        if (appId) {
                            writer.writeLine("        applicationId '${appId}'")
                        }
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

    }

    void applyConfigToIOS(File currentDirPath, Properties properties, String applicationId, String applicationName, String applicationVersionCode, String applicationVersionName, def pubspecYamlData) {
        String appId = properties.getProperty("applicationIdIOS")?.trim() ?: applicationId ?: "\$(PRODUCT_BUNDLE_IDENTIFIER)"
        String appName = properties.getProperty("applicationNameIOS")?.trim() ?: applicationName ?: pubspecYamlData?.name
        String appVersionCode = properties.getProperty("applicationVersionCodeIOS")?.trim() ?: applicationVersionCode ?: "\$(FLUTTER_BUILD_NUMBER)"
        String appVersionName = properties.getProperty("applicationVersionNameIOS")?.trim() ?: applicationVersionName ?: "\$(FLUTTER_BUILD_NAME)"

        File plistFile = new File(currentDirPath, "ios/Runner/Info.plist")
        if (plistFile.exists()) {
            def xmlSlurper = new XmlSlurper()
            xmlSlurper.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
            xmlSlurper.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            def plistContent = xmlSlurper.parse(plistFile)
            plistContent.dict[0].key.eachWithIndex { key, idx ->
                if (key.text() == "CFBundleIdentifier" && appId) {
                    plistContent.dict[0].string[idx] = appId
                }
                if (key.text() == "CFBundleName" && appName) {
                    plistContent.dict[0].string[idx] = appName
                }
                if (key.text() == "CFBundleVersion" && appVersionCode) {
                    plistContent.dict[0].string[idx] = appVersionCode
                }
                if (key.text() == "CFBundleShortVersionString" && appVersionName) {
                    plistContent.dict[0].string[idx] = appVersionName
                }
            }
            plistFile.withWriter('UTF-8') { writer -> XmlUtil.serialize(plistContent, writer)
            }

            def xmlContent = plistFile.text
            String insertText = '\n<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">\n'
            // 在第一行<?xml version="1.0" encoding="UTF-8"?>后插入DOCTYPE声明
            def modifiedXmlContent = xmlContent.replaceFirst(/(?<=<\?xml version="1.0" encoding="UTF-8"\?>)/, insertText)
            plistFile.text = modifiedXmlContent
        } else {
            println "Info.plist file not found!"
        }

    }

    void applyConfigToWeb(File currentDirPath, Properties properties, String applicationId, String applicationName, String applicationVersionCode, String applicationVersionName, def pubspecYamlData) {
        String appId = properties.getProperty("applicationIdWeb")?.trim() ?: applicationId
        String appName = properties.getProperty("applicationNameWeb")?.trim() ?: applicationName ?: pubspecYamlData?.name
        String appVersionCode = properties.getProperty("applicationVersionCodeWeb")?.trim() ?: applicationVersionCode
        String appVersionName = properties.getProperty("applicationVersionNameWeb")?.trim() ?: applicationVersionName

        // 修改 index.html 文件的标题
        File indexHtmlFile = new File(currentDirPath, "web/index.html")
        if (indexHtmlFile.exists()) {
            def lines = indexHtmlFile.readLines()
            indexHtmlFile.withWriter('UTF-8') { writer ->
                lines.each { line ->
                    if (line.contains("<title>")) {
                        writer.writeLine("<title>${appName}</title>")
                    } else {
                        writer.writeLine(line)
                    }
                }
            }
        } else {
            println("index.html file not found!")
        }
    }

    void applyConfigToWindows(File currentDirPath, Properties properties, String applicationId, String applicationName, String applicationVersionCode, String applicationVersionName, def pubspecYamlData) {
        String pubspecYamlVersion = pubspecYamlData?.version ?: "1.0.0"
        pubspecYamlVersion = pubspecYamlVersion.split("\\+").first()

        String appId = properties.getProperty("applicationIdWindows")?.trim() ?: applicationId
        String appName = properties.getProperty("applicationNameWindows")?.trim() ?: applicationName ?: pubspecYamlData?.name
        String appVersionCode = properties.getProperty("applicationVersionCodeWindows")?.trim() ?: applicationVersionCode
        String appVersionName = properties.getProperty("applicationVersionNameWindows")?.trim() ?: applicationVersionName ?: pubspecYamlVersion

        File cmakeListsFile = new File(currentDirPath, "windows/CMakeLists.txt")
        if (cmakeListsFile.exists()) {
            def lines = cmakeListsFile.readLines()
            cmakeListsFile.withWriter('UTF-8') { writer ->
                lines.each { line ->
                    if (line.contains("set(BINARY_NAME")) {
                        writer.writeLine("set(BINARY_NAME \"${appName}\")")
                    } else if (line.contains("project(")) {
                        writer.writeLine("project(${appName} VERSION ${appVersionName} LANGUAGES CXX)")
                    } else if (line.contains("set(PROJECT_VERSION")) {
                        writer.writeLine("set(PROJECT_VERSION \"${appVersionName}\")")
                    } else {
                        writer.writeLine(line)
                    }
                }
            }
        } else {
            println("CMakeLists.txt file not found!")
        }
    }

    void applyConfigToMacOs(File currentDirPath, Properties properties, String applicationId, String applicationName, String applicationVersionCode, String applicationVersionName, def pubspecYamlData) {
        String appId = properties.getProperty("applicationIdMacOs")?.trim() ?: applicationId ?: "\$(PRODUCT_BUNDLE_IDENTIFIER)"
        String appName = properties.getProperty("applicationNameMacOs")?.trim() ?: applicationName ?: pubspecYamlData?.name
        String appVersionCode = properties.getProperty("applicationVersionCodeMacOs")?.trim() ?: applicationVersionCode ?: "\$(FLUTTER_BUILD_NUMBER)"
        String appVersionName = properties.getProperty("applicationVersionNameMacOs")?.trim() ?: applicationVersionName ?: "\$(FLUTTER_BUILD_NAME)"

        File plistFile = new File(currentDirPath, "macos/Runner/Info.plist")
        if (plistFile.exists()) {
            def xmlSlurper = new XmlSlurper()
            xmlSlurper.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
            xmlSlurper.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
            def plistContent = xmlSlurper.parse(plistFile)
            plistContent.dict[0].key.eachWithIndex { key, idx ->
                if (key.text() == "CFBundleIdentifier" && appId) {
                    plistContent.dict[0].string[idx] = appId
                }
                if (key.text() == "CFBundleName" && appName) {
                    plistContent.dict[0].string[idx] = appName
                }
                if (key.text() == "CFBundleVersion" && appVersionCode) {
                    plistContent.dict[0].string[idx] = appVersionCode
                }
                if (key.text() == "CFBundleShortVersionString" && appVersionName) {
                    plistContent.dict[0].string[idx] = appVersionName
                }
            }
            plistFile.withWriter('UTF-8') { writer -> XmlUtil.serialize(plistContent, writer) }
            // 插入 DOCTYPE 声明
            def xmlContent = plistFile.text
            String insertText = '\n<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">\n'
            def modifiedXmlContent = xmlContent.replaceFirst(/(?<=<\?xml version="1.0" encoding="UTF-8"\?>)/, insertText)
            plistFile.text = modifiedXmlContent
        } else {
            println "Info.plist file not found!"
        }
    }

    void applyConfigToLinux(File currentDirPath, Properties properties, String applicationId, String applicationName, String applicationVersionCode, String applicationVersionName, def pubspecYamlData) {
        String pubspecYamlVersion = pubspecYamlData?.version ?: "1.0.0"
        pubspecYamlVersion = pubspecYamlVersion.split("\\+").first()

        String appId = properties.getProperty("applicationIdWindows")?.trim() ?: applicationId
        String appName = properties.getProperty("applicationNameWindows")?.trim() ?: applicationName ?: pubspecYamlData?.name
        String appVersionCode = properties.getProperty("applicationVersionCodeWindows")?.trim() ?: applicationVersionCode
        String appVersionName = properties.getProperty("applicationVersionNameWindows")?.trim() ?: applicationVersionName ?: pubspecYamlVersion

        File cmakeListsFile = new File(currentDirPath, "linux/CMakeLists.txt")
        if (cmakeListsFile.exists()) {
            def lines = cmakeListsFile.readLines()
            cmakeListsFile.withWriter('UTF-8') { writer ->
                lines.each { line ->
                    if (line.contains("set(BINARY_NAME")) {
                        writer.writeLine("set(BINARY_NAME \"${appName}\")")
                    } else if (line.contains("set(APPLICATION_ID")) {
                        if (appId) {
                            writer.writeLine("set(APPLICATION_ID \"${appId}\")")
                        } else {
                            writer.writeLine(line)
                        }
                    } else if (line.contains("project(")) {
                        // 替换project行，添加应用程序名称和版本信息
                        writer.writeLine("project(${appName} VERSION ${appVersionName} LANGUAGES CXX)")
                    } else {
                        writer.writeLine(line)
                    }
                }
            }
        } else {
            println("CMakeLists.txt file not found!")
        }
    }


}
