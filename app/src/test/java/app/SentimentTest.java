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
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
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
        
        SentimentAnalyser sa = new SentimentAnalyser();
        try {
            float response = sa.getCompoundFromFullText("I hate everything");
            System.out.println(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

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

    // @Test
    // public void test_eventCodeIsValid() {
    //     String[] validCodes = { "CCCC", "4RF5", "ac4r", "abcd", "aBc7" };
    //     String[] invalidCodes = { "AAAAAA", "abcd-", "CC-C", " ", "", null };
    //     for (String validCode : validCodes) {
    //         assertTrue(v.eventCodeIsValid(validCode));
    //     }
    //     for (String invalidCode : invalidCodes) {
    //         assertFalse(v.eventCodeIsValid(invalidCode));
    //     }
    // }

}
