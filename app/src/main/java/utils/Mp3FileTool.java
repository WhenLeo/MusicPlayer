package utils;

import android.content.Context;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

import entity.Music;

/**
 * Created by SWL on 2017/11/27.
 */

public class Mp3FileTool implements FilenameFilter {
    @Override
    public boolean accept(File file, String s) {
        return s.endsWith(".mp3");
    }

    public static ArrayList<HashMap<String, String>> MP3PlayList(Context context) {
        ArrayList<HashMap<String, String>> mp3NameList = new ArrayList<HashMap<String, String>>();
        File file = new File(context.getExternalFilesDir(null).toString());
        if (file.listFiles(new Mp3FileTool()).length > 0) {
            for (File mp3File : file.listFiles(new Mp3FileTool())) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("mp3_name", mp3File.getName());
                mp3NameList.add(hashMap);
            }
        }

        return mp3NameList;
    }

    public static ArrayList<Music> hashmapToMusicEntity(ArrayList<HashMap<String, String>> mp3NameList, Context context) {
        ArrayList<Music> musics = new ArrayList<>();

        for (HashMap<String, String> mp3Name : mp3NameList) {
            Music music = new Music();
            music.setTitle(mp3Name.get("mp3_name"));
            music.setUrl(context.getExternalFilesDir(null).toString() + "/" + mp3Name.get("mp3_name"));
            musics.add(music);
        }
        return musics;
    }

}
