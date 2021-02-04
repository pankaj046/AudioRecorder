package sharma.pankaj.audiorecoderdialog;

import android.Manifest;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class RecordAudio {

    Context mContext;
    String FileName = "", FileLoation = "";
    private static String root = null;
    private static Long millis;

    public MediaRecorder recorder = null;
    public MediaPlayer mediaPlayer = null;
    ArrayList<String> arrayList;

    public RecordAudio(Context context) {
        this.mContext = context;
    }

    public String CreateFolder(String location) {
        FileName = location;
        root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File folder = new File(root, FileName);
        if (!folder.exists()) {
            folder.mkdir();
        }
        File audioFolder = new File(folder.getAbsolutePath(), "Audio");
        if (!audioFolder.exists()) {
            audioFolder.mkdir();
        }
        root = audioFolder.getAbsolutePath();
        return root;
    }

    public boolean AddPermission() {
        boolean permission = false;
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
            permission = false;
        } else {
            permission = true;
        }
        return permission;
    }

    public void StartRecording() {
        millis = Calendar.getInstance().getTimeInMillis();
        FileLoation = root + "/" + millis + "audio.wav";
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC_ELD);
        recorder.setAudioSamplingRate(44100);
        recorder.setAudioEncodingBitRate(96000);
        recorder.setOutputFile(FileLoation);

        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void StopRecording() {
        if (recorder != null) {
            recorder.release();
        }
    }

    public void PlayFile(String fileLocPlay) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(fileLocPlay);
            mediaPlayer.setOnCompletionListener(completionListener);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("xcjhxjcx", "PlayFile: " + e);
        }

    }

    private final MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            if (mediaPlayer != null) {
                mediaPlayer.reset();
            }
        }
    };

    public void PlayingStop() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
        }
    }

}
