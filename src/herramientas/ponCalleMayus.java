/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package herramientas;

import java.io.*;
import java.sql.*;

/**
 *
 * @author Administrador
 */
public class ponCalleMayus 
{
    private  Connection conexion = null;        
    private  String sRutaPropiedades = "/data/informes/insertapis7cpp/propiedades/insertapis7cpp.properties";
    private Utilidades.Propiedades propiedades = null;
    
     public static void main(String [] args ) 
    {        
        ponCalleMayus o = new ponCalleMayus();        
        o = null;
        System.gc();
        
    }//main
     
    public ponCalleMayus()
    {//ponemos la calle en mayusculas para los encargos automaticos de popular.
       
       String sConsulta = "";
       String sConsulta2 = "";
       String calle = "";
       String numexp = "";
       Utilidades.Consultas consulta = null;
       try
       {
           File fPropiedades = new File(sRutaPropiedades);                     
           propiedades = new Utilidades.Propiedades(fPropiedades.getAbsolutePath()); 
           conexion = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"),propiedades.getValueProperty("IfxLogin"),propiedades.getValueProperty("IfxPass"));
           conexion.setAutoCommit(false);

           sConsulta = "select solicitudes.calle,solicitudes.numexp from operclientes,solicitudes where solicitudes.numexp = operclientes.numexp and refcliente like 'VLT%' and tipomensaje = 'STA'";
           ResultSet rsencargos = Utilidades.Conexion.select(sConsulta,conexion);
           while (rsencargos.next())            
           { 
               calle = rsencargos.getString("calle");
               numexp = rsencargos.getString("numexp");
               if (calle != null) calle = calle.toUpperCase();
               //actualizamos solicitudes.
               consulta = new Utilidades.Consultas(Utilidades.Consultas.UPDATE);
               consulta.from("solicitudes");
               consulta.set("calle",calle,Utilidades.Consultas.VARCHAR);               
               consulta.where("numexp",numexp,Utilidades.Consultas.VARCHAR);            
               sConsulta2 = consulta.getSql();
               if (Utilidades.Conexion.update(sConsulta2,conexion) == 0) 
               {
                   conexion.rollback();
                   System.out.println("K.O. Imposible actualizar  calle  en el expediente: "+numexp.trim());                                                                
               }
               else
               {
                   conexion.commit();
                   System.out.println("O.K.Actualizada calle  en el expediente: "+numexp.trim());
               }
               consulta = null;
               calle = null;
               numexp = null;
               sConsulta2 = null;
           }//while
           conexion.close();
       }//try
       catch (FileNotFoundException fnfe)
       {
            System.out.println("Excepcion: "+fnfe.toString());
       }
       catch (IOException ioe)
       {
           System.out.println("Excepcion: "+ioe.toString());
       }
       catch (ClassNotFoundException cnfe)
       {
           System.out.println("Excepcion: "+cnfe.toString());
       }
       catch (SQLException sqle)
       {
           System.out.println("Excepcion: "+sqle.toString());
       }
       finally
        {
            try
            {
                if (conexion != null && !conexion.isClosed()) conexion.close();            
            }
            catch (SQLException sqlException)
            {                
            }
            
        }
    }//ponCalleMayus       
            
}
