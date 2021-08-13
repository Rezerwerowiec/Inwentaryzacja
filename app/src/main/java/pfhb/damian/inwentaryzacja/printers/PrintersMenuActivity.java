package pfhb.damian.inwentaryzacja.printers;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.List;

import pfhb.damian.inwentaryzacja.R;
import pfhb.damian.inwentaryzacja.databinding.ActivityPrintersMenuBinding;

public class PrintersMenuActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printers_menu);

        db = FirebaseFirestore.getInstance();
        setUpSpinner();
    }


    private void setUpSpinner(){
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        List<String> spinnerArray =  new ArrayList<String>();
        db.collection("Inwentaryzacja_drukarki")
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

    public void EditCompatible(View view) {
        Spinner tv = findViewById(R.id.spinner);
        String printer =  String.valueOf(tv.getSelectedItem());
        Intent intent = new Intent(this, EditPrinterActivity.class);
        intent.putExtra("printer", printer);
        startActivity(intent);

    }

    public void AddNewPrinter(View view) {
        Intent intent = new Intent(this, NewPrinterActivity.class);
        startActivity(intent);
    }
}