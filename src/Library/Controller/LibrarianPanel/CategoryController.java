package Library.Controller.LibrarianPanel;

import Library.Controller.Panel;
import Library.Utils.Category;
import Library.Utils.DataBase;
import Library.Utils.Users;
import Library.Utils.Utils;
import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class CategoryController extends Panel implements Initializable {

    Users user;
    DataBase db = new DataBase();
    Utils util = new Utils();
    ProgressIndicator indicator;
    @FXML
    private TableView<Category> tableView;
    @FXML
    private TableColumn<Category, Integer> tcCatId;
    @FXML
    private TableColumn<Category, String> tcCatName;
    @FXML
    private TableColumn<Category, Integer> tcTotalBooks;
    @FXML
    private TableColumn<Category, Integer> tcAvailableBooks;
    @FXML
    private TableColumn<Category, Integer> tcPopularity;
    @FXML
    private JFXButton btnAddNewCat;
    @FXML
    private JFXButton btnRemoveCat;
    @FXML
    private StackPane stackPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        indicator = new ProgressIndicator(0);
        stackPane.getChildren().add(indicator);
        btnAddNewCat.setOnAction(event -> util.openModalBox(event, "../fxml/Utils/RemoveBooks.fxml", true, 2));
        btnRemoveCat.setOnAction(event -> util.openModalBox(event, "../fxml/Utils/RemoveBooks.fxml", true, 1));
    }

    @Override
    public void setUser(Users user) {
        this.user = user;
        configureCatPanel();
    }

    private void configureCatPanel() {
        showCat();
    }

    private ObservableList<Category> getCatList() {
        ObservableList<Category> catList = FXCollections.observableArrayList();
        Connection conn = db.getConnection();
        Statement st;
        ResultSet rs;
        try {
            st = conn.createStatement();
            rs = st.executeQuery("SELECT * FROM category");
            Category category;
            while (rs.next()) {
                category = new Category(rs.getInt("catId"), rs.getString("catName"), rs.getInt("totalBook"), rs.getInt("availableBooks"), rs.getInt("popularity"));
                catList.add(category);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return catList;
    }

    private void showCat() {
        Task<ObservableList<Category>> task = new Task<>() {
            @Override
            protected ObservableList<Category> call() {
                return getCatList();
            }
        };
        indicator.setVisible(true);
        indicator.progressProperty().bind(task.progressProperty());
        task.setOnSucceeded(e -> {
            ObservableList<Category> list = task.getValue();
            tcCatId.setCellValueFactory(new PropertyValueFactory<>("catId"));
            tcCatName.setCellValueFactory(new PropertyValueFactory<>("catName"));
            tcTotalBooks.setCellValueFactory(new PropertyValueFactory<>("totalBooks"));
            tcPopularity.setCellValueFactory(new PropertyValueFactory<>("popularity"));
            tcAvailableBooks.setCellValueFactory(new PropertyValueFactory<>("availableBooks"));
            tableView.setItems(list);
            util.setTableViewDim(tableView);
            indicator.progressProperty().unbind();
            indicator.setVisible(false);
        });

        util.createExecutor().execute(task);
    }

    public void refresh() {
        tableView.refresh();
        showCat();
    }

    @Override
    public void setUserId(int userId) {
        this.userId = userId;
    }

}
