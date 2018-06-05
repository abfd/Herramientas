/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package herramientas;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author Ana Belen de Frutos
 */
public class cargaReferencias 
{
    
    //atributos de estado
    public boolean estadoOK = true;
    private int error = 0;
    private String descError = "";
        
    //fichero de propiedades
    private String ficheroPropiedadesName = "herramientas.properties";
    private java.util.Properties fpropiedades = new java.util.Properties();
    
    // Fichero Log4j    
    private Logger logger = Logger.getLogger(cargaReferencias.class);
    private String rutaLog = "";
    
    //
    private String rutaXML = "";   //donde guardamos los xml recuperados del catastro para su procesamiento.
    private String refcatastral = "";
    private String refcliente = "";
    private String tipoinm = "";
    
    //fichero EXCEL
    private String excel = "";
    
    private Connection conexion = null;
    private Connection conexiondesa = null;
    //tratamiento XML
    xmldatostasacion.CrearXMLDatosTasacion XMLOrigen = null;
    xmldatostasacion.CrearXMLDatosTasacion XMLOrigenXY = null;
    
    //objetos a insertar en la base de datos
    Objetos.Solicitudes oSolicitudes = null;
    Objetos.Avisos oAvisos = null;
    Objetos.Entrada oEntrada = null;
    Objetos.Produsu oProdusu = null;
    Objetos.Refer oRefer = null;
    Objetos.Documenta oDocumenta = null;
    Objetos.Datosreg oDatosreg = null;
    Objetos.Catastro oCatastro = null;
    
    //datos recuperados de la tabla de encargos.
    private String oficina = null;
    private int IDencargo = 0;
    
    public static void main(String[] args) 
    {
        // TODO code application logic here
        //cargaReferencias cargaReferencias = new cargaReferencias("25215A004000060000YX","0040000010535","VPT","1");
        cargaReferencias cargaReferencias = new cargaReferencias("e");
        //cargaReferencias cargaReferencias = new cargaReferencias();
        cargaReferencias = null;
        
    }//main
    
    
    public cargaReferencias(String refcatas, String refcli, String tipinm, String estadoEncargo)
    {//constructor
        String sConsulta = null;
        cargaPropiedades();
        try
        {
            conexion = Utilidades.Conexion.getConnection(fpropiedades.getProperty("url_datos"));
            conexion.setAutoCommit(false);                
            //for referencias                   
            //Utilidades.Excel oExcel = new Utilidades.Excel(excel);            
            //org.apache.poi.hssf.usermodel.HSSFCell celda = null;
            //String hoja = "referencias";   //nombre de la hora
            //for (int fila = 0; fila < 48;fila ++)              
            //{
                inicializaEstado();
                
                refcatastral = refcatas.trim();
                refcliente = refcli.trim();
                tipoinm = tipinm.trim();
                oficina = null; //oficina recuperada de la tabla de encargos.
                IDencargo = 0;
                
                XMLOrigen = null;
                XMLOrigenXY = null;
                oSolicitudes = null;
                oEntrada = null;
                oProdusu = null;
                oAvisos = null;
                /*
                celda = oExcel.getCeldaFilaHoja(hoja,fila,0);                
                if (celda != null) 
                {
                    refcatastral = celda.getStringCellValue();
                }
                */
                logger.info("INICIAMOS CARGA REFCATASTRAL: "+refcatastral.trim());
                if (estadoEncargo.trim().equals("5"))
                {//no insertamos en solicitudes unicamente actualizamos el estado en encargos.
                    if (recuperaDatosEncargo())
                    {
                        sConsulta = "UPDATE encargos SET codigo = "+estadoEncargo.trim()+" WHERE ID = "+this.IDencargo;
                        if(Utilidades.Conexion.update(sConsulta,conexion)==0)
                        {                            
                                  estadoOK = false;
                                  descError = "Imposible acutalizar datos encargo";                                
                        }   
                        if (estadoOK)
                        {
                                conexion.commit();
                                logger.info("Actualizado estado a 5 para la referencia : "+refcliente);
                        }
                        else
                        {
                               conexion.rollback();
                               logger.info("Imposible actualizar estado a 5 para la referencia : "+refcliente);
                        }
                    }//                    
                    
                }
                else
                {
                    if (refcatastral != null && refcatastral.trim().length() >= 14 && recuperaXML())
                    {//procesamos el xml para darlo de alta como encargo de tasación
                            XMLOrigen = new xmldatostasacion.CrearXMLDatosTasacion(rutaXML+refcatastral+".xml"); 
                            if (XMLOrigen.generadoXML())
                            {
                                recuperaValores();
                                if (estadoOK)
                                {
                                    //INSERTAMOS EN SOLICITUDES
                                    try
                                    {
                                        if (oSolicitudes.insertSolicitudes(conexion) != 1)
                                        {
                                            estadoOK = false;
                                            descError = "No se puede insertar en Solicitudes. No devuelve 1.";
                                        }
                                    }
                                    catch (Exception e)
                                    {
                                        estadoOK = false;
                                        descError = "No se puede insertar en Solicitudes. Descripcion: "+e.toString();
                                    }
                                    
                                    //AVISOS SOLO SI APORTAN DIRECCION COMPLETA
                                    if (estadoOK && oAvisos != null)
                                    {
                                        oAvisos.numexp = oSolicitudes.numexp;
                                        try
                                        {
                                            if (oAvisos.insert(conexion)  != 1)
                                            {
                                                estadoOK = false;
                                                descError = "No se puede insertar en Avisos. No devuelve 1.";
                                            }
                                        }                            
                                        catch (Exception e)
                                        {
                                              estadoOK = false;
                                              descError = "No se puede insertar en Avisos. Descripcion: "+e.toString();                          
                                        }
                                        oAvisos = null;
                                    }
                                    
                                    //INSERTAMOS EN DATOSREG Y CATASTRO.
                                    if (estadoOK)
                                    {//DATOSREG
                                        oDatosreg = new Objetos.Datosreg();
                                        oDatosreg.numexp = oSolicitudes.numexp;
                                        oDatosreg.tipoinm = oSolicitudes.tipoinm;
                                        try
                                        {
                                            if (oDatosreg.insert(conexion)  != 1)
                                            {
                                                estadoOK = false;
                                                descError = "No se puede insertar en Datosreg. No devuelve 1.";
                                            }
                                        }                            
                                        catch (Exception e)
                                        {
                                              estadoOK = false;
                                              descError = "No se puede insertar en Datosreg. Descripcion: "+e.toString();                          
                                        }
                                        if (estadoOK)
                                        {//CATASTRO
                                            oCatastro = new Objetos.Catastro();
                                            oCatastro.numexp = oSolicitudes.numexp;
                                            oCatastro.tipoinm = oSolicitudes.tipoinm;
                                            oCatastro.fcatastral = refcatastral;
                                            try
                                            {
                                                if (oCatastro.insert(conexion)  != 1)
                                                {
                                                    estadoOK = false;
                                                    descError = "No se puede insertar en Catastro. No devuelve 1.";
                                                }
                                            }                            
                                            catch (Exception e)
                                            {
                                                  estadoOK = false;
                                                  descError = "No se puede insertar en Catastro. Descripcion: "+e.toString();                          
                                            }
                                        }
                                        oDatosreg = null;
                                        oCatastro = null;                                        
                                    }//datosreg + catastro
                                    
                                    //documenta finalidad 9
                                    if (estadoOK)
                                    {//DOCUMENTA
                                        oDocumenta = new Objetos.Documenta();
                                        oDocumenta.numexp = oSolicitudes.numexp;
                                        oDocumenta.tipoinm = oSolicitudes.tipoinm;
                                        oDocumenta.finalidad = "9";
                                        try
                                        {
                                            if (oDocumenta.insertDocumenta(conexion)  != 1)
                                            {
                                                estadoOK = false;
                                                descError = "No se puede insertar en Documenta. No devuelve 1.";
                                            }
                                        }                            
                                        catch (Exception e)
                                        {
                                              estadoOK = false;
                                              descError = "No se puede insertar en Documenta. Descripcion: "+e.toString();                          
                                        }
                                        oDocumenta = null;
                                    }
                                    
                                    if (estadoOK)
                                    {//ACTULIZAMOS EN ENCARGOS EL ESTADO DE ESA REFERENCIA.

                                        sConsulta = "UPDATE encargos SET codigo = "+estadoEncargo.trim()+" WHERE ID = "+this.IDencargo;
                                        if(Utilidades.Conexion.update(sConsulta,conexion)==0)
                                        {                            
                                            estadoOK = false;
                                            descError = "Imposible acutalizar datos encargo";                                
                                        }                                                                        
                                    }

                                    if (estadoOK)
                                    {//INSERTAMOS EN REFER
                                        oRefer = new Objetos.Refer();
                                        oRefer.numexp = oSolicitudes.numexp;
                                        oRefer.referencia = this.refcliente;
                                        try
                                        {
                                            if (oRefer.insertarRefer(conexion)!= 1)
                                            {
                                                estadoOK = false;
                                                descError = "No se puede insertar en Refer. No devuelve 1.";
                                            }
                                        }                            
                                        catch (Exception e)
                                        {
                                              estadoOK = false;
                                              descError = "No se puede insertar en Refer. Descripcion: "+e.toString();                          
                                        }
                                        oRefer = null;

                                    }

                                    if (estadoOK)
                                    { //INSERTAMOS ENTRADA
                                        oEntrada = new Objetos.Entrada();
                                        oEntrada.numexp = oSolicitudes.numexp;
                                        oEntrada.coddel = oSolicitudes.delegado;                                                                                           
                                        try
                                        {
                                            if (oEntrada.insert(conexion)!= 1)
                                            {
                                                estadoOK = false;
                                                descError = "No se puede insertar en Entrada. No devuelve 1.";
                                            }
                                        }                            
                                        catch (Exception e)
                                        {
                                              estadoOK = false;
                                              descError = "No se puede insertar en Entrada. Descripcion: "+e.toString();                          
                                        }
                                        oEntrada = null;
                                    }//ENTRADA

                                    if (estadoOK)
                                    { //INSERTAMOS PRODUSU
                                        oProdusu = new Objetos.Produsu();        
                                     try
                                     {
                                        if (oProdusu.insertProdusuOracle(oSolicitudes.numexp,"0","juan","A","1","RE",conexion)!= 1)
                                        {
                                                estadoOK = false;
                                                descError = "No se puede insertar en Produsu. No devuelve 1.";
                                        }
                                     }
                                     catch (Exception e)
                                     {
                                          estadoOK = false;
                                          descError = "No se puede insertar en Produsu. Descripcion: "+e.toString();     
                                     }
                                     oProdusu = null;
                                    }

                                    if (estadoOK)
                                    {
                                        conexion.commit();
                                        logger.info("Insertada referencia en el expediente: "+oSolicitudes.numexp);
                                    }
                                    else
                                    {
                                        conexion.rollback();
                                        logger.error("ERROR REFCATASTRAL: "+refcatastral.trim());
                                        logger.error(descError);
                                    }


                                }//if ha recuperado valores en solicitudes
                                else
                                {
                                    logger.error("ERROR REFCATASTRAL: "+refcatastral.trim());
                                    logger.error(descError);
                                }
                            }
                    }                        
                    else
                    {
                            logger.error("ERROR REFCATASTRAL: "+refcatastral.trim());                        
                            if (!estadoOK) logger.error(descError);
                            else logger.error ("La referencia es nula o su longitud es menor de 14 posiciones");
                    }
                }//if estado != 5
                logger.info("FINALIZAMOS CARGA REFCATASTRAL: "+refcatastral.trim());                
            //}//for referencias en el excel                            
            conexion.close();
            refcatastral = null;
            XMLOrigen = null;
            oSolicitudes = null;
            oEntrada = null;
            oProdusu = null;
        }
        catch (Exception e)
        {
            estadoOK = false;
            descError = "Error general en la carga de la referencia: "+refcatastral+". Descripción: "+e.toString();
            logger.error(descError);
        }
        finally
        {
            try 
            {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.rollback();
                    conexion.close();
                }
                
            } catch (SQLException sqlException) 
            {
                logger.error("Imposible cerrar conexion con Valtecnic datos.");
            }
        }
        
    }//cargaReferencias
    
    /*
    public cargaReferencias()
    {//constructor
        String sConsulta = null;
        cargaPropiedades();
        try
        {            
            conexion = Utilidades.Conexion.getConnection(fpropiedades.getProperty("url_datos"));
            conexion.setAutoCommit(false);                
            //for referencias      
            excel = "/data/informes/cargaReferencias/excel/referenciasSur.xls";
            Utilidades.Excel oExcel = new Utilidades.Excel(excel);            
            org.apache.poi.hssf.usermodel.HSSFCell celda = null;
            String hoja = "referencias";   //nombre de la hora
            oSolicitudes = null;
            oRefer = null;
            oDatosreg = null;
            oCatastro = null;
            
            for (int fila = 0; fila < 43;fila ++)              
            {
                inicializaEstado();
                
                refcatastral = "";
                refcliente = "";                
                
                oRefer = null;
                
                celda = oExcel.getCeldaFilaHoja(hoja,fila,0);                
                if (celda != null) 
                {
                    refcliente = celda.getStringCellValue();
                    refcliente = refcliente.trim();
                }
                celda = oExcel.getCeldaFilaHoja(hoja,fila,1);                
                if (celda != null) 
                {
                    refcatastral = celda.getStringCellValue();
                    refcatastral = refcatastral.trim();
                }
                
                logger.info("INICIAMOS CARGA REFCATASTRAL: "+refcatastral.trim());
                oSolicitudes = new Objetos.Solicitudes();                
                oRefer = new Objetos.Refer();
                if (oRefer.loadValuesFromReferencia(refcliente, "701", conexion) == 1)
                {
                    oSolicitudes.load(oRefer.numexp, conexion);
                    oDatosreg = new Objetos.Datosreg();
                    if (!oDatosreg.loadDatosregInmueblePrincipal(oRefer.numexp, oSolicitudes.tipoinm, conexion))
                    {//cargamos la referencia en datos registrales                        
                        oDatosreg.numexp = oSolicitudes.numexp;
                        oDatosreg.tipoinm = oSolicitudes.tipoinm;
                        oDatosreg.numero = null;
                        try
                        {
                               if (oDatosreg.insert(conexion)  != 1)
                               {
                                                estadoOK = false;
                                                descError = "No se puede insertar en Datosreg. No devuelve 1.";
                               }
                         }                            
                         catch (Exception e)
                         {
                                estadoOK = false;
                                descError = "No se puede insertar en Datosreg. Descripcion: "+e.toString();                          
                         }
                         if (estadoOK)
                         {//CATASTRO
                                            oCatastro = new Objetos.Catastro();
                                            oCatastro.numexp = oSolicitudes.numexp;
                                            oCatastro.tipoinm = oSolicitudes.tipoinm;
                                            oCatastro.fcatastral = refcatastral;
                                            try
                                            {
                                                if (oCatastro.insert(conexion)  != 1)
                                                {
                                                    estadoOK = false;
                                                    descError = "No se puede insertar en Catastro. No devuelve 1.";
                                                }
                                            }                            
                                            catch (Exception e)
                                            {
                                                  estadoOK = false;
                                                  descError = "No se puede insertar en Catastro. Descripcion: "+e.toString();                          
                                            }
                           }//if catastro
                           if (estadoOK) 
                           {
                               conexion.commit();
                               logger.info("INSERTADA REFENCIA: "+refcliente);
                           }
                           else
                           {
                               conexion.rollback();
                               logger.error("ERROR EN LA INSERCIÓN DE LA REFENCIA: "+refcliente+" Motivo: "+descError);
                           }                                          
                    }//if no existen datosreg
                    else logger.info("La referencia:"+refcliente+" ya tiene datos registrales.");
                }
                else logger.error("La referencia del cliente: "+refcliente+" NO EXISTE.");                
                logger.info("FINALIZAMOS CARGA REFCATASTRAL: "+refcatastral.trim());  
                oSolicitudes = null;
                oRefer = null;
                oDatosreg = null;
                oCatastro = null;   
            }//for referencias en el excel                            
            conexion.close();
            refcatastral = null;            
            oSolicitudes = null;
            oRefer = null;
            oDatosreg = null;
            oCatastro = null;
        }
        catch (Exception e)
        {
            estadoOK = false;
            descError = "Error general en la carga de la referencia: "+refcatastral+". Descripción: "+e.toString();
            logger.error(descError);
        }
        finally
        {
            try 
            {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.rollback();
                    conexion.close();
                }
                
            } catch (SQLException sqlException) 
            {
                logger.error("Imposible cerrar conexion con Valtecnic datos.");
            }
        }
        
    }//cargaReferencias
    */
    public cargaReferencias(String nuevas)
    {//constructor
        String sConsulta = null;
        cargaPropiedades();
        int id = 0;
        String direccion = "";
        String provin = "";
        Double codpos = 0.0;
        String tipo = "";
        String subtipo = "";
        Double superf = 0.0;
        Double objeto = null;
        int cliente = 701;
        String oficina = "BECX";
        //String oficina = "BERE";
        String fechalta = "11092012";
        java.sql.ResultSet rsDatos = null;
        try
        {            
            conexion = Utilidades.Conexion.getConnection(fpropiedades.getProperty("url_datos"));
            conexion.setAutoCommit(false);                
            //for referencias      
            excel = "/data/informes/OW/17.xls";
            Utilidades.Excel oExcel = new Utilidades.Excel(excel);            
            org.apache.poi.hssf.usermodel.HSSFCell celda = null;
            String hoja = "Hoja1";   //nombre de la hora
            oSolicitudes = null;
            oRefer = new Objetos.Refer();
            oDatosreg = null;
            oCatastro = null;
            String subrefcliente = null;
            String last_subrefcliente = "";
            sConsulta = "SELECT max(id) id from encargos";
            ResultSet rs = Utilidades.Conexion.select(sConsulta,conexion);
            if (rs.next()) id = rs.getInt("id");
            for (int fila = 0; fila < 17;fila ++)              
            {
                System.out.println(fila);
                inicializaEstado();
                id ++;
                refcatastral = "";
                refcliente = "";                
                
                
                 
                direccion = "";
                provin = "";
                codpos = 0.0;
                tipo = "";
                subtipo = "";
                subrefcliente = "";
            superf = 0.0;       
            try
            {            
                
                /*
                celda = oExcel.getCeldaFilaHoja(hoja,fila,0);                
                if (celda != null) 
                {//id
                    id = celda.getNumericCellValue();
                    
                }                                
                */
                celda = oExcel.getCeldaFilaHoja(hoja,fila,0);                
                if (celda != null) 
                {//referencia
                    refcliente = celda.getStringCellValue();
                    refcliente = refcliente.trim();
                }
                logger.info("INICIAMOS REFerencia: "+refcliente.trim());
                celda = oExcel.getCeldaFilaHoja(hoja,fila,1);                
                if (celda != null) 
                {//direccion
                    direccion = Utilidades.Cadenas.procesarComillasConsulta(celda.getStringCellValue());   
                    if(direccion!=null) direccion = Utilidades.Cadenas.normalizeBlanks(direccion);
                }
                celda = oExcel.getCeldaFilaHoja(hoja,fila,2);                
                if (celda != null) 
                {//provincia
                    provin = celda.getStringCellValue();                    
                    provin = Utilidades.Cadenas.TrimTotal(provin).toUpperCase();
                }
                celda = oExcel.getCeldaFilaHoja(hoja,fila,3);                
                if (celda != null) 
                {//refcatastral
                    try
                    {
                       refcatastral = celda.getStringCellValue();    
                        if(refcatastral!=null && refcatastral.equals("-")) refcatastral = "";
                        if(refcatastral.length()>25)
                        {
                            refcatastral = Utilidades.Cadenas.normalizeBlanks(refcatastral);
                            //refcatastral = refcatastral.substring(refcatastral.lastIndexOf(" "));
                            if(refcatastral.length()>25) refcatastral = refcatastral.substring(0,24);
                        }//if
                    } 
                    catch(Exception e)
                    {
                        refcatastral = Integer.toString((int)(new Double(celda.getNumericCellValue()).doubleValue()));
                        
                    }
                }
                celda = oExcel.getCeldaFilaHoja(hoja,fila,4);                
                if (celda != null) 
                {//codpos
                    codpos = celda.getNumericCellValue();
                }
                celda = oExcel.getCeldaFilaHoja(hoja,fila,5);                
                if (celda != null) 
                {//tipo
                     tipo = celda.getStringCellValue();                    
                }
                
                celda = oExcel.getCeldaFilaHoja(hoja,fila,6);                
                if (celda != null) 
                {//tipo
                     subtipo = celda.getStringCellValue();                    
                }
                celda = oExcel.getCeldaFilaHoja(hoja,fila,7);                
                if (celda != null) 
                {//tipo
                    try
                    {
                     superf = celda.getNumericCellValue();
                    }
                    catch(Exception e)
                    {
                        
                    }
                }
                
                //******************************************************
                celda = oExcel.getCeldaFilaHoja(hoja,fila,8);                
                if (celda != null) 
                {//tipo
                     subrefcliente = celda.getStringCellValue();
                     if(subrefcliente!=null) subrefcliente = subrefcliente.substring(subrefcliente.indexOf(".")+1);
                }//if
                
                //******************************************************
                
               if(!subrefcliente.equals(last_subrefcliente))
               {
                    last_subrefcliente = subrefcliente;
                    if(last_subrefcliente==null || last_subrefcliente.trim().equals("")) last_subrefcliente="null";
                    //comprobamos que no este ya dado de alta.
                    if (oRefer.loadValuesFromReferencia(refcliente, Integer.toString(cliente), conexion) == 0)
                    {
                        rsDatos = Utilidades.Conexion.select("SELECT FECHA FROM ENCARGOS WHERE REFERENCIA='"+refcliente+"'",conexion);
                        boolean bDatos = rsDatos.next();
                        if(!bDatos)
                        {
                            try
                            {    
                                sConsulta = "INSERT INTO ENCARGOS (id,referencia,codcli,oficina,direccion,provin,refcata,codpos,tipo,subtipo,superf,codigo,fecha,objeto) VALUES (";
                                sConsulta += id+",'"+refcliente+"',"+cliente+",'"+oficina+"','"+direccion+"','"+provin+"','"+refcatastral+"',"+codpos+",'"+tipo+"','"+subtipo+"',"+superf+",0,'"+fechalta+"',"+last_subrefcliente+")";
                                if (Utilidades.Conexion.insert(sConsulta, conexion) != 1)
                                {
                                    conexion.rollback();
                                    logger.error("Error referencia: "+refcliente);
                                }
                                else
                                {
                                   conexion.commit();  //quitar
                                   //conexion.rollback();
                                   logger.info("Insertada referencia: "+ refcliente);
                                }
                            }//try INSERT
                            catch (Exception e)
                            {
                                conexion.rollback();
                                logger.error("Error insercionreferencia :"+refcliente+". Descripcion: "+e.toString());
                                if(e.toString().indexOf("violada")==-1)                   System.out.println(fila+"  : "+e);
                            }
                        }//if
                        else
                        {
                            System.out.println("ENCARGOS YA EXISTENTE CON FECHA: "+rsDatos.getString("FECHA"));
                        }//else
                    }
                    else
                    {
                System.out.println(fila+"  : DUPLICADA - REFERENCIA: "+refcliente);
                        logger.info("REFERENCIA: "+refcliente+" DUPLICADA");
                    }
               }//if last
               else
               {
                        System.out.println(fila+"  : PROMOCION - REFERENCIA: "+refcliente);
               }
            }//TRY CARGA EXCEL
            catch (Exception e)
            {            
                System.out.println(fila+"  : "+e);
                if(e.toString().indexOf("violada")==-1)                   System.out.println(fila+"  : "+e);
            }
       }//for referencias en el excel                            
            conexion.close();
            refcatastral = null;            
            oSolicitudes = null;
            oRefer = null;
            oDatosreg = null;
            oCatastro = null;
        }
        catch (Exception e)
        {
            estadoOK = false;
            descError = "Error general en la carga de la referencia: "+refcatastral+". Descripción: "+e.toString();
            logger.error(descError);
        }
        finally
        {
            try 
            {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.rollback();
                    conexion.close();
                }
                
            } catch (SQLException sqlException) 
            {
                logger.error("Imposible cerrar conexion con Valtecnic datos.");
            }
        }
        
    }//cargaReferencias
    
    public cargaReferencias()
    {//constructor
        String sConsulta = null;
        cargaPropiedades();
        int id = 0;
        String direccion = "";
        String provin = "";
        Double codpos = 0.0;
        String tipo = "";
        String subtipo = "";
        Double superf = 0.0;
        String fechalta = "16082012";
        int cliente = 701;
        String oficina = "BERE";
        //String oficina = "BECX";
        String refcato = "";

        
        try
        {            
            conexion = Utilidades.Conexion.getConnection(fpropiedades.getProperty("url_datos"));
            conexion.setAutoCommit(false);                
            conexiondesa = Utilidades.Conexion.getConnection(fpropiedades.getProperty("url_datos_desa"));
            conexiondesa.setAutoCommit(false);                
            //for referencias      
            
            sConsulta = "SELECT * FROM encargos WHERE ID IN (872,717)";
            ResultSet rs = Utilidades.Conexion.select(sConsulta,conexiondesa);
            while (rs.next()) 
            {
                try
                {
                    id = rs.getInt("id");
                    refcliente = rs.getString("referencia");
                    cliente = rs.getInt("codcli");
                    oficina = rs.getString("oficina");
                    direccion = Utilidades.Cadenas.procesarComillasConsulta(rs.getString("direccion"));
                    provin = rs.getString("provin");
                    refcatastral = rs.getString("refcata");
                    refcato = rs.getString("refcato");
                    codpos = rs.getDouble("codpos");
                    tipo = rs.getString("tipo");
                    subtipo = rs.getString("subtipo");
                    superf = rs.getDouble("superf");
                                        
                sConsulta = "INSERT INTO ENCARGOS (id,referencia,codcli,oficina,direccion,provin,refcata,refcato,codpos,tipo,subtipo,superf,codigo) VALUES (";
                sConsulta += id+",'"+refcliente+"',"+cliente+",'"+oficina+"','"+direccion+"','"+provin+"','"+refcatastral+"','"+refcato+"',"+codpos+",'"+tipo+"','"+subtipo+"',"+superf+",5)";
                
                if (Utilidades.Conexion.insert(sConsulta, conexion) != 1)
                {
                    conexion.rollback();
                    logger.error("Error referencia: "+refcliente);
                }
                else
                {
                    conexion.commit();
                    logger.info("Insertada referencia: "+ refcliente);
                }
                }
                catch (Exception e)
                {
                    conexion.rollback();
                    logger.error("Error insercionreferencia :"+refcliente+". Descripcion: "+e.toString());
                }
            }//while            
            conexion.close();
            conexiondesa.close();
            
         }
         catch (Exception e)
         {             
             logger.error("Excepcion General. Descripcion: "+e.toString());             
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
                
            } catch (SQLException sqlException) 
            {
                logger.error("Imposible cerrar conexion con Valtecnic PRODUCCION datos.");
            }
            try 
            {
                if (conexiondesa != null && !conexiondesa.isClosed()) 
                {
                    conexiondesa.rollback();
                    conexiondesa.close();
                }
                
            } catch (SQLException sqlException) 
            {
                logger.error("Imposible cerrar conexion con Valtecnic DESARROLLO datos.");
            }
        }
        
    }//cargaReferencias
    
    private void cargaPropiedades()
    {   
        boolean cargado = true;
        
        try
        {                    
            inicializaEstado();
            fpropiedades.load(this.getClass().getResourceAsStream(ficheroPropiedadesName));            
            rutaLog = fpropiedades.getProperty("rutaLog");   
            
            //FICHERO LOG4J
            PropertyConfigurator.configure(rutaLog + "Log4j.properties");                         
            rutaXML = fpropiedades.getProperty("rutaXML");
            
            //EXCEL
            excel = fpropiedades.getProperty("sUrlExcel");
            
        }//try        
        catch (Exception e)
        {
            estadoOK = false;
            descError = e.toString();
            System.out.println(e.toString());
        }        
    } //InicializaDatos


    private void inicializaEstado()
    {
        estadoOK = true;
        error = 0;
        descError = "";   
    }//inicializaEstado
    
    private boolean recuperaXML()
    {        
        
        String url =    "http://ovc.catastro.meh.es/ovcservweb/OVCSWLocalizacionRC/OVCCallejeroCodigos.asmx/Consulta_DNPRC_Codigos?CodigoProvincia=&CodigoMunicipio=&CodigoMunicipioINE=&RC="+refcatastral.trim();
        String url_xy = "";
        if (refcatastral.trim().length() > 14)  url_xy = "http://ovc.catastro.meh.es/ovcservweb/OVCSWLocalizacionRC/OVCCoordenadas.asmx/Consulta_CPMRC?Provincia=&Municipio=&SRS=&RC="+refcatastral.trim().substring(0, 14);
        else url_xy = "http://ovc.catastro.meh.es/ovcservweb/OVCSWLocalizacionRC/OVCCoordenadas.asmx/Consulta_CPMRC?Provincia=&Municipio=&SRS=&RC="+refcatastral.trim();
        
        try
        {
                java.net.URL pagina = new java.net.URL(url);
                java.io.BufferedReader in = new java.io.BufferedReader (new java.io.InputStreamReader(pagina.openStream()));
                java.io.FileWriter fichero = new java.io.FileWriter(rutaXML+refcatastral+".xml");                
                int buff;
                char[] buffer = new char[1024];
                //buff = in.read(buffer);
                while ((buff = in.read(buffer)) != -1)
                {
                    if (buff < 1024){
                        char[] ultimo = new char [buff];
                        for (int i = 0; i < buff; i++) ultimo[i] = buffer[i];
                        fichero.write(ultimo);
                    } else fichero.write(buffer);
                }
                in.close();
                fichero.flush();
                fichero.close();                
                pagina = null;
                
                //url para la captura de coordenadas.   
                pagina = new java.net.URL(url_xy);
                in = new java.io.BufferedReader (new java.io.InputStreamReader(pagina.openStream()));
                fichero = new java.io.FileWriter(rutaXML+refcatastral+"_XY.xml");                                
                buffer = new char[1024];
                //buff = in.read(buffer);
                while ((buff = in.read(buffer)) != -1)
                {
                    if (buff < 1024){
                        char[] ultimo = new char [buff];
                        for (int i = 0; i < buff; i++) ultimo[i] = buffer[i];
                        fichero.write(ultimo);
                    } else fichero.write(buffer);
                }
                in.close();
                fichero.flush();
                fichero.close();
                pagina = null;
        }
        catch (Exception e)
        {
            estadoOK = false;
            error = 1;
            descError = "Imposible obtener XML asociado a la referencia:"+refcatastral;               
        }
        finally
        {
            return estadoOK;
        }
    }//recuperaXML
    
    private void recuperaValores()
    {
        //cargamos valores en solicitudes
        try
        {
            
            inicializaEstado();
            
            oSolicitudes = null;     
            oSolicitudes = new Objetos.Solicitudes();            
            String codprovin = "";
            String nameprovin = "";
            String codmunici = "";
            String namemunici = "";
            String codvia = "";
            String tipovia = "";
            String bloque = "";
            String escalera = "";
            String planta = "";
            String puerta = "";
            String prefijo = "";
            String dircompleta = "";
            String poligono = "";
            String parcela = "";
            
            
            
            codprovin = XMLOrigen.recuperaValor("loine", "cp");   
            if (codprovin != null && !codprovin.trim().equals("") && recuperaDatosEncargo())
            {                                
                //CODDEL
                nameprovin = XMLOrigen.recuperaValor("dt", "np");
                prefijo = asignaDelegado(Integer.parseInt(codprovin));
                //NUMEXP
                oSolicitudes.codcli = "701";
                oSolicitudes.oficina = this.oficina;
                oSolicitudes.loadDatosOficina(conexion);
                oSolicitudes.numexp = numero_automatico(oSolicitudes.codcli.substring(1, 3), prefijo);                
                //                                               
                oSolicitudes.solici = "OW";
                oSolicitudes.nifsolici = "";
                //PROVINCIA - asignado en asignaDelegado
                //if (nameprovin != null && !nameprovin.trim().equals("")) oSolicitudes.provin = nameprovin.trim().toUpperCase();
                //else oSolicitudes.provin = "A DETERMINAR";
                //MUNICIPIO
                codmunici = XMLOrigen.recuperaValor("loine", "cm");                
                namemunici = XMLOrigen.recuperaValor("dt", "nm");
                if (namemunici != null && !namemunici.trim().equals("")) oSolicitudes.munici = namemunici.trim().toUpperCase();
                else 
                {
                    if (codmunici != null)
                    {
                        oSolicitudes.munici = recuperaMunicipio(codprovin,codmunici);
                    }
                    else oSolicitudes.munici = "A DETERMINAR";
                }
                
                //DIRCOMPLETA EN AVISOS.
                dircompleta = XMLOrigen.recuperaValor("bi", "ldt");
                if (dircompleta != null && !dircompleta.trim().equals(""))
                {
                    oAvisos = new Objetos.Avisos();
                    oAvisos.aviso1 = dircompleta.trim();                    
                }
                
                                
                oSolicitudes.equis = "";
                oSolicitudes.ies = "";
                oSolicitudes.contacto = "Sede";
                oSolicitudes.telefonos = "915639904";
                //oSolicitudes.delegado se asigna en asignadelegado
                oSolicitudes.codest = "0";
                oSolicitudes.fchenc = new java.text.SimpleDateFormat("dd-MM-yyyy").format(new java.util.Date()); 
                
                //URBANO
                //determinamos si la localización se corresponde con un bien urbano (lous) o rustico (lors)
                XMLOrigen.recuperaElemento("lous");
                int numeroUrbano = XMLOrigen.ElementosRecuperados();
                if (numeroUrbano > 0)
                {//LOCALIZACION MUNICIPAL DEL BIEN URBANO
                    XMLOrigen.recuperaElemento(0);                    
                    codvia = XMLOrigen.recuperaValorElemento("cv");                    
                    tipovia = XMLOrigen.recuperaValorElemento("tv");
                    if (tipovia != null) oSolicitudes.tipovia = tipovia.trim().toUpperCase();
                    if (XMLOrigen.recuperaValorElemento("nv") != null) oSolicitudes.calle = XMLOrigen.recuperaValorElemento("nv").toUpperCase();
                    if (XMLOrigen.recuperaValorElemento("pnp") != null && !XMLOrigen.recuperaValorElemento("pnp").trim().equals("") && !XMLOrigen.recuperaValorElemento("pnp").equals("0"))
                    {
                        oSolicitudes.numero = XMLOrigen.recuperaValorElemento("pnp").trim();
                    }
                    else
                    {
                        if (XMLOrigen.recuperaValorElemento("snp") != null && !XMLOrigen.recuperaValorElemento("snp").trim().equals("") && !XMLOrigen.recuperaValorElemento("snp").equals("0"))
                        {
                            oSolicitudes.numero = XMLOrigen.recuperaValorElemento("snp").trim();
                        }
                    }
                    
                    //bloque = XMLOrigen.recuperaValorElemento("bq");                     
                    escalera = XMLOrigen.recuperaValorElemento("es");                    
                    if (escalera != null && !escalera.trim().equals("")) oSolicitudes.escalera = escalera.trim();
                    planta = XMLOrigen.recuperaValorElemento("pt"); 
                    if (planta != null && !planta.trim().equals("")) oSolicitudes.planta = planta.trim();
                    oSolicitudes.tipoinm = dametipoinm(planta);                                                
                    puerta = XMLOrigen.recuperaValorElemento("pu");    
                    if (puerta != null && !puerta.trim().equals("")) oSolicitudes.puerta = puerta.trim();
                    if (XMLOrigen.recuperaValorElemento("dp") != null && !XMLOrigen.recuperaValorElemento("dp").equals(""))
                    {
                        oSolicitudes.codpos = XMLOrigen.recuperaValorElemento("dp");
                        oSolicitudes.locali = recuperaLocalidad (oSolicitudes.codpos,codprovin,codmunici);
                    }
                    else
                    {
                        oSolicitudes.codpos = codprovin.trim()+"000";
                        oSolicitudes.locali = "A DETERMINAR";
                    }
                    

                }// URBANO
                
                //RUSTICO
                XMLOrigen.recuperaElemento("lors");
                int numeroRustico = XMLOrigen.ElementosRecuperados();
                if (numeroRustico > 0)
                {//LOCALIZACION MUNICIPAL DEL BIEN RUSTICO
                    XMLOrigen.recuperaElemento(0);                                       
                    //tipovia = XMLOrigen.recuperaValorElemento("tv");                    
                    oSolicitudes.tipoinm = dametipoinm(planta);  
                    //LOCALIDAD
                    if (XMLOrigen.recuperaValorElemento("dp") != null && !XMLOrigen.recuperaValorElemento("dp").equals(""))
                    {
                        oSolicitudes.codpos = XMLOrigen.recuperaValorElemento("dp");
                        oSolicitudes.locali = recuperaLocalidad (oSolicitudes.codpos,codprovin,codmunici);
                    }
                    else
                    {
                        oSolicitudes.codpos = codprovin.trim()+"000";
                        oSolicitudes.locali = "A DETERMINAR";
                    }
                    oSolicitudes.calle = "";
                    if (XMLOrigen.recuperaValorElemento("cpo") != null && !XMLOrigen.recuperaValorElemento("cpo").trim().equals("")) oSolicitudes.calle = "Poligono: "+XMLOrigen.recuperaValorElemento("cpo").trim();
                    if (XMLOrigen.recuperaValorElemento("cpo") != null && !XMLOrigen.recuperaValorElemento("cpa").trim().equals("")) oSolicitudes.calle += " Parcela: "+XMLOrigen.recuperaValorElemento("cpa").trim();
                    if (XMLOrigen.recuperaValorElemento("npa") != null && !XMLOrigen.recuperaValorElemento("npa").trim().equals("")) oSolicitudes.calle += " Paraje: " +XMLOrigen.recuperaValorElemento("npa").trim();
                    
                }//RUSTICO
                //coordenadas
                recuperaXY();
            }//if hay provincia
            else 
            {
                estadoOK = false;
                if (descError != null && descError.equals("")) descError = "No se puede dar de alta la referencia: "+refcatastral+". La provincia no es valida";
            }
            
            
        }
        catch (Exception e)
        {
            estadoOK = false;
            descError = "Error general en la recuperacion de valores para la referencia: "+refcatastral+". Descripción: "+e.toString();
        }       
        
    }//recuperaValores
    
    
    private String recuperaMunicipio (String codprovin,String codmunici)
    {
        String sConsulta = null;
        java.sql.ResultSet rs = null;
        int cuantos = 0;
        String namemunici = "A DETERMINAR";
        int codine = 0;
        try
        {
            if (codmunici.trim().length() < 3) codmunici = "0"+codmunici.trim();
            codine = Integer.parseInt(codprovin.trim()+codmunici.trim());
            
            sConsulta = "SELECT municipio  FROM municipios WHERE codine = "+codine;

            rs = Utilidades.Conexion.select(sConsulta,conexion);                                    
            while (rs.next())
            {
                 cuantos ++;
                 namemunici = rs.getString("municipio");
            }                    
            rs.close();
            rs = null;                                    
            if (cuantos > 1 || cuantos == 0) namemunici = "A DETERMINAR";

        }//try
        catch (SQLException sqle)
        {
               logger.error("Imposible asignar localidad. Excepcion: "+sqle.toString());
        }
        catch (Exception e)
        {
               logger.error("Imposible asignar localidad. Excepcion: "+e.toString());
        }
        finally
        {
            return namemunici;
        }
    }//recuperaMunicipio
    
    
    private String recuperaLocalidad (String codpos,String codprovin,String codmunici)
    {
        String sConsulta = null;
        java.sql.ResultSet rs = null;
        int cuantos = 0;
        String namelocali = "A DETERMINAR";
        try
        {
            if (codmunici.trim().length() < 3) codmunici = "0"+codmunici.trim();
            
            if (codpos != null && !codpos.trim().equals("")) sConsulta = "SELECT localidad  FROM localidades WHERE codpos = "+Integer.parseInt(codpos);
            else sConsulta = "SELECT localidad  FROM localidades WHERE codine = "+codprovin.trim()+codmunici.trim();

            rs = Utilidades.Conexion.select(sConsulta,conexion);                                    
            while (rs.next())
            {
                 cuantos ++;
                 namelocali = rs.getString("localidad");
            }                    
            rs.close();
            rs = null;                                    
            if (cuantos > 1 || cuantos == 0) namelocali = "A DETERMINAR";

        }//try
        catch (SQLException sqle)
        {
               logger.error("Imposible asignar localidad. Excepcion: "+sqle.toString());
        }
        catch (Exception e)
        {
               logger.error("Imposible asignar localidad. Excepcion: "+e.toString());
        }
        finally
        {
            return namelocali;
        }
    }//recuperaLocalidad
    
    
    private String asignaDelegado(int codprovincia)
    {//devuelve el prefijo que se asigna en el numero de tasacion
        String prefijo = null;
        String sConsulta = null;
        java.sql.ResultSet rs = null;
        try
        {
        
            sConsulta = "SELECT nompro,prefijo,coddel FROM provincias WHERE codpro = "+codprovincia;                        
            
                if (sConsulta != null)
                {
                    rs = Utilidades.Conexion.select(sConsulta,conexion);                                    
                    if (rs.next())
                    {
                        oSolicitudes.delegado = rs.getString("coddel");  
                        oSolicitudes.provin = rs.getString("nompro");
                        prefijo = rs.getString("prefijo");  
                    }                    
                    rs.close();
                    rs = null;                                    
                }                        
        }//try
        catch (SQLException sqle)
        {
               logger.error("Imposible asignar delegación. Excepcion: "+sqle.toString());
        }
        catch (Exception e)
        {
               logger.error("Imposible asignar delegación. Excepcion: "+e.toString());
        }
        finally
        {
            return prefijo;
        }
    }//asignadelegado
    
    
    private  String numero_automatico(String codcli, String prefijo)
      {
        String numexp = "";
        int intentos = 5;
        int anio = 0;
        int mes = 0;
        int dia = 0;
        int hora = 0;
        int minuto = 0;
        int segundo = 0;
        boolean seguir = true;
        Calendar calendario = null;
        
        try{

            
            //Buscamos la fecha y la hora por medio del objeto Calendar            
            while (seguir && intentos != 0)
            {
                calendario = Calendar.getInstance();
                //Obtiene la ultima cifra del a?o
                anio = calendario.get(calendario.YEAR);
                String s_anio = String.valueOf(anio);
                //s_anio = s_anio.substring(s_anio.length() - 1);

                //Obtiene el mes con dos digitos
                mes = calendario.get(calendario.MONTH) + 1;
                String s_mes = String.valueOf(mes);
                if(s_mes.length() == 1) s_mes = "0" + s_mes;

                //Obtiene el dia con dos digitos
                dia = calendario.get(calendario.DATE);
                String s_dia = String.valueOf(dia);
                if(s_dia.length() == 1) s_dia = "0" + s_dia;

                //Obtiene la hora con dos digitos
                hora = calendario.get(calendario.HOUR_OF_DAY);
                String s_hora = String.valueOf(hora);
                if(s_hora.length() == 1) s_hora = "0" + s_hora;

                //Obtiene los minutos con dos digitos
                minuto = calendario.get(calendario.MINUTE);
                String s_minuto = String.valueOf(minuto);
                if(s_minuto.length() == 1) s_minuto = "0" + s_minuto;

                //Obtiene los segundos en dos digitos
                segundo = calendario.get(calendario.SECOND);
                String s_segundo = String.valueOf(segundo);
                if(s_segundo.length() == 1) s_segundo = "0" + s_segundo;

                numexp = prefijo + codcli+"."+refcliente.trim()+"-"+s_anio.substring(2, 4);
                //numexp = prefijo + codcli + s_anio + " " + s_mes + s_dia + " " + s_hora + s_minuto + s_segundo;
                if (oSolicitudes.exists(numexp, conexion))
                {                  
                    intentos --;
                     try
                     {//ESPERAMOS 1 SEGUNDO ANTES DE conseguir el siguiente numero
                          Thread.currentThread().sleep(1000);                                     
                     }
                     catch (InterruptedException ie)
                     {                                                                                                
                                   
                     }                
                }   
                else seguir = false;
            }//while
            if (seguir) numexp = null;  //quiere decir que hemos hecho el nº max. de intentos y el expte. siempre existe ya en la B.D
            
        }//try
        catch(Exception e)
        {
            //System.out.println(e);
        }
        
        return numexp;
    }//numero_automatico       
    
    private String dametipoinm(String planta)
    {        
        String tipo = "";
        String uso = null;
        
        uso = XMLOrigen.recuperaValor("debi", "luso");
        if (uso != null)
        {
            uso = uso.trim();
            uso = uso.toUpperCase();
            if (uso.equals("ALMACEN-ESTACIONAMIENTO")) return "PZT";
            else if (uso.equals("RESIDENCIAL"))
            {
                if (isNumeric(planta))
                {
                    int nPlanta = Integer.parseInt(planta);
                    if (nPlanta == 0) return "VUT";
                    else return "VPT";
                }
                else
                {
                    if (planta == null || planta.trim().equals("")) return "VUT";
                    else return "VPT";
                }                
            }
            else if (uso.equals("INDUSTRIAL")) return "NUT";
            else if (uso.equals("OFICINAS")) return "OFT";
            else if (uso.equals("COMERCIAL")) return "LCT";
            else if (uso.equals("DEPORTIVO")) return "CDT";
            else if (uso.equals("ESPECTACULOS")) return "EDT";
            else if (uso.equals("OCIO Y HOSTELERIA"))    return "HTT";
            else if (uso.equals("SANIDAD Y BENEFICENCIA")) return "EDT";
            else if (uso.equals("CULTURA")) return "EDT";
            else if (uso.equals("RELIGISO")) return "EDT";
            else if (uso.equals("OBRAS DE URBANIZACIÓN Y JARDINERIA, SUELOS SIN EDIFICAR")) return "TRR";
            else if (uso.equals("EDIFICIO SINGULAR")) return "EDT";
            else if (uso.equals("ALMACEN AGRARIO")) return "FCR";
            else if (uso.equals("INDUSTRIAL AGRARIO")) return "FCR";
            else if (uso.equals("AGRARIO"))    return "FCR";
            else return "XXX";
        }
        else return "XXX";
        
    }//dametipoinm
    
    private boolean isNumeric(String cadena)
    {	
        try 
        {		
            if (cadena != null && !cadena.trim().equals(""))
            {
                Integer.parseInt(cadena);		
                return true;	
            }
            else return false;
        } 
        catch (NumberFormatException nfe)
        {		
            return false;	
        }
    }//isNumeric
    
    private void recuperaXY ()
    {         
        XMLOrigenXY = new xmldatostasacion.CrearXMLDatosTasacion(rutaXML+refcatastral+"_XY.xml"); 
        if (XMLOrigenXY.generadoXML())
        {
            String X = XMLOrigenXY.recuperaValor("geo", "xcen");
            String Y = XMLOrigenXY.recuperaValor("geo", "ycen");
            if (X != null && !X.equals("")) 
            {
                if (X.indexOf(".") != -1) oSolicitudes.equis = X.trim().substring(0,X.indexOf("."));
                else oSolicitudes.equis = X.trim();
            }
            if (Y != null && !Y.equals("")) 
            {
                if (Y.indexOf(".") != -1) oSolicitudes.ies = Y.trim().substring(0,Y.indexOf("."));
                else oSolicitudes.ies = Y.trim();
            }
        }
        XMLOrigenXY = null;
        
    }//recuperaXY
    
    private boolean recuperaDatosEncargo ()
    {
        boolean recuperadosDatosEncargo = false;
        String sConsulta = null;
        ResultSet rs = null;
        
        try
        {
            sConsulta = "SELECT * FROM encargos WHERE codcli = 701 AND referencia = '"+refcliente.trim()+"'";
            rs = Utilidades.Conexion.select(sConsulta,conexion);   
            if (rs.next())
            {                
                oficina = null;
                oficina = rs.getString("oficina");
                IDencargo = rs.getInt("ID");
                recuperadosDatosEncargo = true;
            }
            else recuperadosDatosEncargo = false;
        }
        catch (Exception e)
        {
            estadoOK = false;
            recuperadosDatosEncargo = false;
            descError = "Imposible recuperar datos de encargo para la referencia: "+refcliente+ ". Descripción: "+e.toString();
        }
        finally
        {
            return recuperadosDatosEncargo;
        }
    }//recuperaDatosEncargo
    
   
    
}//public class cargaReferencias 
