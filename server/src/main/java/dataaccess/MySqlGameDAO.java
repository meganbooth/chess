package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.List;

public class MySqlGameDAO implements GameDAO{
    @Override
    public int getNextGameID() throws DataAccessException {
        return 0;
    }

    @Override
    public void clear() throws DataAccessException {
        throw new DataAccessException("not implemented");
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        throw new DataAccessException("not implemented");
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        throw new DataAccessException("not implemented");
    }
}
