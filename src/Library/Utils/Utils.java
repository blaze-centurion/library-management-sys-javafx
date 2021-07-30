package Library.Utils;

import Library.BCrypt;
import Library.Controller.Form.SignUpController;
import Library.Controller.Panel;
import Library.Controller.Utils.ModalBoxController;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

public class Utils {

    public String[] roleArr = {"User", "Librarian", "Admin"};
    public String LibrarianCode = "12345";
    public String AdminCode = "12345";
    public String defaultProfileImg = "images/user.png";
    String[][] modalBxDetails = {
            {"Remove Book", "Book Id", "tfBookId"},
            {"Remove Category", "Category Id", "tfCatId"},
            {"Add Category", "Category Name", "tfCatName"},
            {"Add Fine", "Fine Price", "tfFine"}
    };

    public String capitalizeText(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    public String copyFile(File file) throws Exception {
        Path src = Paths.get(file.getAbsolutePath());
        SimpleDateFormat formatter = new SimpleDateFormat("HH-mm-ss");
        Date date = new Date();
        String fileName = formatter.format(date) + "-" + file.getName();
        Path dest = Paths.get("E:/Coding/LibraryManagementSys/src/images/" + fileName);
        Files.copy(src, dest);
        return "images/" + fileName;
    }

    public String getIssueBookQuery(int userId, int bookId, int cat) {
        return "UPDATE users, books, category " +
                "SET users.totalBooks = users.totalBooks+1, users.issuedBook = users.issuedBook+1, category.popularity = category.popularity+1, category.availableBooks = category.availableBooks-1, books.availableBooks = books.availableBooks-1, books.issuedBook = books.issuedBook+1, books.popularity = books.popularity+1 " +
                "WHERE users.userid = " + userId + " AND books.bookId = " + bookId + " AND category.catId = " + cat + ";";
    }

    public void loadPanel(String filePath, BorderPane userPanel, Label label, String labelText, Users user, VBox sidebar) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(filePath)));
            Parent container = loader.load();
            Panel panel = loader.getController();
            userPanel.setCenter(container);
            label.setText(labelText);
            panel.setUser(user);
            panel.getParentItems(label, sidebar);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Executor createExecutor() {
        return Executors.newCachedThreadPool(runnable -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });
    }

    public void setTableViewDim(TableView tableView) {
        tableView.setFixedCellSize(73);
        tableView.prefHeightProperty().bind(tableView.fixedCellSizeProperty().multiply(Bindings.size(tableView.getItems()).add(1.01)));
        tableView.minHeightProperty().bind(tableView.prefHeightProperty());
        tableView.maxHeightProperty().bind(tableView.prefHeightProperty());
    }

    public void toggleActive(VBox sidebar_items_container, Node sidebar_item) {
        sidebar_items_container.lookup(".sidebar_items.active").getStyleClass().remove("active");
        sidebar_item.getStyleClass().add("active");
    }

    public void toggleSidebar(VBox sidebar_items_container, HBox logoBx) {
        if (sidebar_items_container.getPrefWidth() > 65.0) {
            sidebar_items_container.setPrefWidth(65.0);
            logoBx.setPrefWidth(85);
            for (Node n : sidebar_items_container.lookupAll(".sidebar_items_label")) {
                n.setVisible(false);
            }
        } else {
            sidebar_items_container.setPrefWidth(240.0);
            logoBx.setPrefWidth(240.0);
            for (Node n : sidebar_items_container.lookupAll(".sidebar_items_label")) {
                n.setVisible(true);
            }
        }
    }

    public int getRoleNo(String role) {
        // if array is Null
        if (roleArr == null) {
            return -1;
        }

        // find length of array
        int len = roleArr.length;
        int i = 0;

        // traverse in the array
        while (i < len) {

            // if the i-th element is t
            // then return the index
            if (roleArr[i].equals(role)) return i;
            else i = i + 1;
        }
        return -1;
    }

    public void loadForm(String path, VBox formContainer, double x) {
        TranslateTransition t = new TranslateTransition(Duration.seconds(0.4), formContainer);
        t.setToX(x);
        t.play();
        t.setOnFinished((e) -> {
            try {
                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(path)));
                Parent fxml = loader.load();
                if (path.contains("SignUp")) {
                    SignUpController controller = loader.getController();
                    controller.setFormContainer(formContainer);
                }
                formContainer.getChildren().removeAll();
                formContainer.getChildren().setAll(fxml);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public void showError(Label errorLabel, String err) {
        errorLabel.setText(err);
        errorLabel.setVisible(true);
    }

    public void showErrorAndDisappear(Label errorLabel, String err, int sec) {
        showError(errorLabel, err);
        disappearLabel(errorLabel, sec);
    }

    public void showSuccessAndDisappear(Label errorLabel, String err, int sec) {
        showSuccess(errorLabel, err);
        disappearLabel(errorLabel, sec);
    }

    private void disappearLabel(Label label, int sec) {
        PauseTransition visiblePause = new PauseTransition(
                Duration.seconds(sec)
        );
        visiblePause.setOnFinished(
                event -> label.setVisible(false)
        );
        visiblePause.play();
    }

    public void showSuccess(Label errorLabel, String txt) {
        errorLabel.setStyle("-fx-text-fill: #56fc03;");
        errorLabel.setText(txt);
        errorLabel.setVisible(true);
    }

    public String getGreetingTxt(int time) {
        String greetTxt;
        if (time < 12) {
            greetTxt = "Good Morning";
        } else if (time < 17) {
            greetTxt = "Good Afternoon";
        } else if (time < 20) {
            greetTxt = "Good Evening";
        } else {
            greetTxt = "Good Night";
        }

        return greetTxt;
    }

    private Stage createNewStage(String path, int type, boolean singleField, String id) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(path)));
        VBox root = loader.load();
        ModalBoxController modalBoxController = loader.getController();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        if (singleField)
            modalBoxController.configureSingleFieldModalBx(modalBxDetails[type][0], modalBxDetails[type][1], modalBxDetails[type][0], modalBxDetails[type][2], type, id);
        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.setResizable(false);
        return stage;
    }

    public void openModalBox(ActionEvent event, String path, boolean singleField, int type, String... args) {
        try {
            Stage stage;
            if (args.length > 0) stage = createNewStage(path, type, singleField, args[0]);
            else stage = createNewStage(path, type, singleField, "-1");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getRandomId() {
        return ThreadLocalRandom.current().nextInt(1, 9999999);
    }

    public boolean isNumeric(String strNum) {
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }
}
