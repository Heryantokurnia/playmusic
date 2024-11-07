package com.example.playmusic;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/** @noinspection deprecation*/
public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private Button btnPlay;
    private SeekBar seekBar;
    private final Handler handler = new Handler();
    private Runnable updateSeekBar;

    // Daftar file audio yang akan diputar
    private final int[] songList = {R.raw.sample_audio, R.raw.sample_audio2, R.raw.samplae_audio1}; // Daftar file audio di folder res/raw
    private int currentSongIndex = 0; // Indeks lagu yang sedang diputar

    // Daftar nama lagu dan artis
    private final String[] songTitles = {"Tek it", "Smells like teen spirit", "Boom Boom Bakudan"}; // Nama lagu
    private final String[] artistNames = {"Kurt cobain AI", "Nirvana", "???"}; // Nama artis

    @SuppressLint({"SetTextI18n", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mengaktifkan EdgeToEdge
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Inisialisasi tampilan untuk EdgeToEdge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inisialisasi komponen UI
        btnPlay = findViewById(R.id.btnPlay);
        Button btnNext = findViewById(R.id.btnNext);
        Button btnPrevious = findViewById(R.id.btnPrevious);
        seekBar = findViewById(R.id.seekBar);
        TextView songTitle = findViewById(R.id.songTitle);
        TextView artistName = findViewById(R.id.artistName);

        // Memulai pemutaran lagu pertama
        playSong(currentSongIndex, songTitle, artistName);

        // Tombol Play/Pause
        btnPlay.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                btnPlay.setText("Play"); // Ubah ke tombol Play
            } else {
                mediaPlayer.start();
                btnPlay.setText("Pause"); // Ubah ke tombol Pause
                updateSeekBar();
            }
        });

        // Tombol Next
        btnNext.setOnClickListener(v -> {
            currentSongIndex = (currentSongIndex + 1) % songList.length; // Menambah indeks lagu, dan jika sudah sampai akhir, kembali ke awal
            playSong(currentSongIndex, songTitle, artistName); // Mainkan lagu selanjutnya
            btnPlay.setText("Pause");
            updateSeekBar();
        });

        // Tombol Previous
        btnPrevious.setOnClickListener(v -> {
            currentSongIndex = (currentSongIndex - 1 + songList.length) % songList.length; // Mengurangi indeks lagu dan jika sudah di awal, kembali ke akhir
            playSong(currentSongIndex, songTitle, artistName); // Mainkan lagu sebelumnya
            btnPlay.setText("Pause");
            updateSeekBar();
        });

        // SeekBar Listener
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress); // Update posisi musik saat pengguna menggeser seekBar
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(updateSeekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateSeekBar();
            }
        });
    }

    // Fungsi untuk memutar lagu berdasarkan indeks
    @SuppressLint("SetTextI18n")
    private void playSong(int index, TextView songTitle, TextView artistName) {
        // Jika mediaPlayer sudah ada, stop dan release
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        // Membuat MediaPlayer baru dengan file yang sesuai di array songList
        mediaPlayer = MediaPlayer.create(this, songList[index]);
        mediaPlayer.start();

        // Update SeekBar dengan durasi lagu yang baru
        seekBar.setMax(mediaPlayer.getDuration());

        // Update judul lagu dan nama artis berdasarkan indeks
        songTitle.setText(songTitles[index]); // Update judul lagu
        artistName.setText(artistNames[index]); // Update nama artis

        // Set tombol Play/Pause ke "Pause"
        btnPlay.setText("Pause");

        // Update SeekBar setiap detik
        updateSeekBar();
    }

    // Pembaruan otomatis seekBar
    private void updateSeekBar() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        if (mediaPlayer.isPlaying()) {
            updateSeekBar = this::updateSeekBar;
            handler.postDelayed(updateSeekBar, 1000); // Update setiap 1 detik
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacks(updateSeekBar);
    }
}
