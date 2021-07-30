package Library.Controller.Form;

import Library.BCrypt;
import Library.Controller.Panel;
import Library.Utils.DataBase;
import Library.Utils.Utils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class SignInController implements Initializable {

    @FXML
    private Label errorLabel;

    @FXML
    private TextField tfEmail;

    @FXML
    private TextField tfPassword;

    private void showNewStage(Stage oldStage, String path, ResultSet rs) throws Exception {
        // Creating a new loader and parent node as well as its respective controller.
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        Parent root = loader.load();
        Panel panel = loader.getController();
        panel.setUserId(rs.getInt("userid"));

        // Creating new stage where root node will be displayed.
        Stage newStage = new Stage();
        oldStage.close();
        Scene scene = new Scene(root);
        newStage.initStyle(StageStyle.DECORATED);
        newStage.setScene(scene);
        newStage.show();
        newStage.setOnCloseRequest(windowEvent -> {
            Platform.exit();
            System.exit(-1);
        });
    }

    public void signIn(ActionEvent event) {
        DataBase db = new DataBase();
        Utils util = new Utils();
        Connection conn = db.getConnection();
        Statement st;
        ResultSet rs;
        try {
            if (tfEmail.getText().length() != 0 && tfPassword.getText().length() != 0) {
                st = conn.createStatement();
                rs = st.executeQuery("SELECT * FROM `users` WHERE email = '" + tfEmail.getText() + "'");

                // Check whether the email is exist or not.
                if (rs.next()) {
                    // Check if the password is matching.
                    if (BCrypt.checkpw(tfPassword.getText(), rs.getString("password"))) {

                        // Check the role and display stages according to that.
                        Stage oldStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        if (rs.getInt("role") == 0) {
                            showNewStage(oldStage, "../../fxml/User/UserPanel.fxml", rs);
                        } else if (rs.getInt("role") == 1) {
                            showNewStage(oldStage, "../../fxml/Librarian/LibrarianPanel.fxml", rs);
                        } else {
                            showNewStage(oldStage, "../../fxml/Admin/AdminPanel.fxml", rs);
                        }
                    } else {
                        // Show error if password is invalid.
                        util.showError(errorLabel, "Invalid Credentials.");
                    }
                } else {
                    // Show error if email is invalid.
                    util.showError(errorLabel, "Invalid Credentials.");
                }
            } else {
                // Show error if fields are empty.
                util.showError(errorLabel, "Please fill all fields.");
            }
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tfEmail.setText("librarian@mail.com");
        tfPassword.setText("admin");
    }
}
