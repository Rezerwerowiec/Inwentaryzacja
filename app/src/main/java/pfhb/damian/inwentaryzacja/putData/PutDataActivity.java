package pfhb.damian.inwentaryzacja.putData;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import pfhb.damian.inwentaryzacja.R;
import pfhb.damian.inwentaryzacja.ScanBarcodeActivity;

public class PutDataActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    boolean fromdb = false;
    String barcode;
    private long mLastClickTime = 0;
    Variables _var = new Variables();
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
                            //tv.setText(tv.getText() + "\n"+ Objects.requireNonNull(document.getData()).get("Item"));
                            EditText min = findViewById(R.id.min);
                            EditText typ = findViewById(R.id.item_type);
                            typ.setText(Objects.requireNonNull(Objects.requireNonNull(document.getData()).get("Item")).toString());
                            min.setText(Objects.requireNonNull(document.getData().get("min")).toString());
                        }
                        else tv.setText(barcode + "\nBrak produktu w bazie!\nUzupełnij nazwę oraz minimum.");
                    }
                });
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onClickSendData(View view) {

        if (SystemClock.elapsedRealtime() - mLastClickTime < 5000){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        EditText sp = findViewById(R.id.item_type);
        Map<String, Object> dataToSend = new HashMap<>();

        String item = sp.getText().toString();
        dataToSend.put("Item", item);
        dataToSend.put("Barcode", barcode);
        if(item.equals("") || item.isEmpty()) {
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
        startActivity(new Intent(this, ScanBarcodeActivity.class));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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

        getCompatiblePrinters(newData);
    }

    private void CheckForStockStatus(){


        db.collection("Inwentaryzacja_testy")
                .document(barcode)
                .get()
                .addOnCompleteListener(task ->{
                    if(task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        int min, stan;
                        assert document != null;
                        min = Integer.parseInt(String.valueOf(Objects.requireNonNull(document.getData()).get("min")));
                        stan = Integer.parseInt(String.valueOf(document.getData().get("quantity")));
                        String name = String.valueOf(document.getData().get("Item"));
                        if(min > stan){
                            Intent email = new Intent(Intent.ACTION_SEND);
                            email.putExtra(Intent.EXTRA_EMAIL, new String[] {
                                    "d.piszka@pfhb.pl"
                            });

                            email.putExtra(Intent.EXTRA_SUBJECT, name);
                            Log.d(TAG, "Damian2...   " + _var.getResult());


                            email.putExtra(Intent.EXTRA_TEXT, "Wiadomosc automatyczna.\n\n" +
                                    "Material: \t" + name + "\n" +
                                    "Ilosc na stanie/Ilosc minimalna:\t" + stan + "/" + min + "\n" +
                                    "Pasuja do: " + _var.getResult());

                            email.setType("message/rfc822");
                            startActivity(email);
                        }
                    }
                });
    }

    private void getCompatiblePrinters(Map<String, Object> data){
        String item = Objects.requireNonNull(data.get("Item")).toString();

        db.collection("Inwentaryzacja_drukarki")
                .get()
                .addOnCompleteListener(task ->{
                    for(QueryDocumentSnapshot document: Objects.requireNonNull(task.getResult())){
                        List ls = (List) document.getData().get("array.kompatybilne");
                        assert ls != null;
                        if(ls.contains(item)) {
                            _var.addResult(document.getId());
                            Log.d(TAG, "Damian..." + _var.getResult());
                        }
                    }

                });
        if(_var.getResult() != null && !_var.getResult().equals(""))
                _var.addResult("Brak kompatybilnych drukarek.");
        CheckForStockStatus();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void MakeLogs(Map<String,Object> newData, int quantity){
        newData.put("quantity", quantity);
        newData.put("user", Build.MANUFACTURER +" " + Build.MODEL);
        db.collection("Inwentaryzacja_testy_logs")
                .document(DateTimeFormatter.ofPattern("dd-MM HH:mm:ss")
                        .withZone(ZoneOffset.systemDefault()).format(Instant.now()))
                .set(newData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Logs: DocumentSnapshot added"))
                .addOnFailureListener(e -> Log.w(TAG, "Logs: Error adding document", e));
    }
}