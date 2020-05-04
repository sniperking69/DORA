package com.aputech.dora.Model;

public class Vote {
    private  boolean votecheck;
    public Vote() {
        //empty constructor needed
    }

    public Vote(boolean votecheck) {
        this.votecheck = votecheck;
    }

    public boolean isVotecheck() {
        return votecheck;
    }

    public void setVotecheck(boolean votecheck) {
        this.votecheck = votecheck;
    }
}

