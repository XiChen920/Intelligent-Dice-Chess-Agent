package Experiments.ResearchQuestions;

public class ProgressPrinter {
    private final long startTime;
    private final int totalTasks;
    private final double increment;

    public ProgressPrinter(int totalTasks, double increment){
        this.startTime = System.currentTimeMillis();
        this.totalTasks = totalTasks;
        this.increment = increment;
    }

    public ProgressPrinter(int totalTasks) {
        this.startTime = System.currentTimeMillis();
        this.totalTasks = totalTasks;
        this.increment = 100.0/totalTasks;
    }

    private double lastMessage = 0;
    public void print(int currentTask){
        if(currentTask == 0){
            System.out.print("0% eta = unknown");
            return;
        }
        if(currentTask == totalTasks){
            System.out.println("\r100%, done");
        }
        if(((double)currentTask)/((double) totalTasks)*100 > lastMessage){
            System.out.print("\r"+lastMessage+"% eta = "+(((double)(System.currentTimeMillis()-startTime))/((double) currentTask)*(totalTasks-currentTask))/Math.pow(60,2)+" m");
            lastMessage+= increment;
        }
    }
}
