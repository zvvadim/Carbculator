package com.shakenbeer.nutrition.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;

import com.shakenbeer.nutrition.R;
import com.shakenbeer.nutrition.model.DataCursor;
import com.shakenbeer.nutrition.model.Food;
import com.shakenbeer.nutrition.model.FoodCursorLoader;
import com.shakenbeer.nutrition.model.NutritionLab;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * @author Sviatoslav Melnychenko
 */
public class FoodListFragment extends ListFragment implements LoaderCallbacks<Cursor> {

    public static final String CARBCULATOR_FOODCATALOG_CSV = "carbculator_food_catalog.csv";
    private static final int REQUEST_IMPORT_FILE = 0;
    private static final int ADD_FOOD = 0;
    private static final int EDIT_FOOD = 1;
    private NutritionLab nutritionLab;
    private FoodCursorAdapter adapter;

    private ProgressDialog dialog;
    private Uri uriExport;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        setHasOptionsMenu(true);

        nutritionLab = NutritionLab.getInstance(getActivity().getApplicationContext());

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
            case R.id.action_upload_csv:
                exportToCsv();
                return true;
            case R.id.action_download_csv:
                importFromCsv();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void exportToCsv() {
        if (isExternalStorageWritable()) {
            new AsyncExportToCsv(getActivity()).execute();

        }
    }

    private void importFromCsv() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        setProperMimeType(intent);
        Intent i = Intent.createChooser(intent, "Choose file");
        startActivityForResult(i, REQUEST_IMPORT_FILE);
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        adapter.foodCursor.moveToPosition(position);
        editFood(adapter.foodCursor.get());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMPORT_FILE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                new AsyncImportFromCsv(getActivity()).execute(uri);
            }
        } else {

            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == EDIT_FOOD) {
                    getActivity().setResult(Activity.RESULT_OK);
                }
                getLoaderManager().restartLoader(0, null, this);
            }
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

        dialog = new ProgressDialog(getActivity());
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

    private void setProperMimeType(Intent intent) {
        PackageManager pm = getActivity().getPackageManager();
        intent.setDataAndType(uriExport, "text/csv");
        ResolveInfo info = pm.resolveActivity(intent, 0);
        if (info == null) {
            intent.setDataAndType(uriExport, "text/plain");
        }

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

    private class AsyncExportToCsv extends AsyncTask<Void, Void, Boolean> {

        private Context context;
        private String errorDescription;
        private volatile String dir;

        public AsyncExportToCsv(Context ctx) {
            context = ctx;
        }

        private boolean externalStorageAvailable() {
            return
                    Environment.MEDIA_MOUNTED
                            .equals(Environment.getExternalStorageState());
        }

        protected File getCarbculatorStorageDir() {
            ListView listView = getListView();

            File path = null;

            dir = Environment.DIRECTORY_DOWNLOADS;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                dir = Environment.DIRECTORY_DOCUMENTS;
            }

            if (!externalStorageAvailable()) {
                errorDescription = getString(R.string.dialog_message_no_storage);
                return null;
            }

            path = Environment.getExternalStoragePublicDirectory(dir);
            if (!path.exists()) {
                dir = "Carbculator";
                path = Environment.getExternalStorageDirectory();
                if (!path.exists()) {
                    errorDescription = getString(R.string.dialog_message_no_storage);
                    return null;
                }
                path = new File(path.getAbsolutePath(), "Carbculator");
                if (!path.exists()) {
                    if (!path.mkdirs()) {
                        errorDescription =
                                getString(R.string.dialog_message_error_creating_directory);
                        return null;
                    }
                }
            }

            return path;
        }

        @Override
        protected void onPreExecute() {
            dialog.setTitle(R.string.exporting);
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            File path = getCarbculatorStorageDir();
            if (path == null) {
                return false;
            }

            CSVWriter csvWrite = null;
            try {
                File file = new File(path.getAbsolutePath() + "/" +
                        CARBCULATOR_FOODCATALOG_CSV);

                uriExport = Uri.fromFile(file);

                file.createNewFile();
                csvWrite = new CSVWriter(new FileWriter(file));
                List<Food> foodList = nutritionLab.getFoods();
                csvWrite.writeNext("name", "proteinPerUnit", "fatPerUnit", "carbsPerUnit",
                        "kcalPerUnit", "unitName", "unit");
                String[] en = getResources().getStringArray(R.array.eating_names);
                for (Food food : foodList) {

                    csvWrite.writeNext(
                            food.getName(),
                            String.format("%.1f", food.getProteinPerUnit()),
                            String.format("%.1f", food.getFatPerUnit()),
                            String.format("%.1f", food.getCarbsPerUnit()),
                            String.format("%.1f", food.getKcalPerUnit()),
                            food.getUnitName(),
                            String.valueOf(food.getUnit()));
                }

            } catch (IOException e) {
                dir = e.getMessage();
                return false;
            } finally {
                try {
                    if (csvWrite != null) {
                        csvWrite.close();
                    }
                } catch (IOException ignored) {
                }
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (success) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Export")
                        .setMessage(getString(R.string.dialog_message_export_success) + " " + dir)
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                        .setNeutralButton("Open",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (uriExport == null) {
                                            return;
                                        }

                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        setProperMimeType(intent);
                                        startActivity(intent);

                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();

            } else {

                if (errorDescription != null) {
                    Toast.makeText(getActivity(), getString(R.string.dialog_message_no_storage),
                            Toast.LENGTH_LONG).show();
                }
                Toast.makeText(getActivity(), getString(R.string.dialog_message_export_failure),
                        Toast.LENGTH_LONG).show();
            }

        }
    }

    private class AsyncImportFromCsv extends AsyncTask<Uri, Void, Boolean> {

        private Context context;

        public AsyncImportFromCsv(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            dialog.setTitle(R.string.importing);
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(Uri... params) {

            CSVReader csvReader = null;
            FileReader fileReader = null;
            try {
                String path = params[0].getPath();
                fileReader = new FileReader(new File(path));
                csvReader = new CSVReader(fileReader);
                List<String[]> listEntries = csvReader.readAll();
                for (int i = 1; i < listEntries.size(); i++) {
                    String[] entry = listEntries.get(i);
                    Food food = new Food();
                    food.setName(entry[0]);
                    food.setProteinPerUnit(Float.parseFloat(entry[1]));
                    food.setFatPerUnit(Float.parseFloat(entry[2]));
                    food.setCarbsPerUnit(Float.parseFloat(entry[3]));
                    food.setKcalPerUnit(Float.parseFloat(entry[4]));
                    food.setUnitName(entry[5]);
                    food.setUnit(Integer.parseInt(entry[6]));

                    nutritionLab.saveFood(food);
                }
            } catch (IOException e) {
                return false;
            } finally {
                try {
                    csvReader.close();
                    fileReader.close();
                } catch (IOException e) {
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (success) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Import")
                        .setMessage(getString(R.string.dialog_message_import_success))
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();

                initLoader();

            } else {
                Toast.makeText(getActivity(), getString(R.string.dialog_message_import_failure),
                        Toast.LENGTH_LONG).show();
            }

        }
    }

}
