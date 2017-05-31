package com.shakenbeer.nutrition.stat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.shakenbeer.nutrition.CarbculatorApplication;
import com.shakenbeer.nutrition.databinding.StatisticsViewBinding;
import com.shakenbeer.nutrition.main.MainActivity;
import com.shakenbeer.nutrition.model.Statistics;

import javax.inject.Inject;


public class StatisticsView extends RelativeLayout implements StatisticsContract.View,
        MainActivity.Callbacks {

    @SuppressWarnings("WeakerAccess")
    @Inject
    StatisticsContract.Presenter presenter;

    private final StatisticsViewBinding binding;

    public StatisticsView(Context context) {
        super(context);
        binding = StatisticsViewBinding.inflate(LayoutInflater.from(context), this, true);
        DaggerStatisticsComponent.builder()
                .applicationComponent(CarbculatorApplication.get(context).getComponent())
                .statisticsModule(new StatisticsModule())
                .build()
                .inject(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        presenter.attachView(this);
        presenter.obtainStatistics();
    }

    @Override
    protected void onDetachedFromWindow() {
        presenter.detachView();
        super.onDetachedFromWindow();
    }

    @Override
    public Activity getActivity() {
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void showStatistics(Statistics statistics) {
        binding.setStat(statistics);
        float avgProtein = statistics.getTotalProtein() / statistics.getTotalDays();
        float avgFat = statistics.getTotalFat() / statistics.getTotalDays();
        float avgCarbs = statistics.getTotalCarbs() / statistics.getTotalDays();
        float sum = avgProtein + avgFat + avgCarbs;
        int p = Math.round((avgProtein / sum) * 100);
        int f = Math.round((avgFat / sum) * 100);
        int c = Math.round((avgCarbs / sum) * 100);
        binding.avgRatioTextView.setText(p + "/" + f + "/" + c);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }


}
