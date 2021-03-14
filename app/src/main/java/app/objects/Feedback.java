package app.objects;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;

public class Feedback {

    private int feedback_id;
    private int participant_id;
    private int event_id;
    private String[] results; // Holds results to specific feedback queries
    private Float[] weights; // Holds the weights (for weighted mean) associated with each result (first unprocessed weights, then processed weights)
    private byte[] types; // Holds the type of query that produced each result
    // 0 = plaintext
    // 1 = single answer multiple choice
    // 2 = many answer multiple choice
    private Boolean[] keys; // Holds whether a result is a key result or not (true = key result)
    private byte[][] sub_weights; //Holds weights (unprocessed then processed) associated with each set result in multiple choice queries
    private boolean anonymous;
    private Timestamp timestamp;
    private Float compound; // Holds the compound score (part of sentiment)
    private ArrayList<String> key_results; // Holds an array of all key results

    /**
     * Feedback constructor when sentiment is unknown
     * @param participant_id
     * @param event_id
     * @param results
     * @param weights
     * @param types
     * @param keys
     * @param sub_weights
     * @param anonymous
     * @param timestamp
     */
    public Feedback(int participant_id, int event_id,
            String[] results, Float[] weights, byte[] types, Boolean[] keys, byte[][] sub_weights, boolean anonymous, Timestamp timestamp) {
        this.participant_id = participant_id;
        this.event_id = event_id;
        this.anonymous = anonymous;
        this.timestamp = timestamp;
        this.results = results;
        this.weights = weights;
        this.types = types;
        this.keys = keys;
        this.compound = null;
        this.key_results = new ArrayList<String>();
        this.sub_weights = sub_weights;
    }
    /**
     * Feedback constructor when sentiment is known
     * @param feedback_id
     * @param participant_id
     * @param event_id
     * @param results
     * @param weights
     * @param types
     * @param keys
     * @param sub_weights
     * @param anonymous
     * @param timestamp
     * @param compound
     * @param key_results
     */
    public Feedback(int feedback_id, int participant_id, int event_id,
            String[] results, Float[] weights, byte[] types, Boolean[] keys, byte[][] sub_weights, boolean anonymous, Timestamp timestamp, Float compound, ArrayList<String> key_results) {
        this.feedback_id = feedback_id;
        this.participant_id = participant_id;
        this.event_id = event_id;
        this.anonymous = anonymous;
        this.timestamp = timestamp;
        this.results = results;
        this.weights = weights;
        this.types = types;
        this.keys = keys;
        this.compound = compound;
        this.key_results = key_results;
        this.sub_weights = sub_weights;
    }
    /**
     * gets feedback id
     * @return feedback id
     */
    public int getFeedbackID() {
        return this.feedback_id;
    }
    /**
     * gets event id
     * @return event id
     */
    public int getEventID() {
        return this.event_id;
    }
    /**
     * gets participant id
     * @return participant id
     */
    public int getParticipantID() {
        return this.participant_id;
    }
    /**
     * gets result for specific feedback query
     * @return feedback results
     */
    public String[] getResults() {
        return this.results;
    }
    /**
     * gets the weights (for weighted mean) associated with each result (first unprocessed weights, then processed weights)
     * @return feedback weights
     */
    public Float[] getWeights() {
        return this.weights;
    }
    /**
     * gets feedback type
     * @return feedback type
     */
    public byte[] getTypes() {
        return this.types;
    }
    /**
     * gets feedback key
     * @return feedback key
     */
    public Boolean[] getKeys() {
        return this.keys;
    }
    /**
     * gets compound score
     * @return compound score
     */
    public Float getCompound() {
        return this.compound;
    }
    
    public ArrayList<String> getKey_Results() {
        return this.key_results;
    }
    /**
     * gets whether or not a feedback submission is anonymous or not
     * @return True or False
     */
    public boolean getAnonymous() {
        return this.anonymous;
    }
    /**
     * gets timestamp of a feedback submission
     * @return timestamp
     */
    public Timestamp getTimestamp() {
        return this.timestamp;
    }
    /**
     * gets weights (unprocessed then processed) associated with each set result in multiple choice queries
     * @return weights
     */
    public byte[][] getSub_Weights() {
        return this.sub_weights;
    }
    /**
     * gets sub weights
     * @param i
     * @return weights row
     */
    public byte[] getSub_WeightsRow(int i) {
        return this.sub_weights[i];
    }
    /**
     * sets item weight
     * @param new_weight
     * @param index
     */
    public void setWeightItem(float new_weight, int index) {
        this.weights[index] = new_weight;
    }
    /**
     * sets new compound
     * @param new_compound
     */
    public void setCompound(Float new_compound) {
        this.compound = new_compound;
    }
    /**
     * adds key results
     * @param new_key_result
     */
    public void addKey_Results(String new_key_result) {
        this.key_results.add(new_key_result);
    }

    /**
     * assess sentiment of compound
     * @return sentiment (qualitative)
     */
    public String assessSentiment(){
        Float compound = this.compound;
        if (compound == null) {
            return null;
        }
        if (compound > 1 || compound < -1){
            return null;
        }
        if (compound > 0.15) {
            if (compound < 0.45) {
                return("slightly positive");
            } 
            else if (compound < 0.75) {
                return("positive");
            } 
            else {
                return("very positive");
            }
        } else if (compound < -0.15) {
            if (compound > -0.45) {
                return("slightly negative");
            } 
            else if (compound > -0.75) {
                return("negative");
            } 
            else {
                return("very negative");
            }
        } else {
            return("neutral");
        }
    }

    /**
     * null-safe feedback comparison
     * @param that other feedback
     * @return this equals that
     */
    public boolean equals(Feedback that) {
        // ensure other feedback is not null
        if (that == null){
            return false;
        }
        // ensure feedback IDs match
        if (this.feedback_id != that.getFeedbackID()){
            return false;
        }
        // ensure event IDs match
        if (this.event_id != that.getEventID()){
            return false;
        }
        // ensure participant IDs match
        if (this.participant_id != that.getParticipantID()){
            return false;
        }
        // ensure results arrays are equal
        if (!Arrays.equals(this.results, that.getResults())){
            return false;
        }
        // ensure weights arrays are equal
        if (!Arrays.equals(this.weights, that.getWeights())){
            return false;
        }
        // ensure types arrays are equal
        if (!Arrays.equals(this.types, that.getTypes())){
            return false;
        }
        // ensure keys arrays are equal
        if (!Arrays.equals(this.keys, that.getKeys())){
            return false;
        }
        // ensure compounds are equal
        if (!this.compound.equals(that.getCompound())){
            return false;
        }
        // ensure key results arrays are equal
        if (this.key_results != that.getKey_Results()){
            return false;
        }
        // ensure anonymity states are equal
        if (this.anonymous != that.getAnonymous()){
            return false;
        }
        // ensure timestamps are equal
        if (!this.timestamp.equals(that.getTimestamp())){
            return false;
        }
        // feedback instances considered equal
        return true;
    }
}