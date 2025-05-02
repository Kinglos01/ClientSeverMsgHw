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
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientView implements Initializable {

    @FXML
    private VBox vbox_messages;

    @FXML
    private TextField tf_message;

    @FXML
    private Button button_send;

    Socket socket;
    DataInputStream datainputstream;
    DataOutputStream dataOutputStream;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {

            socket = new Socket("localhost", 6666);
            datainputstream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());


            new Thread(() -> {
                try {
                    while (true) {
                        String message = datainputstream.readUTF();
                        Platform.runLater(() -> addMessage("Server: " + message));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();


            button_send.setOnAction(event -> {
                try {
                    String msg = tf_message.getText();
                    dataOutputStream.writeUTF(msg);
                    addMessage("You: " + msg);
                    tf_message.clear();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void addMessage(String text) {
        Label label = new Label(text);
        vbox_messages.getChildren().add(label);
    }
}

