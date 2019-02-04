package com.medai.jchat;

// Uso: java TCPUpperServer <address> <port>

import com.medai.resources.Inter;
import javafx.application.Platform;

import java.io.*;
import java.net.*;

class TCPServer {

    private String address;
    private int port;

    private BuildMainWindow window;

    TCPServer(String address, int port, BuildMainWindow window) {
        this.window = window;
        this.address = address;
        this.port = port;
    }

    void openServer() throws Exception {
        ServerSocket welcomeSocket = new ServerSocket();

        welcomeSocket.bind(new InetSocketAddress(this.address, this.port)); // si può specificare anche un backlog

        Thread t = new Thread(() -> {
            //noinspection InfiniteLoopStatement
            while (true) {
                try {
                    System.out.println("Waiting for a new client");
                    Socket connectionSocket = welcomeSocket.accept();

                    new Thread(() -> this.InitServeClient(connectionSocket)).start();
                } catch (Exception e) {
                    e.printStackTrace();
                    this.notifyError();
                    break;
                }
            }
        });

        t.start();
    }

    private void InitServeClient(Socket connectionSocket) {
        System.out.println("New connection from " + connectionSocket.getInetAddress() + ":"
                    + connectionSocket.getPort());

            try {
                DataInputStream inFromClient = new DataInputStream(connectionSocket.getInputStream());
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

                outToClient.writeUTF(InetAddress.getLocalHost().getHostName());

                String clientName = inFromClient.readUTF();

                while (clientName.equals("#")) {
                    clientName = inFromClient.readUTF();
                }

                final String clientName1 = clientName;

                ChatItemServer itemServer = new ChatItemServer(this.window, connectionSocket.getInetAddress().toString());
                Platform.runLater(() -> {
                    this.window.getChatList().getChildren().add(itemServer);
                    itemServer.setName(clientName1);
                });

                // sending loop
                new Thread(() -> {
                    //noinspection InfiniteLoopStatement
                    while (true) {
                        try {
                            while (itemServer.getMessage().equals("")) {
                                outToClient.writeUTF("#");
                            }

                            String tmp = itemServer.sendMessage();
                            outToClient.writeUTF(tmp);
                            Platform.runLater(() -> {
                                itemServer.getPreview().setText(Inter.getLocale("s") + tmp);
                                itemServer.addMessage(tmp, false);
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            this.notifyError();
                            break;
                        }
                    }
                }).start();
                // receive loop
                new Thread(() -> {
                    //noinspection InfiniteLoopStatement
                    while (true) {
                        try {
                            String clientSentence = inFromClient.readUTF();

                            while (clientSentence.equals("#")) {
                                clientSentence = inFromClient.readUTF();
                            }

                            if (!itemServer.selected) {
                                itemServer.toggleNewOn();
                            }

                            final String sentence = clientSentence;

                            Platform.runLater(() -> {
                                itemServer.getPreview().setText(Inter.getLocale("r") + sentence);
                                itemServer.addMessage(sentence, true);
                            });
                        } catch (Exception e) {
                            //e.printStackTrace();
                            //this.notifyError();
                            break;
                        }
                    }
                }).start();
            } catch (Exception e1) {
                e1.printStackTrace();
                System.out.println("Connection closed.");
                try {
                    connectionSocket.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
    }

    private void notifyError() {
        Message m = new Message();
        Platform.runLater(() -> m.display(Inter.getLocale("error"), Inter.getLocale("reach")));
    }

}

