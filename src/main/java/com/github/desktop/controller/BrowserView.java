package com.github.desktop.controller;

import com.teamdev.jxbrowser.browser.event.MediaStreamCaptureStarted;
import com.teamdev.jxbrowser.browser.event.MediaStreamCaptureStopped;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.media.MediaDeviceType;
import com.teamdev.jxbrowser.permission.PermissionType;
import com.teamdev.jxbrowser.permission.callback.RequestPermissionCallback;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import lombok.extern.log4j.Log4j2;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;

@Log4j2
@Component
@FxmlView
public class BrowserView {

    @Value("${app.license.jx.browser}")
    private String jxBrowserLicenseKey;

    @FXML
    public VBox contentWebView;

    @FXML
    public void initialize() {

        log.debug("License Key: {}", jxBrowserLicenseKey);

        System.setProperty("jxbrowser.license.key", jxBrowserLicenseKey);

        var engine = Engine.newInstance(HARDWARE_ACCELERATED);
        var mediaDevices = engine.mediaDevices();

        // Get all available video devices, e.g. web cameras.
        var videoDevices = mediaDevices.list(MediaDeviceType.VIDEO_DEVICE);
        log.debug("List Video Devices: {}", videoDevices);

        // Get all available audio devices, e.g. microphones.
        var audioDevices = mediaDevices.list(MediaDeviceType.AUDIO_DEVICE);
        log.debug("List Audio Devices: {}", audioDevices);

        var browser = engine.newBrowser();

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

        browser.on(MediaStreamCaptureStarted.class, e -> {
            log.debug("Started Capturing: {}", e.mediaStreamType());
        });

        browser.on(MediaStreamCaptureStopped.class, e -> {
            log.debug("Stopped Capturing: {}", e.mediaStreamType());
        });

        var view = com.teamdev.jxbrowser.view.javafx.BrowserView.newInstance(browser);
        view.setPrefWidth(Double.MAX_VALUE);
        view.setPrefHeight(Double.MAX_VALUE);
        contentWebView.getChildren().add(view);

        view.prefWidthProperty().bind(contentWebView.widthProperty());
        view.prefHeightProperty().bind(contentWebView.heightProperty());

        browser.navigation().loadUrl("https://demo.asliri.id/");
    }
}
