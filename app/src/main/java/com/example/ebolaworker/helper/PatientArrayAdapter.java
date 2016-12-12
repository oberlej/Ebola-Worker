package com.example.ebolaworker.helper;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.ebolaworker.R;
import com.example.ebolaworker.mainScreens.TriageActivity;
import com.example.ebolaworker.model.BasicPatient;

import java.util.List;

public class PatientArrayAdapter extends ArrayAdapter<BasicPatient> {
    DatabaseHelper mDBHelper;
    LayoutInflater inflater;

    List<BasicPatient> list;

    public PatientArrayAdapter(Context context, int layoutResourceId, List<BasicPatient> data) {
        super(context, layoutResourceId, data);
        list = data;
        inflater = LayoutInflater.from(context);
        mDBHelper = DatabaseHelper.getInstance(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder holder = null;
        BasicPatient p = getItem(position);

        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            rowView = inflater.inflate(R.layout.row_patient, parent, false);
            holder = new ViewHolder(rowView,p.id);
            rowView.setTag(holder);
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), TriageActivity.class);
                    intent.putExtra(getContext().getResources().getString(R.string.PATIENT_ID), ((ViewHolder)v.getTag()).patient_id);
                    getContext().startActivity(intent);
                }
            });
        } else {
            holder = (ViewHolder) rowView.getTag();
        }
        holder.first_name.setText(p.first_name);
        holder.last_name.setText(p.last_name);
        holder.birth_date.setText(p.birth_date);

        return rowView;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public BasicPatient getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<BasicPatient> getData() {
        return list;
    }

    class ViewHolder {
        TextView first_name = null;
        TextView last_name = null;
        TextView birth_date = null;
        long patient_id = -1;

        ViewHolder(View v, long id) {
            first_name = (TextView) v.findViewById(R.id.patient_row_first_name);
            last_name = (TextView) v.findViewById(R.id.patient_row_last_name);
            birth_date = (TextView) v.findViewById(R.id.patient_row_birth_date);
            patient_id = id;
        }

    }
}