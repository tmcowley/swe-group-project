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
     * @param str string to be checked
     * @param len valid length of string
     * @return string validity state
     */
    public boolean alphanumericIsValid(String str, int len) {
        if (str == null)
            return false;
        if (str.length() != len)
            return false;
        if (!StringUtils.isAlphanumeric(str))
            // illegal character in string
            return false;
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
        if (StringUtils.isBlank(hostCode))
            // host code is null, empty or whitespace only
            return false;
        if (hostCode.length() > (peakWordSizeInList * 4 + 3))
            // host code exceeds upper-bound
            return false;
        if (StringUtils.containsWhitespace(hostCode))
            // host code contains illegal whitespace character
            return false;
        if (StringUtils.countMatches(hostCode, "-") != 3)
            // host code not of format: [word-word-word-word]
            return false;
        String[] hostCodeArray = hostCode.split("-");
        for (String word : hostCodeArray) {
            if (!wordListHashSet.contains(word.toLowerCase()))
                return false;
        }
        return true; // host code is valid
    }

    /**
     * Check if a given ID is valid no upper-bound check needed since:
     * upper-bound(java int) > upper-bound(SQLite int)
     * 
     * @param id ID to be checked
     * @return validity state of id
     */
    public boolean idIsValid(int id) {
        boolean isPositive = (id >= 0);
        return (isPositive);
    }

    /**
     * Check if email address is valid
     * 
     * @param e_address email address to be checked
     * @return email address validity state
     */
    public boolean eAddressIsValid(String e_address) {
        if (e_address != null && !e_address.isEmpty()) {
            // email address regular expression
            String regex = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";

            // return true if email matches with the regular expression
            if (e_address.matches(regex)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if both first name and last name are valid: starts with capital letter
     * and contains alphabet characters only
     * 
     * @param name name to be checked
     * @return name validity state
     */
    public boolean nameIsValid(String name) {
        // ensure name is a valid string
        if (!stringIsValid(name))
            return false;
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
        if (StringUtils.isBlank(string))
            return false;
        return true;
    }

    /**
     * Check if event title is valid: contains alpha-numeric (and blank) characters
     * only
     * 
     * @param data title to be checked
     * @return title validity state
     */
    public boolean eventTitleIsValid(String data) {

        // ensure data is not null, blank, or empty
        if (StringUtils.isBlank(data)) {
            return false;
        }

        // title regular expression
        String regex = "^(\\w+)( \\w+)*";

        // title matches with the regular expression
        if (data.matches(regex)) {
            return true;
        }

        // event title is invalid
        return false;
    }

    /**
     * Check if event description is valid: contains all characters exclude '\r\n'
     * 
     * @param data description to be checked
     * @return description validity state
     */
    public boolean eventDescriptionIsValid(String data) {
        // ensure event description is not null, empty, or blank
        if (StringUtils.isBlank(data)) {
            return false;
        }

        // description regular expression
        String descRegex = "^.+( .)*";

        // return true if description matches the regular expression
        if (data.matches(descRegex)) {
            return true;
        }

        return false;
    }

    /**
     * null-safe event type validity check; event only allows five types: 'lecture',
     * 'seminar', 'conference', 'workshop', 'other'
     * 
     * @param data type to be checked
     * @return type validity state
     */
    public boolean eventTypeIsValid(String data) {
        // ensure event type is not null
        if (data == null) {
            return false;
        }
        if ((data.equals("lecture") || data.equals("seminar") || data.equals("conference") || data.equals("workshop")
                || data.equals("other"))) {
            return true;
        }
        return false;
    }

    /**
     * null-safe archived-event validity check
     * 
     * @param archivedEvent ArchivedEvent instance to be checked
     * @return ArchivedEvent validity state
     */
    public boolean isArchivedEventValid(ArchivedEvent archivedEvent) {
        if (archivedEvent == null) {
            return false;
        }
        if (eventTitleIsValid(archivedEvent.getTitle()) && eventDescriptionIsValid(archivedEvent.getDescription())
                && eventDescriptionIsValid(archivedEvent.getType()) && idIsValid(archivedEvent.getEventID())
                && idIsValid(archivedEvent.getHostID())) {
            return true;
        }
        return false;
    }

    /**
     * null-safe check for event validity considers every attribute in event
     * 
     * @param event Event instance to be checked
     * @return Event validity state
     */
    public boolean isEventValid(Event event) {
        // ensure event is not null
        if (event == null) {
            return false;
        }
        // ensure event title is valid
        if (!eventTitleIsValid(event.getTitle())) {
            return false;
        }
        // ensure event description is valid
        if (!eventDescriptionIsValid(event.getDescription())) {
            return false;
        }
        // ensure event type is valid
        if (!eventTypeIsValid(event.getType())) {
            return false;
        }
        // ensure event code is valid
        if (!eventCodeIsValid(event.getEventCode())) {
            return false;
        }
        // ensure event identifier is valid
        if (!idIsValid(event.getEventID())) {
            return false;
        }
        // ensure event author (host) ID is valid
        if (!idIsValid(event.getHostID())) {
            return false;
        }
        if (event.getTemplateID() == -1 || idIsValid(event.getTemplateID())) {
            return true;
        }
        return false;
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
        if (!idIsValid(feedback.getFeedbackID())) {
            return false;
        }
        if (!idIsValid(feedback.getEventID())) {
            return false;
        }
        if (!idIsValid(feedback.getParticipantID())) {
            return false;
        }
        // note: anonymous boolean already valid
        // validate timestamp
        if (feedback.getTimestamp() == null) {
            return false;
        }

        // ensure arrays are not null, nor empty
        if (ArrayUtils.isEmpty(feedback.getTypes())) {
            System.out.println("Error: main array:types is null or empty");
            return false;
        }
        if (ArrayUtils.isEmpty(feedback.getResults())) {
            System.out.println("Error: main array:results is null or empty");
            return false;
        }
        if (ArrayUtils.isEmpty(feedback.getKeys())) {
            System.out.println("Error: main array:keys is null or empty");
            return false;
        }
        if (ArrayUtils.isEmpty(feedback.getWeights())) {
            System.out.println("Error: main array:weights is null or empty");
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
            return false;
        }
        if (types.length != array_length) {
            return false;
        }
        if (keys.length != array_length) {
            return false;
        }
        if (sub_weights.length != array_length){
            return false;
        }

        // get compound (sentiment field)
        Float compound = feedback.getCompound();
        // ensure compound score is either null, or falls between -1 and 1
        if ((compound != null) && (compound < -1 || compound > 1)){
            return false;
        }

        // validate results array
        for (int i = 0; i < results.length; i++) {
            if (StringUtils.isBlank(results[i])) {
                return false;
            }
        }

        // validate weights array
        if (!isNormalisedWeightsValid(weights) && !isNonNormalisedWeightsValid(weights)){
            return false;
        }

        // validate types array
        for (int i = 0; i < types.length; i++) {
            if (types[i] > 2 || types[i] < 0) {
                return false;
            }
        }

        // TODO: Check all values in sub_weights are between 0 and 7
        // validate sub-weights
        for (Float weight : weights) {
            // ensure no weight is null
            if (weight == null) {
                return false;
            }
            // ensure no weight is out of bounds
            if (weight < 0 || weight > 7) {
                return false;
            }
        }

        // ensure sub_weights array is valid
        // (each internal byte array is of length 5)
        for (byte[] sub_weight : sub_weights){
            if (sub_weight.length != 5){
                return false;
            }
        }

        // feedback is valid if all fields are valid
        return true;
    }

    /**
     * check if a non-normalised weights array is valid; 
     * ensure every weight falls within 0-7
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
            if (weight < 0 || weight > 7) {
                return false;
            }
        }

        return true;
    }

    /**
     * check if a normalised weights array is valid; 
     * ensure every weight sums to 1.0
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
     * host instance validity check
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
        // host is true if all of its fields are true
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
     * null-safe template validity check assessing each attribute, and all
     * components
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

        return allValid;
    }

    /**
     * null-safe component validity check
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
        if (component.getId() != null) {
            if (!idIsValid(component.getId()))
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
        // case the component is of question type
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
     * null-safe component type validity check
     * 
     * @param type component type
     * @return component type validity state
     */
    public boolean componentTypeIsValid(String type) {
        // ensure component type is not null
        if (type == null)
            return false;
        // ensure type is either "question", "radio", or "checkbox"
        return (type.equals("question") || type.equals("radio") || type.equals("checkbox"));
    }

    /**
     * Sanitize an event code to ensure it is: valid, and lower-case
     * 
     * @param eventCode event code to be sanitized
     * @return lower-case event code (null if invalid)
     */
    public String sanitizeEventCode(String eventCode) {
        if (!eventCodeIsValid(eventCode))
            return null;
        return eventCode.toLowerCase();
    }

    /**
     * Sanitize a template code to ensure it is: valid, and lower-case
     * 
     * @param eventCode event code to be sanitized
     * @return lower-case template code (null if invalid)
     */
    public String sanitizeTemplateCode(String templateCode) {
        if (!templateCodeIsValid(templateCode))
            return null;
        return templateCode.toLowerCase();
    }

    /**
     * Sanitize a host code to ensure it is: valid, and lower-case
     * 
     * @param hostCode host code to be sanitized
     * @return lower-case host code (null if invalid)
     */
    public String sanitizeHostCode(String hostCode) {
        if (!hostCodeIsValid(hostCode))
            return null;
        return hostCode.toLowerCase();
    }
}

// DEPRECIATED METHODS

/*
 * // Check if IP address is valid // @param ip_address IP address to be checked
 * // @return IP address validity state // public boolean
 * ipAddressIsValid(String ip_address){ if (StringUtils.isBlank(ip_address))
 * return false; // ip-address regular expression - source? ... String regex =
 * "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." +
 * "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
 * +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
 * "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$"; if (!ip_address.matches(regex))
 * return false; return true; }
 */

// /**
// * Check if the given host code is valid:
// * non-null, alpha-numeric (and space) characters only,
// * of the form [word word word word] with each word in word-list
// * @param hostCode host code to be checked
// * @return host code validity state
// */
// public boolean hostCodeIsValid(String hostCode){
// if (StringUtils.isBlank(hostCode))
// // host code is null, empty or whitespace only
// return false;
// if (hostCode.length() > (peakWordSizeInList*4 + 3))
// // host code exceeds upper-bound
// return false;
// if (StringUtils.countMatches(hostCode, " ") != 3)
// // host code not of format: [word word word word]
// return false;
// if (!StringUtils.isAlphaSpace(hostCode)){
// // illegal character in host code
// return false;
// }
// String[] hostCodeArray = hostCode.split(" ");
// for (String word : hostCodeArray){
// if (!wordListHashSet.contains(word))
// return false;
// }
// return true; // host code is valid
// }
