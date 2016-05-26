package edu.wkd.towave.memorycleaner.model;

import android.graphics.drawable.Drawable;
import java.io.Serializable;
import net.tsz.afinal.annotation.sqlite.Table;
import net.tsz.afinal.annotation.sqlite.Transient;

/**
 * Created by towave on 2016/5/26.
 */
@Table(name = "ignores") public class Ignore implements Serializable {
    private int id;
    private Drawable appIcon;
    private String appName;
    @Transient private Boolean isChecked = false;


    public Ignore() {
        super();
    }


    public Ignore(Drawable appIcon, String appName) {
        this.appName = appName;
        this.appIcon = appIcon;
    }


    public Ignore(int id, Drawable appIcon, String appName) {
        this(appIcon, appName);
        this.id = id;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public Boolean getChecked() {
        return isChecked;
    }


    public void setChecked(Boolean checked) {
        isChecked = checked;
    }


    public Drawable getAppIcon() {
        return appIcon;
    }


    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }


    public String getAppName() {
        return appName;
    }


    public void setAppName(String appName) {
        this.appName = appName;
    }
}
