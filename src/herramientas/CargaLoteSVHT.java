/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package herramientas;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import java.util.Date;
/**
 *
 * @author abfd
 */
public class CargaLoteSVHT 
{
         
    private Logger logger = Logger.getLogger(CargaLoteSVHT.class);
    
    
    private java.sql.ResultSet rs = null;
    private java.sql.ResultSet rs2 = null;
    
    //private String sUrlExcel = "/data/informes/cargapretasaExcel/LoteSVHT/Stock_L3T_VALTECNIC_v04072017.xls"; 1º excel que se cargo en lotesvht
    //private String sUrlExcel = "/data/informes/cargapretasaExcel/LoteSVHT/Stock_ACT_BC-CABK 10082017 VALTECNIC_ID.xls"; 2º excel que se cargo en lotesvht
    //private String sUrlExcel = "/data/informes/cargapretasaExcel/LoteSVHT/Stock_ACT_BC-CABK 29082017 VALTECNIC_ID.xls";
    //private String sUrlExcel = "/data/informes/cargapretasaExcel/LoteSVHT/Stock_ACT_BC-CABK 29082017 VALTECNIC_ID_2.xls";
    //private String sUrlExcel = "/data/informes/cargapretasaExcel/LoteSVHT/Stock_ACT_BC-CABK 29082017 VALTECNIC_ID_2_mio.xls";
    private String sUrlExcel = "/data/informes/cargapretasaExcel/LoteSVHT/VALTECNIC_Perímetro_L2T_0731_13042018_mio.xls";
    
    private Utilidades.Excel oExcel = null;            
    private org.apache.poi.hssf.usermodel.HSSFCell celda = null;
    private String HojaActual = "Hoja1";
    
    //campos recibidos en el excel
    private String  numexp = null;
    private Integer sociedad = null;
    private String  ur = null;
    private String  cartera = null;
    private String  direccion = null;
    private String  poblacion = null;
    private Integer codposta = null;
    private String  zona = null;
    private String  provincia = null;
    private String  tipoinm = null;
    private String  refcatastral = null;
    private Integer numregistro = null;
    private String  finca = null;
    private String  tomo = null;
    private String  libro = null;
    private String  folio = null;
    private String  promoconjunta = null;
    private String  promobra = null;
    private String  agrupacion = null;
    private String  tipoagrupacion = null;
    private Integer ursagrupacon = null;
    private Date    fchtasreferencia = null;
    private Double  valtasreferencia = null;
    private String  tipovaloracion = null;
    private String  tipotasacion = null;
    private String  lote = null;
    private String  tasadora = null;
    private String  instruccion = null;
    
    
    
    
    public static void main(String[] args) 
    {
        //CargaLoteSVHT carga = new CargaLoteSVHT("7108");
        CargaLoteSVHT carga = new CargaLoteSVHT();
        carga.actualizaExpedienteLote("7108");
        carga = null;
        System.gc();
    }
    
    /**
     * Realiza la carga de datos del lote recibidos en el excel en la tabla LOTESVHT
     */
    
     
    
    public void CargaLoteSVHT(String loteActual)
    {
        
        java.sql.Connection conexion = null;
        int totalURS = 0;
        int inicioURS = 0;
        java.text.SimpleDateFormat formatoFecha = new java.text.SimpleDateFormat("dd-MM-yyyy");
        String sInsert = null;
        String sUpdate = null;
        int insertadas = 0;
        int noInsertadas = 0;
        int nuevas = 0;
        int actualizadas = 0;
        
        java.sql.ResultSet rs = null;
        String sConsulta = "";
        
        try
        {
            //FICHERO LOG4J
            PropertyConfigurator.configure("/data/informes/cargapretasaExcel/LoteSVHT/" + "Log4j.properties");   
        
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@oraserver03:1521:rvtn3");
            //conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:desarrollo/des0329@oraserver04:1521:rvtn4");
            conexion.setAutoCommit(false);  
            oExcel = new Utilidades.Excel(sUrlExcel);  
            //totalURS = oExcel.getMaximaFila(HojaActual);                                                       
            totalURS = 3791;
            logger.info("Iniciamos carga del lote: "+sUrlExcel);
            for (int fila = inicioURS; fila <totalURS; fila ++)
            {
                clear();  
                sInsert = "";
                try
                {                   
                    //0.-Sociedad
                    celda = oExcel.getCeldaFilaHoja(HojaActual,fila,0);  
                    sociedad = new Double(celda.getNumericCellValue()).intValue();
                    //1.- UR
                    celda = oExcel.getCeldaFilaHoja(HojaActual,fila,1);  
                    ur = new Integer (new Double(celda.getNumericCellValue()).intValue()).toString();
                    //2.- Cartera
                    celda = oExcel.getCeldaFilaHoja(HojaActual,fila,2);
                    cartera = celda.getStringCellValue();
                    //3.- Direccion
                    celda = oExcel.getCeldaFilaHoja(HojaActual,fila,3);
                    direccion = Utilidades.Cadenas.procesarComillasConsulta(celda.getStringCellValue());                    
                    //4.- Poblacion
                    celda = oExcel.getCeldaFilaHoja(HojaActual,fila,4);
                    poblacion = Utilidades.Cadenas.procesarComillasConsulta(celda.getStringCellValue());
                    //5.- CP
                    celda = oExcel.getCeldaFilaHoja(HojaActual,fila,5);
                    codposta = new Double(celda.getNumericCellValue()).intValue();
                    //6.- Zona
                    celda = oExcel.getCeldaFilaHoja(HojaActual,fila,6);
                    zona = celda.getStringCellValue();
                    //7.- Provincia
                    celda = oExcel.getCeldaFilaHoja(HojaActual,fila,7);
                    provincia = Utilidades.Cadenas.procesarComillasConsulta(celda.getStringCellValue());
                    //8.- tipo inmueble
                    celda = oExcel.getCeldaFilaHoja(HojaActual,fila,8);
                    tipoinm = celda.getStringCellValue();
                    //9.- ref. catastral
                    celda = oExcel.getCeldaFilaHoja(HojaActual,fila,9);
                    try
                    {
                        refcatastral = celda.getStringCellValue();
                    }
                    catch (Exception e)
                    {
                        refcatastral = new Integer (new Double(celda.getNumericCellValue()).intValue()).toString();
                    }
                    //10.- nº registro
                    celda = oExcel.getCeldaFilaHoja(HojaActual,fila,10);
                    try
                    {
                        numregistro = new Integer (new Double(celda.getNumericCellValue()).intValue());
                    }
                    catch (Exception e)
                    {
                        numregistro = null;
                    }

                    
                    //11.- finca
                    celda = oExcel.getCeldaFilaHoja(HojaActual,fila,11);
                    try
                    {
                        finca = celda.getStringCellValue();
                    }
                    catch(Exception e)
                    {
                        finca = new Integer (new Double(celda.getNumericCellValue()).intValue()).toString();
                    }
                    //12.- tomo
                    celda = oExcel.getCeldaFilaHoja(HojaActual,fila,12);
                    tomo = new Integer (new Double(celda.getNumericCellValue()).intValue()).toString();
                    //13.- libro
                    celda = oExcel.getCeldaFilaHoja(HojaActual,fila,13);
                    libro = new Integer (new Double(celda.getNumericCellValue()).intValue()).toString();
                    //14.- folio
                    celda = oExcel.getCeldaFilaHoja(HojaActual,fila,14);
                    folio = new Integer (new Double(celda.getNumericCellValue()).intValue()).toString();

                    //15.- promocion conjunta
                    celda = oExcel.getCeldaFilaHoja(HojaActual,fila,15);
                    try
                    {
                        if (new Double(celda.getNumericCellValue()) != null)
                        {
                            promoconjunta = new Integer (new Double(celda.getNumericCellValue()).intValue()).toString();
                        }
                    }
                    catch (Exception e)
                    {
                        promoconjunta = celda.getStringCellValue();
                    }
                    //16.- promocional obra
                    celda = oExcel.getCeldaFilaHoja(HojaActual,fila,16);
                    try
                    {
                        if (new Double (celda.getNumericCellValue()) != null )
                        {
                            promobra = new Integer (new Double(celda.getNumericCellValue()).intValue()).toString();
                        }
                    }
                    catch (Exception e)
                    {
                        promobra = celda.getStringCellValue();
                    }
                    //17.- agrupacion
                    celda = oExcel.getCeldaFilaHoja(HojaActual,fila,17);
                    try
                    {
                        if (new Double(celda.getNumericCellValue()) != null )
                        {
                            agrupacion = new Integer (new Double(celda.getNumericCellValue()).intValue()).toString();
                        }                
                    }
                    catch (Exception e)
                    {
                        agrupacion = celda.getStringCellValue();
                    }
                    //18.- tipo agrupacion
                    celda = oExcel.getCeldaFilaHoja(HojaActual,fila,18);
                    tipoagrupacion = celda.getStringCellValue();
                    //19.- urs agrupacion
                    celda = oExcel.getCeldaFilaHoja(HojaActual,fila,19);
                    ursagrupacon = new Integer (new Double(celda.getNumericCellValue()).intValue());
                    //20.- fecha tasacion referencia                
                    celda = oExcel.getCeldaFilaHoja(HojaActual,fila,20);
                    fchtasreferencia = celda.getDateCellValue();                   
                    //22.- Importe TAS Referencia
                    celda = oExcel.getCeldaFilaHoja(HojaActual,fila,22);
                    try
                    {
                        valtasreferencia = celda.getNumericCellValue();
                    }
                    catch (Exception e)
                    {
                        valtasreferencia = new Double("0");
                    }
                    //23.- Tipo valoracion referencia
                    celda = oExcel.getCeldaFilaHoja(HojaActual,fila,23);
                    try
                    {
                        tipovaloracion = celda.getStringCellValue();
                    }
                    catch (Exception e)
                    {
                        tipovaloracion = new Integer (new Double(celda.getNumericCellValue()).intValue()).toString();
                    }
                    //25.- tipo tasacion
                    celda = oExcel.getCeldaFilaHoja(HojaActual,fila,25);
                    tipotasacion = celda.getStringCellValue();
                    //26.- lotes de solicitud
                    celda = oExcel.getCeldaFilaHoja(HojaActual,fila,26);
                    lote = celda.getStringCellValue();
                    //27.- tasadora
                    celda = oExcel.getCeldaFilaHoja(HojaActual,fila,27);
                    tasadora = celda.getStringCellValue();
                    //28.- instruccion de solicitudes 
                    celda = oExcel.getCeldaFilaHoja(HojaActual,fila,28);
                    instruccion = celda.getStringCellValue();
                    
                    sConsulta = "SELECT * FROM lotesvht WHERE ur = '"+ur+"' and lotevt = '"+loteActual+"'";
                    rs = Utilidades.Conexion.select(sConsulta, conexion);
                    if (!rs.next())
                    {
                        nuevas ++;
                        sInsert = "INSERT INTO lotesvht VALUES (null,"+sociedad+",'"+ur+"','"+cartera+"','"+direccion+"','"+poblacion+"',"+codposta+",'"+zona+"','"+provincia+"','"+tipoinm+"','"+refcatastral+"',"+numregistro;
                        sInsert += ",'"+finca+"','"+tomo+"','"+libro+"','"+folio+"','"+promoconjunta+"','"+promobra+"','"+agrupacion+"','"+tipoagrupacion+"',"+ursagrupacon+",'"+formatoFecha.format(fchtasreferencia)+"',"+valtasreferencia;
                        sInsert += ",'"+tipovaloracion+"','"+tipotasacion+"','"+lote+"','"+tasadora+"','"+instruccion+"','"+loteActual+"')";
                    
                        if (Utilidades.Conexion.insert(sInsert, conexion) == 1) insertadas ++;
                        else noInsertadas  ++;
                    }
                    else if (rs.getString("valtasreferencia") == null || rs.getDouble("valtasreferencia") == new Double(0))
                    {
                        actualizadas ++; 
                        sUpdate = "UPDATE lotesvht SET valtasreferencia = "+valtasreferencia+" WHERE ur= '"+ur+"'";
                         if (Utilidades.Conexion.update(sUpdate, conexion) == 1)
                         {
                            logger.info("Actualizado valor de tasación de la UR: "+ur+". Fila: "+fila);
                            insertadas ++;
                         }
                         else
                         {
                             logger.error ("NO se ha Actualizado valor de tasación de la UR: "+ur+". Fila: "+fila);
                         }
                    }
                    else insertadas ++;
                    rs.close();
                    rs = null;
                }
                catch (Exception e)
                {
                    noInsertadas  ++;
                    logger.error("Excepción en la inserción de la UR: "+ur+". Fila: "+fila+". Descripción: "+e.toString());
                }                
            }//   for
            if (insertadas == totalURS)
            {
                conexion.commit();
                logger.info("Insertadas: "+insertadas+" filas");
            }
            else
            {
                conexion.rollback();
                logger.info("Insertadas: "+insertadas+" .Fallidad: "+noInsertadas);
            }
            logger.info("Nuevas: "+nuevas);
            logger.info("Actualizadas: "+actualizadas);
            conexion.close();
            conexion = null;
        }
        catch (Exception e)
        {
            logger.error("Excepción en la carga del lote: "+sUrlExcel+". Descripción: "+e.toString());
        }
        finally
        {
            try
            {
                if (conexion != null && !conexion.isClosed()) conexion.close();                
            }
            catch (java.sql.SQLException sqlException)
            {     
                logger.error("Imposible cerrar conexión con la B.D");
            }     
            logger.info("Finalizamos carga del lote: "+sUrlExcel);
        }
    }
    
    public void actualizaExpedienteLote(String loteActual)
    {
        java.sql.Connection conexion02 = null;
        //java.sql.Connection conexion03 = null;
        java.sql.ResultSet rsLotesvht = null;
        java.sql.ResultSet rsUrDadasdeAlta = null;
        String sConsulta = "";
        String sConsulta2 = "";
        String sUpdate = "";
        int numurdadasdealta = 0;  
        String numexpvt = "";
        
        try
        {
            PropertyConfigurator.configure("/data/informes/cargapretasaExcel/LoteSVHT/" + "Log4j.properties");   
        
            conexion02 = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@oraserver03:1521:rvtn3");
            conexion02.setAutoCommit(false);  
            //conexion03 = Utilidades.Conexion.getConnection("jdbc:oracle:thin:desarrollo/des0329@oraserver03:1521:rvtn3");
            //conexion03.setAutoCommit(false);  
            
            sConsulta = "SELECT * FROM lotesvht WHERE numexp is null and lotevt = '"+loteActual+"'";
            rsLotesvht = Utilidades.Conexion.select(sConsulta, conexion02);
            while (rsLotesvht.next())
            {
                numurdadasdealta = 0;
                numexpvt = "";
                sConsulta2 = "select s.numexp,s.fchenc,d.numseguim from datosreg d join solicitudes s on d.numexp = s.numexp where s.codcli in (255,355,755,888,1055) and s.fchenc > '17042018' and s.codest != 5 and d.numseguim is not null and d.numseguim = '"+rsLotesvht.getString("ur")+"'";
                rsUrDadasdeAlta = Utilidades.Conexion.select(sConsulta2, conexion02);
                while (rsUrDadasdeAlta.next()) 
                {
                    numurdadasdealta ++;
                    numexpvt = rsUrDadasdeAlta.getString("numexp");
                }
                if (numurdadasdealta == 1)
                {
                    sUpdate = "update lotesvht set numexp = '"+numexpvt+"' where ur = '"+rsLotesvht.getString("ur")+"' and lotevt = '"+loteActual+"'";
                    if (Utilidades.Conexion.update(sUpdate, conexion02) == 1) 
                    {
                        conexion02.commit();
                        logger.info("Actualizado numexp: "+numexpvt+" para la ur: "+rsLotesvht.getString("ur"));
                    }
                    else
                    {
                        conexion02.rollback();
                        logger.error("Imposible actualizar numexp: "+numexpvt+" para la ur: "+rsLotesvht.getString("ur"));
                    }
                }
                else
                {
                    if (numurdadasdealta > 1) logger.error("La ur: "+rsLotesvht.getString("ur")+" está asignada a mas de 1 expediente. Total en: "+numurdadasdealta);
                }
            }
            
            
            conexion02.close();
            conexion02 = null;
            //conexion03.close();
            //conexion03 = null;
        }
        catch (Exception e)
        {
             logger.error("Excepción en la carga del lote: "+sUrlExcel+". Descripción: "+e.toString());
        }
        finally
        {
            try
            {
                if (conexion02 != null && !conexion02.isClosed()) conexion02.close();                
            }
            catch (java.sql.SQLException sqlException)
            {     
                logger.error("Imposible cerrar conexión con la B.D conexion02");
            }
            /*
            try
            {
                if (conexion03 != null && !conexion03.isClosed()) conexion03.close();                
            }
            catch (java.sql.SQLException sqlException)
            {     
                logger.error("Imposible cerrar conexión con la B.D conexion03");
            }
*/
            
        }
        
    }

    private void clear()
    {
        sociedad = null;
        ur = null;
        cartera = null;
        direccion = null;
        poblacion = null;
        codposta = null;
        zona = null;
        provincia = null;
        tipoinm = null;
        refcatastral = null;
        numregistro = null;
        finca = null;
        tomo = null;
        libro = null;
        folio = null;
        promoconjunta = null;
        promobra = null;
        agrupacion = null;
        tipoagrupacion = null;
        ursagrupacon = null;
        fchtasreferencia = null;
        valtasreferencia = null;
        tipovaloracion = null;
        tipotasacion = null;
        lote = null;
        tasadora = null;
        instruccion = null;        
    }
    
}
