package com.anishsainju.udacity.bakingonclick.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class IngreStep implements Parcelable {

    public static final int VIEW_TYPE_INGREDIENT = 0;
    public static final int VIEW_TYPE_STEP = 1;
    //creator - used when un-parceling our parcel (creating the object)
    public static final Parcelable.Creator<IngreStep> CREATOR = new Parcelable.Creator<IngreStep>() {

        @Override
        public IngreStep createFromParcel(Parcel parcel) {
            return new IngreStep(parcel);
        }

        @Override
        public IngreStep[] newArray(int size) {
            return new IngreStep[size];
        }
    };
    private int viewType;
    // Ingredient list
    private List<Ingredient> ingredientList;
    // Step info
    private int id;
    private String shortDescription;
    private String description;
    private String videoURL;
    private String thumbnailURL;

    public IngreStep(int viewType, List<Ingredient> ingredientList) {
        this.viewType = viewType;
        this.ingredientList = ingredientList;
    }

    public IngreStep(int viewType, int id, String shortDescription, String description, String videoURL, String thumbnailURL) {
        this.viewType = viewType;
        this.id = id;
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoURL = videoURL;
        this.thumbnailURL = thumbnailURL;
    }

    public IngreStep(List<Ingredient> ingredientList, int id, String shortDescription, String description, String videoURL, String thumbnailURL) {
        this.ingredientList = ingredientList;
        this.id = id;
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoURL = videoURL;
        this.thumbnailURL = thumbnailURL;
    }

    protected IngreStep(Parcel parcel) {
        viewType = parcel.readInt();
        ingredientList = new ArrayList<>();
        parcel.readList(ingredientList, Ingredient.class.getClassLoader());
        id = parcel.readInt();
        shortDescription = parcel.readString();
        description = parcel.readString();
        videoURL = parcel.readString();
        thumbnailURL = parcel.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewType);
        dest.writeList(ingredientList);
        dest.writeInt(id);
        dest.writeString(shortDescription);
        dest.writeString(description);
        dest.writeString(videoURL);
        dest.writeString(thumbnailURL);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public List<Ingredient> getIngredientList() {
        return ingredientList;
    }

    public void setIngredientList(List<Ingredient> ingredientList) {
        this.ingredientList = ingredientList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }
}
