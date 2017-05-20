package com.peregrin.activities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.peregrin.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class MessageEntryAdapter extends BaseAdapter {

    private ArrayList<HashMap<String, String>> messages;
    private String userLogin;
    private LayoutInflater inflater;

    MessageEntryAdapter(Context context, ArrayList<HashMap<String, String>> messages) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.userLogin = context
                .getSharedPreferences("user", Context.MODE_PRIVATE)
                .getString("phone", null);

        this.messages = messages;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.message_layout, parent, false);
        }

        TextView tvMessage = (TextView) view.findViewById(
                (messages.get(position).get("sender_login").equals(userLogin) ?
                        R.id.tvUserMessage :
                        R.id.tvInterlocutorMessage
                )
        );

        tvMessage.setVisibility(View.VISIBLE);

        tvMessage.setText(messages.get(position).get("content"));

        return view;
    }
}
