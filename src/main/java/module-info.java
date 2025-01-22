module com.github.desktop {
    requires jxbrowser;
    requires jxbrowser.javafx;

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires lombok;
    requires org.apache.logging.log4j;

    requires net.rgielen.fxweaver.core;
    requires net.rgielen.fxweaver.spring;

    requires spring.beans;
    requires spring.context;
    requires spring.web;
    requires spring.boot;
    requires spring.boot.autoconfigure;

    requires java.base;

    exports com.github.desktop;
}