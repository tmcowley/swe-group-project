//TODO - Create a consitency test for this
//TODO - create a does it return something in the correct format test
//TODO - add set queries to the feedback testing
//TODO change for loops to iterate through arrays
//TODO check if one sd is enough/good

package app.sentimentanalysis;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.Locale;

import app.Validator;
import app.objects.*;
import java.util.ArrayList;
import com.vader.sentiment.analyzer.SentimentAnalyzer;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

// for event code sanitization
import org.apache.commons.lang3.ArrayUtils; 

public class SentimentAnalyser {

    Validator v = new Validator();

    /**
     * Take a feedback object and updated it to include sentiment
     * @param feedback feedback to be analysed for sentiment
     */
    public static void main(Feedback feedback) throws IOException{

        if (!v.feedbackIsValid(feedback)){
            System.out.println("Error: feedback instance is invalid");
            return;
        }
    
        byte[] types = feedback.getTypes(); //Holds type of feedback for each result
        String[] results = feedback.getResults(); //Holds type of feedback for each result
        Boolean[] keys = feedback.getKeys(); //Holds type of feedback for each result
        Float[] weights = feedback.getWeights(); //Holds weights (unprocessed then processed) for weighted mean
        int amount = feedback.getResults().length; //Holds amount of results to be (possibly) interpreted for sentiment
        int sum = 0; //Sum of all weights, used in weights processing
        float[] compounds = new float[amount]; //Holds compound score for each feedback result

        // ensure non-empty arrays
        if (!ArrayUtils.isNotEmpty(types)){
            System.out.println("Error: array:types is null or empty");
            return;
        }
        if (!ArrayUtils.isNotEmpty(results)){
            System.out.println("Error: array:results is null or empty");
            return;
        }
        if (!ArrayUtils.isNotEmpty(keys))
            System.out.println("Error: array:keys is null or empty");
            return;
        }
        if (!ArrayUtils.isNotEmpty(weights)){
            System.out.println("Error: array:weights is null or empty");
            return;
        }
        if (!ArrayUtils.isNotEmpty(compounds)){
            System.out.println("Error: array:compounds is null or empty");
            return;
        }

        
        //Process host inputted weights into floats that sum to 1.0
        for (int i = 0; i < amount; i++ ) {
            sum += weights[i];
        }
        for (int i = 0; i < amount; i++ ) {
            if (weights[i] != 0) {
            feedback.setWeightItem(weights[i]/sum, i);
            }
        }
        
        //Get compound score for each result, applying the interpretation method based on result type
        for (int i = 0; i < amount; i++ ) {
            if (types[i] == 0) {
                compounds[i] = getCompoundFromText(results[i]);
            }
            if (types[i] == 1) {
                compounds[i] = getCompoundFromSingle(results[i], i, feedback);
            }
            if (types[i] == 2) {
                compounds[i] = getCompoundFromMultiple(results[i], i, feedback);
            }
            //Checks if result is a key result, if it is adds to the array list of key results
            if (keys[i] == true) {
                feedback.addKey_Results(results[i]);
            }
        }

        feedback.setCompound(weightedMean(feedback.getWeights(), compounds)); //Set final compound score as weighted mean of result specific compound scores

    }

    /**
         * Get weighted mean of compound scores
         * @param weights real array of weights for each score
         * @param scores real array of scores
         * @return weighted mean of compound scores
         */
    private static float weightedMean(Float[] weights, float[] scores) {
        float mean = 0; 
        for (int i = 0; i < weights.length; i++) {
                mean += weights[i]*scores[i];
        }
        return mean;
    }

    /**
    * Break plaintext into sentences
    * Analyse each sentence for compound score
    * Get mean of compound scores
    * @param plaintext string to be analysed for sentiment
    * @return real compound score derived from plaintext
    */
    public static float getCompoundFromText(String plaintext) throws IOException{

        //TODO - fix padding (maybe make 2 sds above or below mean)
        //TODO make private, delete the direct ests from sentiment test

        float compound = 0; //Holds compound score derived from plaintext
        int count = 0; //used to mean compound scores
        ArrayList<String> sentences = new ArrayList<String>(); //Holds plaintext broken into sentences

        //Iterates through plaintext and selects each sentence within plaintext, adds each sentence to array list 
        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.UK); //Might have to change to US - we'll see
        iterator.setText(plaintext);
        int start = iterator.first();
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            sentences.add(plaintext.substring(start,end));
        }

        //Store compound score of each sentence in an array
        double[] values = new double[count];
        for (int i = 0; i < sentences.size(); i++) {
            values[i] = SentimentAnalyzer.getScoresFor(sentences.get(i)).getCompoundPolarity();
        }
        
        //Remove compound scores that [fall within one standard deviation from 0] - may change
        StandardDeviation sd = new StandardDeviation();
        double standev = sd.evaluate(values);
        for (double i : values) {
            if ( (i > 1*standev) != (i < -1*standev) ) {
                compound += i;
                count++;
            }
        }
        
        if (count == 0) {
            return compound;
        } else {
            return compound/count;
        }
    }

    /**
     * Gets row of matrix which holds weights of each possible result, selects correct result, normalises to between 1 and -1
     * @param data Index of the weight of the result chosen
     * @param index Row of matrix to be selected
     * @param feedback Feedback object, needed to use a getter
     * @return Compound score for a specific query
     */
    private static float getCompoundFromSingle(String data, int index, Feedback feedback) {
        return ((feedback.getSub_WeightsRow(index)[Integer.parseInt(data)])-4f)/3f; //Gets row of matrix which holds weights of each possible result, selects correct result, normalises to between 1 and -1
    }

    /**
     * Finds mean weight of all chosen results, normalises to between 1 and -1
     * @param data Index of the weight of the result chosen
     * @param index Row of matrix to be selected
     * @param feedback Feedback object, needed to use a getter
     * @return Compound score for a specific query
     */
    private static float getCompoundFromMultiple(String data, int index, Feedback feedback) {
        byte[] results = feedback.getSub_WeightsRow(index); //Holds weights of results
        float compound = 0; //Holds compound score
        int count = 0; //Used to mean compound scores
        for (int i = 0; i < 5; i++ ) {
            if ( data.contains(Integer.toString(i)) == true ) {
                compound += results[i];
                count++;
            }
        }
        return ((compound/count)-4f)/3f;
    }

}