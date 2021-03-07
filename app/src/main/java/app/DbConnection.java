package app;

// SQL packages
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.ResultSet;

import java.util.ArrayList;

import app.objects.*;

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
        try{
            // PostgreSQL variant: 
            // see: jdbc.postgresql.org/documentation/head/connect.html
            // String dbURL = "jdbc:postgresql:database";
            String dbURL = "jdbc:postgresql://127.0.0.1:5432/cs261";

            // this.conn = DriverManager.getConnection(dbURL);
            this.conn = DriverManager.getConnection(dbURL, "postgres", "fas200");
            
        } catch (SQLException e){
            SQLException updatedException = new SQLException("Error: DB failed to connect; ensure server is running", e);
            throw updatedException;
        }

        // store host-code word list
        getWordList();

        // Instantiate Validator for DBConn instance
        validator = new Validator();
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
    private void getWordList(){
        try{
            BufferedReader readIn = new BufferedReader(new FileReader("resources/word-list.txt"));
            String str;
            while((str = readIn.readLine()) != null){
                wordList.add(str);
            }
            readIn.close();
        } catch (IOException ex){
            // ensure file: word-list.txt is in /app/resources/
            System.out.println(ex.getMessage());
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
                + "VALUES(?, ?, ?::INET, ?, ?) "
                + "RETURNING host_id;";
            System.out.println(createHost);
            stmt = this.conn.prepareStatement(createHost);
            stmt.setString(1, f_name);
            stmt.setString(2, l_name);
            stmt.setObject(3, ip_address);
            stmt.setString(4, e_address);
            stmt.setString(5, host_code);

            rs = stmt.executeQuery();
            if (rs.next()) {
                host_id = rs.getInt("host_id");
            }
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());;
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
            System.out.println(e.getMessage().toUpperCase());;
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
                + "VALUES(?::INET, ?, ?) "
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
            System.out.println(e.getMessage().toUpperCase());;
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
                + "VALUES(?, ?, ?, ?, ?::event_type, ?, ?, ?) "
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
            System.out.println(e.getMessage().toUpperCase());;
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
            System.out.println(e.getMessage().toUpperCase());;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }

        return getArchivedEvent(event_id);
    }

    /**
     * Create an instance of feedback against an event
     * TODO: ensure params are correct for non-processed feedback
     * @param participant_id Participant id of the participant who created this feedback
     * @param event_id Event id of the event which this feedback is written for
     * @param anonymous Whether this feedback is anonymous
     * @param time_stamp Time when the feedback was created
     * @return Feedback instance representing stored data
     */
    public Feedback createFeedback(int participant_id, int event_id, boolean anonymous, Timestamp time_stamp, String[] results){

        PreparedStatement stmt = null;
        ResultSet rs = null;
        Integer feedback_id = null;
        try{
            String createNonProcessedFeedback = ""
                + "INSERT INTO feedback(participant_id, event_id, anonymous, time_stamp, results) "
                + "VALUES(?, ?, ?, ?, ?) "
                + "RETURNING feedback_id;";
            stmt = this.conn.prepareStatement(createNonProcessedFeedback);

            stmt.setInt(1, participant_id);
            stmt.setInt(2, event_id);
            stmt.setBoolean(3, anonymous);
            stmt.setTimestamp(4, time_stamp);
            stmt.setArray(5, this.conn.createArrayOf("TEXT", results));

            rs = stmt.executeQuery();
            if (rs.next()) {
                feedback_id = rs.getInt("feedback_id");
            }
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }

        return getFeedback(feedback_id);
    }

    // TODO COMMENT
    // CREATE FULL FEEDBACK OBJECT (post-processing)
    public Feedback createFeedback(int participant_id, int event_id, boolean anonymous, Timestamp time_stamp, String[] results, Float[] weights, Integer[] type, Integer[] key, float compound, String[] key_results){

        PreparedStatement stmt = null;
        ResultSet rs = null;
        Integer feedback_id = null;
        try{
            String createProcessedFeedback = ""
                + "INSERT INTO feedback(participant_id, event_id, anonymous, time_stamp, results, weights, type, key, compound, key_results) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
                + "RETURNING feedback_id;";
            stmt = this.conn.prepareStatement(createProcessedFeedback);

            stmt.setInt(1, participant_id);
            stmt.setInt(2, event_id);
            stmt.setBoolean(3, anonymous);
            stmt.setTimestamp(4, time_stamp);
            stmt.setArray(5, this.conn.createArrayOf("TEXT", results));
            stmt.setArray(6, this.conn.createArrayOf("REAL", weights));
            stmt.setArray(7, this.conn.createArrayOf("INT", type));
            stmt.setArray(8, this.conn.createArrayOf("INT", key));
            stmt.setFloat(9, compound);
            stmt.setArray(10, this.conn.createArrayOf("TEXT", key_results));

            rs = stmt.executeQuery();
            if (rs.next()) {
                feedback_id = rs.getInt("feedback_id");
            }
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());;
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
            System.out.println(e.getMessage().toUpperCase());;
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
            System.out.println(e.getMessage().toUpperCase());;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }
        return state;
    }

    /**
     * Check if a given participant is muted in the given event
     * @param participant_id participant ID
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
            System.out.println(e.getMessage().toUpperCase());;
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
            System.out.println(e.getMessage().toUpperCase());;
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
            System.out.println(e.getMessage().toUpperCase());;
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
            System.out.println(e.getMessage().toUpperCase());;
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
            System.out.println(e.getMessage().toUpperCase());;
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
                String ip_address = rs.getObject("ip_address").toString();
                String e_address = rs.getString("e_address");
                String f_name = rs.getString("f_name");
                String l_name = rs.getString("l_name");
                boolean sys_ban = rs.getBoolean("sys_ban");

                host = new Host(host_id, host_code, ip_address, e_address, f_name, l_name, sys_ban);
            }
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());;
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
            System.out.println(e.getMessage().toUpperCase());;
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
                String ip_address = rs.getObject("ip_address").toString();
                String f_name = rs.getString("f_name");
                String l_name = rs.getString("l_name");
                boolean sys_ban = rs.getBoolean("sys_ban");

                participant = new Participant(participant_id, ip_address, f_name, l_name, sys_ban);
            }
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());;
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
            System.out.println(e.getMessage().toUpperCase());;
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
            System.out.println(e.getMessage().toUpperCase());;
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
                // non-sentiment related fields
                feedback_id = rs.getInt("feedback_id");
                int participant_id = rs.getInt("participant_id");
                int event_id = rs.getInt("event_id");
                boolean anonymous = rs.getBoolean("anonymous");
                Timestamp time_stamp = rs.getTimestamp("time_stamp");

                // collect sentiment related fields
                String[] results    = (String[])rs.getArray("results").getArray();
                Float[] weights     = (Float[])rs.getArray("weights").getArray();
                Integer[] type      = (Integer[])rs.getArray("type").getArray();
                Integer[] key       = (Integer[])rs.getArray("key").getArray();
                float compound      = rs.getFloat("compound"); 
                String[] key_results = (String[])rs.getArray("key_results").getArray();

                feedback = new Feedback(feedback_id, participant_id, event_id, anonymous, time_stamp, 
                                        results, weights, type, key, compound, key_results);
            }
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }
        return feedback;
    }

    // (int feedback_id, int host_id, int event_id, String[] results, float[] weights, int[] type, int[] key, float compound, String[] key_results, boolean anonymous, Timestamp timestamp)

    /**
     * Check if the given event code exists, 
     * and is against an active event
     * @param event_code the event code
     * @return existence state of eventCode, null if fails
     */
    private Boolean eventCodeExists(String event_code){
        event_code = validator.sanitizeEventCode(event_code);
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
            System.out.println(e.getMessage().toUpperCase());
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }
        return codeExists;
    }

    /**
     * Check if the given template code exists
     * @param template_code template code
     * @return existence state of template_code
     */
    private Boolean templateCodeExists(String template_code){
        template_code = validator.sanitizeTemplateCode(template_code);
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Boolean codeExists = null;
        try{
            String queryTemplateCodeExists = ""
                + "SELECT EXISTS(SELECT 1 FROM template WHERE template_code=?);";
            stmt = this.conn.prepareStatement(queryTemplateCodeExists);
            stmt.setString(1, template_code);
            rs = stmt.executeQuery();
            if (rs.next()) {
                codeExists = rs.getBoolean(1);
            }
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }
        return codeExists;
    }

    /**
     * Check if the given host code exists
     * @param host_code code
     * @return existence state of host_code
     */
    private Boolean hostCodeExists(String host_code){
        host_code = validator.sanitizeHostCode(host_code);
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Boolean codeExists = false;
        try{
            String queryHostCodeExists = ""
                + "SELECT EXISTS(SELECT 1 FROM host WHERE host_code=?);";
            stmt = this.conn.prepareStatement(queryHostCodeExists);
            stmt.setString(1, host_code);
            rs = stmt.executeQuery();
            if (rs.next()) {
                codeExists = rs.getBoolean(1);
            }
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());
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
        String template_code = null;
        while (template_code == null || templateCodeExists(template_code)){
            template_code = RandomStringUtils.randomAlphanumeric(6).toLowerCase();
        }
        return template_code;
    }

    /**
     * Generate a unique host code 
     * (four-word subset of word list)
     * @return generated host code
     */
    protected String generateUniqueHostCode(){
        String host_code = null;
        SecureRandom rand = new SecureRandom();
        while (host_code == null || hostCodeExists(host_code)){
            String[] hostCodeWords = new String[4];
            for (int index=0; index < 4; index++){
                int randomWordIndex = rand.nextInt(wordList.size());
                hostCodeWords[index] = wordList.get(randomWordIndex);
            }
            host_code= String.join("-", hostCodeWords);
        }
        return host_code;
    }

    /**
     * ban host using host ID 
     * @host_id banned hostID
     * @return ban status
     */
    protected Boolean banHost(int host_id){
        PreparedStatement stmt = null;
        Integer bannedHost = null;
        try{
            String banHost = ""
                + "UPDATE host "
                + "SET sys_ban = ? "
                + "WHERE host_id = ?;";
            stmt = this.conn.prepareStatement(banHost);
            stmt.setBoolean(1, true);
            stmt.setInt(2, host_id);
            bannedHost = stmt.executeUpdate();
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
        }
        if (bannedHost == null) return null;
        return (bannedHost != 0);
    }

    /**
     * ban host using email address 
     * @host_id banned hostID
     * @return ban status
     */
    protected Boolean banHost(String eAddress){
        PreparedStatement stmt = null;
        Integer bannedHost = null;
        try{
            String banHost = ""
                + "UPDATE host "
                + "SET sys_ban = ? "
                + "WHERE e_address = ?;";
            stmt = this.conn.prepareStatement(banHost);
            stmt.setBoolean(1, true);
            stmt.setString(2, eAddress);
            bannedHost = stmt.executeUpdate();
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
        }
        if (bannedHost == null) return null;
        return (bannedHost != 0);
    }

    /**
     * ban participant using participant ID 
     * @participant_id banned participantID
     * @return ban status
     */
    protected Boolean banParticipant(int participant_id){
        PreparedStatement stmt = null;
        Integer bannedParticipant = null;
        try{
            String banParticipant = ""
                + "UPDATE participant "
                + "SET sys_ban = ? "
                + "WHERE participant_id = ?;";
            stmt = this.conn.prepareStatement(banParticipant);
            stmt.setBoolean(1, true);
            stmt.setInt(2, participant_id);
            bannedParticipant = stmt.executeUpdate();
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
        }
        if (bannedParticipant == null) return null;
        return (bannedParticipant != 0);
    }

    /**
     * Update data in templates 
     * @template_id templateID of template that needs to be changed
     * @data updated data
     * @return data change status
     */
    protected Boolean addDataToTemplate(int template_id, String data){
        PreparedStatement stmt = null;
        Integer templateFound = null;
        try{
            String updateTemplate = ""
                + "UPDATE template "
                + "SET data = ? "
                + "WHERE template_id = ?;";
            stmt = this.conn.prepareStatement(updateTemplate);
            stmt.setString(1, data);
            stmt.setInt(2, template_id);
            templateFound = stmt.executeUpdate();
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
        }
        if (templateFound == null) return null;
        return (templateFound != 0);
    }

    /**
     * Delete host by ID
     * @host_id host ID of the host needed to be deleted
     * @return delete status
     */
    protected Boolean deleteHost(int host_id){
        PreparedStatement stmt = null;
        Integer hostDeleted = null;
        try{
            String deleteHost = ""
                + "DELETE FROM host "
                + "WHERE host_id = ?;";
            stmt = this.conn.prepareStatement(deleteHost);
            stmt.setInt(1, host_id);
            hostDeleted = stmt.executeUpdate();
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
        }
        if (hostDeleted == null) return null;
        return (hostDeleted != 0);
    }

    /**
     * Delete template by ID
     * @template_id template ID of the template needed to be deleted
     * @return delete status
     */
    protected Boolean deleteTemplate(int template_id){
        PreparedStatement stmt = null;
        Integer templateDeleted = null;
        try{
            String deleteTemplate = ""
                + "DELETE FROM template "
                + "WHERE template_id = ?;";
            stmt = this.conn.prepareStatement(deleteTemplate);
            stmt.setInt(1, template_id);
            templateDeleted = stmt.executeUpdate();
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
        }
        if (templateDeleted == null) return null;
        return (templateDeleted != 0);
    }

    /**
     * Delete participant by ID
     * @participant_id participant ID of the participant needed to be deleted
     * @return delete status
     */
    protected Boolean deleteParticipant(int participant_id){
        PreparedStatement stmt = null;
        Integer participantDeleted = null;
        try{
            String deleteParticipant = ""
                + "DELETE FROM participant "
                + "WHERE participant_id = ?;";
            stmt = this.conn.prepareStatement(deleteParticipant);
            stmt.setInt(1, participant_id);
            participantDeleted = stmt.executeUpdate();
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
        }
        if (participantDeleted == null) return null;
        return (participantDeleted != 0);
    }

    /**
     * Delete event by ID
     * @event_id event ID of archived event needed to be deleted
     * @return delete status
     */
    protected Boolean deleteEvent(int event_id){
        PreparedStatement stmt = null;
        Integer eventDeleted = null;
        try{
            String deleteEvent = ""
                + "DELETE FROM event "
                + "WHERE event_id = ?;";
            stmt = this.conn.prepareStatement(deleteEvent);
            stmt.setInt(1, event_id);
            eventDeleted = stmt.executeUpdate();
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
        }
        if (eventDeleted == null) return null;
        return (eventDeleted != 0);
    }

    /**
     * Delete archived event by ID
     * @event_id event ID of archived event needed to be deleted
     * @return delete status
     */
    protected Boolean deleteArchivedEvent(int event_id){
        PreparedStatement stmt = null;
        Integer eventDeleted = null;
        try{
            String deleteEvent = ""
                + "DELETE FROM archived_event "
                + "WHERE event_id = ?;";
            stmt = this.conn.prepareStatement(deleteEvent);
            stmt.setInt(1, event_id);
            eventDeleted = stmt.executeUpdate();
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
        }
        if (eventDeleted == null) return null;
        return (eventDeleted != 0);
    }

    /**
     * Delete participant and event pair by ID
     * @feedback_id feedback ID of the feedback needed to be deleted
     * @return delete status
     */
    protected Boolean deleteFeedback(int feedback_id){
        PreparedStatement stmt = null;
        Integer feedbackDeleted = null;
        try{
            String deleteFeedback = ""
                + "DELETE FROM feedback "
                + "WHERE feedback_id = ?;";
            stmt = this.conn.prepareStatement(deleteFeedback);
            stmt.setInt(1, feedback_id);
            feedbackDeleted = stmt.executeUpdate();
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
        }
        if (feedbackDeleted == null) return null;
        return (feedbackDeleted != 0);
    }

    /**
     * Remove participant from event (by IDs)
     * @participant_id participant ID of the pair needed to be deleted
     * @event_id event ID of the pair needed to be deleted
     * @return delete status
     */
    protected Boolean removeParticipantFromEvent(int participant_id, int event_id){
        PreparedStatement stmt = null;
        Integer deletedLink = null;
        try{
            String participantInEventDeleted = ""
                + "DELETE FROM participant_in_event "
                + "WHERE participant_id = ? AND event_id = ?;";
            stmt = this.conn.prepareStatement(participantInEventDeleted);
            stmt.setInt(1, participant_id);
            stmt.setInt(1, event_id);
            deletedLink = stmt.executeUpdate();
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
        }
        if (deletedLink == null) return null;
        return (deletedLink != 0);
    }

    /**
     * Delete finished events and add it to archivedEvents 
     * @event_id eventID of event that has already finished
     * @total_mood mood of participants in this event
     * @return added archiveEvent status
     */
    protected Boolean archiveEvent(int event_id, String total_mood){
        Event event = getEvent(event_id);
        ArchivedEvent archivedEvent = createArchivedEvent(event.getHostID(), total_mood, event.getTitle(), event.getDescription(), event.getType(), event.getStartTime(), event.getEndTime());
        if (validator.isArchivedEventValid(archivedEvent)) {
            return true;
        }
        return false;
    }

}
