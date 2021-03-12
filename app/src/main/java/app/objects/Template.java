package app.objects;

// for ArrayList of components
import java.util.ArrayList;
import app.objects.TemplateComponent;

// for ArrayList comparison
import org.apache.commons.collections4.CollectionUtils;

public class Template {
    private int template_id;
    private int host_id;
    private String template_name;
    private String template_code;
    private ArrayList<TemplateComponent> components;

    /**
     * Template constructor
     * 
     * @param template_id
     * @param host_id
     * @param template_name
     * @param template_code
     * @param components
     */
    public Template(int template_id, int host_id, String template_name, String template_code,
            ArrayList<TemplateComponent> components) {
        this.template_id = template_id;
        this.host_id = host_id;
        this.template_name = template_name;
        this.template_code = template_code;

        if (components == null) {
            this.components = new ArrayList<TemplateComponent>();
        } else {
            this.components = components;
        }
    }

    /**
     * overloaded template constructor missing components
     * it is assumed the template is empty (has no components
     * )
     * @param template_id
     * @param host_id
     * @param template_name
     * @param template_code
     */
    public Template(int template_id, int host_id, String template_name, String template_code) {
        this.template_id = template_id;
        this.host_id = host_id;
        this.template_name = template_name;
        this.template_code = template_code;
        this.components = new ArrayList<TemplateComponent>();
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
    public String getTemplate_name() {
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
    public void setTemplate_name(String template_name) {
        this.template_name = template_name;
    }

    /**
     * compares 2 template objects
     * 
     * @param that
     * @return True or False
     */
    public boolean equals(Template that) {
        if (this.template_id != that.getTemplateID())
            return false;
        if (this.host_id != that.getHostID())
            return false;
        if (!this.template_code.equals(that.getTemplateCode()))
            return false;
        if (!CollectionUtils.isEqualCollection(this.components, that.getComponents()))
            return false;
        return true;
    }

}