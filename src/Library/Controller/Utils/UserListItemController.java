package Library.Controller.Utils;

import Library.Utils.Users;
import Library.Utils.Utils;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class UserListItemController {

    @FXML
    private Label userName;

    @FXML
    private Label userId;

    @FXML
    private Label role;

    @FXML
    private Label email;

    @FXML
    private JFXButton viewBtn;

    @FXML
    private Label joinedOn;

    public void setData(Users user) {
        Utils utils = new Utils();
        userName.setText(user.getUsername());
        email.setText("Email: " + user.getEmail());
        userId.setText(String.valueOf(user.getUserid()));
        role.setText(utils.roleArr[user.getRole()]);
        joinedOn.setText("Joined On: " + user.getJoinon());
//        viewBtn.setOnAction(event -> utils.openModalBox());

    }
}
