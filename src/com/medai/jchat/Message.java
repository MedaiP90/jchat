package com.medai.jchat;

import com.medai.resources.Inter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class Message {

    private Stage stage;
    private Scene scene;
    private VBox layout;

    public void display(String title, String message) {
        this.stage = new Stage();
        this.stage.getIcons().add(new Image(BuildMainWindow.class.getResourceAsStream("logo.png")));
        this.stage.setTitle(title);
        this.stage.setResizable(false);

        this.stage.initModality(Modality.APPLICATION_MODAL);

        this.layout = new VBox(10);
        this.layout.setPadding(new Insets(20));
        this.layout.setAlignment(Pos.CENTER);

        Label label = new Label(message);
        this.layout.getChildren().add(label);

        Button button = new Button(Inter.getLocale("confirm"));
        button.setOnAction(e -> this.stage.close());
        this.layout.getChildren().add(button);

        this.scene = new Scene(this.layout);

        this.stage.setScene(this.scene);
        this.stage.showAndWait();
    }

}
