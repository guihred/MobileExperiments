package red.guih.games.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.InputStream;

/**
 * Class to update the database
 * Created by guilherme.hmedeiros on 16/03/2018.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    /*
     * The Android's default system path of the application database in internal
     * storage. The package of the application is part of the path of the
     * directory.
     */

    private static final String DB_NAME = UserRecord.DATABASE_NAME;
    public static final int VERSION = 6;
    public static final String CREATE_DATABASE2_SQL = "create_database2.sql";

    private final Context myContext;

    private boolean createDatabase;
    private boolean upgradeDatabase;

    /**
     * Constructor Takes and keeps a reference of the passed context in order to
     * access to the application assets and resources.
     *
     * @param context the context
     */
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        myContext = context;
        // Get the path of the database that is based on the context.

    }

    /**
     * Upgrade the database in internal storage if it exists but is not current.
     * Create a new empty database in internal storage if it does not exist.
     */
    public void initializeDataBase() {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        if (upgradeDatabase || createDatabase) {
            try {
                onCreate(writableDatabase);
            } catch (Exception e) {
                throw new Error("Error copying database");
            }
        }


    }

    /*
     * This is where the creation of tables and the initial population of the
     * tables should happen, if a database is being created from scratch instead
     * of being copied from the application package assets. Copying a database
     * from the application package assets to internal storage inside this
     * method will result in a corrupted database.
     * <P>
     * NOTE: This method is normally only called when a database has not already
     * been created. When the database has been copied, then this method is
     * called the first time a reference to the database is retrieved after the
     * database is copied since the database last cached by SQLiteOpenHelper is
     * different than the database in internal storage.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        /*
         * Signal that a new database needs to be copied. The copy process must
         * be performed after the database in the cache has been closed causing
         * it to be committed to internal storage. Otherwise the database in
         * internal storage will not have the same creation timestamp as the one
         * in the cache causing the database in internal storage to be marked as
         * corrupted.
         */
        createDatabase = true;

        /*
         * This will create by reading a sql file and executing the commands in
         * it.
         */
        try {
            InputStream is = myContext.getResources().getAssets().open(
                    CREATE_DATABASE2_SQL);

            String[] statements = FileHelper.parseSqlFile(is);
            db.beginTransaction();
            for (int i = 0; i < statements.length; i++) {
                String statement = statements[i];
                db.execSQL(statement);

                if(i%100==0){
                    db.endTransaction();
                    db.beginTransaction();
                }
            }
            db.endTransaction();
        } catch (Exception ex) {
            Log.e("DATABASE", "DATABASE ERROR", ex);
        }
    }

    /**
     * Called only if version number was changed and the database has already
     * been created. Copying a database from the application package assets to
     * the internal data system inside this method will result in a corrupted
     * database in the internal data system.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*
         * Signal that the database needs to be upgraded for the copy method of
         * creation. The copy process must be performed after the database has
         * been opened or the database will be corrupted.
         */
        upgradeDatabase = true;

        /*
         * Code to update the database via execution of sql statements goes
         * here.
         */

        /*
         * This will upgrade by reading a sql file and executing the commands in
         * it.
         */
        try {
            InputStream is = myContext.getResources().getAssets().open(
                    CREATE_DATABASE2_SQL);

            String[] statements = FileHelper.parseSqlFile(is);

            for (String statement : statements) {
                db.execSQL(statement);
            }
        } catch (Exception ex) {
            Log.e("DATABASE", "DATABASE ERROR", ex);
        }
    }

    /**
     * Called every time the database is opened by getReadableDatabase or
     * getWritableDatabase. This is called after onCreate or onUpgrade is
     * called.
     */
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    /*
     * Add your public helper methods to access and get content from the
     * database. You could return cursors by doing
     * "return myDataBase.query(....)" so it'd be easy to you to create adapters
     * for your views.
     */

}
