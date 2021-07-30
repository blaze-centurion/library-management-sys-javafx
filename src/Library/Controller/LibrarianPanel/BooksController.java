package Library.Controller.LibrarianPanel;

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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class BooksController extends Panel implements Initializable {

    private final String[] sortArr = {"Newest", "Popular", "Oldest"};
    Users user;
    DataBase db = new DataBase();
    Utils util = new Utils();
    ProgressIndicator indicator;
    String query = "SELECT * FROM books JOIN category ON books.category = category.catId";
    @FXML
    private JFXComboBox<String> sortBx;
    @FXML
    private TableView<Books> tableView;
    @FXML
    private TableColumn<Books, Integer> tcSno;
    @FXML
    private TableColumn<Books, String> tcBookImg;
    @FXML
    private TableColumn<Books, String> tcBookName;
    @FXML
    private TableColumn<Books, String> tcCategory;
    @FXML
    private TableColumn<Books, Integer> tcTotalBooks;
    @FXML
    private TableColumn<Books, Integer> tcAvailable;
    @FXML
    private TableColumn<Books, Integer> tcIssued;
    @FXML
    private TableColumn<Books, String> tcOperation;
    @FXML
    private JFXButton btnAddNewBook;
    @FXML
    private JFXButton btnRemoveBook;
    @FXML
    private JFXButton btnAddInStock;
    @FXML
    private TextField searchbar;
    @FXML
    private Button searchBtn;
    @FXML
    private StackPane stackPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        indicator = new ProgressIndicator(0);
        stackPane.getChildren().add(indicator);

        tableView.setPlaceholder(new Label("Books Not Found."));
        btnAddNewBook.setOnAction(event -> util.openModalBox(event, "../fxml/Utils/AddBooks.fxml", false, -1));
        btnAddInStock.setOnAction(event -> util.openModalBox(event, "../fxml/Utils/AddInStock.fxml", false, -1));
        btnRemoveBook.setOnAction(event -> util.openModalBox(event, "../fxml/Utils/RemoveBooks.fxml", true, 0));
        searchbar.setOnAction(this::search);
        searchBtn.setOnAction(this::search);
    }

    private void search(ActionEvent event) {
        if (searchbar.getText().length() != 0) {
            showBooks("SELECT * FROM books JOIN category ON books.category = category.catId WHERE books.bookname LIKE '%" + searchbar.getText() + "%'");
        }
    }

    @Override
    public void setUser(Users user) {
        this.user = user;
        for (String item : sortArr) {
            sortBx.getItems().add(item);
        }
        configureBookPanel();
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
        tcCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        tcAvailable.setCellValueFactory(new PropertyValueFactory<>("availableBook"));
        tcTotalBooks.setCellValueFactory(new PropertyValueFactory<>("totalBooks"));
        tcIssued.setCellValueFactory(new PropertyValueFactory<>("issuedBooks"));

        // These method will set button in operation col and image view in book image col. By setting node graphics.
        tcOperation.setCellFactory(getOperCellFactory());
        tcBookImg.setCellFactory(getImgCellFactory());
    }

    private Callback<TableColumn<Books, String>, TableCell<Books, String>> getOperCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<Books, String> call(TableColumn<Books, String> booksStringTableColumn) {
                return new TableCell<>() {
                    final JFXButton addBtn = new JFXButton();
                    final JFXButton removeBtn = new JFXButton();
                    HBox hBox;

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        hBox = new HBox();

                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            configureTableBtn(addBtn, GlyphsDude.createIcon(FontAwesomeIcons.PLUS), "#0dff55", '+', getTableView().getItems().get(getIndex()));
                            configureTableBtn(removeBtn, GlyphsDude.createIcon(FontAwesomeIcons.MINUS), "#ff250d", '-', getTableView().getItems().get(getIndex()));
                            hBox.getChildren().addAll(addBtn, removeBtn);
                            hBox.setSpacing(7);
                            setGraphic(hBox);
                            setText(null);
                        }
                    }
                };
            }
        };
    }

    private void configureTableBtn(JFXButton btn, Text icon, String color, char op, Books book) {
        btn.setGraphic(icon);
        btn.getStyleClass().add("dashboard_blue_btn");
        btn.setStyle("-fx-background-color: " + color + ";");
        btn.setOnAction(event -> IncDecBook(book.getSNo(), book.getCatId(), op));
    }

    private Callback<TableColumn<Books, String>, TableCell<Books, String>> getImgCellFactory() {
        // Callback<TableColumn<Books, String>, TableCell<Books, String>>
        return new Callback<>() {
            @Override
            public TableCell<Books, String> call(TableColumn<Books, String> booksStringTableColumn) {
                // TableCell<Books, String>
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
                                    imgView.setFitWidth(70);
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
                books = new Books(rs.getInt("bookId"), rs.getString("bookImg"), rs.getString("bookname"), rs.getString("catName"), rs.getInt("availableBooks"), rs.getInt("totalBooks"), rs.getInt("issuedBook"), rs.getInt("category"));
                bookList.add(books);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return bookList;
    }

    private void configureBookPanel() {
        showBooks(query);
    }

    private void IncDecBook(int bookId, int catId, char operator) {
        /*
         * This method will increment or decrement books in stock.
         */
        try {
            String cond = "";
            if (operator == '-') {
                cond = " AND books.totalBooks > 0 AND books.availableBooks > 0";
            }
            String query = "UPDATE books, category " +
                    "SET books.totalBooks = books.totalBooks" + operator + "1, books.availableBooks = books.availableBooks" + operator + "1, category.totalBook = category.totalBook" + operator + "1, category.availableBooks = category.availableBooks" + operator + "1 " +
                    "WHERE books.bookId = " + bookId + " AND category.catId = " + catId + cond;
            db.executeUpdateQuery(query);
            refresh();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void refresh() {
        tableView.refresh();
        showBooks(query);
        tableView.refresh();
    }

    @Override
    public void setUserId(int userId) {
        this.userId = userId;
    }
}
