
/******************************************************************************
 * Copyright (C) 2019 Eric Pogue.
 * Licensed under the BSD-3-Clause.
 ******************************************************************************/

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ThunderbirdContact extends URLReader implements Runnable {

    private String firstName;
    public String getFirstName() { return firstName; }

    private String lastName;
    public String getLastName() { return lastName; }

    private String preferredName;

    public String getPreferredName() {
        return preferredName.length() == 0 ? firstName : preferredName;
    }

    private int seatLocation;
    public int getSeat() { return seatLocation; }

    private String authorName;
    private String authorEmail;

    private final Map<String, String> additionalFields;

    ThunderbirdContact(String urlIn) {
        this(urlIn, "", "");
    }

    ThunderbirdContact(String urlIn, String authorNameIn, String authorEmailIn) {

        super(urlIn);

        firstName = "";
        lastName = "";
        preferredName = "";
        seatLocation = 0;

        authorName = authorNameIn == null ? "" : authorNameIn;
        authorEmail = authorEmailIn == null ? "" : authorEmailIn;

        additionalFields = new LinkedHashMap<String, String>();

        // Todo: Add additional fields.
        // AM: Added preferredName and additional contact fields.
    }

    public Boolean Load() {

        Boolean returnValue = false;

        System.out.println("Loading: " + requestURL);

        if (super.readURL()) {
            Parse();
            returnValue = true;
        }

        return returnValue;
    }

    public void Parse() {

        Pattern fieldPattern = Pattern.compile(
            "\\\"([^\\\"]+)\\\"\\s*:\\s*(?:\\\"((?:\\\\\\.|[^\\\"])*)\\\"|(-?\\d+)|(true|false|null))"
        );

        for (String s : urlContent) {

            Matcher matcher = fieldPattern.matcher(s);

            while (matcher.find()) {

                String key = matcher.group(1);
                String value = matcher.group(2);

                if (value == null) value = matcher.group(3);
                if (value == null) value = matcher.group(4);
                if (value == null || value.equals("null")) value = "";

                value = value.replace("\\\"", "\"")
                             .replace("\\n", "\n")
                             .replace("\\r", "\r")
                             .replace("\\t", "\t")
                             .replace("\\\\", "\\");

                if (key.equals("firstName")) {
                    firstName = value;
                }
                else if (key.equals("lastName")) {
                    lastName = value;
                }
                else if (key.equals("preferredName")) {
                    preferredName = value;
                }
                else if (key.equals("seatLocation")) {
                    try {
                        seatLocation = value.length() == 0
                            ? 0 : Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        seatLocation = 0;
                    }
                }
                else {
                    additionalFields.put(key, value);
                }
            }
        }

        // Todo: Parse for additional fields.
        // AM: Parse preferredName and store additional JSON properties.
    }

    public void Validate() {

        if (urlContent.size() < 1) {
            System.out.println("Validating: " + requestURL);
            System.out.println("    **Failed**: No content loaded\n");
            return;
        }

        // Todo: Add author's name and email address to failed messages.
        // AM: Added author name and email to validation failure messages.

        String author = authorName;
        String email = authorEmail;

        if (author.length() == 0) author = "Not provided";
        if (email.length() == 0) email = "Not provided";

        String authorDetails =
            "\n    Author: " + author + " (" + email + ")";

        if (firstName.trim().length() == 0) {

            System.out.println("Validating: " + requestURL);
            System.out.println("    **Failed**: First Name required"
                + authorDetails);
            System.out.println(this);

        } else if (lastName.trim().length() == 0) {

            System.out.println("Validating: " + requestURL);
            System.out.println("    **Failed**: Last Name required"
                + authorDetails);
            System.out.println(this);

        } else if (lastName.trim().length() < 2) {

            System.out.println("Validating: " + requestURL);
            System.out.println("    **Failed**: Last Name must contain at least two characters"
                + authorDetails);
            System.out.println(this);

        } else {

            System.out.println("Validating: " + requestURL + "... Passed!");
        }
    }

    public String toString() {

        // Todo: Add additional fields to returnString.
        // AM: Added preferred name and additional contact information.

        String returnString = "firstName: " + firstName + "\n";
        returnString += "lastName: " + lastName + "\n";
        returnString += "preferredName: " + preferredName + "\n";
        returnString += "seatNumber: " + seatLocation + "\n";

        for (Map.Entry<String, String> entry : additionalFields.entrySet()) {
            returnString += entry.getKey() + ": " + entry.getValue() + "\n";
        }

        returnString += super.toString();

        return returnString;
    }

    public void run() {
        Load();
    }
}
