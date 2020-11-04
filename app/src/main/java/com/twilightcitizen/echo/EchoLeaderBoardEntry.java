/*
David Clark
CE03 - Android Wear
MDV359-O
C20200101
*/

package com.twilightcitizen.echo;

// The supposedly unused entries are required for correctly Firebase functionality.

/*
Echo Leaderboard Entry encapsulates the data for an entry on the Firebase Cloud Firestore
leaderboard more conveniently that Maps or Dictionaries of values.
*/
public class EchoLeaderBoardEntry {
    private String displayName;
    private int    finalScore;

    public EchoLeaderBoardEntry() {}

    EchoLeaderBoardEntry( String displayName, int finalScore ) {
        this.displayName = displayName;
        this.finalScore  = finalScore;
    }

    public String getDisplayName() { return displayName; }
    public int    getFinalScore()  { return finalScore;  }
}
