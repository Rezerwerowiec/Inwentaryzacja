package pfhb.damian.inwentaryzacja;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

public class AddItemActivity extends AppCompatActivity {
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        db = FirebaseFirestore.getInstance();


    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }
    public void onClickAddItem(View view){
        EditText et = findViewById(R.id.edittext);
        Map<String, Object> _data = new HashMap<String, Object>();
        _data.put("Date", Timestamp.now().toDate().toString());
        _data.put("User", Build.USER);
        String docTitle = "";
        try{
            docTitle = et.getText().toString();
        }
        catch(NullPointerException e){
            Toast.makeText(getBaseContext(), "Wpisz nazwę!", Toast.LENGTH_LONG).show();
        }
        if(docTitle.equals("") || docTitle==null || docTitle == "") {
            Toast.makeText(getBaseContext(), "Wpisz nazwę!", Toast.LENGTH_LONG).show();
        }

        db.collection("itemTypes")
                .document(docTitle)
                .set( _data)
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