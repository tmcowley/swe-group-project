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
        String[] inValidCodes = {
            "xxxx-bird-brother-map",
            "fish-bird-brother-ma p",
            "fish-bird-brother map",
            "",
            null
        };
        for (String validCode : validCodes){
            assertTrue(v.hostCodeIsValid(validCode));
        }
        for (String inValidCode : inValidCodes){
            assertFalse(v.hostCodeIsValid(inValidCode));
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
        String[] inValidCodes = {
            "AAAAAA",
            "abcd-",
            "CC-C",
            "",
            null
        };
        for (String validCode : validCodes){
            assertTrue(v.eventCodeIsValid(validCode));
        }
        for (String inValidCode : inValidCodes){
            assertFalse(v.eventCodeIsValid(inValidCode));
        }
    }

    @Test
    public void testIPValidity(){
        String[] validCodes = {
            "213.107.128.255",
            "0.0.0.0"
        };
        String[] inValidCodes = {
            "123107128255",
            "1.1.1.",
            "",
            null
        };
        for (String validCode : validCodes){
            assertTrue(v.ipAddressIsValid(validCode));
        }
        for (String inValidCode : inValidCodes){
            assertFalse(v.ipAddressIsValid(inValidCode));
        }
    }

    @Test
    public void testNameValidity(){
        assertTrue(v.nameIsValid("Edwy"));
        assertFalse(v.nameIsValid("EDwY"));
        assertFalse(v.nameIsValid("3dwy"));
    }

    @Test
    public void testEventTitleValidity(){
        assertTrue(v.eventTitleIsValid("Lecture 1"));
        assertTrue(v.eventTitleIsValid("lecture_1"));
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
