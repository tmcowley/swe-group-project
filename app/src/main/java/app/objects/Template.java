package app.objects;

// for ArrayList of components
import java.util.ArrayList;
import app.objects.TemplateComponent;

// for ArrayList comparison
import org.apache.commons.collections4.CollectionUtils;

public class Template {
    private int template_id;
    private int host_id;
    private String template_code;
    private ArrayList<TemplateComponent> components;
    /**
     * Tempalate constructer
     * @param template_id
     * @param host_id
     * @param template_code
     * @param components
     */
    public Template(int template_id, int host_id, String template_code, ArrayList<TemplateComponent> components) {
        this.template_id = template_id;
        this.host_id = host_id;
        this.template_code = template_code;
        this.components = components;
    }
    /**
     * gets template id
     * @return template id
     */
    public int getTemplateID() {
        return this.template_id;
    }
    /**
     * gets host id
     * @return host id
     */
    public int getHostID() {
        return this.host_id;
    }
    /**
     * gets template code
     * @return template code
     */
    public String getTemplateCode() {
        return this.template_code;
    }
    /**
     * gets templates components
     * @return components
     */
    public ArrayList<TemplateComponent> getComponents(){
        return components;
    }
    /**
     * compares 2 template objects
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