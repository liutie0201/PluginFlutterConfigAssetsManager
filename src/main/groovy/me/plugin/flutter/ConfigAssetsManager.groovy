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
        String loadAssetsName = properties.getProperty("loadAssetsName")?.trim() ?: "assets"
        File assetsDir = new File(currentDirPath, loadAssetsName)
        if (assetsDir.exists() && assetsDir.isDirectory()) {
            File imagesDir = new File(assetsDir, "images")
            File imageDir = new File(assetsDir, "image")
            File imgDir = new File(assetsDir, "img")

            if (imagesDir.exists() && imagesDir.isDirectory() || imageDir.exists() && imageDir.isDirectory() || imgDir.exists() && imgDir.isDirectory()) {
                appImageConfigFile.withWriter("UTF-8") { writer ->
                    writer << "// 自动生成的资源配置文件\n"
                    writer << "class AppImageConfig {\n"

                    // 处理存在的文件夹（images/image/img）
                    if (imagesDir.exists()) {
                        processAssetDirectory(loadAssetsName, imagesDir, writer, currentDirPath)
                    } else if (imageDir.exists()) {
                        processAssetDirectory(loadAssetsName, imageDir, writer, currentDirPath)
                    } else if (imgDir.exists()) {
                        processAssetDirectory(loadAssetsName, imgDir, writer, currentDirPath)
                    } else {
                        println("---------其他文件夹---------")
                    }

                    writer << "}\n"
                }
                // 更新 pubspec.yaml 文件
//            updatePubspecYaml(currentDirPath, assetsDir)
            }
        } else {
            println("指定的 assets 目录不存在或不是有效的目录: ${assetsDir.absolutePath}")
        }
    }

    void processAssetDirectory(String loadAssetsName, File imagesDir, Writer writer, File rootDir) {
        String startPath = "${loadAssetsName}/${imagesDir.name}"

        // 定义分辨率前缀的文件夹
        File dir4x = new File(imagesDir, "4.0x")
        File dir3x = new File(imagesDir, "3.0x")
        File dir2x = new File(imagesDir, "2.0x")

        def imageExtensions = [".png", ".jpg", ".jpeg", ".ico", ".webp", ".heif", ".svg"]

        // 存储最终的图片路径，去除重复项
        def finalImagePaths = new LinkedHashMap<String, String>()

        // 1. 处理 images 目录下的文件（不带分辨率前缀的文件）
        imagesDir.eachFile { file ->
            if (file.isFile() && imageExtensions.any { file.name.toLowerCase().endsWith(it) }) {
                finalImagePaths[file.name] = "${startPath}/${file.name}"
            }
        }

        // 2. 处理分辨率文件夹中的文件，取最大分辨率
        def resolutionDirs = [dir4x, dir3x, dir2x].findAll { it.exists() && it.isDirectory() }

        // 按照分辨率顺序处理（优先 4.0x，然后是 3.0x，最后是 2.0x）
        resolutionDirs.each { dir ->
            dir.eachFile { file ->
                if (file.isFile() && imageExtensions.any { file.name.toLowerCase().endsWith(it) }) {
                    // 如果主目录没有同名图片，记录分辨率文件夹中的图片
                    if (!finalImagePaths.containsKey(file.name)) {
                        finalImagePaths[file.name] = "${startPath}/${dir.name}/${file.name}"
                    }
                }
            }
        }

        // 3. 处理其他子目录（如 home 和 mine）中的文件
        imagesDir.eachFile { file ->
            if (file.isDirectory() && !resolutionDirs.contains(file)) {  // 跳过分辨率文件夹
                file.eachFile { subFile ->
                    if (subFile.isFile() && imageExtensions.any { subFile.name.toLowerCase().endsWith(it) }) {
                        finalImagePaths[subFile.name] = "${startPath}/${file.name}/${subFile.name}"
                    }
                }
            }
        }
        // 输出生成的路径
        finalImagePaths.each { name, path ->
            writer << "  static const String ${getFileType(name)}_${name.split("\\.").first()} = '$path';\n"
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
        pubspecFile.withWriter("UTF-8") { writer -> updatedYaml.dump(originalYaml, writer)
        }

        println("已覆盖更新 pubspec.yaml 文件中的 flutter: assets 部分")
    }
}
