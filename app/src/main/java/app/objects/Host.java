package app.objects;

public class Host {

    private int host_id;
    private String host_code;
    private String e_address;
    private String f_name;
    private String l_name;
    private boolean sys_ban;

    /**
     * create a host; 
     * all attributes controllable
     * 
     * @param host_id host identifier
     * @param host_code host unique token
     * @param e_address host email address
     * @param f_name host first name
     * @param l_name host last name
     * @param sys_ban host system ban state
     */
    public Host(int host_id, String host_code, String e_address, String f_name, String l_name, boolean sys_ban) {
        this.host_id = host_id;
        this.host_code = host_code;
        this.e_address = e_address;
        this.f_name = f_name;
        this.l_name = l_name;
        this.sys_ban = sys_ban;
    }

    /**
     * create a host with a default ban state of false
     * 
     * @param host_id host identifier
     * @param host_code host unique token
     * @param e_address host email address
     * @param f_name host first name
     * @param l_name host last name
     */
    public Host(int host_id, String host_code, String e_address, String f_name, String l_name) {
        this.host_id = host_id;
        this.host_code = host_code;
        this.e_address = e_address;
        this.f_name = f_name;
        this.l_name = l_name;

        // host is not banned, by default
        this.sys_ban = false;
    }

    /**
     * gets host id
     * 
     * @return host id
     */
    public int getHostID() {
        return this.host_id;
    }

    /**
     * gets host code
     * 
     * @return host code
     */
    public String getHostCode() {
        return this.host_code;
    }

    /**
     * gets email address
     * 
     * @return email address
     */
    public String getEAddress() {
        return this.e_address;
    }

    /**
     * gets host's first name
     * 
     * @return first name
     */
    public String getFName() {
        return this.f_name;
    }

    /**
     * gets host's last name
     * 
     * @return last name
     */
    public String getLName() {
        return this.l_name;
    }

    /**
     * shows if host is banned
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
     * sets email address
     * 
     * @param email
     */
    public void setEAddress(String email) {
        this.e_address = email;
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
     * sets host code
     * 
     * @param code
     */
    public void setHostCode(String code) {
        this.host_code = code;
    }

    /**
     * sets host id
     * 
     * @param id
     */
    public void setHostID(int id) {
        this.host_id = id;
    }

    /**
     * compares 2 host objects
     * 
     * @param that
     * @return True or False
     */
    public boolean equals(Host that) {
        // ensure other host is not null
        if (that == null) {
            return false;
        }
        // ensure host IDs match
        if (this.host_id != that.getHostID()) {
            return false;
        }
        // ensure host codes match
        if (!this.host_code.equals(that.getHostCode())) {
            return false;
        }
        // ensure email addresses match
        if (!this.e_address.equals(that.getEAddress())) {
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
        // two hosts are equal
        return true;
    }

}