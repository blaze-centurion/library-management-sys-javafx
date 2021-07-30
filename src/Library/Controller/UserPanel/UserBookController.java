package Library.Controller.UserPanel;

import Library.Controller.Panel;
import Library.Utils.Books;
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
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class UserBookController extends Panel implements Initializable {
    Users user;
    String[] sortData = {"Book Name", "Category"};
    DataBase db = new DataBase();
    Utils util = new Utils();
    ProgressIndicator indicator;
    String initQuery = "SELECT * FROM books JOIN category ON books.category = category.catId ";
    @FXML
    private TableView<Books> tableView;
    @FXML
    private TableColumn<Books, Integer> tcSno;
    @FXML
    private TableColumn<Books, String> tcBookImg;
    @FXML
    private TableColumn<Books, String> tcBookName;
    @FXML
    private TableColumn<Books, String> tcCat;
    @FXML
    private TableColumn<Books, Integer> tcPopularity;
    @FXML
    private TableColumn<Books, Integer> tcAvailable;
    @FXML
    private TableColumn<Books, String> tcIssue;
    @FXML
    private JFXComboBox<String> sortBox;
    @FXML
    private StackPane stackPane;
    @FXML
    private TextField searchBar;
    @FXML
    private Button searchBtn;
    @FXML
    private Label errorLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (String data : sortData) {
            sortBox.getItems().add(data);
        }
        sortBox.getSelectionModel().select(sortData[0]);

        indicator = new ProgressIndicator(0);
        stackPane.getChildren().add(indicator);

        searchBar.setOnAction(this::search);
        searchBtn.setOnAction(this::search);
    }

    public void search(ActionEvent event) {
        String query;
        if (searchBar.getText().length() == 0) {
            showBooks(initQuery);
        } else {
            if (sortBox.getSelectionModel().getSelectedItem().equals(sortData[0])) {
                query = "SELECT * FROM books JOIN category ON books.category = category.catId WHERE books.bookname LIKE '%" + searchBar.getText() + "%'";
            } else {
                query = "SELECT * FROM books JOIN category ON books.category = category.catId WHERE category.catName LIKE '%" + searchBar.getText() + "%'";
            }
            showBooks(query);
        }
    }

    @Override
    public void setUser(Users user) {
        this.user = user;
        showBooks(initQuery);
    }

    private void showBooks(String query) {
        Task<ObservableList<Books>> task = new Task<>() {
            @Override
            protected ObservableList<Books> call() {
                return getBookList(query);
            }
        };

        indicator.setVisible(true);
        indicator.progressProperty().bind(task.progressProperty());
        task.setOnSucceeded(e -> {
            ObservableList<Books> list = task.getValue();
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
        tcSno.setCellValueFactory(new PropertyValueFactory<>("SNo"));
        tcBookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        tcBookImg.setCellValueFactory(new PropertyValueFactory<>("bookImage"));
        tcAvailable.setCellValueFactory(new PropertyValueFactory<>("availableBook"));
        tcPopularity.setCellValueFactory(new PropertyValueFactory<>("popularity"));
        tcCat.setCellValueFactory(new PropertyValueFactory<>("category"));

        // These method will set button in operation col and image view in book image col. By setting node graphics.
        tcIssue.setCellFactory(getOperCellFactory());
        tcBookImg.setCellFactory(getImgCellFactory());

    }

    private ObservableList<Books> getBookList(String query) {
        ObservableList<Books> bookList = FXCollections.observableArrayList();
        Connection conn = db.getConnection();
        Statement st;
        ResultSet rs;

        try {
            st = conn.createStatement();
            rs = st.executeQuery(query);
            Books books;
            while (rs.next()) {
                books = new Books(rs.getInt("bookId"), rs.getString("bookImg"), rs.getString("bookname"), rs.getString("catName"), rs.getInt("availableBooks"), rs.getInt("popularity"), rs.getInt("bookId"), rs.getInt("category"), 0);
                bookList.add(books);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return bookList;
    }

    private Callback<TableColumn<Books, String>, TableCell<Books, String>> getOperCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<Books, String> call(TableColumn<Books, String> booksStringTableColumn) {
                return new TableCell<>() {
                    final JFXButton addBtn = new JFXButton("Issue Book");

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            configureTableBtn(addBtn, GlyphsDude.createIcon(FontAwesomeIcons.PLUS), getTableView().getItems().get(getIndex()));
                            setGraphic(addBtn);
                            setText(null);
                        }
                    }
                };
            }
        };
    }

    private Callback<TableColumn<Books, String>, TableCell<Books, String>> getImgCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<Books, String> call(TableColumn<Books, String> booksStringTableColumn) {
                return new TableCell<>() {
                    final ImageView imgView = new ImageView();
                    Image img;

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            if (item != null) {
                                try {
                                    img = new Image(item);
                                    imgView.setImage(img);
                                    imgView.setFitHeight(55);
                                    imgView.setFitWidth(90);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            setGraphic(imgView);
                            setText(null);
                        }
                    }
                };
            }
        };
    }

    private void configureTableBtn(JFXButton btn, Text icon, Books book) {
        btn.setGraphic(icon);
        btn.getStyleClass().add("dashboard_blue_btn");
        btn.setStyle("-fx-background-color: #0dff55; -fx-padding: 10px;");
        btn.setOnAction(event -> issuedBook(book));
    }

    public void issuedBook(Books book) {
        try {
            ResultSet rs = db.executeQuery("SELECT * FROM book_his WHERE userId = " + user.getUserid() + " AND bookId = " + book.getBookId() + " AND returnAt = 0");
            if (!rs.next()) {
                /*
                 * users.totalBooks+1
                 * users.issuedBook+1
                 * category.popularity+1
                 * category.availableBooks-1
                 * books.availableBooks-1
                 * books.issuedBook+1
                 * books.popularity+1
                 */

                db.executeUpdateQuery(util.getIssueBookQuery(user.getUserid(), book.getBookId(), book.getCatId()));
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                LocalDateTime now = LocalDateTime.now();
                db.executeUpdateQuery("INSERT INTO `book_his`(`username`, `bookname`, `userId`, `bookId`, `issuedAt`) VALUES ('" + user.getUsername() + "', '" + book.getBookName() + "', " + user.getUserid() + ", " + book.getBookId() + ", '" + dtf.format(now) + "');");
                refresh();
            } else {
                util.showErrorAndDisappear(errorLabel, "Already has this book.", 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refresh() {
        showBooks(initQuery);
        tableView.refresh();
    }

    @Override
    public void setUserId(int userId) {
        this.userId = userId;
    }
}
