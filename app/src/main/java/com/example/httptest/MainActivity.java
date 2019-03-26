package com.example.httptest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    TextView responseText;
    Button request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getString("weather",null)!=null){
            Intent intent=new Intent(this,activity_weather.class);
            startActivity(intent);
            finish();
        }

      /*  request=findViewById(R.id.request);
        responseText=findViewById(R.id.response_text);
      *//*  request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*//*
        request.setOnClickListener(this);*/


    }

    /*public void onClick(View v){
        if (v.getId()==R.id.request){
            RequestWithHttpConnection();

        }
    }

    private void RequestWithOKHttp(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url("http://10.0.2.2/get_data.xml").build();
                    Response response = client.newCall(request).execute();
                    String responseData=response.body().string();


                    parseXMLWithPull(responseData);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void RequestWithHttpConnection(){
        //开网络请求
        new Thread(new Runnable(){
            @Override
            public void run() {
                HttpURLConnection connection=null;
                BufferedReader reader=null;
                try {
                    URL url =new URL("http://guolin.tech/api/china");
                    connection=(HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in=connection.getInputStream();
                    //获取输入流
                    reader=new BufferedReader(new InputStreamReader(in));
                    StringBuilder response =new StringBuilder();
                    // StringBuffer类的对象能够被多次的修改，并且不产生新的未使用对象。
                    //
                    //2）StringBuilder 类在 Java 5 中被提出， StringBuilder 的方法不是线程安全的（不能同步访问）。
                    //
                    //3）由于 StringBuilder有速度优势，所以多数情况下建议使用 StringBuilder 类。然而在应用程序要求线程安全的情况下，则不能选用。
                    String line;
                    while((line=reader.readLine())!=null){
                        response.append(line);
                    }
                    showResponse(response.toString());




                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if (reader!=null){
                        try{
                            reader.close();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                    if (connection!=null){
                        connection.disconnect();
                    }

                }

            }
        }).start();



    }



    private void parseXMLWithPull(String xmlData){
        try{
            XmlPullParserFactory factory=XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser=factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType=xmlPullParser.getEventType();
            String id="";
            String name="";
            String version="";
            while (eventType!=XmlPullParser.END_DOCUMENT){
                String nodeName=xmlPullParser.getName();
                switch (eventType){
                    case XmlPullParser.START_TAG:{
                        if ("id".equals(nodeName)){
                            id=xmlPullParser.nextText();
                        }else if ("name".equals(nodeName)){
                            name=xmlPullParser.nextText();
                        }else if ("version".equals(nodeName)){
                            version=xmlPullParser.nextText();
                        }
                        break;
                    }
                    case XmlPullParser.END_TAG:{
                        if ("app".equals(nodeName)){
                            Log.d("MainActivity","id is"+id);
                            Log.d("MainActivity","name is"+name);
                            Log.d("MainActivity","version is"+version);

                        }
                        break;
                    }
                    default:
                        break;
                }
                eventType=xmlPullParser.next();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }








*/


  /*  private void showResponse(final String response){
        runOnUiThread(new Runnable(){
            @Override
            public void run() {
                responseText.setText(response);
            }
         });
    }*/


}
