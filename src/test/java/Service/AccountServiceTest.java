package Service;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.junit.Test;

import DAO.AccountDAO;

public class AccountServiceTest {
    private static final String URL = "jdbc:h2:mem:";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "secret";
    private static final AccountService SERVICE = new AccountService(USERNAME, PASSWORD);
    private static final AccountDAO FIRST_ENTRY = new AccountDAO(1, SERVICE.username(), SERVICE.password());
    private static final String CREATE_TABLE = "CREATE TABLE account (account_id INT PRIMARY KEY AUTO_INCREMENT, username VARCHAR(255) UNIQUE, password VARCHAR(255));";
    private static final String INSERT = "INSERT INTO account (username, password) VALUES ('%s', '%s');"
            .formatted(USERNAME, PASSWORD);
    private static final String SELECT = "SELECT account_id, username, password FROM account;";

    @Test
    public void successfulLogin() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.prepareStatement(CREATE_TABLE).execute();
            conn.prepareStatement(INSERT).execute();
            assertEquals(Optional.of(FIRST_ENTRY), SERVICE.login(conn));
        }
    }

    @Test
    public void failedLogin() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.prepareStatement(CREATE_TABLE).execute();
            assertEquals(Optional.empty(), SERVICE.login(conn));
        }
    }

    @Test
    public void registerUserReturnsData() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.prepareStatement(CREATE_TABLE).execute();
            assertEquals(FIRST_ENTRY, SERVICE.register(conn));
        }
    }

    @Test
    public void registerUserStoresData() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.prepareStatement(CREATE_TABLE).execute();
            SERVICE.register(conn);
            try (ResultSet result = conn.prepareStatement(SELECT).executeQuery()) {
                assert (result.first());
                assert (result.isLast());
                assertEquals(1, result.getInt(1));
                assertEquals(SERVICE.username(), result.getString(2));
                assertEquals(SERVICE.password(), result.getString(3));
            }
        }
    }
}
