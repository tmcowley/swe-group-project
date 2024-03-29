package app;

import app.objects.*;

// testing
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

// used for unique email address generation
import org.apache.commons.lang3.RandomStringUtils;

// Unit tests against DBConnection.java
public class DBConnTest {

    // global DBConn and Validator; set by constructor
    DbConnection db;
    Validator validator;

    public DBConnTest() throws SQLException {
        try {
            db = new DbConnection();
        } catch (SQLException e) {
            // database connection failed; most likely isn't running
            System.out.println(e.getMessage().toUpperCase());
            throw e;
        }
        validator = new Validator();
    }

    @Test
    public void testCodeGeneration() {
        // test ten uniquely generated event codes
        for (int i = 0; i < 10; i++) {
            String event_code = db.generateUniqueEventCode();
            assertTrue(validator.eventCodeIsValid(event_code));
        }

        // test ten unique host codes
        for (int i = 0; i < 10; i++) {
            String host_code = db.generateUniqueHostCode();
            assertTrue(validator.hostCodeIsValid(host_code));
        }

        // test ten unique template codes
        for (int i = 0; i < 10; i++) {
            String template_code = db.generateUniqueTemplateCode();
            assertTrue(validator.templateCodeIsValid(template_code));
        }
    }

    @Test
    public void test_createParticipant() {
        // participant dummy data: first and last names
        String f_name = "testFName";
        String l_name = "testLName";

        // store the participant
        Participant storedParticipant = db.createParticipant(f_name, l_name);
        assertFalse(storedParticipant == null);

        // generate local version using local variables
        int commonParticipantID = storedParticipant.getParticipantID();
        Participant localParticipant = new Participant(commonParticipantID, f_name, l_name);
        assertFalse(localParticipant == null);

        // get stored variant of participant object
        storedParticipant = db.getParticipant(commonParticipantID);
        assertFalse(storedParticipant == null);

        // ensure local and stored participants match
        assertTrue(localParticipant.equals(storedParticipant));

        // DB cleanup
        db.deleteParticipant(commonParticipantID);
    }

    @Test
    public void test_createHost() {
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
    public void test_createEventWithoutTemplate() {
        // host dummy data: first, last names and email
        String f_name = "testFName";
        String l_name = "testLName";
        String e_address = generateUniqueEmail();
        Timestamp ts = new Timestamp(System.currentTimeMillis());

        // generate a host (for event generation)
        Host testHost = db.createHost(f_name, l_name, e_address);
        assertFalse(testHost == null);
        int testHostID = testHost.getHostID();

        // create an event within the database
        Event testEvent = db.createEvent(testHostID, "title", "desc", "lecture", ts, ts);
        assertFalse(testEvent == null);
        String eventCode = testEvent.getEventCode();
        int testEventID = testEvent.getEventID();

        // create an equal event within the back-end
        Event localEvent = new Event(testEventID, testHostID, null, "title", "desc", "lecture", ts, ts, eventCode);
        assertFalse(localEvent == null);

        // ensure local and stored events match
        assertTrue(localEvent.equals(testEvent));

        // DB cleanup
        db.deleteHost(testHostID);
        db.deleteHost(testEventID);
    }

    @Test
    public void test_createEventWithTemplate() {
        // host dummy data: first, last names and email
        String f_name = "testFName";
        String l_name = "testLName";
        String e_address = generateUniqueEmail();
        Timestamp ts = new Timestamp(System.currentTimeMillis());

        // generate a host (for event generation)
        Host testHost = db.createHost(f_name, l_name, e_address);
        assertFalse(testHost == null);
        int testHostID = testHost.getHostID();

        // create a template

        TemplateComponent component = new TemplateComponent("name", "question", "prompt", true, 5, null, null, null,
                "response");
        component = db.createTemplateComponent(component);
        assertFalse(component == null);
        ArrayList<TemplateComponent> components = new ArrayList<>(1);
        components.add(component);
        Template template = db.createTemplate(testHostID, "template_name", ts, components);
        assertFalse(template == null);
        int template_id = template.getTemplateID();

        // create an event within the database
        Event databaseEvent = db.createEvent(testHostID, template_id, "title", "desc", "lecture", ts, ts);
        assertFalse(databaseEvent == null);
        String eventCode = databaseEvent.getEventCode();
        int databaseEventID = databaseEvent.getEventID();

        // create an equal event within the back-end
        Event backendEvent = new Event(databaseEventID, testHostID, template_id, "title", "desc", "lecture", ts, ts,
                eventCode);
        assertFalse(backendEvent == null);

        // ensure local and stored events match
        assertTrue(backendEvent.equals(databaseEvent));

        // DB cleanup
        db.deleteHost(testHostID);
        db.deleteHost(databaseEventID);
    }

    @Test
    public void test_participantInEvent_addParticipantToEvent() {
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

    @Test
    public void test_Bans() {
        // system object dummy data
        String f_name = "testFName";
        String l_name = "testLName";
        String e_address = generateUniqueEmail();

        // create participant
        Participant testPart = db.createParticipant(f_name, l_name);
        assertFalse(testPart == null);
        int testPartID = testPart.getParticipantID();

        // create host
        Host testHost = db.createHost(f_name, l_name, e_address);
        assertFalse(testHost == null);
        int testHostID = testHost.getHostID();

        // ban host and participant
        db.banParticipant(testPartID);
        db.banHost(testHostID);

        // check to see if they were banned
        testPart = db.getParticipant(testPartID);
        testHost = db.getHostByCode(testHost.getHostCode());
        assertTrue(testHost.getSysBan());
        assertTrue(testPart.getSysBan());

        // db cleanup
        db.deleteHost(testHostID);
        db.deleteParticipant(testPartID);
    }

    @Test
    public void test_templateComponents() {
        // system object dummy data
        String f_name = "testFName";
        String l_name = "testLName";
        String e_address = generateUniqueEmail();
        Timestamp timestamp_now = new Timestamp(System.currentTimeMillis());

        // create host
        Host testHost = db.createHost(f_name, l_name, e_address);
        assertFalse(testHost == null);
        int testHostID = testHost.getHostID();

        // create template component
        TemplateComponent component = new TemplateComponent("name", "question", "prompt", true, 5, null, null, null,
                "response");
        component = db.createTemplateComponent(component);
        assertFalse(component == null);
        int component_id = component.getId();

        // create template
        Template testTemplate = db.createTemplate(testHostID, "template-name", timestamp_now, null);
        assertFalse(testTemplate == null);
        int template_id = testTemplate.getTemplateID();
        String template_code = testTemplate.getTemplateCode();

        // add component to template and check it has been added
        Boolean added = db.addComponentToTemplate(component_id, template_id);
        assertTrue(added == true);
        testTemplate = db.getTemplateByCode(template_code);
        assertFalse(testTemplate == null);
        assertTrue(db.componentInTemplate(component_id, template_id));

        // db cleanup
        db.deleteTemplateComponent(component_id);
        db.deleteTemplate(template_id);
        db.deleteHost(testHostID);
    }

    /**
     * generate a unique host email address
     * 
     * @return unique host email
     */
    private String generateUniqueEmail() {
        String e_address;
        do {
            // pick valid email
            e_address = "test@test.com";

            // append 2-digit length random alphanumeric string to avoid collision
            e_address = e_address.concat(RandomStringUtils.randomAlphanumeric(2).toLowerCase());
        } while (db.emailExists(e_address));

        // ensure host email has not collided
        assertFalse(db.emailExists(e_address));

        // return unique email
        return e_address;
    }
}
