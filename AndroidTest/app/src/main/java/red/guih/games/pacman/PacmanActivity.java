package red.guih.games.pacman;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import red.guih.games.R;

/**
 * An activity that displays the pacman view and the game
 *
 * Created by guilherme.hmedeiros on 21/01/2018.
 */

public class PacmanActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pacman);
        ActionBar a = getSupportActionBar();
        if(a!=null){
            a.setTitle(R.string.pacman);
            a.setDisplayHomeAsUpEnabled(true);
        }
    }
}
