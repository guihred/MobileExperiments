package red.guih.games.puzzle;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import red.guih.games.R;

public class PuzzleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        ActionBar a = getSupportActionBar();
        if (a != null) {
            a.setTitle(R.string.puzzle);
            a.setDisplayHomeAsUpEnabled(true);
        }
    }

}
