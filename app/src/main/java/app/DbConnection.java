package app;

import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;

import app.Validator;
import app.Event;

import org.apache.commons.lang3.RandomStringUtils; // eventCode generation

public class DbConnection{

    // MULTI-THREADING NOT SUPPORTED 
    private Connection conn;

    // public static void main(String args[]){};

    /**
     * Constructor, initializes db connection
     * @throws SQLException
     */
    public DbConnection() throws SQLException {
        // see: jdbs.postgres.org/documentation/head/connect.html
        String url = "jdbc:postgresql:database.db";
        this.conn = DriverManager.getConnection(url);
    }

    /**
     * Close the DB Connection: conn
     * @throws SQLException
     */
    public void closeConnection() throws SQLException{
        this.conn.close();
    }

    /**
     * Create an event in the database
     * @param title
     * @param desc
     * @param eventType
     * @param hostID
     * @return
     */
    public Event createEvent(String title, String desc, String eventType, int hostID){
        // generate unique eventCode
        String eventCode = generateUniqueEventCode();

        PreparedStatement stmt = null;
        ResultSet rs = null;
        Integer eventID = null;
        try{
            String createEvent = ""
                + "INSERT INTO event(title, desc, eventType, eventCode, hostID) "
                + "VALUES(?, ?, ?, ?, ?) "
                + "RETURNING eventID";
            stmt = this.conn.prepareStatement(createEvent);
            stmt.setString(1, title);
            stmt.setString(2, desc);
            stmt.setString(3, eventType);
            stmt.setString(4, eventCode);
            stmt.setInt(4, hostID);

            rs = stmt.executeQuery();
            if (rs.next()) {
                eventID = rs.getInt("eventID");
            }
        } catch (SQLException e){
            //throw e;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }

        // get Event from eventID
        return getEvent(eventID);
    }

    /**
     * Get an event ID from an event code.
     * @param eventCode 4-digit alphanumeric event code
     * @return Event object corresponding to eventCode
     */
    public Event getEventFromEventCode(String eventCode){
        eventCode = Validator.sanitizeEventCode(eventCode);
        if (eventCode == null)
            return null;
        if (!eventCodeExists(eventCode)) 
            return null;

        // eventCode sanitized and exists --> query db
        PreparedStatement stmt = null;
        Integer eventID = null;
        ResultSet rs = null;
        try{
            String queryEventID = "SELECT eventID FROM event WHERE eventCode=? LIMIT 1;";
            stmt = this.conn.prepareStatement(queryEventID);
            stmt.setString(1, eventCode);
            rs = stmt.executeQuery();
            if (rs.next()) {
                eventID = rs.getInt("eventID");
            }
        } catch (SQLException e){
            //throw e;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }
        
        // get Event from eventID
        return getEvent(eventID);
    }

    // PRIVATE METHODS

    /**
     * Get an Event object from an eventID
     * @param eventID event ID
     * @return Event object with ID of eventID
     */
    private Event getEvent(int eventID){
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Event event = null;
        try{
            // (title, desc, eventType, eventCode, hostID)
            String createEvent = ""
                + "SELECT * FROM event "
                + "WHERE event.eventID = ? "
                + "LIMIT 1;";
            stmt = this.conn.prepareStatement(createEvent);
            stmt.setInt(1, eventID);

            rs = stmt.executeQuery();
            if (rs.next()) {
                int hostID = rs.getInt("hostID");
                String title = rs.getString("title");
                String desc = rs.getString("desc");
                String type = rs.getString("type");
                String eventCode = rs.getString("eventCode");
                event = new Event(eventID, hostID, title, desc, type, eventCode);
            }
        } catch (SQLException e){
            //throw e;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }
        return event;
    }

    /**
     * Check if a given event code exists
     * @param eventCode pre-sanitized event code
     * @return existence state of the given event code
     */
    private boolean eventCodeExists(String eventCode){
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Boolean codeExists = null;
        try{
            String createEvent = ""
                + "SELECT EXISTS(SELECT 1 FROM event WHERE eventCode=?);";
            stmt = this.conn.prepareStatement(createEvent);
            stmt.setString(1, eventCode);

            rs = stmt.executeQuery();
            if (rs.next()) {
                codeExists = rs.getBoolean(1);
            }
        } catch (SQLException e){
            //throw e;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }

        return codeExists;
    }

    /**
     * Generate a unique event code 
     * (4-didit, case insensitive alpha-numeric String)
     * @return the generated event code
     */
    protected String generateUniqueEventCode(){
        String eventCode = null;
        while (eventCode == null || eventCodeExists(eventCode)){
            eventCode = RandomStringUtils.randomAlphanumeric(4).toUpperCase();
        }

        return eventCode;
    }

}