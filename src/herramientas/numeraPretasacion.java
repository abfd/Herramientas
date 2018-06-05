/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package herramientas;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Administrador
 */
public class numeraPretasacion 
{
    private static Connection conexion = null;    
    private static Connection conexion2 = null;        
    //private static String sRutaPropiedades = "C:/Archivos de programa/Apache Software Foundation/Apache Tomcat 6.0.14/webapps/Pretasaciones/";
    private static String sRutaPropiedades = "/data/informes/cargaUnidades/propiedades/cargaUnidades.properties";
    private static Utilidades.Propiedades propiedades = null;
    
     public static void main(String [] args ) 
     {
        numera();                         
         
     }//main
     
     private static void numera() 
     {
         //cargamos el fichero de propiedades.
         try
         {
            String nump = "";
            java.io.File fichero = new java.io.File(sRutaPropiedades);
            if (fichero.exists())
            {
                propiedades = new Utilidades.Propiedades(fichero.getAbsolutePath());                
                String sConsulta = "";
                conexion = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"),propiedades.getValueProperty("IfxLogin"),propiedades.getValueProperty("IfxPass"));
                conexion.setAutoCommit(false);      
                
                //conexion.close();
                //conexion = null;
                
            }
         
         }//try
         catch (FileNotFoundException fnfe)
         {
             
         }
         catch (IOException ioe)
         {
             
         }
         catch (ClassNotFoundException cnfe)
         {
             
         }
         catch (SQLException sqle)
         {
             
         }
         finally
         {
              try
                 {
                    if (conexion != null && !conexion.isClosed())
                    {
                
                        conexion.close();
                    }
                 }
                 catch (Exception p){
                     
                 }
         }
         
            
     }
    
     
     private static void numera_() 
     {
         //cargamos el fichero de propiedades.
         try
         {
            String nump = "";
            java.io.File fichero = new java.io.File(sRutaPropiedades+"WEB-INF/propiedades/pretasaciones.properties");
            if (fichero.exists())
            {
                propiedades = new Utilidades.Propiedades(fichero.getAbsolutePath());                
                String sConsulta = "";
                conexion = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"),propiedades.getValueProperty("IfxLogin"),propiedades.getValueProperty("IfxPass"));
                conexion.setAutoCommit(false);      
                conexion2 = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"),propiedades.getValueProperty("IfxLogin"),propiedades.getValueProperty("IfxPass"));
                conexion2.setAutoCommit(false);      
                sConsulta = "select * from pretasaciones where numpreta is null and codcli = 126";
                ResultSet rs = Utilidades.Conexion.select(sConsulta,conexion);
                Tablas.pretas pretasacion = new Tablas.pretas(sRutaPropiedades);
                while (rs.next())
                {
                    pretasacion.buscaPretasacion(conexion2,rs.getInt("numpet"));
                    nump = pretasacion.numeraPretasacion(conexion2);
                    if (nump != null && !nump.equals(""))
                    {
                        pretasacion.setValorEtiqueta("numpreta", nump);
                        pretasacion.actualizaNumpreta(conexion2);
                        System.out.println("Cliente: "+rs.getInt("codcli"));
                        if (pretasacion.estadoOK()) System.out.println("Num. Peticion: "+rs.getString("numpet")+" Num. Pretasacion: "+nump);
                        else System.out.println("ERROR: "+pretasacion.dameDescError());
                    }
                    
                }
                conexion.close();
                conexion2.close();
                
            }
         
         }//try
         catch (FileNotFoundException fnfe)
         {
             
         }
         catch (IOException ioe)
         {
             
         }
         catch (ClassNotFoundException cnfe)
         {
             
         }
         catch (SQLException sqle)
         {
             
         }
            
     }
}
