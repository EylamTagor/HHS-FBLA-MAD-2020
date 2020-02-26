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
 * Represents a dialog to confirm that the user wants to change chapters
 */
public class ChangeChapterDialog extends AppCompatDialogFragment {

    private ChangeChapterDialog.ChangeChapterDialogListener listener;

    /**
     * Creates a dialog to confirm that the user wants to change chapters
     *
     * @param savedInstanceState the previous state of the dialog
     * @return the newly created dialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_change_chapter, null);
        builder.setView(view)
                .setTitle("Change Chapter")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.sendChangeConfirmation(false);
                    }
                })
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.sendChangeConfirmation(true);
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
            listener = (ChangeChapterDialog.ChangeChapterDialogListener) context;
        } catch(ClassCastException e) {
            throw new ClassCastException(context.toString() + "Implement method");
        }
    }

    /**
     * This interface is used to send the data of this dialog to its attached activity
     */
    public interface ChangeChapterDialogListener {

        /**
         * Sends the confirmation of whether or not to change chapters to the attached activity
         *
         * @param confirm whether or not to change chapters
         */
        void sendChangeConfirmation(boolean confirm);
    }
}
