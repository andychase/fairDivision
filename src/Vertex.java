//Vertex.java
//by Patrick Vinograd
//March 3, 1999

//Implements the Vertex class, which represents the
//points that make up a Simplex

import java.lang.*;

class Vertex {

    int dimension;
    int level;
    int label;
    char owner;
    int[] elements;  //the array of coordinates for the point

    //constructor with only a dimension given
    Vertex(int size) {
        this.dimension = size;
        this.elements = new int[size];
    }

    //constructor which specifies dimension and level
    Vertex(int size, int level) {
        this.dimension = size;
        this.elements = new int[size];
        this.level = level;
    }


    //gets the owner of the point
    //the number of odd coordinates is counted, and the corresponding letter
    //becomes the owner
    public char getOwner(String divType) {
        {
            int num = 0;
            for (int i = 0; i < this.dimension; i++) {
                if (elements[i] % 2 != 0) {
                    num++;
                }
            }
            int alpha = (int) 'A';
            alpha += num;
            return (char) alpha;
        }
    }

    //this is the transformation from configuration to cutset space
    //it first takes all the points and tacks a 2^level on the end
    //it then divides every point by 2^level
    //it then (starting at the end) gets each point by subtracting the 
    //previous point from itself.  
    public double[] transform(String divType) {
        if (divType == "Rent") {
            double cutset[] = new double[dimension + 1];
            for (int i = 0; i < dimension; i++) {
                cutset[i] = (float) elements[i];
            }
            cutset[dimension] = Math.pow((double) 2, (double) level);
            for (int i = 0; i < dimension + 1; i++) {
                cutset[i] /= Math.pow((double) 2, (double) level);
            }
            for (int i = dimension; i > 0; i--) {
                cutset[i] = (dimension * (cutset[i - 1] - cutset[i])) + 1;
            }
            cutset[0] = (-1 * dimension * cutset[0]) + 1;
            return cutset;
        } else {
            double cutset[] = new double[dimension + 1];
            for (int i = 0; i < dimension; i++) {
                cutset[i] = (float) elements[i];
            }
            cutset[dimension] = Math.pow((double) 2, (double) level);
            for (int i = 0; i < dimension + 1; i++) {
                cutset[i] /= Math.pow((double) 2, (double) level);
            }
            for (int i = dimension; i > 0; i--) {
                cutset[i] -= cutset[i - 1];
            }
            return cutset;
        }
    }

    //this gets the magnitude of the piece with number equal to index
    public double getTransform(int index, String divType) {
        double cutset[] = transform(divType);
        return cutset[index];
    }

    //if no argument is specified, takes this vertex's transfrom array
    //and runs it through the following method
    public double[] textTransform(String divType) {
        double cuts[] = transform(divType);
        return textTransform(cuts);
    }

    //this allows any cutset to be entered, and returns the rounded 
    //amounts of the cuts for display in the history window
    //needed for use in suggested divisions, where the average cutset
    //is required
    public double[] textTransform(double dcuts[]) {
        //double dcuts[] = transform();
        double tcuts[] = new double[dimension + 2];
        tcuts[0] = 0;
        for (int i = 1; i < dimension + 2; i++) {
            tcuts[i] = tcuts[i - 1] + (dcuts[i - 1] * 100);
        }
        for (int i = 0; i < dimension + 2; i++) {
            tcuts[i] = Math.round(100 * tcuts[i]) / 100.0;
        }
        return tcuts;
    }

    //gets the cutset space for this vertex and sends it to the next method
    public int[] imageTransform(int imageWidth, String divType) {
        double cuts[] = transform(divType);
        return imageTransform(cuts, imageWidth);
    }

    //for any given array of cuts, returns the cuts scaled to the width of
    //the graphics, and represented as integers for use by painting methods
    public int[] imageTransform(double dcuts[], int imageWidth) {
        //double dcuts[] = transform();
        int icuts[] = new int[dimension + 2];
        icuts[0] = 0;
        for (int i = 1; i < dimension + 2; i++) {
            icuts[i] = icuts[i - 1] + (int) (dcuts[i - 1] * imageWidth);
        }
        return icuts;
    }

    //checks if two vertices are equal (contain all the same points)
    public boolean equals(Vertex v) {
        if (this.dimension == v.dimension) {
            for (int i = 0; i < dimension; i++) {
                if (this.elements[i] != v.elements[i])
                    return false;
            }
            return true;
        }
        return false;
    }

    //returns the vector sum of two vertices (this + v)
    public Vertex plus(Vertex v) {
        Vertex sum = new Vertex(dimension);
        for (int i = 0; i < dimension; i++) {
            sum.elements[i] = elements[i] + v.elements[i];
        }
        return sum;
    }

    //returns the difference of two vertices (this - v)
    public Vertex minus(Vertex v) {
        Vertex difference = new Vertex(dimension);
        for (int i = 0; i < dimension; i++) {
            difference.elements[i] = elements[i] - v.elements[i];
        }
        return difference;
    }

    //multiplies this vertex by the scalar scale  (this*scale)
    public Vertex times(int scale) {
        Vertex product = new Vertex(dimension);
        for (int i = 0; i < dimension; i++) {
            product.elements[i] = elements[i] * scale;
        }
        return product;
    }

    //divides this vertex by a scalar (this/scale)
    public Vertex dividedBy(int scale) {
        Vertex product = new Vertex(dimension);
        for (int i = 0; i < dimension; i++) {
            product.elements[i] = elements[i] / scale;
        }
        return product;
    }

    //returns the vector dotProduct of this vertex and v
    public int dotProduct(Vertex v) {
        int total = 0;
        for (int i = 0; i < dimension; i++) {
            total += elements[i] * v.elements[i];
        }
        return total;
    }

}
