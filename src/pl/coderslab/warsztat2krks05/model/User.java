package pl.coderslab.warsztat2krks05.model;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    private int id;
    private String username;
    private String email;
    private String password;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        setPassword(password);
    }

    public User() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public boolean isPasswordCorrect(String candidate) {
        return BCrypt.checkpw(candidate, this.password);
    }

    public void setPassword(String password) {
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public void saveToDB(Connection conn)
                throws SQLException {
        if(this.id==0){
            final String sql = "INSERT INTO users(username, email, password) " +
                    "VALUES(?, ?, ?);";

            String[] generatedValues = {"id"};

            PreparedStatement ps = conn.prepareStatement(sql, generatedValues);
            ps.setString(1, this.username);
            ps.setString(2, this.email);
            ps.setString(3, this.password);

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
                this.id = rs.getInt(1);
            }
            rs.close();
            ps.close();
        } else {
            //TODO update db
            throw new SQLException("Not implemented!");
        }
    }

    public static User loadUserById(Connection conn, int id)
                throws SQLException {
        final String sql = "SELECT id, username, email, password " +
                "FROM users WHERE id = ?;";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            User u = new User();
            u.id = rs.getInt("id");
            u.username = rs.getString("username");
            u.email = rs.getString("email");
            u.password = rs.getString("password");

            return u;
        }
        rs.close();
        ps.close();

        return null;
    }
}
