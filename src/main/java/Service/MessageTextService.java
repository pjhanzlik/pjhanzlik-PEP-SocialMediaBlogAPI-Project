package Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import DAO.MessageDAO;

public class MessageTextService {
    private String messageText;

    /**
     * 
     * @param messageText message_text property
     */
    public MessageTextService(@JsonProperty("message_text") String messageText) {
        this.messageText = messageText;
    }

    /**
     * Updates a message record with a particular id to have the message text 
     * from this service.
     * @param conn Connection to the database
     * @param messageId message_id field
     * @return updated message record if the record existed
     * @throws SQLException any database schema violation
     */
    public Optional<MessageDAO> update(Connection conn, int messageId) throws SQLException {
        MessageDAO.update(conn, messageId, messageText);
        return MessageDAO.fetch(conn, messageId);
    }
}
