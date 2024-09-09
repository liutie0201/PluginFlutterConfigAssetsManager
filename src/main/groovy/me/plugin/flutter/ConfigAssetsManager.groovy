package me.plugin.flutter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import org.gradle.api.Project

class ConfigAssetsManager {

    private final Project project

    ConfigAssetsManager(Project project) {
        this.project = project
    }

    void writeAppImageConfig(File currentDirPath, File appImageConfigFile, Properties properties) {
        String loadAssetsName = properties.getProperty("loadAssetsName")?.trim()
        File assetsDir = new File(currentDirPath, loadAssetsName)
        if (assetsDir.exists() && assetsDir.isDirectory()) {
            appImageConfigFile.withWriter("UTF-8") { writer ->
                writer << "// 自动生成的资源配置文件\n"
                writer << "class AppImageConfig {\n"

                processAssetDirectory(assetsDir, writer, currentDirPath)

                writer << "}\n"
            }
            // 更新 pubspec.yaml 文件
//            updatePubspecYaml(currentDirPath, assetsDir)
        } else {
            println("指定的 assets 目录不存在或不是有效的目录: ${assetsDir.absolutePath}")
        }
    }

    void processAssetDirectory(File dir, Writer writer, File rootDir) {
        // 使用 Map 来存储不带分辨率前缀的路径，避免重复加载
        def assetMap = [:]
        dir.eachFileRecurse { file ->
            if (file.isFile()) {
                String relativePath = file.path.replace(rootDir.path, "").replace('\\', '/')
                String cleanPath = relativePath.replaceAll("/\\d+\\.\\dx", "") // 去掉 2.0x, 3.0x 等前缀
                cleanPath = cleanPath.substring(1, cleanPath.length())
                // 如果当前文件的cleanPath已经在Map中，说明已经加载过，跳过此文件
                if (!assetMap.containsKey(cleanPath)) {
                    assetMap[cleanPath] = relativePath
                    String fileType = getFileType(file.name)
                    if (fileType != null) {
                        writer << "  static const String ${fileType}_${file.name.split('\\.')[0]} = '${cleanPath}';\n"
                    }
                }
            }
        }
    }

    String getFileType(String fileName) {
        if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".ico") || fileName.endsWith(".webp") || fileName.endsWith(".heif")) {
            return "image"
        } else if (fileName.endsWith(".svg")) {
            return "svg"
        }
        return null
    }

    void updatePubspecYaml(File currentDirPath, File assetsDir) {
        File pubspecFile = new File(currentDirPath, "pubspec.yaml")
        if (!pubspecFile.exists()) {
            println("pubspec.yaml 文件不存在")
            return
        }

        // 创建自定义的 YAMLFactory，禁用强制加引号
        YAMLFactory factory = new YAMLFactory()
        factory.configure(YAMLGenerator.Feature.MINIMIZE_QUOTES, true)  // 禁用不必要的引号
        factory.configure(YAMLGenerator.Feature.WRITE_DOC_START_MARKER, false) // 禁用文档开头的 "---"
        ObjectMapper yamlMapper = new ObjectMapper(factory)

        // 使用 Jackson YAML 读取 pubspec.yaml 内容
        Map<String, Object> yamlContent = yamlMapper.readValue(pubspecFile, Map)

        // 遍历 key-value 对，根据键名调整引号
        yamlContent.each { key, value ->
            if (key == "description") {
                yamlContent[key] = value.toString()
            } else if (key == "publish_to") {
                yamlContent[key] = value.toString()
            }
        }

        // 动态查找 assets/image, assets/img, assets/images 中的路径
        List<String> newAssetPaths = []
        assetsDir.eachFileRecurse { file ->
            if (file.isFile()) {
                // 获取文件的父路径，并将它转换成相对路径
                String relativePath = file.parent.replace(currentDirPath.path, "").replace('\\', '/')
                relativePath = relativePath.startsWith("/") ? relativePath.substring(1) : relativePath

                // 只添加以 assets/image、assets/img 和 assets/images 开头的路径
                if (relativePath.startsWith("${assetsDir.name}/images") || relativePath.startsWith("${assetsDir.name}/image") || relativePath.startsWith("${assetsDir.name}/img")) {
                    if (!newAssetPaths.contains(relativePath + "/")) {
                        newAssetPaths.add(relativePath + "/")
                    }
                }
            }
        }

        // 确保只更新 flutter 节点下的 assets 部分，其他内容不动
        def flutterSection = yamlContent.getOrDefault("flutter", [:])
        flutterSection["assets"] = newAssetPaths
        yamlContent["flutter"] = flutterSection

        // 使用 Jackson YAML 写回 pubspec.yaml
        pubspecFile.withWriter("UTF-8") { writer ->
            yamlMapper.writeValue(writer, yamlContent)
        }

        println("已覆盖更新 pubspec.yaml 文件中的 flutter: assets 部分")
    }
}
