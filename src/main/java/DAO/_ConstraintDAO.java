package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This helper class should not exist, but we need some way
 * to add constraints to our database.
 * 
 * Technically we also should add a bunch of null constraints
 * and indexes on `account (username)` and 
 * `message (posted_by, message_text, time_posted_epoch)` based
 * on user stories, but that honestly would just slow down our
 * tests.
 * 
 * We should also add constraints on username and password to
 * prevent whitespace at the start and end of these values.
 */
public class _ConstraintDAO {
    /**
     * Adds a check constraint on `account (username)` for non-empty strings.
     * @return true if check constraint was successfully added
     * @throws SQLException if database cannot be connected, read, written, or already has constraint
     */
    public static void addAccountUsernameEmptyString(Connection conn) throws SQLException {
        try (
            PreparedStatement statement = conn.prepareStatement("ALTER TABLE account ADD CONSTRAINT username_not_empty CHECK (username != '');");
        ) {
            statement.execute();
        }
    }

    /**
     * Adds a check constraint on `account (password)` for character length.
     * @param length minimum length of the account (username) field
     * @return true if check constraint was successfully added
     * @throws SQLException if database cannot be connected, read, written, or already has constraint
     */
    public static void addPasswordMinimumLength(Connection conn, long length) throws SQLException {
        try (
            PreparedStatement statement = conn.prepareStatement("ALTER TABLE account ADD CONSTRAINT password_minimum_length CHECK (CHAR_LENGTH(password) >= ?);");
        ) {
            statement.setLong(1, length);
            statement.execute();
        }
    }

    /**
     * Adds a check constraint on `message (message_text)` for non-empty strings.
     * @return true if check constraint was successfully added
     * @throws SQLException if database cannot be connected, read, written, or already has constraint
     */
    public static void addMessageTextEmptyString(Connection conn) throws SQLException {
        try (
            PreparedStatement statement = conn.prepareStatement("ALTER TABLE message ADD CONSTRAINT message_text_not_empty CHECK (message_text != '');");
        ) {
            statement.execute();
        }
    }
}
