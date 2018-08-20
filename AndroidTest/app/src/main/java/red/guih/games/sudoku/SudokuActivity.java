package red.guih.games.sudoku;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;

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
}
