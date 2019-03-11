//HybridSlate.java
//by Patrick Vinograd

//a slate which contains controls, graphics and Java's built-in components

import java.applet.*;
import java.awt.*;
import java.net.*;
import java.lang.*;

class HybridSlate {
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
    Image title;

    //constructor takes dimensions
    HybridSlate(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        myColor = color;
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
    //FS: this changes the size of the title image-- originally 150x60.
    public void drawTitle(Graphics g, Applet app) {
        if (title != null) {
            g.drawImage(title, 0, 0, 150, 90, app);
        } else {
            Font titleFont = new Font("Helvetica", Font.BOLD, 24);
            FontMetrics fontM = app.getFontMetrics(titleFont);
            String s1 = new String("The");
            String s2 = new String("Fair Division");
            String s3 = new String("Calculator");
            g.setColor(lcolor);

            g.drawString(s1, (width / 2 - fontM.stringWidth(s1) / 2), (fontM.getAscent() + 3));
            g.drawString(s2, (width / 2 - fontM.stringWidth(s2) / 2), ((2 * fontM.getAscent()) + 3));
            g.drawString(s3, (width / 2 - fontM.stringWidth(s3) / 2), ((3 * fontM.getAscent()) + 3));
        }
    }

    //redraws the members of the slate, as well as the appropriate title
    //graphic or text
    public void paint(Graphics g, Applet app) {
        if (visible) {
            g.setColor(myColor);
            g.fillRect(x, y, width, height);
            drawTitle(g, app);
            for (int i = 0; i < howMany; i++) {
                members[i].paint(x, y, g, app);
            }
            for (int j = 0; j < num; j++) {
                comps[j].paint(g);
            }
        }
    }


}


