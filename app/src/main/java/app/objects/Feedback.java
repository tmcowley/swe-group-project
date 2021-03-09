package app.objects;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Feedback {

    private int feedback_id;
    private int participant_id;
    private int event_id;
    private String[] results; // Holds results to specific feedback queries
    private Float[] weights; // Holds the weights (for weighted mean) associated with each result (first unprocessed weights, then processed weights)
    private Byte[] types; // Holds the type of query that produced each result
    // 0 = plaintext
    // 1 = single answer multiple choice
    // 2 = many answer multiple choice
    private Boolean[] keys; // Holds whether a result is a key result or not (true = key result)
    private Byte[][] sub_weights; //Holds weights (unprocessed then processed) associated with each set result in multiple choice queries
    private boolean anonymous;
    private Timestamp timestamp;
    private Float compound; // Holds the compound score (part of sentiment)
    private ArrayList<String> key_results; // Holds an array of all key results


    public Feedback(int feedback_id, int participant_id, int event_id,
            String[] results, Float[] weights, Byte[] types, Boolean[] keys, Byte[][] sub_weights, boolean anonymous, Timestamp timestamp) {
        this.feedback_id = feedback_id;
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

    public Feedback(int feedback_id, int participant_id, int event_id,
            String[] results, Float[] weights, Byte[] types, Boolean[] keys, Byte[][] sub_weights, boolean anonymous, Timestamp timestamp, Float compound, ArrayList<String> key_results) {
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

    public int getFeedbackID() {
        return this.feedback_id;
    }

    public int getEventID() {
        return this.event_id;
    }

    public int getParticipantID() {
        return this.participant_id;
    }

    public String[] getResults() {
        return this.results;
    }

    public Float[] getWeights() {
        return this.weights;
    }

    public Byte[] getTypes() {
        return this.types;
    }

    public Boolean[] getKeys() {
        return this.keys;
    }

    public Float getCompound() {
        return this.compound;
    }

    public ArrayList<String> getKey_Results() {
        return this.key_results;
    }

    public boolean getAnonymous() {
        return this.anonymous;
    }

    public Timestamp getTimestamp() {
        return this.timestamp;
    }

    public Byte[][] getSub_Weights() {
        return this.sub_weights;
    }

    public Byte[] getSub_WeightsRow(int i) {
        return this.sub_weights[i];
    }

    public void setWeightItem(Float new_weight, int index) {
        this.weights[index] = new_weight;
    }

    public void setCompound(float new_compound) {
        this.compound = new_compound;
    }

    public void addKey_Results(String new_key_result) {
        this.key_results.add(new_key_result);
    }

    public boolean equals(Feedback that) {
        if (this.event_id != that.getEventID())
            return false;
        if (this.participant_id != that.getParticipantID())
            return false;
        if (this.feedback_id != that.getFeedbackID())
            return false;
        if (this.results != that.getResults())
            return false;
        if (this.weights != that.getWeights())
            return false;
        if (this.types != that.getTypes())
            return false;
        if (this.keys != that.getKeys())
            return false;
        if (this.compound != that.getCompound())
            return false;
        if (this.key_results != that.getKey_Results())
            return false;
        if (this.anonymous != that.getAnonymous())
            return false;
        if (this.timestamp != that.getTimestamp())
            return false;
        return true;
    }
}