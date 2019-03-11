// modified by Francis Su on 4/1/00

import java.awt.*;

class hrsUtil {

    //ARTUS

    int players;
    TextArea history;

    int totalRent;
    double surplus;

    double bids[][];
    double hrsBids[][];
    double compensations[];
    int assignments[];
    //CHANGE
    int backAssignments[];
    double payment[];
    boolean envious[];
    double aveDisBids[][];
    boolean inD[];
    int magD;
    double discounts[][];
    double averages[];

    //variables for utilitarian assignment
    double rNode[];
    double pNode[];
    int rPointer[];
    int pPointer[];
    double finalNode;
    int finalPointer;
    int inCycle;
    //

    //bidding stuff - shouldn't need it, except for a few
    //int whoBidding;
    char alpha = 'A';
    //TextField bidField;
    //Control bidB;
    //QuerySlate bidPanel;
    //boolean algType;
    //String rentType;
    //double surplus;
    //Checkbox howSurplus;
    //boolean aveDisMethod;
    //CHANGE
    //Checkbox cb1, cb2, cb3;
    //boolean exAnte;
    //ARTUS

    boolean aveDisMethod;
    boolean exAnte;

    hrsUtil() {
	/*this.bids = new double[players][];
	  hrsBids = new double[players][];
	  aveDisBids = new double[players][];
	  discounts = new double[players][];
	  for(int i = 0; i < players; i++) {
	  this.bids[i] = new double[players];
	  hrsBids[i] = new double[players];
	  aveDisBids[i] = new double[players];
	  discounts[i] = new double[players];
	  }
	  for(int j = 0; j < players; j++) {
	  for(int k = 0; k < players; k++) {
	  this.bids[j][k] = bids[j][k];
	  hrsBids[j][k] = bids[j][k];
	  }
	  }
	  compensations = new double[players];
	  assignments = new int[players];
	  //CHANGE
	  backAssignments = new int[players];
	  payment = new double[players];
	  envious = new boolean[players];
	  inD = new boolean[players];
	  averages = new double[players];
	*/
    }


    //constructor
    hrsUtil(double[][] bids, int players, TextArea history,
            int totalRent, double surplus, boolean aveDisMethod) {

        //FS: added history if-then
        if (!aveDisMethod) {
            history.appendText(
                    "\n------- HRS w/equal distribution of surplus -------\n" +
                            "In this option, players pay their bids at the start (ex-ante)\n" +
                            "on the utilitarian assignment.  Envy is then eliminated\n" +
                            "by compensation rounds, and any leftover surplus is\n" +
                            "divided equally.\n");
        } else {
            history.appendText(
                    "\n------- HRS w/ average discount method -------\n" +
                            "In this option, players pay their bids at the start (ex-ante)\n" +
                            "on the utilitarian assignment.  Envy is then eliminated by\n" +
                            "compensation rounds, and any leftover surplus is divided\n" +
                            "by averaging envy-free scenarios that favor each player.\n");
        }

        this.players = players;
        this.history = history;
        this.totalRent = totalRent;
        this.surplus = surplus;
        this.aveDisMethod = aveDisMethod;

        this.bids = new double[players][];
        hrsBids = new double[players][];
        aveDisBids = new double[players][];
        discounts = new double[players][];
        for (int i = 0; i < players; i++) {
            this.bids[i] = new double[players];
            hrsBids[i] = new double[players];
            aveDisBids[i] = new double[players];
            discounts[i] = new double[players];
        }
        for (int j = 0; j < players; j++) {
            for (int k = 0; k < players; k++) {
                this.bids[j][k] = bids[j][k];
                hrsBids[j][k] = bids[j][k];
            }
        }
        compensations = new double[players];
        assignments = new int[players];
        //CHANGE
        backAssignments = new int[players];
        payment = new double[players];
        envious = new boolean[players];
        inD = new boolean[players];
        averages = new double[players];
    }


    //initializes the arrays and variables for the HRS algorithm
    //a copy of the bids[][] matrix is made because the hrsBids[][]
    //array gets changed by the algorithm

    public void initHRS() {
        hrsBids = new double[players][];
        aveDisBids = new double[players][];
        discounts = new double[players][];
        for (int i = 0; i < players; i++) {
            hrsBids[i] = new double[players];
            aveDisBids[i] = new double[players];
            discounts[i] = new double[players];
        }
        for (int j = 0; j < players; j++) {
            for (int k = 0; k < players; k++) {
                hrsBids[j][k] = bids[j][k];
            }
        }
        compensations = new double[players];
        assignments = new int[players];
        //CHANGE
        backAssignments = new int[players];
        payment = new double[players];
        envious = new boolean[players];
        inD = new boolean[players];
        averages = new double[players];
    }


    //uses the ? algorithm to find the utilitarian assignment of 
    //players to rooms, for use in the HRS algorithm.

    public void utilitarian() {
        rNode = new double[players];
        pNode = new double[players];
        rPointer = new int[players];
        pPointer = new int[players];
        for (int g = 0; g < players; g++) {
            assignments[g] = g;
            rNode[g] = Double.POSITIVE_INFINITY;
            pNode[g] = Double.POSITIVE_INFINITY;
            finalNode = Double.POSITIVE_INFINITY;
        }
        //CHANGE
        for (int i = 0; i < players + 1; i++) {
            for (int q = 0; q < players; q++) {
                //DEBUGGING
                //history.appendText("Player " + q + ": " + assignments[q] + "\n");
            }
            updateNodes();
            if (traceBack() != -1) {
                changeAssignment();
                for (int j = 0; j < players; j++) {
                    rNode[j] = Double.POSITIVE_INFINITY;
                    pNode[j] = Double.POSITIVE_INFINITY;
                    finalNode = Double.POSITIVE_INFINITY;
                }
                i = 0;
            }
        }

    }


    //updateNodes finds the minimum path to each of the nodes and assigns
    //the pointers to the appropriate path

    public void updateNodes() {
        for (int a = 0; a < players; a++) {
            if (rNode[a] > 0) {
                rNode[a] = 0;
                rPointer[a] = -1;
            }
            for (int b = 0; b < players; b++) {
                if (b != assignments[a]) {
                    if ((pNode[b] - bids[b][a]) < rNode[a]) {
                        rNode[a] = pNode[b] - bids[b][a];
                        rPointer[a] = b;
                    }
                }
            }
            //DEBUGGING
            //history.appendText("min for room " + a + " was from " + rPointer[a] + "\n");
        }
        for (int c = 0; c < players; c++) {
            if ((rNode[assignments[c]] + bids[c][assignments[c]]) < pNode[c]) {
                pNode[c] = (rNode[assignments[c]] + bids[c][assignments[c]]);
                pPointer[c] = assignments[c];
            }
        }
        for (int d = 0; d < players; d++) {
            if (pNode[d] < finalNode) {
                finalNode = pNode[d];
                finalPointer = d;
            }
        }
        //DEBUGGING
        //history.appendText("final came from " + finalPointer + "\n");
    }


    //traceBack follows the pointers back to see if we get back to the 
    //start node (-1) or have entered a loop and should change the 
    //assignments

    public int traceBack() {
        boolean hit[] = new boolean[players];
        int temp;
        for (int i = 0; i < players; i++) {
            hit[i] = false;
        }
        temp = finalPointer;
        //DEBUGGING
        //history.appendText("starting trace\n");
        for (int j = 0; j < players; j++) {
            temp = pPointer[temp];
            temp = rPointer[temp];
            if (temp == -1) {
                j = players;
                //DEBUGGING
                //history.appendText("got to beginning\n");
                break;
            }
            if (hit[temp]) {
                hit[temp] = true;
                inCycle = temp;
            } else {
                hit[temp] = true;
            }
        }
        //DEBUGGING
        //history.appendText("temp is " + temp + "\n");
        //history.appendText("inCycle is " + inCycle + "\n");
        return temp;
    }


    //follows the pointers back, changing the player assignments 
    //based on the pointers

    public void changeAssignment() {
        int temp;
        temp = pPointer[inCycle];
        for (int i = 0; i < players; i++) {
            assignments[rPointer[temp]] = temp;
            temp = pPointer[rPointer[temp]];
        }
    }

    //normalizes the bid matrix used in the HRS algorithm
    //by subtracting the assigned bid in a given column from 
    //every entry in that column

    public void normHRS() {
        //CHANGE
        for (int in = 0; in < players; in++) {
            backAssignments[assignments[in]] = in;
        }
        int total = 0;
        //DEBUGGING
	/*
	for (int a = 0; a < players; a++) {
	    for (int b = 0; b < players; b++) {
		history.appendText(hrsBids[a][b]+ ", ");
	    }
	    history.appendText("\n");
	}
	*/
        //FS: added next line
        history.appendText("\nUse the utilitarian assignment:\n");
        for (int i = 0; i < players; i++) {
            payment[assignments[i]] = hrsBids[i][assignments[i]];
            history.appendText("     Player " + (char) ((int) alpha + i) + " pays $" + payment[assignments[i]] + " for Room " + (assignments[i] + 1) + ".\n");
        }
        for (int j = 0; j < players; j++) {
            for (int k = 0; k < players; k++) {
                hrsBids[j][k] -= payment[k];
                //System.out.println(hrsBids[j][k] + " ");
            }
            //System.out.println("\n");
            total += payment[j];
        }
        surplus = total - totalRent;
        //CHANGE
        history.appendText("After rent is paid, there is a surplus of " + surplus + ".\n");
    }


    //processes the Haake-Raith-Su algorithm for eliminating envy
    //documentation elsewhere

    public void doHRS() {
        boolean envy = true;
        int counter = 0;
        while (envy && (counter < players)) {
            //DEBUGGING
            for (int y = 0; y < players; y++) {
                for (int z = 0; z < players; z++) {
                    hrsBids[y][z] = (Math.round(10000 * hrsBids[y][z])) / 10000.0;
                    //DEBUGGING
                    //history.appendText(hrsBids[y][z] + ", ");
                }
                //history.appendText("\n");
            }
            history.appendText("\nStarting new round of compensations:\n");
            checkEnvy();
            //FS: added this line
            history.appendText("Look for players who most envy the non-envious.\n");
            envy = false;
            for (int i = 0; i < players; i++) {
                boolean thisone = findMax(hrsBids[i], i);
                if (thisone) {
                    envy = true;
                }
            }
            //FS: added this if statement
            if (!envy) {
                history.appendText("     None found.\n");
            }
            for (int a = 0; a < players; a++) {
                for (int b = 0; b < players; b++) {
                    hrsBids[a][b] += compensations[b];
                    //history.appendText(hrsBids[a][b] + " ");
                }
                //history.appendText("\n");
            }
            for (int inc = 0; inc < players; inc++) {
                compensations[inc] = 0;
            }
            counter++;
        }
        for (int j = 0; j < players; j++) {
            //history.appendText("Player " + (char)((int)alpha + j) + " gets back " + hrsBids[j][assignments[j]] + ".\n");
        }
        if (aveDisMethod) {
            averageSurplus();
        } else {
            dealSurplus();
        }
        //dealSurplus();
        //averageSurplus();
    }

    //finds out which players are feeling envious of other players, and 
    //adds those players to envious[]

    public void checkEnvy() {
        for (int row = 0; row < players; row++) {
            boolean iterate = false;
            for (int index = 0; index < players; index++) {
                if (hrsBids[row][assignments[row]] < hrsBids[row][index]) {
                    iterate = true;
                    break;
                }
            }
            envious[row] = iterate;
            if (!envious[row]) {
                history.appendText("     Player " + (char) ((int) alpha + row) + " is not envious.\n");
            }
        }
    }


    //figures out the compensation that should be given  to player
    //in row index to eliminate envy
    //CHANGE
    public boolean findMax(double row[], int index) {
        //debugArray(index);
        double temp = 0;
        //FS: bug fix--- this code incorrect -------------
	/*
	for(int i = 0; i < players; i++) {
	    if( ((row[assignments[index]] - row[i]) < temp)
		&& !envious[backAssignments[i]] ) {
		temp = -1 * (row[assignments[index]] - row[i]);
		char winner = (char)((int)alpha + backAssignments[i]);
		char loser = (char)((int)alpha + index);
		history.appendText("     "+loser + " envies " + winner + ", so give " + (Math.round(100*temp))/100.0 + " to " + loser + " from the surplus.\n");
	    compensations[assignments[index]] += temp;
	    }
	}
	*/
        //FS: Replace it with this ----------------------------
        //FS:  it finds max envy FIRST, then checks if envious!
        char winner = 'A';
        char loser = 'A';
        int ii = 0;
        for (int i = 0; i < players; i++) {
            if ((row[assignments[index]] - row[i]) < temp) {
                temp = (row[assignments[index]] - row[i]);
                winner = (char) ((int) alpha + backAssignments[i]);
                loser = (char) ((int) alpha + index);
                ii = i;
            }
        }
        temp = -1 * temp;
        if ((temp > 0) && !envious[backAssignments[ii]]) {
            history.appendText("     " + loser + " envies " + winner +
                    " the most, so give " + (Math.round(100 * temp)) / 100.0 + " to " +
                    loser + " from the surplus.\n");
            compensations[assignments[index]] += temp;
        }
        //FS: End replacement ---------------------------------
        if (temp == 0) {
            return false;
        } else {
            return true;
        }
    }


    //method for dealing the surplus equally to each player

    public void dealSurplus() {
        for (int i = 0; i < players; i++) {
            surplus -= hrsBids[i][assignments[i]];
            payment[assignments[i]] -= hrsBids[i][assignments[i]];
        }
        history.appendText("\nDistribute leftover surplus of " + surplus +
                " equally.\n" +
                "--- SUGGESTED DIVISION (HRS ex-ante equal) ---\n" +
                "     Total rent: $" + totalRent + ".\n");
        for (int j = 0; j < players; j++) {
            payment[j] -= (surplus / players);
        }
        for (int k = 0; k < players; k++) {
            history.appendText("     Player " + (char) ((int) alpha + k)
                    + " pays $"
                    + (Math.round(100 * payment[assignments[k]]) / 100.0)
                    + " for Room "
                    + (assignments[k] + 1) + ".\n");
        }
    }


    //main method for doing the Average Discount Method
    //finds the best method of distributing surplus for each player
    //and averages them

    public void averageSurplus() {
        double tempSurplus;
        for (int i = 0; i < players; i++) {
            surplus -= hrsBids[i][assignments[i]];
            payment[assignments[i]] -= hrsBids[i][assignments[i]];

        }
        history.appendText("Remaining surplus of " + surplus +
                " will be distributed by giving\n" +
                "     discounts that favor one player without creating envy.\n" +
                "     Repeat for each player, then average the discounts:\n\n");
        for (int index = 0; index < players; index++) {
            for (int j = 0; j < players; j++) {
                for (int k = 0; k < players; k++) {
                    aveDisBids[j][k] = hrsBids[j][k];
                }
            }
            //FS: added next line
            history.appendText("--favoring Player " + (char) ((int) alpha + index) + "--\n");
            tempSurplus = surplus;
            int count = 0;
            while (tempSurplus > 0) {
                for (int y = 0; y < players; y++) {
                    for (int z = 0; z < players; z++) {
                        aveDisBids[y][z] = (Math.round(10000 * aveDisBids[y][z])) / 10000.0;
                        //DEBUGGING
                        //history.appendText(aveDisBids[y][z] + ", ");
                    }
                    //history.appendText("\n");
                }
                getD(index);
                tempSurplus = findMinDiscount(index, tempSurplus);
                tempSurplus = (Math.round(tempSurplus * 10000)) / 10000.0;
                //DEBUGGING
                //history.appendText("tempSurplus is " + tempSurplus + "\n");
                count++;
            }
            //FS: modified the next command to indicate which player
            history.appendText("   Player discounts (favoring " +
                    (char) ((int) alpha + index) + "): ");
            for (int a = 0; a < players; a++) {
                //DEBUGGING
                double overallDis = /*hrsBids[a][assignments[a]] +*/ discounts[index][a];
                history.appendText((Math.round(100 * overallDis) / 100.0) + ", ");
            }
            history.appendText("\n\n");
        }
        averageThem();
        //FS: added next history.appendText command
        history.appendText("--- SUGGESTED DIVISION (HRS ex-ante average) ---\n" +
                "     Total rent: $" + totalRent + ".\n");
        for (int p = 0; p < players; p++) {
            history.appendText("     Player " + (char) ((int) alpha + p)
                    + " pays $"
                    + (Math.round(100 * (payment[assignments[p]] - averages[p])) / 100.0)
                    + " for Room " + (assignments[p] + 1) + ".\n");
        }
        //FS: added if statement in the next 3 lines [bug]
        //if( tempSurplus < 0 ) {
        //  history.appendText("Bids didn't sum to total rent, so solution doesn't");
        //}
    }


    //takes the biased discounts and averages them for each player

    public void averageThem() {
        double sum;
        history.appendText("Average player discounts:  ");
        for (int i = 0; i < players; i++) {
            sum = 0;
            for (int j = 0; j < players; j++) {
                sum += discounts[j][i];
            }
            averages[i] = sum / players;
            history.appendText((Math.round(100 * averages[i]) / 100.0) + ", ");
        }
        history.appendText("\nSum payments minus compensations and discounts.\n");
    }


    //return tempSurplus - if zero, done, but remember to decrease it
    //finds the amount that should be given to the tied set (D)
    //this is the max that can be given to D without creating envy

    public double findMinDiscount(int index, double tempSurplus) {
        double tSurplus = tempSurplus;
        double checkValue = tSurplus / magD;
        double dis = 0;
        int counter = 0;
        for (int i = 0; i < players; i++) {
            if (inD[i]) {
                counter++;
            }
        }
        if (counter == players) {
            for (int j = 0; j < players; j++) {
                dis = tSurplus / players;
            }
        } else {
            dis = checkValue;
            for (int a = 0; a < players; a++) {
                if (inD[a]) {
                    for (int b = 0; b < players; b++) {
                        if (((aveDisBids[b][assignments[b]] - aveDisBids[b][assignments[a]]) < dis) && (aveDisBids[b][assignments[b]] > aveDisBids[b][assignments[a]]) && !inD[b]) {
                            dis = (aveDisBids[b][assignments[b]] - aveDisBids[b][assignments[a]]);
                            //DEBUGGING
                            //history.appendText("dis for " + (char)((int)alpha+b) + " - " + (char)((int)alpha+a)+ " is " + dis + "\n");
                        }

                    }
                }
            }
        }
        for (int c = 0; c < players; c++) {
            if (inD[c]) {
                discounts[index][c] += dis;
                for (int d = 0; d < players; d++) {
                    aveDisBids[d][assignments[c]] += dis;
                }
                tSurplus -= dis;
            }
        }
        history.appendText("each " + (Math.round(dis * 100) / 100.0) + " without creating envy.\n");
        return tSurplus;
    }


    //finds the players in the set D who should be participants 
    //in this round of the average discount method
    //a player is in D if they feel tied with someone who is in D;
    //their assigned bid matches a member of D's entry in that row

    public void getD(int index) {
        magD = 0;
        for (int j = 0; j < players; j++) {
            inD[j] = false;
        }
        inD[index] = true;
        boolean added = true;
        while (added) {
            added = false;
            for (int i = 0; i < players; i++) {
                if (!inD[i]) {
                    for (int j = 0; j < players; j++) {
                        if ((aveDisBids[i][assignments[i]] == aveDisBids[i][assignments[j]]) && inD[j]) {
                            inD[i] = true;
                            magD++;
                            added = true;
                        }
                    }
                }
            }
        }
        history.appendText("Now can give ");
        for (int j = 0; j < players; j++) {
            if (inD[j]) {
                history.appendText((char) ((int) alpha + j) + ", ");
            }
        }
        //history.appendText(" ");
    }

    public void debugArray() {
        for (int i = 0; i < players; i++) {
            for (int j = 0; j < players; j++) {
                history.appendText(hrsBids[i][j] + ", ");
            }
            history.appendText("\n");
        }
        history.appendText("\n\n");
    }

    public void debugArray(int row) {
        for (int i = 0; i < players; i++) {
            history.appendText(hrsBids[row][i] + ", ");
        }
        history.appendText("\n\n");
    }


}
