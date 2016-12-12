package com.example.ebolaworker.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.ebolaworker.R;
import com.example.ebolaworker.model.Symptom;

import java.util.List;

public class SymptomArrayAdapter extends ArrayAdapter<Symptom> {
    DatabaseHelper mDBHelper;
    LayoutInflater inflater;

    List<Symptom> list;

    public SymptomArrayAdapter(Context context, int layoutResourceId, List<Symptom> data) {
        super(context, layoutResourceId, data);
        list = data;
        inflater = LayoutInflater.from(context);
        mDBHelper = DatabaseHelper.getInstance(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder holder = null;

        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            rowView = inflater.inflate(R.layout.row_symptom, parent, false);
            holder = new ViewHolder(rowView);
            holder.group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    Integer pos = (Integer) group.getTag();
                    Symptom s = getItem(pos);
                    switch (checkedId) {
                        // set the Model to hold the answer the user picked
                        case R.id.symptom_radio_yes:
                            s.is_present = DatabaseHelper.SymptomPresent.YES.ordinal();
                            break;
                        case R.id.symptom_radio_no:
                            s.is_present = DatabaseHelper.SymptomPresent.NO.ordinal();
                            break;
                        case R.id.symptom_radio_unk:
                            s.is_present = DatabaseHelper.SymptomPresent.UNKNOWN.ordinal();
                            break;
                        default:
                            s.is_present = -1;
                    }

                }
            });
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        Symptom s = getItem(position);
        holder.group.setTag(position);
        holder.label.setText(s.label);

        if (s.is_present != -1) {
            RadioButton r = (RadioButton) holder.group.getChildAt(s.is_present);
            r.setChecked(true);
        } else {
            holder.group.clearCheck();
            // This is required because although the
            // Model could have the current
            // position to NONE you could be dealing
            // with a previous row where
            // the user already picked an answer.
        }
        return rowView;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Symptom getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<Symptom> getData(){
        return list;
    }

    public class ViewHolder {
        public TextView getLabel() {
            return label;
        }

        TextView label = null;

        public RadioGroup getGroup() {
            return group;
        }

        RadioGroup group;

        ViewHolder(View v) {
            label = (TextView) v.findViewById(R.id.symptom_row_label);
            group = (RadioGroup) v.findViewById(R.id.symptom_row_rg);
        }

    }
}