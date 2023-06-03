package MarcoGiuliano.gymbook;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private final SQLiteDatabase database;

    public DatabaseManager(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        database = databaseHelper.getWritableDatabase();

    }

    public int saveDataRoutine(String name){
        ContentValues values = new ContentValues();
        values.put("name", name);
        return (int)database.insert("routine", null, values);
    }

    @SuppressLint("Range")
    public List<Routine> loadDataRoutine(){
        Cursor cursor = database.query("routine", null, null, null, null, null, null);
        List<Routine> listRoutine = new ArrayList<>();
        while(cursor.moveToNext()){
            Routine routine = new Routine();
            routine.setId(cursor.getInt(cursor.getColumnIndex("id_routine")));
            routine.setName(cursor.getString(cursor.getColumnIndex("name")).toUpperCase());
            listRoutine.add(routine);
        }
        cursor.close();
        return listRoutine;
    }

    public void deleteButtonData(int id){
            String whereClause = "id_routine = ?";
            String[] whereArgs = {String.valueOf(id)};
            database.delete("routine", whereClause, whereArgs);
            database.delete("exercise", whereClause, whereArgs);
            database.delete("metrics", whereClause, whereArgs);
        }

    public void uploadButton(int id, String inputText){
        String whereClause = "id_routine = ?";
        String[] whereArgs = {String.valueOf(id)};
        Cursor cursor = database.query("routine", null,whereClause, whereArgs, null, null, null);
        if(cursor.moveToFirst()){
            ContentValues values = new ContentValues();
            values.put("name", inputText);
            database.update("routine", values, whereClause, whereArgs);
        }
        cursor.close();
    }

    public int saveExerciseName(String name, int id_routine){
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("id_routine", id_routine);
        return (int)database.insert("exercise", null, values);
    }

    @SuppressLint("Range")
    public List<Exercise> loadDataExercise(int id_routine){
        Cursor cursor = database.query("exercise", null, "id_routine ="+ id_routine, null, null, null, null);
        List<Exercise> listExercises = new ArrayList<>();
        while(cursor.moveToNext()){
            Exercise exercise = new Exercise();
            exercise.setId(cursor.getInt(cursor.getColumnIndex("id_exercise")));
            exercise.setName(cursor.getString(cursor.getColumnIndex("name")));
            listExercises.add(exercise);
        }
        cursor.close();
        return listExercises;
    }

    public void saveMetrics(Metrics metrics, int id_routine, int id_exercise){
        if(metrics.getSeries() == 0 && metrics.getReps() == 0 && metrics.getWeight() == 0) return;
        ContentValues values = new ContentValues();

        values.put("series", metrics.getSeries());
        values.put("reps", metrics.getReps());
        values.put("time", 0);
        values.put("weight", metrics.getWeight());
        values.put("date", metrics.getDate());
        values.put("id_routine", id_routine);
        values.put("id_exercise", id_exercise);
        database.insert("metrics", null, values);
    }


    @SuppressLint("Range")
    public void loadDataMetrics(int id_routine, Exercise exercise){
        String selection = "id_routine = ? AND id_exercise = ?";
        String[] selectionArgs = new String[]{String.valueOf(id_routine), String.valueOf(exercise.getId())};

        Cursor cursor = database.query("metrics", null, selection, selectionArgs, null, null, null);
        while(cursor.moveToNext()){
            int series = cursor.getInt(cursor.getColumnIndex("series"));
            int reps = cursor.getInt(cursor.getColumnIndex("reps"));
            int time = cursor.getInt(cursor.getColumnIndex("time"));
            double weight = cursor.getDouble(cursor.getColumnIndex("weight"));
            String date = cursor.getString(cursor.getColumnIndex("date"));

            Metrics metrics = new Metrics(series, reps, time, weight, date);
            exercise.addMetrics(metrics);
        }
        cursor.close();
    }

}


