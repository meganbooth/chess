package dataaccess;

import model.UserData;

public class MySqlUserDAO implements UserDAO {
    @Override
    public void clear() throws DataAccessException {
        throw new DataAccessException("not implemented");
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        throw new DataAccessException("not implemented");
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }
}
