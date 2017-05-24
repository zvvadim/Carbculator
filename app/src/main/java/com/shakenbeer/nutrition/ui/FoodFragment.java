package com.shakenbeer.nutrition.ui;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.shakenbeer.nutrition.R;
import com.shakenbeer.nutrition.model.Food;
import com.shakenbeer.nutrition.model.NutritionLab;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public class FoodFragment extends Fragment {

    public static final String EXTRA_FOOD = "com.shakenbeer.nutrition.ui.FoodFragment.food";
    public static final String EXTRA_REQUEST_CODE = "com.shakenbeer.nutrition.ui.FoodFragment.requestCode";
    protected static final String TAG = "FoodFragment";
    private Food food;
    NutritionLab nutritionLab;

    public static FoodFragment newInstance(Food food) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_FOOD, food);
        FoodFragment fragment = new FoodFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        setHasOptionsMenu(true);

        food = getArguments().getParcelable(EXTRA_FOOD);

        nutritionLab = NutritionLab.getInstance(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food, container, false);

        initilizeWidgets(view);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_food, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            getActivity().finish();
            return true;
        case R.id.delete_food_menu_item:
            deleteFood();
            return true;
        case R.id.save_food_menu_item:
            saveFood();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private class AsyncSaveFood extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            nutritionLab.saveFood(food);
            return null;
        }

    }

    private class AsyncDeleteFood extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            nutritionLab.deleteFood(food);
            return null;
        }

    }

    private void initilizeWidgets(View view) {
        Button saveFoodButton = (Button) view.findViewById(R.id.save_food_button);
        saveFoodButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                saveFood();
            }
        });

        EditText nameEditText = (EditText) view.findViewById(R.id.food_name_edit_text);
        nameEditText.setText(food.getName());

        nameEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                food.setName(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        EditText proteinEditText = (EditText) view.findViewById(R.id.protein_per_unit_edit_text);
        float protein = food.getProteinPerUnit();
        if (protein > 0) {
            proteinEditText.setText(String.valueOf(protein));
        }
        proteinEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty() && !s.toString().equals(".")) {                    
                    food.setProteinPerUnit(Float.parseFloat(s.toString()));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        EditText fatEditText = (EditText) view.findViewById(R.id.fat_per_unit_edit_text);
        float fat = food.getFatPerUnit();
        if (fat > 0) {
            fatEditText.setText(String.valueOf(fat));
        }
        fatEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty() && !s.toString().equals(".")) {
                    food.setFatPerUnit(Float.parseFloat(s.toString()));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        EditText carbsEditText = (EditText) view.findViewById(R.id.carbs_per_unit_edit_text);
        float carbs = food.getCarbsPerUnit();
        if (carbs > 0) {
            carbsEditText.setText(String.valueOf(carbs));
        }
        carbsEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty() && !s.toString().equals(".")) {
                    food.setCarbsPerUnit(Float.parseFloat(s.toString()));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        EditText kcalEditText = (EditText) view.findViewById(R.id.kcal_per_unit_edit_text);
        float kcal = food.getKcalPerUnit();
        if (kcal > 0) {
            kcalEditText.setText(String.valueOf(kcal));
        }
        kcalEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty() && !s.toString().equals(".")) {
                    food.setKcalPerUnit(Float.parseFloat(s.toString()));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        EditText unitEditText = (EditText) view.findViewById(R.id.unit_edit_text);
        unitEditText.setText(String.valueOf(food.getUnit()));
        unitEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    food.setUnit(Integer.parseInt(s.toString()));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        EditText unitNameEditText = (EditText) view.findViewById(R.id.unit_name_edit_text);
        
        unitNameEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    food.setUnitName(s.toString());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        
        if (food.getId() > 0) {
            unitNameEditText.setText(food.getUnitName());
            TextView newFoodTextView = (TextView) view.findViewById(R.id.new_food_text_view);
            newFoodTextView.setText(food.getName());
        } else {
            unitNameEditText.setText(getActivity().getResources().getString(R.string.grams));
        }
    }

    private void saveFood() {
        new AsyncSaveFood().execute();
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    private void deleteFood() {
        if (food.getId() > 0) {
            new AsyncDeleteFood().execute();
            if (getActivity().getIntent().getExtras().get(EXTRA_REQUEST_CODE) != null) {
                getActivity().setResult(Activity.RESULT_OK);
            }
        }
        getActivity().finish();
    }

}
