package MarcoGiuliano.gymbook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.annotation.NonNull;

public class ExerciseActivityLogic {
    private final Context context;
    private final DatabaseManager database;
    private final TableLayout tlRoutine;
    private final Routine routine;

    public ExerciseActivityLogic(Context context, TableLayout tlRoutine, Routine routine, DatabaseManager database){
        this.context = context;
        this.tlRoutine = tlRoutine;
        this.routine = routine;
        this.database = database;
    }

    public void addNewExercise(Exercise exercise){
        LayoutInflater inflater = LayoutInflater.from(context);
        TableRow tableRow = (TableRow) inflater.inflate(R.layout.table_row_exercise, tlRoutine, false);
        EditText name = tableRow.findViewById(R.id.etName);

        if (exercise==null) {
            exercise = new Exercise();
            setOnEnter(name, exercise);
        } else {
            name.setText(exercise.getName());
            name.setEnabled(false);
        }

        EditText series = tableRow.findViewById(R.id.etSeries);
        EditText reps = tableRow.findViewById(R.id.etReps);
        EditText weight_time = tableRow.findViewById(R.id.etKg);
        exercise.setEditText(series, reps, weight_time);

        tlRoutine.addView(tableRow);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setOnEnter(EditText etName, Exercise exercise){
        inputSettings(etName);
        etName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if(!etName.getText().toString().isEmpty()){
                   onEnter(etName, exercise);
                }
                return true;
            }
            return false;
        });

        tlRoutine.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if(!etName.getText().toString().isEmpty()){
                    onEnter(etName, exercise);
                }
                return true;
            }
            return false;
        });
    }

    private void onEnter(EditText etName, Exercise exercise){
        int id;
        String name = etName.getText().toString().toUpperCase();

        etName.setText(name);
        id = database.saveExerciseName(name, routine.getId());
        exercise.setId(id);
        exercise.setName(name);
        routine.addExercise(exercise);

        etName.setEnabled(false);

        addNewExercise(null);
    }

    private void inputSettings(@NonNull EditText name){
        name.setSingleLine(true);
        name.setImeOptions(EditorInfo.IME_ACTION_DONE);
        name.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);
    }

    public void cleanEditText(Exercise exercise){
        exercise.getEtSeries().setText("");
        exercise.getEtReps().setText("");
        exercise.getEtTime_weight().setText("");
    }
}
