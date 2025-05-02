package org.example.clientsevermsgexample;



import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.ResourceBundle;

import static java.lang.Thread.sleep;

public class MainController implements Initializable {
    @FXML
    private ComboBox dropdownPort;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dropdownPort.getItems().addAll("7",     // ping
                "13",     // daytime
                "21",     // ftp
                "23",     // telnet
                "71",     // finger
                "80",     // http
                "119",     // nntp (news)
                "161"      // snmp);
        );
    }

    @FXML
    private Button clearBtn;



    @FXML
    private TextArea resultArea;

    @FXML
    private Label server_lbl;

    @FXML
    private Button testBtn;

    @FXML
    private Label test_lbl;

    @FXML
    private TextField urlName;

    Socket socket1;

    Label lb122, lb12;
    TextField msgText;

    @FXML
    void checkConnection(ActionEvent event) {

        String host = urlName.getText();
        int port = Integer.parseInt(dropdownPort.getValue().toString());

        try {
            Socket sock = new Socket(host, port);
            resultArea.appendText(host + " listening on port " + port + "\n");
            sock.close();
        } catch (UnknownHostException e) {
            resultArea.setText(String.valueOf(e) + "\n");
            return;
        } catch (Exception e) {
            resultArea.appendText(host + " not listening on port "
                    + port + "\n");
        }


    }


    @FXML
    void clearBtn(ActionEvent event) {
        resultArea.setText("");
        urlName.setText("");

    }



    @FXML
    void startServer(ActionEvent event) {
        Stage stage = new Stage();
        Group root = new Group();
        Label lb11 = new Label("Server");
        lb11.setLayoutX(100);
        lb11.setLayoutY(100);

        lb12 = new Label("info");
        lb12.setLayoutX(100);
        lb12.setLayoutY(200);
        root.getChildren().addAll(lb11, lb12);
        Scene scene = new Scene(root, 600, 350);
        stage.setScene(scene);
        lb12.setText("Server is running and waiting for a client...");

        stage.setTitle("Server");
        stage.show();


        new Thread(this::runServer).start();

    }

    String message;

    private void runServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(6666);
            updateServer("Server is running and waiting for a client...");

            Socket clientSocket = serverSocket.accept(); // accept one client
            updateServer("Client connected!");

            DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());

            while (true) {
                try {
                    String message = dis.readUTF();
                    updateServer("Message from client: " + message);

                    dos.writeUTF("Received: " + message);

                    if (message.equalsIgnoreCase("exit")) break;
                } catch (IOException e) {
                    updateServer("Connection lost: " + e.getMessage());
                    break;
                }
            }

            dis.close();
            dos.close();
            clientSocket.close();
            serverSocket.close();

        } catch (IOException e) {
            updateServer("Error: " + e.getMessage());
        }
    }

    private void updateServer(String message) {
        // Run on the UI thread
        javafx.application.Platform.runLater(() -> lb12.setText(message + "\n"));
    }


    @FXML
    void startClient(ActionEvent event) {
        Stage stage = new Stage();
        Group root = new Group();
        Button connectButton = new Button("Connect to server");
        connectButton.setLayoutX(100);
        connectButton.setLayoutY(300);
        connectButton.setOnAction(this::connectToServer);
        //new Thread(this::connectToServer).start();

        Label lb11 = new Label("Client");
        lb11.setLayoutX(100);
        lb11.setLayoutY(100);
        msgText = new TextField("msg");
        msgText.setLayoutX(100);
        msgText.setLayoutY(150);

        lb122 = new Label("info");
        lb122.setLayoutX(100);
        lb122.setLayoutY(200);
        root.getChildren().addAll(lb11, lb122, connectButton, msgText);


        Scene scene = new Scene(root, 600, 350);
        stage.setScene(scene);
        stage.setTitle("Client");
        stage.show();


    }


    private void connectToServer(ActionEvent event) {


        try {
            socket1 = new Socket("localhost", 6666);

            DataOutputStream dos = new DataOutputStream(socket1.getOutputStream());
            DataInputStream dis = new DataInputStream(socket1.getInputStream());

            dos.writeUTF(msgText.getText());
            String response = dis.readUTF();
            updateTextClient("Server response: " + response + "\n");

            dis.close();
            dos.close();
            socket1.close();
        } catch (Exception e) {
            updateTextClient("Error: " + e.getMessage() + "\n");
        }


    }

    private void updateTextClient(String message) {
        javafx.application.Platform.runLater(() -> lb122.setText(message + "\n"));
    }

    @FXML
    private void openClientChat(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/org/example/clientsevermsgexample/client-view.fxml")));
            Stage stage = new Stage();
            stage.setTitle("User 1 (Client)");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openServerChat(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/org/example/clientsevermsgexample/server-view.fxml")));
            Stage stage = new Stage();
            stage.setTitle("User 2 (Server)");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
