/*
 * recuperadocumentoORCL.java
 *
 * Created on 23 de enero de 2007, 16:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package herramientas;

import java.sql.*;
import org.w3c.dom.*;
import java.io.*;


/**
 *
 * @author Administrador
 */
public class autentificacion {
    
    private String sRutaPropiedades = "/data/informes/autentificacion/autentificacion.properties";
    private String sRutaPdfCompleto = "";
    private String sRutaPdf = "";
    private String sRutaLog;
    private Utilidades.Propiedades propiedades = null;    
    private Connection conexion = null;    
    private Connection conexion2 = null;    
    private String sConsulta = "";
    
    public static void main (String [] args ) 
    {               
       autentificacion au = new autentificacion();
    }//main
    
    
    public  autentificacion()
    {
        try
        {
            cargaPropiedades();
            conexion = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"),propiedades.getValueProperty("IfxLogin"),propiedades.getValueProperty("IfxPass"));
            conexion.setAutoCommit(false);    
            conexion2 = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"),propiedades.getValueProperty("IfxLogin"),propiedades.getValueProperty("IfxPass"));
            conexion2.setAutoCommit(false);
            buscaPendientes();            
        }
        catch (FileNotFoundException fnfe)
        {
            Utilidades.Log.addText(sRutaLog,"ERROR 1: "+fnfe.toString());
        }
        catch (IOException ioe)
        {
            Utilidades.Log.addText(sRutaLog,"ERROR 2: "+ioe.toString());
        }
        catch (ClassNotFoundException cnfe)
        {
            Utilidades.Log.addText(sRutaLog,"ERROR 3: "+cnfe.toString());
        }
        catch (SQLException sqle)
        {
            Utilidades.Log.addText(sRutaLog,"ERROR 4: "+sqle.toString());
        }
        catch (InterruptedException ie)
        {
            Utilidades.Log.addText(sRutaLog,"ERROR 5: "+ie.toString()); 
        }
        finally
        {
            try
            {
                if (conexion != null && !conexion.isClosed()) conexion.close();                                
                if (conexion2 != null && !conexion2.isClosed()) conexion2.close();  
                System.gc();
            }
            catch (SQLException sqlException)
            {
                //IMPOSIBLE CERRAR CONEXION CON INFORMIX
            }
        }//finally
                
    }
    
    private void cargaPropiedades() throws FileNotFoundException, IOException
    {                   
       File fPropiedades = new File(sRutaPropiedades);
       if (fPropiedades.exists())
       {
             propiedades = new Utilidades.Propiedades(fPropiedades.getAbsolutePath());
             sRutaPdfCompleto = propiedades.getValueProperty("RutaPdfCompleto");
             sRutaPdf = propiedades.getValueProperty("RutaPdf");
             sRutaLog = propiedades.getValueProperty("RutaLog");                            
        }  
        else
        {
            throw new FileNotFoundException ("Imposible localizar fichero de propiedades en la ruta:  " + sRutaPropiedades.trim());                          
        }
    
    }
    
    /* Busca de la tabla de autentificación todos los que esten en estado 0
     */
    private void buscaPendientes() throws SQLException, InterruptedException, IOException
    {
       String numexp = "";
       String referencia = "";
       String ficheroCompleto = "";
       String ficheroDestino = "";
       File fpdf = null;   //fichero con el pdf
       File faut = null;   //fichero con el encargo de autentificación
       File fcompleto = null;
       Boolean generaPdfCompleto = false;
        
       ResultSet rsTotal = null;
       ResultSet rsRefer = null;
       sConsulta = "SELECT referencia FROM autentificacion WHERE estado  = 0";
       rsTotal = Utilidades.Conexion.select(sConsulta,conexion);            
       while (rsTotal.next()) 
       {
           referencia = rsTotal.getString("referencia");
           sConsulta = "SELECT numexp FROM refer where referencia ='"+referencia+"'";
           rsRefer = Utilidades.Conexion.select(sConsulta,conexion);            
           if (!rsRefer.next()) 
           {//NO SE LOCALIZA LA REFERENCIA ESTADO 3               
               Utilidades.Log.addText(sRutaLog,"INFO: no se localiza la referencia: "+referencia ); 
               sConsulta = "UPDATE autentificacion SET estado = 3 WHERE referencia = '"+referencia+"'";
               if (Utilidades.Conexion.update(sConsulta,conexion2) > 0) conexion2.commit();
               else conexion2.rollback();    
           }
           else
           {    numexp = rsRefer.getString("numexp");
                //1.- por nuestro numero de expte. buscamos el encargo de autentificación tipo 4321.
                documentacion(numexp,referencia,4321,0); 
                //2.- por nuestro numero de expte. buscamos el informe de la tasación tipo 4325
                documentacion(numexp,referencia,4325,0);
                //3.- comprobamos que ambos ficheros se ha descargado de oracle para generar el pdf completo.
                Thread.currentThread().sleep(10000); //establecemos una espera de 10 segundos para que la descarga sea correcta.
                fpdf = new File(sRutaPdf+referencia+"INF.pdf");
                faut = new File(sRutaPdf+referencia+"AUT.pdf");
                if (fpdf.exists() && faut.exists()) generaPdfCompleto = true;
                if (generaPdfCompleto)
                {
                    ficheroCompleto = sRutaPdf+referencia+"AUT.pdf"+" "+sRutaPdf+referencia+"INF.pdf";  
                    ficheroDestino = sRutaPdfCompleto+referencia+".pdf";
                    Utilidades.Archivos.unirVariosPdfLinux(ficheroDestino,ficheroCompleto);
                    Thread.currentThread().sleep(5000); //establecemos una espera de 5 segundos.
                    fcompleto = new File(sRutaPdfCompleto+referencia+".pdf");
                    if (fcompleto.exists())
                    {//TODO CORRECTO ESTADO 1
                        sConsulta = "UPDATE autentificacion SET estado = 1 WHERE referencia = '"+referencia+"'";
                        Utilidades.Log.addText(sRutaLog,"INFO: generado  pdf completo para la referencia: "+referencia);
                    }
                    else
                    {//IMPOSIBLE GENERAR PDF COMPLETO ESTADO 2
                        sConsulta = "UPDATE autentificacion SET estado = 2 WHERE referencia = '"+referencia+"'";
                        Utilidades.Log.addText(sRutaLog,"ERROR: imposible generar  pdf completo para la referencia: "+referencia);
                    }
                    if (Utilidades.Conexion.update(sConsulta,conexion2) > 0) conexion2.commit();
                    else conexion2.rollback();    
                    fpdf.delete();
                    faut.delete();
                }
                else
                {//FALTA EL INFORME O EL ENCARGO ESTADO 4
                    if (fpdf.exists()) fpdf.delete();
                    if (faut.exists()) faut.delete();
                    Utilidades.Log.addText(sRutaLog,"ERROR: falta INF/AUT para generar el pdf completo de la referencia: "+referencia);
                    sConsulta = "UPDATE autentificacion SET estado = 4 WHERE referencia = '"+referencia+"'";
                    if (Utilidades.Conexion.update(sConsulta,conexion2) > 0) conexion2.commit();
                    else conexion2.rollback();    
                    
                }
                                
           }//referencia encontrada                      
            rsRefer.close();
            numexp = "";
            referencia = "";     
            ficheroCompleto = "";
            ficheroDestino = "";
            fpdf = null;
            faut = null;
            fcompleto = null;
            rsRefer = null;
            generaPdfCompleto = false;
       }//while 
       rsTotal.close();
       rsTotal = null;
    }//buscaPendientes
    
    /** Creates a new instance of recuperadocumentoORCL */
    private void documentacion(String numexpv, String referenciap, int tipo, int numero) 
    {   
        
        try{            
            Connection conexOrcl = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecinfx/valtecinfx@192.168.3.7:1521:orcl");
            PreparedStatement  stmtOrcl = null;
            ResultSet rsOracle;            
            stmtOrcl = conexOrcl.prepareStatement("select contenido from docgrafica where numexp ='"+numexpv+"' and tipo ="+tipo);        
            rsOracle = stmtOrcl.executeQuery();
            if (rsOracle.next())
            {
                String archivo = "";
                if (tipo == 4325) archivo = sRutaPdf+referenciap+"INF.pdf";   //INFORME PDF
                else archivo = sRutaPdf+referenciap+"AUT.pdf";                //ENCARGO DE AUTENTIFICACION                                

                Blob b_contenido = (Blob) rsOracle.getObject(1);
                InputStream binput_stream = b_contenido.getBinaryStream();
                FileOutputStream boutput_stream = new FileOutputStream (archivo);
                byte[] b_buffer = new byte[2048];
                int i_bytes = 0;
                while ((i_bytes = binput_stream.read(b_buffer)) != -1)  boutput_stream.write(b_buffer,0,i_bytes);            
                binput_stream.close();
                boutput_stream.flush();
                boutput_stream.close();                        
            }
            conexOrcl.close();
        }
        catch (ClassNotFoundException cnfException)
        {
            Utilidades.Log.addText(sRutaLog,"ERROR 6: "+cnfException.toString()); 
        }
        catch (SQLException sqlException)
        {
            Utilidades.Log.addText(sRutaLog,"ERROR 7: "+sqlException.toString()); 
        }
        catch (FileNotFoundException fnfException)
        {
            Utilidades.Log.addText(sRutaLog,"ERROR 8: "+fnfException.toString()); 
        }
        catch (IOException ioException)
        {
            Utilidades.Log.addText(sRutaLog,"ERROR 9: "+ioException.toString()); 
        }
        
    }//documentacion
    
    public static String primera_visita (Connection conexion,String numexp)  
    {
        String primeraVisita = "";
        Boolean encontradaV = false;
        int puntero = numexp.length() - 1;
        int longitud = numexp.length();
        int numVisita = 0;
        
        try
        {
            while (!encontradaV && puntero > 0)
            {
                if (numexp.substring(puntero,puntero+1).equals("V"))
                {
                    encontradaV = true;
                    numVisita = Integer.parseInt(numexp.substring(puntero+1,longitud));                
                }
                else puntero --;
            }//while
            if (encontradaV)
            {
                //buscamos el original partiendo del numero sin V y buscamos hasta el nº de visita anterior al recibido como parametro.
                int numVisitaAnterior = 0;
                encontradaV = false;
                while (!encontradaV && numVisitaAnterior < numVisita)
                {
                    if (numVisitaAnterior == 0)
                    {
                        primeraVisita = numexp.substring(0,puntero-1);
                    }
                    else
                    {
                        primeraVisita = numexp.substring(0,puntero)+Integer.toString(numVisitaAnterior);                    
                    }


                        Statement stmt = conexion.createStatement();
                        ResultSet rs = stmt.executeQuery("select * from his_solicitudes where numexp='" + primeraVisita + "'");  
                        if (rs.next()) encontradaV = true;
                        else
                        {
                            rs = stmt.executeQuery("select * from solicitudes where numexp='" + primeraVisita + "'");  
                            if (rs.next()) encontradaV = true;
                            else
                            {
                                if (numVisitaAnterior == 0) numVisitaAnterior += 2; //para evitar que buscar la V1
                                else numVisitaAnterior ++;
                            }
                        }                                
                }//while

            }//encontradaV
        }//try
        catch (SQLException sqle)
        {
            
        }
        return primeraVisita;
    }//primera_visita
    
    
}//class autentificacion
 

