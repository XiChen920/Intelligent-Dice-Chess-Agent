package Experiments.ResearchQuestions;

import Experiments.Results.PlayOutsBackup.PrintPlayOut;
import Model.CurrentModel;
import Model.Model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Which  AI  performs  the  best?
 */
public class Question2
{
    public static String experimentName = "Q2";
    public static Model startModel = new CurrentModel();
    public static int i = 0;
    public static double turnTimeMS = Settings.startTurnTimeMS;

    public static void main(String[] args) throws IOException {
        File out = new File(experimentName+PlayOut.getPreferredExtension());
        out.delete();
        if(i == 0) PlayOut.printCVSHeader(experimentName, new PlayOut());
        PlayOut playOut;
        ProgressPrinter progressPrinter;
        for(turnTimeMS = turnTimeMS; turnTimeMS <= Settings.endTurnTimeMS; turnTimeMS += Settings.stepSizeTurnTimeMS){
            progressPrinter = new ProgressPrinter(Settings.games, 1);
            for(i = i; i < Settings.games; i++){
                playOut = PlayOut.getPlayOutEMMABPSIDvsMCTS(turnTimeMS, startModel.clone());
                PlayOut.printCVSLine(experimentName, playOut);
                progressPrinter.print(i);
                PrintPlayOut.print(playOut, experimentName+((int)turnTimeMS)+"ms"+i+".PlayOut");
            }
            System.out.print("\r"+turnTimeMS+" done");
            System.out.println();
            i= 0;
        }
        ObjectOutputStream out2 = new ObjectOutputStream(new FileOutputStream(out));
        out2.close();
    }
}
