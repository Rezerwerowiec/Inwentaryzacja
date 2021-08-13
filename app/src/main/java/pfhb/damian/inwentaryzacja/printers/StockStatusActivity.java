package pfhb.damian.inwentaryzacja.printers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

import pfhb.damian.inwentaryzacja.R;

public class StockStatusActivity extends AppCompatActivity {

    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_status);
        db = FirebaseFirestore.getInstance();
        showStockInfo();
    }

    private void showStockInfo() {
        TextView tv = findViewById(R.id.stockInfo);

        db.collection("Inwentaryzacja_testy")
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot document : task.getResult()){
                            String txt = tv.getText().toString();
                            String name = document.getData().get("Item").toString();
                            int min = Integer.parseInt(Objects.requireNonNull(document.getData().get("min")).toString());
                            int pcs = Integer.parseInt(Objects.requireNonNull(document.getData().get("quantity")).toString());
                            if(pcs < min){
                                tv.setText(name + " " + pcs + " szt. (min. " + min + ")\n"+txt);
                            }
                            else
                                tv.setText(txt + "\n" + name + " " + pcs + " szt. (min. " + min + ")");
                        }
                    }
                });
    }
}