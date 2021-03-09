package app.objects;

public class TemplateComponent {

  private int id; // component ID
  private String name; // component name (simplified prompt)
  private String type; // question, or radio, or checkbox
  private String prompt; // question/ prompt
  private String[] options; // array of radio or checkbox options
  private Boolean[] optionsAns; // array of boolean responses to options array
                                // e.g. t, f, t for checkbox type
                                // empty if type is question
  private String textResponse; // text response field following prompt
                               // null if type radio or checkbox

  public TemplateComponent() {

  }

  public TemplateComponent(int id, String name, String type, String prompt, String[] options, Boolean[] optionsAns, String textResponse) {
    this.id = id; 
    this.name = name; 
    this.type = type; 
    this.prompt = prompt; 
    this.options = options; 
    this.optionsAns = optionsAns; 
    this.textResponse = textResponse; 
  }

  public TemplateComponent setId(int id){
    this.id = id;
    return TemplateComponent.this;
  }

  public int getId(){
    return this.id;
  }

  public TemplateComponent setName(String name){
    this.name = name;
    return TemplateComponent.this;
  }

  public String getName(){
    return this.name;
  }

  public TemplateComponent setType(String type){
    this.type = type;
    return TemplateComponent.this;
  }

  public String getType(){
    return this.type;
  }

  public TemplateComponent setPrompt(String prompt){
    this.prompt = prompt;
    return TemplateComponent.this;
  }

  public String getPrompt(){
    return this.prompt;
  }

  public TemplateComponent setOptions(String[] options){
    this.options = options;
    return TemplateComponent.this;
  }

  public String[] getOptions(){
    return this.options;
  }

  public TemplateComponent setOptionsAns(Boolean[] optionsAns){
    this.optionsAns = optionsAns;
    return TemplateComponent.this;
  }

  public Boolean[] getOptionsAns(){
    return this.optionsAns;
  }

  public TemplateComponent setTextResponse(String textResponse){
    this.textResponse = textResponse;
    return TemplateComponent.this;
  }

  public String getTextResponse(){
    return this.textResponse;
  }
}