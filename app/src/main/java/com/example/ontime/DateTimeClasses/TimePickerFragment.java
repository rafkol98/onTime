package com.example.ontime.DateTimeClasses;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.ontime.R;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Calendar;

/**
 * Fragment used to select the time in the SelectTime class.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    /**
     *
     */
    public interface TimePickerListener{
        void onTimeSet(TimePicker timePicker,int hour,int minute);
    }

    TimePickerListener mListener;

    /**
     *
     * @param context of interest
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (TimePickerListener) context;
        }catch (Exception e){
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
//            try{
//                throw new ClassCastException(getActivity().toString()+" must implement TimePickerListner");
//            } catch (NullPointerException ex) {
//                FirebaseCrashlytics.getInstance().recordException(ex);
//            }
        }
    }

    /**
     * Created dialog
     * @param savedInstanceState
     * @return
     */
    @NonNull
    @Override
   public Dialog onCreateDialog(Bundle savedInstanceState){
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(), R.style.MyTimePickerDialogTheme,this, hour, minute, DateFormat.is24HourFormat(getContext()));

//        new TimePickerDialog(getActivity(), R.style.MyTimePickerDialogTheme,this, Calendar.getInstance().get(Calendar.HOUR),Calendar.getInstance().get(Calendar.MINUTE), DateFormat.is24HourFormat(getContext()));
   }

    /**
     *
     * @param view
     * @param hourOfDay
     * @param minute
     */
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        try{
        mListener.onTimeSet(view, hourOfDay, minute);}
        catch (Exception e){
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

}
