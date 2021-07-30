package Library.Controller.UserPanel;


import Library.Controller.Panel;
import Library.Utils.Users;
import Library.Utils.Utils;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.util.ResourceBundle;

public class UserPanelController extends Panel implements Initializable {
    Utils util = new Utils();
    Users user;
    @FXML
    private VBox sidebar_items_container;

    @FXML
    private Circle profileAvatar;

    @FXML
    private HBox logoBx;

    @FXML
    private BorderPane userPanel;

    @FXML
    private Label panelNameLabel;

    @FXML
    private HBox dashboardItem;

    @FXML
    private HBox bookItem;

    @FXML
    private HBox historyItem;

    @FXML
    private HBox settingItem;

    @Override
    public void setUserId(int userId) {
        this.userId = userId;
        user = configureUser(profileAvatar);
        util.loadPanel("../fxml/User/Dashboard.fxml", userPanel, panelNameLabel, "DashBoard", user, sidebar_items_container);
    }

    public void toggleSidebar() {
        util.toggleSidebar(sidebar_items_container, logoBx);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboardItem.setOnMouseClicked(event -> switchPanel("../fxml/User/Dashboard.fxml", event, "DashBoard"));
        historyItem.setOnMouseClicked(event -> switchPanel("../fxml/User/History.fxml", event, "History"));
        bookItem.setOnMouseClicked(event -> switchPanel("../fxml/User/Books.fxml", event, "Books"));
        settingItem.setOnMouseClicked(event -> switchPanel("../fxml/User/Setting.fxml", event, "Setting"));
    }

    private void switchPanel(String path, Event event, String labelText) {
        util.toggleActive(sidebar_items_container, (Node) event.getSource());
        util.loadPanel(path, userPanel, panelNameLabel, labelText, user, sidebar_items_container);
    }
}
