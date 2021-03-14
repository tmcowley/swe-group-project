package app;

import app.objects.*;

// for testing
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.Test;

// for core objects generation
import java.sql.Timestamp;

// for template creation
import java.util.ArrayList;

// Unit tests against Validator.java
public class ValidatorTest {

    // global validator; set by constructor
    Validator v;

    public ValidatorTest() {
        v = new Validator();
    }

    @Test
    public void test_eventCodeIsValid() {
        String[] validCodes = { "CCCC", "4RF5", "ac4r", "abcd", "aBc7" };
        String[] invalidCodes = { "AAAAAA", "abcd-", "CC-C", " ", "", null };
        // ensure valid codes are recognised as valid
        for (String validCode : validCodes) {
            assertTrue(v.eventCodeIsValid(validCode));
        }
        // ensure invalid codes are not recognised as valid
        for (String invalidCode : invalidCodes) {
            assertFalse(v.eventCodeIsValid(invalidCode));
        }
    }

    @Test
    public void test_templateCodeIsValid() {
        String[] validCodes = { "abcdef", "1foo10", "cgd352", "23fsd3", "a45sdf", "123456" };
        String[] invalidCodes = { "AAAAAAA", "foo1", "41as-f", " ", "", null };
        for (String validCode : validCodes) {
            // ensure valid codes are recognised as valid
            assertTrue(v.templateCodeIsValid(validCode));
        }
        for (String invalidCode : invalidCodes) {
            // ensure invalid codes are not recognised as valid
            assertFalse(v.templateCodeIsValid(invalidCode));
        }
    }

    @Test
    public void test_alphanumericIsValid() {
        String[] validCodes = { "abcde", "a23fs", "cgd35", "23Fsd", "a45sd" };
        String[] invalidCodes = { "AAAAAA", "qwe1", "41a-fa", "     ", " ", "", null };
        for (String validCode : validCodes) {
            // ensure valid codes are recognised as valid
            assertTrue(v.alphanumericIsValid(validCode, 5));
        }
        for (String invalidCode : invalidCodes) {
            // ensure invalid codes are not recognised as valid
            assertFalse(v.alphanumericIsValid(invalidCode, 5));
        }
    }

    @Test
    public void test_hostCodeIsValid() {
        String[] validCodes = { "fish-bird-brother-map", "fish-bird-brother-MAP", "plant-space-path-country" };
        String[] invalidCodes = { "xxxx-bird-brother-map", "fish-bird-brother-ma p", "fish-bird-brother map", " ", "",
                null };
        for (String validCode : validCodes) {
            // ensure valid codes are recognised as valid
            assertTrue(v.hostCodeIsValid(validCode));
        }
        for (String invalidCode : invalidCodes) {
            // ensure invalid codes are not recognised as valid
            assertFalse(v.hostCodeIsValid(invalidCode));
        }
    }

    @Test
    public void test_idIsValid() {
        int[] validIDs = { 1, 1453215, 123, 0, 14 };
        int[] invalidIDs = { -1234, -123, -1 };
        for (int validID : validIDs) {
            assertTrue(v.idIsValid(validID));
        }
        for (int invalidID : invalidIDs) {
            assertFalse(v.idIsValid(invalidID));
        }
    }

    @Test
    public void test_eAddressIsValid() {
        String[] validEmails = { "qweiuhu@ada.com", "qweq_wrq@fas.cn", "xxx@live.warwick.ac.uk" };
        String[] invalidEmails = { "123107128255", "1.5", "@.", " ", "", null };
        for (String validEmail : validEmails) {
            assertTrue(v.eAddressIsValid(validEmail));
        }
        for (String invalidEmail : invalidEmails) {
            assertFalse(v.eAddressIsValid(invalidEmail));
        }
    }

    @Test
    public void test_nameIsValid() {
        String[] validNames = { "name", "Name", "NAME", "a'b", "na-me" };
        String[] invalidNames = { " ", "", null };
        for (String validName : validNames) {
            assertTrue(v.nameIsValid(validName));
        }
        for (String invalidName : invalidNames) {
            assertFalse(v.nameIsValid(invalidName));
        }
    }

    @Test
    public void test_eventTitleIsValid() {
        String[] validTitles = { "Lecture 1", "lecture_1", "lecture1" };
        String[] invalidTitles = { " ", "", null };
        for (String validTitle : validTitles) {
            assertTrue(v.eventTitleIsValid(validTitle));
        }
        for (String invalidTitle : invalidTitles) {
            assertFalse(v.eventTitleIsValid(invalidTitle));
        }
    }

    @Test
    public void test_eventDescriptionIsValid() {
        String[] validDescriptions = { "lecture", "Lecture 1", "lecture1" };
        String[] invalidDescriptions = { " ", "", null };
        for (String validDescription : validDescriptions) {
            assertTrue(v.eventDescriptionIsValid(validDescription));
        }
        for (String invalidDescription : invalidDescriptions) {
            assertFalse(v.eventDescriptionIsValid(invalidDescription));
        }
    }

    @Test
    public void test_eventTypeIsValid() {
        String[] validTypes = { "lecture", "seminar", "conference", "workshop", "other" };
        String[] invalidTypes = { "invalid", " ", "", null };
        for (String validType : validTypes) {
            assertTrue(v.eventTypeIsValid(validType));
        }
        for (String invalidType : invalidTypes) {
            assertFalse(v.eventTypeIsValid(invalidType));
        }
    }

    @Test
    public void test_isEventValid() {
        // generate dummy data
        Timestamp now = new Timestamp(System.currentTimeMillis());

        // create a valid test
        Event validEvent = new Event(0001, 0001, 0001, "Lecture1", "event desc", "seminar", now, now, "CCCC");

        // create an invalid test (event type is incorrect)
        Event invalidEvent = new Event(0001, 0001, 0001, "Lecture1", "event desc", "wrong-type", now, now, "CCCC");

        // ensure valid event is valid, and invalid event is invalid
        assertTrue(v.isEventValid(validEvent));
        assertFalse(v.isEventValid(invalidEvent));
    }

    @Test
    public void test_isHostValid() {
        // create a valid host
        Host validHost = new Host(0001, "fish-bird-brother-map", "moustafa.edwy@gmail.com", "name", "name", false);
        // ensure valid host is recognised as valid
        assertTrue(v.isHostValid(validHost));
    }

    @Test
    public void test_isParticipantValid() {
        // create a valid participant
        Participant validParticipant = new Participant(0001, "name", "name", false);
        // ensure valid participant is recognised as valid
        assertTrue(v.isParticipantValid(validParticipant));
    }

    @Test
    public void test_isTemplateValid() {
        // generate dummy data
        Timestamp timestamp_now = new Timestamp(System.currentTimeMillis());

        // create component of type question
        TemplateComponent question = new TemplateComponent(1, "get-name", "question", "What's your name?", true, 5, null, null, null, "Tom");
        assertFalse(question == null);

        // add the component to components
        ArrayList<TemplateComponent> components = new ArrayList<TemplateComponent>(1);
        components.add(question);

        // create template storing components
        Template testTemplate = new Template(1, 1, "template_name", "a23fsg", timestamp_now, components);
        assertFalse(testTemplate == null);

        assertTrue(v.isTemplateValid(testTemplate));
    }

    @Test
    public void test_sanitizeEventCode() {
        String upperCase = "ABCD";
        String lowerCase = "abcd";
        String sanitized = v.sanitizeEventCode(upperCase);
        assertTrue(sanitized != null && lowerCase.equals(sanitized));
    }

    @Test
    public void test_sanitizeTemplateCode() {
        String upperCase = "ABCDEF";
        String lowerCase = "abcdef";
        String sanitized = v.sanitizeTemplateCode(upperCase);
        assertTrue(sanitized != null && lowerCase.equals(sanitized));
    }

    @Test
    public void test_sanitizeHostCode() {
        String upperCase = "FISH-bird-brother-map";
        String lowerCase = "fish-bird-brother-map";
        String sanitized = v.sanitizeHostCode(upperCase);
        assertTrue(sanitized != null && lowerCase.equals(sanitized));
    }
}
