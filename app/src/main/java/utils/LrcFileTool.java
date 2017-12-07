package utils;

import android.content.Context;
import java.io.BufferedReader;
import java.io.FileReader;
/**
 * Created by SWL on 2017/11/21.
 */

public class LrcFileTool {

    public static String getLrcFile(String mp3Name, Context context) {
        mp3Name = mp3Name.replace(".mp3", ".lrc");
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(context.getExternalFilesDir(null).toString() + "/" + mp3Name));
            String line = "";
            String result = "";
            while ((line = fileReader.readLine()) != null) {
                if (line.trim().equals(""))
                    continue;
                result += line + "\r\n";
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
