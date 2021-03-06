package app;

import java.util.*;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.Locale;
import com.vader.sentiment.analyzer.SentimentAnalyzer;
import java.util.ArrayList;

public class Sentiment {

/**
     * Take feedback and return sentiment
     * @param feedback feedback to be analysed for sentiment
     * @return sentiment containing compound score (add key results?)
     */
public void main() {

    //Currently this takes feedback in the form of an array structure i basically made up on the spot
    //When we decide how feedback is actually handled I can easily (hopefully) change this to use the new format
    //All the TODOs are simple stuff not worth programming until we have feedback and sentiment objects sorted
    
    //TODO - takes each section of text from feedback and runs through getCompound
    //TODO - stores all these compound scores and scores from fixed result queries in an array list
    //TODO - convert arraylist to array and create (or call?) array of weights from feedback
    //TODO - Get weighted mean of compound using weightedMean and the 2 arrays
    //TODO - package the weighted mean with key results into sentiment object



}

/**
     * Get weighted mean of compound scores
     * @param weights real array of weights for each score
     * @param scores real array of scores
     * @return weighted mean of compound scores
     */
private float weightedMean(float weights[], float scores[]) {
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
private float getCompound(String plaintext) throws IOException{

    float compound = 0; //Holds compound score derived from plaintext
    int count = 0; //Used to mean compound scores
    ArrayList<String> sentences = new ArrayList<String>(); //Holds plaintext broken into sentences

    //Iterates through plaintext and selects each sentence within plaintext, adds each sentence to array list 
    BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.UK); //Might have to change to US - we'll see
    iterator.setText(plaintext);
    int start = iterator.first();
    for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
        sentences.add(plaintext.substring(start,end));
        count++;
    }

    //Iterate through each  sentence and get compound score
    for (String sentence : sentences) {
        SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer(sentence);
        sentimentAnalyzer.analyze();
        compound = compound + sentimentAnalyzer.getPolarity().get(sentence);
    }

    compound = compound/count; //Gets mean of all compound scores
    return compound;

}

}