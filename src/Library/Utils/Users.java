package Library.Utils;

public class Users {
    public int userid;
    public String username;
    public String email;
    public int role;
    public String userProfile;
    public int totalBooks;
    public int totalFine;
    public int issuedBook;
    public String joinon;
    private String roleName;

    public Users(int userid, String username, String email, int role, String userProfile, int totalBooks, int totalFine, int issuedBook, String joinon) {
        this.userid = userid;
        this.username = username;
        this.email = email;
        this.role = role;
        this.userProfile = userProfile;
        this.totalBooks = totalBooks;
        this.totalFine = totalFine;
        this.issuedBook = issuedBook;
        this.joinon = joinon;
    }

    public Users(int userid, String username, String email, int role, String joinon) {
        this.userid = userid;
        this.username = username;
        this.email = email;
        this.role = role;
        this.joinon = joinon;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName() {
        Utils utils = new Utils();
        this.roleName = utils.roleArr[this.role];
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public int getTotalBooks() {
        return totalBooks;
    }

    public void setTotalBooks(int totalBooks) {
        this.totalBooks = totalBooks;
    }

    public int getTotalFine() {
        return totalFine;
    }

    public void setTotalFine(int totalFine) {
        this.totalFine = totalFine;
    }

    public int getIssuedBook() {
        return issuedBook;
    }

    public void setIssuedBook(int issuedBook) {
        this.issuedBook = issuedBook;
    }

    public String getJoinon() {
        return joinon;
    }

    public void setJoinon(String joinon) {
        this.joinon = joinon;
    }
}
