package app;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Objects;

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
        Host testHost = db.createHost("testFName", "testLName", "test@test.com");
        int testHostID = testHost.getHostID();

        // test template creation
        Template testTemplate = db.createTemplate(testHostID, null);
        int testTemplateID = testTemplate.getTemplateID();

        // test participant creation
        Participant testPart = db.createParticipant("testFName", "testLName");
        int testPartID = testPart.getParticipantID();

        // test event creation
        Event testEvent = db.createEvent(testHostID, testTemplateID, "event title", "event desc", "seminar", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
        int testEventID = testEvent.getEventID();

        // test archived_event creation
        ArchivedEvent testArchivedEvent = db.createArchivedEvent(testHostID, "", "event title", "event desc", "seminar", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
        int testArchivedEventID = testArchivedEvent.getEventID();

        // test feedback creation
        Feedback testFeedback = db.createFeedback(testPartID, testEventID, false, new Timestamp(System.currentTimeMillis()), null, null, null, null, null, null, null);
        int testFeedbackID = testFeedback.getFeedbackID();

        // test participant_in_event creation
        Boolean partInEvent = db.addParticipantToEvent(testPartID, testEventID);

        assertTrue(testHost.getFName().equals("testFName") && testHost.getLName().equals("testLName") && testHost.getEAddress().equals("test@test.com"));
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

    }



    @Test
    public void test_createParticipant(){
        // participant dummy data: first and last names
        String Fname = "testFName";
        String Lname = "testLName";

        // store the participant
        Participant storedParticipant = db.createParticipant(Fname, Lname);
        assertFalse(storedParticipant == null);

        // generate local version using local variables
        int commonPartID = storedParticipant.getParticipantID();
        Participant localParticipant = new Participant(commonPartID, Fname, Lname);
        
        // get stored variant of participant object
        storedParticipant = db.getParticipant(commonPartID);
        assertFalse(storedParticipant == null);

        // ensure local and stored participant match
        assertTrue(Objects.deepEquals(localParticipant, storedParticipant));
    }

    @Test
    public void test_createHost(){
        String Fname = "testFName";
        String Lname = "testLName";
        String email = "test@test6.com";
        Host testHost = db.createHost(Fname, Lname, email);
        assertFalse(testHost == null);
        String testCode = testHost.getHostCode();
        Host testHost2 = db.getHostByCode(testCode);
        assertTrue(testHost.equals(testHost2));

        // DB cleanup
        db.deleteHost(testHost.getHostID());
    }

    @Test 
    public void test_participantInEvent_addParticipantToEvent(){
        String Fname = "testFName";
        String Lname = "testLName";
        String email = "test@test9.com";
        Timestamp ts = new Timestamp(System.currentTimeMillis());

        Participant testPart = db.createParticipant(Fname, Lname);
        assertFalse(testPart == null);
        int testPartID = testPart.getParticipantID();

        Host testHost = db.createHost(Fname, Lname, email);
        assertFalse(testHost == null);
        int testHostID = testHost.getHostID();

        // MAKE A TEMPLATE code here
        // Template testTemplate = db.createTemplate(testHostID, null);
        // assertFalse(testTemplate == null);
        // int testTemplateID = testTemplate.getTemplateID();
        // db.deleteTemplate(testTemplateID);

        Event testEvent = db.createEvent(testHostID, "title", "desc", "lecture", ts, ts);
        assertFalse(testEvent == null);
        int testEventID = testEvent.getEventID();

        Boolean addedToEvent = db.addParticipantToEvent(testPartID, testEventID);
        assertTrue(addedToEvent == true);
        assertTrue(db.participantInEvent(testPartID, testEventID));
        db.muteParticipantInEvent(testPartID, testEventID);
        assertTrue(!db.participantInEventIsMuted(testPartID, testEventID));
        // DB cleanup
        db.removeParticipantFromEvent(testPartID, testEventID);
        db.deleteParticipant(testPartID);
        db.deleteHost(testHostID);
        db.deleteEvent(testEventID);
    }
}
