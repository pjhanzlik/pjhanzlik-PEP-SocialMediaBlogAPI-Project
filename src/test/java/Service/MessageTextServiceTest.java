package Service;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.junit.Test;

import DAO.MessageDAO;

public class MessageTextServiceTest {
    private static final String OLD_MESSAGE_TEXT = "note";
    private static final String NEW_MESSAGE_TEXT = "Hi, I'm new here!";
    private static final String CREATE_TABLE = "CREATE TABLE message (message_id INT PRIMARY KEY AUTO_INCREMENT, posted_by INT, message_text VARCHAR(255), time_posted_epoch BIGINT);";
    private static final String SELECT = "SELECT message_id, posted_by, message_text, time_posted_epoch FROM message;";
    private static final String DATABASE_URL = "jdbc:h2:mem:";
    private static final int FIRST_MESSAGE_ID = 1;
    private static final int POSTED_BY = 1;
    private static final long TIME_POSTED_EPOCH = 0;
    private static final MessageTextService MESSAGE_TEXT_SERVICE = new MessageTextService(NEW_MESSAGE_TEXT);
    private static final Optional<MessageDAO> AFTER_UPDATE = Optional.of(new MessageDAO(FIRST_MESSAGE_ID, POSTED_BY, NEW_MESSAGE_TEXT, TIME_POSTED_EPOCH));
    private static final String INSERT = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (%d, '%s', %d)".formatted(POSTED_BY, OLD_MESSAGE_TEXT, TIME_POSTED_EPOCH);

    @Test
    public void updateReturnsData() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
            conn.prepareStatement(CREATE_TABLE).execute();
            conn.prepareStatement(INSERT).execute();
            assertEquals(
                AFTER_UPDATE,
                MESSAGE_TEXT_SERVICE.update(conn, FIRST_MESSAGE_ID));
        }
    }

    @Test 
    public void updateChangesStoredData() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
            conn.prepareStatement(CREATE_TABLE).execute();
            conn.prepareStatement(INSERT).execute();
            MESSAGE_TEXT_SERVICE.update(conn, FIRST_MESSAGE_ID);
            try (ResultSet result = conn.prepareStatement(SELECT).executeQuery()) {
                assert(result.first());
                assert(result.isLast());
                assertEquals(FIRST_MESSAGE_ID, result.getInt(1));
                assertEquals(POSTED_BY, result.getInt(2));
                assertEquals(NEW_MESSAGE_TEXT, result.getString(3));
                assertEquals(TIME_POSTED_EPOCH, result.getLong(4));
            }
        }
    }
}
