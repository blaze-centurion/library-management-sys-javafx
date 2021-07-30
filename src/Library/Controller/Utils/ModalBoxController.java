package Library.Controller.Utils;

import Library.Utils.DataBase;
import Library.Utils.Utils;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class ModalBoxController implements Initializable {
    TextField tfSingle;
    Utils util = new Utils();
    DataBase db = new DataBase();
    File selectedFile;
    @FXML
    private TextField tfBookId;
    @FXML
    private Label errorLabel;
    @FXML
    private TextField tfBook;
    @FXML
    private TextField tfBookName;
    @FXML
    private TextField tfBookImg;
    @FXML
    private JFXButton btnFileChooser;
    @FXML
    private JFXButton btn;
    @FXML
    private TextField tfBooks;
    @FXML
    private JFXComboBox<String> cbCat;
    @FXML
    private JFXButton btnAddBook;
    @FXML
    private TextField tfCatName;
    @FXML
    private TextField tfCatId;
    @FXML
    private Label titleLabel;
    @FXML
    private VBox container;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (cbCat != null) {
            try {
                ResultSet rs = db.executeQuery("SELECT catName FROM category");
                ArrayList<String> catArr = new ArrayList<>();
                while (rs.next()) {
                    catArr.add(rs.getString("catName"));
                }
                for (String item : catArr) {
                    cbCat.getItems().add(item);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            btnAddBook.setOnAction(this::addBook);
            btnFileChooser.setOnAction(this::openImgChooser);
        }
    }

    public void openImgChooser(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        selectedFile = chooser.showOpenDialog(null);
        if (selectedFile != null) tfBookImg.setText(selectedFile.getAbsolutePath());
    }

    private String copyFile(File file) {
        Path src = Paths.get(file.getAbsolutePath());
        SimpleDateFormat formatter = new SimpleDateFormat("HH-mm-ss");
        Date date = new Date();
        String fileName = formatter.format(date) + "-" + file.getName();
        Path dest = Paths.get("E:/Coding/LibraryManagement/src/images/" + fileName);
        try {
            Files.copy(src, dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "images/" + fileName;
    }

    private void addBook(ActionEvent event) {
        if (tfBookImg.getText().length() != 0 && tfBooks.getText().length() != 0 && tfBookName.getText().length() != 0 && cbCat.getSelectionModel().getSelectedItem() != null) {
            try {
                if (util.isNumeric(tfBooks.getText()) && Integer.parseInt(tfBooks.getText()) > 0) {
                    ResultSet rs = db.executeQuery("SELECT catId from category WHERE catName = '" + cbCat.getSelectionModel().getSelectedItem() + "'");
                    while (rs.next()) {
                        int totalBooks = Integer.parseInt(tfBooks.getText());
                        String bookImg = util.copyFile(selectedFile);
                        String query1 = "INSERT INTO books (bookId, bookname, bookImg, category, totalBooks, availableBooks) " +
                                "VALUES (" + util.getRandomId() + ", \"" + tfBookName.getText() + "\", '" + bookImg + "', " + rs.getInt("catId") + ", " + totalBooks + ", " + totalBooks + ");";
                        String query2 = "UPDATE category SET totalBook = totalBook+" + totalBooks + ", availableBooks = availableBooks + " + totalBooks + " WHERE catId=" + rs.getInt("catId");
                        db.executeUpdateQuery(query1);
                        db.executeUpdateQuery(query2);
                        util.showSuccess(errorLabel, "Book Added Successfully.");
                    }
                } else {
                    util.showError(errorLabel, "Invalid amount of books.");
                }
            } catch (Exception ex) {
                util.showError(errorLabel, "Something went wrong.");
                ex.printStackTrace();
            }
        } else {
            util.showError(errorLabel, "All fields are required.");
        }
    }

    public void addBookInStock() {
        /*
         * This method will increment or decrement books in stock.
         */
        try {
            if (tfBook.getText().length() != 0 && tfBookId.getText().length() != 0) {
                if (util.isNumeric(tfBook.getText()) && Integer.parseInt(tfBook.getText()) > 0) {
                    ResultSet rs = db.executeQuery("SELECT category FROM books WHERE bookId =" + tfBookId.getText());
                    if (rs.next()) {
                        String query = "UPDATE books, category " +
                                "SET books.totalBooks = books.totalBooks + " + tfBook.getText() + ", books.availableBooks = books.availableBooks + " + tfBook.getText() + ", category.totalBook = category.totalBook +" + tfBook.getText() + ", category.availableBooks = category.availableBooks +" + tfBook.getText() + " " +
                                "WHERE books.bookId = " + tfBookId.getText() + " AND category.catId = " + rs.getInt("category");
                        db.executeUpdateQuery(query);
                        util.showSuccess(errorLabel, "Book Added Successfully.");
                    } else {
                        util.showError(errorLabel, "Book not found.");
                    }
                } else {
                    util.showError(errorLabel, "Invalid amount of books.");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void removeBook() {
        if (tfSingle.getText().length() != 0) {
            try {
                ResultSet rs = db.executeQuery("SELECT * FROM books WHERE bookId = " + tfSingle.getText());
                if (rs.next()) {
                    File file = new File("src/" + rs.getString("bookImg"));
                    String updateCatQuery = "UPDATE category " +
                            "SET totalBook = totalBook - " + rs.getInt("totalBooks") + ", availableBooks = availableBooks - " + rs.getInt("availableBooks") +
                            " WHERE catId = " + rs.getInt("category");
                    db.executeUpdateQuery(updateCatQuery);
                    db.executeUpdateQuery("DELETE FROM books WHERE  bookId= " + tfSingle.getText());
                    file.delete();
                    util.showSuccess(errorLabel, "Book Removed Successfully.");
                } else {
                    util.showError(errorLabel, "Book Not Found");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            util.showError(errorLabel, "All fields are required.");
        }
    }

    public void addNewCat() {
        if (tfSingle.getText().length() != 0) {
            try {
                db.executeUpdateQuery("INSERT INTO category (catName) VALUES ('" + tfSingle.getText() + "')");
                util.showSuccess(errorLabel, "Category Added successfully.");
            } catch (Exception ex) {
                util.showError(errorLabel, "Something went wrong.");
                ex.printStackTrace();
            }
        } else {
            util.showError(errorLabel, "All fields are required.");
        }
    }

    public void configureSingleFieldModalBx(String title, String placeholder, String btnText, String id, int type, String sNo) {
        /*
         * 0 - removeBooks
         * 1 - remove cat
         * 2 - add cat
         * 3 - Add fine
         */
        titleLabel.setText(title);
        tfBookId.setPromptText(placeholder);
        btn.setText(btnText);
        tfSingle = (TextField) container.lookup("#textField");
        tfSingle.setId(id);

        if (type == 0) {
            btn.setOnAction(event -> removeBook());
        } else if (type == 1) {
            btn.setOnAction(event -> removeCat());
        } else if (type == 2) {
            btn.setOnAction(event -> addNewCat());
        } else if (type == 3) {
            btn.setOnAction(event -> addNewFine(sNo));
        }
    }

    private void addNewFine(String id) {
        if (tfSingle.getText().length() != 0) {
            try {
                if (util.isNumeric(tfSingle.getText()) && Integer.parseInt(tfSingle.getText()) > 0) {
                    db.executeUpdateQuery("UPDATE book_his SET fine = fine + " + tfSingle.getText() + " WHERE id = " + id);
                    util.showSuccess(errorLabel, "Fine Added successfully.");
                } else {
                    util.showError(errorLabel, "Invalid amount of fine.");
                }
            } catch (Exception ex) {
                util.showError(errorLabel, "Something went wrong.");
                ex.printStackTrace();
            }
        } else {
            util.showError(errorLabel, "All fields are required.");
        }
    }


    public void removeCat() {
        if (tfSingle.getText().length() != 0) {
            try {
                ResultSet rs = db.executeQuery("SELECT * FROM category WHERE catId= " + tfSingle.getText());
                if (rs.next()) {
                    db.executeUpdateQuery("DELETE FROM category WHERE catId=" + tfSingle.getText());
                    util.showSuccess(errorLabel, "Category Removed successfully.");
                } else {
                    util.showError(errorLabel, "Category Not Found.");
                }
            } catch (Exception ex) {
                util.showError(errorLabel, "Something went wrong.");
                ex.printStackTrace();
            }
        } else {
            util.showError(errorLabel, "All fields are required.");
        }
    }

    public void closeWindow(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

}
