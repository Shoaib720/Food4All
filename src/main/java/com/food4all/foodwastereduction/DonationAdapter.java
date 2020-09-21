package com.food4all.foodwastereduction;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

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
    public void onBindViewHolder(@NonNull final DonationViewHolder holder, int position) {
        final Donation currentDonation = mDonations.get(position);
        Uri imageURI = Uri.parse(currentDonation.getImageFirebaseURL());
        holder.tvDonatedItemName.setText(currentDonation.getItemName());
        holder.food_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passDataToDetailActivity(currentDonation);
            }
        });
        Picasso.get().load(currentDonation.getImageFirebaseURL()).fit().centerCrop().into(holder.ivDonatedItemImage);
        if (currentDonation.getPrice() == Donation.FREE){
            holder.tvDonatedItemPrice.setText("Price: FREE");
        }
        else {
            holder.tvDonatedItemPrice.setText("Price: " + currentDonation.getPrice() + " INR");
        }

    }

    private void passDataToDetailActivity(Donation currentDonation) {
        if (mContext instanceof DonorNavigation){
            Intent intent = new Intent(mContext, FoodItemDonorDetail.class);
            intent.putExtra("title", currentDonation.getItemName());
            intent.putExtra("desc", currentDonation.getDescription());
            intent.putExtra("expDate", currentDonation.getExpiryDate());
            intent.putExtra("imageURL", currentDonation.getImageFirebaseURL());
            intent.putExtra("price", currentDonation.getPrice());
            intent.putExtra("status", currentDonation.getStatus());
            intent.putExtra("receiverEmail", currentDonation.getReceiverEmail());
            mContext.startActivity(intent);
        }else if (mContext instanceof BeneficiaryNavigation){
            Intent intent = new Intent(mContext, FoodItemBeneficiaryDetail.class);
            intent.putExtra("title", currentDonation.getItemName());
            intent.putExtra("desc", currentDonation.getDescription());
            intent.putExtra("expDate", currentDonation.getExpiryDate());
            intent.putExtra("imageURL", currentDonation.getImageFirebaseURL());
            intent.putExtra("price", currentDonation.getPrice());
            intent.putExtra("status", currentDonation.getStatus());
            intent.putExtra("receiverEmail", currentDonation.getReceiverEmail());
            intent.putExtra("donorEmail", currentDonation.getDonorEmail());
            intent.putExtra("itemID", currentDonation.getItemID());
            System.out.println(currentDonation.getItemID());
            mContext.startActivity(intent);
        }


    }

    @Override
    public int getItemCount() {
        return mDonations.size();
    }

    public class DonationViewHolder extends RecyclerView.ViewHolder{

        private CardView food_item;
        public TextView tvDonatedItemName, tvDonatedItemPrice;
        public ImageView ivDonatedItemImage;


        public DonationViewHolder(@NonNull View itemView) {
            super(itemView);
            food_item = (CardView) itemView.findViewById(R.id.food_item_id);
            tvDonatedItemName = itemView.findViewById(R.id.card_item_name);
            tvDonatedItemPrice = itemView.findViewById(R.id.card_item_price);
            ivDonatedItemImage = itemView.findViewById(R.id.card_item_image);

        }
    }
}
