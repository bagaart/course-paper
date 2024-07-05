public class pumpStation {
    private int pumpNumber;
    private String fuelGrade;
    private pumpStation next;

    public pumpStation(){
        this.pumpNumber = -1;
        this.fuelGrade = "None";
        this.next = null;
    }
    public pumpStation(int number, String grade){
        this.pumpNumber = number;
        this.fuelGrade = grade;
        this.next = null;
    }

    public void setNext(pumpStation next) {
        this.next = next;
    }
    public void setFuelGrade(String fuelGrade) {
        this.fuelGrade = fuelGrade;
    }
    public void setPumpNumber(int pumpNumber) {
        this.pumpNumber = pumpNumber;
    }

    public int getPumpNumber() {
        return pumpNumber;
    }
    public String getFuelGrade() {
        return fuelGrade;
    }
    public pumpStation getNext() {
        return next;
    }

    public void destroy(){
        this.pumpNumber = -1;
        this.fuelGrade = "";
        this.next = null;
    }
}
