package pfhb.damian.inwentaryzacja;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.w3c.dom.Text;

import java.util.Map;
import java.util.Objects;

public class ReportGraphActivity extends AppCompatActivity {

    LineGraphSeries<DataPoint> series, series2;
    FirebaseFirestore db;
    String barcodeSaved;
    double x = 0;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_graph);
        Intent intent = getIntent();
        barcodeSaved = intent.getStringExtra("barcode");
        db = FirebaseFirestore.getInstance();
        TextView viewQuantity = findViewById(R.id.textViewQuantity);
        TextView viewAdded = findViewById(R.id.textViewAdded);
        TextView viewDeleted = findViewById(R.id.textViewDeleted);

        GraphView graph = (GraphView) findViewById(R.id.graphView);
        GraphView graph2 = (GraphView) findViewById(R.id.graphView2);

        series = new LineGraphSeries<DataPoint>();
        series2 = new LineGraphSeries<DataPoint>();

        db.collection("Inwentaryzacja_testy_logs")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Getting data...", Toast.LENGTH_LONG).show();
                            int y = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                date = document.getId().toString();
                                String[] parts = date.split(" ");
                                date = parts[1];
                                if(document.getData().get("Barcode").equals(barcodeSaved) && date.contains("Aug")) {
                                    x++;
                                    y += Integer.valueOf(document.getData().get("quantity").toString());
                                    int y2 = Integer.valueOf(document.getData().get("quantity").toString());
                                    series.appendData(new DataPoint(x, y), true, 50);
                                    series2.appendData(new DataPoint(x, y2), true, 50);
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            Toast.makeText(getApplicationContext(), "FAILED", Toast.LENGTH_LONG).show();
                        }

                        Toast.makeText(getApplicationContext(), String.valueOf(x), Toast.LENGTH_SHORT).show();
                        series.setDrawDataPoints(true);

                        graph.getViewport().setXAxisBoundsManual(true);
                        graph.getViewport().setMinX(0);
                        graph.getViewport().setMaxX(x+1);
                        graph.getGridLabelRenderer().setHorizontalAxisTitle(date);
                        graph.getGridLabelRenderer().setVerticalAxisTitle("Sztuki");
                        graph.setTitle("Wykaz ilości sztuk");
                        graph.addSeries(series);

                        series2.setDrawDataPoints(true);
                        graph2.getGridLabelRenderer().setHorizontalAxisTitle(date);
                        graph2.getGridLabelRenderer().setVerticalAxisTitle("Sztuki");
                        graph2.getViewport().setXAxisBoundsManual(true);
                        graph2.getViewport().setMinX(0);
                        graph2.getViewport().setMaxX(x+1);
                        graph2.setTitle("Wykaz dodawanych sztuk");
                        graph2.addSeries(series2);
                    }
                });

        db.collection("Inwentaryzacja_testy")
            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())){
                            if(document.getData().get("Barcode").equals((barcodeSaved))){
                                viewQuantity.setText("Sztuk łącznie: " + document.getData().get("quantity").toString());
                                viewAdded.setText("Sztuk dodanych: " + document.getData().get("q_added").toString());
                                viewDeleted.setText("Sztuk usuniętych: " + document.getData().get("q_deleted").toString());
                            }
                        }
                    }
                }
        });
    }
}