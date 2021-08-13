package pfhb.damian.inwentaryzacja;

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
        TextView tv2 = findViewById(R.id.pcs);

        db.collection("Inwentaryzacja_testy")
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot document : task.getResult()){
                            String txt = tv.getText().toString();
                            String txt2 = tv2.getText().toString();
                            String name = document.getData().get("Item").toString();
                            int min = Integer.parseInt(Objects.requireNonNull(document.getData().get("min")).toString());
                            int pcs = Integer.parseInt(Objects.requireNonNull(document.getData().get("quantity")).toString());
                            int balance = pcs-min;
                            if(balance <= 0){
                                tv.setText(name + " \n"+txt);
                                tv2.setText(pcs + " szt. (min. " + min + ")  " + balance + "\n" + txt2);
                            }
                            else {
                                tv.setText(txt + "\n" + name + " ");
                                tv2.setText(txt2 +"\n" + pcs + " szt. (min. " + min + ")  +" + balance);
                            }
                        }
                    }
                });
    }
}