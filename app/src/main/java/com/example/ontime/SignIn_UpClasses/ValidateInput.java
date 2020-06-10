package com.example.ontime.SignIn_UpClasses;

import android.content.Context;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Class used to validate input when signing up.
 */
public class ValidateInput {

    Context context;
    EditText name, email, password, repeatPassword;

    String nameInput, emailInput, passwordInput, repeatPasswordInput;

    /**
     *
     * @param myContext
     * @param myEmail
     * @param myPassword
     */
    ValidateInput(Context myContext, EditText myEmail, EditText myPassword){
         context = myContext;
         email = myEmail;
         password = myPassword;

    }

    /**
     *
     * @param myContext
     * @param myName
     * @param myEmail
     * @param myPassword
     * @param myRepeatPassword
     */
    ValidateInput(Context myContext, EditText myName,EditText myEmail, EditText myPassword, EditText myRepeatPassword){
        context = myContext;
        name = myName;
        email = myEmail;
        password = myPassword;
        repeatPassword = myRepeatPassword;

    }

    /**
     *
     * @return
     */
    boolean ValidateEmail(){
        emailInput = email.getText().toString().trim();

        if(emailInput.isEmpty()){
            Toast.makeText(context,"Please enter your Email Address",Toast.LENGTH_SHORT).show();
            return false;
        } else if(!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
            Toast.makeText(context,"Invalid Email Address.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    /**
     *
     * @return
     */
    boolean ValidatePassword(){
        passwordInput = password.getText().toString().trim();

        if (passwordInput.isEmpty()){
            Toast.makeText(context,"Please enter your Password.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (passwordInput.length() < 6){
            Toast.makeText(context,"Password too Short. (more than 6 characters)", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }

    }

    /**
     *
     * @return
     */
    boolean repeatPasswordValidation(){
        nameInput= name.getText().toString().trim();
        repeatPasswordInput = repeatPassword.getText().toString().trim();

        if(repeatPasswordInput.isEmpty() || nameInput.isEmpty()){
            Toast.makeText(context,"Fill out all the fields",Toast.LENGTH_SHORT).show();
            return false;
        }else if(!repeatPasswordInput.equals(passwordInput)){
            Toast.makeText(context,"Password's do not match.", Toast.LENGTH_SHORT).show();
            return false;
        } else{
            return true;
        }
    }

}
