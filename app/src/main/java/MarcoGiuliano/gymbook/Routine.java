package MarcoGiuliano.gymbook;

import java.io.Serializable;
import java.util.List;

public class Routine implements Serializable{
    private int id;
    private String name;
    private List<Exercise> listExercise;


    public Routine(String name, int id) {
        this.name = name;
        this.id = id;
    }
    public Routine(){
    }


    public void setName(String name){
        this.name = name;
    }
    public void setId(int id){
        this.id = id;
    }
    public void setListExercise(List<Exercise> listExercise){
        this.listExercise = listExercise;
    }


    public String getName(){
        return name;
    }
    public int getId(){
        return id;
    }
    public List<Exercise> getListExercise(){
        return listExercise;
    }
    public void addExercise(Exercise exercise){
        listExercise.add(exercise);
    }

}
