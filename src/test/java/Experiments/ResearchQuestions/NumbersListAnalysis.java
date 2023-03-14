package Experiments.ResearchQuestions;

import java.util.ArrayList;
import java.util.List;

public class NumbersListAnalysis {
    private final List<Number> numberList;

    public NumbersListAnalysis(List<Number> numberList){
        if(numberList.size() == 0) throw new IllegalArgumentException("numberList must contain elements");
        this.numberList = numberList;
    }

    public NumbersListAnalysis(Number[] numberArray){
        if(numberArray.length == 0) throw new IllegalArgumentException("numbersArray must contain elements");
        this.numberList = new ArrayList<>();
        for(Number number: numberArray) this.numberList.add(number);
    }

    public Number getAverage(){
        double ans = 0;
        for(Number number: numberList){
            ans += number.doubleValue()/numberList.size();
        }
        return ans;
    }

    public Number getNumberAtPart(double part){
        numberList.sort((o1, o2) -> (int) (o1.doubleValue()-o2.doubleValue()));
        if(part < 0 || part > 1) throw new IllegalArgumentException("Part must be in range [0,1]");
        if(part == 1) return numberList.get(numberList.size()-1);
        double tempIndex = numberList.size()*part;
        if(tempIndex%1 == 0) return numberList.get((int) tempIndex);
        int tempIndex2 = (int) tempIndex;
        double residual = tempIndex-tempIndex2;
        try {
            return numberList.get(tempIndex2).doubleValue() * residual + numberList.get(tempIndex2 + 1).doubleValue() * (1 - residual);
        } catch (IndexOutOfBoundsException ignore){
            return numberList.get(numberList.size()-1);
        }
    }

    public Number getMaximum() {
        return getNumberAtPart(1);
    }

    public Number getThirdQuartile() {
        return getNumberAtPart(0.75);
    }

    public Number getMean() {
        return getNumberAtPart(0.5);
    }

    public Number getFirstQuartile() {
        return getNumberAtPart(0.25);
    }

    public Number getMinimum() {
        return getNumberAtPart(0);
    }

    public static List<String> getReportHeaders(){
        String[] temp = new String[]{
                "average",
                "maximum",
                "third quartile",
                "mean",
                "first quartile",
                "minimum",
        };
        List<String> ans = new ArrayList<>();
        for(String t: temp) ans.add(t);
        return ans;
    }

    public List<String> getReport(){
        String[] temp = new String[]{String.valueOf(getAverage()), String.valueOf(getMaximum()), String.valueOf(getThirdQuartile()), String.valueOf(getMean()), String.valueOf(getFirstQuartile()), String.valueOf(getMinimum())};
        List<String> ans = new ArrayList<>();
        for(String t: temp) ans.add(t);
        return ans;
    }
}
