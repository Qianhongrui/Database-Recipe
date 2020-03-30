package com.example.ygl.baking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ygl.baking.sql.model.Blacklist;
import com.example.ygl.baking.sql.model.Favor;
import com.example.ygl.baking.sql.model.Recipe;

import org.litepal.crud.DataSupport;

import java.util.Collections;
import java.util.List;

import static com.example.ygl.baking.MainActivity.number;




public class RecommandAdapter extends RecipeAdapter{

    RecommandAdapter(List<Recipe> list, View emptyPrompt) {
        super(list, emptyPrompt);
        Collections.shuffle(recipeList);
    }
    @Override
    public int getItemCount() {
        if (recipeList.size() > number) {
            //return 3;
            return number;
        } else {
            return recipeList.size();
        }
    }
}
