/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package herramientas;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author Administrador
 */import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;



import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;


public class cargaPretasaExcel 
{
    private java.io.File fPropiedades;      
    private Logger logger = Logger.getLogger(cargaPretasaExcel.class);
    
    
    java.sql.ResultSet rs = null;
    java.sql.ResultSet rs2 = null;
    
    public static String sUrlExcel = "/data/informes/cargapretasaExcel/categorizaciones1.xls";
    public static Utilidades.Excel oExcel = null;            
    public static org.apache.poi.hssf.usermodel.HSSFCell celda = null;
    public static String HojaActual = "hoja1";
    
    
    
    public static void main(String[] args) 
    {
        cargaPretasaExcel carga = new cargaPretasaExcel();
        carga = null;
        System.gc();
    }
    
    public cargaPretasaExcel()
    {
        //cargaPretasaExcelMasivas();
        //cargaPretasacionesNOUCI();
        //cargaPretasacionesNOUCI3();
        //actualizaValoresPretasa();
        //cargaCategorizaciones();
        //cargaDemanda();
        //pretasa528();
        //pretasaDispersion();
        pretasaSavills();
        //pretasa250();        
        //pretasa126();        
        //cargaDocPretasacionesUCI();
        //listaExpedientes();
    }
    
    private void listaExpedientes()
    {
        try
        {
            sUrlExcel = "/data/informes/abfd/Envio ficheros xml para verificacion de oppplus.xls";
            oExcel = new Utilidades.Excel(sUrlExcel);              
            celda = null;
            HojaActual = "Hoja3";
            String sConsulta = null;
            java.sql.ResultSet rs = null;
            String objeto = "";
            java.sql.Connection conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@oraserver02:1521:rvtnprod");            
            conexion.setAutoCommit(false);  
            java.io.File fOrigen = null;
            java.io.File fDestino = null;
            java.io.File fDestinoUTF8 = null;
            
            for (int fila = 1; fila <78; fila ++)
            {//registro
                objeto = dameValorExcel(fila,0);
                if (objeto != null)
                {
                    objeto = Utilidades.Cadenas.TrimTotal(objeto);
                    if (objeto.length() > 6) sConsulta = "SELECT r.numexp FROM refer r JOIN solicitudes s ON r.numexp = s.numexp  WHERE s.codcli IN (103,203,803,903) AND r.referencia ='"+objeto+"'";
                    else sConsulta = "SELECT r.numexp FROM refer r JOIN solicitudes s ON r.numexp = s.numexp  WHERE s.codcli IN (103,203,803,903) AND r.objeto ="+objeto+"";
                    rs = Utilidades.Conexion.select(sConsulta, conexion);
                    if (rs.next())
                    {
                        System.out.println(rs.getString("numexp"));
                        //fOrigen =  new java.io.File("/data/informes/BBVANIDA/envios/xml/"+rs.getString("numexp")+"_UTF8.xml");
                        fOrigen =  new java.io.File("/data/informes/BBVANIDA/envios/xml/"+rs.getString("numexp")+".xml");
                        if (fOrigen.exists())
                        {
                            //lo pasamos a UTF8
                            fDestinoUTF8 =  new java.io.File("/data/informes/BBVANIDA/envios/xml/"+rs.getString("numexp")+"_UTF8.xml");
                            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fOrigen), "8859_1"));
                            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fDestinoUTF8), "utf-8"));
                            String sCadena;
                            while ((sCadena = in.readLine())!=null) 
                            {                              
                                out.write(sCadena);
                            }
                            in.close();
                            out.close();
                            fDestino = new java.io.File("/data/informes/BBVANIDA/envios/xml/revision/"+objeto+".xml");                            
                            fDestinoUTF8.renameTo(fDestino);
                            System.out.println("Movido a revision");
                        }
                        else System.out.println("No existe UTF8");
                    }
                    else System.out.println("NO EXISTE EXPEDIENTE");
                    rs.close();    
                }
            }
            conexion.close();
        }
        catch (Exception e)
        {
            
        }
        finally
        {
            
        }
    }
    
    public void cargaDemanda()
    {
        java.sql.Connection conexion = null; 
        String valor = null;
        int insertados = 0;
        int fallos = 0;
        
        
        String fuente = null;
        Integer codpro = null;
        Integer tipo = null;
        Integer codine = null;
        Integer coddistr = null;
        String fecha = null;
        Double descuento = null;
        Integer pujas = null;
        Double unitario = null;
        
        //FICHERO LOG4J
         PropertyConfigurator.configure("/data/informes/cargapretasaExcel/" + "Log4j.properties");   
         try
         {
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@oraserver01:1521:rvtn1");
            //conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:desarrollo/des0329@oraserver00:1521:rvtn");
            conexion.setAutoCommit(false);                                
              
            
            sUrlExcel = "/data/informes/cargapretasaExcel/Demandafebrero2013.xls";
            oExcel = new Utilidades.Excel(sUrlExcel);              
            celda = null;
            HojaActual = "Hoja1";
                         
             
             
             Utilidades.Consultas oConsultas = null;
             int iNumeroRegistrosAfectados = 0;
             
             for (int fila = 1; fila <= 573; fila ++)
             {//registro
                
                 fuente = dameValorExcel(fila,0);
                 codpro = Integer.parseInt(dameValorExcel(fila,1));
                 tipo = Integer.parseInt(dameValorExcel(fila,2));
                 codine = Integer.parseInt(dameValorExcel(fila,3));
                 coddistr = Integer.parseInt(dameValorExcel(fila,4));
                 fecha = "01-02-2013";
                 descuento = Double.parseDouble(dameValorExcel(fila,6));
                 pujas = Integer.parseInt(dameValorExcel(fila,7));
                 unitario = Double.parseDouble(dameValorExcel(fila,8));
                 
                 
                oConsultas = new Utilidades.Consultas(Utilidades.Consultas.INSERT);
                oConsultas.from("demanda");        
                oConsultas.insert("fuente",Utilidades.Cadenas.getValorSinBlancos(fuente),Utilidades.Consultas.VARCHAR);
                oConsultas.insert("codpro",Utilidades.Cadenas.getValorSinBlancos(codpro),Utilidades.Consultas.INT);
                oConsultas.insert("tipo",Utilidades.Cadenas.getValorSinBlancos(tipo),Utilidades.Consultas.INT);
                oConsultas.insert("codine",Utilidades.Cadenas.getValorSinBlancos(codine),Utilidades.Consultas.INT);
                oConsultas.insert("coddistr",Utilidades.Cadenas.getValorSinBlancos(coddistr),Utilidades.Consultas.INT);
                oConsultas.insert("fecha",Utilidades.Cadenas.getValorSinBlancos(fecha),Utilidades.Consultas.VARCHAR);
                oConsultas.insert("descuento",Utilidades.Cadenas.getValorDecimalBBDD(descuento),Utilidades.Consultas.INT);
                oConsultas.insert("pujas",Utilidades.Cadenas.getValorSinBlancos(pujas),Utilidades.Consultas.INT);
                oConsultas.insert("unitario",Utilidades.Cadenas.getValorDecimalBBDD(unitario),Utilidades.Consultas.INT);
                iNumeroRegistrosAfectados = Utilidades.Conexion.insert(oConsultas.getSql(),conexion);
                
                if (iNumeroRegistrosAfectados == 1)
                {
                    logger.info("Insertada demanda nº: "+ fila);
                    conexion.commit();
                    insertados ++;
                }
                else 
                {
                    logger.error("No insertada categorizacion nº: "+fila);
                    conexion.rollback();
                    fallos ++;
                }
                        
                oConsultas = null;
                 fuente = null;
                codpro = null;
                tipo = null;
                codine = null;
                coddistr = null;
                fecha = null;
                descuento = null;
                pujas = null;
                unitario = null;
             }//for filas
             conexion.close();
             
         }       
         catch (Exception ex)
         {
             logger.info("Excepcion. Descripción: "+ex.toString());
         }
         finally
         {
            try
            {
                if (conexion != null && !conexion.isClosed()) conexion.close();                
            }
            catch (java.sql.SQLException sqlException)
            {     
                logger.info("Imposible cerrar conexión con la B.D Informix");
            }       
            logger.info("Insertados : "+insertados);
            logger.info("Fallos : "+fallos);
         }//finally     
    }
    
    
    public void cargaCategorizaciones()
    {
        java.sql.Connection conexion = null; 
        String valor = null;
        int insertados = 0;
        int fallos = 0;
        
        //FICHERO LOG4J
         PropertyConfigurator.configure("/data/informes/cargapretasaExcel/" + "Log4j.properties");   
         try
         {
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@oraserver01:1521:rvtn1");
            //conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:desarrollo/des0329@oraserver00:1521:rvtn");
            conexion.setAutoCommit(false);                                
              
            //primer fichero cargado sUrlExcel = "/data/informes/cargapretasaExcel/categorizaciones1.xls";
            sUrlExcel = "/data/informes/cargapretasaExcel/categorizaciones2.xls";
            oExcel = new Utilidades.Excel(sUrlExcel);              
            celda = null;
            HojaActual = "hoja1";
            
             Objetos.Pretasaciones oPreta = null;
             
             //86 columnas + 4460 filas
             //con el fichero categorizaciones1 for (int fila = 1; fila <= 4459; fila ++)
             for (int fila = 1; fila <= 231; fila ++)
             {//registro
                oPreta = new Objetos.Pretasaciones(); 
                oPreta.numpet = Utilidades.Conexion.getSequenceNextVal("SEQ_PRETASA", conexion);
                //Utilidades.Cadenas.getValorMostrarWeb(rsDatos,"hucd")+Utilidades.Cadenas.getValorMostrarWeb(rsDatos,"a5cd")
                oPreta.numpreta = dameValorExcel(fila,2)+Utilidades.Cadenas.completTextWithLeftCharacter(dameValorExcel(fila,3),'0',6);                
                oPreta.codcli = 129;                
                //oPreta.coddel = getProvincia (dameValorExcel(fila,73),conexion);
                        
                oPreta.oficina = dameValorExcel(fila,0); //abcd
                oPreta.numexpc = Integer.parseInt(dameValorExcel(fila,1)); //alcd
                oPreta.ag_obj = dameValorExcel(fila,2);//hucd
                oPreta.objeto = Utilidades.Cadenas.completTextWithLeftCharacter(dameValorExcel(fila,3),'0',6);//a5cd
                oPreta.num_tas = "50"; //dameValorExcel(fila,4);//o5br
                oPreta.naturbien = dameValorExcel(fila,5);//aist
                oPreta.ubica = dameValorExcel(fila,6);//vl8oa
                oPreta.acabslo = dameValorExcel(fila,7);//vl8pa
                oPreta.acabpd = dameValorExcel(fila,8);//vl8qa
                oPreta.carpext = dameValorExcel(fila,9);//vl8ra
                oPreta.planta = Integer.parseInt(dameValorExcel(fila,10));//vl8sa
                oPreta.antig = Integer.parseInt(dameValorExcel(fila,11));//vl8ta
                oPreta.ndormit = Integer.parseInt(dameValorExcel(fila,12));//vl8ua
                oPreta.nbanos = Integer.parseInt(dameValorExcel(fila,13));//vl8va
                oPreta.ascens = dameValorExcel(fila,14);//vl8wa
                oPreta.calefacc = dameValorExcel(fila,15);//vl8xa
                oPreta.estcons = dameValorExcel(fila,16);//vl8ya
                oPreta.calcons = dameValorExcel(fila,17);//w11qa
                oPreta.suputil = Double.parseDouble(dameValorExcel(fila,18));//a2va
                oPreta.supcons = Double.parseDouble(dameValorExcel(fila,19));//cyva
                oPreta.supparc = Double.parseDouble(dameValorExcel(fila,20));//z1va
                oPreta.tipopretasa = "g";  //categorizacion.e33eaa
                oPreta.tipologia = dameValorExcel(fila,22);//e33eba
                oPreta.esvpo = dameValorExcel(fila,23);//pist
                oPreta.exptevpo = dameValorExcel(fila,24);//e3k3a
                valor = dameValorExcel(fila,25);//dddt                
                if (!valor.equals("1940-01-01")) oPreta.fchvpo = Utilidades.Cadenas.getDate(Integer.parseInt(valor.substring(8, 10)), Integer.parseInt(valor.substring(5, 7)), Integer.parseInt(valor.substring(0, 4)));//dddt
                oPreta.autopromocion = dameValorExcel(fila,26);//q2ts
                oPreta.finca = dameValorExcel(fila,27);//hrnb
                oPreta.registro = Integer.parseInt(dameValorExcel(fila,28));//hsnb
                oPreta.localireg = dameValorExcel(fila,29);//g4tx
                oPreta.titulareg = dameValorExcel(fila,30);//s4tx
                //oPreta.fcatastral = FCATASTRAL
                oPreta.vivnueva = dameValorExcel(fila,31);//wost
                oPreta.plantassras = Integer.parseInt(dameValorExcel(fila,32));//e33eca
                oPreta.sotanos = Integer.parseInt(dameValorExcel(fila,33));//e33eda
                oPreta.nplantas = Integer.parseInt(dameValorExcel(fila,34));//e33eea                
                oPreta.reforbloq = dameValorExcel(fila,35);//e33efa
                valor = dameValorExcel(fila,36);//e33ega
                if (!valor.equals("1940-01-01")) oPreta.fchreforbloq = Utilidades.Cadenas.getDate(Integer.parseInt(valor.substring(8, 10)), Integer.parseInt(valor.substring(5, 7)), Integer.parseInt(valor.substring(0, 4)));
                oPreta.ite = dameValorExcel(fila,37);//e33eha
                valor = dameValorExcel(fila,38);//e33eia
                if (!valor.equals("1940-01-01")) oPreta.fchite = Utilidades.Cadenas.getDate(Integer.parseInt(valor.substring(8, 10)), Integer.parseInt(valor.substring(5, 7)), Integer.parseInt(valor.substring(0, 4)));
                oPreta.reforviv = dameValorExcel(fila,39);//e33eja
                valor = dameValorExcel(fila,40);//e33eka
                if (!valor.equals("1940-01-01")) oPreta.fchreforviv = Utilidades.Cadenas.getDate(Integer.parseInt(valor.substring(8, 10)), Integer.parseInt(valor.substring(5, 7)), Integer.parseInt(valor.substring(0, 4)));
                oPreta.caliacabados = dameValorExcel(fila,41);//e33ela
                oPreta.aireacond = dameValorExcel(fila,42);//e33ema
                oPreta.telefonia = dameValorExcel(fila,43);//e33ena
                oPreta.otrasinstal = dameValorExcel(fila,44);//e33eoa
                oPreta.anxpiscina = dameValorExcel(fila,45);//e33epa
                oPreta.anxgaraje = dameValorExcel(fila,46);//f115qa
                oPreta.anxtrastero = dameValorExcel(fila,47);//f115pa
                oPreta.anxporche = dameValorExcel(fila,48);//e33eqa
                oPreta.anxotro = dameValorExcel(fila,49);//e33era
                oPreta.piscinaurb = dameValorExcel(fila,50);//e33esa
                oPreta.jardinesurb = dameValorExcel(fila,51);//e33eta
                oPreta.otrosurb = dameValorExcel(fila,52);//e33eua
                oPreta.constanio = Integer.parseInt(dameValorExcel(fila,53));//e33eva
                oPreta.comentc = dameValorExcel(fila,54);//sltx
                oPreta.valtotpretaviv = Double.parseDouble(dameValorExcel(fila,55));//e33ewa
                oPreta.valunitpretaviv = Double.parseDouble(dameValorExcel(fila,56));//e33exa
                oPreta.valtotpretatrt = Double.parseDouble(dameValorExcel(fila,57));//e33eya
                oPreta.valunitpretatrt = Double.parseDouble(dameValorExcel(fila,58));//e33eza
                oPreta.valtotpretapz = Double.parseDouble(dameValorExcel(fila,59));//e33f0a
                oPreta.valunitpretapz = Double.parseDouble(dameValorExcel(fila,60));//e33f1a
                oPreta.valtotrentviv = Double.parseDouble(dameValorExcel(fila,61));//e33f2a
                oPreta.valunitrentviv = Double.parseDouble(dameValorExcel(fila,62));//e33f3a
                oPreta.valtotrentrt = Double.parseDouble(dameValorExcel(fila,63));//e33f4a
                oPreta.valunitrentrt = Double.parseDouble(dameValorExcel(fila,64));//e33f5a
                oPreta.valtotrentpz = Double.parseDouble(dameValorExcel(fila,65));//e33f6a
                oPreta.valunitrentpz = Double.parseDouble(dameValorExcel(fila,66));//e33f7a
                oPreta.valorpretasa = Double.parseDouble(dameValorExcel(fila,67));//e29fa
                oPreta.estado = "2"; //e299a
                valor = dameValorExcel(fila,69);//mbdt
                //oPreta.fchuci = Utilidades.Cadenas.getDate("12-03-2013");
                oPreta.fchuci = Utilidades.Cadenas.getDate("13-03-2013");
                //oPreta.fchuci = Utilidades.Cadenas.getDate(Integer.parseInt(valor.substring(8, 10)), Integer.parseInt(valor.substring(5, 7)), Integer.parseInt(valor.substring(0, 4)));
                //oPreta.horauci = "12:30:00";//dameValorExcel(fila,70);//i2n1
                oPreta.horauci = "17:30:00";//dameValorExcel(fila,70);//i2n1
                valor = dameValorExcel(fila,71);//v59ya
                //oPreta.fchval = Utilidades.Cadenas.getDate(Integer.parseInt(valor.substring(8, 10)), Integer.parseInt(valor.substring(5, 7)), Integer.parseInt(valor.substring(0, 4)));
                oPreta.fchval = Utilidades.Cadenas.getDate("20-10-2014");
                oPreta.horaval = "13:30:00";//e29aa
                oPreta.postalv = Integer.parseInt(dameValorExcel(fila,73));//e29ba
                oPreta.provinv = dameValorExcel(fila,74);//w11aa
                oPreta.municiv = dameValorExcel(fila,75);//w11ba
                oPreta.localiv = dameValorExcel(fila,76);//w11ca
                oPreta.codsituv = dameValorExcel(fila,77);//w11da
                oPreta.situacionv = dameValorExcel(fila,78);//w11ea
                oPreta.tipoviav = dameValorExcel(fila,79);//w11fa
                oPreta.callev = dameValorExcel(fila,80);//w11ga
                oPreta.numerov = dameValorExcel(fila,81);//w11ha
                oPreta.escalerav = dameValorExcel(fila,82);//w11ia
                oPreta.plantav = dameValorExcel(fila,83);//w11ja
                oPreta.puertav = dameValorExcel(fila,84);//w11ka
                oPreta.vventar = Double.parseDouble(dameValorExcel(fila,85));//F45N8A
                oPreta.otratas = dameValorExcel(fila,86);//F45N9A
                oPreta.fiabilidad = null;
                oPreta.numexpv = null;
                oPreta.codusua = null;
                
                if (oPreta.insert(conexion) == 1)
                {
                    logger.info("Insertada categorización nº: "+ fila+ " con el nº de petición: "+oPreta.numpet);
                    conexion.commit();
                    insertados ++;
                }
                else 
                {
                    logger.error("No insertada categorizacion nº: "+fila);
                    conexion.rollback();
                    fallos ++;
                }
                        
                oPreta = null;
             }//for filas
             conexion.close();
             
         }       
         catch (Exception ex)
         {
             logger.info("Excepcion. Descripción: "+ex.toString());
         }
         finally
         {
            try
            {
                if (conexion != null && !conexion.isClosed()) conexion.close();                
            }
            catch (java.sql.SQLException sqlException)
            {     
                logger.info("Imposible cerrar conexión con la B.D Informix");
            }       
            logger.info("Insertados : "+insertados);
            logger.info("Fallos : "+fallos);
         }//finally     
    }
    
    public static String getProvincia(String codpos,java.sql.Connection conexion)
    {
        String provincia = null;
        java.sql.ResultSet rsAux = null;
        try
        {                    
            rsAux = Utilidades.Valtecnic.getProvincias(Integer.parseInt(Utilidades.Cadenas.completTextWithLeftCharacter(codpos,'0',2).substring(0,2)),conexion);
            if(rsAux.next()) provincia = Utilidades.Cadenas.getValorSinBlancos(rsAux,"nompro");
            rsAux.close();
        }
        catch (Exception e)
        {
            provincia = null;
        }
        finally
        {            
            return provincia;
        }
    }//getProvincia
    
     public static int getcoddelProvincia(String provincia,java.sql.Connection conexion)
    {
        int coddel = 0;
        java.sql.ResultSet rsAux = null;
        try
        {                    
             rsAux = Utilidades.Valtecnic.getProvincias(Integer.parseInt(provincia), conexion);
            if(rsAux.next()) coddel = Integer.parseInt(Utilidades.Cadenas.getValorSinBlancos(rsAux,"coddel"));
            rsAux.close();
        }
        catch (Exception e)
        {
            coddel = 0;
        }
        finally
        {            
            return coddel;
        }
    }
     
    public static java.util.Date dameValorFechaExcel(int fila,int col)  
    {
        java.util.Date valor = null;
        try
        {                 
             celda = oExcel.getCeldaFilaHoja(HojaActual,fila,col);                                 
             valor = Utilidades.Excel.getDateCellValue(celda);         
        }   
        catch (FileNotFoundException fnfe)
        {
            valor = null;
        }
        catch (IOException ioe)
        {
            valor = null;
        }
        catch (Exception e)
        {
            valor = null;
        }
        finally
        {
            return valor;
        }
    }//dameValorExcel 
     
    public static String dameValorExcel(int fila,int col)  
    {
        String valor = null;
        try
        {                 
             celda = oExcel.getCeldaFilaHoja(HojaActual,fila,col);                                 
             valor = Utilidades.Excel.getStringCellValue(celda);         
        }   
        catch (FileNotFoundException fnfe)
        {
            valor = null;
        }
        catch (IOException ioe)
        {
            valor = null;
        }
        catch (Exception e)
        {
            valor = null;
        }
        finally
        {
            return valor;
        }
    }//dameValorExcel
   
    public void cargaPretasaExcel1()
    {//borrar las repetidad, su numpet será >= 45138
        String sUrlExcel = "/data/informes/cargapretasaExcel/actu_enero_2011_borradas.xls";
        //FICHERO LOG4J
             PropertyConfigurator.configure("/data/informes/cargapretasaExcel/" + "Log4j.properties");   
             
            String agencia = "";
            String numexpc = "";
            String ag_objeto = "";
            String objeto = "";
            String numtas = "";
            java.sql.Connection conexion = null;
            String consulta = "";
            int iNumeroRegistrosAfectados = 0;
            int ceros = 0;
            String cadena = "";
            
            try
            {
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@192.168.3.215:1521:rvtn");
            conexion.setAutoCommit(false);
            String hoja = "borradas";   //nombre de la hora
            Utilidades.Excel oExcel = new Utilidades.Excel(sUrlExcel);            
            org.apache.poi.hssf.usermodel.HSSFCell celda = null;
            for (int fila = 0; fila < 5;fila ++)              
            {
                agencia = null;
                numexpc = null;            
                ag_objeto = null;
                objeto = null;            
                numtas = null;

                celda = null;
                Double numero = 0.0;

                celda = oExcel.getCeldaFilaHoja(hoja,fila,0);                
                if (celda != null) 
                {
                    numero = celda.getNumericCellValue();
                    agencia = Integer.toString(numero.intValue());
                    numero = 0.0;
                }                

                celda = oExcel.getCeldaFilaHoja(hoja,fila,1);
                
                if (celda != null) 
                {
                    numero = celda.getNumericCellValue();
                    numexpc = Integer.toString(numero.intValue());
                    numero = 0.0;
                }

                celda = oExcel.getCeldaFilaHoja(hoja,fila,2);
                if (celda != null) 
                {
                    numero = celda.getNumericCellValue();
                    ag_objeto = Integer.toString(numero.intValue());
                    numero = 0.0;
                }                                

                celda = oExcel.getCeldaFilaHoja(hoja,fila,3);
                if (celda != null) 
                {
                    numero = celda.getNumericCellValue();
                    objeto = Integer.toString(numero.intValue());
                    ceros = 6 - objeto.length();
                    for (int cero = 1; cero <= ceros;cero ++)    
                    {
                        cadena += "0";
                    }
                    objeto = cadena+objeto;
                    numero = 0.0;
                    cadena = "";
                }                                                

                celda = oExcel.getCeldaFilaHoja(hoja,fila,4);
                if (celda != null) 
                {
                    numero = celda.getNumericCellValue();
                    numtas = Integer.toString(numero.intValue());
                    numero = 0.0;
                }                                
                
            
                consulta = "DELETE FROM pretasaciones WHERE oficina = '"+agencia+"' and numexpc = '"+numexpc +"' and ag_obj = '"+ag_objeto+"' and objeto = '"+objeto+"' and  num_tas = '"+numtas+"' and numpet >= 45138";
                iNumeroRegistrosAfectados = Utilidades.Conexion.delete(consulta,conexion);
                if (iNumeroRegistrosAfectados == 1) 
                {
                    //conexion.commit();
                    conexion.rollback();
                    logger.info("Actualización BORRADA. oficina = '"+agencia+"' and numexpc = '"+numexpc +"' and ag_obj = '"+ag_objeto+"' and objeto = '"+objeto+"' and  num_tas = '"+numtas);
                }
                else
                {
                    conexion.rollback();
                    logger.info("Actualización NO BORRADA. oficina = '"+agencia+"' and numexpc = '"+numexpc +"' and ag_obj = '"+ag_objeto+"' and objeto = '"+objeto+"' and  num_tas = '"+numtas);
                }
                
            }//for
            conexion.close();
         }//try
         catch (Exception ex)
         {
             logger.info("Excepcion Actualización. oficina = '"+agencia+"' and numexpc = '"+numexpc +"' and ag_obj = '"+ag_objeto+"' and objeto = '"+objeto+"' and  num_tas = '"+numtas+". Descripción: "+ex.toString());
         }
         finally
        {
            try
            {
                if (conexion != null && !conexion.isClosed()) conexion.close();                
            }
            catch (java.sql.SQLException sqlException)
            {     
                logger.info("Imposible cerrar conexión con la B.D Informix");
            }             
        }//finally     
    }
    
    public void cargaPretasaExcel() 
    {//buscando en prencargos el expediente para asignar a la actualización la superficie que se utiliza para los
     //calculos.  
     //para ejecutar quitar el void y ponerselo al otro cargaPretasaExcel
     //ultima carga con fecha del 22/06/2011
         //String sUrlExcel = "/data/informes/cargapretasaExcel/jabactp2b(PPT).xls";  //estado = 'a'
         String sUrlExcel = "/data/informes/cargapretasaExcel/jabactp1(DUDOSOS).xls";  //estado = 'b'

        //FICHERO LOG4J
             PropertyConfigurator.configure("/data/informes/cargapretasaExcel/" + "Log4j.properties");    
         //int fila = 1; // - EN LA CERO ESTÁN LOS TEXTOS A MODO DE CABECERA DE TABLA
         int colExpte = 1;
         int colObjeto = 2;
         int colCalle = 11;
         int colLocalidad = 7;
         int colProvincia = 5;

         String agencia = "";
         String numexpc = "";
         String ag_objeto = "";
         String objeto = "";
         String numtas = "";
         
         String sValor = "";
         java.sql.Connection conexion = null;
        int iNumeroRegistrosAfectados = 0;
        String numexp = null;
        String[] clave;
        String referencia = "";
        String objetoAbuscar = "";
        Utilidades.Consultas oConsulta = null;
        String consulta = "";
        java.sql.ResultSet rs = null;
        String solicitudes = "";
        String otrastas = "";
        String elemento5 = "";
        String tabla = "";
        String tipoinm = "";
        Pretasaciones preta = null;
        String hoja = "jabactp1";   //nombre de la hoja
        int fila = 1;
         try
         {
           conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@192.168.3.215:1521:rvtn");
           conexion.setAutoCommit(false);
           
           Utilidades.Excel oExcel = new Utilidades.Excel(sUrlExcel);            
           org.apache.poi.hssf.usermodel.HSSFCell celda = null;
           for (fila = 7489; fila <= 8796;fila ++)             
           {
            numexp = null;
            tipoinm = null;
            referencia = null;
            objeto = null;
            clave = null;
            numexpc = null;
            agencia = null;
            ag_objeto = null;
            objetoAbuscar = null;
            celda = null;
            
            
            celda = oExcel.getCeldaFilaHoja(hoja,fila,0);
            agencia = celda.getStringCellValue();
            
            celda = oExcel.getCeldaFilaHoja(hoja,fila,1);
            Double numero = 0.0;
            if (celda != null) 
            {
                numero = celda.getNumericCellValue();
                numexpc = Integer.toString(numero.intValue());
            }
            
            //clave = numexpc.split("-");
            //agencia = clave[0];
            //numexpc = Integer.toString(Integer.parseInt(clave[1]));
            //clave = objeto.split("-");
            celda = oExcel.getCeldaFilaHoja(hoja,fila,2);
            ag_objeto = celda.getStringCellValue();
            //ag_objeto = clave[0];
            //objeto = clave[1];
            celda = oExcel.getCeldaFilaHoja(hoja,fila,3);
            objeto = celda.getStringCellValue();
            
            referencia = agencia.trim()+"-"+numexpc.trim();
            objetoAbuscar = ag_objeto.trim()+objeto.trim();
            //buscamos en refer el nº de expediente
        
             consulta = "SELECT numexpv FROM prencargos WHERE oficina = '"+agencia+"' and numexpc = '"+numexpc +"' and ag_obj = '"+ag_objeto+"' and objeto = '"+objeto+"' order by fchenc desc";
             rs = Utilidades.Conexion.select(consulta,conexion);     
             if (rs.next()) numexp = rs.getString(1);
             rs.close();
       
             if (numexp != null)
             {
                preta = new Pretasaciones();
                if (Funciones.Fcomunes.enHistorico(conexion, numexp)) 
                {
                    solicitudes = "his_solicitudes";
                    otrastas = "his_otrastas";
                    elemento5 = "his_elemento5";
                }
                else 
                {
                    solicitudes = "solicitudes";
                    otrastas = "otrastas";
                    elemento5 = "elemento5";
                }
                //datos solicitudes
                consulta = "SELECT * FROM "+solicitudes+" WHERE numexp='"+numexp+"'";
                rs = Utilidades.Conexion.select(consulta,conexion);     
                if (rs.next()) 
                {
                    tipoinm = rs.getString("tipoinm");
                    preta.coddel = rs.getInt("delegado");
                    preta.oficina = agencia.trim();
                    preta.numexpc = Integer.parseInt(numexpc.trim());
                    preta.ag_obj = ag_objeto.trim();
                    preta.objeto = objeto.trim();
                    preta.num_tas = "1";
                    preta.postalv = Integer.parseInt(rs.getString("codpos"));
                    preta.municiv = rs.getString("munici");
                    preta.localiv = rs.getString("locali");
                    preta.provinv = rs.getString("provin");
                    preta.codsituv = rs.getString("codsitu");
                    preta.situacionv = rs.getString("situacion");
                    preta.tipoviav = rs.getString("tipovia");
                    preta.callev = rs.getString("calle");
                    preta.numerov = rs.getString("numero");
                    preta.escalerav = rs.getString("escalera");
                    preta.plantav = rs.getString("planta");
                    preta.puertav =   rs.getString("puerta");      
                    
                    if (Funciones.Fcomunes.esTipologia(conexion, tipoinm) == 1)
                    {//tipologia 1
                       consulta = "SELECT suputil,cconparcom FROM "+elemento5+" WHERE numexp='"+numexp+"'";
                       rs = Utilidades.Conexion.select(consulta,conexion);     
                        if (rs.next())  
                        {
                            if (rs.getString("suputil") != null) preta.suputil = Double.parseDouble(rs.getString("suputil"));
                            if (rs.getString("cconparcom") != null) preta.supcons = Double.parseDouble(rs.getString("cconparcom"));                            
                        }
                       //rs.close();
                    }
                    else
                    {//tipologia 2
                       consulta = "SELECT totsuperf FROM "+otrastas+"  WHERE numexp='"+numexp+"'";
                       rs = Utilidades.Conexion.select(consulta,conexion);     
                        if (rs.next())  
                        {
                            if (rs.getString("totsuperf") != null) preta.supcons = Double.parseDouble(rs.getString("totsuperf"));                                                                                    
                        }
                       //rs.close();
                        
                    }
                
                rs.close();
                //tipo de pretasaciones Actualizacion de valor
                //A-actualizaciones normales a-actualizaciones masivas PPT b-actualizaciones masivas dudosos
                preta.tipopretasa = "b";
                //naturaleza del bien
                celda = oExcel.getCeldaFilaHoja(hoja,fila,17);
                preta.naturbien = celda.getStringCellValue();
                //preta.naturbien = "X";
                //codigo situacion 
                /*
                celda = oExcel.getCeldaFilaHoja(hoja,fila,8);
                if (celda != null) 
                {
                    if (celda.getStringCellValue().length() > 60) preta.codsituv = celda.getStringCellValue().substring(0, 60).toUpperCase();
                    else preta.codsituv = celda.getStringCellValue().toUpperCase();
                }
                //
                //situacion
                celda = oExcel.getCeldaFilaHoja(hoja,fila,9);
                if (celda != null) 
                {
                    if (celda.getStringCellValue().length() > 60) preta.situacionv = celda.getStringCellValue().substring(0, 60).toUpperCase();
                    else preta.situacionv = celda.getStringCellValue().toUpperCase();
                }
                //tipo via
                celda = oExcel.getCeldaFilaHoja(hoja,fila,10);
                if (celda != null) 
                {
                    if (celda.getStringCellValue().length() > 60) preta.tipoviav = celda.getStringCellValue().substring(0, 60).toUpperCase();
                    else preta.tipoviav = celda.getStringCellValue().toUpperCase();
                }
                //calle
                celda = oExcel.getCeldaFilaHoja(hoja,fila,11);
                if (celda != null) 
                {
                    if (celda.getStringCellValue().length() > 60) preta.callev = celda.getStringCellValue().substring(0, 60).toUpperCase();
                    else preta.callev = celda.getStringCellValue().toUpperCase();
                }
                //numero
                celda = oExcel.getCeldaFilaHoja(hoja,fila,12);
                if (celda != null) 
                {
                    if (celda.getStringCellValue().length() > 60) preta.numerov = celda.getStringCellValue().substring(0, 60).toUpperCase();
                    else preta.numerov = celda.getStringCellValue().toUpperCase();
                }
                //escalera
                celda = oExcel.getCeldaFilaHoja(hoja,fila,13);
                if (celda != null) 
                {
                    if (celda.getStringCellValue().length() > 60) preta.escalerav = celda.getStringCellValue().substring(0, 60).toUpperCase();
                    else preta.escalerav = celda.getStringCellValue().toUpperCase();
                }
                //planta
                celda = oExcel.getCeldaFilaHoja(hoja,fila,14);
                if (celda != null) 
                {
                    if (celda.getStringCellValue().length() > 60) preta.plantav = celda.getStringCellValue().substring(0, 60).toUpperCase();
                    else preta.plantav = celda.getStringCellValue().toUpperCase();
                }
                //puerta
                celda = oExcel.getCeldaFilaHoja(hoja,fila,15);
                if (celda != null) 
                {
                    if (celda.getStringCellValue().length() > 60) preta.puertav = celda.getStringCellValue().substring(0, 60).toUpperCase();
                    else preta.puertav = celda.getStringCellValue().toUpperCase();
                }
                celda = oExcel.getCeldaFilaHoja(hoja,fila,7);
                preta.localiv = celda.getStringCellValue().toUpperCase();
                
                celda = oExcel.getCeldaFilaHoja(hoja,fila,5);
                preta.provinv = celda.getStringCellValue().toUpperCase();
                 * */
                preta.otratas = "N";  //ya realizada por Valtecnic.
                //esVPO
                celda = oExcel.getCeldaFilaHoja(hoja,fila,18);
                if (celda != null) 
                {
                    preta.esvpo = celda.getStringCellValue();
                }                
                if (preta.setPretacionesFromB23xt (conexion) > 0)
                {
                    conexion.commit();                    
                    logger.info("Actualización numero: "+Integer.toString(fila)+" INSERTADA. Referencia:"+referencia+" Objeto:"+objetoAbuscar);
                }
                else 
                {
                    conexion.rollback();
                    logger.error("Actualización numero: "+Integer.toString(fila)+" NO INSERTADA. Referencia:"+referencia+" Objeto:"+objetoAbuscar);
                }
                //al final
              }//if solicitudes/his_solicitudes  
              else  
              {
                  logger.info("Actualización numero: "+Integer.toString(fila)+" NO LOCALIZADO EXPEDIENTE. Referencia:"+referencia+" Objeto:"+objetoAbuscar);  
              }
              preta = null;
             }
             else
             {
                 logger.info("Actualización numero: "+Integer.toString(fila)+" NO LOCALIZADA. Referencia:"+referencia+" Objeto:"+objetoAbuscar);
             }
            
            
            
            
            System.gc();
           }//for
           conexion.close();
         }
         
         catch (Exception ex)
         {
             logger.info("Actualización numero: "+Integer.toString(fila+1)+" EXCEPCION. Referencia:"+referencia+" Objeto:"+objetoAbuscar+". Descripcion"+ex.toString());
         }
         finally
        {
            try
            {
                if (conexion != null && !conexion.isClosed()) conexion.close();                
            }
            catch (java.sql.SQLException sqlException)
            {     
                logger.info("Imposible cerrar conexión con la B.D Informix");
            }             
        }//finally     

    }//cargaPretasaExcel
    
    public void cargaPretasaExcel2() 
    {//sin buscar en prencargos el expediente para asignar a la actualización la superficie que se utiliza para los
     //calculos. Simplemente carga en precargos.
         String sUrlExcel = "/data/informes/cargapretasaExcel/actu_enero_2011_faltan.xls";

        //FICHERO LOG4J
             PropertyConfigurator.configure("/data/informes/cargapretasaExcel/" + "Log4j.properties");    
         //int fila = 1; // - EN LA CERO ESTÁN LOS TEXTOS A MODO DE CABECERA DE TABLA
         int colExpte = 1;
         int colObjeto = 2;
         int colCalle = 4;
         int colLocalidad = 5;
         int colProvincia = 6;

         String agencia = "";
         String numexpc = "";
         String ag_objeto = "";
         String objeto = "";
         String numtas = "";
         
         String sValor = "";
         java.sql.Connection conexion = null;
        int iNumeroRegistrosAfectados = 0;
        String numexp = null;
        String[] clave;
        String referencia = "";
        String objetoAbuscar = "";
        Utilidades.Consultas oConsulta = null;
        String consulta = "";
        java.sql.ResultSet rs = null;
        String solicitudes = "";
        String otrastas = "";
        String elemento5 = "";
        String tabla = "";
        String tipoinm = "";
        Pretasaciones preta = null;
        String hoja = "actu_enero_2011_faltan";   //nombre de la hora
        int fila = 1;
         try
         {
           conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@192.168.3.215:1521:rvtn");
           conexion.setAutoCommit(false);
           
           Utilidades.Excel oExcel = new Utilidades.Excel(sUrlExcel);            
           org.apache.poi.hssf.usermodel.HSSFCell celda = null;
           for (fila = 1; fila < 934;fila ++)  
           //for (fila = 41; fila < 51;fila ++)      
           {
            numexp = null;
            tipoinm = null;
            referencia = null;
            objeto = null;
            clave = null;
            numexpc = null;
            agencia = null;
            ag_objeto = null;
            objetoAbuscar = null;
            celda = null;
            
            
            celda = oExcel.getCeldaFilaHoja(hoja,fila,0);
            agencia = celda.getStringCellValue();
            
            celda = oExcel.getCeldaFilaHoja(hoja,fila,1);
            Double numero = 0.0;
            if (celda != null) 
            {
                numero = celda.getNumericCellValue();
                numexpc = Integer.toString(numero.intValue());
            }
            
            //clave = numexpc.split("-");
            //agencia = clave[0];
            //numexpc = Integer.toString(Integer.parseInt(clave[1]));
            //clave = objeto.split("-");
            celda = oExcel.getCeldaFilaHoja(hoja,fila,2);
            ag_objeto = celda.getStringCellValue();
            //ag_objeto = clave[0];
            //objeto = clave[1];
            celda = oExcel.getCeldaFilaHoja(hoja,fila,3);
            objeto = celda.getStringCellValue();
            
            referencia = agencia.trim()+"-"+numexpc.trim();
            objetoAbuscar = ag_objeto.trim()+objeto.trim();
            //buscamos en refer el nº de expediente
        
             
                preta = new Pretasaciones();
                
                                        
                    preta.oficina = agencia.trim();
                    preta.numexpc = Integer.parseInt(numexpc.trim());
                    preta.ag_obj = ag_objeto.trim();
                    preta.objeto = objeto.trim();
                    preta.num_tas = "1";
                    
                    celda = oExcel.getCeldaFilaHoja(hoja,fila,4);
                    double valor = celda.getNumericCellValue();
                    String valor1 = Double.toString(valor);
                    int pos = valor1.indexOf(".");
                    valor1 = valor1.substring(0, pos);
                    if (valor1 != null) 
                    {
                        preta.postalv = Integer.parseInt(valor1);
                        String aux_postalv = valor1;
                        if (aux_postalv.length() < 5 ) aux_postalv = "0"+aux_postalv.trim();
                        aux_postalv = aux_postalv.substring(0, 2);
                        consulta = "SELECT * FROM provincias WHERE codpro="+aux_postalv;
                        rs = Utilidades.Conexion.select(consulta,conexion);     
                        if (rs.next()) 
                        {
                            preta.coddel = rs.getInt("coddel");
                        }
                    }
                    
                    celda = oExcel.getCeldaFilaHoja(hoja,fila,6);
                    if (celda.getStringCellValue() != null ) preta.municiv = celda.getStringCellValue().toUpperCase();
                    
                    celda = oExcel.getCeldaFilaHoja(hoja,fila,7);
                    if (celda.getStringCellValue() != null ) preta.localiv = celda.getStringCellValue().toUpperCase();                    
                    
                    celda = oExcel.getCeldaFilaHoja(hoja,fila,5);
                    if (celda.getStringCellValue() != null ) preta.provinv = celda.getStringCellValue().toUpperCase();
                                                                                
                    
                    /*
                    preta.codsituv = rs.getString("codsitu");
                    preta.situacionv = rs.getString("situacion");
                    preta.tipoviav = rs.getString("tipovia");
                    preta.callev = rs.getString("calle");
                    preta.numerov = rs.getString("numero");
                    preta.escalerav = rs.getString("escalera");
                    preta.plantav = rs.getString("planta");
                    preta.puertav =   rs.getString("puerta");      
                    */                    
                                
                //tipo de pretasaciones Actualizacion de valor
                preta.tipopretasa = "A";
                //naturaleza del bien
                celda = oExcel.getCeldaFilaHoja(hoja,fila,17);
                preta.naturbien = celda.getStringCellValue();
                //preta.naturbien = "X";
                //codigo situacion 
                
                celda = oExcel.getCeldaFilaHoja(hoja,fila,8);
                if (celda != null) 
                {
                    if (celda.getStringCellValue().length() > 60) preta.codsituv = celda.getStringCellValue().substring(0, 60).toUpperCase();
                    else preta.codsituv = celda.getStringCellValue().toUpperCase();
                }               
                
                //situacion
                celda = oExcel.getCeldaFilaHoja(hoja,fila,9);
                if (celda != null) 
                {
                    if (celda.getStringCellValue().length() > 60) preta.situacionv = celda.getStringCellValue().substring(0, 60).toUpperCase();
                    else preta.situacionv = celda.getStringCellValue().toUpperCase();
                }
                
                //tipo via
                celda = oExcel.getCeldaFilaHoja(hoja,fila,10);
                if (celda != null) 
                {
                    if (celda.getStringCellValue().length() > 60) preta.tipoviav = celda.getStringCellValue().substring(0, 60).toUpperCase();
                    else preta.tipoviav = celda.getStringCellValue().toUpperCase();
                }
                
                //calle
                celda = oExcel.getCeldaFilaHoja(hoja,fila,11);
                if (celda != null) 
                {
                    if (celda.getStringCellValue().length() > 60) preta.callev = celda.getStringCellValue().substring(0, 60).toUpperCase();
                    else preta.callev = celda.getStringCellValue().toUpperCase();
                }
                
                //numero
                celda = oExcel.getCeldaFilaHoja(hoja,fila,12);
                if (celda != null) 
                {
                    if (celda.getStringCellValue().length() > 60) preta.numerov = celda.getStringCellValue().substring(0, 60).toUpperCase();
                    else preta.numerov = celda.getStringCellValue().toUpperCase();
                }
                
                //escalera
                celda = oExcel.getCeldaFilaHoja(hoja,fila,13);
                if (celda != null) 
                {
                    if (celda.getStringCellValue().length() > 60) preta.escalerav = celda.getStringCellValue().substring(0, 60).toUpperCase();
                    else preta.escalerav = celda.getStringCellValue().toUpperCase();
                }
                //planta
                celda = oExcel.getCeldaFilaHoja(hoja,fila,14);
                if (celda != null) 
                {
                    if (celda.getStringCellValue().length() > 60) preta.plantav = celda.getStringCellValue().substring(0, 60).toUpperCase();
                    else preta.plantav = celda.getStringCellValue().toUpperCase();
                }
                
                //puerta
                celda = oExcel.getCeldaFilaHoja(hoja,fila,15);
                if (celda != null) 
                {
                    if (celda.getStringCellValue().length() > 60) preta.puertav = celda.getStringCellValue().substring(0, 60).toUpperCase();
                    else preta.puertav = celda.getStringCellValue().toUpperCase();
                }
                
                
                preta.otratas = "N";  //ya realizada por Valtecnic.
                //esVPO
                celda = oExcel.getCeldaFilaHoja(hoja,fila,18);
                if (celda != null) 
                {
                    preta.esvpo = celda.getStringCellValue();
                }                
                if (preta.setPretacionesFromB23xt (conexion) > 0)
                {
                    conexion.commit();
                    logger.info("Actualización numero: "+Integer.toString(fila+1)+" INSERTADA. Referencia:"+referencia+" Objeto:"+objetoAbuscar);
                }
                else 
                {
                    conexion.rollback();
                    logger.error("Actualización numero: "+Integer.toString(fila+1)+" NO INSERTADA. Referencia:"+referencia+" Objeto:"+objetoAbuscar);
                }
                //al final
              
              preta = null;
             
            System.gc();
           }//for
           conexion.close();
         }
         
         catch (Exception ex)
         {
             logger.info("Actualización numero: "+Integer.toString(fila+1)+" EXCEPCION. Referencia:"+referencia+" Objeto:"+objetoAbuscar+". Descripcion"+ex.toString());
         }
         finally
        {
            try
            {
                if (conexion != null && !conexion.isClosed()) conexion.close();                
            }
            catch (java.sql.SQLException sqlException)
            {     
                logger.info("Imposible cerrar conexión con la B.D Informix");
            }             
        }//finally     

    }//cargaPretasaExcel
    
    
    public void cargaPretasaExcel3()
    {//borrar las repetidad, su numpet será >= 45138
        String sUrlExcel = "/data/informes/cargapretasaExcel/VTARAP6.XLS";
        //FICHERO LOG4J
             PropertyConfigurator.configure("/data/informes/cargapretasaExcel/" + "Log4j.properties");   
             
            String agencia = "";
            String numexpc = "";
            String ag_objeto = "";
            String objeto = "";
            String numtas = "";
            java.sql.Connection conexion = null;
            String consulta = "";
            int iNumeroRegistrosAfectados = 0;
            int ceros = 0;
            String cadena = "";
            java.sql.ResultSet rs = null;
            String sUpdate = "";
            
            try
            {
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@192.168.3.215:1521:rvtn");
            conexion.setAutoCommit(false);
            String hoja = "VTARAP6";   //nombre de la hora
            Utilidades.Excel oExcel = new Utilidades.Excel(sUrlExcel);            
            org.apache.poi.hssf.usermodel.HSSFCell celda = null;
            for (int fila = 0; fila < 39;fila ++)              
            {
                agencia = null;
                numexpc = null;            
                ag_objeto = null;
                objeto = null;            
                numtas = null;

                celda = null;
                Double numero = 0.0;

                celda = oExcel.getCeldaFilaHoja(hoja,fila,0);                
                if (celda != null) 
                {
                    
                    agencia = celda.getStringCellValue();
                    numero = 0.0;
                }                

                celda = oExcel.getCeldaFilaHoja(hoja,fila,1);
                
                if (celda != null) 
                {
                    numero = celda.getNumericCellValue();
                    numexpc = Integer.toString(numero.intValue());
                    numero = 0.0;
                }

                celda = oExcel.getCeldaFilaHoja(hoja,fila,2);
                if (celda != null) 
                {
                    
                    ag_objeto = celda.getStringCellValue();
                    numero = 0.0;
                }                                

                celda = oExcel.getCeldaFilaHoja(hoja,fila,3);
                if (celda != null) 
                {
                    
                    objeto = celda.getStringCellValue();
                    
                    cadena = "";
                }                                                

                celda = oExcel.getCeldaFilaHoja(hoja,fila,4);
                if (celda != null) 
                {
                    numero = celda.getNumericCellValue();
                    numtas = Integer.toString(numero.intValue());
                    numero = 0.0;
                }                                
                
            
                consulta = "SELECT * FROM pretasaciones WHERE oficina = '"+agencia+"' and numexpc = '"+numexpc +"' and ag_obj = '"+ag_objeto+"' and objeto = '"+objeto+"' and  num_tas = '"+numtas+"'";
                //consulta = "SELECT * FROM b23xt WHERE abcd = '"+agencia+"' and alcd = '"+numexpc +"' and hucd = '"+ag_objeto+"' and a5cd = '"+objeto+"' and  o5br = '"+numtas+"'";
                rs = Utilidades.Conexion.select(consulta,conexion);
                if (rs.next())                
                {                    
                    logger.info("numero: "+fila);
                    //logger.info("Actualización ENCONTRADA NUMPET: "+rs.getString("numpet")+" oficina = '"+agencia+"' and numexpc = '"+numexpc +"' and ag_obj = '"+ag_objeto+"' and objeto = '"+objeto+"' and  num_tas = '"+numtas);
                    logger.info("Actualización ENCONTRADA NUMPET: "+" oficina = '"+agencia+"' and numexpc = '"+numexpc +"' and ag_obj = '"+ag_objeto+"' and objeto = '"+objeto+"' and  num_tas = '"+numtas);
                    //logger.info("estado : "+rs.getString("e299a") + "vventaR: "+rs.getString("f45n8a"));
                    //sUpdate = "update b23xt set e299a = '3' WHERE abcd = '"+agencia+"' and alcd = '"+numexpc +"' and hucd = '"+ag_objeto+"' and a5cd = '"+objeto+"' and  o5br = '"+numtas+"'";
                    sUpdate = "update pretasaciones set estado = '7' where numpet = "+rs.getString("numpet");
                    if (Utilidades.Conexion.update(sUpdate, conexion) == 1) 
                    {
                        conexion.commit();
                        logger.info("numero: "+fila);
                        logger.info("Actualización ENVIADA  oficina = '"+agencia+"' and numexpc = '"+numexpc +"' and ag_obj = '"+ag_objeto+"' and objeto = '"+objeto+"' and  num_tas = '"+numtas);
                    }
                    else
                    {
                        conexion.rollback();
                        logger.info("numero: "+fila);
                        logger.info("Actualización NO ENVIADA  oficina = '"+agencia+"' and numexpc = '"+numexpc +"' and ag_obj = '"+ag_objeto+"' and objeto = '"+objeto+"' and  num_tas = '"+numtas);
                    }
                    
                            
                }
                else
                {
                    logger.info("numero: "+fila);
                    logger.info("Actualización NO ENCONTRADA NUMPET: "+rs.getString("numpet")+" oficina = '"+agencia+"' and numexpc = '"+numexpc +"' and ag_obj = '"+ag_objeto+"' and objeto = '"+objeto+"' and  num_tas = '"+numtas);
                }
                
            }//for
            conexion.close();
         }//try
         catch (Exception ex)
         {
             logger.info("Excepcion Actualización. oficina = '"+agencia+"' and numexpc = '"+numexpc +"' and ag_obj = '"+ag_objeto+"' and objeto = '"+objeto+"' and  num_tas = '"+numtas+". Descripción: "+ex.toString());
         }
         finally
        {
            try
            {
                if (conexion != null && !conexion.isClosed()) conexion.close();                
            }
            catch (java.sql.SQLException sqlException)
            {     
                logger.info("Imposible cerrar conexión con la B.D Informix");
            }             
        }//finally     
    }
    
    
    public void cargaPretasaExcel4()
    {//actualizacion de las pretasaciones que Beatriz envio a 0 con las superficies util y construida recibidas en el exce.
        String sUrlExcel = "/data/informes/cargapretasaExcel/ACTVSINSUP.XLS";
        //FICHERO LOG4J
             PropertyConfigurator.configure("/data/informes/cargapretasaExcel/" + "Log4j.properties");   
             
            String agencia = "";
            String numexpc = "";
            String ag_objeto = "";
            String objeto = "";
            String numtas = "";
            java.sql.Connection conexion = null;
            String consulta = "";
            int iNumeroRegistrosAfectados = 0;
            int ceros = 0;
            String cadena = "";
            java.sql.ResultSet rs = null;
            String sUpdate = "";
            int numpet = 0;
            double suputil = 0;
            double supcons = 0;
            
            try
            {
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@192.168.3.215:1521:rvtn");
            conexion.setAutoCommit(false);
            String hoja = "ACTVSINSUP";   //nombre de la hora
            Utilidades.Excel oExcel = new Utilidades.Excel(sUrlExcel);            
            org.apache.poi.hssf.usermodel.HSSFCell celda = null;
            for (int fila = 1; fila <= 30;fila ++)              
            {
                agencia = null;
                numexpc = null;            
                ag_objeto = null;
                objeto = null;            
                numtas = null;

                celda = null;
                Double numero = 0.0;

                celda = oExcel.getCeldaFilaHoja(hoja,fila,0);                
                if (celda != null) 
                {
                    
                    agencia = celda.getStringCellValue();
                    numero = 0.0;
                }                

                celda = oExcel.getCeldaFilaHoja(hoja,fila,1);
                
                if (celda != null) 
                {
                    numero = celda.getNumericCellValue();
                    numexpc = Integer.toString(numero.intValue());
                    numero = 0.0;
                }

                celda = oExcel.getCeldaFilaHoja(hoja,fila,2);
                if (celda != null) 
                {
                    
                    ag_objeto = celda.getStringCellValue();
                    numero = 0.0;
                }                                

                celda = oExcel.getCeldaFilaHoja(hoja,fila,3);
                if (celda != null) 
                {
                    
                    objeto = celda.getStringCellValue();
                    
                    cadena = "";
                }                                                

                celda = oExcel.getCeldaFilaHoja(hoja,fila,4);
                if (celda != null) 
                {
                    numero = celda.getNumericCellValue();
                    numtas = Integer.toString(numero.intValue());
                    numero = 0.0;
                }      
                
                celda = oExcel.getCeldaFilaHoja(hoja,fila,5);
                if (celda != null) 
                {
                    supcons = celda.getNumericCellValue();
                    //numtas = Integer.toString(numero.intValue());
                    //numero = 0.0;
                }      
                
                celda = oExcel.getCeldaFilaHoja(hoja,fila,6);
                if (celda != null) 
                {
                    suputil = celda.getNumericCellValue();
                    //numtas = Integer.toString(numero.intValue());
                    //numero = 0.0;
                }      
                
            
                consulta = "SELECT * FROM pretasaciones WHERE oficina = '"+agencia+"' and numexpc = '"+numexpc +"' and ag_obj = '"+ag_objeto+"' and objeto = '"+objeto+"' and  num_tas = '"+numtas+"'";
                
                rs = Utilidades.Conexion.select(consulta,conexion);
                if (rs.next())                
                {   
                    numpet = rs.getInt("numpet");
                    logger.info("numero: "+fila);
                    //logger.info("Actualización ENCONTRADA NUMPET: "+rs.getString("numpet")+" oficina = '"+agencia+"' and numexpc = '"+numexpc +"' and ag_obj = '"+ag_objeto+"' and objeto = '"+objeto+"' and  num_tas = '"+numtas);
                    logger.info("Actualización ENCONTRADA NUMPET: "+" oficina = '"+agencia+"' and numexpc = '"+numexpc +"' and ag_obj = '"+ag_objeto+"' and objeto = '"+objeto+"' and  num_tas = '"+numtas);
                    //logger.info("estado : "+rs.getString("e299a") + "vventaR: "+rs.getString("f45n8a"));
                    sUpdate = "update pretasaciones set suputil = "+suputil+", supcons = "+supcons+" WHERE oficina = '"+agencia+"' and numexpc = '"+numexpc +"' and ag_obj = '"+ag_objeto+"' and objeto = '"+objeto+"' and  num_tas = '"+numtas+"'";
                    if (Utilidades.Conexion.update(sUpdate, conexion) == 1) 
                    {
                        conexion.commit();
                        logger.info("numero: "+fila);
                        logger.info("Actualización actualizada  oficina = '"+agencia+"' and numexpc = '"+numexpc +"' and ag_obj = '"+ag_objeto+"' and objeto = '"+objeto+"' and  num_tas = '"+numtas);
                    }
                    else
                    {
                        conexion.rollback();
                        logger.info("numero: "+fila);
                        logger.info("Actualización NO ACTUALIZADA  oficina = '"+agencia+"' and numexpc = '"+numexpc +"' and ag_obj = '"+ag_objeto+"' and objeto = '"+objeto+"' and  num_tas = '"+numtas);
                    }
                    
                            
                }
                else
                {
                    logger.info("numero: "+fila);
                    logger.info("Actualización NO ENCONTRADA NUMPET: "+rs.getString("numpet")+" oficina = '"+agencia+"' and numexpc = '"+numexpc +"' and ag_obj = '"+ag_objeto+"' and objeto = '"+objeto+"' and  num_tas = '"+numtas);
                }
                
            }//for
            conexion.close();
         }//try
         catch (Exception ex)
         {
             logger.info("Excepcion Actualización. oficina = '"+agencia+"' and numexpc = '"+numexpc +"' and ag_obj = '"+ag_objeto+"' and objeto = '"+objeto+"' and  num_tas = '"+numtas+". Descripción: "+ex.toString());
         }
         finally
        {
            try
            {
                if (conexion != null && !conexion.isClosed()) conexion.close();                
            }
            catch (java.sql.SQLException sqlException)
            {     
                logger.info("Imposible cerrar conexión con la B.D Informix");
            }             
        }//finally     
    }
    
    public void cargaPretasaExcel5() 
    {//actualizamos los registros dados de alta el pretasaciones desde los documentos excel con los datos dejados 
     //por UCI en la b23xt. 2 casos de actualizacion:
        //1.- si el registro de la b23xt no existe en pretasaciones los datos de alta
        //2.- si el registro de la b23xt si existe debemos igualar los campos pretasaciones.num_tas = b23xt.o5br, para
        //que cuando haya que enviar a UCI los datos no haya problemas.
        
        
        java.sql.Connection conexion = null;
        
        String sConsulta = null;
        String sConsulta2 = null;       
        String sActualizacion = null;
        
        int contadorActualizados = 0;
        int contadorInsertados = 0;
        int contadorCoincidentes = 0;
        
        int totalAProcesar = 0;
        int totalVueltas = 0;
        int porVuelta = 2000;
        int vuelta = 1;
        
        HSSFWorkbook libro = null;
        HSSFSheet hojaActualizados = null;
        HSSFSheet hojaInsertados   = null;
        HSSFSheet hojaCoincidentes = null;
        
        try
        {
            //FICHERO LOG4J
             PropertyConfigurator.configure("/data/informes/cargapretasaExcel/" + "Log4j.properties");                                       
            
           conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@192.168.3.215:1521:rvtn");
           conexion.setAutoCommit(false);
           
           sConsulta = "SELECT count(*) FROM b23xt WHERE e299a = '7'";
           rs = Utilidades.Conexion.select(sConsulta,conexion);
           if (rs.next()) totalAProcesar = rs.getInt(1);
           totalVueltas = totalAProcesar / porVuelta;
           int resto =  totalAProcesar%porVuelta;
           if (resto > 0) totalVueltas ++;  
           conexion.close();
           System.out.println ("TotalVueltas: "+totalVueltas);
           while (totalVueltas > 0)
           {
               // Se crea el libro
                libro = new HSSFWorkbook();
            // Se crea una hoja dentro del libro
                hojaActualizados =   libro.createSheet("ACTUALIZADOS");
                hojaInsertados   =   libro.createSheet("INSERTADOS");
                hojaCoincidentes =   libro.createSheet("COINCIDENTES");
                contadorActualizados = 0;
                contadorInsertados = 0;
                contadorCoincidentes = 0;
               
               
               System.out.println ("Vuelta numero: "+totalVueltas);
               conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@192.168.3.215:1521:rvtn");
               conexion.setAutoCommit(false);
                              
               sConsulta = "SELECT * FROM b23xt WHERE e299a = '7'";
               rs = Utilidades.Conexion.select(sConsulta,conexion);
               while (rs.next() && vuelta <= porVuelta)
               {
                   /*
                    *   OFICINA              VARCHAR2(4)   
                        NUMEXPC              NUMBER(10)    
                        AG_OBJ               VARCHAR2(2)   
                        OBJETO               VARCHAR2(6)   
                        NUM_TAS              VARCHAR2(2)  
                    * 
                    *   ABCD   NOT NULL VARCHAR2(2)   
                        ALCD            NUMBER(10)    
                        HUCD            VARCHAR2(2)   
                        A5CD            VARCHAR2(6)   
                        O5BR            NUMBER(5) 
                    */
                   System.out.println ("Subvuelta numero: "+vuelta);
                   
                   sConsulta2 =  "SELECT * FROM pretasaciones where oficina = '"+rs.getString("abcd")+"' AND numexpc = "+rs.getString("alcd");
                   sConsulta2 += " AND ag_obj = '"+rs.getString("hucd")+"' AND objeto = '"+rs.getString("A5CD")+"' AND tipopretasa in ('a','b')";
                   rs2 = Utilidades.Conexion.select(sConsulta2,conexion);
                   if (rs2.next())
                   {//tenemos la pretasación en pretasaciones dada de alta inicialmente con num_tas = 1. Tengo que ponerlo el que Beatriz
                    //haya indicado en la b23xt.
                       if (rs.getString("o5br").equals(rs2.getString("num_tas")))
                       {
                           contadorCoincidentes ++;
                           añadeFila (hojaCoincidentes,contadorCoincidentes);
                       }
                       else
                       {//actualizamos el pretasaciones en num_tas con el valor de la b23xt.
                           sActualizacion = "UPDATE pretasaciones SET num_tas = '"+rs.getString("o5br")+"' WHERE numpet = "+rs2.getString("numpet");
                           if (Utilidades.Conexion.update(sActualizacion, conexion) == 1) 
                           {
                                conexion.commit();
                                //conexion.rollback();
                                contadorActualizados ++;
                                añadeFila (hojaActualizados,contadorActualizados);

                           }
                           else
                           {
                                conexion.rollback();
                                logger.info("NO ACTUALIZADA en pretasaciones oficina = '"+rs.getString("abcd")+" numexpc = '"+rs.getString("alcd") +" ag_obj = '"+rs.getString("hucd")+" objeto = '"+rs.getString("a5cd")+" num_tas = '"+rs2.getString("num_tas")+" CON el num_tas = "+rs.getString("o5br"));
                           }

                       }

                   }
                   else
                   {//tenemos que insertar en pretasaciones el registro de la b23xt.
                           if (setPretacionesFromB23xt(conexion) == 1) 
                           {
                               conexion.commit();
                               //conexion.rollback();
                               contadorInsertados ++;
                               añadeFila (hojaInsertados,contadorInsertados);
                           }
                           else
                           {
                                conexion.rollback();
                                logger.info("NO INSERTADA en pretasaciones oficina = '"+rs.getString("abcd")+" numexpc = '"+rs.getString("alcd") +" ag_obj = '"+rs.getString("hucd")+" objeto = '"+rs.getString("a5cd")+" num_tas = '"+rs2.getString("num_tas"));
                           }

                   }
                   rs2.close();
                   rs2 = null;
                   //actualizamos el registro en b23xt cambiando el estado 7 a 8                   
                   sActualizacion =  "UPDATE b23xt SET e299a = '8' WHERE abcd = '"+rs.getString("abcd")+"' AND alcd = "+rs.getString("alcd");
                   sActualizacion += " AND hucd = '"+rs.getString("hucd")+"' AND A5CD = '"+rs.getString("A5CD")+"' AND o5br = '"+rs.getString("o5br")+"' AND e299a = '7'";
                   if (Utilidades.Conexion.update(sActualizacion, conexion) == 1) 
                   {
                        conexion.commit();
                        //conexion.rollback();                        

                   }
                   else
                   {
                        conexion.rollback();
                        logger.error("NO se puede cambiar en b23xt el estado 7 -->8  oficina = '"+rs.getString("abcd")+" numexpc = '"+rs.getString("alcd") +" ag_obj = '"+rs.getString("hucd")+" objeto = '"+rs.getString("a5cd")+" num_tas = '"+rs.getString("o5br"));
                   }
                   vuelta ++;

               }//WHILE
               rs.close();
               rs = null;
               conexion.close();
               String nombre = "CRUCE B23XT-PRETASACIONES-Vuelta"+totalVueltas+".XLS";
               FileOutputStream elFichero = new FileOutputStream("/data/informes/cargapretasaExcel/"+nombre);
               libro.write(elFichero);
               elFichero.close();               
               totalVueltas --;
               vuelta = 1;
           }//while totalVueltas
           
           
        }//try
        catch (Exception e)
        {
            logger.error("Error b23xt--pretasaciones. Excepción: "+e.toString());
        }
        finally
        {
            try
            {
                if (conexion != null && !conexion.isClosed()) conexion.close();                
            }
            catch (java.sql.SQLException sqlException)
            {     
                logger.error("Imposible cerrar conexión con la B.D Informix");
            }             
        }//finally     
    }
    
    
    public final void cargaPretasaExcelMasivas() //QUITAR LOS SYSTEM.OUT
    {//cargamos de la tabla b23xt todos los registros en estado 7 y lo cargamos en la tabla de pretasaciones:
        // "estado","0"
        // si ("sltx").equals("ORIGEN: I") tipopretasa = "a"
        // si ("sltx").equals("ORIGEN: D") tipopretasa = "b"
        //oConsulta.insert("fchuci","22-06-2011",Utilidades.Consultas.DATE);
        //oConsulta.insert("horauci","14:00:00",Utilidades.Consultas.VARCHAR);

        //si el registro que llega en la b23xt ya existe en pretasaciones se cataloga como coincidente y no se da de alta el pretasaciones
        //si el registro que llega en la b23xt no existe en pretasaciones se cataloga como insertado dandose de alta en pretasaciones
        
        
        java.sql.Connection conexion = null;
        
        String sConsulta = null;
        String sConsulta2 = null;       
        String sActualizacion = null;
        
        int contadorActualizados = 0;
        int contadorInsertados = 0;
        int contadorCoincidentes = 0;
        
        int totalAProcesar = 0;
        int totalVueltas = 0;
        int porVuelta = 2000;
        int vuelta = 1;
        
        HSSFWorkbook libro = null;
        //HSSFSheet hojaActualizados = null;
        HSSFSheet hojaInsertados   = null;
        HSSFSheet hojaCoincidentes = null;
        
        try
        {
            //FICHERO LOG4J
             PropertyConfigurator.configure("/data/informes/cargapretasaExcel/" + "Log4j.properties");                                       
            
           conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@orabackup:1521:rvtn");
           conexion.setAutoCommit(false);
           
           sConsulta = "SELECT count(*) FROM b23xt WHERE e299a = '7'";
           rs = Utilidades.Conexion.select(sConsulta,conexion);
           if (rs.next()) totalAProcesar = rs.getInt(1);
           totalVueltas = totalAProcesar / porVuelta;
           int resto =  totalAProcesar%porVuelta;
           if (resto > 0) totalVueltas ++; 
           
           conexion.close();
           
           //System.out.println ("TotalVueltas: "+totalVueltas);
           while (totalVueltas > 0)
           {
               // Se crea el libro
                libro = new HSSFWorkbook();
            // Se crea una hoja dentro del libro                
                hojaInsertados   =   libro.createSheet("INSERTADOS");
                hojaCoincidentes =   libro.createSheet("COINCIDENTES");                
                contadorInsertados = 0;
                contadorCoincidentes = 0;
               
               
               //System.out.println ("Vuelta numero: "+totalVueltas);
               conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@192.168.3.215:1521:rvtn");
               conexion.setAutoCommit(false);
                              
               sConsulta = "SELECT * FROM b23xt WHERE e299a = '7'";
               rs = Utilidades.Conexion.select(sConsulta,conexion);
               while (rs.next() && vuelta <= porVuelta)
               {
                   
                   //System.out.println ("Subvuelta numero: "+vuelta);
                   
                   sConsulta2 =  "SELECT * FROM pretasaciones where oficina = '"+rs.getString("abcd")+"' AND numexpc = "+rs.getString("alcd");
                   sConsulta2 += " AND ag_obj = '"+rs.getString("hucd")+"' AND objeto = '"+rs.getString("A5CD")+"' AND num_tas = '"+rs.getString("O5BR")+"'";
                   rs2 = Utilidades.Conexion.select(sConsulta2,conexion);
                   if (rs2.next())
                   {//tenemos la pretasación en pretasaciones dada de alta
                       
                           contadorCoincidentes ++;
                           añadeFila (hojaCoincidentes,contadorCoincidentes);                      

                   }
                   else
                   {//tenemos que insertar en pretasaciones el registro de la b23xt.
                           if (setPretacionesFromB23xt(conexion) == 1) 
                           {
                               conexion.commit();                               
                               contadorInsertados ++;
                               añadeFila (hojaInsertados,contadorInsertados);
                           }
                           else
                           {
                                conexion.rollback();
                                logger.info("NO INSERTADA en pretasaciones oficina = '"+rs.getString("abcd")+" numexpc = '"+rs.getString("alcd") +" ag_obj = '"+rs.getString("hucd")+" objeto = '"+rs.getString("a5cd")+" num_tas = '"+rs.getString("o5br"));
                           }

                   }
                   rs2.close();
                   rs2 = null;
                   //actualizamos el registro en b23xt cambiando el estado 7 a 8                   
                   sActualizacion =  "UPDATE b23xt SET e299a = '8' WHERE abcd = '"+rs.getString("abcd")+"' AND alcd = "+rs.getString("alcd");
                   sActualizacion += " AND hucd = '"+rs.getString("hucd")+"' AND A5CD = '"+rs.getString("A5CD")+"' AND o5br = '"+rs.getString("o5br")+"' AND e299a = '7'";
                   if (Utilidades.Conexion.update(sActualizacion, conexion) == 1) 
                   {
                        conexion.commit();                                              

                   }
                   else
                   {
                        conexion.rollback();
                        logger.error("NO se puede cambiar en b23xt el estado 7 -->8  oficina = '"+rs.getString("abcd")+" numexpc = '"+rs.getString("alcd") +" ag_obj = '"+rs.getString("hucd")+" objeto = '"+rs.getString("a5cd")+" num_tas = '"+rs.getString("o5br"));
                   }
                   vuelta ++;

               }//WHILE
               rs.close();
               rs = null;
               
               conexion.close();
               
               String nombre = "CRUCE B23XT-PRETASACIONES-Vuelta"+totalVueltas+".XLS";
               FileOutputStream elFichero = new FileOutputStream("/data/informes/cargapretasaExcel/"+nombre);
               libro.write(elFichero);
               elFichero.close();               
               totalVueltas --;
               vuelta = 1;
           }//while totalVueltas
           
           
        }//try
        catch (Exception e)
        {
            logger.error("Error b23xt--pretasaciones. Excepción: "+e.toString());
        }
        finally
        {
            try
            {
                if (conexion != null && !conexion.isClosed()) conexion.close();                
            }
            catch (java.sql.SQLException sqlException)
            {     
                logger.error("Imposible cerrar conexión con la B.D Informix");
            }             
        }//finally     
    }
    
    
    private void añadeFila(HSSFSheet hoja,int contador) 
    {
        HSSFRow fila = null;
        HSSFCell celda = null;
        
        try
        {           
         
            fila = hoja.createRow(contador);
        
            celda = fila.createCell((short) 0); 
            celda.setCellValue(rs.getString("abcd"));
            celda = fila.createCell((short) 1); 
            celda.setCellValue(rs.getString("alcd"));
            celda = fila.createCell((short) 2); 
            celda.setCellValue(rs.getString("hucd"));
            celda = fila.createCell((short) 3); 
            celda.setCellValue(rs.getString("a5cd"));
            celda = fila.createCell((short) 4); 
            celda.setCellValue(rs.getString("o5br"));
            
            fila = null;
            
        }
        catch (Exception e)
        {
            logger.error("Excepción en la generación de excel."+e.toString());
        }
        
    }//añadeFila
    
    private void añadeFila2 (HSSFSheet hoja,int contador, String valor) 
    {
        HSSFRow fila = null;
        HSSFCell celda = null;
        
        try
        {           
                     
            fila = hoja.createRow(contador);
        
            celda = fila.createCell((short) 0); 
            celda.setCellValue(valor);            
            
            fila = null;
            
        }
        catch (Exception e)
        {
            logger.error("Excepción en la generación de excel."+e.toString());
        }
        
    }//añadeFila
    
    
    
    private int setPretacionesFromB23xt(java.sql.Connection conexion) throws java.sql.SQLException,Exception
    {
        int iNumeroRegistrosAfectados = 0;
        int iContadorTotal = 0;
        Utilidades.Consultas oConsulta = null;
        java.sql.ResultSet rsAux = null;
        
        try
        {
            oConsulta = new Utilidades.Consultas(Utilidades.Consultas.INSERT);
            oConsulta.from("PRETASACIONES");
            rsAux = Utilidades.Valtecnic.getProvincias(Integer.parseInt(Utilidades.Cadenas.completTextWithLeftCharacter(rs.getString("e29ba"),'0',5).substring(0,2)),conexion);
            if(rsAux.next()) oConsulta.insert("coddel",Utilidades.Cadenas.getValorSinBlancos(rsAux,"coddel"),Utilidades.Consultas.INT);
            //oConsulta.insert("numpet",Integer.toString(maximoNumeroPeticion(conexion)),Utilidades.Consultas.INT);
            oConsulta.insert("numpet",Integer.toString(Utilidades.Conexion.getSequenceNextVal("SEQ_PRETASA", conexion)),Utilidades.Consultas.INT);
            oConsulta.insert("numpreta",Utilidades.Cadenas.getValorMostrarWeb(rs,"hucd")+Utilidades.Cadenas.getValorMostrarWeb(rs,"a5cd"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("codcli","129",Utilidades.Consultas.INT);            
            oConsulta.insert("oficina",Utilidades.Cadenas.getValorSinBlancos(rs,"abcd"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("numexpc",Utilidades.Cadenas.getValorSinBlancos(rs,"alcd"),Utilidades.Consultas.INT);
            oConsulta.insert("ag_obj",Utilidades.Cadenas.getValorSinBlancos(rs,"hucd"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("objeto",Utilidades.Cadenas.getValorSinBlancos(rs,"a5cd"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("num_tas",Utilidades.Cadenas.getValorSinBlancos(rs,"o5br"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("naturbien",Utilidades.Cadenas.getValorSinBlancos(rs,"aist"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("ubica",Utilidades.Cadenas.getValorSinBlancos(rs,"vl8oa"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("acabslo",Utilidades.Cadenas.getValorSinBlancos(rs,"vl8pa"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("acabpd",Utilidades.Cadenas.getValorSinBlancos(rs,"vl8qa"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("carpext",Utilidades.Cadenas.getValorSinBlancos(rs,"vl8ra"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("planta",Utilidades.Cadenas.getValorSinBlancos(rs,"vl8sa"),Utilidades.Consultas.INT);
            oConsulta.insert("antig",Utilidades.Cadenas.getValorSinBlancos(rs,"vl8ta"),Utilidades.Consultas.INT);
            oConsulta.insert("ndormit",Utilidades.Cadenas.getValorSinBlancos(rs,"vl8ua"),Utilidades.Consultas.INT);
            oConsulta.insert("nbanos",Utilidades.Cadenas.getValorSinBlancos(rs,"vl8va"),Utilidades.Consultas.INT);
            oConsulta.insert("ascens",Utilidades.Cadenas.getValorSinBlancos(rs,"vl8wa"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("calefacc",Utilidades.Cadenas.getValorSinBlancos(rs,"vl8xa"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("estcons",Utilidades.Cadenas.getValorSinBlancos(rs,"vl8ya"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("calcons",Utilidades.Cadenas.getValorSinBlancos(rs,"w11qa"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("suputil",Utilidades.Cadenas.getValorDecimalBBDD(rs,"a2va"),Utilidades.Consultas.INT);
            oConsulta.insert("supcons",Utilidades.Cadenas.getValorDecimalBBDD(rs,"cyva"),Utilidades.Consultas.INT);
            oConsulta.insert("supparc",Utilidades.Cadenas.getValorDecimalBBDD(rs,"z1va"),Utilidades.Consultas.INT);
            if (rs.getString("sltx").equals("ORIGEN: I")) 
            {
                oConsulta.insert("tipopretasa","a",Utilidades.Consultas.VARCHAR);
            }
            if (rs.getString("sltx").equals("ORIGEN: D")) 
            {
                oConsulta.insert("tipopretasa","b",Utilidades.Consultas.VARCHAR);
            }
            
            oConsulta.insert("tipologia",Utilidades.Cadenas.getValorSinBlancos(rs,"e33eba"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("esvpo",Utilidades.Cadenas.getValorSinBlancos(rs,"pist"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("exptevpo",Utilidades.Cadenas.getValorSinBlancos(rs,"e3k3a"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("fchvpo",Utilidades.Cadenas.getValorMostrarWeb(rs,"dddt"),Utilidades.Consultas.DATE);
            oConsulta.insert("autopromocion",Utilidades.Cadenas.getValorSinBlancos(rs,"q2ts"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("finca",Utilidades.Cadenas.getValorSinBlancos(rs,"hrnb"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("registro",Utilidades.Cadenas.getValorSinBlancos(rs,"hsnb"),Utilidades.Consultas.INT);
            oConsulta.insert("localireg",Utilidades.Cadenas.getValorSinBlancos(rs,"g4tx"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("titulareg",Utilidades.Cadenas.getValorSinBlancos(rs,"s4tx"),Utilidades.Consultas.VARCHAR);
            //oConsulta.insert("fcatastral",Utilidades.Cadenas.getValorSinBlancos(rs,"s4tx"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("vivnueva",Utilidades.Cadenas.getValorSinBlancos(rs,"wost"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("plantassras",Utilidades.Cadenas.getValorSinBlancos(rs,"e33eca"),Utilidades.Consultas.INT);
            oConsulta.insert("sotanos",Utilidades.Cadenas.getValorSinBlancos(rs,"e33eda"),Utilidades.Consultas.INT);
            oConsulta.insert("nplantas",Utilidades.Cadenas.getValorSinBlancos(rs,"e33eea"),Utilidades.Consultas.INT);
            oConsulta.insert("reforbloq",Utilidades.Cadenas.getValorSinBlancos(rs,"e33efa"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("fchreforbloq",Utilidades.Cadenas.getValorMostrarWeb(rs,"e33ega"),Utilidades.Consultas.DATE);
            oConsulta.insert("ite",Utilidades.Cadenas.getValorSinBlancos(rs,"e33eha"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("fchite",Utilidades.Cadenas.getValorMostrarWeb(rs,"e33eia"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("reforviv",Utilidades.Cadenas.getValorSinBlancos(rs,"e33eja"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("fchreforviv",Utilidades.Cadenas.getValorMostrarWeb(rs,"e33eka"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("caliacabados",Utilidades.Cadenas.getValorSinBlancos(rs,"e33ela"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("aireacond",Utilidades.Cadenas.getValorSinBlancos(rs,"e33ema"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("telefonia",Utilidades.Cadenas.getValorSinBlancos(rs,"e33ena"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("otrasinstal",Utilidades.Cadenas.getValorSinBlancos(rs,"e33eoa"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("anxpiscina",Utilidades.Cadenas.getValorSinBlancos(rs,"e33epa"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("anxgaraje",Utilidades.Cadenas.getValorSinBlancos(rs,"f115qa"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("anxtrastero",Utilidades.Cadenas.getValorSinBlancos(rs,"f115pa"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("anxporche",Utilidades.Cadenas.getValorSinBlancos(rs,"e33eqa"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("anxotro",Utilidades.Cadenas.getValorSinBlancos(rs,"e33era"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("piscinaurb",Utilidades.Cadenas.getValorSinBlancos(rs,"e33esa"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("jardinesurb",Utilidades.Cadenas.getValorSinBlancos(rs,"e33eta"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("otrosurb",Utilidades.Cadenas.getValorSinBlancos(rs,"e33eua"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("constanio",Utilidades.Cadenas.getValorSinBlancos(rs,"e33eva"),Utilidades.Consultas.INT);
            oConsulta.insert("comentc",Utilidades.Cadenas.getValorSinBlancos(rs,"sltx"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("valtotpretaviv",Utilidades.Cadenas.getValorDecimalBBDD(rs,"e33ewa"),Utilidades.Consultas.INT);
            oConsulta.insert("valunitpretaviv",Utilidades.Cadenas.getValorDecimalBBDD(rs,"e33exa"),Utilidades.Consultas.INT);
            oConsulta.insert("valtotpretatrt",Utilidades.Cadenas.getValorDecimalBBDD(rs,"e33eya"),Utilidades.Consultas.INT);
            oConsulta.insert("valunitpretatrt",Utilidades.Cadenas.getValorDecimalBBDD(rs,"e33eza"),Utilidades.Consultas.INT);
            oConsulta.insert("valtotpretapz",Utilidades.Cadenas.getValorDecimalBBDD(rs,"e33f0a"),Utilidades.Consultas.INT);
            oConsulta.insert("valunitpretapz",Utilidades.Cadenas.getValorDecimalBBDD(rs,"e33f1a"),Utilidades.Consultas.INT);
            oConsulta.insert("valtotrentviv",Utilidades.Cadenas.getValorDecimalBBDD(rs,"e33f2a"),Utilidades.Consultas.INT);
            oConsulta.insert("valunitrentviv",Utilidades.Cadenas.getValorDecimalBBDD(rs,"e33f3a"),Utilidades.Consultas.INT);
            oConsulta.insert("valtotrentrt",Utilidades.Cadenas.getValorDecimalBBDD(rs,"e33f4a"),Utilidades.Consultas.INT);
            oConsulta.insert("valunitrentrt",Utilidades.Cadenas.getValorDecimalBBDD(rs,"e33f5a"),Utilidades.Consultas.INT);
            oConsulta.insert("valtotrentpz",Utilidades.Cadenas.getValorDecimalBBDD(rs,"e33f6a"),Utilidades.Consultas.INT);
            oConsulta.insert("valunitrentpz",Utilidades.Cadenas.getValorDecimalBBDD(rs,"e33f7a"),Utilidades.Consultas.INT);
            oConsulta.insert("valorpretasa",Utilidades.Cadenas.getValorDecimalBBDD(rs,"e29fa"),Utilidades.Consultas.INT);
            oConsulta.insert("estado","0",Utilidades.Consultas.VARCHAR);
            oConsulta.insert("fchuci","14-12-2011",Utilidades.Consultas.DATE);
            oConsulta.insert("horauci","11:00:00",Utilidades.Consultas.VARCHAR);
            oConsulta.insert("fchval",Utilidades.Cadenas.getValorMostrarWeb(rs,"v59ya"),Utilidades.Consultas.DATE);
            oConsulta.insert("horaval",Utilidades.Cadenas.getValorSinBlancos(rs,"e29aa"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("postalv",Utilidades.Cadenas.getValorSinBlancos(rs,"e29ba"),Utilidades.Consultas.INT);
            oConsulta.insert("provinv",Utilidades.Cadenas.getValorSinBlancos(rs,"w11aa"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("municiv",Utilidades.Cadenas.getValorSinBlancos(rs,"w11ba"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("localiv",Utilidades.Cadenas.getValorSinBlancos(rs,"w11ca"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("codsituv",Utilidades.Cadenas.getValorSinBlancos(rs,"w11da"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("situacionv",Utilidades.Cadenas.getValorSinBlancos(rs,"w11ea"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("tipoviav",Utilidades.Cadenas.getValorSinBlancos(rs,"w11fa"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("callev",Utilidades.Cadenas.getValorSinBlancos(rs,"w11ga"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("numerov",Utilidades.Cadenas.getValorSinBlancos(rs,"w11ha"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("escalerav",Utilidades.Cadenas.getValorSinBlancos(rs,"w11ia"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("plantav",Utilidades.Cadenas.getValorSinBlancos(rs,"w11ja"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("puertav",Utilidades.Cadenas.getValorSinBlancos(rs,"w11ka"),Utilidades.Consultas.VARCHAR);
            oConsulta.insert("VVENTAR",Utilidades.Cadenas.getValorDecimalBBDD(rs,"F45N8A"),Utilidades.Consultas.INT);
            oConsulta.insert("OTRATAS",Utilidades.Cadenas.getValorSinBlancos(rs,"F45N9A"),Utilidades.Consultas.VARCHAR);
            iNumeroRegistrosAfectados = Utilidades.Conexion.insert(oConsulta.getSql(), conexion);
            if(iNumeroRegistrosAfectados==1)
            {
                iContadorTotal++;
            }
        
            oConsulta = null;
        }//try
        catch (Exception e)
        {
            iContadorTotal = 0;
        }
        finally
        {
            return iContadorTotal;  
        }
    }//setPretacionesFromB23xt

    private int maximoNumeroPeticion(java.sql.Connection conexion) throws java.sql.SQLException
    {
        int iMaximo = -1;
        Utilidades.Consultas oConsulta = new Utilidades.Consultas(Utilidades.Consultas.SELECT);
        oConsulta.select("MAX(NUMPET) AS MAXIMO");
        oConsulta.from("PRETASACIONES");
        java.sql.ResultSet rsDatos = Utilidades.Conexion.select(oConsulta.getSql(),conexion);
        if(rsDatos.next()) iMaximo = rsDatos.getInt("MAXIMO")+1;
        if(rsDatos!=null) rsDatos.close();
        rsDatos = null;
        oConsulta = null;
        return iMaximo;
    }//maximoNumeroPeticion

    private void cargaPretasacionesNOUCI()
    {
       
        String sConsulta = "";
        String consulta = "";
        boolean seguir = true;
        String solicitudes = "";
        String otrastas = "";
        String elemento5 = "";
        String numexp = null;
        Pretasaciones preta = null;
        String tipoinm = null;
        java.sql.ResultSet rs2 = null;
        
        String postalv = null;
                    String municiv = null;
                    String localiv = null;
                    String provinv = null;
                    String codsituv = null;
                    String situacionv = null;
                    String tipoviav   = null;
                    String callev     = null;
                    String  numerov   = null;
                    String escalerav  = null;
                    String plantav    = null;
                    String  puertav   = null;
                    String numexpv    = null;
        
        java.sql.Connection conexion = null;
        String sUrlExcel = "/data/informes/cargapretasaExcel/deutschebank/carga con expediente.xls";        
        //FICHERO LOG4J
        PropertyConfigurator.configure("/data/informes/cargapretasaExcel/deutschebank/" + "Log4j.properties");   
        try
        {
            //libro para registrar lo que localizamos y lo que no
            HSSFWorkbook libro = null;                    
            HSSFSheet hojaCoincidentes = null;
             // Se crea el libro
                libro = new HSSFWorkbook();
            // Se crea una hoja dentro del libro
                hojaCoincidentes =   libro.createSheet("COINCIDENTES");
                
            //conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:desarrollo/des0329@orabackup:1521:rvtn");
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@orabackup:1521:rvtn");
            conexion.setAutoCommit(false);
            String hoja = "Hoja1";   //nombre de la hora
            Utilidades.Excel oExcel = new Utilidades.Excel(sUrlExcel);            
            org.apache.poi.hssf.usermodel.HSSFCell celda = null;
            
            //lectura de datos del Excel.
            //celda de los datos
            String valor = null;
            Double iValor = 0.0;
            String cFinca14 = null;
            String cProvin12 = null;
            String cNif2 = null;
            int finicio = 283;
            int ffin = 284;            
            for (int fila = finicio; fila < ffin;fila ++)              
            {
                seguir = true;
                valor = null;
                cFinca14 = null;
                cProvin12 = null;
                cNif2 = null;
                numexp = null;
                numexpv    = null;
                postalv = null;
                municiv = null;
                     localiv = null;
                     provinv = null;
                     codsituv = null;
                     situacionv = null;
                     tipoviav   = null;
                     callev     = null;
                      numerov   = null;
                     escalerav  = null;
                     plantav    = null;
                      puertav   = null;
                
                try
                {
                    //finca
                    try
                    {                        
                    
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,14);                
                        if (celda != null) 
                        {
                            valor = celda.getStringCellValue();
                            if (celda != null)
                            {
                                cFinca14 = valor;
                            }
                        }      
                    }
                    catch (NumberFormatException nfe)
                    {
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,14);                
                        if (celda != null) 
                        {
                            iValor = celda.getNumericCellValue();                            
                            if (iValor != null)  cFinca14 = Integer.toString((int) Math.floor(iValor));
                            
                        }      
                    }
                    //Provincia
                    celda = oExcel.getCeldaFilaHoja(hoja,fila,12);                
                    if (celda != null) 
                    {
                        valor = celda.getStringCellValue();
                        if (celda != null)
                        {
                            cProvin12 = valor;
                        }
                    } 
                    //nif
                    celda = oExcel.getCeldaFilaHoja(hoja,fila,2);                
                    if (celda != null) 
                    {
                        valor = celda.getStringCellValue();
                        if (celda != null)
                        {
                            cNif2 = valor;
                        }
                        if (cNif2 != null && cNif2.length()>1)
                        {
                            if (cNif2.substring(0, 1).equals("0") || !isNumeric(cNif2.substring(0, 1)))
                            {
                                cNif2 = '%'+cNif2.substring(1, cNif2.length());
                            }
                            if (!isNumeric(cNif2.substring(cNif2.length()-1, cNif2.length())))
                            {
                                cNif2 = cNif2.substring(0, cNif2.length()-1)+"%";
                            }
                        }
                    }
                    
                    //codigo postal
                    try
                    {                        
                    
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,13);                
                        if (celda != null) 
                        {
                            valor = celda.getStringCellValue();
                            if (celda != null)
                            {
                                postalv = valor.trim();
                            }
                        }      
                    }
                    catch (NumberFormatException nfe)
                    {
                        try
                        {
                            celda = oExcel.getCeldaFilaHoja(hoja,fila,13);                
                            if (celda != null) 
                            {
                                iValor = celda.getNumericCellValue();                            
                                if (iValor != null)  postalv = Integer.toString((int) Math.floor(iValor));
                            
                            }      
                        }
                        catch (Exception e)
                        {
                            postalv = null;
                        }
                    }
                    
                                        
                    //municipio
                     celda = oExcel.getCeldaFilaHoja(hoja,fila,11);                
                    if (celda != null) 
                    {
                        valor = celda.getStringCellValue();
                        if (celda != null)
                        {
                            municiv = valor.trim();
                            if (municiv.length() > 26) municiv = municiv.substring(0, 26);
                        }
                    } 
                        
                    //localidad
                        localiv = municiv;
                        
                    //provincia    
                    celda = oExcel.getCeldaFilaHoja(hoja,fila,12);                
                    if (celda != null) 
                    {
                        valor = celda.getStringCellValue();
                        if (celda != null)
                        {
                            provinv = valor.trim();
                        }
                    } 
                                                                
                    //calle    
                    celda = oExcel.getCeldaFilaHoja(hoja,fila,7);                
                    if (celda != null) 
                    {
                        valor = celda.getStringCellValue();
                        if (celda != null)
                        {
                            callev = valor.trim();
                        }
                    } 
                        
                    //numero   
                    try
                    {                        
                    
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,8);                
                        if (celda != null) 
                        {
                            valor = celda.getStringCellValue();
                            if (celda != null)
                            {
                                numerov = valor.trim();
                            }
                        }      
                    }
                    catch (NumberFormatException nfe)
                    {
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,8);                
                        if (celda != null) 
                        {
                            iValor = celda.getNumericCellValue();                            
                            if (iValor != null)  numerov = Integer.toString((int) Math.floor(iValor));
                            
                        }      
                    }                        
                    
                    //planta   
                    try
                    {                        
                    
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,9);                
                        if (celda != null) 
                        {
                            valor = celda.getStringCellValue();
                            if (celda != null)
                            {
                                plantav = valor.trim();
                            }
                        }      
                    }
                    catch (NumberFormatException nfe)
                    {
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,9);                
                        if (celda != null) 
                        {
                            iValor = celda.getNumericCellValue();                            
                            if (iValor != null)  plantav = Integer.toString((int) Math.floor(iValor));
                            
                        }      
                    }        
                        
                    //puerta    
                    try
                    {                        
                    
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,10);                
                        if (celda != null) 
                        {
                            valor = celda.getStringCellValue();
                            if (celda != null)
                            {
                                puertav = valor.trim();
                            }
                        }      
                    }
                    catch (NumberFormatException nfe)
                    {
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,10);                
                        if (celda != null) 
                        {
                            iValor = celda.getNumericCellValue();                            
                            if (iValor != null)  puertav = Integer.toString((int) Math.floor(iValor));
                            
                        }      
                    }     
                    
                    //numexpv    
                    celda = oExcel.getCeldaFilaHoja(hoja,fila,21);                
                    if (celda != null) 
                    {
                        valor = celda.getStringCellValue();
                        if (celda != null)
                        {
                            numexpv = valor.trim();
                        }
                    } 
                    if (numexpv != null && !numexpv.equals("")) 
                    {//ya está localizado. No le volvemos a procesar.
                        seguir = true;
                        //añadeFila2 (hojaCoincidentes,fila,"");
                        logger.info("Fila: "+Integer.toString(fila+1)+"LOCALIZADO: "+numexpv);
                    }
                        
                }
                catch (Exception e)
                {
                    seguir = false;                    
                    añadeFila2 (hojaCoincidentes,fila,"");
                    logger.error("Fila: "+Integer.toString(fila+1)+"ERROR: "+e.toString());
                }
                                
                
            if (seguir)
            {
                //buscamos la tasación en el sistema
                numexp = null;
                //consulta realizada en la primera pasada sConsulta = "SELECT * FROM solicitudes s JOIN datosreg d on s.numexp = d.numexp where nifsolici like '"+cNif2+"' and d.finca = '"+cFinca14+"' and s.provin = '"+cProvin12+"' and s.codcli in (752,852,452,152,193,252,552,652)";
                sConsulta = "SELECT * FROM solicitudes s where s.numexp ='"+numexpv+"' and s.codcli in (752,852,452,152,193,252,552,652)";
                rs = Utilidades.Conexion.select(sConsulta,conexion);
                if (rs.next())
                {
                    numexp = rs.getString("numexp");
                    solicitudes = "solicitudes";
                    otrastas = "otrastas";
                    elemento5 = "elemento5";
                }                
                else
                {//buscamos en historico
                  rs.close();
                  rs = null;
                  //consulta realizada en la primera pasada sConsulta = "SELECT * FROM his_solicitudes s JOIN his_datosreg d on s.numexp = d.numexp where nifsolici like '"+cNif2+"' and d.finca = '"+cFinca14+"' and s.provin = '"+cProvin12+"' and s.codcli in (752,852,452,152,193,252,552,652)";
                  sConsulta = "SELECT * FROM his_solicitudes s where s.numexp ='"+numexpv+"' and s.codcli in (752,852,452,152,193,252,552,652)";
                  rs = Utilidades.Conexion.select(sConsulta,conexion);
                  if (rs.next())
                  {
                      numexp = rs.getString("numexp");
                      solicitudes = "his_solicitudes";
                      otrastas = "his_otrastas";
                      elemento5 = "his_elemento5";
                  }
                  else numexp = null;
                }

                //Si la encontramos lo insertamos en prentasaciones
                if (numexp != null)
                {
                    rs.close();
                    rs = null;
                    //1.- insertamos en la tabla de pretasaciones
                    preta = new Pretasaciones();
                    sConsulta = "SELECT * FROM "+solicitudes+" WHERE numexp='"+numexp+"'";
                    rs = Utilidades.Conexion.select(sConsulta,conexion);     
                    if (rs.next()) 
                    {
                        tipoinm = rs.getString("tipoinm");
                        preta.coddel = rs.getInt("delegado");
                        preta.codcli = rs.getInt("codcli");
                        preta.oficina = rs.getString("oficina");
                        preta.numexpc = 0;
                        preta.ag_obj = "";
                        preta.objeto = "";
                        preta.num_tas = "1";
                        if (postalv != null) preta.postalv = Integer.parseInt(postalv);
                        preta.municiv = municiv;
                        preta.localiv = localiv;
                        preta.provinv = provinv;
                        //preta.codsituv = rs.getString("codsitu");
                        //preta.situacionv = rs.getString("situacion");
                        //preta.tipoviav = rs.getString("tipovia");
                        preta.callev = callev;
                        preta.numerov = numerov;
                        preta.escalerav = escalerav;
                        preta.plantav = plantav;
                        preta.puertav = puertav;      

                        if (Funciones.Fcomunes.esTipologia(conexion, tipoinm) == 1)
                        {//tipologia 1
                           consulta = "SELECT suputil,cconparcom FROM "+elemento5+" WHERE numexp='"+numexp+"'";
                           rs2 = Utilidades.Conexion.select(consulta,conexion);     
                            if (rs2.next())  
                            {
                                if (rs2.getString("suputil") != null) preta.suputil = Double.parseDouble(rs2.getString("suputil"));
                                if (rs2.getString("cconparcom") != null) preta.supcons = Double.parseDouble(rs2.getString("cconparcom"));                            
                            }
                           //rs.close();
                        }
                        else
                        {//tipologia 2
                           consulta = "SELECT totsuperf FROM "+otrastas+"  WHERE numexp='"+numexp+"'";
                           rs2 = Utilidades.Conexion.select(consulta,conexion);     
                            if (rs2.next())  
                            {
                                if (rs2.getString("totsuperf") != null) preta.supcons = Double.parseDouble(rs2.getString("totsuperf"));                                                                                    
                            }                           
                        }
                        rs2.close();
                        rs2 = null;
                        //tipo de pretasaciones Actualizacion de valor
                    
                        preta.tipopretasa = "D";
                       //naturaleza del bien                        
                        preta.naturbien = "";
                        preta.finca = cFinca14;
                        preta.numexpv = numexp;
                        //preta.naturbien = "X";
                        //codigo situacion 
                        /*
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,8);
                        if (celda != null) 
                        {
                            if (celda.getStringCellValue().length() > 60) preta.codsituv = celda.getStringCellValue().substring(0, 60).toUpperCase();
                            else preta.codsituv = celda.getStringCellValue().toUpperCase();
                        }
                        //
                        //situacion
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,9);
                        if (celda != null) 
                        {
                            if (celda.getStringCellValue().length() > 60) preta.situacionv = celda.getStringCellValue().substring(0, 60).toUpperCase();
                            else preta.situacionv = celda.getStringCellValue().toUpperCase();
                        }
                        //tipo via
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,10);
                        if (celda != null) 
                        {
                            if (celda.getStringCellValue().length() > 60) preta.tipoviav = celda.getStringCellValue().substring(0, 60).toUpperCase();
                            else preta.tipoviav = celda.getStringCellValue().toUpperCase();
                        }
                        //calle
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,11);
                        if (celda != null) 
                        {
                            if (celda.getStringCellValue().length() > 60) preta.callev = celda.getStringCellValue().substring(0, 60).toUpperCase();
                            else preta.callev = celda.getStringCellValue().toUpperCase();
                        }
                        //numero
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,12);
                        if (celda != null) 
                        {
                            if (celda.getStringCellValue().length() > 60) preta.numerov = celda.getStringCellValue().substring(0, 60).toUpperCase();
                            else preta.numerov = celda.getStringCellValue().toUpperCase();
                        }
                        //escalera
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,13);
                        if (celda != null) 
                        {
                            if (celda.getStringCellValue().length() > 60) preta.escalerav = celda.getStringCellValue().substring(0, 60).toUpperCase();
                            else preta.escalerav = celda.getStringCellValue().toUpperCase();
                        }
                        //planta
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,14);
                        if (celda != null) 
                        {
                            if (celda.getStringCellValue().length() > 60) preta.plantav = celda.getStringCellValue().substring(0, 60).toUpperCase();
                            else preta.plantav = celda.getStringCellValue().toUpperCase();
                        }
                        //puerta
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,15);
                        if (celda != null) 
                        {
                            if (celda.getStringCellValue().length() > 60) preta.puertav = celda.getStringCellValue().substring(0, 60).toUpperCase();
                            else preta.puertav = celda.getStringCellValue().toUpperCase();
                        }
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,7);
                        preta.localiv = celda.getStringCellValue().toUpperCase();

                        celda = oExcel.getCeldaFilaHoja(hoja,fila,5);
                        preta.provinv = celda.getStringCellValue().toUpperCase();
                         * */
                        preta.otratas = "N";  //ya realizada por Valtecnic.
                        //esVPO
                        /*
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,18);
                        if (celda != null) 
                        {
                            preta.esvpo = celda.getStringCellValue();
                        } 
                         * 
                         */
                        if (preta.setPretacionesFromB23xt (conexion) > 0)
                        {
                            conexion.commit();   
                            //conexion.rollback();
                            //añadeFila2 (hojaCoincidentes,fila,numexp);
                            logger.info("Fila: "+Integer.toString(fila+1)+ "LOCALIZADO: "+numexp);
                        }
                        else 
                        {
                            conexion.rollback();
                            //añadeFila2 (hojaCoincidentes,fila,"");
                            logger.error("Fila: "+Integer.toString(fila+1)+ "LOCALIZADO (sin insertar): "+numexp);
                        }
                        //al final
                    }//if solicitudes/his_solicitudes  
              
                    preta = null;

                    //2.- escribimos el estado en el fichero de log.
                }
                else
                {                    
                    //añadeFila2 (hojaCoincidentes,fila,"");
                    logger.error("Fila: "+Integer.toString(fila+1)+"SIN LOCALIZAR");
                }
            }
         }//for
            //escribimos el libro
            //FileOutputStream elFichero = new FileOutputStream("/data/informes/cargapretasaExcel/deutschebank/carga2.xls");
            //libro.write(elFichero);
            //elFichero.close();               
            
        }
        catch (Exception e)
        {
            logger.error("Error general en la carga de pretasaciones. Descripción: "+e.toString());
        }
        finally
        {
            try
            {
                if (conexion != null && !conexion.isClosed()) conexion.close();                
            }
            catch (java.sql.SQLException sqlException)
            {     
                logger.error("Imposible cerrar conexión con la B.D"+sqlException.toString());
            }             
        }//finally     
        
    }//cargaPretasacionesNOUCI
    
    
    
    
    
    private void cargaPretasacionesNOUCI2()
    {
       
        String sConsulta = "";
        String consulta = "";
        boolean seguir = true;
        String solicitudes = "";
        String otrastas = "";
        String elemento5 = "";
        String numexp = null;
        Pretasaciones preta = null;
        String tipoinm = null;
        java.sql.ResultSet rs2 = null;
        
        String postalv = null;
                    String municiv = null;
                    String localiv = null;
                    String provinv = null;
                    String codsituv = null;
                    String situacionv = null;
                    String tipoviav   = null;
                    String callev     = null;
                    String  numerov   = null;
                    String escalerav  = null;
                    String plantav    = null;
                    String  puertav   = null;
                    String numexpv    = null;
        
        java.sql.Connection conexion = null;
        String sUrlExcel = "/data/informes/cargapretasaExcel/deutschebank/Libro1.xls";        
        //FICHERO LOG4J
        PropertyConfigurator.configure("/data/informes/cargapretasaExcel/deutschebank/" + "Log4j.properties");   
        try
        {
            //libro para registrar lo que localizamos y lo que no
            HSSFWorkbook libro = null;                    
            HSSFSheet hojaCoincidentes = null;
             // Se crea el libro
                libro = new HSSFWorkbook();
            // Se crea una hoja dentro del libro
                hojaCoincidentes =   libro.createSheet("COINCIDENTES");
                
            //conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:desarrollo/des0329@orabackup:1521:rvtn");
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@orabackup:1521:rvtn");
            conexion.setAutoCommit(false);
            String hoja = "Valtecnic";   //nombre de la hora
            Utilidades.Excel oExcel = new Utilidades.Excel(sUrlExcel);            
            org.apache.poi.hssf.usermodel.HSSFCell celda = null;
            
            //lectura de datos del Excel.
            //celda de los datos
            String valor = null;
            Double iValor = 0.0;
            String cFinca14 = null;
            String cProvin12 = null;
            String cNif2 = null;
            int finicio = 2;
            int ffin = 2329;            
            for (int fila = finicio; fila < ffin;fila ++)              
            {
                seguir = true;
                valor = null;
                cFinca14 = null;
                cProvin12 = null;
                cNif2 = null;
                numexp = null;
                numexpv    = null;
                postalv = null;
                municiv = null;
                     localiv = null;
                     provinv = null;
                     codsituv = null;
                     situacionv = null;
                     tipoviav   = null;
                     callev     = null;
                      numerov   = null;
                     escalerav  = null;
                     plantav    = null;
                      puertav   = null;
                
                try
                {
                    //finca
                    try
                    {                        
                    
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,14);                
                        if (celda != null) 
                        {
                            valor = celda.getStringCellValue();
                            if (celda != null)
                            {
                                cFinca14 = valor;
                            }
                        }      
                    }
                    catch (NumberFormatException nfe)
                    {
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,14);                
                        if (celda != null) 
                        {
                            iValor = celda.getNumericCellValue();                            
                            if (iValor != null)  cFinca14 = Integer.toString((int) Math.floor(iValor));
                            
                        }      
                    }
                    //Provincia
                    celda = oExcel.getCeldaFilaHoja(hoja,fila,12);                
                    if (celda != null) 
                    {
                        valor = celda.getStringCellValue();
                        if (celda != null)
                        {
                            cProvin12 = valor;
                        }
                    } 
                    //nif
                    celda = oExcel.getCeldaFilaHoja(hoja,fila,2);                
                    if (celda != null) 
                    {
                        valor = celda.getStringCellValue();
                        if (celda != null)
                        {
                            cNif2 = valor;
                        }
                        if (cNif2 != null && cNif2.length()>1)
                        {
                            if (cNif2.substring(0, 1).equals("0") || !isNumeric(cNif2.substring(0, 1)))
                            {
                                cNif2 = '%'+cNif2.substring(1, cNif2.length());
                            }
                            if (!isNumeric(cNif2.substring(cNif2.length()-1, cNif2.length())))
                            {
                                cNif2 = cNif2.substring(0, cNif2.length()-1)+"%";
                            }
                        }
                    }
                    
                    //codigo postal
                    try
                    {                        
                    
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,13);                
                        if (celda != null) 
                        {
                            valor = celda.getStringCellValue();
                            if (celda != null)
                            {
                                postalv = valor.trim();
                            }
                        }      
                    }
                    catch (NumberFormatException nfe)
                    {
                        try
                        {
                            celda = oExcel.getCeldaFilaHoja(hoja,fila,13);                
                            if (celda != null) 
                            {
                                iValor = celda.getNumericCellValue();                            
                                if (iValor != null)  postalv = Integer.toString((int) Math.floor(iValor));
                            
                            }      
                        }
                        catch (Exception e)
                        {
                            postalv = null;
                        }
                    }
                    
                                        
                    //municipio
                     celda = oExcel.getCeldaFilaHoja(hoja,fila,11);                
                    if (celda != null) 
                    {
                        valor = celda.getStringCellValue();
                        if (celda != null)
                        {
                            municiv = valor.trim();
                            if (municiv.length() > 26) municiv = municiv.substring(0, 26);
                        }
                    } 
                        
                    //localidad
                        localiv = municiv;
                        
                    //provincia    
                    celda = oExcel.getCeldaFilaHoja(hoja,fila,12);                
                    if (celda != null) 
                    {
                        valor = celda.getStringCellValue();
                        if (celda != null)
                        {
                            provinv = valor.trim();
                        }
                    } 
                                                                
                    //calle    
                    celda = oExcel.getCeldaFilaHoja(hoja,fila,7);                
                    if (celda != null) 
                    {
                        valor = celda.getStringCellValue();
                        if (celda != null)
                        {
                            callev = valor.trim();
                        }
                    } 
                        
                    //numero   
                    try
                    {                        
                    
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,8);                
                        if (celda != null) 
                        {
                            valor = celda.getStringCellValue();
                            if (celda != null)
                            {
                                numerov = valor.trim();
                            }
                        }      
                    }
                    catch (NumberFormatException nfe)
                    {
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,8);                
                        if (celda != null) 
                        {
                            iValor = celda.getNumericCellValue();                            
                            if (iValor != null)  numerov = Integer.toString((int) Math.floor(iValor));
                            
                        }      
                    }                        
                    
                    //planta   
                    try
                    {                        
                    
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,9);                
                        if (celda != null) 
                        {
                            valor = celda.getStringCellValue();
                            if (celda != null)
                            {
                                plantav = valor.trim();
                            }
                        }      
                    }
                    catch (NumberFormatException nfe)
                    {
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,9);                
                        if (celda != null) 
                        {
                            iValor = celda.getNumericCellValue();                            
                            if (iValor != null)  plantav = Integer.toString((int) Math.floor(iValor));
                            
                        }      
                    }        
                        
                    //puerta    
                    try
                    {                        
                    
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,10);                
                        if (celda != null) 
                        {
                            valor = celda.getStringCellValue();
                            if (celda != null)
                            {
                                puertav = valor.trim();
                            }
                        }      
                    }
                    catch (NumberFormatException nfe)
                    {
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,10);                
                        if (celda != null) 
                        {
                            iValor = celda.getNumericCellValue();                            
                            if (iValor != null)  puertav = Integer.toString((int) Math.floor(iValor));
                            
                        }      
                    }     
                    
                    //numexpv    
                    celda = oExcel.getCeldaFilaHoja(hoja,fila,19);                
                    if (celda != null) 
                    {
                        valor = celda.getStringCellValue();
                        if (celda != null)
                        {
                            numexpv = valor.trim();
                        }
                    } 
                    if (numexpv != null && !numexpv.equals("")) 
                    {//ya está localizado. No le volvemos a procesar.
                        seguir = false;
                        añadeFila2 (hojaCoincidentes,fila,"");
                        logger.info("Fila: "+Integer.toString(fila+1)+"YA LOCALIZADO: "+numexpv);
                    }
                        
                }
                catch (Exception e)
                {
                    seguir = false;                    
                    añadeFila2 (hojaCoincidentes,fila,"");
                    logger.error("Fila: "+Integer.toString(fila+1)+"ERROR: "+e.toString());
                }
                                
                
            if (seguir)
            {
                //buscamos la tasación en el sistema
                numexp = null;
                //consulta realizada en la primera pasada sConsulta = "SELECT * FROM solicitudes s JOIN datosreg d on s.numexp = d.numexp where nifsolici like '"+cNif2+"' and d.finca = '"+cFinca14+"' and s.provin = '"+cProvin12+"' and s.codcli in (752,852,452,152,193,252,552,652)";
                sConsulta = "SELECT * FROM solicitudes s JOIN datosreg d on s.numexp = d.numexp where s.codpos ="+postalv+" and d.finca = '"+cFinca14+"' and s.provin = '"+cProvin12+"' and s.codcli in (752,852,452,152,193,252,552,652)";
                rs = Utilidades.Conexion.select(sConsulta,conexion);
                if (rs.next())
                {
                    numexp = rs.getString("numexp");
                    solicitudes = "solicitudes";
                    otrastas = "otrastas";
                    elemento5 = "elemento5";
                }                
                else
                {//buscamos en historico
                  rs.close();
                  rs = null;
                  //consulta realizada en la primera pasada sConsulta = "SELECT * FROM his_solicitudes s JOIN his_datosreg d on s.numexp = d.numexp where nifsolici like '"+cNif2+"' and d.finca = '"+cFinca14+"' and s.provin = '"+cProvin12+"' and s.codcli in (752,852,452,152,193,252,552,652)";
                  sConsulta = "SELECT * FROM his_solicitudes s JOIN his_datosreg d on s.numexp = d.numexp where s.codpos ="+postalv+" and d.finca = '"+cFinca14+"' and s.provin = '"+cProvin12+"' and s.codcli in (752,852,452,152,193,252,552,652)";
                  rs = Utilidades.Conexion.select(sConsulta,conexion);
                  if (rs.next())
                  {
                      numexp = rs.getString("numexp");
                      solicitudes = "his_solicitudes";
                      otrastas = "his_otrastas";
                      elemento5 = "his_elemento5";
                  }
                  else numexp = null;
                }

                //Si la encontramos lo insertamos en prentasaciones
                if (numexp != null)
                {
                    rs.close();
                    rs = null;
                    //1.- insertamos en la tabla de pretasaciones
                    preta = new Pretasaciones();
                    sConsulta = "SELECT * FROM "+solicitudes+" WHERE numexp='"+numexp+"'";
                    rs = Utilidades.Conexion.select(sConsulta,conexion);     
                    if (rs.next()) 
                    {
                        tipoinm = rs.getString("tipoinm");
                        preta.coddel = rs.getInt("delegado");
                        preta.codcli = rs.getInt("codcli");
                        preta.oficina = rs.getString("oficina");
                        preta.numexpc = 0;
                        preta.ag_obj = "";
                        preta.objeto = "";
                        preta.num_tas = "1";
                        if (postalv != null) preta.postalv = Integer.parseInt(postalv);
                        preta.municiv = municiv;
                        preta.localiv = localiv;
                        preta.provinv = provinv;
                        //preta.codsituv = rs.getString("codsitu");
                        //preta.situacionv = rs.getString("situacion");
                        //preta.tipoviav = rs.getString("tipovia");
                        preta.callev = callev;
                        preta.numerov = numerov;
                        preta.escalerav = escalerav;
                        preta.plantav = plantav;
                        preta.puertav = puertav;      

                        if (Funciones.Fcomunes.esTipologia(conexion, tipoinm) == 1)
                        {//tipologia 1
                           consulta = "SELECT suputil,cconparcom FROM "+elemento5+" WHERE numexp='"+numexp+"'";
                           rs2 = Utilidades.Conexion.select(consulta,conexion);     
                            if (rs2.next())  
                            {
                                if (rs2.getString("suputil") != null) preta.suputil = Double.parseDouble(rs2.getString("suputil"));
                                if (rs2.getString("cconparcom") != null) preta.supcons = Double.parseDouble(rs2.getString("cconparcom"));                            
                            }
                           //rs.close();
                        }
                        else
                        {//tipologia 2
                           consulta = "SELECT totsuperf FROM "+otrastas+"  WHERE numexp='"+numexp+"'";
                           rs2 = Utilidades.Conexion.select(consulta,conexion);     
                            if (rs2.next())  
                            {
                                if (rs2.getString("totsuperf") != null) preta.supcons = Double.parseDouble(rs2.getString("totsuperf"));                                                                                    
                            }                           
                        }
                        rs2.close();
                        rs2 = null;
                        //tipo de pretasaciones Actualizacion de valor
                    
                        preta.tipopretasa = "D";
                       //naturaleza del bien                        
                        preta.naturbien = "";
                        preta.finca = cFinca14;
                        preta.numexpv = numexp;
                        //preta.naturbien = "X";
                        //codigo situacion 
                        /*
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,8);
                        if (celda != null) 
                        {
                            if (celda.getStringCellValue().length() > 60) preta.codsituv = celda.getStringCellValue().substring(0, 60).toUpperCase();
                            else preta.codsituv = celda.getStringCellValue().toUpperCase();
                        }
                        //
                        //situacion
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,9);
                        if (celda != null) 
                        {
                            if (celda.getStringCellValue().length() > 60) preta.situacionv = celda.getStringCellValue().substring(0, 60).toUpperCase();
                            else preta.situacionv = celda.getStringCellValue().toUpperCase();
                        }
                        //tipo via
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,10);
                        if (celda != null) 
                        {
                            if (celda.getStringCellValue().length() > 60) preta.tipoviav = celda.getStringCellValue().substring(0, 60).toUpperCase();
                            else preta.tipoviav = celda.getStringCellValue().toUpperCase();
                        }
                        //calle
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,11);
                        if (celda != null) 
                        {
                            if (celda.getStringCellValue().length() > 60) preta.callev = celda.getStringCellValue().substring(0, 60).toUpperCase();
                            else preta.callev = celda.getStringCellValue().toUpperCase();
                        }
                        //numero
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,12);
                        if (celda != null) 
                        {
                            if (celda.getStringCellValue().length() > 60) preta.numerov = celda.getStringCellValue().substring(0, 60).toUpperCase();
                            else preta.numerov = celda.getStringCellValue().toUpperCase();
                        }
                        //escalera
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,13);
                        if (celda != null) 
                        {
                            if (celda.getStringCellValue().length() > 60) preta.escalerav = celda.getStringCellValue().substring(0, 60).toUpperCase();
                            else preta.escalerav = celda.getStringCellValue().toUpperCase();
                        }
                        //planta
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,14);
                        if (celda != null) 
                        {
                            if (celda.getStringCellValue().length() > 60) preta.plantav = celda.getStringCellValue().substring(0, 60).toUpperCase();
                            else preta.plantav = celda.getStringCellValue().toUpperCase();
                        }
                        //puerta
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,15);
                        if (celda != null) 
                        {
                            if (celda.getStringCellValue().length() > 60) preta.puertav = celda.getStringCellValue().substring(0, 60).toUpperCase();
                            else preta.puertav = celda.getStringCellValue().toUpperCase();
                        }
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,7);
                        preta.localiv = celda.getStringCellValue().toUpperCase();

                        celda = oExcel.getCeldaFilaHoja(hoja,fila,5);
                        preta.provinv = celda.getStringCellValue().toUpperCase();
                         * */
                        preta.otratas = "N";  //ya realizada por Valtecnic.
                        //esVPO
                        /*
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,18);
                        if (celda != null) 
                        {
                            preta.esvpo = celda.getStringCellValue();
                        } 
                         * 
                         */
                        if (preta.setPretacionesFromB23xt (conexion) > 0)
                        {
                            conexion.commit();   
                            //conexion.rollback();
                            añadeFila2 (hojaCoincidentes,fila,numexp);
                            logger.info("Fila: "+Integer.toString(fila+1)+ "LOCALIZADO: "+numexp);
                        }
                        else 
                        {
                            conexion.rollback();
                            añadeFila2 (hojaCoincidentes,fila,"");
                            logger.error("Fila: "+Integer.toString(fila+1)+ "LOCALIZADO (sin insertar): "+numexp);
                        }
                        //al final
                    }//if solicitudes/his_solicitudes  
              
                    preta = null;

                    //2.- escribimos el estado en el fichero de log.
                }
                else
                {                    
                    añadeFila2 (hojaCoincidentes,fila,"");
                    logger.error("Fila: "+Integer.toString(fila+1)+"SIN LOCALIZAR");
                }
            }
         }//for
            //escribimos el libro
            FileOutputStream elFichero = new FileOutputStream("/data/informes/cargapretasaExcel/deutschebank/carga2.xls");
            libro.write(elFichero);
            elFichero.close();               
            
        }
        catch (Exception e)
        {
            logger.error("Error general en la carga de pretasaciones. Descripción: "+e.toString());
        }
        finally
        {
            try
            {
                if (conexion != null && !conexion.isClosed()) conexion.close();                
            }
            catch (java.sql.SQLException sqlException)
            {     
                logger.error("Imposible cerrar conexión con la B.D"+sqlException.toString());
            }             
        }//finally     
        
    }//cargaPretasacionesNOUCI
    
    private void cargaPretasacionesNOUCI3()
    {//cargamos las no localizadas con :
        //tipopretasa = d
        //fchite = fecha tasacion
        //valoventar = valor tasacion
       
        String sConsulta = "";
        String consulta = "";
        boolean seguir = true;
        String solicitudes = "";
        String otrastas = "";
        String elemento5 = "";
        String numexp = null;
        Pretasaciones preta = null;
        String tipoinm = null;
        java.sql.ResultSet rs2 = null;
        
        String postalv = null;
                    String municiv = null;
                    String localiv = null;
                    String provinv = null;
                    String codsituv = null;
                    String situacionv = null;
                    String tipoviav   = null;
                    String callev     = null;
                    String  numerov   = null;
                    String escalerav  = null;
                    String plantav    = null;
                    String  puertav   = null;
                    String numexpv    = null;
                    String tipologia = null;
                    Double valorTas1 = 0.0;
                    Double valorTas2 = 0.0;
                    java.util.Date fchTas = null;
        
        java.sql.Connection conexion = null;
        String sUrlExcel = "/data/informes/cargapretasaExcel/deutschebank/deutsche_sin.xls";        
        //FICHERO LOG4J
        PropertyConfigurator.configure("/data/informes/cargapretasaExcel/deutschebank/" + "Log4j.properties");   
        try
        {
            //libro para registrar lo que localizamos y lo que no
            HSSFWorkbook libro = null;                    
            HSSFSheet hojaCoincidentes = null;
             // Se crea el libro
                libro = new HSSFWorkbook();
            // Se crea una hoja dentro del libro
                hojaCoincidentes =   libro.createSheet("COINCIDENTES");
                
                
            //conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:desarrollo/des0329@orabackup:1521:rvtn");
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@orabackup:1521:rvtn");
            conexion.setAutoCommit(false);
            String hoja = "Valtecnic";   //nombre de la hora
            Utilidades.Excel oExcel = new Utilidades.Excel(sUrlExcel);            
            org.apache.poi.hssf.usermodel.HSSFCell celda = null;
            
            //lectura de datos del Excel.
            //celda de los datos
            String valor = null;
            Double iValor = 0.0;
            String cFinca14 = null;
            String cProvin12 = null;
            String cNif2 = null;
            int delegacion = 0;
            int finicio = 2;
            int ffin = 312;            
            for (int fila = finicio; fila < ffin;fila ++)              
            {
                seguir = true;
                valor = null;
                cFinca14 = null;
                cProvin12 = null;
                cNif2 = null;
                numexp = null;
                numexpv    = null;
                postalv = null;
                municiv = null;
                     localiv = null;
                     provinv = null;
                     codsituv = null;
                     situacionv = null;
                     tipoviav   = null;
                     callev     = null;
                      numerov   = null;
                     escalerav  = null;
                     plantav    = null;
                      puertav   = null;
                      delegacion = 0;
                      tipologia = "";
                       valorTas1 = 0.0;
                     valorTas2 = 0.0;
                     fchTas = null;
                
                try
                {
                    //finca
                    try
                    {                        
                    
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,14);                
                        if (celda != null) 
                        {
                            valor = celda.getStringCellValue();
                            if (celda != null)
                            {
                                cFinca14 = valor;
                            }
                        }      
                    }
                    catch (NumberFormatException nfe)
                    {
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,14);                
                        if (celda != null) 
                        {
                            iValor = celda.getNumericCellValue();                            
                            if (iValor != null)  cFinca14 = Integer.toString((int) Math.floor(iValor));
                            
                        }      
                    }
                    //Provincia
                    celda = oExcel.getCeldaFilaHoja(hoja,fila,12);                
                    if (celda != null) 
                    {
                        valor = celda.getStringCellValue();
                        if (celda != null)
                        {
                            cProvin12 = valor;
                        }
                    } 
                     //Tipologia
                    celda = oExcel.getCeldaFilaHoja(hoja,fila,5);                
                    if (celda != null) 
                    {
                        valor = celda.getStringCellValue();
                        if (celda != null)
                        {
                            tipologia = "Tipologia:"+valor.trim().toUpperCase();
                        }
                    } 
                    
                    //nif
                    celda = oExcel.getCeldaFilaHoja(hoja,fila,2);                
                    if (celda != null) 
                    {
                        valor = celda.getStringCellValue();
                        if (celda != null)
                        {
                            cNif2 = valor;
                        }
                        if (cNif2 != null && cNif2.length()>1)
                        {
                            if (cNif2.substring(0, 1).equals("0") || !isNumeric(cNif2.substring(0, 1)))
                            {
                                cNif2 = '%'+cNif2.substring(1, cNif2.length());
                            }
                            if (!isNumeric(cNif2.substring(cNif2.length()-1, cNif2.length())))
                            {
                                cNif2 = cNif2.substring(0, cNif2.length()-1)+"%";
                            }
                        }
                    }
                    
                    //codigo postal
                    try
                    {                        
                    
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,13);                
                        if (celda != null) 
                        {
                            valor = celda.getStringCellValue();
                            if (celda != null)
                            {
                                postalv = valor.trim();
                            }
                        }      
                    }
                    catch (NumberFormatException nfe)
                    {
                        try
                        {
                            celda = oExcel.getCeldaFilaHoja(hoja,fila,13);                
                            if (celda != null) 
                            {
                                iValor = celda.getNumericCellValue();                            
                                if (iValor != null)  postalv = Integer.toString((int) Math.floor(iValor));
                            
                            }      
                        }
                        catch (Exception e)
                        {
                            postalv = null;
                        }
                    }
                    
                                        
                    //municipio
                     celda = oExcel.getCeldaFilaHoja(hoja,fila,11);                
                    if (celda != null) 
                    {
                        valor = celda.getStringCellValue();
                        if (celda != null)
                        {
                            municiv = valor.trim();
                            if (municiv.length() > 26) municiv = municiv.substring(0, 26);
                        }
                    } 
                        
                    //localidad
                        localiv = municiv;
                        
                    //provincia    
                    celda = oExcel.getCeldaFilaHoja(hoja,fila,12);                
                    if (celda != null) 
                    {
                        valor = celda.getStringCellValue();
                        if (celda != null)
                        {
                            provinv = valor.trim().toUpperCase();
                            sConsulta = "SELECT prefijo,coddel FROM provincias WHERE nompro = '"+provinv.trim()+"'";    
                            rs = Utilidades.Conexion.select(sConsulta,conexion);                                    
                            if (rs.next())
                            {
                                delegacion = Integer.parseInt(rs.getString("coddel"));  
                            }
                            else delegacion = 0;
                                        
                            rs.close();
                            rs = null;                         
                        }
                    } 
                                                                
                    //calle    
                    celda = oExcel.getCeldaFilaHoja(hoja,fila,7);                
                    if (celda != null) 
                    {
                        valor = celda.getStringCellValue();
                        if (celda != null)
                        {
                            callev = valor.trim();
                        }
                    } 
                        
                    //numero   
                    try
                    {                        
                    
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,8);                
                        if (celda != null) 
                        {
                            valor = celda.getStringCellValue();
                            if (celda != null)
                            {
                                numerov = valor.trim();
                            }
                        }      
                    }
                    catch (NumberFormatException nfe)
                    {
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,8);                
                        if (celda != null) 
                        {
                            iValor = celda.getNumericCellValue();                            
                            if (iValor != null)  numerov = Integer.toString((int) Math.floor(iValor));
                            
                        }      
                    }                        
                    
                    //planta   
                    try
                    {                        
                    
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,9);                
                        if (celda != null) 
                        {
                            valor = celda.getStringCellValue();
                            if (celda != null)
                            {
                                plantav = valor.trim();
                            }
                        }      
                    }
                    catch (NumberFormatException nfe)
                    {
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,9);                
                        if (celda != null) 
                        {
                            iValor = celda.getNumericCellValue();                            
                            if (iValor != null)  plantav = Integer.toString((int) Math.floor(iValor));
                            
                        }      
                    }        
                        
                    //puerta    
                    try
                    {                        
                    
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,10);                
                        if (celda != null) 
                        {
                            valor = celda.getStringCellValue();
                            if (celda != null)
                            {
                                puertav = valor.trim();
                            }
                        }      
                    }
                    catch (NumberFormatException nfe)
                    {
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,10);                
                        if (celda != null) 
                        {
                            iValor = celda.getNumericCellValue();                            
                            if (iValor != null)  puertav = Integer.toString((int) Math.floor(iValor));
                            
                        }      
                    }    
                    
                     //valor tasacion1    
                    try
                    {                        
                    
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,16);                
                        if (celda != null) 
                        {
                            valor = celda.getStringCellValue();                            
                        }      
                    }
                    catch (NumberFormatException nfe)
                    {
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,16);                
                        if (celda != null) 
                        {
                            valorTas1 = celda.getNumericCellValue();                            
                            
                            
                        }      
                    }    
                    //valor tasacion2   
                    try
                    {                        
                    
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,17);                
                        if (celda != null) 
                        {
                            valor = celda.getStringCellValue();
                           
                        }      
                    }
                    catch (NumberFormatException nfe)
                    {
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,17);                
                        if (celda != null) 
                        {
                            valorTas2 = celda.getNumericCellValue();                            
                            
                            
                        }      
                    }    
                    
                    //fecha tasacin
                      
                    try
                    {                        
                    
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,18);                
                        if (celda != null) 
                        {
                            
                            if (celda.getDateCellValue() != null)
                            {
                                fchTas = celda.getDateCellValue();
                            }
                        }      
                    }
                    catch (NumberFormatException nfe)
                    {
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,18);                
                        if (celda != null) 
                        {                                                        
                            
                        }      
                    }    
                    
                    
                    //numexpv    
                                       
                        seguir = true;                    
                        
                }
                catch (Exception e)
                {
                    seguir = false;                    
                    //añadeFila2 (hojaCoincidentes,fila,"");
                    logger.error("Fila: "+Integer.toString(fila+1)+"ERROR: "+e.toString());
                }
                                
                
            if (seguir)
            {                
                             
                    //1.- insertamos en la tabla de pretasaciones
                    preta = new Pretasaciones();
                    
                        //tipoinm = rs.getString("tipoinm");
                        preta.coddel = delegacion;
                        preta.codcli = 152;
                        preta.oficina = "";
                        preta.numexpc = 0;
                        preta.ag_obj = "";
                        preta.objeto = "";
                        preta.num_tas = "1";
                        if (postalv != null && postalv.length() > 2) preta.postalv = Integer.parseInt(postalv);
                        else preta.postalv = 0;
                        preta.municiv = municiv;
                        preta.localiv = localiv;
                        preta.provinv = provinv;
                        //preta.codsituv = rs.getString("codsitu");
                        //preta.situacionv = rs.getString("situacion");
                        //preta.tipoviav = rs.getString("tipovia");
                        preta.callev = callev;
                        preta.numerov = numerov;
                        preta.escalerav = escalerav;
                        preta.plantav = plantav;
                        preta.puertav = puertav;      
                        preta.suputil = 0.0;
                        preta.supcons = 0.0;
                       
                    
                        preta.tipopretasa = "d";
                       //naturaleza del bien                        
                        preta.naturbien = "";
                        preta.finca = cFinca14;
                        preta.numexpv = numexp;
                        //preta.naturbien = "X";
                        //codigo situacion 
                        /*
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,8);
                        if (celda != null) 
                        {
                            if (celda.getStringCellValue().length() > 60) preta.codsituv = celda.getStringCellValue().substring(0, 60).toUpperCase();
                            else preta.codsituv = celda.getStringCellValue().toUpperCase();
                        }
                        //
                        //situacion
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,9);
                        if (celda != null) 
                        {
                            if (celda.getStringCellValue().length() > 60) preta.situacionv = celda.getStringCellValue().substring(0, 60).toUpperCase();
                            else preta.situacionv = celda.getStringCellValue().toUpperCase();
                        }
                        //tipo via
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,10);
                        if (celda != null) 
                        {
                            if (celda.getStringCellValue().length() > 60) preta.tipoviav = celda.getStringCellValue().substring(0, 60).toUpperCase();
                            else preta.tipoviav = celda.getStringCellValue().toUpperCase();
                        }
                        //calle
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,11);
                        if (celda != null) 
                        {
                            if (celda.getStringCellValue().length() > 60) preta.callev = celda.getStringCellValue().substring(0, 60).toUpperCase();
                            else preta.callev = celda.getStringCellValue().toUpperCase();
                        }
                        //numero
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,12);
                        if (celda != null) 
                        {
                            if (celda.getStringCellValue().length() > 60) preta.numerov = celda.getStringCellValue().substring(0, 60).toUpperCase();
                            else preta.numerov = celda.getStringCellValue().toUpperCase();
                        }
                        //escalera
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,13);
                        if (celda != null) 
                        {
                            if (celda.getStringCellValue().length() > 60) preta.escalerav = celda.getStringCellValue().substring(0, 60).toUpperCase();
                            else preta.escalerav = celda.getStringCellValue().toUpperCase();
                        }
                        //planta
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,14);
                        if (celda != null) 
                        {
                            if (celda.getStringCellValue().length() > 60) preta.plantav = celda.getStringCellValue().substring(0, 60).toUpperCase();
                            else preta.plantav = celda.getStringCellValue().toUpperCase();
                        }
                        //puerta
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,15);
                        if (celda != null) 
                        {
                            if (celda.getStringCellValue().length() > 60) preta.puertav = celda.getStringCellValue().substring(0, 60).toUpperCase();
                            else preta.puertav = celda.getStringCellValue().toUpperCase();
                        }
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,7);
                        preta.localiv = celda.getStringCellValue().toUpperCase();

                        celda = oExcel.getCeldaFilaHoja(hoja,fila,5);
                        preta.provinv = celda.getStringCellValue().toUpperCase();
                         * */
                        preta.otratas = "N";  // realizada por Valtecnic.
                        preta.comentc = tipologia;
                        if (valorTas1 > 0) preta.vventar = valorTas1;
                        else preta.vventar = valorTas2;
                        
                        
                        preta.fchite = fchTas;
                        //esVPO
                        /*
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,18);
                        if (celda != null) 
                        {
                            preta.esvpo = celda.getStringCellValue();
                        } 
                         * 
                         */
                        if (preta.setPretacionesFromB23xt (conexion) > 0)
                        {
                            conexion.commit();   
                            //conexion.rollback();
                            //añadeFila2 (hojaCoincidentes,fila,numexp);
                            logger.info("Fila: "+Integer.toString(fila+1)+ "LOCALIZADO: "+numexp);
                        }
                        else 
                        {
                            conexion.rollback();
                            //añadeFila2 (hojaCoincidentes,fila,"");
                            logger.error("Fila: "+Integer.toString(fila+1)+ "LOCALIZADO (sin insertar): "+numexp);
                        }
                        //al final
                  
              
                    preta = null;

                    //2.- escribimos el estado en el fichero de log.
               
            }
         }//for
            //escribimos el libro
            //FileOutputStream elFichero = new FileOutputStream("/data/informes/cargapretasaExcel/deutschebank/carga2.xls");
            //libro.write(elFichero);
            //elFichero.close();               
            
        }
        catch (Exception e)
        {
            logger.error("Error general en la carga de pretasaciones. Descripción: "+e.toString());
        }
        finally
        {
            try
            {
                if (conexion != null && !conexion.isClosed()) conexion.close();                
            }
            catch (java.sql.SQLException sqlException)
            {     
                logger.error("Imposible cerrar conexión con la B.D"+sqlException.toString());
            }             
        }//finally     
        
    }//cargaPretasacionesNOUCI
    
    
    public void actualizaValoresPretasa()
    {//actualizacion de las pretasaciones con los valores pasados en el excel.
        String sUrlExcel = "/data/informes/cargapretasaExcel/tote3_2.xls";
        //FICHERO LOG4J
             PropertyConfigurator.configure("/data/informes/cargapretasaExcel/" + "Log4j.properties");   
             
            String agencia = "";
            String numexpc = "";
            String ag_objeto = "";
            String objeto = "";
            String numtas = "";
            java.sql.Connection conexion = null;
            String consulta = "";
            int iNumeroRegistrosAfectados = 0;
            int ceros = 0;            
            java.sql.ResultSet rs = null;
            String sUpdate = "";
            int numpet = 0;
            String svventar = "";
            double vventar = 0;
            String svpretasa = "";
            double vpretasa = 0;
            int iNumexpc = 0;
            
            try
            {
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@oraserver01:1521:rvtn1");
            conexion.setAutoCommit(false);
            String hoja = "Hoja1";   //nombre de la hora
            Utilidades.Excel oExcel = new Utilidades.Excel(sUrlExcel);            
            org.apache.poi.hssf.usermodel.HSSFCell celda = null;
            for (int fila = 0; fila < 92;fila ++)              
            {
                agencia = null;
                numexpc = null;            
                ag_objeto = null;
                objeto = null;            
                numtas = null;

                celda = null;
                Double numero = 0.0;
                
                
                /*
                celda = oExcel.getCeldaFilaHoja(hoja,fila,4);                
                if (celda != null) 
                {//oficina
                    
                    agencia = celda.getStringCellValue();
                    numero = 0.0;
                }                

                celda = oExcel.getCeldaFilaHoja(hoja,fila,5);
                
                if (celda != null) 
                {//numexpc                    
                    numexpc = celda.getStringCellValue().trim();
                    iNumexpc = Integer.parseInt(numexpc);
                    numexpc = Integer.toString(iNumexpc);
                    //numexpc = numexpc.substring(1, numexpc.length());
                    
                }

                celda = oExcel.getCeldaFilaHoja(hoja,fila,6);
                if (celda != null) 
                {//ag_objeto
                    
                    ag_objeto = celda.getStringCellValue();
                    numero = 0.0;
                }                                

                celda = oExcel.getCeldaFilaHoja(hoja,fila,7);
                if (celda != null) 
                {//objeto
                    
                    objeto = celda.getStringCellValue();
                    
                    
                }                                                
                 * /
                 * 
                 */
                
                
                celda = oExcel.getCeldaFilaHoja(hoja,fila,0);
                if (celda != null) 
                {//vventa rapida
                    numpet = 0;
                    try
                    {
                        numpet = Integer.parseInt(celda.getStringCellValue());                    
                    }
                    catch (Exception e)
                    {
                        numpet = 0;
                    }
                }      
                
                celda = oExcel.getCeldaFilaHoja(hoja,fila,5);
                if (celda != null) 
                {//valor de pretasacion
                    
                    vpretasa = 0.0;
                    try
                    {
                        vpretasa = celda.getNumericCellValue();   
                    }
                    catch (Exception e)
                    {
                        vpretasa = Double.parseDouble(celda.getStringCellValue());
                    }
                }      
                                                            
                //consulta = "SELECT * FROM pretasaciones WHERE oficina = '"+agencia+"' and numexpc = '"+numexpc +"' and ag_obj = '"+ag_objeto+"' and objeto = '"+objeto+"' AND fchuci = '02082012'";
                consulta = "SELECT * FROM pretasaciones WHERE numpet = "+numpet; 
                
                rs = Utilidades.Conexion.select(consulta,conexion);
                if (rs.next())                
                {   
                    numpet = rs.getInt("numpet");
                    logger.info("numero: "+fila);
                    //logger.info("Actualización ENCONTRADA NUMPET: "+rs.getString("numpet")+" oficina = '"+agencia+"' and numexpc = '"+numexpc +"' and ag_obj = '"+ag_objeto+"' and objeto = '"+objeto+"' and  num_tas = '"+numtas);
                    logger.info("Actualización ENCONTRADA NUMPET: "+" oficina = '"+agencia+"' and numexpc = '"+numexpc +"' and ag_obj = '"+ag_objeto+"' and objeto = '"+objeto+"'");
                    //logger.info("estado : "+rs.getString("e299a") + "vventaR: "+rs.getString("f45n8a"));
                    sUpdate = "update pretasaciones set vventar = "+vventar+", valorpretasa = "+vpretasa+", estado = '3'"+" WHERE oficina = '"+agencia+"' and numexpc = '"+numexpc +"' and ag_obj = '"+ag_objeto+"' and objeto = '"+objeto+"' AND fchuci = '02082012'";
                    if (Utilidades.Conexion.update(sUpdate, conexion) == 1) 
                    {
                        conexion.commit();
                        logger.info("numero: "+fila);
                        logger.info("Actualización actualizada  oficina = '"+agencia+"' and numexpc = '"+numexpc +"' and ag_obj = '"+ag_objeto+"' and objeto = '"+objeto+"' AND fchuci = '02082012'");
                    }
                    else
                    {
                        conexion.rollback();
                        logger.info("numero: "+fila);
                        logger.info("Actualización NO ACTUALIZADA  oficina = '"+agencia+"' and numexpc = '"+numexpc +"' and ag_obj = '"+ag_objeto+"' and objeto = '"+objeto+"' AND fchuci = '02082012'");
                    }
                    
                            
                }
                else
                {
                    logger.info("numero: "+fila);
                    logger.info("Actualización NO ENCONTRADA NUMPET: "+rs.getString("numpet")+" oficina = '"+agencia+"' and numexpc = '"+numexpc +"' and ag_obj = '"+ag_objeto+"' and objeto = '"+objeto+"' AND fchuci = '02082012'");
                }
                
            }//for
            conexion.close();
         }//try
         catch (Exception ex)
         {
             logger.info("Excepcion Actualización. oficina = '"+agencia+"' and numexpc = '"+numexpc +"' and ag_obj = '"+ag_objeto+"' and objeto = '"+objeto+"' . Descripción: "+ex.toString());
         }
         finally
        {
            try
            {
                if (conexion != null && !conexion.isClosed()) conexion.close();                
            }
            catch (java.sql.SQLException sqlException)
            {     
                logger.info("Imposible cerrar conexión con la B.D Informix");
            }             
        }//finally     
    }
    
    
    private  boolean isNumeric(String cadena)
    {	
        try 
        {		
            Integer.parseInt(cadena);		
            return true;	
        } 
        catch (NumberFormatException nfe)
        {		
            return false;	
        }
    }//isNumeric
    
    public void pretasa528()
    {
        java.sql.Connection conexion = null; 
        String valor = null;
        int insertados = 0;
        int fallos = 0;
        
        //FICHERO LOG4J
         PropertyConfigurator.configure("/data/informes/cargapretasaExcel/" + "Log4j.properties");   
         try
         {
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@oraserver01:1521:rvtn1");
            //conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:desarrollo/des0329@oraserver00:1521:rvtn");
            conexion.setAutoCommit(false);                                
              
            //primer fichero cargado sUrlExcel = "/data/informes/cargapretasaExcel/categorizaciones1.xls";
            sUrlExcel = "/data/informes/cargapretasaExcel/pretasa528.xls";
            oExcel = new Utilidades.Excel(sUrlExcel);              
            celda = null;
            HojaActual = "Hoja1";
            
             Objetos.Pretasaciones oPreta = null;
             
             //86 columnas + 4460 filas
             //con el fichero categorizaciones1 for (int fila = 1; fila <= 4459; fila ++)
             for (int fila = 1; fila <= 50; fila ++)
             {//registro
                System.out.println("Fila: "+fila);
                oPreta = new Objetos.Pretasaciones(); 
                oPreta.numpet = Utilidades.Conexion.getSequenceNextVal("SEQ_PRETASA", conexion);
                //Utilidades.Cadenas.getValorMostrarWeb(rsDatos,"hucd")+Utilidades.Cadenas.getValorMostrarWeb(rsDatos,"a5cd")
                oPreta.numpreta = dameValorExcel(fila,0)+"."+dameValorExcel(fila,1)+"-13";
                oPreta.codcli = 528;                
                oPreta.coddel = getcoddelProvincia (dameValorExcel(fila,1).substring(0, 2),conexion);
                        
                //oPreta.oficina = dameValorExcel(fila,0); //abcd
                //oPreta.numexpc = Integer.parseInt(dameValorExcel(fila,1)); //alcd
                //oPreta.ag_obj = dameValorExcel(fila,2);//hucd
                oPreta.objeto = dameValorExcel(fila,1);
                oPreta.num_tas = "1"; //dameValorExcel(fila,4);//o5br
                oPreta.naturbien = dameValorExcel(fila,7);//aist
                //oPreta.ubica = dameValorExcel(fila,6);//vl8oa
                //oPreta.acabslo = dameValorExcel(fila,7);//vl8pa
                //oPreta.acabpd = dameValorExcel(fila,8);//vl8qa
                //oPreta.carpext = dameValorExcel(fila,9);//vl8ra
                //oPreta.planta = Integer.parseInt(dameValorExcel(fila,10));//vl8sa
                oPreta.antig = Integer.parseInt(dameValorExcel(fila,9));//vl8ta
                //oPreta.ndormit = Integer.parseInt(dameValorExcel(fila,12));//vl8ua
                //oPreta.nbanos = Integer.parseInt(dameValorExcel(fila,13));//vl8va
                //oPreta.ascens = dameValorExcel(fila,14);//vl8wa
                //oPreta.calefacc = dameValorExcel(fila,15);//vl8xa
                //oPreta.estcons = dameValorExcel(fila,16);//vl8ya
                //oPreta.calcons = dameValorExcel(fila,17);//w11qa
                //oPreta.suputil = Double.parseDouble(dameValorExcel(fila,18));//a2va
                oPreta.supcons = Double.parseDouble(dameValorExcel(fila,8));//cyva
                //oPreta.supparc = Double.parseDouble(dameValorExcel(fila,20));//z1va
                //oPreta.tipopretasa = "g";  //categorizacion.e33eaa
                //oPreta.tipologia = dameValorExcel(fila,22);//e33eba
                //oPreta.esvpo = dameValorExcel(fila,23);//pist
                //oPreta.exptevpo = dameValorExcel(fila,24);//e3k3a
                //valor = dameValorExcel(fila,25);//dddt                
                //if (!valor.equals("1940-01-01")) oPreta.fchvpo = Utilidades.Cadenas.getDate(Integer.parseInt(valor.substring(8, 10)), Integer.parseInt(valor.substring(5, 7)), Integer.parseInt(valor.substring(0, 4)));//dddt
                //oPreta.autopromocion = dameValorExcel(fila,26);//q2ts
                //oPreta.finca = dameValorExcel(fila,27);//hrnb
                //oPreta.registro = Integer.parseInt(dameValorExcel(fila,28));//hsnb
                //oPreta.localireg = dameValorExcel(fila,29);//g4tx
                //oPreta.titulareg = dameValorExcel(fila,30);//s4tx
                //oPreta.fcatastral = FCATASTRAL
                //oPreta.vivnueva = dameValorExcel(fila,31);//wost
                //oPreta.plantassras = Integer.parseInt(dameValorExcel(fila,32));//e33eca
                //oPreta.sotanos = Integer.parseInt(dameValorExcel(fila,33));//e33eda
                //oPreta.nplantas = Integer.parseInt(dameValorExcel(fila,34));//e33eea                
                //oPreta.reforbloq = dameValorExcel(fila,35);//e33efa
                //valor = dameValorExcel(fila,36);//e33ega
                //if (!valor.equals("1940-01-01")) oPreta.fchreforbloq = Utilidades.Cadenas.getDate(Integer.parseInt(valor.substring(8, 10)), Integer.parseInt(valor.substring(5, 7)), Integer.parseInt(valor.substring(0, 4)));
                //oPreta.ite = dameValorExcel(fila,37);//e33eha
                //valor = dameValorExcel(fila,38);//e33eia
                //if (!valor.equals("1940-01-01")) oPreta.fchite = Utilidades.Cadenas.getDate(Integer.parseInt(valor.substring(8, 10)), Integer.parseInt(valor.substring(5, 7)), Integer.parseInt(valor.substring(0, 4)));
                //oPreta.reforviv = dameValorExcel(fila,39);//e33eja
                //valor = dameValorExcel(fila,40);//e33eka
                //if (!valor.equals("1940-01-01")) oPreta.fchreforviv = Utilidades.Cadenas.getDate(Integer.parseInt(valor.substring(8, 10)), Integer.parseInt(valor.substring(5, 7)), Integer.parseInt(valor.substring(0, 4)));
                //oPreta.caliacabados = dameValorExcel(fila,41);//e33ela
                //oPreta.aireacond = dameValorExcel(fila,42);//e33ema
                //oPreta.telefonia = dameValorExcel(fila,43);//e33ena
                //oPreta.otrasinstal = dameValorExcel(fila,44);//e33eoa
                //oPreta.anxpiscina = dameValorExcel(fila,45);//e33epa
                //oPreta.anxgaraje = dameValorExcel(fila,46);//f115qa
                //oPreta.anxtrastero = dameValorExcel(fila,47);//f115pa
                //oPreta.anxporche = dameValorExcel(fila,48);//e33eqa
                //oPreta.anxotro = dameValorExcel(fila,49);//e33era
                //oPreta.piscinaurb = dameValorExcel(fila,50);//e33esa
                //oPreta.jardinesurb = dameValorExcel(fila,51);//e33eta
               // oPreta.otrosurb = dameValorExcel(fila,52);//e33eua
                //oPreta.constanio = Integer.parseInt(dameValorExcel(fila,53));//e33eva
                //oPreta.comentc = dameValorExcel(fila,54);//sltx
                //oPreta.valtotpretaviv = Double.parseDouble(dameValorExcel(fila,55));//e33ewa
                //oPreta.valunitpretaviv = Double.parseDouble(dameValorExcel(fila,56));//e33exa
                //oPreta.valtotpretatrt = Double.parseDouble(dameValorExcel(fila,57));//e33eya
                //oPreta.valunitpretatrt = Double.parseDouble(dameValorExcel(fila,58));//e33eza
                //oPreta.valtotpretapz = Double.parseDouble(dameValorExcel(fila,59));//e33f0a
                //oPreta.valunitpretapz = Double.parseDouble(dameValorExcel(fila,60));//e33f1a
                //oPreta.valtotrentviv = Double.parseDouble(dameValorExcel(fila,61));//e33f2a
                //oPreta.valunitrentviv = Double.parseDouble(dameValorExcel(fila,62));//e33f3a
                //oPreta.valtotrentrt = Double.parseDouble(dameValorExcel(fila,63));//e33f4a
                //oPreta.valunitrentrt = Double.parseDouble(dameValorExcel(fila,64));//e33f5a
                //oPreta.valtotrentpz = Double.parseDouble(dameValorExcel(fila,65));//e33f6a
                //oPreta.valunitrentpz = Double.parseDouble(dameValorExcel(fila,66));//e33f7a
                oPreta.valorpretasa = Double.parseDouble(dameValorExcel(fila,10));//e29fa
                oPreta.estado = dameValorExcel(fila,13); //e299a
                //valor = dameValorExcel(fila,69);//mbdt
                //oPreta.fchuci = Utilidades.Cadenas.getDate("12-03-2013");
                oPreta.fchuci = Utilidades.Cadenas.getDate("14-10-2013");
                //oPreta.fchuci = Utilidades.Cadenas.getDate(Integer.parseInt(valor.substring(8, 10)), Integer.parseInt(valor.substring(5, 7)), Integer.parseInt(valor.substring(0, 4)));
                //oPreta.horauci = "12:30:00";//dameValorExcel(fila,70);//i2n1
                oPreta.horauci = "16:00:00";//dameValorExcel(fila,70);//i2n1
                //valor = dameValorExcel(fila,71);//v59ya
                //oPreta.fchval = Utilidades.Cadenas.getDate(Integer.parseInt(valor.substring(8, 10)), Integer.parseInt(valor.substring(5, 7)), Integer.parseInt(valor.substring(0, 4)));
                oPreta.fchval = Utilidades.Cadenas.getDate("01-01-1940");
                oPreta.horaval = "00:00:00";//e29aa
                oPreta.postalv = Integer.parseInt(dameValorExcel(fila,5));//e29ba
                oPreta.provinv = dameValorExcel(fila,3);//w11aa
                oPreta.municiv = dameValorExcel(fila,2);//w11ba
                oPreta.localiv = dameValorExcel(fila,2);//w11ca
                //oPreta.codsituv = dameValorExcel(fila,);//w11da
                //oPreta.situacionv = dameValorExcel(fila,78);//w11ea
                //oPreta.tipoviav = dameValorExcel(fila,79);//w11fa
                oPreta.callev = dameValorExcel(fila,6);//w11ga
                //oPreta.numerov = dameValorExcel(fila,81);//w11ha
                //oPreta.escalerav = dameValorExcel(fila,82);//w11ia
                //oPreta.plantav = dameValorExcel(fila,83);//w11ja
                //oPreta.puertav = dameValorExcel(fila,84);//w11ka
                oPreta.vventar = Double.parseDouble(dameValorExcel(fila,11));//F45N8A
                //oPreta.otratas = dameValorExcel(fila,86);//F45N9A
                //oPreta.fiabilidad = null;
                //oPreta.numexpv = null;
                //oPreta.codusua = null;
                
                if (oPreta.insert(conexion) == 1)
                {
                    logger.info("Insertada categorización nº: "+ fila+ " con el nº de petición: "+oPreta.numpet);
                    conexion.commit();
                    insertados ++;
                }
                else 
                {
                    logger.error("No insertada categorizacion nº: "+fila);
                    conexion.rollback();
                    fallos ++;
                }
                        
                oPreta = null;
             }//for filas
             conexion.close();
             
         }       
         catch (Exception ex)
         {
             logger.info("Excepcion. Descripción: "+ex.toString());
         }
         finally
         {
            try
            {
                if (conexion != null && !conexion.isClosed()) conexion.close();                
            }
            catch (java.sql.SQLException sqlException)
            {     
                logger.info("Imposible cerrar conexión con la B.D Informix");
            }       
            logger.info("Insertados : "+insertados);
            logger.info("Fallos : "+fallos);
         }//finally     
    }
    
    public void pretasaDispersion()
    {
        java.sql.Connection conexion = null; 
        java.sql.Connection conexOtradoc = null;
        String valor = null;
        int insertados = 0;
        int fallos = 0;
        
        //FICHERO LOG4J
         PropertyConfigurator.configure("/data/informes/cargapretasaExcel/dispersion/" + "Log4j.properties");   
         try
         {
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@oraserver02:1521:rvtnprod");
            //conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:desarrollo/des0329@oraserver03:1521:rvtn3");
            conexion.setAutoCommit(false);   
            conexOtradoc = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecinfx/valtecinfx@oraserver02:1521:rvtnprod");
            conexOtradoc.setAutoCommit(false);
            //primer fichero cargado sUrlExcel = "/data/informes/cargapretasaExcel/categorizaciones1.xls";
            sUrlExcel = "/data/informes/cargapretasaExcel/dispersion/dispersion.xls";
            oExcel = new Utilidades.Excel(sUrlExcel);              
            celda = null;
            HojaActual = "RESULTADOS";
            
             Objetos.Pretasaciones oPreta = null;
             
             //86 columnas + 4460 filas
             //con el fichero categorizaciones1 for (int fila = 1; fila <= 4459; fila ++)
             for (int fila = 1; fila <= 48; fila ++)
             {//registro
                System.out.println("Fila: "+fila);
                oPreta = new Objetos.Pretasaciones(); 
                oPreta.numpet = Utilidades.Conexion.getSequenceNextVal("SEQ_PRETASA", conexion);
                //Utilidades.Cadenas.getValorMostrarWeb(rsDatos,"hucd")+Utilidades.Cadenas.getValorMostrarWeb(rsDatos,"a5cd")
                oPreta.numpreta = "100."+oPreta.numpet+"-16";
                oPreta.codcli = 100;                
                oPreta.coddel = getcoddelProvincia (dameValorExcel(fila,1).substring(0, 2),conexion);
                        
                oPreta.oficina = "0100"; //abcd
                oPreta.numexpc = oPreta.numpet;
                //oPreta.ag_obj = dameValorExcel(fila,2);//hucd
                //oPreta.objeto = dameValorExcel(fila,1);
                oPreta.num_tas = "1"; //dameValorExcel(fila,4);//o5br
                //oPreta.naturbien = dameValorExcel(fila,7);//aist
                //oPreta.ubica = dameValorExcel(fila,6);//vl8oa
                //oPreta.acabslo = dameValorExcel(fila,7);//vl8pa
                //oPreta.acabpd = dameValorExcel(fila,8);//vl8qa
                //oPreta.carpext = dameValorExcel(fila,9);//vl8ra
                //oPreta.planta = Integer.parseInt(dameValorExcel(fila,10));//vl8sa
                //oPreta.antig = Integer.parseInt(dameValorExcel(fila,9));//vl8ta
                //oPreta.ndormit = Integer.parseInt(dameValorExcel(fila,12));//vl8ua
                //oPreta.nbanos = Integer.parseInt(dameValorExcel(fila,13));//vl8va
                //oPreta.ascens = dameValorExcel(fila,14);//vl8wa
                //oPreta.calefacc = dameValorExcel(fila,15);//vl8xa
                //oPreta.estcons = dameValorExcel(fila,16);//vl8ya
                //oPreta.calcons = dameValorExcel(fila,17);//w11qa
                //oPreta.suputil = Double.parseDouble(dameValorExcel(fila,18));//a2va
                //oPreta.supcons = Double.parseDouble(dameValorExcel(fila,8));//cyva
                //oPreta.supparc = Double.parseDouble(dameValorExcel(fila,20));//z1va
                oPreta.tipopretasa = "D";  //categorizacion.e33eaa
                //oPreta.tipologia = dameValorExcel(fila,22);//e33eba
                //oPreta.esvpo = dameValorExcel(fila,23);//pist
                //oPreta.exptevpo = dameValorExcel(fila,24);//e3k3a
                //valor = dameValorExcel(fila,25);//dddt                
                //if (!valor.equals("1940-01-01")) oPreta.fchvpo = Utilidades.Cadenas.getDate(Integer.parseInt(valor.substring(8, 10)), Integer.parseInt(valor.substring(5, 7)), Integer.parseInt(valor.substring(0, 4)));//dddt
                //oPreta.autopromocion = dameValorExcel(fila,26);//q2ts
                //oPreta.finca = dameValorExcel(fila,27);//hrnb
                //oPreta.registro = Integer.parseInt(dameValorExcel(fila,28));//hsnb
                //oPreta.localireg = dameValorExcel(fila,29);//g4tx
                //oPreta.titulareg = dameValorExcel(fila,30);//s4tx
                //oPreta.fcatastral = FCATASTRAL
                //oPreta.vivnueva = dameValorExcel(fila,31);//wost
                //oPreta.plantassras = Integer.parseInt(dameValorExcel(fila,32));//e33eca
                //oPreta.sotanos = Integer.parseInt(dameValorExcel(fila,33));//e33eda
                //oPreta.nplantas = Integer.parseInt(dameValorExcel(fila,34));//e33eea                
                //oPreta.reforbloq = dameValorExcel(fila,35);//e33efa
                //valor = dameValorExcel(fila,36);//e33ega
                //if (!valor.equals("1940-01-01")) oPreta.fchreforbloq = Utilidades.Cadenas.getDate(Integer.parseInt(valor.substring(8, 10)), Integer.parseInt(valor.substring(5, 7)), Integer.parseInt(valor.substring(0, 4)));
                //oPreta.ite = dameValorExcel(fila,37);//e33eha
                //valor = dameValorExcel(fila,38);//e33eia
                //if (!valor.equals("1940-01-01")) oPreta.fchite = Utilidades.Cadenas.getDate(Integer.parseInt(valor.substring(8, 10)), Integer.parseInt(valor.substring(5, 7)), Integer.parseInt(valor.substring(0, 4)));
                //oPreta.reforviv = dameValorExcel(fila,39);//e33eja
                //valor = dameValorExcel(fila,40);//e33eka
                //if (!valor.equals("1940-01-01")) oPreta.fchreforviv = Utilidades.Cadenas.getDate(Integer.parseInt(valor.substring(8, 10)), Integer.parseInt(valor.substring(5, 7)), Integer.parseInt(valor.substring(0, 4)));
                //oPreta.caliacabados = dameValorExcel(fila,41);//e33ela
                //oPreta.aireacond = dameValorExcel(fila,42);//e33ema
                //oPreta.telefonia = dameValorExcel(fila,43);//e33ena
                //oPreta.otrasinstal = dameValorExcel(fila,44);//e33eoa
                //oPreta.anxpiscina = dameValorExcel(fila,45);//e33epa
                //oPreta.anxgaraje = dameValorExcel(fila,46);//f115qa
                //oPreta.anxtrastero = dameValorExcel(fila,47);//f115pa
                //oPreta.anxporche = dameValorExcel(fila,48);//e33eqa
                //oPreta.anxotro = dameValorExcel(fila,49);//e33era
                //oPreta.piscinaurb = dameValorExcel(fila,50);//e33esa
                //oPreta.jardinesurb = dameValorExcel(fila,51);//e33eta
               // oPreta.otrosurb = dameValorExcel(fila,52);//e33eua
                //oPreta.constanio = Integer.parseInt(dameValorExcel(fila,53));//e33eva
                //oPreta.comentc = dameValorExcel(fila,54);//sltx
                //oPreta.valtotpretaviv = Double.parseDouble(dameValorExcel(fila,55));//e33ewa
                //oPreta.valunitpretaviv = Double.parseDouble(dameValorExcel(fila,56));//e33exa
                //oPreta.valtotpretatrt = Double.parseDouble(dameValorExcel(fila,57));//e33eya
                //oPreta.valunitpretatrt = Double.parseDouble(dameValorExcel(fila,58));//e33eza
                //oPreta.valtotpretapz = Double.parseDouble(dameValorExcel(fila,59));//e33f0a
                //oPreta.valunitpretapz = Double.parseDouble(dameValorExcel(fila,60));//e33f1a
                //oPreta.valtotrentviv = Double.parseDouble(dameValorExcel(fila,61));//e33f2a
                //oPreta.valunitrentviv = Double.parseDouble(dameValorExcel(fila,62));//e33f3a
                //oPreta.valtotrentrt = Double.parseDouble(dameValorExcel(fila,63));//e33f4a
                //oPreta.valunitrentrt = Double.parseDouble(dameValorExcel(fila,64));//e33f5a
                //oPreta.valtotrentpz = Double.parseDouble(dameValorExcel(fila,65));//e33f6a
                //oPreta.valunitrentpz = Double.parseDouble(dameValorExcel(fila,66));//e33f7a
                //oPreta.valorpretasa = Double.parseDouble(dameValorExcel(fila,10));//e29fa
                oPreta.estado = "2"; //e299a
                //valor = dameValorExcel(fila,69);//mbdt
                //oPreta.fchuci = Utilidades.Cadenas.getDate("12-03-2013");
                oPreta.fchuci = Utilidades.Cadenas.getDate("16-11-2016");
                //oPreta.fchuci = Utilidades.Cadenas.getDate(Integer.parseInt(valor.substring(8, 10)), Integer.parseInt(valor.substring(5, 7)), Integer.parseInt(valor.substring(0, 4)));
                //oPreta.horauci = "12:30:00";//dameValorExcel(fila,70);//i2n1
                oPreta.horauci = "11:00:00";//dameValorExcel(fila,70);//i2n1
                //valor = dameValorExcel(fila,71);//v59ya
                //oPreta.fchval = Utilidades.Cadenas.getDate(Integer.parseInt(valor.substring(8, 10)), Integer.parseInt(valor.substring(5, 7)), Integer.parseInt(valor.substring(0, 4)));
                oPreta.fchval = Utilidades.Cadenas.getDate("01-01-1940");
                oPreta.horaval = "00:00:00";//e29aa
                oPreta.postalv = Integer.parseInt(dameValorExcel(fila,1));//e29ba
                oPreta.provinv = dameValorExcel(fila,3);//w11aa
                if (oPreta.provinv != null) oPreta.provinv = oPreta.provinv.toUpperCase();
                oPreta.municiv = dameValorExcel(fila,2);//w11ba
                if (oPreta.municiv != null) oPreta.municiv = oPreta.municiv.toUpperCase();
                oPreta.localiv = dameValorExcel(fila,2);//w11ca
                if (oPreta.localiv != null) oPreta.localiv = oPreta.localiv.toUpperCase();
                //oPreta.codsituv = dameValorExcel(fila,);//w11da
                //oPreta.situacionv = dameValorExcel(fila,78);//w11ea
                //oPreta.tipoviav = dameValorExcel(fila,79);//w11fa
                //oPreta.callev = dameValorExcel(fila,6);//w11ga
                //oPreta.numerov = dameValorExcel(fila,81);//w11ha
                //oPreta.escalerav = dameValorExcel(fila,82);//w11ia
                //oPreta.plantav = dameValorExcel(fila,83);//w11ja
                //oPreta.puertav = dameValorExcel(fila,84);//w11ka
                //oPreta.vventar = Double.parseDouble(dameValorExcel(fila,11));//F45N8A
                //oPreta.otratas = dameValorExcel(fila,86);//F45N9A
                //oPreta.fiabilidad = null;
                //oPreta.numexpv = null;
                //oPreta.codusua = null;
                
                if (oPreta.insert(conexion) == 1)
                {
                    logger.info("Insertada categorización en pretasaciones nº: "+ fila+ " con el nº de petición: "+oPreta.numpet);                                   
                    if (Objetos.Otradoc.insert(oPreta.numpet.toString(), dameValorExcel(fila,0)+".pdf", "application/pdf", "Tasación anterior", 4410,4504, "juan", new java.io.File("/data/informes/cargapretasaExcel/dispersion/"+dameValorExcel(fila,0)+".pdf"), conexOtradoc) == 1)
                    {
                        conexion.commit();  
                        conexOtradoc.commit();
                        insertados ++;         
                        logger.error("Insertada documentación de dipersion nº: "+fila);
                    }
                    else
                    {
                        conexion.rollback();
                        conexOtradoc.rollback();
                        fallos ++; 
                        logger.error("No insertada documentación de dipersion nº: "+fila);
                    }
                }
                else 
                {
                    logger.error("No insertada dipersion nº: "+fila);
                    conexion.rollback();
                    fallos ++;
                }
                        
                oPreta = null;
             }//for filas
             conexion.close();
             conexOtradoc.close();
             
         }       
         catch (Exception ex)
         {
             logger.info("Excepcion. Descripción: "+ex.toString());
         }
         finally
         {
            try
            {
                if (conexion != null && !conexion.isClosed()) conexion.close();                
            }
            catch (java.sql.SQLException sqlException)
            {     
                logger.info("Imposible cerrar conexión datos");
            }
            try
            {
                if (conexOtradoc != null && !conexOtradoc.isClosed()) conexOtradoc.close();                
            }
            catch (java.sql.SQLException sqlException)
            {     
                logger.info("Imposible cerrar conexión documentacion");
            }
            logger.info("Insertados : "+insertados);
            logger.info("Fallos : "+fallos);
         }//finally     
    }
    
    public void pretasaSavills()
    {
        java.sql.Connection conexion = null; 
        java.sql.Connection conexOtradoc = null;
        String valor = null;
        int insertados = 0;
        int fallos = 0;
        
        //FICHERO LOG4J
         PropertyConfigurator.configure("/data/informes/cargapretasaExcel/Savills/" + "Log4j.properties");   
         try
         {
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@oraserver02:1521:rvtnprod");
            //conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:desarrollo/des0329@oraserver03:1521:rvtn3");
            conexion.setAutoCommit(false);   
            conexOtradoc = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecinfx/valtecinfx@oraserver02:1521:rvtnprod");
            conexOtradoc.setAutoCommit(false);
            //primer fichero cargado sUrlExcel = "/data/informes/cargapretasaExcel/categorizaciones1.xls";
            sUrlExcel = "/data/informes/cargapretasaExcel/Savills/savills.xls";
            oExcel = new Utilidades.Excel(sUrlExcel);              
            celda = null;
            HojaActual = "Hoja1";
            
             Objetos.Pretasaciones oPreta = null;
             
             //86 columnas + 4460 filas
             //con el fichero categorizaciones1 for (int fila = 1; fila <= 4459; fila ++)
             for (int fila = 2; fila < 8; fila ++)
             {//registro
                System.out.println("Fila: "+fila);
                oPreta = new Objetos.Pretasaciones(); 
                oPreta.numpet = Utilidades.Conexion.getSequenceNextVal("SEQ_PRETASA", conexion);
                //Utilidades.Cadenas.getValorMostrarWeb(rsDatos,"hucd")+Utilidades.Cadenas.getValorMostrarWeb(rsDatos,"a5cd")
                //el la primera carga de Savills en esa columna nos dieron referencias que comenzaban por D oPreta.numpreta = "100."+dameValorExcel(fila,0)+"-16";
                oPreta.numpreta = "100."+oPreta.numpet+"-16";
                oPreta.codcli = 100;                
                oPreta.coddel = getcoddelProvincia (dameValorExcel(fila,5).substring(0, 2),conexion);                                        
                oPreta.oficina = "0100"; //abcd
                oPreta.numexpc = oPreta.numpet;
                //oPreta.ag_obj = dameValorExcel(fila,2);//hucd
                //oPreta.objeto = dameValorExcel(fila,1);
                oPreta.num_tas = "1"; //dameValorExcel(fila,4);//o5br               
                //oPreta.naturbien = dameValorExcel(fila,7);//aist
                //oPreta.ubica = dameValorExcel(fila,6);//vl8oa
                //oPreta.acabslo = dameValorExcel(fila,7);//vl8pa
                //oPreta.acabpd = dameValorExcel(fila,8);//vl8qa
                //oPreta.carpext = dameValorExcel(fila,9);//vl8ra
                //oPreta.planta = Integer.parseInt(dameValorExcel(fila,10));//vl8sa
                //oPreta.antig = Integer.parseInt(dameValorExcel(fila,9));//vl8ta
                //oPreta.ndormit = Integer.parseInt(dameValorExcel(fila,12));//vl8ua
                //oPreta.nbanos = Integer.parseInt(dameValorExcel(fila,13));//vl8va
                //oPreta.ascens = dameValorExcel(fila,14);//vl8wa
                //oPreta.calefacc = dameValorExcel(fila,15);//vl8xa
                //oPreta.estcons = dameValorExcel(fila,16);//vl8ya
                //oPreta.calcons = dameValorExcel(fila,17);//w11qa
                //oPreta.suputil = Double.parseDouble(dameValorExcel(fila,18));//a2va
                //oPreta.supcons = Double.parseDouble(dameValorExcel(fila,8));//cyva
                //oPreta.supparc = Double.parseDouble(dameValorExcel(fila,20));//z1va
                oPreta.tipopretasa = "D";  //categorizacion.e33eaa
                //oPreta.tipologia = dameValorExcel(fila,22);//e33eba
                //oPreta.esvpo = dameValorExcel(fila,23);//pist
                //oPreta.exptevpo = dameValorExcel(fila,24);//e3k3a
                //valor = dameValorExcel(fila,25);//dddt                
                //if (!valor.equals("1940-01-01")) oPreta.fchvpo = Utilidades.Cadenas.getDate(Integer.parseInt(valor.substring(8, 10)), Integer.parseInt(valor.substring(5, 7)), Integer.parseInt(valor.substring(0, 4)));//dddt
                //oPreta.autopromocion = dameValorExcel(fila,26);//q2ts
                //oPreta.finca = dameValorExcel(fila,27);//hrnb
                //oPreta.registro = Integer.parseInt(dameValorExcel(fila,28));//hsnb
                //oPreta.localireg = dameValorExcel(fila,29);//g4tx
                //oPreta.titulareg = dameValorExcel(fila,30);//s4tx
                //oPreta.fcatastral = FCATASTRAL
                //oPreta.vivnueva = dameValorExcel(fila,31);//wost
                //oPreta.plantassras = Integer.parseInt(dameValorExcel(fila,32));//e33eca
                //oPreta.sotanos = Integer.parseInt(dameValorExcel(fila,33));//e33eda
                //oPreta.nplantas = Integer.parseInt(dameValorExcel(fila,34));//e33eea                
                //oPreta.reforbloq = dameValorExcel(fila,35);//e33efa
                //valor = dameValorExcel(fila,36);//e33ega
                //if (!valor.equals("1940-01-01")) oPreta.fchreforbloq = Utilidades.Cadenas.getDate(Integer.parseInt(valor.substring(8, 10)), Integer.parseInt(valor.substring(5, 7)), Integer.parseInt(valor.substring(0, 4)));
                //oPreta.ite = dameValorExcel(fila,37);//e33eha
                //valor = dameValorExcel(fila,38);//e33eia
                //if (!valor.equals("1940-01-01")) oPreta.fchite = Utilidades.Cadenas.getDate(Integer.parseInt(valor.substring(8, 10)), Integer.parseInt(valor.substring(5, 7)), Integer.parseInt(valor.substring(0, 4)));
                //oPreta.reforviv = dameValorExcel(fila,39);//e33eja
                //valor = dameValorExcel(fila,40);//e33eka
                //if (!valor.equals("1940-01-01")) oPreta.fchreforviv = Utilidades.Cadenas.getDate(Integer.parseInt(valor.substring(8, 10)), Integer.parseInt(valor.substring(5, 7)), Integer.parseInt(valor.substring(0, 4)));
                //oPreta.caliacabados = dameValorExcel(fila,41);//e33ela
                //oPreta.aireacond = dameValorExcel(fila,42);//e33ema
                //oPreta.telefonia = dameValorExcel(fila,43);//e33ena
                //oPreta.otrasinstal = dameValorExcel(fila,44);//e33eoa
                //oPreta.anxpiscina = dameValorExcel(fila,45);//e33epa
                //oPreta.anxgaraje = dameValorExcel(fila,46);//f115qa
                //oPreta.anxtrastero = dameValorExcel(fila,47);//f115pa
                //oPreta.anxporche = dameValorExcel(fila,48);//e33eqa
                //oPreta.anxotro = dameValorExcel(fila,49);//e33era
                //oPreta.piscinaurb = dameValorExcel(fila,50);//e33esa
                //oPreta.jardinesurb = dameValorExcel(fila,51);//e33eta
               // oPreta.otrosurb = dameValorExcel(fila,52);//e33eua
                //oPreta.constanio = Integer.parseInt(dameValorExcel(fila,53));//e33eva
                //oPreta.comentc = dameValorExcel(fila,54);//sltx
                //oPreta.valtotpretaviv = Double.parseDouble(dameValorExcel(fila,55));//e33ewa
                //oPreta.valunitpretaviv = Double.parseDouble(dameValorExcel(fila,56));//e33exa
                //oPreta.valtotpretatrt = Double.parseDouble(dameValorExcel(fila,57));//e33eya
                //oPreta.valunitpretatrt = Double.parseDouble(dameValorExcel(fila,58));//e33eza
                //oPreta.valtotpretapz = Double.parseDouble(dameValorExcel(fila,59));//e33f0a
                //oPreta.valunitpretapz = Double.parseDouble(dameValorExcel(fila,60));//e33f1a
                //oPreta.valtotrentviv = Double.parseDouble(dameValorExcel(fila,61));//e33f2a
                //oPreta.valunitrentviv = Double.parseDouble(dameValorExcel(fila,62));//e33f3a
                //oPreta.valtotrentrt = Double.parseDouble(dameValorExcel(fila,63));//e33f4a
                //oPreta.valunitrentrt = Double.parseDouble(dameValorExcel(fila,64));//e33f5a
                //oPreta.valtotrentpz = Double.parseDouble(dameValorExcel(fila,65));//e33f6a
                //oPreta.valunitrentpz = Double.parseDouble(dameValorExcel(fila,66));//e33f7a
                //oPreta.valorpretasa = Double.parseDouble(dameValorExcel(fila,10));//e29fa
                oPreta.estado = "2"; //e299a
                //valor = dameValorExcel(fila,69);//mbdt
                //oPreta.fchuci = Utilidades.Cadenas.getDate("12-03-2013");
                oPreta.fchuci = Utilidades.Cadenas.getDate("25-05-2017");
                //oPreta.fchuci = Utilidades.Cadenas.getDate(Integer.parseInt(valor.substring(8, 10)), Integer.parseInt(valor.substring(5, 7)), Integer.parseInt(valor.substring(0, 4)));
                //oPreta.horauci = "12:30:00";//dameValorExcel(fila,70);//i2n1
                oPreta.horauci = "10:00:00";//dameValorExcel(fila,70);//i2n1
                //valor = dameValorExcel(fila,71);//v59ya
                //oPreta.fchval = Utilidades.Cadenas.getDate(Integer.parseInt(valor.substring(8, 10)), Integer.parseInt(valor.substring(5, 7)), Integer.parseInt(valor.substring(0, 4)));
                oPreta.fchval = Utilidades.Cadenas.getDate("01-01-1940");
                oPreta.horaval = "00:00:00";//e29aa
                oPreta.postalv = Integer.parseInt(dameValorExcel(fila,5));//e29ba
                oPreta.provinv = dameValorExcel(fila,6);//w11aa
                if (oPreta.provinv != null) oPreta.provinv = oPreta.provinv.toUpperCase();
                oPreta.municiv = dameValorExcel(fila,7);//w11ba
                if (oPreta.municiv != null) 
                {
                    oPreta.municiv = oPreta.municiv.toUpperCase();
                    if (oPreta.municiv.length() > 26) oPreta.municiv = oPreta.municiv.substring(0, 26);
                }
                oPreta.localiv = oPreta.municiv;//w11ca                
                //oPreta.codsituv = dameValorExcel(fila,);//w11da
                //oPreta.situacionv = dameValorExcel(fila,78);//w11ea
                if (dameValorExcel(fila,2) != null) 
                {
                    oPreta.tipoviav = dameValorExcel(fila,2);//w11fa
                    if (oPreta.tipoviav.length() > 4) oPreta.tipoviav = oPreta.tipoviav.substring(0, 4);
                }
                oPreta.callev = dameValorExcel(fila,3);
                if (oPreta.callev.length() > 60) 
                {
                    oPreta.comentc = oPreta.callev;
                    oPreta.callev = oPreta.callev.substring(0, 60);                    
                }                
                //oPreta.numerov = dameValorExcel(fila,81);//w11ha
                //oPreta.escalerav = dameValorExcel(fila,82);//w11ia
                //oPreta.plantav = dameValorExcel(fila,83);//w11ja
                //oPreta.puertav = dameValorExcel(fila,84);//w11ka
                //oPreta.vventar = Double.parseDouble(dameValorExcel(fila,11));//F45N8A
                //oPreta.otratas = dameValorExcel(fila,86);//F45N9A
                //oPreta.fiabilidad = null;
                //oPreta.numexpv = null;
                //oPreta.codusua = null;
                oPreta.comentc = "Titular: "+dameValorExcel(fila,1);
                
                if (oPreta.insert(conexion) == 1)
                {
                    logger.info("Insertada en pretasaciones nº: "+ fila+ " con el nº de petición: "+oPreta.numpet);                                   
                    conexion.commit(); 
                    insertados ++;      
                    if (new java.io.File("/data/informes/cargapretasaExcel/Savills/"+dameValorExcel(fila,0)+".pdf").exists())
                    {
                        if (Objetos.Otradoc.insert(oPreta.numpet.toString(), dameValorExcel(fila,0)+".pdf", "application/pdf", "Tasación anterior", 4410,4504, "juan", new java.io.File("/data/informes/cargapretasaExcel/Savills/"+dameValorExcel(fila,0)+".pdf"), conexOtradoc) == 1)
                        {

                            conexOtradoc.commit();                                                   
                            logger.error("Insertada documentación  fila nº: "+fila);
                        }
                        else
                        {                            
                            conexOtradoc.rollback();                            
                            logger.error("No insertada documentación  fila nº: "+fila);
                        }
                    }
                    else logger.info("Fila nº: "+ fila+ " sin documentacion asociada"); 
                }
                else 
                {
                    logger.error("No insertada fila nº: "+fila);
                    conexion.rollback();
                    fallos ++;
                }
                        
                oPreta = null;
             }//for filas
             conexion.close();
             conexOtradoc.close();
             
         }       
         catch (Exception ex)
         {
             logger.info("Excepcion. Descripción: "+ex.toString());
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
            catch (java.sql.SQLException sqlException)
            {     
                logger.info("Imposible cerrar conexión datos");
            }
            try
            {
                if (conexOtradoc != null && !conexOtradoc.isClosed()) conexOtradoc.close();                
            }
            catch (java.sql.SQLException sqlException)
            {     
                logger.info("Imposible cerrar conexión documentacion");
            }
            logger.info("Insertados : "+insertados);
            logger.info("Fallos : "+fallos);
         }//finally     
    }
    
    
    public void pretasa126()
    {
        java.sql.Connection conexion = null; 
        String valor = null;
        int insertados = 0;
        int fallos = 0;
        boolean fcatastralIncompleta = false;
        
        //FICHERO LOG4J
         PropertyConfigurator.configure("/data/informes/cargapretasaExcel/" + "Log4j.properties");   
         try
         {
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@oraserver02:1521:rvtnprod");
            //conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:desarrollo/des0329@oraserver03:1521:rvtn3");
            conexion.setAutoCommit(false);                                
              
            //primer fichero cargado sUrlExcel = "/data/informes/cargapretasaExcel/categorizaciones1.xls";
            //sUrlExcel = "/data/informes/cargapretasaExcel/ENCARGO ACTUALIZACIONES ING JUNIO2015.xls";
            sUrlExcel = "/data/informes/cargapretasaExcel/2016.03.15 INMUEBLES ADJUDICADOS VTASSET.xls";
            oExcel = new Utilidades.Excel(sUrlExcel);              
            celda = null;
            HojaActual = "Hoja1";
            
             Objetos.Pretasaciones oPreta = null;
             
             //86 columnas + 4460 filas
             //con el fichero categorizaciones1 for (int fila = 1; fila <= 4459; fila ++)
             for (int fila = 2; fila < 94; fila ++)
             {//registro
                System.out.println("Fila: "+fila);
                fcatastralIncompleta = false;
                oPreta = new Objetos.Pretasaciones(); 
                oPreta.numpet = Utilidades.Conexion.getSequenceNextVal("SEQ_PRETASA", conexion);
                //Utilidades.Cadenas.getValorMostrarWeb(rsDatos,"hucd")+Utilidades.Cadenas.getValorMostrarWeb(rsDatos,"a5cd")
                //oPreta.numpreta = "126."+Utilidades.Cadenas.completTextWithLeftCharacter(dameValorExcel(fila,0).trim(),'0',10)+"-15";
                oPreta.numpreta = "126."+oPreta.numpet+"-16";
                oPreta.codcli = 126;                
                oPreta.coddel = getcoddelProvincia (dameValorExcel(fila,1).substring(0, 2),conexion);                        
                oPreta.oficina = "0126";
                oPreta.numexpc = oPreta.numpet;
                //oPreta.ag_obj = dameValorExcel(fila,2);//hucd
                //oPreta.objeto = dameValorExcel(fila,1);
                //oPreta.num_tas = ""; 
                //oPreta.naturbien = dameValorExcel(fila,7);//aist
                //oPreta.ubica = dameValorExcel(fila,6);//vl8oa
                //oPreta.acabslo = dameValorExcel(fila,7);//vl8pa
                //oPreta.acabpd = dameValorExcel(fila,8);//vl8qa
                //oPreta.carpext = dameValorExcel(fila,9);//vl8ra
                //oPreta.planta = Integer.parseInt(dameValorExcel(fila,10));//vl8sa
                //oPreta.antig = Integer.parseInt(dameValorExcel(fila,9));//vl8ta
                try
                {
                    oPreta.ndormit = Integer.parseInt(dameValorExcel(fila,7));//vl8ua
                }
                catch (Exception e)
                {
                    
                }
                //oPreta.nbanos = Integer.parseInt(dameValorExcel(fila,13));//vl8va
                
                if (dameValorExcel(fila,8) != null)
                {
                    if (dameValorExcel(fila,8).equals("SI"))  oPreta.ascens = "S";
                    else oPreta.ascens = "N";
                }
                
                //oPreta.calefacc = dameValorExcel(fila,15);//vl8xa
                //oPreta.estcons = dameValorExcel(fila,16);//vl8ya
                //oPreta.calcons = dameValorExcel(fila,17);//w11qa
                try
                {
                    oPreta.suputil = Double.parseDouble(dameValorExcel(fila,5));//a2va
                }
                catch (Exception e)
                {
                    
                }
                try
                {
                    oPreta.supcons = Double.parseDouble(dameValorExcel(fila,6));//cyva
                }
                catch (Exception e)
                {
                    
                }
                //oPreta.supparc = Double.parseDouble(dameValorExcel(fila,20));//z1va
                oPreta.tipopretasa = "P";  //pretasacion
                oPreta.tipologia = "P";
                //oPreta.esvpo = dameValorExcel(fila,23);//pist
                //oPreta.exptevpo = dameValorExcel(fila,24);//e3k3a
                //valor = dameValorExcel(fila,25);//dddt                
                //if (!valor.equals("1940-01-01")) oPreta.fchvpo = Utilidades.Cadenas.getDate(Integer.parseInt(valor.substring(8, 10)), Integer.parseInt(valor.substring(5, 7)), Integer.parseInt(valor.substring(0, 4)));//dddt
                //oPreta.autopromocion = dameValorExcel(fila,26);//q2ts
                //oPreta.finca = dameValorExcel(fila,27);//hrnb
                //oPreta.registro = Integer.parseInt(dameValorExcel(fila,28));//hsnb
                //oPreta.localireg = dameValorExcel(fila,29);//g4tx
                //oPreta.titulareg = dameValorExcel(fila,30);//s4tx
                oPreta.fcatastral = dameValorExcel(fila,13);
                if (oPreta.fcatastral != null && oPreta.fcatastral.length() > 60) 
                {
                    oPreta.fcatastral = oPreta.fcatastral.substring(0, 20);
                    fcatastralIncompleta = true;
                }
                //oPreta.vivnueva = dameValorExcel(fila,31);//wost
                //oPreta.plantassras = Integer.parseInt(dameValorExcel(fila,32));//e33eca
                //oPreta.sotanos = Integer.parseInt(dameValorExcel(fila,33));//e33eda
                //oPreta.nplantas = Integer.parseInt(dameValorExcel(fila,34));//e33eea                
                //oPreta.reforbloq = dameValorExcel(fila,35);//e33efa
                //valor = dameValorExcel(fila,36);//e33ega
                //if (!valor.equals("1940-01-01")) oPreta.fchreforbloq = Utilidades.Cadenas.getDate(Integer.parseInt(valor.substring(8, 10)), Integer.parseInt(valor.substring(5, 7)), Integer.parseInt(valor.substring(0, 4)));
                //oPreta.ite = dameValorExcel(fila,37);//e33eha
                //valor = dameValorExcel(fila,38);//e33eia
                //if (!valor.equals("1940-01-01")) oPreta.fchite = Utilidades.Cadenas.getDate(Integer.parseInt(valor.substring(8, 10)), Integer.parseInt(valor.substring(5, 7)), Integer.parseInt(valor.substring(0, 4)));
                //oPreta.reforviv = dameValorExcel(fila,39);//e33eja
                //valor = dameValorExcel(fila,40);//e33eka
                //if (!valor.equals("1940-01-01")) oPreta.fchreforviv = Utilidades.Cadenas.getDate(Integer.parseInt(valor.substring(8, 10)), Integer.parseInt(valor.substring(5, 7)), Integer.parseInt(valor.substring(0, 4)));
                //oPreta.caliacabados = dameValorExcel(fila,41);//e33ela
                //oPreta.aireacond = dameValorExcel(fila,42);//e33ema
                //oPreta.telefonia = dameValorExcel(fila,43);//e33ena
                //oPreta.otrasinstal = dameValorExcel(fila,44);//e33eoa
                //oPreta.anxpiscina = dameValorExcel(fila,45);//e33epa
                if (dameValorExcel(fila,9) != null)
                {
                    if (dameValorExcel(fila,9).equals("SI"))  oPreta.anxgaraje = "S";
                    else oPreta.anxgaraje = "N";
                }                
                //oPreta.anxtrastero = dameValorExcel(fila,47);//f115pa
                //oPreta.anxporche = dameValorExcel(fila,48);//e33eqa
                //oPreta.anxotro = dameValorExcel(fila,49);//e33era
                //oPreta.piscinaurb = dameValorExcel(fila,50);//e33esa
                //oPreta.jardinesurb = dameValorExcel(fila,51);//e33eta
               // oPreta.otrosurb = dameValorExcel(fila,52);//e33eua
                //oPreta.constanio = Integer.parseInt(dameValorExcel(fila,53));//e33eva
                valor = null;
                try
                {
                    valor = new SimpleDateFormat("dd/MM/yyyy").format(dameValorFechaExcel(fila,11));
                }
                catch (Exception e)
                {
                    valor = null;
                }   
                oPreta.comentc = "";
                if (valor != null) oPreta.comentc = "Fecha de tasación: "+valor;
                //else oPreta.comentc = "Fecha de valoración: no especificada";
                valor = null;
                valor = dameValorExcel(fila,10);
                if (valor != null && !valor.equalsIgnoreCase("")) oPreta.comentc += ". Valor tasación más reciente: "+valor;                    
                valor = null;
                valor = dameValorExcel(fila,12);
                if (valor != null) oPreta.comentc += ". Ocupado: "+valor;   
                valor = null;
                valor = dameValorExcel(fila,15);
                if (valor != null) oPreta.comentc += ". Nivel 1-5: "+valor;   
                if (fcatastralIncompleta) oPreta.comentc += "Ref. Catastrales: "+dameValorExcel(fila,13).trim();
                if (oPreta.comentc != null && oPreta.comentc.length() > 230) oPreta.comentc = oPreta.comentc.substring(0, 230);
                //oPreta.valtotpretaviv = Double.parseDouble(dameValorExcel(fila,55));//e33ewa
                //oPreta.valunitpretaviv = Double.parseDouble(dameValorExcel(fila,56));//e33exa
                //oPreta.valtotpretatrt = Double.parseDouble(dameValorExcel(fila,57));//e33eya
                //oPreta.valunitpretatrt = Double.parseDouble(dameValorExcel(fila,58));//e33eza
                //oPreta.valtotpretapz = Double.parseDouble(dameValorExcel(fila,59));//e33f0a
                //oPreta.valunitpretapz = Double.parseDouble(dameValorExcel(fila,60));//e33f1a
                //oPreta.valtotrentviv = Double.parseDouble(dameValorExcel(fila,61));//e33f2a
                //oPreta.valunitrentviv = Double.parseDouble(dameValorExcel(fila,62));//e33f3a
                //oPreta.valtotrentrt = Double.parseDouble(dameValorExcel(fila,63));//e33f4a
                //oPreta.valunitrentrt = Double.parseDouble(dameValorExcel(fila,64));//e33f5a
                //oPreta.valtotrentpz = Double.parseDouble(dameValorExcel(fila,65));//e33f6a
                //oPreta.valunitrentpz = Double.parseDouble(dameValorExcel(fila,66));//e33f7a
                //oPreta.valorpretasa = Double.parseDouble(dameValorExcel(fila,10));//e29fa
                oPreta.estado = "2";
                //valor = dameValorExcel(fila,69);//mbdt                
                oPreta.fchuci = Utilidades.Cadenas.getDate("17-03-2016");                                
                oPreta.horauci = "14:00:00";//dameValorExcel(fila,70);//i2n1                
                oPreta.fchval = Utilidades.Cadenas.getDate("01-01-1940");
                oPreta.horaval = "00:00:00";//e29aa
                oPreta.postalv = Integer.parseInt(dameValorExcel(fila,1));//e29ba
                oPreta.provinv = getProvincia(dameValorExcel(fila,1).substring(0, 2),conexion);//w11aa
                oPreta.municiv = null;
                valor = dameValorExcel(fila,4);                
                if (valor != null) oPreta.localiv = valor.toUpperCase();
                else oPreta.localiv = null;
                if (oPreta.localiv != null && oPreta.localiv.length() > 26) oPreta.localiv = oPreta.localiv.substring(0, 26);
                //oPreta.codsituv = dameValorExcel(fila,);//w11da
                //oPreta.situacionv = dameValorExcel(fila,78);//w11ea
                //oPreta.tipoviav = dameValorExcel(fila,5);//w11fa
                /*
                valor = null;
                if (dameValorExcel(fila,6) != null && !dameValorExcel(fila,6).trim().equalsIgnoreCase("")) valor = dameValorExcel(fila,6).trim().toUpperCase()+". ";
                if (dameValorExcel(fila,7) != null && !dameValorExcel(fila,7).trim().equalsIgnoreCase("")) oPreta.situacionv = dameValorExcel(fila,7).trim().toUpperCase()+". ";
                if (dameValorExcel(fila,8) != null && !dameValorExcel(fila,8).trim().equalsIgnoreCase("")) valor += dameValorExcel(fila,8).trim().toUpperCase()+". ";
                if (dameValorExcel(fila,9) != null && !dameValorExcel(fila,9).trim().equalsIgnoreCase("")) 
                {
                    if (Utilidades.Cadenas.TrimTotal(dameValorExcel(fila,9)).length() > 5) valor += dameValorExcel(fila,9).trim().toUpperCase()+". ";
                    else oPreta.puertav = Utilidades.Cadenas.TrimTotal(dameValorExcel(fila,9)).toUpperCase();
                }
                oPreta.callev = valor;//w11ga
*/
                oPreta.callev = dameValorExcel(fila,2);
                if (oPreta.callev != null && oPreta.callev.length() > 60) oPreta.callev = oPreta.callev.substring(0, 60);
                //oPreta.numerov = dameValorExcel(fila,81);//w11ha
                //oPreta.escalerav = dameValorExcel(fila,82);//w11ia
                //oPreta.plantav = dameValorExcel(fila,83);//w11ja
                //oPreta.puertav = dameValorExcel(fila,84);//w11ka
                //oPreta.vventar = Double.parseDouble(dameValorExcel(fila,11));//F45N8A
                //oPreta.otratas = dameValorExcel(fila,86);//F45N9A
                //oPreta.fiabilidad = null;
                //oPreta.numexpv = null;
                //oPreta.codusua = null;
                
                if (oPreta.insert(conexion) == 1)
                {
                    logger.info("Insertada pretasacion nº: "+ fila+ " con el nº de petición: "+oPreta.numpet);
                    conexion.commit();
                    insertados ++;
                }
                else 
                {
                    logger.error("No insertada pretasacion nº: "+fila);
                    conexion.rollback();
                    fallos ++;
                }
                        
                oPreta = null;
             }//for filas
             conexion.close();
             
         }       
         catch (Exception ex)
         {
             logger.info("Excepcion. Descripción: "+ex.toString());
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
            catch (java.sql.SQLException sqlException)
            {     
                logger.info("Imposible cerrar conexión con la B.D Informix");
            }       
            logger.info("Insertados : "+insertados);
            logger.info("Fallos : "+fallos);
         }//finally     
    }//pretasa126
    
    public void pretasa250()
    {//SAREB
        java.sql.Connection conexion = null; 
        String valor = null;
        int insertados = 0;
        int fallos = 0;
        String sConsulta = "";
        java.sql.ResultSet rsseq = null;
        
        //FICHERO LOG4J
         PropertyConfigurator.configure("/data/informes/cargapretasaExcel/" + "Log4j.properties");   
         try
         {
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@oraserver01:1521:rvtn1");
            //conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:desarrollo/des0329@oraserver00:1521:rvtn");
            conexion.setAutoCommit(false);                                
              
            //primer fichero cargado sUrlExcel = "/data/informes/cargapretasaExcel/categorizaciones1.xls";
            //sUrlExcel = "/data/informes/cargapretasaExcel/141013-BANKIA-VALENCIA PROV-PROMOCION-PRICING_MIO.xls";
            sUrlExcel = "/data/informes/cargapretasaExcel/141029-SERVIHABITAT-REO-PRICING-NUEVOS ACTIVOS_MIO.xls";
            oExcel = new Utilidades.Excel(sUrlExcel);              
            celda = null;
            HojaActual = "ACTIVOS";  //de los que se ha generado xml y enviado al cliente.
            //HojaActual = "ACTIVOS-SUELOS"; //suelos que no se han valorado y no se han generado xml ni enviado al cliente.
            
             Objetos.Pretasaciones oPreta = null;
             
             
             for (int fila = 0; fila < 117; fila ++)
             {//registro
                System.out.println("Fila: "+fila);
                oPreta = new Objetos.Pretasaciones(); 
                sConsulta = "select seq_pretasa.nextval numpet from dual";
                rsseq = Utilidades.Conexion.select(sConsulta, conexion);
                if (rsseq.next()) oPreta.numpet = rsseq.getInt("numpet");
                rsseq.close();
                rsseq = null;
                //oPreta.numpet = Utilidades.Conexion.getSequenceNextVal("SEQ_PRETASA", conexion);
                //Utilidades.Cadenas.getValorMostrarWeb(rsDatos,"hucd")+Utilidades.Cadenas.getValorMostrarWeb(rsDatos,"a5cd")
                oPreta.numpreta = dameValorExcel(fila,6)+"-"+dameValorExcel(fila,7);
                oPreta.codcli = 250;                
                oPreta.coddel = getcoddelProvincia (dameValorExcel(fila,15),conexion);                      
                oPreta.oficina = dameValorExcel(fila,8); 
                oPreta.numexpc = Integer.parseInt(dameValorExcel(fila,6)); //alcd
                oPreta.idactivo = dameValorExcel(fila,4);
                oPreta.idexpediente = "ADVALGRP_20141031_00000000000000003858";
                valor = dameValorExcel(fila,14);
                if (valor != null && !valor.equalsIgnoreCase("")) oPreta.idufir = Long.parseLong(valor);
                
                //oPreta.ag_obj = dameValorExcel(fila,2);//hucd
                //oPreta.objeto = dameValorExcel(fila,1);
                oPreta.num_tas = "1"; //dameValorExcel(fila,4);//o5br
                //oPreta.naturbien = dameValorExcel(fila,7);//aist
                //oPreta.ubica = dameValorExcel(fila,6);//vl8oa
                //oPreta.acabslo = dameValorExcel(fila,7);//vl8pa
                //oPreta.acabpd = dameValorExcel(fila,8);//vl8qa
                //oPreta.carpext = dameValorExcel(fila,9);//vl8ra
                //oPreta.planta = Integer.parseInt(dameValorExcel(fila,10));//vl8sa
                //oPreta.antig = Integer.parseInt(dameValorExcel(fila,9));//vl8ta
                //oPreta.ndormit = Integer.parseInt(dameValorExcel(fila,12));//vl8ua
                //oPreta.nbanos = Integer.parseInt(dameValorExcel(fila,13));//vl8va
                //oPreta.ascens = dameValorExcel(fila,14);//vl8wa
                //oPreta.calefacc = dameValorExcel(fila,15);//vl8xa
                //oPreta.estcons = dameValorExcel(fila,16);//vl8ya
                //oPreta.calcons = dameValorExcel(fila,17);//w11qa
                valor = dameValorExcel(fila,30);
                if (valor != null && !valor.equalsIgnoreCase("")) oPreta.suputil = Double.parseDouble(dameValorExcel(fila,30));//a2va
                valor = dameValorExcel(fila,29);
                if (valor != null && !valor.equalsIgnoreCase("")) oPreta.supcons = Double.parseDouble(dameValorExcel(fila,29));//cyva
                valor = dameValorExcel(fila,31);
                if (valor != null && !valor.equalsIgnoreCase("")) oPreta.supparc = Double.parseDouble(dameValorExcel(fila,31));//z1va
                valor = dameValorExcel(fila,27);
                if (valor != null && !valor.equalsIgnoreCase("")) oPreta.valorpretasa = Double.parseDouble(dameValorExcel(fila,27));//z1va
                valor = dameValorExcel(fila,28);
                if (valor != null && !valor.equalsIgnoreCase("")) oPreta.valunitpretaviv = Double.parseDouble(dameValorExcel(fila,28));//z1va
                valor = dameValorExcel(fila,27);
                if (valor != null && !valor.equalsIgnoreCase("")) oPreta.valtotpretaviv = Double.parseDouble(dameValorExcel(fila,27));
                oPreta.tipopretasa = "s";  //categorizacion.e33eaa
                //oPreta.tipologia = dameValorExcel(fila,22);//e33eba
                valor = dameValorExcel(fila,3);
                if ( valor != null)
                {
                    valor = valor.trim().toUpperCase();
                    if (valor.indexOf("VIVIENDA") != -1) oPreta.tipologia = "V";
                    else if (valor.indexOf("PLAZA DE GARAJE") != -1) oPreta.tipologia = "Z";
                }
                //oPreta.esvpo = dameValorExcel(fila,23);//pist
                //oPreta.exptevpo = dameValorExcel(fila,24);//e3k3a
                //valor = dameValorExcel(fila,25);//dddt                
                //if (!valor.equals("1940-01-01")) oPreta.fchvpo = Utilidades.Cadenas.getDate(Integer.parseInt(valor.substring(8, 10)), Integer.parseInt(valor.substring(5, 7)), Integer.parseInt(valor.substring(0, 4)));//dddt
                //oPreta.autopromocion = dameValorExcel(fila,26);//q2ts
                oPreta.finca = dameValorExcel(fila,13);//hrnb
                //oPreta.registro = Integer.parseInt(dameValorExcel(fila,28));//hsnb
                //oPreta.localireg = dameValorExcel(fila,29);//g4tx
                //oPreta.titulareg = dameValorExcel(fila,30);//s4tx
                oPreta.fcatastral = dameValorExcel(fila,12);//s4tx
                //oPreta.vivnueva = dameValorExcel(fila,31);//wost
                //oPreta.plantassras = Integer.parseInt(dameValorExcel(fila,32));//e33eca
                //oPreta.sotanos = Integer.parseInt(dameValorExcel(fila,33));//e33eda
                //oPreta.nplantas = Integer.parseInt(dameValorExcel(fila,34));//e33eea                
                //oPreta.reforbloq = dameValorExcel(fila,35);//e33efa
                //valor = dameValorExcel(fila,36);//e33ega
                //if (!valor.equals("1940-01-01")) oPreta.fchreforbloq = Utilidades.Cadenas.getDate(Integer.parseInt(valor.substring(8, 10)), Integer.parseInt(valor.substring(5, 7)), Integer.parseInt(valor.substring(0, 4)));
                //oPreta.ite = dameValorExcel(fila,37);//e33eha
                //valor = dameValorExcel(fila,38);//e33eia
                //if (!valor.equals("1940-01-01")) oPreta.fchite = Utilidades.Cadenas.getDate(Integer.parseInt(valor.substring(8, 10)), Integer.parseInt(valor.substring(5, 7)), Integer.parseInt(valor.substring(0, 4)));
                //oPreta.reforviv = dameValorExcel(fila,39);//e33eja
                //valor = dameValorExcel(fila,40);//e33eka
                //if (!valor.equals("1940-01-01")) oPreta.fchreforviv = Utilidades.Cadenas.getDate(Integer.parseInt(valor.substring(8, 10)), Integer.parseInt(valor.substring(5, 7)), Integer.parseInt(valor.substring(0, 4)));
                //oPreta.caliacabados = dameValorExcel(fila,41);//e33ela
                //oPreta.aireacond = dameValorExcel(fila,42);//e33ema
                //oPreta.telefonia = dameValorExcel(fila,43);//e33ena
                //oPreta.otrasinstal = dameValorExcel(fila,44);//e33eoa
                //oPreta.anxpiscina = dameValorExcel(fila,45);//e33epa
                //oPreta.anxgaraje = dameValorExcel(fila,46);//f115qa
                //oPreta.anxtrastero = dameValorExcel(fila,47);//f115pa
                //oPreta.anxporche = dameValorExcel(fila,48);//e33eqa
                //oPreta.anxotro = dameValorExcel(fila,49);//e33era
                //oPreta.piscinaurb = dameValorExcel(fila,50);//e33esa
                //oPreta.jardinesurb = dameValorExcel(fila,51);//e33eta
               // oPreta.otrosurb = dameValorExcel(fila,52);//e33eua
                //oPreta.constanio = Integer.parseInt(dameValorExcel(fila,53));//e33eva
                //oPreta.comentc = dameValorExcel(fila,54);//sltx
                //oPreta.valtotpretaviv = Double.parseDouble(dameValorExcel(fila,55));//e33ewa
                //oPreta.valunitpretaviv = Double.parseDouble(dameValorExcel(fila,56));//e33exa
                //oPreta.valtotpretatrt = Double.parseDouble(dameValorExcel(fila,57));//e33eya
                //oPreta.valunitpretatrt = Double.parseDouble(dameValorExcel(fila,58));//e33eza
                //oPreta.valtotpretapz = Double.parseDouble(dameValorExcel(fila,59));//e33f0a
                //oPreta.valunitpretapz = Double.parseDouble(dameValorExcel(fila,60));//e33f1a
                //oPreta.valtotrentviv = Double.parseDouble(dameValorExcel(fila,61));//e33f2a
                //oPreta.valunitrentviv = Double.parseDouble(dameValorExcel(fila,62));//e33f3a
                //oPreta.valtotrentrt = Double.parseDouble(dameValorExcel(fila,63));//e33f4a
                //oPreta.valunitrentrt = Double.parseDouble(dameValorExcel(fila,64));//e33f5a
                //oPreta.valtotrentpz = Double.parseDouble(dameValorExcel(fila,65));//e33f6a
                //oPreta.valunitrentpz = Double.parseDouble(dameValorExcel(fila,66));//e33f7a
                //oPreta.valorpretasa = Double.parseDouble(dameValorExcel(fila,10));//e29fa
                oPreta.estado = "2";
                //valor = dameValorExcel(fila,69);//mbdt
                //oPreta.fchuci = Utilidades.Cadenas.getDate("12-03-2013");
                oPreta.fchuci = Utilidades.Cadenas.getDate("03-11-2014");
                //oPreta.fchuci = Utilidades.Cadenas.getDate(Integer.parseInt(valor.substring(8, 10)), Integer.parseInt(valor.substring(5, 7)), Integer.parseInt(valor.substring(0, 4)));
                //oPreta.horauci = "12:30:00";//dameValorExcel(fila,70);//i2n1
                oPreta.horauci = "13:30:00";//dameValorExcel(fila,70);//i2n1
                //valor = dameValorExcel(fila,71);//v59ya                
                oPreta.fchval = Utilidades.Cadenas.getDate("01-01-1940");
                oPreta.horaval = "00:00:00";//e29aa
                
                oPreta.postalv = Integer.parseInt(dameValorExcel(fila,17).trim());//e29ba
                oPreta.provinv = dameValorExcel(fila,15).trim();//w11aa
                oPreta.municiv = dameValorExcel(fila,16).trim();//w11ba
                oPreta.localiv = dameValorExcel(fila,16).trim();//w11ca
                //oPreta.codsituv = dameValorExcel(fila,);//w11da
                //oPreta.situacionv = dameValorExcel(fila,78);//w11ea
                //oPreta.tipoviav = dameValorExcel(fila,79);//w11fa
                oPreta.callev = dameValorExcel(fila,19);//w11ga
                if (oPreta.callev != null) oPreta.callev = oPreta.callev.trim();
                oPreta.numerov = dameValorExcel(fila,20);//w11ha
                if (oPreta.numerov != null && !oPreta.numerov.equalsIgnoreCase("")) oPreta.callev += " "+oPreta.numerov.trim();
                oPreta.escalerav = dameValorExcel(fila,22);//w11ia
                if (oPreta.escalerav != null && !oPreta.escalerav.equalsIgnoreCase("")) oPreta.callev += " "+oPreta.escalerav.trim();
                oPreta.plantav = dameValorExcel(fila,23);//w11ja
                if (oPreta.plantav != null && !oPreta.plantav.equalsIgnoreCase("")) oPreta.callev += " "+oPreta.plantav.trim();
                if (oPreta.callev.length() > 60) oPreta.callev = oPreta.callev.substring(0, 60);
                if (oPreta.plantav != null)
                {
                    if (oPreta.plantav.length() > 2) oPreta.plantav = oPreta.plantav.substring(0, 2);
                }
                oPreta.puertav = dameValorExcel(fila,24);//w11ka
                if (oPreta.puertav != null)
                {
                    if (oPreta.puertav.length() > 5) oPreta.puertav = oPreta.puertav.substring(0, 5);
                }
                if (oPreta.coddel > 0 && oPreta.coddel < 20) valor = "1";
                else if (oPreta.coddel > 19 && oPreta.coddel < 30) valor = "2";
                else if (oPreta.coddel > 49 && oPreta.coddel < 60) valor = "5";
                oPreta.numexpv = valor+"50."+dameValorExcel(fila,6)+"-"+dameValorExcel(fila,7)+"-14";
                //oPreta.vventar = Double.parseDouble(dameValorExcel(fila,11));//F45N8A
                //oPreta.otratas = dameValorExcel(fila,86);//F45N9A
                //oPreta.fiabilidad = null;
                //oPreta.numexpv = null;
                //oPreta.codusua = null;
                
                if (oPreta.insert(conexion) == 1)
                {
                    logger.info("Insertada peticion sareb nº: "+ fila+ " con el nº de petición: "+oPreta.numpet);
                    conexion.commit();
                    insertados ++;
                }
                else 
                {
                    logger.error("No insertada peticion sareb nº: "+fila);
                    conexion.rollback();
                    fallos ++;
                }
                        
                oPreta = null;
             }//for filas
             conexion.close();
             
         }       
         catch (Exception ex)
         {
             logger.info("Excepcion. Descripción: "+ex.toString());
         }
         finally
         {
            try
            {
                if (conexion != null && !conexion.isClosed()) conexion.close();                
            }
            catch (java.sql.SQLException sqlException)
            {     
                logger.info("Imposible cerrar conexión con la B.D");
            }       
            logger.info("Insertados : "+insertados);
            logger.info("Fallos : "+fallos);
         }//finally     
    }
    
    public void cargaDocPretasacionesUCI()
    {
        
        
        
        
        
        
        
        
        String rutaFichero = "/data/informes/cargapretasaExcel/docpretaUCI/Expte 23-29611 objeto 23-28997.pdf";
        //String rutaFichero = "/data/informes/cargapretasaExcel/docpretaUCI/";
        //String rutaFichero = "/data/informes/cargapretasaExcel/docpretaUCI/";
        //String rutaFichero = "/data/informes/cargapretasaExcel/docpretaUCI/";
        //String rutaFichero = "/data/informes/cargapretasaExcel/docpretaUCI/";
        //String rutaFichero = "/data/informes/cargapretasaExcel/docpretaUCI/";
        //String rutaFichero = "/data/informes/cargapretasaExcel/docpretaUCI/";
        //String rutaFichero = "/data/informes/cargapretasaExcel/docpretaUCI/";
        //String rutaFichero = "/data/informes/cargapretasaExcel/docpretaUCI/";
        //String rutaFichero = "/data/informes/cargapretasaExcel/docpretaUCI/";
        //String rutaFichero = "/data/informes/cargapretasaExcel/docpretaUCI/";
        //String rutaFichero = "/data/informes/cargapretasaExcel/docpretaUCI/";
        //String rutaFichero = "/data/informes/cargapretasaExcel/docpretaUCI/";
        //String rutaFichero = "/data/informes/cargapretasaExcel/docpretaUCI/";
        //String rutaFichero = "/data/informes/cargapretasaExcel/docpretaUCI/";
        //String rutaFichero = "/data/informes/cargapretasaExcel/docpretaUCI/";
        //String rutaFichero = "/data/informes/cargapretasaExcel/docpretaUCI/";
        //String rutaFichero = "/data/informes/cargapretasaExcel/docpretaUCI/";
        //String rutaFichero = "/data/informes/cargapretasaExcel/docpretaUCI/";
        //String rutaFichero = "/data/informes/cargapretasaExcel/docpretaUCI/";
        //String rutaFichero = "/data/informes/cargapretasaExcel/docpretaUCI/";
        //String rutaFichero = "/data/informes/cargapretasaExcel/docpretaUCI/";
        //String rutaFichero = "/data/informes/cargapretasaExcel/docpretaUCI/";
        
        
        java.io.File fichero = null;
        java.sql.Connection oConnectionDocumentation = null;
        
        try
        {
            fichero = new java.io.File(rutaFichero);
            if (fichero.exists())
            {
                oConnectionDocumentation = Utilidades.Conexion.getConnectionDocgraficaProduccion();
                oConnectionDocumentation.setAutoCommit(false);
                
                String mime_type = "application/pdf";
                String pie = "Tasación anterior";
                int numero = 0;
                String descripcion = "";
                int tipo_doc = 4410;
                int tipoid = 4504;
                
                
                
                
                
                
                
                String agencia = "23";
                String objeto = "028997";
                //
                //String agencia = "";
                //String objeto = "";
                //
                //String agencia = "";
                //String objeto = "";
                //
                //String agencia = "";
                //String objeto = "";
                //
                //String agencia = "";
                //String objeto = "";
                //
                //String agencia = "";
                //String objeto = "";
                //
                //String agencia = "";
                //String objeto = "";
                //
                //String agencia = "";
                //String objeto = "";
                //
                //String agencia = "";
                //String objeto = "";
                //
                //String agencia = "";
                //String objeto = "";
                //
                //String agencia = "";
                //String objeto = "";
                //
                //String agencia = "";
                //String objeto = "";
                //
                //String agencia = "";
                //String objeto = "";
                //
                //String agencia = "";
                //String objeto = "";
                //
                //String agencia = "";
                //String objeto = "";
                //
                //String agencia = "";
                //String objeto = "";
                //
                //String agencia = "";
                //String objeto = "";
                //
                //String agencia = "";
                //String objeto = "";
                //
                //String agencia = "";
                //String objeto = "";
                //
                //String agencia = "";
                //String objeto = "";
                //
                //String agencia = "";
                //String objeto = "";
                //
                //String agencia = "";
                //String objeto = "";
                //
                //String agencia = "";
                //String objeto = "";
                //
                //String agencia = "";
                //String objeto = "";
                
                
                
                
                Statement stmtOracle = oConnectionDocumentation.createStatement();
                ResultSet rsOracle;
                
                rsOracle = stmtOracle.executeQuery("select seq_otradoc.nextval from dual");
                if (rsOracle.next()) numero = rsOracle.getInt(1);
                    
                    //Inserta la nota simple en docgrafica
                    //Primero inserta la fila en la BD
                    stmtOracle.execute("insert into otradoc values ('" + agencia.trim()+objeto.trim() + "'," + numero + ",'" + descripcion + "','" + mime_type + "',empty_blob(),'" + pie + "'," + tipo_doc +","+ tipoid +")");
                    
                    //Se posiciona en esa fila
                    PreparedStatement p_stmtOracle = oConnectionDocumentation.prepareStatement("select contenido from otradoc where numero=" + numero + " for update");
                    rsOracle = p_stmtOracle.executeQuery();
                    if (rsOracle.next())
                    {
                        
                        Blob informeBlob = rsOracle.getBlob(1);
                        OutputStream blobOutputStream = ((oracle.sql.BLOB)informeBlob).getBinaryOutputStream();
                        InputStream stream = new FileInputStream(rutaFichero);
                        byte[] buffer2 = new byte[10*1024];
                        int num = 0;
                        while((num = stream.read(buffer2)) != -1) {
                            blobOutputStream.write(buffer2,0,num);
                        }
                        blobOutputStream.flush();
                        stream.close();
                        blobOutputStream.close();
                        rsOracle.close();
                        
                        stmtOracle.execute("insert into idenotra (numero, usuario) values (" + numero + ", 'UCI')");   
                        oConnectionDocumentation.commit();
                        System.out.println("O.K. Insertado fichero: "+fichero.getName());
                    }
            }
            else System.out.println("Error. No se encuentra el fichero: "+fichero.getName());
        }
        catch (Exception e)
        {
            try
            {
                oConnectionDocumentation.rollback();
            }
            catch (Exception ee)
            {
                
            }
            System.out.println("Excepción: "+e.toString());
        }
        
    }
    
}//class cargaPretasaExcel
