package app;
import app.objects.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import java.sql.Timestamp;
import app.Validator;
//import jdk.jfr.Timestamp;

// Unit tests against Validator.java
public class ValidatorTest {

    Validator v;

    public ValidatorTest(){
        v = new Validator();

    }

    // to split
    @Test
    public void tests(){

        assertTrue(v.eventCodeIsValid("CCCC"));
        assertFalse(v.eventCodeIsValid("AAAAAA"));
        assertTrue(v.eventCodeIsValid("33SA"));
        assertFalse(v.eventCodeIsValid("[[[[]]]]"));

        assertTrue(v.hostCodeIsValid("fish-bird-brother-map"));
        assertTrue(v.hostCodeIsValid("fish-bird-brother-MAP"));
        assertFalse(v.hostCodeIsValid("xxxx-bird-brother-map"));
        assertFalse(v.hostCodeIsValid("fish-bird-brother-ma p"));
        assertFalse(v.hostCodeIsValid("xxxx-bird-brother map"));
        assertFalse(v.hostCodeIsValid("map bird brother map"));


    }

    @Test
    public void testEventCodeValidity(){
        assertTrue(v.eventCodeIsValid("CCCC"));
        assertFalse(v.eventCodeIsValid("AAAAAA"));
        assertFalse(v.eventCodeIsValid("CC-C"));
        assertFalse(v.eventCodeIsValid(null));
    }

    @Test
    public void testIPValidity(){
        assertTrue(v.ipAddressIsValid("213.107.128.255"));
        assertFalse(v.ipAddressIsValid("0.0.0.0"));
        assertFalse(v.ipAddressIsValid(null));
        assertFalse(v.ipAddressIsValid("123107128255"));
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
