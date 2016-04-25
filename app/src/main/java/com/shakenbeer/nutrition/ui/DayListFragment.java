package com.shakenbeer.nutrition.ui;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
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
public class DayListFragment extends ListFragment implements LoaderCallbacks<Cursor>, LanguageDialogFragment.Callbacks {

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

        CarbculatorApplication.changeLocale(getActivity().getApplicationContext());

        nutritionLab = NutritionLab.getInstance(getActivity().getApplicationContext());

        getLoaderManager().initLoader(0, null, this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
            case R.id.action_csv:
                exportToCsv();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void exportToCsv() {
        if (isExternalStorageWritable()) {
            new AsyncExportToCsv().execute();

        }

    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
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

        protected File getCarbculatorStorageDir() {
            ListView listView = getListView();

            File path = null;

            dir = Environment.DIRECTORY_DOWNLOADS;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                dir = Environment.DIRECTORY_DOCUMENTS;
            }

            if (!externalStorageAvailable()) {
                Snackbar snackbar = Snackbar
                        .make(listView, getString(R.string.list_snackbar_message_no_storage),
                                Snackbar.LENGTH_LONG);
                snackbar.show();
                return null;
            }

            path = Environment.getExternalStoragePublicDirectory(dir);
            if (!path.exists()) {
                dir = "Carbculator";
                path = Environment.getExternalStorageDirectory();
                if (!path.exists()) {
                    Snackbar snackbar = Snackbar
                            .make(listView, getString(R.string.list_snackbar_message_no_storage),
                                    Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return null;
                }
                path = new File(path.getAbsolutePath(), "Carbculator");
                if (!path.exists()) {
                    if (!path.mkdirs()) {
                        Snackbar snackbar = Snackbar
                                .make(listView, getString(
                                        R.string.list_snackbar_message_error_creating_directory),
                                        Snackbar.LENGTH_LONG);
                        snackbar.show();
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

        private boolean externalStorageAvailable() {
            return
                    Environment.MEDIA_MOUNTED
                            .equals(Environment.getExternalStorageState());
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            File path = getCarbculatorStorageDir();
            if (path == null) {
                return false;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            CSVWriter csvWrite = null;
            try {
                File file = new File(path.getAbsolutePath() + "/" +
                        CARBCULATOR_REPORT_CSV);

                mUriExport = Uri.fromFile(file);

                file.createNewFile();
                csvWrite = new CSVWriter(new FileWriter(file));
                List<Eating> eatings = nutritionLab.getEatings();
                csvWrite.writeNext("date", "number", "kcal", "protein", "fat", "carbs");
                String[] en = getResources().getStringArray(R.array.eating_names);
                for (Eating eating : eatings) {

                    csvWrite.writeNext(
                            sdf.format(eating.getDate()),
                            String.valueOf(en[eating.getNumber()]),
                            String.format("%.1f", eating.getKcal()),
                            String.format("%.1f", eating.getProtein()),
                            String.format("%.1f", eating.getFat()),
                            String.format("%.1f", eating.getCarbs()));
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
            ListView listView = getListView();
            if (success) {
                Snackbar snackbar = Snackbar
                        .make(listView, getString(R.string.list_snackbar_message_export_success)
                                        + " " + dir,
                                Snackbar.LENGTH_LONG);
                snackbar.setAction("Open", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mUriExport == null) {
                            return;
                        }

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(mUriExport, "text/plain");
                        startActivity(intent);
                    }
                });
                snackbar.show();
            } else {
                Snackbar snackbar = Snackbar
                        .make(listView, getString(R.string.list_snackbar_message_export_failure)
                                + "" +
                                " " + dir, Snackbar.LENGTH_LONG);
                snackbar.show();
            }

        }
    }
}
