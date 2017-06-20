package com.siu.jay.e_wallet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.util.Log;
import android.content.Context;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* COMP4521 Siu Chit 20270807 csiuab@connect.ust.hk
   COMP4521 Wong Pak Hing 20212714 phwongag@connect.ust.hk*/

public class Registration extends AppCompatActivity implements View.OnClickListener{
    private String[]items = {"+852", "+86"};
    private String selectedValue, firstNameInput, lastNameInput, passwordInput, conPasswordInput, phoneInput;
    private Spinner regSpinner;
    private String myPhoneNumber;
    EditText regEditUserName, regEditLastName, regEditPassword, regEditConPassword, editPhone;
    final Context context = this;
    private Pattern onlyChars, passwordCheck, hkPhoneCheck, chinaPhoneCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        regEditUserName = (EditText)findViewById(R.id.regEditUserName);
        regEditLastName = (EditText)findViewById(R.id.regEditLastName);
        regEditPassword = (EditText)findViewById(R.id.regEditPassword);
        regEditConPassword = (EditText)findViewById(R.id.regEditConPassword);
        editPhone = (EditText)findViewById(R.id.editPhone);
        regSpinner = (Spinner)findViewById(R.id.regSpinner);

        /* set spinner */
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        regSpinner.setAdapter(spinnerAdapter);
        regSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedValue = regSpinner.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {  }
        });

        /* restore inputs */
        if(this.getIntent().getExtras() != null){
            regEditUserName.setText(this.getIntent().getStringExtra("firstName"));
            regEditLastName.setText(this.getIntent().getStringExtra("lastName"));
            regEditPassword.setText(this.getIntent().getStringExtra("password"));
            regEditConPassword.setText(this.getIntent().getStringExtra("conPassword"));
            editPhone.setText(this.getIntent().getStringExtra("phone"));
            Log.i(" ", this.getIntent().getStringExtra("selectedItem"));
            if(this.getIntent().getStringExtra("selectedItem").equals("+852")){
                regSpinner.setSelection(0);
                selectedValue = regSpinner.getSelectedItem().toString();
            }
            else{
                regSpinner.setSelection(1);
                selectedValue = regSpinner.getSelectedItem().toString();
            }
        }

        /* regular express */
        onlyChars = Pattern.compile("^[A-Z]*[a-z]*$");
        passwordCheck = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{4,20}");
        hkPhoneCheck = Pattern.compile("^1[0-9]{10}$|^[569][0-9]{7}$");
        chinaPhoneCheck = Pattern.compile("^(13[0-9]|14[57]|15[012356789]|17[0678]|18[0-9])[0-9]{8}$");

        /* font */
        TextView regSignInButtonText = (TextView)findViewById(R.id.regSignInButton);
        Typeface regSignInButtonTextFont = Typeface.createFromAsset(getAssets(), "roboto_medium.ttf");
        regSignInButtonText.setTypeface(regSignInButtonTextFont);

        /* listeners */
        regEditUserName.setOnClickListener(this);
        regEditLastName.setOnClickListener(this);


        Button regSignInButton = (Button)findViewById(R.id.regSignInButton);
        regSignInButton.setOnClickListener(this);

        Button regBackwardButton = (Button)findViewById(R.id.regBackwardButton);
        regBackwardButton.setOnClickListener(this);

        Button regForwardButton = (Button)findViewById(R.id.regForwardButton);
        regForwardButton.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.regBackwardButton) {
            Intent goBackLogInAndReg = new Intent(this, LogInAndReg.class);
            startActivity(goBackLogInAndReg);
        }
        else if(view.getId() == R.id.regSignInButton){
            Intent goToSignIn = new Intent(this, SignIn.class);
            startActivity(goToSignIn);
        }
        else if(view.getId() == R.id.regForwardButton){
            firstNameInput = regEditUserName.getText().toString();
            lastNameInput = regEditLastName.getText().toString();
            passwordInput = regEditPassword.getText().toString();
            conPasswordInput = regEditConPassword.getText().toString();
            phoneInput = editPhone.getText().toString();
            if(canGoSMSCon(firstNameInput, lastNameInput, passwordInput, conPasswordInput, phoneInput)) {
                Intent goToSMSCon = new Intent(this, SMSConfirmation.class);
                goToSMSCon.putExtra("firstName", firstNameInput);
                goToSMSCon.putExtra("lastName", lastNameInput);
                goToSMSCon.putExtra("password", passwordInput);
                goToSMSCon.putExtra("conPassword", conPasswordInput);
                goToSMSCon.putExtra("phone", phoneInput);
                goToSMSCon.putExtra("selectedItem", selectedValue);
                startActivity(goToSMSCon);
            }
            else{
                AlertDialog.Builder regValidityBuilder = new AlertDialog.Builder(context);
                regValidityBuilder
                        .setMessage("One/Some of your inputs is/are invalid")
                        .setTitle("Proceed rejected")
                        .setCancelable(true)
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog regValidityBuilderDialog = regValidityBuilder.create();
                regValidityBuilderDialog.show();

            }
        }
        else if(view.getId() == R.id.regEditUserName){
            regEditUserName.setCursorVisible(true);
        }
        else if(view.getId() == R.id.regEditLastName){
            regEditLastName.setCursorVisible(true);
        }
    }

    private boolean isFirsttNameValid(String firstNameInput){
        Matcher onlyCharsMatcher = onlyChars.matcher(firstNameInput);
        if(!TextUtils.isEmpty(firstNameInput) && onlyCharsMatcher.find()) {
            return true;
        }
        else {
            regEditUserName.setError("Your first name must start with capital letter and should not be empty");
            return false;
        }
    }

    private boolean isLastNameValid(String lastNameInput){
        Matcher onlyCharsMatcher = onlyChars.matcher(lastNameInput);
        if(!TextUtils.isEmpty(lastNameInput) && onlyCharsMatcher.find()) {
            return true;
        }
        else {
            regEditLastName.setError("Your last name must start with capital letter and should not be empty");
            return false;
        }

    }

    private boolean isPasswordValid(String passwordInput){
        Matcher passwordMatcher = passwordCheck.matcher(passwordInput);
        if(!TextUtils.isEmpty(passwordInput) && passwordMatcher.find()) {
            return true;
        }
        else {
            regEditPassword.setError("Your password should contain digit(s), capital and small letter(s), and should have at least a length of 4");
            return false;
        }

    }

    private boolean isConPasswordValid(String passwordInput, String conPasswordInput){
        Matcher conPasswordMatcher = passwordCheck.matcher(conPasswordInput);
        if(!TextUtils.isEmpty(conPasswordInput) && conPasswordMatcher.find() && conPasswordInput.equals(passwordInput)) {
            return true;
        }
        else {
            regEditConPassword.setError("Your confirm password should be the same as password and should not be empty");
            return false;
        }

    }

    private boolean isPhoneValid(String phoneInput){
        //TelephonyManager myPhone = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        //myPhoneNumber = myPhone.getLine1Number();
        //Log.i("phone", myPhoneNumber);
        if(selectedValue.equals("+852")){
            Matcher conPhoneMatcher = hkPhoneCheck.matcher(phoneInput);
            if(conPhoneMatcher.find()){return true;} //&& myPhoneNumber.equals(phoneInput)
            else{
                editPhone.setError("Your phone number is invalid or empty");
                return false;
            }
        }
        else{
            Matcher conPhoneMatcher = chinaPhoneCheck.matcher(phoneInput);
            if(conPhoneMatcher.find()){return true;}
            else{
                editPhone.setError("Your phone number is invalid or empty");
                return false;
            }
        }

    }

    private boolean canGoSMSCon(String firstNameInput, String lastNameInput, String passwordInput, String conPasswordInput, String phoneInput){
        if(isFirsttNameValid(firstNameInput) && isLastNameValid(lastNameInput) && isPasswordValid(passwordInput) && isConPasswordValid(passwordInput, conPasswordInput) && isPhoneValid(phoneInput)){return true;}
        else{return false;}
    }

    @Override
    public void onBackPressed() {
        // do not allow android backward button
    }
}