package me.plugin.flutter

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

                writer << "\n}\n"
            }
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

}