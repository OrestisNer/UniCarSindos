package com.example.android.unicarsindos;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.unicarsindos.data.UniCarContract;
import com.example.android.unicarsindos.data.UniCarDbHelper;

public class LogonActivity extends AppCompatActivity {

    private EditText vEtFirstName;
    private EditText vEtSecondName;
    private EditText vEtEmail;
    private EditText vEtPhone;
    private EditText vEtPassword;
    private EditText vEtConfirmPassword;

    private Button vButtonCreate;

    private ContentValues contentValues;

    private String firstName;
    private String secondName;
    private String email;
    private String password;
    private String confirmedPassword;
    private String phone;

    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logon);

        mToast=Toast.makeText(this,"",Toast.LENGTH_SHORT);

        vButtonCreate= findViewById(R.id.button_create);

        vEtFirstName = findViewById(R.id.edit_text_firstname_logon);
        vEtSecondName = findViewById(R.id.edit_text_secondname_logon);
        vEtEmail = findViewById(R.id.edit_text_email_logon);
        vEtPhone = (findViewById(R.id.edit_text_phone_logon));
        vEtPassword = findViewById(R.id.edit_text_password_logon);
        vEtConfirmPassword =  findViewById(R.id.edit_text_confirm_password_logon);

        contentValues = new ContentValues();

        vButtonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readData();
                mToast.cancel();
                if (!isInfoFilled()) {
                    mToast = Toast.makeText(LogonActivity.this, "Πρεπει να συμπληρώσετε όλα τα πεδία.", Toast.LENGTH_LONG);
                    mToast.show();
                } else if (!isPasswordConfirmed()) {
                    mToast = Toast.makeText(LogonActivity.this, "Οι κωδικοί είναι διαφορετικοί.", Toast.LENGTH_LONG);
                    vEtPassword.getText().clear();
                    vEtConfirmPassword.getText().clear();
                    mToast.show();
                } else if (isEmailExists()){
                    mToast=Toast.makeText(LogonActivity.this, "Το email υπάρχει.", Toast.LENGTH_LONG);
                    vEtEmail.getText().clear();
                    mToast.show();
                }else{
                    putDataIntoCV();
                    getContentResolver().insert(UniCarContract.UniCarEntry.CONTENT_URI, contentValues);
                    Toast.makeText(LogonActivity.this, "Επιτυχής Εγγραφή.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }


    private boolean isPasswordConfirmed(){
        return password.equals(confirmedPassword);
    }

    private boolean isInfoFilled(){
        if(firstName.length()==0 ||
                secondName.length()==0 ||
                email.length()==0 ||
                password.length()==0 ||
                confirmedPassword.length()==0)
            return false;
        return true;
    }

    private void readData() {
        firstName=vEtFirstName.getText().toString().toLowerCase();
        secondName=vEtSecondName.getText().toString().toLowerCase();
        email=vEtEmail.getText().toString().toLowerCase();
        password=vEtPassword.getText().toString();
        confirmedPassword = vEtConfirmPassword.getText().toString();
        phone = vEtPhone.getText().toString().toLowerCase();
    }

    private boolean isEmailExists(){
        Cursor mCursor= getContentResolver().query(UniCarContract.UniCarEntry.CONTENT_URI,
                new String[]{UniCarContract.UniCarEntry.COLUMN_EMAIL},
                UniCarContract.UniCarEntry.COLUMN_EMAIL+"=?",
                new String[]{email},
                null);
        if(mCursor.getCount()==0)
            return false;
        mCursor.close();
        return true;
    }


    private void putDataIntoCV(){
        contentValues.put(UniCarContract.UniCarEntry.COLUMN_FIRST_NAME,firstName);
        contentValues.put(UniCarContract.UniCarEntry.COLUMN_SECOND_NAME,secondName);
        contentValues.put(UniCarContract.UniCarEntry.COLUMN_PHONE,phone);
        contentValues.put(UniCarContract.UniCarEntry.COLUMN_EMAIL,email);
        contentValues.put(UniCarContract.UniCarEntry.COLUMN_PASSWORD,password);
    }

}
