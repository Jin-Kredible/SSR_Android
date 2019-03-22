package com.google.android.gms.fit.samples.backgroundgps;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.shin.ssr.layout.tab.FitTab;
import com.shin.ssr.vo.MallsVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

class HttpUtil_Beacon extends AsyncTask<String, String, String> {

    private Context context;

    public HttpUtil_Beacon(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        // 호출 전
        System.out.println("************************************************* 서버 호출 선행 (Beacon)" );
    }


    @Override
    public String doInBackground(String...params) {
        System.out.println("************************************************* 서버 호출 (Beacon)" );
        String paramString = "";
        String url = params[0];
        String param1Key = params[1].split(":")[0];
        String param1Value = params[1].split(":")[1];
        String param2Key = params[2].split(":")[0];
        String param2Value = params[2].split(":")[1]; //보내는거
        System.out.println("************************************************* 서버 호출 url : (Beacon)" + url);

        //paramString = param1Key  + "=" + param1Value + "&" + param2Key  + "=" + param2Value ;
        paramString = param1Key  + "=" + param1Value + "&" + param2Key + "=" + param2Value + "&" ;
        try {
            URL obj = new URL(url + "?" + paramString);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            byte[] outputInBytes = params[0].getBytes("UTF-8");
            OutputStream os = conn.getOutputStream();
            os.write( outputInBytes );
            os.close();

            int retCode = conn.getResponseCode();

            System.out.println("************************************************* 서버 호출 결과 코드 : (Beacon)" + retCode );
            if (retCode == HttpURLConnection.HTTP_OK) {
                InputStream is = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                char[] buff = new char[512];
                int len = -1;
                String line;
                StringBuffer response = new StringBuffer();
                while((len = br.read(buff)) != -1) {
                    response.append(new String(buff, 0, len));
                }
                br.close();
                System.out.println("************************************************* 서버 호출 결과 text : (Beacon)" + response.toString());

                return response.toString();
            }else{
                System.out.println("************************************************* 서버 호출 실패 code : (Beacon)" + retCode );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 호출이 끝난
    @Override
    protected void onPostExecute(String result) {
        JSONArray object = null;
        Log.d("serverB","result from spring" + result);
        ArrayList<MallsVO> mallAry = new ArrayList<MallsVO>();
        try {
            object =  new JSONArray(result);

            for(int i =0; i < object.length(); i++) {
                JSONObject obj = (JSONObject)object.get(i);
                Log.d("serverB",obj.getString("user_id"));
                Log.d("serverB",obj.getString("mall_id"));
                Log.d("serverB",obj.getString("uuid"));
               mallAry.add(new MallsVO(obj.optInt("user_id"), obj.optInt("mall_id"),obj.optString("uuid"),obj.optInt("major"),obj.optInt("minor")));
            }

            for(int j=0; j<3;j++){
                Log.d("serverB", j + "번째 : " + mallAry.get(j).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.d("result", mallAry.toString());

        /*JSONArray array = null;
        for(int i =0; i < array.length(); i++) {
            try {
                object = array.getJSONObject(i).getJSONObject(result);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/



      /*  try {
            object = new JSONObject(result);

        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        System.out.println("************************************************* 서버 호출 후행");
        String rtn = "msg" ;
        //try {
        // return 받은 Json 데이터
        //rtn = URLDecoder.decode(object.getString("DATA"), "UTF-8");
        //} catch (JSONException e) {
        //    e.printStackTrace();
        //} catch (UnsupportedEncodingException e) {
        //    e.printStackTrace();
        // }

        ((RealService)context).get_BaeconList(mallAry);


    }
}