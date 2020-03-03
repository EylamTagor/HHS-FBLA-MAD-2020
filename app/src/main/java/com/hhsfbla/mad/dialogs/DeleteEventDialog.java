package com.hhsfbla.mad.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.hhsfbla.mad.R;

/**
 * Represents a dialog to confirm that a user wants to delete this event
 */
public class DeleteEventDialog extends AppCompatDialogFragment {

    private DeleteEventDialogListener listener;

    /**
     * Creates a dialog that confirms whether or not the user wants to delete this event
     *
     * @param savedInstanceState the previous instance state of the dialog
     * @return the newly created dialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_delete_event, null);
        builder.setView(view)
                .setTitle("Delete Event")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.sendConfirmation(false);
                    }
                })
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.sendConfirmation(true);
                    }
                });
        return builder.create();
    }

    /**
     * Attaches a listener to the dialog from the activity calling it
     *
     * @param context the context of the activity calling the dialog
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (DeleteEventDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Implement method");
        }
    }

    /**
     * This interface is used to send the selection of this dialog to its attached activity
     */
    public interface DeleteEventDialogListener {
        /**
         * Sends the confirmation of whether or not to delete the event to the attached activity
         *
         * @param confirm whether or not to delete the event
         */
        void sendConfirmation(boolean confirm);
    }
}
