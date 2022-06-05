package ChatApp.android.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ChatApp.android.R;
import ChatApp.android.databinding.FragmentGroupsContactBinding;


public class GroupsContact extends Fragment {

   FragmentGroupsContactBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       binding=FragmentGroupsContactBinding.inflate(inflater,container,false);
       View view=binding.getRoot();
        return view;
    }
}