package red.guih.games;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import red.guih.games.dots.DotsActivity;
import red.guih.games.freecell.FreeCellActivity;
import red.guih.games.japanese.JapaneseActivity;
import red.guih.games.madmaze.MadMazeActivity;
import red.guih.games.minesweeper.MinesweeperActivity;
import red.guih.games.pacman.PacmanActivity;
import red.guih.games.puzzle.PuzzleActivity;
import red.guih.games.slidingpuzzle.SlidingPuzzleActivity;
import red.guih.games.solitaire.SolitaireActivity;
import red.guih.games.square2048.Square2048Activity;
import red.guih.games.tetris.TetrisActivity;

/**
 * Main activity which will invoke other games.
 * <p>
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
        labyrinthButton.setOnClickListener((View v) -> startActivity(new Intent(this, MadMazeActivity.class)));
        ImageButton puzzleButton = findViewById(R.id.puzzleButton);
        puzzleButton.setOnClickListener((View v) -> startActivity(new Intent(this, PuzzleActivity.class)));
        ImageButton slidingPuzzleButton = findViewById(R.id.slidingPuzzleButton);
        slidingPuzzleButton.setOnClickListener((View v) -> startActivity(new Intent(this, SlidingPuzzleActivity.class)));
        ImageButton solitaire = findViewById(R.id.solitaireButton);
        solitaire.setOnClickListener((View v) -> startActivity(new Intent(this, SolitaireActivity.class)));
        ImageButton square2048 = findViewById(R.id.square2048Button);
        square2048.setOnClickListener((View v) -> startActivity(new Intent(this, Square2048Activity.class)));
        ImageButton freeCell = findViewById(R.id.freecellButton);
        freeCell.setOnClickListener((View v) -> startActivity(new Intent(this, FreeCellActivity.class)));
        ImageButton japaneseButton = findViewById(R.id.japaneseButton);
        japaneseButton.setOnClickListener((View v) -> startActivity(new Intent(this, JapaneseActivity.class)));

    }

    @Override
    protected void onStart() {
        super.onStart();
        playAnimation(R.id.pacmanButton);
        playAnimation(R.id.square2048Button);
        playAnimation(R.id.puzzleButton);
        playAnimation(R.id.tetrisButton);
        playAnimation(R.id.dotsButton);
    }

    private void playAnimation(int puzzleButton1) {
        ImageButton puzzleButton = findViewById(puzzleButton1);
        Drawable puzzleButtonDrawable = puzzleButton.getDrawable();
        if (puzzleButtonDrawable instanceof AnimatedVectorDrawable) {
            ((Animatable) puzzleButtonDrawable).start();
        }
    }

}
