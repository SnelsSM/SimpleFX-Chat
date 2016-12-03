package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;


/**
 * Created by snels on 30.11.2016.
 */
public class UserCell extends ListCell<String>{

    @FXML private AnchorPane anchorPane;
    @FXML private Label label;
    private FXMLLoader mLLoader;

    @Override
    protected void updateItem(String user, boolean empty) {
        super.updateItem(user, empty);

        if(empty || user == null) {

            setText(null);
            setGraphic(null);

        } else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("/views/UserCell.fxml"));
                mLLoader.setController(this);

                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            label.setText(user);

            setText(null);
            setGraphic(anchorPane);
        }

    }
}
