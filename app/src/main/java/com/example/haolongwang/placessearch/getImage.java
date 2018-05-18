package com.example.haolongwang.placessearch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class getImage extends AsyncTask<String, Void, String> {
    private static final int OUT_TIME = 5000;
    private static Bitmap bitmap;
    private static String Sbitmap = "";


    public AsyncResImage asyncResImage;
    public  void setOnAsyncResponse(AsyncResImage asyncResImage){
        this.asyncResImage = asyncResImage;
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
            //if(params[0]!=null) {
                URL url = new URL(params[0]);
                net = (HttpURLConnection) url.openConnection();
                net.setConnectTimeout(OUT_TIME);
                net.setReadTimeout(OUT_TIME);
                net.setRequestMethod("GET");
                net.setDoInput(true);
                net.connect();
                if (net.getResponseCode() == 200) {
                    input = net.getInputStream();
                    bitmap = BitmapFactory.decodeStream(input);
                    Sbitmap = BitMapToString(bitmap);
                }
            //}
        }
        catch (IOException e){
            e.printStackTrace();
        }
        /*finally {
            if (input != null){
                try{
                    input.close();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
            net.disconnect();
        }*/
        return Sbitmap;
    }

    @Override
    protected void onPostExecute(String Sbitmap){
        bitmap = StringToBitMap(Sbitmap);
        asyncResImage.onImageReceived(bitmap);
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

}
