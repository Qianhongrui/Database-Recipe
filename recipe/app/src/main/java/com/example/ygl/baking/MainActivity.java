package com.example.ygl.baking;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ygl.baking.Preference.StateSQLiteOpenHelper;
import com.example.ygl.baking.Util.Util;
import com.example.ygl.baking.magic_flash.FlashChangeArrowController;
import com.example.ygl.baking.magic_flash.FlashSearchView;
import com.example.ygl.baking.sql.StubProvider;
import com.example.ygl.baking.sql.model.Blacklist;
import com.example.ygl.baking.sql.model.Recipe;
import com.example.ygl.baking.sql.model.Step;
import com.example.ygl.baking.sync.SyncAdapter;

import org.litepal.crud.DataSupport;

import java.util.List;

import static org.litepal.LitePalApplication.getContext;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, SyncAdapter.BadNews, NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private List<Recipe> recipeList;
    private List<Step> stepList;
    private List<Blacklist> recipeList_hate;
    private RecipeAdapter recipeAdapter;
    private RecyclerView recyclerView;
    private TextView emptyPrompt;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;//元件觸發
    private Toolbar mToolboar;

    private static StateSQLiteOpenHelper helper;
    private static SQLiteDatabase db;

    static int[] a;

    public static int number;

    FlashSearchView mFlashSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        a = new int[50];
        //侧拉式选单
        mToolboar = (Toolbar) findViewById(R.id.nav_action);
        setSupportActionBar(mToolboar);//Toolbar取代原本的ActionBar

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolboar, R.string.open, R.string.close);//必須用字串資源檔
        mDrawerLayout.addDrawerListener(mToggle);//監聽選單按鈕是否被觸擊
        mToggle.syncState();//隱藏顯示箭頭返回


        //讓 ActionBar 中的返回箭號置換成 Drawer 的三條線圖示。並且把這個觸發器指定給 layDrawer 。
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);//清單觸發監聽事件
        //侧拉式选单


        //600dp对应的px
        int dp = Util.convertDipOrPx(this, 600);
        //屏幕横向的px
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int widthPx = displayMetrics.widthPixels;

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        if (widthPx >= dp) {
            //强制横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            //强制竖屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            layoutManager = new GridLayoutManager(this, 3);
        }
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            layoutManager = new GridLayoutManager(this, 1);
        }

        SyncAdapter.setBadNewsCallBack(this);

        emptyPrompt = (TextView) findViewById(R.id.empty_prompt);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager); //组件垂直往下

        recipeList = DataSupport.findAll(Recipe.class);
        stepList = DataSupport.findAll(Step.class);
        recipeList_hate = DataSupport.findAll(Blacklist.class);
        helper = new StateSQLiteOpenHelper(getContext());
        if (!hasData("葷菜")) {
            insertData("葷菜", 1);
            Log.i("葷菜", "insert");
        }
        if (!hasData("素菜")) {
            insertData("素菜", 1);
            Log.i("素菜", "insert");
        }
        if (!hasData("辣")) {
            insertData("辣", 1);
            Log.i("辣", "insert");
        }
        if (!hasData("不辣")) {
            insertData("不辣", 1);
            Log.i("不辣", "insert");

        }
        if (!hasData("菜數")) {
            insertData("菜數", 5);
            Log.i("菜数", "insert");
        }
        if (!hasData("人數")) {
            insertData("人數", 1);
            Log.i("人数", "insert");
        }
        if (!hasData("變化")) {
            insertData("變化", 0);
            Log.i("變化", "insert");
        }

        number = getData("菜數");


        Log.i("變化??????????????", String.valueOf(getData("變化") % 2));
        if (getData("變化") % 2 == 1) {
            for (int i = 0; i < recipeList.size(); i++) {
                stepList.remove(i);

                recipeList.get(i).delete();
            }
            Log.i("變化", "fuck!!!!!!!!!!!!!!!!!!!!!!!");
            Log.i(String.valueOf(stepList.size()), "fuck!!!!!!!!!!!!!!!!!!!!!!!");
            for (int i = 0; i < stepList.size(); i++) {
                DataSupport.delete(Step.class, i);

            }
            DataSupport.deleteAll(Step.class);
            Log.i(String.valueOf(stepList.size()), "fuck!!!!!!!!!!!!!!!!!!!!!!!");
            syncData();
            updateData("變化", 0);
        }

        if (recipeList.size() <= 0 && recipeList_hate.size() <= 0) {
            syncData();
        } else {
            recipeAdapter = new RecipeAdapter(recipeList, emptyPrompt);
            recyclerView.setAdapter(recipeAdapter);
        }


        getLoaderManager().initLoader(0, null, this);
        navigationView.bringToFront();
        mFlashSearchView = (FlashSearchView) findViewById(R.id.jjsv_boot);
        mFlashSearchView.setController(new FlashChangeArrowController());

        new Handler().postDelayed(new Runnable() {
            public void run() {
                mFlashSearchView.startAnim();
            }

        }, 200);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                mFlashSearchView.setVisibility(View.GONE);
            }

        }, 900);
    }


    //實作側拉菜單
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public boolean onNavigationItemSelected(MenuItem item) {//實作清單觸發
        int id = item.getItemId();

        if (id == R.id.nav_preference) {
            Toast.makeText(this, "preference", Toast.LENGTH_SHORT).show();
            mFlashSearchView.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    mFlashSearchView.resetAnim();
                }
            }, 100);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    startActivity(new Intent(MainActivity.this, CardActivity.class));
                    //mFlashSearchView.setVisibility(View.GONE);
                    finish();
                }
            }, 600);
        } else if (id == R.id.nav_recommend) {
            Toast.makeText(this, "settings", Toast.LENGTH_SHORT).show();
            mFlashSearchView.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    mFlashSearchView.resetAnim();
                }
            }, 100);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    startActivity(new Intent(MainActivity.this, recommandActivity.class));
                    finish();
                }
            }, 600);

        } else if (id == R.id.nav_enshrine) {
            Toast.makeText(this, "enshrine", Toast.LENGTH_SHORT).show();
            mFlashSearchView.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    mFlashSearchView.resetAnim();
                }
            }, 100);

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    startActivity(new Intent(MainActivity.this, favorActivity.class));
                    finish();
                }
            }, 600);
        } else if (id == R.id.nav_search) {
            Toast.makeText(this, "search_samll", Toast.LENGTH_SHORT).show();
            mFlashSearchView.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    mFlashSearchView.resetAnim();
                }
            }, 100);

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    startActivity(new Intent(MainActivity.this, SearchActivity.class));
                    finish();
                }
            }, 600);
        } else if (id == R.id.nav_blacklist) {
            Toast.makeText(this, "blacklist", Toast.LENGTH_SHORT).show();
            mFlashSearchView.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    mFlashSearchView.resetAnim();
                }
            }, 100);

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    startActivity(new Intent(MainActivity.this, hateActivity.class));
                    finish();
                }
            }, 600);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {//當按下左上三條線或顯示工具列
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //結束側拉菜單


    //并非加载器的正确使用方法
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.i(TAG, "onCreateLoader");
        return new CursorLoader(MainActivity.this, StubProvider.RecipeUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.i(TAG, "onLoadFinished(Loader<Cursor> loader, Cursor cursor)");
        recipeList = DataSupport.findAll(Recipe.class);
        recipeAdapter = new RecipeAdapter(recipeList, emptyPrompt);
        recipeAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(recipeAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
    //結束并非加载器

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //屏幕方向改变需要做的事
    }

    //同步数据
    private void syncData() {
        if (Util.isNetworkConnected(this)) {
            emptyPrompt.setText(R.string.refresh);
            emptyPrompt.setTextColor(getResources().getColor(R.color.dimgray));
            emptyPrompt.setOnClickListener(null);
            SyncAdapter.CreateSyncAccount(MainActivity.this);
        } else {
            updateEmptyView(SyncAdapter.NETWORK_STATUS_NO_NETWORK);
        }
    }

    private void updateEmptyView(@SyncAdapter.NetWorkStatus int netWorkStatus) {
        int message = R.string.no_data;
        switch (netWorkStatus) {
            case SyncAdapter.NETWORK_STATUS_NO_NETWORK:
                message = R.string.no_data_no_net_work;
                break;
            case SyncAdapter.NETWORK_STATUS_SERVER_DOWN:
                message = R.string.no_data_server_down;
                break;
            case SyncAdapter.NETWORK_STATUS_SERVER_TIMEOUT:
                message = R.string.no_data_server_time_out;
                break;
            default:
        }
        emptyPrompt.setTextColor(getResources().getColor(R.color.colorAccent));
        emptyPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recipeList.size() <= 0 && recipeList_hate.size() <= 0)
                    syncData();
            }
        });
        emptyPrompt.setText(message);
    }

    @Override
    public void weHaveABadNews(final int netWorkStatus) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateEmptyView(netWorkStatus);
            }
        });
    }
    //結束同步數據


    private static boolean hasData(String tempName) {
        @SuppressLint("Recycle") Cursor cursor = helper.getReadableDatabase().rawQuery(
                //"select id as _id,name from records where name =?", new String[]{tempName});
                "select state ,state_name from states where state_name =?", new String[]{tempName});
        //  判断是否有下一个
        return cursor.moveToNext();
    }

    private static int getData(String tempName) {
        int state = 0;
        db = helper.getReadableDatabase();
        if (db != null) {
            @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM states WHERE state_name=?", new String[]{tempName});
            while (cursor.moveToNext()) {
                String state_name = cursor.getString(1);
                state = cursor.getInt(2);
                Log.e("SQLiteTest query", "state_name:" + state_name + " state:" + state);
            }
            db.close();
        }
        return state;
    }


    private static void insertData(String tempName, int tempState) {
        db = helper.getWritableDatabase();
        //db.execSQL("insert into records(name) values('" + tempName + "')");
        db.execSQL("insert into states(state_name, state) values('" + tempName + "', '" + tempState + "')");
        db.close();
    }

    private static void updateData(String tempName, int tempState) {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("state", tempState);//key為欄位名，value為值
        db.update("states", values, "state_name=?", new String[]{tempName});
        db.close();
    }
}
