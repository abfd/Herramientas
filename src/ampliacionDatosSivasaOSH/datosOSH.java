/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ampliacionDatosSivasaOSH;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author Ana Belen de Frutos
 */
public class datosOSH {

    //EXCEL
    public  String sUrlExcel = "";
    public  Utilidades.Excel oExcel = null;            
    public  org.apache.poi.hssf.usermodel.HSSFCell celda = null;
    public  String HojaActual = null;
    private Double valorD = null;
    private Integer valorI = null;
    private String valorS = null;
    
    private File fPropiedades;
    private  String sRutaPropiedades = "/data/informes/WSSivasa/propiedades/WSSivasa.properties";
    private Utilidades.Propiedades propiedades = null;
    
     // Fichero Log4j    
    private Logger logger = Logger.getLogger(datosOSH.class);
    private String rutaLogENVOSH = "";
    
    
    private String rutaENVOSH = "";
    
    public static void main(String[] args)
    {
           datosOSH nwOSH = new datosOSH();
           //nwOSH.compruebaYaEnviadas();
           nwOSH.InsertaEnviosOSH();
           System.gc();
    }
    
    
    
    
    public datosOSH() {
       try
       {
            java.io.File fichero = new java.io.File(sRutaPropiedades);
            if (fichero.exists())
            {
                propiedades = new Utilidades.Propiedades(fichero.getAbsolutePath());      
                //FICHERO LOG4J
                //PropertyConfigurator.configure(rutaLog + "Log4j.properties");      
            }
            else
            {                    
                System.out.println("No se ha encontrado el fichero de propiedades en la ruta: "+sRutaPropiedades.trim());            
            }         
       }
       catch (Exception e)
       {
            e.printStackTrace();
       }
        
    }
    
    /*
     * del excel que nos ha enviado Sivasa comprobamos si la tasación ya ha sido enviada y validada.
     */
    private void compruebaYaEnviadas()
    {
        //EXCEL
        sUrlExcel = "/data/informes/WSSivasa/OSH/55_Valtecnic_OSH_mio.xls";                  
        celda = null;
        HojaActual = "Hoja1";
        Connection conexion = null;
        String sConsulta = null;
        java.sql.ResultSet rsDatos = null;
        try
        {
            conexion = Utilidades.Conexion.getConnection(propiedades.getValueProperty("url_datos"));
            conexion.setAutoCommit(false);      
            oExcel = new Utilidades.Excel(sUrlExcel);
            int filaI = 1;
            int filaF = 320;
            Integer id_suelo = 0;
            String numexp = "";
            String estado = "";
            for (int fila = filaI; fila < filaF; fila ++)
            {  
                   dameValorExcel(fila,0,"S");      //referencia sivasa
                   if (valorS != null)
                   {
                       sConsulta = "select r.numexp,r.referencia,s.iden_suelo from refer r left outer join suelo s on (r.numexp = s.numexp) where r.referencia = '"+valorS+"' order by r.numexp";
                       rsDatos = Utilidades.Conexion.select(sConsulta,conexion);
                       if (rsDatos.next()) 
                       {
                           id_suelo = rsDatos.getInt("iden_suelo");
                           numexp = rsDatos.getString("numexp");
                           if (id_suelo == null || id_suelo == 0)
                           {
                               System.out.println(numexp);
                           }
                           
                       }
                   }
                   else System.out.println("Fila nº:"+fila+" con referencia nula");
                   id_suelo = null;
                   numexp = null;
            }
            conexion.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (!conexion.isClosed())
                {
                    conexion.close();
                    conexion = null;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            
        }
        
        
    }
    
    private void InsertaEnviosOSH()
    {
        //EXCEL
        sUrlExcel = "/data/informes/WSSivasa/OSH/55_Valtecnic_OSH_mio.xls";                  
        celda = null;
        HojaActual = "Hoja1";
        Connection conexion = null;
        String sConsulta = null;
        java.sql.ResultSet rsDatos = null;
        Objetos.v2.Operclientes oOperclientes = null;    
        String estado = "H";  //sin enviar. estado asigando a este tipo de envio de datos.
        
        try
        {
            cargaPropiedades();
            conexion = Utilidades.Conexion.getConnection(propiedades.getValueProperty("url_datos"));
            conexion.setAutoCommit(false);      
            oExcel = new Utilidades.Excel(sUrlExcel);
            int filaI = 1;
            int filaF = 320;            
            oOperclientes = new Objetos.v2.Operclientes();
            for (int fila = filaI; fila < filaF; fila ++)
            {  
                   dameValorExcel(fila,0,"S");      //referencia sivasa
                   if (valorS != null)
                   {
                       //generado el certificado insertamos operacion de envio a Sivasa.
                       sConsulta = "select s.numexp,s.codcli  from solicitudes s join refer r on (s.numexp = r.numexp) where r.referencia = '"+valorS+"'";
                       rsDatos = Utilidades.Conexion.select(sConsulta,conexion);
                       if (rsDatos.next()) 
                       {                                                                                 
                               oOperclientes.numexp = rsDatos.getString("numexp");
                               oOperclientes.refcliente = valorS;
                               oOperclientes.cliente = rsDatos.getInt("codcli");
                               oOperclientes.tipoperacion = "ENV";
                               oOperclientes.tipomensaje = "OSH";
                               oOperclientes.control = 0;
                               oOperclientes.postventa = 0;
                               oOperclientes.setFchenvioNow();
                               oOperclientes.setHoraenvioNow();
                               oOperclientes.estado = estado;
                               try
                               {
                                    if (oOperclientes.insert(conexion) == 1)
                                    {
                                       //conexion.commit();  
                                        conexion.rollback();
                                       logger.info(oOperclientes.refcliente);
                                    }
                                    else 
                                    {                                        
                                        conexion.rollback();
                                        logger.error("No se ha podido insertar registro en operclientes para la fila:"+fila);
                                    }
                               }
                               catch (Exception e)
                               {
                                   logger.error("Excepcion al insertar registro en operclientes para la fila:"+fila+ ". Descripcion: "+e.toString());
                               }
                               oOperclientes.clear();
                       }//if se localiza en refere y solicitudes
                       else
                       {
                           logger.error("La fila:"+fila+ ". No se localizan datos en solicitudes y refer.");
                       }
                       rsDatos.close();
                       rsDatos = null;
                       sConsulta = null;
                   }
                   else logger.error("Fila nº:"+fila+" con referencia nula");                   
            }//for filas excel
            conexion.close();
        }
        catch (Exception e)
        {
            logger.error("Excepcion general al insertar registro en operclientes. Descripcion: "+e.toString());
        }
        finally
        {
            try
            {
                if (!conexion.isClosed())
                {
                    conexion.rollback();
                    conexion.close();
                    conexion = null;
                }
            }
            catch (Exception e)
            {
                //e.printStackTrace();
            }
            
        }
        
        
    }
    
     public void dameValorExcel(int fila,int col,String tipo)  
    {
        String valor = null;
        try
        {    valorI = 0;
             valorD = 0.0;
             valorS = null;
             
             celda = oExcel.getCeldaFilaHoja(HojaActual,fila,col);                                 
             if (tipo.equals("I")) valorI = Utilidades.Excel.getIntegerCellValue(celda);  
             if (tipo.equals("D")) valorD = Utilidades.Excel.getDoubleCellValue(celda);
             if (tipo.equals("S")) valorS = Utilidades.Excel.getStringCellValue(celda);
        }   
        catch (FileNotFoundException fnfe)
        {
            valorI = 0;
            valorD = 0.0;
            valorS = null;
        }
        catch (IOException ioe)
        {
            valorI = 0;
            valorD = 0.0;
            valorS = null;
        }
        catch (Exception e)
        {
            valorI = 0;
            valorD = 0.0;
            valorS = null;
        }        
    }//dameValorExcel
     
     private void cargaPropiedades() 
    {
        try
        {                                         
            fPropiedades = new File(sRutaPropiedades);
            if (fPropiedades.exists())
            {                
                propiedades = new Utilidades.Propiedades(fPropiedades.getAbsolutePath());                             
                rutaLogENVOSH = propiedades.getValueProperty("sRutaLogENVOSH");
                rutaENVOSH = propiedades.getValueProperty("sRutaENVOSH");
                //FICHERO LOG4J
                PropertyConfigurator.configure(rutaLogENVOSH + "Log4j.properties");                      
            }// el fichero de propiedades existe.
            
        }
        catch (Exception e)
        {
           System.out.println("Error general en la carga de propiedades del proyecto. Descripión: "+e.toString());            
            
        }        
    }//cargaPropiedades 
    
}
