package pfhb.damian.inwentaryzacja;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void onClickGoToStart(View view){
        Intent intent = new Intent(this, PutProductActivity.class);
        startActivity(intent);
    }

    public void onClickGoAddItem(View view){
        Intent intent = new Intent(this, AddItemActivity.class);
        startActivity(intent);
    }

    public void onClickShowLogs(View view){
        Intent intent = new Intent(this, LogsDisplayActivity.class);
        startActivity(intent);
    }

}