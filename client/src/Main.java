import controllers.Connection;
import controllers.LoginController;
import controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.List;


public class Main extends Application {



    @Override
    public void start(Stage primaryStage) throws Exception{

        final Parameters params = getParameters();
        final List<String> parameters = params.getRaw();

        FXMLLoader fxmlLoader = new FXMLLoader();
        if (parameters.size() == 3) {
            Connection connection = new Connection();
            connection.startConnection(parameters.get(1), Integer.parseInt(parameters.get(2)), parameters.get(0));
            if (connection.isHostError()) {
                System.out.println("Сервер недоступен");
            } else if (connection.isLoginError()) {
                System.out.println("Логин используется");
            } else if (parameters.get(0).length() < 4) {
                System.out.println("Логин должен содержать не менее 4-х символов");
            } else {
                fxmlLoader.setLocation(getClass().getResource("views/MainWindow.fxml"));

                fxmlLoader.setControllerFactory(new Callback<Class<?>, Object>() {
                    @Override
                    public Object call(Class<?> controllerClass) {
                        if (controllerClass == MainController.class) {
                            MainController mainController = new MainController();
                            mainController.setLogin(parameters.get(0));
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

                Parent fxmlMain = fxmlLoader.load();
                MainController controller = fxmlLoader.getController();
                controller.setMainController(controller);
                controller.setLogin(parameters.get(0));
                controller.setConnection(connection);
                Scene mainScene = new Scene(fxmlMain);
                mainScene.setRoot(fxmlMain);
                primaryStage.setTitle("SimpleFX Chat");
                primaryStage.setScene(mainScene);
                primaryStage.show();
            }
        } else {

            fxmlLoader.setLocation(getClass().getResource("views/LoginWindow.fxml"));
            Parent fxmlMain = fxmlLoader.load();
            LoginController controller = fxmlLoader.getController();
            controller.setMainStage(primaryStage);
            primaryStage.setTitle("SimpleFX Chat Login");
            Scene mainScene = new Scene(fxmlMain);
            mainScene.setRoot(fxmlMain);
            primaryStage.setScene(mainScene);
            primaryStage.setResizable(false);
            primaryStage.show();
        }


    }


    public static void main(String[] args) {
        launch(args);
    }
}
