package app.objects;

// for creation time
import java.sql.Timestamp;

// for ArrayList of components
import java.util.ArrayList;

// for ArrayList comparison
import org.apache.commons.collections4.CollectionUtils;

public class Template {

    private int template_id; // template ID
    private int host_id; // author's host ID
    private String template_name; // template name
    private String template_code; // template's unique code
    private Timestamp timestamp; // template's creation timestamp
    // components within the template
    private ArrayList<TemplateComponent> components;

    /**
     * Template constructor
     * 
     * @param template_id   template identifier
     * @param host_id       host (author) identifier
     * @param template_name template name
     * @param template_code template token
     * @param components    template sub-components
     */
    public Template(int template_id, int host_id, String template_name, String template_code, Timestamp timestamp,
            ArrayList<TemplateComponent> components) {
        this.template_id = template_id;
        this.host_id = host_id;
        this.template_name = template_name;
        this.template_code = template_code;
        this.timestamp = timestamp;

        if (components == null) {
            this.components = new ArrayList<TemplateComponent>();
        } else {
            this.components = components;
        }
    }

    /**
     * overloaded template constructor missing components it is assumed the template
     * is empty (without components)
     * 
     * @param template_id   template identifier
     * @param host_id       host (author) identifier
     * @param template_name template name
     * @param template_code template token
     */
    public Template(int template_id, int host_id, String template_name, String template_code, Timestamp timestamp) {
        this.template_id = template_id;
        this.host_id = host_id;
        this.template_name = template_name;
        this.template_code = template_code;
        this.timestamp = timestamp;
        this.components = new ArrayList<TemplateComponent>();
    }

    /**
     * check if a template is empty (has no template components)
     * @return template empty state
     */
    public boolean isEmpty(){
        return (components == null || components.size() == 0);
    }

    /**
     * gets template id
     * 
     * @return template id
     */
    public int getTemplateID() {
        return this.template_id;
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
     * get the template name
     * 
     * @return template name
     */
    public String getTemplateName() {
        return template_name;
    }

    /**
     * gets template code
     * 
     * @return template code
     */
    public String getTemplateCode() {
        return this.template_code;
    }

    /**
     * get timestamp
     * 
     * @return timestamp
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * gets templates components
     * 
     * @return components
     */
    public ArrayList<TemplateComponent> getComponents() {
        return components;
    }

    /**
     * set template name
     * 
     * @param template_name template name
     */
    public void setTemplateName(String template_name) {
        this.template_name = template_name;
    }

    /**
     * set timestamp
     * 
     * @param timestamp timestamp
     */
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * compare this template to another template for equality
     * 
     * @param that other participant
     * @return this equals that
     */
    public boolean equals(Template that) {
        // ensure other template is not null
        if (that == null) {
            return false;
        }
        // ensure template IDs match
        if (this.template_id != that.getTemplateID()) {
            return false;
        }
        // ensure author (host) IDs match
        if (this.host_id != that.getHostID()) {
            return false;
        }
        // ensure template tokens (codes) match
        if (!this.template_code.equals(that.getTemplateCode())) {
            return false;
        }
        // ensure component ArrayLists match
        if (!CollectionUtils.isEqualCollection(this.components, that.getComponents())) {
            return false;
        }
        // templates match
        return true;
    }

}