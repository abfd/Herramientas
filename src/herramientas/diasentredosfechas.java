/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package herramientas;

/**
 *
 * @author Administrador
 */
public class diasentredosfechas                 
{
    public static void main(String[] args) {
        // TODO code application logic here
        int cuantos = diasDesdeUltimoEnvio();
    }
    
    public static int diasDesdeUltimoEnvio()
    {
        int cuantos = 0;
        String fchval = "01-05-2010";
        if (fchval != null && !fchval.equals(""))
        {
            java.util.Date arg0 = Utilidades.Cadenas.getDate(fchval);
            java.util.Date arg1 = Utilidades.Cadenas.getDate(new java.text.SimpleDateFormat("dd-MM-yyyy").format(new java.util.Date()));
            cuantos = Utilidades.Fechas.dateDiffInDays(arg1, arg0);
        }
        
        return cuantos;
    }
}
