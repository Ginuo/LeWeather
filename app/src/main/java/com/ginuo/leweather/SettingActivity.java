package com.ginuo.leweather;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import static com.ginuo.leweather.R.xml.settingui;

public class SettingActivity extends PreferenceActivity {

    private Toolbar mToolbar;
    private AppCompatDelegate delegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);
        initView();
        setToolbar();
        initFragment();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(Color.WHITE);
        delegate.getSupportActionBar().setDisplayShowTitleEnabled(false);
        delegate.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void setSupportActionBar(Toolbar mToolbar) {
        getDelegate().setSupportActionBar(mToolbar);

    }

    private void setToolbar() {
        mToolbar.setTitle("设置");
    }

    private void initFragment() {
        getFragmentManager().beginTransaction().replace(R.id.content, new SettingFragment()).commit();
    }

    public AppCompatDelegate getDelegate() {
        if (delegate == null) {
            delegate = AppCompatDelegate.create(this, null);
        }
        return delegate;
    }

    public static class SettingFragment extends PreferenceFragment {
        private Preference mUpdate;

        private CheckBoxPreference mNotification;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(settingui);
            initNotification();

            initUpdate();
        }


        //通知栏显示天气相应的信息
        private void initNotification() {
            SharedPreferences prefs = getActivity().getSharedPreferences("notification", MODE_PRIVATE);
            final String cityName = prefs.getString("cityName", "");
            final String temp = prefs.getString("temperature", "");
            final String weatherInfo = prefs.getString("weatherInfo", "");
            mNotification = (CheckBoxPreference) findPreference("notification");
            mNotification.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (mNotification.isChecked()) {
                        NotificationManager manager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity());
                        Notification notification = builder
                                .setContentTitle(cityName + "-" + weatherInfo)
                                .setContentText("当前温度:" + temp)
                                .setWhen(System.currentTimeMillis())
                                .setSmallIcon(R.mipmap.bat)
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.bat))
                                .build();
                        manager.notify(1, notification);
                    } else {
                        NotificationManager manager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
                        manager.cancelAll();
                    }

                    return true;
                }
            });
        }
        //由于时间关系该躬耕还未实现 先用Toast替代 嘿嘿
        private void initUpdate() {
            mUpdate = findPreference("update_version");
            mUpdate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Toast.makeText(getActivity(), "当前已是最新版本", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }

    }
}


