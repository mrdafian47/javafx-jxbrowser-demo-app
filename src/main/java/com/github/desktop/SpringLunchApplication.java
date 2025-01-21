package com.github.desktop;

import com.github.desktop.app.MainApplication;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringLunchApplication {

    public static void main(String[] args) {
        Application.launch(MainApplication.class, args);
    }
}
