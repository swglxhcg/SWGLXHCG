package com.slc.qemu;

import android.app.*;
import android.os.*;
import android.content.*;
import java.io.*;
import java.util.*;
import java.lang.Process;
import android.widget.*;
import android.widget.CompoundButton.*;
import android.widget.SeekBar.*;
import android.view.*;
import android.util.*;

public class MainActivity extends Activity 
{

	private Button boot_btn;
	private String boot_cmd,hda_s,hdb_s,cdr_s,hdd_s,qemu_path;
	private EditText hda_et,hdb_et,cdr_et,hdd_et,vnc_et;
	private CheckBox hda_cb,hdb_cb,cdr_cb,hdd_cb;
	private TextView mem_tv,cmd_tv;
	private Spinner cpu_sp,shw_sp;
	private SeekBar mem_sb;

	private String app_path;

	private static String TAG;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		qemu_path="/data/data/com.slc.qemu/files/qemu";
		/* 获取app安装路径 */  
        app_path = getApplicationContext().getFilesDir().getAbsolutePath(); 
		
		
		Context c=getApplicationContext();
		try
		{
			c.openFileInput("busybox");
		}
		catch (FileNotFoundException es)
		{
			
			copyFolderFromAssets(c,"qemu",qemu_path);
			varifyFile(getApplicationContext(), "busybox");  
		}
		
		ts("文件安装完毕！");

		bindView();
		HDDcheck();
		
		mem_sb.setMax(1024);
		mem_sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

				@Override
				public void onStartTrackingTouch(SeekBar p1)
				{
					// TODO: Implement this method
				}

				@Override
				public void onStopTrackingTouch(SeekBar p1)
				{
					// TODO: Implement this method
				}

				@Override
				public void onProgressChanged(SeekBar sb, int p, boolean b)
				{
					mem_tv.setText("运存(" + String.valueOf(p) + "m)");
				}
			});


		//CHECKBOX CHECK

		hda_cb.setOnCheckedChangeListener(myOnCheckedChangeListener);
		hdb_cb.setOnCheckedChangeListener(myOnCheckedChangeListener);
		cdr_cb.setOnCheckedChangeListener(myOnCheckedChangeListener);
		hdd_cb.setOnCheckedChangeListener(myOnCheckedChangeListener);

		//CHECKBOX CHECK
		
		String[] arr={"none","",""};
        //创建ArrayAdapter对象
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,arr);
        cpu_sp.setAdapter(adapter);
		
		//BUTTON ONCLICK
		boot_btn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				String resu=HDDget(),cmd="";
				//test:ts(resu);
				cmd=qsi3bootcmd(qemu_path+"/bin/",qemu_path,cpu_sp.get,shw_sp.getSelectedItem().toString(),mem_sb.getProgress(),String.valueOf(vnc_et.getText().toString()),resu);
			}
		});
		
		//BUTTN ONCLICK
    }
	
	private void ts(String s){
		Toast.makeText(getApplicationContext(),s,2000).show();
	}
	
	private CheckBox.OnCheckedChangeListener myOnCheckedChangeListener = new CheckBox.OnCheckedChangeListener()
    {
        @Override
        public void onCheckedChanged(CompoundButton buttonview, boolean isChecked)
        {
            //相关事件
			HDDcheck();
        }
    };

	private void bindView()
	{
		cmd_tv=(TextView)findViewById(R.id.cmdt);
		hda_et = (EditText)findViewById(R.id.HDAet);
		hda_cb = (CheckBox)findViewById(R.id.HDAch);
		hdb_et = (EditText)findViewById(R.id.HDBet);
		hdb_cb = (CheckBox)findViewById(R.id.HDBch);
		cdr_et = (EditText)findViewById(R.id.CDRet);
		cdr_cb = (CheckBox)findViewById(R.id.CDRch);
		hdd_et = (EditText)findViewById(R.id.HDDet);
		hdd_cb = (CheckBox)findViewById(R.id.HDDch);
		cpu_sp = (Spinner)findViewById(R.id.CPUsl);
		shw_sp = (Spinner)findViewById(R.id.SOUNDsl);
		mem_tv = (TextView)findViewById(R.id.memsizet);
		mem_sb = (SeekBar)findViewById(R.id.MEMseek);
		boot_btn = (Button)findViewById(R.id.BOOTbtn);
	}

	private void HDDcheck()
	{
		if (hda_cb.isChecked())
		{
			hda_et.setEnabled(true);
		}
		else
		{
			hda_et.setEnabled(false);
		}
		if (hdb_cb.isChecked())
		{
			hdb_et.setEnabled(true);

		}
		else
		{
			hdb_et.setEnabled(false);
		}
		if (cdr_cb.isChecked())
		{
			cdr_et.setEnabled(true);

		}
		else
		{
			cdr_et.setEnabled(false);
		}
		if (hdd_cb.isChecked())
		{
			hdd_et.setEnabled(true);

		}
		else
		{
			hdd_et.setEnabled(false);
		}
	}

	private String HDDget()
	{
		String hdd_cmd="";
		if (hda_cb.isChecked())
		{
			hda_s = hda_et.getText().toString();
			hdd_cmd += " -hda "+hda_s;
		}
		else
		{
			hda_s = "";
		}
		if (hdb_cb.isChecked())
		{
			hdb_s = hdb_et.getText().toString();
			hdd_cmd += " -hdb "+hdb_s;
		}
		else
		{
			hdb_s = "";
		}
		if (cdr_cb.isChecked())
		{
			cdr_s = cdr_et.getText().toString();
			hdd_cmd += " -cdrom "+cdr_s;
			
		}
		else
		{
			cdr_s = "";
		}
		if (hdd_cb.isChecked())
		{
			hdd_s = hdd_et.getText().toString();
			hdd_cmd += " -hdd "+hdd_s;
		}
		else
		{
			hdd_s = "";
		}
		return hdd_cmd;
	}

	/** 验证文件是否存在, 如果不存在就拷贝 */  
    private void varifyFile(Context context, String fileName)
	{  


        try
		{  
            /* 查看文件是否存在, 如果不存在就会走异常中的代码 */  
            context.openFileInput(fileName);  
        }
		catch (FileNotFoundException notfoundE)
		{  
            try
			{  
                /* 拷贝文件到app安装目录的files目录下 */  
                copyFromAssets(context, fileName, fileName);  
                /* 修改文件权限脚本 */  
                String script = "chmod 700 " + app_path + "/" + fileName;  
                /* 执行脚本 */  
                exe(script);  
            }
			catch (Exception e)
			{  
                e.printStackTrace();  
            }  
        }  
    }  

    /** 将文件从assets目录中拷贝到app安装目录的files目录下 */  
    private void copyFromAssets(Context context, String source,  
								String destination) throws IOException
	{  
        /* 获取assets目录下文件的输入流 */  
        InputStream is = context.getAssets().open(source);  
        /* 获取文件大小 */  
        int size = is.available();  
        /* 创建文件的缓冲区 */  
        byte[] buffer = new byte[size];  
        /* 将文件读取到缓冲区中 */  
        is.read(buffer);  
        /* 关闭输入流 */  
        is.close();  
        /* 打开app安装目录文件的输出流 */  
        FileOutputStream output = context.openFileOutput(destination,  
														 Context.MODE_PRIVATE);  
        /* 将文件从缓冲区中写出到内存中 */  
        output.write(buffer);  
        /* 关闭输出流 */  
        output.close();  
    }  

    /** 执行 shell 脚本命令 */  
    private List<String> exe(String cmd)
	{  
        /* 获取执行工具 */  
        Process process = null;   
        /* 存放脚本执行结果 */  
        List<String> list = new ArrayList<String>();    
        try
		{    
            /* 获取运行时环境 */  
            Runtime runtime = Runtime.getRuntime();  
            /* 执行脚本 */  
            process = runtime.exec(cmd);   
            /* 获取脚本结果的输入流 */  
            InputStream is = process.getInputStream();  
            BufferedReader br = new BufferedReader(new InputStreamReader(is));  
            String line = null;    
            /* 逐行读取脚本执行结果 */  
            while ((line = br.readLine()) != null)
			{    
                list.add(line);   
            }  
            br.close();   
        }
		catch (IOException e)
		{    
            e.printStackTrace();    
        }   
        return list;  
    }  

	private String rexec(String cmd)
	{
		/* 执行脚本命令 */  
		List<String> results = exe(cmd);  
		String result = "";  
		/* 将结果转换成字符串, 输出到 TextView中 */  
		for (String line : results)
		{  
			result += line + "\n";  
		}  
		return result;
	}

	private String qsi3bootcmd(String qemuway, String biosway, String cpumodel, String soundhwmodel, int memsize, int vnc, String addcmd)
	{
		String result="",cmd="";
		cmd = "." + qemuway + "qemu-systen-i386 " + "-L " + biosway + " -cpu " + cpumodel + " -soundhw " + soundhwmodel + " -m " + memsize + " -vnc " + vnc + " " + addcmd;

		return cmd;

	}
	
	/** 将文件从assets目录中拷贝到SDCARD目录下 */  
    private void copyFromAssetsToSD(Context context, String source,  
								String destination) throws IOException
	{  
        /* 获取assets目录下文件的输入流 */  
        InputStream is = context.getAssets().open(source);  
        /* 获取文件大小 */  
        int size = is.available();  
        /* 创建文件的缓冲区 */  
        byte[] buffer = new byte[size];  
        /* 将文件读取到缓冲区中 */  
        is.read(buffer);  
        /* 关闭输入流 */  
        is.close();  
        /* 打开app安装目录文件的输出流 */  
        FileOutputStream output = new FileOutputStream("/sdcard/"+destination);
        /* 将文件从缓冲区中写出到内存中 */  
        output.write(buffer);  
        /* 关闭输出流 */  
        output.close();  
    }  
	
	/** 
     * 从assets目录下拷贝整个文件夹，不管是文件夹还是文件都能拷贝 
     *  
     * @param context 
     *            上下文 
     * @param rootDirFullPath 
     *            文件目录，要拷贝的目录如assets目录下有一个SBClock文件夹：SBClock 
     * @param targetDirFullPath 
     *            目标文件夹位置如：/sdcrad/SBClock 
     */  
    public static void copyFolderFromAssets(Context context, String rootDirFullPath, String targetDirFullPath) {  
        Log.d(TAG, "copyFolderFromAssets " + "rootDirFullPath-" + rootDirFullPath + " targetDirFullPath-" + targetDirFullPath);  
        try {  
            String[] listFiles = context.getAssets().list(rootDirFullPath);// 遍历该目录下的文件和文件夹  
            for (String string : listFiles) {// 看起子目录是文件还是文件夹，这里只好用.做区分了  
                Log.d(TAG, "name-" + rootDirFullPath + "/" + string);  
                if (isFileByName(string)) {// 文件  
                    copyFileFromAssets(context, rootDirFullPath + "/" + string, targetDirFullPath + "/" + string);  
                } else {// 文件夹  
                    String childRootDirFullPath = rootDirFullPath + "/" + string;  
                    String childTargetDirFullPath = targetDirFullPath + "/" + string;  
                    new File(childTargetDirFullPath).mkdirs();  
                    copyFolderFromAssets(context, childRootDirFullPath, childTargetDirFullPath);  
                }  
            }  
        } catch (IOException e) {  
            Log.d(TAG, "copyFolderFromAssets " + "IOException-" + e.getMessage());  
            Log.d(TAG, "copyFolderFromAssets " + "IOException-" + e.getLocalizedMessage());  
            e.printStackTrace();  
        }  
    }  
	private static boolean isFileByName(String string) {  
        if (string.contains(".")) {  
            return true;  
        }  
        return false;  
    }  

	//Java代码  收藏代码
	/** 
     * 从assets目录下拷贝文件 
     *  
     * @param context 
     *            上下文 
     * @param assetsFilePath 
     *            文件的路径名如：SBClock/0001cuteowl/cuteowl_dot.png 
     * @param targetFileFullPath 
     *            目标文件路径如：/sdcard/SBClock/0001cuteowl/cuteowl_dot.png 
     */  
    public static void copyFileFromAssets(Context context, String assetsFilePath, String targetFileFullPath) {  
        Log.d(TAG, "copyFileFromAssets ");  
        InputStream assestsFileImputStream;  
        try {  
            assestsFileImputStream = context.getAssets().open(assetsFilePath);  
            //FileHelper.copyFile(assestsFileImputStream, targetFileFullPath);  
			//copyFromAssetsToSD(context,assestsFileImputStream,targetFileFullPath);
			/* 获取文件大小 */  
			int size = assestsFileImputStream.available();  
			/* 创建文件的缓冲区 */  
			byte[] buffer = new byte[size];  
			/* 将文件读取到缓冲区中 */  
			assestsFileImputStream.read(buffer);  
			/* 关闭输入流 */  
			assestsFileImputStream.close();  
			/* 打开app安装目录文件的输出流 */  
			FileOutputStream output = new FileOutputStream(targetFileFullPath);
			/* 将文件从缓冲区中写出到内存中 */  
			output.write(buffer);  
			/* 关闭输出流 */  
			output.close();  
        } catch (IOException e) {  
            Log.d(TAG, "copyFileFromAssets " + "IOException-" + e.getMessage());  
            e.printStackTrace();  
        }  
	}
}
