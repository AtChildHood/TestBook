package com.example.hanjun.testbook;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class FirstFragment extends Fragment{
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View root = inflater.inflate(R.layout.firstfragment,container,false);
        return root;
    }

    public void onAttach(Activity activity){
        super.onAttach(activity);
        Toast.makeText(activity, "activity",Toast.LENGTH_SHORT).show();
    }
/*
public void onPause(Activity activity){
super.onPause(activity);
}
*/

    public void onDetach(Activity activity){
        super.onAttach(activity);
        Toast.makeText(activity, "��activity��ɾ��",Toast.LENGTH_SHORT).show();
    }

}
