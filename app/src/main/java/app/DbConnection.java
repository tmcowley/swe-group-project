package app;

import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.ResultSet;

import java.util.ArrayList;

import app.Validator;
import app.Event;

// IO for word-list import
import java.io.BufferedReader;
import java.io.IOException;
import java.security.SecureRandom;
import java.io.FileReader;

import org.apache.commons.lang3.RandomStringUtils; // eventCode generation

public class DbConnection{

    // MULTI-THREADING NOT SUPPORTED 
    private Connection conn;

    // ArrayList to store the host-code word list
    ArrayList<String> wordList = new ArrayList<String>(578);


    // public static void main(String args[]){};

    /**
     * Constructor, initializes db connection
     * @throws SQLException
     */
    public DbConnection() throws SQLException {
        // see: jdbs.postgres.org/documentation/head/connect.html
        String url = "jdbc:postgresql:database";
        this.conn = DriverManager.getConnection(url);

        // store host-code word list
        getWordlist();

        // run tests (move in future)
        runTests();
    }

    private void runTests(){
        Validator v = new Validator();
        System.out.println("10 unique event codes:");
        for (int i = 0; i < 10; i++){
            String eventCode = generateUniqueEventCode();
            System.out.print(eventCode);
            System.out.print(" | is valid: "+ Validator.eventCodeIsValid(eventCode) +"\n");
        }
        System.out.println();

        System.out.println("10 unique host codes:");
        for (int i = 0; i < 10; i++){
            String hostCode = generateUniqueHostCode();
            System.out.print(hostCode);
            System.out.print(" | is valid: "+ v.hostCodeIsValid(hostCode) +"\n");
        }
        System.out.println();

        System.out.println("10 unique template codes:");
        for (int i = 0; i < 10; i++){
            String templateCode = generateUniqueTemplateCode();
            System.out.print(templateCode);
            System.out.print(" | is valid: "+ Validator.templateCodeIsValid(templateCode) +"\n");
        }
        System.out.println();
    }

    /**
     * Close the DB Connection: conn
     * @throws SQLException
     */
    public void closeConnection() throws SQLException{
        this.conn.close();
    }

    /**
     * Store the host code word list in an array list DS
     */
    private void getWordlist(){
        try{
            BufferedReader in = new BufferedReader(new FileReader("resources/wordlist.txt"));
            String str;
            while((str = in.readLine()) != null){
                wordList.add(str);
            }
        } catch (IOException ex){
            // ensure file: wordlist.txt is in /app/resources/
            System.out.println(
                "Error: Getting word list failed" + 
                "       " + "Ensure wordlist.txt is in /app/resources");
            //System.out.println(ex.getMessage());
        }
    }

    /**
     * Create a host in the database
     * @param f_name
     * @param l_name
     * @param ip_address
     * @param e_address
     * @return
     */
    public Host createHost(String f_name, String l_name, String ip_address, String e_address){
        // generate unique eventCode
        String host_code = generateUniqueHostCode();

        PreparedStatement stmt = null;
        ResultSet rs = null;
        Integer host_id = null;
        try{
            String createHost = ""
                + "INSERT INTO host(f_name, l_name, ip_address, e_address, host_code) "
                + "VALUES(?, ?, ?, ?, ?) "
                + "RETURNING host_id";
            stmt = this.conn.prepareStatement(createHost);
            stmt.setString(1, f_name);
            stmt.setString(2, l_name);
            stmt.setString(3, ip_address);
            stmt.setString(4, e_address);
            stmt.setString(5, host_code);

            rs = stmt.executeQuery();
            if (rs.next()) {
                host_id = rs.getInt("host_id");
            }
        } catch (SQLException e){
            //throw e;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }

        // get Host object from host_id
        return getHost(host_id);
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
            stmt.setInt(5, eventID);

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
     * @param event_code 4-digit alphanumeric event code
     * @return Event object corresponding to eventCode
     */
    public Event getEventFromEventCode(String event_code){
        event_code = Validator.sanitizeEventCode(event_code);
        if (!Validator.eventCodeIsValid(event_code)) return null;
        if (!eventCodeExists(event_code)) return null;

        // eventCode valid and exists --> query db
        PreparedStatement stmt = null;
        Integer event_id = null;
        ResultSet rs = null;
        try{
            String queryEventID = "SELECT event_id FROM event WHERE event_code=? LIMIT 1;";
            stmt = this.conn.prepareStatement(queryEventID);
            stmt.setString(1, event_code);
            rs = stmt.executeQuery();
            if (rs.next()) {
                event_id = rs.getInt("event_id");
            }
        } catch (SQLException e){
            //throw e;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }
        
        // get Event from event ID
        return getEvent(event_id);
    }

    // PRIVATE METHODS

    /**
     * Get an Host object from a host ID
     * @param host_id host ID
     * @return Event object with ID of host_id
     */
    private Host getHost(int host_id){
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Host host = null;
        try{
            String selectHostByID = ""
                + "SELECT * FROM host "
                + "WHERE host.host_id = ? "
                + "LIMIT 1;";
            stmt = this.conn.prepareStatement(selectHostByID);
            stmt.setInt(1, host_id);

            rs = stmt.executeQuery();
            if (rs.next()) {
                host_id = rs.getInt("host_id");
                String host_code = rs.getString("host_code");
                String ip_address = rs.getString("ip_address");
                String e_address = rs.getString("e_address");
                String f_name = rs.getString("f_name");
                String l_name = rs.getString("l_name");
                Boolean sys_ban = rs.getBoolean("sys_ban");

                host = new Host(host_id, host_code, ip_address, e_address, f_name, l_name, sys_ban);
            }
        } catch (SQLException e){
            //throw e;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }
        return host;
    }

    /**
     * Get an Event object from an event ID
     * @param event_id event ID
     * @return Event object with ID of event_id
     */
    private Event getEvent(int event_id){
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Event event = null;
        try{
            String selectEventByID = ""
                + "SELECT * FROM event "
                + "WHERE event.event_id = ? "
                + "LIMIT 1;";
            stmt = this.conn.prepareStatement(selectEventByID);
            stmt.setInt(1, event_id);

            rs = stmt.executeQuery();
            if (rs.next()) {
                event_id = rs.getInt("event_id");
                int host_id = rs.getInt("host_id");
                int template_id = rs.getInt("template_id");

                String title = rs.getString("title");
                String description = rs.getString("description");
                String type = rs.getString("type");
                Timestamp start_time = rs.getTimestamp("start_time");
                Timestamp end_time = rs.getTimestamp("end_time");
                String event_code = rs.getString("event_code");

                event = new Event(event_id, host_id, template_id, title, description, type, start_time, end_time, event_code);
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
     * Check if the given event code exists
     * @param eventCode the event code
     * @return existence state of eventCode
     */
    private boolean eventCodeExists(String eventCode){
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Boolean codeExists = null;
        try{
            String queryEventCodeExists = ""
                + "SELECT EXISTS(SELECT 1 FROM event WHERE event_code=?);";
            stmt = this.conn.prepareStatement(queryEventCodeExists);
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
     * Check if the given template code exists
     * @param templateCode template code
     * @return existence state of templateCode
     */
    private boolean templateCodeExists(String templateCode){
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Boolean codeExists = null;
        try{
            String queryTemplateCodeExists = ""
                + "SELECT EXISTS(SELECT 1 FROM template WHERE template_code=?);";
            stmt = this.conn.prepareStatement(queryTemplateCodeExists);
            stmt.setString(1, templateCode);
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
     * Check if the given host code exists
     * @param hostCode host code
     * @return existence state of hostCode
     */
    private boolean hostCodeExists(String hostCode){
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Boolean codeExists = null;
        try{
            String queryHostCodeExists = ""
                + "SELECT EXISTS(SELECT 1 FROM host WHERE host_code=?);";
            stmt = this.conn.prepareStatement(queryHostCodeExists);
            stmt.setString(1, hostCode);
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
            eventCode = RandomStringUtils.randomAlphanumeric(4).toLowerCase();
        }
        return eventCode;
    }

    /**
     * Generate a unique template code 
     * (6-digit, case insensitive alpha-numeric String)
     * @return the generated template code
     */
    protected String generateUniqueTemplateCode(){
        String templateCode = null;
        while (templateCode == null || templateCodeExists(templateCode)){
            templateCode = RandomStringUtils.randomAlphanumeric(6).toLowerCase();
        }
        return templateCode;
    }

    /**
     * Generate a unique host code 
     * (four-word subset of wordlist)
     * @return the generated host code
     */
    protected String generateUniqueHostCode(){
        String hostCode = null;
        SecureRandom rand = new SecureRandom();
        while (hostCode == null || hostCodeExists(hostCode)){
            String[] hostCodeWords = new String[4];
            for (int index=0; index < 4; index++){
                int randomWordIndex = rand.nextInt(wordList.size());
                hostCodeWords[index] = wordList.get(randomWordIndex);
            }
            hostCode = String.join(" ", hostCodeWords);
        }
        return hostCode;
    }

}