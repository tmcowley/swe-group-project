package app.objects;

// for array comparison in class equals method
import java.util.Arrays;

// for checking if a field is null, empty, or blank
import spark.utils.StringUtils;

public class TemplateComponent {

    private Integer id; // component ID
    private String name; // component name (simplified prompt)
    private String type; // question, or radio, or checkbox
    private String prompt; // question/ prompt

    // is the component considered in sentiment analysis
    private Boolean tc_considered_in_sentiment;

    // sentiment weight (1-5)
    private Integer tc_sentiment_weight;

    // array of radio or checkbox options
    private String[] options;

    // array of boolean responses to options array
    // e.g. t, f, t for checkbox type; empty if type is question
    private Boolean[] optionsAns;

    // sentiment positivity rating (1 to 7)
    private int[] tc_options_pos;

    // text response field following prompt
    // null if type radio or checkbox
    private String textResponse;

    /**
     * Template component constructor without IDs
     * 
     * @param id           component ID
     * @param name         component name (simplified prompt)
     * @param type         question, or radio, or checkbox
     * @param prompt       question/ prompt
     * @param options      array of radio or checkbox options
     * @param optionsAns   array of boolean responses to options array
     * @param textResponse text response field following prompt null if type radio
     *                     or checkbox
     */
    public TemplateComponent(String name, String type, String prompt, String[] options, Boolean[] optionsAns,
            String textResponse) {
        this.id = null;
        this.name = name;
        this.type = type;
        this.prompt = prompt;
        this.options = options;
        this.optionsAns = optionsAns;
        this.textResponse = textResponse;
    }

    /**
     * Template component constructor with IDs
     * 
     * @param id           component ID
     * @param name         component name (simplified prompt)
     * @param type         question, or radio, or checkbox
     * @param prompt       question/ prompt
     * @param options      array of radio or checkbox options
     * @param optionsAns   array of boolean responses to options array
     * @param textResponse text response field following prompt null if type radio
     *                     or checkbox
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

    public TemplateComponent(String name, String type, String prompt, Boolean tc_considered_in_sentiment, Integer tc_sentiment_weight, String[] options, int[] tc_options_pos, Boolean[] optionsAns,
            String textResponse) {
        this.name = name;
        this.type = type;
        this.prompt = prompt;
        this.tc_considered_in_sentiment = tc_considered_in_sentiment;
        this.tc_sentiment_weight = tc_sentiment_weight;
        this.options = options;
        this.optionsAns = optionsAns;
        this.tc_options_pos = tc_options_pos;
        this.textResponse = textResponse;
    }

    /**
     * set id for template component
     * 
     * @param id component ID
     * @return edited TemplateComponent object
     */
    public TemplateComponent setId(int id) {
        this.id = id;
        return TemplateComponent.this;
    }

    /**
     * get component ID
     * 
     * @return component ID
     */
    public Integer getId() {
        return this.id;
    }
    public Integer getID() {
        return this.id;
    }

    /**
     * set component name
     * 
     * @param name component name
     * @return edited TemplateComponent object
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
     * @param type component type
     * @return edited TemplateComponent object
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
     * @param prompt question/prompt
     * @return edited TemplateComponent object
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
     * @param options array of radio or checkbox options
     * @return edited TemplateComponent object
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
     * @param optionsAns array of boolean responses to options array
     * @return edited TemplateComponent object
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
     * @param textResponse text response field following prompt null if type radio
     *                     or checkbox
     * @return edited TemplateComponent object
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
     * check if a component is empty (has no prompt)
     * @return empty state
     */
    public boolean isEmptyComponent() {
        return (StringUtils.isBlank(prompt));
    }

    /**
     * null-safe comparison of template component objects
     * 
     * @param that other TemplateComponent
     * @return true or false
     */
    public boolean equals(TemplateComponent that) {
        // ensure other component is not null
        if (that == null) {
            return false;
        }
        // ensure component IDs match
        if (this.id != that.getId()) {
            return false;
        }
        // ensure component names match
        if (!this.name.equals(that.getName())) {
            return false;
        }
        // ensure options arrays match
        if (!Arrays.equals(this.options, that.getOptions())) {
            return false;
        }
        // ensure option answer arrays are equal
        if (!Arrays.equals(this.optionsAns, that.getOptionsAns())) {
            return false;
        }
        // ensure component prompts match
        if (!this.prompt.equals(that.getPrompt())) {
            return false;
        }
        // ensure text responses are equal
        if (!this.textResponse.equals(that.getTextResponse())) {
            return false;
        }
        // ensure component types are equal
        if (!this.type.equals(that.getType())) {
            return false;
        }
        // template components are equal
        return true;
    }
}