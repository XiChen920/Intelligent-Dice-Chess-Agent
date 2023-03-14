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
 * Do  our  methods  outperform  a  regular  baseline  agent
 */
public class BaseLineTest {
    public static String experimentName = "Q1";
    public static Model startModel = new CurrentModel();

    public static void main(String[] args) throws IOException {
        File out = new File(experimentName+PlayOut.getPreferredExtension());
        out.delete();
        int i;
        PlayOut[] playOuts;
        ProgressPrinter progressPrinter;
        for(double turnTimeMS = Settings.startTurnTimeMS; turnTimeMS <= Settings.endTurnTimeMS; turnTimeMS += Settings.stepSizeTurnTimeMS){
            progressPrinter = new ProgressPrinter(Settings.games, 1);
            for(i = 0; i < Settings.games; i++){
                playOuts = new PlayOut[]{
                        PlayOut.getPlayOutEMMABPSIDvsBaseLine(turnTimeMS, startModel.clone()),
                        PlayOut.getPlayOutMCTSvsBaseLine(turnTimeMS, startModel.clone())
                };
                if(turnTimeMS == Settings.startTurnTimeMS && i == 0){
                    PlayOut.printCVSHeader(experimentName, new PlayOut[]{new PlayOut(playOuts[0].name), new PlayOut(playOuts[1].name)});
                }
                PlayOut.printCVSLine(experimentName, playOuts);
                progressPrinter.print(i);
                PrintPlayOut.print(playOuts[0], playOuts[0].name+experimentName+((int)turnTimeMS)+"ms"+i+".PlayOut");
                PrintPlayOut.print(playOuts[1], playOuts[1].name+experimentName+((int)turnTimeMS)+"ms"+i+".PlayOut");
            }
            System.out.print("\r"+turnTimeMS+" done");
            System.out.println();
        }
    }
}
