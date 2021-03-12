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

    // TODO: comment
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
        if (data != null && !data.isEmpty()) {
            // title regular expression
            String regex = "^(\\w+)( \\w+)*";

            // return true if title matches with the regular expression
            if (data.matches(regex)) {
                return true;
            }
        }
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
     * Check if event type is valid: event only allows five types 'lecture',
     * 'seminar', 'conference', 'workshop', 'other'
     * 
     * @param data type to be checked
     * @return type validity state
     */
    public boolean eventTypeIsValid(String data) {
        if (data != null && (data.equals("lecture") || data.equals("seminar") || data.equals("conference")
                || data.equals("workshop") || data.equals("other"))) {
            return true;
        }
        return false;
    }

    // // TODO; LATER IN DEV
    public boolean sentimentIsValid(String data) {
        return false;
    }

    /**
     * Check if ArchivedEvent is valid: check every data inside
     * 
     * @param archivedEvent ArchivedEvent instance to be checked
     * @return ArchivedEvent validity state
     */
    public boolean isArchivedEventValid(ArchivedEvent archivedEvent) {
        if (archivedEvent != null && eventTitleIsValid(archivedEvent.getTitle())
                && eventDescriptionIsValid(archivedEvent.getDescription())
                && eventDescriptionIsValid(archivedEvent.getType()) && idIsValid(archivedEvent.getEventID())
                && idIsValid(archivedEvent.getHostID())) {
            return true;
        }
        return false;
    }

    /**
     * Check if Event is valid: check every data inside
     * 
     * @param event Event instance to be checked
     * @return Event validity state
     */
    public boolean isEventValid(Event event) {
        if (event != null && eventTitleIsValid(event.getTitle()) && eventDescriptionIsValid(event.getDescription())
                && eventTypeIsValid(event.getType()) && eventCodeIsValid(event.getEventCode())
                && idIsValid(event.getEventID()) && (event.getTemplateID() == -1 || idIsValid(event.getTemplateID()))
                && idIsValid(event.getHostID())) {
            return true;
        }
        return false;
    }

    /**
     * TODO: validate more data TODO: Check all values in sub_weights are between 0
     * and 7 TODO: Check results, weights, types, keys are of the same length and
     * that length is at least 1 TODO: check sub_weights holds as many arrays as
     * results (and the other arrays) hold items - (sub weights has x rows) TODO:
     * check each array held in sub_weights is of size 5 - (sub weights has 5
     * columns) TODO: add a test that checks either all values in weights are int
     * between 0 and 7 or all values sum to 1.0 Check if Feedback is valid: check
     * every data inside
     * 
     * @param feedback Feedback instance to be checked
     * @return Feedback validity state
     */
    public boolean isFeedbackValid(Feedback feedback) {
        if (feedback == null)
            return false;

        // validate IDs:
        if (!idIsValid(feedback.getFeedbackID()))
            return false;
        if (!idIsValid(feedback.getEventID()))
            return false;
        if (!idIsValid(feedback.getParticipantID()))
            return false;

        // validate Sentiment fields

        // validates results array
        String[] results = feedback.getResults();
        if (ArrayUtils.isEmpty(results))
            return false;

        for (int i = 0; i < results.length; i++) {
            if (StringUtils.isBlank(results[i]))
                return false;
        }

        // checks type array
        byte[] types = feedback.getTypes();
        for (int i = 0; i < types.length; i++) {
            if (types[i] > 2 || types[i] < 0)
                return false;
        }

        // //checks weights array
        // Float[] weights = feedback.getWeights();
        // float total = 0;
        // for (int i = 0; i < weights.length; i++){
        // if (weights[i] > 7.0 || weights[i] < 0.0)
        // return false;
        // total += weights[i];
        // }

        // validate timestamp
        if (feedback.getTimestamp() == null)
            return false;

        return true;
    }

    /**
     * Check if Host is valid: check every data inside
     * 
     * @param host Host instance to be checked
     * @return Host validity state
     */
    public boolean isHostValid(Host host) {
        if (host != null && hostCodeIsValid(host.getHostCode()) && eAddressIsValid(host.getEAddress())
                && nameIsValid(host.getFName()) && nameIsValid(host.getLName()) && idIsValid(host.getHostID())) {
            return true;
        }
        return false;
    }

    /**
     * Check if Participant is valid: check every data inside
     * 
     * @param participant Participant instance to be checked
     * @return Participant validity state
     */
    public boolean isParticipantValid(Participant participant) {
        if (participant != null && nameIsValid(participant.getFName()) && nameIsValid(participant.getLName())
                && idIsValid(participant.getParticipantID())) {
            return true;
        }
        return false;
    }

    /**
     * null-safe template validity check: assessing each attribute
     * 
     * @param template template
     * @return template validity state
     */
    public boolean isTemplateValid(Template template) {
        // ensure template is not null
        if (template == null)
            return false;
        // ensure IDs are valid
        if (!idIsValid(template.getTemplateID()))
            return false;
        if (!idIsValid(template.getHostID()))
            return false;
        // ensure template code is valid
        if (!templateCodeIsValid(template.getTemplateCode()))
            return false;
        // ensure template title/ name is valid
        if (!nameIsValid(template.getTemplateName()))
            return false;
        // ensure template timestamp is non-null
        if (template.getTimestamp() == null)
            return false;
        // ensure components ArrayList is not null
        if (template.getComponents() == null)
            return false;

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
        if (component == null)
            return false;
        // ensure component ID is valid
        // if (!idIsValid(component.getId()))
        //     return false;
        if (component.getName() == null)
            return false;
        if (!componentTypeIsValid(component.getType()))
            return false;
        if (component.getPrompt() == null) {
            return false;
        }
        String componentType = component.getType();
        // case the component is of radio or checkbox type
        if (componentType == "radio" || componentType == "checkbox") {
            if (component.getOptions() == null)
                return false;
            if (component.getOptionsAns() == null)
                return false;
            if (component.getTextResponse() != null)
                return false;
            if (component.getOptions().length != component.getOptionsAns().length)
                return false;
        }
        // case the component is of question type
        if (componentType == "question") {
            if (component.getOptions() != null)
                return false;
            if (component.getOptionsAns() != null)
                return false;
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
