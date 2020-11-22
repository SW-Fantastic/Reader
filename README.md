# BookReader Project

## 概述 Abstract

这个是阅读器，用于看电子书的那种，计划支持多数常见的电子书格式以及
RSS（Feed）电子订阅，基于SpringBoot，javaFX。

搞事结束，已经不在用 springboot了，但是会留着一个springboot的分支。
目前已经移植到了自己的框架上。

this is a Reader for EBooks ，planning supports most common formats like txt epub pdf and more
include RSS （Feeds），based javaFX and SpringBoot

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
 - [x] Feed/RSS阅读（Feed RSS read implement）

#### 细节功能 Details
 - [x] 基本的书籍数据管理（Book Metadata）
 - [x] 标签功能（Tags）
 - [x] 书籍数据索引功能（index tree from metadata） 
 - [x] 文本内容可以通过键盘左右方向键快速翻页，回车可以重定向焦点。（key-board support）
 - [x] 增加i18n语言切换（i18n language，Chinese English）
 - [x] 增加一个纯色主题