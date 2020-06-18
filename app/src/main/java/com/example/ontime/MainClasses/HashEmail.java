package com.example.ontime.MainClasses;

/**
 * This class is used to generate a hash key for the email of the user.
 */
public class HashEmail {

    //generate hash key for given email.
    public String getHashEmail(String email){
        int hash = 7;
        for (int i = 0; i < email.length(); i++) {
            hash = hash*17 + email.charAt(i);
        }

        String retValue = String.valueOf(hash);
        return retValue;
    }


}
