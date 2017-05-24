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
import android.widget.Toast;

import com.shakenbeer.nutrition.R;
import com.shakenbeer.nutrition.main.MainActivity;
import com.shakenbeer.nutrition.model.DataCursor;
import com.shakenbeer.nutrition.model.Day;
import com.shakenbeer.nutrition.model.DayCursorLoader;
import com.shakenbeer.nutrition.model.Meal;
import com.shakenbeer.nutrition.model.NutritionLab;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * @author Sviatoslav Melnychenko
 */
public class DayListFragment extends ListFragment implements LoaderCallbacks<Cursor> {

    private static final int OPEN_DAY = 1;
    private static final int ADD_EATING = 2;
    private static final int FOOD_LIST = 3;

    private NutritionLab nutritionLab;
    private DataCursor<Day> dayCursor;
    private ProgressDialog dialog;

    private Uri mUriExport;

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
            case R.id.statistics_menu_item:
                Intent intent = new Intent(getActivity(), StatisticsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_csv:
                exportToCsv();
                return true;
            case R.id.action_new_version:
                Intent mainIntent = new Intent(getActivity(), MainActivity.class);
                startActivity(mainIntent);
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

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
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
                List<Meal> meals = nutritionLab.getMeals();
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
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                        .setNeutralButton("Open",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (mUriExport == null) {
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

    private void setProperMimeType(Intent intent) {
        PackageManager pm = getActivity().getPackageManager();
        intent.setDataAndType(mUriExport, "text/csv");
        ResolveInfo info = pm.resolveActivity(intent, 0);
        if (info == null) {
            intent.setDataAndType(mUriExport, "text/plain");
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

        dialog = new ProgressDialog(getActivity());
    }

    private void openDay(Day day) {
        Intent intent = new Intent(getActivity(), DayActivity.class);
        intent.putExtra(DayFragment.EXTRA_DAY, day);
        startActivityForResult(intent, OPEN_DAY);
    }

    private void addEating() {
        Intent intent = new Intent(getActivity(), EatingActivity.class);
        intent.putExtra(EatingFragment.EXTRA_EATING, new Meal(new Date()));
        startActivityForResult(intent, ADD_EATING);
    }

    private void openFoodList() {
        Intent intent = new Intent(getActivity(), FoodListActivity.class);
        startActivityForResult(intent, FOOD_LIST);
    }
}
