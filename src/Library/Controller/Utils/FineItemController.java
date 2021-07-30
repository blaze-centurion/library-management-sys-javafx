package Library.Controller.Utils;


import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class FineItemController {

    @FXML
    private Label bookName;

    @FXML
    private Label bookId;

    @FXML
    private Label reason;

    @FXML
    private Label issuedAt;

    @FXML
    private Label userName;

    @FXML
    private Label userId;

    public void setData(FineItems fineItems) {
        bookName.setText(fineItems.getBookName());
        bookId.setText(fineItems.getBookId());
        reason.setText(fineItems.getReason());
        issuedAt.setText(fineItems.getIssuedAt());
        if (userName != null && userId != null) {
            userName.setText(fineItems.getUserName());
            userId.setText(fineItems.getUserId());
        }
    }

}
