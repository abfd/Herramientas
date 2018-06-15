/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BancaMarch;

import servihabitat.*;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author Ana Belen de Frutos
 * * Proceso que lee de un excel los expedientes cuyo estado es ENVIAR. Si el estado es NO ENVIAR se descarta su envio.
 */
public class envioHistoricas 
{
    private java.sql.Connection conexion = null; 
    private Logger logger = Logger.getLogger(envioHistoricas.class);
    
    public static String sUrlExcel = null;
    public static Utilidades.Excel oExcel = null;            
    public static org.apache.poi.hssf.usermodel.HSSFCell celda = null;
    public static String HojaActual = null;
    
    public static void main(String[] args) 
    {
         //FICHERO LOG4J              
        envioHistoricas nwEnvio = new envioHistoricas();
        nwEnvio = null;
        System.gc();
    }
    
    
    public envioHistoricas()
    {//EN EL EXCEL COLUMNA 1 = EXPEDIENTE COLUMNA 2 = ESTADO ENVIAR/NO ENVIAR
        
         Objetos.v2.Solicitudes oSolicitudes = new Objetos.v2.Solicitudes();
         String numexp = "";       
        
         try
         {
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@oraserver02:1521:rvtnprod");            
            conexion.setAutoCommit(false);                                
              
            
            sUrlExcel = "/data/informes/BancaMarch/historicas/historicas.xls";
            oExcel = new Utilidades.Excel(sUrlExcel);              
            celda = null;
            HojaActual = "Hoja1";
            int totalFilas = 126;
            for (int fila = 0; fila < totalFilas; fila ++)
            {                                                
                celda = oExcel.getCeldaFilaHoja(HojaActual,fila,0);                                 
                numexp = Utilidades.Excel.getStringCellValue(celda);                  
                xmlBancaMarch.ValoracionHistoricas nwValoracion = new xmlBancaMarch.ValoracionHistoricas(numexp,817345);         
                PropertyConfigurator.configure("/data/informes/BancaMarch/historicas/" + "Log4j.properties");   
                if (nwValoracion.estadoOK) logger.info("Tasación generada: "+numexp);
                else logger.error("Error: "+numexp+". Descripción: "+nwValoracion.descError);
                nwValoracion = null;
                try
                {//ESPERAMOS 4 SEGUNDOS ANTES DE PROCESAR EL SIGUEINTE SOLICITUD                                   
                    Thread.currentThread().sleep(4000);
                }
                catch (InterruptedException ie)
                {                                                                                                
                     logger.error(" Imposible realizar espera");
                }
            }//for excel
            conexion.close();
         }//try
         catch (Exception e)
         {
             logger.error("Excepcion: "+e.toString());
         }
         finally
         {
             try
             {
                if (conexion != null && !conexion.isClosed())
                {                
                     conexion.rollback();
                     conexion.close();
                }
             }
             catch (Exception e)
             {
                 
             }                 
         }
    }//envioDOTA
    
    public void envioDOTA()
    {//EN EL EXCEL COLUMNA 1 = EXPEDIENTE COLUMNA 2 = ESTADO ENVIAR/NO ENVIAR
        
        
         String numexp = "";
         String estado = "";
         int totales = 0;
         int aEnviar = 0;
         int noEnviar = 0;
         int lote = 100;
         String sConsulta = null;
         String sUpdate = null;
         java.sql.ResultSet rs = null;
         Objetos.v2.Operclientes oOperclientes = new Objetos.v2.Operclientes();
        
         try
         {            
              
            
            sUrlExcel = "/data/informes/Servihabitat/enviosDOTA/enviosdota1.xls";
            oExcel = new Utilidades.Excel(sUrlExcel);              
            celda = null;
            HojaActual = "RESUMEN";
            int totalFilas = 1471;
            for (int fila = 0; fila < totalFilas; fila ++)
            {//
                 totales ++;
                 if (numexp != null)
                 {
                    numexp = oExcel.getCeldaFilaHoja(HojaActual, fila, 0).getStringCellValue();
                    estado = oExcel.getCeldaFilaHoja(HojaActual, fila, 1).getStringCellValue();
                    if (estado != null)
                    {
                         if (estado.trim().toUpperCase().equals("ENVIAR")) aEnviar ++;                        
                         else if (estado.trim().toUpperCase().equals("NO ENVIAR")) noEnviar ++;
                    }
                    else logger.error("El estado del expediente : "+numexp +"es nulo");
                 }
                 else logger.error("El expediente de la fila : "+fila+1 +"es nulo");
                 
            }//for excel            
         }//try
         catch (Exception e)
         {
             logger.error("Excepcion: "+e.toString());
         }
         finally
         {
            
             logger.info("Totales : "+totales);
             logger.info("A enviar : "+aEnviar);             
             logger.info("Noenviar : "+noEnviar);             
         }
    }//envioDOTA
    
}
