package app;

// IO for word-list import
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;

import java.util.HashSet; // import the HashSet class
import org.apache.commons.lang3.StringUtils; // for event code sanitization

public class Validator {

    // public void main(String[] args) {}

    HashSet<String> wordlistHashSet = new HashSet<String>(1000);
    int peakWordSizeInList = 11;

    public Validator(){
        // store word list (hash-set for O(1) lookup)
        getWordlist();

        // run tests (MOVE TO UTs IN /TEST)
        //runTests();
    }

    private void runTests(){
        System.out.println("Validation tests: t, f, t, f");
        System.out.println(eventCodeIsValid("CCCC"));
        System.out.println(eventCodeIsValid("AAAAAA"));
        System.out.println(eventCodeIsValid("33SA"));
        System.out.println(eventCodeIsValid("[[[[]]]]"));
        System.out.println(hostCodeIsValid("fish bird brother map"));
        System.out.println(hostCodeIsValid("map bird brother asdfasdf"));
        System.out.println("bird in wordlist: "+wordlistHashSet.contains("bird"));
    }

    /**
     * Store the host code wordlist in a hash-set DS
     * Lookup (w/ contains()) is O(1): constant time
     */
    private void getWordlist(){
        try{
            BufferedReader in = new BufferedReader(new FileReader("resources/wordlist.txt"));
            String str;
            while((str = in.readLine()) != null){
                wordlistHashSet.add(str);
            }
        } catch (IOException ex){
            // ensure file: wordlist.txt is in /app/resources/
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
     * non-null, alpha-numeric (and space) characters only, 
     * of the form [word word word word] with each word in wordlist
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

        if (StringUtils.countMatches(hostCode, " ") != 3)
            // host code not of format: [word word word word]
            return false;

        if (!StringUtils.isAlphaSpace(hostCode)){
            // illegal character in host code
            return false;
        }

        String[] hostCodeArray = hostCode.split(" ");
        for (String word : hostCodeArray){
            if (!wordlistHashSet.contains(word))
                return false;
        }

        return true; // host code is valid
    }


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
