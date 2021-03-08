package app.objects;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Feedback {

    private int feedback_id;
    private int participant_id;
    private int event_id;
    private String[] results; // Holds results to specific feedback queries
    private Float[] weights; // Holds the weights (for weighted mean) associated with each result
    private Integer[] types; // Holds the type of query that produced each result
    // 0 = plaintext

    private Boolean[] keys; // Holds whether a result is a key result or not (true = key result)
    private Float compound; // Holds the compound score (part of sentiment)
    private ArrayList<String> key_results; // Holds an array of all key results
    private boolean anonymous;
    private Timestamp timestamp;

    public Feedback(int feedback_id, int participant_id, int event_id, boolean anonymous, Timestamp timestamp,
            String[] results, Float[] weights, Integer[] types, Boolean[] keys, float compound, ArrayList<String> key_results) {
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

    public Integer[] getTypes() {
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