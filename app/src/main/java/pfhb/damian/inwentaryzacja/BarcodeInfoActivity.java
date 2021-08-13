package pfhb.damian.inwentaryzacja;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class BarcodeInfoActivity extends AppCompatActivity {
    FirebaseFirestore db;
    String barcodeSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_info);
        Intent intent = getIntent();
        barcodeSaved = intent.getStringExtra("barcode");

        db = FirebaseFirestore.getInstance();
        Toast.makeText(this, barcodeSaved, Toast.LENGTH_LONG).show();
        GetInfo();
    }

    private void GetInfo(){
        DocumentReference docRef = db.collection("Inwentaryzacja_testy").document(barcodeSaved);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    SecureData(document.getData());
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    private void SecureData(Map<String, Object> data){
        if(data.equals(null)){
            Toast.makeText(this, "Brak danych", Toast.LENGTH_LONG).show();
            return;
        }
        String info = "BARCODE: " + barcodeSaved;
        info +="\nNazwa:\t";
        TextView textView = findViewById(R.id.info);
        info += data.get("Item");
        info += "\nIlość:\t" + data.get("quantity");
        textView.setText(info);
    }

}