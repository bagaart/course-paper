public class gasolineCompany {
    private String companyName;
    private gasolineStation head;
    private gasolineStation tail;

    public gasolineCompany(){
        this.companyName = "None";
        this.head = null;
        this.tail = null;
    }
    public gasolineCompany(String companyName){
        this.companyName = companyName;
        this.head = null;
        this.tail = null;
    }

    public void setHead(gasolineStation head) {
        this.head = head;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    public void setTail(gasolineStation tail) {
        this.tail = tail;
    }

    public gasolineStation getHead() {
        return head;
    }
    public gasolineStation getTail() {
        return tail;
    }
    public String getCompanyName() {
        return companyName;
    }
    public gasolineStation getGasolineStation(int gasolineStationNumber){
        gasolineStation current = head;
        while(current != null && current.getGasolineStationNumber() != gasolineStationNumber){
            current = current.getNext();
        }
        return current;
    }

    public void add(int gasolineStationNumber){
        if (head == null){
            gasolineStation new_station = new gasolineStation(gasolineStationNumber);
            head = new_station;
            tail = new_station;
        } else {
            gasolineStation new_station = new gasolineStation(gasolineStationNumber);
            tail.setNext(new_station);
            tail = tail.getNext();
        }
    }

    public void delete(){
        if (head != null){
            gasolineStation temp = head;
            head = head.getNext();
            if (head == null){
                tail = null;
            }
            temp.destroy();
            temp = null;
        }
    }

    public void destroy(){
        if (head != null){
            gasolineStation temp = head;
            head = head.getNext();
            temp.destroy();
            temp = null;
        }
        head = null;
        tail = null;
        companyName = "None";
    }

}
