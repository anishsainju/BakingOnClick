package com.anishsainju.udacity.bakingonclick;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anishsainju.udacity.bakingonclick.model.IngreStep;

import java.util.List;

public class MultiViewItemTypeViewAdapter extends RecyclerView.Adapter {

    private final RecipeListActivity mParentActivity;
    private final List<IngreStep> mValues;
    private final boolean mTwoPane;
    private Context mContext;
    private View.OnClickListener onClickListener;
    private int selectedPosition = -1;

    public MultiViewItemTypeViewAdapter(RecipeListActivity parent, List<IngreStep> data, Context context, boolean twoPane, View.OnClickListener onClickListener) {
        this.mParentActivity = parent;
        this.mValues = data;
        this.mContext = context;
        this.mTwoPane = twoPane;
        this.onClickListener = onClickListener;
    }

    public void setSelectedPos(int selectedPos) {
        this.selectedPosition = selectedPos;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return mValues.get(position).getViewType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        switch (viewType) {
            case IngreStep.VIEW_TYPE_INGREDIENT:
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_list_content, parent, false);
                viewHolder = new MultiViewItemTypeViewAdapter.IngredientViewHolder(view1);
                break;
            case IngreStep.VIEW_TYPE_STEP:
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_list_content, parent, false);
                viewHolder = new MultiViewItemTypeViewAdapter.StepViewHolder(view2);
                break;
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int pos) {
        if (selectedPosition == pos)
            viewHolder.itemView.setBackgroundColor(Color.parseColor("#00FF00"));
        else
            viewHolder.itemView.setBackgroundColor(0x00000000);
        switch (viewHolder.getItemViewType()) {
            case IngreStep.VIEW_TYPE_INGREDIENT:
                IngredientViewHolder ivh = (IngredientViewHolder) viewHolder;
                ivh.ingredientTextview.setText(R.string.ingredients);
                break;
            case IngreStep.VIEW_TYPE_STEP:
                StepViewHolder svh = (StepViewHolder) viewHolder;
                IngreStep ingreStep = mValues.get(pos);
                svh.stepIdTextview.setText(String.valueOf(ingreStep.getId()));
                svh.shortDescTextview.setText(ingreStep.getShortDescription());
                break;
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewHolder.getItemViewType());
        }
        viewHolder.itemView.setTag(pos);
        viewHolder.itemView.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class IngredientViewHolder extends RecyclerView.ViewHolder {
        TextView ingredientTextview;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            ingredientTextview = itemView.findViewById(R.id.content);
        }
    }

    public class StepViewHolder extends RecyclerView.ViewHolder {
        TextView stepIdTextview, shortDescTextview;

        public StepViewHolder(@NonNull View itemView) {
            super(itemView);
            stepIdTextview = itemView.findViewById(R.id.id_text);
            shortDescTextview = itemView.findViewById(R.id.content);
        }
    }
}
