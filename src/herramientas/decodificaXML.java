/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package herramientas;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 *
 * @author Administrador
 */
public class decodificaXML 
{
    //private  Connection conexionOracle = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecinfx/valtecinfx@192.168.3.215:1521:rvtn");
    //private  Connection conexionDatos = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@192.168.3.215:1521:rvtn");
    
 
    public static void main(String [] args ) throws ParserConfigurationException, SAXException, IOException, Exception 
    {
        decodificaXML env = new decodificaXML("100.TS2012004509-001-12",600,"4311TS2012004509CEX1.xml");        
        //decodificaXML env = new decodificaXML();
    }//main
    
    public decodificaXML() throws FileNotFoundException, IOException
    {
        String rutaXML = "/data/informes/decodificaXML/";
        File recuperado = new File(rutaXML+"4311TS2010009503ENT1.zip");
        FileOutputStream fos = null;
        File ficheroZIP = new File(rutaXML+"4311TS2010009503ENT1.zip");              
        String zipEntryName = "";
        File fzip = new File(rutaXML+"4311TS2010009503ENT1.zip");
        ZipFile elZip=new java.util.zip.ZipFile(fzip);                                                                                 
        ZipInputStream zip = new ZipInputStream(new FileInputStream(ficheroZIP));
        for (Enumeration entradas=elZip.entries();entradas.hasMoreElements();) 
        { //for (3)
                     //3.- descomprimimos cada una de las entradas.
                       zipEntryName=((ZipEntry)entradas.nextElement()).getName();
                       fos = new FileOutputStream(rutaXML+ "4311TS2010009503ENT1.PDF");
                                    
                       int buff1;
                       byte [] buffer1 = new byte [1024];
                       //vamos descomprimiento los ficheros que llegan en el zip
                       zip.getNextEntry();                                        
                       while ((buff1 = zip.read(buffer1)) != -1) 
                       {
                              if (buff1 < 1024) 
                              {
                                  byte [] ultimo1 = new byte [buff1];
                                  for (int p = 0; p < buff1; p++) 
                                  {
                                      ultimo1[p] = buffer1[p];
                                  }
                                  fos.write(ultimo1);
                              } else   fos.write(buffer1);                                            
                       }
                       fos.flush();
                       fos.close();
             }//for del zip     
                         
    }
    public decodificaXML(String numexp,int cliente,String ficheroXML) throws ParserConfigurationException, SAXException, IOException, Exception
    {        
        String rutaXML = "/data/informes/decodificaXML/";
        String [] ficheroPDF= ficheroXML.split("\\.");
        String rutaSalidaZIP = rutaXML+ficheroPDF[0]+".zip";
        FileOutputStream fos = null;
        File recuperado = null;
        File ficheroZIP = null;
        String zipEntryName = "";
        File fzip = null;
        ZipFile elZip = null;        
        ZipInputStream zip = null;
        
        if (cliente == 600)
        {//SEGIPSA
            File fich = new File(rutaXML+ficheroXML);
            //Construye el documento XML a partir del fichero
            DocumentBuilder constructor = null;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            Document xmlDoc = null;
            constructor = dbf.newDocumentBuilder();
            xmlDoc = constructor.parse(fich);    
            NodeList lista_fic = xmlDoc.getElementsByTagName("FICHERO");
            for (int i = 0; i < lista_fic.getLength(); i++) 
            {
                //if (i == 3)
                //{
                    
                    rutaSalidaZIP = rutaXML+"fichero"+Integer.toString(i)+".zip";
                    Node fic = lista_fic.item(i);
                    Node texto_fic = fic.getFirstChild();
                    String fichero = texto_fic.getNodeValue();      
                    
                    Base64 Base64= new Base64();
                    byte[] array_de_bytes = Base64.decode(fichero.trim());
                    
                    
                    recuperado = new File(rutaSalidaZIP);
                    fos = new FileOutputStream(recuperado);
                    int nread = array_de_bytes.length;
                    fos.write(array_de_bytes,0,nread);
                    fos.flush();
                    fos.close();
                    /*
                    if (recuperado.exists())
                    {//descomprimimos y subimos a la base de datos el fichero pdf recuperado.
                       
                        //ficheroZIP = new File(rutaXML+"4311TS2010009503ENT1.zip");              
                        zipEntryName = "";
                        fzip = new File(rutaSalidaZIP);
                        elZip=new java.util.zip.ZipFile(fzip);                                                                                 
                        zip = new ZipInputStream(new FileInputStream(recuperado));
                        for (Enumeration entradas=elZip.entries();entradas.hasMoreElements();) 
                        { //for (3)
                            //3.- descomprimimos cada una de las entradas.
                            zipEntryName=((ZipEntry)entradas.nextElement()).getName();
                            fos = new FileOutputStream(rutaXML+ zipEntryName);
                                    
                            int buff1;
                               byte [] buffer1 = new byte [1024];
                       //vamos descomprimiento los ficheros que llegan en el zip
                           zip.getNextEntry();                                        
                           while ((buff1 = zip.read(buffer1)) != -1) 
                           {
                              if (buff1 < 1024) 
                              {
                                  byte [] ultimo1 = new byte [buff1];
                                  for (int p = 0; p < buff1; p++) 
                                  {
                                      ultimo1[p] = buffer1[p];
                                  }
                                  fos.write(ultimo1);
                              } else   fos.write(buffer1);                                            
                            }
                            fos.flush();
                            fos.close();
                        }//for del zip   
                       
                    }//if recuperado.existe
                    **/
                //}//if i = 3
            }//for ficheros
            
        }//if 600
        System.gc();
    }//decodificaXML
    
    
    
    public void decodificaXMLparaRecuperacion(String numexp,int cliente,String ficheroXML) throws ParserConfigurationException, SAXException, IOException, Exception
    {        
        String rutaXML = "/data/informes/decodificaXML/";
        String [] ficheroPDF= ficheroXML.split("\\.");
        String rutaSalidaZIP = rutaXML+ficheroPDF[0]+".zip";
        if (cliente == 600)
        {//SEGIPSA
            File fich = new File(rutaXML+ficheroXML);
            //Construye el documento XML a partir del fichero
            DocumentBuilder constructor = null;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            Document xmlDoc = null;
            constructor = dbf.newDocumentBuilder();
            xmlDoc = constructor.parse(fich);    
            NodeList lista_fic = xmlDoc.getElementsByTagName("FICHERO");
            for (int i = 0; i < lista_fic.getLength(); i++) 
            {
                if (i == 3)
                {
                    Node fic = lista_fic.item(i);
                    Node texto_fic = fic.getFirstChild();
                    String fichero = texto_fic.getNodeValue();      
                    
                    Base64 Base64= new Base64();
                    byte[] array_de_bytes = Base64.decode(fichero.trim());
                    
                    
                    File recuperado = new File(rutaSalidaZIP);
                    FileOutputStream fos = new FileOutputStream(recuperado);
                    int nread = array_de_bytes.length;
                    fos.write(array_de_bytes,0,nread);
                    fos.flush();
                    fos.close();
                    if (recuperado.exists())
                    {//descomprimimos y subimos a la base de datos el fichero pdf recuperado.
                        
                        /*
                        String zipEntryName = "";
                        int count = 0;                
                        BufferedOutputStream bufferedOutputStream = null;
                        FileOutputStream fileOutputStream = null;
                        java.util.zip.ZipEntry zipEntry;
                        java.util.zip.ZipInputStream zipInputStream = new java.util.zip.ZipInputStream(new BufferedInputStream(new FileInputStream(recuperado)));                
                        while((zipEntry = zipInputStream.getNextEntry()) != null) 
                        {                            
                            byte data[] = new byte[(int) zipEntry.getSize()];
                            zipEntryName = zipEntry.getName();
                            fileOutputStream = new FileOutputStream(rutaXML+zipEntryName);
                            bufferedOutputStream = new BufferedOutputStream(fileOutputStream, (int) zipEntry.getSize());
                            while ((count = zipInputStream.read(data, 0, (int) zipEntry.getSize())) != -1) bufferedOutputStream.write(data, 0, count);
                            bufferedOutputStream.flush();
                            bufferedOutputStream.close();
                            //subimos la documentacion
                            if (numexp != null)
                            {
                                insertadocumentoORCL  doc = new insertadocumentoORCL(numexp,rutaXML+ zipEntryName); 
                                doc = null;
                                
                            }
                            else System.out.println("Imposible subir fichero expediente de Valtecnic ES NULO");

                        }//while    
                        zipInputStream.close();
                        fileOutputStream.close();
                        bufferedOutputStream = null;
                        zipInputStream = null;         
                        fileOutputStream = null;      
                         **/
                    }
                }
            }//for
            
        }//if 600
        System.gc();
    }//decodificaXML
}
