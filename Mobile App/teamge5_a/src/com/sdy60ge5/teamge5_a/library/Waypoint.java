package com.sdy60ge5.teamge5_a.library;

/******************************************************************************************************
 * Κλάση για την διαχείριση αντικειμένων waypoint. Χρησιμοποιείται από την υπηρεσία ParsingGPXDrawing *
 ******************************************************************************************************/
public class Waypoint {
    double lat;
    double lon;
    String name;

    // constructors
    public Waypoint(){

    }

    public Waypoint(double lat,double lon,String name){
        this.lat = lat;
        this.lon = lon;
        this.name = name;

    }

    // setters
    public void setLat(double lat){
        this.lat = lat;
    }

    public void setLon(double lon){
        this.lon = lon;
    }

    public void setName(String name){
        this.name = name;
    }

    // getters
    public double getLat() {
        return this.lat;
    }

    public double getLon() {
        return this.lon;
    }

    public String getName() {
        return this.name;
    }

}
