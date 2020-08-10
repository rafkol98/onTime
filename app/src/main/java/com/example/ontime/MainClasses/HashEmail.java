package com.example.ontime.MainClasses;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class is used to generate a hash key for the email of the user.
 */
public class HashEmail {

    //generate hash key for given email.
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getHashEmail(String email){
        String returnEmail = "";

        try{
        returnEmail =  toHexString(getSHA(email.toLowerCase()));

        } catch (Exception e){
            e.printStackTrace();
        }

        return returnEmail;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static byte[] getSHA(String input) throws NoSuchAlgorithmException
    {
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // digest() method called
        // to calculate message digest of an input
        // and return array of byte
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String toHexString(byte[] hash)
    {
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 32)
        {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }



}
