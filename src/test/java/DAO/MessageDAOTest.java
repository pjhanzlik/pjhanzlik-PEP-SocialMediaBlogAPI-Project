package DAO;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.Test;

public class MessageDAOTest {
    public static final String URL = "jdbc:h2:mem:";
    public static final int POSTED_BY = 1;
    public static final String MESSAGE_TEXT = "note";
    public static final long TIME_POSTED_EPOCH = 0;
    public static final String CREATE_TABLE = "CREATE TABLE message (message_id INT PRIMARY KEY AUTO_INCREMENT, posted_by INT, message_text VARCHAR(255), time_posted_epoch BIGINT);";
    public static final String INSERT = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (%d, '%s', %d)"
            .formatted(POSTED_BY, MESSAGE_TEXT, TIME_POSTED_EPOCH);
    public static final int ANOTHER_POSTED_BY = 2;
    public static final String ANOTHER_INSERT = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (%d, '%s', %d)"
            .formatted(ANOTHER_POSTED_BY, MESSAGE_TEXT, TIME_POSTED_EPOCH);
    public static final String SELECT = "SELECT message_id, posted_by, message_text, time_posted_epoch FROM message;";
    public static final MessageDAO FIRST_ENTRY = new MessageDAO(
            1,
            POSTED_BY,
            MESSAGE_TEXT,
            TIME_POSTED_EPOCH);

    @Test
    public void json() {
        assertEquals(
                "{\"message_id\": %d, \"posted_by\": %d, \"message_text\": \"%s\", \"time_posted_epoch\": %d}"
                        .formatted(1, POSTED_BY, MESSAGE_TEXT, TIME_POSTED_EPOCH),
                FIRST_ENTRY.toString());
    }

    @Test
    public void delete() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.prepareStatement(CREATE_TABLE).execute();
            conn.prepareStatement(INSERT).execute();
            MessageDAO.delete(conn, 1);
            try (ResultSet result = conn
                    .prepareStatement(SELECT).executeQuery()) {
                assert (!result.first());
            }
        }
    }

    @Test
    public void deleteNothing() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL);) {
            conn.prepareStatement(CREATE_TABLE).execute();
            MessageDAO.delete(conn, 1);
            try (ResultSet result = conn
                    .prepareStatement(SELECT).executeQuery()) {
                assert (!result.first());
            }
        }
    }

    @Test
    public void fetchMessageFromContents() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL);) {
            conn.prepareStatement(CREATE_TABLE).execute();
            conn.prepareStatement(INSERT).execute();
            assertEquals(
                    Optional.of(FIRST_ENTRY),
                    MessageDAO.fetch(
                            conn,
                            POSTED_BY,
                            MESSAGE_TEXT,
                            TIME_POSTED_EPOCH));
        }
    }

    @Test
    public void fetchNothingFromContents() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL);) {
            conn.prepareStatement(CREATE_TABLE).execute();
            assertEquals(
                    Optional.empty(),
                    MessageDAO.fetch(
                            conn,
                            POSTED_BY,
                            MESSAGE_TEXT,
                            TIME_POSTED_EPOCH));
        }
    }

    @Test
    public void fetchMessageFromId() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.prepareStatement(CREATE_TABLE).execute();
            conn.prepareStatement(INSERT).execute();
            assertEquals(
                    Optional.of(FIRST_ENTRY),
                    MessageDAO.fetch(conn, 1));
        }
    }

    @Test
    public void fetchNothingFromId() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.prepareStatement(CREATE_TABLE).execute();
            assertEquals(
                    Optional.empty(),
                    MessageDAO.fetch(conn, 1));
        }
    }

    @Test
    public void insert() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL);) {
            conn.prepareStatement(CREATE_TABLE).execute();
            MessageDAO.insert(conn, POSTED_BY, MESSAGE_TEXT, TIME_POSTED_EPOCH);
            try (ResultSet result = conn.prepareStatement(SELECT).executeQuery()) {
                assert (result.first());
                assert (result.isLast());
                assertEquals(1, result.getInt(1));
                assertEquals(POSTED_BY, result.getInt(2));
                assertEquals(MESSAGE_TEXT, result.getString(3));
                assertEquals(TIME_POSTED_EPOCH, result.getLong(4));
            }
        }
    }

    @Test
    public void selectAll() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.prepareStatement(CREATE_TABLE).execute();
            conn.prepareStatement(INSERT).execute();
            conn.prepareStatement(ANOTHER_INSERT).execute();
            conn.prepareStatement(INSERT).execute();
            List<MessageDAO> list = MessageDAO.select(conn);
            MessageDAO[] arr = {FIRST_ENTRY, 
                new MessageDAO(2, ANOTHER_POSTED_BY, MESSAGE_TEXT, TIME_POSTED_EPOCH), 
                new MessageDAO(3, POSTED_BY, MESSAGE_TEXT, TIME_POSTED_EPOCH)};
            List<MessageDAO> expect = Arrays.asList(arr);
            assertEquals(expect, list);
        }
    }

    @Test
    public void selectAllFromAccount() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.prepareStatement(CREATE_TABLE).execute();
            conn.prepareStatement(INSERT).execute();
            conn.prepareStatement(ANOTHER_INSERT).execute();
            conn.prepareStatement(INSERT).execute();
            List<MessageDAO> list = MessageDAO.select(conn, POSTED_BY);
            MessageDAO[] arr = {FIRST_ENTRY, 
                new MessageDAO(3, POSTED_BY, MESSAGE_TEXT, TIME_POSTED_EPOCH)};
            List<MessageDAO> expect = Arrays.asList(arr);
            assertEquals(expect, list);
        }
    }

    @Test
    public void updateMessage() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.prepareStatement(CREATE_TABLE).execute();
            conn.prepareStatement(INSERT).execute();
            MessageDAO.update(conn, POSTED_BY, "changed");
            try (ResultSet result = conn.prepareStatement(SELECT).executeQuery()) {
                assert(result.first());
                assert(result.isLast());
                assertEquals(1, result.getInt(1));
                assertEquals(POSTED_BY, result.getInt(2));
                assertEquals("changed", result.getString(3));
                assertEquals(TIME_POSTED_EPOCH, result.getLong(4));
            }
        }
    }
}
