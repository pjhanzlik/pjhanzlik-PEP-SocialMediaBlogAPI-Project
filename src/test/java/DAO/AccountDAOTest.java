package DAO;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import org.junit.Test;

public class AccountDAOTest {
    public static final String URL = "jdbc:h2:mem:";
    public static final String USERNAME = "user";
    public static final String PASSWORD = "secret";
    public static final String CREATE_TABLE = "CREATE TABLE account (account_id INT PRIMARY KEY AUTO_INCREMENT, username VARCHAR(255) UNIQUE, password VARCHAR(255));";
    public static final String INSERT = "INSERT INTO account (username, password) VALUES ('%s', '%s');"
            .formatted(USERNAME, PASSWORD);
    public static final String SELECT = "SELECT account_id, username, password FROM account;";
    public static final AccountDAO FIRST_ENTRY = new AccountDAO(1, USERNAME, PASSWORD);

    @Test
    public void toStringTest() {
        assertEquals(
                "{\"account_id\": %d, \"username\": \"%s\", \"password\": \"%s\"}"
                        .formatted(1, USERNAME, PASSWORD),
                new AccountDAO(1, USERNAME, PASSWORD).toString());

    }

    @Test
    public void fetchAccountWithUsernameAndPassword() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.prepareStatement(CREATE_TABLE).execute();
            conn.prepareStatement(INSERT).execute();
            assertEquals(
                    Optional.of(FIRST_ENTRY),
                    AccountDAO.fetch(conn, USERNAME, PASSWORD));

        }
    }

    @Test
    public void fetchNothingWithUsernameAndPassword() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.prepareStatement(CREATE_TABLE).execute();
            assertEquals(
                    Optional.empty(),
                    AccountDAO.fetch(conn, USERNAME, PASSWORD));
        }
    }

    @Test
    public void insertAccountWithUsernameAndPassword() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.prepareStatement(CREATE_TABLE).execute();
            AccountDAO.insert(conn, "user", "secret");
            try (ResultSet result = conn.prepareStatement(SELECT).executeQuery()) {
                assert (result.next());
                assert (result.isLast());
                assertEquals(1, result.getInt(1));
                assertEquals(USERNAME, result.getString(2));
                assertEquals(PASSWORD, result.getString(3));
            }
        }
    }
}
