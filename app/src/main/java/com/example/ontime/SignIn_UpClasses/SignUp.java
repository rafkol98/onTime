package com.example.ontime.SignIn_UpClasses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ontime.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Enables the user to Sign Up.
 */
public class SignUp extends AppCompatActivity {

    EditText name_in, sign_up_email, sign_up_password, repeat_password;
    Button sign_up_btn;

    String email, password;

    ValidateInput validateInput;

    AlertDialog.Builder builder;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);

        sign_up_btn = findViewById(R.id.sign_up_btn);
        name_in = findViewById(R.id.name_in);
        sign_up_email = findViewById(R.id.sign_up_email);
        sign_up_password = findViewById(R.id.sign_up_password);
        repeat_password = findViewById(R.id.sign_up_password_repeat);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //AlertDialog Builder
        builder = new AlertDialog.Builder(this);

        validateInput = new ValidateInput(
                SignUp.this, name_in, sign_up_email, sign_up_password, repeat_password
        );

        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, Welcome.class);
                startActivity(intent);
            }

        });

        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpNewAccount();
            }
        });
    }


    public void signUpNewAccount() {

        loadingAnimation();

        boolean emailVerified = validateInput.ValidateEmail();
        boolean passwordVerified = validateInput.ValidatePassword();
        boolean repeatPasswordVerified = validateInput.repeatPasswordValidation();

        if (emailVerified && passwordVerified && repeatPasswordVerified) {

            email = sign_up_email.getText().toString().trim();
            password = sign_up_password.getText().toString().trim();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent intent = new Intent(SignUp.this, Welcome.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(SignUp.this, "Fatal Error", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });


        }
    }

    public void loadingAnimation(){
        LayoutInflater inflater = getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading, null));
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
