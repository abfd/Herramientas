
/*
 * pis1rep_prencargos.java
 *
 * Created on 29 de enero de 2007, 12:33
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package herramientas;

    import Utilidades.Consultas;
    import java.sql.*;
    import org.apache.log4j.Logger;
    import org.apache.log4j.PropertyConfigurator;
    import java.io.*;


public class pis1rep_prencargos {
    
    private Connection conexion = null;
    private String sRutaPropiedades = "/data/informes/crontabs/propiedades/crontabs.properties";
    private Utilidades.Propiedades propiedades = null;
    
    private boolean estadoOK = true;
    private int error = 0;
    private String descError = "";
    
    private ResultSet rsPis1rep = null;
    
    //LOG
    private Logger logger = Logger.getLogger(pis1rep_prencargos.class);
    
    // ---------- MAIN ----------------
     public static void main(String [] args ) {
        pis1rep_prencargos p1r = new pis1rep_prencargos();                
    }//main
    
    public pis1rep_prencargos() 
    {
        try
        {
            File pFichero = new File(sRutaPropiedades);            
            propiedades = new Utilidades.Propiedades(pFichero.getAbsolutePath());
            conexion = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexionVT"));
            conexion.setAutoCommit(false);
            String sConsulta = "select * from prencargos where numexpv is null and codcli = 729";
            ResultSet rsprencargos = Utilidades.Conexion.select(sConsulta,conexion);
            String numexp = "";
            String sConsulta2 = "";
            ResultSet rs2 = null;
            while (rsprencargos.next())
            {
                numexp = "";
                sConsulta2 = "select s.numexp from solicitudes s join refer r on (s.numexp = r.numexp) where s.codcli = 729 and r.procede = '"+rsprencargos.getString("numvtcopia")+"'";
                rs2 = Utilidades.Conexion.select(sConsulta2,conexion);
                if (rs2.next()) numexp = rs2.getString("numexp");
                rs2.close();
                rs2 = null;
                sConsulta2 = "update prencargos set numexpv = '"+numexp+"' where numpet = "+rsprencargos.getInt("numpet");
                if (Utilidades.Conexion.update(sConsulta2, conexion) == 1) conexion.commit();
                else conexion.rollback();
            }
            conexion.close();
            conexion = null;
        }
        catch (Exception e)
        {
            
        }
        finally
        {
            try
            {
                
            
            if (!conexion.isClosed() || conexion != null)
            {
                conexion.close();
                conexion = null;
            }
            }
            catch (Exception e)
            {
                
            }
        }
    }
    
    
    
    
    /** Creates a new instance of pis1rep_prencargos */
    /** Realiza el paso de datos de la tabla pis1rep a prencargos.*/
    public void pis1rep_prencargos() 
    {

        //INICIO DE LA EJECUCION
            int total_encargos = 0;
            int pretasaciones = 0;
            int procesados = 0;
            int no_procesados = 0;
            int borradas = 0;
            int no_borradas = 0;
            
        try
        {                           
            //Cargamos el fichero de propiedades
            File pFichero = new File(sRutaPropiedades);
            if (pFichero.exists())
            {
                estadoOK = true;
                error = 0;
                descError = "";
                propiedades = new Utilidades.Propiedades(pFichero.getAbsolutePath());
                String sRutaFicheroLog = propiedades.getValueProperty("RutaFicheroLog");
                PropertyConfigurator.configure(sRutaFicheroLog + "Log4j.properties");                
            }
            else
            {
                estadoOK = false;
                error = 1;
                descError = "Error: "+ Integer.toString(error);
                descError += " No se puede encontrar el fichero en la ruta especificada: "+ sRutaPropiedades.trim();  
                System.out.println(descError);
            }
            if (estadoOK)
            {
               logger.info("Inicio de la ejecución paso de datos PIS1REP --> PRENCARGOS");
               //conexin con la base de datos
               conexion = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"),propiedades.getValueProperty("IfxLogin"),propiedades.getValueProperty("IfxPass"));
               conexion.setAutoCommit(false);              
               String textoConsulta = preparaConsulta ("PIS1REP", "SELECT");
               //rsPis1rep = Utilidades.Conexion.select(textoConsulta,conexion);
               Statement rs = conexion.createStatement(ResultSet.HOLD_CURSORS_OVER_COMMIT,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
               rsPis1rep = rs.executeQuery(textoConsulta);
               
               while (rsPis1rep.next())
               {
                  total_encargos ++; 
                  if (rsPis1rep.getString("s1f2st") != "V" && rsPis1rep.getString("s1f2st") != "1")  //no es pretasación
                  {//pasamos el encargo a prencargos
                      
                    textoConsulta = preparaConsulta ("PIS1REP","INSERT");
                    if (Utilidades.Conexion.insert(textoConsulta,conexion) == 0)
                    {//no ha insertado nada
                        conexion.rollback();
                        no_procesados ++;
                        estadoOK = false;
                        error = 7;
                        descError = "Error: "+ Integer.toString(error);
                        descError += " Imposible insertar nuevo registro en la tabla PIS1REP--> oficina:  '" +rsPis1rep.getString("S1ABCD")+"' ";
                        descError +="numexpc: '"+rsPis1rep.getString("S1ALCD")+"' ";
                        descError +="agencia: '"+rsPis1rep.getString("S1HUCD")+"' ";
                        descError +="objeto: '" +rsPis1rep.getString("S1A5CD")+"' ";
                        descError +="num_tas: '"+rsPis1rep.getString("S1O5BR")+"' ";                                        
                        logger.info(descError);
                    }                       
                    else
                    {                        
                        textoConsulta = preparaConsulta ("PIS1REP","UPDATE");
                        if (Utilidades.Conexion.update(textoConsulta,conexion) == 0)
                        {// imposible actualizar estado en la pis1rep
                            conexion.rollback();
                            no_procesados ++;
                            estadoOK = false;
                            error = 8;
                            descError = "Error: "+ Integer.toString(error);
                            descError += " Imposible actualizar estado en la tabla PIS1REP--> oficina: '" +rsPis1rep.getString("S1ABCD")+"' ";
                            descError +="numexpc: '"+rsPis1rep.getString("S1ALCD")+"' ";
                            descError +="agencia: '"+rsPis1rep.getString("S1HUCD")+"' ";
                            descError +="objeto: '" +rsPis1rep.getString("S1A5CD")+"' ";
                            descError +="num_tas: '"+rsPis1rep.getString("S1O5BR")+"' ";                                         
                            logger.info(descError);
                        }
                        else
                        {//registro en PIS1REP actualizado
                            conexion.commit();
                            procesados ++;
                            estadoOK = true;
                            error = 0;
                            descError = "";                            
                        }                                                                        
                    }
                  } // no es pretasación
                  else
                  {// se trata de una pretasación y la borramos      
                      pretasaciones ++;
                      textoConsulta = preparaConsulta ("PIS1REP","DELETE");
                      if (Utilidades.Conexion.delete(textoConsulta,conexion) == 0)
                      {
                          conexion.rollback();
                          no_procesados ++;
                          no_borradas ++;
                          estadoOK = false;
                          error = 9;
                          descError = "Error: "+ Integer.toString(error);
                          descError += " Imposible borrar prencargo de la tabla PIS1REP--> oficina: '" +rsPis1rep.getString("S1ABCD")+"' ";
                          descError +="numexpc: '"+rsPis1rep.getString("S1ALCD")+"' ";
                          descError +="agencia: '"+rsPis1rep.getString("S1HUCD")+"' ";
                          descError +="objeto: '" +rsPis1rep.getString("S1A5CD")+"' ";
                          descError +="num_tas: '"+rsPis1rep.getString("S1O5BR")+"' "; 
                          logger.info(descError);
                      }
                      else
                      {
                          conexion.commit();
                          procesados ++;
                          borradas ++;
                          estadoOK = true;
                          error = 0;
                          descError = "";
                      }
                  }
                  
               }//while
               
               //rsPis1rep.close(); 
               //conexion.close();              
            }//estado
        }
        catch (FileNotFoundException fnfException)
        {
            estadoOK = false;
            error = 1;
            descError = "Error: "+ Integer.toString(error);
            descError += " Imposible cargar fichero de propiedades : " + fnfException.toString();
            logger.info(descError);
        }
        catch (IOException ioException)
        {
            estadoOK = false;
            error = 1;
            descError = "Error: "+ Integer.toString(error);
            descError += " Imposible cargar fichero de propiedades : " + ioException.toString();
            logger.info(descError);
        }
        catch (ClassNotFoundException cnfException)
        {
            estadoOK = false;
            error = 2;
            descError = "Error: "+ Integer.toString(error);
            descError += " Imposible cargar driver Informix : " + cnfException.toString();
            logger.info(descError);            
        }
        catch (SQLException sqlException)
        {
            estadoOK = false;
            error = 3;
            descError = "Error: "+ Integer.toString(error);
            descError += " Exceción sql generada : " + sqlException.toString();
            logger.info(descError);
        }
        catch (Exception e)
        {
            estadoOK = false;
            error = 5;
            descError = "Error: "+ Integer.toString(error);
            descError += " Excepcion General: " + e.toString();
            logger.info(descError);
            
        }
        finally
        {
            try
            {
                if (conexion != null && !conexion.isClosed()) conexion.close();
            }
            catch (SQLException sqlException)
            {
                estadoOK = false;
                error = 3;
                descError = "Error: "+ Integer.toString(error);
                descError += " Imposible cerrar conexión con la Base de Datos, error: " + sqlException.toString();
                logger.info(descError);
            }
            //Resumen de la ejecución:
            String estadistica = "Total procesados: " + total_encargos + " Número de Pretasaciones: " + pretasaciones;
            estadistica += " Procesados correctamente: " + procesados + " Procesados con error: " + no_procesados;
            estadistica += " Pretasaciones Borradas: " + borradas + " sin borrar: " + no_borradas;
            logger.info(estadistica);
            logger.info("Fin de la ejecución paso de datos PIS1REP --> PRENCARGOS");
            conexion = null;
            System.gc();
         }
            
                    
        }//pis1rep_prencargos
    
    public String preparaConsulta (String tabla, String tipoConsulta) throws SQLException
    {
        String textoConsulta = "";
        Consultas consulta = null;
        if (tabla.equals("PIS1REP"))
        {
            if (tipoConsulta.equals("SELECT"))
            {
               consulta = new Consultas (Consultas.SELECT) ;
               consulta.select("*");
               consulta.from(tabla);
               consulta.where("s1vzts","E",Consultas.VARCHAR);
               consulta.where("s1vzts","C",Consultas.OR,Consultas.VARCHAR);
               consulta.where("s1vzts","O",Consultas.OR,Consultas.VARCHAR);
               textoConsulta = consulta.getSql();
            }
            else if (tipoConsulta.equals("INSERT"))
            {
                    consulta = new Consultas (Consultas.INSERT);
                    consulta.from("prencargos");
                    consulta.insert("numexpv","",Consultas.VARCHAR);                    
                    consulta.insert("codcli","129",Consultas.INT);
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
                    consulta.insert("fchenc",rsPis1rep.getString("S1MBDT"),Consultas.VARCHAR);
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
                    consulta.insert("enviada","N",Consultas.VARCHAR);
                                                           
                    //generamos el nº de peticion
                    int nwNumpet = 0;
                    String sConsulta = "select max(numpet) from prencargos";
                    ResultSet rNumpet = Utilidades.Conexion.select(sConsulta,conexion);
                    if (rNumpet.next())
                    {
                        nwNumpet = rNumpet.getInt(1);
                    }
                    nwNumpet ++;
                    consulta.insert("numpet",Integer.toString(nwNumpet),Consultas.INT);
                    textoConsulta = consulta.getSql();
            }
            
            else if (tipoConsulta.equals("UPDATE"))
            {
                consulta = new Consultas (Consultas.UPDATE);
                consulta.from("pis1rep");
                consulta.set("s1vzts","P",Consultas.VARCHAR);                        
                consulta.where("s1abcd",rsPis1rep.getString("S1ABCD"),Consultas.VARCHAR);
                consulta.where("s1alcd",rsPis1rep.getString("S1ALCD"),Consultas.AND,Consultas.VARCHAR);
                consulta.where("s1hucd",rsPis1rep.getString("S1HUCD"),Consultas.AND,Consultas.VARCHAR);
                consulta.where("s1a5cd",rsPis1rep.getString("S1A5CD"),Consultas.AND,Consultas.VARCHAR);
                consulta.where("s1o5br",rsPis1rep.getString("S1O5BR"),Consultas.AND,Consultas.VARCHAR);
                textoConsulta = consulta.getSql();
            }
            else if (tipoConsulta.equals("DELETE"))
            {
                consulta = new Consultas (Consultas.DELETE);
                consulta.from("pis1rep");
                consulta.where("s1abcde",rsPis1rep.getString("S1ABCD"),Consultas.VARCHAR);
                consulta.where("s1alcd",rsPis1rep.getString("S1ALCD"),Consultas.AND,Consultas.VARCHAR);
                consulta.where("s1alcd",rsPis1rep.getString("S1HUCD"),Consultas.AND,Consultas.VARCHAR);
                consulta.where("s1alcd",rsPis1rep.getString("S1A5CD"),Consultas.AND,Consultas.VARCHAR);
                consulta.where("s1alcd",rsPis1rep.getString("S1O5BR"),Consultas.AND,Consultas.VARCHAR);
                consulta.where("s1alcd",rsPis1rep.getString("S1F2ST"),Consultas.AND,Consultas.VARCHAR);
                textoConsulta = consulta.getSql();   
            }
         }
        consulta = null;
        return textoConsulta;
        }//preparaConsulta
    
    
    
}

    

