/*
 * Main.java
 *
 * Created on 29 de enero de 2007, 11:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package herramientas;

import java.sql.*;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Administrador
 */
public class Main {
    
    /** Creates a new instance of Main */
    public Main() 
    {        
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        //copia();
        borraPretasacionesBDE();
    }
    
    public static void copia()
    {
        String bb_dd_origen = "jdbc:oracle:thin:valtecnic2/valtecnic2@oraserver01:1521:rvtn1";
        String bb_dd_destino = "jdbc:oracle:thin:desarrollo/des0329@oraserver00:1521:rvtn";
        Connection conexion_origen= null;
        Connection conexion_destino = null;
        
        try
        {
            conexion_origen = Utilidades.Conexion.getConnection(bb_dd_origen);
            conexion_origen.setAutoCommit(false); 
            conexion_destino = Utilidades.Conexion.getConnection(bb_dd_destino);
            conexion_destino.setAutoCommit(false); 
            if (Objetos.v2.Solicitudes.copyFullExpediente("1234 0203 133037", "1234 0211 171954", conexion_origen, conexion_destino))
            {
                System.out.println("COPIADO");
            }
            else System.out.println("COPIADO");
            
        }
        catch (Exception e)
        {
            
        }
        finally
        {
            try
            {
                conexion_origen.close();
                conexion_destino.close();
            }
            catch (Exception e)
            {
                
            }
            
        }
    }
    
    /*
    LOS REGISTROS DE PRETASACIONES ENTRE EL 01/01/2017 Y EL 31032017 SE DUPLICARON EN LA TABLA BDETASFINCA HAY QUE BORRAR UNO DE ESOS REGISTROS.
    */
    private static void borraPretasacionesBDE()
    {
        String bb_dd_origen = "jdbc:oracle:thin:informatica/infx0329@oraserver02:1521:rvtnprod";
        Connection conexion_origen= null;
        String sconsulta = null;
        java.sql.ResultSet rs = null;
        String sconsulta2 = null;
        java.sql.ResultSet rs2 = null;
        String borrado = null;
        Integer totales = 0;
        int borrados = 0;
        int no_borrados = 0;
        try
        {
            conexion_origen = Utilidades.Conexion.getConnection(bb_dd_origen);
            conexion_origen.setAutoCommit(false); 
            sconsulta = "select numexp,count(*) from bdetasfinca where numexp like '%_PRET' and fchrem between '01012017' and '31032017'  group by numexp having count(*) = 2 order by numexp";
            rs = Utilidades.Conexion.select(sconsulta, conexion_origen);
            while (rs.next())
            {
                totales ++;                
                sconsulta2 = "select rowid id ,bdetasfinca.* from bdetasfinca where numexp = '"+rs.getString("numexp")+"' AND numexp like '%_PRET' and fchrem between '01012017' and '31032017' order by rowid asc";
                rs2 = Utilidades.Conexion.select(sconsulta2, conexion_origen);
                if (rs2.next())
                {
                    borrado = "delete from bdetasfinca where rowid = '"+rs2.getString("id")+"'";
                    if (Utilidades.Conexion.delete(borrado, conexion_origen) == 1) 
                    {
                        borrados ++;
                        System.out.println("Borrado: "+rs.getString("numexp")+". Id: "+rs2.getString("id"));
                    }
                    else no_borrados ++;
                }
                rs2.close();
                rs2 = null;
            }
            rs.close();
            rs = null;
            
            
        }
        catch (Exception e)
        {
            totales = null;
        }
        finally
        {
            try
            {
                if (totales != null)
                {
                    if (no_borrados == 0 && totales.intValue() == borrados)
                    {
                        conexion_origen.commit();
                    }
                }
                else
                {
                    conexion_origen.rollback();
                }                       
                conexion_origen.close();                         
            }
            catch (Exception e)
            {
                
            }
            
        }
    }
    
}
