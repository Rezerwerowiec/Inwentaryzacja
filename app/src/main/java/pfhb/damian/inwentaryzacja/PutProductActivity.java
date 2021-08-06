package pfhb.damian.inwentaryzacja;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PutProductActivity extends AppCompatActivity {

    FirebaseFirestore db;
    Map<String, Object> data;
    String barcodeSaved ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.put_product_activity);
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.item_type, R.layout.spinner_view); //change the last argument here to your xml above.
        typeAdapter.setDropDownViewResource(android.R.layout.activity_list_item);
        db = FirebaseFirestore.getInstance();

    }


    public synchronized void FireBasePutData(Map<String, Object> newData) {


        db.collection("Inwentaryzacja_testy")
                .document(barcodeSaved)
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

        try{
            wait(2000);
            db.collection("Inwentaryzacja_testy")
                    .document(barcodeSaved)
                    .update("quantity", FieldValue.increment(quantity));
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }




    }

    public void FireBaseReadData(){
        db.collection("Inwentaryzacja_testy")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }




    public void scanBarCode(View view) {


        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan Code");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(intentResult != null) {
            if(intentResult.getContents() == null) {
                Log.d("MainActivity", "Cancelled");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Scanned");
                Toast.makeText(this, "Scanned: " + intentResult.getContents(), Toast.LENGTH_LONG).show();
                TextView textView = findViewById(R.id.barcodeview);
                textView.setText(intentResult.getContents());
                barcodeSaved = intentResult.getContents();
            }
        }


    }
    public void onClickSendData(View view) {
        EditText et;
        Spinner sp = findViewById(R.id.item_type);
        Map<String, Object> dataToSend = new HashMap<>();
        if(barcodeSaved.equals("") || barcodeSaved == null) {
            Toast.makeText(this, "Najpierw zeskanuj BARCODE!!!", Toast.LENGTH_LONG).show();
            return;
        }
        String item = String.valueOf(sp.getSelectedItem());
        dataToSend.put("Barcode", barcodeSaved);

        if(!item.equals("Nie zmieniaj nazwy..."))
            dataToSend.put("Item", item);


        FireBasePutData(dataToSend);

        TextView textView = findViewById(R.id.barcodeview);
        textView.setText("");
        et = findViewById(R.id.quantity);
        et.setText("");
        barcodeSaved = "";

    }

    public void FireBaseGetInfoAboutBarcode(View view){
        if(barcodeSaved.equals("")){
            Toast.makeText(this, "Zeskanuj najpierw BARCODE!!!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, BarcodeInfoActivity.class);
        intent.putExtra("barcode", barcodeSaved);
        startActivity(intent);

    }


}
