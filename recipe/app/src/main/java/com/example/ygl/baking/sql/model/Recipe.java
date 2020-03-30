package com.example.ygl.baking.sql.model;

        import org.litepal.crud.DataSupport;


//litepal需要的模型
public class Recipe extends DataSupport {
    private String RecipeId;
    private String RecipeName;
    private String Servings;
    private String ImageUrl;
    public String Vegetarian_food;
    public String Hot;
    public String Soup;
    public int People;
    public int Calories;
    public int Carbohydrates;
    public int Protein;
    public int Fat;
    public int Sodium;

    public String getRecipeId() {
        return RecipeId;
    }

    public void setRecipeId(String recipeId) {
        RecipeId = recipeId;
    }

    public String getRecipeName() {
        return RecipeName;
    }

    public void setRecipeName(String recipeName) {
        RecipeName = recipeName;
    }

    public String getServings() {
        return Servings;
    }

    public void setServings(String servings) {
        Servings = servings;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getVegetarian_food() { return "素菜"; }

    public void setVegetarian_food(String vegetarian_food) {
        Vegetarian_food = vegetarian_food;
    }

    public String getHot() { return Hot; }

    public void setHot(String hot) {
        Hot = hot;
    }

    public String getSoup() { return "菜"; }

    public void setSoup(String soup) {
        Soup = soup;
    }

    public int getPeople() {
        return People;
    }

    public void setPeople(int people) {
        People = people;
    }

    public int getCalories() {
        return Calories;
    }

    public void setCalories(int calories) {
        Calories = calories;
    }

    public int getCarbohydrates() {
        return Carbohydrates;
    }

    public void setCarbohydrates(int carbohydrates) {
        Carbohydrates = carbohydrates;
    }

    public int getProtein() {
        return Protein;
    }

    public void setProtein(int protein) {
        Protein = protein;
    }

    public int getFat() {
        return Fat;
    }

    public void setFat(int fat) {
        Fat = fat;
    }

    public int getSodium() {
        return Sodium;
    }

    public void setSodium(int sodium) {
        Sodium = sodium;
    }

}
