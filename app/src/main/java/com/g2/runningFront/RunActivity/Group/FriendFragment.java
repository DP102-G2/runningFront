package com.g2.runningFront.RunActivity.Group;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.g2.runningFront.R;
import com.g2.runningFront.RunActivity.Group.Friend;


public class FriendFragment extends Fragment {


    private Activity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle(R.string.textFriend);
        return inflater.inflate(R.layout.fragment_friend, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView imageView = view.findViewById(R.id.imageView);
        TextView tvId = view.findViewById(R.id.tvId);
        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvtotal_time = view.findViewById(R.id.tvtotal_time);
        TextView tvtotal_distance = view.findViewById(R.id.tvtotal_distance);
        TextView tvspeed = view.findViewById(R.id.tvspeed);
        TextView tvcalories = view.findViewById(R.id.tvcalories);
        TextView tvlove = view.findViewById(R.id.tvlove);
        TextView tvjoin_time = view.findViewById(R.id.tvjoin_time);


        // 取得Bundle物件
        Bundle bundle = getArguments();
        // 如果Bundle不為null，進一步取得Friend物件
        Friend friend = bundle == null ? null : (Friend) bundle.getSerializable("friend");
        // 如果Friend物件不為null，顯示各個屬性值，否則顯示錯誤訊息
        if (friend != null) {
            imageView.setImageResource(friend.getImageId());
            tvId.setText(String.valueOf(friend.getPhone()));
            tvName.setText(friend.getName());
            tvtotal_time.setText(friend.getTotal_time());
            tvtotal_distance.setText(friend.getTotal_distance());
            tvspeed.setText(friend.getSpeed());
            tvcalories.setText(friend.getCalories());
            tvlove.setText(friend.getLove());
            tvjoin_time.setText(friend.getJoin_time());
        }

    }

}