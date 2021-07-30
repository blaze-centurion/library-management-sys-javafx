package Library.Controller.LibrarianPanel;

import Library.Controller.Panel;
import Library.Utils.DataBase;
import Library.Utils.Users;
import Library.Utils.Utils;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class UserController extends Panel {

    DataBase db = new DataBase();
    ProgressIndicator indicator;
    Utils util = new Utils();
    String initQuery = "SELECT * FROM users WHERE role<>2";
    String[] searchSortArr = {"User Name", "Email", "User Id"};
    String[] colArr = {"username", "email", "userid"};
    @FXML
    private JFXComboBox<String> sortBx;
    @FXML
    private StackPane stackPane;
    @FXML
    private TableView<Users> tableView;
    @FXML
    private TableColumn<Users, String> tcSno;
    @FXML
    private TableColumn<Users, String> tcUserName;
    @FXML
    private TableColumn<Users, String> tcEmail;
    @FXML
    private TableColumn<Users, String> tcRole;
    @FXML
    private TableColumn<Users, String> tcJoinedOn;
    @FXML
    private TableColumn<Users, Integer> tcIssuedBook;
    @FXML
    private TableColumn<Users, Integer> tcTotalFine;
    @FXML
    private TableColumn<Users, String> tcView;
    @FXML
    private TextField searchBar;
    @FXML
    private Button searchBtn;

    @Override
    public void setUser(Users user) {
        for (String item : searchSortArr) sortBx.getItems().add(item);
        sortBx.getSelectionModel().select(0);
        indicator = new ProgressIndicator();
        stackPane.getChildren().add(indicator);
        showUsers(initQuery);
        searchBar.setOnAction(this::search);
        searchBtn.setOnAction(this::search);
    }

    private void search(ActionEvent event) {
        if (searchBar.getText().length() != 0)
            showUsers("SELECT * FROM users WHERE " + colArr[sortBx.getSelectionModel().getSelectedIndex()] + " LIKE '%" + searchBar.getText() + "%' AND role <> 2");
        else showUsers(initQuery);
    }

    private void showUsers(String query) {
        Task<ObservableList<Users>> task = new Task<>() {
            @Override
            protected ObservableList<Users> call() {
                return getUserList(query);
            }
        };

        indicator.setVisible(true);
        indicator.progressProperty().bind(task.progressProperty());
        task.setOnSucceeded(e -> {
            ObservableList<Users> list = task.getValue();
            setTableColValueFactory();
            tableView.setItems(list);
            util.setTableViewDim(tableView);
            indicator.progressProperty().unbind();
            indicator.setVisible(false);
        });
        util.createExecutor().execute(task);
    }

    private void setTableColValueFactory() {
        // Setting all cell factory and cell value factory.
        tcSno.setCellValueFactory(new PropertyValueFactory<>("userid"));
        tcUserName.setCellValueFactory(new PropertyValueFactory<>("username"));
        tcEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tcJoinedOn.setCellValueFactory(new PropertyValueFactory<>("joinon"));
        tcRole.setCellValueFactory(new PropertyValueFactory<>("roleName"));
        tcTotalFine.setCellValueFactory(new PropertyValueFactory<>("totalFine"));
        tcIssuedBook.setCellValueFactory(new PropertyValueFactory<>("issuedBook"));

        // These method will set button in operation col and image view in book image col. By setting node graphics.
        tcView.setCellFactory(getOperCellFactory());
    }

    private ObservableList<Users> getUserList(String query) {
        ObservableList<Users> userList = FXCollections.observableArrayList();
        Connection conn = db.getConnection();
        Statement st;
        ResultSet rs;

        try {
            st = conn.createStatement();
            rs = st.executeQuery(query);
            Users user;
            while (rs.next()) {
                user = new Users(rs.getInt("userid"), rs.getString("username"), rs.getString("email"), rs.getInt("role"), rs.getString("userProfile"), rs.getInt("totalBooks"), rs.getInt("totalFine"), rs.getInt("issuedBook"), rs.getString("joinon"));
                user.setRoleName();
                userList.add(user);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return userList;
    }

    private Callback<TableColumn<Users, String>, TableCell<Users, String>> getOperCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<Users, String> call(TableColumn<Users, String> booksStringTableColumn) {
                return new TableCell<>() {
                    final JFXButton viewBtn = new JFXButton();

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            configureTableBtn(viewBtn, GlyphsDude.createIcon(FontAwesomeIcons.EYE));
                            setGraphic(viewBtn);
                            setText(null);
                        }
                    }
                };
            }
        };
    }

    private void configureTableBtn(JFXButton btn, Text icon) {
        btn.setGraphic(icon);
        btn.getStyleClass().add("dashboard_blue_btn");
        btn.setStyle("-fx-background-color: #0dff55;");
    }

    public void refresh() {
        showUsers(initQuery);
        tableView.refresh();
    }

    @Override
    public void setUserId(int userId) {
        this.userId = userId;
    }
}
