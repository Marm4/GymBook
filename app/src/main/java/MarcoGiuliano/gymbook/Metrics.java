package MarcoGiuliano.gymbook;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Metrics extends Exercise{
    private int series;
    private int reps;
    private int time;
    private Double weight;
    private String date;


    public Metrics(int series, int reps, int time, double weight, String date){
        this.series = series;
        this.reps = reps;
        this.time = time;
        this.weight = weight;
        this.date = date;
    }

    public Metrics(){
        setDate();
    }

    public void setSeries(int series){   this.series = series;   }

    public void setReps(int reps){   this.reps = reps;   }

    public void setWeight(Double weight){   this.weight = weight;   }

    @SuppressLint("SimpleDateFormat")
    private void setDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        this.date = dateFormat.format(new Date());
    }

    public int getSeries(){   return this.series;   }

    public int getReps(){   return this.reps;   }

    public double getWeight(){   return this.weight;   }

    public int getTime(){   return this.time;   }

    public String getDate(){
        return date;
    }
}
