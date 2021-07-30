package Library.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DataBase {

    public Connection getConnection() {
        Connection conn;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/librarymanagement", "root", "");
            return conn;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public void executeUpdateQuery(String query) throws Exception {
        Connection conn = getConnection();
        Statement st;
        st = conn.createStatement();
        st.executeUpdate(query);
    }

    public ResultSet executeQuery(String query) throws Exception {
        Connection conn = getConnection();
        Statement st;
        ResultSet rs;
        st = conn.createStatement();
        rs = st.executeQuery(query);
        return rs;
    }
}
