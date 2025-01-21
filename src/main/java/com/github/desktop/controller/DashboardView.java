package com.github.desktop.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import lombok.extern.log4j.Log4j2;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@FxmlView
public class DashboardView {

    @Autowired
    private FxWeaver fxWeaver;

    @FXML
    public void initialize() {

    }

    @FXML
    public void onNavigateToBrowser(ActionEvent actionEvent) {

    }
}
