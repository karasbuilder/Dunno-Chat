package ChatApp.android.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ChatApp.android.R;
import ChatApp.android.databinding.FragmentContactUserBinding;
import ChatApp.android.databinding.FragmentConversationUserBinding;


public class ContactUser extends Fragment {
    FragmentContactUserBinding binding;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding= FragmentContactUserBinding.inflate(inflater,container,false);
        View view=binding.getRoot();
        return view;
    }
}