package red.guih.games.japanese;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import red.guih.games.R;
import red.guih.games.db.DatabaseHelper;

public class JapaneseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_japanese);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this::doAction);
        new Thread(this::executeDatabase).start();
    }

    private void executeDatabase() {
        DatabaseHelper myDbHelper;
        SQLiteDatabase myDb = null;

        myDbHelper = new DatabaseHelper(this);
        /*
         * Database must be initialized before it can be used. This will ensure
         * that the database exists and is the current version.
         */

        myDbHelper.initializeDataBase();
        try {
            // A reference to the database can be obtained after initialization.
            myDb = myDbHelper.getWritableDatabase();
        /*
         * Place code to use database here.
         */
        } catch (Exception ex) {
            Log.e("JP", "DATABASE ERROR", ex);

        } finally {
            try {
                myDbHelper.close();
            } catch (Exception ex) {
                Log.e("JP", "DATABASE ERROR", ex);
            } finally {
                if (myDb != null) {
                    myDb.close();
                }
            }
        }
    }

    private void doAction(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

}

