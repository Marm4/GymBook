package MarcoGiuliano.gymbook;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class RoutineActivity extends AppCompatActivity {
    private Button btAddNewRoutine;
    private ViewGroup layout;
    private LayoutInflater inflater;
    private RoutineActivityLogic logic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine);

        initializerVars();
        loadAndListenerButtons();
    }

    @Override
    public void onBackPressed() {
        logic.restoreView();
    }

    private void initializerVars(){
        btAddNewRoutine = findViewById(R.id.btAddNewRoutine);
        layout = findViewById(R.id.rlButtonContainer);
        inflater = LayoutInflater.from(RoutineActivity.this);
        logic = new RoutineActivityLogic(this, layout, inflater, btAddNewRoutine);
    }

    private void loadAndListenerButtons(){
       logic.loadAndAddFocus(getWindow());
       btAddNewRoutine.setOnClickListener(view -> addNewRoutine());
    }

    private void addNewRoutine(){
        createNewEditText();
        logic.setView(btAddNewRoutine, 0,0);
    }

    private void createNewEditText(){
        EditText input = (EditText) inflater.inflate(R.layout.edit_text_layout_routine, layout, false);
        logic.etSettings(input, (int)btAddNewRoutine.getX(), (int)btAddNewRoutine.getY());
        layout.addView(input);
        logic.setListenerEditText(input);
    }

}