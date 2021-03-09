package app.objects;

import java.sql.Timestamp;

// enum eventType {
//     lecture, 
//     seminar, 
//     conference, 
//     workshop, 
//     other
// }

public class ArchivedEvent {

    private int event_id;
    private int host_id;
    private String total_mood;
    private String title;
    private String description;
    private String type;
    private Timestamp start_time;
    private Timestamp end_time;

    public ArchivedEvent(int event_id, int host_id, String total_mood, String title, String description, String type,
            Timestamp start_time, Timestamp end_time) {
        this.event_id = event_id;
        this.host_id = host_id;
        this.total_mood = total_mood;
        this.title = title;
        this.description = description;
        this.type = type;
        this.start_time = start_time;
        this.end_time = end_time;
    }
    /**
     * @return event id
     */
    public int getEventID() {
        return this.event_id;
    }
    /**
     * @return host id
     */
    public int getHostID() {
        return this.host_id;
    }
    /**
     * 
     * @return total mood of event
     */
    public String getMood() {
        return this.total_mood;
    }
    /**
     * 
     * @return event title
     */
    public String getTitle() {
        return this.title;
    }
    /**
     * 
     * @return event description
     */
    public String getDescription() {
        return this.description;
    }
    /**
     * 
     * @return event type
     */
    public String getType() {
        return this.type;
    }
    /**
     * 
     * @return event start time
     */
    public Timestamp getStartTime() {
        return this.start_time;
    }
    /**
     * 
     * @return event end time
     */
    public Timestamp getEndTime() {
        return this.end_time;
    }
    /**
     * compares all fields of 2 event objects
     * @param that
     * @return True or False
     */
    public boolean equals(ArchivedEvent that) {
        if (this.event_id != that.getEventID())
            return false;
        if (this.host_id != that.getHostID())
            return false;
        if (this.total_mood != that.getMood())
            return false;
        if (this.title != that.getTitle())
            return false;
        if (this.description != that.getDescription())
            return false;
        if (this.type != that.getType())
            return false;
        if (this.start_time != that.getStartTime())
            return false;
        if (this.end_time != that.getEndTime())
            return false;
        return true;
    }
}