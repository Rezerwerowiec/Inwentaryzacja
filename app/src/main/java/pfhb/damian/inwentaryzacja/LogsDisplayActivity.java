package pfhb.damian.inwentaryzacja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LogsDisplayActivity extends AppCompatActivity {

    String Date="Data\n\n", User="User\n\n", Barcode="BarCode\n\n", Quantity="Sztuki\n\n";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs_display);
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();

        TextView dateView = findViewById(R.id.viewDate);
        TextView userView = findViewById(R.id.viewUser);
        TextView barcodeView = findViewById(R.id.viewBarcode);
        TextView quantityView = findViewById(R.id.viewQuantity);

        db.collection("Inwentaryzacja_testy_logs")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String[] toSplit = document.getId().split(" ");

                            dateView.setText(dateView.getText() + toSplit[2] +"/" + toSplit[1] + " " + toSplit[3] + "\n");
                            userView.setText(userView.getText().toString() + document.getData().get("user") + "\n");
                            barcodeView.setText(barcodeView.getText().toString() + document.getData().get("Barcode") + "\n");
                            quantityView.setText(quantityView.getText().toString() + document.getData().get("quantity") + "\n");
                            //updateData(Date,Quantity,Barcode,User);

                        }
                    }
                });
    }

    private void updateData(String date, String user, String barcode, String quantity){
        Date += date;
        Quantity += quantity;
        Barcode += barcode;
        User += user;
    }

}