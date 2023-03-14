package User.Agent.ExpectiMiniMax;

import Model.Model;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

@Deprecated
public class ExpectiMiniMaxAlphaBetaPruningDynamicMultiThreaded extends ExpectiMiniMaxAlphaBetaPruningLimitedMultiThreaded{
    /**
     * The default target CPU utilization
     */
    protected final static double DEFAULTTargetCPUUtilization = 0.95;

    /**
     * The target CPU utilization
     */
    protected double targetCPUUtilization;

    /**
     * The OperatingSystemMxBean for the current operating system
     */
    protected OperatingSystemMXBean operatingSystemMXBean;
    private static final int DEFAULTDept = 4;

    /**
     * Create a ExpectiMiniMax with a custom dept and target CPU utilization
     * @param modelReference the model the agent players on
     * @param playerId the player the agent is
     * @param dept the dept it is allowed to go to (every min player and every max player node count towards this number), must be >= 1
     * @param targetCPUUtilization the target degree of utilization of the CPU, must be within [0,1]
     */
    public ExpectiMiniMaxAlphaBetaPruningDynamicMultiThreaded(Model modelReference, boolean playerId, int dept, double targetCPUUtilization) {
        super(modelReference, playerId, dept);
        if(targetCPUUtilization < 0 || targetCPUUtilization >1) throw new IllegalArgumentException("targetCPUUtilization "+targetCPUUtilization+" not in [0,1]");
        this.targetCPUUtilization = targetCPUUtilization;
        operatingSystemMXBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    }

    /**
     * Create a ExpectiMiniMax with a custom dept
     * @param modelReference the model the agent players on
     * @param playerId the player the agent is
     * @param dept the dept it is allowed to go to (every min player and every max player node count towards this number), must be >= 1
     */
    public ExpectiMiniMaxAlphaBetaPruningDynamicMultiThreaded(Model modelReference, boolean playerId, int dept) {
        super(modelReference, playerId, dept);
        this.targetCPUUtilization = DEFAULTTargetCPUUtilization;
        operatingSystemMXBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    }

    /**
     * Create a standard ExpectiMiniMax
     * @param modelReference the model the agent players on
     * @param playerId the player the agent is
     */
    public ExpectiMiniMaxAlphaBetaPruningDynamicMultiThreaded(Model modelReference, boolean playerId) {
        super(modelReference, playerId, DEFAULTDept);
        this.targetCPUUtilization = DEFAULTTargetCPUUtilization;
        operatingSystemMXBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    }

    protected double lastLoad;
    @Override
    protected boolean roomForANewThread(){
        //operatingSystemMXBean.getSystemCpuLoad() seems to have a delay or updates slowly, combined with that CPU load always chances, this method should do
        //getSystemCpuLoad() is deprecated, replacement is getCpuLoad(), but kept because not all development members are up-to-date (apparently)
        double load = operatingSystemMXBean.getSystemCpuLoad();
        if(lastLoad != load){
            lastLoad = load;
            return load< targetCPUUtilization;
        }
        return false;
    }

    @Override
    public String toString() {
        return "ExpectiMiniMaxAlphaBetaPruningDynamicMultiThreaded dept = "+dept+" evaluationFunction = "+evaluationFunction+" ID = "+playerId+" current model = "+modelReference+" pruning = "+pruning+" targetCPUUtilization = "+targetCPUUtilization;
    }

    @Override
    public Object clone(){
        ExpectiMiniMaxAlphaBetaPruningDynamicMultiThreaded ans = new ExpectiMiniMaxAlphaBetaPruningDynamicMultiThreaded(null, false);
        ans.clone(this);
        return ans;
    }

    protected void clone(ExpectiMiniMaxAlphaBetaPruningDynamicMultiThreaded ans) {
        super.clone(ans);
        targetCPUUtilization = ans.targetCPUUtilization;
        operatingSystemMXBean = ans.operatingSystemMXBean;
        lastLoad = ans.lastLoad;
    }

    public void setTargetCPUUtilization(double targetCPUUtilization) {
        if(targetCPUUtilization > 1) throw new IllegalArgumentException("the following is not true: targetCPUUtilization <= 1");
        this.targetCPUUtilization = targetCPUUtilization;
    }
}