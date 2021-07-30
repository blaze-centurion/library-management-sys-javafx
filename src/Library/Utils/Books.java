package Library.Utils;

public class Books {
    private int SNo;
    private String bookImage;
    private String bookName;
    private String category;
    private int availableBook;
    private int totalBooks;
    private int issuedBooks;
    private int popularity;
    private int catId;
    private int bookId;

    public Books(int SNo, String bookImage, String bookName, String category, int availableBook, int totalBooks, int issuedBooks, int catId) {
        this.SNo = SNo;
        this.bookImage = bookImage;
        this.bookName = bookName;
        this.category = category;
        this.availableBook = availableBook;
        this.totalBooks = totalBooks;
        this.issuedBooks = issuedBooks;
        this.catId = catId;
    }

    public Books(int SNo, String bookImage, String bookName, String category, int availableBook, int popularity, int bookId, int catId, int type) {
        this.SNo = SNo;
        this.bookImage = bookImage;
        this.bookName = bookName;
        this.category = category;
        this.availableBook = availableBook;
        this.popularity = popularity;
        this.bookId = bookId;
        this.catId = catId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }


    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }


    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }


    public int getSNo() {
        return SNo;
    }

    public void setSNo(int SNo) {
        this.SNo = SNo;
    }

    public String getBookImage() {
        return bookImage;
    }

    public void setBookImage(String bookImage) {
        this.bookImage = bookImage;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getAvailableBook() {
        return availableBook;
    }

    public void setAvailableBook(int availableBook) {
        this.availableBook = availableBook;
    }

    public int getTotalBooks() {
        return totalBooks;
    }

    public void setTotalBooks(int totalBooks) {
        this.totalBooks = totalBooks;
    }

    public int getIssuedBooks() {
        return issuedBooks;
    }

    public void setIssuedBooks(int issuedBooks) {
        this.issuedBooks = issuedBooks;
    }
}
