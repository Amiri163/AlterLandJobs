package ru.alterlandjobs.common;

import java.util.ArrayList;
import java.util.List;

public class JobInfo {
    private int route = 0;
    private String routeName;
    private int variable;

    public JobInfo(int var, String nameRout, int route) {
        this.routeName = nameRout;
        this.route = route;
        this.variable = var;
        toString();
    }

    @Override
    public String toString() {

        return variable + ") "+  routeName + ". Количество остановок: " + route;
    }

}
