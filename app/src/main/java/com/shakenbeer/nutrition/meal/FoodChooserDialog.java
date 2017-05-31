package com.shakenbeer.nutrition.meal;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.shakenbeer.nutrition.CarbculatorApplication;
import com.shakenbeer.nutrition.R;
import com.shakenbeer.nutrition.data.NutritionLab2;
import com.shakenbeer.nutrition.model.Food;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Sviatoslav Melnychenko
 *
 */
//TODO should be refactored with MVP and PAGINATION and proper Rx SEARCH
public class FoodChooserDialog extends DialogFragment {

    interface Callbacks {
        void onItemSelected(Food food);
    }

    private ArrayAdapter<Food> foodArrayAdapter;
    private Callbacks callbacks;
    private NutritionLab2 nutritionLab2;

    public static FoodChooserDialog newInstance(Callbacks callbacks) {
        FoodChooserDialog foodChooserDialog = new FoodChooserDialog();
        foodChooserDialog.callbacks = callbacks;
        return foodChooserDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        nutritionLab2 = CarbculatorApplication.get(getActivity()).getComponent().nutritionLab2();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_food_list, (ViewGroup) this.getView(),
                false);

        final ListView foodListView = (ListView) layout.findViewById(R.id.food_list_view);
        foodListView.setOnItemClickListener((parent, view, position, id) -> {
            callbacks.onItemSelected(foodArrayAdapter.getItem(position));
            FoodChooserDialog.this.getDialog().dismiss();

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

        nutritionLab2.getFoodsRx(0, 1000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(foods -> {
                    foodArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,
                            foods);
                    foodListView.setAdapter(foodArrayAdapter);
                });

        return builder.create();
    }

}
