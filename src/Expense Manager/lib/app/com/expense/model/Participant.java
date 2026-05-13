package com.expense.model;

import java.util.Objects;

public class Participant {
    private String name;
    private double totalSpent;

    public Participant(String name) {
        this.name = name;
        this.totalSpent = 0;
    }

    public String getName() { return name; }
    public double getTotalSpent() { return totalSpent; }

    public void addSpent(double amount) {
        this.totalSpent += amount;
    }

    public void subtractSpent(double amount) {
        this.totalSpent -= amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participant that = (Participant) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
