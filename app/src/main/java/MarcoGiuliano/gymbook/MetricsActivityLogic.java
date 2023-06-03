package MarcoGiuliano.gymbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MetricsActivityLogic {
    private final TableLayout tableLayout;
    private final LayoutInflater inflater;


    public MetricsActivityLogic(Context context, TableLayout tableLayout){
        this.tableLayout = tableLayout;
        inflater = LayoutInflater.from(context);
    }

    public void addNewTitle(String name){
        TextView tvTitle = (TextView) inflater.inflate(R.layout.text_view_title_metrics, tableLayout, false);

        tvTitle.setText(name);
        tableLayout.addView(tvTitle);
    }

    public void addNewExercise(Metrics metrics){
        TableRow tableRow = (TableRow) inflater.inflate(R.layout.table_row_metrics, tableLayout, false);

        TextView tvSeries = tableRow.findViewById(R.id.tvSeries);
        TextView tvReps = tableRow.findViewById(R.id.tvReps);
        TextView tvKg = tableRow.findViewById(R.id.tvKg);
        TextView tvDate = tableRow.findViewById(R.id.tvDate);

        String series = metrics.getSeries() + " series";
        String reps = metrics.getReps() + " reps";
        String weight = metrics.getWeight() + " kg";

        tvSeries.setText(series);
        tvReps.setText(reps);
        tvKg.setText(weight);
        tvDate.setText(metrics.getDate());

        tableLayout.addView(tableRow);
    }
}
