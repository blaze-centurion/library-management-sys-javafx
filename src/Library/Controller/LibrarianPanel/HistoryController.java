package Library.Controller.LibrarianPanel;

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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class HistoryController extends Panel {

    Users user;
    DataBase db = new DataBase();
    Utils util = new Utils();
    ProgressIndicator indicator;
    @FXML
    private JFXComboBox<String> sortBx;
    @FXML
    private TableView<BookHis> tableView;
    @FXML
    private TableColumn<BookHis, Integer> tcBookId;
    @FXML
    private TableColumn<BookHis, String> tcBookName;
    @FXML
    private TableColumn<BookHis, Integer> tcUserId;
    @FXML
    private TableColumn<BookHis, String> tcUserName;
    @FXML
    private TableColumn<BookHis, String> tcIssued;
    @FXML
    private TableColumn<BookHis, String> tcReturn;
    @FXML
    private TableColumn<BookHis, Integer> tcFine;
    @FXML
    private TableColumn<BookHis, String> tcOperation;
    @FXML
    private TextField searchbar;
    @FXML
    private Button searchBtn;
    @FXML
    private StackPane stackPane;

    @Override
    public void setUser(Users user) {
        this.user = user;
        indicator = new ProgressIndicator(0);
        stackPane.getChildren().add(indicator);
        searchbar.setOnAction(this::search);
        searchBtn.setOnAction(this::search);
        configureHisPanel();
    }

    private void configureHisPanel() {
        showHis("SELECT * FROM book_his");
    }

    public void showHis(String query) {
        Task<ObservableList<BookHis>> task = new Task<>() {
            @Override
            protected ObservableList<BookHis> call() {
                return getBookHisList(query);
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

    private Callback<TableColumn<BookHis, String>, TableCell<BookHis, String>> getOperCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<BookHis, String> call(TableColumn<BookHis, String> booksStringTableColumn) {
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
                            configureTableBtn(addBtn, GlyphsDude.createIcon(FontAwesomeIcons.PLUS), "#0dff55", 0, getTableView().getItems().get(getIndex()));
                            configureTableBtn(removeBtn, GlyphsDude.createIcon(FontAwesomeIcons.TRASH), "#ff250d", 1, getTableView().getItems().get(getIndex()));
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

    private void configureTableBtn(JFXButton btn, Text icon, String color, int addOrRemove, BookHis bookHis) {
        btn.setGraphic(icon);
        btn.getStyleClass().add("dashboard_blue_btn");
        btn.setStyle("-fx-background-color: " + color + ";");
        if (addOrRemove == 0) {
            btn.setOnAction(event -> openAddModalBox(event, bookHis.getId()));
        } else {
            btn.setOnAction(event -> removeFine(bookHis.getId()));
        }
    }

    private void removeFine(int id) {
        try {
            db.executeUpdateQuery("UPDATE book_his SET fine = 0 WHERE id = " + id);
            refresh();
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }

    private void openAddModalBox(ActionEvent event, int id) {
        util.openModalBox(event, "../fxml/Utils/RemoveBooks.fxml", true, 3, String.valueOf(id));
    }

    public void refresh() {
        showHis("SELECT * FROM book_his");
        tableView.refresh();
    }

    private ObservableList<BookHis> getBookHisList(String query) {
        ObservableList<BookHis> bookList = FXCollections.observableArrayList();
        Connection conn = db.getConnection();
        Statement st;
        ResultSet rs;

        try {
            st = conn.createStatement();
            rs = st.executeQuery(query);
            BookHis books;
            while (rs.next()) {
                books = new BookHis(rs.getString("bookname"), rs.getInt("bookId"), rs.getString("issuedAt"), rs.getString("returnAt"), rs.getString("username"), rs.getInt("userId"), rs.getInt("fine"));
                books.setId(rs.getInt("id"));
                bookList.add(books);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return bookList;
    }

    private void setTableColValueFactory() {
        tcBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        tcBookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        tcUserName.setCellValueFactory(new PropertyValueFactory<>("userName"));
        tcUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        tcIssued.setCellValueFactory(new PropertyValueFactory<>("issuedAt"));
        tcReturn.setCellValueFactory(new PropertyValueFactory<>("returnAt"));
        tcFine.setCellValueFactory(new PropertyValueFactory<>("fine"));
        tcOperation.setCellFactory(getOperCellFactory());
    }

    private void search(ActionEvent event) {
        if (searchbar.getText().length() != 0) {
            showHis("SELECT * FROM book_his WHERE bookname LIKE '%" + searchbar.getText() + "%' OR username LIKE '%" + searchbar.getText() + "%'");
        }
    }

    @Override
    public void setUserId(int userId) {
        this.userId = userId;
    }
}
