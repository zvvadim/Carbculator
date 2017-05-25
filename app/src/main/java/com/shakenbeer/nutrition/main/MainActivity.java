package com.shakenbeer.nutrition.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
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

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class MainActivity extends AppCompatActivity {

    private static final int CALENDAR = 1;
    private static final int FOOD_LIST = 2;
    private static final int STATISTICS = 3;
    private static final int SETTINGS = 4;

    private int currentPage = CALENDAR;

    private MainContainer container;

    private Callbacks callbacks;

    private Menu menu;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
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
    protected void onResume() {
        super.onResume();
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
                exportToCsv();
            }
            if (currentPage == FOOD_LIST) {
                exportFoodsToCsv();
            }
            return true;
        }
        if (item.getItemId() == R.id.action_import_from_csv) {
            if (currentPage == FOOD_LIST) {
                importFoodFromCsv();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    void updateMenu() {
        switch (currentPage) {
            case CALENDAR:
                hideOption(R.id.action_import_from_csv);
                showOption(R.id.action_export_to_csv);
                break;
            case FOOD_LIST:
                showOption(R.id.action_import_from_csv);
                showOption(R.id.action_export_to_csv);
                break;
            case STATISTICS:
                hideOption(R.id.action_import_from_csv);
                hideOption(R.id.action_export_to_csv);
                break;
            default:
                showCalendarUi();
        }
    }

    public void showCalendarUi() {
        CalendarView calendarView = new CalendarView(this);
        callbacks = calendarView;
        container.replace(calendarView);
    }

    public void showFoodListUi() {
        FoodListView foodListView = new FoodListView(this);
        callbacks = foodListView;
        container.replace(foodListView);
    }

    public void showStatisticUi() {
        StatisticsView statisticsView = new StatisticsView(this);
        callbacks = statisticsView;
        container.replace(statisticsView);
    }

    private void updateCurrentPage(int page) {
        currentPage = page;
        updateMenu();
    }

    private void clearContent() {
        if (container.getChildAt(0) != null) {
            container.removeViewAt(0);
        }
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

    ////////// Copy from old version //////////

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
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private ProgressDialog dialog;
    private Uri mUriExport;

    private class AsyncExportToCsv extends AsyncTask<Void, Void, Boolean> {

        public static final String CARBCULATOR_REPORT_CSV = "carbculator_report.csv";
        private volatile String dir;

        private Context context;
        private String errorDescription;

        public AsyncExportToCsv(Context ctx) {
            context = ctx;
        }

        private boolean externalStorageAvailable() {
            return
                    Environment.MEDIA_MOUNTED
                            .equals(Environment.getExternalStorageState());
        }

        protected File getCarbculatorStorageDir() {

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
                        errorDescription = getString(R.string.dialog_message_error_creating_directory);
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
                            String.format("%.1f", meal.getKcal()),
                            String.format("%.1f", meal.getProtein()),
                            String.format("%.1f", meal.getFat()),
                            String.format("%.1f", meal.getCarbs()));
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

    public static final String CARBCULATOR_FOODCATALOG_CSV = "carbculator_food_catalog.csv";
    private Uri uriExport;

    private class AsyncExportFoodsToCsv extends AsyncTask<Void, Void, Boolean> {

        private Context context;
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

        protected File getCarbculatorStorageDir() {
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

                uriExport = Uri.fromFile(file);

                file.createNewFile();
                csvWrite = new CSVWriter(new FileWriter(file));
                List<Food> foodList = NutritionLab.getInstance(context).getFoods();
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
                                (dialog12, id) -> dialog12.cancel())
                        .setNeutralButton("Open",
                                (dialog1, id) -> {
                                    if (uriExport == null) {
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

        private Context context;

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
