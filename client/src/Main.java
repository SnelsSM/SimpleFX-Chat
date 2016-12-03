import controllers.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("views/LoginWindow.fxml"));

        Parent fxmlMain = fxmlLoader.load();
        LoginController controller = fxmlLoader.getController();
        controller.setMainStage(primaryStage);

        primaryStage.setTitle("Hello World");
        Scene mainScene = new Scene(fxmlMain);
        mainScene.setRoot(fxmlMain);
        primaryStage.setScene(mainScene);
        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
