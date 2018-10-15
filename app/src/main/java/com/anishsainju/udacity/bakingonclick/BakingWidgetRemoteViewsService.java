package com.anishsainju.udacity.bakingonclick;

import android.content.Intent;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

public class BakingWidgetRemoteViewsService extends RemoteViewsService {

    public static final String RECIPE_SEL = "recipe_selected";
    public static final String INGREDIENTS_LIST = "ingredients_list";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        ArrayList<String> ingreList = intent.getStringArrayListExtra(INGREDIENTS_LIST);
        return new BakingWidgetRemoteViewsFactory(this.getApplicationContext(), ingreList);
    }
}
