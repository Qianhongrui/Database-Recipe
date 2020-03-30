package com.example.ygl.baking;


import android.content.Context;
import android.content.Intent;
import android.database.CursorJoiner;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.example.ygl.baking.Search.ICallBack;
import com.example.ygl.baking.Search.SearchView;
import com.example.ygl.baking.Search.bCallBack;
import com.example.ygl.baking.Util.GsonModel.RecipeModel;
import com.example.ygl.baking.magic_flash.FlashDotGoPathController;
import com.example.ygl.baking.magic_flash.FlashSearchView;
import com.example.ygl.baking.sql.model.Recipe;
import com.example.ygl.baking.sql.model.Result;
import com.example.ygl.baking.sql.model.Step;

import org.litepal.crud.DataSupport;

import java.util.List;




public class SearchActivity extends AppCompatActivity {
    public static  String search_string;
    // 1. 初始化搜索框变量
    private SearchView searchView;
    FlashSearchView mFlashSearchView;
    private List<Recipe> recipeList;
    private List<Step> stepList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        // 2. 绑定视图
        setContentView(R.layout.activity_search);
        search_string="";
        // 3. 绑定组件
        searchView = (SearchView) findViewById(R.id.search_view);

        // 4. 设置点击搜索按键后的操作（通过回调接口）
        // 参数 = 搜索框输入的内容
        searchView.setOnClickSearch(new ICallBack() {
            @Override
            public void SearchAction(String string) {
                search_string=string;
                System.out.println("我收到了" + string);

                hideKeyboard();
                start(searchView);
            }
        });

        // 5. 设置点击返回按键后的操作（通过回调接口）
        searchView.setOnClickBack(new bCallBack() {
            @Override
            public void BackAciton() {
                startActivity(new Intent(SearchActivity.this, MainActivity.class));
                finish();
            }
        });


        mFlashSearchView = (FlashSearchView) findViewById(R.id.jjsv);
        mFlashSearchView.setController(new FlashDotGoPathController());
        searchView.bringToFront();
    }


    public void start(View v) {
        DataSupport.deleteAll(Result.class);
        Search(search_string);
        mFlashSearchView.startAnim();
        searchView.setVisibility(View.GONE);
        //searchView.listView.setVisibility(View.GONE);
        //searchView.tv_clear.setVisibility(View.GONE);
        //searchView.tv_clear.setVisibility(INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent();
                intent.setClass(SearchActivity.this, ResultActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//它可以關掉所要到的介面中間的activity
                startActivity(intent);
                finish();
            }

        }, 3 * 1000);

    }

    public void reset(View v) {
        mFlashSearchView.resetAnim();
    }

    private void hideKeyboard() {
        View viewFocus = this.getCurrentFocus();
        if (viewFocus != null) {
            InputMethodManager imManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imManager.hideSoftInputFromWindow(viewFocus.getWindowToken(), 0);
        }
    }
    //禁用返回键
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.setClass(SearchActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//它可以關掉所要到的介面中間的activity
            startActivity(intent);
            finish();
        }
        return false;
    }

    public void  Search(String string){
        if (!search_string.equals("")) {
            recipeList = DataSupport.findAll(Recipe.class);
            stepList = DataSupport.findAll(Step.class);
            String ambitiousName = null;
            for (int j=0; j<stepList.size(); j++){
                if(stepList.get(j).getDescription().contains(string)){
                    ambitiousName = stepList.get(j).getForRecipe();
                }
            }
            for (int i=0; i<recipeList.size(); i++){
                if(recipeList.get(i).getRecipeName().contains(string) || (ambitiousName!=null && recipeList.get(i).getRecipeName().contains(ambitiousName))){
                    Result result = new Result();
                    result.setRecipeId(recipeList.get(i).getRecipeId());
                    result.setImageUrl(recipeList.get(i).getImageUrl());
                    result.setRecipeName(recipeList.get(i).getRecipeName());
                    result.setServings(recipeList.get(i).getServings());
                    result.setCalories(recipeList.get(i).getCalories());
                    result.setCarbohydrates(recipeList.get(i).getCarbohydrates());
                    result.setFat(recipeList.get(i).getFat());
                    result.setHot(recipeList.get(i).getHot());
                    result.setPeople(recipeList.get(i).getPeople());
                    result.setProtein(recipeList.get(i).getProtein());
                    result.setSodium(recipeList.get(i).getSodium());
                    result.setSoup(recipeList.get(i).getSoup());
                    result.setVegetarian_food(recipeList.get(i).getVegetarian_food());
                    result.save();
                }
            }
        }
    }
}
