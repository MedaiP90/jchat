package com.medai.jchat;

import com.medai.resources.Inter;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

@SuppressWarnings({"AccessStaticViaInstance", "FieldCanBeLocal"})
class NewWindow {

    private BuildMainWindow window;

    private Stage stage;
    private Scene scene;
    private GridPane layout;

    private HBox container;

    private TextField name;
    private TextField address;
    private Button confirm;
    private Button cancel;
    private Label nameLabel;
    private Label addressLabel;
    private ChoiceBox<String> choiceBox;
    private Button search;
    private HBox choice;

    private String myIp;

    void display(BuildMainWindow window, String a) {
        this.myIp = a;

        this.window = window;
        this.stage = new Stage();
        this.stage.getIcons().add(new Image(BuildMainWindow.class.getResourceAsStream("logo.png")));
        this.stage.setTitle(Inter.getLocale("addnew"));
        this.stage.setResizable(false);

        this.stage.initModality(Modality.APPLICATION_MODAL);

        this.layout = new GridPane();
        this.layout.setHgap(10);
        this.layout.setVgap(10);
        this.layout.setPadding(new Insets(20));
        this.layout.setAlignment(Pos.CENTER);

        // setting up the main content
        this.nameLabel = new Label(Inter.getLocale("name"));
        this.name = new TextField();
        this.name.setPromptText(Inter.getLocale("name"));
        this.addressLabel = new Label(Inter.getLocale("address"));
        this.address = new TextField();
        this.address.setPromptText(Inter.getLocale("address"));
        this.container = new HBox(10);
        this.container.setAlignment(Pos.CENTER);
        this.confirm = new Button(Inter.getLocale("confirm"));
        this.cancel = new Button(Inter.getLocale("cancel"));

        this.container.getChildren().addAll(this.confirm, this.cancel);

        this.confirm.setOnAction(e -> this.add());
        this.cancel.setOnAction(e -> this.close());

        this.choice = new HBox(10);
        this.choice.setAlignment(Pos.CENTER);

        this.choiceBox = new ChoiceBox<>();
        this.choiceBox.getItems().add(Inter.getLocale("defaultlist"));
        this.choiceBox.setValue(Inter.getLocale("defaultlist"));
        this.search = new Button(Inter.getLocale("search"));
        this.search.setOnAction(e -> this.findUsers());

        this.choiceBox.setOnAction(e -> {
            String[] value = this.choiceBox.getValue().split("::");
            this.name.setText(value[0]);
            this.address.setText(value[1]);
        });

        this.choice.getChildren().addAll(this.choiceBox, this.search);

        this.layout.setConstraints(this.nameLabel, 0, 0);
        this.layout.setConstraints(this.name, 1, 0);
        this.layout.setConstraints(this.addressLabel, 0, 1);
        this.layout.setConstraints(this.address, 1, 1);
        this.layout.setConstraints(this.choice,0, 2, 2, 1);
        this.layout.setConstraints(this.container, 0, 3, 2, 1);
        this.layout.getChildren().addAll(this.nameLabel, this.name, this.addressLabel, this.address,
                this.choice, this.container);

        this.scene = new Scene(this.layout);

        this.stage.setScene(this.scene);
        this.stage.showAndWait();
    }

    private void close() {
        this.stage.close();
    }

    private void add() {
        String n = this.name.getText();
        String a = this.address.getText();

        if(!this.isNull(a) && !this.isNull(n)) {
            this.stage.close();

            new Thread(() -> {
                try {
                    @SuppressWarnings("unused")
                    TCPClient client = new TCPClient(a, this.window, n);
                } catch (Exception e) {
                    e.printStackTrace();
                    Message m = new Message();
                    Platform.runLater(() -> m.display(Inter.getLocale("error"), Inter.getLocale("reach")));
                }
            }).start();
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isNull(String s) {
        return s == null || s.equals("");
    }

    private void findUsers() {
        Wait w = new Wait();
        w.display(Inter.getLocale("waiting"), Inter.getLocale("searching"), true);
        new Thread(() -> {
            String ip_part = "192.168.1.";
            for(int i = 1; i < 255; i++) {
                final int tmpi = i;
                Platform.runLater(() -> {
                    w.getProgress().setProgress((tmpi*100d)/25500d);
                    w.getPerc().setText((tmpi*100)/255 + "%");
                });
                try {
                    String tmp = ip_part + i;

                    if(!tmp.equals(this.myIp)) {
                        System.out.println("Testing: " + tmp);

                        Socket clientSocket = new Socket();
                        clientSocket.connect(new InetSocketAddress(tmp, 8888), 400);

                        DataInputStream inFromServer = new DataInputStream(clientSocket.getInputStream());
                        String n = inFromServer.readUTF();

                        System.out.println("Connected to: " + tmp);

                        String tmp1 = n + "::" + tmp;
                        Platform.runLater(() -> this.choiceBox.getItems().add(tmp1));

                        clientSocket.close();
                        System.out.println("Connection to " + tmp + " closed");
                    }
                } catch(Exception ignored) {}
            }
            Platform.runLater(() -> w.stage.close());
        }).start();
    }

}
