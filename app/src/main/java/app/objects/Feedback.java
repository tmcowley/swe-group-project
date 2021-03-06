package app.objects;

import java.sql.Timestamp;

public class Feedback{

    private int feedback_id;
    private int participant_id;
    private int event_id;
    private String[] results;   //Holds results to specific feedback queries
    private float[] weights;    //Holds the weights (for weighted mean) associated with each result
    private int[] type;         //Holds the type fo query that produced each result
    private int[] key;          //Holds wether a result is a key result or not
    private float compound;     //Holds the compound score (part of sentiment)
    private String[] key_results; //Holds an array of all key results
    private boolean anonymous;
    private Timestamp timestamp;

    public Feedback(int feedback_id, int participant_id, int event_id, String[] results, float[] weights, int[] type, int[] key, float compound, String[] key_results, boolean anonymous, Timestamp timestamp){
        this.feedback_id    = feedback_id; 
        this.participant_id = participant_id;
        this.event_id       = event_id;
        this.results        = results;
        this.weights        = weights;
        this.type           = type;
        this.key            = key;
        this.compound       = compound;
        this.key_results    = key_results;
        this.anonymous      = anonymous;
        this.timestamp      = timestamp;
    }

    public int getFeedbackID(){
        return this.feedback_id;
    }

    public int getEventID(){
        return this.event_id;
    }

    public int getParticipantID(){
        return this.participant_id;
    }

    public String[] getResults(){
        return this.results;
    }

    public float[] getWeights(){
        return this.weights;
    }

    public int[] getType(){
        return this.type;
    }

    public int[] getKey(){
        return this.key;
    }

    public float getCompound(){
        return this.compound;
    }

    public String[] getKey_Results(){
        return this.key_results;
    }

    public boolean getAnonymous(){
        return this.anonymous;
    }

    public Timestamp getTimestamp(){
        return this.timestamp;
    }

    public void setCompound(float new_compound){
        this.compound = new_compound;
    }

    public void setKey_Results(String[] new_key_results){
        this.key_results = new_key_results;
    }
    
    public boolean equals(Feedback that){
        if(this.event_id != that.getEventID())          return false;
        if(this.participant_id != that.getParticipantID())     return false;
        if(this.feedback_id != that.getFeedbackID())    return false;
        if(this.results != that.getResults())           return false;
        if(this.weights != that.getWeights())           return false;
        if(this.type != that.getType())                 return false;
        if(this.key != that.getKey())                   return false;
        if(this.compound != that.getCompound())         return false;
        if(this.key_results != that.getKey_Results())   return false;
        if(this.anonymous != that.getAnonymous())       return false;
        if(this.timestamp != that.getTimestamp())       return false;
        return true;
    }
}