package com.example.ontime.SignIn_UpClasses;

import android.content.Context;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

public class ValidateInput {

    Context context;
    EditText name, email, password, repeatPassword;

    String nameInput, emailInput, passwordInput, repeatPasswordInput;

    ValidateInput(Context myContext, EditText myEmail, EditText myPassword){
         context = myContext;
         email = myEmail;
         password = myPassword;

    }
    ValidateInput(Context myContext, EditText myName,EditText myEmail, EditText myPassword, EditText myRepeatPassword){
        context = myContext;
        name = myName;
        email = myEmail;
        password = myPassword;
        repeatPassword = myRepeatPassword;

    }


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
