package Library.Controller.Form;

import Library.Utils.DataBase;
import Library.Utils.Utils;
import com.jfoenix.controls.JFXComboBox;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpController implements Initializable {

    private final DataBase db = new DataBase();
    private final Utils util = new Utils();
    @FXML
    private TextField tfUserName;
    @FXML
    private TextField tfEmail;
    @FXML
    private TextField tfPassword;
    @FXML
    private TextField tfCode;
    @FXML
    private JFXComboBox<String> cbRole;
    @FXML
    private Label errorLabel;
    @FXML
    private VBox signupContainer;
    private VBox formContainer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        HBox hBox = (HBox) signupContainer.lookup(".tf_hidden");
        signupContainer.getChildren().remove(hBox);

        for (String item : util.roleArr) {
            cbRole.getItems().add(item);
        }

        cbRole.valueProperty().addListener((observableValue, oldVal, newVal) -> {
            if (!newVal.equals(util.roleArr[0])) {
                if (signupContainer.lookup(".tf_hidden") == null) {
                    signupContainer.getChildren().add(5, hBox);
                }
            } else {
                signupContainer.getChildren().remove(hBox);
            }
        });

    }

    public void setFormContainer(VBox formContainer) {
        this.formContainer = formContainer;
    }

    private boolean isEmail(String email) {
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}";
        // Compile regular expression to get the pattern
        Pattern pattern = Pattern.compile(regex);
        // Create instance of matcher
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void checkCodeAndRegister(int role, String code) {
        if (tfCode.getText().equals(code)) {
            register(role);
        } else {
            util.showError(errorLabel, "Your code is incorrect.");
        }
    }

    public void signup(Event event) {
        /*
         * Role reference: Indexes of element in utils.roleArr.
         * Users - 0
         * Librarian - 1
         * Admin - 2
         */
        try {
            if (tfUserName.getText().length() != 0 && tfEmail.getText().length() != 0 && tfPassword.getText().length() != 0) {
                if (isEmail(tfEmail.getText())) {
                    int role = util.getRoleNo(cbRole.getSelectionModel().getSelectedItem());
                    /*
                     * Check if role is user or something else.
                     * If role != 0, then we'll check whether the tfCode label is empty or not.
                     * If that's not empty then we'll match the code according to it's role.
                     */
                    if (role != 0) {
                        if (tfCode.getText().length() != 0) {
                            if (role == 1) {
                                checkCodeAndRegister(role, util.LibrarianCode);
                            } else {
                                checkCodeAndRegister(role, util.AdminCode);
                            }
                        } else {
                            util.showError(errorLabel, "Please fill all fields.");
                        }
                    } else {
                        register(role);
                    }
                } else {
                    util.showError(errorLabel, "Invalid email.");
                }
            } else {
                util.showError(errorLabel, "Please fill all fields.");
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    private void register(int role) {
        /*
         * 1) Hashing the password.
         * 2) Creating random userId.
         * 3) Executing query to insert user into db.
         */

        String hashedPwd = util.hashPassword(tfPassword.getText());
        int userId = util.getRandomId();
        String query = String.format("INSERT INTO `users` (userid, username, email, password, role, userProfile, joinon) VALUES (%d, '%s', '%s', '%s', %s, '%s', '%s' )", userId, tfUserName.getText().trim(), tfEmail.getText().trim(), hashedPwd, role, util.defaultProfileImg, LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        try {
            db.executeUpdateQuery(query);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        util.loadForm("../fxml/Form/SignIn.fxml", this.formContainer, this.formContainer.getLayoutX() * 19);
    }
}
