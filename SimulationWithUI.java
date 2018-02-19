import sim.portrayal.network.*;
import sim.portrayal.continuous.*;
import sim.engine.*;
import sim.display.*;
import sim.portrayal.simple.*;
import sim.portrayal.*;
import javax.swing.*;
import java.awt.Color;
import java.awt.*;

public class SimulationWithUI extends GUIState implements Steppable
{
    public Display2D display;
    public JFrame displayFrame;
    ContinuousPortrayal2D yardPortrayal = new ContinuousPortrayal2D();
    TimeSeriesPlot population;


    public static void main(String[] args)
    {
        SimulationWithUI vid = new SimulationWithUI();
        Console c = new Console(vid);
        c.setVisible(true);
    }


    public SimulationWithUI() {
        super(new Simulation( System.currentTimeMillis()));
        display = new Display2D(Simulation.state.yardSize*4,Simulation.state.yardSize*4,this);
        population = new TimeSeriesPlot("Predator/Prey Population","Time (steps)","Population");
    }


    public SimulationWithUI(SimState state) { super(state); }


    public static String getName() { return "Savannah Cliques"; }


    public void start()
    {
        super.start();
        setupPortrayals();
        scheduleRepeatingImmediatelyAfter(this);
    }


    public void load(SimState state)
    {
        super.load(state);
        setupPortrayals();
    }


    public void setupPortrayals()
    {
        Simulation simulation = (Simulation) state;
// tell the portrayals what to portray and how to portray them
        yardPortrayal.setField( simulation.yard );
        yardPortrayal.setPortrayalForClass(Prey.class, new OvalPortrayal2D(Color.red));
        yardPortrayal.setPortrayalForClass(Predator.class, new OvalPortrayal2D(Color.blue));
// reschedule the displayer
        display.reset();
        display.setBackdrop(Color.white);
// redraw the display
        display.repaint();
    }


    public void init(Controller c)
    {
        super.init(c);

        population
                .addVariable("Predators", ()-> {
                    return((double)Predator.nPredators);
                })
                .addVariable("Prey", () -> {
                    return((double)Prey.nPrey);
                });
        JTabbedPane newTabPane = new JTabbedPane();
        population.addToPane(newTabPane);
        JFrame myChartFrame = new JFrame("My Graphs");
        myChartFrame.add(newTabPane);
        myChartFrame.pack();
        controller.registerFrame(myChartFrame);

        display.setClipping(false);
        displayFrame = display.createFrame();
        displayFrame.setTitle("Sabannah Display");
        c.registerFrame(displayFrame);
// so the frame appears in the "Display" list
        displayFrame.setVisible(true);
        display.attach( yardPortrayal, "Yard" );
    }

    @Override
    public void step(SimState state) {
        double t = Simulation.state.schedule.getTime();
        population.recordValues(t);
    }


    public void quit()
    {
        super.quit();
        if (displayFrame!=null) displayFrame.dispose();
        displayFrame = null;
        display = null;
    }
}

