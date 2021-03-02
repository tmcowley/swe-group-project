package app.objects;

import java.sql.Timestamp;

public class Feedback{

    protected int feedback_id;
    protected int host_id;
    protected int event_id;
    protected String data;
    protected String sentiment;
    protected boolean anonymous;
    protected Timestamp timestamp;

    public Feedback(int feedback_id, int host_id, int event_id, String data, String sentiment, boolean anonymous, Timestamp timestamp){
        this.feedback_id    = feedback_id; 
        this.host_id        = host_id;
        this.event_id       = event_id;
        this.data           = data;
        this.sentiment      = sentiment;
        this.anonymous      = anonymous;
        this.timestamp      = timestamp;
    }

    public int getFeedbackID(){
        return this.feedback_id;
    }

    public int getEventID(){
        return this.event_id;
    }

    public int getHostID(){
        return this.host_id;
    }

    public String getData(){
        return this.data;
    }

    public String getSentiment(){
        return this.sentiment;
    }

    public boolean getAnonymous(){
        return this.anonymous;
    }

    public Timestamp getTimestamp(){
        return this.timestamp;
    }
    
}