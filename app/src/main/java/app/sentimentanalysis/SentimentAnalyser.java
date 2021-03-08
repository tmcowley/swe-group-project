//TODO - Create a consitency test for this
//TODO - create a does it return something in the correct format test

package app.sentimentanalysis;

import java.util.*;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.Locale;
import app.objects.*;
import java.util.ArrayList;

public class SentimentAnalyser {

/**
     * Take feedback and return sentiment
     * @param feedback feedback to be analysed for sentiment
     * @return sentiment containing compound score (add key results?)
     */
public static void main(Feedback feedback) throws IOException{
    
    
    Integer[] types = feedback.getTypes(); //Holds type of feedback for each result
    String[] results = feedback.getResults(); //Holds type of feedback for each result
    Boolean[] keys = feedback.getKeys(); //Holds type of feedback for each result
    int amount = feedback.getResults().length; //Holds amount of results to be (possibly) interpreted for sentiment
    float[] compounds = new float[amount]; //Holds compound score for each feedback result
    
    //Get compound score for each result, applying the interpretation method based on result type
    for (int i = 0; i < amount; i++ ) {
        if (types[i] == 0) {
            compounds[i] = getCompoundFromText(results[i]);
        }
        //TODO - get scores from fixed queries and put in compounds array
        
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
private static float getCompoundFromText(String plaintext) throws IOException{

    //TODO - fix padding

    float compound = 0; //Holds compound score derived from plaintext
    int count = 0; //Used to mean compound scores
    ArrayList<String> sentences = new ArrayList<String>(); //Holds plaintext broken into sentences

    //Iterates through plaintext and selects each sentence within plaintext, adds each sentence to array list 
    BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.UK); //Might have to change to US - we'll see
    iterator.setText(plaintext);
    int start = iterator.first();
    for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
        sentences.add(plaintext.substring(start,end));
    }

    //Iterate through each sentence and get compound score
     for (String sentence : sentences) {
        compound += SentimentAnalyzer.getScoresFor(sentence).getCompoundPolarity();
        count++;
     }

     return compound/count;

}

}