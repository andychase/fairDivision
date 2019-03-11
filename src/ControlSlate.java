//ControlSlate.java
//by Patrick Vinograd
//March 3, 1999

//a Slate to contain controls

import java.applet.*;
import java.awt.*;

class ControlSlate {
    int x, y;
    int width, height;
    boolean visible;
    Control members[];
    int howMany;
    Color myColor;
    Color color = new Color(153, 156, 212);
    Color dcolor = new Color(69, 71, 128);
    Color lcolor = new Color(255, 255, 219);

    //constructor takes dimensions of slate
    ControlSlate(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        myColor = color;
        members = new Control[10];
        howMany = 0;
    }

    //adds a control to the list of this slate's controls
    public void add(Control cont) {
        members[howMany] = cont;
        howMany++;
    }

    //checks if the given coordinates are inside the window
    public boolean contains(int xpos, int ypos) {
        if (visible && (xpos >= x && xpos <= x + width) && (ypos >= y && ypos <= y + height))
            return true;
        else
            return false;
    }

    //goes through this slate's members and finds which contained the 
    //mouse click
    public String which(int xpos, int ypos) {
        for (int i = 0; i < howMany; i++) {
            if (members[i].contains(xpos - x, ypos - y)) {
                return members[i].title;
            }
        }
        return "";
    }

    //draw a filled rectangle, then paint each member
    public void paint(Graphics g, Applet app) {
        if (visible) {
            g.setColor(myColor);
            g.fillRect(x, y, width, height);
            for (int i = 0; i < howMany; i++) {
                members[i].paint(x, y, g, app);
            }
        }
    }


}


