package app;

// enum eventType {
//     lecture, 
//     seminar, 
//     conference, 
//     workshop, 
//     other
// }

public class Event{

    protected int eventID;
    protected int hostID;
    protected String title;
    protected String desc;
    protected String eventCode;
    protected String type;

    /**
     * Construct an Event object
     * @param eventID
     * @param hostID
     * @param title
     * @param desc
     * @param eventCode
     * @param type
     */
    public Event(int eventID, int hostID, String title, String desc, String eventCode, String type){
        this.eventID    = eventID; 
        this.hostID     = hostID;
        this.title      = title;
        this.desc       = desc;
        this.eventCode  = eventCode;
        this.type       = type;
    }
}