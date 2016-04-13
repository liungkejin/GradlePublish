# PublishGradle

本文介绍了使用 Android Studio & Gradle 来发布 Java/Kotlin Android 库到 MavenCentral/JCenter,
对于 Java 库或者其他 IDE 没有实际


## Java Library

### 第一步
复制`java-bintray.gradle`到你的库目录下, 并在你的库目录(/proj/lib/)下的 build.gradle 里面添加:

```groovy
buildscript {
    repositories {
        mavenCentral()
        jcenter()
    }

    dependencies {
        classpath 'com.jfrog.bintray.gradle:gradle-bintray-plugin:1.6'
        classpath "org.jfrog.buildinfo:build-info-extractor-gradle:3.2.0"
    }
}

repositories {
    mavenCentral()
    jcenter()
}

apply from: 'bintray.gradle'
```

### 第二步
在你的工程目录(/proj/)下的 `gradle.properties` 里面添加, 将值修改为自己库的信息

```
# 库的报名
PROJ_GROUP=cn.kejin.android.views
# 库的ID
PROJ_ARTIFACTID=XImageView
# 库的版本
PROJ_VERSION=1.0.2
### 最后 gradle引用的形式就是 $PROJ_GROUP:$PROJ_ARTIFACTID:$PROJ_VERSION

# 库名
PROJ_NAME=XImageView
# 库的项目主页
PROJ_WEBSITEURL=https://github.com/liungkejin/XImageView
# 问题跟踪地址
PROJ_ISSUETRACKERURL=https://github.com/liungkejin/XImageView/issues
# VCS 地址
PROJ_VCSURL=https://github.com/liungkejin/XImageView.git
# 库的简单描述
PROJ_DESCRIPTION=Android View for display large image

# 开发者的信息, 可以随意
DEVELOPER_ID=Kejin
DEVELOPER_NAME=Liang Ke Jin
DEVELOPER_EMAIL=liungkejin@gmail.com
```

在你的
### 第三步
在你的库目录(/proj/lib/)下添加
