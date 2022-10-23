module reader {

    requires java.sql;
    requires java.desktop;

    requires swdc.application.dependency;
    requires swdc.application.fx;
    requires swdc.application.configs;
    requires swdc.application.data;


    requires jakarta.inject;
    requires jakarta.annotation;

    requires javafx.swing;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.slf4j;
    requires java.persistence;

    requires org.jsoup;
    requires org.apache.commons.codec;
    requires cpdetector;

    requires org.controlsfx.controls;
    requires pinyin4j;
    requires mobi.java;
    requires pdfbox;
    requires epublib.core;

    requires jmimemagic;

    requires jchmlib;
    requires umdTextFile;
    requires djvu;

    exports org.swdc.reader to javafx.graphics;

    opens icons;
    opens org.swdc.reader.entity to
            swdc.application.data,
            swdc.application.dependency,
            javafx.base,
            org.hibernate.orm.core;

    opens  org.swdc.reader.core.configs to
            swdc.application.dependency,
            swdc.application.configs,
            org.controlsfx.controls;

    opens org.swdc.reader.core.ext to
            swdc.application.dependency;

    opens org.swdc.reader.core.readers to
            swdc.application.dependency;

    opens org.swdc.reader.repo to swdc.application.dependency;

    opens views.main;
    opens views.dialogs;

    opens org.swdc.reader to
            javafx.fxml,
            swdc.application.fx,
            swdc.application.dependency,
            swdc.application.configs;

    opens org.swdc.reader.ui to
            javafx.fxml,
            swdc.application.fx,
            swdc.application.dependency,
            swdc.application.configs;

    opens org.swdc.reader.ui.dialogs.mainview to
            javafx.fxml,
            swdc.application.fx,
            swdc.application.dependency,
            swdc.application.configs;

    opens org.swdc.reader.ui.dialogs.reader to
            javafx.fxml,
            swdc.application.fx,
            swdc.application.dependency,
            swdc.application.configs;

    opens org.swdc.reader.services to
            javafx.fxml,
            swdc.application.fx,
            swdc.application.dependency,
            swdc.application.configs;

    opens org.swdc.reader.ui.cells to
            javafx.fxml,
            swdc.application.configs,
            swdc.application.dependency,
            swdc.application.fx;

    provides java.net.spi.URLStreamHandlerProvider with org.swdc.reader.core.URLManager;

}