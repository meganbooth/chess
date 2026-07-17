package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

import static dataaccess.DatabaseManager.getConnection;

public class MySqlUserDAO implements UserDAO {
    @Override
    public void clear() throws DataAccessException {
        var statement = "DELETE FROM Users";
        try (var conn = getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to clear user", ex);
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        var statement = "INSERT INTO Users (username, password, email) VALUES (?, ?, ?)";
        try (var conn = getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.setString(1, user.username());
            String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.setString(3, user.email());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to insert user", ex);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        var statement = "SELECT username, password, email FROM Users WHERE username = ?";
        try (var conn = getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.setString(1, username);
            var rs = preparedStatement.executeQuery();
            if (rs.next()) {
                var resultUsername = rs.getString("username");
                var resultPassword = rs.getString("password");
                var resultEmail = rs.getString("email");

                UserData user = new UserData(resultUsername,resultPassword,resultEmail);
                return user;
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get user", ex);
        }
        return null;
    }
}
