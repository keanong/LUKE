package com.xdel.luke.RecycleViewsRelated.test;

public class User {

    private int id;
    private String name;
    private String imageUrl;
    private String type;
    private boolean IsEditing;
    private String IsHeader;

    public User(int id, String name, String imageUrl, String type, String aIsHeader) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.type = type;
        this.IsHeader = aIsHeader;
    }

    public User(User user) {
        this.id = user.id;
        this.name = user.name;
        this.imageUrl = user.imageUrl;
        this.type = user.type;
        this.IsHeader = "f";
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getIsHeader() {
        return this.IsHeader;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public String getType() {
        return this.type;
    }

    public boolean getIsEditing(){
        return this.IsEditing;
    }

    public void setIsEditing(boolean a) {
        this.IsEditing = a;
    }

}
