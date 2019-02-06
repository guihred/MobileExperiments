package red.guih.games.sudoku;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import red.guih.games.BaseActivity;
import red.guih.games.R;

public class SudokuActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);
        ActionBar a = getSupportActionBar();
        if (a != null) {
            a.setTitle(R.string.sudoku);
            a.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sudoku, menu);

        MenuItem item = menu.findItem(R.id.reset);
        item.setTitle(R.string.reset);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        SudokuView view = findViewById(R.id.sudokuView);
        switch (item.getItemId()) {
            case R.id.solve:
                view.solve();
                view.invalidate();
                return true;
            case R.id.reset:
                view.reset();
                view.invalidate();
                return true;
            case R.id.blank:
                view.blank();
                view.invalidate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
