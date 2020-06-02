package com.example.ontime.MainClasses;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

/**
 * Fragment used to select the time in the SelectTime class.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    public interface TimePickerListener{
        void onTimeSet(TimePicker timePicker,int hour,int minute);
    }

    TimePickerListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (TimePickerListener) context;
        }catch (Exception e){
            throw new ClassCastException(getActivity().toString()+" must implement TimePickerListner");
        }
    }

    //Created dialog
    @NonNull
    @Override
   public Dialog onCreateDialog(Bundle savedInstanceState){
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(),this,hour,minute, DateFormat.is24HourFormat(getContext()));
   }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mListener.onTimeSet(view,hourOfDay,minute);
    }

}
