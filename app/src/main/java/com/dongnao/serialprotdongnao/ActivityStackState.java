package com.dongnao.serialprotdongnao;

public class ActivityStackState {
    private static int activityType;

    public static int getActivityType() {
        return activityType;
    }

    public static void setActivityType(int activityType) {
        ActivityStackState.activityType = activityType;
    }

    private static final ActivityStackState ourInstance = new ActivityStackState();

    public static ActivityStackState getInstance() {
        return ourInstance;
    }

    private ActivityStackState() {
    }
}
