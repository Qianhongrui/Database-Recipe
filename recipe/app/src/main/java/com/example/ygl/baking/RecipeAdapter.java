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




public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
    private Context mContext;
    protected List<Recipe> recipeList;
    private boolean state;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardView;
        ImageView recipeImageView;
        ImageView mark;
        TextView recipeName;
        TextView recipeServings;

        ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            recipeImageView = (ImageView) view.findViewById(R.id.recipe_image_view);
            mark = (ImageView) view.findViewById(R.id.mark);
            recipeName = (TextView) view.findViewById(R.id.recipe_name);
            recipeServings = (TextView) view.findViewById(R.id.recipe_servings);
            View favor = view.findViewById(R.id.favor);
            View hate = view.findViewById(R.id.hate);
            favor.setOnClickListener((View.OnClickListener) this);
            hate.setOnClickListener((View.OnClickListener) this);
            favor.setSelected(state);
            if (!state) mark.setVisibility(View.INVISIBLE);
        }

        //收藏或者拉黑
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.favor) {
                //int temp=0;
                List<Favor> favorList = DataSupport.findAll(Favor.class);
                List<Blacklist> recipeList_hate = DataSupport.findAll(Blacklist.class);
                if (v.isSelected()) {
                    for (int i = 0; i < favorList.size(); i++) {
                        if (favorList.get(i).getRecipeName().equals(recipeList.get(getLayoutPosition()).getRecipeName())) {
                            Toast.makeText(v.getContext(), "已取消收藏", Toast.LENGTH_SHORT).show();
                            favorList.get(i).delete();
                            //favorList.remove(i);

                            v.setSelected(false);
                            mark.setVisibility(View.INVISIBLE);
                            state = false;
                            notifyDataSetChanged();
                            break;
                        }
                    }
                } else {
                    state = true;
                    v.setSelected(true);
                    mark.setVisibility(View.VISIBLE);

                    Favor favor = new Favor();
                    favor.setRecipeId(recipeList.get(getLayoutPosition()).getRecipeId());
                    favor.setImageUrl(recipeList.get(getLayoutPosition()).getImageUrl());
                    favor.setRecipeName(recipeList.get(getLayoutPosition()).getRecipeName());
                    favor.setServings(recipeList.get(getLayoutPosition()).getServings());
                    favor.setCalories(recipeList.get(getLayoutPosition()).getCalories());
                    favor.setCarbohydrates(recipeList.get(getLayoutPosition()).getCarbohydrates());
                    favor.setFat(recipeList.get(getLayoutPosition()).getFat());
                    favor.setHot(recipeList.get(getLayoutPosition()).getHot());
                    favor.setPeople(recipeList.get(getLayoutPosition()).getPeople());
                    favor.setProtein(recipeList.get(getLayoutPosition()).getProtein());
                    favor.setSodium(recipeList.get(getLayoutPosition()).getSodium());
                    favor.setSoup(recipeList.get(getLayoutPosition()).getSoup());
                    favor.setVegetarian_food(recipeList.get(getLayoutPosition()).getVegetarian_food());
                    favor.save();

                    Toast.makeText(v.getContext(), "已收藏", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();

                }
            } else if (v.getId() == R.id.hate) {
                int temp = 0;
                List<Blacklist> blacklist = DataSupport.findAll(Blacklist.class);
                List<Favor> recipeList_favor = DataSupport.findAll(Favor.class);
                for (int i = 0; i < recipeList_favor.size(); i++) {
                    if (recipeList_favor.get(i).getRecipeName().equals(recipeList.get(getLayoutPosition()).getRecipeName())) {
                        Toast.makeText(v.getContext(), "請先取消收藏", Toast.LENGTH_SHORT).show();
                        temp = 1;
                        break;
                    }
                }
                if (temp == 0) {
                    Blacklist hate = new Blacklist();
                    hate.setRecipeId(recipeList.get(getLayoutPosition()).getRecipeId());
                    hate.setImageUrl(recipeList.get(getLayoutPosition()).getImageUrl());
                    hate.setRecipeName(recipeList.get(getLayoutPosition()).getRecipeName());
                    hate.setServings(recipeList.get(getLayoutPosition()).getServings());
                    hate.setCalories(recipeList.get(getLayoutPosition()).getCalories());
                    hate.setCarbohydrates(recipeList.get(getLayoutPosition()).getCarbohydrates());
                    hate.setFat(recipeList.get(getLayoutPosition()).getFat());
                    hate.setHot(recipeList.get(getLayoutPosition()).getHot());
                    hate.setPeople(recipeList.get(getLayoutPosition()).getPeople());
                    hate.setProtein(recipeList.get(getLayoutPosition()).getProtein());
                    hate.setSodium(recipeList.get(getLayoutPosition()).getSodium());
                    hate.setSoup(recipeList.get(getLayoutPosition()).getSoup());
                    hate.setVegetarian_food(recipeList.get(getLayoutPosition()).getVegetarian_food());
                    hate.save();

                    Toast.makeText(v.getContext(), "已加入黑名單", Toast.LENGTH_SHORT).show();
                    recipeList.get(getLayoutPosition()).delete();
                    recipeList.remove(getLayoutPosition());

                    //recipeList.add(0,recipeList.get(getLayoutPosition()));

                    notifyItemRemoved(getLayoutPosition());
                    //notifyDataSetChanged();
                    //recipeList.add(3,recipeList.get(getLayoutPosition()));
                    //notifyItemInserted(0);
                }
            }
        }
    }

    RecipeAdapter(List<Recipe> list, View emptyPrompt) {
        recipeList = list;
        emptyPrompt.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {

        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recipe, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击事件
                Intent intent = new Intent(mContext, StepActivity.class);
                intent.putExtra(mContext.getString(R.string.recip_name),
                        recipeList.get(viewHolder.getLayoutPosition()).getRecipeName());
                mContext.startActivity(intent);
            }
        });
        return viewHolder;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mark.bringToFront();
        Recipe recipe = recipeList.get(position);
        if (TextUtils.isEmpty(recipe.getImageUrl())) {
            Glide.with(mContext).load(R.drawable.logo_black).into(holder.recipeImageView);
        } else {
            //加载网络图片
            Glide.with(mContext).load(recipe.getImageUrl()).into(holder.recipeImageView);
        }

        holder.recipeName.setText(recipe.getRecipeName());
        holder.recipeServings.setText(
                "湯菜種類：" + recipe.getSoup() +
                        "\r\n辣度：" + recipe.getHot() +
                        "\r\n熱量：" + recipe.getCalories() + "大卡" +
                        "\r\n脂肪：" + recipe.getFat() + "g" +
                        "\r\n蛋白质：" + recipe.getProtein() + "g");
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    /*@Override
    public int getItemCount() {
        if (recipeList.size() > number) {
            //return 3;
            return number;
        } else {
            return recipeList.size();
        }
    }*/

    @Override
    public int getItemViewType(int position) {
        List<Favor> favorList = DataSupport.findAll(Favor.class);
        for (int i = 0; i < favorList.size(); i++) {
            if (favorList.get(i).getRecipeName().equals(recipeList.get(position).getRecipeName())) {
                state = true;
                break;
            }
            state = false;
        }
        return 0;
    }

}
