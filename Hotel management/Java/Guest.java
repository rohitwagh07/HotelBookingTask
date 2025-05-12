import java.io.*;
import java.text.*;
import java.util.*;


// Blueprint
class Guest {
    String name, contact, idProof;
    int roomNumber;
    Date checkIn, checkOut;

    Guest(String name, String contact, String idProof, int roomNumber, Date checkIn, Date checkOut) {
        this.name = name;
        this.contact = contact;
        this.idProof = idProof;
        this.roomNumber = roomNumber;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    //convert the java object into string format and return the guest information concat
    public String toFileString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return name + "," + contact + "," + idProof + "," + roomNumber + "," +
               sdf.format(checkIn) + "," + sdf.format(checkOut);
    }

    //split the string using split method
    public static Guest fromFileString(String line) throws ParseException {
        String[] parts = line.split(",");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return new Guest(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]),
                         sdf.parse(parts[4]), sdf.parse(parts[5]));
    }
}

