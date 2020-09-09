package com.food4all.foodwastereduction;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.List;

public class DonationAdapter extends RecyclerView.Adapter<DonationAdapter.DonationViewHolder> {

    private Context mContext;
    private List<Donation> mDonations;

    public DonationAdapter(Context context, List<Donation> donations){
        mContext = context;
        mDonations = donations;
    }

    @NonNull
    @Override
    public DonationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.donation_card_item, parent, false);
        return new DonationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DonationViewHolder holder, int position) {
        Donation currentDonation = mDonations.get(position);
        Uri imageURI = Uri.parse(currentDonation.getImageFirebaseURL());
        holder.tvDonatedItemName.setText(currentDonation.getItemName());
        Picasso.get().load(currentDonation.getImageFirebaseURL()).fit().centerCrop().into(holder.ivDonatedItemImage);
        if (currentDonation.getPrice() == Donation.FREE){
            holder.tvDonatedItemPrice.setText("Price: FREE");
        }
        else {
            holder.tvDonatedItemPrice.setText("Price: " + currentDonation.getPrice() + " INR");
        }

    }

    @Override
    public int getItemCount() {
        return mDonations.size();
    }

    public class DonationViewHolder extends RecyclerView.ViewHolder{

        public TextView tvDonatedItemName, tvDonatedItemPrice;
        public ImageView ivDonatedItemImage;

        public DonationViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDonatedItemName = itemView.findViewById(R.id.card_item_name);
            tvDonatedItemPrice = itemView.findViewById(R.id.card_item_price);
            ivDonatedItemImage = itemView.findViewById(R.id.card_item_image);
        }
    }
}
