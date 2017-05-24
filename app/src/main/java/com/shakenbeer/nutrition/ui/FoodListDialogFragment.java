package com.shakenbeer.nutrition.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.shakenbeer.nutrition.R;
import com.shakenbeer.nutrition.model.Food;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public class FoodListDialogFragment extends DialogFragment {

    public interface Callbacks {
        void onItemSelected(Food food);
    }

    private ArrayAdapter<Food> foodArrayAdapter;
    private Callbacks callbacks;

    public static FoodListDialogFragment newInstance(ArrayAdapter<Food> adapter, Callbacks callbacks) {
        FoodListDialogFragment fragment = new FoodListDialogFragment();
        fragment.setFoodArrayAdapter(adapter);
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

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_food_list, (ViewGroup) this.getView(),
                false);

        ListView foodListView = (ListView) layout.findViewById(R.id.food_list_view);
        foodListView.setAdapter(foodArrayAdapter);
        foodListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                callbacks.onItemSelected(foodArrayAdapter.getItem(position));
                FoodListDialogFragment.this.getDialog().dismiss();

            }
        });

        EditText searchEditText = (EditText) layout.findViewById(R.id.search_edit_text);
        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                foodArrayAdapter.getFilter().filter(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(layout);

        return builder.create();
    }
    
    

    @Override
    public void onDismiss(DialogInterface dialog) {
        foodArrayAdapter.getFilter().filter("");
        super.onDismiss(dialog);
    }

    public void setFoodArrayAdapter(ArrayAdapter<Food> foodArrayAdapter) {
        this.foodArrayAdapter = foodArrayAdapter;
    }

    public void setCallbacks(Callbacks callbacks) {
        this.callbacks = callbacks;
    }

}
