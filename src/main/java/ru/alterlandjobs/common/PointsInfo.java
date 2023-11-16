package ru.alterlandjobs.common;

public class PointsInfo {

    private String routeName;
    private int variable;

    public PointsInfo(int var, String coordinats) {
        this.routeName = coordinats;

        this.variable = var;
        toString();
    }

    @Override
    public String toString() {
        return variable + ") " + routeName;
    }
}
