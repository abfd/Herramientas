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
public class recuperadocumentoORCL {
    
    public static void main(String [] args ) {
       recuperadocumentoORCL  doc = new recuperadocumentoORCL("103.01822446500067-15","STA",301,18377770);                
    }//main
    
    
    
    
    /** Creates a new instance of recuperadocumentoORCL */
    public recuperadocumentoORCL(String numexpv, String descripcion, int tipo, int numero) 
    {   
        
        try{
            String RutaSalidaXML = "/data/informes/tmp/";
            Connection conexOrcl = Utilidades.Conexion.getConnectionDocgraficaProduccion();
            PreparedStatement  stmtOrcl = null;
            ResultSet rsOracle;
            //stmtOrcl = conexOrcl.prepareStatement("select contenido from docgrafica where numexp ='"+numexpv+"' and descripcion = '"+descripcion+"' and tipo ="+tipo+" and numero = "+numero);        
            stmtOrcl = conexOrcl.prepareStatement("select contenido from docgrafica where  numero = "+numero);        
            rsOracle = stmtOrcl.executeQuery();
            if (rsOracle.next())
            {
                String archivo = RutaSalidaXML+numexpv.trim()+"_"+descripcion+".xml";
                /*File fichero = new File(archivo);
                if (fichero.exists()) fichero.delete();*/

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
            
        }
        catch (SQLException sqlException)
        {
            
        }
        catch (FileNotFoundException fnfException)
        {
            
        }
        catch (IOException ioException)
        {
            
        }
        catch (Exception e)
        {
            
        }
    }//recuperadocumentoORCL
    
    
    
    
    
    }
 

