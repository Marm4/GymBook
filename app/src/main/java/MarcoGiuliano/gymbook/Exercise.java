package MarcoGiuliano.gymbook;

import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class Exercise extends Routine{
    private int id;
    private String name;
    private transient EditText etSeries, etReps, etTime_weight;
    private final List<Metrics> listMetrics;

    public Exercise(){
        listMetrics = new ArrayList<>();
    }

    public void setEditText(EditText etSeries, EditText etReps, EditText etTime_weight){
        this.etSeries = etSeries;
        this.etReps = etReps;
        this.etTime_weight = etTime_weight;
    }

    public EditText getEtSeries() {
        return etSeries;
    }

    public EditText getEtReps() {
        return etReps;
    }

    public EditText getEtTime_weight() {
        return etTime_weight;
    }

    public List<Metrics> getListMetrics() {
        return listMetrics;
    }
    public void addMetrics(Metrics metrics){
        listMetrics.add(metrics);
    }

}
