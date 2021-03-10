package app;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import java.sql.SQLException;
import java.sql.Timestamp;

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
            // java.lang.IllegalArgumentException: bound must be positive
            // at app.DBConnTest.testCodeGeneration(DBConnTest.java:40)
            assertTrue(validator.hostCodeIsValid(host_code));
        }

        // Test ten unique template codes
        for (int i = 0; i < 10; i++){
            String template_code = db.generateUniqueTemplateCode();
            assertTrue(validator.templateCodeIsValid(template_code));
        }
    }

    //@Test
    public void testCreationAndDeletion(){
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
        Feedback testFeedback = db.createFeedback(testPartID, testEventID, false, new Timestamp(System.currentTimeMillis()), null, null, null, null, null, null);
        int testFeedbackID = testFeedback.getFeedbackID();

        // test participant_in_event creation
        Boolean partInEvent = db.addParticipantToEvent(testPartID, testEventID);

        assertTrue(testHost.getFName().equals("testFName") && testHost.getLName().equals("testLName") && testHost.getIPAddress().equals("127.0.0.1") && testHost.getEAddress().equals("test@test.com"));
        assertTrue(testTemplate.getHostID() == testHostID);
        assertTrue(testPart.getFName().equals("testFName") && testPart.getLName().equals("testLName") && testPart.getIPAddress().equals("127.0.0.1"));
        assertTrue(testEvent.getHostID() == testHostID && testEvent.getTemplateID() == testTemplateID && testEvent.getTitle().equals("event title") && testEvent.getDescription().equals("event desc") && testEvent.getType().equals("seminar"));
        assertTrue(testArchivedEvent.getHostID() == testHostID && testArchivedEvent.getMood().equals("") && testArchivedEvent.getTitle().equals("event title") && testArchivedEvent.getDescription().equals("event desc") && testArchivedEvent.getType().equals("seminar"));
        assertTrue(testFeedback.getParticipantID() == testPartID && testFeedback.getEventID() == testEventID && testFeedback.getAnonymous() == false);


        // clear all testing objects from DB
        db.deleteHost(testHostID);
        db.deleteTemplate(testTemplateID);
        db.deleteParticipant(testPartID);
        db.deleteEvent(testEventID);
        db.deleteArchivedEvent(testArchivedEventID);
        db.deleteFeedback(testFeedbackID);
        db.removeParticipantFromEvent(testPartID, testEventID);

        // assertions
        assertFalse(testHost == null);
        assertFalse(testTemplate == null);
        assertFalse(testPart == null);
        assertFalse(testEvent == null);
        assertFalse(testFeedback == null);
        assertFalse(partInEvent == null || partInEvent == false);

        //assertFalse(true);

    }
}
