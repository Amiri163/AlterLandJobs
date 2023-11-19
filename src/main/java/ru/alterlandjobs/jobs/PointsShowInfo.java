package ru.alterlandjobs.jobs;

public class PointsShowInfo {
    private String routeName;
    private int variable;


    public PointsShowInfo(int var, String cord) {
        this.variable = var;
        this.routeName = cord;
        toString();
    }

    @Override
    public String toString() {
        return variable + ") " +  routeName;
    }
}
