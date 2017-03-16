package com.shakenbeer.nutrition.ui;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ListFragment;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.shakenbeer.nutrition.R;
import com.shakenbeer.nutrition.model.Component;
import com.shakenbeer.nutrition.model.Meal;
import com.shakenbeer.nutrition.model.Food;
import com.shakenbeer.nutrition.model.NutritionLab;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public class EatingFragment extends ListFragment implements OnDateSetListener, OnTimeSetListener {

    public static final String EXTRA_EATING = "com.shakenbeer.nutrition.ui.EatingFragment.meal";
    private static final int ADD_FOOD = 0;
    private static final int FOOD_LIST = 1;
    private NutritionLab nutritionLab;
    private ComponentArrayAdapter componentAdapter;
    private ArrayAdapter<Food> foodArrayAdapter;
    private List<Component> toDelete = new ArrayList<>();
    private Meal meal;
    private Button dateButton;
    private Button timeButton;
    private Locale locale;

    public static EatingFragment newInstance(Meal meal) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_EATING, meal);
        EatingFragment fragment = new EatingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        setHasOptionsMenu(true);

        locale = getActivity().getResources().getConfiguration().locale;

        nutritionLab = NutritionLab.getInstance(getActivity().getApplicationContext());

        meal = getArguments().getParcelable(EXTRA_EATING);

        new AsyncComponentLoad().execute(meal);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_eating, container, false);
        initializeWidgets(view);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ADD_FOOD || requestCode == FOOD_LIST) {
                new AsyncFoodLoader().execute();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_eating, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            getActivity().finish();
            return true;
        case R.id.add_food_menu_item:
            addFood();
            return true;
        case R.id.delete_eating_menu_item:
            deleteEating();
            return true;
        case R.id.add_component_menu_item:
            componentAdapter.add(new Component());
            return true;
        case R.id.save_eating_menu_item:
            saveEating();
            return true;
        case R.id.food_list_menu_item:
            openFoodList();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(meal.getDate());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth, hour, minute);
        meal.setDate(calendar.getTime());
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        dateButton.setText(df.format(meal.getDate()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(meal.getDate());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        calendar = new GregorianCalendar(year, month, day, hourOfDay, minute);
        meal.setDate(calendar.getTime());
        DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT, locale);
        timeButton.setText(df.format(meal.getDate()));
    }

    private class AsyncComponentLoad extends AsyncTask<Meal, Void, List<Component>> {

        @Override
        protected List<Component> doInBackground(Meal... params) {
            return nutritionLab.getComponents(params[0]);
        }

        @Override
        protected void onPostExecute(List<Component> result) {
            componentAdapter = new ComponentArrayAdapter(getActivity(), result);
            setListAdapter(componentAdapter);
        }
    }

    private class AsyncSaveEating extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            long eatingId = nutritionLab.saveEating(meal);
            for (int i = 0; i < componentAdapter.getCount(); i++) {
                nutritionLab.saveComponent(componentAdapter.getItem(i), eatingId);
            }
            for (Component c : toDelete) {
                nutritionLab.deleteComponent(c);
            }
            return null;
        }

    }

    private class AsyncDeleteEating extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            nutritionLab.deleteEating(meal);
            return null;
        }

    }

    private class AsyncFoodLoader extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            foodArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,
                    nutritionLab.getFoods());
            return null;
        }
    }

    private void initializeWidgets(View view) {
        dateButton = (Button) view.findViewById(R.id.eating_date_button);
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM,
                getActivity().getResources().getConfiguration().locale);
        dateButton.setText(df.format(meal.getDate()));
        dateButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                changeDate();
            }
        });

        timeButton = (Button) view.findViewById(R.id.eating_time_button);
        df = DateFormat.getTimeInstance(DateFormat.SHORT, getActivity().getResources().getConfiguration().locale);
        timeButton.setText(df.format(meal.getDate()));
        timeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                changeTime();

            }
        });

        Spinner eatingSpinner = (Spinner) view.findViewById(R.id.eating_name_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.eating_names,
                R.layout.spinner_item_eating);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_eating);
        eatingSpinner.setAdapter(adapter);
        eatingSpinner.setSelection(meal.getNumber());

        eatingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                meal.setNumber(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button addComponent = (Button) view.findViewById(R.id.add_component_button);
        addComponent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                componentAdapter.add(new Component());
            }
        });

        Button saveButton = (Button) view.findViewById(R.id.save_eating_button);
        saveButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                saveEating();
            }
        });

        if (meal.getId() > 0) {
            TextView newEatingTextView = (TextView) view.findViewById(R.id.new_eating_text_view);
            newEatingTextView.setText(getActivity().getResources().getStringArray(R.array.eating_names)[meal
                    .getNumber()]);
        }
    }

    private void changeDate() {
        DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(this, meal.getDate());
        datePickerFragment.show(getFragmentManager(), null);
    }

    private void changeTime() {
        TimePickerFragment timePickerFragment = TimePickerFragment.newInstance(this, meal.getDate());
        timePickerFragment.show(getFragmentManager(), null);
    }

    private void saveEating() {
        new AsyncSaveEating().execute();
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();

    }

    private void deleteEating() {
        if (meal.getId() > 0) {
            new AsyncDeleteEating().execute();
            getActivity().setResult(Activity.RESULT_OK);
        }
        getActivity().finish();

    }

    private void addFood() {
        Intent intent = new Intent(getActivity(), FoodActivity.class);
        intent.putExtra(FoodFragment.EXTRA_FOOD, new Food());
        startActivityForResult(intent, ADD_FOOD);
    }

    private void openFoodList() {
        Intent intent = new Intent(getActivity(), FoodListActivity.class);
        startActivityForResult(intent, FOOD_LIST);
    }

    private class ComponentArrayAdapter extends ArrayAdapter<Component> {

        public ComponentArrayAdapter(Context context, List<Component> components) {
            super(context, 0, components);

            new AsyncFoodLoader().execute();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Component component = getItem(position);

            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.list_item_component, null);
                EditText gramsEditText = (EditText) convertView.findViewById(R.id.component_grams_edit_text);
                gramsEditText.addTextChangedListener(new MyTextWatcher(convertView));
            }

            Button foodButton = (Button) convertView.findViewById(R.id.component_button);
            foodButton.setText(R.string.select_food);
            if (component.getFoodName() != null) {
                foodButton.setText(component.getFoodName());
            }
            foodButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    FoodListDialogFragment.newInstance(foodArrayAdapter, new SelectedFood(component)).show(
                            getActivity().getFragmentManager(), null);
                }
            });

            EditText gramsEditText = (EditText) convertView.findViewById(R.id.component_grams_edit_text);
            gramsEditText.setTag(component);
            gramsEditText.setText(String.valueOf(component.getGrams()));

            ImageButton deleteImageButton = (ImageButton) convertView.findViewById(R.id.delete_component);
            deleteImageButton.setTag(component);
            deleteImageButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Component component = (Component) v.getTag();
                    remove(component);
                    toDelete.add(component);
                }
            });

            TextView unitNameTextView = (TextView) convertView.findViewById(R.id.component_unit_name_text_view);
            unitNameTextView.setText(component.getFoodUnitName());

            return convertView;
        }

        private class SelectedFood implements FoodListDialogFragment.Callbacks {

            private Component component;

            public SelectedFood(Component component) {
                this.component = component;
            }

            @Override
            public void onItemSelected(Food food) {
                component.setFoodId(food.getId());
                component.setFoodName(food.getName());
                component.setFoodUnitName(food.getUnitName());
                ComponentArrayAdapter.this.notifyDataSetChanged();
            }

        }

        private class MyTextWatcher implements TextWatcher {

            private View view;

            private MyTextWatcher(View view) {
                this.view = view;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                EditText gramsEditText = (EditText) view.findViewById(R.id.component_grams_edit_text);
                Component component = (Component) gramsEditText.getTag();
                if (!s.toString().isEmpty()) {
                    component.setGrams(Integer.parseInt(s.toString()));
                }
            }
        }

    }
}
