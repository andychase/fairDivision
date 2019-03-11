//HybridSlate.java
//by Patrick Vinograd

//a slate which contains controls, graphics and Java's built-in components

import java.applet.*;
import java.awt.*;
import java.net.*;
import java.lang.*;

class QuerySlate {
    int x, y;
    int width, height;
    boolean visible;
    Control members[];
    int howMany, num;
    Component comps[];
    Color myColor;
    Color color = new Color(153, 156, 212);
    Color dcolor = new Color(69, 71, 128);
    Color lcolor = new Color(255, 255, 219);
    String text;
    int text_x;
    int text_y;
    Font font;
    FontMetrics fontM;

    //constructor takes dimensions
    QuerySlate(int x, int y, int w, int h) {
        this.font = new Font("Dialog", Font.PLAIN, 12);
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        myColor = lcolor;
        members = new Control[10];
        comps = new Component[3];
        num = 0;
        howMany = 0;
    }

    //add one of Java's built in components UNUSED because of redrawing 
    //problems
    public void add(Component comp) {
        comps[num] = comp;
        num++;
    }

    //add a control
    public void add(Control cont) {
        members[howMany] = cont;
        howMany++;
    }

    //checks if the given coordinates are inside the boundaries
    public boolean contains(int xpos, int ypos) {
        if (visible && (xpos >= x && xpos <= x + width) && (ypos >= y && ypos <= y + height))
            return true;
        else
            return false;
    }

    //checks which control the mouse click occured in
    public String which(int xpos, int ypos) {
        for (int i = 0; i < howMany; i++) {
            if (members[i].contains(xpos - x, ypos - y)) {
                return members[i].title;
            }
        }
        return "";
    }

    //the title graphic...if there is an image, draw that, otherwise write 
    //text at the top of the window
    public void drawText(Graphics g, Applet app) {
        g.setColor(dcolor);
        g.setFont(font);
        fontM = app.getFontMetrics(font);
        g.drawString(text, (x + width / 2 - fontM.stringWidth(text) / 2), text_y);
    }

    //redraws the members of the slate, as well as the appropriate title
    //graphic or text
    public void paint(Graphics g, Applet app) {
        if (visible) {
            g.setColor(myColor);
            g.fillRect(x, y, width, height);
            drawText(g, app);
            for (int i = 0; i < howMany; i++) {
                members[i].paint(x, y, g, app);
            }
            for (int j = 0; j < num; j++) {
                comps[j].paint(g);
            }
        }
    }


}


