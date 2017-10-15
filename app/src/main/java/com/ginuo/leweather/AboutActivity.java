package com.ginuo.leweather;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;


/**
 *Navigation功能之设置界面及功能的实现
 */
public class AboutActivity extends BaseActivity {

    private Button btn_code;
    private Button btn_shared;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initView();
        initToolBar();
    }


    //初始化ToolBar控件相关设置
    private void initToolBar() {
        Toolbar toolbarAbout = (Toolbar) findViewById(R.id.toolbar_about);
        setSupportActionBar(toolbarAbout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarAbout.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initView() {
        btn_code= (Button) findViewById(R.id.btn_code);
        btn_shared= (Button) findViewById(R.id.btn_share);
        btn_shared.setOnClickListener(new BtnListener());
        btn_code.setOnClickListener(new BtnListener());
    }

    class BtnListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                //查看项目源代码
                case R.id.btn_code:
                    visitWebsite("https://github.com/Ginuo/LeWeather");
                    break;
                //分享天气应用
                case R.id.btn_share:
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");;
                    startActivity(Intent.createChooser(shareIntent,"分享到"));
                    break;

            }
        }
    }

    private void visitWebsite(String url) {
        Uri uri = Uri.parse(url);   //指定网址
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);           //指定Action
        intent.setData(uri);                            //设置Uri
        startActivity(intent);        //启动Activity
    }

}
