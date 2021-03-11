package app.objects;

public class Participant {

    private int participant_id;
    private String ip_address;
    private String f_name;
    private String l_name;
    private boolean sys_ban;
    /**
     * Participant constructor
     * @param ip_address
     * @param f_name
     * @param l_name
     * @param sys_ban
     */
    public Participant(int participant_id, String f_name, String l_name, boolean sys_ban) {
        this.participant_id = participant_id;
        this.f_name = f_name;
        this.l_name = l_name;
        this.sys_ban = sys_ban;
    }
    /**
     * gets participant id
     * @return participant id
     */
    public int getParticipantID() {
        return this.participant_id;
    }
    /**
     * gets participant's IP address
     * @return IP address
     */
    public String getIPAddress() {
        return this.ip_address;
    }
    /**
     * gets participant's first name
     * @return first name
     */
    public String getFName() {
        return this.f_name;
    }
    /**
     * gets participant's last name
     * @return last name
     */
    public String getLName() {
        return this.l_name;
    }
    /**
     * shows if participant is banned
     * @return ban status
     */
    public boolean getSysBan() {
        return this.sys_ban;
    }
    /**
     * compares 2 participant objects
     * @param that
     * @return True or False
     */
    public boolean equals(Participant that) {
        if (this.participant_id != that.getParticipantID())
            return false;
        if (this.ip_address != that.getIPAddress())
            return false;
        if (this.f_name != that.getFName())
            return false;
        if (this.l_name != that.getLName())
            return false;
        if (this.sys_ban != that.getSysBan())
            return false;
        return true;
    }
}