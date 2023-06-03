package MarcoGiuliano.gymbook;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MetricsActivity extends AppCompatActivity {
    private Routine routine;
    private DatabaseManager database;
    private MetricsActivityLogic logic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metrics);

        initializerVars();
        loadData();
    }

    private void initializerVars(){
        routine = (Routine)getIntent().getSerializableExtra("routine");
        database = new DatabaseManager(this);

        TableLayout tableLayout = findViewById(R.id.tlMetrics);
        logic = new MetricsActivityLogic(this, tableLayout);
    }

    private void loadData(){
        setTitle(routine.getName());
        for(Exercise exercise : routine.getListExercise()) {
            database.loadDataMetrics(routine.getId(), exercise);
            logic.addNewTitle(exercise.getName());
            setMetrics(exercise);
        }

    }

    private void setTitle(String name){
        TextView tvName = findViewById(R.id.tvNameOfRoutineHistory);
        tvName.setText(name);
    }

    private void setMetrics(Exercise exercise){
        for(Metrics metrics : exercise.getListMetrics()){
            logic.addNewExercise(metrics);
        }
    }
  }