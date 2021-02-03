package app;

import org.apache.commons.lang3.StringUtils; // sanitizeEventCode

public class Validator {

    // public static void main(String[] args) {}

    public Validator(){
        // tests
        System.out.println("Validation tests: t, f, t, f");
        System.out.println(checkEventCode("CCCC"));
        System.out.println(checkEventCode("AAAAAA"));
        System.out.println(checkEventCode("33SA"));
        System.out.println(checkEventCode("[[[[]]]]"));
    }

    /**
     * Sanitize an event code to ensure it is:
     * non-null, 4-digits in length, alpha-numeric
     * @param eventCode event code to be sanitized
     * @return event code in upper case, null if fails
     */
    public static String sanitizeEventCode(String eventCode){
        if (eventCode == null)
            return null;
        if (eventCode.length() != 4)
            return null;
        if (!StringUtils.isAlphanumeric(eventCode.toUpperCase()))
            return null;
        return eventCode.toUpperCase();
    }

    /**
     * Assess if an eventCode meets requirements:
     * non-null, 4-digits in length, alpha-numeric
     * @param eventCode event code to be checked
     * @return event code validity state
     */
    public static boolean checkEventCode(String eventCode){
        return !(sanitizeEventCode(eventCode) == null);
    }
}
