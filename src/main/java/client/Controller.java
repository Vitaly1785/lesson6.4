package client;


import FileData.FileReadWrite;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private TextArea textArea;
    @FXML
    private TextField textField;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private HBox authPanel;
    @FXML
    private HBox msgPanel;
    @FXML
    private ListView<String> clientList;
    @FXML
    private TextField nicknameField;

    private Socket socket;
    private final String IP_ADRESS = "localhost";
    private final int PORT = 8190;

    private DataInputStream in;
    private DataOutputStream out;

    private boolean authenticated;
    private String nickname;
    private Stage stage;
    private Stage regStage;
    private RegController regController;

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
        msgPanel.setManaged(authenticated);
        msgPanel.setVisible(authenticated);
        authPanel.setManaged(!authenticated);
        authPanel.setVisible(!authenticated);
        clientList.setManaged(authenticated);
        clientList.setVisible(authenticated);
        if (!authenticated) {
            nickname = "";
        }
        setTitle(nickname);
        textArea.clear();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createRegWindow();
        Platform.runLater(() -> {
            stage = (Stage) textField.getScene().getWindow();
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    System.out.println("bye");
                    if (socket != null && !socket.isClosed()) {
                        try {
                            out.writeUTF("/end");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        });
        setAuthenticated(false);
    }

    private void connect() {
        try {
            socket = new Socket(IP_ADRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    // цикл аутентификации
                    while (true) {
                        String str = in.readUTF();

                        if (str.startsWith("/")) {
                            //
                            if (str.equals("/end")) {
                                textArea.setText("Socket disconnect");
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                break;
                            }

                            //
                            if (str.equals("/regOk")) {
                                regController.addMessage("Registration done!");
                            }
                            if (str.equals("/regNo")) {
                                regController.addMessage("Registration failed!\n" + "Login & Nickname may already be used");
                            }
                            if (str.startsWith("/authOk ")) {
                                nickname = str.split("\\s")[1];
                                setAuthenticated(true);
                                break;
                            }
                        } else {
                            textArea.appendText(str + "\n");

                        }
                    }


                        try {
                            FileReadWrite newFile = new FileReadWrite();
                            String[] strings = newFile.readFileTxt(newFile.allChatMessage());
                            for (int i = 0; i < strings.length; i++) {
                                textArea.appendText(strings[i] + "\n");
                            }
                        }
                        catch (Exception e) {
                            throw new RuntimeException("SWW", e);
                        }

                    // цикл работы
                    while (true) {
                        String str = in.readUTF();

                        if (str.startsWith("/")) {
                            if (str.startsWith("/clientList ")) {
                                String[] token = str.split("\\s");
                                Platform.runLater(() -> {
                                    clientList.getItems().clear();
                                    for (int i = 1; i < token.length; i++) {
                                        clientList.getItems().add(token[i]);
                                    }
                                });
                            }

                            if (str.startsWith("/changeNick ")) {
                                nickname = str.split("\\s")[1];
                            }

                            if (str.equals("/end")) {
                                break;
                            }
                        } else {
                            textArea.appendText(str + "\n");
                        }
                    }
                } catch (EOFException e){
                    System.out.println("End");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    setAuthenticated(false);
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void clickBtn() {
        try {
            out.writeUTF(textField.getText());
            textField.clear();
            textField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void tryToAuth(ActionEvent actionEvent) {
        if (socket == null || socket.isClosed()) {
            connect();
        }
        String msg = String.format("/auth %s %s", loginField.getText().trim(), passwordField.getText().trim());
        try {
            out.writeUTF(msg);
            passwordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setTitle(String username) {
        String title = String.format("SpaceChat [ %s ]", username);
        if (username.equals("")) {
            title = "SpaceChat";
        }
        String chatTitle = title;
        Platform.runLater(() -> {
            stage.setTitle(chatTitle);
        });
    }

    @FXML
    public void touchEnter(ActionEvent actionEvent) {
        passwordField.requestFocus();
    }

    @FXML
    public void clickClientList(MouseEvent mouseEvent) {
        String msg = String.format("/w %s ", clientList.getSelectionModel().getSelectedItem());
        textField.setText(msg);
    }

    private void createRegWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("reg.fxml"));
            Parent root = fxmlLoader.load();
            regStage = new Stage();
            regStage.setTitle("SpaceChat Registration");
            regStage.setScene(new Scene(root, 350, 300));
            regStage.initModality(Modality.APPLICATION_MODAL);
            regController = fxmlLoader.getController();
            regController.setController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showRegWindow(ActionEvent actionEvent) {
        regStage.show();
    }

    public boolean tryToReg(String login, String password, String nickname)  {
        String msg = String.format("/reg %s %s %s", login, password, nickname);

        if (socket == null || socket.isClosed()) {
            connect();
        }
        try{
            out.writeUTF(msg);
        } catch (IOException e){
            e.printStackTrace();
        }
        return true;
    }
}
