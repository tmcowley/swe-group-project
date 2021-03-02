package app;

// IO for word-list import
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;

import java.util.HashSet; // import the HashSet class
import org.apache.commons.lang3.StringUtils; // for event code sanitization

public class Validator {

    // public void main(String[] args) {}

    HashSet<String> wordListHashSet = new HashSet<String>(1000);
    int peakWordSizeInList = 11;

    public Validator(){
        // store word list (hash-set for O(1) lookup)
        getWordList();
    }

    /**
     * Store the host code wordList in a hash-set DS
     * Lookup (w/ contains()) is O(1): constant time
     */
    private void getWordList(){
        try{
            BufferedReader readIn = new BufferedReader(new FileReader("resources/wordList.txt"));
            String str;
            while((str = readIn.readLine()) != null){
                wordListHashSet.add(str);
            }
            readIn.close();
        } catch (IOException ex){
            // ensure file: wordList.txt is in /app/resources/
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Check if the given event code is valid:
     * non-null, 4-digits in length, alpha-numeric
     * @param eventCode event code to be checked
     * @return event code validity state
     */
    public boolean eventCodeIsValid(String eventCode){
        return alphanumericIsValid(eventCode, 4);
    }

    /**
     * Check if the given template code is valid:
     * non-null, 6-digits in length, alpha-numeric
     * @param templateCode template code to be checked
     * @return template code validity state
     */
    public boolean templateCodeIsValid(String templateCode){
        return alphanumericIsValid(templateCode, 6);
    }

    /**
     * Check if the given string is valid:
     * non-null, len-digits in length, alpha-numeric
     * @param str string to be checked
     * @param len valid length of string
     * @return string validity state
     */
    public boolean alphanumericIsValid(String str, int len){
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
     * Check if the given host code is valid:
     * non-null, alpha-numeric (and dash) characters only, 
     * of the form [word-word-word-word] with each word in wordList
     * @param hostCode host code to be checked
     * @return host code validity state
     */
    public boolean hostCodeIsValid(String hostCode){
        if (StringUtils.isBlank(hostCode))
            // host code is null, empty or whitespace only
            return false;
        if (hostCode.length() > (peakWordSizeInList*4 + 3))
            // host code exceeds upper-bound
            return false;
        if (StringUtils.containsWhitespace(hostCode))
            // host code contains illegal whitespace character
            return false;
        if (StringUtils.countMatches(hostCode, "-") != 3)
            // host code not of format: [word-word-word-word]
            return false;
        String[] hostCodeArray = hostCode.split("-");
        for (String word : hostCodeArray){
            if (!wordListHashSet.contains(word.toLowerCase()))
                return false;
        }
        return true; // host code is valid
    }

    /**
     * Check if IP address is valid
     * @param ip_address IP address to be checked
     * @return IP address validity state
     */
    public boolean ipAddressIsValid(String ip_address){
        if (ip_address != null && !ip_address.isEmpty()) {
			// ip address regular expression
			String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";

            // return true if ip matches with the regular expression
            if (ip_address.matches(regex)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if email address is valid
     * @param e_address email address to be checked
     * @return email address validity state
     */
    public boolean eAddressIsValid(String e_address){
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
     * Check if both first name and last name are valid:
     * starts with capital letter and contains alphabet characters only
     * @param name name to be checked
     * @return name validity state
     */
    public boolean nameIsValid(String name){
        if (name != null && !name.isEmpty()) {
			// first name regular expression
			String regex = "^[A-Z][a-z]+$";

            // return true if name matches with the regular expression
            if (name.matches(regex)) {
                return true;
            }
        }
        return false;
    }

    // // TODO; LATER IN DEV
    // public boolean templateDataIsValid(String data){
    //     return false;
    // }

    /**
     * Check if event title is valid:
     * contains alpha-numeric (and blank) characters only
     * @param data title to be checked
     * @return title validity state
     */
    public boolean eventTitleIsValid(String data){
        if (data != null && !data.isEmpty()) {
			// title regular expression
			String regex = "^\\w+( \\w+)*";

            // return true if title matches with the regular expression
            if (data.matches(regex)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if event description is valid:
     * contains all characters exclude '\r\n'
     * @param data description to be checked
     * @return description validity state
     */
    public boolean eventDescriptionIsValid(String data){
        if (data != null && !data.isEmpty()) {
			// description regular expression
			String regex = "^.+( .)*";

            // return true if description matches with the regular expression
            if (data.matches(regex)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if event type is valid:
     * event only allows five types 'lecture', 'seminar', 'conference', 'workshop', 'other'
     * @param data type to be checked
     * @return type validity state
     */
    public boolean eventTypeIsValid(String data){
        if (data.equals("lecture")||
            data.equals("seminar")||
            data.equals("conference")||
            data.equals("workshop")||
            data.equals("other")) {
            return true;
        }
        return false;
    }

    // // TODO; LATER IN DEV
    // public boolean sentimentIsValid(String data){
    //     return false;
    // }

    // TODO (*6): FOR EACH Object IN OBJECTS, create method isObjectValid


    /**
     * Sanitize an event code to ensure it is:
     * valid, and lower-case
     * @param eventCode event code to be sanitized
     * @return lower-case event code (null if invalid)
     */
    public String sanitizeEventCode(String eventCode){
        if (!eventCodeIsValid(eventCode))
            return null;
        return eventCode.toLowerCase();
    }

    /**
     * Sanitize a template code to ensure it is:
     * valid, and lower-case
     * @param eventCode event code to be sanitized
     * @return lower-case template code (null if invalid)
     */
    public String sanitizeTemplateCode(String templateCode){
        if (!templateCodeIsValid(templateCode))
            return null;
        return templateCode.toLowerCase();
    }

    /**
     * Sanitize a host code to ensure it is:
     * valid, and lower-case
     * @param hostCode host code to be sanitized
     * @return lower-case host code (null if invalid)
     */
    public String sanitizeHostCode(String hostCode){
        if (!hostCodeIsValid(hostCode))
            return null;
        return hostCode.toLowerCase();
    }
}

// DEPRECIATED METHODS

// /**
//  * Check if the given host code is valid:
//  * non-null, alpha-numeric (and space) characters only, 
//  * of the form [word word word word] with each word in wordList
//  * @param hostCode host code to be checked
//  * @return host code validity state
//  */
// public boolean hostCodeIsValid(String hostCode){
//     if (StringUtils.isBlank(hostCode))
//         // host code is null, empty or whitespace only
//         return false;
//     if (hostCode.length() > (peakWordSizeInList*4 + 3))
//         // host code exceeds upper-bound
//         return false;
//     if (StringUtils.countMatches(hostCode, " ") != 3)
//         // host code not of format: [word word word word]
//         return false;
//     if (!StringUtils.isAlphaSpace(hostCode)){
//         // illegal character in host code
//         return false;
//     }
//     String[] hostCodeArray = hostCode.split(" ");
//     for (String word : hostCodeArray){
//         if (!wordListHashSet.contains(word))
//             return false;
//     }
//     return true; // host code is valid
// }
