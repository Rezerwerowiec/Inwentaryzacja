package pfhb.damian.inwentaryzacja;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class PutProductActivity extends AppCompatActivity {

    FirebaseFirestore db;
    String barcodeSaved;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.put_product_activity);

        db = FirebaseFirestore.getInstance();

    }


    public synchronized void FireBasePutData(Map<String, Object> newData) throws InterruptedException {


        db.collection("Inwentaryzacja_testy")
                .document(barcodeSaved)
                .set(newData, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot added");
                        Toast.makeText(getApplicationContext(), "Data successfully sent.", Toast.LENGTH_LONG).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        Toast.makeText(getApplicationContext(), "Cannot send data...", Toast.LENGTH_LONG).show();

                    }
                });

        try{
            wait(2000);
            EditText et = (EditText) findViewById(R.id.quantity);
            db.collection("Inwentaryzacja_testy")
                    .document(barcodeSaved)
                    .update("quantity", FieldValue.increment(Integer.valueOf(String.valueOf(et.getText()))));
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }




    }

    public void FireBaseReadData(){
        db.collection("Inwentaryzacja_testy")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }




    public void scanBarCode(View view) throws SQLException, InterruptedException {


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
                TextView textView = (TextView) findViewById(R.id.barcodeview);
                textView.setText(intentResult.getContents());
                barcodeSaved = intentResult.getContents();
            }
        }


    }
    public void onClickSendData(View view) throws InterruptedException {
        EditText et = (EditText) findViewById(R.id.quantity);
        Spinner sp = (Spinner) findViewById(R.id.item_type);
        Map<String, Object> dataToSend = new HashMap<>();

        dataToSend.put("Barcode", barcodeSaved);
        dataToSend.put("Item", String.valueOf(sp.getSelectedItem()));
        FireBasePutData(dataToSend);


        TextView textView = (TextView) findViewById(R.id.barcodeview);
        textView.setText("");
        et =(EditText) findViewById(R.id.quantity);
        et.setText("");
    }

}
