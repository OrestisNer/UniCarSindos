package com.example.android.unicarsindos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.example.android.unicarsindos.data.UniCarContract;
import com.example.android.unicarsindos.data.UniCarDbHelper;
import com.example.android.unicarsindos.utilities.StringUtils;

public class MainActivity extends AppCompatActivity implements UniCarSindosAdapter.AdapterOnClickHandler {

    private UniCarSindosAdapter mAdapter;
    private ProgressBar mLoadingIndicator;
    private String usersEmail;
    private SQLiteDatabase db;
    private UniCarDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView mRecyclerView = findViewById(R.id.recyclerview_main);
        Intent intentThatStartedThisActivity = getIntent();


        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                usersEmail = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
            }
        }

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new UniCarSindosAdapter(this,this);

        dbHelper=new UniCarDbHelper(this);
        db=dbHelper.getReadableDatabase();
        String query="SELECT * FROM "+ UniCarContract.UniCarEntry.TABLE_NAME+" WHERE "
                + UniCarContract.UniCarEntry.COLUMN_EMAIL+"!=?"+" AND "+ UniCarContract.UniCarEntry.COLUMN_OFFER_RIDE+"=?";
        Cursor cursor=db.rawQuery(query,new String[]{usersEmail,"1"});
        mAdapter.swapCursor(cursor);
        mRecyclerView.setAdapter(mAdapter);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

    }



    @Override
    public void onClick(String selectedUserID) {
        /*String[] seperatedNames= StringUtils.seperateName(selectedUser);
        String first_name=seperatedNames[0].toLowerCase();
        String second_name=seperatedNames[1].toLowerCase();
        Cursor cursor =getContentResolver().query(UniCarContract.UniCarEntry.CONTENT_URI,
                                 new String[]{UniCarContract.UniCarEntry.COLUMN_EMAIL},
                UniCarContract.UniCarEntry.COLUMN_FIRST_NAME+"=? AND "+ UniCarContract.UniCarEntry.COLUMN_SECOND_NAME+"=?",
                 new String[]{first_name,second_name},
                null);*/
        Cursor cursor =getContentResolver().query(UniCarContract.UniCarEntry.CONTENT_URI,
                new String[]{UniCarContract.UniCarEntry.COLUMN_EMAIL},
                UniCarContract.UniCarEntry._ID+"=?",
                new String[]{selectedUserID},
                null);
        if(cursor!=null) {
            cursor.moveToFirst();
            String email = cursor.getString(cursor.getColumnIndex(UniCarContract.UniCarEntry.COLUMN_EMAIL));
            Intent profileActivity = new Intent(MainActivity.this, ProfileActivity.class);
            profileActivity.putExtra("usersEmail", usersEmail);
            profileActivity.putExtra("selectedUser", email);
            startActivity(profileActivity);
            cursor.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_activity, menu);
        MenuItem title= menu.findItem(R.id.go_to_profile);
        /*Cursor mCursor=getContentResolver().query(UniCarContract.UniCarEntry.CONTENT_URI,
                                   new String[]{UniCarContract.UniCarEntry.COLUMN_FIRST_NAME},
                                  UniCarContract.UniCarEntry.COLUMN_EMAIL+"=?",
                                   new String[]{usersEmail},
                                  null);*/
        /*if(mCursor!=null) {
            mCursor.moveToFirst();
            String firstName = mCursor.getString(mCursor.getColumnIndex(UniCarContract.UniCarEntry.COLUMN_FIRST_NAME));
            title.setTitle(StringUtils.upperCaseFirstLetter(firstName));
            mCursor.close();
        }*/

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id= item.getItemId();
        if(id==R.id.go_to_profile){
            Intent profileIntent= new Intent(MainActivity.this,ProfileActivity.class);
            profileIntent.putExtra("usersEmail",usersEmail);
            profileIntent.putExtra("selectedUser",usersEmail);
            startActivity(profileIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }

        builder.setTitle("Έξοδος;").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                 Intent logOutIntent= new Intent(MainActivity.this,LoginActivity.class);
                 startActivity(logOutIntent);
            }
        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
            }
        }).setIcon(android.R.drawable.ic_dialog_alert).show();
    }
}
