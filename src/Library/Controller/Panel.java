package Library.Controller;

import Library.Utils.DataBase;
import Library.Utils.Users;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public abstract class Panel {
    public int userId;
    public Label panelNameLabel;
    public VBox sidebar;
    Users user = null;

    abstract public void setUserId(int userId);

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Users configureUser(Circle profileAvatar) {
        DataBase db = new DataBase();
        Connection conn = db.getConnection();
        Statement st;
        ResultSet rs;
        try {
            st = conn.createStatement();
            rs = st.executeQuery("SELECT * FROM users WHERE userid=" + userId);
            while (rs.next()) {
                user = new Users(rs.getInt("userid"), rs.getString("username"), rs.getString("email"), rs.getInt("role"), rs.getString("userProfile"), rs.getInt("totalBooks"), rs.getInt("totalFine"), rs.getInt("issuedBook"), rs.getString("joinon"));
                Image img = new Image(user.getUserProfile());
                profileAvatar.setFill(new ImagePattern(img));
            }
            return user;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void getParentItems(Label panelNameLabel, VBox sidebar) {
        this.sidebar = sidebar;
        this.panelNameLabel = panelNameLabel;
    }
}
