package app;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import app.DbConnection;

// Unit tests against App.
public class DBConnTest 
{

    DbConnection db;
    Validator validator;

    public DBConnTest(){
        db = new DbConnection();
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
            assertTrue(validator.hostCodeIsValid(host_code));
        }

        // Test ten unique template codes
        for (int i = 0; i < 10; i++){
            String template_code = db.generateUniqueTemplateCode();
            assertTrue(validator.templateCodeIsValid(template_code));
        }
    }
}
