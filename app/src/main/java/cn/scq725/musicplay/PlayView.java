package cn.scq725.musicplay;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


import constant.MusicSerConstant;
import entity.Music;
import parser.DefaultParser;
import utils.LrcFileTool;
import utils.Mp3FileTool;
import utils.TimeTool;

import static cn.scq725.musicplay.MusicService.MUSIC_CURRENT;
import static cn.scq725.musicplay.MusicService.MUSIC_DURATION;
import static cn.scq725.musicplay.MusicService.UPDATE_ACTION;

/**
 * Android音乐播放器  播放界面Activity
 * Created by SWL on 2017/11/22.
 */

public class PlayView extends AppCompatActivity implements View.OnClickListener {


    DefaultParser defaultParser = new DefaultParser();
    private SeekBar sb;
    private ImageButton ibplay;
    private ImageButton ibPrevious;
    private ImageButton ibnext;
    private TextView tvMp3Title;
    private TextView tvCurrentTime;
    private TextView tvTotalTime;
    private LrcView tvLrc;
    private boolean flag;
    private boolean isPause = false;
    private boolean isPlaying;
    private int mCurrentPosition;
    private ArrayList<Music> mp3Infos;
    private Music mSongBean;
    private PlayerReceiver playerReceiver;
    private int currentTime;
    private String url;
    private Timer mTimer;
    private TimerTask mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_view);
        Intent intent = getIntent();
        int position = intent.getIntExtra("position", -1);
        if (position != mCurrentPosition) {
            mCurrentPosition = position;
            flag = true;
            isPause = false;
            isPlaying = true;
        } else {
            flag = false;
            isPlaying = true;
            isPause = false;
        }
        mp3Infos = Mp3FileTool.hashmapToMusicEntity(Mp3FileTool.MP3PlayList(PlayView.this), PlayView.this);
        mSongBean = mp3Infos.get(mCurrentPosition);
        ibplay = (ImageButton) findViewById(R.id.ibplay);
        sb = (SeekBar) this.findViewById(R.id.sb);
        tvMp3Title = (TextView) this.findViewById(R.id.tvMusicTitle);
        tvCurrentTime = (TextView) findViewById(R.id.tvCurrentTime);
        tvTotalTime = (TextView) findViewById(R.id.tvTotalTime);
        tvLrc = (LrcView) findViewById(R.id.LrcView);
        tvLrc.setLrc(defaultParser.getLrcRows(LrcFileTool.getLrcFile(mSongBean.getTitle(), PlayView.this)));
        ibplay.setOnClickListener(this);
        ibnext = (ImageButton) findViewById(R.id.ibnext);
        ibnext.setOnClickListener(this);
        ibPrevious = (ImageButton) findViewById(R.id.iblast);
        ibPrevious.setOnClickListener(this);
        tvMp3Title.setText(mSongBean.getTitle());
        sb.setMax((int) mSongBean.getDuration());
        tvTotalTime.setText(TimeTool.formatTime(mSongBean.getDuration()));
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                if (fromUser) {
                    ibplay.setImageResource(android.R.drawable.ic_media_play);
                    isPlaying = true;
                    isPause = false;
                    audioTrackChange(progress);
                }
            }
        });
        playerReceiver = new PlayerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(UPDATE_ACTION);
        filter.addAction(MUSIC_CURRENT);
        filter.addAction(MUSIC_DURATION);
        this.registerReceiver(playerReceiver, filter);
        if (flag) {
            audioTrackChange(0);
            isPlaying = true;
            isPause = false;
            //ibplay.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            Intent intent2 = new Intent(this, MusicService.class);
            intent2.putExtra("MSG", MusicSerConstant.PLAYING);
            intent2.putExtra("position", mCurrentPosition);
            intent2.putExtra("url", mSongBean.getUrl());
            intent2.setAction("com.swl.media.MUSIC_SERVICE");
            startService(intent2);
            isPlaying = true;
            isPause = false;
        }
        if (isPause) {
            ibplay.setImageResource(android.R.drawable.ic_media_play);
        } else {
            ibplay.setImageResource(android.R.drawable.ic_media_pause);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibplay:
                if (isPlaying) {
                    pause();
                } else if (isPause) {
                    resume();
                } else {
                    play();

                }
                break;

            case R.id.iblast:
                mCurrentPosition--;
                if (mCurrentPosition >= 0) {
                    mSongBean = mp3Infos.get(mCurrentPosition);
                    tvMp3Title.setText(mSongBean.getTitle());
                    sb.setProgress(0);
                    previous();
                    if (isPlaying) {
                        ibplay.setImageResource(android.R.drawable.ic_media_pause);
                    } else {
                        ibplay.setImageResource(android.R.drawable.ic_media_play);
                    }
                } else {
                    mCurrentPosition = 0;
                    Toast.makeText(PlayView.this, "没有上一首了", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.ibnext:
                mCurrentPosition++;
                if (mCurrentPosition < mp3Infos.size()) {
                    mSongBean = mp3Infos.get(mCurrentPosition);
                    tvMp3Title.setText(mSongBean.getTitle());
                    sb.setProgress(0);
                    //singerTextView.setText(mSongBean.getArtist());
                    //mResource = MediaUtil.getArtwork(this, mSongBean.getId(), mSongBean.getAlbumId(), true);
                    next();
                    if (isPlaying) {
                        ibplay.setImageResource(android.R.drawable.ic_media_pause);
                    } else {
                        ibplay.setImageResource(android.R.drawable.ic_media_play);
                    }
                } else {
                    mCurrentPosition = mp3Infos.size() - 1;
                    Toast.makeText(PlayView.this, "没有下一首了", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                break;
        }

    }

    public void play() {
        Intent intent = new Intent();
        intent.setAction("com.swl.media.MUSIC_SERVICE");
        intent.setClass(this, PlayView.class);
        intent.putExtra("url", mSongBean.getUrl());
        intent.putExtra("position", mCurrentPosition);
        intent.putExtra("MSG", MusicSerConstant.PLAY);
        startService(intent);
        ibplay.setImageResource(android.R.drawable.ic_media_pause);
        isPlaying = true;
        isPause = false;
    }

    public void pause() {
        Intent intent = new Intent();
        intent.setClass(PlayView.this, MusicService.class);
        intent.setAction("com.swl.media.MUSIC_SERVICE");
        intent.putExtra("MSG", MusicSerConstant.PAUSE);
        startService(intent);
        isPlaying = false;
        isPause = true;
        ibplay.setImageResource(android.R.drawable.ic_media_play);
    }

    private void resume() {
        Intent intent = new Intent();
        intent.setAction("com.swl.media.MUSIC_SERVICE");
        intent.setClass(PlayView.this, MusicService.class);
        intent.putExtra("MSG", MusicSerConstant.CONTINUE);
        startService(intent);
        isPause = false;
        isPlaying = true;
        ibplay.setImageResource(android.R.drawable.ic_media_pause);
    }

    public void previous() {
        this.pause();
        audioTrackChange(0);
    }

    public void next() {

        this.pause();
        audioTrackChange(0);

    }


    public void audioTrackChange(int progress) {
        Intent intent = new Intent();
        intent.setClass(this, MusicService.class);
        intent.setAction("com.swl.media.MUSIC_SERVICE");
        intent.putExtra("url", mSongBean.getUrl());
        intent.putExtra("MSG", MusicSerConstant.PROGRESS_CHANGE);
        intent.putExtra("position", mCurrentPosition);
        intent.putExtra("progress", progress);
        mTimer = new Timer();
        mTask = new LrcTask();
        mTimer.scheduleAtFixedRate(mTask, 0, 100);
        startService(intent);
    }

    public class PlayerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MUSIC_CURRENT)) {
                currentTime = intent.getIntExtra("currentTime", -1);
                tvCurrentTime.setText(TimeTool.formatTime(currentTime));
                sb.setProgress(currentTime);
                //tvLrc.seekLrcToTime(currentTime);
            } else if (action.equals(MUSIC_DURATION)) {
                int duration = intent.getIntExtra("duration", -1);
                sb.setMax(duration);
                tvTotalTime.setText(TimeTool.formatTime(duration));
            } else if (action.equals(UPDATE_ACTION)) {
                mCurrentPosition = intent.getIntExtra("current", -1);
                mSongBean = mp3Infos.get(mCurrentPosition);
                url = mSongBean.getUrl();
                if (mCurrentPosition >= 0) {
                    tvMp3Title.setText(mSongBean.getTitle());
                }
                if (mCurrentPosition == 0) {
                    tvTotalTime.setText(TimeTool.formatTime(mp3Infos.get(
                            mCurrentPosition).getDuration()));
                    ibplay.setImageResource(android.R.drawable.ic_media_pause);
                    isPause = true;
                }
            }
        }
    }


    public void backToMenu(View view) {
        Intent intent = new Intent();
        //指定intent要启动的类
        intent.setClass(this, MusicList.class);
        startActivity(intent);
        //关闭当前Activity
        this.finish();
    }

    class LrcTask extends TimerTask {
        @Override
        public void run() {
            //获取歌曲播放的位置
            PlayView.this.runOnUiThread(new Runnable() {
                public void run() {
                    //滚动歌词

                    tvLrc.setLrc(defaultParser.getLrcRows(LrcFileTool.getLrcFile(mSongBean.getTitle(), PlayView.this)));

                    tvLrc.seekLrcToTime(currentTime);
                }
            });

        }
    }

    ;

}
