package com.example.ygl.baking;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.example.ygl.baking.Preference.CardFragmentPagerAdapter;
import com.example.ygl.baking.Preference.CardItem;
import com.example.ygl.baking.Preference.CardPagerAdapter;
import com.example.ygl.baking.Preference.StateSQLiteOpenHelper;

import static org.litepal.LitePalApplication.getContext;

public class CardActivity extends AppCompatActivity {
    public static boolean su;
    public static boolean hun;
    public static boolean la;
    public static boolean bula;
    public static int cai;
    public static int renshu;


    private static StateSQLiteOpenHelper helper;
    private static SQLiteDatabase db;

    //private Button mButton;
    private static ViewPager mViewPager;

    private CardPagerAdapter mCardAdapter;
    //private ShadowTransformer mCardShadowTransformer;
    private CardFragmentPagerAdapter mFragmentCardAdapter;
    // private ShadowTransformer mFragmentCardShadowTransformer;

    private boolean mShowingFragments = false;
    public static FloatingActionButton fab;
    public static FloatingActionButton back;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        //mButton = (Button) findViewById(R.id.cardTypeBtn);
        //((CheckBox) findViewById(R.id.checkBox)).setOnCheckedChangeListener(this);
        //mButton.setOnClickListener(this);
        mCardAdapter = new CardPagerAdapter();
        mCardAdapter.addCardItem(new CardItem(R.string.title_1, 1));
        mCardAdapter.addCardItem(new CardItem(R.string.title_2, 2));
        mCardAdapter.addCardItem(new CardItem(R.string.title_3, 3));
        mCardAdapter.addCardItem(new CardItem(R.string.title_4, 4));
        mFragmentCardAdapter = new CardFragmentPagerAdapter(getSupportFragmentManager(),
                dpToPixels(2, this));

        //mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);
        //mFragmentCardShadowTransformer = new ShadowTransformer(mViewPager, mFragmentCardAdapter);

        mViewPager.setAdapter(mCardAdapter);
        //mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(3);

        fab = (FloatingActionButton) findViewById(R.id.return_preference);
        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(CardActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//它可以關掉所要到的介面中間的activity
                startActivity(intent);
                finish();
            }
        });
        back = (FloatingActionButton) findViewById(R.id.no_change);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData("變化",0);
                Intent intent = new Intent();
                intent.setClass(CardActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//它可以關掉所要到的介面中間的activity
                startActivity(intent);
                finish();
            }
        });
        //fab.setVisibility(View.GONE);

        helper = new StateSQLiteOpenHelper(getContext());

    }


    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }



    //禁用返回键
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.setClass(CardActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//它可以關掉所要到的介面中間的activity
            startActivity(intent);
            finish();
        }
        return false;
    }

    private static void updateData(String tempName, int tempState) {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("state", tempState);//key為欄位名，value為值
        db.update("states", values, "state_name=?", new String[]{tempName});
        db.close();
    }
}
