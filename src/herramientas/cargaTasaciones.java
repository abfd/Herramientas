/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package herramientas;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author Ana Belen de Frutos
 */
public class cargaTasaciones 
{
    private java.io.File fPropiedades;      
    private Logger logger = Logger.getLogger(cargaTasaciones.class);
    
    
    java.sql.ResultSet rs = null;
    java.sql.ResultSet rs2 = null;
    
    private int cliente = 930;
    private String oficina = null;
    
    private int finicio = 3;
    private int ffin = 120;     
    private String hoja = "Hoja1";
    private Utilidades.Excel oExcel = null;
    private org.apache.poi.hssf.usermodel.HSSFCell celda = null;
    
    //tablas de insercion
    private Objetos.v2.Solicitudes oSolicitudes = null;
    private Objetos.Refer oRefer = null;
    private Objetos.Entrada oEntrada = null;
    private Objetos.Clidir oClidir = null;
    private Objetos.Avisos oAvisos = null;
    private Objetos.Documenta oDocumenta = null;
    private Objetos.Datosreg oDatosreg = null;
    private Objetos.Catastro oCatastro = null;
    private Objetos.Notas_simples oNotasSimples = null;
    private Objetos.unidades.Suelo oSuelo = null;
    private Objetos.unidades.Edificios oEdificios = null;
    private Objetos.unidades.Elementos oElementos = null;
    private Objetos.unidades.Relafincas oRelafincas = null;
    private Utilidades.Resultado oResultado = null;
    
    private boolean decimal = false;
    
    public static void main(String[] args) 
    {
        cargaTasaciones carga = new cargaTasaciones();
        carga = null;
        System.gc();
    }
    
    public cargaTasaciones()
    {//carga
        //cargaTasaciones930();
        //cargaTasaciones952();
        //cargaTasaciones7AQR();
        //ActualizaRefclienteTasaciones7AQR();
        //cargaTasacionesSAREB();
        //solicitarNotaSimple();
        //cargaJuicioCritico();
        //cargaDeyDe();
        //cargaDeyDeMer();
        //cargaDeyDeMerLost();
        //cargaDeyDeSol();
        cargaOficinas();
    }
    
    
    private void cargaTasaciones930()
    {//carga desde excel asignando cliente 930 y la oficina en funcion del campo llaves:
        // si llaves = Si => oficina = CVIS
        // si llaves = No => oficina = SVIS
        
        java.sql.Connection conexion = null;
        String sUrlExcel = "/data/informes/cargatasaciones/930/carga930.xls";        
        //FICHERO LOG4J
        PropertyConfigurator.configure("/data/informes/cargatasaciones/930/" + "Log4j.properties");   
        
        try
        {
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@orabackup:1521:rvtn");
            conexion.setAutoCommit(false);
               //nombre de la hora
            
            
            String valor = null;
            Double iValor = 0.0;
              
            String referencia = null;
            String contacto = null;
            String provincia = null;
            String expediente = null; 
            String sConsulta = null;
            String prefijo = null;
            String delegacion = null;
            int iNumeroRegistrosAfectados = 0;
            String tefno1 = null;
            String tefno2 = null;
            boolean seguir = true;
            
            oExcel = new Utilidades.Excel(sUrlExcel);  
            //for para la actualizacion de los datos de contacto del excel sobre solicitudes
            for (int fila = finicio; fila < ffin;fila ++)              
            {
                //referencia
                referencia = null;
                referencia = getCellValue(fila,1);
                contacto = null;
                contacto = getCellValue(fila,14);
                
                if (referencia != null && contacto != null && !contacto.equals(""))
                {
                    oRefer = new Objetos.Refer();
                    if (oRefer.loadValuesFromReferencia(Utilidades.Cadenas.TrimTotal(referencia), Integer.toString(cliente), conexion) == 1)
                    {
                         oSolicitudes = new Objetos.v2.Solicitudes();
                         if (oSolicitudes.load(oRefer.numexp, conexion) == 1)
                         {
                             if (oSolicitudes.contacto == null || oSolicitudes.contacto.equals("")) 
                             {                                 
                                 oSolicitudes.contacto = Utilidades.Cadenas.TrimTotal(contacto);
                                 if (oSolicitudes.contacto.length() > 30) oSolicitudes.contacto = oSolicitudes.contacto.substring(0, 30);
                                 oSolicitudes.setConnection(conexion);
                                 if (oSolicitudes.update() == 1)
                                 {
                                     conexion.commit();
                                     logger.info("Fila: "+Integer.toString(fila+1)+".Expediente: "+oRefer.numexp.trim() +"contacto actualizado");                    
                                 }
                                 else
                                 {
                                     conexion.commit();
                                     logger.error("Fila: "+Integer.toString(fila+1)+".Expediente: "+oRefer.numexp.trim() +"contacto NO actualizado");                    
                                 }
                             }
                             else logger.info("Fila: "+Integer.toString(fila+1)+".Expediente: "+oRefer.numexp.trim() +"contacto YA asignado");                    
                             
                         }
                         else logger.error("Fila: "+Integer.toString(fila+1)+".Expediente: "+oRefer.numexp.trim() +"No localizada en solicitudes");                    
                    }
                    else logger.error("Fila: "+Integer.toString(fila+1)+".Referencia: "+referencia.trim() +"No localizada en refer");                    
                }                
                logger.error("Fila: "+Integer.toString(fila+1)+".Referencia  o contacto nulos. Imposible actualizar contacto");                    
            }
            
            /*for para la carga inicial de los datos del excel en solicitudes
             * 
             
            for (int fila = finicio; fila < ffin;fila ++)              
            {
                valor = null;
                iValor = 0.0;
                oficina = null;
                referencia = null;
                provincia = null;
                expediente = null;
                sConsulta = null;
                prefijo = null;
                delegacion = null;
                iNumeroRegistrosAfectados = 0;
                seguir = true;
                oSolicitudes = null;
                oRefer = null;
                oEntrada = null;
                oClidir = null;
                tefno1 = null;
                tefno2 = null;
                
                //leemos valores del excel
                
                //referencia
                referencia = getCellValue(fila,1);
               
                //provincia
                provincia = getCellValue(fila,11);                
                
                if (referencia != null && provincia != null)
                {//numeramos la tasacion
                    sConsulta = "SELECT prefijo,coddel FROM provincias WHERE nompro = '"+provincia.trim()+"'";    
                    rs = Utilidades.Conexion.select(sConsulta,conexion);                                    
                    if (rs.next())
                    {
                        delegacion = rs.getString("coddel");  
                        prefijo = rs.getString("prefijo");  
                                        
                        rs.close();
                        rs = null; 
                    
                        expediente = prefijo.trim()+"30."+referencia.trim()+"-12";
                        //comprobamos que no exista ya el expediente
                        sConsulta = "SELECT numexp FROM solicitudes WHERE numexp = '"+expediente.trim()+"'";
                        rs = Utilidades.Conexion.select(sConsulta,conexion);                                    
                        if (rs.next())
                        {
                            expediente = null;
                            logger.error("Fila: "+Integer.toString(fila+1)+".Referencia repetida en el expediente: "+expediente);                    
                        }
                        else
                        {
                            sConsulta = "SELECT numexp FROM his_solicitudes WHERE numexp = '"+expediente.trim()+"'";
                            rs = Utilidades.Conexion.select(sConsulta,conexion);                                    
                            if (rs.next())
                            {
                                expediente = null;
                                logger.error("Fila: "+Integer.toString(fila+1)+".Referencia repetida en el expediente(historico): "+expediente);                    
                            }
                        }
                        rs.close();
                        rs = null;    
                    }
                    else
                    {
                        logger.error("Fila: "+Integer.toString(fila+1)+"Imposible numerar.Referencia: "+referencia+" .Provincia no Existe: "+provincia);                    
                    }
                }
                else
                {
                    expediente = null;
                    logger.error("Fila: "+Integer.toString(fila+1)+"Imposible numerar.Referencia: "+referencia+" .Provincia: "+provincia);                    
                    
                }
                
                if (expediente != null)
                {
                    // - SOLICITUDES
                    oSolicitudes = new Objetos.v2.Solicitudes();
                    oSolicitudes.numexp = expediente;
                    oSolicitudes.codcli = cliente;
                    oSolicitudes.tipoinm = "XXX";
                    if (getCellValue(fila,13) != null)
                    {
                        if (getCellValue(fila,13).toUpperCase().equals("SI")) oSolicitudes.oficina = "CVIS";
                        else oSolicitudes.oficina = "SVIS";
                    }                    
                    
                    oClidir = new Objetos.Clidir();
                    if (oClidir.load(cliente, oSolicitudes.oficina, conexion))
                    {                                                
                        oSolicitudes.denomina = oClidir.denomina;
                        oSolicitudes.dirofi = oClidir.direcc;
                        oSolicitudes.postal = oClidir.codpos;
                        oSolicitudes.poblacion = oClidir.pobla;
                        oSolicitudes.provclient = oClidir.provin;
                        oSolicitudes.telefo = oClidir.telefo;
                        oSolicitudes.fax = oClidir.fax;
                        oSolicitudes.encarg = oClidir.percon;                        
                    }
                    
                    oSolicitudes.calle = getCellValue(fila,6);
                    if (oSolicitudes.calle != null && oSolicitudes.calle.length() > 60) oSolicitudes.calle = oSolicitudes.calle.substring(0,60);
                    oSolicitudes.numero = getCellValue (fila,9);
                    oSolicitudes.planta = getCellValue (fila,7);
                    oSolicitudes.puerta = getCellValue (fila,8);
                    oSolicitudes.locali = getCellValue (fila,10);
                    oSolicitudes.munici = getCellValue (fila,10);                  
                    oSolicitudes.codpos = Integer.parseInt(getCellValue(fila,12));
                    oSolicitudes.delegado = Integer.parseInt(delegacion);                                    
                    oSolicitudes.provin = provincia;
                    oSolicitudes.codage = null;
                    oSolicitudes.fchenc = new java.util.Date();
                    oSolicitudes.codest = new Integer(0);    
                    tefno1 = getCellValue (fila,15);
                    if (tefno1 == null) tefno1 = "";                    
                    tefno2 = getCellValue (fila,16);                    
                    oSolicitudes.telefonos = tefno1.trim();
                    if (tefno2 != null && !tefno2.equals("")) oSolicitudes.telefonos += "/"+tefno2.trim();
                    

                    iNumeroRegistrosAfectados = oSolicitudes.insertSolicitudes(conexion);
                    if (iNumeroRegistrosAfectados == 1) seguir = true;
                    else
                    {
                        seguir = false;
                        logger.error("Fila: "+Integer.toString(fila+1)+"Imposible insertar en SOLICITUDES expediente: "+expediente);                    
                    }
                    if (seguir)    
                    {//                                        // - REFER
                        oRefer = new Objetos.Refer();
                        oRefer.numexp = oSolicitudes.numexp;
                        oRefer.referencia = referencia;                                        
                        iNumeroRegistrosAfectados = oRefer.insertarRefer(conexion);
                        if (iNumeroRegistrosAfectados == 1) seguir = true;
                        else
                        {
                            seguir = false;
                            logger.error("Fila: "+Integer.toString(fila+1)+"Imposible insertar en REFER expediente: "+expediente);                    
                        }
                        
                    }
                    if (seguir)
                    {// - ENTRADA
                          oEntrada = new Objetos.Entrada();
                          oEntrada.numexp = oSolicitudes.numexp;
                          if(oSolicitudes.delegado!=null) oEntrada.coddel = oSolicitudes.delegado.toString();
                          iNumeroRegistrosAfectados = oEntrada.insertarEntrada(conexion);
                          if (iNumeroRegistrosAfectados == 1) seguir = true;
                          else
                          {
                              seguir = false;
                              logger.error("Fila: "+Integer.toString(fila+1)+"Imposible insertar en ENTRADA expediente: "+expediente);                    
                          }
                    }
                    if (seguir)
                    {
                        iNumeroRegistrosAfectados = Utilidades.Valtecnic.insertProdusu(oSolicitudes.numexp,"0","juan","A","1","RE", conexion);
                        if (iNumeroRegistrosAfectados == 1) seguir = true;
                        else
                        {
                              seguir = false;
                              logger.error("Fila: "+Integer.toString(fila+1)+"Imposible insertar en PRODUSU expediente: "+expediente);                    
                        }
                    }
                    if (seguir) 
                    {
                        conexion.commit();
                        logger.info("Fila: "+Integer.toString(fila+1)+"Insertado expediente: "+expediente);                    
                    }
                    else
                    {
                        conexion.rollback();
                    }
                    
                    
                    
                }//if expediente != null
                
            }//for lectura excel
             * for carga inicial del excel en solicitudes
            */
        }
        catch (Exception e)
        {
            logger.error("Error general en la carga de tasaciones .Descripción: "+e.toString());
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
        
    }//    private void cargaTasaciones930()
    
    
    private void cargaTasaciones952()
    {//carga desde excel asignando cliente 930 y la oficina en funcion del campo llaves:
        // si llaves = Si => oficina = CVIS
        // si llaves = No => oficina = SVIS
        
        java.sql.Connection conexion = null;
        //String sUrlExcel = "/data/informes/cargatasaciones/952/carga952.xls";        
        //String sUrlExcel = "/data/informes/cargatasaciones/952/carga952-2.xls";        
        //String sUrlExcel = "/data/informes/cargatasaciones/REVA-14022014/encargosdeutsche.xls";        
        //String sUrlExcel = "/data/informes/cargatasaciones/952/REVA/11082015/cargalote7520.xls";        
        //String sUrlExcel = "/data/informes/cargatasaciones/952/REVA/08092015/lote_062015_reva.xls";        
        //String sUrlExcel = "/data/informes/cargatasaciones/952/REVA/09092015/lote_062015_reva.xls";        
        //String sUrlExcel = "/data/informes/cargatasaciones/952/REVA/10092015/lote_062015_reva.xls";        
        //String sUrlExcel = "/data/informes/cargatasaciones/952/REVA/29092015/lote_072015_reva.xls";        
        String sUrlExcel = "/data/informes/cargatasaciones/952/REVA/28122015/lote7524.xls";        
        
        //FICHERO LOG4J
        PropertyConfigurator.configure("/data/informes/cargatasaciones/952/" + "Log4j.properties");   
        
        try
        {
            //conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:desarrollo/des0329@oraserver03:1521:rvtn3");
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@oraserver02:1521:rvtnprod");
            conexion.setAutoCommit(false);
               //nombre de la hora
            
            cliente = 952;
            String valor = null;
            Double iValor = 0.0;
              
            String referencia = null;
            String tipoinm = null;
            String txtAviso = null;
            String direccion = null;
            String numero = null;
            String planta = null;
            String puerta = null;
            String superficie = null;
            String localidad = null;
            String provincia = null;
            String codpos = null;
            String registro = null;
            String nregistro = null;
            String tomo = null;
            String libro = null;
            String folio = null;
            String finca = null;
            String prestamo = null;
            String expediente = null; 
            String sConsulta = null;
            String prefijo = null;
            String delegacion = null;
            String refcatastral = null;
            int iNumeroRegistrosAfectados = 0;            
            boolean seguir = true;            
            
            
            oExcel = new Utilidades.Excel(sUrlExcel);              
            hoja = "Hoja1";
            for (int fila = 1; fila < 81;fila ++)              
            {                                
                seguir = true;
                //leemos valores del excel                                
                referencia = null;
                tipoinm = null;
                txtAviso = null;
                direccion = null;
                numero = null;
                planta = null;
                puerta = null;
                superficie = null;
                localidad = null;
                provincia = null;
                codpos = null;
                registro = null;
                nregistro = null;
                tomo = null;
                libro = null;
                folio = null;
                finca = null;
                prestamo = null;
                expediente = null; 
                sConsulta = null;
                prefijo = null;
                delegacion = null;
                refcatastral = null;
                
                //referencia
                decimal = false;
                referencia = getCellValue(fila,0);
                tipoinm = getCellValue(fila,1);
                txtAviso = getCellValue(fila,2);
                direccion = getCellValue(fila,3);
                numero = getCellValue(fila,4);
                planta = getCellValue(fila,5);
                puerta = getCellValue(fila,6);
                //superficie = getCellValue(fila,3);
                localidad = getCellValue(fila,7);
                //provincia = getCellValue(fila,5);
                codpos = getCellValue(fila,8);
                registro = getCellValue(fila,9);
                nregistro = getCellValue(fila,10);
                tomo = getCellValue(fila,11);                
                libro = getCellValue(fila,12);
                folio = getCellValue(fila,13);
                finca = getCellValue(fila,14);
                
                refcatastral = getCellValue(fila,15);
                //decimal = true;
                //prestamo = getCellValue(fila,14);                
                delegacion = null;
                //provincia
                
                
                if (referencia != null)
                {//numeramos la tasacion
                    //sConsulta = "SELECT prefijo,coddel FROM provincias WHERE nompro = '"+provincia.trim().toUpperCase()+"'";    
                    sConsulta = "SELECT prefijo,coddel,nompro FROM provincias WHERE codpro = "+Utilidades.Cadenas.completTextWithLeftCharacter(codpos, '0', 5).substring(0, 2);
                    rs = Utilidades.Conexion.select(sConsulta,conexion);                                    
                    if (rs.next())
                    {
                        delegacion = rs.getString("coddel");  
                        prefijo = rs.getString("prefijo");  
                        provincia = rs.getString("nompro");
                                        
                        rs.close();
                        rs = null; 
                    
                        expediente = prefijo.trim()+"52."+referencia.trim()+"-15";
                        //comprobamos que no exista ya el expediente
                        sConsulta = "SELECT numexp FROM solicitudes WHERE numexp = '"+expediente.trim()+"'";
                        rs = Utilidades.Conexion.select(sConsulta,conexion);                                    
                        if (rs.next())
                        {
                            expediente = null;
                            logger.error("Fila: "+Integer.toString(fila+1)+".Referencia repetida en el expediente: "+rs.getString("numexp"));                               
                        }
                        else
                        {
                            sConsulta = "SELECT numexp FROM his_solicitudes WHERE numexp = '"+expediente.trim()+"'";
                            rs = Utilidades.Conexion.select(sConsulta,conexion);                                    
                            if (rs.next())
                            {
                                expediente = null;
                                logger.error("Fila: "+Integer.toString(fila+1)+".Referencia repetida en el expediente(historico): "+rs.getString("numexp"));                    
                            }
                        }
                        rs.close();
                        rs = null;    
                    }
                    else
                    {
                        logger.error("Fila: "+Integer.toString(fila+1)+"Imposible numerar.Referencia: "+referencia+" .Provincia no Existe: "+provincia);                    
                    }
                }
                else
                {
                    expediente = null;
                    logger.error("Fila: "+Integer.toString(fila+1)+"Imposible numerar.Referencia: "+referencia+" .Provincia: "+provincia);                    
                    
                }
                iNumeroRegistrosAfectados = 0;
                if (expediente != null)
                {
                    // - SOLICITUDES
                    oSolicitudes = new Objetos.v2.Solicitudes();
                    oSolicitudes.numexp = expediente;
                    oSolicitudes.codcli = cliente;
                    oSolicitudes.oficina = "REVA";
                    oSolicitudes.tipoif = 8;
                    oSolicitudes.nifsolici = "A08000614";
                    oSolicitudes.solici = "DEUTSCHE BANK, SAE";
                    if (tipoinm != null) oSolicitudes.tipoinm = tipoinm.trim().toUpperCase();
                    else oSolicitudes.tipoinm = "XXX";                                     
                    
                    oClidir = new Objetos.Clidir();
                    if (oClidir.load(cliente, oSolicitudes.oficina, conexion))
                    {                                                
                        oSolicitudes.denomina = oClidir.denomina;
                        oSolicitudes.dirofi = oClidir.direcc;
                        oSolicitudes.postal = oClidir.codpos;
                        oSolicitudes.poblacion = oClidir.pobla;
                        oSolicitudes.provclient = oClidir.provin;
                        oSolicitudes.telefo = oClidir.telefo;
                        oSolicitudes.fax = oClidir.fax;
                        oSolicitudes.encarg = oClidir.percon;                        
                    }
                    
                    if (direccion != null) oSolicitudes.calle = direccion.trim().toUpperCase();
                    else oSolicitudes.calle = "";
                    if (oSolicitudes.calle != null && oSolicitudes.calle.length() > 60) oSolicitudes.calle = oSolicitudes.calle.substring(0,60);
                    oSolicitudes.numero = numero;
                    oSolicitudes.planta = planta;
                    oSolicitudes.puerta = puerta;
                    if (localidad != null && !localidad.trim().equals(""))
                    {
                        oSolicitudes.locali = localidad.trim().toUpperCase();
                        oSolicitudes.munici = oSolicitudes.locali;           
                    }
                    if (codpos != null) oSolicitudes.codpos = Integer.parseInt(codpos);
                    oSolicitudes.delegado = Integer.parseInt(delegacion);                                    
                    oSolicitudes.provin = provincia.toUpperCase();
                    oSolicitudes.codage = null;
                    oSolicitudes.fchenc = new java.util.Date();
                    oSolicitudes.codest = new Integer(0);   
                    oSolicitudes.contacto = "In situ";
                    oSolicitudes.indicador = 1; //insertamos DOCUMENTA
                    //oSolicitudes.telefonos = tefno1.trim();
                    
                    iNumeroRegistrosAfectados = oSolicitudes.insertSolicitudes(conexion);
                    if (iNumeroRegistrosAfectados == 1) seguir = true;
                    else
                    {
                        seguir = false;
                        logger.error("Fila: "+Integer.toString(fila+1)+"Imposible insertar en SOLICITUDES expediente: "+expediente);                    
                    }
                    if (seguir)    
                    {//                                        // - REFER
                        oRefer = new Objetos.Refer();
                        oRefer.numexp = oSolicitudes.numexp;
                        oRefer.referencia = referencia;      
                        oRefer.objeto = referencia;
                        oRefer.lote = "7524";
                        if (prestamo != null) oRefer.prtmo = prestamo.replace(',', '.');
                        iNumeroRegistrosAfectados = oRefer.insertarRefer(conexion);
                        if (iNumeroRegistrosAfectados == 1) seguir = true;
                        else
                        {
                            seguir = false;
                            logger.error("Fila: "+Integer.toString(fila+1)+"Imposible insertar en REFER expediente: "+expediente);                    
                        }
                        
                    }
                    if (seguir)
                    {// - ENTRADA
                          oEntrada = new Objetos.Entrada();
                          oEntrada.numexp = oSolicitudes.numexp;
                          if(oSolicitudes.delegado!=null) oEntrada.coddel = oSolicitudes.delegado.toString();
                          iNumeroRegistrosAfectados = oEntrada.insert(conexion);
                          if (iNumeroRegistrosAfectados == 1) seguir = true;
                          else
                          {
                              seguir = false;
                              logger.error("Fila: "+Integer.toString(fila+1)+"Imposible insertar en ENTRADA expediente: "+expediente);                    
                          }
                    }
                    
                    if (seguir)
                    {// - AVISOS
                        oAvisos = new Objetos.Avisos();
                        oAvisos.numexp = oSolicitudes.numexp;
                        //oAvisos.aviso1 = "Seguir circular 0020/2013. Tipo de inmueble encargado: "+txtAviso;
                        oAvisos.aviso1 = "Tipo de inmueble encargado: "+txtAviso;
                        iNumeroRegistrosAfectados = oAvisos.insert(conexion);
                        if (iNumeroRegistrosAfectados == 1) seguir = true;
                        else
                        {
                              seguir = false;
                              logger.error("Fila: "+Integer.toString(fila+1)+"Imposible insertar en AVISOS expediente: "+expediente);                    
                        }
                        
                    }
                    
                    if (seguir && finca != null && !finca.trim().equals(""))
                    {//DATOS REGISTRALES
                        oDatosreg = new Objetos.Datosreg();
                        oDatosreg.numexp = oSolicitudes.numexp;
                        oDatosreg.numero = 1;
                        oDatosreg.tipoinm = oSolicitudes.tipoinm;
                        if (registro != null && !registro.trim().equals(""))oDatosreg.registro = registro.trim().toUpperCase();
                        if (nregistro != null && !nregistro.trim().equals("")) oDatosreg.registro += " Nº "+nregistro.trim().toUpperCase();
                        if (tomo != null && !tomo.trim().equals("")) oDatosreg.tomo = tomo.trim().toUpperCase(); 
                        if (libro != null && !libro.trim().equals("")) oDatosreg.libro = libro.trim().toUpperCase(); 
                        if (folio != null && !folio.trim().equals("")) oDatosreg.folio = folio.trim().toUpperCase(); 
                        if (finca != null && !finca.trim().equals("")) oDatosreg.finca = finca.trim().toUpperCase(); ;
                        iNumeroRegistrosAfectados = oDatosreg.insert(conexion);
                        if (iNumeroRegistrosAfectados == 1) seguir = true;
                        else
                        {
                              seguir = false;
                              logger.error("Fila: "+Integer.toString(fila+1)+"Imposible insertar en DATOSREG expediente: "+expediente);                    
                        }
                        if (seguir)
                        {//CATASTRO
                            oCatastro = new Objetos.Catastro();
                            oCatastro.numexp = oSolicitudes.numexp;
                            oCatastro.numero = 1;    
                            oCatastro.tipoinm = oSolicitudes.tipoinm;
                            if (refcatastral != null && !refcatastral.equalsIgnoreCase("")) oCatastro.fcatastral = refcatastral;
                            iNumeroRegistrosAfectados = oCatastro.insert(conexion);
                            if (iNumeroRegistrosAfectados == 1) seguir = true;
                            else
                            {
                              seguir = false;
                              logger.error("Fila: "+Integer.toString(fila+1)+"Imposible insertar en CATASTRO expediente: "+expediente);                    
                            }
                        }                       
                    }
                    else
                    {
                        if (seguir) logger.info("Tasacion :"+oSolicitudes.numexp+ " sin FINCA.");
                    }
                    
                    if (seguir && finca != null && !finca.trim().equals(""))
                    {
                        sConsulta = "INSERT INTO notas_simples VALUES ('"+oSolicitudes.numexp+"','"+oDatosreg.finca+"','juan','26-11-2015 16:45:00',null,'1')";
                        if (Utilidades.Conexion.insert(sConsulta, conexion) == 1)
                        {
                            seguir = true;
                        }
                        else 
                        {
                            seguir = false;
                            logger.error("Fila: "+Integer.toString(fila+1)+"Imposible insertar en NOTAS_SIMPLES expediente: "+expediente);  
                        }
                    }                                            
                    if (seguir)
                    {//DATOS DOCUMENTA
                        oDocumenta = new Objetos.Documenta();
                        oDocumenta.numexp = oSolicitudes.numexp;
                        oDocumenta.tipoinm = oSolicitudes.tipoinm;        
                        oDocumenta.finalidad = "0";
                        iNumeroRegistrosAfectados = oDocumenta.insertDocumenta(conexion);
                        if (iNumeroRegistrosAfectados == 1) seguir = true;
                        else
                        {
                              seguir = false;
                              logger.error("Fila: "+Integer.toString(fila+1)+"Imposible insertar en DOCUMENTA expediente: "+expediente);                    
                        }
                    }
                    if (seguir)
                    {
                        iNumeroRegistrosAfectados = Utilidades.Valtecnic.insertProdusu(oSolicitudes.numexp,"0","juan","A","1","RE", conexion);
                        if (iNumeroRegistrosAfectados == 1) seguir = true;
                        else
                        {
                              seguir = false;
                              logger.error("Fila: "+Integer.toString(fila+1)+"Imposible insertar en PRODUSU expediente: "+expediente);                    
                        }
                    }
                    if (seguir) 
                    {
                        conexion.commit();
                        logger.info("Fila: "+Integer.toString(fila+1)+" Insertado expediente: "+expediente);                    
                    }
                    else
                    {
                        conexion.rollback();
                        logger.error("Fila: "+Integer.toString(fila+1)+" No Insertada referencia: "+referencia);   
                    }
                    
                    
                    
                }//if expediente != null
                oSolicitudes = null;
                oRefer = null;
                oEntrada = null;                    
                oAvisos = null;
                oDocumenta = null;
                oDatosreg = null;
                oCatastro = null;
                oNotasSimples = null;
            }//for lectura excel
            conexion.close();
        }
        catch (Exception e)
        {
            logger.error("Error general en la carga de tasaciones .Descripción: "+e.toString());
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
                logger.error("Imposible cerrar conexión con la B.D"+sqlException.toString());
            }             
        }//finally     
        
    }//    private void cargaTasaciones930()
    
    private void cargaTasaciones7AQR()
    {//carga desde excel asignando cliente 930 y la oficina en funcion del campo llaves:
        // si llaves = Si => oficina = CVIS
        // si llaves = No => oficina = SVIS
        
        java.sql.Connection conexion = null;
        //String sUrlExcel = "/data/informes/cargatasaciones/952/carga952.xls";        
        //String sUrlExcel = "/data/informes/cargatasaciones/952/carga952-2.xls";        
        String sUrlExcel = "/data/informes/cargatasaciones/7AQR/CAIXABANK 7AQR IV PARA CARGAR ANA_mio.xls";        
        
        //FICHERO LOG4J
        PropertyConfigurator.configure("/data/informes/cargatasaciones/7AQR/" + "Log4j.properties");   
        
        try
        {
            //conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:desarrollo/des0329@oraserver00:1521:rvtn");
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@oraserver01:1521:rvtn1");
            conexion.setAutoCommit(false);
               //nombre de la hora
            
            cliente = 455;
            String valor = null;
            Double iValor = 0.0;
              
            String objeto = null;
            String referencia = null;
            String letra = null;
            String tipoinm = null;
            String txtAviso = null;
            String direccion = null;
            String direccion1 = null;
            String direccion2 = null;
            String numero = null;
            String planta = null;
            String puerta = null;
            String superficie = null;
            String localidad = null;
            String provincia = null;
            String codpos = null;
            String registro = null;
            String nregistro = null;
            String tomo = null;
            String libro = null;
            String folio = null;
            String finca = null;
            String prestamo = null;
            String expediente = null; 
            String sConsulta = null;
            String prefijo = null;
            String delegacion = null;
            int iNumeroRegistrosAfectados = 0;            
            boolean seguir = true;            
            String fcatastral = null;
            boolean existe = false;
            boolean dirEnAvisos = false;
            String titular = "";
            String seccion = "";
            boolean estadoOK = true;
            String lote = "";
            
            oExcel = new Utilidades.Excel(sUrlExcel);  
            
            hoja = "Hoja1";
            lote = "7004";
            for (int fila = 1; fila < 50;fila ++)              
            {                                
                //leemos valores del excel  
                objeto = null;
                referencia = null;
                letra = null;
                tipoinm = null;
                txtAviso = null;
                direccion = null;
                direccion1 = null;
                direccion2 = null;
                numero = null;
                planta = null;
                puerta = null;
                superficie = null;
                localidad = null;
                provincia = null;
                codpos = null;
                registro = null;
                nregistro = null;
                tomo = null;
                libro = null;
                folio = null;
                finca = null;
                prestamo = null;
                expediente = null; 
                sConsulta = null;
                prefijo = null;
                delegacion = null;
                fcatastral = null;
                existe = false;
                dirEnAvisos = false;
                titular = "";
                seccion = null;
                estadoOK = true;
                
                //referencia
                decimal = false;
                objeto = getCellValue(fila,0);
                referencia = getCellValue(fila,1);
                letra = getCellValue(fila,2);
                if (letra != null && !letra.trim().equals("")) referencia = referencia.trim()+" "+letra;
                tipoinm = "XXX";
                titular = getCellValue(fila,14);
                txtAviso = "Circulares 0007/2014 y 0008/2014";
                if (titular != null && !titular.trim().equals("")) txtAviso += ". "+titular.trim();
                direccion1 = getCellValue(fila,7);
                //direccion2 = getCellValue(fila,3);
                //numero = getCellValue(fila,4);
                //planta = getCellValue(fila,5);
                //puerta = getCellValue(fila,6);
                //superficie = getCellValue(fila,3);
                localidad = getCellValue(fila,8);
                //provincia = getCellValue(fila,5);
                codpos = getCellValue(fila,10);
                registro = getCellValue(fila,5);
                //nregistro = getCellValue(fila,10);
                //tomo = getCellValue(fila,11);
                //libro = getCellValue(fila,10);
                //folio = getCellValue(fila,12);
                finca = getCellValue(fila,3);
                finca = quitaCerosFinca(finca);
                fcatastral = getCellValue(fila,6);
                //decimal = true;
                //prestamo = getCellValue(fila,14);                
                delegacion = null;
                //provincia
                seccion = getCellValue(fila,4);
                
                
                if (referencia != null)
                {//numeramos la tasacion
                    //sConsulta = "SELECT prefijo,coddel FROM provincias WHERE nompro = '"+provincia.trim().toUpperCase()+"'";    
                    sConsulta = "SELECT prefijo,coddel,nompro FROM provincias WHERE codpro = "+Utilidades.Cadenas.completTextWithLeftCharacter(codpos, '0', 5).substring(0, 2);
                    rs = Utilidades.Conexion.select(sConsulta,conexion);                                    
                    if (rs.next())
                    {
                        delegacion = rs.getString("coddel");  
                        prefijo = rs.getString("prefijo");  
                        provincia = rs.getString("nompro");
                                        
                        rs.close();
                        rs = null; 
                    
                        expediente = prefijo.trim()+"55."+referencia.trim()+"-14";
                        //comprobamos que no exista ya el expediente
                        sConsulta = "SELECT s.numexp FROM solicitudes s join refer r ON s.numexp = r.numexp WHERE r.referencia = '"+referencia.trim()+"' AND s.codcli = 455 AND (s.oficina = '1AQR' OR s.oficina = '7AQR')";
                        rs = Utilidades.Conexion.select(sConsulta,conexion);                                    
                        if (rs.next())
                        {
                            expediente = rs.getString("numexp");
                            existe = true;
                            logger.error("Fila: "+Integer.toString(fila+1)+".Referencia repetida en el expediente: "+rs.getString("numexp"));                               
                        }
                        else
                        {
                            sConsulta = "SELECT s.numexp FROM his_solicitudes s join refer r ON s.numexp = r.numexp WHERE r.referencia = '"+referencia.trim()+"' AND s.codcli = 455 AND (s.oficina = '1AQR' OR s.oficina = '7AQR')";
                            rs = Utilidades.Conexion.select(sConsulta,conexion);                                    
                            if (rs.next())
                            {
                                existe = true;
                                expediente = rs.getString("numexp");
                                logger.error("Fila: "+Integer.toString(fila+1)+".Referencia repetida en el expediente(historico): "+rs.getString("numexp"));                    
                            }
                        }
                        rs.close();
                        rs = null;    
                    }
                    else
                    {
                        logger.error("Fila: "+Integer.toString(fila+1)+"Imposible numerar.Referencia: "+referencia+" .Provincia no Existe: "+provincia);                    
                        estadoOK = false;
                    }
                }
                else
                {
                    expediente = null;
                    logger.error("Fila: "+Integer.toString(fila+1)+"Imposible numerar.Referencia: "+referencia+" .Provincia: "+provincia);  
                    estadoOK = false;
                    
                }
                iNumeroRegistrosAfectados = 0;
                
                if (estadoOK)
                {
                    if (!existe)
                    {
                        // - SOLICITUDES
                        oSolicitudes = new Objetos.v2.Solicitudes();
                        oSolicitudes.numexp = expediente;
                        oSolicitudes.codcli = cliente;
                        oSolicitudes.oficina = "1AQR";
                        oSolicitudes.tipoif = 8;
                        oSolicitudes.nifsolici = "A08663619";
                        oSolicitudes.solici = referencia;                   
                        oSolicitudes.tipoinm = "XXX";                                     

                        oClidir = new Objetos.Clidir();
                        if (oClidir.load(cliente, oSolicitudes.oficina, conexion))
                        {                                                
                            oSolicitudes.denomina = oClidir.denomina;
                            oSolicitudes.dirofi = oClidir.direcc;
                            oSolicitudes.postal = oClidir.codpos;
                            oSolicitudes.poblacion = oClidir.pobla;
                            oSolicitudes.provclient = oClidir.provin;
                            oSolicitudes.telefo = oClidir.telefo;
                            oSolicitudes.fax = oClidir.fax;
                            oSolicitudes.encarg = oClidir.percon;                        
                        }
                        direccion = direccion1.trim().toUpperCase();
                        if (direccion2 != null) direccion += "."+ direccion2.trim().toUpperCase();                        
                        if (direccion != null && direccion.length() > 60) 
                        {
                            oSolicitudes.calle = direccion.substring(0,60).toUpperCase();
                            dirEnAvisos = true;
                        }
                        else oSolicitudes.calle = direccion.toUpperCase();
                        //oSolicitudes.numero = numero;
                        //oSolicitudes.planta = planta;
                        //oSolicitudes.puerta = puerta;
                        if (localidad != null && !localidad.trim().equals(""))
                        {
                            oSolicitudes.locali = localidad.trim().toUpperCase();
                            oSolicitudes.munici = oSolicitudes.locali;           
                        }
                        if (codpos != null) oSolicitudes.codpos = Integer.parseInt(codpos);
                        oSolicitudes.delegado = Integer.parseInt(delegacion);                                    
                        oSolicitudes.provin = provincia.toUpperCase();
                        oSolicitudes.codage = null;
                        oSolicitudes.fchenc = new java.util.Date();
                        oSolicitudes.codest = new Integer(0);   
                        //oSolicitudes.contacto = "In situ";
                        oSolicitudes.indicador = 1; //insertamos DOCUMENTA
                        //oSolicitudes.telefonos = tefno1.trim();

                        iNumeroRegistrosAfectados = oSolicitudes.insertSolicitudes(conexion);
                        if (iNumeroRegistrosAfectados == 1) seguir = true;
                        else
                        {
                            seguir = false;
                            logger.error("Fila: "+Integer.toString(fila+1)+"Imposible insertar en SOLICITUDES expediente: "+expediente);                    
                        }
                        if (seguir)    
                        {//                                        // - REFER
                            oRefer = new Objetos.Refer();
                            oRefer.numexp = oSolicitudes.numexp;
                            oRefer.referencia = referencia;      
                            oRefer.api = lote;
                            //oRefer.objeto = objeto;                                                
                            iNumeroRegistrosAfectados = oRefer.insertarRefer(conexion);
                            if (iNumeroRegistrosAfectados == 1) seguir = true;
                            else
                            {
                                seguir = false;
                                logger.error("Fila: "+Integer.toString(fila+1)+"Imposible insertar en REFER expediente: "+expediente);                    
                            }

                        }
                        if (seguir)
                        {// - ENTRADA
                              oEntrada = new Objetos.Entrada();
                              oEntrada.numexp = oSolicitudes.numexp;
                              if(oSolicitudes.delegado!=null) oEntrada.coddel = oSolicitudes.delegado.toString();
                              iNumeroRegistrosAfectados = oEntrada.insert(conexion);
                              if (iNumeroRegistrosAfectados == 1) seguir = true;
                              else
                              {
                                  seguir = false;
                                  logger.error("Fila: "+Integer.toString(fila+1)+"Imposible insertar en ENTRADA expediente: "+expediente);                    
                              }
                        }
                        if (seguir)
                        {// - AVISOS
                            oAvisos = new Objetos.Avisos();
                            oAvisos.numexp = oSolicitudes.numexp;
                            oAvisos.aviso1 = txtAviso;
                            if (dirEnAvisos) oAvisos.aviso1 += ". Direccion completa: "+direccion;
                            iNumeroRegistrosAfectados = oAvisos.insert(conexion);
                            if (iNumeroRegistrosAfectados == 1) seguir = true;
                            else
                            {
                                  seguir = false;
                                  logger.error("Fila: "+Integer.toString(fila+1)+"Imposible insertar en AVISOS expediente: "+expediente);                    
                            }

                        }
                        if (seguir && finca != null && !finca.trim().equals(""))
                        {//DATOS REGISTRALES
                            oDatosreg = new Objetos.Datosreg();
                            oDatosreg.numexp = oSolicitudes.numexp;
                            oDatosreg.numero = 1;
                            oDatosreg.tipoinm = oSolicitudes.tipoinm;
                            try
                            {
                                if (seccion != null && !seccion.equalsIgnoreCase("")) oDatosreg.seccion = Integer.parseInt(seccion);
                            }
                            catch (Exception e)
                            {//por si no podemos pasarlo a entero.
                                logger.error("No se ha podido convertir la sección :"+seccion+ " en el expediente: "+oDatosreg.numexp);
                            }
                            
                            if (registro != null && !registro.trim().equals(""))oDatosreg.registro = registro.trim().toUpperCase();
                            //if (nregistro != null && !nregistro.trim().equals("")) oDatosreg.registro += " Nº "+nregistro.trim().toUpperCase();
                            //if (tomo != null && !tomo.trim().equals("")) oDatosreg.tomo = tomo.trim().toUpperCase(); 
                            //if (libro != null && !libro.trim().equals("")) oDatosreg.libro = libro.trim().toUpperCase(); 
                            //if (folio != null && !folio.trim().equals("")) oDatosreg.folio = folio.trim().toUpperCase(); 
                            if (finca != null && !finca.trim().equals("")) oDatosreg.finca = finca.trim().toUpperCase(); ;
                            iNumeroRegistrosAfectados = oDatosreg.insert(conexion);
                            if (iNumeroRegistrosAfectados == 1) seguir = true;
                            else
                            {
                                  seguir = false;
                                  logger.error("Fila: "+Integer.toString(fila+1)+"Imposible insertar en DATOSREG expediente: "+expediente);                    
                            }
                            if (seguir)
                            {//CATASTRO
                                oCatastro = new Objetos.Catastro();
                                oCatastro.numexp = oSolicitudes.numexp;
                                oCatastro.numero = 1;    
                                oCatastro.tipoinm = oSolicitudes.tipoinm;
                                oCatastro.refcliente = objeto;
                                if (fcatastral != null) oCatastro.fcatastral = fcatastral.toUpperCase().trim();
                                iNumeroRegistrosAfectados = oCatastro.insert(conexion);
                                if (iNumeroRegistrosAfectados == 1) seguir = true;
                                else
                                {
                                  seguir = false;
                                  logger.error("Fila: "+Integer.toString(fila+1)+"Imposible insertar en CATASTRO expediente: "+expediente);                    
                                }
                            } 
                            if (seguir)
                            {
                                sConsulta = "SELECT * FROM notas_simples WHERE numexp = '"+oDatosreg.numexp+"' AND finca = '"+oDatosreg.finca+"'";
                                rs = Utilidades.Conexion.select(sConsulta,conexion);
                                if (!rs.next())
                                {
                                    sConsulta = "INSERT INTO notas_simples VALUES ('"+oSolicitudes.numexp+"','"+oDatosreg.finca+"','juan','04-06-2014 10:00:00',null,'1')";
                                    if (Utilidades.Conexion.insert(sConsulta, conexion) == 1)
                                    {
                                        seguir = true;
                                    }
                                    else 
                                    {
                                        seguir = false;
                                        logger.error("Fila: "+Integer.toString(fila+1)+"Imposible insertar en NOTAS SIMPLES expediente: "+expediente+" finca: "+oDatosreg.finca);                                       
                                    }                                                                                                
                                }
                                else
                                {
                                        logger.error("Fila: "+Integer.toString(fila+1)+"NOTAS SIMPLE YA SOLICITADA expediente: "+expediente+" finca: "+oDatosreg.finca);                                       
                                }
                                rs.close();
                                rs = null;
                            }
                        }
                        else
                        {
                            if (seguir) logger.info("Tasacion :"+oSolicitudes.numexp+ " sin FINCA.");
                        }                                        
                        if (seguir)
                        {
                            iNumeroRegistrosAfectados = Utilidades.Valtecnic.insertProdusu(oSolicitudes.numexp,"0","juan","A","1","RE", conexion);
                            if (iNumeroRegistrosAfectados == 1) seguir = true;
                            else
                            {
                                  seguir = false;
                                  logger.error("Fila: "+Integer.toString(fila+1)+"Imposible insertar en PRODUSU expediente: "+expediente);                    
                            }
                        }
                        if (seguir) 
                        {
                            conexion.commit();
                            logger.info("Fila: "+Integer.toString(fila+1)+" Insertado expediente: "+expediente);                    
                        }
                        else
                        {
                            conexion.rollback();
                            logger.error("Fila: "+Integer.toString(fila+1)+" No Insertada referencia: "+referencia);   
                        }



                    }//if expediente no existe
                    else
                    {//si existe damos de alta una nueva finca.
                        sConsulta = "SELECT max(numero) numero FROM datosreg WHERE numexp = '"+expediente.trim()+"'";
                        rs = Utilidades.Conexion.select(sConsulta,conexion);                                    
                        if (rs.next())
                        {
                                if (seguir && finca != null && !finca.trim().equals(""))
                                {//DATOS REGISTRALES
                                    oDatosreg = new Objetos.Datosreg();
                                    oDatosreg.numexp = expediente;
                                    oDatosreg.numero = rs.getInt("numero")+1;
                                    oDatosreg.tipoinm = "XXX";
                                    if (registro != null && !registro.trim().equals(""))oDatosreg.registro = registro.trim().toUpperCase();
                                    //if (nregistro != null && !nregistro.trim().equals("")) oDatosreg.registro += " Nº "+nregistro.trim().toUpperCase();
                                    //if (tomo != null && !tomo.trim().equals("")) oDatosreg.tomo = tomo.trim().toUpperCase(); 
                                    //if (libro != null && !libro.trim().equals("")) oDatosreg.libro = libro.trim().toUpperCase(); 
                                    //if (folio != null && !folio.trim().equals("")) oDatosreg.folio = folio.trim().toUpperCase(); 
                                    if (finca != null && !finca.trim().equals("")) oDatosreg.finca = finca.trim().toUpperCase(); ;
                                    iNumeroRegistrosAfectados = oDatosreg.insert(conexion);
                                    if (iNumeroRegistrosAfectados == 1) seguir = true;
                                    else
                                    {
                                          seguir = false;
                                          logger.error("Fila: "+Integer.toString(fila+1)+"Imposible insertar en DATOSREG expediente: "+expediente);                    
                                    }
                                    if (seguir)
                                    {//CATASTRO
                                        oCatastro = new Objetos.Catastro();
                                        oCatastro.numexp = expediente;
                                        oCatastro.numero = oDatosreg.numero;    
                                        oCatastro.tipoinm = oDatosreg.tipoinm;
                                        oCatastro.refcliente = objeto;
                                        if (fcatastral != null) oCatastro.fcatastral = fcatastral.toUpperCase().trim();
                                        iNumeroRegistrosAfectados = oCatastro.insert(conexion);
                                        if (iNumeroRegistrosAfectados == 1) seguir = true;
                                        else
                                        {
                                          seguir = false;
                                          logger.error("Fila: "+Integer.toString(fila+1)+"Imposible insertar en CATASTRO expediente: "+expediente);                    
                                        }
                                    }
                                    if (seguir)
                                    {
                                        sConsulta = "SELECT * FROM notas_simples WHERE numexp = '"+expediente+"' AND finca = '"+oDatosreg.finca+"'";
                                        rs = Utilidades.Conexion.select(sConsulta,conexion);
                                        if (!rs.next())
                                        {
                                            sConsulta = "INSERT INTO notas_simples VALUES ('"+expediente+"','"+oDatosreg.finca+"','juan','06-05-2014 16:45:23',null,'1')";
                                            if (Utilidades.Conexion.insert(sConsulta, conexion) == 1)
                                            {
                                                seguir = true;
                                            }
                                            else 
                                            {
                                                seguir = false;
                                                logger.error("Fila: "+Integer.toString(fila+1)+"Imposible insertar en NOTAS SIMPLES expediente: "+expediente+" finca: "+oDatosreg.finca);                                       
                                            }      
                                        }
                                        else
                                        {
                                            logger.error("Fila: "+Integer.toString(fila+1)+"NOTAS SIMPLE YA SOLICITADA expediente: "+expediente+" finca: "+oDatosreg.finca);                                       
                                        }
                                        rs.close();
                                        rs = null;
                                    }
                                    if (seguir) 
                                    {
                                        conexion.commit();
                                        logger.info("Fila: "+Integer.toString(fila+1)+" Insertado expediente: "+expediente);                    
                                    }
                                    else
                                    {
                                        conexion.rollback();
                                        logger.error("Fila: "+Integer.toString(fila+1)+" No Insertada referencia: "+referencia);   
                                    }
                                }
                                else
                                {
                                    if (seguir) logger.info("Tasacion :"+oSolicitudes.numexp+ " sin FINCA.");
                                }                                                                                                                        
                        }//if
                    }
                }
                oSolicitudes = null;
                oRefer = null;
                oEntrada = null;                    
                oAvisos = null;
                oDocumenta = null;
                oDatosreg = null;
                oCatastro = null;
                oNotasSimples = null;
            }//for lectura excel
            conexion.close();
        }
        catch (Exception e)
        {
            logger.error("Error general en la carga de tasaciones .Descripción: "+e.toString());
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
                logger.error("Imposible cerrar conexión con la B.D"+sqlException.toString());
            }             
        }//finally     
        
    }//
    
    
    private void ActualizaRefclienteTasaciones7AQR()
    {//carga desde excel asignando cliente 930 y la oficina en funcion del campo llaves:
        // si llaves = Si => oficina = CVIS
        // si llaves = No => oficina = SVIS
        
        java.sql.Connection conexion = null;
        //String sUrlExcel = "/data/informes/cargatasaciones/952/carga952.xls";        
        //String sUrlExcel = "/data/informes/cargatasaciones/952/carga952-2.xls";        
        String sUrlExcel = "/data/informes/cargatasaciones/7AQR/mio_ref.xls";        
        
        //FICHERO LOG4J
        PropertyConfigurator.configure("/data/informes/cargatasaciones/7AQR/" + "Log4j.properties");   
        
        try
        {
            //conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:desarrollo/des0329@oraserver00:1521:rvtn");
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@oraserver01:1521:rvtn1");
            conexion.setAutoCommit(false);
               //nombre de la hora
            
            cliente = 455;
            String valor = null;
            Double iValor = 0.0;
              
            String objeto = null;
            String referencia = null;
            String letra = null;
            String tipoinm = null;
            String txtAviso = null;
            String direccion = null;
            String direccion1 = null;
            String direccion2 = null;
            String numero = null;
            String planta = null;
            String puerta = null;
            String superficie = null;
            String localidad = null;
            String provincia = null;
            String codpos = null;
            String registro = null;
            String nregistro = null;
            String tomo = null;
            String libro = null;
            String folio = null;
            String finca = null;
            String prestamo = null;
            String expediente = null; 
            String sConsulta = null;
            String prefijo = null;
            String delegacion = null;
            int iNumeroRegistrosAfectados = 0;            
            boolean seguir = true;            
            String fcatastral = null;
            boolean existe = false;
            boolean dirEnAvisos = false;
            String titular = "";
            String seccion = "";
            boolean estadoOK = true;
            String sUpdate = null;
            
            oExcel = new Utilidades.Excel(sUrlExcel);  
            
            hoja = "Hoja1";
            for (int fila = 0; fila < 1137;fila ++)              
            {                                
                //leemos valores del excel  
                objeto = null;
                referencia = null;
                letra = null;
                tipoinm = null;
                txtAviso = null;
                direccion = null;
                direccion1 = null;
                direccion2 = null;
                numero = null;
                planta = null;
                puerta = null;
                superficie = null;
                localidad = null;
                provincia = null;
                codpos = null;
                registro = null;
                nregistro = null;
                tomo = null;
                libro = null;
                folio = null;
                finca = null;
                prestamo = null;
                expediente = null; 
                sConsulta = null;
                prefijo = null;
                delegacion = null;
                fcatastral = null;
                existe = false;
                dirEnAvisos = false;
                titular = "";
                seccion = null;
                estadoOK = true;
                sUpdate = null;
                //referencia
                decimal = false;
                objeto = getCellValue(fila,0);
                referencia = getCellValue(fila,1);
                finca = getCellValue(fila,2);                             
                
                if (referencia != null)
                {//numeramos la tasacion
                    
                    /*
                     * este codigo es para cambiar el api generando el lote 7001
                     */
                    sConsulta = "SELECT numexp FROM refer WHERE referencia = '"+referencia.trim()+"'";
                    rs = Utilidades.Conexion.select(sConsulta,conexion);                                    
                    if (rs.next())
                    {
                        expediente = rs.getString("numexp");    
                        sUpdate = "UPDATE refer SET api = '7001' WHERE numexp = '"+expediente+"' AND referencia = '"+referencia.trim()+"'";
                        if (Utilidades.Conexion.update(sUpdate, conexion) == 1)                        
                        {                            
                           
                                conexion.commit();
                                logger.info("Actualizado refer expediente: "+expediente+ " con API: 7001");
                        }
                        else
                        {
                                conexion.rollback();
                                logger.info("NO actulizado refer expediente: "+expediente+ " con API: 7001");
                        }
                       
                    }                    
                    else 
                    {
                        logger.error("No se localiza refer para la referencia :"+referencia);
                    }
                    /* esto codigo es para cambiar en catastro refcliente
                    sConsulta = "SELECT numexp FROM refer WHERE referencia = '"+referencia.trim()+"'";
                    rs = Utilidades.Conexion.select(sConsulta,conexion);                                    
                    if (rs.next())
                    {
                        expediente = rs.getString("numexp");
                                        
                        rs.close();
                        rs = null; 
                    
                        sConsulta = "SELECT * FROM datosreg WHERE numexp = '"+expediente+"' AND finca = '"+finca.trim()+"'";
                        rs = Utilidades.Conexion.select(sConsulta,conexion); 
                        if (rs.next()) 
                        {
                            numero = rs.getString("numero");
                            oCatastro = new Objetos.Catastro();
                        if (oCatastro.load(expediente, Integer.parseInt(numero),conexion))
                        {
                            oCatastro.refcliente = objeto;
                            if (oCatastro.update(conexion) == 1) 
                            {
                                conexion.commit();
                                logger.info("Actualizado refcliente expediente: "+expediente+ " finca:"+ finca+ "con el valor: "+objeto);
                            }
                            else
                            {
                                conexion.rollback();
                                logger.info("No se puede actualizar refcliente expediente: "+expediente+ " finca:"+ finca+ "con el valor: "+objeto);
                            }
                            }
                            else 
                            {
                                logger.error("No se localiza catastro para el expediente :"+expediente +" y finca: "+finca);
                            }
                        }
                        else
                        {
                            logger.error("No se localiza finca:"+finca+ "en el expediente: "+expediente);
                        }
                        
                        rs.close();
                        rs = null;
                    }
                    else 
                        {
                            logger.error("No se localiza refer para la referencia :"+referencia);
                        }
                   *
                   * */
                }
                else
                {
                    expediente = null;
                    logger.error("referencia nula");  
                    estadoOK = false;
                    
                }   
                
            }//for lectura excel
            conexion.close();
        }
        catch (Exception e)
        {
            logger.error("Error general en la carga de tasaciones .Descripción: "+e.toString());
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
                logger.error("Imposible cerrar conexión con la B.D"+sqlException.toString());
            }             
        }//finally     
        
    }//
    
    private void cargaTasacionesSAREB()
    {//carga desde excel 
        //cliente = 250
        //oficina = S.C.
        //numeracion automática.
        //sin peticion de nota.
        
        java.sql.Connection conexion = null;
        //String sUrlExcel = "/data/informes/cargatasaciones/952/carga952.xls";        
        //String sUrlExcel = "/data/informes/cargatasaciones/952/carga952-2.xls";        
        //String sUrlExcel = "/data/informes/cargatasaciones/SAREB/CARGA1SAREB14052014mio.xls";        
        String sUrlExcel = "/data/informes/cargatasaciones/SAREB/SAREBLOTE217062014133000.xls";        
        
        //FICHERO LOG4J
        PropertyConfigurator.configure("/data/informes/cargatasaciones/SAREB/" + "Log4j.properties");   
        
        try
        {
            
               //nombre de la hora
            
            int cliente = 250;
            String oficina = "S.C.";
            String valor = null;
            Double iValor = 0.0;
            
            String idufir = null;
            String refcliente = null;
            String referencia = null;            
            String tipoinm = null;
            String txtAviso = "";
            String direccion = null;
            String direccion1 = null;                        
            String localidad = null;
            String provincia = null;
            String codpos = null;
            String registro = null;            
            String tomo = null;
            String libro = null;
            String folio = null;
            String finca = null;            
            String fcatastral = null;
            String expediente = null; 
            String titular = "";
            String sConsulta = null;
            String prefijo = null;
            String delegacion = null;
            int iNumeroRegistrosAfectados = 0;            
            boolean seguir = true;            
            
            boolean existe = false;
            boolean dirEnAvisos = false;
                        
            boolean estadoOK = true;
            String lote = "";
            
            oExcel = new Utilidades.Excel(sUrlExcel);  
            
            hoja = "Hoja1";
            //lote = "1"; asociado al excel CARGA1SAREB14052014mio.xls + el excel SAREBLOTE217062014133000.xls
            lote="2"; //asociado al excel SAREBLOTE212062014121500
            for (int fila = 52; fila < 632;fila ++)              
            {                                
                //leemos valores del excel                  
                //conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:desarrollo/des0329@oraserver00:1521:rvtn");
                conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@oraserver01:1521:rvtn1");
                conexion.setAutoCommit(false);
            
                referencia = null;   
                refcliente = null;
                tipoinm = null;
                txtAviso = "Ver Circular 0010/2014";
                direccion = null;
                direccion1 = null;                                
                localidad = null;
                provincia = null;
                codpos = null;
                registro = null;                
                tomo = null;
                libro = null;
                folio = null;
                finca = null;                
                expediente = null; 
                sConsulta = null;
                prefijo = null;
                delegacion = null;
                fcatastral = null;
                idufir = null;
                
                existe = false;
                dirEnAvisos = false;
                titular = "";                
                estadoOK = true;
                
                //referencia
                decimal = false;
                
                referencia = getCellValue(fila,0);   
                refcliente = getCellValue(fila,1);   
                titular = getCellValue(fila,2);
                finca = getCellValue(fila,3);
                tomo = getCellValue(fila,4);
                libro = getCellValue(fila,5);
                folio = getCellValue(fila,6);
                registro = getCellValue(fila,7);
                idufir = getCellValue(fila,8);
                if (idufir != null) idufir = Utilidades.Cadenas.TrimTotal(idufir);
                fcatastral = getCellValue(fila,9);
                direccion = getCellValue(fila,10);
                direccion1 = getCellValue(fila,11);
                tipoinm = getCellValue(fila,12);
                provincia = getCellValue(fila,13);
                localidad = getCellValue(fila,14);
                codpos = getCellValue(fila,15); 
                if (codpos != null) codpos = Utilidades.Cadenas.TrimTotal(codpos);
                
                if (referencia != null)
                {//
                    //buscamos si ya existe la tasacion por la referencia
                    referencia = Utilidades.Cadenas.TrimTotal(referencia).toUpperCase();
                    sConsulta = "SELECT numexp FROM refer WHERE referencia = '"+referencia+"'";
                    rs = Utilidades.Conexion.select(sConsulta,conexion);                                    
                    if (rs.next())
                    {//la tasacion ya existe por tanto ese registro lo insertamos como nueva finca registral.
                        expediente = rs.getString("numexp");
                    }
                    else expediente = null;
                    rs.close();
                    rs = null;                    
                }
                
                if (expediente == null)
                {//nuevo expediente
                   //numeramos la tasacion
                   boolean existexp = true;
                   while (existexp)
                   {
                       sConsulta = "SELECT prefijo,coddel,nompro FROM provincias WHERE codpro = "+Utilidades.Cadenas.completTextWithLeftCharacter(codpos, '0', 5).substring(0, 2);
                       rs = Utilidades.Conexion.select(sConsulta,conexion);                                    
                       if (rs.next())
                       {
                            delegacion = rs.getString("coddel");  
                            prefijo = rs.getString("prefijo");  
                            provincia = rs.getString("nompro");
                       }
                       rs.close();
                       rs = null;
                       expediente = numera_tasacion(prefijo,"50");
                       //comprobamos que no exista ya el expediente
                        sConsulta = "SELECT numexp FROM solicitudes WHERE numexp = '"+expediente+"'";
                        rs = Utilidades.Conexion.select(sConsulta,conexion);                                    
                        if (rs.next())
                        {
                            expediente = rs.getString("numexp");
                            existexp = true;
                            logger.error("Fila: "+Integer.toString(fila+1)+".Numeracion repetida en solicitudes para el expediente: "+expediente);                               
                        }
                        else
                        {
                            sConsulta = "SELECT numexp FROM his_solicitudes WHERE numexp = '"+expediente+"'";
                            rs = Utilidades.Conexion.select(sConsulta,conexion);                                    
                            if (rs.next())
                            {                                
                                expediente = rs.getString("numexp");
                                existexp = true;
                                logger.error("Fila: "+Integer.toString(fila+1)+".Numeracion repetida en his_solicitudes para el expediente: "+expediente);                               
                                logger.error("Seguimos numerando");
                            }
                            else existexp = false;
                        }
                        if (existexp)
                        {
                            try
                            {//ESPERAMOS 2 SEGUNDOS ANTES DE PROCESAR EL SIGUEINTE SOLICITUD                                   
                                  Thread.currentThread().sleep(2000);                                    
                            }
                            catch (InterruptedException ie)
                            {                                                                                                
                                 logger.error(" Imposible realizar espera para recibir respuesta de UCI");                                        
                            }
                        }
                        rs.close();
                        rs = null;   
                    }//while existexp
                   
                    if (!existexp && expediente != null)
                    {
                        // - SOLICITUDES
                        oSolicitudes = new Objetos.v2.Solicitudes();
                        oSolicitudes.numexp = expediente;
                        oSolicitudes.codcli = cliente;
                        oSolicitudes.oficina = oficina;
                        oSolicitudes.tipoif = 8;
                        oSolicitudes.nifsolici = "A86602158";
                        oSolicitudes.solici = "SAREB "+quitaCerosFinca(titular)+"-"+Utilidades.Cadenas.TrimTotal(referencia);                   
                        oSolicitudes.tipoinm = tipoinm;                                     

                        oClidir = new Objetos.Clidir();
                        if (oClidir.load(cliente, oSolicitudes.oficina, conexion))
                        {                                                
                            oSolicitudes.denomina = oClidir.denomina;
                            oSolicitudes.dirofi = oClidir.direcc;
                            oSolicitudes.postal = oClidir.codpos;
                            oSolicitudes.poblacion = oClidir.pobla;
                            oSolicitudes.provclient = oClidir.provin;
                            oSolicitudes.telefo = oClidir.telefo;
                            oSolicitudes.fax = oClidir.fax;
                            oSolicitudes.encarg = oClidir.percon;                        
                        }
                                                
                        if (direccion != null)
                        {
                            direccion = Utilidades.Cadenas.TrimTotal(direccion);
                            if (direccion.length() > 60) 
                            {
                                oSolicitudes.calle = direccion.substring(0,60).toUpperCase();
                                dirEnAvisos = true;
                            }
                            else oSolicitudes.calle = direccion.toUpperCase();
                        }
                        if (direccion1 != null)
                        {
                            direccion1 = Utilidades.Cadenas.TrimTotal(direccion1);
                            if (direccion1.length() > 40) 
                            {
                                oSolicitudes.situacion = direccion1.substring(0,40).toUpperCase();                                
                            }
                            else oSolicitudes.situacion = direccion1.toUpperCase();
                        }
                        
                        if (localidad != null && !localidad.trim().equals(""))
                        {
                            oSolicitudes.locali = localidad.trim().toUpperCase();
                            oSolicitudes.munici = oSolicitudes.locali;           
                        }
                        else 
                        {
                            oSolicitudes.locali = "A DETERMINAR";
                            oSolicitudes.munici = "A DETERMINAR";
                        }
                        if (codpos != null) oSolicitudes.codpos = Integer.parseInt(codpos);
                        oSolicitudes.delegado = Integer.parseInt(delegacion);                                    
                        oSolicitudes.provin = provincia.toUpperCase();
                        oSolicitudes.codage = null;
                        oSolicitudes.fchenc = new java.util.Date();
                        oSolicitudes.codest = new Integer(0);   
                        //oSolicitudes.contacto = "In situ";
                        oSolicitudes.indicador = 1; //insertamos DOCUMENTA
                        //oSolicitudes.telefonos = tefno1.trim();

                        iNumeroRegistrosAfectados = oSolicitudes.insertSolicitudes(conexion);
                        if (iNumeroRegistrosAfectados == 1) seguir = true;
                        else
                        {
                            seguir = false;
                            logger.error("Fila: "+Integer.toString(fila+1)+"Imposible insertar en SOLICITUDES expediente: "+expediente);                    
                        }
                        if (seguir)    
                        {//                                        // - REFER
                            oRefer = new Objetos.Refer();
                            oRefer.numexp = oSolicitudes.numexp;
                            oRefer.referencia = referencia;      
                            oRefer.api = lote;
                            //oRefer.objeto = objeto;                                                
                            iNumeroRegistrosAfectados = oRefer.insertarRefer(conexion);
                            if (iNumeroRegistrosAfectados == 1) seguir = true;
                            else
                            {
                                seguir = false;
                                logger.error("Fila: "+Integer.toString(fila+1)+"Imposible insertar en REFER expediente: "+expediente);                    
                            }

                        }
                        if (seguir)
                        {// - ENTRADA
                              oEntrada = new Objetos.Entrada();
                              oEntrada.numexp = oSolicitudes.numexp;
                              if(oSolicitudes.delegado!=null) oEntrada.coddel = oSolicitudes.delegado.toString();
                              iNumeroRegistrosAfectados = oEntrada.insert(conexion);
                              if (iNumeroRegistrosAfectados == 1) seguir = true;
                              else
                              {
                                  seguir = false;
                                  logger.error("Fila: "+Integer.toString(fila+1)+"Imposible insertar en ENTRADA expediente: "+expediente);                    
                              }
                        }
                        if (seguir)
                        {// - AVISOS
                            oAvisos = new Objetos.Avisos();
                            oAvisos.numexp = oSolicitudes.numexp;
                            oAvisos.aviso1 = txtAviso;
                            if (dirEnAvisos) oAvisos.aviso1 += ". Direccion completa: "+direccion;
                            iNumeroRegistrosAfectados = oAvisos.insert(conexion);
                            if (iNumeroRegistrosAfectados == 1) seguir = true;
                            else
                            {
                                  seguir = false;
                                  logger.error("Fila: "+Integer.toString(fila+1)+"Imposible insertar en AVISOS expediente: "+expediente);                    
                            }
                        }
                        if (seguir && finca != null && !finca.trim().equals(""))
                        {//DATOS REGISTRALES
                            oDatosreg = new Objetos.Datosreg();
                            oDatosreg.numexp = oSolicitudes.numexp;
                            oDatosreg.numero = 1;
                            oDatosreg.tipoinm = oSolicitudes.tipoinm;                                                        
                            if (registro != null && !registro.trim().equals(""))
                            {
                                registro = Utilidades.Cadenas.TrimTotal(registro).toUpperCase();
                                oDatosreg.registro = registro.trim().toUpperCase();
                            }                            
                            if (tomo != null && !tomo.trim().equals("")) oDatosreg.tomo = tomo.trim().toUpperCase(); 
                            if (libro != null && !libro.trim().equals("")) oDatosreg.libro = libro.trim().toUpperCase(); 
                            if (folio != null && !folio.trim().equals("")) oDatosreg.folio = folio.trim().toUpperCase(); 
                            if (finca != null && !finca.trim().equals("")) oDatosreg.finca = quitaCerosFinca(finca.trim()).toUpperCase();
                            if (idufir != null && !idufir.trim().equals("")) oDatosreg.idufir = Long.parseLong(idufir);
                            iNumeroRegistrosAfectados = oDatosreg.insert(conexion);
                            if (iNumeroRegistrosAfectados == 1) seguir = true;
                            else
                            {
                                  seguir = false;
                                  logger.error("Fila: "+Integer.toString(fila+1)+"Imposible insertar en DATOSREG expediente: "+expediente);                    
                            }
                            if (seguir)
                            {//CATASTRO
                                oCatastro = new Objetos.Catastro();
                                oCatastro.numexp = oSolicitudes.numexp;
                                oCatastro.numero = 1;    
                                oCatastro.tipoinm = oSolicitudes.tipoinm;
                                if (refcliente != null) oCatastro.refcliente = refcliente.trim();
                                if (fcatastral != null) oCatastro.fcatastral = Utilidades.Cadenas.TrimTotal(fcatastral).toUpperCase();
                                iNumeroRegistrosAfectados = oCatastro.insert(conexion);
                                if (iNumeroRegistrosAfectados == 1) seguir = true;
                                else
                                {
                                  seguir = false;
                                  logger.error("Fila: "+Integer.toString(fila+1)+"Imposible insertar en CATASTRO expediente: "+expediente);                    
                                }
                            }
                            if (seguir)
                            {
                                iNumeroRegistrosAfectados = Utilidades.Valtecnic.insertProdusu(oSolicitudes.numexp,"0","juan","A","1","RE", conexion);
                                if (iNumeroRegistrosAfectados == 1) seguir = true;
                                else
                                {
                                    seguir = false;
                                    logger.error("Fila: "+Integer.toString(fila+1)+"Imposible insertar en PRODUSU expediente: "+expediente);                    
                                }
                            }                                                        
                            if (seguir) 
                            {
                                conexion.commit();
                                logger.info("Fila: "+Integer.toString(fila+1)+" Insertado expediente: "+expediente);                    
                            }
                            else
                            {
                                conexion.rollback();
                                logger.error("Fila: "+Integer.toString(fila+1)+" No Insertada referencia: "+referencia);   
                            }
                        }
                        else
                        {
                            if (seguir) logger.info("Tasacion :"+oSolicitudes.numexp+ " sin FINCA.");
                        }
                    }//expediente nuevo                   
                }//if expediente es nulo
                else
                {//si existe damos de alta una nueva finca.
                        sConsulta = "SELECT max(numero) numero FROM datosreg WHERE numexp = '"+expediente.trim()+"'";
                        rs = Utilidades.Conexion.select(sConsulta,conexion);                                    
                        if (rs.next())
                        {
                                if (seguir && finca != null && !finca.trim().equals(""))
                                {//DATOS REGISTRALES
                                    oDatosreg = new Objetos.Datosreg();
                                    oDatosreg.numexp = expediente;
                                    oDatosreg.numero = oDatosreg.numero = rs.getInt("numero")+1;;
                                    oDatosreg.tipoinm = tipoinm;                                                        
                                    if (registro != null && !registro.trim().equals(""))
                                    {
                                        registro = Utilidades.Cadenas.TrimTotal(registro).toUpperCase();
                                        oDatosreg.registro = registro.trim().toUpperCase();
                                    }                            
                                    if (tomo != null && !tomo.trim().equals("")) oDatosreg.tomo = tomo.trim().toUpperCase(); 
                                    if (libro != null && !libro.trim().equals("")) oDatosreg.libro = libro.trim().toUpperCase(); 
                                    if (folio != null && !folio.trim().equals("")) oDatosreg.folio = folio.trim().toUpperCase(); 
                                    if (finca != null && !finca.trim().equals("")) oDatosreg.finca = finca.trim().toUpperCase();
                                    if (idufir != null && !idufir.trim().equals("")) oDatosreg.idufir = Long.parseLong(idufir);
                                    iNumeroRegistrosAfectados = oDatosreg.insert(conexion);
                                    if (iNumeroRegistrosAfectados == 1) seguir = true;
                                    else
                                    {
                                          seguir = false;
                                          logger.error("Fila: "+Integer.toString(fila+1)+"Imposible insertar en DATOSREG expediente: "+expediente);                    
                                    }
                                    if (seguir)
                                    {//CATASTRO
                                        oCatastro = new Objetos.Catastro();
                                        oCatastro.numexp = oDatosreg.numexp;
                                        oCatastro.numero = oDatosreg.numero;    
                                        oCatastro.tipoinm = oDatosreg.tipoinm;
                                        if (refcliente != null) oCatastro.refcliente = refcliente.trim();
                                        if (fcatastral != null) oCatastro.fcatastral = Utilidades.Cadenas.TrimTotal(fcatastral).toUpperCase();
                                        iNumeroRegistrosAfectados = oCatastro.insert(conexion);
                                        if (iNumeroRegistrosAfectados == 1) seguir = true;
                                        else
                                        {
                                          seguir = false;
                                          logger.error("Fila: "+Integer.toString(fila+1)+"Imposible insertar en CATASTRO expediente: "+expediente);                    
                                        }
                                    }
                                    
                                    if (seguir) 
                                    {
                                        conexion.commit();
                                        logger.info("Fila: "+Integer.toString(fila+1)+" Insertada Solo finca: "+expediente);                    
                                    }
                                    else
                                    {
                                        conexion.rollback();
                                        logger.error("Fila: "+Integer.toString(fila+1)+" No Insertada Solo finca: "+referencia);   
                                    }
                                }
                                else
                                {
                                    logger.error("Fila: "+Integer.toString(fila+1)+"la finca es nula expediente: "+expediente);   
                                }                                                                                                                        
                        }//if
                    
                }                              
                oSolicitudes = null;
                oRefer = null;
                oEntrada = null;                    
                oAvisos = null;
                oDocumenta = null;
                oDatosreg = null;
                oCatastro = null;
                oSuelo = null;
                oEdificios = null;
                oElementos = null;
                oRelafincas = null;
                oResultado = null;
                try
                {
                    conexion.close();
                    if (conexion != null && !conexion.isClosed()) 
                    {                        
                        conexion.close();   
                        conexion = null;
                    }
                }
                catch (java.sql.SQLException sqlException)
                {     
                    logger.error("Imposible cerrar conexión con la B.D"+sqlException.toString());
                }
            }//for lectura excel
            
        }
        catch (Exception e)
        {
            logger.error("Error general en la carga de tasaciones .Descripción: "+e.toString());
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
                logger.error("Imposible cerrar conexión con la B.D"+sqlException.toString());
            }             
        }//finally     
        
    }//cargaTasacionesSAREB
    
    private String getStringCellValue(int fila,int columna)
    {
        String valor = null;
        try
        {
                    celda = oExcel.getCeldaFilaHoja(hoja,fila,columna);                
                    if (celda != null) 
                    {
                        valor = celda.getStringCellValue();
                        if (celda != null)
                        {
                            valor = valor.trim().toUpperCase();
                        }
                    } 
        }
        catch (Exception e)
        {
            valor = null;
        }
        finally
        {
            return valor;
        }
        
    }//getStringCellValue
    
    private Integer getNumericCellValue(int fila,int columna)
    {
        Integer valor = 0;
        Double ivalor = 0.0;
        
        try
        {                        
                    
                        celda = oExcel.getCeldaFilaHoja(hoja,fila,columna);                
                        if (celda != null) 
                        {
                            ivalor = celda.getNumericCellValue();
                            if (celda != null)
                            {
                                if (ivalor != null)  valor = (int) Math.floor(ivalor);
                            }
                        }      
                    }
         catch (Exception e)
         {
             valor = null;
         }     
        finally
        {
            return valor;
        }
        
         
    }//getNumericCellValue
    
    private String getCellValue(int fila,int columna)
    {
         String valor = null;
         Double iValor = 0.0;
         
         try
         {                        
          
              celda = oExcel.getCeldaFilaHoja(hoja,fila,columna);                
              if (celda != null) 
              {
                   valor = celda.getStringCellValue();
              }      
         }
         catch (NumberFormatException nfe)
         {             
             celda = oExcel.getCeldaFilaHoja(hoja,fila,columna);                
             if (celda != null) 
             {
                 iValor = celda.getNumericCellValue();                                             
                 if (iValor != null && !decimal)  valor = Long.toString((long) Math.floor(iValor));                 
                 if (iValor != null && decimal)  valor = Double.toString((Double)(iValor));                                             
             }      
         }
         catch (Exception e)
         {
             valor = null;
             logger.error("Fila: "+Integer.toString(fila)+"Columna: "+Integer.toString(columna)+" Imposible leer valor: "+e.toString());                    
         }
         finally
         {
             return valor;
         }
    }
    
    public static String quita_comilla(String cadena){
        
        cadena = cadena.replaceAll("\"", "");
        String devuelve = "";
        for (int i=0; i<cadena.length(); i++){            
            if(cadena.charAt(i)!='\'') devuelve = devuelve + cadena.charAt(i);
            else devuelve = devuelve +  "''";
        }
        return devuelve;
    }
    
    private String quitaCerosFinca(String sFinca)
  {
      String finca = null;
      try
      {
        if (sFinca != null) 
        {
           for (int j = 0; j< sFinca.length();j++)
           {//quitamos los ceros que Sivasa envia al incio de cada finca
                if (!sFinca.substring(j,j+1).equals("0")) 
                {
                       finca = sFinca.substring(j).trim();
                       break;
                }
           }//for
        }//if
      }
      catch (Exception e)
      {
          finca = null;
      }
      finally
      {
        return finca;
      }                            
  }//quitaCerosFinca    
    
    
  private String numera_tasacion(String prefijo,String parteCliente)  
  {
      String sNumExp = "";
      String sAnioEncargo = "";
      
      try
      {
                    //-Patrón para las horas y las fechas
                     SimpleDateFormat sdfHoras = new SimpleDateFormat("HH:mm:ss");
                     SimpleDateFormat sdfFechas = new SimpleDateFormat("yyyy-MM-dd");
                     sAnioEncargo = sdfFechas.format(new java.util.Date()).substring(3, 4);
                    //Se obtiene la hora del sistema HH:MM:SS
                    String sHoraSistema = sdfHoras.format(new java.util.Date()).toString();
                    String sFechaSistema = sdfFechas.format(new java.util.Date()).toString();
                    String sAuxiliar="";
                    sAuxiliar = sHoraSistema;
                    String sHoraActual = sAuxiliar.substring(0,sAuxiliar.indexOf(":"));
                    sAuxiliar = sAuxiliar.substring(sAuxiliar.indexOf(":")+1,sAuxiliar.length());
                    String sMinutoActual = sAuxiliar.substring(0,sAuxiliar.indexOf(":"));
                    sAuxiliar = sAuxiliar.substring(sAuxiliar.indexOf(":")+1,sAuxiliar.length());
                    String sSegundoActual = sAuxiliar.substring(0,sAuxiliar.length());
                    //Se obtiene la fecha del sistema yyyy-MM-dd
                    sAuxiliar = sFechaSistema;
                    String sAnioActual = sAuxiliar.substring(0,sAuxiliar.indexOf("-"));
                    sAuxiliar = sAuxiliar.substring(sAuxiliar.indexOf("-")+1,sAuxiliar.length());
                    String sMesActual = sAuxiliar.substring(0,sAuxiliar.indexOf("-"));
                    sAuxiliar = sAuxiliar.substring(sAuxiliar.indexOf("-")+1,sAuxiliar.length());
                    String sDiaActual = sAuxiliar.substring(0,sAuxiliar.length());
                    //-Conformación del número de expediente
                    sNumExp = prefijo.trim() + parteCliente + sAnioEncargo.substring(sAnioEncargo.length()-1)+" "+sMesActual+sDiaActual+" ";
                    sNumExp+=sHoraActual+sMinutoActual+sSegundoActual;        
      }
      catch (Exception e)
      {
           sNumExp = null;   
      }
      finally
      {
          return sNumExp;
      }
      
  }//numera_tasacion
  
  
  private void solicitarNotaSimple()
  {/* inserta la peticion de nota 
   * 
   * 
   */
      String sConsulta = "";
      ResultSet rs = null;
      Connection conexion = null;
      int solicitadas = 0;
      int noSolicitadas = 0;
      int total = 0;
      try
      {
          //FICHERO LOG4J
          PropertyConfigurator.configure("/data/informes/cargaNotasSimples/" + "Log4j.properties");  
          conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@oraserver01:1521:rvtn1");
          conexion.setAutoCommit(false);
          sConsulta = "select d.numexp,d.finca from datosreg d join solicitudes s on (d.numexp = s.numexp) where s.codcli = 729";
          rs = Utilidades.Conexion.select(sConsulta, conexion);
          while (rs.next())
          {
              total ++;
              logger.info("Tasación : "+rs.getString("numexp")+ ". Finca: "+rs.getString("finca"));
              if (rs.getString("finca") != null && !rs.getString("finca").trim().equals(""))
              {
                        sConsulta = "INSERT INTO notas_simples VALUES ('"+rs.getString("numexp")+"','"+rs.getString("finca")+"','juan','16-07-2014 12:45:23',null,'1')";
                        if (Utilidades.Conexion.insert(sConsulta, conexion) == 1)
                        {
                            conexion.commit();
                            logger.info("Nota simple solicitada");
                            solicitadas ++;
                        }
                        else 
                        {
                            conexion.rollback();
                            logger.error("Nota simple NO solicitada");
                            noSolicitadas ++;
                        }
              }    
          }//while
          rs.close();
          rs = null;
          conexion.close();
          
      }
      catch (Exception e)
      {
          logger.error("Excepcion en la peticion de notas. Motivo: "+e.toString());
      }
      finally
      {
          try
          {
            if (!conexion.isClosed()) conexion.close();
          }
          catch (Exception e)
          {
              
          }            
          logger.info("Total: "+total);
          logger.info("Solicitadas: "+solicitadas);
          logger.info("NO Solicitadas: "+noSolicitadas);
      }
           
  }
  
  private void cargaJuicioCritico()
  {
    
      String sConsulta = "";
      ResultSet rs = null;      
      Connection conexion = null;
      int solicitadas = 0;
      int noSolicitadas = 0;
      int total = 0;
      Objetos.Txtlib oTxtlib = new Objetos.Txtlib();
      
      try
      {
          //FICHERO LOG4J
          PropertyConfigurator.configure("/data/informes/cargaNotasSimples/" + "Log4j.properties");  
          conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@oraserver01:1521:rvtn1");
          conexion.setAutoCommit(false);
          sConsulta = "select s.numexp,r.procede from solicitudes s join refer r on (s.numexp = r.numexp) where s.codcli = 729 and r.procede is not null";
          rs = Utilidades.Conexion.select(sConsulta, conexion);
          while (rs.next())
          {                
                if (rs.getString("procede") != null && oTxtlib.load(rs.getString("procede"), "S", conexion))
                {
                    total ++;
                    oTxtlib.numexp = rs.getString("numexp");
                    if (oTxtlib.insert(conexion) == 1)
                    {
                        conexion.commit();
                        solicitadas ++;
                        logger.info("Cargado Juicio critico del expediente: "+rs.getString("procede")+" sobre el expediente: "+rs.getString("numexp"));
                    }
                    else
                    {
                        conexion.rollback();
                        noSolicitadas ++;
                        logger.error("NO se ha podido cargar Juicio critico del expediente: "+rs.getString("procede")+" sobre el expediente: "+rs.getString("numexp"));
                    }
                }
                oTxtlib.codigo = "";
                oTxtlib.codusua = "";
                oTxtlib.numexp = "";
                oTxtlib.texto = "";
          }//while
          rs.close();
          rs = null;
          conexion.close();
          
      }
      catch (Exception e)
      {
          logger.error("Excepcion en la peticion de notas. Motivo: "+e.toString());
      }
      finally
      {
          try
          {
            if (!conexion.isClosed()) conexion.close();
          }
          catch (Exception e)
          {
              
          }            
          logger.info("Total: "+total);
          logger.info("Solicitadas: "+solicitadas);
          logger.info("NO Solicitadas: "+noSolicitadas);
      }
           
  }//cargaJuicioCritico
  
  private void cargaDeyDe()
  {
      
  }
   
  
    private void cargaDeyDeSol()
    {
        java.sql.Connection conexion = null;
        int total = 0;
        int buenos = 0;
        int malos = 0;
        int repetidos = 0;
        
        //String sUrlExcel = "/data/informes/cargatasaciones/deyde/deydesol.xls";        
        String sUrlExcel = "/data/informes/cargatasaciones/deyde/DEDEYDEPRE.xls";        
        //FICHERO LOG4J
        PropertyConfigurator.configure("/data/informes/cargatasaciones/deyde/" + "Log4j.properties");   
        
        try
        {
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:informatica/infx0329@oraserver02:1521:rvtnprod");
            conexion.setAutoCommit(false);
               
            String cposta = null;
            String cpobla = null;
            String lmunici = null;
            String seccion = null;
            String indica = null;
            String indpob = null;
            String indcpo = null;
            String refcat = null;
            String refvaltecnic = null;
            
            Objetos.Deydesol oDeydelsol = new Objetos.Deydesol();
            
            oExcel = new Utilidades.Excel(sUrlExcel);  
            hoja = "VALTEPRE";                        
            finicio = 2;                                  
            ffin = 29183;                        
            for (int fila = finicio; fila < ffin;fila ++)              
            {
                 
                try
                {
                    oDeydelsol.clear();
                    total ++;
                    cposta = getCellValue(fila,6);
                    if (cposta != null && !cposta.trim().equalsIgnoreCase("") && cposta.trim().length() > 0) oDeydelsol.cposta = Integer.parseInt(cposta);
                    cpobla = getCellValue(fila,7);
                    if (cpobla != null && !cpobla.trim().equalsIgnoreCase("") && cpobla.trim().length() > 0) oDeydelsol.cpobla = cpobla;
                    lmunici = getCellValue(fila,9);
                    if (lmunici != null ) oDeydelsol.lmunici = Utilidades.Cadenas.procesarComillasConsulta(lmunici);
                    seccion = getCellValue(fila,11);
                    if (seccion != null && !seccion.trim().equalsIgnoreCase("") && seccion.trim().length() > 0) oDeydelsol.seccion = Long.parseLong(seccion);
                    indica = getCellValue(fila,12);
                    if (indica != null) oDeydelsol.indica = indica;
                    indpob = getCellValue(fila,14);
                    if (indpob != null && !indpob.trim().equalsIgnoreCase("") && indpob.trim().length() > 0) oDeydelsol.indpob = Integer.parseInt(indpob);
                    indcpo = getCellValue(fila,16);
                    if (indcpo != null && !indcpo.trim().equalsIgnoreCase("") && indcpo.trim().length() > 0) oDeydelsol.indcpo = Integer.parseInt(indcpo);
                    refcat = getCellValue(fila,21);
                    if (refcat != null) oDeydelsol.refcat = refcat;
                    refvaltecnic = getCellValue(fila,29);     
                    if (refvaltecnic != null && !refvaltecnic.trim().equalsIgnoreCase("") && refvaltecnic.trim().length() > 0) 
                    {
                        oDeydelsol.refvaltecnic = Integer.parseInt(refvaltecnic);                
                        if (!oDeydelsol.load(oDeydelsol.refvaltecnic, conexion))
                        {
                            if (oDeydelsol.insert(conexion) == 1)
                            {
                                logger.info("Cargada referencia Valtecnic: "+oDeydelsol.refvaltecnic);
                                buenos ++;
                                conexion.commit();
                            }
                            else
                            {
                                logger.info("No se puede cargar referencia Valtecnic: "+oDeydelsol.refvaltecnic);
                                malos ++;
                                conexion.rollback();
                            }
                        }
                        else
                        {
                            logger.info("referencia Valtecnic duplicada: "+oDeydelsol.refvaltecnic);
                            repetidos ++;
                        }
                    }
                }//try carga del excel
                catch (Exception e)
                {
                    logger.error("Excepción en la carga del registro nº: "+fila+". Descripcion: "+e.toString());
                    malos ++;
                }
            }
            conexion.close();
           
        }
        catch (Exception e)
        {
            logger.error("Error general en la carga de tasaciones .Descripción: "+e.toString());
        }
        finally
        {
            logger.info("Total registros: "+total);
            logger.info("Total registros buenos: "+buenos);
            logger.info("Total registros malos: "+malos);
            logger.info("Total registros repetidos: "+repetidos);
            
            try
            {
                if (conexion != null && !conexion.isClosed()) conexion.close();                
            }
            catch (java.sql.SQLException sqlException)
            {     
                logger.error("Imposible cerrar conexión con la B.D"+sqlException.toString());
            }             
        }//finally     
        
    }//   
      
    private void cargaDeyDeMer()
    {
        java.sql.Connection conexion = null;
        int total = 0;
        int buenos = 0;
        int malos = 0;
        int repetidos = 0;
        int perdidos = 0;
        
        String sUrlExcel = "/data/informes/cargatasaciones/deyde/deydemer280001-resto.xls";        
        //FICHERO LOG4J
        PropertyConfigurator.configure("/data/informes/cargatasaciones/deyde/" + "Log4j.properties");   
        
        try
        {
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:informatica/infx0329@oraserver02:1521:rvtnprod");
            conexion.setAutoCommit(false);
               
            String cposta = null;
            String cpobla = null;
            String lmunici = null;
            String seccion = null;
            String indica = null;
            String indpob = null;
            String indcpo = null;
            String refcat = null;
            String codigo = null;
            String provincia = null;
            String calle = null;
            String numero = null;
            String planta = null;
            String supcons = null;
            Integer iSupcons = null;
            
            Objetos.Deydemer oDeydelmer = new Objetos.Deydemer();
            
            oExcel = new Utilidades.Excel(sUrlExcel);  
            hoja = "Hoja1";
            finicio = 1;
            ffin = 35029;    
            String [] codigos = null;
            boolean b = false;
            int contador = 0;
            
            for (int fila = finicio; fila < ffin;fila ++)              
            {
                 
                try
                {
                    logger.info ("Fila actual: "+fila+"Codigo actual: "+getCellValue(fila,28));                    
                    cposta = null;
                    cpobla = null;
                    lmunici = null;
                    seccion = null;
                    indica = null;
                    indpob = null;
                    indcpo = null;
                    refcat = null;                    
                    codigo = null;
                    provincia = null;
                    calle = null;
                    numero = null;
                    planta = null;
                    supcons = null;
                    
                    oDeydelmer.clear();
                    total ++;
                    
                     provincia = getCellValue(fila,30);
                     calle = getCellValue(fila,35);
                     numero = getCellValue(fila,36);
                     planta = getCellValue(fila,37);
                     supcons = getCellValue(fila,38); 
                     if (supcons != null && !supcons.trim().equalsIgnoreCase("")) iSupcons = Integer.parseInt(supcons);
                     
                    cposta = getCellValue(fila,6);
                    if (cposta != null && !cposta.trim().equalsIgnoreCase("") && cposta.trim().length() > 0) oDeydelmer.cposta = Integer.parseInt(cposta);
                    cpobla = getCellValue(fila,7);
                    if (cpobla != null && !cpobla.trim().equalsIgnoreCase("") && cpobla.trim().length() > 0) oDeydelmer.cpobla = Utilidades.Cadenas.procesarComillasConsulta(cpobla);
                    lmunici = getCellValue(fila,9);
                    if (lmunici != null && !lmunici.trim().equalsIgnoreCase("") && lmunici.trim().length() > 0) oDeydelmer.lmunici = Utilidades.Cadenas.procesarComillasConsulta(lmunici);
                    seccion = getCellValue(fila,11);
                    if (seccion != null && !seccion.trim().equalsIgnoreCase("") && seccion.trim().length() > 0) oDeydelmer.seccion = Long.parseLong(seccion);
                    indica = getCellValue(fila,12);
                    if (indica != null) oDeydelmer.indica = indica;
                    indpob = getCellValue(fila,14);
                    if (indpob != null && !indpob.trim().equalsIgnoreCase("") && indpob.trim().length() > 0) oDeydelmer.indpob = Integer.parseInt(indpob);
                    indcpo = getCellValue(fila,16);
                    if (indcpo != null && !indcpo.trim().equalsIgnoreCase("") && indcpo.trim().length() > 0) oDeydelmer.indcpo = Integer.parseInt(indcpo);
                    refcat = getCellValue(fila,20);
                    if (refcat != null) oDeydelmer.refcat = refcat;
                    //
                        if (fila == 397)
                        {
                          //System.out.println("parrar");
                                  
                        }
                        if (Objetos.Mercabs.getCount(provincia, calle, numero, planta, iSupcons, conexion) > 1)
                        {                            
                            codigos = Objetos.Mercabs.getCodigos(provincia, calle, numero, planta, iSupcons, conexion);                            
                            if (codigos != null)
                            {
                                logger.info("Entra a recorrer el array de codigos");
                                for (String elcodigo:codigos)
                                {
                                    if (elcodigo != null && !elcodigo.trim().equalsIgnoreCase("") && !Objetos.Deydemer.exists(Integer.parseInt(elcodigo), conexion))
                                    {
                                        codigo = elcodigo;
                                        logger.info("Muestra duplicada. Fila: "+fila+". Se asigna codigo de mercabs: "+codigo);
                                        break;
                                    }
                                }
                            }
                            if (codigo == null || codigo.equals("")) 
                            {
                                codigo = getCellValue(fila,28); 
                                logger.info("Se asigna el codigo del excel y la muestra está duplicada. Fila: "+fila+"Codigo asignado del excel: "+codigo);
                            }
                        }
                        else
                        {
                            codigo = Objetos.Mercabs.getCodigo(provincia, calle, numero, planta, iSupcons, conexion);                            
                            logger.info ("Se asigna codigo de mercabs: "+codigo);
                        }
                                          
                    if (codigo != null && !codigo.trim().equalsIgnoreCase("") && codigo.trim().length() > 0) 
                    {
                        oDeydelmer.codigo = Integer.parseInt(codigo);                
                        if (!oDeydelmer.load(oDeydelmer.codigo, conexion))
                        {
                            if (oDeydelmer.insert(conexion) == 1)
                            {
                                logger.info("Cargada referencia Valtecnic: "+oDeydelmer.codigo);
                                buenos ++;
                                conexion.commit();
                            }
                            else
                            {
                                logger.error("No se puede insertar referencia Valtecnic: "+oDeydelmer.codigo);
                                malos ++;
                                conexion.rollback();
                            }
                        }
                        else
                        {
                            logger.error("referencia Valtecnic duplicada: "+oDeydelmer.codigo);
                            repetidos ++;
                        }
                    }
                    else
                    {
                        logger.error("No se asigna codigo para la fila: "+fila);
                        perdidos ++;
                    }
                }//try carga del excel
                catch (Exception e)
                {
                    logger.error("Excepción en la carga del registro nº: "+fila+". Descripcion: "+e.toString());
                    malos ++;
                }
            }
            conexion.close();
           
        }
        catch (Exception e)
        {
            logger.error("Error general en la carga de deydelmer .Descripción: "+e.toString());
        }
        finally
        {
            logger.info("Total registros: "+total);
            logger.info("Total registros buenos: "+buenos);
            logger.info("Total registros malos: "+malos);
            logger.info("Total registros repetidos: "+repetidos);
            logger.info("Total registros perdidos: "+perdidos);
            
            try
            {
                if (conexion != null && !conexion.isClosed()) conexion.close();                
            }
            catch (java.sql.SQLException sqlException)
            {     
                logger.error("Imposible cerrar conexión con la B.D"+sqlException.toString());
            }             
        }//finally             
    }//   
    
     private void cargaDeyDeMerLost()
    {
        java.sql.Connection conexion = null;
        int total = 0;
        int buenos = 0;
        int malos = 0;
        int repetidos = 0;
        int perdidos = 0;
        
        //String sUrlExcel = "/data/informes/cargatasaciones/deyde/deydemer1-60000.xls";        
        //String sUrlExcel = "/data/informes/cargatasaciones/deyde/deydemer60001-120000.xls";        
        //String sUrlExcel = "/data/informes/cargatasaciones/deyde/deydemer120001-180000.xls";        
        //String sUrlExcel = "/data/informes/cargatasaciones/deyde/deydemer180001-200000.xls";        
        //String sUrlExcel = "/data/informes/cargatasaciones/deyde/deydemer200001-260000.xls";        
        //String sUrlExcel = "/data/informes/cargatasaciones/deyde6/deydemer260001-280000.xls";        
        String sUrlExcel = "/data/informes/cargatasaciones/deyde7/deydemer280001-resto.xls";        
        //FICHERO LOG4J
        PropertyConfigurator.configure("/data/informes/cargatasaciones/deyde7/" + "Log4j.properties");   
        
        try
        {
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:informatica/infx0329@oraserver02:1521:rvtnprod");
            conexion.setAutoCommit(false);
               
            String cposta = null;
            String cpobla = null;
            String lmunici = null;
            String seccion = null;
            String indica = null;
            String indpob = null;
            String indcpo = null;
            String refcat = null;
            String codigo = null;
            String provincia = null;
            String calle = null;
            String numero = null;
            String planta = null;
            String supcons = null;
            Integer iSupcons = null;
            Integer iCodigo = null;
            
            Objetos.Deydemer oDeydelmer = new Objetos.Deydemer();
            logger.info("Fichero: "+sUrlExcel);
            oExcel = new Utilidades.Excel(sUrlExcel);  
            hoja = "Hoja1";
            finicio = 1;
            //ffin = 60000;    
            //ffin = 60001;    
            //ffin = 60001;    
            //ffin = 20001;    
            //ffin = 60001;    
            //ffin = 20001;    
            ffin = 35029;    
            
            String [] codigos = null;
            boolean b = false;
            int contador = 0;
            
            for (int fila = finicio; fila < ffin;fila ++)              
            {
                 
                try
                {
                    logger.info ("Fila actual: "+fila+"Codigo actual: "+getCellValue(fila,28));                    
                    cposta = null;
                    cpobla = null;
                    lmunici = null;
                    seccion = null;
                    indica = null;
                    indpob = null;
                    indcpo = null;
                    refcat = null;                    
                    codigo = null;
                    iCodigo = null;
                    provincia = null;
                    calle = null;
                    numero = null;
                    planta = null;
                    supcons = null;
                    
                    oDeydelmer.clear();
                    total ++;
                    
                     provincia = getCellValue(fila,30);
                     calle = getCellValue(fila,35);
                     numero = getCellValue(fila,36);
                     planta = getCellValue(fila,37);
                     supcons = getCellValue(fila,38); 
                     if (supcons != null && !supcons.trim().equalsIgnoreCase("")) iSupcons = Integer.parseInt(supcons);
                     
                    cposta = getCellValue(fila,6);
                    if (cposta != null && !cposta.trim().equalsIgnoreCase("") && cposta.trim().length() > 0) oDeydelmer.cposta = Integer.parseInt(cposta);
                    cpobla = getCellValue(fila,7);
                    if (cpobla != null && !cpobla.trim().equalsIgnoreCase("") && cpobla.trim().length() > 0) oDeydelmer.cpobla = Utilidades.Cadenas.procesarComillasConsulta(cpobla);
                    lmunici = getCellValue(fila,9);
                    if (lmunici != null && !lmunici.trim().equalsIgnoreCase("") && lmunici.trim().length() > 0) oDeydelmer.lmunici = Utilidades.Cadenas.procesarComillasConsulta(lmunici);
                    seccion = getCellValue(fila,11);
                    if (seccion != null && !seccion.trim().equalsIgnoreCase("") && seccion.trim().length() > 0) oDeydelmer.seccion = Long.parseLong(seccion);
                    indica = getCellValue(fila,12);
                    if (indica != null) oDeydelmer.indica = indica;
                    indpob = getCellValue(fila,14);
                    if (indpob != null && !indpob.trim().equalsIgnoreCase("") && indpob.trim().length() > 0) oDeydelmer.indpob = Integer.parseInt(indpob);
                    indcpo = getCellValue(fila,16);
                    if (indcpo != null && !indcpo.trim().equalsIgnoreCase("") && indcpo.trim().length() > 0) oDeydelmer.indcpo = Integer.parseInt(indcpo);
                    refcat = getCellValue(fila,20);
                    if (refcat != null) oDeydelmer.refcat = refcat;
                    //
                        
                    iCodigo = Objetos.Mercabs.getCodigoDontExistsDeydemer(provincia, calle, numero,planta, iSupcons, conexion);
                    if (iCodigo == null)
                    {
                        codigo = Objetos.Mercabs.getCodigo(provincia, calle, numero, planta, iSupcons, conexion);   //todo                         
                        if (codigo == null || codigo.equals(""))
                        {                        
                            iCodigo = Objetos.Mercabs.getCodigoDontExistsDeydemer(provincia, calle, planta, iSupcons, conexion); //sin numero
                            if (iCodigo == null) 
                            {
                                iCodigo = Objetos.Mercabs.getCodigoDontExistsDeydemer(provincia, calle, iSupcons, conexion); //sin numero y planta
                                if (iCodigo == null) 
                                {
                                    iCodigo = Objetos.Mercabs.getCodigoDontExistsDeydemer(provincia, calle, conexion); //solo calle
                                    if (iCodigo != null) 
                                    {
                                        codigo = iCodigo.toString();
                                        logger.info("Codigo por dir. solo calle: "+codigo);
                                    }
                                    else
                                    {
                                       iCodigo = Objetos.Mercabs.getCodigoDontExistsDeydemerWithoutCalle(provincia,numero,planta,iSupcons,conexion) ;
                                       if (iCodigo != null) 
                                       {
                                            codigo = iCodigo.toString();
                                            logger.info("Codigo por dir. sin calle: "+codigo); 
                                       }
                                       else  codigo = null; 
                                    }
                                        
                                }
                                else
                                {
                                    codigo = iCodigo.toString();
                                    logger.info("Codigo por dir. sin numero y planta.: "+codigo);
                                }
                            }
                            else
                            {
                                codigo = iCodigo.toString();
                                logger.info("Codigo por dir. sin numero.: "+codigo);
                            }                        
                        }
                        else logger.info("Codigo por dir. completa.: "+codigo);
                    }
                    else
                    {
                        codigo = iCodigo.toString();
                        logger.info("Codigo por dir. completa y da registros identicos: "+codigo);
                    }
                        
                                          
                    if (codigo != null && !codigo.trim().equalsIgnoreCase("") && codigo.trim().length() > 0) 
                    {
                        oDeydelmer.codigo = Integer.parseInt(codigo);                
                        if (!oDeydelmer.load(oDeydelmer.codigo, conexion))
                        {
                            if (oDeydelmer.insert(conexion) == 1)
                            {
                                logger.info("Cargada referencia Valtecnic: "+oDeydelmer.codigo);
                                buenos ++;
                                conexion.commit();
                                
                            }
                            else
                            {
                                logger.error("No se puede insertar referencia Valtecnic: "+oDeydelmer.codigo);
                                malos ++;
                                conexion.rollback();
                            }
                        }
                        else
                        {
                            logger.error("referencia Valtecnic duplicada: "+oDeydelmer.codigo);
                            repetidos ++;
                        }
                    }
                    else
                    {
                        logger.error("No se asigna codigo para la fila: "+fila);
                        perdidos ++;
                    }
                }//try carga del excel
                catch (Exception e)
                {
                    logger.error("Excepción en la carga del registro nº: "+fila+". Descripcion: "+e.toString());
                    malos ++;
                }
            }
            conexion.close();
           
        }
        catch (Exception e)
        {
            logger.error("Error general en la carga de deydelmer .Descripción: "+e.toString());
        }
        finally
        {
            logger.info("Total registros: "+total);
            logger.info("Total registros buenos: "+buenos);
            logger.info("Total registros malos: "+malos);
            logger.info("Total registros repetidos: "+repetidos);
            logger.info("Total registros perdidos: "+perdidos);
            
            try
            {
                if (conexion != null && !conexion.isClosed()) conexion.close();                
            }
            catch (java.sql.SQLException sqlException)
            {     
                logger.error("Imposible cerrar conexión con la B.D"+sqlException.toString());
            }             
        }//finally             
    }//   
    
    private void cargaOficinas() 
    {
        String sUrlExcel = "/data/informes/cargatasaciones/oficinas/oficinas_abanca.xls";        
        java.sql.Connection conexion = null;
        
        String codcli = null;
        String denomina = null;
        String oficina = null;
        String direccion = null;
        String codpos = null;
        String provincia = null;
        String telefono = null;
        Integer leidos = 0;
        Integer insertados = 0;
        Integer noInsertados = 0;
        Objetos.Clidir oClidir = null;
        
        try
        {
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@oraserver02:1521:rvtnprod");
            conexion.setAutoCommit(false);
               
                                   
            oExcel = new Utilidades.Excel(sUrlExcel);  
            hoja = "Hoja1";
            finicio = 2;          
            ffin = 402;    
                                 
            for (int fila = finicio; fila < ffin;fila ++)              
            {                
                try
                {
                    leidos ++;
                    oClidir = new Objetos.Clidir();
                    codcli = null;
                    denomina = null;
                    oficina = null;
                    direccion = null;
                    codpos = null;
                    provincia = null;
                    telefono = null;
                    
                     codcli = getCellValue(fila,0);
                     if (codcli != null) oClidir.codcli = Integer.parseInt(codcli);
                     denomina = getCellValue(fila,1);
                     if (denomina != null) oClidir.denomina = denomina.trim().toUpperCase();
                     oficina = getCellValue(fila,2);
                     if (oficina != null) oClidir.oficina = Utilidades.Cadenas.completTextWithLeftCharacter(oficina, '0', 4);
                     direccion = getCellValue(fila,3);
                     if (direccion != null) oClidir.direcc = direccion.trim().toUpperCase();
                     codpos = getCellValue(fila,4);
                     if (codpos != null && !codpos.equalsIgnoreCase("")) oClidir.codpos = Integer.parseInt(codpos);
                     provincia = getCellValue(fila,5);
                     if (provincia != null) oClidir.provin = provincia.trim().toUpperCase();
                     telefono = getCellValue(fila,9);
                     if (telefono != null && !telefono.equalsIgnoreCase("")) oClidir.telefo = telefono.trim().toUpperCase();
                     
                     if (oClidir.insert(conexion) == 1)
                     {
                         conexion.commit();                         
                         insertados ++;
                     }
                     else 
                     {
                         conexion.rollback();
                         noInsertados ++;
                     }                                          
                }//try carga del excel
                catch (Exception e)
                {
                    noInsertados ++;
                    System.out.println("Excepcion oficina: "+oficina+". Motivo: "+e.toString());
                }
            }//for
            conexion.close();           
        }//try
        catch (Exception e)
        {
            
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
                
            } 
            System.out.println("Leidos: "+leidos);
            System.out.println("Insertados: "+insertados);
            System.out.println("No Insertados: "+noInsertados);
        }//finally        
    }
     
}
