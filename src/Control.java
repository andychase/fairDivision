//Control.java
//by Patrick Vinograd
//March 4, 1999
// modified by Francis Su on 4/1/00

//Implements platform independent buttons

import java.applet.*;
import java.awt.*;

class Control {

    int x, y;
    int width, height;
    String title;
    Font font;
    FontMetrics fontM;
    Color color = new Color(153, 156, 212);
    //FS: should be      Color dcolor = new Color(69,71,128);
    //FS: turquoise is  0-180-180
    //FS: dark purple is  160-72-128
    Color dcolor = new Color(0, 160, 160);
    Color lcolor = new Color(255, 255, 219);
    Color disColor = new Color(163, 163, 204);
    Color dislColor = new Color(255, 255, 255);
    //FS: originally       Color redColor = new Color(216,0,0);
    Color redColor = new Color(255, 0, 128);
    Color myColor;
    Color myBackground;
    boolean enabled;
    String altText;
    boolean ALTFLAG;
    String secondLine;
    boolean SECFLAG;

    //constructor with only a name for the control given
    Control(String str) {
        this.title = str;
        this.font = new Font("Dialog", Font.PLAIN, 12);
        //this.fontM = getFontMetrics(getFont());
        this.x = 0;
        this.y = 0;
        this.width = 10;
        this.height = 10;
        myColor = dcolor;
        myBackground = lcolor;
        enabled = true;
        ALTFLAG = false;
    }

    //constructor for string and dimensions
    Control(String str, int x, int y, int w, int h) {
        this.title = str;
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.font = new Font("Dialog", Font.PLAIN, 12);
        myColor = dcolor;
        myBackground = lcolor;
        enabled = true;
    }

    //checks if the given coordinates (i.e. a mouse click) are inside
    //the boundaries of the control.  The control must also be enabled
    public boolean contains(int xpos, int ypos) {
        int ex = x + 2;
        int ey = y + 2;
        int ewidth = width - 4;
        int eheight = height - 4;
        if (enabled && (xpos >= ex && xpos <= ex + ewidth) && (ypos >= ey && ypos <= ey + eheight)) {
            return true;
        } else
            return false;
    }

    //inverts the default color scheme
    public void invert() {
        myColor = lcolor;
        myBackground = dcolor;
    }


    //actually draws the button, with a filled rectangle, some shadow and
    //highlight lines, and the button's title
    public void paint(int px, int py, Graphics graphics, Applet app) {
        int ex = x + px + 2;
        int ey = y + py + 2;
        int ewidth = width - 4;
        int eheight = height - 4;
        graphics.setColor(myBackground);
        graphics.fillRect(ex, ey, ewidth - 1, eheight - 1);
        graphics.setColor(Color.white);
        graphics.drawLine(ex, ey, ex + ewidth - 1, ey);
        graphics.drawLine(ex, ey, ex, ey + eheight);
        graphics.setColor(Color.black);
        graphics.drawLine(ex + 1, ey + eheight, ex + ewidth, ey + eheight);
        graphics.drawLine(ex + ewidth, ey, ex + ewidth, ey + eheight);
        graphics.setColor(myColor);
        graphics.setFont(font);
        fontM = app.getFontMetrics(font);
        if (SECFLAG) {
            if (ALTFLAG) {
                graphics.drawString(altText,
                        (ex + ewidth / 2 - fontM.stringWidth(altText) / 2),
                        (ey + eheight / 2 - 1));
            } else {
                graphics.drawString(title,
                        (ex + ewidth / 2 - fontM.stringWidth(title) / 2),
                        (ey + eheight / 2 - 1));
            }
            graphics.drawString(secondLine,
                    (ex + ewidth / 2 - fontM.stringWidth(secondLine) / 2),
                    (ey + eheight / 2 + fontM.getAscent() + 1));
        } else {
            if (ALTFLAG) {
                graphics.drawString(altText,
                        (ex + ewidth / 2 - fontM.stringWidth(altText) / 2),
                        (ey + eheight / 2 + fontM.getAscent() / 2));
            } else {
                graphics.drawString(title,
                        (ex + ewidth / 2 - fontM.stringWidth(title) / 2),
                        (ey + eheight / 2 + fontM.getAscent() / 2));
            }
        }

    }

    //enables the control, allowing it to receive mouse clicks
    public void enable() {
        enabled = true;
        myBackground = redColor;
        myColor = lcolor;
    }

    //disables the control, dimming it and ignoring mouse clicks
    public void disable() {
        enabled = false;
        myBackground = disColor;
        myColor = dislColor;
    }

}




