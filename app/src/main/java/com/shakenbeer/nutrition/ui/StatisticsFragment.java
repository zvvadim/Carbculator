package com.shakenbeer.nutrition.ui;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shakenbeer.nutrition.R;
import com.shakenbeer.nutrition.model.DataCursor;
import com.shakenbeer.nutrition.model.Day;
import com.shakenbeer.nutrition.model.NutritionLab;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public class StatisticsFragment extends Fragment {
    private NutritionLab nutritionLab;
    TextView totalDaysTextView, totalProteinTextView, totalFatTextView, totalCarbsTextView, totalKcalTextView,
            avgProteinTextView, avgFatTextView, avgCarbsTextView, avgKcalTextView, avgPfcRatioTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        setHasOptionsMenu(true);

        nutritionLab = NutritionLab.getInstance(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        totalDaysTextView = (TextView) view.findViewById(R.id.total_days_text_view);
        totalProteinTextView = (TextView) view.findViewById(R.id.total_protein_text_view);
        totalFatTextView = (TextView) view.findViewById(R.id.total_fat_text_view);
        totalCarbsTextView = (TextView) view.findViewById(R.id.total_carbs_text_view);
        totalKcalTextView = (TextView) view.findViewById(R.id.total_kcal_text_view);
        avgProteinTextView = (TextView) view.findViewById(R.id.avg_protein_text_view);
        avgFatTextView = (TextView) view.findViewById(R.id.avg_fat_text_view);
        avgCarbsTextView = (TextView) view.findViewById(R.id.avg_carbs_text_view);
        avgKcalTextView = (TextView) view.findViewById(R.id.avg_kcal_text_view);
        avgPfcRatioTextView = (TextView) view.findViewById(R.id.avg_ratio_text_view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        new AsyncLoadStatistics().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            getActivity().finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private class AsyncLoadStatistics extends AsyncTask<Void, Void, NutritionStats> {

        @Override
        protected NutritionStats doInBackground(Void... params) {
            float totalProtein = 0;
            float totalFat = 0;
            float totalCarbs = 0;
            float totalKcal = 0;
            int totalDays = 0;
            DataCursor<Day> cursor = nutritionLab.getDayCursor();
            if (cursor.moveToFirst()) {

                do {
                    Day day = cursor.get();
                    totalProtein += day.getProtein();
                    totalFat += day.getFat();
                    totalCarbs += day.getCarbs();
                    totalKcal += day.getKcal();
                } while (cursor.moveToNext());
                totalDays = cursor.getCount();

            }
            return new NutritionStats(totalDays, totalProtein, totalFat, totalCarbs, totalKcal);
        }

        @Override
        protected void onPostExecute(NutritionStats result) {
            float avgProtein = result.totalProtein / result.totalDays;
            float avgFat = result.totalFat / result.totalDays;
            float avgCarbs = result.totalCarbs / result.totalDays;
            float avgKcal = result.totalKcal / result.totalDays;
            float sum = avgProtein + avgFat + avgCarbs;
            int p = Math.round((avgProtein / sum) * 100);
            int f = Math.round((avgFat / sum) * 100);
            int c = Math.round((avgCarbs / sum) * 100);
            totalDaysTextView.setText(String.valueOf(result.totalDays));
            totalProteinTextView.setText(String.format("%.1f", result.totalProtein));
            totalFatTextView.setText(String.format("%.1f", result.totalFat));
            totalCarbsTextView.setText(String.format("%.1f", result.totalCarbs));
            totalKcalTextView.setText(String.format("%.1f", result.totalKcal));
            avgProteinTextView.setText(String.format("%.1f", avgProtein));
            avgFatTextView.setText(String.format("%.1f", avgFat));
            avgCarbsTextView.setText(String.format("%.1f", avgCarbs));
            avgKcalTextView.setText(String.format("%.1f", avgKcal));
            avgPfcRatioTextView.setText(p + "/" + f + "/" + c);
        }

    }

    private class NutritionStats {
        int totalDays = 0;
        float totalProtein = 0;
        float totalFat = 0;
        float totalCarbs = 0;
        float totalKcal = 0;

        public NutritionStats(int totalDays, float totalProtein, float totalFat, float totalCarbs, float totalKcal) {
            super();
            this.totalDays = totalDays;
            this.totalProtein = totalProtein;
            this.totalFat = totalFat;
            this.totalCarbs = totalCarbs;
            this.totalKcal = totalKcal;
        }
    }

}
