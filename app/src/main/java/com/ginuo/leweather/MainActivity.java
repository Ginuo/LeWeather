
package com.ginuo.leweather;

import android.content.Intent;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.ginuo.leweather.util.Common;

public class MainActivity extends BaseActivity {
    //侧滑菜单（抽屉效果）
    private DrawerLayout mDrawerLayout;
    private FragmentManager mFragmentManager;
    //用来取代ActionBar的组件，符合MD规范
    private Toolbar mIndexToolBar;

    /*
    三个高德地图相关的成员
     */
    public AMapLocationClient mLocationClient=null;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private AMapLocationListener mLocationListener;

    //weatherId，在County中也有对应的字段
    private String weatherId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);     //根元素时DrawerLayout
        initNavigation();
        initToolBar();
        initAMapLocationListener();
        initLocation();

        Log.d("LifeCycle","MainActivity_OnCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("LifeCycle","MainActivity_OnStart");

    }
    @Override
    protected void onResume() {
        super.onResume();

        Log.d("LifeCycle","MainActivity_OnResume");
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("LifeCycle","MainActivity_OnRestart");
    }



    @Override
    protected void onPause() {
        super.onPause();
        Log.d("LifeCycle","MainActivity_OnPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("LifeCycle","MainActivity_OnStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("LifeCycle","MainActivity_OnDestroy");
    }

    /**
     * singleTask启动模式的Activity 需通过onNewIntent()传递数据
     * 因为SingleTask只有一个Activity实例，但是有时又可能被多个startActivity()启动，且传入不同的Intent
     *
     * 或者这样说：
     * launchMode为singleTask的时候，通过Intent启到一个Activity,如果系统已经存在一个实例，
     * 系统就会将请求发送到这个实例上，但这个时候，系统就不会再调用通常情况下我们处理请求数据的onCreate方法，
     * 而是调用onNewIntent方法
     *
     * 这里负责更新WeatherFragment（创建新的Fragment以替换原来的），以显示新的天气
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        String weatherId = getIntent().getStringExtra("weather_id");
        if (weatherId!=null){
            WeatherFragment weatherFrag= new WeatherFragment();
            Bundle bundle = new Bundle();
            bundle.putString("weather_id", weatherId);
            weatherFrag.setArguments(bundle);      //通过FragmentArguments传递weather_id参数
            mFragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            //更换（更新）WeatherFragment，以显示不同的天气（根据weather_id参数）
            transaction.replace(R.id.myCoor, weatherFrag).commit();
        }

        Log.d("LifeCycle", "MainActivity_OnResume");

    }

    /**
    显示ToolBar，显示ToolBar左边的抽屉按钮，并监听
     */
    private void initToolBar() {
        mIndexToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mIndexToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        //以下用于显示和监听ToolBar左边的抽屉按钮
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,mDrawerLayout,
                mIndexToolBar,R.string.drawer_open,R.string.drawer_close);
        toggle.syncState();
        mDrawerLayout.addDrawerListener(toggle);
    }

    /**
     * 初始化Navigation参数，为抽屉菜单项设置响应动作
     */
    private void initNavigation(){
        mDrawerLayout= (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //为抽屉菜单的各菜单项按钮设置监听器
        //其实抽屉菜单也是菜单，各item也定义在menu目录中，与ActionBar中的菜单的区别主要是：
        //ActionBar中的菜单不需要程序员来设置监听器，只需重写方法即可
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.add_city:
                        Intent intentCity = new Intent(MainActivity.this,ChooseCity.class);
                        startActivity(intentCity);
                        break;
//                    case R.id.multi_cities:
//                        Toast.makeText(MainActivity.this, "此功能再下个版本添加！", Toast.LENGTH_SHORT).show();
//                        break;
                    case R.id.about:
                        Intent intentAbout =new Intent(MainActivity.this,AboutActivity.class);
                        startActivity(intentAbout);
                        break;
                    case R.id.setting:
                        Intent intentSetting =new Intent(MainActivity.this,SettingActivity.class);
                        startActivity(intentSetting);
                        break;
                    case R.id.exit:
                        finish();
                }
                return false;
            }
        });
    }

    /**
     * 刚启动app时，通过高德地图定位城市，并显示当前城市的天气信息（定位失败显示默认城市）
     */
    private void initAMapLocationListener() {
        mLocationListener=new AMapLocationListener() {
            @Override public void onLocationChanged(AMapLocation amapLocation) {
                if(amapLocation!=null){
                    //ErrorCode为0说明定位失败
                    if(amapLocation.getErrorCode()==0) {
                        //定位成功回调信息，设置相关消息
                        amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                        amapLocation.getLatitude();//获取纬度
                        amapLocation.getLongitude();//获取经度
                        amapLocation.getAccuracy();//获取精度信息
                        String city = amapLocation.getCity();
                        if (!TextUtils.isEmpty(city)) {
                            String cityName = city.replace("市", "");
                            Log.i("定位成功", "当前城市为" + cityName);
                            queryWeatherCode(cityName);

                            Toast.makeText(MainActivity.this, "当前城市"+cityName, Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                        Log.e("aMapError", "ErrCode:" + amapLocation.getErrorCode()
                                + ", errInfo:" + amapLocation.getErrorInfo());
                        Log.e("定位失败","");
                        Toast.makeText(MainActivity.this, "定位失败加载默认城市〒_〒", Toast.LENGTH_SHORT).show();
                        //定位失败加载默认城市
                        String cityName = "合肥";
                        queryWeatherCode(cityName);

                    }
                    //停止定位
                    mLocationClient.stopLocation();
                    //销毁定位
                    mLocationClient.onDestroy();
                }
            }
        };
    }


    /**
     * 初始化高德地图定位参数
     */
    private void initLocation() {
        mLocationClient=new AMapLocationClient(getApplicationContext());
        mLocationClient.setLocationListener(mLocationListener);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();


        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);


        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        // 启动定位
        mLocationClient.startLocation();
    }

    /**
     * 将城市名转换成城市编码，新建并提交WeatherFragment
     */
    private void queryWeatherCode(String cityName) {

        weatherId = Common.getCityIdByName(cityName);
        if (weatherId != null) {
            WeatherFragment weatherFragment = new WeatherFragment();
            Bundle bundle = new Bundle();
            bundle.putString("weather_id", weatherId);
            weatherFragment.setArguments(bundle);
            mFragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.add(R.id.myCoor, weatherFragment).commit();

        }
    }

    /**
     * 点击返回键两次退出程序
     */
    private long exitTime = 0;
    @Override
    public void onBackPressed() {
        //先关闭抽屉
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            //第一次点击Back，或者距离上一次超过两秒
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }else{       //第二次点击Back
                finish();
                System.exit(0);
                Process.killProcess(Process.myPid());        //kill整个进程
            }
        }
    }

}
