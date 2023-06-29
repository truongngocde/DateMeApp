package com.truongngocde.adapter;
 
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.truongngocde.dateme.ChatsFragment;
import com.truongngocde.dateme.FriendsFragment;
import com.truongngocde.dateme.RequestFragment;

public class SectionPagerAdapter extends FragmentPagerAdapter {

    public SectionPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position==0){
            ChatsFragment chatsFragment =new ChatsFragment();
            return chatsFragment;
        }
        else if(position==1){
            FriendsFragment firendsFragment =new FriendsFragment();
            return firendsFragment;
        }
        else if(position==2){
            RequestFragment requestsFragment =new RequestFragment();
            return requestsFragment;
        }
        else return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position){
        if(position==0){
            return "Tin nhắn";
        }
        else if(position==1){
            return "Bạn bè";
        }
        else if(position==2){
            return "Kết bạn";
        }
        else return null;
    }

}
