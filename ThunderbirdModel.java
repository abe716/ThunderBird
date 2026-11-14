
/******************************************************************************
 * Copyright (C) 2019 Eric Pogue.
 * Licensed under the BSD-3-Clause.
 ******************************************************************************/

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ThunderbirdModel extends URLReader {

    // Todo: Add the URL of the index file for your class here.
    // AM: Added the assignment's Thunderbird index URL.

    private String indexURL =
        "https://thunderbird-index.azurewebsites.net/w0a6zk195d.json";

    private ArrayList<ThunderbirdContact> contactList;

    ThunderbirdModel() {
        contactList = new ArrayList<ThunderbirdContact>();
    }

    public Boolean LoadIndex() {

        Boolean returnValue = false;
        contactList.clear();

        if (readURL(indexURL)) {

            Pattern urlPattern = Pattern.compile(
                "\\\"ContactURL\\\"\\s*:\\s*\\\"([^\\\"]+)\\\""
            );

            for (String line : urlContent) {

                Matcher matcher = urlPattern.matcher(line);

                while (matcher.find()) {

                    String authorName = extractJsonString(line, "Name");
                    String authorEmail = extractJsonString(line, "Email");

                    contactList.add(
                        new ThunderbirdContact(
                            matcher.group(1),
                            authorName,
                            authorEmail
                        )
                    );
                }
            }
        }

        // Todo: Verify at least one contact was added.
        // AM: Return true only when at least one contact URL exists.

        returnValue = !contactList.isEmpty();

        return returnValue;
    }

    private String extractJsonString(String jsonLine, String key) {

        Pattern pattern = Pattern.compile(
            "\\\"" + Pattern.quote(key) + "\\\"\\s*:\\s*\\\"([^\\\"]*)\\\""
        );

        Matcher matcher = pattern.matcher(jsonLine);

        return matcher.find() ? matcher.group(1) : "";
    }

    public void LoadContacts() {

        long start = System.currentTimeMillis();
        int contactsLoaded = 0;

        for (ThunderbirdContact contact : contactList) {
            if (contact.Load()) {
                contactsLoaded++;
            }
        }

        System.out.println("Contacts Loaded: " + contactsLoaded);

        System.out.println(
            "LoadContacts Elapsed Time: " +
            (System.currentTimeMillis() - start) + " ms\n"
        );
    }

    public void LoadContactsThreaded() {

        long start = System.currentTimeMillis();

        ArrayList<Thread> threadList = new ArrayList<Thread>();

        for (ThunderbirdContact contact : contactList) {
            threadList.add(new Thread(contact));
        }

        for (Thread thread : threadList) {
            thread.start();
        }

        try {
            for (Thread thread : threadList) {
                thread.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Loading interrupted: " + e.getMessage());
        }

        System.out.println("Contacts Loaded: " + contactList.size());

        System.out.println(
            "LoadContacts Elapsed Time = " +
            (System.currentTimeMillis() - start) + " ms\n"
        );
    }

    public void ValidateContacts() {

        long start = System.currentTimeMillis();

        System.out.println("Validating Contacts:");

        for (ThunderbirdContact contact : contactList) {
            contact.Validate();
        }

        System.out.println(
            "ValidateContacts Elapsed Time = " +
            (System.currentTimeMillis() - start) + " ms\n"
        );
    }

    public String toString() {

        String returnString = "Index:\n" + super.toString();

        returnString += "\nPrinting Contacts:\n";

        for (ThunderbirdContact contact : contactList) {
            returnString += contact + "\n";
        }

        return returnString;
    }

    public ThunderbirdContact findContactInSeat(int seat) {

        for (ThunderbirdContact contact : contactList) {
            if (contact.getSeat() == seat) {
                return contact;
            }
        }

        return null;
    }

    public int getContactCount() {
        return contactList.size();
    }
}
