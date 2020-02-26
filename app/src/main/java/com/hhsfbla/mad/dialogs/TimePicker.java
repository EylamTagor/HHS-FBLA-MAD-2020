package com.hhsfbla.mad.dialogs;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.text.DateFormat;
import java.util.Calendar;

/**
 * Represents a Time Picker that can be used to select times for events
 */
public class TimePicker extends DialogFragment {

    /**
     * Creates a new time picker dialog to select a time, with the current time as the initial selection
     *
     * @param savedInstanceState the previous instance state of the dialog
     * @return the newly created dialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener)getActivity(), hour, minute, true);
    }
}
