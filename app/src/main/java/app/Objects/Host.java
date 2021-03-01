package app.Objects;

public class Host{

    protected int host_id;
    protected String host_code;
    protected String ip_address;
    protected String e_address;
    protected String f_name;
    protected String l_name;
    protected boolean sys_ban;

    public Host(int host_id, String host_code, String ip_address, String e_address, String f_name, String l_name, boolean sys_ban){
        this.host_id = host_id;
        this.host_code = host_code;
        this.ip_address = ip_address;
        this.e_address = e_address;
        this.f_name = f_name;
        this.l_name = l_name;
        this.sys_ban = sys_ban;
    }

}