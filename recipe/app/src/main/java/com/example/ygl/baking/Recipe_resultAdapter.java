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
import com.example.ygl.baking.sql.model.Result;

import org.litepal.crud.DataSupport;

import java.util.List;


public class Recipe_resultAdapter extends RecyclerView.Adapter<Recipe_resultAdapter.ViewHolder> {
    private Context mContext;
    private List<Result> recipe_resultList;
    private List<Recipe> recipeList;
    private boolean state;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CardView cardView;
        ImageView recipeImageView;
        ImageView mark;
        TextView recipeName;
        TextView recipeServings;

        ViewHolder(View view){
            super(view);
            cardView=(CardView) view;
            recipeImageView=(ImageView)view.findViewById(R.id.recipe_image_view);
            mark=(ImageView)view.findViewById(R.id.mark);
            recipeName=(TextView)view.findViewById(R.id.recipe_name);
            recipeServings=(TextView)view.findViewById(R.id.recipe_servings);
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
            if(v.getId() == R.id.favor) {
                //int temp=0;
                List<Favor> favorList= DataSupport.findAll(Favor.class);
                List<Blacklist> recipeList_hate= DataSupport.findAll(Blacklist.class);
                if (v.isSelected()){
                    for ( int i=0; i<favorList.size(); i++){
                        if (favorList.get(i).getRecipeName().equals(recipe_resultList.get(getLayoutPosition()).getRecipeName())) {
                            Toast.makeText(v.getContext(), "已取消收藏", Toast.LENGTH_SHORT).show();
                            favorList.get(i).delete();
                            //favorList.remove(i);

                            v.setSelected(false);
                            mark.setVisibility(View.INVISIBLE);
                            state=false;
                            notifyDataSetChanged();
                            break;
                        }
                    }
                }else{
                    Favor favor = new Favor();
                    favor.setRecipeId(recipe_resultList.get(getLayoutPosition()).getRecipeId());
                    favor.setImageUrl(recipe_resultList.get(getLayoutPosition()).getImageUrl());
                    favor.setRecipeName(recipe_resultList.get(getLayoutPosition()).getRecipeName());
                    favor.setServings(recipe_resultList.get(getLayoutPosition()).getServings());
                    favor.setCalories(recipe_resultList.get(getLayoutPosition()).getCalories());
                    favor.setCarbohydrates(recipe_resultList.get(getLayoutPosition()).getCarbohydrates());
                    favor.setFat(recipe_resultList.get(getLayoutPosition()).getFat());
                    favor.setHot(recipe_resultList.get(getLayoutPosition()).getHot());
                    favor.setPeople(recipe_resultList.get(getLayoutPosition()).getPeople());
                    favor.setProtein(recipe_resultList.get(getLayoutPosition()).getProtein());
                    favor.setSodium(recipe_resultList.get(getLayoutPosition()).getSodium());
                    favor.setSoup(recipe_resultList.get(getLayoutPosition()).getSoup());
                    favor.setVegetarian_food(recipe_resultList.get(getLayoutPosition()).getVegetarian_food());
                    favor.save();

                    Toast.makeText(v.getContext(), "已收藏", Toast.LENGTH_SHORT).show();

                    v.setSelected(true);
                    mark.setVisibility(View.VISIBLE);
                    state=true;
                    notifyDataSetChanged();
                }
            }else if(v.getId() == R.id.hate) {
                int temp=0;
                List<Blacklist> blacklist= DataSupport.findAll(Blacklist.class);
                List<Favor> recipeList_favor= DataSupport.findAll(Favor.class);
                List<Recipe> recipeList= DataSupport.findAll(Recipe.class);
                for (int i=0; i<recipeList_favor.size(); i++){
                    if (recipeList_favor.get(i).getRecipeName().equals(recipe_resultList.get(getLayoutPosition()).getRecipeName())){
                        Toast.makeText(v.getContext(), "請先取消收藏", Toast.LENGTH_SHORT).show();
                        temp=1;
                        break;
                    }
                }
                if(temp==0){
                    Blacklist hate = new Blacklist();
                    hate.setRecipeId(recipe_resultList.get(getLayoutPosition()).getRecipeId());
                    hate.setImageUrl(recipe_resultList.get(getLayoutPosition()).getImageUrl());
                    hate.setRecipeName(recipe_resultList.get(getLayoutPosition()).getRecipeName());
                    hate.setServings(recipe_resultList.get(getLayoutPosition()).getServings());
                    hate.setCalories(recipe_resultList.get(getLayoutPosition()).getCalories());
                    hate.setCarbohydrates(recipe_resultList.get(getLayoutPosition()).getCarbohydrates());
                    hate.setFat(recipe_resultList.get(getLayoutPosition()).getFat());
                    hate.setHot(recipe_resultList.get(getLayoutPosition()).getHot());
                    hate.setPeople(recipe_resultList.get(getLayoutPosition()).getPeople());
                    hate.setProtein(recipe_resultList.get(getLayoutPosition()).getProtein());
                    hate.setSodium(recipe_resultList.get(getLayoutPosition()).getSodium());
                    hate.setSoup(recipe_resultList.get(getLayoutPosition()).getSoup());
                    hate.setVegetarian_food(recipe_resultList.get(getLayoutPosition()).getVegetarian_food());
                    hate.save();

                    Toast.makeText(v.getContext(), "已加入黑名單", Toast.LENGTH_SHORT).show();
                    recipe_resultList.get(getLayoutPosition()).delete();

                    recipe_resultList.remove(getLayoutPosition());


                    //recipeList.add(0,recipeList.get(getLayoutPosition()));

                    notifyItemRemoved(getLayoutPosition());
                    recipeList.get(getLayoutPosition()).delete();
                    recipeList.remove(getLayoutPosition());
                    notifyDataSetChanged();
                    //notifyDataSetChanged();
                    //recipeList.add(3,recipeList.get(getLayoutPosition()));
                    //notifyItemInserted(0);
                }
            }
        }
    }

    Recipe_resultAdapter(List<Result> list, View emptyPrompt){
        recipe_resultList=list;
        emptyPrompt.setVisibility(list.size()==0?View.VISIBLE:View.GONE);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType){

        if (mContext==null){
            mContext=parent.getContext();
        }
        View view= LayoutInflater.from(mContext).inflate(R.layout.item_recipe_result,parent,false);
        final ViewHolder viewHolder=new ViewHolder(view);

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击事件
                Intent intent = new Intent(mContext, StepActivity.class);
                intent.putExtra(mContext.getString(R.string.recip_name),
                        recipe_resultList.get(viewHolder.getLayoutPosition()).getRecipeName());
                mContext.startActivity(intent);
            }
        });
        return viewHolder;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){
        holder.mark.bringToFront();
        Result recipe=recipe_resultList.get(position);
        if (TextUtils.isEmpty(recipe.getImageUrl())){
            Glide.with(mContext).load(R.drawable.logo_black).into(holder.recipeImageView);
        }else {
            //加载网络图片
            Glide.with(mContext).load(recipe.getImageUrl()).into(holder.recipeImageView);
        }

        holder.recipeName.setText(recipe.getRecipeName());
        holder.recipeServings.setText(
                "湯菜種類：" + recipe.getSoup()+
                        "\r\n辣度："+recipe.getHot()+
                        "\r\n熱量："+recipe.getCalories()+"大卡"+
                        "\r\n脂肪："+recipe.getFat()+"g"+
                        "\r\n蛋白质："+recipe.getProtein()+"g");

    }

    @Override
    public int getItemCount(){
            return recipe_resultList.size();
    }

    @Override
    public int getItemViewType(int position){
        List<Favor> favorList= DataSupport.findAll(Favor.class);
        for ( int i=0; i<favorList.size(); i++){
            if (favorList.get(i).getRecipeName().equals(recipe_resultList.get(position).getRecipeName())) {
                state = true;
                break;
            }
            state = false;
        }
        return 0;
    }

}
