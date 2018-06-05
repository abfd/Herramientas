/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package herramientas;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author Ana Belen de Frutos
 */
public class CEVUCI 
{
  
    private java.io.File fPropiedades;      
    private Logger logger = Logger.getLogger(CEVUCI.class);
    private String rutaCEVUCI = "/data/informes/CEVUCI/";
    
    java.sql.ResultSet rs = null;
    
    // Atributos de estado
    private boolean estadoOK = true;
    private int error = 0;
    private String descError = "";
    
    private Connection conexion = null;  
    private Connection conexionOCO = null;  
    private Connection conexGraficos = null;
    private Connection conexGraficosOCO = null;
    
    private int insertados = 0;
    private int fallos = 0;
    private int noLocalizados = 0;
    private int duplicados = 0;
    
    private Objetos.v2.Solicitudes oSolicitudes = new Objetos.v2.Solicitudes();
    private Objetos.v2.Solicitudes oSolicitudesoco = new Objetos.v2.Solicitudes();
    private Objetos.Documenta oDocumenta = new Objetos.Documenta();
    private Objetos.Datosreg oDatosreg = new Objetos.Datosreg();
    private Objetos.Catastro oCatastro = new Objetos.Catastro();
    private Objetos.v2.Refer oRefer = new Objetos.v2.Refer();
    private Objetos.v2.Entrada oEntrada = new Objetos.v2.Entrada();
    private Objetos.Produsu oProdusu = new Objetos.Produsu();
    private Objetos.Avisos oAvisos = new Objetos.Avisos();
    private Objetos.v2.Otrastas oOtrastas = new Objetos.v2.Otrastas();
    
    
    //EXCEL
    public static String sUrlExcel = "/data/informes/CEVUCI/FicheroValtecnic09052013-2.xls";
    public static Utilidades.Excel oExcel = null;            
    public static org.apache.poi.hssf.usermodel.HSSFCell celda = null;
    public static String HojaActual = null;
    
    public  String fcatastral = null;
    public  String gestor = null;
    public  String tlfgestor = null;
    public  String eci = null;
    public  String tlfeci = null;
    public  String ecr = null;
    public  String tlfecr = null;
    public  String tipocontacto = null;
    public  String tipoinmOrigen = null;
    private boolean esHistorico = false;
    
    private   int fila = 0;
    
    public static void main(String[] args) 
    {
        CEVUCI carga = new CEVUCI();
        carga = null;
        System.gc();
    }
    
    /*
     public CEVUCI()
    {//realizadas por ANIDA
        
        String numexpc = null;
        String objeto = null;
        String sConsulta = null;
        java.sql.ResultSet rs = null;
        String expOrigen = null;
        String expDestino = null;   
        int valor = 0;
      
        
        
        try
        {
             //HojaActual = "Certificación energetica UCI"; PRIMERA HOJA DADA DE ALTA.
            sUrlExcel = "/data/informes/CEVUCI/certificacionesanida.xls";
            HojaActual = "Hoja1";
            //1.- cargamos el fichero de propiedades
            //FICHERO LOG4J
             PropertyConfigurator.configure(rutaCEVUCI + "Log4j.properties");   
            //1.- POR CADA TASACIÓN LOCALIZADA GENERAMOS SU EXPEDIENTE C.E       

             //CONEXION OCO
             //conexionOCO = Utilidades.Conexion.getConnection("jdbc:oracle:thin:oco/oco0329@oraserver01:1521:rvtn1");
             //conexionOCO.setAutoCommit(false);    
             //conexGraficosOCO = Utilidades.Conexion.getConnection("jdbc:oracle:thin:ocoinfx/ocoinfx0329@oraserver01:1521:rvtn1");                        
             //conexGraficosOCO.setAutoCommit(false); 
             
            //CONEXION VT 
            //conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:desarrollo/des0329@oraserver00:1521:rvtn"); 
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@oraserver01:1521:rvtn1");            
            conexion.setAutoCommit(false);    

            //conexGraficos = Utilidades.Conexion.getConnection("jdbc:oracle:thin:pruebainfx/pruebainfx@oraserver00:1521:rvtn");            
            conexGraficos = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecinfx/valtecinfx@oraserver01:1521:rvtn1");            
            conexGraficos.setAutoCommit(false); 
                        
            
            //RECUPERAMOS DATOS DEL EXCEL
            oExcel = new Utilidades.Excel(sUrlExcel); 
            
            for (fila = 0; fila <= 236; fila ++)
            {
                expOrigen = dameValorExcel(fila,0);
                expOrigen = Utilidades.Cadenas.TrimTotal(expOrigen)                                                ;
                gestor =  dameValorExcel(fila,11);  //gestor en refer responsable comercial en el excel
                tlfgestor =  "";
                eci =  dameValorExcel(fila,12);  //responsable gestion va a contacto
                tlfeci =  "";  //ref_catastral
                                
                
                                                     
                    oSolicitudes.clear();
                    if (oSolicitudes.load(expOrigen, conexion) == 1)
                    {
                    
                        if (Funciones.Fcomunes.enHistorico(conexion, expOrigen)) esHistorico = true;
                        else esHistorico = false;
                        expDestino = expOrigen.substring(0, (expOrigen.length()-2))+"CE";
                        
                        logger.info("Volcando datos tasacion: "+expOrigen+" numexpc: "+numexpc+" objeto: "+objeto);
                        if (vuelcaDatosTasacionEnVT (expOrigen,expDestino))
                        {
                            if (vuelcaDocumentacionEnVT (expOrigen,expDestino))
                            {                    
                                conexion.commit();      //DATOS
                                conexGraficos.commit(); //GRAFICOS
                                insertados ++;
                                logger.info ("Tasación de origen: "+expOrigen+ " volcada sobre la tasación destino: "+expDestino);
                            }
                            else
                            {
                                conexion.rollback();    //DATOS
                                conexGraficos.rollback();//GRAFICOS
                                fallos ++;
                                logger.error("Imposible volcar datos graficos de la tasación: "+expOrigen+ " sobre la tasación: "+expDestino+". Motivo: "+descError);            
                            }                                
                        }
                        else
                        {
                            conexion.rollback();
                            fallos ++;
                            logger.error("Imposible volcar datos de la tasación: "+expOrigen+ " sobre la tasación: "+expDestino+". Motivo: "+descError);            
                        }
                    }
                    else
                    {
                        logger.info ("Tasación de origen: "+expOrigen+ " no existe en VT");  
                        noLocalizados ++;
                    }
                                
                numexpc = null;
                objeto = null;
                expOrigen = null;
                fcatastral = null;
                gestor = null;
                tlfgestor = null;
                eci = null;  
                tlfeci = null;
                ecr = null;
                tlfecr = null;
            }//for filas excel
            conexion.close();            
            conexGraficos.close();            
        }
        catch (Exception e)
        {
              logger.error("Excepción general en el vuelco de datos. Motivo: "+e.toString());
        }
        finally
        {
            logger.info("Insertados : "+insertados);
            logger.info("Fallos : "+fallos);
            logger.info("No Localizados : "+noLocalizados);
            
            try
            {
                if (conexion != null && !conexion.isClosed()) conexion.close();                
            }
            catch (SQLException sqle)
            {     
                logger.info("Imposible cerrar conexión con la B.D Oracle. Motivo: "+sqle.toString());
            }                
            try
            {
                if (conexGraficos != null && !conexGraficos.isClosed()) conexGraficos.close();                
            }
            catch (SQLException sqle)
            {     
                logger.info("Imposible cerrar conexión con la B.D. DOCUMENTACION Oracle. Motivo: "+sqle.toString());
            }             
        }

        
    }//CEVUCI()
    */
    
    /*
     public CEVUCI()
    {//realizadas por VALTECNIC para UCI
        
        String numexpc = null;
        String objeto = null;
        String sConsulta = null;
        java.sql.ResultSet rs = null;
        String expOrigen = null;
        String expDestino = null;   
        int valor = 0;
      
        
        //prueba datos
        //String expOrigen = "129.61-15042-11";
        //String expDestino = "129.61-15042-11-CEV";  //duda. que numero asignamos.
        //prueba documentacion
        //String expOrigen = "129.15-10321-12";
        //String expDestino = "1235";  //duda. que numero asignamos.
        try
        {
             //HojaActual = "Certificación energetica UCI"; PRIMERA HOJA DADA DE ALTA.
            HojaActual = "80CEMAS";
            //1.- cargamos el fichero de propiedades
            //FICHERO LOG4J
             PropertyConfigurator.configure(rutaCEVUCI + "Log4j.properties");   
            //1.- POR CADA TASACIÓN LOCALIZADA GENERAMOS SU EXPEDIENTE C.E       

             //CONEXION OCO
             conexionOCO = Utilidades.Conexion.getConnection("jdbc:oracle:thin:oco/oco0329@oraserver01:1521:rvtn1");
             conexionOCO.setAutoCommit(false);    
             conexGraficosOCO = Utilidades.Conexion.getConnection("jdbc:oracle:thin:ocoinfx/ocoinfx0329@oraserver01:1521:rvtn1");                        
             conexGraficosOCO.setAutoCommit(false); 
             
            //CONEXION VT 
            //conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:desarrollo/des0329@oraserver00:1521:rvtn"); 
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@oraserver01:1521:rvtn1");            
            conexion.setAutoCommit(false);    

            //conexGraficos = Utilidades.Conexion.getConnection("jdbc:oracle:thin:pruebainfx/pruebainfx@oraserver00:1521:rvtn");            
            conexGraficos = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecinfx/valtecinfx@oraserver01:1521:rvtn1");            
            conexGraficos.setAutoCommit(false); 
                        
            
            //RECUPERAMOS DATOS DEL EXCEL
            oExcel = new Utilidades.Excel(sUrlExcel); 
            
            for (fila = 0; fila <= 79; fila ++)
            {
                numexpc = dameValorExcel(fila,1);
                valor = 0;
                valor = Integer.parseInt(numexpc.substring(2,numexpc.length()));
                numexpc = numexpc.substring(0, 2)+"-"+Integer.toString(valor);
                //if (numexpc.substring(2, 3).equals("0")) numexpc = numexpc.substring(0, 2)+"-"+numexpc.substring(3,numexpc.length());
                //else numexpc = numexpc.substring(0, 2)+"-"+numexpc.substring(2,numexpc.length());
                objeto = dameValorExcel(fila,2);
                fcatastral = dameValorExcel(fila,24);  //ref_catastral
                gestor =  dameValorExcel(fila,22);  //ref_catastral
                tlfgestor =  dameValorExcel(fila,23);  //ref_catastral
                eci =  dameValorExcel(fila,17);  //ref_catastral 
                tlfeci =  dameValorExcel(fila,18);  //ref_catastral
                ecr =  dameValorExcel(fila,20);  //ref_catastral
                tlfecr =  dameValorExcel(fila,21);  //ref_catastral
                tipocontacto = dameValorExcel(fila,27);  //ref_catastral
                
                sConsulta = "select s.numexp,d.finalidad finalidad,s.fchrem fecha from solicitudes s join refer r on (s.numexp = r.numexp) join documenta d on s.numexp = d.numexp where r.referencia = '"+numexpc+"' and r.objeto = '"+objeto+"' and s.codest = 10 union ";
                sConsulta += " select s.numexp,d.finalidad finalidad,s.fchrem fecha from his_solicitudes s join refer r on (s.numexp = r.numexp) join his_documenta d on s.numexp = d.numexp where r.referencia = '"+numexpc+"' and r.objeto = '"+objeto+"' and s.codest = 10 ";
                sConsulta += " order by finalidad, fecha desc";

                rs = Utilidades.Conexion.select(sConsulta, conexion);
                if (rs.next())
                {                    
                    expOrigen = rs.getString("numexp");
                    oSolicitudesoco.clear();
                    if (oSolicitudesoco.load(expOrigen, conexionOCO) == 0)
                    {
                    
                        if (Funciones.Fcomunes.enHistorico(conexion, expOrigen)) esHistorico = true;
                        else esHistorico = false;
                        expDestino = expOrigen;
                        logger.info("Volcando datos tasacion: "+expOrigen+" numexpc: "+numexpc+" objeto: "+objeto);
                        if (vuelcaDatosTasacion (expOrigen,expDestino))
                        {
                            if (vuelcaDocumentacion (expOrigen,expDestino))
                            {                    
                                conexionOCO.commit();      //DATOS
                                conexGraficosOCO.commit(); //GRAFICOS
                                insertados ++;
                                logger.info ("Tasación de origen: "+expOrigen+ " volcada sobre la tasación destino: "+expDestino);
                            }
                            else
                            {
                                conexionOCO.rollback();    //DATOS
                                conexGraficosOCO.rollback();//GRAFICOS
                                fallos ++;
                                logger.error("Imposible volcar datos graficos de la tasación: "+expOrigen+ " sobre la tasación: "+expDestino+". Motivo: "+descError);            
                            }                                
                        }
                        else
                        {
                            conexionOCO.rollback();
                            fallos ++;
                            logger.error("Imposible volcar datos de la tasación: "+expOrigen+ " sobre la tasación: "+expDestino+". Motivo: "+descError);            
                        }
                    }
                    else
                    {
                        logger.info ("Tasación de origen: "+expOrigen+ " ya insertada en OCO");  
                        insertados ++;
                    }
                }
                else
                {
                    logger.error("No se localiza expediente UCI: "+numexpc+" objeto: "+objeto);
                    noLocalizados ++;
                }
                rs.close();
                rs = null;
                numexpc = null;
                objeto = null;
                expOrigen = null;
                fcatastral = null;
                gestor = null;
                tlfgestor = null;
                eci = null;  
                tlfeci = null;
                ecr = null;
                tlfecr = null;
            }//for filas excel
            conexion.close();
            conexionOCO.close();
            conexGraficos.close();
            conexGraficosOCO.close();
        }
        catch (Exception e)
        {
              logger.error("Excepción general en el vuelco de datos. Motivo: "+e.toString());
        }
        finally
        {
            logger.info("Insertados : "+insertados);
            logger.info("Fallos : "+fallos);
            logger.info("No Localizados : "+noLocalizados);
            
            try
            {
                if (conexion != null && !conexion.isClosed()) conexion.close();                
            }
            catch (SQLException sqle)
            {     
                logger.info("Imposible cerrar conexión con la B.D Oracle. Motivo: "+sqle.toString());
            }    
            try
            {
                if (conexionOCO != null && !conexionOCO.isClosed()) conexionOCO.close();                
            }
            catch (SQLException sqle)
            {     
                logger.info("Imposible cerrar conexión con la B.D OCO Oracle. Motivo: "+sqle.toString());
            }  
            try
            {
                if (conexGraficos != null && !conexGraficos.isClosed()) conexGraficos.close();                
            }
            catch (SQLException sqle)
            {     
                logger.info("Imposible cerrar conexión con la B.D. DOCUMENTACION Oracle. Motivo: "+sqle.toString());
            } 
            try
            {
                if (conexGraficosOCO != null && !conexGraficosOCO.isClosed()) conexGraficosOCO.close();                
            }
            catch (SQLException sqle)
            {     
                logger.info("Imposible cerrar conexión con la B.D. DOCUMENTACION OCO Oracle. Motivo: "+sqle.toString());
            } 
        }

        
    }//CEVUCI()
    **/
     
    /*
    public CEVUCI()
    {//NO realizadas por VALTECNIC
        
        String numexpc = null;
        String objeto = null;
        String sConsulta = null;
        java.sql.ResultSet rs = null;
        String expOrigen = null;
        String expDestino = null;   
        int valor = 0;
        
        //prueba datos
        //String expOrigen = "129.61-15042-11";
        //String expDestino = "129.61-15042-11-CEV";  //duda. que numero asignamos.
        //prueba documentacion
        //String expOrigen = "129.15-10321-12";
        //String expDestino = "1235";  //duda. que numero asignamos.
        try
        {
            HojaActual = "novt";
            //1.- cargamos el fichero de propiedades
            //FICHERO LOG4J
             PropertyConfigurator.configure(rutaCEVUCI + "Log4j.properties");   
            //1.- POR CADA TASACIÓN LOCALIZADA GENERAMOS SU EXPEDIENTE C.E       

             //CONEXION OCO
             conexionOCO = Utilidades.Conexion.getConnection("jdbc:oracle:thin:oco/oco0329@oraserver01:1521:rvtn1");
             conexionOCO.setAutoCommit(false);    
             conexGraficosOCO = Utilidades.Conexion.getConnection("jdbc:oracle:thin:ocoinfx/ocoinfx0329@oraserver01:1521:rvtn1");                        
             conexGraficosOCO.setAutoCommit(false); 
             
            //CONEXION VT 
            //conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:desarrollo/des0329@oraserver00:1521:rvtn"); 
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@oraserver01:1521:rvtn1");            
            conexion.setAutoCommit(false);    

            //conexGraficos = Utilidades.Conexion.getConnection("jdbc:oracle:thin:pruebainfx/pruebainfx@oraserver00:1521:rvtn");            
            conexGraficos = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecinfx/valtecinfx@oraserver01:1521:rvtn1");            
            conexGraficos.setAutoCommit(false); 
                        
            
            //RECUPERAMOS DATOS DEL EXCEL
            oExcel = new Utilidades.Excel(sUrlExcel); 
            
            for (fila = 0; fila <= 100; fila ++)
            {
                numexpc = dameValorExcel(fila,1);
                valor = 0;
                valor = Integer.parseInt(numexpc.substring(2,numexpc.length()));
                numexpc = numexpc.substring(0, 2)+"-"+Integer.toString(valor);
                //if (numexpc.substring(2, 3).equals("0")) numexpc = numexpc.substring(0, 2)+"-"+numexpc.substring(3,numexpc.length());
                //else numexpc = numexpc.substring(0, 2)+"-"+numexpc.substring(2,numexpc.length());
                objeto = dameValorExcel(fila,2);
                fcatastral = dameValorExcel(fila,24);  //ref_catastral
                gestor =  dameValorExcel(fila,22);  //ref_catastral
                tlfgestor =  dameValorExcel(fila,23);  //ref_catastral
                eci =  dameValorExcel(fila,17);  //ref_catastral 
                tlfeci =  dameValorExcel(fila,18);  //ref_catastral
                ecr =  dameValorExcel(fila,20);  //ref_catastral
                tlfecr =  dameValorExcel(fila,21);  //ref_catastral
                tipocontacto = dameValorExcel(fila,27);  //ref_catastral
                
                                   
                    
                    
                        
                        logger.info("Insertando datos numexpc: "+numexpc+" objeto: "+objeto);
                        if (vuelcaDatosTasacion_novt (numexpc,objeto))
                        {
                            
                                conexionOCO.commit();      //DATOS
                            
                                insertados ++;
                                logger.info ("Tasación de origen: "+numexpc+ " y objeto: "+objeto+" dado de alta sobre la tasación destino: "+oSolicitudes.numexp);                                
                        }
                        else
                        {
                                conexionOCO.rollback();    //DATOS
                                fallos ++;
                                logger.error ("No se puede dar de alta Tasación de origen: "+numexpc+ " y objeto: "+objeto+" sobre la tasación destino: "+oSolicitudes.numexp);
                                logger.error(".Motivo: "+descError);
                                     
                        }                                                                            
                              
                numexpc = null;
                objeto = null;
                expOrigen = null;
                fcatastral = null;
                gestor = null;
                tlfgestor = null;
                eci = null;  
                tlfeci = null;
                ecr = null;
                tlfecr = null;
            }//for filas excel
            conexion.close();
            conexionOCO.close();
            conexGraficos.close();
            conexGraficosOCO.close();
        }
        catch (Exception e)
        {
              logger.error("Excepción general en el vuelco de datos. Motivo: "+e.toString());
        }
        finally
        {
            logger.info("Insertados : "+insertados);
            logger.info("Fallos : "+fallos);
            logger.info("No Localizados : "+noLocalizados);
            
            try
            {
                if (conexion != null && !conexion.isClosed()) conexion.close();                
            }
            catch (SQLException sqle)
            {     
                logger.info("Imposible cerrar conexión con la B.D Oracle. Motivo: "+sqle.toString());
            }    
            try
            {
                if (conexionOCO != null && !conexionOCO.isClosed()) conexionOCO.close();                
            }
            catch (SQLException sqle)
            {     
                logger.info("Imposible cerrar conexión con la B.D OCO Oracle. Motivo: "+sqle.toString());
            }  
            try
            {
                if (conexGraficos != null && !conexGraficos.isClosed()) conexGraficos.close();                
            }
            catch (SQLException sqle)
            {     
                logger.info("Imposible cerrar conexión con la B.D. DOCUMENTACION Oracle. Motivo: "+sqle.toString());
            } 
            try
            {
                if (conexGraficosOCO != null && !conexGraficosOCO.isClosed()) conexGraficosOCO.close();                
            }
            catch (SQLException sqle)
            {     
                logger.info("Imposible cerrar conexión con la B.D. DOCUMENTACION OCO Oracle. Motivo: "+sqle.toString());
            } 
        }

        
    }//CEVUCI()
   */
    
    public CEVUCI()
    {//ALTA certificaciones en OCO para ALISEDA sin que existan en VT. Documento cee_popu.xls
        
        String numexp = null;
        
        
        
        String sConsulta = null;
        java.sql.ResultSet rs = null;
        String expOrigen = null;
        String expDestino = null;   
        int valor = 0;
        
        
        try
        {
            HojaActual = "Hoja1";
            //1.- cargamos el fichero de propiedades
            //FICHERO LOG4J
             PropertyConfigurator.configure(rutaCEVUCI + "Log4j.properties");   
            //1.- POR CADA TASACIÓN LOCALIZADA GENERAMOS SU EXPEDIENTE C.E       

             //CONEXION OCO
             conexionOCO = Utilidades.Conexion.getConnection("jdbc:oracle:thin:oco/oco0329@oraserver01:1521:rvtn1");
             conexionOCO.setAutoCommit(false);    
             conexGraficosOCO = Utilidades.Conexion.getConnection("jdbc:oracle:thin:ocoinfx/ocoinfx0329@oraserver01:1521:rvtn1");                        
             conexGraficosOCO.setAutoCommit(false); 
             
            //CONEXION VT 
            //conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:desarrollo/des0329@oraserver00:1521:rvtn"); 
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@oraserver01:1521:rvtn1");            
            conexion.setAutoCommit(false);    

            //conexGraficos = Utilidades.Conexion.getConnection("jdbc:oracle:thin:pruebainfx/pruebainfx@oraserver00:1521:rvtn");            
            //conexGraficos = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecinfx/valtecinfx@oraserver01:1521:rvtn1");            
            //conexGraficos.setAutoCommit(false); 
                        
            
            //RECUPERAMOS DATOS DEL EXCEL
            sUrlExcel = "/data/informes/CEVUCI/cee_popu.xls";
            oExcel = new Utilidades.Excel(sUrlExcel); 
            
            for (fila = 1; fila <= 189; fila ++)
            {                                                                                         
                        logger.info("Insertando datos fila nº: "+fila);
                        if (vuelcaDatosTasacion_novt ())
                        {
                            
                                conexionOCO.commit();      //DATOS
                            
                                insertados ++;
                                logger.info ("Referencia dada de alta sobre la tasación destino: "+oSolicitudes.numexp);                                
                        }
                        else
                        {
                                conexionOCO.rollback();    //DATOS
                                fallos ++;
                                logger.error ("No se puede dar de alta la fila nº: "+fila);
                                logger.error(".Motivo: "+descError);
                                     
                        }                                                                            
                              
                
            }//for filas excel
            conexion.close();
            conexionOCO.close();
            conexGraficos.close();
            conexGraficosOCO.close();
        }
        catch (Exception e)
        {
              logger.error("Excepción general en el vuelco de datos. Motivo: "+e.toString());
        }
        finally
        {
            logger.info("Insertados : "+insertados);
            logger.info("Fallos : "+fallos);
            logger.info("No Localizados : "+noLocalizados);
            
            try
            {
                if (conexion != null && !conexion.isClosed()) conexion.close();                
            }
            catch (SQLException sqle)
            {     
                logger.info("Imposible cerrar conexión con la B.D Oracle. Motivo: "+sqle.toString());
            }    
            try
            {
                if (conexionOCO != null && !conexionOCO.isClosed()) conexionOCO.close();                
            }
            catch (SQLException sqle)
            {     
                logger.info("Imposible cerrar conexión con la B.D OCO Oracle. Motivo: "+sqle.toString());
            }  
            try
            {
                if (conexGraficos != null && !conexGraficos.isClosed()) conexGraficos.close();                
            }
            catch (SQLException sqle)
            {     
                logger.info("Imposible cerrar conexión con la B.D. DOCUMENTACION Oracle. Motivo: "+sqle.toString());
            } 
            try
            {
                if (conexGraficosOCO != null && !conexGraficosOCO.isClosed()) conexGraficosOCO.close();                
            }
            catch (SQLException sqle)
            {     
                logger.info("Imposible cerrar conexión con la B.D. DOCUMENTACION OCO Oracle. Motivo: "+sqle.toString());
            } 
        }

        
    }//CEVUCI()
    
    /*
    public CEVUCI()
    {//realizadas por VALTECNIC pero que no se localizan en el sistema
     //buscamos por objeto sin tener en cuenta el nº de expediente y si lo encuentra lo da de alta.
     //si aún asi no lo encuentra lo damos de alta como una certificacion nueva.
        
        String numexpc = null;
        String objeto = null;
        String sConsulta = null;
        java.sql.ResultSet rs = null;
        String expOrigen = null;
        String expDestino = null;   
        int valor = 0;
      
        
        //prueba datos
        //String expOrigen = "129.61-15042-11";
        //String expDestino = "129.61-15042-11-CEV";  //duda. que numero asignamos.
        //prueba documentacion
        //String expOrigen = "129.15-10321-12";
        //String expDestino = "1235";  //duda. que numero asignamos.
        try
        {
             HojaActual = "Certificación energetica UCI"; //PRIMERA HOJA DADA DE ALTA.
            //HojaActual = "80CEMAS";   2º hoja enviada por Karolina.
            //1.- cargamos el fichero de propiedades
            //FICHERO LOG4J
             PropertyConfigurator.configure(rutaCEVUCI + "Log4j.properties");   
            //1.- POR CADA TASACIÓN LOCALIZADA GENERAMOS SU EXPEDIENTE C.E       

             //CONEXION OCO
             conexionOCO = Utilidades.Conexion.getConnection("jdbc:oracle:thin:oco/oco0329@oraserver01:1521:rvtn1");
             conexionOCO.setAutoCommit(false);    
             conexGraficosOCO = Utilidades.Conexion.getConnection("jdbc:oracle:thin:ocoinfx/ocoinfx0329@oraserver01:1521:rvtn1");                        
             conexGraficosOCO.setAutoCommit(false); 
             
            //CONEXION VT 
            //conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:desarrollo/des0329@oraserver00:1521:rvtn"); 
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@oraserver01:1521:rvtn1");            
            conexion.setAutoCommit(false);    

            //conexGraficos = Utilidades.Conexion.getConnection("jdbc:oracle:thin:pruebainfx/pruebainfx@oraserver00:1521:rvtn");            
            conexGraficos = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecinfx/valtecinfx@oraserver01:1521:rvtn1");            
            conexGraficos.setAutoCommit(false); 
                        
            
            //RECUPERAMOS DATOS DEL EXCEL
            oExcel = new Utilidades.Excel(sUrlExcel); 
            
            for (fila = 0; fila <= 2954; fila ++)
            {
                numexpc = dameValorExcel(fila,1);
                valor = 0;
                valor = Integer.parseInt(numexpc.substring(2,numexpc.length()));
                numexpc = numexpc.substring(0, 2)+"-"+Integer.toString(valor);
                //if (numexpc.substring(2, 3).equals("0")) numexpc = numexpc.substring(0, 2)+"-"+numexpc.substring(3,numexpc.length());
                //else numexpc = numexpc.substring(0, 2)+"-"+numexpc.substring(2,numexpc.length());
                objeto = dameValorExcel(fila,2);
                fcatastral = dameValorExcel(fila,24);  //ref_catastral
                gestor =  dameValorExcel(fila,22);  //ref_catastral
                tlfgestor =  dameValorExcel(fila,23);  //ref_catastral
                eci =  dameValorExcel(fila,17);  //ref_catastral 
                tlfeci =  dameValorExcel(fila,18);  //ref_catastral
                ecr =  dameValorExcel(fila,20);  //ref_catastral
                tlfecr =  dameValorExcel(fila,21);  //ref_catastral
                tipocontacto = dameValorExcel(fila,27);  //ref_catastral
                
                sConsulta = "select s.numexp,d.finalidad finalidad,s.fchrem fecha from solicitudes s join refer r on (s.numexp = r.numexp) join documenta d on s.numexp = d.numexp where r.referencia = '"+numexpc+"' and r.objeto = '"+objeto+"' and s.codest = 10 union ";
                sConsulta += " select s.numexp,d.finalidad finalidad,s.fchrem fecha from his_solicitudes s join refer r on (s.numexp = r.numexp) join his_documenta d on s.numexp = d.numexp where r.referencia = '"+numexpc+"' and r.objeto = '"+objeto+"' and s.codest = 10 ";
                sConsulta += " order by finalidad, fecha desc";

                rs = Utilidades.Conexion.select(sConsulta, conexion);
                if (rs.next())
                {                    
                    expOrigen = rs.getString("numexp");
                    oSolicitudesoco.clear();
                    if (oSolicitudesoco.load(expOrigen, conexionOCO) == 0)
                    {
                    
                        if (Funciones.Fcomunes.enHistorico(conexion, expOrigen)) esHistorico = true;
                        else esHistorico = false;
                        expDestino = expOrigen;
                        logger.info("Volcando datos tasacion: "+expOrigen+" numexpc: "+numexpc+" objeto: "+objeto);
                        if (vuelcaDatosTasacion (expOrigen,expDestino))
                        {
                            if (vuelcaDocumentacion (expOrigen,expDestino))
                            {                    
                                conexionOCO.commit();      //DATOS
                                conexGraficosOCO.commit(); //GRAFICOS
                                insertados ++;
                                logger.info ("Tasación de origen: "+expOrigen+ " volcada sobre la tasación destino: "+expDestino);
                            }
                            else
                            {
                                conexionOCO.rollback();    //DATOS
                                conexGraficosOCO.rollback();//GRAFICOS
                                fallos ++;
                                logger.error("Imposible volcar datos graficos de la tasación: "+expOrigen+ " sobre la tasación: "+expDestino+". Motivo: "+descError);            
                            }                                
                        }
                        else
                        {
                            conexionOCO.rollback();
                            fallos ++;
                            logger.error("Imposible volcar datos de la tasación: "+expOrigen+ " sobre la tasación: "+expDestino+". Motivo: "+descError);            
                        }
                    }
                    else
                    {
                        logger.info ("Tasación de origen: "+expOrigen+ " ya insertada en OCO");  
                        insertados ++;
                    }
                }
                else
                {
                    rs.close();
                    //buscamos solo por objeto
                    logger.info("BUSCAMOS SOLO POR OBJETO: "+objeto);
                    sConsulta = "select s.numexp,d.finalidad finalidad,s.fchrem fecha from solicitudes s join refer r on (s.numexp = r.numexp) join documenta d on s.numexp = d.numexp where r.objeto = '"+objeto+"' and s.codest = 10 union ";
                    sConsulta += " select s.numexp,d.finalidad finalidad,s.fchrem fecha from his_solicitudes s join refer r on (s.numexp = r.numexp) join his_documenta d on s.numexp = d.numexp where r.objeto = '"+objeto+"' and s.codest = 10 ";
                    sConsulta += " order by finalidad, fecha desc";
                    
                    rs = Utilidades.Conexion.select(sConsulta, conexion);                    
                
                    if (rs.next())
                    {                    
                        expOrigen = rs.getString("numexp");
                        oSolicitudesoco.clear();
                        if (oSolicitudesoco.load(expOrigen, conexionOCO) == 0)
                        {

                            if (Funciones.Fcomunes.enHistorico(conexion, expOrigen)) esHistorico = true;
                            else esHistorico = false;
                            expDestino = expOrigen;
                            logger.info("Volcando datos tasacion: "+expOrigen+" numexpc: "+numexpc+" objeto: "+objeto);
                            if (vuelcaDatosTasacion (expOrigen,expDestino))
                            {
                                if (vuelcaDocumentacion (expOrigen,expDestino))
                                {                    
                                    conexionOCO.commit();      //DATOS
                                    conexGraficosOCO.commit(); //GRAFICOS
                                    insertados ++;
                                    logger.info ("Tasación de origen: "+expOrigen+ " volcada sobre la tasación destino: "+expDestino);
                                }
                                else
                                {
                                    conexionOCO.rollback();    //DATOS
                                    conexGraficosOCO.rollback();//GRAFICOS
                                    fallos ++;
                                    logger.error("Imposible volcar datos graficos de la tasación: "+expOrigen+ " sobre la tasación: "+expDestino+". Motivo: "+descError);            
                                }                                
                            }
                            else
                            {
                                conexionOCO.rollback();
                                fallos ++;
                                logger.error("Imposible volcar datos de la tasación: "+expOrigen+ " sobre la tasación: "+expDestino+". Motivo: "+descError);            
                            }
                        }
                        else
                        {
                            logger.info ("Ojeto origen: "+objeto+ " ya insertada en OCO con otro numero de expediente: "+expOrigen);  
                            duplicados ++;
                        }
                    }//if lo hemos localizado solo por objeto
                    else
                    {//lo damos de alta como si no la hubiera realizado valtecnic.
                        logger.info("Expte. VT no localizado insertado como NUEVO numexpc: "+numexpc+" objeto: "+objeto);
                        if (vuelcaDatosTasacion_novt (numexpc,objeto))
                        {
                            
                                conexionOCO.commit();      //DATOS
                            
                                insertados ++;
                                logger.info ("Tasación de origen: "+numexpc+ " y objeto: "+objeto+" dado de alta sobre la tasación destino: "+oSolicitudes.numexp);                                
                        }
                        else
                        {
                                conexionOCO.rollback();    //DATOS
                                fallos ++;
                                logger.error ("No se puede dar de alta Tasación de origen: "+numexpc+ " y objeto: "+objeto+" sobre la tasación destino: "+oSolicitudes.numexp);
                                logger.error(".Motivo: "+descError);
                                     
                        }     
                    }
                }//else no localizamod por expete y objeto y buscamos solo por objeto
                rs.close();
                rs = null;
                numexpc = null;
                objeto = null;
                expOrigen = null;
                fcatastral = null;
                gestor = null;
                tlfgestor = null;
                eci = null;  
                tlfeci = null;
                ecr = null;
                tlfecr = null;
            }//for filas excel
            conexion.close();
            conexionOCO.close();
            conexGraficos.close();
            conexGraficosOCO.close();
        }
        catch (Exception e)
        {
              logger.error("Excepción general en el vuelco de datos. Motivo: "+e.toString());
        }
        finally
        {
            logger.info("Insertados : "+insertados);
            logger.info("Duplicados : "+duplicados);
            logger.info("Fallos : "+fallos);
            logger.info("No Localizados : "+noLocalizados);
            
            try
            {
                if (conexion != null && !conexion.isClosed()) conexion.close();                
            }
            catch (SQLException sqle)
            {     
                logger.info("Imposible cerrar conexión con la B.D Oracle. Motivo: "+sqle.toString());
            }    
            try
            {
                if (conexionOCO != null && !conexionOCO.isClosed()) conexionOCO.close();                
            }
            catch (SQLException sqle)
            {     
                logger.info("Imposible cerrar conexión con la B.D OCO Oracle. Motivo: "+sqle.toString());
            }  
            try
            {
                if (conexGraficos != null && !conexGraficos.isClosed()) conexGraficos.close();                
            }
            catch (SQLException sqle)
            {     
                logger.info("Imposible cerrar conexión con la B.D. DOCUMENTACION Oracle. Motivo: "+sqle.toString());
            } 
            try
            {
                if (conexGraficosOCO != null && !conexGraficosOCO.isClosed()) conexGraficosOCO.close();                
            }
            catch (SQLException sqle)
            {     
                logger.info("Imposible cerrar conexión con la B.D. DOCUMENTACION OCO Oracle. Motivo: "+sqle.toString());
            } 
        }

        
    }//CEVUCI()
    **/
    
    /*
    public CEVUCI()
    {//realizadas por VALTECNIC para UCI
        
        String numexpc = null;
        String objeto = null;
        String sConsulta = null;
        java.sql.ResultSet rs = null;
        String expOrigen = null;
        String expDestino = null;   
        int valor = 0;
      
        
        //prueba datos
        //String expOrigen = "129.61-15042-11";
        //String expDestino = "129.61-15042-11-CEV";  //duda. que numero asignamos.
        //prueba documentacion
        //String expOrigen = "129.15-10321-12";
        //String expDestino = "1235";  //duda. que numero asignamos.
        try
        {
             //HojaActual = "Certificación energetica UCI"; PRIMERA HOJA DADA DE ALTA.
            HojaActual = "80CEMAS";
            //1.- cargamos el fichero de propiedades
            //FICHERO LOG4J
             PropertyConfigurator.configure(rutaCEVUCI + "Log4j.properties");   
            //1.- POR CADA TASACIÓN LOCALIZADA GENERAMOS SU EXPEDIENTE C.E       

             //CONEXION OCO
             conexionOCO = Utilidades.Conexion.getConnection("jdbc:oracle:thin:oco/oco0329@oraserver01:1521:rvtn1");
             conexionOCO.setAutoCommit(false);    
             conexGraficosOCO = Utilidades.Conexion.getConnection("jdbc:oracle:thin:ocoinfx/ocoinfx0329@oraserver01:1521:rvtn1");                        
             conexGraficosOCO.setAutoCommit(false); 
             
            //CONEXION VT 
            //conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:desarrollo/des0329@oraserver00:1521:rvtn"); 
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@oraserver01:1521:rvtn1");            
            conexion.setAutoCommit(false);    

            //conexGraficos = Utilidades.Conexion.getConnection("jdbc:oracle:thin:pruebainfx/pruebainfx@oraserver00:1521:rvtn");            
            conexGraficos = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecinfx/valtecinfx@oraserver01:1521:rvtn1");            
            conexGraficos.setAutoCommit(false); 
                        
            
            //RECUPERAMOS DATOS DEL EXCEL
            oExcel = new Utilidades.Excel(sUrlExcel); 
            
            for (fila = 0; fila <= 79; fila ++)
            {
                numexpc = dameValorExcel(fila,1);
                valor = 0;
                valor = Integer.parseInt(numexpc.substring(2,numexpc.length()));
                numexpc = numexpc.substring(0, 2)+"-"+Integer.toString(valor);
                //if (numexpc.substring(2, 3).equals("0")) numexpc = numexpc.substring(0, 2)+"-"+numexpc.substring(3,numexpc.length());
                //else numexpc = numexpc.substring(0, 2)+"-"+numexpc.substring(2,numexpc.length());
                objeto = dameValorExcel(fila,2);
                fcatastral = dameValorExcel(fila,24);  //ref_catastral
                gestor =  dameValorExcel(fila,22);  //ref_catastral
                tlfgestor =  dameValorExcel(fila,23);  //ref_catastral
                eci =  dameValorExcel(fila,17);  //ref_catastral 
                tlfeci =  dameValorExcel(fila,18);  //ref_catastral
                ecr =  dameValorExcel(fila,20);  //ref_catastral
                tlfecr =  dameValorExcel(fila,21);  //ref_catastral
                tipocontacto = dameValorExcel(fila,27);  //ref_catastral
                
                sConsulta = "select s.numexp,d.finalidad finalidad,s.fchrem fecha from solicitudes s join refer r on (s.numexp = r.numexp) join documenta d on s.numexp = d.numexp where r.referencia = '"+numexpc+"' and r.objeto = '"+objeto+"' and s.codest = 10 union ";
                sConsulta += " select s.numexp,d.finalidad finalidad,s.fchrem fecha from his_solicitudes s join refer r on (s.numexp = r.numexp) join his_documenta d on s.numexp = d.numexp where r.referencia = '"+numexpc+"' and r.objeto = '"+objeto+"' and s.codest = 10 ";
                sConsulta += " order by finalidad, fecha desc";

                rs = Utilidades.Conexion.select(sConsulta, conexion);
                if (rs.next())
                {                    
                    expOrigen = rs.getString("numexp");
                    oSolicitudesoco.clear();
                    if (oSolicitudesoco.load(expOrigen, conexionOCO) == 0)
                    {
                    
                        if (Funciones.Fcomunes.enHistorico(conexion, expOrigen)) esHistorico = true;
                        else esHistorico = false;
                        expDestino = expOrigen;
                        logger.info("Volcando datos tasacion: "+expOrigen+" numexpc: "+numexpc+" objeto: "+objeto);
                        if (vuelcaDatosTasacion (expOrigen,expDestino))
                        {
                            if (vuelcaDocumentacion (expOrigen,expDestino))
                            {                    
                                conexionOCO.commit();      //DATOS
                                conexGraficosOCO.commit(); //GRAFICOS
                                insertados ++;
                                logger.info ("Tasación de origen: "+expOrigen+ " volcada sobre la tasación destino: "+expDestino);
                            }
                            else
                            {
                                conexionOCO.rollback();    //DATOS
                                conexGraficosOCO.rollback();//GRAFICOS
                                fallos ++;
                                logger.error("Imposible volcar datos graficos de la tasación: "+expOrigen+ " sobre la tasación: "+expDestino+". Motivo: "+descError);            
                            }                                
                        }
                        else
                        {
                            conexionOCO.rollback();
                            fallos ++;
                            logger.error("Imposible volcar datos de la tasación: "+expOrigen+ " sobre la tasación: "+expDestino+". Motivo: "+descError);            
                        }
                    }
                    else
                    {
                        logger.info ("Tasación de origen: "+expOrigen+ " ya insertada en OCO");  
                        insertados ++;
                    }
                }
                else
                {
                    logger.error("No se localiza expediente UCI: "+numexpc+" objeto: "+objeto);
                    noLocalizados ++;
                }
                rs.close();
                rs = null;
                numexpc = null;
                objeto = null;
                expOrigen = null;
                fcatastral = null;
                gestor = null;
                tlfgestor = null;
                eci = null;  
                tlfeci = null;
                ecr = null;
                tlfecr = null;
            }//for filas excel
            conexion.close();
            conexionOCO.close();
            conexGraficos.close();
            conexGraficosOCO.close();
        }
        catch (Exception e)
        {
              logger.error("Excepción general en el vuelco de datos. Motivo: "+e.toString());
        }
        finally
        {
            logger.info("Insertados : "+insertados);
            logger.info("Fallos : "+fallos);
            logger.info("No Localizados : "+noLocalizados);
            
            try
            {
                if (conexion != null && !conexion.isClosed()) conexion.close();                
            }
            catch (SQLException sqle)
            {     
                logger.info("Imposible cerrar conexión con la B.D Oracle. Motivo: "+sqle.toString());
            }    
            try
            {
                if (conexionOCO != null && !conexionOCO.isClosed()) conexionOCO.close();                
            }
            catch (SQLException sqle)
            {     
                logger.info("Imposible cerrar conexión con la B.D OCO Oracle. Motivo: "+sqle.toString());
            }  
            try
            {
                if (conexGraficos != null && !conexGraficos.isClosed()) conexGraficos.close();                
            }
            catch (SQLException sqle)
            {     
                logger.info("Imposible cerrar conexión con la B.D. DOCUMENTACION Oracle. Motivo: "+sqle.toString());
            } 
            try
            {
                if (conexGraficosOCO != null && !conexGraficosOCO.isClosed()) conexGraficosOCO.close();                
            }
            catch (SQLException sqle)
            {     
                logger.info("Imposible cerrar conexión con la B.D. DOCUMENTACION OCO Oracle. Motivo: "+sqle.toString());
            } 
        }

        
    }//CEVUCI()
   
   */
    private void inicializaEstado()
    {
        estadoOK = true;
        error = 0;
        descError = "";       
    }//inicializaEstado
    
    
    
    private boolean vuelcaDatosTasacion (String origen, String destino)
    {
        String sConsulta = null;
        inicializaEstado();        
        java.sql.ResultSet rs = null;
        Double suputil = 0.0;
        String svalor = null;
        int ivalor = 0;
        int cuantos = 0;
        
        try
        {
            
            //1.- insertamos SOLICITUDES
            
            oSolicitudes.clear();
            
            
            if (esHistorico) sConsulta = "SELECT suputil FROM his_elemento5 WHERE numexp ='"+origen+"'";
            else sConsulta = "SELECT suputil FROM elemento5 WHERE numexp ='"+origen+"'";
            rs = Utilidades.Conexion.select(sConsulta, conexion);
            if (rs.next()) suputil = rs.getDouble("suputil");
            rs.close();
            
            
           if (oSolicitudes.load(origen, conexion) == 0)
           {
               estadoOK = false;
               descError = "No se encuentra en solicitudes tasación de origen: "+origen;
           }            
            
            
            if (estadoOK)
            {//
                tipoinmOrigen = oSolicitudes.tipoinm;
                oSolicitudes.tipoinm = "CEV";
                oSolicitudes.codcli = 329;
                oSolicitudes.oficina = "S.C.";
                oSolicitudes.loadDatosOficina(conexion);   
                oSolicitudes.solici = "U.C.I. SERVICIOS CENTRALES";                
                oSolicitudes.nifsolici = "A-39025515";                
                oSolicitudes.tipoif = 8;
                oSolicitudes.codage = null;                
                oSolicitudes.codest = 0;
                oSolicitudes.indicador = null;
                oSolicitudes.estimado = null;                
                oSolicitudes.fchenc = new java.util.Date();
                oSolicitudes.fchvis = null;
                oSolicitudes.fchrec = null;
                oSolicitudes.fchrem = null;
                oSolicitudes.tasacion = null;
                oSolicitudes.seguro = null;         
                //contacto las naranjas coger los ECR el resto lo que haya si hay 2 tomo el ECI
                if (tipocontacto != null && tipocontacto.equals("1"))
                {//el contacto es el ECR
                    oSolicitudes.contacto = ecr.trim();
                    oSolicitudes.telefonos = tlfecr.trim();
                }
                else
                {
                    if (eci != null && !eci.trim().equals(""))
                    {
                        oSolicitudes.contacto = eci.trim();                    
                    }
                    else
                    {
                        if (ecr != null && !ecr.trim().equals(""))
                        {
                            oSolicitudes.contacto = ecr.trim();                    
                        }
                        else oSolicitudes.contacto = "";
                    }
                    
            
                    if (tlfeci != null && !tlfeci.trim().equals(""))
                    {
                        oSolicitudes.telefonos = tlfeci.trim();                    
                    }
                    else
                    {
                        if (tlfecr != null && !tlfecr.trim().equals(""))
                        {
                            oSolicitudes.telefonos = tlfecr.trim();                    
                        }
                    }
                }
                
                if (oSolicitudes.insert(false,conexionOCO) != 1)  //el false es para no inserte en historico.
                {
                   estadoOK = false;
                   descError = "No se inserta en solicitudes expediente: "+origen+" objeto: "+destino+ " sobre la tasacion: "+oSolicitudes.numexp;
                }  
                else logger.info("Expediente asignado: "+oSolicitudes.numexp);
            }
            if (estadoOK)
            {//datosreg                
                            
                    oDatosreg.clear();
                    if (esHistorico) sConsulta = "SELECT * FROM his_datosreg WHERE numexp ='"+origen+"'";
                    else sConsulta = "SELECT * FROM datosreg WHERE numexp ='"+origen+"'";
                    rs = Utilidades.Conexion.select(sConsulta, conexion);
                    while (estadoOK && rs.next())
                    {
                         oDatosreg.clear();
                         if (oDatosreg.load(origen, rs.getInt("numero"), conexion))
                         {
                             oDatosreg.numexp = oSolicitudes.numexp;
                             oDatosreg.tipoinm = oSolicitudes.tipoinm;       
                             if (oDatosreg.insert(conexionOCO) != 1)
                             {
                                 estadoOK = false;
                                 descError = "No se inserta en datosreg tasacion: "+destino+ " numero: "+rs.getInt("numero");
                             }
                         }
                         else
                         {
                             estadoOK = false;
                             descError = "No se localiza en datosreg tasacion: "+destino+ " numero: "+rs.getInt("numero");
                         }                                                                                         
                    }
                    rs.close();
                                
            }//datosreg
            
            if (estadoOK)
            {//catastro                
                 sConsulta = "SELECT * FROM catastro WHERE numexp ='"+origen+"'";
                 rs = Utilidades.Conexion.select(sConsulta, conexion);
                    while (estadoOK && rs.next())
                    {         
                         cuantos ++;
                         if (oCatastro.load(origen, rs.getInt("numero"), conexion))
                         {
                             oCatastro.numexp = oSolicitudes.numexp;
                             oCatastro.tipoinm = oSolicitudes.tipoinm;    
                             if (cuantos == 1)
                             {
                                 if ((oCatastro.fcatastral == null || oCatastro.fcatastral.equals("")) && fcatastral != null) oCatastro.fcatastral = fcatastral; 
                             }
                             if (oCatastro.insert(conexionOCO) != 1)
                             {
                                 estadoOK = false;
                                 descError = "No se inserta en oCatastro tasacion: "+destino+ " numero: "+rs.getInt("numero");
                             }
                         }
                         else
                         {
                             estadoOK = false;
                             descError = "No se localiza en oCatastro tasacion: "+destino+ " numero: "+rs.getInt("numero");
                         }                                                                                         
                    }
                    rs.close();                                                             
             }
                            
            
            if (estadoOK)
            {
                oOtrastas.numexp = oSolicitudes.numexp;
                oOtrastas.tipoinm = oSolicitudes.tipoinm;
                oOtrastas.numtipo1 = 1;
                //oOtrastas.tipologia1 = tipoinmOrigen; asi se asigno el tipo de inmuble en la primera carga
                oOtrastas.tipologia1 = "CE1";
                oOtrastas.porcen1 = 100.0;
                oOtrastas.superf1 = suputil;
                oOtrastas.tipologia2 = "CE2";
                oOtrastas.tipologia3 = "CE3";
                oOtrastas.tipologia4 = "CE4";
                oOtrastas.tipologia5 = "CE5";
                oOtrastas.tipologia6 = "CE6";
                oOtrastas.tottipos = 6;                                                
                if (oOtrastas.insertOtrastas(conexionOCO) != 1)
                {
                        estadoOK = false;
                        descError = "No se inserta en otrastas expediente: "+destino;                             
                }  
                
            }
            
            
            if (estadoOK)
            {//refer
                oRefer.clear();
                
                    if (oRefer.load(origen, conexion))
                    {
                        oRefer.numexp = oSolicitudes.numexp;                                               
                        if (tlfgestor != null) oRefer.nombstor = tlfgestor.trim();
                        if (gestor != null) oRefer.nombstor += gestor.trim();
                        if (oRefer.nombstor != null && oRefer.nombstor.length() > 35) oRefer.nombstor = oRefer.nombstor.substring(0, 35);
                        if (oRefer.insert(conexionOCO) != 1)
                        {
                            estadoOK = false;
                            descError = "No se inserta en refer expediente: "+origen+" objeto: "+destino+ " sobre la tasacion: "+oSolicitudes.numexp;                          
                        }
                    }
                    else
                    {
                        estadoOK = false;
                        descError = "No se localiza en refer expediente: "+origen;
                    }
               
            }
            
            if (estadoOK && gestor != null)
            {//avisos
                oAvisos.numexp = oSolicitudes.numexp;
                oAvisos.aviso1 = "Comercializador : "+gestor.trim()+" ";
                if (tlfgestor != null) oAvisos.aviso1 += tlfgestor.trim();                                
                    if (oAvisos.insert(conexionOCO) != 1)
                    {
                        estadoOK = false;
                        descError = "No se inserta en refer expediente: "+destino;                             
                    }                                    
            }
            
            
            if (estadoOK)
            {//entrada      
                oEntrada.numexp = oSolicitudes.numexp;
                oEntrada.coddel = oSolicitudes.delegado;
                if (oEntrada.insert(conexionOCO) != 1)
                {
                        estadoOK = false;
                        descError = "No se inserta en entrada expediente: "+destino;                             
                }                    
                /*
                if (oEntrada.load(origen, conexion))
                {                    
                    oEntrada.numexp = destino;                       
                    if (oEntrada.insert(conexion) != 1)
                    {
                        estadoOK = false;
                        descError = "No se inserta en entrada expediente: "+destino;                             
                    }                    
                }
                else
                {
                    estadoOK = false;
                    descError = "No se encuentra en entrada el expediente de origen: "+origen;
                }
                * */
            }
            
            if (estadoOK)
            {//documenta      
                oDocumenta.clear();
                if (oDocumenta.load(origen, conexion) == 1)
                {
                    oDocumenta.numexp = destino;  
                    oDocumenta.tipoinm = oSolicitudes.tipoinm;
                    if (oDocumenta.insertDocumenta(conexionOCO) != 1)
                    {
                        estadoOK = false;
                        descError = "No se inserta en documenta expediente: "+destino;                             
                    }                    
                }
                else
                {
                    estadoOK = false;
                    descError = "No se encuentra en documenta el expediente de origen: "+origen;
                }
            }
            
            if (estadoOK)
            {//PRODUSU      

                oProdusu.numexp = oSolicitudes.numexp;;
                oProdusu.coddel = oSolicitudes.delegado;
                oProdusu.codusu = "juan";
                oProdusu.fecha  = new java.util.Date();
                oProdusu.accion = "A";
                oProdusu.tiempo = 1;
                oProdusu.pantalla = "RE";
                if (oProdusu.insertProdusu(conexionOCO) != 1)
                {
                    estadoOK = false;
                    descError = "No se inserta en produsu expediente: "+origen+" objeto: "+destino+ " sobre la tasacion: "+oSolicitudes.numexp; 
                }
            }
            
        }//try
        catch (Exception e)
        {
            estadoOK = false;
            descError = "Imposible realizar vuelco de datos del expediente: "+origen+ " y objeto: "+destino+ ". Motivo: "+e.toString();
        }
        finally
        {
            return estadoOK;
        }
    }//vuelcaDatosTasacion
    
    
    private boolean vuelcaDatosTasacionEnVT (String origen, String destino)
    {
        String sConsulta = null;
        inicializaEstado();        
        java.sql.ResultSet rs = null;
        Double suputil = 0.0;
        String svalor = null;
        int ivalor = 0;
        int cuantos = 0;
        
        try
        {
            
            //1.- insertamos SOLICITUDES
            
            oSolicitudes.clear();
            
            
            if (esHistorico) sConsulta = "SELECT suputil FROM his_elemento5 WHERE numexp ='"+origen+"'";
            else sConsulta = "SELECT suputil FROM elemento5 WHERE numexp ='"+origen+"'";
            rs = Utilidades.Conexion.select(sConsulta, conexion);
            if (rs.next()) suputil = rs.getDouble("suputil");
            rs.close();
            
            
           if (oSolicitudes.load(origen, conexion) == 0)
           {
               estadoOK = false;
               descError = "No se encuentra en solicitudes tasación de origen: "+origen;
           }            
            
            
            if (estadoOK)
            {//                
                oSolicitudes.numexp = destino;
                oSolicitudes.tipoinm = "CEV";                
                oSolicitudes.codage = null;                
                oSolicitudes.codest = 0;
                oSolicitudes.indicador = null;
                oSolicitudes.estimado = null;                
                oSolicitudes.fchenc = new java.util.Date();
                oSolicitudes.fchvis = null;
                oSolicitudes.fchrec = null;
                oSolicitudes.fchrem = null;
                oSolicitudes.tasacion = null;
                oSolicitudes.seguro = null;         
                //contacto las naranjas coger los ECR el resto lo que haya si hay 2 tomo el ECI
               
                    if (eci != null && !eci.trim().equals(""))
                    {
                        oSolicitudes.contacto = eci.trim();                    
                    }                    
                    
            
                    if (tlfeci != null && !tlfeci.trim().equals(""))
                    {
                        oSolicitudes.telefonos = tlfeci.trim();                    
                    }
                    
                
                
                if (oSolicitudes.insert(false,conexion) != 1)  //el false es para no inserte en historico.
                {
                   estadoOK = false;
                   descError = "No se inserta en solicitudes expediente: "+origen+" objeto: "+destino+ " sobre la tasacion: "+oSolicitudes.numexp;
                }  
                else logger.info("Expediente asignado: "+oSolicitudes.numexp);
            }
            if (estadoOK)
            {//datosreg                
                            
                    oDatosreg.clear();
                    if (esHistorico) sConsulta = "SELECT * FROM his_datosreg WHERE numexp ='"+origen+"'";
                    else sConsulta = "SELECT * FROM datosreg WHERE numexp ='"+origen+"'";
                    rs = Utilidades.Conexion.select(sConsulta, conexion);
                    while (estadoOK && rs.next())
                    {
                         oDatosreg.clear();
                         if (oDatosreg.load(origen, rs.getInt("numero"), conexion))
                         {
                             oDatosreg.numexp = destino;
                             oDatosreg.tipoinm = oSolicitudes.tipoinm;       
                             if (oDatosreg.insert(conexion) != 1)
                             {
                                 estadoOK = false;
                                 descError = "No se inserta en datosreg tasacion: "+destino+ " numero: "+rs.getInt("numero");
                             }
                         }
                         else
                         {
                             estadoOK = false;
                             descError = "No se localiza en datosreg tasacion: "+destino+ " numero: "+rs.getInt("numero");
                         }                                                                                         
                    }
                    rs.close();
                                
            }//datosreg
            
            if (estadoOK)
            {//catastro                
                 sConsulta = "SELECT * FROM catastro WHERE numexp ='"+origen+"'";
                 rs = Utilidades.Conexion.select(sConsulta, conexion);
                    while (estadoOK && rs.next())
                    {         
                         
                         if (oCatastro.load(origen, rs.getInt("numero"), conexion))
                         {
                             oCatastro.numexp = destino;
                             oCatastro.tipoinm = oSolicitudes.tipoinm;                                
                             if (oCatastro.insert(conexion) != 1)
                             {
                                 estadoOK = false;
                                 descError = "No se inserta en oCatastro tasacion: "+destino+ " numero: "+rs.getInt("numero");
                             }
                         }
                         else
                         {
                             estadoOK = false;
                             descError = "No se localiza en oCatastro tasacion: "+destino+ " numero: "+rs.getInt("numero");
                         }                                                                                         
                    }
                    rs.close();                                                             
             }
                            
            
            if (estadoOK)
            {
                oOtrastas.numexp = destino;
                oOtrastas.tipoinm = oSolicitudes.tipoinm;
                //oOtrastas.numtipo1 = 1;
                //oOtrastas.tipologia1 = tipoinmOrigen; asi se asigno el tipo de inmuble en la primera carga
                oOtrastas.tipologia1 = "CE1";
                oOtrastas.porcen1 = 100.0;
                oOtrastas.superf1 = suputil;
                oOtrastas.tipologia2 = "CE2";
                oOtrastas.tipologia3 = "CE3";
                oOtrastas.tipologia4 = "CE4";
                oOtrastas.tipologia5 = "CE5";
                oOtrastas.tipologia6 = "CE6";
                oOtrastas.tottipos = 6;                                                
                if (oOtrastas.insertOtrastas(conexion) != 1)
                {
                        estadoOK = false;
                        descError = "No se inserta en otrastas expediente: "+destino;                             
                }  
                
            }
            
            
            if (estadoOK)
            {//refer
                oRefer.clear();
                
                    if (oRefer.load(origen, conexion))
                    {
                        oRefer.numexp = destino;
                        oRefer.procede = origen;
                        if (tlfgestor != null) oRefer.nombstor = tlfgestor.trim();                        
                        if (gestor != null) oRefer.nombstor += gestor.trim();
                        if (oRefer.nombstor != null && oRefer.nombstor.length() > 35) oRefer.nombstor = oRefer.nombstor.substring(0, 35);
                        if (oRefer.insert(conexion) != 1)
                        {
                            estadoOK = false;
                            descError = "No se inserta en refer expediente: "+origen+" objeto: "+destino+ " sobre la tasacion: "+oSolicitudes.numexp;                          
                        }
                    }
                    else
                    {
                        estadoOK = false;
                        descError = "No se localiza en refer expediente: "+origen;
                    }
               
            }
            
            if (estadoOK && gestor != null)
            {//avisos
                oAvisos.numexp = destino;
                oAvisos.aviso1 = "Comercializador : "+gestor.trim()+" ";
                if (tlfgestor != null) oAvisos.aviso1 += tlfgestor.trim();                                
                    if (oAvisos.insert(conexion) != 1)
                    {
                        estadoOK = false;
                        descError = "No se inserta en refer expediente: "+destino;                             
                    }                                    
            }
            
            
            if (estadoOK)
            {//entrada      
                oEntrada.numexp = destino;
                oEntrada.coddel = oSolicitudes.delegado;
                if (oEntrada.insert(conexion) != 1)
                {
                        estadoOK = false;
                        descError = "No se inserta en entrada expediente: "+destino;                             
                }                    
                /*
                if (oEntrada.load(origen, conexion))
                {                    
                    oEntrada.numexp = destino;                       
                    if (oEntrada.insert(conexion) != 1)
                    {
                        estadoOK = false;
                        descError = "No se inserta en entrada expediente: "+destino;                             
                    }                    
                }
                else
                {
                    estadoOK = false;
                    descError = "No se encuentra en entrada el expediente de origen: "+origen;
                }
                * */
            }
            
            if (estadoOK)
            {//documenta      
                oDocumenta.clear();
                if (oDocumenta.load(origen, conexion) == 1)
                {
                    oDocumenta.numexp = destino;  
                    oDocumenta.tipoinm = oSolicitudes.tipoinm;
                    if (oDocumenta.insertDocumenta(conexion) != 1)
                    {
                        estadoOK = false;
                        descError = "No se inserta en documenta expediente: "+destino;                             
                    }                    
                }
                else
                {
                    estadoOK = false;
                    descError = "No se encuentra en documenta el expediente de origen: "+origen;
                }
            }
            
            if (estadoOK)
            {//PRODUSU      

                oProdusu.numexp = destino;
                oProdusu.coddel = oSolicitudes.delegado;
                oProdusu.codusu = "juan";
                oProdusu.fecha  = new java.util.Date();
                oProdusu.accion = "A";
                oProdusu.tiempo = 1;
                oProdusu.pantalla = "RE";
                if (oProdusu.insertProdusu(conexion) != 1)
                {
                    estadoOK = false;
                    descError = "No se inserta en produsu expediente: "+origen+" objeto: "+destino+ " sobre la tasacion: "+oSolicitudes.numexp; 
                }
            }
            
        }//try
        catch (Exception e)
        {
            estadoOK = false;
            descError = "Imposible realizar vuelco de datos del expediente: "+origen+ " y objeto: "+destino+ ". Motivo: "+e.toString();
        }
        finally
        {
            return estadoOK;
        }
    }//vuelcaDatosTasacionEnVT
    
    
    private boolean vuelcaDatosTasacion_novt(String origen, String destino)
    {
        String sConsulta = null;
        inicializaEstado();        
        java.sql.ResultSet rs = null;
        Double suputil = 0.0;
        String svalor = null;
        int ivalor = 0;
        String letra = "";
        try
        {
            
            //1.- insertamos SOLICITUDES
            
            oSolicitudes.clear();
            
            svalor = dameValorExcel(fila,12);
            if (svalor != null && !svalor.trim().equals("")) svalor = svalor.replace(",", ".");
            else svalor = "0.0";
            suputil = Double.parseDouble(svalor);
            
            //provincia
            svalor = dameValorExcel(fila,9);  //codigo postal            
            if (svalor != null && !svalor.trim().equals(""))
            {
                if (svalor.length() == 5) svalor = svalor.substring(0,2);
                else svalor = svalor.substring(0,1);
                ivalor = Integer.parseInt(svalor);
            }
            sConsulta = "SELECT * FROM provincias WHERE codpro = "+ivalor;
            rs = Utilidades.Conexion.select(sConsulta, conexion);
            if (rs.next())
            {
                oSolicitudes.numexp = rs.getString("prefijo")+"29."+origen+"-13";
                oSolicitudes.provin = rs.getString("nompro");
                oSolicitudes.delegado = Integer.parseInt(rs.getString("coddel"));
            }
            else
            {
                estadoOK = false;
                descError = "No se localiza provincia asociada al numexpc: "+origen+" objeto: "+destino;
            }
            rs.close();
            oSolicitudesoco.clear();
            if (oSolicitudesoco.load(oSolicitudes.numexp, conexionOCO) != 0)
            {             
                sConsulta = "select * from refer where numexp = '"+oSolicitudes.numexp+"'";
                rs = Utilidades.Conexion.select(sConsulta, conexionOCO);
                if (rs.next()) 
                {
                    //logger.error("numexpc: "+origen+" objeto: "+destino+ "ya existe en OCO nuevo expediente: "+oSolicitudesoco.numexp);
                    if (rs.getString("referencia").equals(origen) && !rs.getString("objeto").equals(destino))
                    {//hay que asignar nueva garantia.
                        letra = garantias_anteriores_msg(origen);
                        oSolicitudes.numexp = oSolicitudes.numexp+ " "+letra.trim();
                        estadoOK = true;
                    }
                    else
                    {
                        estadoOK = false;
                        logger.error("numexpc: "+origen+" objeto: "+destino+ "ya existe en OCO nuevo expediente: "+oSolicitudesoco.numexp);
                    }
                }                                                       
                //logger.error("numexpc: "+origen+" objeto: "+destino+ "ya existe en OCO expediente de VT: "+oSolicitudesoco.numexp);                                
                //else logger.error("numexpc: "+origen+" objeto: "+destino+ "ya existe en OCO nuevo expediente: "+oSolicitudesoco.numexp);                                
                rs.close();
            }
            
            
            if (estadoOK)
            {//
                tipoinmOrigen = "";
                oSolicitudes.tipoinm = "CEV";
                oSolicitudes.codcli = 329;
                oSolicitudes.oficina = "S.C.";
                oSolicitudes.loadDatosOficina(conexion);   
                oSolicitudes.solici = "U.C.I. SERVICIOS CENTRALES";                
                oSolicitudes.nifsolici = "A-39025515";
                oSolicitudes.codpos = Integer.parseInt(dameValorExcel(fila,9));                 
                oSolicitudes.munici = dameValorExcel(fila,8).toUpperCase();
                oSolicitudes.locali = oSolicitudes.munici;
                oSolicitudes.calle = dameValorExcel(fila,7).toUpperCase();                
                oSolicitudes.tipoif = 8;
                oSolicitudes.codage = null;                
                oSolicitudes.codest = 0;
                oSolicitudes.indicador = null;
                oSolicitudes.estimado = null;                
                oSolicitudes.fchenc = new java.util.Date();
                oSolicitudes.fchvis = null;
                oSolicitudes.fchrec = null;
                oSolicitudes.fchrem = null;
                oSolicitudes.tasacion = null;
                oSolicitudes.seguro = null;         
                oSolicitudes.superficie = suputil;
                //contacto las naranjas coger los ECR el resto lo que haya si hay 2 tomo el ECI
                if (tipocontacto != null && tipocontacto.equals("1"))
                {//el contacto es el ECR
                    oSolicitudes.contacto = ecr.trim();
                    oSolicitudes.telefonos = tlfecr.trim();
                }
                else
                {
                    if (eci != null && !eci.trim().equals(""))
                    {
                        oSolicitudes.contacto = eci.trim();                    
                    }
                    else
                    {
                        if (ecr != null && !ecr.trim().equals(""))
                        {
                            oSolicitudes.contacto = ecr.trim();                    
                        }
                        else oSolicitudes.contacto = "";
                    }
                    
            
                    if (tlfeci != null && !tlfeci.trim().equals(""))
                    {
                        oSolicitudes.telefonos = tlfeci.trim();                    
                    }
                    else
                    {
                        if (tlfecr != null && !tlfecr.trim().equals(""))
                        {
                            oSolicitudes.telefonos = tlfecr.trim();                    
                        }
                    }
                }
                
                if (oSolicitudes.insert(false,conexionOCO) != 1)  //el false es para no inserte en historico.
                {
                   estadoOK = false;
                   descError = "No se inserta en solicitudes expediente: "+origen+" objeto: "+destino+ " sobre la tasacion: "+oSolicitudes.numexp;
                }  
                else logger.info("Expediente asignado: "+oSolicitudes.numexp);
            }
            if (estadoOK)
            {//datosreg                
                            
                    oDatosreg.clear();
                    
                         oDatosreg.numexp = oSolicitudes.numexp;
                         oDatosreg.tipoinm = oSolicitudes.tipoinm;       
                         oDatosreg.numero = 1;
                         if (oDatosreg.insert(conexionOCO) != 1)
                         {
                             estadoOK = false;
                             descError = "No se inserta en datosreg expediente: "+origen+" objeto: "+destino+ " sobre la tasacion: "+oSolicitudes.numexp;
                         }                                            
                                
            }//datosreg
            
            if (estadoOK)
            {//catastro                
                
                         oCatastro.numexp = oSolicitudes.numexp;
                         oCatastro.tipoinm = oSolicitudes.tipoinm;
                         oCatastro.numero = 1;
                         if (fcatastral != null) oCatastro.fcatastral = fcatastral;                         
                         if (oCatastro.insert(conexionOCO) != 1)
                         {
                             estadoOK = false;
                            descError = "No se inserta en catastro expediente: "+origen+" objeto: "+destino+ " sobre la tasacion: "+oSolicitudes.numexp;
                         }
                                    
             }
                            
            /*
            if (estadoOK)
            {
                oOtrastas.numexp = oSolicitudes.numexp;
                oOtrastas.tipoinm = oSolicitudes.tipoinm;
                oOtrastas.numtipo1 = 1;
                oOtrastas.tipologia1 = tipoinmOrigen;
                oOtrastas.porcen1 = 100.0;
                oOtrastas.superf1 = suputil;
                oOtrastas.tottipos = 1;                                                
                if (oOtrastas.insertOtrastas(conexionOCO) != 1)
                {
                        estadoOK = false;
                        descError = "No se inserta en otrastas expediente: "+destino;                             
                }  
                
            }
            * **/
            
            if (estadoOK)
            {//refer
                oRefer.clear();
                
                    oRefer.numexp = oSolicitudes.numexp;   
                    oRefer.referencia = origen;
                    oRefer.objeto = Integer.parseInt(destino);
                    if (tlfgestor != null) oRefer.nombstor = tlfgestor.trim();
                    if (gestor != null) oRefer.nombstor += gestor.trim();
                    if (oRefer.nombstor.length() > 35) oRefer.nombstor = oRefer.nombstor.substring(0, 35);
                    if (oRefer.insert(conexionOCO) != 1)
                    {
                        estadoOK = false;
                        descError = "No se inserta en refer expediente: "+origen+" objeto: "+destino+ " sobre la tasacion: "+oSolicitudes.numexp;                          
                    }                    
               
            }
            
            if (estadoOK && gestor != null)
            {//avisos
                oAvisos.numexp = oSolicitudes.numexp;
                oAvisos.aviso1 = "Comercializador : "+gestor.trim()+" ";
                if (tlfgestor != null) oAvisos.aviso1 += tlfgestor.trim();                                
                    if (oAvisos.insert(conexionOCO) != 1)
                    {
                        estadoOK = false;
                        descError = "No se inserta en refer expediente: "+destino;                             
                    }                                    
            }
            
            
            if (estadoOK)
            {//entrada      
                oEntrada.numexp = oSolicitudes.numexp;
                oEntrada.coddel = oSolicitudes.delegado;
                if (oEntrada.insert(conexionOCO) != 1)
                {
                        estadoOK = false;
                        descError = "No se inserta en entrada expediente: "+destino;                             
                }                    
                /*
                if (oEntrada.load(origen, conexion))
                {                    
                    oEntrada.numexp = destino;                       
                    if (oEntrada.insert(conexion) != 1)
                    {
                        estadoOK = false;
                        descError = "No se inserta en entrada expediente: "+destino;                             
                    }                    
                }
                else
                {
                    estadoOK = false;
                    descError = "No se encuentra en entrada el expediente de origen: "+origen;
                }
                * */
            }
            /*
            if (estadoOK)
            {//documenta      
                oDocumenta.clear();
                if (oDocumenta.load(origen, conexion) == 1)
                {
                    oDocumenta.numexp = destino;                       
                    if (oDocumenta.insertDocumenta(conexionOCO) != 1)
                    {
                        estadoOK = false;
                        descError = "No se inserta en documenta expediente: "+destino;                             
                    }                    
                }
                else
                {
                    estadoOK = false;
                    descError = "No se encuentra en documenta el expediente de origen: "+origen;
                }
            }
            **/
            if (estadoOK)
            {//PRODUSU      

                oProdusu.numexp = oSolicitudes.numexp;;
                oProdusu.coddel = oSolicitudes.delegado;
                oProdusu.codusu = "juan";
                oProdusu.fecha  = new java.util.Date();
                oProdusu.accion = "A";
                oProdusu.tiempo = 1;
                oProdusu.pantalla = "RE";
                if (oProdusu.insertProdusu(conexionOCO) != 1)
                {
                    estadoOK = false;
                    descError = "No se inserta en produsu expediente: "+origen+" objeto: "+destino+ " sobre la tasacion: "+oSolicitudes.numexp; 
                }
            }
            
        }//try
        catch (Exception e)
        {
            estadoOK = false;
            descError = "Imposible realizar vuelco de datos del expediente: "+origen+ " y objeto: "+destino+ ". Motivo: "+e.toString();
        }
        finally
        {
            return estadoOK;
        }
    }//vuelcaDatosTasacion
    
    
    
    private boolean vuelcaDatosTasacion_novt()
    {
        String sConsulta = null;
        inicializaEstado();        
        java.sql.ResultSet rs = null;
        Double suputil = 0.0;
        String svalor = null;
        int ivalor = 0;
        String letra = "";
        
        String referencia = "";
        int codcli = 317;
        String oficina = "CEE";
        String tipoinm = "";
        String aviso = "";
        String tipovia = "";
        String calle = "";
        String numero = "";
        String escalera = "";
        String planta = "";
        String puerta = "";
        String localidad = "";
        String provincia = "";
        String codpos = "";
        String finca = "";
        String refcatastral = "";
        String contacto = "";
        String mailcontacto = "";
        
        
        try
        {
            
                referencia = dameValorExcel(fila,0).trim();          
                tipoinm = dameValorExcel(fila,3).trim();
                aviso = dameValorExcel(fila,4).trim();
                tipovia = dameValorExcel(fila,5);
                calle = dameValorExcel(fila,6);
                numero = dameValorExcel(fila,7);
                escalera = dameValorExcel(fila,10);
                planta = dameValorExcel(fila,11);
                puerta = dameValorExcel(fila,12);
                localidad =dameValorExcel(fila,13);
                provincia = dameValorExcel(fila,14);
                if (provincia != null) provincia = provincia.trim();
                codpos = dameValorExcel(fila,15);
                finca = dameValorExcel(fila,16);
                refcatastral = dameValorExcel(fila,17);
                contacto = dameValorExcel(fila,18);
                mailcontacto = dameValorExcel(fila,19);
            
            
            //1.- insertamos SOLICITUDES
            
            oSolicitudes.clear();
            
            
            
            //provincia
            /*busqueda por codigo
            svalor = dameValorExcel(fila,9);  //codigo postal            
            if (svalor != null && !svalor.trim().equals(""))
            {
                if (svalor.length() == 5) svalor = svalor.substring(0,2);
                else svalor = svalor.substring(0,1);
                ivalor = Integer.parseInt(svalor);
            }
            sConsulta = "SELECT * FROM provincias WHERE codpro = "+ivalor;
            * */
            sConsulta = "SELECT * FROM provincias WHERE nompro = '"+provincia+"'";
            rs = Utilidades.Conexion.select(sConsulta, conexion);
            if (rs.next())
            {
                oSolicitudes.numexp = rs.getString("prefijo")+"17."+referencia+"-CE";
                oSolicitudes.provin = rs.getString("nompro");
                oSolicitudes.delegado = Integer.parseInt(rs.getString("coddel"));
            }
            else
            {
                estadoOK = false;
                descError = "No se localiza provincia: "+provincia;
            }
            rs.close();
            oSolicitudesoco.clear();
            if (oSolicitudesoco.load(oSolicitudes.numexp, conexionOCO) != 0)
            {                             
                  estadoOK = false;
                  logger.error("ya existe en OCO nuevo expediente: "+oSolicitudesoco.numexp);                             
            }
            
            
            if (estadoOK)
            {//                
                oSolicitudes.tipoinm = tipoinm;
                oSolicitudes.codcli = codcli;
                oSolicitudes.oficina = oficina;
                oSolicitudes.loadDatosOficina(conexion);   
                oSolicitudes.solici = "ALISEDA";                
                oSolicitudes.nifsolici = "A28335388";
                oSolicitudes.codpos = Integer.parseInt(codpos);                 
                oSolicitudes.locali = localidad.trim();
                oSolicitudes.munici = oSolicitudes.locali; 
                if (tipovia != null) tipovia = tipovia.trim();
                else tipovia = "";
                if (tipovia.length() > 4)
                {
                    oSolicitudes.codsitu = tipovia;
                    oSolicitudes.situacion = calle.trim();
                }
                else
                {
                    oSolicitudes.tipovia = tipovia;
                    oSolicitudes.calle = calle.trim();
                }
                oSolicitudes.numero = numero;
                oSolicitudes.escalera = escalera;
                oSolicitudes.planta = planta;
                oSolicitudes.puerta = puerta;
                oSolicitudes.tipoif = 8;
                oSolicitudes.codage = null;                
                oSolicitudes.codest = 0;
                oSolicitudes.indicador = null;
                oSolicitudes.estimado = null;                
                oSolicitudes.fchenc = new java.util.Date();
                oSolicitudes.fchvis = null;
                oSolicitudes.fchrec = null;
                oSolicitudes.fchrem = null;
                oSolicitudes.tasacion = null;
                oSolicitudes.seguro = null;         
                oSolicitudes.superficie = 0.0;
                if (contacto != null)
                {
                    oSolicitudes.contacto = contacto.trim().toUpperCase();
                }
                //contacto las naranjas coger los ECR el resto lo que haya si hay 2 tomo el ECI
                
                
                if (oSolicitudes.insert(false,conexionOCO) != 1)  //el false es para no inserte en historico.
                {
                   estadoOK = false;
                   descError = "No se inserta en solicitudes expediente: "+oSolicitudes.numexp;
                }  
                else logger.info("Expediente asignado: "+oSolicitudes.numexp);
            }
            if (estadoOK)
            {//datosreg                
                            
                    oDatosreg.clear();
                    
                         oDatosreg.numexp = oSolicitudes.numexp;
                         oDatosreg.tipoinm = oSolicitudes.tipoinm;       
                         oDatosreg.numero = 1;
                         oDatosreg.finca = finca;                         
                         if (oDatosreg.insert(conexionOCO) != 1)
                         {
                             estadoOK = false;
                             descError = "No se inserta en datosreg expediente: "+oSolicitudes.numexp;
                         }                                            
                                
            }//datosreg
            
            if (estadoOK)
            {//catastro                
                
                         oCatastro.numexp = oSolicitudes.numexp;
                         oCatastro.tipoinm = oSolicitudes.tipoinm;
                         oCatastro.numero = 1;
                         oCatastro.fcatastral = refcatastral;
                         
                         if (fcatastral != null) oCatastro.fcatastral = fcatastral;                         
                         if (oCatastro.insert(conexionOCO) != 1)
                         {
                             estadoOK = false;
                            descError = "No se inserta en catastro expediente: "+oSolicitudes.numexp;
                         }
                                    
             }
                            
            /*
            if (estadoOK)
            {
                oOtrastas.numexp = oSolicitudes.numexp;
                oOtrastas.tipoinm = oSolicitudes.tipoinm;
                oOtrastas.numtipo1 = 1;
                oOtrastas.tipologia1 = tipoinmOrigen;
                oOtrastas.porcen1 = 100.0;
                oOtrastas.superf1 = suputil;
                oOtrastas.tottipos = 1;                                                
                if (oOtrastas.insertOtrastas(conexionOCO) != 1)
                {
                        estadoOK = false;
                        descError = "No se inserta en otrastas expediente: "+destino;                             
                }  
                
            }
            * **/
            
            if (estadoOK)
            {//refer
                oRefer.clear();
                
                    oRefer.numexp = oSolicitudes.numexp;   
                    oRefer.referencia = referencia;
                    if (mailcontacto != null)
                    oRefer.nombstor = mailcontacto.trim();                    
                    if (oRefer.nombstor.length() > 35) oRefer.nombstor = oRefer.nombstor.substring(0, 35);
                    if (oRefer.insert(conexionOCO) != 1)
                    {
                        estadoOK = false;
                        descError = "No se inserta en refer expediente: "+oSolicitudes.numexp;                          
                    }                    
               
            }
            
            if (estadoOK && aviso != null && !aviso.trim().equals(""))
            {//avisos
                oAvisos.numexp = oSolicitudes.numexp;
                oAvisos.aviso1 = aviso.trim();
                
                    if (oAvisos.insert(conexionOCO) != 1)
                    {
                        estadoOK = false;
                        descError = "No se inserta en avisos expediente: "+oSolicitudes.numexp;;                             
                    }                                    
            }
            
            
            if (estadoOK)
            {//entrada      
                oEntrada.numexp = oSolicitudes.numexp;
                oEntrada.coddel = oSolicitudes.delegado;
                if (oEntrada.insert(conexionOCO) != 1)
                {
                        estadoOK = false;
                        descError = "No se inserta en entrada expediente: "+oSolicitudes.numexp;                        
                }                    
                
            }
            /*
            if (estadoOK)
            {//documenta      
                oDocumenta.clear();
                if (oDocumenta.load(origen, conexion) == 1)
                {
                    oDocumenta.numexp = destino;                       
                    if (oDocumenta.insertDocumenta(conexionOCO) != 1)
                    {
                        estadoOK = false;
                        descError = "No se inserta en documenta expediente: "+destino;                             
                    }                    
                }
                else
                {
                    estadoOK = false;
                    descError = "No se encuentra en documenta el expediente de origen: "+origen;
                }
            }
            **/
            if (estadoOK)
            {//PRODUSU      

                oProdusu.numexp = oSolicitudes.numexp;;
                oProdusu.coddel = oSolicitudes.delegado;
                oProdusu.codusu = "juan";
                oProdusu.fecha  = new java.util.Date();
                oProdusu.accion = "A";
                oProdusu.tiempo = 1;
                oProdusu.pantalla = "RE";
                if (oProdusu.insertProdusu(conexionOCO) != 1)
                {
                    estadoOK = false;
                    descError = "No se inserta en produsu expediente: "+oSolicitudes.numexp; 
                }
            }
            
        }//try
        catch (Exception e)
        {
            estadoOK = false;
            descError = "Imposible realizar vuelco de datos del expediente. Motivo: "+e.toString();
        }
        finally
        {
            return estadoOK;
        }
    }//vuelcaDatosTasacion
    
    private boolean vuelcaDocumentacion (String origen,String destino)
    {
        try
        {            
            //1.- recuperamos los ficheros de PLANTA
            PreparedStatement  stmtOrcl_origen = null;
            ResultSet rsOracle_origen;      
            
            PreparedStatement  stmtOrcl_destino = null;
            ResultSet rsOracle_destino;      
            
            java.sql.PreparedStatement p_stmtOracle;
            
            int x_tipo = 245;  //planos
            int x_tipo_situacion = 4320; // situacion y entorno
            String usuario = "juan";
            
            //stmtOrcl_origen = conexGraficos.prepareStatement("select d.*,i.usuario,i.fechahor from docgrafica d join idendoc i on (d.numero = i.numero) WHERE d.numexp = '"+origen+"' and (tipo = " + x_tipo +" or tipo= "+x_tipo_situacion+")");
            stmtOrcl_origen = conexGraficos.prepareStatement("select d.* from docgrafica d  WHERE d.numexp = '"+origen+"' and (tipo = " + x_tipo +" or tipo= "+x_tipo_situacion+")");
            rsOracle_origen = stmtOrcl_origen.executeQuery( );
            java.sql.Blob contenido_origen = null;
            java.sql.Blob contenido_destino = null;
            java.io.InputStream inStream = null;
            java.io.OutputStream blobOutputStream = null;
            int numero = 0;
            byte[] buffer2 = null;
            int num = 0;
            
            while (estadoOK && rsOracle_origen.next()) 
            {
                contenido_origen = rsOracle_origen.getBlob("contenido"); 
                inStream = contenido_origen.getBinaryStream();  //fichero a duplicar
                
                //este fichero lo subimos al expediente de destino.
                stmtOrcl_destino = conexGraficosOCO.prepareStatement("select seq_docgrafica.nextval from dual");
                rsOracle_destino = stmtOrcl_destino.executeQuery();
                if (rsOracle_destino.next()) numero = rsOracle_destino.getInt(1);
                
                stmtOrcl_destino.execute("insert into docgrafica values ('" + destino + "'," + numero + ",'" + rsOracle_origen.getString("descripcion") + "','" + rsOracle_origen.getString("mime_type") + "',empty_blob(),'" + rsOracle_origen.getString("pie") + "'," + rsOracle_origen.getString("tipo") +")");
                p_stmtOracle = conexGraficosOCO.prepareStatement("select contenido from docgrafica where numero=" + numero + " for update");
                rsOracle_destino = p_stmtOracle.executeQuery();
                
                if (rsOracle_destino.next())
                {

                     contenido_destino = rsOracle_destino.getBlob(1);
                     blobOutputStream = ((oracle.sql.BLOB)contenido_destino).getBinaryOutputStream();
                     //java.io.InputStream stream = new java.io.FileInputStream(ruta.trim()+fichero);
                     buffer2 = new byte[10*1024];
                     num = 0;
                     while((num = inStream.read(buffer2)) != -1) 
                     {
                            blobOutputStream.write(buffer2,0,num);
                     }
                     blobOutputStream.flush();                     
                     blobOutputStream.close();
                     blobOutputStream = null;

                     
                     stmtOrcl_destino.execute("insert into idendoc (numero, usuario) values (" + numero + ", '"+usuario+"')");                                                                              
                 }                                                                
                
                inStream.close();
                inStream = null;
                
                rsOracle_destino.close();
                rsOracle_destino = null;
                numero = 0;                                
              
                //contenido_origen.free();
                contenido_origen = null;  
                //contenido_destino.free();
                contenido_destino= null;   
                
                stmtOrcl_destino.close();
                stmtOrcl_destino = null;
                p_stmtOracle.close();
                p_stmtOracle = null;                                
            }//while fotos
            stmtOrcl_origen.close();
            stmtOrcl_origen = null;
            rsOracle_origen.close();
            rsOracle_origen = null;
        }
        catch (Exception e)
        {
            estadoOK = false;
            descError = "Imposible realizar vuelco de documentacion del expediente: "+origen+ " sobre el expediente: "+destino+ ". Motivo: "+e.toString();
        }
        finally
        {
            return estadoOK;
        }
    }//vuelcaDocumentacion
    
    private boolean vuelcaDocumentacionEnVT (String origen,String destino)
    {
        try
        {            
            //1.- recuperamos los ficheros de PLANTA
            PreparedStatement  stmtOrcl_origen = null;
            ResultSet rsOracle_origen;      
            
            PreparedStatement  stmtOrcl_destino = null;
            ResultSet rsOracle_destino;      
            
            java.sql.PreparedStatement p_stmtOracle;
            
            int x_tipo = 245;  //planos
            int x_tipo_situacion = 4320; // situacion y entorno
            String usuario = "juan";
            
            //stmtOrcl_origen = conexGraficos.prepareStatement("select d.*,i.usuario,i.fechahor from docgrafica d join idendoc i on (d.numero = i.numero) WHERE d.numexp = '"+origen+"' and (tipo = " + x_tipo +" or tipo= "+x_tipo_situacion+")");
            stmtOrcl_origen = conexGraficos.prepareStatement("select d.* from docgrafica d  WHERE d.numexp = '"+origen+"' and (tipo = " + x_tipo +" or tipo= "+x_tipo_situacion+")");
            rsOracle_origen = stmtOrcl_origen.executeQuery( );
            java.sql.Blob contenido_origen = null;
            java.sql.Blob contenido_destino = null;
            java.io.InputStream inStream = null;
            java.io.OutputStream blobOutputStream = null;
            int numero = 0;
            byte[] buffer2 = null;
            int num = 0;
            
            while (estadoOK && rsOracle_origen.next()) 
            {
                contenido_origen = rsOracle_origen.getBlob("contenido"); 
                inStream = contenido_origen.getBinaryStream();  //fichero a duplicar
                
                //este fichero lo subimos al expediente de destino.
                stmtOrcl_destino = conexGraficos.prepareStatement("select seq_docgrafica.nextval from dual");
                rsOracle_destino = stmtOrcl_destino.executeQuery();
                if (rsOracle_destino.next()) numero = rsOracle_destino.getInt(1);
                
                stmtOrcl_destino.execute("insert into docgrafica values ('" + destino + "'," + numero + ",'" + rsOracle_origen.getString("descripcion") + "','" + rsOracle_origen.getString("mime_type") + "',empty_blob(),'" + rsOracle_origen.getString("pie") + "'," + rsOracle_origen.getString("tipo") +")");
                p_stmtOracle = conexGraficos.prepareStatement("select contenido from docgrafica where numero=" + numero + " for update");
                rsOracle_destino = p_stmtOracle.executeQuery();
                
                if (rsOracle_destino.next())
                {

                     contenido_destino = rsOracle_destino.getBlob(1);
                     blobOutputStream = ((oracle.sql.BLOB)contenido_destino).getBinaryOutputStream();
                     //java.io.InputStream stream = new java.io.FileInputStream(ruta.trim()+fichero);
                     buffer2 = new byte[10*1024];
                     num = 0;
                     while((num = inStream.read(buffer2)) != -1) 
                     {
                            blobOutputStream.write(buffer2,0,num);
                     }
                     blobOutputStream.flush();                     
                     blobOutputStream.close();
                     blobOutputStream = null;

                     
                     stmtOrcl_destino.execute("insert into idendoc (numero, usuario) values (" + numero + ", '"+usuario+"')");                                                                              
                 }                                                                
                
                inStream.close();
                inStream = null;
                
                rsOracle_destino.close();
                rsOracle_destino = null;
                numero = 0;                                
              
                //contenido_origen.free();
                contenido_origen = null;  
                //contenido_destino.free();
                contenido_destino= null;   
                
                stmtOrcl_destino.close();
                stmtOrcl_destino = null;
                p_stmtOracle.close();
                p_stmtOracle = null;                                
            }//while fotos
            stmtOrcl_origen.close();
            stmtOrcl_origen = null;
            rsOracle_origen.close();
            rsOracle_origen = null;
        }
        catch (Exception e)
        {
            estadoOK = false;
            descError = "Imposible realizar vuelco de documentacion del expediente: "+origen+ " sobre el expediente: "+destino+ ". Motivo: "+e.toString();
        }
        finally
        {
            return estadoOK;
        }
    }//vuelcaDocumentacionEnVT
    
    public static String dameValorExcel(int fila,int col)  
    {
        String valor = null;
        try
        {                 
             celda = oExcel.getCeldaFilaHoja(HojaActual,fila,col);                                 
             valor = Utilidades.Excel.getStringCellValue(celda);         
        }   
        catch (FileNotFoundException fnfe)
        {
            valor = null;
        }
        catch (IOException ioe)
        {
            valor = null;
        }
        catch (Exception e)
        {
            valor = null;
        }
        finally
        {
            return valor;
        }
    }//dameValorExcel
    
    private String garantias_anteriores_msg(String referencia) {
        String letra = null;
        int cuantasHay = 0;
        ResultSet rsAnteriores = null;
        try {            
            String consulta = "SELECT count(*) FROM refer WHERE refer.referencia = '" + referencia.trim() + "'";
            rsAnteriores = Utilidades.Conexion.select(consulta, conexionOCO);
            if (rsAnteriores.next()) {
                cuantasHay = rsAnteriores.getInt(1);
            }
            switch (cuantasHay) {
                case (0): {
                    letra = "";
                    break;
                }
                case (1): {
                    letra = "B";
                    break;
                }
                case (2): {
                    letra = "C";
                    break;
                }
                case (3): {
                    letra = "D";
                    break;
                }
                case (4): {
                    letra = "E";
                    break;
                }
                case (5): {
                    letra = "F";
                    break;
                }
                case (6): {
                    letra = "G";
                    break;
                }
                case (7): {
                    letra = "H";
                    break;
                }
                case (8): {
                    letra = "I";
                    break;
                }
                case (9): {
                    letra = "J";
                    break;
                }
                case (10): {
                    letra = "K";
                    break;
                }
                case (11): {
                    letra = "L";
                    break;
                }
                case (12): {
                    letra = "M";
                    break;
                }
                case (13): {
                    letra = "N";
                    break;
                }
                case (14): {
                    letra = "O";
                    break;
                }
                case (15): {
                    letra = "P";
                    break;
                }
                case (16): {
                    letra = "Q";
                    break;
                }
                case (17): {
                    letra = "R";
                    break;
                }
                case (18): {
                    letra = "S";
                    break;
                }
                case (19): {
                    letra = "T";
                    break;
                }
                case (20): {
                    letra = "U";
                    break;
                }
                case (21): {
                    letra = "V";
                    break;
                }
                case (22): {
                    letra = "W";
                    break;
                }
                case (23): {
                    letra = "X";
                    break;
                }
                case (24): {
                    letra = "Y";
                    break;
                }
                case (25): {
                    letra = "Z";
                    break;
                }
                case (26): {
                    letra = "BB";
                    break;
                }
                case (27): {
                    letra = "BC";
                    break;
                }
                case (28): {
                    letra = "BD";
                    break;
                }    
            }// switch
        }//try
        catch (SQLException sqle) {
            estadoOK = false;
            descError = "Imposible recuperar garantias anteriores. Excepcion: " + sqle.toString();
            letra = null;
        } finally {
            return letra;
        }
    }//garantias_anteriores
    
    
}//class CEVUCI
