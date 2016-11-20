/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author MaRose
 */
public class Main {

    public static void main(String[] args) {
        DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy, hh:mm:ss a");
        Date d = new Date();
        System.out.println(dateFormat.format(d));
    }
}
