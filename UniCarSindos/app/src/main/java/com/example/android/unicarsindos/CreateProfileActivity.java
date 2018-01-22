package com.example.android.unicarsindos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.unicarsindos.data.UniCarContract;
import com.example.android.unicarsindos.utilities.OpenMapUtils;
import com.example.android.unicarsindos.utilities.StringUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class CreateProfileActivity extends AppCompatActivity
              implements OnMapReadyCallback{


    private Button vButtonOfferRide,vButtonTakeRide;
    private String addressString;
    private String zipcodeString;
    private String usersEmail;
    private String areaString;
    private Cursor cursorUserDetail;
    private LatLng sindosLatLog;
    private LatLng userLocation;
    private GoogleMap googleMap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_type);

        Intent intentThatStartedThisActivity = getIntent();


        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                usersEmail = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
            }
        }

        sindosLatLog=  new LatLng(40.658315, 22.803919);
        vButtonOfferRide = findViewById(R.id.button_offer_ride);
        vButtonTakeRide = findViewById(R.id.button_take_ride);

        userAction();
     }


     private void userAction(){

        vButtonOfferRide.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 setContentView(R.layout.offer_a_ride_page_1);
                 try{
                     cursorUserDetail=getContentResolver().query(
                             UniCarContract.UniCarEntry.CONTENT_URI,
                            null,
                             UniCarContract.UniCarEntry.COLUMN_EMAIL+"=?",
                             new String[]{usersEmail},
                             null);
                 }catch (Exception e){
                     e.printStackTrace();
                     cursorUserDetail=null;
                 }
                 fillInfo();
                 final Button  findAddressButton= findViewById(R.id.button_find_address);
                 Button  nextButton = findViewById(R.id.action_next);
                 findAddressButton.setOnClickListener(new View.OnClickListener(){

                     @Override
                     public void onClick(View view) {
                         final EditText address= findViewById(R.id.edit_text_address);
                         final EditText zipcode= findViewById(R.id.edit_text_zipcode);
                         final EditText area= findViewById(R.id.edit_text_area);
                         zipcodeString=zipcode.getText().toString();
                         addressString=address.getText().toString();
                         areaString= area.getText().toString();
                         String testAddressString=address.getText().toString().trim();
                         if(testAddressString.length()!=0) {
                             SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                     .findFragmentById(R.id.map);
                             mapFragment.getMapAsync(CreateProfileActivity.this);
                         }
                     }
                 });

                 nextButton.setOnClickListener(new View.OnClickListener(){

                     @Override
                     public void onClick(View view) {
                         if(userLocation!=null) {
                             setContentView(R.layout.offer_a_ride_page_2);
                             Button finishButton = findViewById(R.id.action_finish);
                             finishButton.setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View view) {
                                     String dayshours = storeDaysHours();
                                     EditText etInformation = findViewById(R.id.edit_text_information);
                                     String information = etInformation.getText().toString();
                                     ContentValues cv = new ContentValues();
                                     cv.put(UniCarContract.UniCarEntry.COLUMN_ADDRESS, addressString + "," + zipcodeString);
                                     cv.put(UniCarContract.UniCarEntry.COLUMN_DAYS_HOURS, dayshours);
                                     cv.put(UniCarContract.UniCarEntry.COLUMN_INFORMATION, information);
                                     cv.put(UniCarContract.UniCarEntry.COLUMN_AREA, areaString);
                                     cv.put(UniCarContract.UniCarEntry.COLUMN_LATITUDE, userLocation.latitude);
                                     cv.put(UniCarContract.UniCarEntry.COLUMN_LONGITUDE, userLocation.longitude);
                                     cv.put(UniCarContract.UniCarEntry.COLUMN_OFFER_RIDE, 1);
                                     cv.put(UniCarContract.UniCarEntry.COLUMN_HAS_PROFILE, 1);
                                     getContentResolver().update(UniCarContract.UniCarEntry.CONTENT_URI,
                                             cv,
                                             UniCarContract.UniCarEntry.COLUMN_EMAIL + "=?",
                                             new String[]{usersEmail});
                                     Intent mainActivityIntent = new Intent(CreateProfileActivity.this, MainActivity.class);
                                     mainActivityIntent.putExtra(Intent.EXTRA_TEXT, usersEmail);
                                     startActivity(mainActivityIntent);
                                 }
                             });
                         }else{
                             Toast.makeText(CreateProfileActivity.this, "Πρέπει να διαλέξετε περιοχή στο χάρτη"
                                     , Toast.LENGTH_SHORT).show();
                         }
                     }
                 });
             }
         });

         vButtonTakeRide.setOnClickListener(new View.OnClickListener() {

             @Override
             public void onClick(View view) {
                 Intent startMainActivity= new Intent(CreateProfileActivity.this, MainActivity.class);
                 startMainActivity.putExtra(Intent.EXTRA_TEXT,usersEmail);
                 startActivity(startMainActivity);
             }
         });
     }


    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap=gMap;
        userLocation = getLocationFromAddress(this,addressString,zipcodeString);
        OpenMapUtils.initMap(CreateProfileActivity.this,googleMap,userLocation,sindosLatLog);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                userLocation=latLng;
                OpenMapUtils.initMap(CreateProfileActivity.this,googleMap,userLocation,sindosLatLog);
            }
        });


    }


    public LatLng getLocationFromAddress(Context context, String strAddress,String strZipcode)
    {
        Geocoder coder= new Geocoder(context);
        List<Address> address;
        LatLng locationLatLng = null;
        strAddress+=","+strZipcode;

        try
        {
            address = coder.getFromLocationName(strAddress, 5);
            if(address==null)
            {
                return null;
            }
            Address location = address.get(0);

            locationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return locationLatLng;

    }

    private void fillInfo(){
        cursorUserDetail.moveToFirst();
        String firstName = cursorUserDetail.getString(cursorUserDetail.getColumnIndex(UniCarContract.UniCarEntry.COLUMN_FIRST_NAME));
        String secondName= cursorUserDetail.getString(cursorUserDetail.getColumnIndex(UniCarContract.UniCarEntry.COLUMN_SECOND_NAME));
        String email= cursorUserDetail.getString(cursorUserDetail.getColumnIndex(UniCarContract.UniCarEntry.COLUMN_EMAIL));
        String phone = cursorUserDetail.getString(cursorUserDetail.getColumnIndex(UniCarContract.UniCarEntry.COLUMN_PHONE));

        EditText etFirstName= findViewById(R.id.edit_text_name);
        EditText etEmail=  findViewById(R.id.edit_text_email);
        EditText etPhone=  findViewById(R.id.edit_text_phone);
        TextView tvHeader= findViewById(R.id.text_view_header_create_profile);

        etFirstName.setText(String.format("%s %s", StringUtils.upperCaseFirstLetter(firstName), StringUtils.upperCaseFirstLetter(secondName)));
        etEmail.setText(email);
        etPhone.setText(phone);
        tvHeader.setText("Δημιουργία Προφίλ");
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

    private String storeDaysHours(){
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

    @Override
    public void onBackPressed() {
        backToLogin();
    }


    private void backToLogin(){
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }

        builder.setTitle("Έξοδος;").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent backToLogingIntent= new Intent(CreateProfileActivity.this,LoginActivity.class);
                startActivity(backToLogingIntent);
            }
        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
            }
        }).setIcon(android.R.drawable.ic_dialog_dialer).show();
    }
}
