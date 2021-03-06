#### Sprint Cycle 5: Term 2, Week 8 (Mar 1st to Mar 7th)
# Testing and Validation

## Unit Testing
Component development made use of unit testing at the point of development, as planned in our `Design and Planning Document`.
Unit testing against the Java back-end, the PostgreSQL database, and the sentiment analyser have been developed in the form of JUnit tests, stored in `/app/src/test/`.

Below is an example of a JUnit test run against the `Validator.java` method `eventCodeIsValid`. As defined in Design and Planning, an event code is a non-null, 4 digit, alphanumeric code uniquely identifying an event. 
```
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
```
This testing format is scalable: for valid ... TODO

