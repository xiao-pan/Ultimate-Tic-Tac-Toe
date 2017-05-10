package org.example.tictactoe;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

public class SettingActivity extends Activity {
    MediaPlayer mMediaPlayer;
    SeekBar bSeekBar,mSeekBar;
    SharedPreferences shared = null;
    AudioManager mAudioManager;
    RadioGroup model,first;
    RadioButton hard,simple,firstP,firstC;
    String firstText,modelText;
    int maxVolume;
    int currentVolume;
    static int firstId,modelId,brightness,volume;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_SETTINGS = 1;//标识符

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        bSeekBar = (SeekBar) findViewById(R.id.brightSeekBar);
        mSeekBar = (SeekBar) findViewById(R.id.musicSeekBar);
        model = (RadioGroup) findViewById(R.id.modelRadioGroup);
        first = (RadioGroup) findViewById(R.id.firstRadioGroup);
        hard = (RadioButton) findViewById(R.id.hard);
        simple = (RadioButton) findViewById(R.id.simple);
        firstC = (RadioButton) findViewById(R.id.firstC);
        firstP = (RadioButton) findViewById(R.id.firstP);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mSeekBar.setProgress(currentVolume);

        shared=getSharedPreferences("base64",MODE_PRIVATE);
        brightness=shared.getInt("seekBarNum", 0);
        volume=shared.getInt("seekBarNum1", 0);
        firstId=shared.getInt("first_Id", 0);
        modelId=shared.getInt("model_Id", 0);

        //恢复上次退出前的设置状态
        first.check(firstId);
        model.check(modelId);
        bSeekBar.setProgress(brightness);
        mSeekBar.setProgress(volume);

        //亮度调节处理
        bSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                // >Android6.0时作权限检查
                if(Build.VERSION.SDK_INT >= 23) {
                    if(!Settings.System.canWrite(SettingActivity.this)){
                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, MY_PERMISSIONS_REQUEST_WRITE_SETTINGS);
                    } else {
                        changeAppBrightness((int)(progress * 25.5));
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //声音调节处理
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
         }

         @Override
         public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // 设置音量
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            mSeekBar.setProgress(currentVolume);
         }
      });

        //按钮处理事件
        Button intentButton = (Button)findViewById(R.id.intentButton);
        intentButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             switch (first.getCheckedRadioButtonId()) {
                 case R.id.firstC:
                     firstText = "电脑";
                     break;
                 case R.id.firstP:
                     firstText = "玩家";
                     break;
             }

             switch (model.getCheckedRadioButtonId()) {
                 case R.id.simple:
                     modelText = "简单";
                     break;
                 case R.id.hard:
                     modelText = "困难";
                     break;
             }
             Log.d("data",firstText + "," + modelText);

             Toast.makeText(SettingActivity.this, "提示：你选择了" + firstText + "先手和" + modelText + "模式！",
                 Toast.LENGTH_SHORT).show();
             finish();
         }
      });
   }


   //改变屏幕亮度
    private void changeAppBrightness(int value) {
        // 开启手动调节亮度模式
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        // 设置系统亮度值
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, value);
    }

    @Override
    protected void onResume() {
      super.onResume();
      mMediaPlayer = MediaPlayer.create(this, R.raw.a_guy_1_epicbuilduploop);
      mMediaPlayer.setVolume(0.5f, 0.5f);
      mMediaPlayer.setLooping(true);
      mMediaPlayer.start();
   }

    @Override
    protected void onPause() {
      super.onPause();
      mMediaPlayer.stop();
      mMediaPlayer.reset();
      mMediaPlayer.release();
   }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        //通过SharedPreferences保存设置数据并进行读取
        super.onStop();
        Editor editor= shared.edit();
        editor.clear();
        editor.putInt("seekBarNum", bSeekBar.getProgress());
        editor.putInt("seekBarNum1", mSeekBar.getProgress());

        editor.putInt("first_Id",first.getCheckedRadioButtonId());
        editor.putInt("model_Id",model.getCheckedRadioButtonId());
        editor.commit();

        firstId = shared.getInt("first_Id", 0);
        modelId = shared.getInt("model_Id", 0);

        brightness = shared.getInt("seekBarNum",0);
        volume = shared.getInt("seekBarNum1",0);
   }
}
