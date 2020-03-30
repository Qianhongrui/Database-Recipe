package com.example.ygl.baking.Preference;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ygl.baking.CardActivity;
import com.example.ygl.baking.R;
import com.zhouyou.view.seekbar.SignSeekBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.ygl.baking.CardActivity.fab;
import static org.litepal.LitePalApplication.getContext;

public class CardPagerAdapter extends PagerAdapter implements CardAdapter {

    private List<CardView> mViews;
    private List<CardItem> mData;
    private float mBaseElevation;
    public static ImageView image;
    public static ImageView fanye;
    public static TextView titleTextView;
    public static CheckBox b1;
    public static CheckBox b2;
    public static CheckBox b3;
    public static CheckBox b4;
    public static SignSeekBar signSeekBar1;
    private static StateSQLiteOpenHelper helper;
    private static SQLiteDatabase db;

    private Context context;


    public CardPagerAdapter() {
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
    }

    public void addCardItem(CardItem item) {
        mViews.add(null);
        mData.add(item);
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.preference_card_adapter, container, false);
        container.addView(view);
        bind(mData.get(position), view);
        CardView cardView = (CardView) view.findViewById(R.id.cardView);

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    private void bind(CardItem item, final View view) {
        image = (ImageView) view.findViewById(R.id.preference_imageView);
        fanye = (ImageView) view.findViewById(R.id.fanye);
        fanye.bringToFront();
        titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        titleTextView.setText(item.getTitle());
        b1 = view.findViewById(R.id.checkbox1);
        b2 = view.findViewById(R.id.checkbox2);
        b3 = view.findViewById(R.id.checkbox3);
        b4 = view.findViewById(R.id.checkbox4);
        signSeekBar1 = (SignSeekBar) view.findViewById(R.id.seekbar1);



        helper = new StateSQLiteOpenHelper(getContext());


        //TextView contentTextView = (TextView) view.findViewById(R.id.contentTextView);
        //View contentTextView = (TextView) view.findViewById(R.id.contentTextView);

        if (item.getImage() == 1) {
            image.setImageResource(R.drawable.meat_vegetable);
            b1.setText("葷菜");
            b2.setText("素菜");

            signSeekBar1.setVisibility(View.GONE);
            b3.setVisibility(View.GONE);
            b4.setVisibility(View.GONE);

            if (getData("葷菜") == 1) b1.setChecked(true);
            if (getData("葷菜") == 0) b1.setChecked(false);
            if (getData("素菜") == 1) b2.setChecked(true);
            if (getData("素菜") == 0) b2.setChecked(false);


            b1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int state;
                    if (isChecked) {
                        state = 1;
                        Log.i("葷菜", "Yes");
                        b2.setClickable(true);
                    } else {
                        state = 0;
                        Log.i("葷菜", "No");
                        Toast.makeText(view.getContext(), "請至少勾選一項", Toast.LENGTH_LONG).show();
                        b2.setEnabled(false);
                        b2.setClickable(false);
                    }
                    updateData("葷菜", state);
                    updateData("變化",1);
                    Log.i("變化??????????????", String.valueOf(getData("變化")));
                    fab.setVisibility(View.VISIBLE);
                }
            });
            b2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int state;
                    if (isChecked) {
                        state = 1;
                        Log.i("素菜", "Yes");
                        updateData("變化",0);
                    } else {
                        state = 0;
                        Log.i("素菜", "No");
                        updateData("變化",1);
                    }
                    updateData("素菜", state);
                    updateData("變化",1);
                    Log.i("變化??????????????", String.valueOf(getData("變化")));
                    fab.setVisibility(View.VISIBLE);
                }
            });
        } else if (item.getImage() == 2) {
            image.setImageResource(R.drawable.pepper);
            b3.setText("你要不要吃辣der");
            b4.setText("不辣");
            b4.setVisibility(View.GONE);
            b1.setVisibility(View.GONE);
            b2.setVisibility(View.GONE);
            signSeekBar1.setVisibility(View.GONE);

            if (getData("辣") == 1) {
                b3.setChecked(true);
                //b3.setFocusable(true);
            }
            if (getData("辣") == 0) {
                b3.setChecked(false);
                //b3.setFocusable(false);
            }
            if (getData("不辣") == 1) {
                b4.setChecked(true);
                //b4.setFocusable(true);
            }
            if (getData("不辣") == 0) {
                b4.setChecked(false);
                //b4.setFocusable(false);
            }


            /*if (b1.isChecked()) b1.setChecked(true);
            else b1.setChecked(false);
            if (b2.isChecked()) b2.setChecked(true);
            else b2.setChecked(false);*/
            b3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int state;
                    if (isChecked) {
                        state = 1;
                        Log.i("辣", "Yes");
                    } else {
                        state = 0;
                        Log.i("辣", "No");
                    }
                    updateData("辣", state);
                    updateData("變化",1);
                    Log.i("變化??????????????", String.valueOf(getData("變化")));
                    fab.setVisibility(View.VISIBLE);
                }
            });
            b4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int state;
                    if (isChecked) {
                        state = 1;
                        Log.i("不辣", "Yes");
                    } else {
                        state = 0;
                        Log.i("不辣", "No");
                    }
                    updateData("不辣", state);
                    updateData("變化",1);
                    fab.setVisibility(View.VISIBLE);
                }
            });
        } else if (item.getImage() == 3) {
            image.setImageResource(R.drawable.soup_dish);
            b1.setVisibility(View.GONE);
            b2.setVisibility(View.GONE);
            b3.setVisibility(View.GONE);
            b4.setVisibility(View.GONE);
            SetSeekBar1(view);
        } else if (item.getImage() == 4) {
            image.setImageResource(R.drawable.number);
            b1.setVisibility(View.GONE);
            b2.setVisibility(View.GONE);
            b3.setVisibility(View.GONE);
            b4.setVisibility(View.GONE);
            SetSeekBar2(view);
            fanye.setVisibility(View.GONE);
        }

        //contentTextView.setText(item.getText());

    }

    private void SetSeekBar1(View view) {
        final TextView progressText = (TextView) view.findViewById(R.id.demo_5_progress_text_2);

        signSeekBar1.getConfigBuilder()
                .min(0)
                .max(4)
                .progress(getData("菜數")-1)
                .sectionCount(4)
                .trackColor(ContextCompat.getColor(getContext(), R.color.gray))
                .secondTrackColor(ContextCompat.getColor(getContext(), R.color.colorAccent))
                .thumbColor(ContextCompat.getColor(getContext(), R.color.colorAccent))
                .sectionTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                .sectionTextSize(12)
                //.thumbTextColor(ContextCompat.getColor(getContext(), R.color.color_red))
                //.thumbTextSize(18)
                //.signColor(ContextCompat.getColor(getContext(), R.color.color_green))
                //.signTextSize(18)
                .sectionTextPosition(SignSeekBar.TextPosition.BELOW_SECTION_MARK)
                .build();
        //signSeekBar.setEnabled(false);//设置不可以用的时候，可以设置ssb_unusable_color不可用颜色
        signSeekBar1.setOnProgressChangedListener(new SignSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(SignSeekBar signSeekBar, int progress, float progressFloat, boolean fromUser) {
            }

            @Override
            public void getProgressOnActionUp(SignSeekBar signSeekBar, int progress, float progressFloat) {
                String s = String.format(Locale.TAIWAN, "我选择%d道菜", progress + 1);
                progressText.setText(s);
                fab.setVisibility(View.VISIBLE);
            }

            @Override
            public void getProgressOnFinally(SignSeekBar signSeekBar, int progress, float progressFloat, boolean fromUser) {
                String s = String.format(Locale.TAIWAN, "我选择%d道菜", progress + 1);
                updateData("菜數", progress+1);
                Log.i("人數??????????????", String.valueOf(progress+1));
                updateData("變化",1);
                Log.i("變化??????????????", String.valueOf(getData("變化")));
                progressText.setText(s /*+ getContext().getResources().getStringArray(R.array.labels)[progress]*/);
            }
        });
    }

    private void SetSeekBar2(View view) {
        final TextView progressText = (TextView) view.findViewById(R.id.demo_5_progress_text_2);

        signSeekBar1.getConfigBuilder()
                .min(0)
                .max(4)
                .progress(getData("人數")-1)
                .sectionCount(4)
                .trackColor(ContextCompat.getColor(getContext(), R.color.gray))
                .secondTrackColor(ContextCompat.getColor(getContext(), R.color.colorAccent))
                .thumbColor(ContextCompat.getColor(getContext(), R.color.colorAccent))
                .sectionTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                .sectionTextSize(12)
                //.thumbTextColor(ContextCompat.getColor(getContext(), R.color.color_red))
                //.thumbTextSize(18)
                //.signColor(ContextCompat.getColor(getContext(), R.color.color_green))
                //.signTextSize(18)
                .sectionTextPosition(SignSeekBar.TextPosition.BELOW_SECTION_MARK)
                .build();
        //signSeekBar.setEnabled(false);//设置不可以用的时候，可以设置ssb_unusable_color不可用颜色
        signSeekBar1.setOnProgressChangedListener(new SignSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(SignSeekBar signSeekBar, int progress, float progressFloat, boolean fromUser) {
            }

            @Override
            public void getProgressOnActionUp(SignSeekBar signSeekBar, int progress, float progressFloat) {
                String s = String.format(Locale.TAIWAN, "有%d人一起享用", progress + 1);
                progressText.setText(s);
                fab.setVisibility(View.VISIBLE);
            }

            @Override
            public void getProgressOnFinally(SignSeekBar signSeekBar, int progress, float progressFloat, boolean fromUser) {
                String s = String.format(Locale.TAIWAN, "有%d人一起享用", progress + 1);
                updateData("人數", progress+1);
                Log.i("人數??????????????", String.valueOf(progress+1));
                updateData("變化",1);
                Log.i("變化??????????????", String.valueOf(getData("變化")));
                progressText.setText(s /*+ getContext().getResources().getStringArray(R.array.labels)[progress]*/);
            }
        });
    }


    private static boolean hasData(String tempName) {
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                //"select id as _id,name from records where name =?", new String[]{tempName});
                "select state ,state_name from states where state_name =?", new String[]{tempName});
        //  判断是否有下一个
        return cursor.moveToNext();
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
