/*
David Clark
CE03 - Android Wear
MDV359-O
C20200101
*/

package edu.fullsail.echo;

public class EchoLeaderBoardEntry {
    private String displayName;
    private int    finalScore;

    public EchoLeaderBoardEntry() {}

    public EchoLeaderBoardEntry( String displayName, int finalScore ) {
        this.displayName = displayName;
        this.finalScore  = finalScore;
    }

    public String getDisplayName() { return displayName; }
    public int    getFinalScore()  { return finalScore;  }
}
