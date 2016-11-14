package fr.e_services.ppg_radium.tp_nfc;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    NfcAdapter nfcAdapter;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        textView = (TextView) findViewById(R.id.textView);

        // Vérifie si l'appareil a le NFC activé et s'il est activé
        if(nfcAdapter == null){
            Toast.makeText(this, "NFC n'est pas disponible :'(", Toast.LENGTH_LONG).show();
        }else if(nfcAdapter.isEnabled()){
            Toast.makeText(this, "NFC activé", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "NFC non activé", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // Permet de notifier la reconnaissance d'un TAG NFC
        Toast.makeText(this, "NFC intent reçu!", Toast.LENGTH_LONG).show();

        textView.setText(textView.getText() + "\n" + formatTimeToFrance());

        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilter = new IntentFilter[]{};

        if(nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilter, null);
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        if(nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }

        super.onPause();
    }

    // Cocorico
    protected String formatTimeToFrance() {
        long millis = System.currentTimeMillis();
        return String.format("%02d:%02d:%02d", (TimeUnit.MILLISECONDS.toHours(millis) % 24) + 1,
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }
}