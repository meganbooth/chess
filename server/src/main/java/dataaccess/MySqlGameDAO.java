package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import static dataaccess.DatabaseManager.getConnection;

public class MySqlGameDAO implements GameDAO{
    @Override
    public int getNextGameID() throws DataAccessException {
        var statement = "SELECT MAX(gameID) AS maxID FROM Games";
        try (var conn = getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {
            var rs = preparedStatement.executeQuery();
            if (rs.next()) {
                int maxID = rs.getInt("maxID");
                return maxID + 1;
            }
            return 1;
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get next game ID", ex);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "DELETE FROM Games";
        try (var conn = getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to clear games", ex);
        }
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        var statement = "INSERT INTO Games (gameID, whiteUsername, blackUsername, gameName, gameState) VALUES (?, ?, ?, ?, ?)";
        try (var conn = getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.setInt(1, game.gameID());
            preparedStatement.setString(2, game.whiteUsername());
            preparedStatement.setString(3, game.blackUsername());
            preparedStatement.setString(4, game.gameName());
            preparedStatement.setString(5, new Gson().toJson(game.game()));
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to insert game", ex);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        var statement = "SELECT gameID, whiteUsername,blackUsername, gameName, gameState FROM Games WHERE gameID = ?";
        try (var conn = getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.setInt(1, gameID);
            var rs = preparedStatement.executeQuery();
            if (rs.next()) {
                var resultGameID = rs.getInt("gameID");
                var resultWhiteUsername = rs.getString("whiteUsername");
                var resultBlackUsername = rs.getString("blackUsername");
                var resultGameName = rs.getString("gameName");
                var resultGameStateJson = rs.getString("gameState");
                var resultGameState = new Gson().fromJson(resultGameStateJson, ChessGame.class);

                return new GameData(resultGameID,resultWhiteUsername,resultBlackUsername,resultGameName,resultGameState);
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get game", ex);
        }
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, gameState FROM Games";
        Collection<GameData> gameList = new ArrayList<>();
        try (var conn = getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {
            var rs = preparedStatement.executeQuery();
            while (rs.next()) {
                var resultGameID = rs.getInt("gameID");
                var resultWhiteUsername = rs.getString("whiteUsername");
                var resultBlackUsername = rs.getString("blackUsername");
                var resultGameName = rs.getString("gameName");
                var resultGameStateJson = rs.getString("gameState");
                var resultGameState = new Gson().fromJson(resultGameStateJson, ChessGame.class);

                GameData nextGame = new GameData(resultGameID, resultWhiteUsername, resultBlackUsername, resultGameName, resultGameState);
                gameList.add(nextGame);
            }
            return gameList;
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get game list", ex);
        }
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        var statement = "UPDATE Games SET whiteUsername = ?, blackUsername = ?, " +
                "gameName = ?, gameState = ? WHERE gameID = ?";
        try (var conn = getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.setString(1, game.whiteUsername());
            preparedStatement.setString(2, game.blackUsername());
            preparedStatement.setString(3, game.gameName());
            preparedStatement.setString(4, new Gson().toJson(game.game()));
            preparedStatement.setInt(5, game.gameID());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to update game", ex);
        }
    }
}
