package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by snels on 25.11.2016.
 */
public class LoginController implements Initializable {

    private Parent parentChat;
    private FXMLLoader fxmlLoader = new FXMLLoader();
    private MainController mainController;
    private Stage loginStage;
    private boolean isConnected;
    private Connection connection;
    private List<String> parameters;

    @FXML private TextField login;
    @FXML private TextField host;
    @FXML private TextField port;
    @FXML private Label errorLabel;
    @FXML private Button enterButton;
    @FXML private AnchorPane parentLogin;

    public void setMainStage(Stage loginStage) {
        this.loginStage = loginStage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        System.out.println(parameters.size());
    }

    @FXML
    private void logIn(ActionEvent event) throws IOException, InterruptedException {
        connection = new Connection();
        fxmlLoader.setLocation(getClass().getResource("/views/MainWindow.fxml"));
        fxmlLoader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> controllerClass) {
                if (controllerClass == MainController.class) {
                    mainController = new MainController();
                    mainController.setLogin(login.getText());
                    mainController.setConnection(connection);
                    mainController.setMainController(mainController);
                    connection.setMainController(mainController);

                    return mainController;
                } else {
                    try {
                        return controllerClass.newInstance();
                    } catch (Exception exc) {
                        throw new RuntimeException(exc); // just bail
                    }
                }
            }
        });

            connection.startConnection(host.getText(), Integer.parseInt(port.getText()), login.getText());
            if (connection.isHostError()) {
                setErrorLabel("Сервер недоступен");
            } else if (connection.isLoginError()) {
                setErrorLabel("Логин используется");
            } else if (login.getText().length() < 4) {
                errorLabel.wrapTextProperty().setValue(true);
                setErrorLabel("Логин должен содержать не менее 4-х символов");
            }else {
                isConnected = true;
                ((Node) event.getSource()).getScene().getWindow().hide();
                parentChat = fxmlLoader.load();
                Stage stage = new Stage();
                Scene scene = new Scene(parentChat, 800, 550);
                stage.setScene(scene);
                stage.setMinHeight(550);
                stage.setMinWidth(800);
                stage.setTitle("SimpleFX Chat");
                //stage.initStyle(StageStyle.UNDECORATED);
                stage.show();
            }
    }

    @FXML
    private void closeWindow(ActionEvent event) {
        loginStage = (Stage) parentLogin.getScene().getWindow();
        loginStage.close();
    }

    private void setErrorLabel(String error) {
        errorLabel.setText(error);
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
        if (parameters.size() == 3) {
            login.setText(parameters.get(0));
            host.setText(parameters.get(1));
            port.setText(parameters.get(2));

        }
    }

}