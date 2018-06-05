/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package herramientas;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author Administrador
 */
public class generaOperacionesbsch 
{
    private  Connection conexion = null;        
    private  String sRutaPropiedades = "/data/informes/generaOperacionesbsch/propiedades/generaOperacionesbsch.properties";
    private Utilidades.Propiedades propiedades = null;
    
    // Fichero Log4j
    private Logger logger = Logger.getLogger(generaPrencargos.class);
    
    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException, SQLException 
    {
        generaOperacionesbsch nuevo = new generaOperacionesbsch();        
        System.gc();
    }
    

    public generaOperacionesbsch() throws FileNotFoundException, IOException, ClassNotFoundException, SQLException
    {
        File fPropiedades = new File(sRutaPropiedades);                     
        propiedades = new Utilidades.Propiedades(fPropiedades.getAbsolutePath());
        
        
        //FICHERO LOG4J
        PropertyConfigurator.configure(propiedades.getValueProperty("rutaLog") + "Log4j.properties");             
        
        
        conexion = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"),propiedades.getValueProperty("IfxLogin"),propiedades.getValueProperty("IfxPass"));
        conexion.setAutoCommit(false);
        
        String sConsulta1 = "";
        ResultSet rsoperacionesbsch = null;
        String numexp = "";
        String slafecha = "";
        SimpleDateFormat osdf;
        
        String sConsulta = "select numexp from solicitudes where fchenc between '26082010' and '10092010' and (codcli = 323 or codcli = 223)";
        ResultSet rssolicitudes = Utilidades.Conexion.select(sConsulta,conexion);
        while (rssolicitudes.next())
        { 
            numexp = "";
            numexp = rssolicitudes.getString("numexp");
            sConsulta1 = "SELECT * from operacionesbsch where numexp = '"+numexp.trim()+"'";
            rsoperacionesbsch = Utilidades.Conexion.select(sConsulta1,conexion);
            if (rsoperacionesbsch.next()) logger.info(numexp+ " EN OPERACIONESBSCH");
            else
            {
                 //- Inserción en OPERACIONESBSCH
                        String sNumExp = intermedioTrim(numexp);
                        //sTablaError = "Operacionesbsch";
                        Calendar lafecha = Calendar.getInstance();
                        slafecha = lafecha.get(lafecha.YEAR)+"-"+(lafecha.get(lafecha.MONTH)+1)+"-"+lafecha.get(lafecha.DATE);

                        java.util.Date hoy = new java.util.Date();
                        osdf = new SimpleDateFormat("dd-MM-yyyy");
                        String sFecha =  osdf.format(hoy);// +" "+slahora;                                              
                        //String slahora = lafecha.get(lafecha.HOUR_OF_DAY)+":"+lafecha.get(lafecha.MINUTE)+":"+lafecha.get(lafecha.SECOND);
                        //campos[9] = new java.text.SimpleDateFormat("dd-MM-yyyy").format(new java.util.Date());
                        String slahora= "TO_TIMESTAMP('"+new java.text.SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new java.util.Date())+"','YYYYMMDD HH24:MI:SS')";
                        String sOperacionesBsch = "INSERT INTO OPERACIONESBSCH (REFENVIO,NUMEXP,NUMOPERACION,TIPOPERACION,TIPOMENSAJE,CONTROL,POSTVENTA,ESTADO,FCHACUSE,HORACUSE)";
                        sOperacionesBsch+= " VALUES ('"+sNumExp+"','"+numexp+"',1,'REC','ASI',0,0,'R','"+sFecha+"',"+slahora+")";
                        if (Utilidades.Conexion.insert(sOperacionesBsch,conexion) == 0)
                        {//no ha insertado nada
                            conexion.rollback();
                            logger.error(numexp+" NO INSERTADO");
                        }
                        else
                        {
                            conexion.commit();
                            logger.info(numexp+" INSERTADO");
                        }
            }//no esta en operacionesbsch
        }//while
        conexion.close();
    }//generaOperacionesbsch
    public static String intermedioTrim(String sEntrada) {
        String sAux = "";
        char[] arrayCaracteres = sEntrada.toCharArray();
        for (int iContador=0;iContador<arrayCaracteres.length;iContador++) {
            if((arrayCaracteres[iContador])!=(' '))
                sAux+=arrayCaracteres[iContador];
        }//for
        return sAux;
    }//intermedioTrim
    
}
