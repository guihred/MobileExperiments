package red.guih.games.db;

import android.arch.persistence.db.SupportSQLiteStatement;
import android.content.Context;
import android.util.Log;

import java.io.InputStream;

/**
 * Class to update the database
 * Created by guilherme.hmedeiros on 16/03/2018.
 */
public class DatabaseMigration {
    public static final String CREATE_DATABASE2_SQL = "create_database2.sql";

    public static void createDatabase(Context c, UserRecordDatabase db) {
        String currentStatement = null;
        try {
            InputStream is = c.getResources().getAssets().open(
                    CREATE_DATABASE2_SQL);

            String[] statements = FileHelper.parseSqlFile(is);

            for (int i = 0; i < statements.length; i++) {

                String statement = statements[i];
                currentStatement = statement;
                SupportSQLiteStatement supportSQLiteStatement = db.compileStatement(statement);
                if (i == 0) {

                    supportSQLiteStatement.executeUpdateDelete();
                } else {
                    supportSQLiteStatement.executeInsert();
                }

            }
            db.japaneseLessonDao().getAll(1);

        } catch (Exception ex) {
            Log.e("DATABASE", "DATABASE ERROR: " + currentStatement, ex);
        }
        Log.e("DATABASE", "DATABASE CREATED");
    }


}
