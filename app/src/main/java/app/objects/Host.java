package app.objects;

public class Host{

    private int host_id;
    private String host_code;
    private String ip_address;
    private String e_address;
    private String f_name;
    private String l_name;
    private boolean sys_ban;

    public Host(int host_id, String host_code, String ip_address, String e_address, String f_name, String l_name, boolean sys_ban){
        this.host_id = host_id;
        this.host_code = host_code;
        this.ip_address = ip_address;
        this.e_address = e_address;
        this.f_name = f_name;
        this.l_name = l_name;
        this.sys_ban = sys_ban;
    }

    public int getHostID(){
        return this.host_id;
    }
    
    public String getHostCode(){
        return this.host_code;
    }

    public String getIPAddress(){
        return this.ip_address;
    }

    public String getEAddress(){
        return this.e_address;
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

    public boolean equals(Host that){
        if(this.host_id != that.getHostID()) return false;
        if(this.host_code != that.getHostCode()) return false;
        if(this.ip_address != that.getIPAddress()) return false;
        if(this.e_address != that.getEAddress()) return false;
        if(this.f_name != that.getFName()) return false;
        if(this.l_name != that.getLName()) return false;
        if(this.sys_ban != that.getSysBan()) return false;
        return true;
    }
    
}