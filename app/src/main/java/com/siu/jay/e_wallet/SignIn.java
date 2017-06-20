package com.siu.jay.e_wallet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/* COMP4521 Siu Chit 20270807 csiuab@connect.ust.hk
   COMP4521 Wong Pak Hing 20212714 phwongag@connect.ust.hk*/

public class SignIn extends AppCompatActivity implements View.OnClickListener{
    EditText editUserName, editPassword;
    private String userNameInput, passwordInput;
    private ProgressDialog signIn;
    final Context context = this;
    private FirebaseAuth firebaseAuth;
    String ext = "a@bar.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        editPassword = (EditText)findViewById(R.id.editPassword);
        editUserName = (EditText)findViewById(R.id.regEditUserName);
        signIn = new ProgressDialog(this);

        /* font */
        TextView signInWelcomText = (TextView)findViewById(R.id.signInWelcom);
        Typeface signInWelcomTextFont = Typeface.createFromAsset(getAssets(), "roboto_regular.ttf");
        signInWelcomText.setTypeface(signInWelcomTextFont);

        TextView signInUserNameText = (TextView)findViewById(R.id.signInUserName);
        Typeface signInUserNameTextFont = Typeface.createFromAsset(getAssets(), "roboto_regular.ttf");
        signInUserNameText.setTypeface(signInUserNameTextFont);

        TextView signInPasswordText = (TextView)findViewById(R.id.signInPassword);
        Typeface signInPasswordTextFont = Typeface.createFromAsset(getAssets(), "roboto_regular.ttf");
        signInPasswordText.setTypeface(signInPasswordTextFont);

        /* listeners */
        /*editUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View view, boolean hasFocus) {
                if(view.getId() == R.id.editUserName) {
                    if(editUserName.isFocused()){editUserName.setCursorVisible(true);}
                    else{editUserName.setCursorVisible(false);}
                }
            }
        });*/
        editUserName.setOnClickListener(this);

        Button signInBackwardButton = (Button)findViewById(R.id.signInBackwardButton);
        signInBackwardButton.setOnClickListener(this);

        Button signInForwardButton = (Button)findViewById(R.id.signInForwardButton);
        signInForwardButton.setOnClickListener(this);

        TextView signInRegLinkView = (TextView)findViewById(R.id.signInRegLink);
        signInRegLinkView.setOnClickListener(this);

        //initiate firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.signInBackwardButton) {
            Intent goBackLogInAndReg = new Intent(this, LogInAndReg.class);
            startActivity(goBackLogInAndReg);
        }
        else if(view.getId() == R.id.signInForwardButton){
            userNameInput = editUserName.getText().toString();
            passwordInput = editPassword.getText().toString();
            Log.i("The userName is ", userNameInput);
            Log.i("The password is ", passwordInput);
            loginUser(userNameInput,passwordInput);
        }
        else if(view.getId() == R.id.signInRegLink){
            Intent goToReg = new Intent(this, Registration.class);
            startActivity(goToReg);
        }
        else if(view.getId() == R.id.regEditUserName){
            editUserName.setCursorVisible(true);
        }
    }

    private void loginUser(String userNameInput,String passwordInput){
        String email = userNameInput.concat(ext);   //concat the Phone with a@foo.com

        signIn.setMessage("Signing In ...");
        signIn.show();

        firebaseAuth.signInWithEmailAndPassword(email,passwordInput)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){    // if sign in success
                            finish();
                            Intent goToTransaction = new Intent(getApplicationContext(), Transaction.class);
                            startActivity(goToTransaction);
                        }else{
                            AlertDialog.Builder wrongUserPasswordBuilder = new AlertDialog.Builder(context);
                            wrongUserPasswordBuilder
                                    .setMessage("Your user name and/or password are/is incorrect")
                                    .setTitle("Incorrect Input(s)")
                                    .setCancelable(true)
                                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog wrongUserPasswordDialog = wrongUserPasswordBuilder.create();
                            wrongUserPasswordDialog.show();

                        }
                        signIn.dismiss();
                    }
                });

    }

    @Override
    public void onBackPressed() {
        // do not allow android backward button
    }


}

