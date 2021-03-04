package app;
import app.objects.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import app.Validator;
//import jdk.jfr.Timestamp;

// Unit tests against Validator.java
public class ValidatorTest {

    Validator v;

    public ValidatorTest(){
        v = new Validator();
    }

    @Test
    public void test_templateCodeIsValid(){
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

    @Test // TODO
    public void test_templateCodeIsValid(){}

    @Test // TODO
    public void test_alphanumericIsValid(){}

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

    @Test // TODO
    public void test_idIsValid(){}

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
            assertTrue(v.ipAddressIsValid(validCode));
        }
        for (String invalidIP : invalidIPs){
            assertFalse(v.ipAddressIsValid(invalidCode));
        }
    }

    @Test // TODO
    public void test_eAddressIsValid(){}

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
            assertTrue(v.nameIsValid(validCode));
        }
        for (String invalidName : invalidNames){
            assertFalse(v.nameIsValid(invalidCode));
        }
    }

    @Test // TODO (after method made)
    public void test_templateDataIsValid(){}

    @Test
    public void test_eventTitleIsValid(){
        String[] validTitles = {
            "Lecture 1",
            "lecture_1",
            "lecture-1"
        };
        String[] invalidTitles = {
            "",
            null
        };
        for (String validTitle : validTitles){
            assertTrue(v.eventTitleIsValid(validCode));
        }
        for (String invalidTitle : invalidTitles){
            assertFalse(v.eventTitleIsValid(invalidCode));
        }
    }

    @Test // TODO
    public void test_eventDescriptionIsValid(){}

    @Test // TODO
    public void test_eventTypeIsValid(){}

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

    @Test // TODO
    public void test_isParticipantValid(){}

    @Test // TODO
    public void test_isTemplateValid(){}

    @Test // TODO
    public void test_sanitizeEventCode(){}

    @Test // TODO
    public void test_sanitizeTemplateCode(){}

    @Test // TODO
    public void test_sanitizeHostCode(){}
}
