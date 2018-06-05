/*
 * borrarFicheros.java
 *
 * Created on 28 de junio de 2007, 10:38
 *
 * To change this template, choose Tools | Template Manager
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
public class borrarFicheros {
    
    
    private  String sRutaPropiedades = "/data/informes/cargainecenso/propiedades/inecenso.properties";
    private Utilidades.Propiedades propiedades = null;
    private String ruta = "/data/informes/caixacontrolnormativa/xmlgenerados/";
    private  Connection conexion = null;
    
    
     public static void main(String [] args ) 
     {
        borrarFicheros borrar = new borrarFicheros();                
     }//main
    
    /** Creates a new instance of borrarFicheros */
    public borrarFicheros() 
    {
         try
         {
            //cargamos el fichero de propiedades.
            java.io.File fichero = new java.io.File(sRutaPropiedades);
            if (fichero.exists())
            {
               
                    propiedades = new Utilidades.Propiedades(fichero.getAbsolutePath());                
            }
            else
            {                
                System.out.println("No se ha encontrado el fichero de propiedades en la ruta: "+sRutaPropiedades.trim());                
            }  
            String sConsulta = "";
            String numero = "";
            boolean bBorrado = false;
            conexion = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"),propiedades.getValueProperty("IfxLogin"),propiedades.getValueProperty("IfxPass"));
            conexion.setAutoCommit(false);
            sConsulta = "SELECT numexp FROM controlcaixa WHERE estado = 2 and fchrem is not null order by numexp";
            ResultSet rs = Utilidades.Conexion.select(sConsulta,conexion);
            while (rs.next())
            {                                
                numero = rs.getString(1)+".xml";
                //numero = "5557 0613 161519.xml";
                java.io.File fArchivo = new java.io.File(ruta,numero);  bBorrado = fArchivo.delete();
                if(bBorrado) System.out.println("ARCHIVO BORRADO: "+numero); else System.out.println("IMPOSIBLE DE BORRAR: "+numero);
                bBorrado = false;
            }//while
            conexion.close();
          }  //try                                
          catch (FileNotFoundException fnfe)
          {
              System.out.println("Excepción fnfe: "+fnfe.toString());
          }
          catch (IOException ioe)
          {
               System.out.println("Excepción ioe: "+ioe.toString());
          }
          catch (ClassNotFoundException cnfe)
          {
               System.out.println("Excepción ioe: "+cnfe.toString());
          }
          catch (SQLException sqle)
          {
               System.out.println("Excepción ioe: "+sqle.toString());
          }
         finally
         {
                try
                {
                    if (conexion != null && !conexion.isClosed()) conexion.close();
                }
                catch (SQLException e)
                {
                    System.out.println("Imposible cerrar conexión: "+e.toString().trim());                    
                }
                conexion = null;
                System.gc();
         }//finally
       
    }//borrarFicheros
    
}
