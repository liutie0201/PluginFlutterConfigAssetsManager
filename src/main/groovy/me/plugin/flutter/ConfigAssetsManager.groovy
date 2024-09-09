package me.plugin.flutter

import org.gradle.api.Project
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml

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
            updatePubspecYaml(currentDirPath, assetsDir)
        } else {
            println("指定的 assets 目录不存在或不是有效的目录: ${assetsDir.absolutePath}")
        }
    }

    void processAssetDirectory(File dir, Writer writer, File rootDir) {
        // 使用 Map 来存储不带分辨率前缀的路径，避免重复加载
        def assetMap = new LinkedHashMap<String, String>()
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
                        writer << "  static const String ${fileType}_${file.name.split("\\.").first()} = '$cleanPath';\n"
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

        Yaml yaml = new Yaml()
        Map<String, Object> yamlContent = yaml.load(pubspecFile.text)

        // 动态查找 assets/image, assets/img, assets/images 中的路径
        List<String> newAssetPaths = []
        assetsDir.eachFileRecurse { file ->
            if (file.isFile()) {
                println(assetsDir.name)
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
        if (yamlContent.containsKey("flutter")) {
            Map<String, Object> flutterSection = yamlContent["flutter"] as Map<String, Object>
            flutterSection["assets"] = newAssetPaths
        } else {
            yamlContent["flutter"] = ["assets": newAssetPaths]
        }

        // 将 flutter.assets 内容合并回原始的 pubspec.yaml 内容
        String originalContent = pubspecFile.text
        Map<String, Object> originalYaml = yaml.load(originalContent)

        // 只覆盖 flutter.assets 部分，不动其他字段
        if (originalYaml.containsKey("flutter")) {
            originalYaml["flutter"].put("assets", yamlContent["flutter"]["assets"])
        } else {
            originalYaml["flutter"] = ["assets": newAssetPaths]
        }

        // 使用 SnakeYAML 输出 YAML
        DumperOptions options = new DumperOptions()
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
        Yaml updatedYaml = new Yaml(options)

        // 写回 pubspec.yaml
        pubspecFile.withWriter("UTF-8") { writer ->
            updatedYaml.dump(originalYaml, writer)
        }

        println("已覆盖更新 pubspec.yaml 文件中的 flutter: assets 部分")
    }
}
