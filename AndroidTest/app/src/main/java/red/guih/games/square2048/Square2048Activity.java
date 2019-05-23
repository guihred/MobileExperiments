/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package red.guih.games.square2048;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import red.guih.games.BaseActivity;
import red.guih.games.R;
import red.guih.games.db.UserRecord;

public class Square2048Activity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_square2048);

        ActionBar a = getSupportActionBar();
        if (a != null) {
            a.setTitle(R.string.square2048);
            a.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_records, menu);
        MenuItem item = menu.findItem(R.id.config);
        item.setTitle(R.string.reset);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int i = item.getItemId();
        if (i == R.id.config) {
            Square2048View viewById = findViewById(R.id.square2048_view);
            viewById.reset();
            return true;
        } else if (i == R.id.records) {
            showRecords(Square2048View.MAP_WIDTH, UserRecord.SQUARE_2048);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
