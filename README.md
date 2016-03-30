# Demo In Github

浏览Demo工程以理解如何使用NiuChart SDK快速配置一个复杂的多维数据故事。
使用git进行clone.

```git

git clone https://github.com/NiuChartTeam/NXChart-Demo.git

```
[Demo in Github](https://github.com/NiuChartTeam/NXChart-Demo)会在随版本更新会持续在github上更新。

>使用   

1)运行AndroidStudio 
2)点击Quick Start中的`Open an existing Android Studio project` 选择clone下来的目录
3)耐心等待...
4)运行Demo，在phone端或pad端观看效果。(支持Android3.0 API11及以上平台) 

###工程结构解析
需要对sdk输入的2种文件：
1. json配置文件 (.nx文件后缀)
2. sqlite数据库文件(nxdb文件后缀),mac推荐使用[sqlite-sqlcipher-browser](http://mirror.salasaga.org/sqlitebrowser/onceoffs/sqlitebrowser-sqlcipher_201506172157.dmg)打开。

Demo演示工程将这这些文件放在assets文件中，当app第一次运行后将这些文件copy到内部存储器上。Production情况下请自行决定动态地从网络获取或静态得放在res/raw中。


###gradle配置

1)添加niuchart sdk github repositories

```gradle

repositories {
    maven { url "https://raw.githubusercontent.com/NiuChartTeam/maven-repo-niuchart/master" }
}

```

2)添加dependencies

版本号请按情况改成最新的即可。

```gradle

dependencies {
    compile('com.niuchart:NXCore:1.0.3-snapshot2@aar') {
            transitive = true;
    }
}

```

3)添加packagingOptions

```gradle

android {
  packagingOptions {
      exclude 'META-INF/ASL2.0'
      exclude 'META-INF/LICENSE'
      exclude 'META-INF/LICENSE.txt'
      exclude 'META-INF/NOTICE'
      exclude 'META-INF/NOTICE.txt'
      exclude 'META-INF/maven/com.belerweb/pinyin4j/pom.xml'
      exclude 'META-INF/maven/com.belerweb/pinyin4j/pom.properties'
      exclude 'LICENSE.txt'
  }
}

```

4）proguard配置

```properites

-dontwarn com.niuchart.**
-keep class com.niuchart.** {*;}

```

不清楚请参见[这里](https://github.com/NiuChartTeam/NXChart-Demo/blob/master/app/build.gradle)或留言或加批注。
