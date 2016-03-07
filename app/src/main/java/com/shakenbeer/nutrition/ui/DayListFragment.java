package com.shakenbeer.nutrition.ui;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.shakenbeer.nutrition.CarbculatorApplication;
import com.shakenbeer.nutrition.R;
import com.shakenbeer.nutrition.model.DataCursor;
import com.shakenbeer.nutrition.model.Day;
import com.shakenbeer.nutrition.model.DayCursorLoader;
import com.shakenbeer.nutrition.model.Eating;
import com.shakenbeer.nutrition.model.NutritionLab;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public class DayListFragment extends ListFragment implements LoaderCallbacks<Cursor>, LanguageDialogFragment.Callbacks {

    private static final int OPEN_DAY = 1;
    private static final int ADD_EATING = 2;
    private static final int FOOD_LIST = 3;

    private NutritionLab nutritionLab;
    private DataCursor<Day> dayCursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        setHasOptionsMenu(true);
        
        CarbculatorApplication.changeLocale(getActivity().getApplicationContext());

        nutritionLab = NutritionLab.getInstance(getActivity().getApplicationContext());

        getLoaderManager().initLoader(0, null, this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day_list, container, false);
        initializeWidgets(view);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
            case OPEN_DAY:
            case FOOD_LIST:
            case ADD_EATING:
                getLoaderManager().restartLoader(0, null, this);
                break;
            default:
                break;
            }
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        dayCursor.moveToPosition(position);
        openDay(dayCursor.get());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_day_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.add_eating_menu_item:
            addEating();
            return true;
        case R.id.food_list_menu_item:
            openFoodList();
            return true;
        case R.id.language_list_menu_item:
            LanguageDialogFragment fragment = LanguageDialogFragment.newInstance(this);
            fragment.show(getFragmentManager(), null);
            return true;
        case R.id.statistics_menu_item:
            Intent intent = new Intent(getActivity(), StatisticsActivity.class);
            startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new DayCursorLoader(getActivity());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        dayCursor = (DataCursor<Day>) data;
        setListAdapter(new DayCursorAdapter(getActivity(), dayCursor));

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        setListAdapter(null);
    }

    private class AsyncDeleteDey extends AsyncTask<Day, Void, Void> {

        @Override
        protected Void doInBackground(Day... params) {
            for (int i = 0; i < params.length; i++) {
                nutritionLab.deleteDay(params[i]);
            }
            getLoaderManager().restartLoader(0, null, DayListFragment.this);
            return null;
        }

    }

    private void initializeWidgets(View view) {

        LinearLayout empty = (LinearLayout) view.findViewById(android.R.id.empty);
        if (empty != null) {
            TextView dateTextView = (TextView) empty.findViewById(R.id.name_text_view);
            DateFormat df = DateFormat.getDateInstance(DateFormat.LONG,
                    getActivity().getResources().getConfiguration().locale);
            String dateSting = df.format(new Date());
            dateTextView.setText(dateSting);
            empty.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    openDay(new Day());
                }
            });
        }

        ListView listView = (ListView) view.findViewById(android.R.id.list);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);

        listView.setMultiChoiceModeListener(new NutritionMultiChoiceModeListener() {

            @Override
            public void deleteSelectedItems() {
                DayCursorAdapter adapter = (DayCursorAdapter) getListAdapter();
                List<Day> toDelete = new ArrayList<>();
                for (int i = adapter.getCount() - 1; i >= 0; i--) {
                    if (getListView().isItemChecked(i)) {
                        adapter.getCursor().moveToPosition(i);
                        dayCursor.moveToPosition(i);
                        toDelete.add(dayCursor.get());
                    }
                }
                new AsyncDeleteDey().execute(toDelete.toArray(new Day[toDelete.size()]));
            }
        });
    }

    private void openDay(Day day) {
        Intent intent = new Intent(getActivity(), DayActivity.class);
        intent.putExtra(DayFragment.EXTRA_DAY, day);
        startActivityForResult(intent, OPEN_DAY);
    }

    private void addEating() {
        Intent intent = new Intent(getActivity(), EatingActivity.class);
        intent.putExtra(EatingFragment.EXTRA_EATING, new Eating(new Date()));
        startActivityForResult(intent, ADD_EATING);
    }

    private void openFoodList() {
        Intent intent = new Intent(getActivity(), FoodListActivity.class);
        startActivityForResult(intent, FOOD_LIST);
    }

    @Override
    public void onItemSelected(int which) {
        String newLocale = "en";

        switch (which) {
        case 0:
            newLocale = "en";
            break;
        case 1:
            newLocale = "ru_RU";
            break;
        case 2:
            newLocale = "uk_UK";
            break;
        default:
            break;
        }

        PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).edit()
                .putString(CarbculatorApplication.APP_LANG, newLocale).commit();
        
        CarbculatorApplication.changeLocale(getActivity().getApplicationContext());
        
        getActivity().recreate();
    }
}
