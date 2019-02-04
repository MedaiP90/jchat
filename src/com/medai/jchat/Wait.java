package com.medai.jchat;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

@SuppressWarnings("FieldCanBeLocal")
public class Wait {

    Stage stage;
    private Scene scene;
    private VBox layout;

    private ProgressBar pb;
    private Label perc;

    public void display(String title, String message, boolean up) {
        this.stage = new Stage();
        this.stage.getIcons().add(new Image(BuildMainWindow.class.getResourceAsStream("logo.png")));
        this.stage.setTitle(title);
        this.stage.setResizable(false);

        if(up)
            this.stage.initModality(Modality.APPLICATION_MODAL);

        this.stage.setOnCloseRequest(e -> {
            e.consume(); // consuma l'evento di chiusura e non lo esegue dopo
        });

        this.layout = new VBox(10);
        this.layout.setPadding(new Insets(20));
        this.layout.setAlignment(Pos.CENTER);

        Label label = new Label(message);
        this.layout.getChildren().add(label);

        this.pb = new ProgressBar();

        this.perc = new Label("");

        StackPane pane = new StackPane();
        pane.setAlignment(Pos.CENTER);

        pane.getChildren().addAll(this.pb, this.perc);
        this.layout.getChildren().add(pane);

        this.scene = new Scene(this.layout);

        this.stage.setScene(this.scene);

        this.stage.show();
    }

    ProgressBar getProgress() {
        return this.pb;
    }

    Label getPerc() {
        return this.perc;
    }

}
