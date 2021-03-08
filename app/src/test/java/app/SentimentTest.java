package app;

import app.objects.*;
import app.sentimentanalysis.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import java.sql.Timestamp;
import java.time.Instant;
import org.junit.Test;

// Unit tests against SentimentAnalyser.java
public class SentimentTest {

    //Create feedback object as input for testing
    int i = 0;
    boolean f = false;
    Timestamp timestamp = new Timestamp(System.currentTimeMillis();
    //A large variety of results drawn from our research and testing, including; potential user testing, existing product user feedback. These results are in the context of the product and other feedback contexts - to test the system thoroughly.
    String[] results = {
        "The rooms were dirty, very uncomfortable, and the staff was terrible. They went over and beyond to ignore us. I do not recommend this hotel for anyone visiting downtown. They were extremely unaccommodating and did not allow us to check in early. We got to hotel super early and I didn’t wanna wait. So this was a big minus. The service was very poor as well. Would definitely not send a friend there. The staff at this property are all shit! They all go above and beyond to make your stay uncomfortable. Please give your staff beatings! The worst hotel I’ve ever been privileged enough to stay at. Ugly building, and it only gets more breathtaking when you walk in. Low quality rooms (there was not even a tv by the shower), and low quality service. Also, they are one of few hotels that do not allow people under 21 to book a reservation.",
        "The rooms were clean, very comfortable, and the staff was amazing. They went over and beyond to help make our stay enjoyable. I highly recommend this hotel for anyone visiting downtown. They were extremely accommodating and allowed us to check in early at like 10am. We got to hotel super early and I didn’t wanna wait. So this was a big plus. The service was exceptional as well. Would definitely send a friend there. The staff at this property are all great! They all go above and beyond to make your stay comfortable. Please give your staff awards! The best hotel I’ve ever been privileged enough to stay at. Gorgeous building, and it only gets more breathtaking when you walk in. High quality rooms (there was even a tv by the shower), and high quality service. Also, they are one of few hotels that allow people under 21 to book a reservation.",
        "The rooms were clean, very comfortable, and the staff was amazing. They went over and beyond to help make our stay enjoyable. I highly recommend this hotel for anyone visiting downtown. They were extremely accommodating and allowed us to check in early at like 10am. We got to hotel super early and I didn’t wanna wait. So this was a big plus. The service was exceptional as well. Would definitely send a friend there. The staff at this property are all great! They all go above and beyond to make your stay comfortable. Please give your staff awards! The best hotel I’ve ever been privileged enough to stay at. Gorgeous building, and it only gets more breathtaking when you walk in. High quality rooms (there was even a tv by the shower), and high quality service. Also, they are one of few hotels that allow people under 21 to book a reservation.The rooms were dirty, very uncomfortable, and the staff was terrible. They went over and beyond to ignore us. I do not recommend this hotel for anyone visiting downtown. They were extremely unaccommodating and did not allow us to check in early. We got to hotel super early and I didn’t wanna wait. So this was a big minus. The service was very poor as well. Would definitely not send a friend there. The staff at this property are all shit! They all go above and beyond to make your stay uncomfortable. Please give your staff beatings! The worst hotel I’ve ever been privileged enough to stay at. Ugly building, and it only gets more breathtaking when you walk in. Low quality rooms (there was not even a tv by the shower), and low quality service. Also, they are one of few hotels that do not allow people under 21 to book a reservation.",
        "The rooms were clean, very comfortable, and the staff was amazing. The rooms were dirty, very uncomfortable, and the staff was terrible. They went over and beyond to help make our stay enjoyable. They went over and beyond to ignore us. I highly recommend this hotel for anyone visiting downtown. They were extremely accommodating and allowed us to check in early at like 10am. We got to hotel super early and I didn’t wanna wait. So this was a big plus. I do not recommend this hotel for anyone visiting downtown. They were extremely unaccommodating and did not allow us to check in early. We got to hotel super early and I didn’t wanna wait. So this was a big minus. The service was exceptional as well. Would definitely send a friend there. The staff at this property are all great! They all go above and beyond to make your stay comfortable. Please give your staff awards! he service was very poor as well. Would definitely not send a friend there. The staff at this property are all shit! They all go above and beyond to make your stay uncomfortable. Please give your staff beatings! The best hotel I’ve ever been privileged enough to stay at. Gorgeous building, and it only gets more breathtaking when you walk in. High quality rooms (there was even a tv by the shower), and high quality service. Also, they are one of few hotels that allow people under 21 to book a reservation. The worst hotel I’ve ever been privileged enough to stay at. Ugly building, and it only gets more breathtaking when you walk in. Low quality rooms (there was not even a tv by the shower), and low quality service. Also, they are one of few hotels that do not allow people under 21 to book a reservation."
    };
    Float[] weights = {0.1f, 0.1f, 0.1f, 0.1f};
    Integer[] types = {0, 0, 0, 0};
    Boolean[] keys = {false, false, false, false};
    Feedback test = new Feedback( i, i, i, results, weights, types, keys, f, timestamp);

    //TODO - expand feedback object with many more diverse plaintext and nopn-plaintext
    @Test
    public void test_main() {
        //Perform tests
        test_main_consistency();
        test_main_correctness();
        test_main_return();
    }

    private void test_main_consistency() {
        //TODO RUN TWICE AND COMPARE
    }

    private void test_main_correctness() {
        //TODO check key results are listed, come up with some bound of acceptiability bound for compound
        
    }

    private void test_main_return() {
        //Check feedback has been set correctly
    }




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

    @Test
    public void test_templateCodeIsValid() {
        String[] validCodes = { "asdfgh", "a23fsg", "cgd352", "23fsd3", "a45sdf" };
        String[] invalidCodes = { "AAAAAAA", "qwer1", "41as-f", " ", "", null };
        for (String validCode : validCodes) {
            assertTrue(v.templateCodeIsValid(validCode));
        }
        for (String invalidCode : invalidCodes) {
            assertFalse(v.templateCodeIsValid(invalidCode));
        }
    }

    @Test
    public void test_alphanumericIsValid() {
        String[] validCodes = { "asdfg", "a23fs", "cgd35", "23Fsd", "a45sd" };
        String[] invalidCodes = { "AAAAAA", "qwe1", "41a-fa", "     ", " ", "", null };
        for (String validCode : validCodes) {
            assertTrue(v.alphanumericIsValid(validCode, 5));
        }
        for (String invalidCode : invalidCodes) {
            assertFalse(v.alphanumericIsValid(invalidCode, 5));
        }
    }

    @Test
    public void test_hostCodeIsValid() {
        String[] validCodes = { "fish-bird-brother-map", "fish-bird-brother-MAP", "plant-space-path-country" };
        String[] invalidCodes = { "xxxx-bird-brother-map", "fish-bird-brother-ma p", "fish-bird-brother map", " ", "",
                null };
        for (String validCode : validCodes) {
            assertTrue(v.hostCodeIsValid(validCode));
        }
        for (String invalidCode : invalidCodes) {
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
    public void test_ipAddressIsValid() {
        String[] validIPs = { "213.107.128.255",
                // "0.0.0.0",
                // "1.1.1."
        };
        String[] invalidIPs = { "123107128255", "1.5", " ", "", null };
        for (String validIP : validIPs) {
            assertTrue(v.ipAddressIsValid(validIP));
        }
        for (String invalidIP : invalidIPs) {
            assertFalse(v.ipAddressIsValid(invalidIP));
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
        String[] validNames = { "Edwy" };
        String[] invalidNames = { "EDwY", "3dwy", " ", "", null };
        for (String validName : validNames) {
            assertTrue(v.nameIsValid(validName));
        }
        for (String invalidName : invalidNames) {
            assertFalse(v.nameIsValid(invalidName));
        }
    }

    @Test // TODO (after method made)
    public void test_templateDataIsValid() {
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
        String[] validDescriptions = { "Lecture 1", "lecfsertwtwet", "lecture1", "        " };
        String[] invalidDescriptions = { "", null };
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
        String[] invalidTypes = { " ", "", "qtaesf", null };
        for (String validType : validTypes) {
            assertTrue(v.eventTypeIsValid(validType));
        }
        for (String invalidType : invalidTypes) {
            assertFalse(v.eventTypeIsValid(invalidType));
        }
    }

    @Test // TODO
    public void test_sentimentIsValid() {
    }

    @Test // TODO
    public void test_isArchivedEventValid() {
    }

    @Test
    public void test_isEventValid() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Event testEvent = new Event(0001, 0001, 0001, "Lecture 1", "event desc", "seminar", now, now, "CCCC");
        Event newEvent = new Event(0001, 0001, 0001, "Lecture 1", "event desc", "movie", now, now, "CCCC");

        assertTrue(v.isEventValid(testEvent));
        assertFalse(v.isEventValid(newEvent));
    }

    @Test // TODO
    public void test_isFeedbackValid() {
    }

    @Test
    public void test_isHostValid() {
        Host testHost = new Host(0001, "fish-bird-brother-map", "213.107.128.255", "moustafa.edwy@gmail.com",
                "Moustafa", "Eledwy", false);

        assertTrue(v.isHostValid(testHost));
    }

    @Test
    public void test_isParticipantValid() {
        Participant testParticipant = new Participant(0001, "213.107.128.255", "Moustafa", "Eledwy", false);

        assertTrue(v.isParticipantValid(testParticipant));
    }

    @Test
    public void test_isTemplateValid() {
        Template testTemplate = new Template(0001, 0001, "a23fsg", "TestTemplateData");

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
