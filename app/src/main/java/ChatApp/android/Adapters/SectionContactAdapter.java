package ChatApp.android.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import androidx.viewpager2.adapter.FragmentStateAdapter;

import ChatApp.android.Fragments.FriendsContact;
import ChatApp.android.Fragments.GroupsContact;

public class SectionContactAdapter extends FragmentStateAdapter {


    public SectionContactAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return  new FriendsContact();
            case 1:
                return new GroupsContact();
            default:
                return new FriendsContact();

        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
