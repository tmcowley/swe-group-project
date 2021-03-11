package app.objects;

// for array comparison in class equals method
import java.util.Arrays;

public class TemplateComponent {

  private int id; // component ID
  private String name; // component name (simplified prompt)
  private String type; // question, or radio, or checkbox
  private String prompt; // question/ prompt
  private String[] options; // array of radio or checkbox options

  // array of boolean responses to options array
  // e.g. t, f, t for checkbox type; empty if type is question
  private Boolean[] optionsAns;

  // text response field following prompt
  // null if type radio or checkbox
  private String textResponse;

  public TemplateComponent() {}

  /**
   * Template component constructor
   * 
   * @param id
   * @param name
   * @param type
   * @param prompt
   * @param options
   * @param optionsAns
   * @param textResponse
   */
  public TemplateComponent(int id, String name, String type, String prompt, String[] options, Boolean[] optionsAns,
      String textResponse) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.prompt = prompt;
    this.options = options;
    this.optionsAns = optionsAns;
    this.textResponse = textResponse;
  }

  /**
   * sets id for template component
   */
  public TemplateComponent setId(int id) {
    this.id = id;
    return TemplateComponent.this;
  }

  /**
   * gets component id
   * 
   * @return component id
   */
  public int getId() {
    return this.id;
  }

  /**
   * sets component name
   * 
   * @param name
   * @return
   */
  public TemplateComponent setName(String name) {
    this.name = name;
    return TemplateComponent.this;
  }

  /**
   * gets component name
   * 
   * @return component name
   */
  public String getName() {
    return this.name;
  }

  /**
   * sets component type
   * 
   * @param type
   * @return
   */
  public TemplateComponent setType(String type) {
    this.type = type;
    return TemplateComponent.this;
  }

  /**
   * gets component type
   * 
   * @return component type
   */
  public String getType() {
    return this.type;
  }

  /**
   * sets component prompt
   * 
   * @param prompt
   * @return
   */
  public TemplateComponent setPrompt(String prompt) {
    this.prompt = prompt;
    return TemplateComponent.this;
  }

  /**
   * gets component prompt
   * 
   * @return component prompt
   */
  public String getPrompt() {
    return this.prompt;
  }

  /**
   * sets component options
   * 
   * @param options
   * @return
   */
  public TemplateComponent setOptions(String[] options) {
    this.options = options;
    return TemplateComponent.this;
  }

  /**
   * gets component options
   * 
   * @return component options
   */
  public String[] getOptions() {
    return this.options;
  }

  /**
   * sets the answers for the options
   * 
   * @param optionsAns
   * @return
   */
  public TemplateComponent setOptionsAns(Boolean[] optionsAns) {
    this.optionsAns = optionsAns;
    return TemplateComponent.this;
  }

  /**
   * gets the answers of the options
   * 
   * @return option answers
   */
  public Boolean[] getOptionsAns() {
    return this.optionsAns;
  }

  /**
   * sets the text response
   * 
   * @param textResponse
   * @return
   */
  public TemplateComponent setTextResponse(String textResponse) {
    this.textResponse = textResponse;
    return TemplateComponent.this;
  }

  /**
   * gets text response
   * 
   * @return text response
   */
  public String getTextResponse() {
    return this.textResponse;
  }

  /**
   * null-safe comparison of template component objects
   * 
   * @param that other TemplateComponent
   * @return true or false
   */
  public boolean equals(TemplateComponent that) {
    if (that == null)
      return false;
    if (this.id != that.getId())
      return false;
    if (this.name != that.getName())
      return false;
    if (!Arrays.equals(this.options, that.getOptions()))
      return false;
    if (!Arrays.equals(this.optionsAns, that.getOptionsAns()))
      return false;
    if (this.prompt != that.getPrompt())
      return false;
    if (this.textResponse != that.getTextResponse())
      return false;
    if (this.type != that.getType())
      return false;
    return true;
  }
}