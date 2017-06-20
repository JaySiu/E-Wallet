package com.siu.jay.e_wallet;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* COMP4521 Siu Chit 20270807 csiuab@connect.ust.hk
   COMP4521 Wong Pak Hing 20212714 phwongag@connect.ust.hk*/

public class Cards extends AppCompatActivity implements View.OnClickListener{
    private ListView cardList;
    final int HSBC = R.drawable.hsbc, BOC = R.drawable.boc, HS = R.drawable.hang_seng;
    final int DELETE_BUTTON = R.drawable.delete_card_button;
    private String ownPhone, noOfCards;
    private String card_1, card_2, card_3;
    private String card_1_number, card_2_number, card_3_number;
    private boolean isDelButtonClicked = false, firstVisit = true;
    private DatabaseReference mCurrentUser;
    private int oldBalance;
    private Pattern amountCheck;
    List<Integer> userCardType = new ArrayList<Integer>();
    List<String> userCardNumber = new ArrayList<String>();
    private FirebaseAuth firebaseAuth;
    final Context context = this;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mUser = mRootRef.child("User");
    DatabaseReference mPhoneNo = mUser.child("Phone Number");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);


        //initiate the firebase
        firebaseAuth = FirebaseAuth.getInstance();
        // get the phone of current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String email = user.getEmail();
        String [] parts = email.split("a");
        ownPhone = parts[0];
        // get the database reference of current user
        mCurrentUser = mPhoneNo.child(ownPhone);
        DatabaseReference mNumberOfCard = mCurrentUser.child("numberOfCards");
        DatabaseReference mBalance = mCurrentUser.child("balanceAmount");
        DatabaseReference mcard_1 = mCurrentUser.child("card_1");
        DatabaseReference mcard_2 = mCurrentUser.child("card_2");
        DatabaseReference mcard_3 = mCurrentUser.child("card_3");
        DatabaseReference mcard_1_number = mCurrentUser.child("card_1_number");
        DatabaseReference mcard_2_number = mCurrentUser.child("card_2_number");
        DatabaseReference mcard_3_number = mCurrentUser.child("card_3_number");

        mBalance.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String balance = dataSnapshot.getValue(String.class);
                oldBalance = Integer.parseInt(balance);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Toast.makeText(Transaction.this, "Cannot find your balance",Toast.LENGTH_SHORT).show();
            }
        });
        mcard_1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                card_1 = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mcard_2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                card_2 = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mcard_3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                card_3 = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mcard_1_number.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                card_1_number = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mcard_2_number.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                card_2_number = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mcard_3_number.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                card_3_number = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mNumberOfCard.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                noOfCards = dataSnapshot.getValue(String.class);
                //Log.i("noOfCards from DB", noOfCards);
                    showMyCards();
                    updateList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        cardList = (ListView)findViewById(R.id.cardList);
        cardList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                final EditText amount = new EditText(context);
                AlertDialog.Builder popMoneyBuilder = new AlertDialog.Builder(context);
                popMoneyBuilder
                        .setTitle("TOP UP / WITHDRAW")
                        .setMessage("Enter the amount you want to pop out from or withdraw to this debit card")
                        .setView(amount)
                        .setCancelable(true)
                        .setPositiveButton("Pop Out", new DialogInterface.OnClickListener() {
                             public void onClick(DialogInterface dialog, int id) {
                                 String amountPop = amount.getText().toString();
                                 Matcher amountValidity = amountCheck.matcher(amountPop);
                                 if(amountValidity.find()){
                                     if(amountPop.contains(".")){
                                         String[] separatedAmount = amountPop.split("\\.");
                                         String inte = separatedAmount[0];
                                         String deci = separatedAmount[1];
                                         int integ = Integer.parseInt(inte);
                                         int decimal = Integer.parseInt(deci);
                                         if(deci.length() == 1){decimal = decimal * 10;}
                                         int totalAmount = integ*100 + decimal;
                                         mCurrentUser.child("balanceAmount").setValue(totalAmount + oldBalance + "");
                                         Toast.makeText(Cards.this, "Top up success",Toast.LENGTH_SHORT).show();
                                     }
                                     else{
                                         int totalAmount = Integer.parseInt(amountPop) * 100;
                                         mCurrentUser.child("balanceAmount").setValue(totalAmount + oldBalance + "");
                                         Toast.makeText(Cards.this, "Top up succeed",Toast.LENGTH_SHORT).show();
                                     }
                                 }
                                 else{
                                     Toast.makeText(Cards.this, "Invalid top up amount",Toast.LENGTH_SHORT).show();
                                 }

                    }
                })
                        .setNegativeButton("Withdraw", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String amountPop = amount.getText().toString();
                                Matcher amountValidity = amountCheck.matcher(amountPop);
                                if(amountValidity.find()){
                                    if(amountPop.contains(".")){
                                        String[] separatedAmount = amountPop.split("\\.");
                                        String inte = separatedAmount[0];
                                        String deci = separatedAmount[1];
                                        int integ = Integer.parseInt(inte);
                                        int decimal = Integer.parseInt(deci);
                                        if(deci.length() == 1){decimal = decimal * 10;}
                                        int totalAmount = integ*100 + decimal;
                                        if(oldBalance > totalAmount){
                                            mCurrentUser.child("balanceAmount").setValue(oldBalance - totalAmount + "");
                                            Toast.makeText(Cards.this, "Withdraw succeed",Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Toast.makeText(Cards.this, "You don not have enough balance",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else{
                                        int totalAmount = Integer.parseInt(amountPop) * 100;
                                        if(oldBalance > totalAmount){
                                            mCurrentUser.child("balanceAmount").setValue(oldBalance - totalAmount + "");
                                            Toast.makeText(Cards.this, "Withdraw succeed",Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Toast.makeText(Cards.this, "You don not have enough balance",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                                else{
                                    Toast.makeText(Cards.this, "Invalid top up amount",Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                android.app.AlertDialog popMoneyBuilderDialog = popMoneyBuilder.create();
                popMoneyBuilderDialog.show();
            }
        });

        /* listeners */
        Button cardsBackwardButton = (Button) findViewById(R.id.cardsBackwardButton);
        cardsBackwardButton.setOnClickListener(this);

        Button addCardsButton = (Button) findViewById(R.id.addCardButton);
        addCardsButton.setOnClickListener(this);

        Button updateButton = (Button) findViewById(R.id.delete);
        updateButton.setOnClickListener(this);

        amountCheck = Pattern.compile("^(?:[0-9][0-9]{0,3}(?:\\.\\d{0,2})?|10000|10000.00|10000.0)$");
    }

    class ListAdapter extends BaseAdapter {
        List<Integer> userCard = new ArrayList<>();
        List<String> userCardNo = new ArrayList<>();

        ListAdapter(){
            userCard = null;
            userCardNo = null;
        }

        public ListAdapter(List<Integer> card, List<String> no) {
            userCard = card;
            userCardNo = no;
            //Log.i("size", userCard.size()+"");
           // Log.i("size", userCardNo.size()+"");

        }

        @Override
        public int getCount() {
            return userCard.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater listInflater = getLayoutInflater();
            View customizedList;
            customizedList = listInflater.inflate(R.layout.card_list, parent, false);
            FrameLayout cardFrame = (FrameLayout)customizedList.findViewById(R.id.cardFrame);
            TextView cardNumber = (TextView)customizedList.findViewById(R.id.cardNumber);
            cardFrame.setBackgroundResource(userCard.get(position));
            cardNumber.setText(userCardNo.get(position)+"");
            return customizedList;
        }
    }
    class DeleteListAdapter extends BaseAdapter {
        List<Integer> userCard = new ArrayList<>();
        List<String> userCardNo = new ArrayList<>();

        DeleteListAdapter(){
            userCard = null;
            userCardNo = null;
        }

        public DeleteListAdapter(List<Integer> card, List<String> no) {
            userCard = card;
            userCardNo = no;
            //Log.i("size", userCard.size()+"");
            // Log.i("size", userCardNo.size()+"");

        }

        @Override
        public int getCount() {
            return userCard.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater listInflater = getLayoutInflater();
            View customizedList;
            customizedList = listInflater.inflate(R.layout.card_list_delete, parent, false);
            FrameLayout cardFrame = (FrameLayout)customizedList.findViewById(R.id.cardFrame);
            TextView cardNumber = (TextView)customizedList.findViewById(R.id.cardNumber);
            Button deleteButton = (Button)customizedList.findViewById(R.id.deleteCardButton);
            cardFrame.setBackgroundResource(userCard.get(position));
            cardNumber.setText(userCardNo.get(position)+"");
            deleteButton.setBackgroundResource(DELETE_BUTTON);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    isDelButtonClicked = false;
                    reorderList(position);
                }
            });
            return customizedList;
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.cardsBackwardButton) {
            Intent goBackTransaction = new Intent(this, Transaction.class);
            startActivity(goBackTransaction);
        }
        else if(view.getId() == R.id.addCardButton){
            Intent goToAddCard = new Intent(this, AddCard.class);
            startActivity(goToAddCard);
        }
        else if(view.getId() == R.id.delete){
            showDeleteList();
        }
    }

    @Override
    public void onBackPressed() {
        // do not allow android backward button
    }

    private void showMyCards(){
        //Log.i("card 1", card_1);
        //Log.i("card 1", card_2);
        //Log.i("card 1", card_3);
        if(firstVisit){firstVisit = false;}
        else{
            userCardType.clear();
            userCardNumber.clear();
        }

        if(card_1 != null && !card_1.isEmpty()){
            switch(card_1){
                case "HSBC":
                    userCardType.add(HSBC);
                    break;
                case "BOC":
                    userCardType.add(BOC);
                    break;
                case "HS":
                    userCardType.add(HS);
                    break;
            }
            userCardNumber.add(card_1_number);
        }
        else{
            userCardType.clear();
            userCardNumber.clear();
        }

        if(card_2 != null && !card_2.isEmpty()){
            switch(card_2){
                case "HSBC":
                    userCardType.add(HSBC);
                    break;
                case "BOC":
                    userCardType.add(BOC);
                    break;
                case "HS":
                    userCardType.add(HS);
                    break;
            }
            userCardNumber.add(card_2_number);
        }

        if(card_3 != null && !card_3.isEmpty()){
            switch(card_3){
                case "HSBC":
                    userCardType.add(HSBC);
                    break;
                case "BOC":
                    userCardType.add(BOC);
                    break;
                case "HS":
                    userCardType.add(HS);
                    break;
            }
            userCardNumber.add(card_3_number);
        }
    }

    private void updateList(){
        cardList.setAdapter(new Cards.ListAdapter(userCardType, userCardNumber));
    }

    private void showDeleteList(){
        if(!isDelButtonClicked){
            isDelButtonClicked = true;
            cardList.setAdapter(new Cards.DeleteListAdapter(userCardType, userCardNumber));
        }
        else{
            isDelButtonClicked = false;
            cardList.setAdapter(new Cards.ListAdapter(userCardType, userCardNumber));
        }
    }

    private void reorderList(int position){
        if(position == 0){
            if(card_2.isEmpty() && card_3.isEmpty()){
                mCurrentUser.child("card_1").setValue("");
                mCurrentUser.child("card_1_number").setValue("");
                mCurrentUser.child("numberOfCards").setValue("0");
            }
            else if(!card_2.isEmpty() && card_3.isEmpty()){
                mCurrentUser.child("card_1").setValue(card_2);
                mCurrentUser.child("card_1_number").setValue(card_2_number);
                mCurrentUser.child("card_2").setValue("");
                mCurrentUser.child("card_2_number").setValue("");
                mCurrentUser.child("numberOfCards").setValue("1");
            }
            else if(!card_2.isEmpty() && !card_3.isEmpty()){
                mCurrentUser.child("card_1").setValue(card_2);
                mCurrentUser.child("card_1_number").setValue(card_2_number);
                mCurrentUser.child("card_2").setValue(card_3);
                mCurrentUser.child("card_2_number").setValue(card_3_number);
                mCurrentUser.child("card_3").setValue("");
                mCurrentUser.child("card_3_number").setValue("");
                mCurrentUser.child("numberOfCards").setValue("2");
            }
        }
        else if(position == 1){
            if(card_3.isEmpty()){
                mCurrentUser.child("card_2").setValue("");
                mCurrentUser.child("card_2_number").setValue("");
                mCurrentUser.child("numberOfCards").setValue("1");
            }
            else{
                mCurrentUser.child("card_2").setValue(card_3);
                mCurrentUser.child("card_2_number").setValue(card_3_number);
                mCurrentUser.child("card_3").setValue("");
                mCurrentUser.child("card_3_number").setValue("");
                mCurrentUser.child("numberOfCards").setValue("2");
            }
        }
        else if(position == 2){
            mCurrentUser.child("card_3").setValue("");
            mCurrentUser.child("card_3_number").setValue("");
            mCurrentUser.child("numberOfCards").setValue("2");
        }
    }

}
