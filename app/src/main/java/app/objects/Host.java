package app.objects;

public class Host {

    private int host_id;
    private String host_code;
    private String e_address;
    private String f_name;
    private String l_name;
    private boolean sys_ban;
    /**
     * Host constructer
     * @param host_id
     * @param host_code
     * @param e_address
     * @param f_name
     * @param l_name
     * @param sys_ban
     */
    public Host(int host_id, String host_code, String e_address, String f_name, String l_name,
            boolean sys_ban) {
        this.host_id = host_id;
        this.host_code = host_code;
        this.e_address = e_address;
        this.f_name = f_name;
        this.l_name = l_name;
        this.sys_ban = sys_ban;
    }
    /**
     * gets host id
     * @return host id
     */
    public int getHostID() {
        return this.host_id;
    }
    /**
     * gets host code
     * @return host code
     */
    public String getHostCode() {
        return this.host_code;
    }
    /**
     * gets email address
     * @return email address
     */
    public String getEAddress() {
        return this.e_address;
    }
    /**
     * gets host's first name
     * @return first name
     */
    public String getFName() {
        return this.f_name;
    }
    /**
     * gets host's last name
     * @return last name
     */
    public String getLName() {
        return this.l_name;
    }
    /**
     * shows if host is banned
     * @return ban status
     */
    public boolean getSysBan() {
        return this.sys_ban;
    }
    /**
     * compares 2 host objects
     * @param that
     * @return True or False
     */
    public boolean equals(Host that) {
        if (this.host_id != that.getHostID())
            return false;
        if (this.host_code != that.getHostCode())
            return false;
        if (this.e_address != that.getEAddress())
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