package red.guih.games.tetris;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.List;

import red.guih.games.BaseActivity;
import red.guih.games.R;
import red.guih.games.db.UserRecord;

public class TetrisActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tetris);
        ActionBar a = getSupportActionBar();
        if (a != null) {
            a.setTitle(R.string.tetris);
            a.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_records, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.records:
                showRecords(1, UserRecord.TETRIS);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        TetrisView viewById = findViewById(R.id.tetris_view);
        if (hasFocus) {
            viewById.continueGame();
        } else {
            viewById.pause();
        }
    }

    @Override
    protected List<UserRecord> getAll(int difficulty, String gameName) {
        return db.userDao().getAllDesc(difficulty, gameName);
    }
}
