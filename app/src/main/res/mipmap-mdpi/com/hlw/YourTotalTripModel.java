package oditek.com.hlw;

public class YourTotalTripModel {
    int car;
    int tripCanceledPic;
    int tripScheduledPic;
    int ridecancel;
    String dayTime;
    String carDetails;
    String pickupLocation;
    String dropLocation;
    String price;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getRidecancel() {
        return ridecancel;
    }

    public void setRidecancel(int ridecancel) {
        this.ridecancel = ridecancel;
    }

    public int getCar() {
        return car;
    }

    public void setCar(int car) {
        this.car = car;
    }

    public int getTripScheduledPic() {
        return tripScheduledPic;
    }

    public void setTripScheduledPic(int tripScheduledPic) {
        this.tripScheduledPic = tripScheduledPic;
    }

    public String getDayTime() {
        return dayTime;
    }

    public void setDayTime(String dayTime) {
        this.dayTime = dayTime;
    }

    public String getCarDetails() {
        return carDetails;
    }

    public void setCarDetails(String carDetails) {
        this.carDetails = carDetails;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation(String dropLocation) {
        this.dropLocation = dropLocation;
    }

    public int getTripCanceledPic() {
        return tripCanceledPic;
    }

    public void setTripCanceledPic(int tripCanceledPic) {
        this.tripCanceledPic = tripCanceledPic;
    }

}
