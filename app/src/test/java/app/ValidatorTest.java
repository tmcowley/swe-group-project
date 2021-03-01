package app;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import app.Validator;

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

}
