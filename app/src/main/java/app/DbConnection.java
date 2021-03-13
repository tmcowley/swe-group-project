package app;

// SQL packages
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Arrays;

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

            //this.conn = DriverManager.getConnection(dbURL);
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
     * @param e_address Email address
     * @return Host instance representing stored data
     */
    public Host createHost(String f_name, String l_name, String e_address){
        // generate unique host code
        String host_code = generateUniqueHostCode();

        // ensure email-address is non-unique
        if (emailExists(e_address)){
            System.out.println("Error: DbConn:createHost(): email-address non-unique");
            return null;
        } 

        PreparedStatement stmt = null;
        ResultSet rs = null;
        Integer host_id = null;
    
        try{
            String createHost = ""
                + "INSERT INTO host(f_name, l_name, e_address, host_code) "
                + "VALUES(?, ?, ?, ?) "
                + "RETURNING host_id;";
            stmt = this.conn.prepareStatement(createHost);
            stmt.setString(1, f_name);
            stmt.setString(2, l_name);
            stmt.setString(3, e_address);
            stmt.setString(4, host_code);
            rs = stmt.executeQuery();

            if (rs.next()) {
                host_id = rs.getInt("host_id");
            }
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }

        // get Host object from host_id
        return getHost(host_id);
    }

    /**
     * Create a template component in the database using a TemplateComponent object
     * @param tc TemplateComponent object
     * @return Stored template component
     */
    public TemplateComponent createTemplateComponent(TemplateComponent tc){

        // ensure template component is valid
        if (!validator.isComponentValid(tc)){
            return null;
        }

        // component with an ID already exists in sys
        if (tc.getId() != null){
            return tc;
        }

        // if component is of type question
        if (tc.getType().equals("question")){
            return createQuestionTemplateComponent(tc.getName(), tc.getType(), tc.getPrompt(), tc.getTextResponse());
        }

        // if component is of type option
        if (tc.getType().equals("radio") || tc.getType().equals("checkbox")){
            return createOptionTemplateComponent(tc.getName(), tc.getType(), tc.getPrompt(), tc.getOptions(), tc.getOptionsAns());
        }

        else {
            return null;
        }
    }

    /**
     * Create a template component in the database
     * @param name Component name
     * @param type Question, or radio, or checkbox
     * @param prompt Question/ prompt
     * @param options Array of radio or checkbox options
     * @param optionsAns Array of boolean responses to options array e.g. t, f, t for checkbox type; empty if type is question
     * @param textResponse Text response field following prompt (null if type radio or checkbox)
     * @return Stored template component
     */
    public TemplateComponent createTemplateComponent(String name, String type, String prompt, String[] options, Boolean[] optionsAns, String textResponse){
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Integer tc_id = null;
        try{
            // create empty template object
            String createTemplateComponent = ""
                + "INSERT INTO template_component(tc_name, tc_type, tc_prompt, tc_options, tc_options_ans, tc_text_response) "
                + "VALUES(?, ?, ?, ?, ?, ?) "
                + "RETURNING tc_id;";
            stmt = this.conn.prepareStatement(createTemplateComponent);
            stmt.setString(1, name);
            stmt.setString(2, type);
            stmt.setString(3, prompt);
            stmt.setArray(4, this.conn.createArrayOf("TEXT", options));
            stmt.setArray(5, this.conn.createArrayOf("BOOLEAN", optionsAns));
            stmt.setString(6, textResponse);
            rs = stmt.executeQuery();

            if (rs.next()) {
                tc_id = rs.getInt("tc_id");
            }

        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }

        // get Template object by ID
        return getTemplateComponent(tc_id);
    }

    public TemplateComponent createOptionTemplateComponent(String name, String type, String prompt, String[] options, Boolean[] optionsAns){
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Integer tc_id = null;
        try{
            // create empty template object
            String createTemplateComponent = ""
                + "INSERT INTO template_component(tc_name, tc_type, tc_prompt, tc_options, tc_options_ans) "
                + "VALUES(?, ?, ?, ?, ?) "
                + "RETURNING tc_id;";
            stmt = this.conn.prepareStatement(createTemplateComponent);
            stmt.setString(1, name);
            stmt.setString(2, type);
            stmt.setString(3, prompt);
            stmt.setArray(4, this.conn.createArrayOf("TEXT", options));
            stmt.setArray(5, this.conn.createArrayOf("BOOLEAN", optionsAns));
            rs = stmt.executeQuery();

            if (rs.next()) {
                tc_id = rs.getInt("tc_id");
            }

        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }

        // get Template object by ID
        return getTemplateComponent(tc_id);
    }

    public TemplateComponent createQuestionTemplateComponent(String name, String type, String prompt, String textResponse){
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Integer tc_id = null;

        // debugging statements
        // System.out.println("createQuestionTemplateComponent inputs: name" + name + ", type: " + type + ", prompt: " + prompt + ", text response: " + textResponse);

        try{
            // create empty template object
            String createTemplateComponent = ""
                + "INSERT INTO template_component(tc_name, tc_type, tc_prompt, tc_text_response) "
                + "VALUES(?, ?, ?, ?) "
                + "RETURNING tc_id;";
            stmt = this.conn.prepareStatement(createTemplateComponent);
            stmt.setString(1, name);
            stmt.setString(2, type);
            stmt.setString(3, prompt);
            stmt.setString(4, textResponse);

            rs = stmt.executeQuery();

            if (rs.next()) {
                tc_id = rs.getInt("tc_id");
            }

        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }

        if (tc_id == null){
            System.out.println("Error in DB: createQuestionTemplateComponent: tc_id is null -> inputs not collected");
            return null;
        }

        // get Template object by ID
        return getTemplateComponent(tc_id);
    }

    /**
     * Create an empty template in the database
     * @param host_id Creator ID
     * @param template_name Name of the template
     * @param timestamp Created time
     * @return Stored template instance
     */
    public Template createEmptyTemplate(int host_id, String template_name, Timestamp timestamp){
        return createTemplate(host_id, template_name, timestamp, new ArrayList<TemplateComponent>());
    }

    /**
     * Create an empty template in the database
     * @param host_id Creator ID
     * @param template_name Name of the template
     * @param timestamp Created time
     * @param components An arraylist of linked template components
     * @return Stored template instance
     */
    public Template createTemplate(int host_id, String template_name, Timestamp timestamp, ArrayList<TemplateComponent> components){
        // generate unique template code
        String template_code = generateUniqueTemplateCode();

        PreparedStatement stmt = null;
        ResultSet rs = null;
        Integer template_id = null;
        try{
            // create empty template
            String createTemplate = ""
                + "INSERT INTO template(host_id, template_code, template_name, timestamp) "
                + "VALUES(?, ?, ?, ?) "
                + "RETURNING template_id";
            stmt = this.conn.prepareStatement(createTemplate);
            stmt.setInt(1, host_id);
            stmt.setString(2, template_code);
            stmt.setString(3, template_name);
            stmt.setTimestamp(4, timestamp);
            rs = stmt.executeQuery();
            if (rs.next()) {
                template_id = rs.getInt("template_id");
            }

            // if Template contains components
            if (components != null){
                for (TemplateComponent tc : components){
                    tc = createTemplateComponent(tc);
                    int component_id = tc.getId();
                    addComponentToTemplate(component_id, template_id);
                }
            }

        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }

        // get Template object by ID
        return getTemplate(template_id);
    }

    /**
     * Create a participant in the database
     * @param f_name First name
     * @param l_name Last name
     * @return Participant instance representing stored data
     */
    public Participant createParticipant(String f_name, String l_name){
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Integer participant_id = null;
        String createParticipant;

        try{
            createParticipant = ""
                + "INSERT INTO participant(f_name, l_name) "
                + "VALUES(?, ?) "
                + "RETURNING participant_id";
            stmt = this.conn.prepareStatement(createParticipant);
            stmt.setString(1, f_name);
            stmt.setString(2, l_name);

            rs = stmt.executeQuery();
            if (rs.next()) {
                participant_id = rs.getInt("participant_id");
            }
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }
        return getParticipant(participant_id);
    }
    
    /**
     * Create a event with template in the database
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
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }

        // get Event by ID
        return getEvent(event_id);
    }

    /**
     * Create a event without template in the database
     * @param host_id Host id of its host
     * @param template_id Template id it used
     * @param title Event title
     * @param desc Event description
     * @param type Event type
     * @param start_time Start time of the event
     * @param end_time End time of the event
     * @return Event instance representing stored data
     */
    public Event createEvent(int host_id, String title, String desc, String type, Timestamp start_time, Timestamp end_time){
        // generate unique event code
        String event_code = generateUniqueEventCode();

        PreparedStatement stmt = null;
        ResultSet rs = null;
        Integer event_id = null;
        try{
            String createEvent = ""
                + "INSERT INTO event(host_id, title, description, type, start_time, end_time, event_code) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?) "
                + "RETURNING event_id;";
            stmt = this.conn.prepareStatement(createEvent);
            stmt.setInt(1, host_id);
            stmt.setString(2, title);
            stmt.setString(3, desc);
            stmt.setString(4, type);
            stmt.setTimestamp(5, start_time);
            stmt.setTimestamp(6, end_time);
            stmt.setString(7, event_code);

            rs = stmt.executeQuery();
            if (rs.next()) {
                event_id = rs.getInt("event_id");
            }
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
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
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }

        return getArchivedEvent(event_id);
    }


    /**
     * Create an instance of feedback against an event using feedback objects
     * @param feedback Feedback object to be stored
     * @return Feedback instance representing stored data
     */
    public Feedback createFeedback(Feedback feedback){

        // convert key_results from String ArrayList to String array
        ArrayList<String> keyResultsList = feedback.getKey_Results();
        String[] keyResultsArray = keyResultsList.toArray(new String[keyResultsList.size()]);

        return createFeedback(feedback.getParticipantID(), feedback.getEventID(), feedback.getAnonymous(), feedback.getTimestamp(), feedback.getResults(), feedback.getWeights(), feedback.getTypes(), feedback.getKeys(), feedback.getSub_Weights(), feedback.getCompound(), keyResultsArray);
    }

    /**
     * Create an instance of feedback against an event
     * @param participant_id Participant id of the participant who created this feedback
     * @param event_id Event id of the event which this feedback is written for
     * @param anonymous Whether this feedback is anonymous
     * @param time_stamp Time when the feedback was created
     * @param results Results array to specific feedback queries
     * @param weights Weights array (for weighted mean) associated with each result
     * @param types Type array of query that produced each result
     * @param keys Keys array that holds whether a result is a key result or not
     * @param sub_weights Sub_weights array that holds weights (unprocessed then processed) associated with each set result in multiple choice queries
     * @param compound Compound score of sentiment
     * @param key_results an array of all key results
     * @return Feedback instance representing stored data
     */
    public Feedback createFeedback(int participant_id, int event_id, boolean anonymous, Timestamp time_stamp, String[] results, Float[] weights, byte[] types, Boolean[] keys, byte[][] sub_weights, Float compound, String[] key_results){

        // results         TEXT[],
        // weights         REAL[],
        // types           bytea,
        // keys            BOOLEAN[],
        // sub_weights     bytea[],
        // compound        REAL,
        // key_results     TEXT[],

        PreparedStatement stmt = null;
        ResultSet rs = null;
        Integer feedback_id = null;
        try{
            String createProcessedFeedback = ""
                + "INSERT INTO feedback(participant_id, event_id, anonymous, time_stamp, results, weights, types, keys, compound, key_results) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
                + "RETURNING feedback_id;";
            stmt = this.conn.prepareStatement(createProcessedFeedback);

            stmt.setInt(1, participant_id);
            stmt.setInt(2, event_id);
            stmt.setBoolean(3, anonymous);
            stmt.setTimestamp(4, time_stamp);
            stmt.setArray(5, this.conn.createArrayOf("TEXT", results));
            stmt.setArray(6, this.conn.createArrayOf("float4", weights));
            stmt.setBytes(7, types);
            stmt.setArray(8, this.conn.createArrayOf("BOOLEAN", keys));
            stmt.setFloat(9, compound.floatValue());
            stmt.setArray(10, this.conn.createArrayOf("TEXT", key_results));

            rs = stmt.executeQuery();
            if (rs.next()) {
                feedback_id = rs.getInt("feedback_id");
            }
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
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
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
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
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
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
                + "participant_in_event.event_id=?;";
            stmt = this.conn.prepareStatement(queryMutedState);
            stmt.setInt(1, participant_id);
            stmt.setInt(2, event_id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                muted = rs.getBoolean("muted");
            }
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
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
                + "SET muted = ?"
                + "WHERE "
                + "participant_in_event.participant_id=? "
                + "AND "
                + "participant_in_event.event_id=?;";
            stmt = this.conn.prepareStatement(muteParticipant);
            stmt.setBoolean(1, true);
            stmt.setInt(2, participant_id);
            stmt.setInt(3, event_id);
            stmt.executeUpdate();
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }
        return participantInEventIsMuted(participant_id, event_id);
    }

    /**
     * Check whether a template component is in a template
     * @param component_id Component to be checked
     * @param template_id Template which may contain this template component
     * @return Method success state 
     */
    public boolean componentInTemplate(int component_id, int template_id){
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Boolean state = null;
        try{
            String queryComponentInTemplate = ""
                + "SELECT EXISTS "
                + "(SELECT 1 FROM component_in_template WHERE "                
                + "component_in_template.component_id=? "
                + "AND "
                + "component_in_template.template_id=? " 
                + ");";
            stmt = this.conn.prepareStatement(queryComponentInTemplate);
            stmt.setInt(1, component_id);
            stmt.setInt(2, template_id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                state = rs.getBoolean(1);
            }
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }
        return state;
    }

    /**
     * Add a template component to a template
     * @param component_id Component 
     * @param template_id Template which contains this template component
     * @return Method success state 
     */
    public Boolean addComponentToTemplate(int component_id, int template_id){
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            String addComponentToTemplate = ""
                + "INSERT INTO component_in_template(component_id, template_id) "
                + "VALUES(?, ?) ";
            stmt = this.conn.prepareStatement(addComponentToTemplate);
            stmt.setInt(1, component_id);
            stmt.setInt(2, template_id);
            stmt.executeUpdate();
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }

        // return success state (select query)
        return componentInTemplate(component_id, template_id);
    }

    /**
     * Get a Host object by its code.
     * @param host_code host code
     * @return Host object corresponding to its code
     */
    public Host getHostByCode(String host_code){
        host_code = validator.sanitizeHostCode(host_code);
        if (!validator.hostCodeIsValid(host_code)) return null;
        if (!hostCodeExists(host_code)) return null;

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
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }
        
        // get Host by ID
        return getHost(host_id);
    }

    /**
     * Get a Host object by their email address.
     * @param e_address host email address
     * @return host with email e_address
     */
    public Host getHostByEmail(String e_address){

        // ensure the email is valid
        if (!validator.eAddressIsValid(e_address)) 
            return null;

        // ensure the email exists in the system
        if (!emailExists(e_address)) 
            return null;

        // host email address valid and exists --> query db
        PreparedStatement stmt = null;
        Integer host_id = null;
        ResultSet rs = null;
        try{
            String queryHostByEmail= "SELECT host_id FROM host WHERE e_address=? LIMIT 1;";
            stmt = this.conn.prepareStatement(queryHostByEmail);
            stmt.setString(1, e_address);
            rs = stmt.executeQuery();
            if (rs.next()) {
                host_id = rs.getInt("host_id");
            }
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
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
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }
        
        // get Template by ID
        return getTemplate(template_id);
    }

    /**
     * Get an array of templates created by a host
     * ordered in descending order of creation
     * @param host_id host's ID
     * @return array of templates
     */
    public Template[] getTemplatesByHostID(int host_id){
        // template code valid and exists --> query db
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Template[] foundTemplates = new Template[0];
        try{
            String queryTemplateByCode = "SELECT * FROM template WHERE host_id=? ORDER BY timestamp DESC;";
            stmt = this.conn.prepareStatement(queryTemplateByCode, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setInt(1, host_id);
            rs = stmt.executeQuery();

            // get count of results
            rs.last();
            int rsSize = rs.getRow();
            foundTemplates = new Template[rsSize];
            Template foundTemplate = null;
            int templateCount = 0;
            rs.beforeFirst();
            
            // get each matched template
            while (rs.next()) {
                foundTemplate = new Template(rs.getInt("template_id"), rs.getInt("host_id"), rs.getString("template_name"), rs.getString("template_code"), rs.getTimestamp("timestamp"));
                foundTemplates[templateCount] = foundTemplate;
                foundTemplate = null;
                templateCount++;
            }
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }
        
        return foundTemplates;
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
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }
        
        // get Event from event ID
        return getEvent(event_id);
    }

    // PRIVATE METHODS

    /**
     * Get a template component instance using template component ID
     * @param tc_id Template component ID
     * @return Found TemplateComponent instance
     */
    private TemplateComponent getTemplateComponent(int tc_id){
        PreparedStatement stmt = null;
        ResultSet rs = null;
        TemplateComponent tc = null;
        try{
            String selectComponentByID = ""
                + "SELECT * FROM template_component "
                + "WHERE template_component.tc_id = ? "
                + "LIMIT 1;";
            stmt = this.conn.prepareStatement(selectComponentByID);
            stmt.setInt(1, tc_id);

            rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("tc_id");
                String name = rs.getString("tc_name");
                String type = rs.getString("tc_type");
                String prompt = rs.getString("tc_prompt");
                String[] options = null;
                Boolean[] options_ans = null;
                String text_response = null;

                // component is of type question
                if (type.equals("question")){
                    text_response = rs.getString("tc_text_response");
                }

                // component is of type options
                if (type.equals("radio") || type.equals("checkbox")){
                    options = (String[]) rs.getArray("tc_options").getArray();
                    options_ans = (Boolean[]) rs.getArray("tc_options_ans").getArray();
                }

                tc = new TemplateComponent(id, name, type, prompt, options, options_ans, text_response);
            }
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }
        return tc;
    }

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
                String e_address = rs.getString("e_address");
                String f_name = rs.getString("f_name");
                String l_name = rs.getString("l_name");
                boolean sys_ban = rs.getBoolean("sys_ban");

                host = new Host(host_id, host_code, e_address, f_name, l_name, sys_ban);
            }
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }
        return host;
    }

    /**
     * Get an Template object from an template ID
     * @param template_id Template ID
     * @return Template object with ID of template_id
     */
    private Template getTemplate(int template_id){
        PreparedStatement stmt1 = null;
        PreparedStatement stmt2 = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        Template template = null;
        TemplateComponent templateComponent = null;
        ArrayList<TemplateComponent> components = new ArrayList<TemplateComponent>();
        Integer host_id = null;
        String template_code = null;
        String template_name = null;
        Timestamp timestamp = null;
        try{
            String selectTemplateByID = ""
                + "SELECT * FROM template "
                + "WHERE template.template_id = ? "
                + "LIMIT 1;";

            // String alternativeSelectComponentsByID = ""
            //     + "SELECT * FROM template_component "
            //     + "INNER JOIN (component_in_template ct INNER JOIN template t USING(template_id)) USING(tc_id)"
            //     + "INNER JOIN (component_in_template INNER JOIN template USING(template_id)) USING(tc_id)"
            //     + "WHERE template.template_id = ? ";
            
            // works but has extra fields
            String selectComponentsByID = ""
                + "SELECT * FROM template_component c "
                + "INNER JOIN component_in_template ct " 
                + "ON (c.tc_id = ct.component_id) "
                + "WHERE (ct.template_id = ?);";

            stmt1 = this.conn.prepareStatement(selectTemplateByID);
            stmt1.setInt(1, template_id);

            // get template main fields
            rs1 = stmt1.executeQuery();
            if (rs1.next()) {
                template_id = rs1.getInt("template_id");
                host_id = rs1.getInt("host_id");
                template_name = rs1.getString("template_name");
                template_code = rs1.getString("template_code");
                timestamp = rs1.getTimestamp("timestamp");
            }
            stmt2 = this.conn.prepareStatement(selectComponentsByID);
            stmt2.setInt(1, template_id);

            // get all components
            rs2 = stmt2.executeQuery();
            while (rs2.next()) {
                int component_id = rs2.getInt("tc_id");
                String name = rs2.getString("tc_name");
                String type = rs2.getString("tc_type");
                String prompt = rs2.getString("tc_prompt"); 
                String[] options = null;
                Boolean[] options_ans = null;
                String text_response = null;

                // component is of type question
                if (type.equals("question")){
                    text_response = rs2.getString("tc_text_response");
                }

                // component is of type options
                if (type.equals("radio") || type.equals("checkbox")){
                    options = (String[]) rs2.getArray("tc_options").getArray();
                    options_ans = (Boolean[]) rs2.getArray("tc_options_ans").getArray();
                }

                templateComponent = new TemplateComponent(component_id, name, type, prompt, options, options_ans, text_response);
                components.add(templateComponent);
            }
            template = new Template(template_id, host_id, template_name, template_code, timestamp, components);
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
        } finally {
            try { if (stmt1 != null) stmt1.close(); } catch (Exception e) {};
            try { if (stmt2 != null) stmt2.close(); } catch (Exception e) {};
            try { if (rs1 != null)   rs1.close(); }   catch (Exception e) {};
            try { if (rs2 != null)   rs2.close(); }   catch (Exception e) {};
        }
        return template;
    }

    /**
     * Get an Participant object from an participant ID
     * @param participant_id Participant ID
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
                String f_name = rs.getString("f_name");
                String l_name = rs.getString("l_name");
                boolean sys_ban = rs.getBoolean("sys_ban");

                participant = new Participant(participant_id, f_name, l_name, sys_ban);
            }
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }
        return participant;
    }

    /**
     * Get an Event object from an event ID
     * @param event_id Event ID
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
                // collect event attributes
                event_id = rs.getInt("event_id");
                int host_id = rs.getInt("host_id");
                Integer template_id = (Integer) rs.getObject("template_id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                String type = rs.getString("type");
                Timestamp start_time = rs.getTimestamp("start_time");
                Timestamp end_time = rs.getTimestamp("end_time");
                String event_code = rs.getString("event_code");

                event = new Event(event_id, host_id, template_id, title, description, type, start_time, end_time, event_code);
            }
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }
        return event;
    }

    /**
     * Get an archived Event object from an event ID
     * @param event_id Event ID
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
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }
        return archivedEvent;
    }

    /**
     * Get a feedback instance using feedback ID
     * @param feedback_id Feedback ID
     * @return Found feedback instance
     */
    public Feedback getFeedback(int feedback_id){
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Feedback feedback = null;
        try{
            String queryFeedback = "SELECT * FROM feedback WHERE feedback_id=? LIMIT 1;";
            stmt = this.conn.prepareStatement(queryFeedback);
            stmt.setInt(1, feedback_id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                // ArrayList<String> keyResults = new ArrayList<String>();
                // String[] keyresults = (String[]) rs.getArray("key_results").getArray();
                // for (String result : keyresults) {
                //     keyResults.add(result);
                // }
                
                // convert key_results from String Array to String ArrayList
                String[] keyresults = (String[]) rs.getArray("key_results").getArray();
                ArrayList<String> keyResults = new ArrayList<String>(Arrays.asList(keyresults));

                feedback = new Feedback(rs.getInt("feedback_id"), rs.getInt("participant_id"), rs.getInt("event_id"),
                (String[]) rs.getArray("results").getArray(), (Float[]) rs.getArray("weights").getArray(), rs.getBytes("types"), (Boolean[]) rs.getArray("keys").getArray(), new byte[0][0], rs.getBoolean("anonymous"), rs.getTimestamp("time_stamp"), Float.valueOf(rs.getFloat("compound")), keyResults);
            }
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }
        
        return feedback;
    }

    /**
     * Get an array of feedback instances by event ID
     * @param event_id Event ID
     * @return An array of found feedback instances in this event
     */
    public Feedback[] getFeedbacksByEventID(int event_id){
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Feedback[] foundFeedbacks = new Feedback[0];
        try{
            String queryFeedbackByEventID = "SELECT * FROM feedback WHERE event_id=? ORDER BY feedback.time_stamp DESC;";
            stmt = this.conn.prepareStatement(queryFeedbackByEventID, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setInt(1, event_id);
            rs = stmt.executeQuery();
            rs.last();
            int rsSize= rs.getRow();
            foundFeedbacks = new Feedback[rsSize];
            Feedback foundFeedback = null;
            int feedbackCount = 0;
            rs.beforeFirst();
            while (rs.next()) {
                String[] keyresults = (String[]) rs.getArray("key_results").getArray();
                ArrayList<String> keyResults = new ArrayList<String>(Arrays.asList(keyresults));
                foundFeedback = new Feedback(rs.getInt("feedback_id"), rs.getInt("participant_id"), rs.getInt("event_id"),
                (String[]) rs.getArray("results").getArray(), (Float[]) rs.getArray("weights").getArray(), rs.getBytes("types"), (Boolean[]) rs.getArray("keys").getArray(), new byte[0][0], rs.getBoolean("anonymous"), rs.getTimestamp("time_stamp"), Float.valueOf(rs.getFloat("compound")), keyResults);
                foundFeedbacks[feedbackCount] = foundFeedback;
                feedbackCount++;
            }
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());;
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }
        
        return foundFeedbacks;
    }

    /**
     * Get an array of feedback instances by event ID
     * @param event_id Event ID
     * @param participant_id Participant ID
     * @return An array of found feedback instances by a specific participant in this event in descending order of time generated (newest first)
     */
    // 
    public Feedback[] getFeedbacksInEventByParticipantID(int event_id, int participant_id){
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Feedback[] foundFeedbacks = new Feedback[0];
        try{
            String queryFeedbackByEventID = "SELECT * FROM feedback WHERE event_id=? AND participant_id=? ORDER BY feedback.time_stamp DESC;";
            stmt = this.conn.prepareStatement(queryFeedbackByEventID, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setInt(1, event_id);
            stmt.setInt(2, participant_id);
            rs = stmt.executeQuery();
            rs.last();
            int rsSize= rs.getRow();
            foundFeedbacks = new Feedback[rsSize];
            Feedback foundFeedback = null;
            int feedbackCount = 0;
            rs.beforeFirst();
            while (rs.next()) {
                ArrayList<String> keyResults = new ArrayList<String>();
                String[] keyresults = (String[]) rs.getArray("key_results").getArray();
                for (String result : keyresults) {
                    keyResults.add(result);
                }
                foundFeedback = new Feedback(rs.getInt("feedback_id"), rs.getInt("participant_id"), rs.getInt("event_id"),
                (String[]) rs.getArray("results").getArray(), (Float[]) rs.getArray("weights").getArray(), rs.getBytes("types"), (Boolean[]) rs.getArray("keys").getArray(), new byte[0][0], rs.getBoolean("anonymous"), rs.getTimestamp("time_stamp"), Float.valueOf(rs.getFloat("compound")), keyResults);
                foundFeedbacks[feedbackCount] = foundFeedback;
                feedbackCount++;
            }
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }
        
        return foundFeedbacks;
    }

    // /**
    //  * Get a feedback object from an feedback ID
    //  * @param feedback_id feedback ID
    //  * @return Feedback object with ID of feedback_id
    //  */
    // public Feedback getFeedback(int feedback_id){
    //     PreparedStatement stmt = null;
    //     ResultSet rs = null;
    //     Feedback feedback = null;
    //     try{
    //         String selectFeedbackByID = ""
    //             + "SELECT * FROM feedback "
    //             + "WHERE feedback.feedback_id = ? "
    //             + "LIMIT 1;";
    //         stmt = this.conn.prepareStatement(selectFeedbackByID);
    //         stmt.setInt(1, feedback_id);

    //         rs = stmt.executeQuery();
    //         if (rs.next()) {
    //             // non-sentiment related fields
    //             feedback_id = rs.getInt("feedback_id");
    //             int participant_id = rs.getInt("participant_id");
    //             int event_id = rs.getInt("event_id");
    //             boolean anonymous = rs.getBoolean("anonymous");
    //             Timestamp time_stamp = rs.getTimestamp("time_stamp");

    //             // collect sentiment related fields
    //             String[] results    = (String[])rs.getArray("results").getArray();
    //             Float[] weights     = (Float[])rs.getArray("weights").getArray();
    //             Integer[] type      = (Integer[])rs.getArray("type").getArray();
    //             Boolean[] keys      = (Boolean[])rs.getArray("keys").getArray();
    //             Float compound      = rs.getFloat("compound"); 
    //             ArrayList<String> key_results = new ArrayList (Arrays.asList(String[])rs.getArray("key_results").getArray());

    //             feedback = new Feedback(feedback_id, participant_id, event_id, anonymous, time_stamp, results, weights, type, keys, compound, key_results);

    //             // (int feedback_id, int participant_id, int event_id, boolean anonymous, Timestamp timestamp, String[] results, Float[] weights, Integer[] types, Boolean[] keys)
    //         }
    //     } catch (SQLException e){
    //         System.out.println(e.getMessage().toUpperCase());;
    //     } finally {
    //         try { if (stmt != null) stmt.close(); } catch (Exception e) {};
    //         try { if (rs != null)   rs.close(); }   catch (Exception e) {};
    //     }
    //     return feedback;
    // }

    // (int feedback_id, int host_id, int event_id, String[] results, float[] weights, int[] type, int[] key, float compound, String[] key_results, boolean anonymous, Timestamp timestamp)

    /**
     * Check if the given event code exists, 
     * and is against an active event
     * @param event_code the event code
     * @return existence state of eventCode, null if fails
     */
    public Boolean eventCodeExists(String event_code){
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
            e.printStackTrace();
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
    public Boolean templateCodeExists(String template_code){
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
            e.printStackTrace();
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
    public Boolean hostCodeExists(String host_code){
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
            e.printStackTrace();
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
     * Check whether an email address is already registered
     * @param e_address Email address
     * @return Return True if email address is exist
     */
    public Boolean emailExists(String e_address){
        // ensure email is valid
        if (!validator.eAddressIsValid(e_address)) return false;

        PreparedStatement stmt = null;
        ResultSet rs = null;
        Boolean emailExists = false;
        try{
            String queryHostEmailExists = ""
                + "SELECT EXISTS(SELECT * FROM host WHERE e_address=?);";
            stmt = this.conn.prepareStatement(queryHostEmailExists);
            stmt.setString(1, e_address);
            rs = stmt.executeQuery();
            if (rs.next()) {
                emailExists = rs.getBoolean(1);
            }
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
            try { if (rs != null)   rs.close(); }   catch (Exception e) {};
        }
        return emailExists;
    }

    /**
     * ban host using host ID 
     * @param host_id banned hostID
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
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
        }
        if (bannedHost == null) return null;
        return (bannedHost != 0);
    }

    /**
     * ban host using email address 
     * @param host_id banned hostID
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
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
        }
        if (bannedHost == null) return null;
        return (bannedHost != 0);
    }

    /**
     * Ban participant using participant ID 
     * @param participant_id banned participantID
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
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
        }
        if (bannedParticipant == null) return null;
        return (bannedParticipant != 0);
    }

    // /**
    //  * Update data in templates 
    //  * @template_id templateID of template that needs to be changed
    //  * @data updated data
    //  * @return data change status
    //  */
    // protected Boolean addDataToTemplate(int template_id, String data){
    //     PreparedStatement stmt = null;
    //     Integer templateFound = null;
    //     try{
    //         String updateTemplate = ""
    //             + "UPDATE template "
    //             + "SET data = ? "
    //             + "WHERE template_id = ?;";
    //         stmt = this.conn.prepareStatement(updateTemplate);
    //         stmt.setString(1, data);
    //         stmt.setInt(2, template_id);
    //         templateFound = stmt.executeUpdate();
    //     } catch (SQLException e){
    //         System.out.println(e.getMessage().toUpperCase());
    //         e.printStackTrace();
    //     } finally {
    //         try { if (stmt != null) stmt.close(); } catch (Exception e) {};
    //     }
    //     if (templateFound == null) return null;
    //     return (templateFound != 0);
    // }

    /**
     * Delete host by ID
     * @param host_id host ID of the host needed to be deleted
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
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
        }
        if (hostDeleted == null) return null;
        return (hostDeleted != 0);
    }

    /**
     * Delete template by ID
     * @param template_id template ID of the template needed to be deleted
     * @return delete status
     */
    public Boolean deleteTemplate(int template_id){
        PreparedStatement stmt = null;
        Integer templateDeleted = null;
        try{
            Template template = getTemplate(template_id);
            for (TemplateComponent templateComponent : template.getComponents()) {
                deleteTemplateComponent(templateComponent.getId());
            }
            String deleteTemplate = ""
                + "DELETE FROM template "
                + "WHERE template_id = ?;";
            stmt = this.conn.prepareStatement(deleteTemplate);
            stmt.setInt(1, template_id);
            templateDeleted = stmt.executeUpdate();
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
        }
        if (templateDeleted == null) return null;
        return (templateDeleted != 0);
    }

    /**
     * Delete template component by ID
     * @param tc_id template component ID of the template needed to be deleted
     * @return Delete status
     */
    public Boolean deleteTemplateComponent(int tc_id){
        PreparedStatement stmt = null;
        Integer templateComponentDeleted = null;
        try{
            String deleteTemplateComponent = ""
                + "DELETE FROM template_component "
                + "WHERE tc_id = ?;";
            stmt = this.conn.prepareStatement(deleteTemplateComponent);
            stmt.setInt(1, tc_id);
            templateComponentDeleted = stmt.executeUpdate();
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
        }
        if (templateComponentDeleted == null) return null;
        return (templateComponentDeleted != 0);
    }

    /**
     * Delete participant by ID
     * @param participant_id participant ID of the participant needed to be deleted
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
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
        }
        if (participantDeleted == null) return null;
        return (participantDeleted != 0);
    }

    /**
     * Delete event by ID
     * @param event_id event ID of archived event needed to be deleted
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
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
        }
        if (eventDeleted == null) return null;
        return (eventDeleted != 0);
    }

    /**
     * Delete archived event by ID
     * @param event_id event ID of archived event needed to be deleted
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
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
        }
        if (eventDeleted == null) return null;
        return (eventDeleted != 0);
    }

    /**
     * Delete participant and event pair by ID
     * @param feedback_id feedback ID of the feedback needed to be deleted
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
     * @param participant_id participant ID of the pair needed to be deleted
     * @param event_id event ID of the pair needed to be deleted
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
            stmt.setInt(2, event_id);
            deletedLink = stmt.executeUpdate();
        } catch (SQLException e){
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
        }
        if (deletedLink == null) return null;
        return (deletedLink != 0);
    }

    /**
     * Delete finished events and add it to archivedEvents 
     * @param event_id eventID of event that has already finished
     * @param total_mood mood of participants in this event
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
