package red.guih.games;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import red.guih.games.dots.DotsActivity;
import red.guih.games.labyrinth.LabyrinthActivity;
import red.guih.games.minesweeper.MinesweeperActivity;
import red.guih.games.pacman.PacmanActivity;
import red.guih.games.tetris.TetrisActivity;

/**
 * Main activity which will invoke other games.
 *
 * Created by guilherme.hmedeiros on 18/01/2018.
 */

public class GamesActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);

        ImageButton minesweeperButton = findViewById(R.id.minesweeperButton);

        minesweeperButton.setOnClickListener((View v) -> startActivity(new Intent(this, MinesweeperActivity.class)));
        ImageButton dotsButton = findViewById(R.id.dotsButton);
        dotsButton.setOnClickListener((View v) -> startActivity(new Intent(this, DotsActivity.class)));
        ImageButton pacmanButton = findViewById(R.id.pacmanButton);
        pacmanButton.setOnClickListener((View v) -> startActivity(new Intent(this, PacmanActivity.class)));
        ImageButton tetrisButton = findViewById(R.id.tetrisButton);
        tetrisButton.setOnClickListener((View v) -> startActivity(new Intent(this, TetrisActivity.class)));
        ImageButton labyrinthButton = findViewById(R.id.labyrinthButton);
        labyrinthButton.setOnClickListener((View v) -> startActivity(new Intent(this, LabyrinthActivity.class)));

    }
}
