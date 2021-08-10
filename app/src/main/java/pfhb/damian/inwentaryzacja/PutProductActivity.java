package pfhb.damian.inwentaryzacja;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.admin.DeviceAdminInfo;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Selection;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        db = FirebaseFirestore.getInstance();


        setUpSpinner();
    }

    private void setUpSpinner(){
        Spinner spinner = (Spinner) findViewById(R.id.item_type);
        List<String> spinnerArray =  new ArrayList<String>();
        db.collection("itemTypes")
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot document: task.getResult()){
                            spinnerArray.add(document.getId());

                        }
                        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                                this,R.layout.spinner_layout,spinnerArray);
                        spinner.setBackgroundColor(Color.rgb(100,100,100));
                        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_layout);
                        spinnerArrayAdapter.notifyDataSetChanged();

                        spinner.setAdapter(spinnerArrayAdapter);
                    }
                });



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

        Map<String, Object> mapped = new HashMap<>();
        if(quantity>0) {
            mapped.put("q_added", FieldValue.increment(quantity));
        }
        else{
            mapped.put("q_deleted", FieldValue.increment(-quantity));
        }
        mapped.put("quantity", FieldValue.increment(quantity));

        try{
            wait(1200);
            db.collection("Inwentaryzacja_testy")
                    .document(barcodeSaved)
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
        newData.put("user", Build.MANUFACTURER.toString()+" " + Build.MODEL.toString());
        db.collection("Inwentaryzacja_testy_logs")
                .document(Timestamp.now().toDate().toString())
                .set(newData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Logs: DocumentSnapshot added");
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Logs: Error adding document", e);
                });
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

        if(!item.equals("!Nie zmieniaj nazwy"))
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

    public void onClickShowReport(View view){
        if(barcodeSaved.equals("")){
            Toast.makeText(this, "Zeskanuj najpierw BARCODE!!!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, ReportGraphActivity.class);
        intent.putExtra("barcode", barcodeSaved);
        startActivity(intent);
    }



}
