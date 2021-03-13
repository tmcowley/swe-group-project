package app.objects;

import java.sql.Timestamp;

// enum eventType {
//     lecture, 
//     seminar, 
//     conference, 
//     workshop, 
//     other
// }

public class Event {

    private int event_id;
    private int host_id;
    private int template_id;
    private String title;
    private String description;
    private String type;
    private Timestamp start_time;
    private Timestamp end_time;
    private String eventCode;

    /**
     * Event constructor
     * @param event_id
     * @param host_id
     * @param template_id
     * @param title
     * @param description
     * @param type
     * @param start_time
     * @param end_time
     * @param eventCode
     */
    public Event(int event_id, int host_id, int template_id, String title, String description, String type,
            Timestamp start_time, Timestamp end_time, String eventCode) {
        this.event_id = event_id;
        this.host_id = host_id;
        this.template_id = template_id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.start_time = start_time;
        this.end_time = end_time;
        this.eventCode = eventCode;
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
     * gets template id
     * @return template id
     */
    public int getTemplateID() {
        return this.template_id;
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
     * gets event code
     * @return event code
     */
    public String getEventCode() {
        return this.eventCode;
    }
    /**
     * null-safe event comparison
     * @param that other event
     * @return equals state
     */
    public boolean equals(Event that) {
        // ensure other event is not null
        if (that == null){
            return false;
        }
        if (this.event_id != that.getEventID())
            return false;
        if (this.host_id != that.getHostID())
            return false;
        if (this.template_id != that.getTemplateID())
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
        if (!this.eventCode.equals(that.getEventCode()))
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
    /**
     * sets event code
     * @param event_code
     */
    public void setEventCode(String event_code){
        this.eventCode = event_code;
    }
    /**
     * sets template id
     * @param id
     */
    public void setTemplateID(int id){
        this.template_id = id;
    }
}