//fairDivision.java
//by Patrick Vinograd
//February 12, 1999
//  modified 5/1/00 by Francis E. Su

//The fairDivision class is the main applet.  It handles the user interface and
//manages the other classes.  


import java.lang.*;
import java.awt.*;
import java.applet.*;
import java.net.*;

public class fairDivision extends Applet {

    //global variables and objects

    Image image;              //offscreen buffered image
    Graphics graphics;
    Simplex divvy;
    HybridSlate inputPanel;
    Panel outputPanel;
    ImageSlate displayPanel;
    ControlSlate controlPanel;
    ControlSlate configPanel;
    ControlSlate rentPanel;
    TextArea history;
    TextField choice;
    Control reStart;
    Control aboutText;
    Control clearText;
    //FS: added next
    Control thanksText;
    Control enterData;
    Control suggest;
    Color color = new Color(153, 156, 212);    //light purple
    Color dcolor = new Color(69, 71, 128);     //dark purple
    Color lcolor = new Color(255, 255, 219);   //yellow
    Color tanColor = new Color(255, 235, 148);
    Color backgroundColor = color;
    //FS: added text for thanksApplet
    String thanksApplet = new String(
            "\n\n        ----- ACKNOWLEDGEMENTS -----\n" +
                    "   Thanks to: F. Simmons, M. Starbird, D. Mazzoni,\n" +
                    "   M. Raith, C.J. Haake, HMC Research Grants 1998-2000,\n" +
                    "   and MOST OF ALL, Elisha Peterson and Patrick Vinograd.\n" +
                    "   This applet would not have been possible without\n" +
                    "   Elisha's initial craftsmanship and Patrick's technical\n" +
                    "   wizardry and dedication to this project over the\n" +
                    "   last couple of years.\n" +
                    "              -------Francis Edward Su,  April 2000\n");
    String aboutApplet = new String(
            "\n\n   ----- THE FAIR DIVISION CALCULATOR v.3.02 -----\n" +
                    //"     A java applet for envy-free solutions!\n\n"+
                    "  Project Director: Francis Edward Su\n" +
                    "  Applet Coding and Design:\n" +
                    "       Patrick Vinograd (v.2.0-3.0), Elisha Peterson (v.1.0)\n" +
                    "  Sperner Algorithms: Forest Simmons and Francis Su\n" +
                    "  HRS: Claus-Jochen Haake, Matthias Raith, and F.E. Su\n" +
                    "       Comments, feedback welcome:  su@math.hmc.edu\n" +
                    "       May not be reproduced without permission.\n" +
                    "   ------- (c) 1998-2000 by Francis Edward Su -------\n");
    Label command;
    Label credit;
    Label howMany;
    FontMetrics textFontM;
    //FS: define font
    Font textFont;
    int width, height;
    boolean working;             //running a division or not?
    boolean proposing;           //suggesting a division or not?
    boolean graduated;           //moved up a level or not?
    int players;
    String divType;              //"Rent" "Chores" or "Cake"
    Image picture;               //the appropriate graphic
    Image title;                 //the title graphic
    MediaTracker tracker;
    boolean gettingRent;         //getting the total rent or not?
    int totalRent;
    boolean needBids;

    //ARTUS
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

    int whoBidding;
    char alpha = 'A';
    TextField bidField;
    Control bidB;
    QuerySlate bidPanel;
    boolean algType;
    String rentType;
    double surplus;
    Checkbox howSurplus;
    boolean aveDisMethod;
    //CHANGE
    Checkbox cb1, cb2, cb3;
    boolean exAnte;

    //FS: I'm adding this global variable in case bids are too small.
    // Is this bad form?  See how it is used later.  Search on bidsTooSmall.
    boolean bidsTooSmall;

    //ARTUS

    hrsUtil hrsDivision;

    // called when the applet is first loaded, init() just makes sure the
    //applet gets set up correctly

    public void init() {
        setLayout(null);
        makeImageBuffer();
        setBackground(lcolor);
        width = Integer.parseInt(getParameter("width"));
        height = Integer.parseInt(getParameter("height"));
        //textFont = new Font("Courier", Font.PLAIN, 10);
        //FS: to change the font
        textFont = new Font("Helvetica", Font.PLAIN, 11);
        working = false;
        addPanels();
        getConfig();
        //FS: switched order of the next 2 lines
        makeAuction();
        howRent();

        setup();
        //FS: added this line so that choice window active on startup
        choice.requestFocus();
        repaint();
    }

    //setup() is called each time the applet is restarted
    //any setting that needs to be reset for a new division should go here

    public void setup() {
        players = -1;
        totalRent = -1;
        divType = null;
        displayPanel.image = null;
        displayPanel.visible = false;
        configPanel.visible = true;
        //howMany.show();
        choice.setText("");
        history.appendText("\n\n       =======* STARTING NEW DIVISION *=======\n");
        //history.appendText("\n*STARTING NEW DIVISION*\n");
        history.appendText("Please enter the number of players, press 'Enter',\n");
        history.appendText("and press a button for a division type.\n");
        history.appendText("Then follow the instructions in this window.\n\n");
        suggest.title = "Suggest Division";
        suggest.disable();
        //FS: added this next line with new variable
        bidsTooSmall = false;
        proposing = false;
        working = false;
        gettingRent = false;
        algType = false;
        needBids = false;
        rentPanel.visible = false;
        bidPanel.visible = false;
        repaint();
    }

    //adds the four main parts of the window to the applet
    //inputPanel: (top left) title graphic, data entry, two controls
    //outputPanel: (bottom left) history window
    //displayPanel: (top right) graphics 
    //controlPanel: (bottom right) three controls

    public void addPanels() {
        inputPanel = new HybridSlate(0, 0, 150, 150);
        inputPanel.visible = true;
        displayPanel = new ImageSlate(150, 0, 300, 150, 50, 25, 200, 100);
        outputPanel = new Panel();
        //FS: sizes history window (0,150,350x150)
        outputPanel.reshape(0, 150, 350, 200);
        outputPanel.setBackground(Color.white);
        //FS: uncommented the next line to change font
        outputPanel.setFont(textFont);
        outputPanel.setLayout(null);
        this.add(outputPanel);
        //FS: needed resize this to add Thanks.  Old: (350,150,100,150)
        controlPanel = new ControlSlate(350, 150, 100, 200);
        controlPanel.visible = true;
        addElements();
    }

    //adds the user interface components to the four panels

    public void addElements() {
        reStart = new Control("Restart", 0, 0, 100, 50);
        //FS: I switched the positions of About and Clear buttons, added Thanks
        clearText = new Control("Clear", 0, 50, 100, 50);
        aboutText = new Control("About", 0, 100, 100, 50);
        thanksText = new Control("Thanks", 0, 150, 100, 50);
        controlPanel.add(aboutText);
        controlPanel.add(reStart);
        controlPanel.add(clearText);
        //FS: added next line
        controlPanel.add(thanksText);
        textFontM = outputPanel.getFontMetrics(outputPanel.getFont());
        history = new TextArea();
        outputPanel.add(history);
        //FS sizes history window 350x150;
        history.reshape(0, 0, 350, 200);
        history.setEditable(false);
        choice = new TextField(10);
        choice.reshape(0, 90, 75, 30);
        this.add(choice);
        //showStatus(choice.getFont().toString());
        enterData = new Control("Enter", 75, 90, 75, 30);
        inputPanel.add(enterData);
        suggest = new Control("Suggest Division", 0, 120, 150, 30);
        inputPanel.add(suggest);
        suggest.disable();
        //loading title graphic below
        try {
            URL place = new URL(getDocumentBase(), "title.gif");
            Toolkit tk = Toolkit.getDefaultToolkit();
            title = tk.getImage(place);
        } catch (MalformedURLException mal) {
        }
        try {
            tracker.addImage(title, 0);
            tracker.waitForAll();
        } catch (Exception except) {
        }
        inputPanel.title = title;
    }

    //initializes configPanel and gets the configuration (players, type of division) for
    //this division

    public void getConfig() {
        configPanel = new ControlSlate(150, 0, 300, 150);
        displayPanel.visible = false;
        configPanel.visible = true;
        Control cakeB = new Control("Cake", 0, 0, 100, 150);
        Control choreB = new Control("Chores", 100, 0, 100, 150);
        Control rentB = new Control("Rent", 200, 0, 100, 150);
        choreB.invert();
        cakeB.invert();
        rentB.invert();
        cakeB.altText = "Goods";
        choreB.altText = "Burdens";
        cakeB.ALTFLAG = true;
        choreB.ALTFLAG = true;
        configPanel.add(choreB);
        configPanel.add(cakeB);
        configPanel.add(rentB);
    }

    //CHANGE
    public void makeAuction() {
        bidPanel = new QuerySlate(150, 0, 300, 150);
        bidField = new TextField(260);
        bidField.reshape(175, 35, 200, 30);
        this.add(bidField);
        bidB = new Control("Bid", 225, 35, 50, 30);
        bidPanel.add(bidB);
        bidPanel.text_y = 30;
        CheckboxGroup hrsMethodChoice = new CheckboxGroup();
        //FS: renamed checkboxes below
        cb1 = new Checkbox("with equal distribution of surplus",
                hrsMethodChoice, false);
        cb2 = new Checkbox("with average discount method",
                hrsMethodChoice, true);
        cb3 = new Checkbox("with ex-post equal payments & cycling",
                hrsMethodChoice, false);
        //howSurplus = new Checkbox("Average Discount Method");
        //this.add(howSurplus);
        //howSurplus.reshape(160,130,220,20);
        graphics.setColor(dcolor);
        //FS: changed lengths in next 3 lines from 220 to 260
        cb1.reshape(160, 70, 260, 25);
        cb2.reshape(160, 95, 260, 25);
        cb3.reshape(160, 120, 260, 25);
        this.add(cb1);
        this.add(cb2);
        this.add(cb3);
    }

    public void howRent() {
        rentPanel = new ControlSlate(300, 0, 150, 150);
        Control intSperner = new Control("Interactive", 0, 0, 150, 50);
        Control autoSperner = new Control("Auto-Choose", 0, 50, 150, 50);
        Control hRS = new Control("HRS", 0, 100, 150, 50);
        intSperner.invert();
        autoSperner.invert();
        hRS.invert();
        //version2.5
        //hRS.disable();
        rentPanel.add(intSperner);
        rentPanel.add(autoSperner);
        rentPanel.add(hRS);
        intSperner.secondLine = "Sperner";
        intSperner.SECFLAG = true;
        autoSperner.secondLine = "Sperner";
        autoSperner.SECFLAG = true;
        //version2.5
        //hRS.secondLine = "[coming soon]";
        //hRS.SECFLAG = true;
    }


    //mouse handling routine finds the component where the mouse was clicked and responds
    //appropriately

    public boolean mouseUp(Event e, int x, int y) {
        choice.requestFocus();
        if (inputPanel.contains(x, y)) {
            String si = inputPanel.which(x, y);

            if (si == "Suggest Division" && working) {
                proposing = true;
                suggest.title = "Continue Iteration";
                suggestDivision();
                repaint();
                return true;
            }
            if (si == "Continue Iteration") {
                proposing = false;
                suggest.title = "Suggest Division";
                choice.setText("");
                history.appendText("--- CONTINUING ITERATION ---\n\n");
                tellCuts();
                repaint();
                return true;
            }

            //if the ENTER button is pressed

            if (si == "Enter") {

                //if we're getting the total rent, check to see if the number in
                //choice is a positive integer, and if so, begin the division
                if (gettingRent) {
                    try {
                        totalRent = Integer.parseInt(choice.getText());
                    } catch (NumberFormatException exc) {
                        choice.setText("");
                        history.appendText("Please enter a positive number of dollars.\n");
                        totalRent = -1;
                        return true;
                    }
                    if (totalRent < 0) {
                        choice.setText("");
                        history.appendText("Please enter a positive rent.\n");
                        totalRent = -1;
                        return true;
                    }
                    history.appendText("   Total rent: $" + totalRent + ".\n");
                    gettingRent = false;
                    // working = true;
                    choice.setText("");
                    algType = true;
                    //howRent();
                    bids = new double[players][];
                    configPanel.visible = false;
                    rentPanel.visible = true;
                    makeSimplex();
                    history.appendText("Please select one of the algorithms at top right.\n" +
                            "   * Interactive Sperner --- iterative, useful for arbitrary\n" +
                            "      player preferences.\n" +
                            "   * Auto-Choose Sperner --- accepts bids, useful if\n" +
                            "      you can assume linear utility in money.\n" +
                            "   * HRS --- bidding/compensation procedure that mimics\n" +
                            "      natural mediation process (computer not needed).\n");
                    repaint();
                    return true;
                }

                //if we're still setting up (don't have a number of players)
                //see if the input is valid, and use that as number of players
                if (players == -1) {
                    try {
                        players = Integer.parseInt(choice.getText());
                    } catch (NumberFormatException exc) {
                        choice.setText("");
                        history.appendText("Between 2 and 26 players please.\n");
                        return false;
                    }
                    if (players < 2 || players > 26) {
                        choice.setText("");
                        history.appendText("Between 2 and 26 players please.\n");
                        players = -1;
                        return false;
                    }
                    // howMany.hide();
                    choice.setText("");
                    history.appendText("   " + players + " players selected.\n");
                    areYouReady();
                    return true;
                }

                //if we're in a division, and the input in choice is a valid
                //piece selection for this division, accept it and run the algorithm
                if (working && !proposing) {
                    int input = 0;
                    try {
                        input = Integer.parseInt(choice.getText());
                    } catch (NumberFormatException exc) {
                        history.appendText(" Not a valid choice.\n");
                        choice.setText("");
                        return true;
                    }
                    if (input < 1 || input > players) {
                        history.appendText(" Not a valid choice.\n");
                        choice.setText("");
                        return true;
                    }

                    if (divType == "Cake" && divvy.points[divvy.newPoint].getTransform(input - 1, divType) == 0) {
                        history.appendText(" Not a valid choice.\n");
                        choice.setText("");
                        return true;
                    }

                    //set choice to newPoint's label, find the pivot, and run the
                    //algorithm to update the Simplex
                    divvy.points[divvy.newPoint].label = input;
                    history.appendText(" [" + divvy.points[divvy.newPoint].label + "] was chosen.\n\n");
                    divvy.findPivot();
                    graduated = divvy.bigAl();

                    //if this is chore division, let the autoChoose run as long as
                    //there is a zero sized piece
                    if (divType == "Chores") {
                        while (divvy.autoChoose()) {
                            graduated = divvy.bigAl();
                        }
                    }

                    //enable the suggest division button if we've gone up a level
                    if (graduated) {
                        suggest.enable();
                    } else {
                        suggest.disable();
                    }
                    tellCuts();
                    choice.setText("");
                    repaint();
                    return true;
                }
            }
        }

        //if mouse is clicked in controlPanel, either show the about text,
        //clear the history, or restart the applet, depending on which button
        if (controlPanel.contains(x, y)) {
            String s = controlPanel.which(x, y);
            if (s == "About") {
                history.appendText(aboutApplet);
                return true;
            }
            if (s == "Clear") {
                history.setText("");
                return true;
            }
            if (s == "Restart") {
                setup();
                return true;
            }
            //FS: what the Thanks button does
            if (s == "Thanks") {
                // thanks
                history.appendText(thanksApplet);
                return true;
            }
        }

        //if we're still setting up, this selects the type of division
        if (configPanel.contains(x, y)) {
            String sc = configPanel.which(x, y);
            if (sc != "") {
                divType = sc;
                //history.appendText("   "+ divType+" division selected.\n\n");
                if (divType == "Cake") {
                    history.appendText(
                            "   Division of goods (e.g., cake) selected.\n" +
                                    "Please place the goods along a linear scale from 0 to 100.\n" +
                                    "   Suggested divisions below will use parallel knives\n" +
                                    "   along this scale. (The graphic above is just an example.)\n" +
                                    "Pressing the 'Suggest Division' button when it turns\n" +
                                    "   red gives a solution good to displayed precision.\n");
                } else if (divType == "Chores") {
                    history.appendText(
                            "Division of burdens (e.g., chores) selected.\n" +
                                    "Please place burdens along a linear scale from 0 to 100.\n" +
                                    "   Suggested divisions below will use parallel knives\n" +
                                    "   along this scale. (The graphic above is just an example.)\n" +
                                    "Pressing the 'Suggest Division' button when it turns\n" +
                                    "   red gives a solution good to displayed precision.\n");
                } else {
                    history.appendText("   Rent division selected.\n");
                }
                //FS: I added this if statement (next 3 lines)
                //  to handle the case where user doesn't read instructions.
                if (players == -1) {
                    history.appendText("\nPlease enter number of players and press 'Enter'.\n");
                }
                configPanel.visible = false;
                areYouReady();
                repaint();
                return true;
            }
        }

        if (rentPanel.contains(x, y)) {
            String pressed = rentPanel.which(x, y);
            rentType = pressed;
            if (pressed == "Interactive") {
                history.appendText(
                        "\n-----------------\n" +
                                "Proceeding with Interactive Sperner Algorithm.\n" +
                                "Calculator will suggest possible rents; please\n" +
                                "choose the room you most prefer at those prices.\n" +
                                "(Negative rents mean being paid to take a room.)\n" +
                                "   Pressing the 'Suggest Division' button when it turns\n" +
                                "red gives a solution good to displayed precision.\n\n");
                algType = false;
                working = true;
                //rentPanel.visible = false;
                displayPanel.visible = true;
                initSimplex();
                startDivision();
                //makeSimplex();
                return true;
            }
            if (pressed == "Auto-Choose") {
                history.appendText(
                        "\n-----------------\n" +
                                "Proceeding with Auto-Choose Sperner Algorithm.\n" +
                                "This option allows players to make bids on rooms, and\n" +
                                "runs the Sperner algorithm for an envy-free outcome\n" +
                                "based on the surplus [bid]-[cost].  (Doing this assumes\n" +
                                "that your utility for money is linear---to avoid this\n" +
                                "assumption, use the Interactive Sperner option.)\n" +
                                "     Please enter bids, separated by commas,\n" +
                                "in order of the room numbers in the window above.\n");
                //FS: added next line
                suggest.disable();
                needBids = true;
                rentPanel.visible = false;
                bidPanel.visible = true;
                startBids();
                return true;
            }
            if (pressed == "HRS") {
                history.appendText(
                        "\n-----------------\n" +
                                "Proceeding with the HRS algorithm.\n" +
                                "Players make bids on rooms, and envy-free outcome is\n" +
                                "obtained by a series of compensations.  Envy is\n" +
                                "based the excess [bid]-[cost] (assumes quasi-linear\n" +
                                "utilities).  Choose a sub-option for dealing with\n" +
                                "the surplus after envy is eliminated.\n" +
                                "     Then please enter bids, separated by commas,\n" +
                                "(in order of room numbers) in the top right window.\n");
                //FS: added next line
                suggest.disable();
                needBids = true;
                rentPanel.visible = false;
                bidPanel.visible = true;
                startBids();
                return true;
            }
        }

        if (bidPanel.contains(x, y)) {
            String pressed = bidPanel.which(x, y);
            if (pressed == "Bid") {
                if (parseString(bidField.getText())) {
                    whoBidding++;
                    if (whoBidding == players) {
                        //aveDisMethod = howSurplus.getState();
                        if (cb1.getState()) {
                            exAnte = true;
                            aveDisMethod = false;
                        } else if (cb2.getState()) {
                            //FS: added the if statement in case bids are too small
                            //  originally it was just the 2 lines in the else part:
                            //  exAnte = true;
                            //  aveDisMethod = true;
                            if (bidsTooSmall && rentType == "HRS") {
                                exAnte = true;
                                aveDisMethod = false;
                                history.appendText("     Bids did not sum to total rent for all players.\n");
                                history.appendText("***  Using equal distribution of surplus instead. ***\n");
                            } else {
                                exAnte = true;
                                aveDisMethod = true;
                            }
                        } else {
                            exAnte = false;
                            aveDisMethod = false;
                        }
                        //FS: moved the next history/for statements from
                        // where it was-- before the previous if-else-else.
                        history.appendText("     Successfully got all bids:\n\n");
                        for (int i = 0; i < players; i++) {
                            history.appendText("     " + bids[i][0]);
                            for (int j = 1; j < players; j++) {
                                history.appendText(", " + bids[i][j]);
                            }
                            history.appendText("\n");
                        }
                        //FS: added next line to reset boolean
                        bidsTooSmall = false;
                        bidPanel.visible = false;
                        displayPanel.visible = true;
                        rentPanel.visible = true;
                        repaint();
                        needBids = false;
                        // makeSimplex();
                        initSimplex();
                        startDivision();
                        return true;
                    }
                    askBids();
                    repaint();
                    return true;
                } else {
                    askAgain();
                    return true;
                }
            }
            return true;
        }


        //allows graphical selection for cake/chore division
        //this calls getChoice, which uses the transformation for graphics
        //to figure out which piece was chosen
        if (displayPanel.contains(x, y) && working && !proposing && (divType != "Rent")) {
            //FS: added displayChoice and if-else statement so that when user
            //   clicks outside the cake, it doesn't accept it as at zero!
            int displayChoice = displayPanel.getChoice(divvy.points[divvy.newPoint], 200, x, divType);
            if (displayChoice != 0) {
                divvy.points[divvy.newPoint].label = displayChoice;
                history.appendText(" [" + divvy.points[divvy.newPoint].label + "] was chosen.\n\n");
                divvy.findPivot();
                graduated = divvy.bigAl();
                if (divType == "Chores") {
                    while (divvy.autoChoose()) {
                        graduated = divvy.bigAl();
                    }
                }
                if (graduated) {
                    suggest.enable();
                } else {
                    suggest.disable();
                }
                tellCuts();
                repaint();
                return true;
            } else {
                history.append(" Not a valid choice.\n");
            }
        }
        return false;
    }

    //called when the Suggest Division button is pressed

    public double printRound(double d) {
        return (Math.round(100 * d)) / 100.0;
    }

    public double carryRound(double d) {
        return (Math.round(10000 * d)) / 10000.0;
    }

    public void suggestDivision() {
        howsThis();
    }


    //displays the cutset information in the history textArea
    //during normal operation
    public void tellCuts() {
        double cuts[] = divvy.points[divvy.newPoint].textTransform(divType);
        //    history.appendText("   Player "+divvy.points[divvy.newPoint].getOwner(divType)+", choose a piece:\n");
        for (int i = 0; i < players; i++) {
            if (divType == "Rent") {
                history.appendText("     Room " + (i + 1) + "  rent: $" + (Math.round(totalRent * (cuts[i + 1] - cuts[i])) / 100.0) + "\n");
            } else {
                history.appendText("     Piece " + (i + 1) + ": scalepoints " + cuts[i] + " to " + cuts[i + 1]);
                /*history.appendText(" (Size: "+(Math.round(100*(cuts[i+1]-cuts[i]))/100.0)+")");*/
                history.appendText("\n");
            }
        }
        history.appendText("Player " + divvy.points[divvy.newPoint].getOwner(divType) + ", which one would you prefer?");
        //    history.appendText("\n");
    }

    //displays the suggested division information in the history textArea

    public void howsThis() {
        history.appendText("\n\n--- SUGGESTED DIVISION ---\n");
        //FS: added this next if statement (3 lines)
        if (divType == "Rent") {
            history.appendText("     Total rent: $" + totalRent + ".\n");
        }
        double cuts[] = divvy.dummy.textTransform(divvy.suggestedTransform());
        for (int i = 0; i < players; i++) {
            if (divType == "Rent") {
                //FS: Added j-loop below and if-statement
                // as a fix to make it print in order of the Players:
                for (int j = 0; j < players; j++) {
                    if ((char) ((int) alpha + i) == divvy.suggestedOwner(j + 1)) {
                        // history.appendText("     Player "+divvy.suggestedOwner(i+1)+
                        // " pays $"+(Math.round(totalRent*(cuts[i+1]-cuts[i]))/100.0)+
                        // " for Room "+(i+1)+".\n");
                        //
                        history.appendText("     Player " + divvy.suggestedOwner(j + 1) +
                                " pays $" + (Math.round(totalRent * (cuts[j + 1] - cuts[j])) / 100.0) +
                                " for Room " + (j + 1) + ".\n");
                    }
                }
            } else {
                history.appendText(
                        "   Player " + divvy.suggestedOwner(i + 1) + "  " +
                                "  Piece " + (i + 1) + ": scalepoints " + cuts[i] + " to " + cuts[i + 1]);
                /*history.appendText(" (Size: "+(Math.round(100*(cuts[i+1]-cuts[i]))/100.0)+")");*/
                history.appendText("\n");
            }
        }
        //FS: I added the if statement in the next 3 lines.
        if (divType == "Cake" || divType == "Chores") {
            history.appendText("(Press 'Continue Iteration' for more precision.)");
        }
        history.appendText("\n");
    }

    //initializes the simplex, and also fetches the rent/cake/chore graphics

    public void initSimplex() {
        divvy = new Simplex(players, divType);
        if (divType == "Rent") {
            divvy.totalRent = totalRent;
        }
    }


    public void makeSimplex() {
        divvy = new Simplex(players, divType);
        if (divType == "Rent") {
            divvy.totalRent = totalRent;
        }

        //FS: I added the history & if statements in the next 4 lines
        history.appendText("\n");
        if (divType == "Cake" || divType == "Chores") {
            history.appendText("[Note that the graphic above is clickable!]\n");
        }

        history.appendText("          ------- Initializing... please wait. -------\n\n");

        //image loading
        try {
            URL loc = new URL(getDocumentBase(), divType + ".gif");
            Toolkit tk = Toolkit.getDefaultToolkit();
            picture = tk.getImage(loc);
        } catch (MalformedURLException mal) {
            history.appendText("Caught: " + mal + "\n");
        }
        tracker = new MediaTracker(this);
        try {
            tracker.addImage(picture, 0);
            tracker.waitForAll();
        } catch (Exception except) {
            history.appendText("Caught: " + except);
            history.appendText("\nCould not load image.\n\n");
        }
        displayPanel.image = picture;
        //displayPanel.visible=true;


        if (divType == "Rent") {
            displayPanel.width = 150;
            displayPanel.imagex = 25;
            displayPanel.imageWidth = 100;
        } else {
            displayPanel.width = 300;
            displayPanel.imagex = 50;
            displayPanel.imageWidth = 200;
        }
        displayPanel.visible = true;
        repaint();

    }


    //starts the division process

    public void startDivision() {
        if (divType == "Cake" || divType == "Chores") {
            working = true;
            while (divType == "Chores" && divvy.autoChoose()) {
                graduated = divvy.bigAl();
            }
            if (graduated) {
                suggest.enable();
            } else {
                suggest.disable();
            }
            tellCuts();
        } else if (divType == "Rent" && rentType == "Interactive") {
            working = true;
            tellCuts();
        } else if (divType == "Rent" && rentType == "Auto-Choose") {
            //FS: change precision next to total rent (1000 = 3 places)
            int target = (int) ((Math.log((double) (totalRent * 10000))) / (Math.log((double) (2))));
            //FS: debug added this next line
            System.out.println("Level: " + target);
            while (divvy.level < target && divvy.autoChoose(bids)) {

                graduated = divvy.bigAl();
            }
            howsThis();
        } else if (divType == "Rent" && rentType == "HRS") {
            initHRS();
            hrsDivision.utilitarian();
            hrsDivision.normHRS();
            hrsDivision.doHRS();
        }

    }

    //checks whether we have a number of players and a division type
    //if so, begins the division, unless we need to get a total rent
    public void areYouReady() {
        if (players != -1 && divType != null) {
            if (divType == "Rent") {
                //FS: comment out the next line since some forget to
                //    press rent button and this erases what they typed!
                //choice.setText("");
                history.appendText("Please enter the total rent and press 'Enter'.\n");
                gettingRent = true;
            } else {
                //working = true;
                makeSimplex();
                startDivision();
            }
        }
    }


    //BIDDING


    //starts the bidding process

    public void startBids() {
        //bids = new double[players][];
        whoBidding = 0;
        askBids();
        repaint();
    }


    //prompts users to enter their bids, placing bids from the
    //previous division in the textField

    public void askBids() {
        char nombre = (char) ((int) alpha + whoBidding);
        String s = new String("");
        try {
            s += bids[whoBidding][0];
            for (int i = 1; i < players; i++) {
                s += ", ";
                s += bids[whoBidding][i];
            }
            bidField.setText(s);
        } catch (NullPointerException e) {
            bidField.setText("");
        }
        //bidField.setText("");
        bidPanel.text = "Player " + nombre + ", enter or modify your bids:";
        bidField.requestFocus();
    }


    //if the entered bids had an error, asks the player to re-enter bids

    public void askAgain() {
        char nombre = (char) ((int) alpha + whoBidding);
        bidField.setText("");
        bidPanel.text = "Player " + nombre + ", please re-enter your bids:";
    }


    //parses a comma-delimited string of bids into the double values and 
    //enters them into the bids[][] array

    public boolean parseString(String s) {
        double wip[] = new double[players];
        int index = 0;
        double accumulator = 0;
        double temp = 0;
        int start = 0;
        int end = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ',' || (i == s.length() - 1)) {
                if (i == s.length() - 1) {
                    i++;
                }
                try {
                    temp = Double.valueOf(s.substring(start, i).trim()).doubleValue();
                } catch (NumberFormatException exc) {
                    System.out.println(exc);
                    return false;
                }
                wip[index] = (double) temp;
                accumulator += (double) temp;
                // System.out.println(bids[index]);
                index++;
                start = i + 1;
            }
        }
        if (accumulator < totalRent) {
            //FS: added next line
            bidsTooSmall = true;
            //FS: replace this with line beneath
            // history.appendText("     Player " + (char)((int)alpha + whoBidding) + ", if your bids sum to less than the total rent,\n");
            history.appendText("\n     Warning: if your bids sum to less than the total rent,\n");
            history.appendText("     the solution will still be envy-free, but you\n");
            history.appendText("     may have to pay more than what you bid.\n");
            //FS:added this if statement below
            if (cb2.getState() && rentType == "HRS") {
                history.appendText("  The Average Discount Method cannot be used.\n   *** Please select another option. ***\n");
            }
        }
        bids[whoBidding] = wip;
        return true;
    }


    //BIDDING


    //HRS


    //initializes the arrays and variables for the HRS algorithm
    //a copy of the bids[][] matrix is made because the hrsBids[][]
    //array gets changed by the algorithm

    public void initHRS() {
        if (exAnte) {
            hrsDivision = new hrsUtil(bids, players, history, totalRent,
                    surplus, aveDisMethod);
        } else {
            hrsDivision = new hrsCycle(bids, players, history, totalRent,
                    surplus, aveDisMethod);
        }
	    
	
	/*hrsBids = new double[players][];
	  aveDisBids = new double[players][];
	  discounts = new double[players][];
	  for(int i = 0; i < players; i++) {
	  hrsBids[i] = new double[players];
	  aveDisBids[i] = new double[players];
	  discounts[i] = new double[players];
	  }
	  for(int j = 0; j < players; j++) {
	  for(int k = 0; k < players; k++) {
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
        for (int i = 0; i < players; i++) {
            payment[assignments[i]] = hrsBids[i][assignments[i]];
            history.appendText("Player " + (char) ((int) alpha + i) + " pays " + payment[assignments[i]] + " for room " + (assignments[i] + 1) + ".\n");
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
        history.appendText("After rent is paid, there is a surplus of " + surplus + "\n");
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
            history.appendText("\nStarting new round of compensation:\n");
            checkEnvy();
            envy = false;
            for (int i = 0; i < players; i++) {
                boolean thisone = findMax(hrsBids[i], i);
                if (thisone) {
                    envy = true;
                }
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
                history.appendText("Player " + (char) ((int) alpha + row) + " is not envious.\n");
            }
        }
    }


    //figures out the compensation that should be given  to player
    //in row index to eliminate envy

    //CHANGE
    public boolean findMax(double row[], int index) {
        //debugArray(index);
        double temp = 0;
        for (int i = 0; i < players; i++) {
            if (((row[assignments[index]] - row[i]) < temp)
                    && !envious[backAssignments[i]]) {
                temp = -1 * (row[assignments[index]] - row[i]);
                char winner = (char) ((int) alpha + backAssignments[i]);
                char loser = (char) ((int) alpha + index);
                history.appendText(" Since Player " + loser + " envies Player " + winner + ", pay " + (Math.round(100 * temp)) / 100.0 + " to " + loser + "\n");
                compensations[assignments[index]] += temp;
            }
        }
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
        history.appendText("\nDistributing surplus of " + surplus + " equally.\n\n");
        for (int j = 0; j < players; j++) {
            payment[j] -= (surplus / players);
        }
        for (int k = 0; k < players; k++) {
            history.appendText("Overall, Player " + (char) ((int) alpha + k) + " pays " + (Math.round(100 * payment[assignments[k]]) / 100.0) + "\n");
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
        history.appendText("Surplus: " + surplus + "\n\n");
        for (int index = 0; index < players; index++) {
            for (int j = 0; j < players; j++) {
                for (int k = 0; k < players; k++) {
                    aveDisBids[j][k] = hrsBids[j][k];
                }
            }
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
            history.appendText("Leftover discounts to players: ");
            for (int a = 0; a < players; a++) {
                //DEBUGGING
                double overallDis = /*hrsBids[a][assignments[a]] +*/ discounts[index][a];
                history.appendText((Math.round(100 * overallDis) / 100.0) + ", ");
            }
            history.appendText("\n\n");
        }
        averageThem();
        for (int p = 0; p < players; p++) {
            history.appendText("Overall, Player " + (char) ((int) alpha + p) + " pays " + (Math.round(100 * (payment[assignments[p]] - averages[p])) / 100.0) + "\n");
        }
    }


    //takes the biased discounts and averages them for each player

    public void averageThem() {
        double sum;
        history.appendText("Average leftover discounts to players:\n");
        for (int i = 0; i < players; i++) {
            sum = 0;
            for (int j = 0; j < players; j++) {
                sum += discounts[j][i];
            }
            averages[i] = sum / players;
            history.appendText((Math.round(100 * averages[i]) / 100.0) + ", ");
        }
        history.appendText("\n\n");
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
        history.appendText("Give " + (Math.round(dis * 100) / 100.0) + " to tied set.\n");
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
        history.appendText("Now ");
        for (int j = 0; j < players; j++) {
            if (inD[j]) {
                history.appendText((char) ((int) alpha + j) + ", ");
            }
        }
        history.appendText("in tied set.\n");
    }


    //HRS


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


    //---------------------
    //GRAPHICS STUFF BELOW
    //---------------------

    //creates an offscreen set of graphics that each component will draw 
    //itself to

    void makeImageBuffer() {
        image = createImage(size().width, size().height);
        graphics = image.getGraphics();
        clear();
    }

    // clears the graphics image

    void clear() {
        graphics.setColor(backgroundColor);
        graphics.fillRect(0, 0, size().width, size().height);
        graphics.drawRect(0, 0, size().width - 1, size().height - 1);
    }

    // update is implicitly called when repaint() is called
    // g will be bound to the Graphics object in the Applet,
    // each component is drawn into graphics, which is then copied
    //onscreen, which eliminates flicker.

    public void update(Graphics g) {
        if (graphics == null) {
            makeImageBuffer();
        }
        outputPanel.paint(graphics);
        inputPanel.paint(graphics, this);
        controlPanel.paint(graphics, this);
        displayPanel.paint(graphics, this);
        configPanel.paint(graphics, this);
        rentPanel.paint(graphics, this);
        bidPanel.paint(graphics, this);
        bidField.show(bidPanel.visible);
        //CHANGE
        //howSurplus.show(bidPanel.visible && (rentType == "HRS"));
        cb1.show(bidPanel.visible && (rentType == "HRS"));
        cb2.show(bidPanel.visible && (rentType == "HRS"));
        cb3.show(bidPanel.visible && (rentType == "HRS"));

        g.drawImage(image, 0, 0, this);
    }

    // paint(Graphics) is called by the applet whenever
    // the screen needs painting

    public void paint(Graphics g) {
        update(g);
    }

}


