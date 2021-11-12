package com.example.mp3player;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView SarkıList;                                   //Şarkı listesi listView içerisinde items' ler ile görüntülenicek.
    String[] items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SarkıList=(ListView) findViewById(R.id.SarkıListView);
        RuntimePermission();
    }
    public void RuntimePermission(){           //Dosyalara erişmek için izin isteme
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)   //uygulamaya harici depolama biriminden okuma izni istenir.
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {  //İzin verildiğinde çalışır.
                    disPlay();
                    }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {    //İzin reddedildiğinde çalışır.

                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

    }
    public ArrayList<File>findSong(File file){

        ArrayList<File> arrayList=new ArrayList<>();     //listFiles ile belirtilen dosyalar bulunmaya çalışılır.
        File[] files=file.listFiles();

        for(File singleFile:files){

            if(singleFile.isDirectory() && !singleFile.isHidden()){     //singleFile 'ın isDirectory ile belirtilen dosya yolu olup olmadığını kontrol eder.
            arrayList.addAll(findSong(singleFile));
            }
            else{
                if(singleFile.getName().endsWith(".mp3")  ||          //sonu mp3 ve wav ile biten dosyalar bulmaya çalışılıcak.
                singleFile.getName().endsWith(".wav")){
                    arrayList.add(singleFile);                        //eğer dosyalar bulunduysa arrayList'e ekler.
                }
            }
        }
        return arrayList;
    }
    void disPlay(){

        final ArrayList<File> mySongs=findSong(Environment.getExternalStorageDirectory()); //Sd karttaki dosyalara erişim sağlanır.
        items=new String[mySongs.size()];    //items ile Sarkılarım arrayList'indeki dosyalar görüntülenir.

        for(int i=0;i<mySongs.size();i++){      //Sarkılarım listesinin büyüklüğü kadar mp3 ve wav dosyalarını görüntüler.

            items[i]=mySongs.get(i).getName().toString().replace(".mp3","").replace("Wav","");
        }

        ArrayAdapter<String> myAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items); //Dosyaların kaydırılabilir  listesini dikey bir şekilde görebilmek için
        SarkıList.setAdapter(myAdapter);

        SarkıList.setOnItemClickListener(new AdapterView.OnItemClickListener() { //listedeki öğelere tıklanma özelliği getirilir.

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {   //
                String songName=SarkıList.getItemAtPosition(i).toString();    //belirtilen şarkıya dair varileri alır.

                startActivity(new Intent(getApplicationContext(),PlayerActivity.class)
                        .putExtra("Şarkılar",mySongs).putExtra("Song Name",songName)  //putExtra() ile şarkı ismi bilgileri  diğeer akt.v.ty 'e gönderilir.
                        .putExtra("pos",i));

            }
        });

    }
}