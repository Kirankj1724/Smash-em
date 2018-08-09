package com.androappdroid.smashem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.androappdroid.smashem.Models.UserInfoModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.Calendar;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText textInputEditTextUsername,textInputEditTextEmail;
    private Button buttonLogin;
    private boolean isUserExists=false;
    private FirebaseDatabase fbInstance;
    private DatabaseReference root,user;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textInputEditTextUsername=findViewById(R.id.tie_name_login);
        textInputEditTextEmail=findViewById(R.id.tie_email_login);
        buttonLogin=findViewById(R.id.btn_play_login);

        buttonLogin.setOnClickListener(this);

        init();
    }

    private void init() {

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        checkForUserData();

        }

    private void checkForUserData() {
        SharedPreferences sharedPreferences=getSharedPreferences("SP_SMASH_EM",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("userInfoModel", "");

        if(json!=null && json.length()>0)
        {
            UserInfoModel userInfoModel = gson.fromJson(json, UserInfoModel.class);
            navigateToGameScreen();
            Log.e("data",userInfoModel.getUserName()+" : "+userInfoModel.getTopScore());
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_play_login :
                doLoginAction();
            break;
        }
    }

    private void doLoginAction() {
        String userName=textInputEditTextUsername.getText().toString().trim();
        String email=textInputEditTextEmail.getText().toString().trim().toLowerCase();
        if(userName.length()<3)
            showMessage("User name length has to be greater than 3 characters!");
        else if(!isValidEmail(email))
            showMessage("Please enter a valid e-mail address!");
        else
        {
            showProgress();

            final UserInfoModel userInfoModel=new UserInfoModel();
            userInfoModel.setUserId(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            userInfoModel.setUserName(userName);
            userInfoModel.setEmailId(email);
            userInfoModel.setTopScore(0);

            fbInstance = FirebaseDatabase.getInstance();
            root = fbInstance.getReference("userInfo");
            user=root.child("user");

            // check if data exists
            user.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Log.e("data",snapshot.child("emailId").getValue().toString());

                    if (snapshot.child("emailId").getValue().toString().equalsIgnoreCase(userInfoModel.getEmailId())) {
                        isUserExists=true;
                    }else{
                        isUserExists=false;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            Log.e("user exists",isUserExists+"");
//            if(!isUserExists)
//            {
//                // insert new user ifo
//                user.setValue(userInfoModel).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        storeUserData(userInfoModel);
//                        hideProgress();
//                        navigateToGameScreen();
//                    }
//                });
//            }
//            else
//            {
//                // fetch existing user info
//                final UserInfoModel existingUserModel=new UserInfoModel();
//                // Read from the database
//                user.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        // This method is called once with the initial value and again
//                        // whenever data at this location is updated.
//                        String value = dataSnapshot.child("emailId").getValue().toString();
//                        Log.e("TAG", "Value is: " + value);
//                        existingUserModel.setEmailId(dataSnapshot.child("emailId").getValue().toString());
//                        existingUserModel.setUserName(dataSnapshot.child("userName").getValue().toString());
//                        existingUserModel.setUserId(dataSnapshot.child("userId").getValue().toString());
//                        existingUserModel.setTopScore(Integer.parseInt(dataSnapshot.child("topScore").getValue().toString()));
//                        storeUserData(existingUserModel);
//                        hideProgress();
//                        navigateToGameScreen();
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError error) {
//                        // Failed to read value
//                        Log.e("TAG", "Failed to read value.", error.toException());
//                    }
//                });
//            }
        }
    }

    private void hideProgress() {
        progressDialog.dismiss();
    }

    private void showProgress() {
        progressDialog.show();
    }

    private void storeUserData(UserInfoModel userInfoModel) {
        SharedPreferences sharedPreferences=getSharedPreferences("SP_SMASH_EM",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(userInfoModel);
        editor.putString("userInfoModel",json);
        editor.apply();
    }

    private void navigateToGameScreen() {
        Intent intent=new Intent(this,GameActivity.class);
        startActivity(intent);
        finish();
    }

    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
