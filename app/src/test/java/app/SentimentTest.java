package app;

import app.objects.*;
import app.sentimentanalysis.*;

// JUnit packages for testing
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.util.ArrayList;
import java.io.IOException;
import java.sql.Timestamp;

// Unit tests against SentimentAnalyser.java
public class SentimentTest {

    // global Feedback instances
    Feedback test;
    Feedback test2;

    // constructor generates global feedback testing objects
    public SentimentTest() {

        // Create feedback object as input for testing
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        // A large variety of results drawn from our research and testing, including;
        // potential user testing, existing product user feedback. These results are in
        // the context of the product and other feedback contexts - to test the system
        // thoroughly.
        String[] results = {
                "The rooms were dirty, very uncomfortable, and the staff was terrible. They went over and beyond to ignore us. I do not recommend this hotel for anyone visiting downtown. They were extremely unaccommodating and did not allow us to check in early. We got to hotel super early and I didn’t wanna wait. So this was a big minus. The service was very poor as well. Would definitely not send a friend there. The staff at this property are all shit! They all go above and beyond to make your stay uncomfortable. Please give your staff beatings! The worst hotel I’ve ever been privileged enough to stay at. Ugly building, and it only gets more breathtaking when you walk in. Low quality rooms (there was not even a tv by the shower), and low quality service. Also, they are one of few hotels that do not allow people under 21 to book a reservation.",
                "The rooms were clean, very comfortable, and the staff was amazing. They went over and beyond to help make our stay enjoyable. I highly recommend this hotel for anyone visiting downtown. They were extremely accommodating and allowed us to check in early at like 10am. We got to hotel super early and I didn’t wanna wait. So this was a big plus. The service was exceptional as well. Would definitely send a friend there. The staff at this property are all great! They all go above and beyond to make your stay comfortable. Please give your staff awards! The best hotel I’ve ever been privileged enough to stay at. Gorgeous building, and it only gets more breathtaking when you walk in. High quality rooms (there was even a tv by the shower), and high quality service. Also, they are one of few hotels that allow people under 21 to book a reservation.",
                "The rooms were clean, very comfortable, and the staff was amazing. They went over and beyond to help make our stay enjoyable. I highly recommend this hotel for anyone visiting downtown. They were extremely accommodating and allowed us to check in early at like 10am. We got to hotel super early and I didn’t wanna wait. So this was a big plus. The service was exceptional as well. Would definitely send a friend there. The staff at this property are all great! They all go above and beyond to make your stay comfortable. Please give your staff awards! The best hotel I’ve ever been privileged enough to stay at. Gorgeous building, and it only gets more breathtaking when you walk in. High quality rooms (there was even a tv by the shower), and high quality service. Also, they are one of few hotels that allow people under 21 to book a reservation.The rooms were dirty, very uncomfortable, and the staff was terrible. They went over and beyond to ignore us. I do not recommend this hotel for anyone visiting downtown. They were extremely unaccommodating and did not allow us to check in early. We got to hotel super early and I didn’t wanna wait. So this was a big minus. The service was very poor as well. Would definitely not send a friend there. The staff at this property are all shit! They all go above and beyond to make your stay uncomfortable. Please give your staff beatings! The worst hotel I’ve ever been privileged enough to stay at. Ugly building, and it only gets more breathtaking when you walk in. Low quality rooms (there was not even a tv by the shower), and low quality service. Also, they are one of few hotels that do not allow people under 21 to book a reservation.",
                "The rooms were clean, very comfortable, and the staff was amazing. The rooms were dirty, very uncomfortable, and the staff was terrible. They went over and beyond to help make our stay enjoyable. They went over and beyond to ignore us. I highly recommend this hotel for anyone visiting downtown. They were extremely accommodating and allowed us to check in early at like 10am. We got to hotel super early and I didn’t wanna wait. So this was a big plus. I do not recommend this hotel for anyone visiting downtown. They were extremely unaccommodating and did not allow us to check in early. We got to hotel super early and I didn’t wanna wait. So this was a big minus. The service was exceptional as well. Would definitely send a friend there. The staff at this property are all great! They all go above and beyond to make your stay comfortable. Please give your staff awards! he service was very poor as well. Would definitely not send a friend there. The staff at this property are all shit! They all go above and beyond to make your stay uncomfortable. Please give your staff beatings! The best hotel I’ve ever been privileged enough to stay at. Gorgeous building, and it only gets more breathtaking when you walk in. High quality rooms (there was even a tv by the shower), and high quality service. Also, they are one of few hotels that allow people under 21 to book a reservation. The worst hotel I’ve ever been privileged enough to stay at. Ugly building, and it only gets more breathtaking when you walk in. Low quality rooms (there was not even a tv by the shower), and low quality service. Also, they are one of few hotels that do not allow people under 21 to book a reservation.",
                "Terrible, just terrible.",
                "I'm having a great time. I really appreciate how you speak clearly and slowly. One of the best lectures I've had the pleasure of listening to!",
                "3", "1244" };
        Float[] weights = { 4f, 4f, 4f, 4f, 5f, 6f, 2f, 3f };
        byte[] types = { 0, 0, 0, 0, 0, 0, 1, 2 };
        Boolean[] keys = { false, false, false, false, true, false, true, true };
        byte[][] sub_weights = { { 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0 }, { 1, 7, 4, 5, 6 }, { 1, 7, 4, 7, 1 } };
        test = new Feedback(0, 0, results, weights, types, keys, sub_weights, false, timestamp);
        test2 = new Feedback(0, 0, results, weights, types, keys, sub_weights, false, timestamp);

        try {
            // run sentiment analysis on both feedback objects
            SentimentAnalyser.main(test);
            SentimentAnalyser.main(test2);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    public void test_main_consistency() throws IOException {
        // assertTrue(test.equals(test2));
        assertTrue(test.getWeights().equals(test2.getWeights()));
        assertTrue(test.getCompound().equals(test2.getCompound()));
        assertTrue(test.getKey_Results().equals(test2.getKey_Results()));
    }

    @Test
    public void test_main_correctness() throws IOException {
        ArrayList<String> krs = new ArrayList<String>();
        krs.add("Terrible, just terrible.");
        krs.add("3");
        krs.add("1244");
        assertTrue(test.getKey_Results().equals(krs));
        assertTrue(0.15 < test.getCompound() && test.getCompound() < 0.25); // Expected range for compound score
    }

    @Test
    public void test_main_return() throws IOException {
        // Check feedback has been set correctly
    }
}
