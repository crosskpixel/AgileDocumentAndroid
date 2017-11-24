package schoolofnet.com.agiledocument.Utils.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mactus on 23/11/2017.
 */

public class DbHandler extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "agile_document";
    public static final Integer DATABASE_VERSION = 1;


    public static final String TBL_IP = "tbl_ip";

    public DbHandler(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TBL_IP + " (id INTEGER PRIMARY KEY,ipAddress VARCHAR(20))";
        String sqlInit = "INSERT INTO " + TBL_IP + "(ipAddress) VALUES ('null')";
        db.execSQL(sql);
        db.execSQL(sqlInit);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TBL_IP + " (id INTEGER PRIMARY KEY,ipAddress VARCHAR(20))";
        String sqlInit = "INSERT INTO " + TBL_IP + "(ipAddress) VALUES ('null')";
        db.execSQL(sql);
        db.execSQL(sqlInit);
    }
}
