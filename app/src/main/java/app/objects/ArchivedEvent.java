package app.objects;

import java.sql.Timestamp;

// enum eventType {
//     lecture, 
//     seminar, 
//     conference, 
//     workshop, 
//     other
// }

public class ArchivedEvent{

    private int event_id;
    private int host_id;
    private String total_mood;
    private String title;
    private String description;
    private String type;
    private Timestamp start_time;
    private Timestamp end_time;

    public ArchivedEvent(int event_id, int host_id, String total_mood, String title, String description, String type, Timestamp start_time, Timestamp end_time){
        this.event_id       = event_id; 
        this.host_id        = host_id;
        this.total_mood     = total_mood;
        this.title          = title;
        this.description    = description;
        this.type           = type;
        this.start_time     = start_time;
        this.end_time       = end_time;
    }

    public int getEventID(){
        return this.event_id;
    }

    public int getHostID(){
        return this.host_id;
    }

    public String getMood(){
        return this.total_mood;
    }

    public String getTitle(){
        return this.title;
    }

    public String getDescription(){
        return this.description;
    }

    public String getType(){
        return this.type;
    }

    public Timestamp getStartTime(){
        return this.start_time;
    }
    
    public Timestamp getEndTime(){
        return this.end_time;
    }
}