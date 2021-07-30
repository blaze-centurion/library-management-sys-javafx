package Library.Controller.AdminPanel;

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

public class AdminPanelController extends Panel implements Initializable {

    private final Utils util = new Utils();
    Users user;
    @FXML
    private BorderPane userPanel;
    @FXML
    private VBox sidebar_items_container;
    @FXML
    private HBox logoBx;
    @FXML
    private Label panelNameLabel;
    @FXML
    private Circle profileAvatar;
    @FXML
    private HBox adDashboardItem;
    @FXML
    private HBox adBookItem;
    @FXML
    private HBox adHisItem;
    @FXML
    private HBox adCatItem;
    @FXML
    private HBox adUserItem;
    @FXML
    private HBox dashboardItem;
    @FXML
    private HBox bookItem;

    @Override
    public void setUserId(int userId) {
        this.userId = userId;
        user = configureUser(profileAvatar);
        util.loadPanel("../fxml/Librarian/Dashboard.fxml", userPanel, panelNameLabel, "DashBoard", user, sidebar_items_container);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        adDashboardItem.setOnMouseClicked(event -> switchPanel("../fxml/Librarian/Dashboard.fxml", event, "DashBoard"));
        adHisItem.setOnMouseClicked(event -> switchPanel("../fxml/Librarian/History.fxml", event, "History"));
        adBookItem.setOnMouseClicked(event -> switchPanel("../fxml/Librarian/Books.fxml", event, "Books"));
        adCatItem.setOnMouseClicked(event -> switchPanel("../fxml/Librarian/Category.fxml", event, "Category"));
        adUserItem.setOnMouseClicked(event -> switchPanel("../fxml/Librarian/Users.fxml", event, "Users"));
        dashboardItem.setOnMouseClicked(event -> switchPanel("../fxml/User/Dashboard.fxml", event, "DashBoard"));
        bookItem.setOnMouseClicked(event -> switchPanel("../fxml/User/Books.fxml", event, "Books"));
    }

    private void switchPanel(String path, Event event, String labelTxt) {
        util.toggleActive(sidebar_items_container, (Node) event.getSource());
        util.loadPanel(path, userPanel, panelNameLabel, labelTxt, user, sidebar_items_container);
    }

    public void toggleSidebar() {
        util.toggleSidebar(sidebar_items_container, logoBx);
    }

}