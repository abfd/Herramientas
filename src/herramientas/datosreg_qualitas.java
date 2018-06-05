/*
 * datosreg_qualitas.java
 *
 * Created on 10 de enero de 2008, 17:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package herramientas;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


/**
 *
 * @author Administrador
 */
public class datosreg_qualitas {
    
    private String sRutaPropiedades = "/data/informes/datosreg_qualitas/datosregqualitas.properties";    
    private String sRutaLog;
    private Utilidades.Propiedades propiedades = null;    
    private Connection conexion = null;    
    private Connection conexion2 = null;    
    private String sConsulta = "";
    
    //LOG
    public static Logger logger = Logger.getLogger(datosreg_qualitas.class);
    
    
    public static void main (String [] args ) 
    {               
        datosreg_qualitas datregq = new datosreg_qualitas();
    }//main
    
    
    
    /** Creates a new instance of datosreg_qualitas */
    public datosreg_qualitas() 
    {
        try
        {
            cargaPropiedades();
            cruza_datosreg_e4xtt();
        }
        catch (FileNotFoundException fnfe)
        {
            logger.info("ERROR 1: "+fnfe.toString());
            //Utilidades.Log.addText(sRutaLog,"ERROR 1: "+fnfe.toString());
            System.out.println("ERROR 1");
        }
        catch (IOException ioe)
        {
            logger.info("ERROR 2: "+ioe.toString());
            //Utilidades.Log.addText(sRutaLog,"ERROR 2: "+ioe.toString());
            System.out.println("ERROR 2");
        }
    }
    
    private void cruza_datosreg_e4xtt() 
    {
        String numexp = "";
        String libro = "";
        String tomo = "";
        String registro = "";
        int total = 0;
        int buenos = 0;
        int malos = 0;
        int noEncontrados = 0;
        int numero;
        
        try
        {
            conexion = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"),propiedades.getValueProperty("IfxLogin"),propiedades.getValueProperty("IfxPass"));
            conexion.setAutoCommit(false);    
            conexion2 = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"),propiedades.getValueProperty("IfxLogin"),propiedades.getValueProperty("IfxPass"));
            conexion2.setAutoCommit(false);
            
            ResultSet rsTotal = null;
            //ResultSet rsE4xtt = null;
            sConsulta = "SELECT datosreg.*,e4xtt.* FROM datosreg,e4xtt WHERE numexp = e4xha and numero = e4xya and tomo = 'A' AND libro = '16685'";
            rsTotal = Utilidades.Conexion.select(sConsulta,conexion);   
            while (rsTotal.next())
            {
                libro = "";
                tomo = "";
                registro = "";
                total ++;
                System.out.println("Subtotal: "+Integer.toString(total));                
                numexp = rsTotal.getString("numexp");
                numero = rsTotal.getInt("numero");
                System.out.println("Expediente: "+numexp);
                //sConsulta = "SELECT * FROM e4xtt WHERE e4xha = '"+numexp+"' and e4xya =" + Integer.toString(numero);
                //rsE4xtt = Utilidades.Conexion.select(sConsulta,conexion);   
                //if (rsE4xtt.next())
                //{
                    //System.out.println("ENCONTRADO");
                    tomo = rsTotal.getString("e4y0a");
                    libro = rsTotal.getString("e4y1a");
                    registro = rsTotal.getString("e4xza");
                    sConsulta = "UPDATE datosreg SET tomo ='"+tomo+"', libro ='"+libro+"', registro = '"+registro+"' where numexp = '"+numexp+"' and numero ="+Integer.toString(numero);
                    if (Utilidades.Conexion.update(sConsulta,conexion2) > 0) 
                    {
                        conexion2.commit();                        
                        buenos ++;
                        logger.info("Actualizado expediente: "+numexp+ " Tomo: "+tomo+" Libro: "+libro+" Registro: "+registro);
                        //Utilidades.Log.addText(sRutaLog,"Actualizado expediente: "+numexp+ " Tomo: "+tomo+" Libro: "+libro+" Registro: "+registro);
                        
                    }
                    else
                    {
                        conexion2.rollback();
                        malos ++;
                        logger.info("ERROR 5: Imposible acutalizar datos registrales para el expediente: "+numexp);
                        //Utilidades.Log.addText(sRutaLog,"ERROR 5: Imposible acutalizar datos registrales para el expediente: "+numexp);
                        System.out.println("ERROR 3");
                    }
                    System.out.println("Buenos: "+Integer.toString(buenos));
                    System.out.println("Malos: "+Integer.toString(malos));
                //}
                    /*
                else
                {
                    noEncontrados ++;
                    Utilidades.Log.addText(sRutaLog,"Expediente no localizado en E4XTT: "+numexp);
                    System.out.println("NO ENCONTRADO");
                }
                rsE4xtt.close();                
                     */
            }
            System.out.println("Total: "+Integer.toString(total));
            logger.info("TOTAL procesados: "+Integer.toString(total));
            //Utilidades.Log.addText(sRutaLog,"TOTAL procesados: "+Integer.toString(total));
            logger.info("BUENOS: "+Integer.toString(buenos));
            //Utilidades.Log.addText(sRutaLog,"BUENOS: "+Integer.toString(buenos));
            logger.info("MALOS: "+Integer.toString(malos));
            //Utilidades.Log.addText(sRutaLog,"MALOS: "+Integer.toString(malos));
            logger.info("NO ENCONTRADOS: "+Integer.toString(noEncontrados));
            //Utilidades.Log.addText(sRutaLog,"NO ENCONTRADOS: "+Integer.toString(noEncontrados));
            rsTotal.close();
            conexion.close();
            conexion2.close();
            System.gc();
        }
        catch (ClassNotFoundException cnfe)
        {
            logger.info("ERROR 3: "+cnfe.toString());
            //Utilidades.Log.addText(sRutaLog,"ERROR 3: "+cnfe.toString());
            System.out.println("ERROR 3");
        }
        catch (SQLException sqle)
        {
            logger.info("ERROR 4: "+sqle.toString());
            //Utilidades.Log.addText(sRutaLog,"ERROR 4: "+sqle.toString());
            System.out.println("ERROR 4");
        }        
        finally
            {
                try
                {
                    if (conexion != null && !conexion.isClosed()) conexion.close();
                    if (conexion2 != null && !conexion2.isClosed()) conexion2.close();
                }
                catch (SQLException e)
                {
                    logger.info("Imposible cerrar conexión: "+e.toString().trim());
                    //Utilidades.Log.addText(sRutaLog,"Imposible cerrar conexión: "+e.toString().trim());                    
                }
                conexion = null;
                conexion2 = null;
                System.gc();
            }

    }//cruza_datosreg_e4xtt
    
    private void cargaPropiedades() throws FileNotFoundException, IOException
    {                   
       File fPropiedades = new File(sRutaPropiedades);
       if (fPropiedades.exists())
       {
             propiedades = new Utilidades.Propiedades(fPropiedades.getAbsolutePath());
             sRutaLog = propiedades.getValueProperty("RutaLog");    
             PropertyConfigurator.configure(sRutaLog + "Log4j.properties");
        }  
        else
        {
            throw new FileNotFoundException ("Imposible localizar fichero de propiedades en la ruta:  " + sRutaPropiedades.trim());                          
        }
    }//cargaPropiedades
    
}
