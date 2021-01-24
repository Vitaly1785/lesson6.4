package client;

import connect.ConnectionService;
import connect.SpaceRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class RegController {
    @FXML
    private TextField loginField;
    @FXML
    private TextField nicknameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextArea textArea;

    private Controller controller;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @FXML
    public void touchEnter(ActionEvent actionEvent) {
        passwordField.requestFocus();
    }

    @FXML
    public void touchEnter1(ActionEvent actionEvent) {
        nicknameField.requestFocus();
    }

    @FXML
    public void tryToReg(ActionEvent actionEvent) {
        SpaceRepository repository = new SpaceRepository();

        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();
        String nickname = nicknameField.getText().trim();
        controller.tryToReg(login, password, nickname);

      //  repository.createUsers(login,password,nickname);
    }

    public void addMessage(String msg) {
        textArea.appendText(msg + "\n");
    }
}
