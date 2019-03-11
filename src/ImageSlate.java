import java.applet.*;
import java.awt.*;
import java.lang.*;

//This is the class for the main graphics display window

class ImageSlate {
    int x, y;
    int width, height;
    boolean visible;
    Image image;
    int imagex, imagey;
    int imageWidth, imageHeight;
    Color color;
    int cuts[];

    //constructor takes the window's dimensions and the dimensions of the
    //image
    ImageSlate(int x, int y, int w, int h, int ix, int iy, int iw, int ih) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.imagex = ix;
        this.imagey = iy;
        this.imageWidth = iw;
        this.imageHeight = ih;
        color = Color.white;
        visible = true;
    }

    //if not cuts are specified, it passes the newPoint's cuts to the next
    //method
    public void drawCuts(Simplex divvy, Graphics g) {
        int cuts[] = divvy.points[divvy.newPoint].imageTransform(imageWidth, divvy.divType);
        drawCuts(cuts, divvy, g);
    }

    //draws lines to represent the given cuts on the image, and labels each
    //resultant piece with numbers
    //allows the average cutset to be passed
    public void drawCuts(int cuts[], Simplex divvy, Graphics g) {
        int xo = x + imagex;
        int yo = y + imagey - 5;
        int heighto = yo + imageHeight + 10;
        g.setColor(Color.black);
        //int cuts[] = divvy.points[divvy.newPoint].imageTransform(imageWidth, divvy.divType);
        for (int i = 0; i < divvy.players + 1; i++) {
            g.drawLine(xo + cuts[i], yo, xo + cuts[i], heighto);
        }
        for (int i = 1; i < divvy.players + 1; i++) {
            String lb = new String("" + (i));
            if ((cuts[i] - cuts[i - 1]) != 0) {
                g.drawString(lb, (xo + cuts[i - 1] + (cuts[i] - cuts[i - 1]) / 2), yo - 2);
            }
        }
    }

    //prints the precision of the division in the lower left corner of the
    //window, rounded to 2 digits
    public void drawPrecision(Simplex divvy, Graphics g) {
        double prec = Math.round((1 / Math.pow((double) 2, (double) divvy.level)) * 10000) / 100.0;
        if (divvy.divType == "Rent") {
            prec = Math.round(prec * divvy.totalRent) / 100.0;
            prec *= 2;
        }
        g.setColor(Color.black);
        if (divvy.divType == "Rent")
            g.drawString("Precision: $" + prec, x + 2, y + height - 2);
        else
            g.drawString("Precision: " + prec, x + 2, y + height - 2);
    }

    //takes the location of a mouse click and finds which piece
    //it corresponds to
    public int getChoice(Vertex v, int imageWidth, int click, String divType) {
        int cuts[] = v.imageTransform(imageWidth, divType);
        for (int i = 0; i < v.dimension + 1; i++) {
            if (click > x + imagex + cuts[i] && click <= x + imagex + cuts[i + 1])
                return (i + 1);
        }
        return 0;
    }

    //checks if the given points are inside the bounds of the window
    public boolean contains(int xpos, int ypos) {
        if (visible && (xpos >= x && xpos <= x + width) && (ypos >= y && ypos <= y + height))
            return true;
        else
            return false;
    }

    //redraw method: draws the image, then, if there is a division going,
    //draws either the cuts or the suggested cuts, and the precision
    //(for rent, only the precision and the image are drawn)
    public void paint(Graphics g, fairDivision app) {
        if (visible) {
            if (image != null) {
                g.setColor(color);
                g.fillRect(x, y, width, height);
                g.drawImage(image, x + imagex, y + imagey, imageWidth, imageHeight, app);
            }
            if (app.working) {
                if (app.divType != "Rent") {
                    if (app.proposing) {
                        drawCuts(app.divvy.dummy.imageTransform(app.divvy.suggestedTransform(), imageWidth), app.divvy, g);
                    } else {
                        drawCuts(app.divvy, g);
                    }
                }
                if (app.rentType != "HRS") {
                    drawPrecision(app.divvy, g);
                }
            }
        }
    }

}
