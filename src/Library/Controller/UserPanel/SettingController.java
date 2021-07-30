package Library.Controller.UserPanel;

import Library.Controller.Panel;
import Library.Utils.DataBase;
import Library.Utils.Users;
import Library.Utils.Utils;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

import java.io.File;


public class SettingController extends Panel {

    Utils util = new Utils();
    DataBase db = new DataBase();
    Users user;
    @FXML
    private Circle profileAvatar;
    @FXML
    private JFXButton changeProfileBtn;
    @FXML
    private TextField tfUserName;
    @FXML
    private TextField tfEmail;
    @FXML
    private TextField tfJoinedOn;
    @FXML
    private JFXComboBox<String> cbRole;
    @FXML
    private TextField tfLibCode;
    @FXML
    private Label userIdLabel;
    @FXML
    private Label errorLabel;
    @FXML
    private VBox settingTfBox;
    @FXML
    private JFXButton saveBtn;
    private boolean isRoleChanged = false;
    private boolean isChangedAnything = false;
    private String role;


    @Override
    public void setUser(Users user) {
        this.user = user;
        role = util.roleArr[user.getRole()];
        configureLibCode();
        for (String item : util.roleArr) {
            cbRole.getItems().add(item);
        }

        changeProfileBtn.setOnAction(event -> changeProfile());

        // Configuring the panel.
        setProfile(user.getUserProfile());
        tfUserName.setText(user.getUsername());
        tfEmail.setText(user.getEmail());
        tfJoinedOn.setText(user.getJoinon());
        userIdLabel.setText(String.valueOf(user.getUserid()));
        cbRole.getSelectionModel().select(user.getRole());
        saveBtn.setOnAction(event -> saveUserDetail());
        if (user.getRole() == util.getRoleNo("Librarian")) {
            tfLibCode.setEditable(false);
            tfLibCode.setText(util.LibrarianCode);
        }
    }

    public void setProfile(String path) {
        Image img = new Image(path);
        profileAvatar.setFill(new ImagePattern(img));
    }

    private void changeProfile() {
        FileChooser chooser = new FileChooser();
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            try {
                String filePath = util.copyFile(file);
                if (!user.getUserProfile().equals(util.defaultProfileImg)) {
                    File file1 = new File("src/" + user.getUserProfile());
                    file1.delete();
                }
                String query = "UPDATE users SET userProfile = '" + filePath + "' WHERE userid = " + user.getUserid();
                db.executeUpdateQuery(query);
                configureUser(profileAvatar);
                util.showSuccessAndDisappear(errorLabel, "Profile Changed. Please restart the application to see changes.", 5);
            } catch (Exception e) {
                e.printStackTrace();
                util.showErrorAndDisappear(errorLabel, "Something went wrong. Please try again.", 4);
            }
        }

    }

    private void saveUserDetail() {
        try {
            isChangedAnything = isRoleChanged || !tfUserName.getText().trim().equals(user.getUsername());
            if (isChangedAnything) {
                if (!cbRole.getSelectionModel().getSelectedItem().equals(util.roleArr[user.getRole()])) {
                    if (tfLibCode.getText().length() != 0) {
                        if (!tfLibCode.getText().equals(util.LibrarianCode)) return;
                    } else {
                        return;
                    }
                }
                String query = "UPDATE users SET username = '" + tfUserName.getText().trim() + "', role = " + util.getRoleNo(role) + " WHERE userid = " + user.getUserid() + ";";
                db.executeUpdateQuery(query);
                configureUser(profileAvatar);
                util.showSuccessAndDisappear(errorLabel, "User Details Updated. Please restart the application to see changes.", 5);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void configureLibCode() {
        HBox hBox = (HBox) settingTfBox.lookup("#libCode");
        settingTfBox.getChildren().remove(hBox);
        cbRole.valueProperty().addListener((observableValue, oldVal, newVal) -> {
            isRoleChanged = !newVal.equals(role);
            if (!newVal.equals(util.roleArr[0])) {
                if (settingTfBox.lookup("#libCode") == null) {
                    settingTfBox.getChildren().add(2, hBox);
                }
            } else {
                settingTfBox.getChildren().remove(hBox);
            }
        });

    }

    @Override
    public void setUserId(int userId) {
        this.userId = userId;
    }

}
