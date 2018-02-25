package red.guih.games.solitaire;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        MenuItem item = menu.findItem(R.id.config);
        item.setTitle(R.string.reset);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.config:
                SolitaireView viewById = findViewById(R.id.solitaire_view);
                viewById.reset();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
