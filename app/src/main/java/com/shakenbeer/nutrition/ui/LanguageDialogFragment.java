package com.shakenbeer.nutrition.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.shakenbeer.nutrition.R;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public class LanguageDialogFragment extends DialogFragment  {

    public interface Callbacks {
        public void onItemSelected(int which);
    }

    private Callbacks callbacks;
    
    public static LanguageDialogFragment newInstance(Callbacks callbacks) {
        LanguageDialogFragment fragment = new LanguageDialogFragment();
        fragment.setCallbacks(callbacks);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
 
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        
        builder.setItems(R.array.languages, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                callbacks.onItemSelected(which);
                dialog.dismiss();
            }
        });

        return builder.create();
    }

    public void setCallbacks(Callbacks callbacks) {
        this.callbacks = callbacks;
    }

}
