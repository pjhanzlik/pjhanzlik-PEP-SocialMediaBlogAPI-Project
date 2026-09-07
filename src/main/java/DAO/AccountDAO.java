package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AccountDAO repesents a record in the account table.
 * It contains static methods corresponding to atomic database operations.
 */
public final class AccountDAO {
    private final int accountId;
    private final String username;
    private final String password;

    /**
     * @return account_id field
     */
    @JsonProperty("account_id") int accountId() {
        return accountId;
    }

    /**
     * @return username field
     */
    @JsonProperty("username") String username() {
        return username;
    }

    /**
     * @return password field
     */
    @JsonProperty("password") String password() {
        return password;
    }

    @Override
    public boolean equals(Object obj) {
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        AccountDAO other = (AccountDAO) obj;
        return this.accountId == other.accountId && this.username.equals(other.username)
                && this.password.equals(other.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.accountId, this.username, this.password);
    }

    @Override
    public String toString() {
        return "{\"account_id\": %d, \"username\": \"%s\", \"password\": \"%s\"}".formatted(
                this.accountId,
                this.username,
                this.password);
    }

    /**
     * Represents an account record. Most likely you do not want to call
     * this constructor unless you are validating a returned record.
     * Instead, use static methods of this class to create and fetch 
     * records directly from the database.
     * @param accountId account_id field
     * @param username username field
     * @param password password field
     */
    public AccountDAO(int accountId, String username, String password) {
        this.accountId = accountId;
        this.username = username;
        this.password = password;
    }

    /**
     * Searches for an account record with matching username and password.
     * Does not do any constraint validation. You must do that with your
     * database schema.
     * @param conn Connection to the database
     * @param username username field
     * @param password password field
     * @return account record if it exists
     * @throws SQLException any database schema violation
     */
    public static final Optional<AccountDAO> fetch(Connection conn, String username, String password)
            throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(
                "SELECT account_id, username, password FROM account WHERE username=? AND password=?;")) {
            statement.setString(1, username);
            statement.setString(2, password);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.first()) {
                    return Optional.empty();
                }
                return Optional.of(new AccountDAO(result.getInt(1), result.getString(2), result.getString(3)));
            }
        }
    }

    /**
     * Inserts username and password into the account table.
     * Does not do any constraint validation. You must do that with your
     * database schema.
     * @param conn Connection to the database
     * @param username username field
     * @param password password field
     * @throws SQLException any database schema violation
     */
    public static final void insert(Connection conn, String username, String password) throws SQLException {
        try (PreparedStatement statement = conn
                .prepareStatement("INSERT INTO account (username, password) VALUES (?, ?);");) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.execute();
        }
    }
}