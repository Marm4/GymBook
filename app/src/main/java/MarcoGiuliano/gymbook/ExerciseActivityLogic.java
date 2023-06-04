package MarcoGiuliano.gymbook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.util.List;

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
            name.setInputType(InputType.TYPE_NULL);
            new ButtonAnimationHelper(null, this, name);
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
        etName.setId(id);
        exercise.setId(id);
        exercise.setName(name);
        routine.addExercise(exercise);
        etName.setInputType(InputType.TYPE_NULL);

        addNewExercise(null);
        new ButtonAnimationHelper(null, this, etName);
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

    public void editTextName(EditText changeName){
        changeName.setText("");
        changeName.setInputType(InputType.TYPE_CLASS_TEXT);
    }


    public void alertDelete(EditText etDelete){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete exercise");
        builder.setMessage("Are you sure you want to delete this exercise? All metrics wil be deleted as well");

        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                database.deleteDataExercise(etDelete.getId());
                tlRoutine.removeView((TableRow)etDelete.getParent());
                removeExerciseById(etDelete.getId());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void removeExerciseById(int id){
        List<Exercise> listExercise = routine.getListExercise();
        for(Exercise exercise : routine.getListExercise()){
            if(exercise.getId() == id) {
                listExercise.remove(exercise);
            }
        }
    }
}
