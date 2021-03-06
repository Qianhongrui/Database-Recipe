package com.example.ygl.baking;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.example.ygl.baking.Util.Util;
import com.example.ygl.baking.sql.StubProvider;
import com.example.ygl.baking.sql.model.Blacklist;
import com.example.ygl.baking.sql.model.Result;
import com.example.ygl.baking.sync.SyncAdapter;

import org.litepal.crud.DataSupport;

import java.util.List;

public class ResultActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,SyncAdapter.BadNews {
    private static final String TAG = "ResultActivity";
    private List<Result> recipeList;
    private List<Blacklist> recipeList_hate;
    private Recipe_resultAdapter recipeAdapter;
    private RecyclerView recyclerView;
    private TextView emptyPrompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);


        //600dp对应的px
        int dp= Util.convertDipOrPx(this,600);
        //屏幕横向的px
        DisplayMetrics displayMetrics=getResources().getDisplayMetrics();
        int widthPx=displayMetrics.widthPixels;

        GridLayoutManager layoutManager=new GridLayoutManager(this,1);
        if(widthPx>=dp){
            //强制横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else {
            //强制竖屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        if(getRequestedOrientation()== ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            layoutManager=new GridLayoutManager(this,3);
        }
        if(getRequestedOrientation()== ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            layoutManager=new GridLayoutManager(this,1);
        }

        SyncAdapter.setBadNewsCallBack(this);

        emptyPrompt=(TextView) findViewById(R.id.empty_prompt);
        recyclerView=(RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager); //组件垂直往下

        recipeList= DataSupport.findAll(Result.class);
        recipeList_hate= DataSupport.findAll(Blacklist.class);

        recipeList= DataSupport.findAll(Result.class);
        if(recipeList.size()<=0){
            /*Intent intent = new Intent();
            intent.setClass(ResultActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//它可以關掉所要到的介面中間的activity
            startActivity(intent);
            finish();*/
            emptyPrompt.setText("拍謝，查無類似食譜喲"+"\n"+"換個關鍵字再來試試看");
        }else {
            recipeAdapter=new Recipe_resultAdapter(recipeList,emptyPrompt);
            recyclerView.setAdapter(recipeAdapter);
        }


        getLoaderManager().initLoader(0, null,this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.return_result);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(ResultActivity.this, SearchActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//它可以關掉所要到的介面中間的activity
                startActivity(intent);
                finish();
            }
        });
    }



    //并非加载器的正确使用方法
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle){
        Log.i(TAG,"onCreateLoader");
        return new CursorLoader(ResultActivity.this, StubProvider.RecipeUri,null,null,null,null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.i(TAG,"onLoadFinished(Loader<Cursor> loader, Cursor cursor)");
        recipeList= DataSupport.findAll(Result.class);
        recipeAdapter=new Recipe_resultAdapter(recipeList,emptyPrompt);
        recipeAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(recipeAdapter);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader){
    }
    //結束并非加载器

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        //屏幕方向改变需要做的事
    }

    //同步数据
    private void syncData(){
        if(Util.isNetworkConnected(this)){
            emptyPrompt.setText(R.string.refresh);
            emptyPrompt.setTextColor(getResources().getColor(R.color.dimgray));
            emptyPrompt.setOnClickListener(null);
            SyncAdapter.CreateSyncAccount(ResultActivity.this);
        }else {
            updateEmptyView(SyncAdapter.NETWORK_STATUS_NO_NETWORK);
        }
    }

    private void updateEmptyView(@SyncAdapter.NetWorkStatus int netWorkStatus) {
        int message= R.string.no_data;
        switch (netWorkStatus){
            case SyncAdapter.NETWORK_STATUS_NO_NETWORK:
                message= R.string.no_data_no_net_work;
                break;
            case SyncAdapter.NETWORK_STATUS_SERVER_DOWN:
                message= R.string.no_data_server_down;
                break;
            case SyncAdapter.NETWORK_STATUS_SERVER_TIMEOUT:
                message= R.string.no_data_server_time_out;
                break;
            default:
        }
        emptyPrompt.setTextColor(getResources().getColor(R.color.colorAccent));
        emptyPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recipeList.size()<=0 && recipeList_hate.size()<=0)
                    syncData();
            }
        });
        emptyPrompt.setText(message);
    }

    @Override
    public void weHaveABadNews(final int netWorkStatus){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateEmptyView(netWorkStatus);
            }
        });
    }
    //結束同步數據
    //禁用返回键
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.setClass(ResultActivity.this, SearchActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//它可以關掉所要到的介面中間的activity
            startActivity(intent);
            finish();
        }
        return false;
    }

}
