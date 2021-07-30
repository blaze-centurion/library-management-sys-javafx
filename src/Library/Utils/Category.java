package Library.Utils;

public class Category {
    private int catId;
    private String catName;
    private int totalBooks;
    private int availableBooks;
    private int popularity;

    public Category(int catId, String catName, int totalBooks, int availableBooks, int popularity) {
        this.catId = catId;
        this.catName = catName;
        this.totalBooks = totalBooks;
        this.availableBooks = availableBooks;
        this.popularity = popularity;
    }

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public int getTotalBooks() {
        return totalBooks;
    }

    public void setTotalBooks(int totalBooks) {
        this.totalBooks = totalBooks;
    }

    public int getAvailableBooks() {
        return availableBooks;
    }

    public void setAvailableBooks(int availableBooks) {
        this.availableBooks = availableBooks;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }
}
