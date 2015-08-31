package it.vibwear.app.adapters;

/**
 * Created by biospank on 31/08/15.
 */
public class Notification {
    private String packageName;

    public Notification(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

}
