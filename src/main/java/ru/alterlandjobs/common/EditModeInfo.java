package ru.alterlandjobs.common;

public class EditModeInfo {
    private static String jobName;
    private static String routeName;

    public EditModeInfo(String jobName, String routeName) {
        EditModeInfo.jobName = jobName;
        EditModeInfo.routeName = routeName;
    }

    public static String getJobName() {
        return jobName;
    }

    public static String getRouteName() {
        return routeName;
    }
}
