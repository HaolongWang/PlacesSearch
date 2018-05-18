package com.example.haolongwang.placessearch;

import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class loadjson extends AsyncTask<String, Void, String> {
    private static final int OUT_TIME = 5000;
    private static String response;


    public  loadjson(){

    }

    public AsyncResponse asyncResponse;
    public  void setOnAsyncResponse(AsyncResponse asyncResponse){
        this.asyncResponse = asyncResponse;
    }

    @Override
    protected void onPreExecute(){
        //animation
    }

    @Override
    protected String doInBackground(String... params){
        HttpURLConnection net = null;
        InputStream input = null;
        try{
            URL url = new URL(params[0]);
            net = (HttpURLConnection) url.openConnection();
            net.setConnectTimeout(OUT_TIME);
            net.setReadTimeout(OUT_TIME);
            net.setRequestMethod("GET");
            net.setDoInput(true);
            net.connect();
            if(net.getResponseCode() == 200){
                input = net.getInputStream();
                response = changeInputStream(input);
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            if (input != null){
                try{
                    input.close();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
            net.disconnect();
        }
        return response;
    }

    @Override
    protected void onPostExecute(String response){
       asyncResponse.onDataReceived(response);
    }

    private static String changeInputStream(InputStream inputStream){
        String JString = "";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];
        int lon = 0;
        try{
            while((lon = inputStream.read(bytes)) != -1){
                outputStream.write(bytes, 0 , lon);
            }
            JString = new String(outputStream.toByteArray());
        } catch (IOException e){
            e.printStackTrace();
        }
        return JString;
    }

}

