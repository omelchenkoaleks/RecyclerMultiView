package com.omelchenkoaleks.recyclermultiview.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.Utils;
import com.omelchenkoaleks.recyclermultiview.R;
import com.omelchenkoaleks.recyclermultiview.interfaces.ItemClickListener;
import com.omelchenkoaleks.recyclermultiview.model.Item;

import java.util.List;

// для макета без наследника
class MyViewHolderWithoutChild extends RecyclerView.ViewHolder
        implements View.OnClickListener {
    public TextView mTextView;
    ItemClickListener mItemClickListener;

    public MyViewHolderWithoutChild(@NonNull View itemView) {
        super(itemView);

        mTextView = itemView.findViewById(R.id.text_view_tv);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        mItemClickListener.onClick(view, getAdapterPosition(), false);
    }
}

// для макета с наследником
class MyViewHolderWithChild extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    public TextView mTextView;
    public TextView mTextViewChild;
    public RelativeLayout mButtonRelativeLayout;
    public ExpandableLinearLayout mExpandableLayout;

    ItemClickListener mItemClickListener;

    public MyViewHolderWithChild(@NonNull View itemView) {
        super(itemView);

        mTextView = itemView.findViewById(R.id.text_view_tv);
        mTextViewChild = itemView.findViewById(R.id.text_view_child_tv);
        mButtonRelativeLayout = itemView.findViewById(R.id.button);
        mExpandableLayout = itemView.findViewById(R.id.expandable_layout_el);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        mItemClickListener.onClick(view, getAdapterPosition(), false);
    }
}


public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Item> mItems;
    Context mContext;
    SparseBooleanArray mExpandState = new SparseBooleanArray();

    public MyAdapter(List<Item> items) {
        mItems = items;
        for (int i = 0; i < mItems.size(); i++)
            mExpandState.append(i, false);
    }

    @Override
    public int getItemViewType(int position) {
        if (mItems.get(position).isExpandable())
            return 1;
        else
            return 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        if (viewType == 0) { // Without item
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            View view = layoutInflater.inflate(
                    R.layout.layout_whithout_child, parent, false);
            return new MyViewHolderWithoutChild(view);
        } else {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            View view = layoutInflater.inflate(
                    R.layout.layout_with_child, parent, false);
            return new MyViewHolderWithChild(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case 0: {
                MyViewHolderWithoutChild myViewHolderWithoutChild = (MyViewHolderWithoutChild) holder;
                Item item = mItems.get(position);
                myViewHolderWithoutChild.setIsRecyclable(false);
                myViewHolderWithoutChild.mTextView.setText(item.getText());

                // set event
                myViewHolderWithoutChild.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(mContext, "Without child click : "
                                + mItems.get(position).getText(), Toast.LENGTH_LONG).show();
                    }
                });
            }
            break;
            case 1: {
                final MyViewHolderWithChild myViewHolderWithChild = (MyViewHolderWithChild) holder;
                Item item = mItems.get(position);
                myViewHolderWithChild.setIsRecyclable(false);
                myViewHolderWithChild.mTextView.setText(item.getText());

                myViewHolderWithChild.mExpandableLayout.setInRecyclerView(true);
                myViewHolderWithChild.mExpandableLayout.setExpanded(mExpandState.get(position));
                myViewHolderWithChild.mExpandableLayout.setListener(
                        new ExpandableLayoutListenerAdapter() {
                            @Override
                            public void onPreOpen() {
                                changeRotate(myViewHolderWithChild.mButtonRelativeLayout,
                                        0f, 180f).start();
                                mExpandState.put(position, true);
                            }

                            @Override
                            public void onPreClose() {
                                changeRotate(myViewHolderWithChild.mButtonRelativeLayout,
                                        180f, 0f).start();
                                mExpandState.put(position, false);
                            }
                        });

                myViewHolderWithChild.mButtonRelativeLayout.setRotation(
                        mExpandState.get(position) ? 180f : 0f);
                myViewHolderWithChild.mButtonRelativeLayout.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Expandable child item
                                myViewHolderWithChild.mExpandableLayout.toggle();
                            }
                        });

                myViewHolderWithChild.mTextViewChild.setText(mItems.get(position).getSubText());
                myViewHolderWithChild.mTextViewChild.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(mContext, ""
                                + mItems.get(position).getSubText(), Toast.LENGTH_LONG).show();
                    }
                });

                // set event
                myViewHolderWithChild.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(mContext, "With child click : "
                                + mItems.get(position).getText(), Toast.LENGTH_LONG).show();
                    }
                });
            }
            break;
            default:
                break;
        }
    }

    private ObjectAnimator changeRotate(RelativeLayout buttonRelativeLayout, float from, float to) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(
                buttonRelativeLayout, "rotation", from, to);
        objectAnimator.setDuration(300);
        objectAnimator.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));
        return objectAnimator;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
