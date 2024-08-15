# PluginFlutterConfigAssetsManager

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
```

在 `setting.gradle` 文件中添加以下配置：

```
rootProject.name = 'xxx'
include 'lib'
```






