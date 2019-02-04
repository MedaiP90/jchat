package com.medai.jchat;

import com.medai.resources.Inter;
import javafx.application.Application;
import javafx.stage.Stage;

import java.net.InetSocketAddress;
import java.net.ServerSocket;

@SuppressWarnings({"FieldCanBeLocal"})
public class Main extends Application {

    private int port = 8888;
    private String ip = "localhost";

    @SuppressWarnings("unused")
    private BuildMainWindow window;

    @Override
    public void start(Stage primaryStage) {
        this.getIP();

        System.out.println("IP: " + this.ip);
        this.window = new BuildMainWindow(primaryStage, this.ip, this.port);
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void getIP() {
        Wait w = new Wait();
        w.display(Inter.getLocale("waiting"), Inter.getLocale("loading"), false);
        String ip_part = "192.168.1.";
        for(int i = 1; i < 255; i++) {
            try {
                String tmp = ip_part + i;
                ServerSocket welcomeSocket = new ServerSocket();
                welcomeSocket.bind(new InetSocketAddress(tmp, this.port));

                this.ip = tmp;
                welcomeSocket.close();
                break;
            } catch(Exception ignored) {}
        }
        w.stage.close();

        if(this.ip.equals("localhost")) {
            Message m = new Message();
            m.display(Inter.getLocale("error"), Inter.getLocale("server"));
        }
    }
}
