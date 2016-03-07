package com.shakenbeer.nutrition.ui;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.shakenbeer.nutrition.R;
import com.shakenbeer.nutrition.model.Day;
import com.shakenbeer.nutrition.model.Eating;
import com.shakenbeer.nutrition.model.NutritionLab;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public class DayFragment extends ListFragment {

    public static final String EXTRA_DAY = "com.shakenbeer.nutrition.ui.DayFragment.day";
    private static final int OPEN_EATING = 1;
    private static final int FOOD_LIST = 2;

    private NutritionLab nutritionLab;
    private Day day;
    private EatingArrayAdapter eatingAdapter;
    LinearLayout dayView;

    public static DayFragment newInstance(Day day) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_DAY, day);
        DayFragment fragment = new DayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        setHasOptionsMenu(true);

        nutritionLab = NutritionLab.getInstance(getActivity().getApplicationContext());

        day = (Day) getArguments().getParcelable(EXTRA_DAY);

        new AsyncEatingsLoad().execute(day);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day, container, false);
        initializeWidgets(view);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_day, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            getActivity().finish();
            return true;
        case R.id.add_eating_menu_item:
            Calendar target = mergeDateTime();
            openEating(new Eating(target.getTime()));
            return true;
        case R.id.delete_day_menu_item:
            deleteDay();
            return true;
        case R.id.food_list_menu_item:
            openFoodList();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Eating eating = (Eating) getListAdapter().getItem(position);
        openEating(eating);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
            case OPEN_EATING:
            case FOOD_LIST:
                getActivity().setResult(Activity.RESULT_OK);
                new AsyncEatingsLoad().execute(day);
                break;
            default:
                break;
            }
        }
    }

    private class AsyncEatingsLoad extends AsyncTask<Day, Void, List<Eating>> {

        @Override
        protected List<Eating> doInBackground(Day... params) {
            return nutritionLab.getEatings(day);
        }

        @Override
        protected void onPostExecute(List<Eating> result) {
            eatingAdapter = new EatingArrayAdapter(getActivity(), result);
            setListAdapter(eatingAdapter);
            refreshDay();
        }
    }

    private class AsyncDeleteEating extends AsyncTask<Eating, Void, Void> {

        @Override
        protected Void doInBackground(Eating... params) {
            for (int i = 0; i < params.length; i++) {
                nutritionLab.deleteEating(params[i]);
            }
            return null;
        }

    }

    private void initializeWidgets(View view) {
        dayView = (LinearLayout) view.findViewById(R.id.day_view);
        udpateDayView();

        Button addEatingButton = (Button) view.findViewById(R.id.add_eating_button);
        addEatingButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar target = mergeDateTime();
                openEating(new Eating(target.getTime()));
            }
        });

        ListView listView = (ListView) view.findViewById(android.R.id.list);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);

        listView.setMultiChoiceModeListener(new NutritionMultiChoiceModeListener() {

            @Override
            public void deleteSelectedItems() {
                List<Eating> toDelete = new ArrayList<>();
                for (int i = 0; i < eatingAdapter.getCount(); i++) {
                    if (getListView().isItemChecked(i)) {
                        Eating eating = eatingAdapter.getItem(i);
                        toDelete.add(eating);
                        eatingAdapter.remove(eating);
                    }
                }
                refreshDay();
                new AsyncDeleteEating().execute(toDelete.toArray(new Eating[toDelete.size()]));
                getActivity().setResult(Activity.RESULT_OK);
            }
        });
    }

    private void refreshDay() {
        List<Eating> eatings = new ArrayList<>();
        for (int i = 0; i < eatingAdapter.getCount(); i++) {
            eatings.add(eatingAdapter.getItem(i));
        }
        float protein = 0;
        float fat = 0;
        float carbs = 0;
        float kcal = 0;
        for (Eating eating : eatings) {
            protein += eating.getProtein();
            fat += eating.getFat();
            carbs += eating.getCarbs();
            kcal += eating.getKcal();
        }
        day.setProtein(protein);
        day.setFat(fat);
        day.setCarbs(carbs);
        day.setKcal(kcal);
        udpateDayView();
    }

    private void udpateDayView() {
        TextView dateTextView = (TextView) dayView.findViewById(R.id.name_text_view);
        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG,
                getActivity().getResources().getConfiguration().locale);
        String dateSting = df.format(day.getDate());
        dateTextView.setText(dateSting);

        TextView proteinTextView = (TextView) dayView.findViewById(R.id.protein_text_view);
        proteinTextView.setText(getActivity().getResources().getString(R.string.protein_colon)
                + String.format("%.1f", day.getProtein()));

        TextView carbsTextView = (TextView) dayView.findViewById(R.id.carbs_text_view);
        carbsTextView.setText(getActivity().getResources().getString(R.string.carbs_colon)
                + String.format("%.1f", day.getCarbs()));

        TextView fatTextView = (TextView) dayView.findViewById(R.id.fat_text_view);
        fatTextView.setText(getActivity().getResources().getString(R.string.fat_colon)
                + String.format("%.1f", day.getFat()));

        TextView kcalTextView = (TextView) dayView.findViewById(R.id.kcal_text_view);
        kcalTextView.setText(getActivity().getResources().getString(R.string.kcal_colon)
                + String.format("%.1f", day.getKcal()));

        TextView pdcTextView = (TextView) dayView.findViewById(R.id.pfc_ratio);
        pdcTextView.setText(getActivity().getResources().getString(R.string.pfc_ratio) + day.getPfcRatio());
    }

    private void openEating(Eating eating) {
        Intent intent = new Intent(getActivity(), EatingActivity.class);
        intent.putExtra(EatingFragment.EXTRA_EATING, eating);
        startActivityForResult(intent, OPEN_EATING);
    }

    private void deleteDay() {
        Eating[] toDelete = new Eating[eatingAdapter.getCount()];
        for (int i = 0; i < eatingAdapter.getCount(); i++) {
            toDelete[i] = eatingAdapter.getItem(i);
            new AsyncDeleteEating().execute(toDelete);
        }
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    private void openFoodList() {
        Intent intent = new Intent(getActivity(), FoodListActivity.class);
        startActivityForResult(intent, FOOD_LIST);
    }

    private Calendar mergeDateTime() {
        Calendar current = Calendar.getInstance();
        Calendar target = Calendar.getInstance();
        target.setTime(day.getDate());
        target.set(Calendar.HOUR_OF_DAY, current.get(Calendar.HOUR_OF_DAY));
        target.set(Calendar.MINUTE, current.get(Calendar.MINUTE));
        return target;
    }
}
