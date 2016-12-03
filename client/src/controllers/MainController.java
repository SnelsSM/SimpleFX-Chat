package controllers;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import messages.Message;
import messages.MessageHistory;
import methods.Audio;
import netscape.javascript.JSObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Scanner;

public class MainController implements Initializable{

    @FXML private WebView listMessages;
    @FXML private ListView listUsers;
    @FXML private TextField textField;
    @FXML private AnchorPane root;

    private double xOffset;
    private double yOffset;
    private Stage stage;
    private boolean loaded;
    private MainController mainController;

    private String login;
    private controllers.Connection connection;
    StringBuilder messageTpl = new StringBuilder();
    StringBuilder messageMinTpl = new StringBuilder();

    public void setMainStage(Stage stage) {
        this.stage = stage;
    }
    public void setMainController(MainController mainController) { this.mainController = mainController; }

    @Override
    public void initialize(URL location, ResourceBundle resources){

        setMessageTpl();
        URL url = getClass().getResource("/chatArea/chatIndex.html");
        listMessages.getEngine().load(url.toExternalForm());
        listMessages.setContextMenuEnabled(false);

        Audio audio = new Audio("sent");
        //listMessages.setItems(connection.getMessages()); // Заполняем listView
        listUsers.setItems(connection.getUsersOnline());
        listUsers.setCellFactory(usersListView -> new UserCell());

        root.setOnMousePressed(event -> {
            xOffset = ((Stage) root.getScene().getWindow()).getX() - event.getScreenX();
            yOffset = ((Stage) root.getScene().getWindow()).getY() - event.getScreenY();
            root.setCursor(Cursor.CLOSED_HAND);
        });

        root.setOnMouseDragged(event -> {
            ((Stage) root.getScene().getWindow()).setX(event.getScreenX() + xOffset);
            ((Stage) root.getScene().getWindow()).setY(event.getScreenY() + yOffset);

        });

        root.setOnMouseReleased(event -> {
            root.setCursor(Cursor.DEFAULT);
        });
    }


    @FXML
    private void closeWindow(ActionEvent event) {
        stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void sendMessage(ActionEvent event) {
        if (!textField.getText().equals("")) {
            try {
                connection.sendMessage(login, textField.getText());
                new Audio("sent");
                textField.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void  setListMessages(String message) {
        if (loaded) listMessages.getEngine().executeScript(message);
        else {
            listMessages.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
                @Override
                public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
                    if (newValue != Worker.State.SUCCEEDED) { return; }

                    listMessages.getEngine().executeScript(message);
                    JSObject windowObject = (JSObject) listMessages.getEngine().executeScript("window");
                    windowObject.setMember("MainController", mainController);
                    loaded = true;
                }
            });
        }
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void getHistory() {
        connection.historyQuery();
    }

    public void upHistory(MessageHistory messageHistory) {
        String result = "";
        String loginMessageOld = "";
        String temp = "";
        ArrayList<Message> list = messageHistory.getMessages();
        for (Message message : list) {
            if(loginMessageOld.equals(message.getLogin()))
                temp = messageMinTpl.toString();
            else
                temp = messageTpl.toString();

            temp = temp.replace("$_login", message.getLogin());
            temp = temp.replace("$_message", message.getMessage());
            if (login.equals(message.getLogin())) {
                temp = temp.replace("$_css_id", "from_me");
                temp = temp.replace("$_id", "right");
            } else {
                temp = temp.replace("$_css_id", "to_me");
                temp = temp.replace("$_id", "left");
            }
            result += temp;
            loginMessageOld = message.getLogin();
        }

        listMessages.getEngine().executeScript("upHistory('" +result+ "')");
    }

    public void setMessageTpl() {
        try {
            Scanner sMessage = new Scanner(new File("client/src/resources/Message.tpl"));
            Scanner sMessageMin = new Scanner(new File("client/src/resources/MessageMin.tpl"));
            while (sMessage.hasNext())
                messageTpl.append(sMessage.nextLine());
            while (sMessageMin.hasNext())
                messageMinTpl.append(sMessageMin.nextLine());

            sMessage.close();
            sMessageMin.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}