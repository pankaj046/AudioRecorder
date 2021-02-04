package sharma.pankaj.audiorecoderdialog;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.recorder).setOnClickListener(v -> {
            AppDialogs.showAudioRecorderDialog(this);
        });
    }
}