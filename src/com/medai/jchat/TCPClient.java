package com.medai.jchat;

// Uso: java TCPUpperClient <server address> <port>

import com.medai.resources.Inter;
import javafx.application.Platform;

import java.io.*;
import java.net.*;

class TCPClient {

    TCPClient(String ip, BuildMainWindow window, String n) throws Exception {
        Socket clientSocket = new Socket();

        clientSocket.connect(new InetSocketAddress(ip, 8888));

        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream()); // dal client al server
        DataInputStream inFromServer = new DataInputStream(clientSocket.getInputStream());  // dal server al client

        try {
            inFromServer.readUTF();
            outToServer.writeUTF(InetAddress.getLocalHost().getHostName());
        } catch (IOException e) {
            e.printStackTrace();
        }

        ChatItem itemServer = new ChatItem(window, n, ip);
        Platform.runLater(() -> window.getChatList().getChildren().add(itemServer));

        // sending loop
        new Thread(() -> {
            //noinspection InfiniteLoopStatement
            while (true) {
                try {
                    while (itemServer.getMessage().equals("")) {
                        outToServer.writeUTF("#");
                    }

                    String tmp = itemServer.sendMessage();
                    outToServer.writeUTF(tmp);
                    Platform.runLater(() -> {
                        itemServer.getPreview().setText(Inter.getLocale("s") + tmp);
                        itemServer.addMessage(tmp, false);
                    });
                } catch(Exception e) {
                    e.printStackTrace();
                    this.notifyError();
                    try {
                        clientSocket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    System.out.println("Bye bye!");
                    break;
                }
            }
        }).start();
        // receive loop
        new Thread(() -> {
            //noinspection InfiniteLoopStatement
            while (true) {
                try {
                    String clientSentence = inFromServer.readUTF();

                    while(clientSentence.equals("#")) {
                        clientSentence = inFromServer.readUTF();
                    }

                    if(!itemServer.selected) {
                        itemServer.toggleNewOn();
                    }

                    final String sentence = clientSentence;

                    Platform.runLater(() -> {
                        itemServer.getPreview().setText(Inter.getLocale("r") + sentence);
                        itemServer.addMessage(sentence, true);
                    });
                } catch(Exception e) {
                    //e.printStackTrace();
                    //this.notifyError();
                    try {
                        clientSocket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    System.out.println("Bye bye!");
                    break;
                }
            }
        }).start();
    }

    private void notifyError() {
        Message m = new Message();
        Platform.runLater(() -> m.display(Inter.getLocale("error"), Inter.getLocale("reach")));
    }

}

