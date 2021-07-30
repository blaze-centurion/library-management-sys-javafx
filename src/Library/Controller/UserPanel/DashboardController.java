package Library.Controller.UserPanel;

import Library.Controller.Panel;
import Library.Controller.Utils.FineItemController;
import Library.Controller.Utils.FineItems;
import Library.Controller.Utils.IssuedBookItemController;
import Library.Utils.BookHis;
import Library.Utils.DataBase;
import Library.Utils.Users;
import Library.Utils.Utils;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class DashboardController extends Panel {

    Users user;
    Utils util = new Utils();
    DataBase db = new DataBase();
    @FXML
    private Label greetingLabel;
    @FXML
    private Label userName;
    @FXML
    private Label totalBookIssued;
    @FXML
    private Label totalBook;
    @FXML
    private Label totalFine;
    @FXML
    private Label tableTotalFine;
    @FXML
    private VBox issuedBookBox;
    @FXML
    private VBox fineHisBox;
    @FXML
    private VBox historyTable;
    @FXML
    private VBox dashboardPanel;

    @Override
    public void setUser(Users user) {
        this.user = user;
        this.configureDashboard();
    }

    public void configureDashboard() {
        /*
         * 1) Set the username.
         * 2) Set the greeting text on the basis of time.
         * 3) Set book his, issued list and fine his.
         */

        String username = user.getUsername();
        userName.setText(util.capitalizeText(username));

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH");
        int time = Integer.parseInt(dtf.format(LocalDateTime.now()));
        greetingLabel.setText(util.getGreetingTxt(time) + ":");

        ArrayList<Integer> bookDetails = getBooksDetail();
        totalBook.setText(String.valueOf(bookDetails.get(0)));
        totalFine.setText(String.valueOf(bookDetails.get(1)));
        tableTotalFine.setText(String.valueOf(bookDetails.get(1)));
        totalBookIssued.setText(String.valueOf(bookDetails.get(2)));

        try {
            String query = "SELECT * FROM users INNER JOIN book_his ON users.userid = book_his.userId WHERE users.userId = " + user.getUserid() + " AND book_his.returnAt";
            // Issued book table.
            configureTable("../../fxml/Utils/IssuedBookItem.fxml", query + "= 0", issuedBookBox);
            // Fine his table.
            configureFineTable();
            // Book his table.
            configureTable("../../fxml/Utils/BookHisItem.fxml", query + "<> 0", historyTable);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void configureFineTable() throws IOException {
        List<FineItems> issuedBooks = new ArrayList<>(fineHistory("SELECT * FROM users INNER JOIN fine_his ON users.userid = fine_his.userId WHERE users.userId = " + user.getUserid()));
        for (FineItems book : issuedBooks) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../../fxml/Utils/FineItems.fxml"));
            HBox item = loader.load();
            FineItemController fic = loader.getController();
            fic.setData(book);
            fineHisBox.getChildren().add(item);
        }
    }

    private void configureTable(String path, String query, VBox container) throws IOException {
        /*
         * It will add issued books or book his list in issued book box or book his box.
         */
        List<BookHis> bookHis = new ArrayList<>(issuedHistory(query));
        for (BookHis book : bookHis) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            HBox item = loader.load();
            IssuedBookItemController ibc = loader.getController();
            ibc.setData(book);
            container.getChildren().add(item);
        }
    }

    private List<BookHis> issuedHistory(String query) {
        /*
         * @return  Issued list.
         */

        List<BookHis> ls = new ArrayList<>();
        try {
            ResultSet rs = db.executeQuery(query);
            BookHis bookHis;
            while (rs.next()) {
                bookHis = new BookHis();
                bookHis.setBookName(rs.getString("bookname"));
                bookHis.setIssuedAt(rs.getString("issuedAt"));
                bookHis.setBookId(rs.getInt("bookId"));
                bookHis.setId(rs.getInt("id"));
                ls.add(bookHis);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return ls;
    }

    private ArrayList<Integer> getBooksDetail() {
        /*
         * @return: List of book details containing - total books, total fine, issued books
         */
        ArrayList<Integer> bookDetails = new ArrayList<>();

        try {
            String query = "SELECT totalBooks, totalFine, issuedBook FROM users WHERE userid = " + user.getUserid();
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                bookDetails.add(0, rs.getInt("totalBooks"));
                bookDetails.add(1, rs.getInt("totalFine"));
                bookDetails.add(2, rs.getInt("issuedBook"));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return bookDetails;
    }

    private List<FineItems> fineHistory(String query) {
        /*
         * @return  Issued list.
         */

        List<FineItems> ls = new ArrayList<>();
        try {
            ResultSet rs = db.executeQuery(query);
            FineItems fineItems;
            while (rs.next()) {
                fineItems = new FineItems();
                fineItems.setBookName(rs.getString("bookname"));
                fineItems.setIssuedAt(rs.getString("issuedAt"));
                fineItems.setBookId(rs.getString("bookId"));
                fineItems.setReason(rs.getString("reason"));
                ls.add(fineItems);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return ls;
    }

    @Override
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void openHistoryPanel(Event event) {
        util.toggleActive(sidebar, (Node) event.getSource());
        util.loadPanel("../fxml/User/History.fxml", (BorderPane) dashboardPanel.getParent(), panelNameLabel, "History", user, null);
    }

}
