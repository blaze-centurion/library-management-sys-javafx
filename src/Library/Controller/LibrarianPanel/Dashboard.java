package Library.Controller.LibrarianPanel;

import Library.Controller.Panel;
import Library.Controller.Utils.FineItemController;
import Library.Controller.Utils.FineItems;
import Library.Controller.Utils.IssuedBookItemController;
import Library.Controller.Utils.UserListItemController;
import Library.Utils.BookHis;
import Library.Utils.DataBase;
import Library.Utils.Users;
import Library.Utils.Utils;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Dashboard extends Panel implements Initializable {

    Users user;
    Utils util = new Utils();
    DataBase db = new DataBase();
    @FXML
    private Label totalBook;
    @FXML
    private Label availabelBooks;
    @FXML
    private Label issuedBooks;
    @FXML
    private Label totalFine;
    @FXML
    private Label greetingLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private VBox fineHisTable;
    @FXML
    private VBox bookHisTable;
    @FXML
    private JFXButton btnAddNewBook;
    @FXML
    private JFXButton btnRemoveBook;
    @FXML
    private VBox userItemBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnAddNewBook.setOnAction(event -> util.openModalBox(event, "../fxml/Utils/AddBooks.fxml", false, -1));
        btnRemoveBook.setOnAction(event -> util.openModalBox(event, "../fxml/Utils/RemoveBooks.fxml", true, -1));
    }

    @Override
    public void setUser(Users user) {
        this.user = user;
        this.configureDashboard();
    }

    private void configureDashboard() {
        String username = user.getUsername();
        usernameLabel.setText(util.capitalizeText(username));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH");
        int time = Integer.parseInt(dtf.format(LocalDateTime.now()));
        greetingLabel.setText(util.getGreetingTxt(time) + ":");

        totalBook.setText(String.valueOf(getBooksDetail().get(0)));
        availabelBooks.setText(String.valueOf(getBooksDetail().get(1)));
        issuedBooks.setText(String.valueOf(getBooksDetail().get(2)));

        configureFineTab();
        configureHisTab();
        configureUserTab();
    }

    private void configureUserTab() {
        ArrayList<Users> userList = new ArrayList<>(getUserList());
        for (Users user : userList) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../../fxml/Utils/UserListItem.fxml"));
            try {
                HBox item = loader.load();
                UserListItemController uic = loader.getController();
                uic.setData(user);
                userItemBox.getChildren().add(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private ArrayList<Users> getUserList() {
        ArrayList<Users> ls = new ArrayList<>();

        try {
            String query = "SELECT * FROM users WHERE role <> 2";
            ResultSet rs = db.executeQuery(query);
            Users user;
            while (rs.next()) {
                user = new Users(rs.getInt("userid"), rs.getString("username"), rs.getString("email"), rs.getInt("role"), rs.getString("joinon"));
                ls.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ls;
    }

    private void configureFineTab() {
        ArrayList<FineItems> fineItems = new ArrayList<>(getFineDetails());
        int fine = 0;
        for (FineItems fineItem : fineItems) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../../fxml/Utils/LibFineItems.fxml"));
            try {
                HBox item = loader.load();
                FineItemController fic = loader.getController();
                fic.setData(fineItem);
                fine += fineItem.getFine();
                fineHisTable.getChildren().add(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        totalFine.setText(String.valueOf(fine));
    }

    private void configureHisTab() {
        ArrayList<BookHis> bookHisItems = new ArrayList<>(getBookHis());
        for (BookHis bookHisItem : bookHisItems) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../../fxml/Utils/LibBookItem.fxml"));
            try {
                HBox item = loader.load();
                IssuedBookItemController fic = loader.getController();
                fic.setData(bookHisItem);
                bookHisTable.getChildren().add(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private ArrayList<Integer> getBooksDetail() {
        /*
         * @return: List of book details containing - total books, available books, issued books
         */
        ArrayList<Integer> bookDetails = new ArrayList<>();

        try {
            String query = "SELECT totalBooks, availableBooks, issuedBook FROM books";
            ResultSet rs = db.executeQuery(query);
            int totalBooks = 0;
            int availableBook = 0;
            int issuedBook = 0;
            while (rs.next()) {
                totalBooks += rs.getInt("totalBooks");
                availableBook += rs.getInt("availableBooks");
                issuedBook += rs.getInt("issuedBook");
            }
            bookDetails.add(0, totalBooks);
            bookDetails.add(1, availableBook);
            bookDetails.add(2, issuedBook);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return bookDetails;
    }

    private ArrayList<BookHis> getBookHis() {
        ArrayList<BookHis> ls = new ArrayList<>();
        try {
            String query = "SELECT * FROM book_his";
            ResultSet rs = db.executeQuery(query);
            BookHis hisItem;
            while (rs.next()) {
                hisItem = new BookHis(rs.getString("bookname"), rs.getInt("bookId"), rs.getString("issuedAt"), rs.getString("returnAt"), rs.getString("username"), rs.getInt("userId"), rs.getInt("fine"));
                ls.add(hisItem);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ls;
    }

    private ArrayList<FineItems> getFineDetails() {

        ArrayList<FineItems> ls = new ArrayList<>();

        try {
            String query = "SELECT * FROM fine_his";
            ResultSet rs = db.executeQuery(query);
            FineItems fineItems;
            while (rs.next()) {
                fineItems = new FineItems();
                fineItems.setBookName(rs.getString("bookname"));
                fineItems.setIssuedAt(rs.getString("issuedAt"));
                fineItems.setBookId(rs.getString("bookId"));
                fineItems.setReason(rs.getString("reason") + " - " + rs.getString("fine") + "Rs");
                fineItems.setUserName(rs.getString("username"));
                fineItems.setUserId(rs.getString("userId"));
                fineItems.setFine(rs.getInt("fine"));
                ls.add(fineItems);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ls;
    }

    @Override
    public void setUserId(int userId) {
        this.userId = userId;
    }

}
