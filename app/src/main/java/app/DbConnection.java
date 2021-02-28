package app;

import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.ResultSet;

import java.util.ArrayList;

import app.Objects.*;

// IO for word-list import
import java.io.BufferedReader;
import java.io.IOException;
import java.security.SecureRandom;
import java.io.FileReader;

// event code generation
import org.apache.commons.lang3.RandomStringUtils;

public class DbConnection{

    // MULTI-THREADING NOT SUPPORTED 
    private Connection conn;

    private Validator validator;

    // ArrayList to store the host-code word list
    ArrayList<String> wordList = new ArrayList<String>(578);


    // public static void main(String args[]){};

    /**
     * Constructor; initializes db connection
     * @throws SQLException
     */
    public DbConnection() throws SQLException {
        // see: jdbc.postgresql.org/documentation/head/connect.html
        String url = "jdbc:postgresql:database";
        this.conn = DriverManager.getConnection(url);

        // store host-code word list
        getWordlist();

        // Instantiate Validator for DBConn instance
        validator = new Validator();

        // run tests (move in future)
        runTests();
    }

    private void runTests(){
        System.out.println("10 unique event codes:");
        for (int i = 0; i < 10; i++){
            String event_code = generateUniqueEventCode();
            System.out.print(event_code);
            System.out.print(" | is valid: "+ validator.eventCodeIsValid(event_code) +"\n");
        }
        System.out.println();

        System.out.println("10 unique host codes:");
        for (int i = 0; i < 10; i++){
            String hostCode = generateUniqueHostCode();
            System.out.print(hostCode);
            System.out.print(" | is valid: "+ validator.hostCodeIsValid(hostCode) +"\n");
        }
        System.out.println();

        System.out.println("10 unique template codes:");
        for (int i = 0; i < 10; i++){
            String templateCode = generateUniqueTemplateCode();
            System.out.print(templateCode);
            System.out.print(" | is valid: "+ validator.templateCodeIsValid(templateCode) +"\n");
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
            BufferedReader readIn = new BufferedReader(new FileReader("resources/wordlist.txt"));
            String str;
            while((str = readIn.readLine()) != null){
                wordList.add(str);
            }
            readIn.close();
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
     * @param f_name First name
     * @param l_name Last name
     * @param ip_address Ip address
     * @param e_address Email address
     * @return Host instance representing stored data
     */
    public Host createHost(String f_name, String l_name, String ip_address, String e_address){
        // generate unique host code
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
     * Create a template in the database
     * @param host_id Host id that it belongs to
     * @param template_code template code
     * @param data content
     * @return Template instance representing stored data
     */
    public Template createTemplate(int host_id, String data){
        // generate unique template code
        String template_code = generateUniqueTemplateCode();

        PreparedStatement stmt = null;
        ResultSet rs = null;
        Integer template_id = null;
        try{
            String createTemplate = ""
                + "INSERT INTO template(host_id, template_code, data) "
                + "VALUES(?, ?, ?) "
                + "RETURNING template_id";
            stmt = this.conn.prepareStatement(createTemplate);
            stmt.setInt(1, host_id);
            stmt.setString(2, template_code);
            stmt.setString(3, data);

            rs = stmt.executeQuery();
            if (rs.next()) {
                template_id = rs.getInt("template_id");
            }
        } catch (SQLException e){
            //throw e;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }

        // get Template object by ID
        return getTemplate(template_id);
    }

    /**
     * Create a participant in the database
     * @param ip_address Ip address
     * @param f_name First name
     * @param l_name Last name
     * @return Participant instance representing stored data
     */
    public Participant createParticipant(String ip_address, String f_name, String l_name){
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Integer participant_id = null;
        try{
            String createParticipant = ""
                + "INSERT INTO participant(ip_address, f_name, l_name) "
                + "VALUES(?, ?, ?) "
                + "RETURNING participant_id";
            stmt = this.conn.prepareStatement(createParticipant);
            stmt.setString(1, ip_address);
            stmt.setString(2, f_name);
            stmt.setString(3, l_name);

            rs = stmt.executeQuery();
            if (rs.next()) {
                participant_id = rs.getInt("participant_id");
            }
        } catch (SQLException e){
            //throw e;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }
        return getParticipant(participant_id);
    }
    
    /**
     * Create a event in the database
     * @param host_id Host id of its host
     * @param template_id Template id it used
     * @param title Event title
     * @param desc Event description
     * @param type Event type
     * @param start_time Start time of the event
     * @param end_time End time of the event
     * @return Event instance representing stored data
     */
    public Event createEvent(int host_id, int template_id, String title, String desc, String type, Timestamp start_time, Timestamp end_time){
        // generate unique event code
        String event_code = generateUniqueEventCode();

        PreparedStatement stmt = null;
        ResultSet rs = null;
        Integer event_id = null;
        try{
            String createEvent = ""
                + "INSERT INTO event(host_id, template_id, title, description, type, start_time, end_time, event_code) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?, ?) "
                + "RETURNING event_id";
            stmt = this.conn.prepareStatement(createEvent);
            stmt.setInt(1, host_id);
            stmt.setInt(2, template_id);
            stmt.setString(3, title);
            stmt.setString(4, desc);
            stmt.setString(5, type);
            stmt.setTimestamp(6, start_time);
            stmt.setTimestamp(7, end_time);
            stmt.setString(8, event_code);

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

        // get Event by ID
        return getEvent(event_id);
    }

    /**
     * Create an archived event in the database
     * @param host_id Host id of its host
     * @param total_mood Mood of participants in this event
     * @param title Event title
     * @param desc Event description
     * @param type Event type
     * @param start_time Start time of the event
     * @param end_time End time of the event
     * @return Event instance representing stored data
     */
    public ArchivedEvent createArchivedEvent(int host_id, String total_mood, String title, String desc, String type, Timestamp start_time, Timestamp end_time){

        PreparedStatement stmt = null;
        ResultSet rs = null;
        Integer event_id = null;
        try{
            String createArchivedEvent = ""
                + "INSERT INTO archived_event(host_id, total_mood, title, description, type, start_time, end_time) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?) "
                + "RETURNING event_id";
            stmt = this.conn.prepareStatement(createArchivedEvent);
            stmt.setInt(1, host_id);
            stmt.setString(2, total_mood);
            stmt.setString(3, title);
            stmt.setString(4, desc);
            stmt.setString(5, type);
            stmt.setTimestamp(6, start_time);
            stmt.setTimestamp(7, end_time);

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

        return getArchivedEvent(event_id);
    }

    /**
     * Create an instance of feedback against an event
     * @param participant_id Participant id of the participant who created this feedback
     * @param event_id Event id of the event which this feedback is written for
     * @param data Feedback content
     * @param sentiment Sentiment
     * @param anonymous Whether this feedback is anonymous
     * @param time_stamp Time when the feedback was created
     * @return Feedback instance representing stored data
     */
    public Feedback createFeedback(int participant_id, int event_id, String data, String sentiment, boolean anonymous, Timestamp time_stamp){

        PreparedStatement stmt = null;
        ResultSet rs = null;
        Integer feedback_id = null;
        try{
            String createArchivedEvent = ""
                + "INSERT INTO feedback(participant_id, event_id, data, sentiment, anonymous, time_stamp) "
                + "VALUES(?, ?, ?, ?, ?, ?) "
                + "RETURNING feedback_id";
            stmt = this.conn.prepareStatement(createArchivedEvent);

            stmt.setInt(1, participant_id);
            stmt.setInt(2, event_id);
            stmt.setString(3, data);
            stmt.setString(4, sentiment);
            stmt.setBoolean(5, anonymous);
            stmt.setTimestamp(6, time_stamp);

            rs = stmt.executeQuery();
            if (rs.next()) {
                feedback_id = rs.getInt("feedback_id");
            }
        } catch (SQLException e){
            //throw e;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }

        return getFeedback(feedback_id);
    }

    /**
     * Add a participant (by ID) to an event (by ID)
     * @param participant_id participant ID
     * @param event_id event ID
     * @return success state
     */
    public Boolean addParticipantToEvent(int participant_id, int event_id){
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            String addParticipantToEvent = ""
                + "INSERT INTO participant_in_event(participant_id, event_id) "
                + "VALUES(?, ?) ";
            stmt = this.conn.prepareStatement(addParticipantToEvent);
            stmt.setInt(1, participant_id);
            stmt.setInt(2, event_id);
            stmt.executeUpdate();
        } catch (SQLException e){
            //throw e;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }

        // return success state (select query)
        return participantInEvent(participant_id, event_id);
    }

    /**
     * Check if a participant is in an event
     * @param participant_id participant ID
     * @param event_id event ID
     * @return method success state
     */
    public Boolean participantInEvent(int participant_id, int event_id){
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Boolean state = null;
        try{
            String queryParticipantInEvent = ""
                + "SELECT EXISTS "
                + "(SELECT 1 FROM participant_in_event WHERE "                
                + "participant_in_event.participant_id=? "
                + "AND "
                + "participant_in_event.event_id=? " 
                + ");";
            stmt = this.conn.prepareStatement(queryParticipantInEvent);
            stmt.setInt(1, participant_id);
            stmt.setInt(2, event_id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                state = rs.getBoolean(1);
            }
        } catch (SQLException e){
            //throw e;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }
        return state;
    }

    /**
     * Check if a given participant is muted in the given event
     * @param participant_id participant
     * @param event_id event containing participant
     * @return method success state 
     */
    public Boolean participantInEventIsMuted(int participant_id, int event_id){
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Boolean muted = null;
        try{
            String queryMutedState = ""
                + "SELECT muted FROM participant_in_event WHERE "                
                + "participant_in_event.participant_id=? "
                + "AND "
                + "participant_in_event.event_id=? " 
                + ");";
            stmt = this.conn.prepareStatement(queryMutedState);
            stmt.setInt(1, participant_id);
            stmt.setInt(2, event_id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                muted = rs.getBoolean("muted");
            }
        } catch (SQLException e){
            //throw e;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }
        return muted;
    }

    /**
     * Mute a participant in an event
     * @param participant_id participant to be muted
     * @param event_id event in which participant is muted
     * @return method success state 
     */
    public Boolean muteParticipantInEvent(int participant_id, int event_id){
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            String muteParticipant = ""
                + "UPDATE participant_in_event "
                + "SET muted = TRUE"
                + "WHERE "
                + "participant_in_event.participant_id=? "
                + "AND "
                + "participant_in_event.event_id=? " 
                + ");";
            stmt = this.conn.prepareStatement(muteParticipant);
            stmt.setInt(1, participant_id);
            stmt.setInt(2, event_id);
            stmt.executeUpdate();
        } catch (SQLException e){
            //throw e;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }
        return participantInEventIsMuted(participant_id, event_id);
    }

    /**
     * Get a Host object by its code.
     * @param host_code host code
     * @return Host object corresponding to its code
     */
    public Host getHostByCode(String host_code){
        host_code = validator.sanitizeHostCode(host_code);
        if (!validator.templateCodeIsValid(host_code)) return null;
        if (!templateCodeExists(host_code)) return null;

        // host code valid and exists --> query db
        PreparedStatement stmt = null;
        Integer host_id = null;
        ResultSet rs = null;
        try{
            String queryHostByCode = "SELECT host_id FROM host WHERE host_code=? LIMIT 1;";
            stmt = this.conn.prepareStatement(queryHostByCode);
            stmt.setString(1, host_code);
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
        
        // get Host by ID
        return getHost(host_id);
    }

    /**
     * Get a Template object by its code.
     * @param template_code template code
     * @return Template object corresponding to its code
     */
    public Template getTemplateByCode(String template_code){
        template_code = validator.sanitizeTemplateCode(template_code);
        if (!validator.templateCodeIsValid(template_code)) return null;
        if (!templateCodeExists(template_code)) return null;

        // template code valid and exists --> query db
        PreparedStatement stmt = null;
        Integer template_id = null;
        ResultSet rs = null;
        try{
            String queryTemplateByCode = "SELECT template_id FROM template WHERE template_code=? LIMIT 1;";
            stmt = this.conn.prepareStatement(queryTemplateByCode);
            stmt.setString(1, template_code);
            rs = stmt.executeQuery();
            if (rs.next()) {
                template_id = rs.getInt("template_id");
            }
        } catch (SQLException e){
            //throw e;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }
        
        // get Template by ID
        return getTemplate(template_id);
    }

    /**
     * Get an Event object by its code.
     * @param event_code 4-digit alphanumeric event code
     * @return Event object corresponding to eventCode
     */
    public Event getEventByCode(String event_code){
        event_code = validator.sanitizeEventCode(event_code);
        if (!validator.eventCodeIsValid(event_code)) return null;
        if (!eventCodeExists(event_code)) return null;

        // eventCode valid and exists --> query db
        PreparedStatement stmt = null;
        Integer event_id = null;
        ResultSet rs = null;
        try{
            String queryEventByCode = "SELECT event_id FROM event WHERE event_code=? LIMIT 1;";
            stmt = this.conn.prepareStatement(queryEventByCode);
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
     * @return Host object with ID of host_id
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
                boolean sys_ban = rs.getBoolean("sys_ban");

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
     * Get an Template object from an template ID
     * @param template_id template ID
     * @return Template object with ID of template_id
     */
    private Template getTemplate(int template_id){
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Template template = null;
        try{
            String selectTemplateByID = ""
                + "SELECT * FROM template "
                + "WHERE template.template_id = ? "
                + "LIMIT 1;";
            stmt = this.conn.prepareStatement(selectTemplateByID);
            stmt.setInt(1, template_id);

            rs = stmt.executeQuery();
            if (rs.next()) {
                template_id = rs.getInt("template_id");
                int host_id = rs.getInt("host_id");
                String template_code = rs.getString("template_code");
                String data = rs.getString("data");

                template = new Template(template_id, host_id, template_code, data);
            }
        } catch (SQLException e){
            //throw e;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }
        return template;
    }

    /**
     * Get an Participant object from an participant ID
     * @param participant_id participant ID
     * @return Participant object with ID of participant_id
     */
    public Participant getParticipant(int participant_id){
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Participant participant = null;
        try{
            String getParticipantByID = ""
                + "SELECT * FROM participant "
                + "WHERE participant.participant_id = ? "
                + "LIMIT 1;";
            stmt = this.conn.prepareStatement(getParticipantByID);
            stmt.setInt(1, participant_id);

            rs = stmt.executeQuery();
            if (rs.next()) {
                participant_id = rs.getInt("participant_id");
                String ip_address = rs.getString("ip_address");
                String f_name = rs.getString("f_name");
                String l_name = rs.getString("l_name");
                boolean sys_ban = rs.getBoolean("sys_ban");

                participant = new Participant(participant_id, ip_address, f_name, l_name, sys_ban);
            }
        } catch (SQLException e){
            //throw e;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }
        return participant;
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
     * Get an archived Event object from an event ID
     * @param event_id event ID
     * @return ArchivedEvent object with ID of event_id
     */
    public ArchivedEvent getArchivedEvent(int event_id){
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArchivedEvent archivedEvent = null;
        try{
            String selectArchivedEventByID = ""
                + "SELECT * FROM archived_event "
                + "WHERE archived_event.event_id = ? "
                + "LIMIT 1;";
            stmt = this.conn.prepareStatement(selectArchivedEventByID);
            stmt.setInt(1, event_id);

            rs = stmt.executeQuery();
            if (rs.next()) {
                event_id = rs.getInt("event_id");
                int host_id = rs.getInt("host_id");
                String total_mood = rs.getString("total_mood");

                String title = rs.getString("title");
                String description = rs.getString("description");
                String type = rs.getString("type");
                Timestamp start_time = rs.getTimestamp("start_time");
                Timestamp end_time = rs.getTimestamp("end_time");

                archivedEvent = new ArchivedEvent(event_id, host_id, total_mood, title, description, type, start_time, end_time);
            }
        } catch (SQLException e){
            //throw e;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }
        return archivedEvent;
    }

    /**
     * Get a feedback object from an feedback ID
     * @param feedback_id feedback ID
     * @return Feedback object with ID of feedback_id
     */
    public Feedback getFeedback(int feedback_id){
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Feedback feedback = null;
        try{
            String selectFeedbackByID = ""
                + "SELECT * FROM feedback "
                + "WHERE feedback.feedback_id = ? "
                + "LIMIT 1;";
            stmt = this.conn.prepareStatement(selectFeedbackByID);
            stmt.setInt(1, feedback_id);

            rs = stmt.executeQuery();
            if (rs.next()) {
                feedback_id = rs.getInt("feedback_id");
                int participant_id = rs.getInt("participant_id");
                int event_id = rs.getInt("event_id");
                String data = rs.getString("data");
                String sentiment = rs.getString("sentiment");
                boolean anonymous = rs.getBoolean("anonymous");
                Timestamp time_stamp = rs.getTimestamp("time_stamp");

                feedback = new Feedback(feedback_id, participant_id, event_id, data, sentiment, anonymous, time_stamp);
            }
        } catch (SQLException e){
            //throw e;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }
        return feedback;
    }

    /**
     * Check if the given event code exists, 
     * and is against an active event
     * @param eventCode the event code
     * @return existence state of eventCode, null if fails
     */
    private Boolean eventCodeExists(String event_code){
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Boolean codeExists = null;
        try{
            String queryEventCodeExists = ""
                + "SELECT EXISTS(SELECT 1 FROM event WHERE event_code=?);";
            stmt = this.conn.prepareStatement(queryEventCodeExists);
            stmt.setString(1, event_code);
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
     * (4-digit, case insensitive alpha-numeric String)
     * @return the generated event code
     */
    protected String generateUniqueEventCode(){
        String event_code = null;
        while (event_code == null || eventCodeExists(event_code)){
            event_code = RandomStringUtils.randomAlphanumeric(4).toLowerCase();
        }
        return event_code;
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
     * (four-word subset of word list)
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
