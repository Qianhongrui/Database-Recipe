package com.example.ygl.baking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.example.ygl.baking.sql.model.Favor;

import java.util.List;



public class Recipe_favorAdapter extends RecyclerView.Adapter<Recipe_favorAdapter.ViewHolder> {
    private Context mContext;
    private List<Favor> recipeList;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardView;
        ImageView recipeImageView;
        TextView recipeName;
        TextView recipeServings;

        ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            recipeImageView = (ImageView) view.findViewById(R.id.recipe_image_view);
            recipeName = (TextView) view.findViewById(R.id.recipe_name);
            recipeServings = (TextView) view.findViewById(R.id.recipe_servings);
            View cancel = view.findViewById(R.id.cancel_favor);
            cancel.setOnClickListener((View.OnClickListener) this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.cancel_favor) {
                Toast.makeText(v.getContext(), "已取消收藏", Toast.LENGTH_SHORT).show();

                //Favor.delete(recipeList,recipeList.get(getAdapterPosition());
                //Favor.delete(recipeList,recipeList.get(getAdapterPosition());
                /*DataSupport.delete(Favor.class,getAdapterPosition());
                DataSupport.saveAllAsync(recipeList);*/
                recipeList.get(getAdapterPosition()).delete();
                recipeList.remove(getAdapterPosition());

                notifyItemRemoved(getAdapterPosition());

            }
        }
    }


    public Recipe_favorAdapter(List<Favor> list, View emptyPrompt) {
        recipeList = list;
        emptyPrompt.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recipe_favor, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击事件
                Intent intent = new Intent(mContext, StepActivity.class);
                intent.putExtra(mContext.getString(R.string.recip_name),
                        recipeList.get(viewHolder.getAdapterPosition()).getRecipeName());
                mContext.startActivity(intent);
            }
        });
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Favor recipe = recipeList.get(position);
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
}
