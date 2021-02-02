package app;

import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
// import java.sql.Statement;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;


public class DbConnection{

    private Connection conn;

    public static void main(String args[]){};

    public void DbConnection() throws SQLException {
        // see: jdbs.postgres.org/documentation/head/connect.html
        String url = "jdbc:postgresql:database.db";
        this.conn = DriverManager.getConnection(url);
    }

    public void closeConnection() throws SQLException{
        this.conn.close();
    }

    public void createEvent(String title, String desc, String eventType, int hostID){
        // generate unique eventCode
        String eventCode = generateUniqueEventCode();
    }

    /**
     * Get an event ID from an event code.
     * @param eventCode 4-digit alphanumeric event code
     * @return eventID corresponding to eventCode; null otherwise
     */
    public Integer getEventFromEventCode(String eventCode){
        eventCode = sanitizeEventCode(eventCode);
        if (eventCode == null)
            return null;
        if (!eventCodeExists(eventCode)) 
            return null;

        // eventCode sanitized and exists --> query db
        PreparedStatement   stmt  = null;
        ResultSet           rs    = null;
        Integer             eventID = null;
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
        return eventID;
    }

    // PRIVATE METHODS

    private String generateUniqueEventCode(){
        String eventCode = null;
        do {
            eventCode = RandomStringUtils.randomAlphanumeric(4).toUpperCase();
        } while (eventCodeExists(eventCode));

        return eventCode;
    }

    // pre-sanitized eventCode
    private boolean eventCodeExists(String eventCode){

        return false;
    }

    private String sanitizeEventCode(String eventCode){
        if (eventCode == null)
            return null;
        if (eventCode.length() != 4)
            return null;
        if (!StringUtils.isAlphanumeric(eventCode.toUpperCase()))
            return null;
        return eventCode.toUpperCase();
    }



}