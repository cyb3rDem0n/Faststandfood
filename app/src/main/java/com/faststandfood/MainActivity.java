package com.faststandfood;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    //nav menu
    private static final String SELECTED_ITEM = "arg_selected_item";

    private BottomNavigationView mBottomNav;
    private int mSelectedItem;

    private View user_profile;
    private View response;
    private View rec;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Context context = this;

        user_profile = (RelativeLayout) findViewById(R.id.user_profile_layout);
        response = (RelativeLayout) findViewById(R.id.request);
        rec = (RelativeLayout) findViewById(R.id.rec_layout);

        //nav menu
        mBottomNav = (BottomNavigationView) findViewById(R.id.navigation);
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectFragment(item);
                return true;
            }
        });

        MenuItem selectedItem;
        if (savedInstanceState != null) {
            mSelectedItem = savedInstanceState.getInt(SELECTED_ITEM, 0);
            selectedItem = mBottomNav.getMenu().findItem(mSelectedItem);
        } else {
            selectedItem = mBottomNav.getMenu().getItem(0);
        }
        selectFragment(selectedItem);


    ImageButton button_pizza = (ImageButton) findViewById(R.id.pizza);
    button_pizza.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View v){
            Intent intent = new Intent(context, NearFoodActivity.class);
            intent.putExtra("scelta", "1");
            startActivity(intent);
        }
    });

        ImageButton button_vegani_ = (ImageButton) findViewById(R.id.vegani);
        button_vegani_.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(context, NearFoodActivity.class);
                intent.putExtra("scelta", "2");
                startActivity(intent);
            }
        });

        ImageButton button_carni = (ImageButton) findViewById(R.id.carne_pesce);
        button_carni.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(context, NearFoodActivity.class);
                intent.putExtra("scelta", "3");
                startActivity(intent);
            }
        });

        ImageButton button_altro = (ImageButton) findViewById(R.id.altro);
        button_altro.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(context, NearFoodActivity.class);
                intent.putExtra("scelta", "3");
                startActivity(intent);
            }
        });


        ImageButton button1 = (ImageButton)findViewById(R.id.imageButton_standRev);
        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(context, RecensioneClass.class);
                startActivity(intent);
            }
        });
}

    public void read(View v) {
        Intent read_intent = new Intent(MainActivity.this, ReadData.class);
        startActivity(read_intent);
    }

    // nav menu
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SELECTED_ITEM, mSelectedItem);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        MenuItem homeItem = mBottomNav.getMenu().getItem(0);
        if (mSelectedItem != homeItem.getItemId()) {
            // select home item
            selectFragment(homeItem);
        } else {
            super.onBackPressed();
        }
    }

    private void selectFragment(MenuItem item) {
        Fragment frag = null;
        // init corresponding fragment
        switch (item.getItemId()) {
            case R.id.menu_home:
                frag = MenuFragment.newInstance(getString(R.string.text_home),
                        getColorFromRes(R.color.color_home));
                response.setVisibility(View.VISIBLE);
                user_profile.setVisibility(View.GONE);
                rec.setVisibility(View.GONE);
                break;
            case R.id.user_profile:
                frag = MenuFragment.newInstance(getString(R.string.text_notifications),
                        getColorFromRes(R.color.color_user_profile));
                user_profile.setVisibility(View.VISIBLE);
                response.setVisibility(View.GONE);
                rec.setVisibility(View.GONE);

                break;
            case R.id.review:
                frag = MenuFragment.newInstance(getString(R.string.text_search),
                        getColorFromRes(R.color.color_review));
                response.setVisibility(View.GONE);
                user_profile.setVisibility(View.GONE);
                rec.setVisibility(View.VISIBLE);
                break;
        }

        // update selected item
        mSelectedItem = item.getItemId();

        // uncheck the other items.
        for (int i = 0; i< mBottomNav.getMenu().size(); i++) {
            MenuItem menuItem = mBottomNav.getMenu().getItem(i);
            menuItem.setChecked(menuItem.getItemId() == item.getItemId());
        }

        updateToolbarText(item.getTitle());

        if (frag != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.container, frag, frag.getTag());
            ft.commit();
        }
    }

    private void updateToolbarText(CharSequence text) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(text);
        }
    }

    private int getColorFromRes(@ColorRes int resId) {
        return ContextCompat.getColor(this, resId);
    }

    public void mymap(View v){
        Intent intent = new Intent(this, PositionActivity.class);
        startActivity(intent);
    }
}
