package Library.Utils;

public class BookHis {
    private String bookName;
    private int bookId;
    private String issuedAt;
    private String returnAt;
    private String userName;
    private int userId;
    private int fine;
    private int id;

    public BookHis() {
    }

    public BookHis(String bookName, int bookId, String issuedAt, String returnAt, String userName, int userId, int fine) {
        this.bookName = bookName;
        this.bookId = bookId;
        this.issuedAt = issuedAt;
        this.returnAt = returnAt;
        this.userName = userName;
        this.userId = userId;
        this.fine = fine;
    }

    public BookHis(String bookName, int bookId, String issuedAt, String returnAt, int id, int fine) {
        this.bookName = bookName;
        this.bookId = bookId;
        this.issuedAt = issuedAt;
        this.returnAt = returnAt;
        this.fine = fine;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getReturnAt() {
        return returnAt;
    }

    public void setReturnAt(String returnAt) {
        this.returnAt = returnAt;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFine() {
        return fine;
    }

    public void setFine(int fine) {
        this.fine = fine;
    }

    public String getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(String issuedAt) {
        this.issuedAt = issuedAt;
    }
}
