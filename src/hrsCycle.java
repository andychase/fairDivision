/*
 *hrsCycle.java
 *Patrick Vinograd
 *February 20, 2000
 *This class implements the Haake-Raith-Su (HRS) algorithm into the
 *Fair Division Calculator using the cycling-to-efficiency method.
 */
// modified 4/1/00 by Francis Su


import java.awt.*;

class hrsCycle extends hrsUtil {

    //ARTUS

    /*    int players;
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
    */
    int brake = 0;
    int envyTarget[];
    boolean inEnvyCycle[];
    double totalCompensation[];
    boolean hadEnvy[];
    boolean beenCompensated[];

    //constructor
    hrsCycle(double[][] bids, int players, TextArea history,
             int totalRent, double surplus, boolean aveDisMethod) {

        //FS: added history line
        history.appendText(
                "\n----- HRS w/ ex-post payments & cycling to efficiency -----\n" +
                        "This option show how to start with an arbitrary (diagonal)\n" +
                        "assignment and use the envy relations to determine an\n" +
                        "efficent one.  After envy-freeness is reached (ex-post),\n" +
                        "players equally split the cost of rent and compensations.\n");

        this.players = players;
        this.history = history;
        this.totalRent = totalRent;
        this.surplus = surplus;
        this.aveDisMethod = aveDisMethod;

        this.bids = new double[players][];
        hrsBids = new double[players][];
        //aveDisBids = new double[players][];
        //discounts = new double[players][];
        for (int i = 0; i < players; i++) {
            this.bids[i] = new double[players];
            hrsBids[i] = new double[players];
            //aveDisBids[i] = new double[players];
            //discounts[i] = new double[players];
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
        envyTarget = new int[players];
        totalCompensation = new double[players];
        payment = new double[players];
        envious = new boolean[players];
        //inD = new boolean[players];
        //averages = new double[players];
        inEnvyCycle = new boolean[players];
        hadEnvy = new boolean[players];
        beenCompensated = new boolean[players];
        for (int i = 0; i < players; i++) {
            hadEnvy[i] = false;
            beenCompensated[i] = false;
            envyTarget[i] = -1;
        }
        //FS: added the next 2 commands -- history and if staements
        history.appendText("\nStart with the diagonal assignment:\n");
        for (int i = 0; i < players; i++) {
            history.appendText("     Player " + (char) ((int) alpha + i) +
                    " gets Room " + (i + 1) + ".\n");
        }
        //history.appendText("using the hrsCycle class\n\n");
    }


    public void utilitarian() {
        for (int i = 0; i < players; i++) {
            assignments[i] = i;
            backAssignments[i] = i;
        }
    }

    //Normalizes the bid matrix used in the HRS algorithm
    //In the case of cycling, we want the assessment matrix to be the 
    //original bid matrix, so we just copy it over and keep the original

    public void normHRS() {
        for (int i = 0; i < players; i++) {
            for (int j = 0; j < players; j++) {
                hrsBids[i][j] = bids[i][j];
            }
        }
    }


    public void doHRS() {
        doNewHRS();
        //doOldHRS();
    }


    //processes the Haake-Raith-Su algorithm for eliminating envy
    //documentation elsewhere

    /*    public void doOldHRS() {
	  boolean cycling = false;
	  boolean envy = true;
	  int counter = 0;
	  while(envy && (counter < players) && brake < 20) {
	  history.appendText("\nStarting new round of compensation:\n");
	  boolean throwaway = getEnvy();
	  envy = false;
	  for(int i = 0; i < players; i++) {
	  boolean thisone = findMax(hrsBids[i],i);
	  if(thisone) {
	  if (!hadEnvy[i]) {
	  cycling = true;
	  }
	  envy = true;
	  }
	  }
	  if (checkLoops()) {
	  changeAssignment();
	  resetMatrix();
	  counter = 0;
	  }
	  else {
	  for(int a = 0; a < players; a++) {
	  for(int b = 0; b < players; b++) {
	  hrsBids[a][b] += compensations[b];
	  totalCompensation[b] += compensations[b];
	  //history.appendText(hrsBids[a][b] + " ");
	  }
	  //history.appendText("\n");
	  } 
	  for (int inc = 0; inc < players; inc++) {
	  compensations[inc] = 0;
	  }
	  counter++;
	  }
	  brake++;
	  }
	  dealPayment();
	  }
    */

    public void doNewHRS() {
        int counter = 0;
        boolean envy = true;
        while (envy && (counter < 10)) {
            envy = getEnvy();
            if (envy) {
                boolean cycles = checkCycles();
                if (cycles) {
                    changeAssignment();
                    resetMatrix();
                    //		    counter = 0;
                } else {
                    compensate();
                }
            }
            counter++;
        }
        dealPayment();
    }

    //finds out which players are feeling envious of other players, and 
    //adds those players to envious[]

    public boolean getEnvy() {
        history.appendText("\n");
        int envyCount = 0;
        for (int row = 0; row < players; row++) {
            //hadEnvy[row] = envious[row];
            double temp = 0;
            int target = -1;
            boolean iterate = false;
            for (int index = 0; index < players; index++) {
                if ((hrsBids[row][assignments[row]] - hrsBids[row][index])
                        < (-1 * temp)) {
                    temp = -1 * (hrsBids[row][assignments[row]] -
                            hrsBids[row][index]);
                    target = index;
                }
            }
            //this player is envious
            if (temp > 0) {
                envyCount++;
                envious[row] = true;
                hadEnvy[row] = true;
                envyTarget[row] = backAssignments[target];
                history.appendText("     Player " + (char) ((int) alpha + row) +
                        " is envious:");
                history.appendText("\t" + (char) ((int) alpha + row) + "->" +
                        (char) ((int) alpha + envyTarget[row])
                        + " : " + temp + ".\n");
                compensations[row] = temp;
            }
            //this player is not envious
            else {
                envious[row] = false;
                //envyTarget[row] = -1;
                history.appendText("     Player " + (char) ((int) alpha + row) +
                        " is not envious.");
                if ((envyTarget[row] != -1) && beenCompensated[row]) {
                    history.appendText("\t" + (char) ((int) alpha + row) + "=>"
                            + (char) ((int) alpha + envyTarget[row])
                            + " : " + temp + ".");
                }
                //FS: Added this line
                history.appendText("\n");
            }
        }
        return (envyCount > 0);
    }

    public void compensate() {
        for (int p = 0; p < players; p++) {
            if ((envious[p]) &&
                    (envyTarget[p] != -1) &&
                    (!envious[envyTarget[p]])) {
                for (int row = 0; row < players; row++) {
                    hrsBids[row][assignments[p]] += compensations[p];
                }
                history.appendText("Compensate Player "
                        + (char) ((int) alpha + p)
                        + " by "
                        + compensations[p]
                        + ".\n");
                totalCompensation[p] += compensations[p];
                beenCompensated[p] = true;
            }
        }
        //debugArray();
    }


    //figures out the compensation that should be given  to player
    //in row index to eliminate envy

    //It's not finding envy correctly!

    //CHANGE
    public boolean findMax(double row[], int index) {
        //debugArray(index);
        double temp = 0;
        envyTarget[index] = -1;
        debugArray();
        //for (int i = 0; i < players; i++) {
        //   envyTarget[index] = -1;
        //}
        for (int i = 0; i < players; i++) {
            if (((row[assignments[index]] - row[i]) < temp)
                /*&& !envious[backAssignments[i]]*/) {
                temp = -1 * (row[assignments[index]] - row[i]);
                char winner = (char) ((int) alpha + backAssignments[i]);
                char loser = (char) ((int) alpha + index);
                history.appendText(" Since Player " + loser
                        + " envies Player " + winner + ", pay "
                        + (Math.round(100 * temp)) / 100.0 + " to "
                        + loser + "\n");
                envyTarget[index] = backAssignments[i];
                compensations[assignments[index]] += temp;
            }
	    /*	    if( ((row[assignments[index]] - row[i]) < temp)
		    && !envious[backAssignments[i]] ) {
		    temp = -1 * (row[assignments[index]] - row[i]);
		    char winner = (char)((int)alpha + backAssignments[i]);
		    char loser = (char)((int)alpha + index);
		    history.appendText(" Since Player " + loser 
		    + " envies Player " + winner + ", pay " 
		    + (Math.round(100*temp))/100.0 + " to " + loser + "\n");
		    envyTarget[index] = backAssignments[i];
		    compensations[assignments[index]] += temp;
		    }*/
        }
        if (temp == 0) {
            return false;
        } else {
            return true;
        }
    }


    //method for dealing payment amounts
    //do compensations map to players or to rooms?
    public void dealPayment() {
        double totalcomp = 0;
        for (int i = 0; i < players; i++) {
            totalcomp += totalCompensation[i];
            //history.appendText(totalCompensation[i] + "\n");
        }
        double netPayment = totalcomp + totalRent;
        history.appendText("\nAll players are now charged equally for the total\n" +
                "cost (" + netPayment + ") of the rent and compensations.\n" +
                "--- SUGGESTED DIVISION (HRS ex-post equal) ---\n" +
                "     Total rent: $" + totalRent + ".\n");
        for (int i = 0; i < players; i++) {
            payment[i] = netPayment / players;
            payment[i] = payment[i] - totalCompensation[i];
            history.appendText("     Player " + (char) ((int) alpha + i)
                    + " pays $"
                    + (Math.round(100 * payment[i]) / 100.0)
                    + " for Room " + (assignments[i] + 1) + ".\n");
        }
    }

    public boolean checkCycles() {
        boolean tempCheck[] = new boolean[players];
        for (int a = 0; a < players; a++) {
            inEnvyCycle[a] = false;
            tempCheck[a] = false;
        }
        for (int i = 0; i < players; i++) {
            if (cycleTest(i, tempCheck)) {
                for (int j = 0; j < players; j++) {
                    inEnvyCycle[j] = tempCheck[j];

                }
                return true;
            } else {
                for (int j = 0; j < players; j++) {
                    tempCheck[j] = false;
                }
            }
        }
        return false;
    }


    //Only returns true on closed loops

    public boolean cycleTest(int start, boolean flag[]) {
        int current = start;
        for (int i = 0; i < players; i++) {
            flag[current] = true;
            if (envyTarget[current] == -1) {
                return false;
            } else if (envyTarget[current] == start) {
                return true;
            } else {
                current = envyTarget[current];
            }
        }
        return false;
    }

    /*    public boolean checkLoops() {
	  boolean temp = false;
	  for (int i = 0; i < players; i++) {	    
	  if (recurseLoop(i, i)) {
	  temp = true;
	  }
	  }
	  if (temp) {
	  history.appendText("An envy cycle exists in 
	  the current assignment.\n");
	  }
	  return temp;
	  }

	  public boolean recurseLoop(int current, int start) {
	  if (envyTarget[current] == -1) {
	  inEnvyCycle[current] = false;
	  return false;
	  }
	  else if (envyTarget[current] == start) {
	  inEnvyCycle[current] = true;
	  return true;
	  }
	  else {
	  inEnvyCycle[current] = recurseLoop(envyTarget[current], start);
	  return inEnvyCycle[current];
	  }
	  }
    */

    //Remember to change backAssignments!

    public void changeAssignment() {
        history.appendText("\nAn envy cycle exists, so change assignments:\n");
        for (int i = 0; i < players; i++) {
            if (inEnvyCycle[i]) {
                int temp = assignments[i];
                int current = i;
                int target = envyTarget[i];
                while (envyTarget[current] != i) {
                    assignments[current] = assignments[envyTarget[current]];
                    history.appendText("     Player " + (char) ((int) alpha + current)
                            + " gets Room "
                            + (assignments[current] + 1) + ".\n");
                    current = envyTarget[current];
                }
                //the last person in the cycle needs the first person's
                //assignment, which we had to keep in temp so it didn't
                //get wiped out.
                assignments[current] = temp;
                history.appendText("     Player " + (char) ((int) alpha + current)
                        + " gets Room " + (assignments[current] + 1)
                        + "\n");
                i = players;
            }
        }
        for (int in = 0; in < players; in++) {
            backAssignments[assignments[in]] = in;
        }
    }

    public void resetMatrix() {
        normHRS();
        for (int i = 0; i < players; i++) {
            envious[i] = false;
            hadEnvy[i] = false;
            envyTarget[i] = -1;
            compensations[i] = 0;
            totalCompensation[i] = 0;
            beenCompensated[i] = false;
        }
        history.appendText("     Have players return all compensations.\n");
    }


}
