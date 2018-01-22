package com.example.android.unicarsindos;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.unicarsindos.data.UniCarContract;
import com.example.android.unicarsindos.utilities.OpenMapUtils;
import com.example.android.unicarsindos.utilities.StringUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class UpdateProfileActivity extends AppCompatActivity implements OnMapReadyCallback{

    String usersEmail;
    String zipcodeString;
    String addressString;
    String areaString;
    String daysHours;
    String informationString;

    GoogleMap googleMap;
    LatLng usersLocation;
    LatLng sindosLatLog;

    EditText etFirstName;
    EditText etEmail;
    EditText etPhone;
    EditText etAddress;
    EditText etZipcode;
    EditText etArea;
    TextView tvHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offer_a_ride_page_1);

        Intent intentThatStartedThisActivity = getIntent();
        sindosLatLog=  new LatLng(40.658315, 22.803919);


        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                usersEmail = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
            }
        }

        fillInfo();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(UpdateProfileActivity.this);
        userAction();


    }

    private void fillInfo(){
        Cursor cursorUserDetail=getContentResolver().query(UniCarContract.UniCarEntry.CONTENT_URI,
                                  null,
                                  UniCarContract.UniCarEntry.COLUMN_EMAIL+"=?",
                                   new String[]{usersEmail},
                                  null);
        cursorUserDetail.moveToFirst();
        String firstName = cursorUserDetail.getString(cursorUserDetail.getColumnIndex(UniCarContract.UniCarEntry.COLUMN_FIRST_NAME));
        String secondName= cursorUserDetail.getString(cursorUserDetail.getColumnIndex(UniCarContract.UniCarEntry.COLUMN_SECOND_NAME));
        String email= cursorUserDetail.getString(cursorUserDetail.getColumnIndex(UniCarContract.UniCarEntry.COLUMN_EMAIL));
        String phone = cursorUserDetail.getString(cursorUserDetail.getColumnIndex(UniCarContract.UniCarEntry.COLUMN_PHONE));
        String address=cursorUserDetail.getString(cursorUserDetail.getColumnIndex(UniCarContract.UniCarEntry.COLUMN_ADDRESS));
        String area=cursorUserDetail.getString(cursorUserDetail.getColumnIndex(UniCarContract.UniCarEntry.COLUMN_AREA));
        daysHours=cursorUserDetail.getString(cursorUserDetail.getColumnIndex(UniCarContract.UniCarEntry.COLUMN_DAYS_HOURS));
        informationString=cursorUserDetail.getString(cursorUserDetail.getColumnIndex(UniCarContract.UniCarEntry.COLUMN_INFORMATION));
        double latitude=cursorUserDetail.getDouble(cursorUserDetail.getColumnIndex(UniCarContract.UniCarEntry.COLUMN_LATITUDE));
        double longtitude=cursorUserDetail.getDouble(cursorUserDetail.getColumnIndex(UniCarContract.UniCarEntry.COLUMN_LONGITUDE));

        usersLocation=new LatLng(latitude,longtitude);



        etFirstName= findViewById(R.id.edit_text_name);
        etEmail=  findViewById(R.id.edit_text_email);
        etPhone=  findViewById(R.id.edit_text_phone);
        etAddress= findViewById(R.id.edit_text_address);
        etZipcode= findViewById(R.id.edit_text_zipcode);
        etArea= findViewById(R.id.edit_text_area);
        tvHeader= findViewById(R.id.text_view_header_create_profile);
        //clearEditTexts();



        etFirstName.setText(String.format("%s %s", StringUtils.upperCaseFirstLetter(firstName), StringUtils.upperCaseFirstLetter(secondName)));
        etEmail.setText(email);
        etPhone.setText(phone);
        if(address!=null) {
            String[] addressZipcode = StringUtils.seperateAddress(address.trim());
            if (!(addressZipcode[0].equals("null")) && addressZipcode[0] != null)
                etAddress.setText(addressZipcode[0]);
            if (addressZipcode[1] != null && !(addressZipcode[1].equals("null")))
                etZipcode.setText(addressZipcode[1]);

        }
        if(area!=null) {
            etArea.setText(area);
        }
        tvHeader.setText("Επεξεργασία Προφιλ");

    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap=gMap;
        if(usersLocation.latitude!=0.0 || usersLocation.longitude!=0.0) {
            OpenMapUtils.initMap(UpdateProfileActivity.this, googleMap, usersLocation, sindosLatLog);
        }
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                usersLocation=latLng;
                OpenMapUtils.initMap(UpdateProfileActivity.this,googleMap,usersLocation,sindosLatLog);
            }
        });
    }

    private void userAction(){
        final Button  findAddressButton= findViewById(R.id.button_find_address);
        Button  nextButton = findViewById(R.id.action_next);
        findAddressButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                zipcodeString=etZipcode.getText().toString();
                addressString=etAddress.getText().toString();
                areaString= etArea.getText().toString();
                String testAddressString=etAddress.getText().toString().trim();
                if(testAddressString.length()!=0) {
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(UpdateProfileActivity.this);
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (usersLocation != null) {
                    setContentView(R.layout.offer_a_ride_page_2);
                    setDaysHours();
                    final EditText etInformation=findViewById(R.id.edit_text_information);
                    etInformation.setText(informationString);
                    Button finishButton = findViewById(R.id.action_finish);
                    finishButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String daysHours=storeData();
                            String information = etInformation.getText().toString();
                            ContentValues cv = new ContentValues();
                            cv.put(UniCarContract.UniCarEntry.COLUMN_ADDRESS, addressString + "," + zipcodeString);
                            cv.put(UniCarContract.UniCarEntry.COLUMN_DAYS_HOURS, daysHours);
                            cv.put(UniCarContract.UniCarEntry.COLUMN_INFORMATION, information);
                            cv.put(UniCarContract.UniCarEntry.COLUMN_AREA, areaString);
                            cv.put(UniCarContract.UniCarEntry.COLUMN_LATITUDE, usersLocation.latitude);
                            cv.put(UniCarContract.UniCarEntry.COLUMN_LONGITUDE, usersLocation.longitude);
                            getContentResolver().update(UniCarContract.UniCarEntry.CONTENT_URI,
                                    cv,
                                    UniCarContract.UniCarEntry.COLUMN_EMAIL + "=?",
                                    new String[]{usersEmail});
                            Intent mainActivityIntent = new Intent(UpdateProfileActivity.this, MainActivity.class);
                            mainActivityIntent.putExtra(Intent.EXTRA_TEXT, usersEmail);
                            startActivity(mainActivityIntent);
                        }
                    });
                }
            }
        });

    }

    public void onCheckboxClicked(View view){
        boolean checked= ((CheckBox) view).isChecked();

        switch (view.getId()){

            case R.id.check_box_monday:
                EditText mondayGo =findViewById(R.id.edit_text_time_monday_go);
                EditText mondayReturn =findViewById(R.id.edit_text_time_monday_return);
                if(checked) {
                    enableViews(mondayGo,mondayReturn);
                }else {
                    disableViews(mondayGo, mondayReturn);
                }
                break;
            case R.id.check_box_tuesday:
                EditText tuesdayGo =findViewById(R.id.edit_text_time_tuesday_go);
                EditText tuesdayReturn = findViewById(R.id.edit_text_time_tuesday_return);
                if(checked) {
                    enableViews(tuesdayGo,tuesdayReturn);
                }else {
                    disableViews(tuesdayGo, tuesdayReturn);
                }
                break;
            case R.id.check_box_wensday:
                EditText wensdayGo =findViewById(R.id.edit_text_time_wensday_go);
                EditText wensdayReturn =findViewById(R.id.edit_text_time_wensday_return);
                if(checked) {
                    enableViews(wensdayGo,wensdayReturn);
                }else {
                    disableViews(wensdayGo, wensdayReturn);
                }
                break;
            case R.id.check_box_thursday:
                EditText thursdayGo =findViewById(R.id.edit_text_time_thursday_go);
                EditText thursdayReturn = findViewById(R.id.edit_text_time_thursday_return);
                if(checked) {
                    enableViews(thursdayGo,thursdayReturn);
                }else {
                    disableViews(thursdayGo, thursdayReturn);
                }
                break;
            case R.id.check_box_friday:
                EditText fridayGo = findViewById(R.id.edit_text_time_friday_go);
                EditText fridayReturn =findViewById(R.id.edit_text_time_friday_return);
                if(checked) {
                    enableViews(fridayGo,fridayReturn);
                }else {
                    disableViews(fridayGo, fridayReturn);
                }
                break;
        }
    }

    private void enableViews(View viewGo, View viewReturn){
        if(viewGo instanceof EditText && viewReturn instanceof EditText){
            viewGo.setBackground(getDrawable(R.drawable.rounded_edit_text));
            viewReturn.setBackground(getDrawable(R.drawable.rounded_edit_text));
            viewReturn.setEnabled(true);
            viewReturn.setFocusableInTouchMode(true);
            viewReturn.invalidate();
            viewGo.setEnabled(true);
            viewGo.setFocusableInTouchMode(true);
            viewGo.requestFocus();
            viewGo.invalidate();
        }
    }

    private void disableViews(View viewGo, View viewReturn){
        if(viewGo instanceof EditText && viewReturn instanceof EditText){
            viewGo.setBackground(getDrawable(R.drawable.rounded_edit_text_disabled));
            viewReturn.setBackground(getDrawable(R.drawable.rounded_edit_text_disabled));
            viewGo.setEnabled(false);
            viewGo.setFocusable(false);
            viewReturn.setEnabled(false);
            viewReturn.setFocusable(false);
            ((EditText) viewGo).setText("");
            ((EditText) viewReturn).setText("");
        }
    }

    private String storeData(){
        CheckBox monday=findViewById(R.id.check_box_monday);
        CheckBox tuesday= findViewById(R.id.check_box_tuesday);
        CheckBox wensday= findViewById(R.id.check_box_wensday);
        CheckBox thursday=findViewById(R.id.check_box_thursday);
        CheckBox friday= findViewById(R.id.check_box_friday);
        StringBuilder stringBuilder=new StringBuilder();
        if(monday.isChecked()) {
            EditText mondayGo = findViewById(R.id.edit_text_time_monday_go);
            EditText mondayReturn =findViewById(R.id.edit_text_time_monday_return);
            stringBuilder.append("Δευτέρα:").append(mondayGo.getText().toString()).append("-").append(mondayReturn.getText().toString()).append("\n");
        }
        if (tuesday.isChecked()) {
            EditText tuesdayGo =findViewById(R.id.edit_text_time_tuesday_go);
            EditText tuesdayReturn =findViewById(R.id.edit_text_time_tuesday_return);
            stringBuilder.append("Τρίτη:").append(tuesdayGo.getText().toString()).append("-").append(tuesdayReturn.getText().toString()).append("\n");
        }
        if (wensday.isChecked()) {
            EditText wensdayGo =findViewById(R.id.edit_text_time_wensday_go);
            EditText wensdayReturn = findViewById(R.id.edit_text_time_wensday_return);
            stringBuilder.append("Τετάρτη:").append(wensdayGo.getText().toString()).append("-").append(wensdayReturn.getText().toString()).append("\n");
        }
        if (thursday.isChecked()) {
            EditText thursdayGo = findViewById(R.id.edit_text_time_thursday_go);
            EditText thursdayReturn =findViewById(R.id.edit_text_time_thursday_return);
            stringBuilder.append("Πέμπτη:").append(thursdayGo.getText().toString()).append("-").append(thursdayReturn.getText().toString()).append("\n");
        }
        if (friday.isChecked()) {
            EditText fridayGo = findViewById(R.id.edit_text_time_friday_go);
            EditText fridayReturn =findViewById(R.id.edit_text_time_friday_return);
            stringBuilder.append("Παρασκευή:").append(fridayGo.getText().toString()).append("-").append(fridayReturn.getText().toString()).append("\n");
        }
        return stringBuilder.toString();
    }

    private void setDaysHours(){
        if(daysHours!=null) {
            String[] day = StringUtils.seperateDays("Δευτέρα", daysHours);
            if (day != null) {
                CheckBox monday = findViewById(R.id.check_box_monday);
                EditText mondayGo = findViewById(R.id.edit_text_time_monday_go);
                EditText mondayReturn = findViewById(R.id.edit_text_time_monday_return);
                monday.setChecked(true);
                enableViews(mondayGo, mondayReturn);
                mondayGo.setText(day[0]);
                mondayReturn.setText(day[1]);
            }
            day = StringUtils.seperateDays("Τρίτη", daysHours);
            if (day != null) {
                CheckBox tuesday = findViewById(R.id.check_box_tuesday);
                EditText tuesdayGo = findViewById(R.id.edit_text_time_tuesday_go);
                EditText tuesdayReturn = findViewById(R.id.edit_text_time_tuesday_return);
                tuesday.setChecked(true);
                enableViews(tuesdayGo, tuesdayReturn);
                tuesdayGo.setText(day[0]);
                tuesdayReturn.setText(day[1]);
            }
            day = StringUtils.seperateDays("Τετάρτη", daysHours);
            if (day != null) {
                CheckBox wensday = findViewById(R.id.check_box_wensday);
                EditText wensdayGo = findViewById(R.id.edit_text_time_wensday_go);
                EditText wensdayReturn = findViewById(R.id.edit_text_time_wensday_return);
                wensday.setChecked(true);
                enableViews(wensdayGo, wensdayReturn);
                wensdayGo.setText(day[0]);
                wensdayReturn.setText(day[1]);
            }
            day = StringUtils.seperateDays("Πέμπτη", daysHours);
            if (day != null) {
                CheckBox thursday = findViewById(R.id.check_box_thursday);
                EditText thursdayGo = findViewById(R.id.edit_text_time_thursday_go);
                EditText thursdayReturn = findViewById(R.id.edit_text_time_thursday_return);
                thursday.setChecked(true);
                enableViews(thursdayGo, thursdayReturn);
                thursdayGo.setText(day[0]);
                thursdayReturn.setText(day[1]);
            }
            day = StringUtils.seperateDays("Παρασκευή", daysHours);
            if (day != null) {
                CheckBox friday = findViewById(R.id.check_box_friday);
                EditText fridayGo = findViewById(R.id.edit_text_time_friday_go);
                EditText fridayReturn = findViewById(R.id.edit_text_time_friday_return);
                friday.setChecked(true);
                enableViews(fridayGo, fridayReturn);
                fridayGo.setText(day[0]);
                fridayReturn.setText(day[1]);
            }
        }
    }

    /*private void clearEditTexts(){
        etFirstName.setText("");
        etEmail.setText("");
        etPhone.setText("");
        etAddress.setText("");
        etZipcode.setText("");
        etArea.setText("");
    }*/
}
