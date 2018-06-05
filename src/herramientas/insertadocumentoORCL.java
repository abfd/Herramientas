/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package herramientas;

import java.io.*;
import java.sql.*;
import java.util.Calendar;
/**
 *
 * @author Administrador
 */
public class insertadocumentoORCL 
{
    public static void main(String [] args ) {
       
       
       //insertadocumentoORCL  doc = new insertadocumentoORCL("500.TS2010555019-001-10","/data/informes/WSClientSegipsa20/REC/4311TS2010555019STA.XML"); 
        //insertadocumentoORCL  doc = new insertadocumentoORCL("200.TS2010010005-006-10","/tmp/200.TS2010010005-006-10.pdf"); 
        //insertadocumentoORCL  doc = new insertadocumentoORCL();
       insertadocumentoORCL  doc = new insertadocumentoORCL("203.01820005500041-15","/data/informes/ancert/tmp/STA/01820005500041.xml"); 
        //insertadocumentoORCL  doc = new insertadocumentoORCL("100.TS2010008674-001-10","/data/informes/decodificaXML/RESUMENTS2010008674-001.pdf"); 
        //numero_automatico("55", "21");
      
    }//main
    public insertadocumentoORCL() 
    {
        String numexp = "100.TS2010003790-001-10";  //VPT
        Connection conexOrclTEST = null;
        //public static int loadDocgraficaClusterFromRp(String numexp,String urlOrigen,java.sql.Connection conexionDestino) throws java.sql.SQLException, java.lang.Exception
        try
        {
            System.out.println("Expediente: "+numexp);
            conexOrclTEST = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecinfx/valtecinfx@ (DESCRIPTION = (ADDRESS = (PROTOCOL = TCP)(HOST = oraserver01.valtecnic.com)(PORT = 1521)) (ADDRESS = (PROTOCOL = TCP)(HOST =oraserver02.valtecnic.com)(PORT =1521)) (LOAD_BALANCE = off) (CONNECT_DATA = (SERVER = DEDICATED) (SERVICE_NAME = test) (FAILOVER_MODE = (TYPE = SELECT) (METHOD = BASIC) (RETRIES = 20) (DELAY = 15))))");
            String conexOrclVTN = "jdbc:oracle:thin:valtecinfx/valtecinfx@ (DESCRIPTION = (ADDRESS = (PROTOCOL = TCP)(HOST = oraserver01.valtecnic.com)(PORT = 1521)) (ADDRESS = (PROTOCOL = TCP)(HOST = oraserver02.valtecnic.com)(PORT =1521)) (LOAD_BALANCE = off) (CONNECT_DATA = (SERVER = DEDICATED) (SERVICE_NAME = vtn) (FAILOVER_MODE = (TYPE = SELECT) (METHOD = BASIC) (RETRIES = 20) (DELAY = 15))))";
            Objetos.Docgrafica.loadDocgraficaClusterFromRp(numexp, conexOrclVTN, conexOrclTEST);
            conexOrclTEST.close();
            System.out.println("O.K");
        }
        catch (ClassNotFoundException cnfe)
        {
            System.out.println("ERROR"+cnfe.toString());
        }
        catch (SQLException sqle)
        {
            System.out.println("ERROR"+sqle.toString());
        }
        catch (Exception e)
        {
            System.out.println("ERROR"+e.toString());
        }
        finally
        {
             try
             {
                 if (conexOrclTEST != null && !conexOrclTEST.isClosed()) 
                 {
                     conexOrclTEST.close();
                     conexOrclTEST = null;
                 }                 

             }
             catch (SQLException sqlException)
             {                  
             }        
        }
 

    }
    public insertadocumentoORCL(String numexp,String ruta_fich) 
    {   
        
        try
        {
        //a DESARROLLO    
        //Connection conexionOracle = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecinfx/valtecinfx@ (DESCRIPTION = (ADDRESS = (PROTOCOL = TCP)(HOST = oraserver01.valtecnic.com)(PORT = 1521)) (ADDRESS = (PROTOCOL = TCP)(HOST =oraserver02.valtecnic.com)(PORT =1521)) (LOAD_BALANCE = off) (CONNECT_DATA = (SERVER = DEDICATED) (SERVICE_NAME = test) (FAILOVER_MODE = (TYPE = SELECT) (METHOD = BASIC) (RETRIES = 20) (DELAY = 15))))");
        //a EXPLOTACION
        //Connection conexionOracle = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecinfx/valtecinfx@ (DESCRIPTION = (ADDRESS = (PROTOCOL = TCP)(HOST = oraserver01.valtecnic.com)(PORT = 1521)) (ADDRESS = (PROTOCOL = TCP)(HOST =oraserver02.valtecnic.com)(PORT =1521)) (LOAD_BALANCE = off) (CONNECT_DATA = (SERVER = DEDICATED) (SERVICE_NAME = vtn) (FAILOVER_MODE = (TYPE = SELECT) (METHOD = BASIC) (RETRIES = 20) (DELAY = 15))))");
        Connection conexionOracle = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecinfx/valtecinfx@oraserver02:1521:rvtnprod");
        PreparedStatement  stmtOrcl = null;
        
        
        int numero=0;
        //PARA RECUPERACIONES INFORME COMPLETO EN PDF 
        //String descripcion=numexp.trim()+".pdf";        
        //String mime_type="application/pdf";
        //String pie="PDF recuperado";
        //int tipo_doc=4323;
        //String usuario = "recu";
        
        //PARA INFORME COMPLETO EN PDF 
        //String descripcion="100.TS2009050010-003-09.pdf";        
        //String mime_type="application/pdf";
        //String pie="Informe Completo";
        //int tipo_doc=4325;
        
        //PARA INFORME COMPLETO EN PDF 
        //String descripcion="portada-101.TS2009050011-001-09.pdf";        
        //String mime_type="application/pdf";
        //String pie="Portada Informe";
        //int tipo_doc=999;
        
        
        //PARA INFORME DE TASACON EN PDF 
        //String descripcion="200.TS2010010005-006-10";        
        //String mime_type="application/pdf";
        //String pie="Informe de Tasacion";
        //int tipo_doc=4298;
        //String usuario = "SEGIP";
        
        
        //PARA FOTOS Y PLANOS JPEG
        //String descripcion="foto.jpg";        
        //String mime_type="image/jpeg";
        //String pie="Fachada";
        //int tipo_doc=244;
        
        //String pie="Entorno";
        //String pie="Situación";
        //int tipo_doc=4320;
        
        //para encargo XML SEGIPSA
        //String descripcion="Encargo";        
        //String mime_type="text/xml";
        //String pie="Encargo XML";
        //int tipo_doc=4321;
        //String usuario = "SEGIP";
        
        //para BBVA
        String descripcion="STA";        
        String mime_type="text/xml";
        String pie="Documento STA";
        int tipo_doc=301;
        String usuario = "BBVA";
        
        File fichero = new File(ruta_fich);            
        Statement stmtOracle = conexionOracle.createStatement();
        ResultSet rsOracle = null;
        rsOracle = stmtOracle.executeQuery("select seq_docgrafica.nextval from dual");
        if (rsOracle.next()) numero = rsOracle.getInt(1);
        int resultado = stmtOracle.executeUpdate("insert into docgrafica values ('" + numexp + "'," + numero + ",'" + descripcion + "','" + mime_type + "',empty_blob(),'" + pie + "'," + tipo_doc + ")");
        if (resultado == 1) 
        {
             rsOracle = stmtOracle.executeQuery("select contenido from docgrafica where numero=" + numero + " for update");
             if (rsOracle.next())
             {
                  Blob informeBlob = rsOracle.getBlob(1);
                  OutputStream blobOutputStream = ((oracle.sql.BLOB)informeBlob).getBinaryOutputStream();
                  InputStream stream = new FileInputStream(fichero);
                  byte[] buffer = new byte[10*1024];
                  int nread = 0;
                  while((nread = stream.read(buffer)) != -1) blobOutputStream.write(buffer,0,nread);
                  blobOutputStream.close();
                  stream.close();
                  rsOracle.close();
             }
         }
                   
         resultado = stmtOracle.executeUpdate("insert into idendoc(numero, usuario) values (" + numero + ", '"+usuario+"')");
         if (resultado == 1) 
         {
             conexionOracle.commit();
         }
         else 
         {
              conexionOracle.rollback();
         }
         conexionOracle.close();
        }//try 
       catch (SQLException sqle)
       {
          System.out.println(sqle.toString()); 
       }
       catch (ClassNotFoundException cnfe)
       {
          System.out.println(cnfe.toString());  
       }
       catch (FileNotFoundException fnfe)
       {
          System.out.println(fnfe.toString());  
       }
       catch (IOException ioe)
       {
           System.out.println(ioe.toString()); 
       }
        
    }//insertadocumentoORCL
    
     public static String numero_automatico(String codcli, String codpro)
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
        Connection conexion = null;
        
        try{

            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@192.168.3.215:1521:rvtn");
            Statement stmt = conexion.createStatement();
            //Necesitamos recuperar de la tabla de provincias el prefijo asociado al codigo de 
            //provincia ya que eso determina la delegacion a la que pertenece
            ResultSet rs = stmt.executeQuery("select prefijo from provincias where codpro='" + codpro + "'");
            String prefijo = "";
            if (rs.next()) prefijo = rs.getString(1);
            
            //Buscamos la fecha y la hora por medio del objeto Calendar            
            while (seguir && intentos != 0)
            {
                calendario = Calendar.getInstance();
                //Obtiene la ultima cifra del a?o
                anio = calendario.get(calendario.YEAR);
                String s_anio = String.valueOf(anio);
                s_anio = s_anio.substring(s_anio.length() - 1);

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

                numexp = prefijo + codcli + s_anio + " " + s_mes + s_dia + " " + s_hora + s_minuto + s_segundo;
                System.out.println(numexp);
                if (!Objetos.Solicitudes.exists(numexp, conexion)) 
                {
                    intentos --;
                     try
                     {//ESPERAMOS 2 SEGUNDOS ANTES DE PROCESAR EL SIGUEINTE SOLICITUD                                   
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
}//CLASS
