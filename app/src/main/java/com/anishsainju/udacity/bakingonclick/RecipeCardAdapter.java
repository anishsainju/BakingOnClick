package com.anishsainju.udacity.bakingonclick;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anishsainju.udacity.bakingonclick.model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeCardAdapter extends RecyclerView.Adapter<RecipeCardAdapter.RecipeCardAdapterViewHolder> {

    private final RecipeCardAdapterOnClickHandler mClickHandler;
    private Context context;
    private List<Recipe> mRecipesData = new ArrayList<>();

    public RecipeCardAdapter(Context context, RecipeCardAdapterOnClickHandler clickHandler) {
        this.context = context;
        this.mClickHandler = clickHandler;
    }

    public RecipeCardAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();

        int layoutIdForListItem = R.layout.recipe_card_item;
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        boolean shouldAttachToParentImmediately = false;
        View view = layoutInflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);

        return new RecipeCardAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeCardAdapterViewHolder holder, int position) {
        Recipe selectedRecipe = mRecipesData.get(position);
//        Picasso.with(context)
//                .load(selectedRecipe.getImage())
//                .placeholder(R.drawable.ic_no_image)
//                .into(holder.mImageView);
        holder.mTextView.setText(selectedRecipe.getName());
    }

    @Override
    public int getItemCount() {
        if (null == mRecipesData) return 0;
        return mRecipesData.size();
    }

    public void setRecipeData(List<Recipe> moviesData) {
        mRecipesData.clear();
        mRecipesData.addAll(moviesData);
        notifyDataSetChanged();
    }

    public interface RecipeCardAdapterOnClickHandler {
        void onClick(int position);
    }

    public class RecipeCardAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //        final ImageView mImageView;
        final TextView mTextView;

        RecipeCardAdapterViewHolder(View view) {
            super(view);
            mTextView = view.findViewById(R.id.tv_recipe_card);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int recipePosition = getAdapterPosition();
            mClickHandler.onClick(recipePosition);
        }
    }
}
