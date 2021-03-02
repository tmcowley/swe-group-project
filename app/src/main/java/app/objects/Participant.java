package app.objects;

public class Participant{

    private int participant_id;
    private String ip_address;
    private String f_name;
    private String l_name;
    private boolean sys_ban;

    public Participant(int participant_id, String ip_address, String f_name, String l_name, boolean sys_ban){
        this.participant_id = participant_id;
        this.ip_address = ip_address;
        this.f_name = f_name;
        this.l_name = l_name;
        this.sys_ban = sys_ban;
    }

    public int getParticipantID(){
        return this.participant_id;
    }

    public String getIPAddress(){
        return this.ip_address;
    }

    public String getFName(){
        return this.f_name;
    }

    public String getLName(){
        return this.l_name;
    }

    public boolean getSysBan(){
        return this.sys_ban;
    }

}