package ru.alterlandjobs.common;

import java.util.ArrayList;
import java.util.List;

public class JobInfo {
    private int route = 0;
    private String routeName;

    public JobInfo(String nameRout, int route) {
        this.routeName = nameRout;
        this.route = route;
        toString();
    }

    @Override
    public String toString() {
        int var = 1;
        return var + ") "+  routeName + ". Количество остановок: " + route;
    }

}
