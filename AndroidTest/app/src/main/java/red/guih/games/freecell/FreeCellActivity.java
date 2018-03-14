package red.guih.games.freecell;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import red.guih.games.BaseActivity;
import red.guih.games.R;

public class FreeCellActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freecell);

        ActionBar a = getSupportActionBar();
        if (a != null) {
            a.setTitle(R.string.freecell);
            a.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        MenuItem item = menu.findItem(R.id.config);
        item.setTitle(R.string.reset);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ActionBar a = getSupportActionBar();
        if (a != null) {
            if (newConfig.screenWidthDp > newConfig.screenHeightDp) {
                a.hide();
            } else {
                a.show();
            }
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.config:
                FreeCellView viewById = findViewById(R.id.solitaire_view);
                viewById.reset();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
