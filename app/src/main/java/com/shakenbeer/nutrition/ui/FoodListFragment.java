package com.shakenbeer.nutrition.ui;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import com.shakenbeer.nutrition.R;
import com.shakenbeer.nutrition.model.DataCursor;
import com.shakenbeer.nutrition.model.Food;
import com.shakenbeer.nutrition.model.FoodCursorLoader;
import com.shakenbeer.nutrition.model.NutritionLab;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public class FoodListFragment extends ListFragment implements LoaderCallbacks<Cursor> {

    private static final int ADD_FOOD = 0;
    private static final int EDIT_FOOD = 1;
    private NutritionLab nutritionLab;
    private FoodCursorAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        setHasOptionsMenu(true);

        nutritionLab = NutritionLab.getInstance(getActivity().getApplicationContext());

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_list, container, false);

        initializeWidgets(view);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_food_list, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setOnQueryTextListener(new FoodQueryTextListener());
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                initLoader();
                return true;
            }
        });
    }

    private void initLoader() {
        Loader<Object> loader = getLoaderManager().getLoader(0);
        if (loader != null && !loader.isReset())
            getLoaderManager().restartLoader(0, null, FoodListFragment.this);
        else
            getLoaderManager().initLoader(0, null, FoodListFragment.this);
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
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        adapter.foodCursor.moveToPosition(position);
        editFood(adapter.foodCursor.get());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == EDIT_FOOD) {
                getActivity().setResult(Activity.RESULT_OK);
            }
            getLoaderManager().restartLoader(0, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new FoodCursorLoader(getActivity());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        DataCursor<Food> foodCursor = (DataCursor<Food>) data;
        adapter = new FoodCursorAdapter(getActivity(), foodCursor);
        setListAdapter(adapter);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        setListAdapter(null);

    }

    private class AsyncDeleteFood extends AsyncTask<Food, Void, Void> {

        @Override
        protected Void doInBackground(Food... params) {
            for (int i = 0; i < params.length; i++) {
                nutritionLab.deleteFood(params[i]);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            getLoaderManager().restartLoader(0, null, FoodListFragment.this);
        }

    }

    private void initializeWidgets(View view) {
        Button addFood = (Button) view.findViewById(R.id.add_food_button);
        addFood.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                addFood();
            }
        });

        ListView listView = (ListView) view.findViewById(android.R.id.list);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);

        listView.setMultiChoiceModeListener(new NutritionMultiChoiceModeListener() {

            @Override
            public void deleteSelectedItems() {
                FoodCursorAdapter adapter = (FoodCursorAdapter) getListAdapter();
                List<Food> toDelete = new ArrayList<>();
                for (int i = adapter.getCount() - 1; i >= 0; i--) {
                    if (getListView().isItemChecked(i)) {
                        adapter.foodCursor.moveToPosition(i);
                        toDelete.add(adapter.foodCursor.get());
                    }
                }
                new AsyncDeleteFood().execute(toDelete.toArray(new Food[toDelete.size()]));
            }
        });
    }

    private void addFood() {
        Intent intent = new Intent(getActivity(), FoodActivity.class);
        intent.putExtra(FoodFragment.EXTRA_FOOD, new Food());
        startActivityForResult(intent, ADD_FOOD);
    }

    private void editFood(Food food) {
        Intent intent = new Intent(getActivity(), FoodActivity.class);
        intent.putExtra(FoodFragment.EXTRA_FOOD, food);
        intent.putExtra(FoodFragment.EXTRA_REQUEST_CODE, EDIT_FOOD);
        startActivityForResult(intent, EDIT_FOOD);
    }

    private class FoodQueryTextListener implements SearchView.OnQueryTextListener {
        @Override
        public boolean onQueryTextSubmit(String query) {
            Log.d("FoodListFragment", "Filter, q = " + query);
            adapter.getFilter().filter(query);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    }
}
