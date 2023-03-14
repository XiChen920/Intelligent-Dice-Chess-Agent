package User.Agent.ExpectiMiniMax;

import Model.Model;
import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;
import java.util.Objects;

public class ExpectiMiniMaxAlphaBetaPruningSortedDynamicMultiThreaded extends ExpectiMiniMaxAlphaBetaPruningSortedMultiThreaded{
    private double targetCPUUtilization;
    private final static double DEFAULTTargetCPUUtilization = 0.95;
    protected OperatingSystemMXBean operatingSystemMXBean;

    public ExpectiMiniMaxAlphaBetaPruningSortedDynamicMultiThreaded(Model modelReference, boolean playerId, int dept, double targetCPUUtilization) {
        super(modelReference, playerId, dept);
        this.targetCPUUtilization = targetCPUUtilization;
        operatingSystemMXBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    }

    public ExpectiMiniMaxAlphaBetaPruningSortedDynamicMultiThreaded(Model modelReference, boolean playerId, double targetCPUUtilization) {
        super(modelReference, playerId);
        this.targetCPUUtilization = targetCPUUtilization;
        operatingSystemMXBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    }

    public ExpectiMiniMaxAlphaBetaPruningSortedDynamicMultiThreaded(Model modelReference, boolean playerId, int dept) {
        super(modelReference, playerId, dept);
        targetCPUUtilization = DEFAULTTargetCPUUtilization;
        operatingSystemMXBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    }

    public ExpectiMiniMaxAlphaBetaPruningSortedDynamicMultiThreaded(Model modelReference, boolean playerId) {
        super(modelReference, playerId);
        targetCPUUtilization = DEFAULTTargetCPUUtilization;
        operatingSystemMXBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    }

    private double lastLoad;
    @Override
    protected boolean addNewTread() {
        double load = operatingSystemMXBean.getSystemCpuLoad();
        if(lastLoad != load){
            lastLoad = load;
            return load< targetCPUUtilization;
        }
        return false;
    }

    @Override
    public String toString() {
        return "ExpectiMiniMaxAlphaBetaPruningSortedDynamicMultiThreaded  dept = "+dept+" evaluationFunction = "+evaluationFunction+" ID = "+playerId+" current model = "+modelReference+" pruning = "+pruning+" sort = "+sort+" targetCPUUtilization = "+targetCPUUtilization;
    }

    @Override
    public Object clone(){
        ExpectiMiniMaxAlphaBetaPruningSortedDynamicMultiThreaded ans = new ExpectiMiniMaxAlphaBetaPruningSortedDynamicMultiThreaded(null, false);
        ans.clone(this);
        return ans;
    }

    protected void clone(ExpectiMiniMaxAlphaBetaPruningSortedDynamicMultiThreaded ans){
        super.clone(ans);
        targetCPUUtilization = ans.targetCPUUtilization;
        lastLoad = ans.lastLoad;
        operatingSystemMXBean = ans.operatingSystemMXBean;
    }

    public void setTargetCPUUtilization(double targetCPUUtilization) {
        this.targetCPUUtilization = targetCPUUtilization;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExpectiMiniMaxAlphaBetaPruningSortedDynamicMultiThreaded)) return false;
        if (!super.equals(o)) return false;
        ExpectiMiniMaxAlphaBetaPruningSortedDynamicMultiThreaded that = (ExpectiMiniMaxAlphaBetaPruningSortedDynamicMultiThreaded) o;
        return Double.compare(that.targetCPUUtilization, targetCPUUtilization) == 0 && Double.compare(that.lastLoad, lastLoad) == 0 && Objects.equals(operatingSystemMXBean, that.operatingSystemMXBean);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), targetCPUUtilization, operatingSystemMXBean, lastLoad);
    }
}
