package com.peregrin.activities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.peregrin.R;

import java.util.ArrayList;


public class MessageEntryAdapter extends ArrayAdapter<String> {
    public MessageEntryAdapter(Context context, ArrayList<String> strings) {
        super(context, R.layout.user_message, strings);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final LayoutInflater inflater =
                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View entryView = inflater.inflate(R.layout.user_message, parent, false);
        final TextView entryMessage = (TextView) entryView.findViewById(R.id.tvMessage);

        final String fullMessage = getItem(position);
        if (fullMessage != null) {
            final int endOfName = fullMessage.indexOf(":") + 1;

            entryMessage.setText(fullMessage.substring(endOfName, fullMessage.length()));
        }

        return entryView;
    }
}
