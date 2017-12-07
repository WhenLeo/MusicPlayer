package cn.scq725.musicplay;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import utils.Mp3FileTool;
/**
 * Android音乐播放器  音乐列表Activity
 * Created by SWL on 2017/11/22.
 */
public class MusicList extends Activity implements ListView.OnItemClickListener {

    private ListView musicList;
    private ArrayList<HashMap<String, String>> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.musiclist);
        musicList = findViewById(R.id.musiclist);
        new GetSystemMusicDataTask().execute();
        musicList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent playIntent = new Intent(MusicList.this, PlayView.class);
        playIntent.putExtra("position", position);
        ;
        startActivity(playIntent);
        MusicList.this.finish();
    }

    public class GetSystemMusicDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            arrayList = Mp3FileTool.MP3PlayList(MusicList.this);
            return null;
        }

        @Override
        protected void onPreExecute() {
            arrayList = new ArrayList<>();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            SimpleAdapter listAdapter = new SimpleAdapter(MusicList.this, arrayList,
                    R.layout.mp3list,
                    new String[]{"mp3_name"},
                    new int[]{R.id.mp3name});
            musicList.setAdapter(listAdapter);
        }
    }
}


