package com.example.ygl.baking;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ygl.baking.sql.model.Step;

import org.litepal.crud.DataSupport;

import java.util.List;



public class StepFragment extends Fragment implements StepAdapter.ReplaceFragment {
    private static final String TAG = "StepFragment";
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragment_step,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        RecyclerView recyclerView=(RecyclerView)getActivity().findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager=new GridLayoutManager(getActivity(),1);
        recyclerView.setLayoutManager(layoutManager);
        String recipeName=getArguments().getString(getString(R.string.recip_name));
        List<Step> stepList= DataSupport.select("StepTitle","StepId").where("ForRecipe=?",recipeName).find(Step.class);
        StepAdapter stepAdapter;
        if(getArguments().getBoolean(getString(R.string.is_land))){
            //平板电脑横屏模式
            stepAdapter=new StepAdapter(this,stepList);
            recyclerView.setBackgroundColor(getResources().getColor(R.color.white));
        }else {
            stepAdapter=new StepAdapter(stepList);
        }
        recyclerView.setAdapter(stepAdapter);
    }

    @Override
    public void replaceFragment(String stepId){
        DescriptionFragment description=new DescriptionFragment();
        //使用Bundle携带数据
        Bundle fragmentBundle=new Bundle();
        fragmentBundle.putString(getString(R.string.step_id),stepId);
        description.setArguments(fragmentBundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.description,description).commit();
    }
}
