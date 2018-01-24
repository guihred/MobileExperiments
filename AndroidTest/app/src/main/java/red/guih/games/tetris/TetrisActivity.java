package red.guih.games.tetris;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import red.guih.games.R;

public class TetrisActivity extends AppCompatActivity {
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
}
