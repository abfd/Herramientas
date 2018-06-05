/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package herramientas;


import java.sql.Connection;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author Ana Belen de Frutos
 */
public class solicitudesSVHT 
{
    //fichero de propiedades
    private String ficheroPropiedadesName = "herramientas.properties";
    private java.util.Properties fpropiedades = new java.util.Properties();
    
    // Fichero Log4j    
    private Logger logger = Logger.getLogger(solicitudesSVHT.class);
    private String rutaLog = "/data/informes/solicitudesSVHT/";
    
    private Connection conexion = null;
    
    
    public static void main(String[] args) 
    {
        // TODO code application logic here
        //cargaReferencias cargaReferencias = new cargaReferencias("25215A004000060000YX","0040000010535","VPT","1");
        solicitudesSVHT solSVHT = new solicitudesSVHT();
        //cargaReferencias cargaReferencias = new cargaReferencias();
        solSVHT = null;
        
    }//main
    
    public solicitudesSVHT()
    {
        
        
        
        String ruta = "/data/informes/Servihabitat/REC";
        String solicitud = null;
        java.io.File fArchivo = new java.io.File(ruta);
        String sConsulta = null;
        java.sql.ResultSet rs = null;        
        try
        {
            fpropiedades.load(this.getClass().getResourceAsStream(ficheroPropiedadesName));                        
            
            //FICHERO LOG4J
            PropertyConfigurator.configure(rutaLog + "Log4j.properties");                         
            if(fArchivo.exists() && fArchivo.isDirectory())
            {
                        java.io.File[] archivos = fArchivo.listFiles();                        
                        String descError = "";
                        int iContador = 0;    
                        conexion = Utilidades.Conexion.getConnection(fpropiedades.getProperty("url_datos"));
                        conexion.setAutoCommit(false);        
                        while(iContador<archivos.length)
                        {
                           if (archivos[iContador].getName().indexOf(".ZIP") != -1 || archivos[iContador].getName().indexOf(".zip") != -1)
                           {     
                                logger.info("Fichero Solicitud: "+archivos[iContador].getName());   
                                solicitud = null;

                                if (archivos[iContador].getName().indexOf("TAS2_SOL") != -1)
                                {//solicitud
                                    solicitud = archivos[iContador].getName().trim().substring(9, 19);
                                    if (solicitud != null)
                                    {
                                         sConsulta = "SELECT s.numexp FROM solicitudes s join refer r on ( s.numexp = r.numexp) WHERE r.referencia = '"   +solicitud.trim()+"' AND s.codcli in (255,355,755,855,955)";
                                         try
                                         {
                                            rs = Utilidades.Conexion.select(sConsulta,conexion); 
                                            if (rs.next()) logger.info("La solicitud: "+archivos[iContador].getName()+" esta dada de alta en el expediente: "+rs.getString("numexp"));
                                            else logger.error("La solicitud: "+archivos[iContador].getName()+" NO EXISTE EN EL SISTEMA");
                                         }
                                         catch (Exception e)
                                         {
                                             logger.error("Error en la comprobacion de la solicitud: "+archivos[iContador].getName()+". Descripcion: "+e.toString());
                                         }
                                    }
                                }
                           }
                           iContador ++;
                        }//while 
                        conexion.close();                        
                    }//if(fArchivo.exists() && fArchivo.isDirectory())  
        }//try
        catch (Exception e)
        {
          logger.error("Error en la comprobacion de las solicitus. Descripcion: "+e.toString());
        }
        finally
        {
            try 
            {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.rollback();
                    conexion.close();
                }
                
            } catch (Exception e) 
            {
                logger.error("Imposible cerrar conexion con Valtecnic datos.");
            }
        }
        
        
    }//solicitudesSVHT
}
