package org.example;

public class Member {

    private int id;
    private String name;
    private int activeLoans;

    public Member(int id, String name)  {
        this.id = id;
        this.name = name;
        this.activeLoans = 0;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getActiveLoans() {
        return activeLoans;
    }

    public void increaseLoans() {
        this.activeLoans++;
    }

    public void decreaseLoans() {
        if (activeLoans > 0) {
            activeLoans--;
        }
    }

    public boolean canBorrow() {
        return activeLoans < 2;
    }
}
