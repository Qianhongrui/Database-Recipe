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
import com.example.ygl.baking.sql.model.Recipe;

import java.util.List;


public class Recipe_hateAdapter extends RecyclerView.Adapter<Recipe_hateAdapter.ViewHolder> {
    private Context mContext;
    private List<Blacklist> recipeList;
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CardView cardView;
        ImageView recipeImageView;
        TextView recipeName;
        TextView recipeServings;
        ViewHolder(View view){
            super(view);
            cardView=(CardView) view;
            recipeImageView=(ImageView)view.findViewById(R.id.recipe_image_view);
            recipeName=(TextView)view.findViewById(R.id.recipe_name);
            recipeServings=(TextView)view.findViewById(R.id.recipe_servings);
            View cancel = view.findViewById(R.id.cancel_hate);
            cancel.setOnClickListener((View.OnClickListener) this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.cancel_hate){
                Toast.makeText(v.getContext(), "已移出黑名單",Toast.LENGTH_SHORT).show();

                Recipe recipe = new Recipe();
                recipe.setRecipeId(recipeList.get(getLayoutPosition()).getRecipeId());
                recipe.setImageUrl(recipeList.get(getLayoutPosition()).getImageUrl());
                recipe.setRecipeName(recipeList.get(getLayoutPosition()).getRecipeName());
                recipe.setServings(recipeList.get(getLayoutPosition()).getServings());
                recipe.setCalories(recipeList.get(getLayoutPosition()).getCalories());
                recipe.setCarbohydrates(recipeList.get(getLayoutPosition()).getCarbohydrates());
                recipe.setFat(recipeList.get(getLayoutPosition()).getFat());
                recipe.setHot(recipeList.get(getLayoutPosition()).getHot());
                recipe.setPeople(recipeList.get(getLayoutPosition()).getPeople());
                recipe.setProtein(recipeList.get(getLayoutPosition()).getProtein());
                recipe.setSodium(recipeList.get(getLayoutPosition()).getSodium());
                recipe.setSoup(recipeList.get(getLayoutPosition()).getSoup());
                recipe.setVegetarian_food(recipeList.get(getLayoutPosition()).getVegetarian_food());
                recipe.save();

                recipeList.get(getAdapterPosition()).delete();
                recipeList.remove(getAdapterPosition());

                notifyItemRemoved(getAdapterPosition());
            }
        }
    }

    public Recipe_hateAdapter(List<Blacklist> list, View emptyPrompt){
        recipeList=list;
        emptyPrompt.setVisibility(list.size()==0?View.VISIBLE:View.GONE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType){
        if (mContext==null){
            mContext=parent.getContext();
        }
        View view= LayoutInflater.from(mContext).inflate(R.layout.item_recipe_hate,parent,false);
        final ViewHolder viewHolder=new ViewHolder(view);
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击事件
                Intent intent=new Intent(mContext,StepActivity.class);
                intent.putExtra(mContext.getString(R.string.recip_name),
                        recipeList.get(viewHolder.getLayoutPosition()).getRecipeName());
                mContext.startActivity(intent);
            }
        });
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){
        Blacklist recipe=recipeList.get(position);
        if (TextUtils.isEmpty(recipe.getImageUrl())){
            Glide.with(mContext).load(R.drawable.logo_black).into(holder.recipeImageView);
        }else {
            //加载网络图片
            Glide.with(mContext).load(recipe.getImageUrl()).into(holder.recipeImageView);
        }
        holder.recipeName.setText(recipe.getRecipeName());
        holder.recipeServings.setText("Serving: " + recipe.getServings());
        holder.recipeServings.setText(
                "湯菜種類：" + recipe.getSoup()+
                        "\r\n辣度："+recipe.getHot()+
                        "\r\n熱量："+recipe.getCalories()+"大卡"+
                        "\r\n脂肪："+recipe.getFat()+"g"+
                        "\r\n蛋白质："+recipe.getProtein()+"g");
    }

    @Override
    public int getItemCount(){return recipeList.size();}
}
