/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package herramientas;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author abfd
 * leemos de un excel los id de expediente y registramos en operclientes un nuevo envío para cada una
 * de las tasaciones pertenecientes a ese expediente.
 */
public class enviaExpedientesSareb {
 
    private java.sql.Connection conexion = null; 
    private Logger logger = Logger.getLogger(enviaExpedientesSareb.class);
    
    public static String sUrlExcel = null;
    public static Utilidades.Excel oExcel = null;            
    public static org.apache.poi.hssf.usermodel.HSSFCell celda = null;
    public static String HojaActual = null;
    
    public static void main(String[] args) 
    {
         //FICHERO LOG4J              
        enviaExpedientesSareb nwEnvio = new enviaExpedientesSareb();
        nwEnvio = null;
        System.gc();
    }
    
    public enviaExpedientesSareb()
    {
        String idExpediente = "" ;
        String sConsulta = "";
        java.sql.ResultSet rs = null;
        int numexptes = 0;
        Objetos.v2.Operclientes oOperclientes = new Objetos.v2.Operclientes();
        int inumInsertadas = 0;
        try
         {
            PropertyConfigurator.configure("/data/informes/sareb/envioExcel/" + "Log4j.properties");   
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@oraserver02:1521:rvtnprod");            
            conexion.setAutoCommit(false);                                                          
            sUrlExcel = "/data/informes/sareb/envioExcel/envioExcel.xls";
            oExcel = new Utilidades.Excel(sUrlExcel);              
            celda = null;
            HojaActual = "Hoja1";
            int filaInicial = 90;
            int totalFilas = 104;
            for (int fila = filaInicial; fila < totalFilas; fila ++)
            {                                                
                numexptes = 0;
                inumInsertadas = 0;
                celda = oExcel.getCeldaFilaHoja(HojaActual,fila,0);                  
                idExpediente = Utilidades.Excel.getStringCellValue(celda); 
                logger.info("Procesamos envío id: "+idExpediente);
                sConsulta = "SELECT r.numexp,r.referencia FROM refer r JOIN solicitudes s ON (r.numexp = s.numexp) JOIN operclientes o ON (s.numexp = o.numexp) WHERE r.idexpediente = '"+idExpediente+"' AND s.codest = 10 AND s.codcli = 250 AND s.tipoinm != 'XXI' AND o.tipoperacion = 'REC' AND o.tipomensaje = 'STA'";
                rs = Utilidades.Conexion.select(sConsulta, conexion);
                while (rs.next())
                {
                    numexptes ++;                   
                    oOperclientes.clear();
                    oOperclientes.numexp = rs.getString("numexp");
                    oOperclientes.cliente = 250;
                    oOperclientes.refcliente = rs.getString("referencia");
                    oOperclientes.tipoperacion = "ENV";
                    oOperclientes.tipomensaje = "ENT";
                    oOperclientes.control = 0;
                    oOperclientes.postventa = 0;
                    oOperclientes.setFchenvioNow();
                    oOperclientes.setHoraenvioNow();
                    //oOperclientes.estado = "X";  estado para generar el CD
                    oOperclientes.estado = "E"; //estado para envios normales por sistema.
                    if (oOperclientes.insert(conexion) == 1) inumInsertadas ++;
                }
                if (numexptes == inumInsertadas)
                {
                    conexion.commit();
                    logger.info("Id Expediente: "+idExpediente+". Insertados: "+inumInsertadas+" expedientes de Valtecnic.");
                }
                else
                {
                    conexion.rollback();
                    logger.info("Id Expediente: "+idExpediente+". NO Insertado. Totales en VT: "+numexptes+". Insertados: "+inumInsertadas);
                }
            }//for excel
            conexion.close();
         }//try
         catch (Exception e)
         {
             try
             {
                conexion.rollback();
             }
             catch (Exception ex){}
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
    }
    
}
