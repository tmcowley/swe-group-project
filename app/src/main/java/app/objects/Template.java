package app.objects;

public class Template{

    protected int template_id;
    protected int host_id;
    protected String template_code;
    protected String data;

    public Template(int template_id, int host_id, String template_code, String data){
        this.template_id = template_id;
        this.host_id = host_id;
        this.template_code = template_code;
        this.data = data;
    }

    public int getTemplateID(){
        return this.template_id;
    }

    public int getHostID(){
        return this.host_id;
    }

    public String getTemplateCode(){
        return this.template_code;
    }

    public String getData(){
        return this.data;
    }
    
}