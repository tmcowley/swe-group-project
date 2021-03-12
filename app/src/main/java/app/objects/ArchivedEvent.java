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
     * gets event id
     * @return event id
     */
    public int getEventID() {
        return this.event_id;
    }
    /**
     * gets host id
     * @return host id
     */
    public int getHostID() {
        return this.host_id;
    }
    /**
     * gets tootal mood of event
     * @return total mood of event
     */
    public String getMood() {
        return this.total_mood;
    }
    /**
     * gets event title
     * @return event title
     */
    public String getTitle() {
        return this.title;
    }
    /**
     * gets event description
     * @return event description
     */
    public String getDescription() {
        return this.description;
    }
    /**
     * gets event type
     * @return event type
     */
    public String getType() {
        return this.type;
    }
    /**
     * gets event start time
     * @return event start time
     */
    public Timestamp getStartTime() {
        return this.start_time;
    }
    /**
     * gets event end time
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
        if (!this.total_mood.equals(that.getMood()))
            return false;
        if (!this.title.equals(that.getTitle()))
            return false;
        if (!this.description.equals(that.getDescription()))
            return false;
        if (!this.type.equals(that.getType()))
            return false;
        if (this.start_time != that.getStartTime())
            return false;
        if (this.end_time != that.getEndTime())
            return false;
        return true;
    }
    /**
     * sets host id
     * @param id
     */
    public void setHostID(int id){
        this.host_id = id;
    }
    /**
     * sets event id
     * @param id
     */
    public void setEventID(int id){
        this.event_id = id;
    }
    /**
     * sets title
     * @param title
     */
    public void setTitle(String title){
        this.title = title;
    }
    /**
     * sets mood
     * @param mood
     */
    public void setMood(String mood){
        this.total_mood = mood;
    }
    /**
     * sets description
     * @param desc
     */
    public void setDesciption(String desc){
        this.description = desc;
    }
    /**
     * sets type
     * @param type
     */
    public void setType(String type){
        this.type = type;
    }
    /**
     * sets Start time
     * @param ts
     */
    public void setStartTime(Timestamp ts){
        this.start_time = ts;
    }
    /**
     * sets end time
     * @param ts
     */
    public void setEndTime(Timestamp ts){
        this.end_time = ts;
    }
}