package pfhb.damian.inwentaryzacja;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;

import pfhb.damian.inwentaryzacja.printers.PrintersMenuActivity;

public class MainActivity extends AppCompatActivity {
    private AppUpdateManager mAppUpdateManager;
    private static final int RC_APP_UPDATE = 11;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        CheckUpdates();
    }

    private void CheckUpdates() {
        Toast.makeText(getApplicationContext(), "Szukanie aktualizacji...", Toast.LENGTH_LONG*2).show();

        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);

        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // Checks whether the platform allows the specified type of update,
        // and checks the update priority.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    //&& appUpdateInfo.updatePriority() >= 4 /* high priority */
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                Toast.makeText(getApplicationContext(), "Znaleziono aktualizację", Toast.LENGTH_LONG*2).show();

                // Request an immediate update.
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            // Pass the intent that is returned by 'getAppUpdateInfo()'.
                            appUpdateInfo,
                            // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                            AppUpdateType.IMMEDIATE,
                            // The current activity making the update request.
                            this,
                            // Include a request code to later monitor this update request.
                            11);
                } catch (IntentSender.SendIntentException e) {
                    Toast.makeText(getApplicationContext(), "Błąd w trakcie aktualizowania....", Toast.LENGTH_LONG*2).show();

                    e.printStackTrace();
                }
            }else{
                Toast.makeText(getApplicationContext(), "Brak aktualizacji.", Toast.LENGTH_LONG*2).show();
            }
        });
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

    public void onClickShowPrintersMenu(View view) {
        Intent intent = new Intent (this, PrintersMenuActivity.class);
        startActivity(intent);
    }
}