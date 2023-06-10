package MarcoGiuliano.gymbook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

public class RoutineActivityLogic {
    private final ViewGroup layout;
    private final LayoutInflater inflater;
    private final DatabaseManager database;
    private final Context context;
    private boolean dataLoaded;
    private final Button btnAddNewRoutine;
    private final List<EditText> listChangeNameRoutine;
    private List<Routine> listRoutine;
    private final List<Button> listButtonRoutine;
    private final List<ButtonAnimationHelper> listButtonAnimation;

    public RoutineActivityLogic(Context context, ViewGroup layout, LayoutInflater inflater, Button btnAddNewRoutine){
        this.layout = layout;
        this.inflater = inflater;
        this.context = context;
        this.btnAddNewRoutine = btnAddNewRoutine;
        database = new DatabaseManager(context);
        listButtonAnimation = new ArrayList<>();
        listChangeNameRoutine = new ArrayList<>();
        listButtonRoutine = new ArrayList<>();
        dataLoaded = false;
    }

    //Creates the list of buttons and organizes the view of the future new buttons
    public void loadAndAddFocus(Window window){
        ViewTreeObserver viewTreeObserver = window.getDecorView().getViewTreeObserver();

        viewTreeObserver.addOnWindowFocusChangeListener(hasFocus -> {
            if (hasFocus && !dataLoaded)  loadData();
        });
    }

    //Brings the information from the data base, and create the buttons necessary.
    private void loadData(){
        listRoutine = database.loadDataRoutine();
        for(Routine routine : listRoutine){
            String name = routine.getName();
            int id = routine.getId();
            createNewButton(id, name);
            setView(btnAddNewRoutine, 0, 0);
        }
        dataLoaded = true;
    }

    //Creates a new button whit all the parameters necessary to work (text, id, view and listeners).
    private void createNewButton(int id, String name){
        Button newButton = (Button) inflater.inflate(R.layout.button_layout_routine, layout, false);
        layout.addView(newButton);
        listButtonRoutine.add(newButton);

        setView(newButton, (int)btnAddNewRoutine.getX(), (int) btnAddNewRoutine.getY());
        newButton.setText(name.toUpperCase());
        newButton.setId(id);
        setClicks(newButton);
    }

    //Sets animation to a button and layout clicks
    @SuppressLint("ClickableViewAccessibility")
    public void setClicks(Button button){
        ButtonAnimationHelper buttonAnimation = new ButtonAnimationHelper(this, null, button);
        listButtonAnimation.add(buttonAnimation);
        layout.setOnClickListener(view -> restoreView());
    }

    //Restores the buttons view
    public void restoreView(){
        for(ButtonAnimationHelper buttonAnimation: listButtonAnimation)
            buttonAnimation.stopAnimation();

        for(EditText etChangeNameRoutine: listChangeNameRoutine){
            layout.removeView(etChangeNameRoutine);
            etChangeNameRoutine.setText("");
        }

        for(Button buttonRoutine: listButtonRoutine){
            if(buttonRoutine.getVisibility() == View.GONE)
                buttonRoutine.setVisibility(View.VISIBLE);
        }
    }

    //Crates a new Exercise Activity whit the parameters necessary
    public void newExerciseActivity(Button button){
        Intent intent = new Intent(context, ExerciseActivity.class);
        int id = button.getId();
        intent.putExtra("routine", findRoutineById(id));
        context.startActivity(intent);
    }

    //Finds a routine by his id
    private Routine findRoutineById(int id){
        for(Routine routine : listRoutine){
            if(routine.getId() == id)
                return routine;
        }
        return null;
    }

    //Sets view (button or editText) on the position it has to be
    public void setView(View view, int positionX, int positionY) {
        if (view instanceof Button) {
            if(positionX == 0 && positionY == 0){
                float button_new_position = context.getResources().getDimension(R.dimen.button_new_position_main_activity);
                view.setY((int) view.getY() + view.getHeight() + button_new_position);
            }
            else {
                Button button = (Button) view;
                button.setX(positionX);
                button.setY(positionY);
            }
        }
        else if (view instanceof EditText) {
            EditText editText = (EditText) view;
            editText.setX(positionX);
            editText.setY(positionY);
        }
    }

    //Add a listener to modify text inside the button through an EditText
    public void editButtonName(Button editButton){
        EditText etChangeNameRoutine = (EditText) inflater.inflate(R.layout.edit_text_layout_routine, layout, false);
        listChangeNameRoutine.add(etChangeNameRoutine);

        etSettings(etChangeNameRoutine, (int)editButton.getX(), (int)editButton.getY());

        layout.addView(etChangeNameRoutine);
        editButton.setVisibility(View.GONE);

        etChangeNameRoutine.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String inputText = etChangeNameRoutine.getText().toString().toUpperCase();
                database.updateButton(editButton.getId(), inputText);
                editButton.setText(inputText);
                editButton.setVisibility(View.VISIBLE);
                layout.removeView(etChangeNameRoutine);
                changeNameRoutineById(editButton.getId(), inputText);
                return true;
            }
            return false;
        });

    }

    //EditText Settings: One line, save on press done, automatic position cursor, show keyboard.
    public void etSettings(EditText input, int posX, int posY){
        setView(input, posX, posY);
        input.setSingleLine(true);
        input.setImeOptions(EditorInfo.IME_ACTION_DONE);
        input.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
    }

    //Put the buttons in their respective places after a deletion
    private void newPositionButtons(Button deletedButton){
        boolean originalPosition = false;
        int positionX = (int) deletedButton.getX();
        int positionY = (int) deletedButton.getY();
        int auxX;
        int auxY;
        for(Button button : listButtonRoutine) {
            if(originalPosition){
                auxX = (int) button.getX();
                auxY = (int) button.getY();
                setView(button, positionX, positionY);
                positionX = auxX;
                positionY = auxY;
            }
            if(button.getId() == deletedButton.getId())    originalPosition = true;
        }
        if(originalPosition)    setView(btnAddNewRoutine, positionX, positionY);
        listButtonRoutine.remove(deletedButton);
    }

    public void changeNameRoutineById(int id, String name){
        for (Routine routine : listRoutine) {
            if (routine.getId() == id) {
                routine.setName(name);
                break;
            }
        }
    }

   //Listener Edit Text. Pressing enter or screen create new Button, then add to listButton (doOnTouch)
    @SuppressLint("ClickableViewAccessibility")
    public void setListenerEditText(EditText input){
        input.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String inputText = input.getText().toString();
                if(!inputText.isEmpty()) {
                    doOnTouch(input, inputText);
                    layout.setOnTouchListener(null);
                }
                return true;
            }
            return false;
        });
        layout.setOnTouchListener((View v, MotionEvent event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                String inputText = input.getText().toString();
                if(!inputText.isEmpty()) {
                    doOnTouch(input, inputText);
                    layout.setOnTouchListener(null);
                }
                return true;
            }
            return false;
        });
    }

    //Creates a new button and put it in the corresponding position
    private void doOnTouch(EditText input, String inputText){
        int id = database.saveDataRoutine(inputText.toUpperCase());
        Routine newRoutine = new Routine(input.getText().toString().toUpperCase(), id);
        listRoutine.add(newRoutine);

        setView(btnAddNewRoutine, (int)input.getX(),(int)input.getY()); //When its create a newButton, its takes the place of btnAddNewRoutine
        createNewButton(id, inputText);
        layout.removeView(input);
        setView(btnAddNewRoutine, 0,0); //Then set btnAddNewRoutine where its have to be
    }

    public void alertDelete(Button button){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete routine");
        builder.setMessage("Are you sure you want to delete this routine? All exercise and metrics will be deleted as well");
        button.setVisibility(View.GONE);

        builder.setPositiveButton("Accept", (dialog, which) -> {
            layout.removeView(button);
            listRoutine.removeIf(routine -> routine.getId() == button.getId());
            database.deleteButtonData(button.getId());
            newPositionButtons(button);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> button.setVisibility(View.VISIBLE));

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
