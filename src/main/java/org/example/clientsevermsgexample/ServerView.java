package org.example.clientsevermsgexample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ServerView implements Initializable {

    @FXML
    private VBox vbox_messages;

    @FXML
    private TextField tf_message;

    @FXML
    private Button button_send;

    ServerSocket serverSocket;
    Socket clientSocket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(6666);
                addMessageOnUI("Server started. Waiting for client...");

                clientSocket = serverSocket.accept();
                addMessageOnUI("Client connected!");

                dataInputStream = new DataInputStream(clientSocket.getInputStream());
                dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

                while (true) {
                    try {
                        String message = dataInputStream.readUTF();
                        addMessageOnUI("Client: " + message);
                    } catch (IOException e) {
                        addMessageOnUI("Client disconnected.");
                        break;
                    }
                }

            } catch (IOException e) {
                addMessageOnUI("Server error: " + e.getMessage());
            } finally {
                closeConnections();
            }
        }).start();

        button_send.setOnAction(event -> {
            try {
                String msg = tf_message.getText();
                if (dataOutputStream != null && !msg.isEmpty()) {
                    dataOutputStream.writeUTF(msg);
                    addMessage("You: " + msg);
                    tf_message.clear();
                } else {
                    addMessage("Cannot send: No client connected.");
                }
            } catch (IOException e) {
                addMessage("Error sending message: " + e.getMessage());
            }
        });
    }

    private void addMessageOnUI(String msg) {
        Platform.runLater(() -> addMessage(msg));
    }

    private void addMessage(String text) {
        Label label = new Label(text);
        vbox_messages.getChildren().add(label);
    }

    private void closeConnections() {
        try {
            if (dataInputStream != null) dataInputStream.close();
            if (dataOutputStream != null) dataOutputStream.close();
            if (clientSocket != null) clientSocket.close();
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

