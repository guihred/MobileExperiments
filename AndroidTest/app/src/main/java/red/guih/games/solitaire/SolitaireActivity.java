package red.guih.games.solitaire;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import red.guih.games.BaseActivity;
import red.guih.games.R;

public class SolitaireActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solitaire);
        ActionBar a = getSupportActionBar();
        if (a != null) {
            a.setTitle(R.string.solitaire);
            a.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return expandMenu(menu, R.string.reset);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ActionBar a = getSupportActionBar();
        if (a != null) {
            if (newConfig.screenWidthDp <= newConfig.screenHeightDp) {
                a.show();
            } else {
                a.hide();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.config) {
            SolitaireView viewById = findViewById(R.id.solitaire_view);
            viewById.reset();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
