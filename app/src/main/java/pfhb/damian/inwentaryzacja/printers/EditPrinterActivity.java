package pfhb.damian.inwentaryzacja.printers;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import pfhb.damian.inwentaryzacja.R;

public class EditPrinterActivity extends AppCompatActivity {

    FirebaseFirestore db;
    String printer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_printer);
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        printer = intent.getStringExtra("printer");

        SetUpScreen();
    }

    public void SetUpScreen(){
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout ep_ll = findViewById(R.id.ep_ll);

        db.collection("itemTypes")
                .get()
                .addOnCompleteListener( task -> {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot document : task.getResult()){
                            CheckBox ch = new CheckBox(this);
                            String doc = document.getId();
                            ch.setText(doc);
                            ep_ll.addView(ch, lp);
                        }
                        setCheckboxes();
                    }
                });


    }

    private void setCheckboxes() {

        ArrayList<View> allCheckboxes = findViewById(R.id.ep_ll).getTouchables();
        db.collection("Inwentaryzacja_drukarki")
                .document(printer)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            for(View v : allCheckboxes){
                                CheckBox cb;
                                cb = (CheckBox) v;
                                Map<String, Object> data = document.getData();
                                List<String> ls = (List) data.get("array.kompatybilne");
                                for(String s :  ls){
                                    if(cb.getText().equals(s)){
                                        cb.setChecked(true);
                                    }
                                }
                            }
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                });
    }

    public void onClickApplyChanges(View view) {
        ArrayList<View> allCheckboxes;
        allCheckboxes = findViewById(R.id.ep_ll).getTouchables();
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
                .document(printer)
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