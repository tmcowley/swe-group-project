package app.objects;

public class TemplateComponent {

  private int id;
  private String name;
  private String type;
  private String prompt;
  private String[] options;
  private Boolean[] optionsAns;
  private String textResponse;

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