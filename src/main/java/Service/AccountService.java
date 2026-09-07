package Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import DAO.AccountDAO;

public class AccountService {
    private final String username;
    private final String password;

    /**
     * @return username field
     */
    String username() {
        return username;
    }

    /**
     * @return password field
     */
    String password() {
        return password;
    }

    /**
     * This constructor is mostly used to create a AccountService from a JSON body.
     * @param username  property corresponding to an account username field
     * @param password property corresponding to an account password field
     */
    public AccountService(@JsonProperty("username") String username, @JsonProperty("password") String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Retrieves an account record if and only if username and password
     * match an existing record in the database.
     * @param conn Connection to the database
     * @return account record if it exists
     * @throws SQLException any database violation
     */
    public Optional<AccountDAO> login(Connection conn) throws SQLException {
        return AccountDAO.fetch(conn, this.username, this.password);
    }

    /**
     * Inserts an account record into the database and returns it.
     * @param conn Connection to the database
     * @return the inserted account record
     * @throws SQLException any database violtion
     */
    public AccountDAO register(Connection conn) throws SQLException {
        AccountDAO.insert(conn, this.username, this.password);
        return AccountDAO.fetch(conn, this.username, this.password).get();
    }
}
