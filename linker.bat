@echo off

rem 注意，这个是使用jdk14的jpackage打包的脚本，
rem 所以运行前请在此环境准备jdk14以及jdk14打包需要的WIX
rem wix需要放入PATH环境变量中。
rem 运行此脚本打包前需要运行maven的package。

echo cleaning...

rmdir /s /q deploy
mkdir deploy

rem 定义应用名称
set name="Reader"
rem 定义模块名称
set moduleName="FReader"
rem 定义启动类全限定名
set mainClass="org.swdc.reader.ReaderApplication"
rem 定义javafx的jmod所在位置
set javafxJmodPath="D:\javafx15"
rem 应用的图标
set iconName="icon.ico"

echo start jlink for runtime...

rem 进行连接，生成JRE环境
jlink --module-path %javafxJmodPath% --add-modules java.instrument,java.base,java.naming,java.scripting,javafx.fxml,javafx.controls,javafx.base,javafx.graphics,jdk.jfr,java.datatransfer,java.prefs,java.xml,java.sql,java.transaction.xa,java.desktop,jdk.unsupported,java.instrument,javafx.web,javafx.media,java.compiler,java.xml.crypto,jdk.unsupported.desktop,java.net.http,jdk.jsobject,jdk.xml.dom,java.prefs,java.transaction.xa,java.logging,javafx.swing,java.management.rmi,jdk.zipfs --output deploy\runtime

echo deploy application....

rem 执行打包，注意，打包之后的lib里面会附带很多javafx的包，这些应该删掉，他们都已经在jre里面了，多了的话无法启动
cd deploy
jpackage --runtime-image runtime  --type app-image -n %name% -p ../target/lib --icon ../icon.ico -m %moduleName%/%mainClass%
rem 复制应用jar包
copy ..\target\*.jar "./%name%/app/mods/"
echo done.

cd ../