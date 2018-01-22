package com.example.android.unicarsindos;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.unicarsindos.data.UniCarContract;
import com.example.android.unicarsindos.utilities.StringUtils;
import com.google.android.gms.maps.model.LatLng;

public class ProfileActivity extends AppCompatActivity  {

    private String usersEmail;
    private String selectedUser;
    private LatLng selectedUserLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intentThatStartedThisActivity = getIntent();
        Button buttonOpenMap= findViewById(R.id.open_map);


        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra("usersEmail")) {
                usersEmail = intentThatStartedThisActivity.getStringExtra("usersEmail");
            }
            if (intentThatStartedThisActivity.hasExtra("selectedUser")) {
                selectedUser = intentThatStartedThisActivity.getStringExtra("selectedUser");
            }
        }

        fillInfo();


        buttonOpenMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedUserLocation.latitude!=0.0 || selectedUserLocation.longitude!=0.0) {
                    Intent mapActivityIntent = new Intent(ProfileActivity.this, MapActivity.class);
                    mapActivityIntent.putExtra(Intent.EXTRA_TEXT, selectedUser);
                    startActivity(mapActivityIntent);
                }else{
                    Toast.makeText(ProfileActivity.this, "Δεν υπάρχει τοποθεσία.", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    private void fillInfo(){
        TextView tvName= findViewById(R.id.text_view_users_name);
        TextView tvEmail= findViewById(R.id.text_view_users_email);
        TextView tvphone= findViewById(R.id.text_view_users_phone);
        TextView tvAddress= findViewById(R.id.text_view_users_address);
        TextView tvArea= findViewById(R.id.text_view_users_area);
        TextView tvDaysHours= findViewById(R.id.text_view_users_days_hours);
        TextView tvInformation= findViewById(R.id.text_view_users_information);

        Cursor mCursor= getContentResolver().query(UniCarContract.UniCarEntry.CONTENT_URI,
                                      null,
                                      UniCarContract.UniCarEntry.COLUMN_EMAIL+"=?",
                                       new String[]{selectedUser},
                                      null);
        mCursor.moveToFirst();
        String firstName=mCursor.getString(mCursor.getColumnIndex(UniCarContract.UniCarEntry.COLUMN_FIRST_NAME));
        String secondName=mCursor.getString(mCursor.getColumnIndex(UniCarContract.UniCarEntry.COLUMN_SECOND_NAME));
        tvName.setText(String.format("%s %s", StringUtils.upperCaseFirstLetter(firstName), StringUtils.upperCaseFirstLetter(secondName)));
        tvEmail.setText(mCursor.getString(mCursor.getColumnIndex(UniCarContract.UniCarEntry.COLUMN_EMAIL)));
        tvphone.setText(mCursor.getString(mCursor.getColumnIndex(UniCarContract.UniCarEntry.COLUMN_PHONE)));
        double latitude= mCursor.getDouble(mCursor.getColumnIndex(UniCarContract.UniCarEntry.COLUMN_LATITUDE));
        double longitude=mCursor.getDouble(mCursor.getColumnIndex(UniCarContract.UniCarEntry.COLUMN_LONGITUDE));
        selectedUserLocation=new LatLng(latitude,longitude);

        String address=mCursor.getString(mCursor.getColumnIndex(UniCarContract.UniCarEntry.COLUMN_ADDRESS));
        String[] addressZipcode=StringUtils.seperateAddress(address);
        if(!(addressZipcode[0].equals("null") && addressZipcode[1].equals("null")))
           tvAddress.setText(address);

        tvArea.setText(mCursor.getString(mCursor.getColumnIndex(UniCarContract.UniCarEntry.COLUMN_AREA)));
        tvDaysHours.setText(mCursor.getString(mCursor.getColumnIndex(UniCarContract.UniCarEntry.COLUMN_DAYS_HOURS)));
        tvInformation.setText(mCursor.getString(mCursor.getColumnIndex(UniCarContract.UniCarEntry.COLUMN_INFORMATION)));
        mCursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_change){
            Intent changeIntent= new Intent(ProfileActivity.this,UpdateProfileActivity.class);
            changeIntent.putExtra(Intent.EXTRA_TEXT,usersEmail);
            startActivity(changeIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}
