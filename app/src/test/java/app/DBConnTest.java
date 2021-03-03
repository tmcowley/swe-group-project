package app;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import java.sql.SQLException;
import java.sql.Timestamp;

import app.DbConnection;
import app.objects.*;

// Unit tests against DBConnection.java
public class DBConnTest {

    DbConnection db;
    Validator validator;

    public DBConnTest() throws SQLException{
        try{
            db = new DbConnection();
        } catch (SQLException e){
            System.out.println(e.getMessage());
            throw e;
        }
        validator = new Validator();
    }

    @Test
    public void testCodeGeneration(){
        // Test ten uniquely generated event codes
        for (int i = 0; i < 10; i++){
            String event_code = db.generateUniqueEventCode();
            assertTrue(validator.eventCodeIsValid(event_code));
        }

        // Test ten unique host codes
        for (int i = 0; i < 10; i++){
            String host_code = db.generateUniqueHostCode();
            assertTrue(validator.hostCodeIsValid(host_code));
        }

        // Test ten unique template codes
        for (int i = 0; i < 10; i++){
            String template_code = db.generateUniqueTemplateCode();
            assertTrue(validator.templateCodeIsValid(template_code));
        }
    }

    //@Test
    public void testCreation(){
        // test host creation
        Host testHost = db.createHost("testFName", "testLName", "127.0.0.1", "test@test.com");
        int testHostID = testHost.getHostID();

        // test template creation
        Template testTemplate = db.createTemplate(testHostID, "data");
        int testTemplateID = testTemplate.getTemplateID();

        // test participant creation
        Participant testPart = db.createParticipant("127.0.0.1", "testFName", "testLName");
        int testPartID = testPart.getParticipantID();

        // test event creation
        Event testEvent = db.createEvent(testHostID, testTemplateID, "event title", "event desc", "seminar", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
        int testEventID = testEvent.getEventID();

        // test archived_event creation
        ArchivedEvent testArchivedEvent = db.createArchivedEvent(testHostID, "", "event title", "event desc", "seminar", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
        int testArchivedEventID = testArchivedEvent.getEventID();

        // test feedback creation
        Feedback testFeedback = db.createFeedback(testPartID, testEventID, "data", "sentiment", false, new Timestamp(System.currentTimeMillis()));

        // test participant_in_event creation
        Boolean partInEvent = db.addParticipantToEvent(testPartID, testEventID);
        
        // TODO: clear all objects from DB


        // assertions
        assertFalse(testHost == null);
        assertFalse(testTemplate == null);
        assertFalse(testPart == null);
        assertFalse(testEvent == null);
        assertFalse(testFeedback == null);
        assertFalse(partInEvent == null || partInEvent == false);

    }
}
