package com.medai.jchat;

import com.medai.resources.Inter;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

@SuppressWarnings("FieldCanBeLocal")
class ChatItemServer extends VBox {

    private int spacing = 10;
    private Insets padding = new Insets(5, 10, 5, 5);

    private Label name;
    private Label address;
    private Label preview;
    private HBox container;
    private VBox conversation;

    private BuildMainWindow mainWindow;

    @SuppressWarnings("unused")
    private String message = "";
    boolean selected = false;

    ChatItemServer(BuildMainWindow mainWindow, String a) {
        this.mainWindow = mainWindow;

        this.getStyleClass().add("item");

        this.name = new Label(Inter.getLocale("defaultname"));
        this.address = new Label("(" + a + ")");
        this.preview = new Label("");
        this.conversation = new VBox(10);
        this.conversation.setPadding(new Insets(10, 20, 10, 20));

        this.setSpacing(this.spacing);
        this.setPadding(this.padding);
        this.setPrefSize(355, 60);
        this.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        this.container = new HBox(this.spacing);
        this.container.getChildren().addAll(this.name, this.address);

        this.getChildren().addAll(this.container, this.preview);

        this.setOnMouseClicked( e -> handler() );

        this.focusedProperty().addListener((ObservableValue<? extends Boolean> observable,
                                            Boolean oldValue, Boolean newValue) -> this.focusState(newValue));
    }

    private void focusState(boolean value) {
        this.selected = value;
        if(value)
            this.toggleNewOff();
    }

    private void handler() {
        this.requestFocus();
        this.mainWindow.getLabel2().setText(this.name.getText());

        this.mainWindow.getSend().setOnAction(e -> {
            String m = this.mainWindow.getMessage();
            if(!(m == null || m.equals(""))) {
                // send the message
                this.message = m;
            }
        });

        // add the chat to scroll pane
        this.mainWindow.getScroll2().setContent(this.conversation);
    }

    void toggleNewOn() {
        this.getStyleClass().remove("item");
        this.getStyleClass().add("new");
    }

    private void toggleNewOff() {
        this.getStyleClass().remove("new");
        this.getStyleClass().add("item");
    }

    public String getMessage() {
        return this.message;
    }

    String sendMessage() {
        String m = this.message;
        this.message = "";
        this.toggleNewOff();
        return m;
    }

    Label getPreview() {
        return this.preview;
    }

    void addMessage(String m, boolean r) {
        ChatItem.addMessage(m, r, this.conversation);
    }

    void setName(String n) {
        this.name.setText(n);
    }

}
