
## Unit Testing
Development used of unit testing at the point of development, as planned in our `Design and Planning Document`. This means that unit testing was carried out between sprint cycles one and four.
Unit testing against the Java back-end, the PostgreSQL database, and the sentiment analyser have been developed in the form of JUnit tests, stored in `/app/src/test/`. These testing classes contain unit tests against each publicly accessible method from the main source code.

Below is an example of a JUnit test run against the `Validator.java` method `eventCodeIsValid`, from the class `ValidatorTest.java`. 

As defined in Design and Planning, an event code is a non-null, 4 digit, alphanumeric code uniquely identifying an event.
```
@Test
    public void test_eventCodeIsValid() {
        String[] validCodes = { "CCCC", "4RF5", "ac4r", "abcd", "aBc7" };
        String[] invalidCodes = { "AAAAAA", "abcd-", "CC-C", " ", "", null };
        for (String validCode : validCodes) {
            assertTrue(v.eventCodeIsValid(validCode));
        }
        for (String invalidCode : invalidCodes) {
            assertFalse(v.eventCodeIsValid(invalidCode));
        }
    }
```
This testing format is scalable: additional valid and invalid codes can easily be tested by expanding their corresponding string arrays. Each array is then iterated, with each value being tested as either valid, or invalid. The assertions will fail in either the case of a false-negative: valid code being labelled as invalid, or in the case of a false-positive: an invalid code being labelled as valid. The standard invalid codes of the empty string, the blank string, and the null object have been tested in the above example, and are included in other tests, where appropriate.

<br>

## Component Testing

...

<br>

## Requirements Validation

...