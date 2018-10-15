package com.anishsainju.udacity.bakingonclick;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.anishsainju.udacity.bakingonclick.model.Ingredient;
import com.anishsainju.udacity.bakingonclick.model.Recipe;

import java.util.ArrayList;

import static com.anishsainju.udacity.bakingonclick.BakingWidgetRemoteViewsService.INGREDIENTS_LIST;
import static com.anishsainju.udacity.bakingonclick.BakingWidgetRemoteViewsService.RECIPE_SEL;

/**
 * Implementation of App Widget functionality.
 */
public class BakingWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int[] appWidgetIds, Recipe recipe) {
        for (int appWidgetId : appWidgetIds) {
            CharSequence widgetText = "Recipe";
            // Construct the RemoteViews object
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_widget_provider);

            Intent mainActivityIntent = new Intent(context, MainActivity.class);
            Intent bakingWidgetRemoteViewsServiceIntent = new Intent(context, BakingWidgetRemoteViewsService.class);
            if (recipe != null) {
                bakingWidgetRemoteViewsServiceIntent.putStringArrayListExtra(INGREDIENTS_LIST, getIngredientsStringArray(recipe));
                bakingWidgetRemoteViewsServiceIntent.setData(Uri.fromParts("Widget", String.valueOf(appWidgetId + Math.random()), null));
                views.setTextViewText(R.id.appwidget_text, widgetText + ": " + recipe.getName());
            }
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainActivityIntent, 0);
            views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);
            views.setRemoteAdapter(R.id.appwidget_listview, bakingWidgetRemoteViewsServiceIntent);
//        views.setTextViewText(R.id.appwidget_text, widgetText);

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private static ArrayList<String> getIngredientsStringArray(Recipe recipe) {
        ArrayList<String> val = new ArrayList<>();
        for (Ingredient i : recipe.getIngredients()) {
            System.out.println("######### " + i.toString());
            val.add(i.toString());
        }
        return val;
    }

    public static void sendRefreshBroadcast(Context context, Recipe recipe) {
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(RECIPE_SEL, recipe);
        intent.setComponent(new ComponentName(context, BakingWidgetProvider.class));
        context.sendBroadcast(intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        super.onUpdate(context, appWidgetManager, appWidgetIds);
//        for (int appWidgetId : appWidgetIds) {
//            updateAppWidget(context, appWidgetManager, appWidgetId);
//        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            Recipe recipe = intent.getParcelableExtra(RECIPE_SEL);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName componentName = new ComponentName(context, BakingWidgetProvider.class);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(componentName), R.id.appwidget_listview);
            updateAppWidget(context, appWidgetManager, appWidgetManager.getAppWidgetIds(componentName), recipe);
            onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(componentName));
        }
    }
}

