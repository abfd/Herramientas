/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servihabitat;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author Ana Belen de Frutos
 * * Proceso que lee de un excel los expedientes cuyo estado es ENVIAR. Si el estado es NO ENVIAR se descarta su envio.
 */
public class envioDOTA 
{
    private java.sql.Connection conexion = null; 
    private Logger logger = Logger.getLogger(envioDOTA.class);
    
    public static String sUrlExcel = null;
    public static Utilidades.Excel oExcel = null;            
    public static org.apache.poi.hssf.usermodel.HSSFCell celda = null;
    public static String HojaActual = null;
    
    public static void main(String[] args) 
    {
         //FICHERO LOG4J
        PropertyConfigurator.configure("/data/informes/Servihabitat/enviosDOTA/" + "Log4j.properties");   
        envioDOTA nwEnvioDOTA = new envioDOTA();
        nwEnvioDOTA = null;
        System.gc();
    }
    
    
    public envioDOTA()
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
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@oraserver01:1521:rvtn1");
            //conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:desarrollo/des0329@oraserver00:1521:rvtn");
            conexion.setAutoCommit(false);                                
              
            
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
                         if (estado.trim().toUpperCase().equals("ENVIAR"))
                         {                             
                             sConsulta = "SELECT tipoinm,codcli,codest,referencia FROM solicitudes s JOIN refer r ON(s.numexp = r.numexp) WHERE s.numexp = '"+numexp.trim()+"'";    
                             rs = Utilidades.Conexion.select(sConsulta,conexion);                                    
                             if (rs.next())
                             {
                                 if (!rs.getString("tipoinm").equals("XXC") && rs.getInt("codest") == 11)
                                 {
                                     if (lote > 0)
                                     {
                                         //insertamos un nuevo envio de tasacion
                                        oOperclientes.numexp = numexp;
                                        oOperclientes.cliente = rs.getInt("codcli");
                                        oOperclientes.refcliente = rs.getString("referencia");
                                        oOperclientes.tipoperacion = "ENV";
                                        oOperclientes.tipomensaje = "ENT";
                                        oOperclientes.control = 11;
                                        oOperclientes.postventa = 0;
                                        oOperclientes.setFchenvioSistema();
                                        oOperclientes.setHoraenvioSistema();
                                        oOperclientes.estado = "0";
                                        if (oOperclientes.insert(conexion) == 1) 
                                        {
                                            sUpdate = "UPDATE solicitudes SET codest = 10 WHERE numexp = '"+numexp.trim()+"'";
                                            if (Utilidades.Conexion.update(sUpdate, conexion) == 1)
                                            {
                                                conexion.commit();
                                                logger.info("Enviado: "+numexp);                                                
                                                aEnviar ++;
                                                lote --;
                                            }
                                            else
                                            {
                                                logger.error("Sin enviar: "+numexp+". No se actualiza estado a 10 en solicitudes");
                                                conexion.rollback();
                                            }
                                        }
                                        else
                                        {
                                            logger.error("Sin enviar: "+numexp+". No se inserta en operclientes");
                                            conexion.rollback();
                                        }
                                     }
                                     
                                 }
                                 else logger.error("Sin enviar. Estado NO 11 o XXC");
                             }
                             rs.close();
                             rs = null;
                             sConsulta = null;
                             sUpdate = null;
                         }
                         else if (estado.trim().toUpperCase().equals("NO ENVIAR")) noEnviar ++;
                    }
                    else logger.error("El estado del expediente : "+numexp +"es nulo");
                 }
                 else logger.error("El expediente de la fila : "+fila+1 +"es nulo");
                 
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
             logger.info("Totales : "+totales);
             logger.info("Enviados : "+aEnviar);             
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
