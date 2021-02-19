package app;

import java.io.*;

import java.util.HashSet; // import the HashSet class
import org.apache.commons.lang3.StringUtils; // for event code sanitization

public class Validator {

    // public static void main(String[] args) {}

    HashSet<String> wordlistMap = new HashSet<String>(1000);
    // ArrayList<String> wordlistList = new ArrayList<String>();
    int peakWordSizeInList = 11;

    public Validator(){
        // store the word list in a hash-set for O(1) lookup
        getWordlist();

        // tests
        // System.out.println("Validation tests: t, f, t, f");
        // System.out.println(eventCodeIsValid("CCCC"));
        // System.out.println(eventCodeIsValid("AAAAAA"));
        // System.out.println(eventCodeIsValid("33SA"));
        // System.out.println(eventCodeIsValid("[[[[]]]]"));

        // System.out.println(hostCodeIsValid("fish bird brother map"));
        // System.out.println(hostCodeIsValid("map bird brother asdfasdf"));

        // System.out.println("bird in wordlist: "+wordlistMap.contains("bird"));

    }

    private void getWordlist(){
        try{
            BufferedReader in = new BufferedReader(new FileReader("resources/wordlist.txt"));
            String str;
            while((str = in.readLine()) != null){
                wordlistMap.add(str);
            }
        } catch (IOException ex){
            // ensure file: wordlist.txt is in /app/resources/
            System.out.println(ex.getMessage());
        }
    }


    /**
     * Sanitize an event code to ensure it is:
     * non-null, 4-digits in length, alpha-numeric
     * @param eventCode event code to be sanitized
     * @return event code in upper case, null if fails
     */
    public static String sanitizeEventCode(String eventCode){
        if (!eventCodeIsValid(eventCode))
            return null;
        return eventCode.toUpperCase();
    }

    /**
     * Assess if an eventCode meets requirements:
     * non-null, 4-digits in length, alpha-numeric
     * @param eventCode event code to be checked
     * @return event code validity state
     */
    public static boolean eventCodeIsValid(String eventCode){
        if (eventCode == null)
            return false;
        if (eventCode.length() != 4)
            return false;
        if (!StringUtils.isAlphanumeric(eventCode.toUpperCase()))
            // string contains illegal character
            return false;
        return true;
    }


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
            if (! wordlistMap.contains(word))
                return false;
        }

        return true;
    }


}
