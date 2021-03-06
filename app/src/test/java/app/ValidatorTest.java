package app;
import app.objects.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import java.lang.reflect.Array;
import java.sql.Timestamp;

// Unit tests against Validator.java
public class ValidatorTest {

    Validator v;

    public ValidatorTest(){
        v = new Validator();
    }

    @Test
    public void test_eventCodeIsValid(){
        String[] validCodes = {
            "CCCC",
            "4RF5",
            "ac4r",
            "abcd",
            "aBc7"
        };
        String[] invalidCodes = {
            "AAAAAA",
            "abcd-",
            "CC-C",
            "",
            null
        };
        for (String validCode : validCodes){
            assertTrue(v.eventCodeIsValid(validCode));
        }
        for (String invalidCode : invalidCodes){
            assertFalse(v.eventCodeIsValid(invalidCode));
        }
    }

    @Test
    public void test_templateCodeIsValid(){
        String[] validCodes = {
            "asdfgh",
            "a23fsg",
            "cgd352",
            "23fsd3",
            "a45sdf"
        };
        String[] invalidCodes = {
            "AAAAAAA",
            "qwer1",
            "41as-f",
            "",
            null
        };
        for (String validCode : validCodes){
            assertTrue(v.templateCodeIsValid(validCode));
        }
        for (String invalidCode : invalidCodes){
            assertFalse(v.templateCodeIsValid(invalidCode));
        }
    }

    @Test
    public void test_alphanumericIsValid(){
        String[] validCodes = {
            "asdfg",
            "a23fs",
            "cgd35",
            "23Fsd",
            "a45sd"
        };
        String[] invalidCodes = {
            "AAAAAA",
            "qwe1",
            "41a-f",
            "",
            null
        };
        for (String validCode : validCodes){
            assertTrue(v.alphanumericIsValid(validCode, 5));
        }
        for (String invalidCode : invalidCodes){
            assertFalse(v.alphanumericIsValid(invalidCode, 5));
        }
    }

    @Test
    public void test_hostCodeIsValid(){
        String[] validCodes = {
            "fish-bird-brother-map",
            "fish-bird-brother-MAP"
        };
        String[] invalidCodes = {
            "xxxx-bird-brother-map",
            "fish-bird-brother-ma p",
            "fish-bird-brother map",
            "",
            null
        };
        for (String validCode : validCodes){
            assertTrue(v.hostCodeIsValid(validCode));
        }
        for (String invalidCode : invalidCodes){
            assertFalse(v.hostCodeIsValid(invalidCode));
        }
    }

    @Test
    public void test_idIsValid(){
        int[] validIDs = {
            1,
            1453215,
            123,
            0,
            14
        };
        int[] invalidIDs = {
            -1234,
            -123,
            -1
        };
        for (int validID : validIDs){
            assertTrue(v.idIsValid(validID));
        }
        for (int invalidID : invalidIDs){
            assertFalse(v.idIsValid(invalidID));
        }
    }

    @Test
    public void test_ipAddressIsValid(){
        String[] validIPs = {
            "213.107.128.255",
            //"0.0.0.0",
            //"1.1.1."
        };
        String[] invalidIPs = {
            "123107128255",
            "1.5",
            "",
            null
        };
        for (String validIP : validIPs){
            assertTrue(v.ipAddressIsValid(validIP));
        }
        for (String invalidIP : invalidIPs){
            assertFalse(v.ipAddressIsValid(invalidIP));
        }
    }

    @Test
    public void test_eAddressIsValid(){
        String[] validEmails = {
            "qweiuhu@ada.com",
            "qweq_wrq@fas.cn",
            "xxx@live.warwick.ac.uk"
        };
        String[] invalidEmails = {
            "123107128255",
            "1.5",
            "@.",
            "",
            null
        };
        for (String validEmail : validEmails){
            assertTrue(v.eAddressIsValid(validEmail));
        }
        for (String invalidEmail : invalidEmails){
            assertFalse(v.eAddressIsValid(invalidEmail));
        }
    }

    @Test
    public void test_nameIsValid(){
        String[] validNames = {
            "Edwy"
        };
        String[] invalidNames = {
            "EDwY",
            "3dwy",
            "",
            null
        };
        for (String validName : validNames){
            assertTrue(v.nameIsValid(validName));
        }
        for (String invalidName : invalidNames){
            assertFalse(v.nameIsValid(invalidName));
        }
    }

    @Test // TODO (after method made)
    public void test_templateDataIsValid(){}

    @Test
    public void test_eventTitleIsValid(){
        String[] validTitles = {
            "Lecture 1",
            "lecture_1",
            "lecture1"
        };
        String[] invalidTitles = {
            "",
            null
        };
        for (String validTitle : validTitles){
            assertTrue(v.eventTitleIsValid(validTitle));
        }
        for (String invalidTitle : invalidTitles){
            assertFalse(v.eventTitleIsValid(invalidTitle));
        }
    }

    @Test
    public void test_eventDescriptionIsValid(){
        String[] validDescriptions = {
            "Lecture 1",
            "lecfsertwtwet",
            "lecture1",
            "        "
        };
        String[] invalidDescriptions = {
            "",
            null
        };
        for (String validDescription : validDescriptions){
            assertTrue(v.eventDescriptionIsValid(validDescription));
        }
        for (String invalidDescription : invalidDescriptions){
            assertFalse(v.eventDescriptionIsValid(invalidDescription));
        }
    }

    @Test
    public void test_eventTypeIsValid(){
        String[] validTypes = {
            "lecture",
            "seminar",
            "conference",
            "workshop",
            "other"
        };
        String[] invalidTypes = {
            "",
            "qtaesf",
            null
        };
        for (String validType : validTypes){
            assertTrue(v.eventTypeIsValid(validType));
        }
        for (String invalidType : invalidTypes){
            assertFalse(v.eventTypeIsValid(invalidType));
        }
    }

    @Test // TODO
    public void test_sentimentIsValid(){}

    @Test // TODO
    public void test_isArchivedEventValid(){}

    @Test
    public void test_isEventValid(){
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Event testEvent = new Event(0001, 0001, 0001, "Lecture 1", "event desc", "seminar", now, now, "CCCC");
        Event newEvent = new Event(0001, 0001, 0001, "Lecture 1", "event desc", "movie", now, now, "CCCC");

        assertTrue(v.isEventValid(testEvent));
        assertFalse(v.isEventValid(newEvent));
    }

    @Test // TODO
    public void test_isFeedbackValid(){}

    @Test
    public void test_isHostValid(){
        Host testHost = new Host(0001, "fish-bird-brother-map", "213.107.128.255", "moustafa.edwy@gmail.com", "Moustafa", "Eledwy", false);

        assertTrue(v.isHostValid(testHost));
    }

    @Test
    public void test_isParticipantValid(){
        Participant testParticipant = new Participant(0001, "213.107.128.255", "Moustafa", "Eledwy", false);

        assertTrue(v.isParticipantValid(testParticipant));
    }

    @Test
    public void test_isTemplateValid(){
        Template testTemplate = new Template(0001, 0001, "a23fsg", "TestTemplateData");

        assertTrue(v.isTemplateValid(testTemplate));
    }

    @Test
    public void test_sanitizeEventCode(){
        String upperCase = "ABCD";
        String lowerCase = "abcd";
        String sanitized = v.sanitizeEventCode(upperCase);
        assertTrue(sanitized != null && lowerCase.equals(sanitized));
    }

    @Test
    public void test_sanitizeTemplateCode(){
        String upperCase = "ABCDEF";
        String lowerCase = "abcdef";
        String sanitized = v.sanitizeTemplateCode(upperCase);
        assertTrue(sanitized != null && lowerCase.equals(sanitized));
    }

    @Test
    public void test_sanitizeHostCode(){
        String upperCase = "FISH-bird-brother-map";
        String lowerCase = "fish-bird-brother-map";
        String sanitized = v.sanitizeHostCode(upperCase);
        assertTrue(sanitized != null && lowerCase.equals(sanitized));
    }
}
