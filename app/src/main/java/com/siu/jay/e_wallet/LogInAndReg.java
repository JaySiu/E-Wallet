package com.siu.jay.e_wallet;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

/* COMP4521 Siu Chit 20270807 csiuab@connect.ust.hk
   COMP4521 Wong Pak Hing 20212714 phwongag@connect.ust.hk*/

public class LogInAndReg extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_and_reg);

        //initiate the firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        //check current auth state
        if(firebaseAuth.getCurrentUser() != null){
            finish();
            // if already log in, go to Transaction
            startActivity(new Intent(getApplicationContext(), Transaction.class));
        }

        /* font */
        TextView signInButtonText = (TextView)findViewById(R.id.signInButton);
        Typeface signInFont = Typeface.createFromAsset(getAssets(), "blackjack.otf");
        signInButtonText.setTypeface(signInFont);

        TextView regButtonText = (TextView)findViewById(R.id.regButton);
        Typeface regButtonFont = Typeface.createFromAsset(getAssets(), "blackjack.otf");
        regButtonText.setTypeface(regButtonFont);

        TextView ORText = (TextView)findViewById(R.id.OR);
        Typeface ORTextFont = Typeface.createFromAsset(getAssets(), "roboto_regular.ttf");
        ORText.setTypeface(ORTextFont);

        TextView welcomeText = (TextView)findViewById(R.id.welcome);
        Typeface welcomeTextFont = Typeface.createFromAsset(getAssets(), "roboto_medium.ttf");
        welcomeText.setTypeface(welcomeTextFont);

        /* set bit flags to draw strike-through */
        TextView leftORStrike = (TextView) findViewById(R.id.leftOR);
        leftORStrike.setPaintFlags(leftORStrike.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        TextView rightORStrike = (TextView) findViewById(R.id.rightOR);
        rightORStrike.setPaintFlags(rightORStrike.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        /* onClickListener of the buttons*/
        Button signInButton = (Button) findViewById(R.id.signInButton);
        signInButton.setOnClickListener(this);

        Button regButton = (Button) findViewById(R.id.regButton);
        regButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.signInButton) {
            Intent goToSignIn = new Intent(this, SignIn.class);
            startActivity(goToSignIn);
        }
        else{
            Intent goToReg = new Intent(this, Registration.class);
            startActivity(goToReg);
        }
    }

    @Override
    public void onBackPressed() {
        // do not allow android backward button
    }

}

