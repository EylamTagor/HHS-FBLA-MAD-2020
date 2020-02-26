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
 *
 */
public class ChangeChapterDialog extends AppCompatDialogFragment {

    private ChangeChapterDialog.ChangeChapterDialogListener listener;

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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (ChangeChapterDialog.ChangeChapterDialogListener) context;
        } catch(ClassCastException e) {
            throw new ClassCastException(context.toString() + "Implement method");
        }
    }

    public interface ChangeChapterDialogListener {
        void sendChangeConfirmation(boolean confirm);
    }
}
