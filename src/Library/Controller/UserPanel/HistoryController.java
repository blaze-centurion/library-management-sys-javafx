package Library.Controller.UserPanel;

import Library.Controller.Panel;
import Library.Utils.BookHis;
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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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


public class HistoryController extends Panel implements Initializable {

    ProgressIndicator indicator;
    Users user;
    DataBase db = new DataBase();
    Utils util = new Utils();
    String initQuery = "SELECT * FROM book_his WHERE userId = ";
    @FXML
    private JFXComboBox<String> sortBx;
    @FXML
    private TableView<BookHis> tableView;
    @FXML
    private TableColumn<BookHis, String> tcSno;
    @FXML
    private TableColumn<BookHis, String> tcBookName;
    @FXML
    private TableColumn<BookHis, String> tcIssued;
    @FXML
    private TableColumn<BookHis, String> tcReturn;
    @FXML
    private TableColumn<BookHis, String> tcFine;
    @FXML
    private TableColumn<BookHis, String> tcOperation;
    @FXML
    private StackPane stackPane;
    @FXML
    private Label errorLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        indicator = new ProgressIndicator();
        stackPane.getChildren().add(indicator);
    }

    @Override
    public void setUser(Users user) {
        this.user = user;
        showBooks(initQuery + user.getUserid());
    }

    private void showBooks(String query) {
        Task<ObservableList<BookHis>> task = new Task<>() {
            @Override
            protected ObservableList<BookHis> call() {
                return getBookList(query);
            }
        };

        indicator.setVisible(true);
        indicator.progressProperty().bind(task.progressProperty());
        task.setOnSucceeded(e -> {
            ObservableList<BookHis> list = task.getValue();
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
        tcSno.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        tcBookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        tcIssued.setCellValueFactory(new PropertyValueFactory<>("issuedAt"));
        tcReturn.setCellValueFactory(new PropertyValueFactory<>("returnAt"));
        tcFine.setCellValueFactory(new PropertyValueFactory<>("fine"));

        // These method will set button in operation col and image view in book image col. By setting node graphics.
        tcOperation.setCellFactory(getOperCellFactory());
    }

    private ObservableList<BookHis> getBookList(String query) {
        ObservableList<BookHis> bookList = FXCollections.observableArrayList();
        Connection conn = db.getConnection();
        Statement st;
        ResultSet rs;

        try {
            st = conn.createStatement();
            rs = st.executeQuery(query);
            BookHis books;
            while (rs.next()) {
                books = new BookHis(rs.getString("bookname"), rs.getInt("bookId"), rs.getString("issuedAt"), rs.getString("returnAt"), rs.getInt("id"), rs.getInt("fine"));
                bookList.add(books);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return bookList;
    }

    private Callback<TableColumn<BookHis, String>, TableCell<BookHis, String>> getOperCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<BookHis, String> call(TableColumn<BookHis, String> booksStringTableColumn) {
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

    private void configureTableBtn(JFXButton btn, Text icon, BookHis book) {
        btn.setGraphic(icon);
        btn.getStyleClass().add("dashboard_blue_btn");
        btn.setStyle("-fx-background-color: #0dff55; -fx-padding: 10px;");
        btn.setOnAction(event -> issuedBook(book));
    }

    public void issuedBook(BookHis book) {
        try {
            ResultSet rs1 = db.executeQuery("SELECT * FROM book_his WHERE userId = " + user.getUserid() + " AND bookId = " + book.getBookId() + " AND returnAt = 0");
            if (!rs1.next()) {
                /*
                 * users.totalBooks+1
                 * users.issuedBook+1
                 * category.popularity+1
                 * category.availableBooks-1
                 * books.availableBooks-1
                 * books.issuedBook+1
                 * books.popularity+1
                 */
                ResultSet rs2 = db.executeQuery("SELECT category FROM books WHERE bookId = " + book.getBookId());
                while (rs2.next()) {
                    db.executeUpdateQuery(util.getIssueBookQuery(user.getUserid(), book.getBookId(), rs2.getInt("category")));
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    LocalDateTime now = LocalDateTime.now();
                    db.executeUpdateQuery("INSERT INTO `book_his`(`username`, `bookname`, `userId`, `bookId`, `issuedAt`) VALUES ('" + user.getUsername() + "', '" + book.getBookName() + "', " + user.getUserid() + ", " + book.getBookId() + ", '" + dtf.format(now) + "');");
                    refresh();
                }
            } else {
                util.showErrorAndDisappear(errorLabel, "Already has this book.", 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refresh() {
        showBooks(initQuery + user.getUserid());
        tableView.refresh();
    }


    @Override
    public void setUserId(int userId) {
        this.userId = userId;
    }

}
