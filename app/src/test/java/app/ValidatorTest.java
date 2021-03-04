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
    public void testHostCodeValidity(){
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
    public void testEventCodeValidity(){
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
    public void testIPValidity(){
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

    @Test
    public void testNameValidity(){
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

    @Test
    public void testEventTitleValidity(){
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

    @Test
    public void testEventValidity(){
        Event testEvent = new Event(0001, 0001, 0001, "Lecture 1", "event desc", "seminar", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), "CCCC");
        Event newEvent = new Event(0001, 0001, 0001, "Lecture 1", "event desc", "movie", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), "CCCC");

        assertTrue(v.isEventValid(testEvent));
        assertFalse(v.isEventValid(newEvent));
    }

    @Test
    public void testHostValidity(){
        Host testHost = new Host(0001, "fish-bird-brother-map", "213.107.128.255", "moustafa.edwy@gmail.com", "Moustafa", "Eledwy", false);
        
        assertTrue(v.isHostValid(testHost));
    }

}
