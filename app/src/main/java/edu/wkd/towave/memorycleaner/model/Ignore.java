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
    private String packName;

    @Transient private String appName;
    @Transient private Drawable appIcon;
    @Transient private Boolean isChecked = false;


    public Ignore() {
        super();
    }


    public Ignore(String packName) {
        this.packName = packName;
    }


    public Ignore(String packName, String appName) {
        this(packName);
        this.appName = appName;
    }


    public Ignore(int id, String packName, String appName) {
        this(packName, appName);
        this.id = id;
    }


    public String getPackName() {
        return packName;
    }


    public void setPackName(String packName) {
        this.packName = packName;
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
