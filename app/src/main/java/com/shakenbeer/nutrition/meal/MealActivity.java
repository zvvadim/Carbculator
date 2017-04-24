package com.shakenbeer.nutrition.meal;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.shakenbeer.nutrition.CarbculatorApplication;
import com.shakenbeer.nutrition.R;
import com.shakenbeer.nutrition.databinding.ActivityMealBinding;
import com.shakenbeer.nutrition.model.Component;
import com.shakenbeer.nutrition.model.Meal;
import com.shakenbeer.nutrition.util.ui.ItemListener;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

public class MealActivity extends AppCompatActivity implements MealContract.View, ItemListener,
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    public static final String MEAL_EXTRA = "com.shakenbeer.nutrition.meal.mealExtra";
    @Inject
    ComponentAdapter adapter;
    @Inject
    MealContract.Presenter presenter;
    private ActivityMealBinding binding;

    public static void start(Context context, Meal meal) {
        Intent starter = new Intent(context, MealActivity.class);
        starter.putExtra(MEAL_EXTRA, meal);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_meal);
        injectDependencies();
        initUi();
        initPresenter();
    }

    @Override
    protected void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }

    private void injectDependencies() {
        DaggerMealComponent.builder()
                .applicationComponent(CarbculatorApplication.get(this).getComponent())
                .mealModule(new MealModule(this))
                .build()
                .inject(this);
    }

    private void initUi() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.componentsRecyclerView.setLayoutManager(layoutManager);
        binding.componentsRecyclerView.
                addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));
        binding.componentsRecyclerView.setAdapter(adapter);
        binding.addComponentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onAddComponent();
            }
        });
        binding.saveEatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSaveClick();
            }
        });
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.eating_names,
                R.layout.spinner_item_eating);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_eating);
        binding.eatingNameSpinner.setAdapter(arrayAdapter);
        binding.eatingNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                presenter.onMealTypeSelected(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.eatingDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });
        binding.eatingTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog();
            }
        });
    }

    private void showDateDialog() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(binding.getMeal().getDate());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(this, this, year, month, day);
        dialog.show();
    }

    private void showTimeDialog() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(binding.getMeal().getDate());
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog dialog = new TimePickerDialog(this, this, hourOfDay,minute,true);
        dialog.show();
    }

    private void initPresenter() {
        presenter.attachView(this);
        Meal meal = getIntent().getParcelableExtra(MEAL_EXTRA);
        presenter.obtainComponents(meal);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add) {
            presenter.onAddComponent();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAction(int position) {
        presenter.onRemoveComponent(adapter.getItem(position), position);
    }

    @Override
    public void showMeal(Meal meal) {
        binding.setMeal(meal);
        binding.eatingNameSpinner.setSelection(meal.getNumber());
    }

    @Override
    public void showComponents(List<Component> components) {
        adapter.setItems(components);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showNewComponent(Component component) {
        adapter.notifyItemInserted(adapter.getItemCount() - 1);
    }

    @Override
    public void removeComponent(Component component, int position) {
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showPreviousUi() {
        onBackPressed();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        presenter.onMealDateSelected(year, month, dayOfMonth);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        presenter.onMealTimeSelected(hourOfDay, minute);
    }
}