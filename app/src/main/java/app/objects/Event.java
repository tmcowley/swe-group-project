package app.objects;

import java.sql.Timestamp;

// enum eventType {
//     lecture, 
//     seminar, 
//     conference, 
//     workshop, 
//     other
// }

public class Event{

    protected int event_id;
    protected int host_id;
    protected int template_id;
    protected String title;
    protected String description;
    protected String type;
    protected Timestamp start_time;
    protected Timestamp end_time;
    protected String eventCode;

    public Event(int event_id, int host_id, int template_id, String title, String description, String type, Timestamp start_time, Timestamp end_time, String eventCode){
        this.event_id    = event_id; 
        this.host_id     = host_id;
        this.template_id = template_id;
        this.title          = title;
        this.description    = description;
        this.type           = type;
        this.start_time     = start_time;
        this.end_time       = end_time;
        this.eventCode      = eventCode;
    }

    public int getEventID(){
        return this.event_id;
    }

    public int getHostID(){
        return this.host_id;
    }

    public int getTemplateID(){
        return this.template_id;
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

    public String getEventCode(){
        return this.eventCode;
    }
}