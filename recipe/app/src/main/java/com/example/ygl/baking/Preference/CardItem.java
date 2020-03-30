package com.example.ygl.baking.Preference;


public class CardItem {
    private int mTitleResource;
    private int mImage;
    public CardItem(int title, int image) {
        mTitleResource = title;
        mImage = image;
    }


    public int getImage() {
        return mImage;
    }

    public int getTitle() {
        return mTitleResource;
    }
}
