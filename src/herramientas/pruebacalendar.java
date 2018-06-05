/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package herramientas;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author Ana Belen de Frutos
 */
public class pruebacalendar {
    
    
    public static void main (String[] args) throws ParseException
    {
        
        /*
        Calendar myCalendario = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd-MMMM-yyyy");
        myCalendario.set(Calendar.YEAR, 2013);
        myCalendario.set(Calendar.MONTH, 0);
        myCalendario.set(Calendar.DAY_OF_MONTH , 1);
        myCalendario.setMinimalDaysInFirstWeek(1); 
        System.out.println ("Fecha de hoy: "+sdf.format(myCalendario.getTime()));
        System.out.println ("Dia del mes: "+myCalendario.get(Calendar.DAY_OF_MONTH));
        System.out.println ("Dia de la semana: "+myCalendario.get(Calendar.DAY_OF_WEEK));
        System.out.println ("Dia de la semana en el mes: "+myCalendario.get(Calendar.DAY_OF_WEEK_IN_MONTH));
        System.out.println ("Semana del mes: "+myCalendario.get(Calendar.WEEK_OF_MONTH));
        **/
        
        /*
        for (int i=0; i<12;i++)
        {
            PintaUnMes nwMes = new PintaUnMes(2012,i);
            nwMes = null;
        }
        */
        
        PintaUnMes nw = new PintaUnMes();
        //0.- años 1.- meses 2.- dias.
        int tipo = 0;
        java.util.Date inicio =  new SimpleDateFormat("dd-MM-yyyy").parse("29-11-1972");
        java.util.Date fin =  new SimpleDateFormat("dd-MM-yyyy").parse("28-11-2014");
        
        System.out.println(nw.getDiffDates(inicio, fin, 0));
    }
}
