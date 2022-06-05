package ChatApp.android.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import ChatApp.android.Adapters.SectionContactAdapter;
import ChatApp.android.R;
import ChatApp.android.databinding.FragmentContactUserBinding;
import ChatApp.android.databinding.FragmentConversationUserBinding;


public class ContactUser extends Fragment {
    private FragmentContactUserBinding binding;
    private  ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private SectionContactAdapter viewPagerAdapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding= FragmentContactUserBinding.inflate(inflater,container,false);
        View view=binding.getRoot();
        tabLayout=binding.tabContact;
        viewPager2=binding.contactViewpager;

        viewPagerAdapter=new SectionContactAdapter(this);
        viewPager2.setAdapter(viewPagerAdapter);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position){
                case 0:
                    tab.setText("Friends");
                    break;
                case 1:
                    tab.setText("Groups");
                    break;

            }
        }).attach();


        return view;
    }
}