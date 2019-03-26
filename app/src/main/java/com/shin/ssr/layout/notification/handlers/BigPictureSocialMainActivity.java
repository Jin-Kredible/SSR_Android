/*
Copyright 2016 The Android Open Source Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.shin.ssr.layout.notification.handlers;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.fit.samples.stepcounter.R;
import com.shin.ssr.layout.notification.PushNotification;
import com.shin.ssr.vo.ProductVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.shin.ssr.layout.tab.FitTab.SERVER_URL;

/**
 * Template class meant to include functionality for your Social App. (This project's main focus
 * is on Notification Styles.)
 */
public class BigPictureSocialMainActivity extends Activity implements Runnable {


    ArrayList<Bitmap> bitmap = new ArrayList<>();
    ArrayList<ProductVO> productArry = new ArrayList<>();
    ImageView img1, img2, img3, img4;
    TextView name1, name2, name3, name4, weight1, weight2, weight3, weight4, price1, price2, price3, price4, customTxt;


    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_product);

        img1 = findViewById(R.id.recommendedProduct1);
        img2 = findViewById(R.id.recommendedProduct2);
        img3 = findViewById(R.id.recommendedProduct3);
        img4 = findViewById(R.id.recommendedProduct4);

        name1 = findViewById(R.id.recommendedProduct1_name);
        name2 = findViewById(R.id.recommendedProduct2_name);
        name3 = findViewById(R.id.recommendedProduct3_name);
        name4 = findViewById(R.id.recommendedProduct4_name);

        weight1 = findViewById(R.id.recommendedProduct1_weight);
        weight2 = findViewById(R.id.recommendedProduct2_weight);
        weight3 = findViewById(R.id.recommendedProduct3_weight);
        weight4 = findViewById(R.id.recommendedProduct4_weight);

        price1 = findViewById(R.id.recommendedProduct1_price);
        price2 = findViewById(R.id.recommendedProduct2_price);
        price3 = findViewById(R.id.recommendedProduct3_price);
        price4 = findViewById(R.id.recommendedProduct4_price);

        customTxt = findViewById(R.id.custom_recom);


        // Cancel Notification
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.cancel(PushNotification.NOTIFICATION_ID);
        Thread th = new Thread(BigPictureSocialMainActivity.this);

        th.start();


    }

    public void closePage(View v) {
        Intent intent = new Intent(BigPictureSocialMainActivity.this, com.shin.ssr.layout.tab.FitTab.class);
        startActivity(intent);

//        finish();
    }

    class ButtonActivity extends AppCompatActivity implements View.OnClickListener {
        @Override
        public void onClick(View recommend) {
            Button btn1 = (Button) findViewById(R.id.btnClose);
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }


    @Override
    public void run() {
        HttpUtil_Push hu = new HttpUtil_Push(BigPictureSocialMainActivity.this);
        String[] params = {SERVER_URL + "/product.do", "age:" + 30, "gender:" + 2};


        hu.execute(params);
        String result;
        URL url = null;

        try {
            result = hu.get();
            JSONArray object = null;
            Log.d("geo", "result from spring" + result);

            try {
                object = new JSONArray(result);

                for (int i = 0; i < 4; i++) {
                    JSONObject obj = (JSONObject) object.get(i);
                    android.util.Log.d("geo", obj.getString("item_price"));
                    android.util.Log.d("geo", obj.getString("item_name"));
                    android.util.Log.d("geo", obj.getString("item_img_path"));
                    productArry.add(new ProductVO(obj.optString("item_name"), obj.optString("item_price"), obj.optString("item_weight"), obj.optString("item_img_path")));
                }
                JSONObject obj2 = (JSONObject) object.get(4);
                productArry.add(new ProductVO(obj2.optInt("age"), obj2.optInt("gender"), obj2.optInt("time")));

                HttpURLConnection conn = null;
                InputStream is = null;

                for (int i = 0; i < 4; i++) {
                    url = new URL(SERVER_URL + "resources/img" + productArry.get(i).getItem_img_path());
                    conn = (HttpURLConnection) url.openConnection();
                    conn.connect();

                    Log.d("geo", url.toString());

                    is = conn.getInputStream();
                    // 스트림에서 받은 데이터를 비트맵 변환
                    // 인터넷에서 이미지 가져올 때는 Bitmap을 사용해야함
                    bitmap.add(BitmapFactory.decodeStream(is));

                    Log.d("geo", "inside thread" + bitmap.get(i).toString());


                }
                /*new Thread() {
                    public void run() {
                        Message message = handler.obtainMessage();
                        handler.sendMessage(message);
                    }
                }.start();*/

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ImageView[] productImg = {img1, img2, img3, img4};
                        TextView[] productName = {name1, name2, name3, name4};
                        TextView[] productWeight = {weight1, weight2, weight3, weight4};
                        TextView[] productPrice = {price1, price2, price3, price4};

                        for (int z = 0; z < bitmap.size(); z++) {
                            Log.d("geo", bitmap.get(z).toString());
                        }

                        // 서버에서 받아온 이미지를 핸들러를 경유해 이미지뷰에 비트맵 리소스 연결
                        for (int i = 0; i < bitmap.size(); i++) {
                            Log.d("geo", productImg[i].toString());
                            Log.d("geo", "inside for loop for image?");
                            Bitmap bitmap1 = bitmap.get(i);
                            productImg[i].setImageBitmap(bitmap1);
                        }

                        for (int i = 0; i < 4; i++) {
                            productName[i].setText(productArry.get(i).getItem_name());
                            productWeight[i].setText(productArry.get(i).getItem_weight());
                            productPrice[i].setText(productArry.get(i).getItem_price());
                        }

                        String gender = null;
                        String time = null;
                        if (productArry.get(4).getGender() == 1) {
                            gender = "남성";
                        } else if (productArry.get(4).getGender() == 2) {
                            gender = "여성";
                        }

                        if (productArry.get(4).getTime() == 1) {
                            time = "화이팅 아침";
                        } else if (productArry.get(4).getTime() == 2) {
                            time = "배고픈 점심";
                        } else if (productArry.get(4).getTime() == 3) {
                            time = "나른한 오후";
                        } else if (productArry.get(4).getTime() == 4) {
                            time = "불타는 저녁";
                        } else if (productArry.get(4).getTime() == 5) {
                            time = "야심한 밤";
                        }

                        //customTxt.setText(time + " " + productArry.get(4).getAge() + " 대 " + gender + "을 위한 추천");
                        String customstyle = time + " " + productArry.get(4).getAge() + "대 " + gender + "을 위한 추천";
                        /*"<font color='#333743'> <b> " + total + "</b> / 7000 </font>";*/
                        customTxt.setText(Html.fromHtml(customstyle), TextView.BufferType.SPANNABLE);
                        customTxt.setText(time + " " + productArry.get(4).getAge() + " 대 " + gender + "을 위한 추천");
                        customTxt.setTypeface(Typeface.createFromAsset(getAssets(), "font/bmhannapro.ttf"));

                    }
                });
                // 핸들러에게 화면 갱신을 요청한다.
                /*handler.sendEmptyMessage(0);*/
                // 연결 종료
                is.close();
                conn.disconnect();

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}