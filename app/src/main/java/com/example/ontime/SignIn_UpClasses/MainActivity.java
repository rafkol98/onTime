package com.example.ontime.SignIn_UpClasses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ontime.MainClasses.MPage;
import com.example.ontime.R;
import com.example.ontime.AutoSuggestClasses.Suggestion;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.database.DatabaseReference;

import java.io.File;

public class MainActivity extends AppCompatActivity {


    //Initialise variables.
    EditText sign_in_email, sign_in_password;
    TextView create_account_txt;
    Button sign_in;

    ValidateInput validateInput;

    String email, password;

    AlertDialog.Builder builder;
    AlertDialog dialog;


    private FirebaseAuth mAuth;

    private final String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION};

    @SuppressLint("InlinedApi")
    private final String[] PERMISSIONS_Q = {Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION,
                                            Manifest.permission.ACCESS_BACKGROUND_LOCATION};

    static final int PERMISSION_ALL = 134;

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

        // Determine if permissions should be requested at Runtime
        if (Build.VERSION.SDK_INT >= 23) {
            // If prior to Android Q request normal permissions else request background location
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
                if(!hasPermissions(this, PERMISSIONS)){
                    ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
                }
            } else {
                if(!hasPermissions(this, PERMISSIONS_Q)){
                    ActivityCompat.requestPermissions(this, PERMISSIONS_Q, PERMISSION_ALL);
                }
            }
        }
    }

    //Sign in to you account using firbase database authentication.
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
                        }
                    });
        }
    }


    //Generate a Loading Animation.
    public void loadingAnimation() {
        LayoutInflater inflater = getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading, null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    //There was a googleMap bug that affected every device using the google maps. This fixes that bug.
    private void fixGoogleMapBug() {
        SharedPreferences googleBug = getSharedPreferences("google_bug", Context.MODE_PRIVATE);
        if (!googleBug.contains("fixed")) {
            File corruptedZoomTables = new File(getFilesDir(), "ZoomTables.data");
            corruptedZoomTables.delete();
            googleBug.edit().putBoolean("fixed", true).apply();
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
