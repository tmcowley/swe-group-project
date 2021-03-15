package app;

// IO for word-list import
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;

import java.util.HashSet; // import the HashSet class
import org.apache.commons.lang3.StringUtils; // for event code sanitization
import org.apache.commons.lang3.ArrayUtils;

import app.objects.*;

public class Validator {

    // public void main(String[] args) {}

    HashSet<String> wordListHashSet = new HashSet<String>(1000);
    int peakWordSizeInList = 11;

    public Validator() {
        // store word list (hash-set for O(1) lookup)
        getWordList();
    }

    /**
     * Store the host code word-list in a hash-set DS Lookup (w/ contains()) is
     * O(1): constant time
     */
    private void getWordList() {
        try {
            BufferedReader readIn = new BufferedReader(new FileReader("resources/word-list.txt"));
            String str;
            while ((str = readIn.readLine()) != null) {
                wordListHashSet.add(str);
            }
            readIn.close();
        } catch (IOException ex) {
            // ensure file: word-list.txt is in /app/resources/
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Check if the given event code is valid: non-null, 4-digits in length,
     * alpha-numeric
     * 
     * @param eventCode event code to be checked
     * @return event code validity state
     */
    public boolean eventCodeIsValid(String eventCode) {
        return alphanumericIsValid(eventCode, 4);
    }

    /**
     * Check if the given template code is valid: non-null, 6-digits in length,
     * alpha-numeric
     * 
     * @param templateCode template code to be checked
     * @return template code validity state
     */
    public boolean templateCodeIsValid(String templateCode) {
        return alphanumericIsValid(templateCode, 6);
    }

    /**
     * Check if the given string is valid: non-null, len-digits in length,
     * alpha-numeric
     * 
     * @param string string to be checked
     * @param len    valid length of string
     * @return string validity state
     */
    public boolean alphanumericIsValid(String string, int len) {
        // ensure string is not null
        if (string == null) {
            return false;
        }
        // ensure lengths are equal
        if (string.length() != len) {
            return false;
        }
        // avoid illegal characters
        if (!StringUtils.isAlphanumeric(string)) {
            return false;
        }
        // string is valid
        return true;
    }

    /**
     * Check if the given host code is valid: non-null, alpha-numeric (and dash)
     * characters only, of the form [word-word-word-word] with each word in
     * word-list
     * 
     * @param hostCode host code to be checked
     * @return host code validity state
     */
    public boolean hostCodeIsValid(String hostCode) {
        if (StringUtils.isBlank(hostCode)) {
            // host code is null, empty or whitespace only
            return false;
        }
        if (hostCode.length() > (peakWordSizeInList * 4 + 3)) {
            // host code exceeds upper-bound
            return false;
        }
        if (StringUtils.containsWhitespace(hostCode)) {
            // host code contains illegal whitespace character
            return false;
        }
        if (StringUtils.countMatches(hostCode, "-") != 3) {
            // host code not of format: [word-word-word-word]
            return false;
        }
        String[] hostCodeArray = hostCode.split("-");
        for (String word : hostCodeArray) {
            if (!wordListHashSet.contains(word.toLowerCase())) {
                return false;
            }
        }
        // host code is valid
        return true;
    }

    /**
     * check if a given ID is valid no upper-bound check needed since:
     * upper-bound(java int) > upper-bound(SQLite int)
     * 
     * @param id identifier to be checked
     * @return validity state of id
     */
    public boolean idIsValid(int id) {
        boolean isPositive = (id >= 0);
        return (isPositive);
    }

    /**
     * check if email address is valid
     * 
     * @param e_address email address to be checked
     * @return email address validity state
     */
    public boolean eAddressIsValid(String e_address) {
        // ensure email is not blank, empty, or null
        if (!stringIsValid(e_address)) {
            return false;
        }
        // email address regular expression
        String regex = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        // ensure email matches regex
        if (!e_address.matches(regex)) {
            return false;
        }
        // email is valid
        return true;
    }

    /**
     * check if both first name and last name are valid: starts with capital letter
     * and contains alphabet characters only
     * 
     * @param name name to be checked
     * @return name validity state
     */
    public boolean nameIsValid(String name) {
        // ensure name is not blank, empty, or null
        if (!stringIsValid(name)) {
            return false;
        }
        return true;
    }

    /**
     * check if a string is valid: not null, empty, or blank
     * 
     * @param string string being tested
     * @return string validity state
     */
    public boolean stringIsValid(String string) {
        // ensure name is not null, empty, or blank
        if (StringUtils.isBlank(string)) {
            return false;
        }
        return true;
    }

    /**
     * check if event title is valid: contains alpha-numeric (and blank) characters
     * only
     * 
     * @param title title to be checked
     * @return title validity state
     */
    public boolean eventTitleIsValid(String title) {
        // ensure data is not null, blank, or empty
        if (!stringIsValid(title)) {
            return false;
        }
        // title regular expression
        String regex = "^(\\w+)( \\w+)*";
        // ensure title matches with the regular expression
        if (!title.matches(regex)) {
            return false;
        }
        // event title is valid
        return true;
    }

    /**
     * Check if event description is valid: contains all characters exclude '\r\n'
     * 
     * @param description description to be checked
     * @return description validity state
     */
    public boolean eventDescriptionIsValid(String description) {
        // ensure event description is not null, empty, or blank
        if (!stringIsValid(description)) {
            return false;
        }
        // description regular expression
        String descRegex = "^.+( .)*";
        // ensure description matches the regular expression
        if (!description.matches(descRegex)) {
            return false;
        }
        // event description is valid
        return true;
    }

    /**
     * event type validity check; events have five possible types: 'lecture',
     * 'seminar', 'conference', 'workshop', 'other'
     * 
     * @param data type to be checked
     * @return type validity state
     */
    public boolean eventTypeIsValid(String type) {
        // ensure event type is not null
        if (!stringIsValid(type)) {
            return false;
        }
        // ensure type is valid (5 valid types)
        if ((type.equals("lecture") || type.equals("seminar") || type.equals("conference") || type.equals("workshop")
                || type.equals("other"))) {
            return true;
        }
        // doesn't match standard type -> is invalid
        return false;
    }

    /**
     * check for event validity; considers every event attribute
     * 
     * @param event Event instance to be checked
     * @return event validity state
     */
    public boolean isEventValid(Event event) {
        // ensure event is not null
        if (event == null) {
            return false;
        }
        // ensure event identifier is valid
        if (!idIsValid(event.getEventID())) {
            // System.out.println("Error: event id invalid");
            return false;
        }
        // ensure event author (host) ID is valid
        if (!idIsValid(event.getHostID())) {
            // System.out.println("Error: host id invalid");
            return false;
        }
        // ensure either template does not exist, or does exist and is valid
        Integer template_id = event.getTemplateID();
        if ((template_id != null) && (!idIsValid(template_id))) {
            // System.out.println("Error: template id invalid");
            return false;
        }
        // ensure event title is valid
        if (!eventTitleIsValid(event.getTitle())) {
            // System.out.println("Error: title invalid");
            return false;
        }
        // ensure event description is valid
        if (!eventDescriptionIsValid(event.getDescription())) {
            // System.out.println("Error: desc invalid");
            return false;
        }
        // ensure event type is valid
        if (!eventTypeIsValid(event.getType())) {
            // System.out.println("Error: type invalid");
            return false;
        }
        // ensure event code is valid
        if (!eventCodeIsValid(event.getEventCode())) {
            // System.out.println("Error: event code invalid");
            return false;
        }
        // event is valid if each attribute is valid
        return true;
    }

    /**
     * feedback instance validity check
     * 
     * @param feedback Feedback instance to be checked
     * @return feedback validity state
     */
    public boolean isFeedbackValid(Feedback feedback) {
        // ensure feedback object is not null
        if (feedback == null) {
            return false;
        }

        // validate fields: IDs:
        Integer feedback_id = feedback.getFeedbackID();
        if ((feedback_id != null) && (!idIsValid(feedback_id))) {
            return false;
        }
        if (!idIsValid(feedback.getEventID())) {
            // System.out.println("Error: event id invalid");
            return false;
        }
        if (!idIsValid(feedback.getParticipantID())) {
            // System.out.println("Error: part id invalid");
            return false;
        }
        // note: anonymous boolean already valid
        // validate timestamp
        if (feedback.getTimestamp() == null) {
            // System.out.println("Error: timestamp is null");
            return false;
        }

        // ensure arrays are not null, nor empty
        if (ArrayUtils.isEmpty(feedback.getTypes())) {
            // System.out.println("Error: main array:types is null or empty");
            return false;
        }
        if (ArrayUtils.isEmpty(feedback.getResults())) {
            // System.out.println("Error: main array:results is null or empty");
            return false;
        }
        if (ArrayUtils.isEmpty(feedback.getKeys())) {
            // System.out.println("Error: main array:keys is null or empty");
            return false;
        }
        if (ArrayUtils.isEmpty(feedback.getWeights())) {
            // System.out.println("Error: main array:weights is null or empty");
            return false;
        }

        // collect arrays from feedback instance
        String[] results = feedback.getResults();
        Float[] weights = feedback.getWeights();
        byte[] types = feedback.getTypes();
        Boolean[] keys = feedback.getKeys();
        byte[][] sub_weights = feedback.getSub_Weights();

        // ensure length of arrays: results, weights, types, and keys are all equal
        int array_length = results.length;
        if (weights.length != array_length) {
            // System.out.println("Error: weights array length diff");
            return false;
        }
        if (types.length != array_length) {
            // System.out.println("Error: types array length diff");
            return false;
        }
        if (keys.length != array_length) {
            // System.out.println("Error: keys array length diff");
            return false;
        }
        if (sub_weights.length != array_length) {
            // System.out.println("Error: sub weights array length diff");
            return false;
        }

        // get compound (sentiment field)
        Float compound = feedback.getCompound();
        // ensure compound score is either null, or falls between -1 and 1
        if ((compound != null) && (compound < -1 || compound > 1)) {
            // System.out.println("Error: compound invalid");
            return false;
        }

        // validate results array
        for (int i = 0; i < results.length; i++) {
            // if (StringUtils.isBlank(results[i])) {
            if (results[i] == null) {
                // System.out.println("Error: results invalid");
                return false;
            }
        }

        // validate weights array
        if (!isNormalisedWeightsValid(weights) && !isNonNormalisedWeightsValid(weights)) {
            // System.out.println("Error: weights array invalid");
            return false;
        }

        // validate types array
        for (int i = 0; i < types.length; i++) {
            if (types[i] > 2 || types[i] < 0) {
                // System.out.println("Error: types array invalid");
                return false;
            }
        }

        // ensure sub_weights array is valid
        // (each internal byte array is of length 5)
        for (byte[] sub_weight : sub_weights) {
            if (sub_weight.length != 5) {
                // System.out.println("Error: sub weights array invalid");
                return false;
            }
        }

        // feedback is valid if all fields are valid
        return true;
    }

    /**
     * check if a non-normalised weights array is valid; ensure every weight falls
     * within 0-7
     * 
     * @param weights weights array
     * @return valid state
     */
    private boolean isNonNormalisedWeightsValid(Float[] weights) {
        // weights already found to be not null, nor empty

        // ensure every weight is in-between 0 and 7 (closed interval)
        for (Float weight : weights) {
            // ensure no weight is null
            if (weight == null) {
                return false;
            }
            // ensure no weight is out of bounds
            if (weight < 0 || weight > 5) {
                return false;
            }
        }

        return true;
    }

    /**
     * check if a normalised weights array is valid; ensure every weight sums to 1.0
     * 
     * @param weights weights array
     * @return valid state
     */
    private boolean isNormalisedWeightsValid(Float[] weights) {
        // weights already found to be not null, nor empty

        // ensure every weight sums to 1.0
        float weight_sum = 0;
        for (Float weight : weights) {
            // ensure no weight is null
            if (weight == null) {
                return false;
            }
            weight_sum += weight;
        }

        // ensure every weight sums to 1.0 (account for loss of precision)
        return (weight_sum > 0.99 && weight_sum < 1.01);
    }

    /**
     * host instance validity check; host is valid if all of its fields are true
     * 
     * @param host Host instance to be checked
     * @return Host validity state
     */
    public boolean isHostValid(Host host) {
        // ensure host is not null
        if (host == null) {
            return false;
        }
        // ensure host ID is valid
        if (!idIsValid(host.getHostID())) {
            return false;
        }
        // ensure host code is valid
        if (!hostCodeIsValid(host.getHostCode())) {
            return false;
        }
        // ensure host email address is valid
        if (!eAddressIsValid(host.getEAddress())) {
            return false;
        }
        // ensure host first-name is valid
        if (!nameIsValid(host.getFName())) {
            return false;
        }
        // ensure host last-name is valid
        if (!nameIsValid(host.getLName())) {
            return false;
        }
        // host is valid if all of its fields are true
        return true;
    }

    /**
     * participant instance validity check
     * 
     * @param participant Participant instance to be checked
     * @return participant validity state
     */
    public boolean isParticipantValid(Participant participant) {
        // ensure participant is not null
        if (participant == null) {
            return false;
        }
        // ensure participant ID is valid
        if (!idIsValid(participant.getParticipantID())) {
            return false;
        }
        // ensure participant first name is valid
        if (!nameIsValid(participant.getFName())) {
            return false;
        }
        // ensure participant last name is valid
        if (!nameIsValid(participant.getLName())) {
            return false;
        }
        // participant is valid if all of its fields are valid
        return true;
    }

    /**
     * template validity check; template is valid when all of its fields are valid
     * 
     * @param template template
     * @return template validity state
     */
    public boolean isTemplateValid(Template template) {
        // ensure template is not null
        if (template == null) {
            return false;
        }
        // ensure IDs are valid
        if (!idIsValid(template.getTemplateID())) {
            return false;
        }
        if (!idIsValid(template.getHostID())) {
            return false;
        }
        // ensure template code is valid
        if (!templateCodeIsValid(template.getTemplateCode())) {
            return false;
        }
        // ensure template title/ name is valid
        if (!nameIsValid(template.getTemplateName())) {
            return false;
        }
        // ensure template timestamp is non-null
        if (template.getTimestamp() == null) {
            return false;
        }
        // ensure components ArrayList is not null
        if (template.getComponents() == null) {
            return false;
        }

        // validate each template component in template
        boolean allValid = true;
        for (TemplateComponent component : template.getComponents()) {
            allValid = (allValid && isComponentValid(component));
        }
        // ensure each component is valid
        if (!allValid) {
            return false;
        }

        // template is valid when all of its fields are valid
        return true;
    }

    /**
     * component validity check
     * 
     * @param component component checked
     * @return component validity state
     */
    public boolean isComponentValid(TemplateComponent component) {
        // ensure template component is not null
        if (component == null) {
            return false;
        }
        // ensure component ID is valid (if set)
        if (component.getId() != null && !idIsValid(component.getId())) {
            return false;
        }
        // ensure component name is valid (not null, empty, or blank)
        if (!stringIsValid(component.getName())) {
            return false;
        }
        // ensure component type is valid (is question, radio or checkbox)
        if (!componentTypeIsValid(component.getType())) {
            return false;
        }
        // ensure prompt is not null - when unfilled, should be empty string
        if (component.getPrompt() == null) {
            return false;
        }

        // get component type
        String componentType = component.getType();

        // case the component is of radio or checkbox type
        if (componentType == "radio" || componentType == "checkbox") {
            // options and options ans arrays should be populated
            if (component.getOptions() == null) {
                return false;
            }
            if (component.getOptionsAns() == null) {
                return false;
            }
            // text response should be null since it isn't necessary
            if (component.getTextResponse() != null) {
                return false;
            }
            // ensure options and options answer arrays are equal in length
            if (component.getOptions().length != component.getOptionsAns().length) {
                return false;
            }
        }
        // case the component is of type: question
        else if (componentType == "question") {
            // ensure options and options answer arrays are empty
            if (component.getOptions() != null) {
                return false;
            }
            if (component.getOptionsAns() != null) {
                return false;
            }
        }
        // component is valid
        return true;
    }

    /**
     * component type validity check
     * 
     * @param type component type
     * @return component type validity state
     */
    public boolean componentTypeIsValid(String type) {
        // ensure component type is not null
        if (!stringIsValid(type)) {
            return false;
        }
        // ensure type is either "question", "radio", or "checkbox"
        return (type.equals("question") || type.equals("radio") || type.equals("checkbox"));
    }

    /**
     * sanitize an event code to ensure it is: valid, and lower-case
     * 
     * @param eventCode event code to be sanitized
     * @return lower-case event code (null if invalid)
     */
    public String sanitizeEventCode(String eventCode) {
        // ensure event code is valid
        if (!eventCodeIsValid(eventCode)) {
            return null;
        }
        // return lowercase variant
        return eventCode.toLowerCase();
    }

    /**
     * sanitize a template code to ensure it is: valid, and lower-case
     * 
     * @param eventCode event code to be sanitized
     * @return lower-case template code (null if invalid)
     */
    public String sanitizeTemplateCode(String templateCode) {
        // ensure template code is valid
        if (!templateCodeIsValid(templateCode)) {
            return null;
        }
        // return lowercase variant
        return templateCode.toLowerCase();
    }

    /**
     * sanitize a host code to ensure it is: valid, and lower-case
     * 
     * @param hostCode host code to be sanitized
     * @return lower-case host code (null if invalid)
     */
    public String sanitizeHostCode(String hostCode) {
        // ensure host code is valid
        if (!hostCodeIsValid(hostCode)) {
            return null;
        }
        // return lowercase variant
        return hostCode.toLowerCase();
    }

    // Depreciated methods

    /**
     * archived-event validity check
     * 
     * archived events are not functional within the system
     * 
     * @param archivedEvent ArchivedEvent instance to be checked
     * @return archivedEvent validity state
     */
    // public boolean isArchivedEventValid(ArchivedEvent archivedEvent) {
    // // ensure archived event is not null
    // if (archivedEvent == null) {
    // return false;
    // }
    // // ensure event ID is valid
    // if (!idIsValid(archivedEvent.getEventID())){
    // return false;
    // }
    // // ensure event author (host) ID is valid
    // if (!idIsValid(archivedEvent.getHostID())){
    // return false;
    // }
    // // ensure event description is valid
    // if (!eventTypeIsValid(archivedEvent.getType())){
    // return false;
    // }
    // // ensure event description is valid
    // if (!eventDescriptionIsValid(archivedEvent.getDescription())){
    // return false;
    // }
    // // ensure event title is valid
    // if (!eventTitleIsValid(archivedEvent.getTitle())){
    // return false;
    // }
    // // archived event is valid if all of its fields are valid
    // return true;
    // }
}
