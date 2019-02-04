package com.medai.jchat;

import com.medai.resources.Inter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

@SuppressWarnings("FieldCanBeLocal")
class BuildMainWindow {

    // window dimensions {width, height}
    private double[] wDim = {830, 650};
    private boolean resizable = false;

    private int spacing = 10;
    private Insets paddingMain = new Insets(10, 20, 10, 20);
    private Pos center = Pos.CENTER;

    private Stage window;
    private Scene scene;
    private GridPane layout;
    private ScrollPane scroll1;
    private ScrollPane scroll2;
    private VBox chatList;

    private Label label1;
    private Label label2;
    private Button addNew;
    private HBox sending;
    private TextField message;
    private Button send;

    private NewWindow popup = new NewWindow();

    @SuppressWarnings("AccessStaticViaInstance")
    BuildMainWindow(Stage window, String ip, int port) {
        // define the new window
        this.window = window;
        this.window.getIcons().add(new Image(BuildMainWindow.class.getResourceAsStream("logo.png")));
        this.window.setTitle(Inter.getLocale("title") + " (IP: " + ip + ")");
        this.window.setResizable(this.resizable);
        this.window.setOnCloseRequest(e -> {
            e.consume(); // consuma l'evento di chiusura e non lo esegue dopo
            this.window.close();
            System.exit(0);
        });

        // add conversation button
        this.addNew = new Button(Inter.getLocale("addnew"));
        this.addNew.setOnAction( e -> popup.display(this, ip));

        // initialize labels
        this.label1 = new Label(Inter.getLocale("chats"));
        this.label1.setPadding(new Insets(0,0,0,10));
        this.label2 = new Label(Inter.getLocale("default"));
        this.label2.setPadding(new Insets(0,0,0,10));

        // setting up the scroll panels
        this.scroll1 = new ScrollPane();
        this.scroll1.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.scroll1.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.scroll1.setPrefSize(this.wDim[0]/2 - this.paddingMain.getLeft(),
                this.wDim[1]/2 - this.paddingMain.getTop());
        this.scroll2 = new ScrollPane();
        this.scroll2.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.scroll2.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.scroll2.setPrefSize(this.wDim[0]/2 - this.paddingMain.getRight(),
                this.wDim[1]/2 - this.paddingMain.getTop());

        // setting up the chat list
        this.chatList = new VBox(this.spacing);
        this.chatList.setPadding(this.paddingMain);
        this.chatList.setAlignment(this.center);

        this.scroll1.setContent(this.chatList);

        // setting up the sending section
        this.sending = new HBox(this.spacing);
        this.sending.prefWidth(this.wDim[0]/2 - this.paddingMain.getRight());
        // text and button
        this.message = new TextField();
        this.message.setPromptText(Inter.getLocale("write"));
        this.send = new Button(Inter.getLocale("send"));

        this.sending.setHgrow(this.message, Priority.ALWAYS);
        this.sending.getChildren().addAll(this.message, this.send);

        // setting up the layout of the scene
        this.layout = new GridPane();
        this.layout.setHgap(this.spacing);
        this.layout.setVgap(this.spacing);
        this.layout.setPadding(this.paddingMain);
        this.layout.setAlignment(this.center);

        this.layout.setConstraints(this.addNew, 0, 2);
        this.layout.setConstraints(this.label1, 0, 0);
        this.layout.setConstraints(this.label2, 1, 0);
        this.layout.setConstraints(this.scroll1, 0, 1);
        this.layout.setConstraints(this.scroll2, 1, 1);
        this.layout.setConstraints(this.sending, 1, 2);
        this.layout.getChildren().addAll(this.addNew, this.label1, this.label2, this.scroll1,
                this.scroll2, this.sending);

        // initialize the scene
        this.scene = new Scene(this.layout); //, this.wDim[0], this.wDim[1]);
        this.scene.getStylesheets().add("com/medai/jchat/itemStyle.css");

        this.window.setScene(this.scene);
        this.window.show();

        System.out.println("Main window prepared and showed");

        this.runServer(ip, port);
    }

    private void runServer(String ip, int port) {
        try {
            TCPServer server = new TCPServer(ip, port, this);
            server.openServer();
        } catch (Exception e) {
            e.printStackTrace();
            Message m = new Message();
            m.display(Inter.getLocale("error"), Inter.getLocale("server"));
        }
    }

    Button getSend() {
        return this.send;
    }

    String getMessage() {
        String message = this.message.getText() + "\n";
        this.message.setText("");
        return message;
    }

    Label getLabel2() {
        return this.label2;
    }

    VBox getChatList() {
        return this.chatList;
    }

    ScrollPane getScroll2() {
        return this.scroll2;
    }

}
