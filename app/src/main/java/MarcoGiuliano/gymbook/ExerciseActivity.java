package MarcoGiuliano.gymbook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ExerciseActivity extends AppCompatActivity {
    private Routine routine;
    private Button btSave, btRecord;
    private ExerciseActivityLogic logic;
    private DatabaseManager database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        initializer();
        sharedPrefs();
        addButtonsListeners();
    }

    private void initializer(){
        routine = (Routine)getIntent().getSerializableExtra("routine");

        TableLayout tlRoutine = findViewById(R.id.tlRoutine);
        btSave = findViewById(R.id.btSave);
        btRecord = findViewById(R.id.btHistory);

        database = new DatabaseManager(this);
        logic = new ExerciseActivityLogic(this, tlRoutine, routine, database);

        loadData();
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String editTextValue;
        for(Exercise exercise : routine.getListExercise()){
            editTextValue = exercise.getEtSeries().getText().toString();
            editor.putString("series", editTextValue);

            editTextValue = exercise.getEtReps().getText().toString();
            editor.putString("reps", editTextValue);

            editTextValue = exercise.getEtTime_weight().getText().toString();
            editor.putString("weight_time", editTextValue);

        }
        editor.apply();
    }

    private void sharedPrefs(){
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String savedText;

        for(Exercise exercise : routine.getListExercise()){
            if(exercise.getEtSeries() != null ||
               exercise.getEtReps() != null ||
               exercise.getEtTime_weight() != null) {

                savedText = sharedPreferences.getString("series", "");
                exercise.getEtSeries().setText(savedText);

                savedText = sharedPreferences.getString("reps", "");
                exercise.getEtReps().setText(savedText);

                savedText = sharedPreferences.getString("weight_time", "");
                exercise.getEtTime_weight().setText(savedText);
            }
        }
    }
    private void addButtonsListeners(){
        btSave.setOnClickListener(view -> saveData());
        btRecord.setOnClickListener(view -> seeMetrics());
    }

    private void loadData(){
        setTitle(routine.getName());
        routine.setListExercise(database.loadDataExercise(routine.getId()));
        for(Exercise exercise : routine.getListExercise()){
            logic.addNewExercise(exercise);
        }
        logic.addNewExercise(null);
    }

    private void setTitle(String name){
        TextView tvName = findViewById(R.id.tvNameOfRoutineRoutine);
        tvName.setText(name);
    }

    private void saveData(){
        for(Exercise exercise : routine.getListExercise()){
            Metrics metrics = new Metrics();

            String series = exercise.getEtSeries().getText().toString();
            if(series.isEmpty()) series = String.valueOf(0);
            metrics.setSeries(Integer.parseInt(series));

            String reps = exercise.getEtReps().getText().toString();
            if(reps.isEmpty())    reps = String.valueOf(0);
            metrics.setReps(Integer.parseInt(reps));

            String weight = exercise.getEtTime_weight().getText().toString();
            if(weight.isEmpty())   weight = String.valueOf(0);
            metrics.setWeight(Double.parseDouble(weight));

            database.saveMetrics(metrics, routine.getId(), exercise.getId());
            logic.cleanEditText(exercise);
        }
    }

    private void seeMetrics(){
        Intent intent = new Intent(ExerciseActivity.this, MetricsActivity.class);
        intent.putExtra("routine", routine);
        startActivity(intent);
    }


}