import java.util.Objects;

public class gasolineStation {
    private pumpStation head;
    private int gasolineStationNumber;
    private gasolineStation next;

    public gasolineStation(){
        this.head = null;
        this.gasolineStationNumber = -1;
        this.next = null;
    }
    public gasolineStation(int number){
        this.gasolineStationNumber = number;
        this.next = null;
        this.head = null;
    }

    public void setNext(gasolineStation next) {
        this.next = next;
    }
    public void setGasolineStationNumber(int gasolineStationNumber) {
        this.gasolineStationNumber = gasolineStationNumber;
    }
    public void setHead(pumpStation head) {
        this.head = head;
    }

    public gasolineStation getNext() {
        return next;
    }
    public int getGasolineStationNumber() {
        return gasolineStationNumber;
    }
    public pumpStation getHead() {
        return head;
    }
    public pumpStation getPump(int pumpNumber, String fuelGrade){
        pumpStation current = this.head;
        while(current != null) {
            if (current.getPumpNumber() == pumpNumber & Objects.equals(current.getFuelGrade(), fuelGrade)){
                break;
            }
            current = current.getNext();
        }
        return current;
    }

    public void add(int pumpNumber, String fuelGrade, int bOrA, pumpStation place) {
        if (head == null) {
            head = new pumpStation(pumpNumber, fuelGrade);
        } else if ((bOrA != 1 & bOrA != 2) | place == null) {
            pumpStation newPumpStation = new pumpStation(pumpNumber, fuelGrade);
            pumpStation current = head;
            while (current.getNext() != null) {
                current = current.getNext();
            }
            current.setNext(newPumpStation);
        } else if (bOrA == 1){
            pumpStation newPumpStation = new pumpStation(pumpNumber, fuelGrade);
            if (head == place){
                newPumpStation.setNext(head);
                head = newPumpStation;
            } else {
                pumpStation current = head;
                while (current.getNext() != place) {
                    current = current.getNext();
                }
                current.setNext(newPumpStation);
                newPumpStation.setNext(place);
            }
        }else {
            pumpStation newPumpStation = new pumpStation(pumpNumber, fuelGrade);
            pumpStation current = head;
            while(current != place){
                current = current.getNext();
            }
            newPumpStation.setNext(current.getNext());
            current.setNext(newPumpStation);
        }
    }

    public pumpStation find(int pumpNumber, String fuelGrade){
        pumpStation current = head;
        while(current != null) {
            if (Objects.equals(current.getFuelGrade(), fuelGrade) & current.getPumpNumber() == pumpNumber){
                break;
            }
            current = current.getNext();
        }
        return current;
    }

    public void delete(int pumpNumber, String fuelGrade){
        pumpStation found = find(pumpNumber, fuelGrade);
        if (found != null){
            if (head == found){
                head = head.getNext();
                found.destroy();
                found = null;
            }else {
                pumpStation current = head;
                while (current.getNext() != null) {
                    if (current.getNext() == found) {
                        break;
                    }
                    current = current.getNext();
                }
                current.setNext(found.getNext());
                found.destroy();
                found = null;
            }
        }
    }

    public void destroy(){
        while (head != null){
            pumpStation temp = head;
            head = head.getNext();
            temp.destroy();
            temp = null;
        }
        this.head = null;
        this.gasolineStationNumber = -1;
    }
}
