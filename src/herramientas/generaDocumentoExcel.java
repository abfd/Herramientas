/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package herramientas;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 *
 * @author Administrador
 */
public class generaDocumentoExcel 
{
    
    //conexiones
    private Connection conexion = null;  
    
    //fichero de propiedades
    private String ficheroPropiedadesName = "herramientas.properties";
    private java.util.Properties fpropiedades = new java.util.Properties();
    
    //conexiones
    //private Connection conexion = null;
    private Connection conexionVT = null;
    private Connection docgraficax = null;
    
    //correo 
    private String servidorCorreo = null;
    private String correoOrigen = null;
    private String correoError = null;    
    private String correoCCO = null;  
    
    //log
    private String rutaLog = null;    
    private Logger logger = Logger.getLogger(generaDocumentoExcel.class);
    
    //atributos de estado
    private boolean estadoOK =  true;
    private int error = 0;
    private String descError = "";
    
    //
    private String sConsulta = "";
    
    public static void main(String[] args) 
    {
        generaDocumentoExcel carga = new generaDocumentoExcel();
        carga = null;
        System.gc();
    }
    
 
    public generaDocumentoExcel() 
    {
        
        int contadorFila = 0;
        int contadorCol = 0;
        
        try
        {
            inicializaEstado();
            cargaPropiedades();
            if (estadoOK)
            {
                // Se crea el libro
                HSSFWorkbook libro = new HSSFWorkbook();

                // Se crea una hoja dentro del libro
                HSSFSheet hoja = libro.createSheet();

                // Se crea una fila dentro de la hoja
                HSSFRow fila = null;

                // Se crea una celda dentro de la fila
                HSSFCell celda = null;

                // Se crea el contenido de la celda y se mete en ella.
                //HSSFRichTextString texto = new HSSFRichTextString("hola mundo");
                       
                //fila 0 -- descripcion de las columnas.
                fila = hoja.createRow(contadorFila);
                //6 descripciones 
                celda = fila.createCell((short) 0);
                celda.setCellValue("Expediente");
                celda = fila.createCell((short) 1);
                celda.setCellValue("Fch. Encargo");
                celda = fila.createCell((short) 2);
                celda.setCellValue("Nº Solicitud");
                celda = fila.createCell((short) 3);
                celda.setCellValue("Expediente/Cod. Promo");
                celda = fila.createCell((short) 4);
                celda.setCellValue("Sociedad");
                celda = fila.createCell((short) 5);
                celda.setCellValue("Unidad Registral");
                
                conexion = Utilidades.Conexion.getConnection(fpropiedades.getProperty("url_datos"));
                conexion.setAutoCommit(false);                              
            
                sConsulta = "select numexp ,referencia , objeto , to_char(api,'0000') ,";
                sConsulta += "CASE estado WHEN '301' THEN 'Envio Tasación' WHEN '303' THEN 'Envio Revisión' END,"; 
                sConsulta += "to_char (fchenvio,'DD-MM-YYYY')||'/'||to_char (horaenvio,'HH24:MI:SS') ,";
                sConsulta += "to_char (fchacuse,'DD-MM-YYYY')||'/'||to_char (horacuse,'HH24:MI:SS')  ,";
                sConsulta += "CASE resultado WHEN '00' THEN 'Sin error' WHEN '01' THEN 'Con error' END ";
                sConsulta += "FROM operclientes NATURAL JOIN refer WHERE cliente in (255,355) AND  fchenvio between '05072011' and '05072011' ";
                sConsulta += "and estado in ('301','303') order by fchenvio,numexp";
                                
                java.sql.ResultSet rs = Utilidades.Conexion.select(sConsulta,conexion);    
                while (rs.next())
                {//volcamos los datos en excel                
                    contadorFila ++;   //por cada registro añadimos una fila
                    fila = hoja.createRow(contadorFila);
                    //celda.setCellValue("Expediente");
                    celda = fila.createCell((short) 0);
                    celda.setCellValue(rs.getString(1));
                    //celda.setCellValue("Fch. Encargo");
                    celda = fila.createCell((short) 1);
                    celda.setCellValue(rs.getString(2));
                    //celda.setCellValue("Nº Solicitud");
                    celda = fila.createCell((short) 2);
                    celda.setCellValue(rs.getString(3));
                    //celda.setCellValue("Expediente/Cod. Promo");
                    celda = fila.createCell((short) 3);
                    celda.setCellValue(rs.getString(4));
                    //celda.setCellValue("Sociedad");
                    celda = fila.createCell((short) 4);
                    celda.setCellValue(rs.getString(5));
                    //celda.setCellValue("Unidad Registral");
                    celda = fila.createCell((short) 5);
                    celda.setCellValue(rs.getString(6));
                    
                }
                conexion.close();
                // Se salva el libro.
                try 
                {
                    FileOutputStream elFichero = new FileOutputStream("/data/informes/holamundo.xls");
                    libro.write(elFichero);
                    elFichero.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }//if estadoOK
        }//try
        catch (Exception e)
        {
           estadoOK = false;
           descError = "Excepción en la generación del Excel. Descripción: "+e.toString();
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
                
           } 
           catch (SQLException sqlException) 
           {
                
           }
        }//finally
    }//generaDocumentoExcel
    private void cargaPropiedades()
    {   
        boolean cargado = true;
        
        try
        {                    
            inicializaEstado();
            fpropiedades.load(this.getClass().getResourceAsStream(ficheroPropiedadesName));
            
            //rutaLog = fpropiedades.getProperty("rutaLogREC");   
            servidorCorreo = fpropiedades.getProperty("servidorCorreo");
            correoOrigen = fpropiedades.getProperty("mailOrigen");
            correoError = fpropiedades.getProperty("mailError");
            correoCCO = fpropiedades.getProperty("mailCCO");
            
            //FICHERO LOG4J
            PropertyConfigurator.configure(rutaLog + "Log4j.properties");                         
            
        }//try
        catch (FileNotFoundException fnfe)
        {
            estadoOK = false;
            descError = fnfe.toString();
        }
        catch (IOException ioe)
        {
            estadoOK = false;
            descError = ioe.toString();
        }
        catch (Exception e)
        {
            estadoOK = false;
            descError = e.toString();
        }        
    } //InicializaDatos
    
    private void inicializaEstado()
    {
        estadoOK = true;
        error = 0;
        descError = "";   
    }//inicializaEstado
}
