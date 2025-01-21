package com.github.desktop.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
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
    public VBox contentDashboardView;

    @FXML
    public void initialize() {
        loadDefaultView();
        setTitle("Dashboard");
    }

    private void onNavigateToBrowser(ActionEvent actionEvent) {
        Node node = fxWeaver.loadView(MainBrowserView.class);
        contentDashboardView.getChildren().clear();
        contentDashboardView.getChildren().add(node);
        setTitle("Browser");
    }

    private void loadDefaultView() {
        VBox layoutDefault = new VBox(
                36.0,
                getLabelCurrentOS(),
                getMainContent()
        );
        layoutDefault.setAlignment(Pos.CENTER);

        contentDashboardView.getChildren().clear();
        contentDashboardView.getChildren().add(layoutDefault);
    }

    private HBox getLabelCurrentOS() {

        Label keyCurrentOS = new Label("Current OS");

        String currentOS = System.getProperty("os.name").toLowerCase();
        Label valueCurrentOS = new Label(currentOS);

        HBox layout = new HBox(16.0, keyCurrentOS, valueCurrentOS);
        layout.setAlignment(Pos.CENTER);

        return layout;
    }

    private VBox getMainContent() {

        Label labelMenuBrowser = new Label("Menu Browser");

        Button buttonMenuBrowser = new Button("NAVIGATE");
        buttonMenuBrowser.setOnAction(this::onNavigateToBrowser);

        VBox layoutMenuBrowser = new VBox(16.0, labelMenuBrowser, buttonMenuBrowser);
        layoutMenuBrowser.setAlignment(Pos.CENTER);

        return layoutMenuBrowser;
    }

    private void setTitle(String title) {
        Scene scene = contentDashboardView.getScene();
        if (scene != null) {
            Stage stage = (Stage) scene.getWindow();
            stage.setTitle(title);
        }
    }
}
