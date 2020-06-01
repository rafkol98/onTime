package com.example.ontime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    TextView name_txt;
    EditText name_in;
    String name_input;


    EditText sign_in_email, sign_in_password;
    TextView create_account_txt;
    Button sign_in;

    ValidateInput validateInput;

    String email, password;

    AlertDialog.Builder builder;
    AlertDialog dialog;

    DatabaseReference reff;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fixGoogleMapBug();

        create_account_txt = findViewById(R.id.sign_up_text);
        sign_in_email = findViewById(R.id.sign_in_email);
        sign_in_password = findViewById(R.id.sign_in_password);
        sign_in = findViewById(R.id.sign_in_button);

        validateInput = new ValidateInput(MainActivity.this, sign_in_email, sign_in_password);

        // Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();

        //check if user is already logged in.
        if(user != null) {
        //put intent to go to mainActivity
            Intent intent = new Intent(MainActivity.this, MPage.class);
            startActivity(intent);
            this.finish();
        } else {

            //AlertDialog Builder
            builder = new AlertDialog.Builder(this);

            create_account_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, SignUp.class);
                    startActivity(intent);
                }

            });


            sign_in.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signInAccount();
                }

            });
        }

    }

    public void signInAccount() {

        loadingAnimation();

        boolean emailVerified = validateInput.ValidateEmail();
        boolean passwordVerified = validateInput.ValidatePassword();

        if (emailVerified && passwordVerified) {
            email = sign_in_email.getText().toString().trim();
            password = sign_in_password.getText().toString().trim();


            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Intent intent = new Intent(MainActivity.this, MPage.class);
                                startActivity(intent);
                                dialog.dismiss();
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                dialog.dismiss();
                                Toast.makeText(MainActivity.this, "Password and Email mismatch.", Toast.LENGTH_SHORT).show();

                            }

                            // ...
                        }
                    });
        }
    }

    public void addListenerOnButton() {
        Intent myIntent = new Intent(MainActivity.this, Suggestion.class);
        startActivity(myIntent);
    }


    public void loadingAnimation() {
        LayoutInflater inflater = getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading, null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    private void fixGoogleMapBug() {
        SharedPreferences googleBug = getSharedPreferences("google_bug", Context.MODE_PRIVATE);
        if (!googleBug.contains("fixed")) {
            File corruptedZoomTables = new File(getFilesDir(), "ZoomTables.data");
            corruptedZoomTables.delete();
            googleBug.edit().putBoolean("fixed", true).apply();
        }
    }
}
