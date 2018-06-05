/*
 * insertapis7cpp.java
 *
 * Created on 22 de marzo de 2007, 16:06
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package herramientas;

import Utilidades.Consultas;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;

/**
 *
 * @author Administrador
 */
public class insertapis7cpp {
        
    private  Connection conexion = null;
    private  Connection conexpis7cpp = null;
    private  String sRutaPropiedades = "/data/informes/insertapis7cpp/propiedades/insertapis7cpp.properties";
    private  String sRutaLog = "/data/informes/insertapis7cpp/log/";
    private  String sNombreLog = "insertapis7cpp.log";    
    private Utilidades.Propiedades propiedades = null;
    private String LineaLog="";
    
    private String s7abcd;
    private String s7alcd;
    private String s7hucd;
    private String s7a5cd;
    private String s7o5br;
    private String v9dza;
    private String s7sxbr;
    private String s7mgdt;
    private String s7mhdt;
    private String s7fjna;
    private String s7ruts;
    
    public static void main(String [] args ) {
        insertapis7cpp o = new insertapis7cpp();        
    }//main
    
    
    
    public insertapis7cpp() 
    {
        try
        {
            File fPropiedades = new File(sRutaPropiedades);                     
            propiedades = new Utilidades.Propiedades(fPropiedades.getAbsolutePath());
            String consulCircuns1 = "";
            Consultas consulta;
            String textoConsulta = "";
            String numexp = "";
            String sConsulta = "";
            conexion = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"),propiedades.getValueProperty("IfxLogin"),propiedades.getValueProperty("IfxPass"));
            conexion.setAutoCommit(false);
            //numexp = "229.36-15484-07 A"; //para pruebas
            //sConsulta = "select numexp from solicitudes where codest = 3 and codcli = 129 and numexp = '"+numexp.trim()+"'";  //para pruebas
            sConsulta = "select numexp from solicitudes where codest = 3 and codcli = 129";
            ResultSet rssolicitudes = Utilidades.Conexion.select(sConsulta,conexion);
            while (rssolicitudes.next())
            { 
                numexp = rssolicitudes.getString("numexp").trim();
                inicializa();
                sConsulta = "select * from prencargos where numexpv = '" + numexp.trim()+"'";
                ResultSet rsprencargos = Utilidades.Conexion.select(sConsulta,conexion);
                if (rsprencargos.next())
                {
                    s7abcd = rsprencargos.getString("oficina").trim();
                    s7alcd = rsprencargos.getString("numexpc").trim();
                    s7hucd = rsprencargos.getString("ag_obj").trim();
                    s7a5cd = rsprencargos.getString("objeto").trim();
                    s7o5br = rsprencargos.getString("num_tas").trim();
                    sConsulta = "select * from incidenc where numexp = '"+numexp.trim()+"' and fchsol is null";
                    ResultSet rsincidenc = Utilidades.Conexion.select(sConsulta,conexion);
                    while (rsincidenc.next())
                    {           
                        v9dza = Integer.toString(rsincidenc.getInt("numero"));
                        consulta = new Consultas (Consultas.SELECT) ;
                        consulta.select("*");
                        consulta.from("PIS7CPP");
                        consulta.where("S7ABCD",s7abcd,Consultas.VARCHAR);
                        consulta.where("S7ALCD",s7alcd,Consultas.AND,Consultas.INT);
                        consulta.where("S7HUCD",s7hucd,Consultas.AND,Consultas.VARCHAR);
                        consulta.where("S7A5CD",s7a5cd,Consultas.AND,Consultas.VARCHAR);
                        consulta.where("S7O5BR",s7o5br,Consultas.AND,Consultas.INT);
                        consulta.where("V9DZA",v9dza,Consultas.AND,Consultas.INT);
                        textoConsulta = consulta.getSql();
                        Statement rs = conexion.createStatement();
                        ResultSet rspis7cpp = rs.executeQuery(textoConsulta);
                        if (rspis7cpp.next()) ;   //existe la incidencia. No hacemos nada                        
                        else 
                        {//debemos insertar en pis7cpp                          
                          s7sxbr = rsincidenc.getString("codinc2");
                          s7mgdt = rsincidenc.getString("fchinc");
                          //descripción de la incidencia
                          consulCircuns1 = "SELECT texto FROM circuns1 WHERE cod_cir1 = " + Integer.parseInt(s7sxbr) + " AND tipo = 'I' AND idioma = 'e'";
                          ResultSet res = Utilidades.Conexion.select(consulCircuns1,conexion);
                          if (res.next()) 
                          {
                              s7fjna = res.getString("texto").trim()+" "+rsincidenc.getString("texinc").trim();
                              if (s7fjna.trim().length() > 256) s7fjna = rsincidenc.getString("texinc").trim();                              
                          }
                          //INSERTAMOS EN LA PIS7CPP
                          consulta = new Consultas (Consultas.INSERT);
                          consulta.from("PIS7CPP");
                          consulta.insert("s7abcd",s7abcd,Consultas.VARCHAR);
                          consulta.insert("s7alcd",s7alcd,Consultas.INT);
                          consulta.insert("s7hucd",s7hucd,Consultas.VARCHAR);
                          consulta.insert("s7a5cd",s7a5cd,Consultas.VARCHAR);
                          consulta.insert("s7o5br",s7o5br,Consultas.INT);
                          consulta.insert("v9dza",v9dza,Consultas.INT);
                          consulta.insert("s7sxbr",s7sxbr,Consultas.INT);
                          consulta.insert("s7mgdt",s7mgdt,Consultas.VARCHAR);
                          consulta.insert("s7mhdt",s7mhdt,Consultas.VARCHAR);
                          consulta.insert("s7fjna",s7fjna.trim(),Consultas.VARCHAR);
                          consulta.insert("s7ruts",s7ruts,Consultas.VARCHAR);
                          textoConsulta = consulta.getSql();
                          conexpis7cpp = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"),propiedades.getValueProperty("IfxLogin"),propiedades.getValueProperty("IfxPass"));
                          conexpis7cpp.setAutoCommit(false);
                           if (Utilidades.Conexion.insert(textoConsulta,conexpis7cpp) == 0)
                           {
                                conexpis7cpp.rollback();
                                LineaLog = "ERROR INSERCIÓN INCIDENCIA: expediente: "+numexp.trim()+ "Nº de incidencia: "+v9dza.trim();
                                Utilidades.Log.addText(sRutaLog,sNombreLog,"",LineaLog.trim(),"","",Utilidades.Log.INFORMACION);
                           }
                           else
                           {
                               conexpis7cpp.commit();
                               LineaLog = "INSERCIÓN CORRECTA DE INCIDENCIA: expediente: "+numexp.trim()+ "Nº de incidencia: "+v9dza.trim();
                               Utilidades.Log.addText(sRutaLog,sNombreLog,"",LineaLog.trim(),"","",Utilidades.Log.INFORMACION);
                           }
                           conexpis7cpp.close();
                        }
                    }//while incidencias
                    
                }
                else
                {
                    LineaLog = "No se ha encontrado en prencargos la tasación: " + numexp.trim();
                    Utilidades.Log.addText(sRutaLog,sNombreLog,"",LineaLog.trim(),"","",Utilidades.Log.INFORMACION);
                }
                
            } //while solicitudes
        }//try
        catch (ClassNotFoundException cnfe)
        {
            LineaLog = cnfe.toString();
            Utilidades.Log.addText(sRutaLog,sNombreLog,"",LineaLog.trim(),"","",Utilidades.Log.ERROR);
        }
        catch (SQLException sqle)
        {
            LineaLog = sqle.toString();
            Utilidades.Log.addText(sRutaLog,sNombreLog,"",LineaLog.trim(),"","",Utilidades.Log.ERROR);
        }
        catch (FileNotFoundException fnfe)
        {           
            LineaLog = fnfe.toString(); 
            Utilidades.Log.addText(sRutaLog,sNombreLog,"",LineaLog.trim(),"","",Utilidades.Log.ERROR);
        }
        catch (IOException ioe)
        {
            LineaLog = ioe.toString();
            Utilidades.Log.addText(sRutaLog,sNombreLog,"",LineaLog.trim(),"","",Utilidades.Log.ERROR);
        }
         finally
        {
            try
            {
                if (conexion != null && !conexion.isClosed()) conexion.close();
                if (conexpis7cpp != null && !conexpis7cpp.isClosed()) conexpis7cpp.close();
            }
            catch (SQLException sqlException)
            {
                LineaLog = "Imposible cerrar conexión con Informix";
                Utilidades.Log.addText(sRutaLog,sNombreLog,"",LineaLog.trim(),"","",Utilidades.Log.ERROR);
            }
        }
        
    } //insertapis7cpp
    
    private void inicializa()
    {
        s7abcd = "";
        s7alcd = "";
        s7hucd = "";
        s7a5cd = "";
        s7o5br = "";
        v9dza  = "";
        s7sxbr = "";
        s7mgdt = "";
        s7mhdt = "1940-01-01";
        s7fjna = "";
        s7ruts = "S";
    }
    
}
