package pfhb.damian.inwentaryzacja.printers;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pfhb.damian.inwentaryzacja.R;

public class NewPrinterActivity extends AppCompatActivity {
    FirebaseFirestore db;
    public List<String> documentList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_printer);
        db = FirebaseFirestore.getInstance();

        dynamicCheckboxList();
    }

    private void dynamicCheckboxList(){
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout ll = findViewById(R.id.ll);

        db.collection("itemTypes")
                .get()
                .addOnCompleteListener( task -> {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot document : task.getResult()){
                            CheckBox ch = new CheckBox(this);
                            String doc = document.getId();
                            ch.setText(doc);
                            Toast.makeText(getBaseContext(), "Utworzono checkbox: " + doc, Toast.LENGTH_SHORT).show();
                            ll.addView(ch, lp);
                        }
                    }
                });
    }


    public void onClickAddPrinter(View view) {
        ArrayList<View> allCheckboxes;
        allCheckboxes = ((LinearLayout) findViewById(R.id.ll)).getTouchables();
        EditText et = findViewById(R.id.edittext);
        Map<String, List<String>> data = new HashMap<>();
        List<String> ls = new ArrayList<>();
        for(View v : allCheckboxes){
            CheckBox cb;
            cb = (CheckBox) v;
            if(cb.isChecked()){
                ls.add(cb.getText().toString());
            }

        }
        data.put("array.kompatybilne", ls);
        db.collection("Inwentaryzacja_drukarki")
                .document(et.getText().toString())
                .set(data)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot added");
                    Toast.makeText(getApplicationContext(), "Data successfully sent.", Toast.LENGTH_LONG).show();

                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                    Toast.makeText(getApplicationContext(), "Cannot send data...", Toast.LENGTH_LONG).show();

                });
    }
}