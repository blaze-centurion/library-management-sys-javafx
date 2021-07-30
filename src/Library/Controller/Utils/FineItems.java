package Library.Controller.Utils;

public class FineItems {
    private String bookName;
    private String bookId;
    private String reason;
    private String issuedAt;
    private int fine;
    private String userName;
    private String userId;

    public int getFine() {
        return fine;
    }

    public void setFine(int totalFine) {
        this.fine = totalFine;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = "User Id: " + userId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = "S.No. " + bookId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(String issuedAt) {
        this.issuedAt = "Issued At: " + issuedAt;
    }
}
