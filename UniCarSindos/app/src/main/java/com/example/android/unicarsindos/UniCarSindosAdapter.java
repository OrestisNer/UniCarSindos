package com.example.android.unicarsindos;

import android.content.Intent;
import android.database.Cursor;
import android.view.View;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.unicarsindos.data.UniCarContract;
import com.example.android.unicarsindos.utilities.StringUtils;

import org.w3c.dom.Text;

public class UniCarSindosAdapter extends RecyclerView.Adapter<UniCarSindosAdapter.UniCarSindosAdapterViewHolder> {

    private Cursor mCursor;

    private final Context mContext;

    private final AdapterOnClickHandler mClickHandler;

    public interface AdapterOnClickHandler {
        void onClick(String usersName);
    }

    public UniCarSindosAdapter(AdapterOnClickHandler clickHandler,Context context) {
        mContext=context;
        mClickHandler = clickHandler;
    }

    @Override
    public UniCarSindosAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.list_item,viewGroup,false);
        view.setFocusable(true);
        return new UniCarSindosAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UniCarSindosAdapterViewHolder adapterViewHolder, int position) {
        mCursor.moveToPosition(position);

        String first_name=mCursor.getString(mCursor.getColumnIndex(UniCarContract.UniCarEntry.COLUMN_FIRST_NAME));
        String second_name=mCursor.getString(mCursor.getColumnIndex(UniCarContract.UniCarEntry.COLUMN_SECOND_NAME));
        String area= mCursor.getString(mCursor.getColumnIndex(UniCarContract.UniCarEntry.COLUMN_AREA));
        int id=mCursor.getInt(mCursor.getColumnIndex(UniCarContract.UniCarEntry._ID));
        first_name= StringUtils.upperCaseFirstLetter(first_name);
        second_name=StringUtils.upperCaseFirstLetter(second_name);
        adapterViewHolder.mUserNameTextView.setText(String.format("%s %s",first_name, second_name));
        adapterViewHolder.mUserAreaTextView.setText(StringUtils.upperCaseFirstLetter(area));
        adapterViewHolder.mUserIDTextView.setText(String.valueOf(id));
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }


    public class UniCarSindosAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mUserNameTextView;
        public final TextView mUserAreaTextView;
        public final TextView mUserIDTextView;

        public UniCarSindosAdapterViewHolder(View view) {
            super(view);
            mUserNameTextView =  view.findViewById(R.id.text_view_user_data);
            mUserAreaTextView = view.findViewById(R.id.text_view_area);
            mUserIDTextView=view.findViewById(R.id.text_view_id);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String selectedUserID = mUserIDTextView.getText().toString();
            mClickHandler.onClick(selectedUserID);
        }
    }
}