package com.example.android.unicarsindos;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.unicarsindos.data.UniCarContract;

import static com.example.android.unicarsindos.data.UniCarContract.*;


public class LoginActivity extends AppCompatActivity {

    private EditText vEtEmail;
    private EditText vEtPassword;
    private TextView vTvLogon;
    private Button vButtonLogin;
    private String vEmailString;
    private String vPasswordString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        vButtonLogin= findViewById(R.id.button_login);
        vTvLogon=  findViewById(R.id.text_view_logon);

        //getContentResolver().delete(UniCarEntry.CONTENT_URI,null,null);
        userAction();


    }

    private void getUserInput(){
        vEtEmail= findViewById(R.id.edit_text_email_login);
        vEtPassword = findViewById(R.id.edit_text_password_login);

        vEmailString= vEtEmail.getText().toString().toLowerCase();
        vPasswordString= vEtPassword.getText().toString();
    }

    private void userAction(){

        vButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserInput();
                vEtEmail.getText().clear();
                vEtPassword.getText().clear();
                Cursor loginCursor;
                try{
                    loginCursor=getContentResolver().query(
                            UniCarEntry.CONTENT_URI,
                            new String[]{UniCarEntry.COLUMN_EMAIL,UniCarEntry.COLUMN_PASSWORD,UniCarEntry.COLUMN_HAS_PROFILE},
                            UniCarEntry.COLUMN_EMAIL+"=?"+" AND "+ UniCarEntry.COLUMN_PASSWORD + "=?",
                            new String[]{vEmailString, vPasswordString},
                            null);
                }catch (Exception e){
                    e.printStackTrace();
                    loginCursor=null;
                }
                //Ο cursor είναι άδειος
                if(!(loginCursor.moveToFirst()) || loginCursor.getCount() ==0){
                    Toast.makeText(LoginActivity.this,"Λάθος email ή Κωδικός.",Toast.LENGTH_LONG).show();
                    vEtEmail.getText().clear();
                    vEtPassword.getText().clear();
                }else if(loginCursor.getCount()==1){
                    int hasProfile= loginCursor.getInt(loginCursor.getColumnIndex(UniCarEntry.COLUMN_HAS_PROFILE));
                    if(hasProfile==0) {
                        loginCursor.close();
                        /*ContentValues cv = new ContentValues();
                        cv.put(UniCarEntry.COLUMN_HAS_PROFILE,1);
                        getContentResolver().update(UniCarEntry.CONTENT_URI,cv,UniCarEntry.COLUMN_EMAIL+"=?"
                                ,new String[]{vEmailString});*/
                        Intent createProfileActivityIntent = new Intent(LoginActivity.this,
                                CreateProfileActivity.class);
                        createProfileActivityIntent.putExtra(Intent.EXTRA_TEXT,vEmailString);
                        startActivity(createProfileActivityIntent);
                    }else{
                        loginCursor.close();
                        Intent mainActivityIntent = new Intent(LoginActivity.this,
                                MainActivity.class);
                        mainActivityIntent.putExtra(Intent.EXTRA_TEXT, vEmailString);
                        startActivity(mainActivityIntent);
                    }
                }

            }
        });

        vTvLogon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent logonActivityIntent= new Intent(LoginActivity.this,LogonActivity.class);
                startActivity(logonActivityIntent);
            }
        });

    }

}
