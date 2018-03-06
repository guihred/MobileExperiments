/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package red.guih.games.square2048;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import red.guih.games.BaseActivity;
import red.guih.games.R;
import red.guih.games.db.UserRecord;

public class Square2048Activity extends BaseActivity {


    //    public void start(Stage stage) throws Exception {
//        final GridPane gridPane = new GridPane();
//        gridPane.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)));
//
//        final Square2048Model memoryModel = new Square2048Model(gridPane);
//
//        for (int i = 0; i < memoryModel.getMap().length; i++) {
//            for (int j = 0; j < memoryModel.getMap()[i].length; j++) {
//                Square2048 map1 = memoryModel.getMap()[i][j];
//                gridPane.add(map1, i, j);
//            }
//        }
//
//        final BorderPane borderPane = new BorderPane(gridPane);
//        final Scene scene = new Scene(borderPane);
//        stage.setScene(scene);
//        stage.setWidth(400);
//        stage.setHeight(400);
//
//        scene.setOnKeyPressed(memoryModel::handleKeyPressed);
//        stage.show();
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_square2048);

        ActionBar a = getSupportActionBar();
        if (a != null) {
            a.setTitle(R.string.square2048);
            a.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_records, menu);

        MenuItem item = menu.findItem(R.id.config);
        item.setTitle(R.string.reset);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.config:
                Square2048View viewById = findViewById(R.id.square2048_view);
                viewById.reset();
                return true;
            case R.id.records:
                showRecords(Square2048View.MAP_WIDTH, UserRecord.SQUARE_2048);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
