package app.objects;

// arrayList of components
import java.util.ArrayList;
import app.objects.TemplateComponent;

public class Template {

    private int template_id;
    private int host_id;
    private String template_code;
    private String data;
    private ArrayList<TemplateComponent> components;

    public Template(int template_id, int host_id, String template_code, String data, ArrayList<TemplateComponent> components) {
        this.template_id = template_id;
        this.host_id = host_id;
        this.template_code = template_code;
        this.data = data;
        this.components = components;
    }

    public int getTemplateID() {
        return this.template_id;
    }

    public int getHostID() {
        return this.host_id;
    }

    public String getTemplateCode() {
        return this.template_code;
    }

    public String getData() {
        return this.data;
    }

    public ArrayList<TemplateComponent> getComponents(){
        return components;
    }

    public boolean equals(Template that) {
        if (this.template_id != that.getTemplateID())
            return false;
        if (this.host_id != that.getHostID())
            return false;
        if (this.template_code != that.getTemplateCode())
            return false;
        if (this.data != that.getData())
            return false;
        return true;
    }

}