package com.weetard.playerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private TextView textOut;
    private String nameAudio;
    private final String DATA_STREAM = "https://muzos.net/uploads/music/2023/02/Yeat_How_it_go.mp3";
    private Toast toast;

    private boolean isPaused;
    private MediaPlayer mediaPlayer;
    private Switch switchLoop;
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textOut = findViewById(R.id.textOut);
        switchLoop = findViewById(R.id.switchLoop);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        switchLoop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (mediaPlayer != null) {
                  mediaPlayer.setLooping(isChecked);
                  if (textOut.getText() != "Проигрыватель остановлен\n(Выберите источник)")
                  textOut.setText((isPaused ? "На паузе: " : "Играет: ") + nameAudio + (mediaPlayer.isLooping() ? "\nПовторяется" : "\nНе повторяется"));
                }
            }
        });
    }

    public void onClickSource(View view) throws IOException  {

        releaseMediaPlayer();

        try {
            switch (view.getId()) {
                case R.id.btnStream :
                    toast = Toast.makeText(this, "Запущен поток аудио", Toast.LENGTH_SHORT);
                    toast.show();

                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(DATA_STREAM);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setOnPreparedListener(this);
                    mediaPlayer.prepareAsync();
                    nameAudio = "Yeat - How It Go";
                    isPaused = false;
                    textOut.setText("Воспроизвести с интернета");
                    break;
                case R.id.btnRAW :
                    toast = Toast.makeText(this, "Запущен аудио-файл с памяти телефона", Toast.LENGTH_SHORT);
                    toast.show();
                    mediaPlayer = MediaPlayer.create(this, R.raw.carti);
                    nameAudio = "PlayBoi Carti - We So Proud Of Him";
                    isPaused = false;
                    textOut.setText("Воспроизвести с памяти телефона");
                    break;

            }
        } catch (IOException e) {
            e.printStackTrace();
            toast = Toast.makeText(this, "Источник информации не найден", Toast.LENGTH_SHORT); // инициализация
            toast.show(); // демонстрация тоста на экране
        }

        if (mediaPlayer == null) return;

        mediaPlayer.setLooping(switchLoop.isChecked()); // включение / выключение повтора
        mediaPlayer.setOnCompletionListener(this); // слушатель окончания проигрывания
    }
    public void onClick(View view) {

        if (mediaPlayer == null) return;
        switch (view.getId()) {
            case R.id.btnResume:
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start(); // метод возобновления проигрывания
                    isPaused = false;
                }
                break;
            case R.id.btnPause:
                if (mediaPlayer.isPlaying())
                    mediaPlayer.pause(); // метод паузы
                isPaused = true;
                break;
            case R.id.btnStop:
                mediaPlayer.stop(); // метод остановки
                textOut.setText("Проигрыватель остановлен\n(Выберите источник)");
                break;
            case R.id.btnForward:
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 5000); // переход к определённой позиции трека
                // mediaPlayer.getCurrentPosition() - метод получения текущей позиции
                break;
            case R.id.btnBack:
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 5000); // переход к определённой позиции трека
                break;
        }
        if (textOut.getText() != "Проигрыватель остановлен\n(Выберите источник)")
        textOut.setText((isPaused ? "На паузе: " : "Играет: ") + nameAudio + (mediaPlayer.isLooping() ? "\nПовторяется" : "\nНе повторяется"));
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        //mediaPlayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }
    private void releaseMediaPlayer() {
       if (mediaPlayer != null) {
           mediaPlayer.release();
           mediaPlayer = null;
       }
    }
}