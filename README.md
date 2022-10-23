# BookReader Project

## 概述 Abstract

这个是阅读器，用于看电子书的那种，计划支持多数常见的电子书格式以及
RSS（Feed）电子订阅，基于SpringBoot，javaFX。

搞事结束，已经不在用 springboot了，但是会留着一个springboot的分支。
目前已经移植到了自己的框架上。

本项目于20211/7/3日重新设计，便为新的基础框架，这使得工程结构更加清晰。

如果需要构建（build）此工程，需要提前在本地安装以下工程：
[Application-Dependency](https://github.com/SW-Fantastic/swdc-dependency)

[Application-JavaFX](https://github.com/SW-Fantastic/swdc-javafx)

[Application-Configure](https://github.com/SW-Fantastic/swdc-configure)

[Application-Data](https://github.com/SW-Fantastic/application-db)

本工程以及以上的组件需要Java 11 以上（建议JDK14），安装以上工程后直接安装一般的maven工程启动即可。

就目前而言，此工程还在完善之中。

this is a Reader for EBooks ，planning supports most common formats like txt epub pdf and more
include RSS （Feeds）。

## 进度 Process

#### 主要功能 Main Functions
 - [x] 基础结构搭建（Basic Data Structs)
 - [x] 基础UI的设计和美化 (UI Design)
 - [x] 数据结构建模（DataStruct Design）
 - [x] 核心接口（Core interface）
 - [x] 文本阅读的实现（Text reader implemented）
 - [x] PDF阅读的实现（Adobe PDF reader implement）
 - [x] EPublic阅读的实现（EPUB File Reader implement）
 - [x] MOBI文件的基础支撑（Mobi kindle format implement)
 - [x] 早期UMD格式的支持（Universal Mobile document implement）
 - [x] Djvu扫描格式的支持（Djvu scanned document implement）
 - [ ] Feed/RSS阅读（Feed RSS read implement）

#### 细节功能 Details
 - [x] 基本的书籍数据管理（Book Metadata）
 - [x] 标签功能（Tags）
 - [x] 书籍数据索引功能（index tree from metadata） 
 - [x] 文本内容可以通过键盘左右方向键快速翻页，回车可以重定向焦点。（key-board support）
 - [ ] 增加i18n语言切换（i18n language，Chinese English）

#### Bug记录 

 - [x] 主分类选中时首次切换到标签或者作者等位于Tree中的选项，将会导致表格空白。
 - [x] 书籍属性中，tag标签在窗口关闭后未被清空。