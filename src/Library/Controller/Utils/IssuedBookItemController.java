package Library.Controller.Utils;

import Library.Utils.BookHis;
import Library.Utils.DataBase;
import Library.Utils.Users;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IssuedBookItemController {

    Users user;
    int id;
    @FXML
    private Label bookName;
    @FXML
    private Label bookId;
    @FXML
    private Label issuedAt;
    @FXML
    private Label userName;
    @FXML
    private Label userId;
    @FXML
    private Label returnAt;
    @FXML
    private JFXButton returnBtn;
    @FXML
    private HBox itemBx;
    private VBox container;

    public void setContainer(VBox container) {
        this.container = container;
    }

    public void setData(BookHis bookHis) {
        bookName.setText(bookHis.getBookName());
        bookId.setText("S.No. " + bookHis.getBookId());
        issuedAt.setText("Issued At: " + bookHis.getIssuedAt());
        id = bookHis.getId();
        if (userName != null) {
            userName.setText(bookHis.getUserName());
            userId.setText("User Id: " + bookHis.getUserId());

            String returnTxt = "Not Yet";
            if (!bookHis.getReturnAt().equals("0")) {
                // That means book is returned.
                returnTxt = bookHis.getReturnAt();
                returnAt.getStyleClass().remove("not_returned");
                returnAt.getStyleClass().add("returned");
            }
            returnAt.setText(returnTxt);
        }
    }

    public void returnBook() {
        String bookId = getBookId().substring(5).trim();
        DataBase db = new DataBase();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        String curDate = formatter.format(date);

        try {
            ResultSet rs = db.executeQuery("SELECT category FROM books WHERE bookId = " + bookId);
            while (rs.next()) {
                String query = "UPDATE book_his, books, category SET book_his.returnAt = '" + curDate + "', books.issuedBook = books.issuedBook-1, books.availableBooks = books.availableBooks+1, category.availableBooks = category.availableBooks+1 WHERE book_his.id = " + id + " AND books.bookId = " + bookId + " AND category.catId = " + rs.getInt("category") + ";";
                db.executeUpdateQuery(query);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getBookId() {
        return bookId.getText();
    }
}
