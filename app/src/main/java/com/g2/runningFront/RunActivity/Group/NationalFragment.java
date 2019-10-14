package com.g2.runningFront.RunActivity.Group;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.g2.runningFront.R;

import java.util.ArrayList;
import java.util.List;


public class NationalFragment extends Fragment {


    private Activity activity;
    private RecyclerView recyclerView;
    int flag = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_national, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<Friend> friends = getFriends();
        recyclerView = view.findViewById(R.id.gp_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(new FriendAdapter(activity, friends));

        Button btFrist=view.findViewById(R.id.gp_btAll);

        btFrist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).popBackStack();
            }
        });

    }

    private class FriendAdapter extends RecyclerView.Adapter {                                      //把RecyclerView.Adapter圈起來按command+1//前2個選Conyext型別,之esc
        Context context;
        List<Friend> friends;

        public FriendAdapter(Context context, List<Friend> friends) {
            this.context = context;
            this.friends = friends;
        }

        @Override
        public int getItemCount() {
            return friends.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView,ic_lovered;
            TextView tvName, tvPhone;
            Button btyes;



            public MyViewHolder(View itemView) {                                                     //botton 追蹤or未追蹤
                super(itemView);
                imageView = itemView.findViewById(R.id.gp_ivFriend);
                tvName = itemView.findViewById(R.id.gp_tvFriend);
                tvPhone = itemView.findViewById(R.id.gp_tvKm);
                btyes = itemView.findViewById(R.id.btyes);
                ic_lovered= itemView.findViewById(R.id.gp_ivHeart);


                ic_lovered.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (flag){
                           case 0:
                              ic_lovered.setImageResource(R.drawable.ic_lovered);
                                flag = 1;

                               break;
                           case 1:
                               ic_lovered.setImageResource(R.drawable.ic_loveblack);
                               flag = 0;

                               break;
                       }
                    }
                });

            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


            View itemView = LayoutInflater.from(context)
                    .inflate(R.layout.itern_view_group2, parent, false);
            return new MyViewHolder(itemView);
        }


        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final Friend friend = friends.get(position);
            FriendAdapter.MyViewHolder viewHolder = (FriendAdapter.MyViewHolder) holder;
            viewHolder.imageView.setImageResource(friend.getImageId());
            viewHolder.tvName.setText(friend.getName());
            viewHolder.tvPhone.setText(friend.getPhone());
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("friend", friend);
                    //Navigation.findNavController(v)
                            //.navigate(R.id.action_nationalFragment_to_friendFragment, bundle);
                }
            });
        }
    }


    private List<Friend> getFriends() {
        List<Friend> friends = new ArrayList<>();
        friends.add(new Friend(R.drawable.p01, "John","03-1111111","2:30:14","15.2km","6'15","4500","81","2019/10/01"));
        friends.add(new Friend(R.drawable.p02, "Jack","03-12345678","2:19:17","12.33km","7'12","4900","156","2010/10/01"));
        friends.add(new Friend(R.drawable.p03, "Mark","01-12345678",",2:13:13","13.44km","3'44","1200","65","2019/10/01"));
        friends.add(new Friend(R.drawable.p04, "Ben","02-12345678","2:10:13","15.44km","5'44","1150","53","2019/10/01"));
        friends.add(new Friend(R.drawable.p05, "James","03-12345678","2:17:13","13.34km","3'44","1120","5","2019/10/01"));
        friends.add(new Friend(R.drawable.p06, "David","04-12345678","2:43:13","17.74km","7'44","1420","53","2019/10/01"));
        friends.add(new Friend(R.drawable.p07, "Ken","05512345678","2:33:13","17.44km","7'34","1340","511","2019/10/01"));
        friends.add(new Friend(R.drawable.p08, "Ron","03-4247725627","2:43:13","13.34km","3'44","1530","58","2019/10/01"));
        friends.add(new Friend(R.drawable.p09, "Jerry","03-26726726","2:13:13","15.14km","4'34","1130","511","2019/10/01"));
        friends.add(new Friend(R.drawable.p10, "Maggie","03-662635846","2:13:13","15.14km","4'34","1130","35","2019/10/01"));
        friends.add(new Friend(R.drawable.p11, "Sue","03-248525276872","2:13:13","15.14km","4'34","1130","82","2019/10/01"));
        friends.add(new Friend(R.drawable.p12, "Cathy","03-24762875827","2:13:13","15.14km","4'34","1130","578","2019/10/01"));
        return friends;
    }
}


