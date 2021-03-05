package app;

public class Sentiment {

/**
     * Take feedback and return sentiment
     * @param feedback feedback to be analysed for sentiment
     * @return sentiment containing compound score (add key results?)
     */
public String[] main(String[][] feedback) {

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
private Real weightedMean(Real[] weights, Real[] scores) {
Real mean = 0; 
for (int i = 0; i < len(weights); i++) {
    mean += weights[i]*scores[i];
}
return mean;
}


/**
     * Break plaintext into scentences
     * Analyse each scentence for compound score
     * Get mean of compound scores
     * @param plaintext string to be analysed for sentiment
     * @return real compound score derived from plaintext
     */
private Real getCompound(String plaintext) {

    Real compound = 0; //Holds compound score derived from plaintext
    Int count = 0; //Used to mean compound scores

    BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.UK); //Look into changing to a UK version, if such exists
    iterator.setText(plaintext);
    int start = iterator.first();
    
    //Iterates through plaintext and selects each scentence within plaintext
    for (int end = iterator.next();
        end != BreakIterator.DONE;
        start = end, end = iterator.next()) {
        
        //Analyses scentence for compound score
        String scentence= plaintext.substring(start,end));
        SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer(sentence);
        sentimentAnalyzer.analyse();
        compound += sentimentAnalyzer.getPolarity(); //Adds compound score to sum of all compound scores
        count++;
    
    }

    compound = compound/count; //Gets mean of all compound scores
    return compound;

}
}