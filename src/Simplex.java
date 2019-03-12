//Simplex.java
//by Patrick Vinograd
//March 3, 1999

//Implements the simplex class, which represents an n-dimensional 
//structure used in the fairDivision calculator

class Simplex {

    int players;
    int dimension;
    int level;         //equal to the level of the lower vertices
    int pivot;         //the pivot
    int newPoint;      //the newest point
    int marker;        //division between upper and lower levels
    Vertex points[];   //the array of vertices
    String divType;    //"Rent" "Cake" or "Chores"
    Vertex dummy;      //used for average transformations
    int totalRent;

    //constructor takes players, division

    Simplex(int n, String divType) {
        players = n;
        this.divType = divType;
        dimension = n + 1;
        level = 0;
        points = new Vertex[dimension];
        dummy = new Vertex(players - 1);

        //initial Simplex for Cake division or Rent division
        if (divType == "Cake" || divType == "Rent") {
            for (int i = 0; i < dimension; i++) {
                points[i] = new Vertex(players - 1);
                for (int k = 0; k < players - 1; k++) {
                    points[i].elements[k] = 1;
                }
                points[i].label = i + 1;
            }
            for (int col = 0; col < dimension - 1; col++) {
                for (int j = 0; j < col; j++) {
                    points[col].elements[j] = 0;
                }
                points[col].level = 0;
            }
            points[dimension - 1] = points[0].plus(points[dimension - 2]);
            points[dimension - 1].level = 1;
            marker = dimension - 2;
            newPoint = dimension - 1;
        }

        //initial Simplex for Chore division
        if (divType == "Chores") {
            for (int i = 0; i < dimension; i++) {
                points[i] = new Vertex(players - 1);
                for (int k = 0; k < players - 1; k++) {
                    points[i].elements[k] = 1;
                }
            }
            for (int col = 0; col < dimension - 1; col++) {
                for (int j = 0; j < col; j++) {
                    points[col].elements[j] = 0;
                }
                points[col].level = 0;
                points[col].label = col + 2;
            }
            points[dimension - 2].label = 1;
            points[dimension - 1] = points[0].plus(points[dimension - 2]);
            points[dimension - 1].level = 1;
            marker = dimension - 2;
            newPoint = dimension - 1;
        }
    }
    //initial Simplex for Rent division -- specified as a cutset space
    //then inverted using rentInvert()
    // if(divType == "Rent") {
	
	/*      Vertex temp[] = new Vertex[dimension];
	for(int i = 0; i < dimension; i++) {
	points[i] = new Vertex(players-1);
	}
	for(int i = 0; i < players; i++) {
	    temp[i] = new Vertex(players);
	    points[i].level = 0;
	    for(int j = 0; j < players; j++) {
		temp[i].elements[j] = 1;
	    }
	}
      for(int col = 0; col < players; col++) {
	temp[col].elements[col]= 2 - players;
      }
      for(int inv = 0; inv < dimension-1; inv++) {
	temp[inv].rentInvert();
	points[inv].label = inv+1;
      }
      for(int k = 0; k < players; k++) {
	  for (int l = 0; l < players-1; l++) {
	      points[k].elements[l] = temp[k].elements[l];
	  }
      } 
      points[dimension-1] = points[0].plus(points[dimension-2]);
      marker = dimension-2;
      newPoint = dimension-1;
      points[dimension-1].level = 1;
      }
      }*/

    //autoChoose automatically iterates the algorithm for Chore division, 
    //choosing the smallest piece in the Simplex as long as there is one that 
    //is negative or zero
    //returns true if it chose something, false otherwise

    public boolean autoChoose() {
        int temp = -1;
        for (int i = 0; i < points[newPoint].dimension + 1; i++) {
            if (points[newPoint].getTransform(i, divType) <= 0) {
                if (temp == -1) {
                    temp = i;
                } else {
                    if (points[newPoint].getTransform(i, divType) < points[newPoint].getTransform(temp, divType)) {
                        temp = i;
                    }
                }
            }
        }
        if (temp == -1)
            return false;
        else {
            points[newPoint].label = temp + 1;
            findPivot();
            return true;
        }
    }


    public boolean autoChoose(double bids[][]) {
        char person = points[newPoint].getOwner(divType);
        char alpha = 'A';
        int who = (int) person - (int) alpha;
        int temp = 0;
        for (int i = 1; i < points[newPoint].dimension + 1; i++) {
            if ((bids[who][i] - (totalRent * points[newPoint].getTransform(i, divType))) > (bids[who][temp] - (totalRent * points[newPoint].getTransform(temp, divType)))) {
                temp = i;
            }
        }
        points[newPoint].label = temp + 1;
        findPivot();
        return true;
    }


    //the main algorithm which updates the Simplex depending on where the 
    //the pivot is 
    //returns true if it has moved the simplex up a level

    public boolean bigAl() {

        //FIRST AND ONLY POINT IN LOWER LIST
        int changed = level;
        if (pivot == 0 && marker == 0) {
            shift(1, dimension - 1, -1);
            points[dimension - 1] = points[0].plus(points[dimension - 2]);
            newPoint = dimension - 1;
            marker = dimension - 2;
            level++;
            points[dimension - 1].level = level + 1;
        }
        //FIRST OR LAST BUT NOT ONLY VERTEX IN LOWER LIST
        else if ((marker != 0) && (pivot == 0 || pivot == marker)) {
            shift(pivot + 1, dimension - 1, -1);
            marker--;
            points[dimension - 1] = points[0].plus(points[marker]);
            newPoint = dimension - 1;
            points[newPoint].level = points[dimension - 2].level;
        }
        //INTERNAL TO EITHER LIST
        else if (pivot != 0 && pivot != marker && pivot != marker + 1 && pivot != dimension - 1) {
            points[pivot] = points[pivot - 1].plus(points[pivot + 1]).minus(points[pivot]);
            points[pivot].level = points[pivot - 1].level;
            newPoint = pivot;
        }
        //FIRST BUT BUT NOT ONLY VERTEX IN UPPER LIST
        else if (pivot == marker + 1 && pivot != dimension - 1) {
            Vertex temp = points[pivot + 1].times(2);
            points[pivot] = temp.minus(points[pivot]);
            points[pivot].level = level + 1;
            newPoint = pivot;
        }
        //ONLY VERTEX OF UPPER LIST
        else if (pivot == marker + 1 && pivot == dimension - 1) {
            shift(0, pivot - 1, 1);
            marker = 0;
            points[0] = points[dimension - 1].dividedBy(2);
            level--;
            points[0].level = level;
            newPoint = 0;
        }
        //LAST BUT NOT ONLY VERTEX OF UPPER LIST
        else if (pivot == dimension - 1 && marker < dimension - 2) {
            Vertex temp = points[pivot].minus(points[pivot - 1]);
            int check = temp.dotProduct(points[0]);
            if (check % 2 == 0) {
                shift(0, dimension - 2, 1);
                marker++;
                points[0] = points[dimension - 1].minus(points[marker]);
                points[0].level = points[1].level;
                newPoint = 0;
            } else {
                shift(marker + 1, dimension - 2, 1);
                marker++;
                points[marker] = points[dimension - 1].minus(points[0]);
                points[marker].level = level;
                newPoint = marker;
            }
        }
        return (changed != level);
    }

    //averages the transformations of the current simplex to get the 
    //suggested division
    public double[] suggestedTransform() {
        double ave[] = new double[players + 1];
        for (int i = 0; i < players; i++) {
            for (int j = 0; j < players + 1; j++) {
                if (j != newPoint) {
                    ave[i] += points[j].getTransform(i, divType);
                }
            }
        }
        for (int k = 0; k < players; k++) {
            ave[k] /= players;
        }
        return ave;
    }

    //suggestedOwner(int) gets the owner of the Vertex with a given label
    public char suggestedOwner(int seeking) {
        for (int i = 0; i < dimension; i++) {
            if (points[i].label == seeking && i != newPoint) {
                return points[i].getOwner(divType);
            }
        }
        return '~';
    }

    //findPivot() returns the new pivot after the simplex has been
    //updated by finding the vertex with the same label as newPoint
    public void findPivot() {
        int target = -1;
        for (int i = 0; i < dimension; i++) {
            if ((points[i].label == points[newPoint].label) && (i != newPoint)) {
                target = i;
            }
        }
        pivot = target;
    }

    //shift(start,end,where) slides the elements between  start to end
    //(inclusive) by a number(where)of  positions in the array

    public void shift(int start, int end, int where) {
        if (where > 0) {
            for (int i = end; i >= start; i--) {   //shift left
                points[i + where] = points[i];
            }
        }
        if (where < 0) {
            for (int i = start; i <= end; i++) {   //shift right
                points[i + where] = points[i];
            }
        }
    }

}






