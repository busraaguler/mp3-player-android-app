package com.example.mp3player;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    Button btn_pause,btn_previous,btn_next;
    TextView songTextLabel;
    SeekBar songSeekbar;
    String sname;
    static MediaPlayer mymediaPlayer;
    int position;
    ArrayList<File> mySongs;           //Şarkıların listeleneceği arrayList oluşturuldu.
    Thread updateseekBar;                 //Seekbarda değişiklik yapabilmek için




    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        btn_next=(Button) findViewById(R.id.next);
        btn_pause=(Button) findViewById(R.id.pause);
        btn_previous=(Button) findViewById(R.id.previous);

        songTextLabel=(TextView) findViewById(R.id.SongLabel);
       songTextLabel.setSelected(true);
      songTextLabel.setSingleLine();
      songTextLabel.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        songSeekbar=(SeekBar) findViewById(R.id.seekBar);

        getSupportActionBar().setTitle("Çalıyooor:)");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);           //anasayfa'nın gösterilmesi
        getSupportActionBar().setDisplayShowHomeEnabled(true);          //anasayfaya geri dön


        updateseekBar =new Thread(){
            @Override
            public void run() {

                int totalDuration=mymediaPlayer.getDuration();          //getDuration() ile seekbar'ın üst sınırı belirlenir.
                int currentPosition=0;                                  //mevcut konum
                while(currentPosition<totalDuration){                    //mevcut konumun bitişten önce olup olmadığını kontrol eder.
                    try{
                        sleep(500);                                     //eğer önceyse 500 milisaniye bekler.
                        currentPosition=mymediaPlayer.getCurrentPosition();
                        songSeekbar.setProgress(currentPosition);             //Seekbar mevcut konumda ilerler.

                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        };
        if(mymediaPlayer!=null){                      //mediaplayer başlatılamazsa dur ,artık kullanılmıyor.release() metodu
        mymediaPlayer.stop();
        mymediaPlayer.release();
        }
        Intent i=getIntent();                        //aktivity geçişi sağlandı.
        Bundle bundle =i.getExtras();                //getExtras() ile bundle nesnesi döndürür.

        mySongs=(ArrayList) bundle.getParcelableArrayList("Şarkılar"); //Şarkılarım listesindeki objeleri diğer sayfaya transfer edilmei gerçekleştirildi.
        sname=mySongs.get(position).getName().toString();           //Sarkılarım listesinden sname Stringi

        String songName=i.getStringExtra("Song Name");
        songTextLabel.setText(songName);
        songTextLabel.setSelected(true);                               //Seçim işlemi denetlenir.

        position=bundle.getInt("pos",0);

        Uri u=Uri.parse(mySongs.get(position).toString());
        mymediaPlayer=MediaPlayer.create(getApplicationContext(),u);

        mymediaPlayer.start();
        songSeekbar.setMax(mymediaPlayer.getDuration());

        updateseekBar.start();
        songSeekbar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        songSeekbar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.MULTIPLY);

        songSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            mymediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        btn_pause.setOnClickListener(new View.OnClickListener() {               //pause butonu etkin hale getirilir.
            @Override
            public void onClick(View view) {
                songSeekbar.setMax(mymediaPlayer.getDuration());

                if(mymediaPlayer.isPlaying()){                              //mediaplayer'ın başlatılıp başlatılmadığını sınar.
                    btn_pause.setBackgroundResource(R.drawable.icon_play);
                    mymediaPlayer.pause();                                          //oynatılıyor.
                }
                else{
                    btn_pause.setBackgroundResource(R.drawable.icon_pause);
                    mymediaPlayer.start();                                       //çalmayı başlatmak için start çağrıldı.

                }
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {                  //next butonu etkin hale getirilir.
            @Override
            public void onClick(View view) {
                mymediaPlayer.stop();
                mymediaPlayer.release();
                position=((position+1)%mySongs.size());                        //Şuanki konumdan bir sonrakini çalıştır.

                Uri u=Uri.parse(mySongs.get(position).toString());
                mymediaPlayer=MediaPlayer.create(getApplicationContext(),u);

                sname=mySongs.get(position).getName().toString();
                songTextLabel.setText(sname);

                mymediaPlayer.start();

            }
        });

        btn_previous.setOnClickListener(new View.OnClickListener() {                   //previous butonu eetkin hale getirilir.
            @Override
            public void onClick(View view) {
                mymediaPlayer.stop();
                mymediaPlayer.release();
                position=((position-1)<0)?(mySongs.size()-1):(position-1);           //şuanki konumdan bir önceki şarkı çalar.

                Uri u=Uri.parse(mySongs.get(position).toString());
                mymediaPlayer=MediaPlayer.create(getApplicationContext(),u);

                sname=mySongs.get(position).getName().toString();
                songTextLabel.setText(sname);
                mymediaPlayer.start();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==android.R.id.home){
        onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}