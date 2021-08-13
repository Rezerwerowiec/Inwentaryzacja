package pfhb.damian.inwentaryzacja;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Map;

import pfhb.damian.inwentaryzacja.putData.PutDataActivity;

public class ScanBarcodeActivity extends AppCompatActivity {

    FirebaseFirestore db;
    Map<String, Object> data;
    String barcodeSaved ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scan);
        db = FirebaseFirestore.getInstance();
    }

    public void scanBarCode(View view) {
    String[] st = new String[1];
    st[0] = String.valueOf(Manifest.permission.CAMERA.toString());

        ActivityCompat.requestPermissions(ScanBarcodeActivity.this,  st,100);
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


    public void onClickGoPutData(View view) {
        if(barcodeSaved.equals("") || barcodeSaved == null) {
            Toast.makeText(this, "Najpierw zeskanuj BARCODE!!!", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this, PutDataActivity.class);
        intent.putExtra("barcode", barcodeSaved);
        startActivity(intent);
    }
}
