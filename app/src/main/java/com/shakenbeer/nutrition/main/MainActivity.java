package com.shakenbeer.nutrition.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.shakenbeer.nutrition.R;
import com.shakenbeer.nutrition.calendar.CalendarContract;
import com.shakenbeer.nutrition.calendar.CalendarView;
import com.shakenbeer.nutrition.foodlist.FoodListContract;
import com.shakenbeer.nutrition.foodlist.FoodListView;
import com.shakenbeer.nutrition.model.Food;
import com.shakenbeer.nutrition.model.Meal;
import com.shakenbeer.nutrition.model.NutritionLab;
import com.shakenbeer.nutrition.stat.StatisticsView;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class MainActivity extends AppCompatActivity {

    private static final int CALENDAR = 1;
    private static final int FOOD_LIST = 2;
    private static final int STATISTICS = 3;
    private static final int SETTINGS = 4;

    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 325;
    private static final int EXPORT_CALENDAR = 1;
    private static final int EXPORT_FOODS = 2;
    private static final int IMPORT_FOODS = 3;

    private int wantedAction;

    private int currentPage = CALENDAR;

    private MainContainer container;

    private Callbacks callbacks;

    private Menu menu;

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_calendar:
                showCalendarUi();
                updateCurrentPage(CALENDAR);
                return true;
            case R.id.navigation_food_list:
                showFoodListUi();
                updateCurrentPage(FOOD_LIST);
                return true;
            case R.id.navigation_statistics:
                showStatisticUi();
                updateCurrentPage(STATISTICS);
                return true;
        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        container = (MainContainer) findViewById(R.id.content);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        dialog = new ProgressDialog(this);

        showCalendarUi();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        updateMenu();
        return true;
    }

    private void hideOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    private void showOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add) {
            if (currentPage == CALENDAR) {
                ((CalendarContract.View) callbacks).showNewMealUi();
            }
            if (currentPage == FOOD_LIST) {
                ((FoodListContract.View) callbacks).showNewFoodUi();
            }
            return true;
        }
        if (item.getItemId() == R.id.action_export_to_csv) {
            if (currentPage == CALENDAR) {
                if (ensureWritePermission(EXPORT_CALENDAR)) {
                    exportToCsv();
                }
            }
            if (currentPage == FOOD_LIST) {
                if (ensureWritePermission(EXPORT_FOODS)) {
                    exportFoodsToCsv();
                }
            }
            return true;
        }
        if (item.getItemId() == R.id.action_import_from_csv) {
            if (currentPage == FOOD_LIST) {
                if (ensureWritePermission(IMPORT_FOODS)) {
                    importFoodFromCsv();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMenu() {
        switch (currentPage) {
            case CALENDAR:
                showOption(R.id.add);
                hideOption(R.id.action_import_from_csv);
                showOption(R.id.action_export_to_csv);
                break;
            case FOOD_LIST:
                showOption(R.id.add);
                showOption(R.id.action_import_from_csv);
                showOption(R.id.action_export_to_csv);
                break;
            case STATISTICS:
                hideOption(R.id.add);
                hideOption(R.id.action_import_from_csv);
                hideOption(R.id.action_export_to_csv);
                break;
            default:
                showCalendarUi();
        }
    }

    private void showCalendarUi() {
        CalendarView calendarView = new CalendarView(this);
        callbacks = calendarView;
        container.replace(calendarView);
    }

    private void showFoodListUi() {
        FoodListView foodListView = new FoodListView(this);
        callbacks = foodListView;
        container.replace(foodListView);
    }

    private void showStatisticUi() {
        StatisticsView statisticsView = new StatisticsView(this);
        callbacks = statisticsView;
        container.replace(statisticsView);
    }

    private void updateCurrentPage(int page) {
        currentPage = page;
        updateMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMPORT_FILE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                new AsyncImportFoodsFromCsv(this).execute(uri);
            }
        }
        if (callbacks != null) {
            callbacks.onActivityResult(requestCode, resultCode, data);
        }
    }

    public interface Callbacks {
        Activity getActivity();

        void onActivityResult(int requestCode, int resultCode, Intent data);
    }

    private boolean ensureWritePermission(int action) {
        wantedAction = action;
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                new AlertDialog.Builder(this)
                        .setTitle(R.string.write_external_storage_request_title)
                        .setMessage(R.string.write_external_storage_request_rationale)
                        .setPositiveButton(R.string.permission_button_accept, (dialog1, which) ->
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE))
                        .setNegativeButton(R.string.permission_button_deny, null)
                        .show();


            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (wantedAction == EXPORT_CALENDAR) {
                        exportToCsv();
                    }
                    if (wantedAction == EXPORT_FOODS) {
                        exportFoodsToCsv();
                    }
                    if (wantedAction == IMPORT_FOODS) {
                        importFoodFromCsv();
                    }
                }
            }
        }
    }

    ///////////////////////////////////////////
    ////////// Copy from old version //////////
    ///////////////////////////////////////////

    private void exportToCsv() {
        if (isExternalStorageWritable()) {
            new AsyncExportToCsv(this).execute();
        }
    }

    private void exportFoodsToCsv() {
        if (isExternalStorageWritable()) {
            new AsyncExportFoodsToCsv(this).execute();
        }
    }

    private static final int REQUEST_IMPORT_FILE = 516;

    private void importFoodFromCsv() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        setProperMimeType(intent);
        Intent i = Intent.createChooser(intent, "Choose file");
        startActivityForResult(i, REQUEST_IMPORT_FILE);
    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private ProgressDialog dialog;
    private Uri mUriExport;

    private class AsyncExportToCsv extends AsyncTask<Void, Void, Boolean> {

        public static final String CARBCULATOR_REPORT_CSV = "carbculator_report.csv";
        private volatile String dir;

        private final Context context;
        private String errorDescription;

        public AsyncExportToCsv(Context ctx) {
            context = ctx;
        }

        private boolean externalStorageAvailable() {
            return
                    Environment.MEDIA_MOUNTED
                            .equals(Environment.getExternalStorageState());
        }

        File getCarbculatorStorageDir() {

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
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            File path = getCarbculatorStorageDir();
            if (path == null) {
                return false;
            }

            CSVWriter csvWrite = null;
            try {
                File file = new File(path.getAbsolutePath() + "/" +
                        CARBCULATOR_REPORT_CSV);

                mUriExport = Uri.fromFile(file);

                file.createNewFile();
                csvWrite = new CSVWriter(new FileWriter(file));
                List<Meal> meals = NutritionLab.getInstance(context).getMeals();
                csvWrite.writeNext("date", "number", "kcal", "protein", "fat", "carbs");
                String[] en = getResources().getStringArray(R.array.eating_names);
                for (Meal meal : meals) {

                    csvWrite.writeNext(
                            sdf.format(meal.getDate()),
                            String.valueOf(en[meal.getNumber()]),
                            String.format(Locale.getDefault(), "%.1f", meal.getKcal()),
                            String.format(Locale.getDefault(), "%.1f", meal.getProtein()),
                            String.format(Locale.getDefault(), "%.1f", meal.getFat()),
                            String.format(Locale.getDefault(), "%.1f", meal.getCarbs()));
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
                                (dialog1, id) -> dialog1.cancel())
                        .setNeutralButton("Open",
                                (dialog12, id) -> {
                                    if (mUriExport == null) {
                                        return;
                                    }

                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    setProperMimeType(intent);
                                    startActivity(intent);

                                    dialog12.cancel();
                                });
                AlertDialog alert = builder.create();
                alert.show();

            } else {

                if (errorDescription != null) {
                    Toast.makeText(context, getString(R.string.dialog_message_no_storage),
                            Toast.LENGTH_LONG).show();
                }
                Toast.makeText(context, getString(R.string.dialog_message_export_failure),
                        Toast.LENGTH_LONG).show();
            }

        }
    }

    private void setProperMimeType(Intent intent) {
        PackageManager pm = getPackageManager();
        intent.setDataAndType(mUriExport, "text/csv");
        ResolveInfo info = pm.resolveActivity(intent, 0);
        if (info == null) {
            intent.setDataAndType(mUriExport, "text/plain");
        }

    }

    ///////// From old version - import/export foods list /////////

    private static final String CARBCULATOR_FOODCATALOG_CSV = "carbculator_food_catalog.csv";

    private class AsyncExportFoodsToCsv extends AsyncTask<Void, Void, Boolean> {

        private final Context context;
        private String errorDescription;
        private volatile String dir;

        public AsyncExportFoodsToCsv(Context ctx) {
            context = ctx;
        }

        private boolean externalStorageAvailable() {
            return
                    Environment.MEDIA_MOUNTED
                            .equals(Environment.getExternalStorageState());
        }

        File getCarbculatorStorageDir() {
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
            File path = getCarbculatorStorageDir();
            if (path == null) {
                return false;
            }

            CSVWriter csvWrite = null;
            try {
                File file = new File(path.getAbsolutePath() + "/" +
                        CARBCULATOR_FOODCATALOG_CSV);

                mUriExport = Uri.fromFile(file);

                file.createNewFile();
                csvWrite = new CSVWriter(new FileWriter(file));
                List<Food> foodList = NutritionLab.getInstance(context).getFoods();
                csvWrite.writeNext("name", "proteinPerUnit", "fatPerUnit", "carbsPerUnit",
                        "kcalPerUnit", "unitName", "unit");
                String[] en = getResources().getStringArray(R.array.eating_names);
                for (Food food : foodList) {

                    csvWrite.writeNext(
                            food.getName(),
                            String.format(Locale.getDefault(), "%.1f", food.getProteinPerUnit()),
                            String.format(Locale.getDefault(), "%.1f", food.getFatPerUnit()),
                            String.format(Locale.getDefault(), "%.1f", food.getCarbsPerUnit()),
                            String.format(Locale.getDefault(), "%.1f", food.getKcalPerUnit()),
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
                                (dialog12, id) -> dialog12.cancel())
                        .setNeutralButton("Open",
                                (dialog1, id) -> {
                                    if (mUriExport == null) {
                                        return;
                                    }

                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    setProperMimeType(intent);
                                    startActivity(intent);

                                    dialog1.cancel();
                                });
                AlertDialog alert = builder.create();
                alert.show();

            } else {

                if (errorDescription != null) {
                    Toast.makeText(context, getString(R.string.dialog_message_no_storage),
                            Toast.LENGTH_LONG).show();
                }
                Toast.makeText(context, getString(R.string.dialog_message_export_failure),
                        Toast.LENGTH_LONG).show();
            }

        }
    }

    private class AsyncImportFoodsFromCsv extends AsyncTask<Uri, Void, Boolean> {

        private final Context context;

        public AsyncImportFoodsFromCsv(Context ctx) {
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
                    food.setProteinPerUnit(TextUtils.isEmpty(entry[1]) ? 0f : Float.parseFloat(entry[1]));
                    food.setFatPerUnit(TextUtils.isEmpty(entry[2]) ? 0f : Float.parseFloat(entry[2]));
                    food.setCarbsPerUnit(TextUtils.isEmpty(entry[3]) ? 0f : Float.parseFloat(entry[3]));
                    food.setKcalPerUnit(TextUtils.isEmpty(entry[4]) ? 0f : Float.parseFloat(entry[4]));
                    food.setUnitName(entry[5]);
                    food.setUnit(TextUtils.isEmpty(entry[6]) ? 0 : Integer.parseInt(entry[6]));

                    NutritionLab.getInstance(context).saveFood(food);
                }
            } catch (NumberFormatException e) {
                return false;
            } catch (IOException e) {
                return false;
            } finally {
                try {
                    csvReader.close();
                    fileReader.close();
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
                builder.setTitle("Import")
                        .setMessage(getString(R.string.dialog_message_import_success))
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                (dialog1, id) -> dialog1.cancel());
                AlertDialog alert = builder.create();
                alert.show();

                if (currentPage == FOOD_LIST) {
                    showFoodListUi();
                    updateCurrentPage(FOOD_LIST);
                }

            } else {
                Toast.makeText(context, getString(R.string.dialog_message_import_failure),
                        Toast.LENGTH_LONG).show();
            }

        }
    }
}
