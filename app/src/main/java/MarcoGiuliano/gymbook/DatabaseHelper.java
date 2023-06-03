package MarcoGiuliano.gymbook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "DATABASE_NAME";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE routine (id_routine INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT)");
        db.execSQL("CREATE TABLE exercise (id_exercise INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, id_routine INTEGER, FOREIGN KEY (id_routine) REFERENCES routine(id))");
        db.execSQL("CREATE TABLE metrics (id_metrics INTEGER PRIMARY KEY AUTOINCREMENT, series INTEGER, reps INTEGER, time INTEGER, weight REAL, date TEXT, id_exercise INTEGER, id_routine INTEGER, FOREIGN KEY (id_exercise) REFERENCES exercise(id), FOREIGN KEY (id_routine) REFERENCES routine(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE routine ADD COLUMN nueva_columna TEXT");
            db.execSQL("ALTER TABLE exercise ADD COLUMN nueva_columna TEXT");
            db.execSQL("ALTER TABLE metrics ADD COLUMN nueva_columna TEXT");
        }
    }

}