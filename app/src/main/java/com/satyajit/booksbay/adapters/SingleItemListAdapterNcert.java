package com.satyajit.booksbay.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.satyajit.booksbay.R;
import com.satyajit.booksbay.activity.NcertBookReadPage;

import java.util.ArrayList;

public class SingleItemListAdapterNcert extends RecyclerView.Adapter<SingleItemListAdapterNcert.MyViewHolder> {
    private ArrayList<DummyParentDataItem> dummyParentDataItems;
    String cls, subject, parent;

    public SingleItemListAdapterNcert(ArrayList<DummyParentDataItem> dummyParentDataItems) {
        this.dummyParentDataItems = dummyParentDataItems;
    }

    @Override
    public SingleItemListAdapterNcert.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_parent_child_listing, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SingleItemListAdapterNcert.MyViewHolder holder, int position) {
        DummyParentDataItem dummyParentDataItem = dummyParentDataItems.get(position);
        holder.textView_parentName.setText(dummyParentDataItem.getParentName());
        subject = dummyParentDataItem.getParentName();
        cls = dummyParentDataItem.getCls();



        int noOfChildTextViews = holder.linearLayout_childItems.getChildCount();
        for (int index = 0; index < noOfChildTextViews; index++) {
            TextView currentTextView = (TextView) holder.linearLayout_childItems.getChildAt(index);
            currentTextView.setVisibility(View.VISIBLE);
        }

        int noOfChild = dummyParentDataItem.getChildDataItems().size();
        if (noOfChild < noOfChildTextViews) {
            for (int index = noOfChild; index < noOfChildTextViews; index++) {
                TextView currentTextView = (TextView) holder.linearLayout_childItems.getChildAt(index);
                currentTextView.setVisibility(View.GONE);
            }
        }
        for (int textViewIndex = 0; textViewIndex < noOfChild; textViewIndex++) {
            TextView currentTextView = (TextView) holder.linearLayout_childItems.getChildAt(textViewIndex);
            currentTextView.setText(dummyParentDataItem.getChildDataItems().get(textViewIndex).getChildName());
                /*currentTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(mContext, "" + ((TextView) view).getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                });*/
        }
    }

    @Override
    public int getItemCount() {
        return dummyParentDataItems.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context context;
        private TextView textView_parentName;
        private LinearLayout linearLayout_childItems, clicker;
        private ImageView arw_drop;


        MyViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            textView_parentName = itemView.findViewById(R.id.tv_parentName);
            clicker = itemView.findViewById(R.id.clicker);
            linearLayout_childItems = itemView.findViewById(R.id.ll_child_items);
            arw_drop = itemView.findViewById(R.id.img_icon);
            linearLayout_childItems.setVisibility(View.GONE);

            Typeface Cav = Typeface.createFromAsset(context.getAssets(),"fonts/cav.ttf");

            int intMaxNoOfChild = 0;
            for (int index = 0; index < dummyParentDataItems.size(); index++) {
                int intMaxSizeTemp = dummyParentDataItems.get(index).getChildDataItems().size();
                if (intMaxSizeTemp > intMaxNoOfChild) intMaxNoOfChild = intMaxSizeTemp;
            }
            for (int indexView = 0; indexView < intMaxNoOfChild; indexView++) {

                TextView textView = new TextView(context);
                textView.setTypeface(Cav);
                textView.setTextSize(20);
                textView.setId(indexView);
                textView.setPadding(60, 35, 0, 35);
                textView.setGravity(Gravity.START);
                TypedValue outValue = new TypedValue();
                context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground,outValue, true);
                textView.setBackgroundResource(outValue.resourceId);
                textView.setClickable(true);
                textView.setFocusable(true);

              textView.setBackground(ContextCompat.getDrawable(context, R.drawable.background_sub_module_text));

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                textView.setOnClickListener(view -> {
                    if (view.getId() == R.id.tv_parentName) {
                        if (linearLayout_childItems.getVisibility() == View.VISIBLE) {
                            linearLayout_childItems.setVisibility(View.GONE);

                            arw_drop.setColorFilter(context.getResources().getColor(R.color.arw_clr));
                            arw_drop.animate().setDuration(200).rotation(0);


                        } else {
                            linearLayout_childItems.setVisibility(View.VISIBLE);
                            arw_drop.animate().setDuration(200).rotation(180);
                            arw_drop.setColorFilter(context.getResources().getColor(R.color.colorPrimary));

                        }
                    } else {
                        TextView textViewClicked = (TextView) view;
                        String temp = "NCERT/"+cls.replace(' ','_')+"_"+textView_parentName.getText()+"_"+textViewClicked.getText();
                        context.startActivity(new Intent(context, NcertBookReadPage.class)

                                .putExtra("BOOK_NAME",textViewClicked.getText().toString())
                                .putExtra("IMG_NAME",temp+".jpg")
                                .putExtra("FILE_LOC",temp)

                        );

                        //Toast.makeText(context, "" + textViewClicked.getText().toString()+" "+cls, Toast.LENGTH_SHORT).show();

                    }
                });
                linearLayout_childItems.addView(textView, layoutParams);
            }
            clicker.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

                if (linearLayout_childItems.getVisibility() == View.VISIBLE) {
                    linearLayout_childItems.setVisibility(View.GONE);

                    arw_drop.setColorFilter(context.getResources().getColor(R.color.arw_clr));
                    arw_drop.animate().setDuration(200).rotation(0);

                } else {
                    linearLayout_childItems.setVisibility(View.VISIBLE);
                    arw_drop.setColorFilter(context.getResources().getColor(R.color.colorPrimary));
                    arw_drop.animate().setDuration(200).rotation(180);

                }

        }
    }
}