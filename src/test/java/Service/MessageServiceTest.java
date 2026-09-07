package Service;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;

import DAO.MessageDAO;

public class MessageServiceTest {
    private static final String CREATE_TABLE = "CREATE TABLE message (message_id INT PRIMARY KEY AUTO_INCREMENT, posted_by INT, message_text VARCHAR(255), time_posted_epoch BIGINT);";
    private static final String SELECT = "SELECT message_id, posted_by, message_text, time_posted_epoch FROM message;";
    private static final String DATABASE_URL = "jdbc:h2:mem:";
    private static final int FIRST_MESSAGE_ID = 1;
    private static final int FIRST_POSTED_BY = 1;
    private static final String MESSAGE_TEXT = "note";
    private static final long TIME_POSTED_EPOCH = 0;
    private static final MessageService MESSAGE_SERVICE = new MessageService(FIRST_POSTED_BY, MESSAGE_TEXT, TIME_POSTED_EPOCH);

    @Test 
    public void createInsertsData() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
            conn.prepareStatement(CREATE_TABLE).execute();
            MESSAGE_SERVICE.create(conn);
            try (ResultSet result = conn.prepareStatement(SELECT).executeQuery()) {
                assert(result.first());
                assert(result.isLast());
                assertEquals(FIRST_MESSAGE_ID, result.getInt(1));
                assertEquals(FIRST_POSTED_BY, result.getInt(2));
                assertEquals(MESSAGE_TEXT, result.getString(3));
                assertEquals(TIME_POSTED_EPOCH, result.getLong(4));
            }
        }
    }

    @Test 
    public void createReturnsData() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
            conn.prepareStatement(CREATE_TABLE).execute();
            MessageDAO message = MESSAGE_SERVICE.create(conn);
            assertEquals(new MessageDAO(FIRST_MESSAGE_ID, FIRST_POSTED_BY, MESSAGE_TEXT, TIME_POSTED_EPOCH), message);
        }
    }
}
