/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package herramientas;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;





public class documentacionUCI 
{
   private String rutaRECDOCtmp = "/data/informes/docUCI/"; 
   private Logger logger = Logger.getLogger(documentacionUCI.class);    
   
   public static void main(String[] args) 
    {        
        documentacionUCI carga = new documentacionUCI();        
        carga = null;        
        System.gc();
    }
    
    public documentacionUCI()
    {
        //java.io.File ficheros = new java.io.File("/data/informes/docUCI/");
         
        
        String rutaLog = "/data/informes/docUCI/log/";
        //LOG
           
        boolean bError = false;
        int iContador = 0;          
        String nombre = "";
        String objeto = "";
        Connection conexion = null;
        String sConsulta = "";
        ResultSet rs = null;
        String numexp = null;
        try
        {
            //FICHERO LOG4J
            PropertyConfigurator.configure(rutaLog + "Log4j.properties");   
            //conexion datos
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@oraserver01:1521:rvtn1");
            conexion.setAutoCommit(false);
            String[] nfichero;
            File ficheros = new File(rutaRECDOCtmp);                  
            java.io.File[] archivos = ficheros.listFiles();
            while(iContador<archivos.length && !bError)
            {
              numexp = "";
              objeto = "";              
              nombre = archivos[iContador].getName();
              
              
                  nfichero = nombre.split("-");
                  
                  if (nfichero.length > 0)
                  {
                      objeto = nfichero[nfichero.length-1];
                      nfichero = objeto.split("\\.");
                      objeto = nfichero[0];
                      if (objeto != null)
                      {
                          sConsulta = "SELECT s.numexp FROM solicitudes s JOIN refer r on (s.numexp = r.numexp) WHERE r.objeto = "+objeto+" ORDER BY fchenc DESC";
                          rs = Utilidades.Conexion.select(sConsulta, conexion);
                          if (rs.next())
                          {
                              numexp = rs.getString("numexp");
                          }
                          if (numexp != null)
                          {
                              //subimos documentacion
                              if (insertaDocumentacion(numexp,nombre))
                              {
                                System.out.println("Fichero: "+nombre+ " subido al expediente: "+numexp);
                                logger.info("Fichero: "+nombre+ " subido al expediente: "+numexp);
                              }
                              
                          }
                          else 
                          {
                              logger.error("No se puede obtener expediente para el documento: "+nombre);
                          }
                      }
                      
                  }
              
              iContador++;
            }//while
            conexion.close();
        }//try
        catch (Exception e)
        {
            System.out.println("Excepción: "+e.toString());
        }
        finally
        {
            try
            {
                if (conexion != null && !conexion.isClosed())
                {
                    conexion.close();
                }
            }
            catch (Exception e)
            {
                
            }
        }               
    }
    private boolean insertaDocumentacion(String numexp,String descripcion)
    {                        
        Integer numero = 0;        
        String mime_type = "";
        String pie = "";
        Integer tipo = 0;
        String ficheroBlob = "";
        boolean insertado = false;
        Connection conexionDocgrafica = null;
        try
        {                      
            //abrimos conexion
            conexionDocgrafica = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecinfx/valtecinfx@oraserver01:1521:rvtn1");
            conexionDocgrafica.setAutoCommit(false);  
            
            //fichero blob
            ficheroBlob = rutaRECDOCtmp+descripcion.trim();
            
            //informacion complementaria
            mime_type = "application/pdf";  
            tipo = 4323;
            pie = "Inf. complementaria";
            Statement stmtOracle = conexionDocgrafica.createStatement();
            ResultSet rsOracle;
                //SUBIMOS EL FICHERO A ORACLE.
                //Obtiene el siguiente numero de una secuencia que es la clave para la tabla con la documentacion grafica
                rsOracle = stmtOracle.executeQuery("select seq_docgrafica.nextval from dual");
                if (rsOracle.next()) numero = rsOracle.getInt(1);               
                stmtOracle.execute("insert into docgrafica values ('" + numexp + "'," + numero + ",'" + descripcion + "','" + mime_type + "',empty_blob(),'" + pie + "'," + tipo +")");

                //Se posiciona en esa fila
                 PreparedStatement p_stmtOracle = conexionDocgrafica.prepareStatement("select contenido from docgrafica where numero=" + numero + " for update");
                 rsOracle = p_stmtOracle.executeQuery();
                 if (rsOracle.next())
                 {

                      Blob informeBlob = rsOracle.getBlob(1);
                      OutputStream blobOutputStream = ((oracle.sql.BLOB)informeBlob).getBinaryOutputStream();
                      InputStream stream = new FileInputStream(ficheroBlob);
                      byte[] buffer2 = new byte[10*1024];
                      int num = 0;
                      while((num = stream.read(buffer2)) != -1) 
                      {
                             blobOutputStream.write(buffer2,0,num);
                      }
                      blobOutputStream.flush();
                      stream.close();
                      blobOutputStream.close();
                      rsOracle.close();

                      stmtOracle.execute("insert into idendoc (numero, usuario) values (" + numero + ", 'UCI')");                                                            
                      insertado = true;
                  }
                  stmtOracle.close();
                  conexionDocgrafica.commit();                                    
                  conexionDocgrafica.close();                    
        }
        catch (Exception e)
        {
            
            logger.error("Error al insertar en DOCGRAFIA el fichero: "+ficheroBlob+". Descripción: "+e.toString());
        }
        finally
        {
            try
            {
                if (conexionDocgrafica != null && !conexionDocgrafica.isClosed()) 
                {    conexionDocgrafica.rollback();
                     conexionDocgrafica.close();                
                }
            }
            catch (Exception e)
            {
                
            }  
            return insertado;
        }//finally
        
    }//insertaDocumentacion
}
