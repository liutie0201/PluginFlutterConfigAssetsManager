package me.plugin.flutter

import org.gradle.api.Project

class ConfigBuildManager {

    private final Project project

    ConfigBuildManager(Project project) {
        this.project = project
    }

    void writeAppBuildConfig(File appBuildConfigFile, Properties properties) {
        appBuildConfigFile.withWriter("UTF-8") { writer ->
            writer << "// 自动生成的配置文件\n"
            writer << "class AppBuildConfig {\n"

            properties.each { key, value ->
                if (value?.trim()) { // 仅写入有实际值的属性
                    if (!key.equals("loadAssetsName")) {
                        writer << "  static const ${value.isInteger() ? "int" : "String"} ${key} = ${value.isInteger() ? value : "\"${value}\""};\n"
                    }
                }
            }

            writer << "}\n"
        }
        def platformConfigManager = new PlatformConfigManager(project)
        platformConfigManager.applyConfigToPlatforms(properties)
    }
}