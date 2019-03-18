package com.google.android.gms.fit.samples.backgroundgps;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.shin.ssr.vo.LocationVO;
import com.shin.ssr.vo.MallVO;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationManage extends AppCompatActivity {



    private double longitude;
    private double latitude;
    private double altitude;
    private  float accuracy;
    private  String provider;
    private double distance;
    MallVO mTemp = new MallVO("이마트 청계천점",37.571079,127.029903);
    private LocationVO locationVO = new LocationVO();


    @SuppressLint("MissingPermission")
    public void onLocation(LocationManager lm){


        // tb = (ToggleButton) findViewById(R.id.toggle1);

        // LocationManager 객체를 얻어온다



        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                100, // 통지사이의 최소 시간간격 (miliSecond)
                1, // 통지사이의 최소 변경거리 (m)
                mLocationListener);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                100, // 통지사이의 최소 시간간격 (miliSecond)
                1, // 통지사이의 최소 변경거리 (m)
                mLocationListener);

        InitMallLocation();

  //      Log.d("onLocation", "경도 : " + Double.toString(longitude));
  //      Log.d("onLocation", "위도 : " + Double.toString(latitude));
    }

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.

            Log.d("test", "onLocationChanged, location:" + location);
            longitude = location.getLongitude(); //경도
            latitude = location.getLatitude();   //위도
            altitude = location.getAltitude();   //고도
            accuracy = location.getAccuracy();    //정확도
            provider = location.getProvider();   //위치제공자
 //           Log.d("locationTest", "경도 : " + longitude);
  //          Log.d("locationTest", "위도 : " + latitude);
            locationVO.setLongitude(longitude);
            locationVO.setLatitude(latitude);
            locationVO.setAltitude(altitude);
            locationVO.setAccuracy(accuracy);
            locationVO.setProvider(provider);
    //        Log.d("locationTest2", "경도 : " + locationVO.getLongitude());
      //      Log.d("locationTest2", "위도 : " + locationVO.getLatitude());

            //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
            //Network 위치제공자에 의한 위치변화
            //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.

            Location locationPoint = new Location("Mall");

            locationPoint.setLatitude(mTemp.getMlatitude());
            locationPoint.setLongitude(mTemp.getLongitude());

            distance = locationPoint.distanceTo(location);

            if(distance < 100){

            }// 지점과 지금 거리가 100m 이내 일떄

            Log.d("mall",mTemp.getmMallNmae());
            Log.d("mall","현재 위도 "  + location.getLatitude());
            Log.d("mall","현재 경도 "  + location.getLongitude());
            Log.d("mall","성수점 위도 "  + mTemp.getMlatitude());
            Log.d("mall","성수점 경도 "  + mTemp.getLongitude());
            Log.d("mall", "위치 : " + distance);



        }
        public void onProviderDisabled(String provider) {
            // Disabled시
            Log.d("test", "onProviderDisabled, provider:" + provider);
        }

        public void onProviderEnabled(String provider) {
            // Enabled시
            Log.d("test", "onProviderEnabled, provider:" + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };

    public LocationVO getVoData(){
        return locationVO;
    }

//    public List check_Mall(){
//
//       return list;
//    }

    public void InitMallLocation(){
        List<MallVO> mLocation_data = new ArrayList<MallVO>();
      //  MallVO mTemp = new MallVO("이마트 청계천점",37.571079,127.029903);

        mLocation_data.add(mTemp);
        mTemp.setMallVO("이마트 성수점", 37.539673, 127.053375);

        mLocation_data.add(mTemp);
         /* mTemp.setMallVO("이마트 용산점", 37.529456, 126.965545);
        mLocation_data.add(mTemp);
        mTemp.setMallVO( "이마트 아이앤씨점", 37.559805, 126.983122);
        mLocation_data.add(mTemp);

        for(int i=0;i<=3;i++){
            Log.d("mall",i +"번쨰 : " +  mLocation_data.get(i).getmMallNmae());
        }

        Map<Integer,MallVO> mMall_data = new HashMap<>();
*/
      //  mLocation_data.add((MallVO) mLocation_data);

    }

}
