package herramientas;
/*
 * cargainecenso06.java
 *
 * Created on 17 de enero de 2007, 11:11
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

    
import java.io.FileNotFoundException;
    import java.io.IOException;
    import java.sql.*;
    import org.apache.log4j.Logger;
    import org.apache.log4j.PropertyConfigurator;

    public class cargainecenso06 {      
    
    private  Connection conexion = null;    
    private  Connection conexion2 = null;    
    private  String sRutaPropiedades = "/data/informes/cargainecenso/propiedades/inecenso.properties";
    private Utilidades.Propiedades propiedades = null;
    private String LineaLog="";
    private String sRutaFicheroLog = "";
    private String sNomFichLog = "";
    private String sTexto = "";
    
    //LOG
    public static Logger logger = Logger.getLogger(cargainecenso06.class);
    
    public static void main(String [] args ) {
        //cargainecenso06 o = new cargainecenso06();        
      
        //Objetos.Inecenso.loadFromExcel();
      
    }//main
    
    public cargainecenso06() 
    {
        try
        {
            boolean estadok = true;
            int codine06  = 0;
            int c06 = 0;
            int enm06 = 0;
            int eni06 = 0;
            int procesados = 0;
            
            //cargamos el fichero de propiedades.
            java.io.File fichero = new java.io.File(sRutaPropiedades);
            if (fichero.exists())
            {
                propiedades = new Utilidades.Propiedades(fichero.getAbsolutePath());                
            }
            else
            {
                estadok = false;
                sTexto = "No se ha encontrado el fichero de propiedades en la ruta: "+sRutaPropiedades.trim();
                logger.info(sTexto);                
            }          
            if (estadok)
            {   
                String sConsulta = "";
                conexion = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"),propiedades.getValueProperty("IfxLogin"),propiedades.getValueProperty("IfxPass"));
                conexion.setAutoCommit(false);              
                conexion2 = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"),propiedades.getValueProperty("IfxLogin"),propiedades.getValueProperty("IfxPass"));
                conexion2.setAutoCommit(false);              
                sRutaFicheroLog = propiedades.getValueProperty("RutaFicheroLog");
                //sNomFichLog = propiedades.getValueProperty("sNomFichLog");
                PropertyConfigurator.configure(sRutaFicheroLog + "Log4j.properties");
                sConsulta = "select codine,c_06 from inecenso06 where enm is null and eni is null";
                ResultSet rs = Utilidades.Conexion.select(sConsulta,conexion);
                while (rs.next())
                {
                    procesados ++;
                    estadok = true;
                    sConsulta = "";
                    codine06 = rs.getInt("codine");
                    c06 = rs.getInt("c_06");
                    // al log
                    sTexto = "PROCESANDO CODIGO: "+codine06;
                    logger.info(sTexto);

                    //MUNICIPIOS
                    sConsulta = "select codine from municipios where codine = "+codine06;
                    ResultSet rsm = Utilidades.Conexion.select(sConsulta,conexion);
                    if (rsm.next())
                    {//indica que el codigo ine ha sido encontrado en municipios
                        enm06 = 1;
                    }
                    else
                    {//indica que el codigo ine NO ha sido encontrado en municipios
                        enm06 = 0;
                        sTexto = "No encontrado en Municipios el código: "+codine06;
                        logger.info(sTexto);
                    }
                    rsm = null;
                    //INECENSO
                    sConsulta = "select codine from inecenso where codine = "+codine06;
                    ResultSet rsi = Utilidades.Conexion.select(sConsulta,conexion);
                    if (rsi.next())
                    {//indica que el codigo ine ha sido encontrado en inecenso
                        eni06 = 1;
                    }
                    else
                    {//indica que el codigo ine NO ha sido encontrado en inecenso
                        eni06 = 0;
                        sTexto = "No encontrado en Inicenso el código: "+codine06;
                        logger.info(sTexto);
                    }
                    rsi = null;
                    if (enm06 == 1 && eni06 == 1)            
                    {
                        // al log
                        sTexto = "Encontrado en Municipios e Inecenso. Actualizamos "+codine06;
                        logger.info(sTexto);
                        sConsulta = "update inecenso set c_08 = "+c06+" where codine = "+codine06;
                        if(Utilidades.Conexion.update(sConsulta,conexion2)==0)
                            {                            
                                estadok = false;
                                // al log
                                sTexto = "ERROR al actualizar INECENSO: "+codine06;
                                logger.info(sTexto);

                            }
                        else
                            {
                                estadok = true;
                                sTexto = "Actualizado INECENSO: "+codine06;
                                logger.info(sTexto);
                            }                        
                    }
                    if (estadok)
                    {
                        sConsulta = "update inecenso06 set enm = "+enm06+",eni = "+eni06+" where codine = "+codine06;
                        if(Utilidades.Conexion.update(sConsulta,conexion2)==0)
                        {
                             estadok = false;
                             sTexto = "ERROR al actualizar INECENSO06: "+codine06;
                             logger.info(sTexto);                         
                        }
                        else
                        {                               
                            estadok = true;
                            sTexto = "Actualizado INECENSO06: "+codine06;
                            logger.info(sTexto);
                        }
                    }
                    if (estadok)
                    {
                        conexion2.commit();
                        sTexto = "PROCESANDO SIN ERROR CODIGO: "+codine06;
                        logger.info(sTexto);
                    }                    
                    else
                    {
                        conexion2.rollback();
                        sTexto = "PROCESANDO CON ERROR CODIGO: "+codine06;
                        logger.info(sTexto);
                    }
                    System.out.println("LLEVAMOS PROCESADOS: "+procesados);
                }//while
                rs = null;
                conexion.close();                  
                conexion2.close();                  
               }
        }//try
        catch (ClassNotFoundException cnfException)
        {
            sTexto = "Error al cargar el driver de informix";
            logger.info(sTexto);                 
        }
        catch (SQLException sqlExcepcion)
        {
            sTexto = "Error:"+sqlExcepcion.toString();
            logger.info(sTexto);
        }
        catch (FileNotFoundException fnfException)
        {
            sTexto = "Problema con el fichero de propiedades: "+fnfException.toString();
            logger.info(sTexto);
        }       
        catch (IOException  ioException)
        {
            sTexto = "Problema con el fichero de propiedades: "+ioException.toString();   
            logger.info(sTexto);
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
                    sTexto = "Imposible cerrar conexión: "+e.toString().trim();
                    logger.info(sTexto);                    
                }
                conexion = null;
                conexion2 = null;
                System.gc();
            }
    }
    
}
