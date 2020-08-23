module FReader {
    requires fx.framework.core;
    requires fx.framework.resource;
    requires fx.framework.aop;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.instrument;
    requires javafx.swing;
    requires static lombok;
    requires fx.framework.jpa;
    requires java.persistence;
    requires org.controlsfx.controls;
    requires org.hibernate.orm.core;
    requires java.desktop;
    requires epublib.core;
    requires kxml2;
    requires org.jsoup;
    requires cpdetector;
    requires mobi.java;
    requires pdfbox;
    requires net.bytebuddy;
    requires pinyin4j;
    requires jdk.jsobject;
    requires jmimemagic;
    requires commons.io;
    requires slf4j.api;

    opens org.swdc.reader.aspect to
            fx.framework.core,
            fx.framework.aop;

    opens org.swdc.reader.core.locators to
            fx.framework.core;

    opens org.swdc.reader.core.views to
            javafx.web,
            fx.framework.core;

    opens org.swdc.reader.core.configs to
            org.controlsfx.controls,
            fx.framework.core;

    opens org.swdc.reader.core.readers to
            fx.framework.core;

    opens org.swdc.reader.core.ext to
            fx.framework.core;

    opens org.swdc.reader to
            fx.framework.core,
            javafx.graphics;

    opens org.swdc.reader.services to
            net.bytebuddy,
            fx.framework.aop,
            fx.framework.core;

    exports org.swdc.reader.services;

    opens org.swdc.reader.config to
            fx.framework.core;

    // 被aop的类需要对AOP的组件open
    opens org.swdc.reader.ui.view to
            fx.framework.core,
            fx.framework.aop,
            javafx.fxml;

    // 导出被aop的类的包和他们需要的类所在的包，因为aop后新类在未命名模块
    // 不导出未命名模块无法使用它们
    exports org.swdc.reader.ui.view;
    exports org.swdc.reader.ui.events;

    opens org.swdc.reader.ui.controller to
            fx.framework.core,
            javafx.fxml;

    opens org.swdc.reader.ui.events to
            fx.framework.core;

    opens org.swdc.reader.entity to
            org.hibernate.orm.core,
            fx.framework.jpa;

    exports org.swdc.reader.entity;

    opens org.swdc.reader.ui.controller.dialog to
            fx.framework.core,
            javafx.fxml;

    opens org.swdc.reader.ui.view.dialogs to
            fx.framework.core,
            javafx.fxml;

    opens org.swdc.reader.ui.view.cells to
            fx.framework.core,
            javafx.fxml;

    opens views to fx.framework.core;

}