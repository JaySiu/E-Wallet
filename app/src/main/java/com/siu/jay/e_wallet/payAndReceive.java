package com.siu.jay.e_wallet;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* COMP4521 Siu Chit 20270807 csiuab@connect.ust.hk
   COMP4521 Wong Pak Hing 20212714 phwongag@connect.ust.hk*/

public class payAndReceive extends AppCompatActivity implements View.OnClickListener{
    public static int white = 0xFFFFFFFF;
    public static int black = 0xFF000000;
    public final static int WIDTH = 500;
    final Context context = this;
    private ImageView qrCodeView;
    private EditText editAmount;
    private Button payReceiveBackwardButton;
    //private Switch payReceiveSwitch;
    private boolean isFirstEntered = true;
    private View activityRootView;
    //private boolean globalIsChecked = false;
    private Pattern amountCheck;
    String ownPhone;
    private FirebaseAuth firebaseAuth;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mUser = mRootRef.child("User");
    DatabaseReference mPhoneNo = mUser.child("Phone Number");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_and_receive);

        /* regular express */
        amountCheck = Pattern.compile("^(?:[0-9][0-9]{0,3}(?:\\.\\d{0,2})?|10000|10000.00|10000.0)$");

        /* font */
        TextView enterAmountText = (TextView) findViewById(R.id.enterAmountText);
        Typeface enterAmountTextFont = Typeface.createFromAsset(getAssets(), "roboto_medium.ttf");
        enterAmountText.setTypeface(enterAmountTextFont);

        //payReceiveSwitch = (Switch) findViewById(R.id.payReceiveSwitch);
        qrCodeView = (ImageView) findViewById(R.id.qrCode);
        editAmount = (EditText) findViewById(R.id.editAmount);
        activityRootView = findViewById(R.id.activity_pay_and_receive);


        /* listener */
        payReceiveBackwardButton = (Button)findViewById(R.id.payReceiveBackwardButton);
        payReceiveBackwardButton.setOnClickListener(this);
        editAmount.setOnClickListener(this);

        /*payReceiveSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                globalIsChecked = isChecked;
                Log.i("Transaction type", isChecked + "");
                if(isChecked && isEntered){
                    String tranAmount = editAmount.getText().toString();
                    String tranMessage = "Pay" + " " + tranAmount + " " + ownPhone;
                    Log.i("The string is ", tranMessage);
                    try {
                        Bitmap bitmap = encodeAsBitmap(tranMessage);
                        qrCodeView.setImageBitmap(bitmap);
                        editAmount.setCursorVisible(false);
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                }
                else if(!isChecked && isEntered){
                    String tranAmount = editAmount.getText().toString();
                    String tranMessage = "Receive" + " " + tranAmount + " " + ownPhone;
                    Log.i("The string is ", tranMessage);
                    try {
                        Bitmap bitmap = encodeAsBitmap(tranMessage);
                        qrCodeView.setImageBitmap(bitmap);
                        editAmount.setCursorVisible(false);

                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                }
            }
        });*/

        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                String tranAmount = editAmount.getText().toString();
                Matcher amountValidity = amountCheck.matcher(tranAmount);

                Rect r = new Rect();
                activityRootView.getWindowVisibleDisplayFrame(r);
                int heightDiff = activityRootView.getRootView().getHeight() - (r.bottom - r.top);
                if (heightDiff > 70 && !editAmount.getText().toString().matches("")) {
                    if (amountValidity.find()) {
                            String tranMessage = ownPhone + " " + tranAmount;
                            Log.i("The string is ", tranMessage);
                            try {
                                Bitmap bitmap = encodeAsBitmap(tranMessage);
                                qrCodeView.setImageBitmap(bitmap);
                                editAmount.setCursorVisible(false);
                            } catch (WriterException e) {
                                e.printStackTrace();
                            }
                    } else {
                        qrCodeView.setImageResource(android.R.color.transparent);
                        editAmount.setError("Maximum transaction amount each time is 10000.00 and should not be any characters");
                    }
                } else {
                    qrCodeView.setImageResource(android.R.color.transparent);
                }
            }
        });

        //initiate the firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        // get the phone of current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String email = user.getEmail();
        String [] parts = email.split("a");
        ownPhone = parts[0];

        // get the database reference of current user
        DatabaseReference mCurrentUser = mPhoneNo.child(ownPhone);
        DatabaseReference mBalance = mCurrentUser.child("balanceAmount");

        mBalance.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (isFirstEntered) {
                    isFirstEntered = false;
                }
                else if(!isFirstEntered){
                    AlertDialog.Builder resultBuilder = new AlertDialog.Builder(context);
                    resultBuilder
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent goBackTransaction = new Intent(getApplicationContext(), Transaction.class);
                                    startActivity(goBackTransaction);
                                }
                            })
                            .setCancelable(true)
                            .setTitle("Transaction Complete")
                            .setMessage("Your balance will be updated");
                    AlertDialog resultDialog = resultBuilder.create();
                    resultDialog.show();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Toast.makeText(Transaction.this, "Cannot find your balance",Toast.LENGTH_SHORT).show();
            }
        });

    }

    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, WIDTH, WIDTH, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? black : white;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, WIDTH, 0, 0, w, h);
        return bitmap;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.payReceiveBackwardButton) {
            Intent goBackTransaction = new Intent(this, Transaction.class);
            startActivity(goBackTransaction);
        }
        if(view.getId() == R.id.editAmount){
            editAmount.setCursorVisible(true);
        }
    }

    @Override
    public void onBackPressed() {
        // do not allow android backward button
    }


}

/*
    <Switch
        android:showText="true"
        android:textOn="PAY"
        android:textOff="RECEIVE"
        android:thumb="@drawable/switch_selector"
        android:track="@drawable/switch_track"
        android:layout_height="wrap_content"
        android:id="@+id/payReceiveSwitch"
        android:layout_width="wrap_content"
        android:layout_alignParentTop="true"
        android:layout_centerHorizontal="true"
        android:layout_marginTop="14dp" />

 */
