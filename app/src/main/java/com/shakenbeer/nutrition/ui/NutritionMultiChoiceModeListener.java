package com.shakenbeer.nutrition.ui;

import com.shakenbeer.nutrition.R;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView.MultiChoiceModeListener;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public abstract class NutritionMultiChoiceModeListener implements MultiChoiceModeListener {

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater menuInflater = mode.getMenuInflater();
        menuInflater.inflate(R.menu.fragment_list_context, menu);
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_item_delete:
            deleteSelectedItems();
            mode.finish(); // Action picked, so close the CAB
            return true;
        default:
            return false;
        }
    }

    public abstract void deleteSelectedItems();

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

    }

}
