package ChatApp.android.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ChatApp.android.R;
import ChatApp.android.databinding.FragmentFriendsContactBinding;


public class FriendsContact extends Fragment {


   FragmentFriendsContactBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      binding=FragmentFriendsContactBinding.inflate(inflater,container,false);
      View view=binding.getRoot();
      return view;
    }
}