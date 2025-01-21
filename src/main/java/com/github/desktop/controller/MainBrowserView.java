package com.github.desktop.controller;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.browser.event.MediaStreamCaptureStarted;
import com.teamdev.jxbrowser.browser.event.MediaStreamCaptureStopped;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.engine.Language;
import com.teamdev.jxbrowser.engine.event.EngineCrashed;
import com.teamdev.jxbrowser.media.MediaDevice;
import com.teamdev.jxbrowser.media.MediaDeviceType;
import com.teamdev.jxbrowser.media.MediaDevices;
import com.teamdev.jxbrowser.net.ConnectionType;
import com.teamdev.jxbrowser.net.Network;
import com.teamdev.jxbrowser.net.event.NetworkChanged;
import com.teamdev.jxbrowser.net.event.ResponseBytesReceived;
import com.teamdev.jxbrowser.net.event.ResponseStarted;
import com.teamdev.jxbrowser.permission.PermissionType;
import com.teamdev.jxbrowser.permission.callback.RequestPermissionCallback;
import com.teamdev.jxbrowser.view.javafx.BrowserView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.extern.log4j.Log4j2;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;

import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;

@Log4j2
@Component
@FxmlView
public class MainBrowserView {

    private static final String DEFAULT_URL_ADDRESS = "https://demo.asliri.id/";

    @Value("${app.license.jx.browser}")
    private String jxBrowserLicenseKey;

    @FXML
    public ImageView backIcon;

    @FXML
    public ImageView forwardIcon;

    @FXML
    public ImageView reloadIcon;

    @FXML
    public TextField inputUrlAddress;

    @FXML
    public Label placeholderLoading;

    @FXML
    public VBox contentWebView;

    private Browser browser;
    private Engine engine;
    private Network network;

    @FXML
    public void initialize() {

        setupIconForNavigation();
        setupInputUrlAddress();

        setupLicenseKey();
        setupEngine();
        setupNetwork();
        setupMediaList();
        setupBrowser();
        setupBrowserView();
    }

    @FXML
    public void onBackButton(ActionEvent actionEvent) {
        log.debug("Click Back Button");
        if (browser.navigation().canGoBack()) {
            browser.navigation().goBack();
        }
    }

    @FXML
    public void onForwardButton(ActionEvent actionEvent) {
        log.debug("Click Forward Button");
        if (browser.navigation().canGoForward()) {
            browser.navigation().goForward();
        }
    }

    @FXML
    public void onReloadButton(ActionEvent actionEvent) {
        log.debug("Click Reload Button");
        browser.navigation().reload();
    }

    private void setupIconForNavigation() {

        setImageIcon("/icon/ic_back.png", backIcon);
        setImageIcon("/icon/ic_forward.png", forwardIcon);
        setImageIcon("/icon/ic_reload.png", reloadIcon);
    }

    private void setupInputUrlAddress() {

        inputUrlAddress.textProperty().set(DEFAULT_URL_ADDRESS);
        inputUrlAddress.setOnAction(event -> {
            String newUrlAddress = inputUrlAddress.getText();
            if (!newUrlAddress.startsWith("http://") && !newUrlAddress.startsWith("https://")) {
                newUrlAddress = "http://" + newUrlAddress;
            }

            browser.navigation().loadUrl(newUrlAddress);
        });
    }

    private void setupLicenseKey() {

        log.debug("License Key: {}", jxBrowserLicenseKey);
        System.setProperty("jxbrowser.license.key", jxBrowserLicenseKey);
    }

    private void setupEngine() {

        EngineOptions engineOptions = EngineOptions.newBuilder(HARDWARE_ACCELERATED)
                .language(Language.ENGLISH_US)
                .disableTouchMenu()
                .build();

        engine = Engine.newInstance(engineOptions);

        engine.on(EngineCrashed.class, event -> {
            var exitCode = event.exitCode();
            log.debug("Engine Crash with Exit Code: {}", exitCode);
        });

        engine.permissions().set(RequestPermissionCallback.class, (params, tell) -> {
            var type = params.permissionType();
            log.debug("Permission Type: {}", type);

            if (type == PermissionType.VIDEO_CAPTURE || type == PermissionType.AUDIO_CAPTURE) {
                log.debug("Tell Grant");
                tell.grant();
            } else {
                log.debug("Tell Deny");
                tell.deny();
            }
        });
    }

    private void setupNetwork() {

        network = engine.network();

        network.on(NetworkChanged.class, e -> {
            if (e.connectionType() == ConnectionType.TYPE_NONE) {
                Platform.runLater(() -> {
                    placeholderLoading.textProperty().set("No connection");
                    placeholderLoading.visibleProperty().set(true);
                });
            }
        });

        network.on(ResponseStarted.class, (event) -> {
            Platform.runLater(() -> {
                placeholderLoading.textProperty().set("Loading content...");
                placeholderLoading.visibleProperty().set(true);
            });
        });

        network.on(ResponseBytesReceived.class, event -> {
            Platform.runLater(() -> {
                placeholderLoading.textProperty().set("");
                placeholderLoading.visibleProperty().set(false);
            });
        });
    }

    private void setupMediaList() {

        MediaDevices mediaDevices = engine.mediaDevices();

        // Get all available video devices, e.g. web cameras.
        List<MediaDevice> videoDevices = mediaDevices.list(MediaDeviceType.VIDEO_DEVICE);
        log.debug("List Video Devices: {}", videoDevices);

        // Get all available audio devices, e.g. microphones.
        List<MediaDevice> audioDevices = mediaDevices.list(MediaDeviceType.AUDIO_DEVICE);
        log.debug("List Audio Devices: {}", audioDevices);
    }

    private void setupBrowser() {

        browser = engine.newBrowser();

        browser.on(MediaStreamCaptureStarted.class, e -> {
            log.debug("Started Capturing: {}", e.mediaStreamType());
        });

        browser.on(MediaStreamCaptureStopped.class, e -> {
            log.debug("Stopped Capturing: {}", e.mediaStreamType());
        });
    }

    private void setupBrowserView() {

        BrowserView view = BrowserView.newInstance(browser);
        view.dragAndDrop().disable();
        view.setPrefWidth(Double.MAX_VALUE);
        view.setPrefHeight(Double.MAX_VALUE);
        contentWebView.getChildren().add(view);

        view.prefWidthProperty().bind(contentWebView.widthProperty());
        view.prefHeightProperty().bind(contentWebView.heightProperty());
    }

    private void setImageIcon(String path, ImageView imageView) {
        URL imageURL = getClass().getResource(path);
        if (imageURL != null) {
            String imagePath = imageURL.toExternalForm();
            Image image = new Image(imagePath);
            imageView.setImage(image);
            imageView.setFitWidth(16.0);
            imageView.setFitHeight(16.0);
            imageView.setPreserveRatio(true);
        }
    }
}
