package app.Objects;

public class Participant{

    protected int participant_id;
    protected String ip_address;
    protected String f_name;
    protected String l_name;
    protected boolean sys_ban;

    public Participant(int participant_id, String ip_address, String f_name, String l_name, boolean sys_ban){
        this.participant_id = participant_id;
        this.ip_address = ip_address;
        this.f_name = f_name;
        this.l_name = l_name;
        this.sys_ban = sys_ban;
    }

}