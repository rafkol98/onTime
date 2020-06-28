package com.example.ontime.SignIn_UpClasses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.*;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;

import static com.example.ontime.PermissionConstants.BACKGROUND;
import static com.example.ontime.PermissionConstants.PERMISSIONS;
import static com.example.ontime.PermissionConstants.PERMISSION_ALL;
import static com.example.ontime.PermissionConstants.PERMISSION_BACKGROUND;

/**
 *
 */
public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {


    //Initialise variables.
    EditText sign_in_email, sign_in_password;
    TextView create_account_txt;
    Button sign_in;

    ValidateInput validateInput;

    String email, password;

    AlertDialog.Builder builder;
    AlertDialog dialog;

    private View mLayout;

    private FirebaseAuth mAuth;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLayout = findViewById(R.id.main_layout);

        setPersistence();
        fixGoogleMapBug();

        create_account_txt = findViewById(R.id.sign_up_text);
        sign_in_email = findViewById(R.id.sign_in_email);
        sign_in_password = findViewById(R.id.sign_in_password);
        sign_in = findViewById(R.id.sign_in_button);

        validateInput = new ValidateInput(MainActivity.this, sign_in_email, sign_in_password);

        // Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();

        //TODO: if user's average speed does not exist( if the user created his account online), take him to do the test as soon as he logs in.

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
        if(!hasPermissions(this, PERMISSIONS)) {
            requestRequiredPermissions();
        }
    }

    /**
     *
     */
    private void setPersistence() {
        // Get instance of the Firebase database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        // Try to set persistence enabled on the database
        try {
            firebaseDatabase.setPersistenceEnabled(true);
        } catch (RuntimeException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    private void requestRequiredPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)){
            Snackbar.make(mLayout, R.string.location_access_required,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, PERMISSION_ALL);
                }
            }).show();
        } else {
            Snackbar.make(mLayout, R.string.location_unavailable, Snackbar.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    private void requestBackgroundPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION)){
            Snackbar.make(mLayout, R.string.background_access_required,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(MainActivity.this, BACKGROUND, PERMISSION_BACKGROUND);
                }
            }).show();
        } else {
            Snackbar.make(mLayout, R.string.background_location_unavailable, Snackbar.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(MainActivity.this, BACKGROUND, PERMISSION_BACKGROUND);
        }
    }
    /**
     * Sign in to you account using firbase database authentication.
     */
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ALL:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(mLayout, R.string.location_permission_granted,
                            Snackbar.LENGTH_SHORT).show();
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        // Determine if permissions should be requested at Runtime
                        if(!hasPermissions(this, BACKGROUND)) {
                            requestBackgroundPermission();
                        }
                    }
                }  else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    Snackbar.make(mLayout, R.string.location_permission_denied,
                            Snackbar.LENGTH_SHORT).show();
                }
                return;

            case PERMISSION_BACKGROUND:
                if (grantResults.length == 1 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(mLayout, R.string.background_location_permission_granted,
                            Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(mLayout, R.string.background_location_permission_denied,
                            Snackbar.LENGTH_SHORT).show();
                }

        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }


    /**
     * Generate a Loading Animation.
     */
    public void loadingAnimation() {
        LayoutInflater inflater = getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading, null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    /**
     * There was a googleMap bug that affected every device using the google maps. This fixes that
     * bug.
     */
    private void fixGoogleMapBug() {
        SharedPreferences googleBug = getSharedPreferences("google_bug", Context.MODE_PRIVATE);
        if (!googleBug.contains("fixed")) {
            File corruptedZoomTables = new File(getFilesDir(), "ZoomTables.data");
            corruptedZoomTables.delete();
            googleBug.edit().putBoolean("fixed", true).apply();
        }
    }

    /**
     *
     * @param context
     * @param permissions
     * @return
     */
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
