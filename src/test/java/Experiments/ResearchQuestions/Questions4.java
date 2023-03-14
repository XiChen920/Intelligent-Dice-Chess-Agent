package Experiments.ResearchQuestions;

import Experiments.Results.PlayOutsBackup.PrintPlayOut;
import Model.Model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import Model.CurrentModel;

/**
 * Hybrid agent experiments
 */
public class Questions4
{
    public static Model startModel = new CurrentModel();
    public static int highLevelExperimentSelection = 1;

    public static void main(String[] args)
    {
        try
        {
            experiment(highLevelExperimentSelection);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public static PlayOut selectPlayoutExperiment(int selection, double turnTime, Model arena)
    {
        PlayOut selected;
        switch(selection)
        {
            case 0:
                selected = PlayOut.getPlayOutEMMABPSIDMLPEvaluationFunctionVSMCTS(turnTime, arena);
                break;
            case 1:
                selected = PlayOut.getPlayOutMCTSMLPEvaluationVSEMMABPSID(turnTime, arena);
                break;
            case 2:
                selected = PlayOut.getPlayOutEMMABPSIDMLPEvaluationFunctionVSMCTSMLPEvaluationFunction(turnTime, arena);
                break;
            default:
                selected = null;
                break;
        }
        return selected;
    }

    public static double turnTimeMS = Settings.startTurnTimeMS;
    public static int i = 0;

    public static void experiment(int selection) throws IOException
    {
        String experimentName = "MLP selection experiment" + Integer.toString(selection);
        File out = new File(experimentName+PlayOut.getPreferredExtension());
        out.delete();

        if(turnTimeMS == Settings.startTurnTimeMS &&  i == 0) PlayOut.printCVSHeader(experimentName, new PlayOut());
        PlayOut playOut;
        ProgressPrinter progressPrinter;

        for(turnTimeMS = turnTimeMS; turnTimeMS <= Settings.endTurnTimeMS; turnTimeMS += Settings.stepSizeTurnTimeMS)
        {
            progressPrinter = new ProgressPrinter(Settings.games, 1);
            for (i = i; i < Settings.games; i++)
            {
                playOut = selectPlayoutExperiment(selection, turnTimeMS, startModel.clone());
                PlayOut.printCVSLine(experimentName, playOut);
                PrintPlayOut.print(playOut,experimentName+turnTimeMS+"i"+i+".PlayOut");
                progressPrinter.print(i+1);
            }
            i = 0;
            System.out.print("\r"+turnTimeMS+" done");
            System.out.println();
        }
    }
}
