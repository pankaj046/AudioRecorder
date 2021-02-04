package sharma.pankaj.audiorecoderdialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.slider.Slider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class AppDialogs {


    @SuppressLint("UseCompatLoadingForDrawables")
    public static void showPlayerDialog(Context context, String file) {
        Handler handler = new Handler();
        final MediaPlayer[] mediaPlayer = {new MediaPlayer()};
        final AtomicBoolean[] wasPlaying = {new AtomicBoolean(false)};
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.layout_audio_player);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        TextView cancel = (TextView) dialog.findViewById(R.id.cancel);
        TextView ok = (TextView) dialog.findViewById(R.id.submit);
        TextView timer = (TextView) dialog.findViewById(R.id.timer);
        Slider slider = dialog.findViewById(R.id.progress);
        ImageButton play = dialog.findViewById(R.id.play);

        slider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
                //int du = mediaPlayer[0].getDuration();
                int x = (int) Math.ceil(slider.getValue() / 1000f);

                if (x < 10)
                    timer.setText("0:0" + x);
                else
                    timer.setText("0:" + x);

                if (slider.getValue() > 0 && mediaPlayer[0] != null && !mediaPlayer[0].isPlaying()) {
                    mediaPlayer[0].stop();
                    mediaPlayer[0].release();
                    mediaPlayer[0] = null;
                    play.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_play_24));
                    slider.setValue(0);
                }
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                if (mediaPlayer[0] != null && mediaPlayer[0] .isPlaying()) {
                    mediaPlayer[0] .seekTo((int) slider.getValue());
                }
            }
        });

        play.setOnClickListener(v -> {
            try {
                if (mediaPlayer[0] != null && mediaPlayer[0].isPlaying()) {
                    mediaPlayer[0].stop();
                    mediaPlayer[0].release();
                    mediaPlayer[0] = null;
                    slider.setValueFrom(0);
                    wasPlaying[0].set(true);
                    play.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_play_24));
                }
                if (!wasPlaying[0].get()) {

                    if (mediaPlayer[0] == null) {
                        mediaPlayer[0] = new MediaPlayer();
                    }
                    play.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pause_24));
                    mediaPlayer[0].setDataSource(file);
                    mediaPlayer[0].setOnCompletionListener(mp -> {
                        if (mp!= null) {
                            mp.reset();
                            play.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_play_24));
                        }
                    });
                    mediaPlayer[0].prepare();
                    mediaPlayer[0].setVolume(0.5f, 0.5f);
                    mediaPlayer[0].setLooping(false);
                    int du = mediaPlayer[0].getDuration();
                    mediaPlayer[0].start();
                    slider.setValueTo(du);
                    @SuppressLint({"SetTextI18n", "DefaultLocale"})
                    Thread t = new Thread() {
                        public void run() {
                            int currentPosition = mediaPlayer[0].getCurrentPosition();
                            int total = mediaPlayer[0].getDuration();

                            while (mediaPlayer != null && mediaPlayer[0].isPlaying() && currentPosition < total) {
                                try {
                                    Thread.sleep(1000);
                                    currentPosition = mediaPlayer[0].getCurrentPosition();
                                } catch (Exception e) {
                                    return;
                                }

                                int Seconds, Minutes, MilliSeconds;
                                long MillisecondTime = 0, TimeBuff = 0, UpdateTime = 0L;
                                ///timer.setText(currentPosition);
                                slider.setValue(currentPosition);

                                UpdateTime = TimeBuff + MillisecondTime;
                                Seconds = (int) (UpdateTime / 1000);
                                Minutes = Seconds / 60;
                                Seconds = Seconds % 60;
                                MilliSeconds = (int) (UpdateTime % 1000);
//                                timer.setText("" + Minutes + ":"
//                                        + String.format("%02d", Seconds) + ":"
//                                        + String.format("%03d", MilliSeconds));

                                String t = "" + TimeUnit.MILLISECONDS.toMinutes(currentPosition) + ":"
                                        + String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(currentPosition) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentPosition)));

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        timer.setText(""+t);
                                    }
                                });

                            }
                        }
                    };
                    t.start();
                }
                wasPlaying[0].set(false);
            } catch (Exception e) {
                e.printStackTrace();

            }
        });

        cancel.setOnClickListener(v -> {
            mediaPlayer[0].stop();
            mediaPlayer[0].release();
            mediaPlayer[0] = null;
            dialog.dismiss();
        });

        ok.setOnClickListener(v -> {

        });
        dialog.show();
    }

    public static void showAudioRecorderDialog(Context context) {

        Handler handler = new Handler();
        String folder_name = "AudioFile";
        final long[] StartTime = {0};
        AtomicReference<String> fileLoc = new AtomicReference<>("");
        RecordAudio recordAudio = new RecordAudio(context);
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.layout_audio_recorder);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        TextView cancel = (TextView) dialog.findViewById(R.id.cancel);
        TextView ok = (TextView) dialog.findViewById(R.id.submit);
        TextView timer = (TextView) dialog.findViewById(R.id.timer);

        Runnable runnable = new Runnable() {

            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            public void run() {
                int Seconds, Minutes, MilliSeconds;
                long MillisecondTime, TimeBuff = 0, UpdateTime = 0L;

                MillisecondTime = SystemClock.uptimeMillis() - StartTime[0];
                UpdateTime = TimeBuff + MillisecondTime;
                Seconds = (int) (UpdateTime / 1000);
                Minutes = Seconds / 60;
                Seconds = Seconds % 60;
                MilliSeconds = (int) (UpdateTime % 1000);
                timer.setText("" + Minutes + ":"
                        + String.format("%02d", Seconds) + ":"
                        + String.format("%03d", MilliSeconds));

                handler.postDelayed(this, 0);
            }

        };


        cancel.setOnClickListener(v -> {
            recordAudio.StopRecording();
            dialog.dismiss();
        });

        ok.setOnClickListener(v -> {
            if (recordAudio.AddPermission()) {
                fileLoc.set(recordAudio.CreateFolder(folder_name));
                if (recordAudio.FileLoation.equalsIgnoreCase("")) {
                    StartTime[0] = SystemClock.uptimeMillis();
                    handler.postDelayed(runnable, 0);
                    ok.setText("Stop");
                    recordAudio.StartRecording();
                } else {
                    recordAudio.StopRecording();
                    handler.removeCallbacks(runnable);
                    dialog.dismiss();
                    showPlayerDialog(context, recordAudio.FileLoation);
                }
            } else {
                Toast.makeText(context, "Please Provide Permission", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }
}
