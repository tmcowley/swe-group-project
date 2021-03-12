package app;

import app.objects.*;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import java.sql.SQLException;
import java.sql.Timestamp;

// used in unique e_address generation
import org.apache.commons.lang3.RandomStringUtils;

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

        // create timestamp
        Timestamp timestamp_now = new Timestamp(System.currentTimeMillis());

        // test host creation
        Host testHost = db.createHost("testFName", "testLName", "test@test.com");
        int testHostID = testHost.getHostID();

        // test template creation
        Template testTemplate = db.createTemplate(testHostID, "template-name", timestamp_now,  null);
        int testTemplateID = testTemplate.getTemplateID();

        // test participant creation
        Participant testPart = db.createParticipant("testFName", "testLName");
        int testPartID = testPart.getParticipantID();

        // test event creation
        Event testEvent = db.createEvent(testHostID, testTemplateID, "event title", "event desc", "seminar", timestamp_now, timestamp_now);
        int testEventID = testEvent.getEventID();

        // test archived_event creation
        ArchivedEvent testArchivedEvent = db.createArchivedEvent(testHostID, "", "event title", "event desc", "seminar", timestamp_now, timestamp_now);
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
        String f_name = "testFName";
        String l_name = "testLName";

        // store the participant
        Participant storedParticipant = db.createParticipant(f_name, l_name);
        assertFalse(storedParticipant == null);

        // generate local version using local variables
        int commonPartID = storedParticipant.getParticipantID();
        Participant localParticipant = new Participant(commonPartID, f_name, l_name);
        
        // get stored variant of participant object
        storedParticipant = db.getParticipant(commonPartID);
        assertFalse(storedParticipant == null);

        // ensure local and stored participants match
        assertTrue(localParticipant.equals(storedParticipant));

        // DB cleanup
        db.deleteParticipant(commonPartID);
    }

    /**
     * generate a unique host email address
     * @return unique host email
     */
    private String generateUniqueEmail(){
        String e_address;
        do {
            // pick valid email
            e_address = "test@test.com";

            // append 8-digit length random alphanumeric string to avoid collision
            e_address.concat(RandomStringUtils.randomAlphanumeric(8).toLowerCase());

        } while (db.emailExists(e_address));

        // ensure host email has not collided
        assertFalse(db.emailExists(e_address));

        // return unique email
        return e_address;
    }

    @Test
    public void test_createHost(){
        // host dummy data: first, last names and email
        String f_name = "testFName";
        String l_name = "testLName";
        String e_address = generateUniqueEmail();

        // store the host
        Host storedHost = db.createHost(f_name, l_name, e_address);
        assertFalse(storedHost == null);
        int commonHostID = storedHost.getHostID();
        String commonHostCode = storedHost.getHostCode();

        // get stored variant of host object
        Host localHost = new Host(commonHostID, commonHostCode, e_address, f_name, l_name);
        assertFalse(localHost == null);

        // ensure local and stored hosts match
        assertTrue(localHost.equals(storedHost));

        // DB cleanup
        db.deleteHost(commonHostID);
    }

    @Test 
    public void test_participantInEvent_addParticipantToEvent(){
        // system object dummy data
        String f_name = "testFName";
        String l_name = "testLName";
        String e_address = generateUniqueEmail();
        Timestamp ts = new Timestamp(System.currentTimeMillis());

        // generate a participant
        Participant testPart = db.createParticipant(f_name, l_name);
        assertFalse(testPart == null);
        int testPartID = testPart.getParticipantID();

        // generate a host (for event generation)
        Host testHost = db.createHost(f_name, l_name, e_address);
        assertFalse(testHost == null);
        int testHostID = testHost.getHostID();

        // MAKE A TEMPLATE code here
        // Template testTemplate = db.createTemplate(testHostID, null);
        // assertFalse(testTemplate == null);
        // int testTemplateID = testTemplate.getTemplateID();
        // db.deleteTemplate(testTemplateID);

        // generate an event
        Event testEvent = db.createEvent(testHostID, "title", "desc", "lecture", ts, ts);
        assertFalse(testEvent == null);
        int testEventID = testEvent.getEventID();

        // add participant to event
        Boolean addedToEvent = db.addParticipantToEvent(testPartID, testEventID);
        assertTrue(addedToEvent == true);
        Boolean participantInEvent = db.participantInEvent(testPartID, testEventID);
        assertTrue(participantInEvent);

        // mute the test participant in their event
        Boolean mutedInEvent = db.muteParticipantInEvent(testPartID, testEventID);
        assertTrue(mutedInEvent == true);
        Boolean mutedInEventCheck2 = db.participantInEventIsMuted(testPartID, testEventID);
        assertTrue(mutedInEventCheck2);

        // DB cleanup
        db.removeParticipantFromEvent(testPartID, testEventID);
        db.deleteParticipant(testPartID);
        db.deleteHost(testHostID);
        db.deleteEvent(testEventID);
    }
}
