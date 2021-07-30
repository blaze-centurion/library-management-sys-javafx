package Library.Controller.Form;

import Library.Utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class FormController implements Initializable {
    Utils util = new Utils();
    FXMLLoader loader;
    SignUpController signUpController;
    @FXML
    private VBox formContainer;
    private Parent fxml;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("../../fxml/Form/SignUp.fxml")));
            fxml = loader.load();
            signUpController = loader.getController();
            formContainer.getChildren().removeAll();
            formContainer.getChildren().setAll(fxml);
            signUpController.setFormContainer(formContainer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void open_signIn() {
        util.loadForm("../fxml/Form/SignIn.fxml", formContainer, formContainer.getLayoutX() * 19);
    }

    public void open_signUp() {
        util.loadForm("../fxml/Form/SignUp.fxml", formContainer, 0);
    }

    public void exit(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
