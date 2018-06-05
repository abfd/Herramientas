/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package herramientas;

import Utilidades.Consultas;
import java.io.*;
import java.sql.*;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author Administrador
 */
public class generaPrencargos 
{
    private  Connection conexion = null;        
    private  String sRutaPropiedades = "/data/informes/generaPrencargos/propiedades/generaPrencargos.properties";
    private Utilidades.Propiedades propiedades = null;
    
    // Fichero Log4j
    private Logger logger = Logger.getLogger(generaPrencargos.class);
    
    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException, SQLException 
    {
        //generaPrencargos nuevo = new generaPrencargos();        
        //generaPrencargos nuevo = new generaPrencargos("AAARYYAAOAABq3qAAE","129.76-17235-10 B");        
        generaPrencargos nuevo = new generaPrencargos("AAARYYAAOAABq3qAAF","129.76-17235-10 A");        
        System.gc();
    }
    
    public generaPrencargos(String id,String numexpv) throws FileNotFoundException, IOException, ClassNotFoundException, SQLException
    {
        File fPropiedades = new File(sRutaPropiedades);                     
        propiedades = new Utilidades.Propiedades(fPropiedades.getAbsolutePath());
        
        
        //FICHERO LOG4J
        PropertyConfigurator.configure(propiedades.getValueProperty("rutaLog") + "Log4j.properties");             
        
        
        conexion = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"),propiedades.getValueProperty("IfxLogin"),propiedades.getValueProperty("IfxPass"));
        conexion.setAutoCommit(false);
        
        
        
        ResultSet rsPrencargos = null;
        ResultSet rsPis1rep = null;
        String sConsulta2 = "";
        String sConsulta3 = "";
        String sConsulta4 = "";    
        String textoConsulta = "";
        String[] clave;
        String fecha = "";
        ResultSet rNumpet = null;
        String descError = "";
        Consultas consulta = null;      
        int nwNumpet = 0;
        
        
             sConsulta2 = "SELECT * FROM prencargos WHERE numexpv = '"+numexpv.trim()+"'";
             rsPrencargos = Utilidades.Conexion.select(sConsulta2,conexion);
             if (rsPrencargos.next()) logger.info("Expediente: "+numexpv+" YA EXISTE");
             else
             {//BUSCAMOS EN LA PIS1REP SUS DATOS
                sConsulta3 = "SELECT * FROM pis1rep WHERE rowid = '"+id.trim()+"'";
                rsPis1rep = Utilidades.Conexion.select(sConsulta3,conexion);
                if (rsPis1rep.next())
                {//insertamos en PRENCARGOS                    
                    consulta = new Consultas (Consultas.INSERT);
                    consulta.from("prencargos");
                    consulta.insert("numexpv",numexpv,Consultas.VARCHAR);
                    if (rsPis1rep.getString("S1APACOD").trim().equals("350258")) consulta.insert("codcli","429",Consultas.INT);  //BANCO CETELEM
                    else if (rsPis1rep.getString("S1KKTS") != null && rsPis1rep.getString("S1KKTS").equals("D")) consulta.insert("codcli","329",Consultas.INT); //DACIONES
                    else if (rsPis1rep.getString("S1KKTS") != null && rsPis1rep.getString("S1KKTS").equals("I")) consulta.insert("codcli","229",Consultas.INT); //INMUEBLES                    
                    else consulta.insert("codcli","129",Consultas.INT);  //RESTO
                    consulta.insert("oficina",rsPis1rep.getString("S1ABCD"),Consultas.VARCHAR);
                    consulta.insert("numexpc",rsPis1rep.getString("S1ALCD"),Consultas.VARCHAR);
                    consulta.insert("ag_obj",rsPis1rep.getString("S1HUCD"),Consultas.VARCHAR);
                    consulta.insert("objeto",rsPis1rep.getString("S1A5CD"),Consultas.VARCHAR);
                    consulta.insert("num_tas",rsPis1rep.getString("S1O5BR"),Consultas.VARCHAR);
                    consulta.insert("tipotas",rsPis1rep.getString("S1F2ST"),Consultas.VARCHAR);
                    consulta.insert("origenap",rsPis1rep.getString("S1EZTS"),Consultas.VARCHAR);
                    consulta.insert("autoprom",rsPis1rep.getString("S1Q2TS"),Consultas.VARCHAR);
                    consulta.insert("disp",rsPis1rep.getString("S1U0NB"),Consultas.VARCHAR);
                    consulta.insert("tipoinm",rsPis1rep.getString("S1AIST"),Consultas.VARCHAR);                                     
                    consulta.insert("vivvpo",rsPis1rep.getString("S1BKST"),Consultas.VARCHAR);
                    consulta.insert("sescritura ",rsPis1rep.getString("S1GOVA"),Consultas.INT);
                    consulta.insert("telefcto",rsPis1rep.getString("S1JRN1"),Consultas.VARCHAR);
                    consulta.insert("contacto",rsPis1rep.getString("S1LNXT"),Consultas.VARCHAR);
                    consulta.insert("coment",rsPis1rep.getString("S1Z1NA"),Consultas.VARCHAR);
                    consulta.insert("solici",rsPis1rep.getString("S1LPXT"),Consultas.VARCHAR);
                    consulta.insert("tiponif",rsPis1rep.getString("S1BLST"),Consultas.VARCHAR);
                    consulta.insert("nifsolici",rsPis1rep.getString("S1CHTX"),Consultas.VARCHAR);
                    consulta.insert("origenpet",rsPis1rep.getString("S1KKTS"),Consultas.VARCHAR);
                    consulta.insert("prestamo",rsPis1rep.getString("S1AJVA"),Consultas.INT);
                    consulta.insert("previa",rsPis1rep.getString("S1PJST"),Consultas.VARCHAR);
                    consulta.insert("fisica",rsPis1rep.getString("S1XXST"),Consultas.VARCHAR);
                    consulta.insert("codapa",rsPis1rep.getString("S1APACOD"),Consultas.VARCHAR);
                    consulta.insert("nombapa",rsPis1rep.getString("S1R9TX"),Consultas.VARCHAR);
                    consulta.insert("estado",rsPis1rep.getString("S1VZTS"),Consultas.VARCHAR);
                    clave = rsPis1rep.getString("S1MBDT").substring(0,10).split("-");
                    fecha = clave[2]+"-"+clave[1]+"-"+clave[0];                    
                    consulta.insert("fchenc",fecha,Consultas.VARCHAR);
                    consulta.insert("hora",rsPis1rep.getString("S1I2N1"),Consultas.VARCHAR);
                    consulta.insert("objeto_ant",rsPis1rep.getString("S1WBTS"),Consultas.VARCHAR);
                    consulta.insert("postalv",rsPis1rep.getString("S1HOVL"),Consultas.INT);
                    consulta.insert("provinv",rsPis1rep.getString("S1O8XT"),Consultas.VARCHAR);
                    consulta.insert("municiv",rsPis1rep.getString("S1O9XT"),Consultas.VARCHAR);
                    consulta.insert("localiv",rsPis1rep.getString("S1PAXT"),Consultas.VARCHAR);
                    consulta.insert("codsituv",rsPis1rep.getString("S1P9CD"),Consultas.VARCHAR);
                    consulta.insert("situacionv ",rsPis1rep.getString("S1PBXT"),Consultas.VARCHAR);
                    consulta.insert("tipoviav",rsPis1rep.getString("S1QECD"),Consultas.VARCHAR);
                    consulta.insert("callev",rsPis1rep.getString("S1PCXT"),Consultas.VARCHAR);
                    consulta.insert("numerov",rsPis1rep.getString("S1QACD"),Consultas.VARCHAR);
                    consulta.insert("escalerav",rsPis1rep.getString("S1QBCD"),Consultas.VARCHAR);
                    consulta.insert("plantav",rsPis1rep.getString("S1QCCD"),Consultas.VARCHAR);
                    consulta.insert("puertav",rsPis1rep.getString("S1QDCD"),Consultas.VARCHAR);
                    consulta.insert("espdirecc",rsPis1rep.getString("S1FXTX"),Consultas.VARCHAR);
                    consulta.insert("comercial",rsPis1rep.getString("S1AA7UA"),Consultas.VARCHAR);
                    consulta.insert("tlfcomercial  ",rsPis1rep.getString("S1E4XGA"),Consultas.INT);
                    consulta.insert("ag_procede",rsPis1rep.getString("S1EUCD"),Consultas.VARCHAR);
                    consulta.insert("numproce",rsPis1rep.getString("S1AYNB"),Consultas.INT);
                    consulta.insert("numvtcopia",rsPis1rep.getString("S1E4XHA"),Consultas.VARCHAR);
                    consulta.insert("ag_objproc ",rsPis1rep.getString("S1E4ZUA"),Consultas.VARCHAR);
                    consulta.insert("ob_proc",rsPis1rep.getString("S1E4ZVA"),Consultas.VARCHAR);
                    consulta.insert("canal",rsPis1rep.getString("S1I9N1"),Consultas.INT);
                    /*
                    if (baseDatos.equals("QT"))
                    {
                        //estos 4 campos son solo en QUALITAS
                        consulta.insert("distrito",rsPis1rep.getString("B3Q7A"),Consultas.VARCHAR);
                        consulta.insert("concelho",rsPis1rep.getString("B3Q6A"),Consultas.VARCHAR);
                        consulta.insert("freguesia",rsPis1rep.getString("B3Q5A"),Consultas.VARCHAR);
                        consulta.insert("localidade",rsPis1rep.getString("B3Q8A"),Consultas.VARCHAR);
                        //HASTA AQUI QUALITAS
                    }
                     * */
                    consulta.insert("enviada","E",Consultas.VARCHAR);   //ESTADO PROCESADAS.
                                                           
                    //generamos el n? de peticion
                    nwNumpet = 0;
                    sConsulta4 = "select max(numpet) from prencargos";
                    rNumpet = Utilidades.Conexion.select(sConsulta4,conexion);
                    if (rNumpet.next())
                    {
                        nwNumpet = rNumpet.getInt(1);
                    }
                    nwNumpet ++;
                    consulta.insert("numpet",Integer.toString(nwNumpet),Consultas.INT);
                    textoConsulta = consulta.getSql();
                    if (Utilidades.Conexion.insert(textoConsulta,conexion) == 0)
                    {//no ha insertado nada
                            conexion.rollback();
                                                        
                            descError = " Imposible insertar nuevo registro en la tabla PRENCARGOS--> oficina:  '" +rsPis1rep.getString("S1ABCD")+"' ";
                            descError +="numexpc: '"+rsPis1rep.getString("S1ALCD")+"' ";
                            descError +="agencia: '"+rsPis1rep.getString("S1HUCD")+"' ";
                            descError +="objeto: '" +rsPis1rep.getString("S1A5CD")+"' ";
                            descError +="num_tas: '"+rsPis1rep.getString("S1O5BR")+"' ";                                        
                            logger.error(descError);
                        }                       
                        else
                        {        
                            conexion.commit();
                            logger.info("Expediente: "+numexpv+ " INSERTADO EN PRENCARGOS");
                        }                        

                }
                else logger.error("ID DE REGISTRO:" + id.trim()+ "NO EN PIS1REP");
             }
        
        conexion.close();
        
    }
    public generaPrencargos() throws FileNotFoundException, IOException, ClassNotFoundException, SQLException
    {
        File fPropiedades = new File(sRutaPropiedades);                     
        propiedades = new Utilidades.Propiedades(fPropiedades.getAbsolutePath());
        
        
        //FICHERO LOG4J
        PropertyConfigurator.configure(propiedades.getValueProperty("rutaLog") + "Log4j.properties");             
        
        
        conexion = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"),propiedades.getValueProperty("IfxLogin"),propiedades.getValueProperty("IfxPass"));
        conexion.setAutoCommit(false);
        
        String numexpv = "";
        String oficina = "";
        String numexpc = "";
        String agencia = "";
        String objeto = "";
        String numtas = "";
        
        ResultSet rsPrencargos = null;
        ResultSet rsPis1rep = null;
        String sConsulta2 = "";
        String sConsulta3 = "";
        String sConsulta4 = "";    
        String textoConsulta = "";
        String[] clave;
        String fecha = "";
        ResultSet rNumpet = null;
        String descError = "";
        Consultas consulta = null;      
        int nwNumpet = 0;
        
        
        
        
        String sConsulta = "select * from pis9rep where v47ua between '26082010' and '10092010'";
        ResultSet rspis9rep = Utilidades.Conexion.select(sConsulta,conexion);
        while (rspis9rep.next())
        { 
             numexpv = "";
             oficina = "";
             numexpc = "";
             agencia = "";
             objeto = "";
             numtas = "";
             numexpv = rspis9rep.getString("e4xha");
             oficina = rspis9rep.getString("s9abcd");
             numexpc = rspis9rep.getString("s9alcd");
             agencia = rspis9rep.getString("s9hucd");
             objeto = rspis9rep.getString("s9a5cd");
             numtas = rspis9rep.getString("s9o5br");
                    
             
             
             
             sConsulta2 = "SELECT * FROM prencargos WHERE numexpv = '"+numexpv.trim()+"'";
             rsPrencargos = Utilidades.Conexion.select(sConsulta2,conexion);
             if (rsPrencargos.next()) logger.info("Expediente: "+numexpv+" YA EXISTE");
             else
             {//BUSCAMOS EN LA PIS1REP SUS DATOS
                sConsulta3 = "SELECT * FROM pis1rep WHERE s1abcd = '"+oficina.trim()+"' AND s1alcd = '"+numexpc+"'"+" AND s1hucd = '"+agencia+"' AND s1a5cd = '"+objeto+"' AND s1o5br = '"+numtas+"'";
                rsPis1rep = Utilidades.Conexion.select(sConsulta3,conexion);
                if (rsPis1rep.next())
                {//insertamos en PRENCARGOS                    
                    consulta = new Consultas (Consultas.INSERT);
                    consulta.from("prencargos");
                    consulta.insert("numexpv",numexpv,Consultas.VARCHAR);
                    if (rsPis1rep.getString("S1APACOD").trim().equals("350258")) consulta.insert("codcli","429",Consultas.INT);  //BANCO CETELEM
                    else if (rsPis1rep.getString("S1KKTS") != null && rsPis1rep.getString("S1KKTS").equals("D")) consulta.insert("codcli","329",Consultas.INT); //DACIONES
                    else if (rsPis1rep.getString("S1KKTS") != null && rsPis1rep.getString("S1KKTS").equals("I")) consulta.insert("codcli","229",Consultas.INT); //INMUEBLES                    
                    else consulta.insert("codcli","129",Consultas.INT);  //RESTO
                    consulta.insert("oficina",rsPis1rep.getString("S1ABCD"),Consultas.VARCHAR);
                    consulta.insert("numexpc",rsPis1rep.getString("S1ALCD"),Consultas.VARCHAR);
                    consulta.insert("ag_obj",rsPis1rep.getString("S1HUCD"),Consultas.VARCHAR);
                    consulta.insert("objeto",rsPis1rep.getString("S1A5CD"),Consultas.VARCHAR);
                    consulta.insert("num_tas",rsPis1rep.getString("S1O5BR"),Consultas.VARCHAR);
                    consulta.insert("tipotas",rsPis1rep.getString("S1F2ST"),Consultas.VARCHAR);
                    consulta.insert("origenap",rsPis1rep.getString("S1EZTS"),Consultas.VARCHAR);
                    consulta.insert("autoprom",rsPis1rep.getString("S1Q2TS"),Consultas.VARCHAR);
                    consulta.insert("disp",rsPis1rep.getString("S1U0NB"),Consultas.VARCHAR);
                    consulta.insert("tipoinm",rsPis1rep.getString("S1AIST"),Consultas.VARCHAR);                                     
                    consulta.insert("vivvpo",rsPis1rep.getString("S1BKST"),Consultas.VARCHAR);
                    consulta.insert("sescritura ",rsPis1rep.getString("S1GOVA"),Consultas.INT);
                    consulta.insert("telefcto",rsPis1rep.getString("S1JRN1"),Consultas.VARCHAR);
                    consulta.insert("contacto",rsPis1rep.getString("S1LNXT"),Consultas.VARCHAR);
                    consulta.insert("coment",rsPis1rep.getString("S1Z1NA"),Consultas.VARCHAR);
                    consulta.insert("solici",rsPis1rep.getString("S1LPXT"),Consultas.VARCHAR);
                    consulta.insert("tiponif",rsPis1rep.getString("S1BLST"),Consultas.VARCHAR);
                    consulta.insert("nifsolici",rsPis1rep.getString("S1CHTX"),Consultas.VARCHAR);
                    consulta.insert("origenpet",rsPis1rep.getString("S1KKTS"),Consultas.VARCHAR);
                    consulta.insert("prestamo",rsPis1rep.getString("S1AJVA"),Consultas.INT);
                    consulta.insert("previa",rsPis1rep.getString("S1PJST"),Consultas.VARCHAR);
                    consulta.insert("fisica",rsPis1rep.getString("S1XXST"),Consultas.VARCHAR);
                    consulta.insert("codapa",rsPis1rep.getString("S1APACOD"),Consultas.VARCHAR);
                    consulta.insert("nombapa",rsPis1rep.getString("S1R9TX"),Consultas.VARCHAR);
                    consulta.insert("estado",rsPis1rep.getString("S1VZTS"),Consultas.VARCHAR);
                    clave = rsPis1rep.getString("S1MBDT").substring(0,10).split("-");
                    fecha = clave[2]+"-"+clave[1]+"-"+clave[0];                    
                    consulta.insert("fchenc",fecha,Consultas.VARCHAR);
                    consulta.insert("hora",rsPis1rep.getString("S1I2N1"),Consultas.VARCHAR);
                    consulta.insert("objeto_ant",rsPis1rep.getString("S1WBTS"),Consultas.VARCHAR);
                    consulta.insert("postalv",rsPis1rep.getString("S1HOVL"),Consultas.INT);
                    consulta.insert("provinv",rsPis1rep.getString("S1O8XT"),Consultas.VARCHAR);
                    consulta.insert("municiv",rsPis1rep.getString("S1O9XT"),Consultas.VARCHAR);
                    consulta.insert("localiv",rsPis1rep.getString("S1PAXT"),Consultas.VARCHAR);
                    consulta.insert("codsituv",rsPis1rep.getString("S1P9CD"),Consultas.VARCHAR);
                    consulta.insert("situacionv ",rsPis1rep.getString("S1PBXT"),Consultas.VARCHAR);
                    consulta.insert("tipoviav",rsPis1rep.getString("S1QECD"),Consultas.VARCHAR);
                    consulta.insert("callev",rsPis1rep.getString("S1PCXT"),Consultas.VARCHAR);
                    consulta.insert("numerov",rsPis1rep.getString("S1QACD"),Consultas.VARCHAR);
                    consulta.insert("escalerav",rsPis1rep.getString("S1QBCD"),Consultas.VARCHAR);
                    consulta.insert("plantav",rsPis1rep.getString("S1QCCD"),Consultas.VARCHAR);
                    consulta.insert("puertav",rsPis1rep.getString("S1QDCD"),Consultas.VARCHAR);
                    consulta.insert("espdirecc",rsPis1rep.getString("S1FXTX"),Consultas.VARCHAR);
                    consulta.insert("comercial",rsPis1rep.getString("S1AA7UA"),Consultas.VARCHAR);
                    consulta.insert("tlfcomercial  ",rsPis1rep.getString("S1E4XGA"),Consultas.INT);
                    consulta.insert("ag_procede",rsPis1rep.getString("S1EUCD"),Consultas.VARCHAR);
                    consulta.insert("numproce",rsPis1rep.getString("S1AYNB"),Consultas.INT);
                    consulta.insert("numvtcopia",rsPis1rep.getString("S1E4XHA"),Consultas.VARCHAR);
                    consulta.insert("ag_objproc ",rsPis1rep.getString("S1E4ZUA"),Consultas.VARCHAR);
                    consulta.insert("ob_proc",rsPis1rep.getString("S1E4ZVA"),Consultas.VARCHAR);
                    consulta.insert("canal",rsPis1rep.getString("S1I9N1"),Consultas.INT);
                    /*
                    if (baseDatos.equals("QT"))
                    {
                        //estos 4 campos son solo en QUALITAS
                        consulta.insert("distrito",rsPis1rep.getString("B3Q7A"),Consultas.VARCHAR);
                        consulta.insert("concelho",rsPis1rep.getString("B3Q6A"),Consultas.VARCHAR);
                        consulta.insert("freguesia",rsPis1rep.getString("B3Q5A"),Consultas.VARCHAR);
                        consulta.insert("localidade",rsPis1rep.getString("B3Q8A"),Consultas.VARCHAR);
                        //HASTA AQUI QUALITAS
                    }
                     * */
                    consulta.insert("enviada","E",Consultas.VARCHAR);   //ESTADO PROCESADAS.
                                                           
                    //generamos el n? de peticion
                    nwNumpet = 0;
                    sConsulta4 = "select max(numpet) from prencargos";
                    rNumpet = Utilidades.Conexion.select(sConsulta4,conexion);
                    if (rNumpet.next())
                    {
                        nwNumpet = rNumpet.getInt(1);
                    }
                    nwNumpet ++;
                    consulta.insert("numpet",Integer.toString(nwNumpet),Consultas.INT);
                    textoConsulta = consulta.getSql();
                    if (Utilidades.Conexion.insert(textoConsulta,conexion) == 0)
                    {//no ha insertado nada
                            conexion.rollback();
                                                        
                            descError = " Imposible insertar nuevo registro en la tabla PRENCARGOS--> oficina:  '" +rsPis1rep.getString("S1ABCD")+"' ";
                            descError +="numexpc: '"+rsPis1rep.getString("S1ALCD")+"' ";
                            descError +="agencia: '"+rsPis1rep.getString("S1HUCD")+"' ";
                            descError +="objeto: '" +rsPis1rep.getString("S1A5CD")+"' ";
                            descError +="num_tas: '"+rsPis1rep.getString("S1O5BR")+"' ";                                        
                            logger.error(descError);
                        }                       
                        else
                        {        
                            conexion.commit();
                            logger.info("Expediente: "+numexpv+ " INSERTADO EN PRENCARGOS");
                        }                        

                }
                else logger.error("s1abcd = '"+oficina.trim()+" AND s1alcd = '"+numexpc+"'"+" AND s1hucd = '"+agencia+"' AND s1a5cd = '"+objeto+"' AND s1o5br = '"+numtas+" NO EN PIS1REP");
             }
        }//while
        conexion.close();
        
    }
}
