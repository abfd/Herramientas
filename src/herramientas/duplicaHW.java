/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package herramientas;

import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
/**
 *
 * @author Administrador
 */
public class duplicaHW 
{
     public static void main(String[] args) 
    {
        duplicaHW carga = new duplicaHW();
        carga = null;
        System.gc();
    }
   
    public duplicaHW()  
    {
        //FICHERO LOG4J
        PropertyConfigurator.configure("/data/informes/duplicaHW/" + "Log4j.properties");   
        Logger logger = Logger.getLogger(cargaPretasaExcel.class);
        java.sql.Connection conexion = null;
        Objetos.EquiposHW nwEquipo = null;
        java.sql.ResultSet rsDatos = null;
        String dirip = null;     
        int codigohw = 131;
        
       try
       {
           
       
        conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:peticiones/peticiones@192.168.3.215:1521:rvtn");
        conexion.setAutoCommit(false);
        
        String sConsulta = "select dirip from equiposhw where oficinahw = 0 and fechabaja is null and dirip like '192.168.3.%'";
        rsDatos = Utilidades.Conexion.select(sConsulta,conexion);
        while (rsDatos.next())
        {
            dirip = null;            
            dirip = rsDatos.getString("dirip");
            nwEquipo = new Objetos.EquiposHW();
            nwEquipo.loadFromIP(dirip, conexion);
            nwEquipo.dirip = dirip.substring(0, 8)+"33."+dirip.substring(10, dirip.trim().length());
            nwEquipo.codigohw = codigohw;
            if (nwEquipo.insert(conexion) == 1)
            {
                conexion.commit();                
                logger.info("Duplicada dirección: "+rsDatos.getString("dirip")+" en la direccion: "+nwEquipo.dirip);
            }
            else
            {
                conexion.rollback();
                logger.error("Imposible duplicadar dirección: "+rsDatos.getString("dirip")+" en la direccion: "+nwEquipo.dirip);
            }
            nwEquipo = null;
            codigohw ++;
        }//while
        conexion.close();
       }//try      
       catch (SQLException sqle)
       {
           logger.error("Excepción al duplicadar dirección: "+dirip+" en la direccion: "+nwEquipo.dirip+ " Descripcion: "+sqle.toString());
       }
       catch (Exception e)
       {
           logger.error("Excepción al duplicadar dirección: "+dirip+" en la direccion: "+nwEquipo.dirip+ " Descripcion: "+e.toString());
       }
       finally
       {
            try
            {
                if (conexion != null && !conexion.isClosed()) conexion.close();                
            }
            catch (java.sql.SQLException sqlException)
            {     
                logger.info("Imposible cerrar conexión con la B.D Informix");
            }  
       }
    }
}
