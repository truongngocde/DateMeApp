package com.truongngocde.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.truongngocde.dateme.R;
import com.truongngocde.models.Friends;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsAdapter extends ArrayAdapter<Friends> {
    public FriendsAdapter(Activity context, ArrayList<Friends> androidFlavors) {
        super(context, 0, androidFlavors);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View ListItemView = convertView;

        if(ListItemView == null) {
            ListItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.friends_list, parent, false);
        }

        // Get the {@link AndroidFlavor} object located at this position in the chatting_list
        Friends currentFriend = getItem(position);

        //define xml components
        CircleImageView FriendImage = (CircleImageView) ListItemView.findViewById(R.id.FriendImage);
        TextView FriendName = (TextView) ListItemView.findViewById(R.id.FriendName);
        TextView FriendDate = (TextView)ListItemView.findViewById(R.id.FriendDate);
        CircleImageView FriendOnlineIcon = (CircleImageView)ListItemView.findViewById(R.id.FriendOnlineIcon);

        //set data
        Picasso.get().load(currentFriend.getFriendImage()).placeholder(R.drawable.user).into(FriendImage);
        FriendName.setText(currentFriend.getFriendName());
        FriendDate.setText(currentFriend.getFriendDate());


        if(currentFriend.isOnline()) {
            FriendOnlineIcon.setImageResource(currentFriend.getFriendOnlineIcon());
            FriendOnlineIcon.setVisibility(View.VISIBLE);
        }
        else{
            FriendOnlineIcon.setVisibility(View.GONE);
        }


        return ListItemView;
    }

}
