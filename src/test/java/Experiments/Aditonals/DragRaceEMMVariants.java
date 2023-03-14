package Experiments.Aditonals;

import Experiments.ResearchQuestions.ExperimentLogger;
import Experiments.ResearchQuestions.ProgressPrinter;
import Model.Model;
import Model.CurrentModel;
import User.Agent.Agent;
import User.Agent.ExpectiMiniMax.*;
import tests.TestEMMVariants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DragRaceEMMVariants {
    public static String experimentName = "EMMDragRace";
    public static Model startModel = new CurrentModel();
    public static int startDept = 4;
    public static int endDept = 10;
    public static int incrementSize = 1;
    public static int numberOfTestsPerDept = 100;
    public static File backupDir = new File("src/test/java/Experiments/Results/DragRaceModelsBackup");
    public static ExpectiMiniMax[] racers = new ExpectiMiniMax[]{
            new ExpectiMiniMax(null, false, startDept),
            new ExpectiMiniMaxAlphaBetaPruning(null, false, startDept),
            new ExpectiMiniMaxAlphaBetaPruningSorted(null, false, startDept),
    };
    public static int i = 37;
    public static void main(String[] args) {
        String[] strings = new String[racers.length+1];
        strings[0] = "dept";
        for(int i = 0; i < racers.length; i++) strings[i+1] = "Turn time "+racers[i];
        if(i == 0) ExperimentLogger.logToCSV(experimentName, strings);
        Model model;
        ExpectiMiniMax temp;
        long startTime;
        ProgressPrinter progressPrinter;
        for(int d = startDept; d <= endDept; d += incrementSize){
            progressPrinter = new ProgressPrinter(numberOfTestsPerDept);
            for(i = i; i < numberOfTestsPerDept; i++){
                model = TestEMMVariants.getRandomModel(startModel.clone());
                try{
                    File file = new File(backupDir, experimentName+"d"+d+"i"+i+".Model");
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                    objectOutputStream.writeObject(model);
                    objectOutputStream.close();
                    fileOutputStream.close();
                } catch (IOException e){
                    System.out.println(e.getStackTrace());
                }
                strings = new String[racers.length+1];
                strings[0] = String.valueOf(d);
                for(int j = 0; j < racers.length; j++){
                    temp = racers[j];
                    temp.setModel(model.clone());
                    temp.setDept(d);
                    startTime = System.currentTimeMillis();
                    temp.generateMove();
                    strings[j+1] = String.valueOf(System.currentTimeMillis()-startTime);
                }
                ExperimentLogger.logToCSV(experimentName, strings);
                progressPrinter.print(i+1);
            }
            i = 0;
        }
    }
}
