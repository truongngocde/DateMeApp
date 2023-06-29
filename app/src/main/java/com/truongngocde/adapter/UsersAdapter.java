package com.truongngocde.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.truongngocde.dateme.R;
import com.truongngocde.models.Users;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends ArrayAdapter<Users> {
    public UsersAdapter(Activity context, ArrayList<Users> androidFlavors) {

        super(context, 0, androidFlavors);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View ListItemView = convertView;

        if(ListItemView == null) {
            ListItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.users_list, parent, false);
        }

        // Get the {@link AndroidFlavor} object located at this position in the chating_list
        Users currentUser = getItem(position);

        //define xml components
        CircleImageView UserImage = (CircleImageView) ListItemView.findViewById(R.id.UserImage);
        TextView UserName = (TextView) ListItemView.findViewById(R.id.UserName);
        TextView UserStatus = (TextView)ListItemView.findViewById(R.id.UserStatus);


        Picasso.get().load(currentUser.getUserImage()).placeholder(R.drawable.user).into(UserImage);
        UserName.setText(currentUser.getUserName());
        UserStatus.setText(currentUser.getUserStatus());

        return ListItemView;
    }

}
