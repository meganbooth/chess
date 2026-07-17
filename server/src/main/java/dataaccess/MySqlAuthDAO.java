package dataaccess;

import model.AuthData;

public class MySqlAuthDAO implements AuthDAO{
    @Override
    public void clear() throws DataAccessException {
        throw new DataAccessException("not implemented");
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        throw new DataAccessException("not implemented");
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        throw new DataAccessException("not implemented");
    }
}
