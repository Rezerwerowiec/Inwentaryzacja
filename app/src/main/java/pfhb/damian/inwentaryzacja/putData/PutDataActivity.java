package pfhb.damian.inwentaryzacja.putData;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import pfhb.damian.inwentaryzacja.R;

public class PutDataActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    boolean fromdb = false;
    String barcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_put_data);
        barcode = getIntent().getStringExtra("barcode");
        showBarcode();
    }

    private void showBarcode() {
        TextView tv = findViewById(R.id.info);
        tv.setText(barcode);


        db.collection("Inwentaryzacja_testy")
                .document(barcode)
                .get()
                .addOnCompleteListener(task ->{
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        assert document != null;
                        if (document.exists()) {
                            fromdb = true;
                            tv.setText(tv.getText() + "\n"+ Objects.requireNonNull(document.getData()).get("Item"));
                            EditText min = findViewById(R.id.min);
                            EditText typ = findViewById(R.id.item_type);
                            typ.setText(Objects.requireNonNull(document.getData().get("Item")).toString());
                            min.setText(Objects.requireNonNull(document.getData().get("min")).toString());
                        }
                        else tv.setText(tv.getText() + "\nBrak produktu w bazie!\nUzupełnij nazwę oraz minimum.");
                    }
                });
    }



    public void onClickSendData(View view) {
        EditText et;
        EditText sp = findViewById(R.id.item_type);
        Map<String, Object> dataToSend = new HashMap<>();

        String item = sp.getText().toString();
        dataToSend.put("Barcode", barcode);

        if(!item.equals("Nie zmieniaj nazwy###"))
            dataToSend.put("Item", item);
        else if(!fromdb) {
            Toast.makeText(getBaseContext(), "Wybierz typ przedmiotu!", Toast.LENGTH_LONG).show();
            return;
        }

        EditText et2 =findViewById(R.id.min);
        if(et2.getText().toString().equals("")){
            Toast.makeText(getBaseContext(), "Wypełnij minimalną ilość sztuk!", Toast.LENGTH_SHORT).show();
            return;
        }
        dataToSend.put("min", et2.getText().toString());


        FireBasePutData(dataToSend);

        TextView textView = findViewById(R.id.info);
        textView.setText("");
        et = findViewById(R.id.quantity);
        et.setText("");
        barcode = "";

    }

    public synchronized void FireBasePutData(Map<String, Object> newData) {


        db.collection("Inwentaryzacja_testy")
                .document(barcode)
                .set(newData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot added");
                    Toast.makeText(getApplicationContext(), "Data successfully sent.", Toast.LENGTH_LONG).show();

                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                    Toast.makeText(getApplicationContext(), "Cannot send data...", Toast.LENGTH_LONG).show();

                });
        EditText et = findViewById(R.id.quantity);
        int quantity = 0;

        try{
            quantity = Integer.parseInt(String.valueOf(et.getText()));
        } catch (NumberFormatException e){
            Log.d(TAG, e.getLocalizedMessage());
        }
        if(quantity == 0)
            quantity = 1;

        Map<String, Object> mapped = new HashMap<>();
        if(quantity>0) {
            mapped.put("q_added", FieldValue.increment(quantity));
            mapped.put("q_deleted", FieldValue.increment(0));
        }
        else{
            mapped.put("q_deleted", FieldValue.increment(-quantity));
            mapped.put("q_added", FieldValue.increment(0));
        }
        mapped.put("quantity", FieldValue.increment(quantity));

        try{
            wait(1200);
            db.collection("Inwentaryzacja_testy")
                    .document(barcode)
                    .update(mapped);
            wait(1200);
            MakeLogs(newData, quantity);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void MakeLogs(Map<String,Object> newData, int quantity){
        newData.put("quantity", quantity);
        newData.put("user", Build.MANUFACTURER +" " + Build.MODEL);
        db.collection("Inwentaryzacja_testy_logs")
                .document(Timestamp.now().toDate().toString())
                .set(newData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Logs: DocumentSnapshot added"))
                .addOnFailureListener(e -> Log.w(TAG, "Logs: Error adding document", e));
    }
}