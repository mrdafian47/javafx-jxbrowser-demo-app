package com.github.desktop.app;

import com.github.desktop.controller.DashboardView;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class PrimaryStageInitializer implements ApplicationListener<StageReadyEvent> {

    private final FxWeaver fxWeaver;

    @Autowired
    public PrimaryStageInitializer(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }

    @Override
    public void onApplicationEvent(@NonNull StageReadyEvent event) {
        Stage stage = event.getStage();

        Parent parent = fxWeaver.loadView(DashboardView.class);
        Scene scene = new Scene(parent);

        stage.setTitle("Dashboard");
        stage.setScene(scene);
        stage.show();
    }
}
