package com.example.ygl.baking.sync;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.util.Log;

import com.example.ygl.baking.Preference.StateSQLiteOpenHelper;
import com.example.ygl.baking.R;
import com.example.ygl.baking.Util.GsonModel.RecipeModel;
import com.example.ygl.baking.sql.StubProvider;
import com.example.ygl.baking.sql.model.Recipe;
import com.example.ygl.baking.sql.model.Step;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.ACCOUNT_SERVICE;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */

/**
 * 重要!
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static StateSQLiteOpenHelper helper;
    private static SQLiteDatabase db;
    // Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;
    private static final String TAG = "SyncAdapter";
    // Constants
    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "com.example.ygl.baking";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "baking.ygl.example.com";
    // The account name
    public static final String ACCOUNT = "Baking";

    //请求数据遇到问题接口
    private static BadNews badNews;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NETWORK_STATUS_NO_NETWORK, NETWORK_STATUS_SERVER_DOWN, NETWORK_STATUS_SERVER_TIMEOUT})
    public @interface NetWorkStatus {
    }

    public static final int NETWORK_STATUS_NO_NETWORK = 0;
    public static final int NETWORK_STATUS_SERVER_DOWN = 1;
    public static final int NETWORK_STATUS_SERVER_TIMEOUT = 2;

    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(
            final Account account,
            final Bundle extras,
            final String authority,
            final ContentProviderClient provider,
            final SyncResult syncResult) {
        /*
         * Put the data transfer code here.
         */
        Log.i(TAG, "onPerformSync is run");
        //final String urlstr=getContext().getString(R.string.api_url);
        netWork(/*urlstr*/);
    }

    private void netWork(/*final String string*/) {
        helper = new StateSQLiteOpenHelper(getContext());

        final String meat = String.valueOf(getData("葷菜"));
        final String vegetable = String.valueOf(getData("素菜"));
        final String hot = String.valueOf(getData("辣"));
        final String nothot = String.valueOf(getData("不辣"));
        final String people = String.valueOf(getData("人數"));

        //使用okhttp
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                FormBody formBody = new FormBody
                        .Builder()
                        .add("meat", meat)
                        .add("vegetable", vegetable)
                        .add("hot", hot)
                        .add("nothot", nothot)
                        .add("people", people)
                        //.add("age", "20")
                        .build();
                final Request request = new Request.Builder()
                        .url("http://163.25.101.97/respond_1.php")
                        .post(formBody)
                        .build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.i("!!!!!!!!!!!!", "Post Parameter 失败");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String responseStr = response.body().string();
                        Log.i("!!!!!!!!!!!!",responseStr.length()+responseStr);
                        Log.i("!!!!!!!!!!!!", "Code：" + String.valueOf(response.code()));
                        new Thread(new Runnable(){
                            @Override
                            public void run() {
                                analysisJson(responseStr);
                            }
                        }).start();
                    }
                });
            }
        }).start();
    }

    //使用Gson的优秀例子
    private void analysisJson(String JsonStr) {
        Log.i("!!!!!!!!!!!!","##############");
        Gson gson = new Gson();
        Type type = new TypeToken<List<RecipeModel>>() {
        }.getType();
        List<RecipeModel> list = gson.fromJson(JsonStr, type);
        int stepId = 0;
        for (RecipeModel OneRecipe : list) {
            //保存一个菜谱
            Recipe recipe = new Recipe();
            recipe.setRecipeId(OneRecipe.id);
            recipe.setRecipeName(OneRecipe.name);
            recipe.setServings(OneRecipe.servings);
            recipe.setImageUrl(OneRecipe.image);
            recipe.setVegetarian_food(OneRecipe.vegetarian_food);
            recipe.setHot(OneRecipe.hot);
            recipe.setCalories(OneRecipe.calories);
            recipe.setCarbohydrates(OneRecipe.carbohydrates);
            recipe.setFat(OneRecipe.fat);
            recipe.setProtein(OneRecipe.protein);
            recipe.setPeople(OneRecipe.people);
            recipe.setSoup(OneRecipe.soup);
            recipe.setSodium(OneRecipe.sodium);

            recipe.save();

            //将配料表保存为一个步骤
            List<RecipeModel.IngredientsModel> IngredientsList = OneRecipe.ingredients;
            String DescriptionStr = "";
            for (RecipeModel.IngredientsModel OneIngredients : IngredientsList) {
                DescriptionStr = DescriptionStr
                        + getContext().getString(R.string.n) + getContext().getString(R.string.quantity) + OneIngredients.quantity
                        + getContext().getString(R.string.n) + getContext().getString(R.string.measure) + OneIngredients.measure
                        + getContext().getString(R.string.n) + getContext().getString(R.string.ingredient) + OneIngredients.ingredient
                        + getContext().getString(R.string.n) + getContext().getString(R.string.price) + OneIngredients.price + getContext().getString(R.string.n);
            }
            Step IngredientsStep = new Step();
            IngredientsStep.setForRecipe(OneRecipe.name);
            IngredientsStep.setStepId(String.valueOf(stepId++));
            IngredientsStep.setStepTitle(getContext().getString(R.string.ingredients_title));
            IngredientsStep.setDescription(DescriptionStr);
            IngredientsStep.save();
            //保存所有步骤
            List<RecipeModel.StepsModel> StepsList = OneRecipe.steps;
            for (RecipeModel.StepsModel OneSteps : StepsList) {
                Step step = new Step();
                step.setForRecipe(OneRecipe.name);
                step.setStepId(String.valueOf(stepId++));
                step.setStepTitle(OneSteps.shortDescription);
                step.setDescription(OneSteps.description);
                if (!TextUtils.isEmpty(OneSteps.videoURL)) {
                    step.setVideoUrl(OneSteps.videoURL);
                } else {
                    step.setVideoUrl(OneSteps.thumbnailURL);
                }
                step.save();
            }
        }
        //通知,数据库发生变化
        noticeSQLChange();
        Log.i(TAG, "Data input SQL");
    }

    //通知,数据库发生变化
    public void noticeSQLChange() {
        ContentValues[] cvArray = new ContentValues[1];
        getContext().getContentResolver().bulkInsert(StubProvider.RecipeUri, cvArray);
    }

    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    public static Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Log.i(TAG, "CreateSyncAccount is run");
        Account newAccount = new Account(
                ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
        if (null == accountManager.getPassword(newAccount)) {
            /*
             * Add the account and account type, no password or user data
             * If successful, return the Account object, otherwise report an error.
             */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
        }
        onAccountCreated(newAccount, context);
        return newAccount;
    }

    //创建账号后开始同步
    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        //设置周期同步

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        //网络可用时自动同步

        /*
         * Finally, let's do a sync to get things started
         */
        //手动同步
        syncImmediately(newAccount, context);
    }

    public static void syncImmediately(Account newAccount, Context context) {
        Log.i(TAG, "syncImmediately is run");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(newAccount, AUTHORITY, bundle);
    }

    private void setBadNews(@NetWorkStatus int netWorkStatus) {
        if (badNews != null) {
            badNews.weHaveABadNews(netWorkStatus);
        }
    }

    public interface BadNews {
        void weHaveABadNews(@NetWorkStatus int netWorkStatus);
    }

    public static void setBadNewsCallBack(BadNews bn) {
        badNews = bn;
    }

    private static boolean hasData(String tempName) {
        @SuppressLint("Recycle") Cursor cursor = helper.getReadableDatabase().rawQuery(
                //"select id as _id,name from records where name =?", new String[]{tempName});
                "select state ,state_name from states where state_name =?", new String[]{tempName});
        //  判断是否有下一个
        return !cursor.moveToNext();
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
