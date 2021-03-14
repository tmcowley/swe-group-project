package app.objects;

public class Participant {

    private int participant_id; // participant identifier
    private String ip_address; // participant IP address
    private String f_name; // participant first name
    private String l_name; // participant last name
    private boolean sys_ban; // participant system ban state

    /**
     * construct a participant
     * 
     * @param f_name  participant first-name
     * @param l_name  participant last-name
     * @param sys_ban participant system ban state
     */
    public Participant(int participant_id, String f_name, String l_name, boolean sys_ban) {
        this.participant_id = participant_id;
        this.f_name = f_name;
        this.l_name = l_name;
        this.sys_ban = sys_ban;
    }

    /**
     * construct a participant with a default ban state of false
     * 
     * @param f_name participant first-name
     * @param l_name participant last-name
     */
    public Participant(int participant_id, String f_name, String l_name) {
        this.participant_id = participant_id;
        this.f_name = f_name;
        this.l_name = l_name;

        // participant is not banned, by default
        this.sys_ban = false;
    }

    /**
     * gets participant id
     * 
     * @return participant id
     */
    public int getParticipantID() {
        return this.participant_id;
    }

    /**
     * gets participant's IP address
     * 
     * @return IP address
     */
    public String getIPAddress() {
        return this.ip_address;
    }

    /**
     * gets participant's first name
     * 
     * @return first name
     */
    public String getFName() {
        return this.f_name;
    }

    /**
     * gets participant's last name
     * 
     * @return last name
     */
    public String getLName() {
        return this.l_name;
    }

    /**
     * shows if participant is banned
     * 
     * @return ban status
     */
    public boolean getSysBan() {
        return this.sys_ban;
    }

    /**
     * sets first name
     * 
     * @param name
     */
    public void setFName(String name) {
        this.f_name = name;
    }

    /**
     * sets last name
     * 
     * @param name
     */
    public void setLName(String name) {
        this.l_name = name;
    }

    /**
     * sets system ban
     * 
     * @param ban
     */
    public void setSysBan(Boolean ban) {
        this.sys_ban = ban;
    }

    /**
     * sets participant id
     * 
     * @param id
     */
    public void setPartID(int id) {
        this.participant_id = id;
    }

    /**
     * compares 2 participant objects
     * 
     * @param that
     * @return True or False
     */
    public boolean equals(Participant that) {
        // ensure other host is not null
        if (that == null) {
            return false;
        }
        // ensure participant IDs match
        if (this.participant_id != that.getParticipantID()) {
            return false;
        }
        // ensure first names match
        if (!this.f_name.equals(that.getFName())) {
            return false;
        }
        // ensure last names match
        if (!this.l_name.equals(that.getLName())) {
            return false;
        }
        // ensure system ban states match
        if (this.sys_ban != that.getSysBan()) {
            return false;
        }
        // participants are equal
        return true;
    }
}