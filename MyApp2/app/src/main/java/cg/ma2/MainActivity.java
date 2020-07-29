package cg.ma2;

import java.io.BufferedReader;  
import java.io.FileNotFoundException;  
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.io.InputStream;  
import java.io.InputStreamReader;  
import java.util.ArrayList;  
import java.util.List;  

import android.content.Context;  
import android.os.Bundle;  
//import android.support.v7.app.ActionBarActivity;  
import android.view.View;  
import android.widget.EditText;  
import android.widget.TextView;
import android.app.*;  

/** 看不懂注释我就吃半斤狗粮 :-) */  
public class MainActivity extends Activity {  

    private EditText et_cmd;  
    private String app_path;  
    private EditText tv_result;  

    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.main);  

        /*初始化控件*/  
        et_cmd = (EditText) findViewById(R.id.et_cmd);  
        tv_result = (EditText) findViewById(R.id.tv_result);  
        /* 获取app安装路径 */  
        app_path = getApplicationContext().getFilesDir().getAbsolutePath();  

    }  


    /** 按钮点击事件 */  
    public void onClick(View view) {  
        int id = view.getId();  
        switch (id) {  
			case R.id.copy_busybox: /* 拷贝busybox可执行文件 */  
				varifyFile(getApplicationContext(), "busybox");  
				varifyFile(getApplicationContext(), "su.zip");
				break;  
			case R.id.copy_traceroute:/* 拷贝traceroute可执行文件 */  
				varifyFile(getApplicationContext(), "traceroute");  
				break;  
			case R.id.exe_busybox:/* 将busybox命令添加到Editext中 */  
				String cmd = "." + app_path + "/busybox";  
				System.out.println(et_cmd);  
				et_cmd.setText(cmd);  
				break;  
			case R.id.exe_traceroute:/* 将traceroute命令添加到Editext中 */  
				cmd = "." + app_path + "/traceroute 8.8.8.8";  
				et_cmd.setText(cmd);  
				break;  
			case R.id.exe: /* 执行Editext中的命令 */  
				cmd = et_cmd.getText().toString();  
				/* 执行脚本命令 */  
				List<String> results = exe(cmd);  
				String result = "";  
				/* 将结果转换成字符串, 输出到 TextView中 */  
				for(String line : results){  
					result += line + "\n";  
				}  
				tv_result.setText(result);  
				break;  

			default:  
				break;  
        }  
    }  

    /** 验证文件是否存在, 如果不存在就拷贝 */  
    private void varifyFile(Context context, String fileName) {  


        try {  
            /* 查看文件是否存在, 如果不存在就会走异常中的代码 */  
            context.openFileInput(fileName);  
        } catch (FileNotFoundException notfoundE) {  
            try {  
                /* 拷贝文件到app安装目录的files目录下 */  
                copyFromAssets(context, fileName, fileName);  
                /* 修改文件权限脚本 */  
                String script = "chmod 700 " + app_path + "/" + fileName;  
                /* 执行脚本 */  
                exe(script);  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
    }  

    /** 将文件从assets目录中拷贝到app安装目录的files目录下 */  
    private void copyFromAssets(Context context, String source,  
								String destination) throws IOException {  
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
    private List<String> exe(String cmd) {  
        /* 获取执行工具 */  
        Process process = null;   
        /* 存放脚本执行结果 */  
        List<String> list = new ArrayList<String>();    
        try {    
            /* 获取运行时环境 */  
            Runtime runtime = Runtime.getRuntime();  
            /* 执行脚本 */  
            process = runtime.exec(cmd);   
            /* 获取脚本结果的输入流 */  
            InputStream is = process.getInputStream();  
            BufferedReader br = new BufferedReader(new InputStreamReader(is));  
            String line = null;    
            /* 逐行读取脚本执行结果 */  
            while ((line = br.readLine()) != null) {    
                list.add(line);   
            }  
            br.close();   
        } catch (IOException e) {    
            e.printStackTrace();    
        }   
        return list;  
    }  

}  
