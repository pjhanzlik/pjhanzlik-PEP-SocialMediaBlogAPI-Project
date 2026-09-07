package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class MessageDAO {
    private final int messageId;
    private final int postedBy;
    private final String messageText;
    private final long timePostedEpoch;

    @Override
    public String toString() {
        return "{\"message_id\": %d, \"posted_by\": %d, \"message_text\": \"%s\", \"time_posted_epoch\": %d}"
                .formatted(messageId, postedBy, messageText, timePostedEpoch);
    }

    @Override
    public boolean equals(Object obj) {
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        MessageDAO other = (MessageDAO) obj;
        return this.messageId == other.messageId() && this.postedBy == other.postedBy()
                && this.messageText.equals(other.messageText())
                && this.timePostedEpoch == other.timePostedEpoch;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.messageId, this.postedBy, this.messageText, this.timePostedEpoch);
    }

    /**
     * Represents a message record.  Most likely you do not want to call
     * this constructor unless you are validating a returned record has
     * a specific form.  Instead, use static methods of this class
     * to create and fetch records directly from the database.
     * @param messageId message_id field
     * @param postedBy posted_by field
     * @param messageText message_text field
     * @param timePostedEpoch time_posted_epoch field
     */
    public MessageDAO(
        @JsonProperty("message_id") int messageId, 
        @JsonProperty("posted_by") int postedBy, 
        @JsonProperty("message_text") String messageText, 
        @JsonProperty("time_posted_epoch") long timePostedEpoch) {
        this.messageId = messageId;
        this.postedBy = postedBy;
        this.messageText = messageText;
        this.timePostedEpoch = timePostedEpoch;
    }

    /**
     * @return message_id field
     */
    @JsonProperty("message_id") int messageId() {
        return this.messageId;
    }

    /**
     * @return posted_by field
     */
    @JsonProperty("posted_by") int postedBy() {
        return this.postedBy;
    }

    /**
     * @return message_text field
     */
    @JsonProperty("message_text") String messageText() {
        return this.messageText;
    }

    /**
     * @return time_posted_epoch field
     */
    @JsonProperty("time_posted_epoch") long timePostedEpoch() {
        return this.timePostedEpoch;
    }

    /**
     * Deletes message record with message_id if it exists.
     * @param conn Connection to the database
     * @param messageId message_id field
     * @throws SQLException any database schema violation
     */
    public static final void delete(Connection conn, int messageId) throws SQLException {
        try (PreparedStatement statement = conn
                .prepareStatement("DELETE FROM message WHERE message_id=?;")) {
            statement.setInt(1, messageId);
            statement.execute();
        }
    }

    /**
     * Returns the first matching message record in the database if it exists. Does
     * not do any constraint validation. You have to do that in the database
     * schema and then handle any thrown SQLException yourself.
     * @param conn Connection to the database
     * @param postedBy posted_by field
     * @param messageText message_text field
     * @param timePostedEpoch time_posted_epoch field
     * @return message record if it exists
     * @throws SQLException any database schema violation
     */
    public static final Optional<MessageDAO> fetch(Connection conn, int postedBy, String messageText,
            long timePostedEpoch)
            throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(
                "SELECT message_id, posted_by, message_text, time_posted_epoch FROM message WHERE posted_by=? AND message_text=? AND time_posted_epoch=? FETCH FIRST ROW ONLY;")) {
            statement.setInt(1, postedBy);
            statement.setString(2, messageText);
            statement.setLong(3, timePostedEpoch);
            try (ResultSet result = statement.executeQuery()) {
                if (result.first()) {
                    return Optional.of(
                            new MessageDAO(
                                    result.getInt(1),
                                    result.getInt(2),
                                    result.getString(3),
                                    result.getLong(4)));
                }
                return Optional.empty();
            }
        }
    }

    /**
     * Returns the first matching message record in the database if it exists. Does
     * not do any constraint validation. You have to do that in the database
     * schema and then handle any thrown SQLException yourself.
     * @param conn Connection to the database
     * @param messageId message_id field
     * @return message record if it exists
     * @throws SQLException any database schema violation
     */
    public static final Optional<MessageDAO> fetch(Connection conn, int messageId) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(
                "SELECT message_id, posted_by, message_text, time_posted_epoch FROM message WHERE message_id=? FETCH FIRST ROW ONLY;")) {
            statement.setInt(1, messageId);
            try (ResultSet result = statement.executeQuery()) {
                if (result.first()) {
                    return Optional.of(
                            new MessageDAO(
                                    result.getInt(1),
                                    result.getInt(2),
                                    result.getString(3),
                                    result.getLong(4)));
                }
                return Optional.empty();
            }
        }
    }

    /**
     * Inserts a message record into the database.  Does not return
     * the record, so use the corresponding fetch method to retrieve
     * the record with a message_id field attached.
     * @param conn Connection to the database
     * @param postedBy posted_by field
     * @param messageText message_text field
     * @param timePostedEpoch time_posted_epoch field
     * @throws SQLException any database violation
     */
    public static final void insert(Connection conn, int postedBy, String messageText, long timePostedEpoch)
            throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(
                "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?);")) {
            statement.setInt(1, postedBy);
            statement.setString(2, messageText);
            statement.setLong(3, timePostedEpoch);
            statement.execute();
        }
    }

    /**
     * Retrieves every message record in the database.
     * @param conn Connection to the database
     * @return a list containing every message record
     * @throws SQLException any database violation
     */
    public static final List<MessageDAO> select(Connection conn) throws SQLException {
        try (ResultSet result = conn.prepareStatement(
                "SELECT message_id, posted_by, message_text, time_posted_epoch FROM message")
                .executeQuery();) {
            List<MessageDAO> list = new LinkedList<>();
            while (result.next()) {
                list.add(new MessageDAO(result.getInt(1), result.getInt(2), result.getString(3), result.getLong(4)));
            }
            return list;
        }
    }

    /**
     * Retrieves every message record in the database posted by a particular user.
     * @param conn Connection to the database
     * @param postedBy posted_by field
     * @return a list containing message records for a particular poster
     * @throws SQLException any database violation
     */
    public static final List<MessageDAO> select(Connection conn, int postedBy) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(
                "SELECT message_id, posted_by, message_text, time_posted_epoch FROM message WHERE posted_by=?;");) {
            statement.setInt(1, postedBy);
            try (ResultSet result = statement.executeQuery()) {
                List<MessageDAO> list = new LinkedList<>();
                while (result.next()) {
                    list.add(
                            new MessageDAO(result.getInt(1), result.getInt(2), result.getString(3), result.getLong(4)));
                }
                return list;
            }
        }
    }

    /**
     * Updates the message_text for a particular message record.
     * @param conn Connection to the database
     * @param messageId message_id field
     * @param messageText text that will be placed in the message_text field
     * @throws SQLException any database violation
     */
    public static final void update(Connection conn, int messageId, String messageText) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(
                "UPDATE message SET message_text=? WHERE message_id=? FETCH FIRST ROW ONLY;")) {
            statement.setString(1, messageText);
            statement.setInt(2, messageId);
            statement.execute();
        }
    }
}
