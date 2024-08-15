# PluginFlutterConfigAssetsManager

# 在项目的根目录创建一个`plugin_build_files`文件夹

# 在`plugin_build_files`文件夹中一个`build.gradle`文件

## 配置 `build.gradle`

在 `build.gradle` 文件中添加以下配置：

```
buildscript {
    repositories {
        maven {
            url uri('file:///E:/Maven/RepLocal/PluginFlutterConfigAssetsManager')
        }
    }
    dependencies {
        classpath 'me.plugin.flutter:PluginFlutterConfigAssetsManager:1.0.1'
    }
}

apply plugin: 'PluginFlutterConfigAssetsManager'

// 定义一个空任务，用于触发 PluginFlutterConfigAssetsManager 中的任务
task nullTask {
       // 空任务，无需额外配置
}
```






