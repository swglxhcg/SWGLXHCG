package com.slc.demo.player;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.view.View.*;
import android.view.*;
import android.media.*;
import android.net.*;
import android.database.*;
import java.io.*;
import android.provider.*;
import android.content.*;
import android.util.*;
import android.graphics.*;

public class MainActivity extends Activity implements View.OnClickListener
{
	private VideoView vv;
	private ImageButton play_btn,open_btn,stop_btn;
	private TextView sound_tv,time_tv,name_tv;;
	private SeekBar sound_sk,time_sk;
	//private MediaPlayer mp;
	private String v_path;
	private boolean isplay;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		isplay=false;
		//mp=new MediaPlayer();
		bindView();
		//vv.setBackgroundColor(Color.BLACK);
				
		
    }
	
	private void bindView(){
		vv=(VideoView)findViewById(R.id.mainVideoView1);
		//play_btn=(ImageButton)findViewById(R.id.btnplay);
		open_btn=(ImageButton)findViewById(R.id.btnopen);
		//stop_btn=(ImageButton)findViewById(R.id.btnstop);
		//sound_tv=(TextView)findViewById(R.id.soundtv);
		//time_tv=(TextView)findViewById(R.id.timetv);
		name_tv=(TextView)findViewById(R.id.videonametv);
		//sound_sk=(SeekBar)findViewById(R.id.soundseek);
		//time_sk=(SeekBar)findViewById(R.id.timeseek);
		//play_btn.setOnClickListener(this);
		//stop_btn.setOnClickListener(this);
		open_btn.setOnClickListener(this);
		
	}
	
	//选择文件【调用系统的文件管理】
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        //intent.setType(“image/*”);//选择图片
//        //intent.setType(“audio/*”); //选择音频
//        //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
//        //intent.setType(“video/*;image/*”);//同时选择视频和图片
//        intent.setType("*/*");//无类型限制
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        startActivityForResult(intent, REQUEST_CHOOSEFILE);


@Override
protected void onActivityResult(int requestCode,int resultCode,Intent data){//选择文件返回
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode==RESULT_OK){
       // switch(requestCode){
          //    case REQUEST_CHOOSEFILE:
              Uri uri=data.getData();
		String chooseFilePath=FileChooseUtil.getInstance(this).getChooseFileResultPath(uri);
		Log.d("tt", "选择文件返回：" + chooseFilePath);
		sendFile(chooseFilePath);
		name_tv.setText(chooseFilePath);
		//vv.setVideoPath(chooseFilePath);
		if(!isplay){
			vv.setVideoPath(chooseFilePath);

			isplay=false;
			vv.start();
		}else{
			vv.pause();
			vv.setVideoPath(chooseFilePath);
			isplay=false;
			vv.start();
		}
		vv.setMediaController(new MediaController(MainActivity.this));
		
              // break;
        }
    }

	
	private String sendFile(String s){
		return s;
	}

	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {//是否选择，没选择就不会继续
			Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
			String[] proj = {MediaStore.Images.Media.DATA};
			Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
			int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			actualimagecursor.moveToFirst();
			String img_path = actualimagecursor.getString(actual_image_column_index);
			File file = new File(img_path);
			Toast.makeText(MainActivity.this, file.toString(), Toast.LENGTH_SHORT).show();
			v_path=file.toString();
		}
	}*/
	
	@Override
	public void onClick(View v){
		switch(v.getId()){
			case R.id.btnopen:
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("video/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
				intent.addCategory(Intent.CATEGORY_OPENABLE);
				startActivityForResult(intent,1);
				
				
				break;
			/*case R.id.btnplay:
				if(isplay){
					//true
					vv.pause();

					isplay=false;
					break;
				}else{
					//false
					vv.start();

					isplay=true;

				}
				break;
			case R.id.btnstop:

				break;*/
		}
	}

	@Override
	protected void onDestroy()
	{
		// TODO: Implement this method
		super.onDestroy();
		
	}
	
}
