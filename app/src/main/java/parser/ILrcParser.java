package parser;

import java.util.List;

import entity.Lrc;

/**
 * 歌词解析接口
 *
 * Created by SWL on 2017/11/21.
 */

public interface ILrcParser {

    List<Lrc> getLrcRows(String rawLrc);

}
