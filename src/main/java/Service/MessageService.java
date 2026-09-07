package Service;

import java.sql.Connection;
import java.sql.SQLException;

import com.fasterxml.jackson.annotation.JsonProperty;

import DAO.MessageDAO;

public class MessageService {
    private final int postedBy;
    private final String messageText;
    private final long timePostedEpoch;

    /**
     * Constructs a MessageService, mostly used to parse corresponding
     * fields from JSON.
     * @param postedBy posted_by field
     * @param messageText message_text field
     * @param timePostedEpoch time_posted_epoch field
     */
    public MessageService(
            @JsonProperty("posted_by") int postedBy,
            @JsonProperty("message_text") String messageText,
            @JsonProperty("time_posted_epoch") long timePostedEpoch) {
        this.postedBy = postedBy;
        this.messageText = messageText;
        this.timePostedEpoch = timePostedEpoch;
    }

    /**
     * Creates a message record in the database correspoding to the service
     * properties.  The created record is returned.
     * @param conn Connection to the database
     * @return message record inserted into the database
     * @throws SQLException any database schema violation
     */
    public MessageDAO create(Connection conn) throws SQLException {
        MessageDAO.insert(conn, postedBy, messageText, timePostedEpoch);
        return MessageDAO.fetch(conn, postedBy, messageText, timePostedEpoch).get();
    }
}
