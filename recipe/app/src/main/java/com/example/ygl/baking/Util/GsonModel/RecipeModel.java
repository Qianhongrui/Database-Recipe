package com.example.ygl.baking.Util.GsonModel;

import java.util.List;



public class RecipeModel {
    public String id;
    public String name;
    public List<IngredientsModel> ingredients;
    public List<StepsModel> steps;
    public String servings;
    public String image;
    public String vegetarian_food;
    public String hot;
    public String soup;
    public int people;
    public int calories;
    public int carbohydrates;
    public int protein;
    public int fat;
    public int sodium;

    public static class IngredientsModel {
        public String quantity;
        public String measure;
        public String ingredient;
        public String price;
    }
    public static class StepsModel {
        public int id;
        public String shortDescription;
        public String description;
        public String videoURL;
        public String thumbnailURL;
    }
}
