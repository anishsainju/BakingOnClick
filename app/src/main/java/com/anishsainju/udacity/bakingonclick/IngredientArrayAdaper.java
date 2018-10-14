package com.anishsainju.udacity.bakingonclick;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.anishsainju.udacity.bakingonclick.model.Ingredient;

import java.util.List;

public class IngredientArrayAdaper extends ArrayAdapter<Ingredient> {
    private List<Ingredient> ingredientList;
    private Context mContext;

    IngredientArrayAdaper(@NonNull Context context, @NonNull List<Ingredient> ingredients) {
        super(context, R.layout.ingredient_item, ingredients);
        this.mContext = context;
        this.ingredientList = ingredients;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Ingredient ingredient = getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.ingredient_item, parent, false);
            viewHolder.tvIngredientName = convertView.findViewById(R.id.tv_ingredientName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (ingredient != null) {
            viewHolder.tvIngredientName.setText(ingredient.toString());
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return ingredientList.size();
    }

    private static class ViewHolder {
        TextView tvIngredientName;
    }
}
