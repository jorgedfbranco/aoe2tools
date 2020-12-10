package ui.model;

public class RatingColor {
    public static String color(int rating) {
        if (rating <= 1500) return "green";
        if (rating <= 2000) return "#FFCC00";
        else if (rating <= 2500) return "#e6ac00";
        else return "red";
    }
}