/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package herramientas;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Ana Belen de Frutos
 */
public class cargaUnidades 
{
    private  Connection conexion = null;    
    private  String sRutaPropiedades = "/data/informes/cargaUnidades/propiedades/cargaUnidades.properties";
    private Utilidades.Propiedades propiedades = null;
     private String rutaLog = "/data/informes/cargaUnidades/log/";
    //LOG
    public  Logger logger = Logger.getLogger(cargaUnidades.class);
    
    //EXCEL
    public  String sUrlExcel = "";
    public  Utilidades.Excel oExcel = null;            
    public  org.apache.poi.hssf.usermodel.HSSFCell celda = null;
    public  String HojaActual = "Unidad";
    
    //objetos a cargar
    private Utilidades.Resultado oResultado = null;
    private Objetos.v2.Solicitudes oSolicitudes = new Objetos.v2.Solicitudes();
    private Objetos.unidades.Suelo oSuelo =  new Objetos.unidades.Suelo();
    private Objetos.unidades.Edificios oEdificios = new Objetos.unidades.Edificios();
    private Objetos.unidades.Elementos oElementos = new Objetos.unidades.Elementos();
    private Objetos.unidades.Superficies oSuperficies = new Objetos.unidades.Superficies();
    private Objetos.unidades.Unitarios oUnitarios = new Objetos.unidades.Unitarios();
    private Objetos.unidades.Vtotales oVtotales = new Objetos.unidades.Vtotales();
    private Objetos.unidades.Vtotalestas oVtotalestas = new Objetos.unidades.Vtotalestas();
    private Objetos.Datosreg oDatosreg = new Objetos.Datosreg();
    private Objetos.Catastro oCatastro = new Objetos.Catastro();
    
    //
    Double valorD = null;
    Integer valorI = null;
    String valorS = null;
    
    public String descError = "";
    public Boolean estadoOK = true;
    
    java.util.List<String> expedientesSareb = new java.util.ArrayList();
    
    public static void main(String[] args) 
    {        
        //cargaUnidades carga = new cargaUnidades("",null,null,null,null,null,null);//para vuelco de nuevos datos técnicos
        cargaUnidades carga = new cargaUnidades("ADVALGRP_20170621_00000000000000179124",250,"/data/informes/cargaUnidades/Unidad.xls",null,false,null,null);//para caixa
        //cargaUnidades carga = new cargaUnidades("548.VLT17A000100752185 A",155,"/data/informes/cargaUnidades/Unidad.xls",1803508,false,null,null);//para caixa
        //cargaUnidades carga = new cargaUnidades("500.TS2015004723-001-15",600,null,null,null,null,null);//para segipsa
        //cargaUnidades carga = new cargaUnidades();
        carga = null;        
        System.gc();
    }
    public cargaUnidades()
    {
        try
        {
            java.io.File fichero = new java.io.File(sRutaPropiedades);
            if (fichero.exists())
            {
                propiedades = new Utilidades.Propiedades(fichero.getAbsolutePath());      
                //FICHERO LOG4J
                PropertyConfigurator.configure(rutaLog + "Log4j.properties");      
            }
            else
            {                        
                System.out.println("No se ha encontrado el fichero de propiedades en la ruta: "+sRutaPropiedades.trim());            
            } 
            recalcularValores("555.962029552723296-14");
        }
        catch (Exception e)
        {
            
        }
     
    }
    
    public cargaUnidades (String pnumexp,Integer cliente,String rutaCompleta,Integer idEdificio, Boolean enConstruccion, Logger log, Connection conex)
    {
        String numexp = null;
        boolean estadok = true;
        int procesados = 0;
        Integer totales = 0;
        int filaI = 0;
        int filaF = 0;
        int fila = 0;
        boolean esSareb = false;        
        Utilidades.Resultado oResultado = null;        
        Document xmlDoc = null;
        boolean cerrarConexion = false;
        
        Integer ivalor = null;
        
        try
        {
            descError = "";
            estadoOK = true;
            esSareb = Utilidades.Constantes.esSareb(cliente);            
            //cargamos el fichero de propiedades.
            if (log == null && conex == null)
            {
                java.io.File fichero = new java.io.File(sRutaPropiedades);
                if (fichero.exists())
                {
                    propiedades = new Utilidades.Propiedades(fichero.getAbsolutePath());      
                    //FICHERO LOG4J
                    PropertyConfigurator.configure(rutaLog + "Log4j.properties");      
                }
                else
                {
                    estadok = false;                
                    System.out.println("No se ha encontrado el fichero de propiedades en la ruta: "+sRutaPropiedades.trim());            
                } 
            }
            if (cliente == null)
            {
                try
                {
                    conexion = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"),propiedades.getValueProperty("IfxLogin"),propiedades.getValueProperty("IfxPass"));
                    conexion.setAutoCommit(false);      
                    String origen = "2553 0715 152103";
                    String destino = "2554 1009 201702";
                    //Objetos.unidades.Comunes.copy(origen, destino, conexion);
                }
                catch (Exception e)
                {
                    
                }
                finally
                {
                    conexion.close();
                }
                
            }
            else
            {
            if (estadok && (cliente == 155 || Utilidades.Constantes.esSareb(cliente)))
            {//En el caso de Sareb el numexp pasado como parametro será el idgrupoexpedientes. En este caso:
             //1.- el expediente donde insertar las unidades vendrá especificado en la columna 0
             //2.- para cada uno de estos expedientes hay que comprobar que pertenece a ese iddgrupoexpedientes.
             //3.- si el idEdificio es nulo creamos uno nuevo y sobre el cargamos los elementos.   
                
                if (conex == null)
                {
                    conexion = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"),propiedades.getValueProperty("IfxLogin"),propiedades.getValueProperty("IfxPass"));
                    conexion.setAutoCommit(false);
                    cerrarConexion = true;
                }
                else conexion = conex;
                sUrlExcel = rutaCompleta;
                HojaActual = "Unidad";
                oExcel = new Utilidades.Excel(sUrlExcel);
                if (log != null) logger = log;
                //idEdificio = idEdificio;
                filaI = 4;
                filaF = oExcel.getMaximaFila(HojaActual) + 1;                
                if (!esSareb) logger.info("Iniciada carga de datos para el expediente: "+pnumexp);
                expedientesSareb.clear();
                for (fila = filaI; fila < filaF; fila ++)
                {  
                    //System.out.println ("Elemento: "+fila);
                    totales ++;
                    //Utilidades.Resultado resut = oElementos.insert(conexion);
                    //oElementos.id_elmto = resut.getIntegerResultado();
                    ivalor = null;
                    oResultado = null;
                    oSolicitudes.clear();
                    oSuelo.clear();
                    oEdificios.clear();
                    oElementos.clear();
                    oSuperficies.clear();
                    oUnitarios.clear();
                    oVtotales.clear();
                    oVtotalestas.clear();
                    oDatosreg.clear();
                    oCatastro.clear();
                                        
                    if (esSareb)
                    {
                        dameValorExcel(fila,0,"S");   //numero de expediente
                        if (valorS != null)
                        {
                            if (esActivoDeGrupo(pnumexp,valorS)) numexp = valorS;
                            else numexp = null;
                        }
                        else numexp = null;
                    }
                    else numexp = pnumexp;
                    if (numexp != null)                                                            
                    {//elementos
                        oSolicitudes.load(numexp, conexion);
                        if (esSareb) logger.info("Iniciada carga de datos para el expediente: "+numexp);
                        
                        oElementos.numexp = numexp;                        
                        if (esSareb)
                        {//vemos los elementos que ya hay insertados en ese expediente.
                            ivalor = numElementosByExpediente(numexp);
                            if (ivalor != null) 
                            {
                                ivalor ++;
                                oElementos.refunidad = ivalor.toString();
                            }
                        }
                        else oElementos.refunidad = totales.toString(); 
                        if (esSareb)
                        {//actualizamos los datos registrales.
                            dameValorExcel(fila,9,"S");   //finca registral
                            if (valorS != null)
                            {
                                if (oDatosreg.load(numexp, valorS, conexion))
                                {
                                    if (oCatastro.load(numexp, oDatosreg.numero, conexion))
                                    {//aqui hemos cargado datosregistrales y caltastro.
                                        dameValorExcel(fila,8,"S"); //idufir
                                        if (valorS != null && !valorS.equalsIgnoreCase("")) oDatosreg.idufir = Long.parseLong(valorS.trim());
                                        dameValorExcel(fila,10,"S"); //seccion
                                        if (valorS != null && !valorS.equalsIgnoreCase("")) oDatosreg.seccion = Integer.parseInt(valorS.trim());
                                        dameValorExcel(fila,11,"S"); //tomo
                                        if (valorS != null && !valorS.equalsIgnoreCase("")) oDatosreg.tomo = valorS.trim();
                                        dameValorExcel(fila,12,"S"); //libro
                                        if (valorS != null && !valorS.equalsIgnoreCase("")) oDatosreg.libro = valorS.trim();
                                        dameValorExcel(fila,13,"S"); //folio
                                        if (valorS != null && !valorS.equalsIgnoreCase("")) oDatosreg.folio = valorS.trim();
                                        dameValorExcel(fila,17,"S"); //referencia catastral
                                        if (valorS != null && !valorS.equalsIgnoreCase("")) oCatastro.fcatastral = valorS.trim();
                                        oDatosreg.update(conexion);
                                        oCatastro.update(conexion);
                                    }
                                }
                            } 
                        }
                        //superficies
                        oSuperficies.numexp = numexp;
                        //unitarios
                        oUnitarios.numexp = numexp;
                        //totales unidad
                        oVtotales.numexp = numexp;
                        //totalestas
                        //oVtotales.numexp = numexp;
                        dameValorExcel(fila,1,"I");  //valor del edificio
                        if ( (!esSareb && valorI != null) || (esSareb && oElementos.refunidad != null))
                        {
                            if (!esSareb)
                            {
                                if (idEdificio == null) idEdificio = valorI;
                                oElementos.id_edificio = idEdificio; //recibido como param. de entrada.
                            }
                            else
                            {//para sareb si no hay edificio lo creamos. Y si lo hay comprobamos que sea de ese expediente.
                                if (valorI != null && oEdificios.load(numexp, valorI, conexion)) 
                                {//vemos si existe para ese expediente ese edificio
                                    oElementos.id_edificio = valorI;                                    
                                }
                                else
                                {//vemos si existe un edificio para ese expediente y si no existe lo crea.
                                    if (!oEdificios.loadFirst(numexp, conexion))
                                    {
                                        oEdificios.clear();
                                        oEdificios.setDefaultValuesIfAreNull();
                                        oEdificios.numexp = numexp;
                                        if (!oSuelo.loadFirst(numexp, conexion))
                                        {//si no hay suelo lo creamos.
                                        oSuelo.clear();
                                        oSuelo.numexp = numexp;
                                        oSuelo.tipoinm = "SOL";
                                        oResultado = oSuelo.insert(conexion);
                                        if (oResultado.getNumeroRegistrosAfectados() != 1) 
                                        {
                                            conexion.rollback();
                                            oSuelo.clear();
                                            logger.error("Imposible insertar nuevo suelo.");
                                        }
                                        else logger.info("Insertado nuevo suelo.");
                                        }
                                            
                                        oEdificios.iden_suelo = oSuelo.iden_suelo;                                        
                                        oEdificios.tipoinm = "XXX";
                                        oEdificios.codpos = oSolicitudes.codpos;
                                        if (oSolicitudes.tipovia != null) oEdificios.tipovia = oSolicitudes.tipovia;
                                        else if (oSolicitudes.codsitu != null) oEdificios.tipovia = oSolicitudes.codsitu;
                                        else oEdificios.tipovia = "";                                    
                                        if (oSolicitudes.calle != null) oEdificios.via = oSolicitudes.calle;
                                        else if (oSolicitudes.situacion != null) oEdificios.via = oSolicitudes.situacion;
                                        else oEdificios.via = "";                                      
                                        if (oSolicitudes.numero != null) oEdificios.numvia = oSolicitudes.numero;        
                                        else oEdificios.numvia = "S/N";
                                        oEdificios.nunidades = 1;
                                        oResultado = oEdificios.insert(conexion);
                                        if (oResultado.getNumeroRegistrosAfectados() != 1) 
                                        {
                                            conexion.rollback();
                                            oEdificios.id_edificio = null;
                                            logger.error("Imposible insertar nuevo edificio.");
                                        }
                                        else
                                        {                                        
                                            oElementos.id_edificio = oEdificios.id_edificio;
                                            logger.info("Insertado nuevo edificio.");
                                        }
                                    }
                                    else oElementos.id_edificio = oEdificios.id_edificio;
                                }
                            }                                                                                    
                            if (oElementos.id_edificio != null)
                            {
                                dameValorExcel(fila,9,"S");                    
                                oElementos.num_dr = getNumdr(numexp,valorS);                            
                                dameValorExcel(fila,2,"I");                    
                                oElementos.idgrupotestigos = valorI;
                                dameValorExcel(fila,3,"I");                                        
                                oElementos.idfase = valorI;
                                dameValorExcel(fila,4,"I");                    
                                oElementos.id_agrupa = valorI;
                                dameValorExcel(fila,5,"I");
                                oUnitarios.obraparada = valorI; ///////////////////// nuevos 30012015 ///////////////////
                                dameValorExcel(fila,6,"I");
                                oUnitarios.obrarecu = valorI; ///////////////////// nuevos 30012015 ///////////////////  
                                dameValorExcel(fila,7,"D");                    
                                oUnitarios.porcenobra = valorD;
                                dameValorExcel(fila,15,"I");                    
                                oElementos.usortral = valorI;
                                dameValorExcel(fila,16,"I");                    
                                oElementos.usotas = valorI;
                                dameValorExcel(fila,18,"S");                    
                                oElementos.planta = valorS;
                                dameValorExcel(fila,19,"S");
                                if (valorS != null)
                                {
                                    valorS = valorS.trim();
                                    if (valorS.trim().length() > 10) valorS = valorS.substring(0, 10);                    
                                }
                                oElementos.puerta = valorS;
                                dameValorExcel(fila,20,"S");                    
                                oElementos.bloque = valorS;
                                dameValorExcel(fila,21,"S"); 
                                oElementos.escalera = valorS;
                                dameValorExcel(fila,22,"S"); 
                                if (valorS != null)
                                {
                                    valorS = valorS.trim();
                                    if (valorS.trim().length() > 100) valorS = valorS.substring(0, 100);                    
                                }
                                oElementos.masdirecc = valorS;
                                dameValorExcel(fila,23,"I"); 
                                oElementos.tipologia = valorI;    
                                if (esSareb)
                                {
                                    enConstruccion = Funciones.Fcomunes.enConstruccion(oSolicitudes.tipoinm);
                                }
                                if (enConstruccion)
                                {
                                    switch (valorI)
                                    {
                                    case (1): 
                                    {//vivienda div. horizontal
                                        oElementos.tipoinm = "VPC";
                                        break;
                                    }
                                    case (2): 
                                    {//vivienda unifamiliar adosada, pareada
                                        oElementos.tipoinm = "VUC";
                                        break;
                                    }  
                                        case (3): 
                                    {//vivienda unifamiliar aislada
                                        oElementos.tipoinm = "VUC";
                                        break;
                                    }
                                    case (4): 
                                    {//local comercial
                                        oElementos.tipoinm = "LCC";
                                        break;
                                    }
                                    case (5): 
                                    {//oficina
                                        oElementos.tipoinm = "OFC";
                                        break;
                                    }
                                    case (6): 
                                    {//nave industrial
                                        oElementos.tipoinm = "XXX";
                                        break;
                                    }    
                                    case (7): 
                                    {//plaza de parking
                                        oElementos.tipoinm = "PZC";
                                        break;
                                    }
                                    case (8): 
                                    {//trastero
                                        oElementos.tipoinm = "TRC";
                                        break;
                                    }
                                    case (9): 
                                    {//terrazas
                                        oElementos.tipoinm = "TZC";
                                        break;
                                    }
                                    case (10): 
                                    {//anexos generales
                                        oElementos.tipoinm = "ANX";
                                        break;
                                    }
                                    case (11): 
                                    {//Finca rustica
                                        oElementos.tipoinm = "FCR";
                                        break;
                                    }
                                    case (12): 
                                    {//Solar o Terreno
                                        oElementos.tipoinm = "SOL";
                                        break;
                                    }
                                    case (13): 
                                    {//Otros
                                        oElementos.tipoinm = "XXX";
                                        break;
                                    }
                                    case (14): 
                                    {//Hoteles
                                        oElementos.tipoinm = "HTC";
                                        break;
                                    }
                                    default:
                                    {
                                        oElementos.tipoinm = "XXX";
                                        break;
                                    }
                                    }//switch
                                }
                                else
                                {
                                    switch (valorI)
                                    {
                                        case (1): 
                                        {//opciona
                                            oElementos.tipoinm = "VPT";
                                            break;
                                        }
                                        case (2): 
                                        {//opciona
                                            oElementos.tipoinm = "VUT";
                                            break;
                                        }  
                                            case (3): 
                                        {//opciona
                                            oElementos.tipoinm = "VUT";
                                            break;
                                        }
                                        case (4): 
                                        {//opciona
                                            oElementos.tipoinm = "LCT";
                                            break;
                                        }
                                        case (5): 
                                        {//opciona
                                            oElementos.tipoinm = "OFT";
                                            break;
                                        }
                                        case (6): 
                                        {//opciona
                                            oElementos.tipoinm = "XXX";
                                            break;
                                        }    
                                        case (7): 
                                        {//opciona
                                            oElementos.tipoinm = "PZT";
                                            break;
                                        }
                                        case (8): 
                                        {//opciona
                                            oElementos.tipoinm = "TRT";
                                            break;
                                        }
                                        case (9): 
                                        {//opciona
                                            oElementos.tipoinm = "TRZ";
                                            break;
                                        }
                                        case (10): 
                                        {//opciona
                                            oElementos.tipoinm = "XXX";
                                            break;
                                        }
                                        case (11): 
                                        {//opciona
                                            oElementos.tipoinm = "FCR";
                                            break;
                                        }
                                        case (12): 
                                        {//opciona
                                            oElementos.tipoinm = "SOL";
                                            break;
                                        }
                                        case (13): 
                                        {//opciona
                                            oElementos.tipoinm = "XXX";
                                            break;
                                        }
                                        case (14): 
                                        {//Hoteles
                                            oElementos.tipoinm = "HTT";
                                            break;
                                        }
                                        default:
                                        {
                                            oElementos.tipoinm = "XXX";
                                            break;
                                        }    
                                    }
                                }
                                dameValorExcel(fila,24,"I"); 
                                oElementos.calglo = valorI;
                                dameValorExcel(fila,25,"I"); 
                                oElementos.estint = valorI;
                                dameValorExcel(fila,26,"I");
                                oElementos.numdorm = valorI;
                                dameValorExcel(fila,27,"I");
                                oElementos.numbanos = valorI;
                                dameValorExcel(fila,28,"I");
                                oElementos.ocuantes = valorI;
                                dameValorExcel(fila,29,"I");
                                oElementos.ocupahora = valorI;
                                dameValorExcel(fila,30,"I");
                                oElementos.divisibilidad = valorI;
                                dameValorExcel(fila,31,"I"); //posicion en el edificio
                                oElementos.intext = valorI;
                                dameValorExcel(fila,32,"I");
                                oElementos.aspamb = valorI;
                                dameValorExcel(fila,33,"I");
                                oElementos.orientan = valorI;
                                dameValorExcel(fila,34,"I");
                                oElementos.orientas = valorI;
                                dameValorExcel(fila,35,"I");
                                oElementos.orientae = valorI;
                                dameValorExcel(fila,36,"I");
                                oElementos.orientaw = valorI;                                                
                                dameValorExcel(fila,37,"I");
                                oElementos.distribuc = valorI;
                                dameValorExcel(fila,38,"I");
                                oElementos.accesib = valorI;
                                dameValorExcel(fila,39,"I");
                                oElementos.polivalte = valorI;  
                                dameValorExcel(fila,40,"I");
                                oElementos.habitaelmto = valorI;
                                dameValorExcel(fila,41,"I");
                                oElementos.consereltmo = valorI;
                                dameValorExcel(fila,42,"I");
                                oElementos.necerefelmt = valorI;
                                dameValorExcel(fila,43,"I");
                                oElementos.factorfma = valorI;
                                dameValorExcel(fila,44,"I");
                                oElementos.intensrfma = valorI;                                                                                                                                                                                                                                                                     
                                dameValorExcel(fila,45,"I");   
                                oElementos.antigelmto = valorI;  //Antiguiedad
                                dameValorExcel(fila,46,"I");
                                oElementos.vidauttot = valorI;  //ultima rehabilitacion
                                dameValorExcel(fila,47,"I");
                                oElementos.vidaelmto = valorI; //vida util total
                                dameValorExcel(fila,48,"I");
                                oElementos.vidaresid = valorI; //vida residual
                                dameValorExcel(fila,49,"D");
                                oElementos.gtoscomun = valorD;  
                                dameValorExcel(fila,50,"I");
                                oElementos.conautnuv = valorI; //construccion autorizada
                                dameValorExcel(fila,51,"I");
                                oElementos.usoautnuv = valorI; //uso autorizada   
                                dameValorExcel(fila,52,"I");
                                oElementos.cedulahab = valorI; //Licencia obra/Cedula habitabilidad/Licencia funcionamiento
                                dameValorExcel(fila,53,"D");
                                oSuperficies.cescritura = valorD;  
                                dameValorExcel(fila,57,"D");
                                oSuperficies.supparcel = valorD;
                                dameValorExcel(fila,58,"D");
                                oSuperficies.suptasinic = valorD;                                                                        
                                dameValorExcel(fila,59,"D");
                                oSuperficies.suputil = valorD;
                                dameValorExcel(fila,60,"D");
                                oSuperficies.constneta = valorD;
                                dameValorExcel(fila,61,"D");
                                oSuperficies.suptrza = valorD;
                                dameValorExcel(fila,62,"D");
                                oSuperficies.cparcom = valorD;                        
                                dameValorExcel(fila,63,"I");
                                oSuperficies.metodosup = valorI;
                                dameValorExcel(fila,64,"D");
                                oSuperficies.alturatot = valorD;
                                dameValorExcel(fila,65,"D");
                                oSuperficies.longff = valorD;
                                dameValorExcel(fila,66,"D");
                                oSuperficies.suppb = valorD;
                                dameValorExcel(fila,67,"D");
                                oSuperficies.supsto = valorD;
                                dameValorExcel(fila,68,"D");
                                oSuperficies.supetpl = valorD;
                                dameValorExcel(fila,69,"D");
                                oSuperficies.supmaxlegal = valorD;
                                dameValorExcel(fila,70,"D");
                                oSuperficies.suprenta = valorD;
                                dameValorExcel(fila,71,"D");
                                oSuperficies.otrasup = valorD;
                                dameValorExcel(fila,72,"D");
                                oUnitarios.repactual = valorD;
                                dameValorExcel(fila,73,"D");
                                oUnitarios.repfinal = valorD; 
                                dameValorExcel(fila,74,"D");
                                oUnitarios.benefind = valorD; 
                                dameValorExcel(fila,75,"D");
                                oUnitarios.contractual = valorD; 
                                dameValorExcel(fila,76,"D");
                                oUnitarios.contrafinal = valorD; 
                                dameValorExcel(fila,77,"D");
                                oUnitarios.porchonor = valorD; 
                                dameValorExcel(fila,78,"D");
                                oUnitarios.porclicen = valorD;
                                dameValorExcel(fila,79,"D");
                                oUnitarios.porcgtos = valorD;                        
                                dameValorExcel(fila,80,"D");
                                oUnitarios.porcdire = valorD;
                                dameValorExcel(fila,81,"I");
                                oUnitarios.tmerccons = valorI;
                                dameValorExcel(fila,82,"D");
                                oUnitarios.valrmeract = valorD;
                                dameValorExcel(fila,83,"D");
                                oUnitarios.valrmerfi = valorD;
                                dameValorExcel(fila,84,"D");                        
                                oUnitarios.coefke = valorD;                                                
                                dameValorExcel(fila,85,"D");
                                oUnitarios.ajuste= valorD;
                                dameValorExcel(fila,86,"D");                        
                                oUnitarios.valrentameract = valorD;
                                dameValorExcel(fila,87,"D");                        
                                oUnitarios.valrentamerfin = valorD;
                                dameValorExcel(fila,88,"D");                        
                                oUnitarios.valrentac = valorD;
                                dameValorExcel(fila,89,"D");                        
                                oUnitarios.valrentfi = valorD;
                                dameValorExcel(fila,90,"D");                        
                                oUnitarios.valrentaexpact = valorD;
                                dameValorExcel(fila,91,"D");                        
                                oUnitarios.valrentaexpfin = valorD;
                                dameValorExcel(fila,92,"D");                        
                                oUnitarios.valmaxlac = valorD;                                                
                                dameValorExcel(fila,93,"D");
                                oUnitarios.valmaxlfi = valorD;                        
                                dameValorExcel(fila,94,"D");
                                oUnitarios.pcpolival = valorD;
                                dameValorExcel(fila,95,"D");
                                oUnitarios.ctelmtopoli = valorD;                        
                                dameValorExcel(fila,96,"D");
                                oUnitarios.vcompgt = valorD;
                                dameValorExcel(fila,97,"D");
                                oVtotales.valseg = valorD;
                                dameValorExcel(fila,98,"D");
                                oVtotales.valsegcli = valorD;
                                dameValorExcel(fila,99,"D");
                                oVtotales.vdchoreal = valorD;                                                                        
                                dameValorExcel(fila,100,"D");                    
                                oVtotales.votrmetod = valorD;                        
                                dameValorExcel(fila,101,"D");                    
                                oVtotales.votrmetodfin = valorD;                        
                                dameValorExcel(fila,102,"I");                    
                                oVtotales.metodo = valorI;
                                dameValorExcel(fila,103,"I");                    
                                oVtotales.metodofin = valorI;                        
                                dameValorExcel(fila,104,"I");                    
                                oVtotales.metodoslo = valorI;                        

                                oResultado = oElementos.insert(conexion);
                                if (oResultado.getNumeroRegistrosAfectados() == 1)                
                                {

                                    logger.info("Insertado en elementos fila numero: "+fila);
                                    oSuperficies.idnumero = oElementos.id_elmto;
                                    if (oSuperficies.insert(conexion) == 1)
                                    {
                                        logger.info("Insertado en superficies fila numero: "+fila);
                                        oUnitarios.idnumero = oElementos.id_elmto;
                                        if (oUnitarios.insert(conexion) == 1)
                                        {
                                            logger.info("Insertado en unitarios fila numero: "+fila);
                                            oVtotales.idnumero = oElementos.id_elmto;
                                            if (oVtotales.insert(conexion) == 1)
                                            {
                                                if (esSareb) 
                                                {
                                                    conexion.commit();                                                     
                                                    expedientesSareb.add(numexp);
                                                }
                                                procesados ++;
                                                logger.info("Insertado en totales fila numero: "+fila);
                                            }
                                            else
                                            {
                                                if (esSareb) conexion.rollback();
                                                logger.error("No se inserta en totales fila numero: "+fila);
                                            }
                                        }
                                        else
                                        {
                                            if (esSareb) conexion.rollback();
                                            logger.error("No se inserta en unitarios fila numero: "+fila);
                                        }
                                    }
                                    else 
                                    {
                                        if (esSareb) conexion.rollback();
                                        logger.error("No se inserta en superficies fila numero: "+fila);
                                    }
                                }
                                else
                                {
                                    if (esSareb) conexion.rollback();
                                    logger.error("No se inserta en elementos fila numero: "+fila);
                                }
                            }
                            else
                            {//linea en blanco si no hay id edificio
                                procesados ++;
                            }
                        }
                        else
                        {//linea en blanco si no hay edificio y no es sareb o siendo sareb no se puede numerar el nw. elemento.
                            procesados ++;
                        }
                    }//if numexp != null
                    else 
                    {
                        if (esSareb) procesados ++;  //le damos como procesado. Si el expediente del excel no es del grupo no lo insertamos pero continuamos con el resto.
                    }
                    if (esSareb) logger.info("Finalizada carga de datos para el expediente: "+numexp); 
               }//for
               if (!esSareb) 
               {
                   logger.info("Finalizada carga de datos para el expediente: "+numexp); 
               }
               else
               {
                  
                    logger.info("Se procede a los calculos de los expedientes.");
                    for (String ex:expedientesSareb)
                    {
                        if (Objetos.unidades.calculos.Totales.run_TodosLosCalculos(ex,conexion))
                        {
                                conexion.commit();                                
                                logger.info("Calculos realizados para el expediente: "+ex);
                        }
                        else
                        {
                                conexion.rollback();
                                logger.error("Calculos NO realizados para el expediente: "+ex);
                        }
                    }                   
               }
               if (procesados == totales)
               {
                   if (!esSareb) 
                   {
                       conexion.commit(); 
                       estadoOK = true;                                   
                       logger.info("Unidades cargadas para el expediente: "+numexp);
                       //esto quitarlo cuando se suba a explotacion. Esto es solo para carga manual de tasaciones que no son Sareb.
                       /*
                       if (Objetos.unidades.calculos.Totales.run_TodosLosCalculos(numexp,conexion))
                       {
                                conexion.commit();                                
                                logger.info("Calculos realizados para el expediente: "+numexp);
                       }
                       else
                       {
                                conexion.rollback();
                                logger.error("Calculos NO realizados para el expediente: "+numexp);
                       }
                       */
                   }                   
               }
               else 
               {
                   if (!esSareb) conexion.rollback();
                   descError = "mposible cargar unidades para el expediente: "+numexp+"\n";
                   descError += "Totales: "+totales;
                   descError += "procesados: "+procesados;
                   logger.error(descError);                   
                   estadoOK = false;
                   //System.out.println("NO TODAS CARGADAS");
               }
               oExcel = null;
                              
            }//if 155            
            if (estadok && cliente == 600)
            {//para caixa insertamos en unidades       
                //sUrlExcel = "/data/informes/cargaUnidades/100.TS2014007054-001-14.xls";
                sUrlExcel = "/data/informes/cargaUnidades/"+numexp;
                oExcel = new Utilidades.Excel(sUrlExcel+".xls");  
                HojaActual = "XML";
                filaI = 2;                
                filaF = oExcel.getMaximaFila(HojaActual) + 1;
                File ficheroXML = new java.io.File(sUrlExcel+".xml");                
                Element tasacion = null;
                Element superficies = null;
                Element valores = null;
                Element Datos_Registrales = null;
                
                conexion = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"),propiedades.getValueProperty("IfxLogin"),propiedades.getValueProperty("IfxPass"));
                conexion.setAutoCommit(false);
                
                xmlDoc = Utilidades.xml.Documento.crearDocumentoXMLVacio(); 
                //ELEMENTO RAIZ
                tasacion = Utilidades.xml.Documento.crearNodo(xmlDoc,"TASACION"); 
                for (fila = filaI; fila <= filaF; fila ++)
                {                                       
                    //por cada fila vamos escribiendo el xml      
                    //datosregistrales
                    dameValorExcel(fila,1,"S"); //finca registral
                    oDatosreg.clear();
                    if (oDatosreg.load(numexp, valorS, conexion))
                    {
                        oCatastro.load(numexp, oDatosreg.numero, conexion);
                        Datos_Registrales = Utilidades.xml.Documento.crearNodo(xmlDoc,"DATOS_REGISTRALES");
                        Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Datos_Registrales,"REGISTRO_PROPIEDAD",nombreRegistro(oDatosreg.registro));
                        Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Datos_Registrales,"NUMERO_REGISTRO",numeroRegistro(oDatosreg.registro));
                        Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Datos_Registrales,"NUMERO_FINCA_REGISTRAL",oDatosreg.finca);
                        Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Datos_Registrales,"SECCION","");
                        String tomo = oDatosreg.tomo;
                        if (tomo == null) tomo = "";
                        else
                        {
                            if (tomo.length() > 25) tomo = tomo.substring(0,25);
                        }
                        Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Datos_Registrales,"TOMO",tomo);
                        String libro = oDatosreg.libro;           
                        if (libro == null) libro = "";
                        else
                        {
                            if (libro.length() > 25) libro = libro.substring(0,25);
                        }
                        Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Datos_Registrales,"LIBRO",libro);
                        String folio = oDatosreg.folio;
                        if (folio == null) folio = "";
                        else
                        {
                            if (folio.length() > 25) folio = folio.substring(0,25);
                        }
                        Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Datos_Registrales,"FOLIO",folio);
                        String refcatastral = oCatastro.fcatastral;
                        if (refcatastral == null) refcatastral = ""; 
                        else 
                        {
                            if (refcatastral.length() > 50) refcatastral = refcatastral.substring(0, 50);
                        }
                        Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Datos_Registrales,"REF_CATASTRAL",refcatastral);
                        Utilidades.xml.Documento.addNodoHijo2NodoSuperior(tasacion,Datos_Registrales);
                        Datos_Registrales = null;
                    }
                    //superficies
                    superficies = Utilidades.xml.Documento.crearNodo(xmlDoc,"SUPERFICIES");
                    dameValorExcel(fila,2,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,superficies,"UTIL_REGISTRAL",valorS);
                    dameValorExcel(fila,3,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,superficies,"CONSTRUIDA_SIN_COMUNES_REGISTRAL",valorS);
                    dameValorExcel(fila,4,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,superficies,"CONSTRUIDA_CON_COMUNES_REGISTRAL",valorS);
                    dameValorExcel(fila,5,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,superficies,"SIN_DEFINIR_REGISTRAL",valorS);
                    dameValorExcel(fila,6,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,superficies,"UTIL",valorS);
                    dameValorExcel(fila,7,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,superficies,"CONSTRUIDA_SIN_COMUNES",valorS);
                    dameValorExcel(fila,8,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,superficies,"CONSTRUIDA_CON_COMUNES",valorS);
                    dameValorExcel(fila,9,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,superficies,"ADOPTADA",valorS);
                    dameValorExcel(fila,10,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,superficies,"ADOPTADA_MERCADO",valorS);
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,superficies,"CATASTRAL_SUELO","1083");
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,superficies,"CATASTRAL_VUELO","0");
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,superficies,"CATASTRAL_COMUNES","0");
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,superficies,"RELACION_REAL_UTIL","0");
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,superficies,"SUELO_REGISTRAL","0");
                    Utilidades.xml.Documento.addNodoHijo2NodoSuperior(tasacion,superficies);
                    superficies = null;
                    //valores
                    valores = Utilidades.xml.Documento.crearNodo(xmlDoc,"VALORES");
                    dameValorExcel(fila,11,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"VALOR_TASACION_TOTAL",valorS);
                    dameValorExcel(fila,12,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"VALOR_REFERENCIA",valorS);
                    dameValorExcel(fila,13,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"VALOR_ADOPTADO_TOTAL",valorS);
                    dameValorExcel(fila,14,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"VALOR_REPERCUSION",valorS);
                    dameValorExcel(fila,15,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"COSTE_CONSTRUCCION_BRUTO",valorS);
                    dameValorExcel(fila,16,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"GASTOS_NECESARIOS_CONSTRUCCION_BRUTO",valorS);
                    dameValorExcel(fila,17,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"COSTE_ELEMENTOS_POLIVALENTES",valorS);
                    dameValorExcel(fila,18,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"DEPRECIACION_FISICA_REAL",valorS);
                    dameValorExcel(fila,19,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"DEPRECIACION_FISICA_ADOPTADA",valorS);
                    dameValorExcel(fila,20,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"DEPRECIACION_ELEMENTOS_POLIVAL",valorS);
                    dameValorExcel(fila,21,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"DEPRECIACION_FUNCIONAL",valorS);
                    dameValorExcel(fila,22,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"VRB_UNITARIO",valorS);
                    dameValorExcel(fila,23,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"VRN_UNITARIO",valorS);
                    dameValorExcel(fila,24,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"VALOR_COMPARACION_UNITARIO",valorS);
                    dameValorExcel(fila,25,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"COEFICIENTE_KH_HIPOTECARIO",valorS);
                    dameValorExcel(fila,26,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"VALOR_COMPARACION_AJUSTADO_UNITARIO",valorS);
                    dameValorExcel(fila,27,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"VALOR_COMPARACION",valorS);
                    dameValorExcel(fila,28,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"PORCENTAJE_PROPIEDAD",valorS);
                    dameValorExcel(fila,29,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"VALOR_OTROS_TOTAL",valorS);
                    dameValorExcel(fila,30,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"VALOR_EXPLOTACION_TOTAL",valorS);
                    Utilidades.xml.Documento.addNodoHijo2NodoSuperior(tasacion,valores);
                    valores = null;
                                                                                                                             
                }
                Utilidades.xml.Documento.addNodoRaiz2DocumentoXML(xmlDoc,tasacion);
                Utilidades.xml.Utilidades.printDOM(xmlDoc, ficheroXML.getAbsolutePath());
                conexion.close();
            }//if segipsa
            }//if cliente no es nulo
            
            
        }
        catch (Exception e)
        {            
            estadoOK = false;
            descError = "Excepcion general en la carga de unidades. Motivo"+e.toString();
            System.out.println(descError);
        }
        finally
        {
            try
            {
                if (cerrarConexion && conexion != null)
                {
                    conexion.rollback();
                    conexion.close();
                }
            }
            catch (Exception e){}
            
        }
    }//cargaUnidades
    
    
    public void cargaUnidades_(String pnumexp,Integer cliente,String rutaCompleta,Integer idEdificio, Boolean enConstruccion, Logger log, Connection conex)
    {
        String numexp = null;
        boolean estadok = true;
        int procesados = 0;
        Integer totales = 0;
        int filaI = 0;
        int filaF = 0;
        int fila = 0;
        boolean esSareb = false;        
        Utilidades.Resultado oResultado = null;        
        Document xmlDoc = null;
        boolean cerrarConexion = false;
        
        Integer ivalor = null;
        
        try
        {
            descError = "";
            estadoOK = true;
            esSareb = Utilidades.Constantes.esSareb(cliente);            
            //cargamos el fichero de propiedades.
            if (log == null && conex == null)
            {
                java.io.File fichero = new java.io.File(sRutaPropiedades);
                if (fichero.exists())
                {
                    propiedades = new Utilidades.Propiedades(fichero.getAbsolutePath());      
                    //FICHERO LOG4J
                    PropertyConfigurator.configure(rutaLog + "Log4j.properties");      
                }
                else
                {
                    estadok = false;                
                    System.out.println("No se ha encontrado el fichero de propiedades en la ruta: "+sRutaPropiedades.trim());            
                } 
            }
            if (cliente == null)
            {
                try
                {
                    conexion = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"),propiedades.getValueProperty("IfxLogin"),propiedades.getValueProperty("IfxPass"));
                    conexion.setAutoCommit(false);      
                    String origen = "2553 0715 152103";
                    String destino = "2554 1009 201702";
                    //Objetos.unidades.Comunes.copy(origen, destino, conexion);
                }
                catch (Exception e)
                {
                    
                }
                finally
                {
                    conexion.close();
                }
                
            }
            else
            {
            if (estadok && (cliente == 155 || Utilidades.Constantes.esSareb(cliente)))
            {//En el caso de Sareb el numexp pasado como parametro será el idgrupoexpedientes. En este caso:
             //1.- el expediente donde insertar las unidades vendrá especificado en la columna 0
             //2.- para cada uno de estos expedientes hay que comprobar que pertenece a ese iddgrupoexpedientes.
             //3.- si el idEdificio es nulo creamos uno nuevo y sobre el cargamos los elementos.   
                
                if (conex == null)
                {
                    conexion = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"),propiedades.getValueProperty("IfxLogin"),propiedades.getValueProperty("IfxPass"));
                    conexion.setAutoCommit(false);
                    cerrarConexion = true;
                }
                else conexion = conex;
                sUrlExcel = rutaCompleta;
                HojaActual = "Unidad";
                oExcel = new Utilidades.Excel(sUrlExcel);
                if (log != null) logger = log;
                //idEdificio = idEdificio;
                filaI = 4;
                filaF = oExcel.getMaximaFila(HojaActual) + 1;                
                if (!esSareb) logger.info("Iniciada carga de datos para el expediente: "+numexp);
                expedientesSareb.clear();
                for (fila = filaI; fila < filaF; fila ++)
                {  
                    //System.out.println ("Elemento: "+fila);
                    totales ++;
                    //Utilidades.Resultado resut = oElementos.insert(conexion);
                    //oElementos.id_elmto = resut.getIntegerResultado();
                    
                                        
                    if (esSareb)
                    {
                        dameValorExcel(fila,0,"S");   //numero de expediente
                        if (valorS != null)
                        {
                            if (esActivoDeGrupo(pnumexp,valorS)) numexp = valorS;
                            else numexp = null;
                        }
                        else numexp = null;
                    }
                    else numexp = pnumexp;
                    if (numexp != null)                                                            
                    {//elementos
                        try
                        {
                          if (Objetos.unidades.Comunes.delete_all_data_technics(numexp,conexion)) 
                          {
                              conexion.commit();
                              logger.info("Borrado expediente: "+numexp);
                          }
                          else
                          {
                              conexion.rollback();
                              logger.error("No se ha podido borrar expediente: "+numexp);
                          }
                        }
                        catch (Exception e)
                        {
                            logger.error("Excepciión al borrar expediente: "+numexp+" motivo: "+e.toString());
                        }
                    }//if numexp != null
                    else 
                    {
                        if (esSareb) procesados ++;  //le damos como procesado. Si el expediente del excel no es del grupo no lo insertamos pero continuamos con el resto.
                    }                    
               }//for
               
               oExcel = null;                              
            }//if 155            
            if (estadok && cliente == 600)
            {//para caixa insertamos en unidades       
                //sUrlExcel = "/data/informes/cargaUnidades/100.TS2014007054-001-14.xls";
                sUrlExcel = "/data/informes/cargaUnidades/"+numexp;
                oExcel = new Utilidades.Excel(sUrlExcel+".xls");  
                HojaActual = "XML";
                filaI = 2;                
                filaF = oExcel.getMaximaFila(HojaActual) + 1;
                File ficheroXML = new java.io.File(sUrlExcel+".xml");                
                Element tasacion = null;
                Element superficies = null;
                Element valores = null;
                Element Datos_Registrales = null;
                
                conexion = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"),propiedades.getValueProperty("IfxLogin"),propiedades.getValueProperty("IfxPass"));
                conexion.setAutoCommit(false);
                
                xmlDoc = Utilidades.xml.Documento.crearDocumentoXMLVacio(); 
                //ELEMENTO RAIZ
                tasacion = Utilidades.xml.Documento.crearNodo(xmlDoc,"TASACION"); 
                for (fila = filaI; fila <= filaF; fila ++)
                {                                       
                    //por cada fila vamos escribiendo el xml      
                    //datosregistrales
                    dameValorExcel(fila,1,"S"); //finca registral
                    oDatosreg.clear();
                    if (oDatosreg.load(numexp, valorS, conexion))
                    {
                        oCatastro.load(numexp, oDatosreg.numero, conexion);
                        Datos_Registrales = Utilidades.xml.Documento.crearNodo(xmlDoc,"DATOS_REGISTRALES");
                        Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Datos_Registrales,"REGISTRO_PROPIEDAD",nombreRegistro(oDatosreg.registro));
                        Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Datos_Registrales,"NUMERO_REGISTRO",numeroRegistro(oDatosreg.registro));
                        Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Datos_Registrales,"NUMERO_FINCA_REGISTRAL",oDatosreg.finca);
                        Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Datos_Registrales,"SECCION","");
                        String tomo = oDatosreg.tomo;
                        if (tomo == null) tomo = "";
                        else
                        {
                            if (tomo.length() > 25) tomo = tomo.substring(0,25);
                        }
                        Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Datos_Registrales,"TOMO",tomo);
                        String libro = oDatosreg.libro;           
                        if (libro == null) libro = "";
                        else
                        {
                            if (libro.length() > 25) libro = libro.substring(0,25);
                        }
                        Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Datos_Registrales,"LIBRO",libro);
                        String folio = oDatosreg.folio;
                        if (folio == null) folio = "";
                        else
                        {
                            if (folio.length() > 25) folio = folio.substring(0,25);
                        }
                        Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Datos_Registrales,"FOLIO",folio);
                        String refcatastral = oCatastro.fcatastral;
                        if (refcatastral == null) refcatastral = ""; 
                        else 
                        {
                            if (refcatastral.length() > 50) refcatastral = refcatastral.substring(0, 50);
                        }
                        Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Datos_Registrales,"REF_CATASTRAL",refcatastral);
                        Utilidades.xml.Documento.addNodoHijo2NodoSuperior(tasacion,Datos_Registrales);
                        Datos_Registrales = null;
                    }
                    //superficies
                    superficies = Utilidades.xml.Documento.crearNodo(xmlDoc,"SUPERFICIES");
                    dameValorExcel(fila,2,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,superficies,"UTIL_REGISTRAL",valorS);
                    dameValorExcel(fila,3,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,superficies,"CONSTRUIDA_SIN_COMUNES_REGISTRAL",valorS);
                    dameValorExcel(fila,4,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,superficies,"CONSTRUIDA_CON_COMUNES_REGISTRAL",valorS);
                    dameValorExcel(fila,5,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,superficies,"SIN_DEFINIR_REGISTRAL",valorS);
                    dameValorExcel(fila,6,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,superficies,"UTIL",valorS);
                    dameValorExcel(fila,7,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,superficies,"CONSTRUIDA_SIN_COMUNES",valorS);
                    dameValorExcel(fila,8,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,superficies,"CONSTRUIDA_CON_COMUNES",valorS);
                    dameValorExcel(fila,9,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,superficies,"ADOPTADA",valorS);
                    dameValorExcel(fila,10,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,superficies,"ADOPTADA_MERCADO",valorS);
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,superficies,"CATASTRAL_SUELO","1083");
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,superficies,"CATASTRAL_VUELO","0");
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,superficies,"CATASTRAL_COMUNES","0");
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,superficies,"RELACION_REAL_UTIL","0");
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,superficies,"SUELO_REGISTRAL","0");
                    Utilidades.xml.Documento.addNodoHijo2NodoSuperior(tasacion,superficies);
                    superficies = null;
                    //valores
                    valores = Utilidades.xml.Documento.crearNodo(xmlDoc,"VALORES");
                    dameValorExcel(fila,11,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"VALOR_TASACION_TOTAL",valorS);
                    dameValorExcel(fila,12,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"VALOR_REFERENCIA",valorS);
                    dameValorExcel(fila,13,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"VALOR_ADOPTADO_TOTAL",valorS);
                    dameValorExcel(fila,14,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"VALOR_REPERCUSION",valorS);
                    dameValorExcel(fila,15,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"COSTE_CONSTRUCCION_BRUTO",valorS);
                    dameValorExcel(fila,16,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"GASTOS_NECESARIOS_CONSTRUCCION_BRUTO",valorS);
                    dameValorExcel(fila,17,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"COSTE_ELEMENTOS_POLIVALENTES",valorS);
                    dameValorExcel(fila,18,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"DEPRECIACION_FISICA_REAL",valorS);
                    dameValorExcel(fila,19,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"DEPRECIACION_FISICA_ADOPTADA",valorS);
                    dameValorExcel(fila,20,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"DEPRECIACION_ELEMENTOS_POLIVAL",valorS);
                    dameValorExcel(fila,21,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"DEPRECIACION_FUNCIONAL",valorS);
                    dameValorExcel(fila,22,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"VRB_UNITARIO",valorS);
                    dameValorExcel(fila,23,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"VRN_UNITARIO",valorS);
                    dameValorExcel(fila,24,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"VALOR_COMPARACION_UNITARIO",valorS);
                    dameValorExcel(fila,25,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"COEFICIENTE_KH_HIPOTECARIO",valorS);
                    dameValorExcel(fila,26,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"VALOR_COMPARACION_AJUSTADO_UNITARIO",valorS);
                    dameValorExcel(fila,27,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"VALOR_COMPARACION",valorS);
                    dameValorExcel(fila,28,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"PORCENTAJE_PROPIEDAD",valorS);
                    dameValorExcel(fila,29,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"VALOR_OTROS_TOTAL",valorS);
                    dameValorExcel(fila,30,"S");    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,valores,"VALOR_EXPLOTACION_TOTAL",valorS);
                    Utilidades.xml.Documento.addNodoHijo2NodoSuperior(tasacion,valores);
                    valores = null;
                                                                                                                             
                }
                Utilidades.xml.Documento.addNodoRaiz2DocumentoXML(xmlDoc,tasacion);
                Utilidades.xml.Utilidades.printDOM(xmlDoc, ficheroXML.getAbsolutePath());
                conexion.close();
            }//if segipsa
            }//if cliente no es nulo
            
            
        }
        catch (Exception e)
        {            
            estadoOK = false;
            descError = "Excepcion general en la carga de unidades. Motivo"+e.toString();
            System.out.println(descError);
        }
        finally
        {
            try
            {
                if (cerrarConexion && conexion != null)
                {
                    conexion.rollback();
                    conexion.close();
                }
            }
            catch (Exception e){}
            
        }
    }//cargaUnidades
    
    
    public int getNumdr(String numexp,String finca)
    {
        String registro = "";
        String sConsulta = "";
        java.sql.ResultSet rs = null;
        int numReg = 0;
        
        try
        {         
            
            sConsulta = "SELECT numero FROM datosreg WHERE numexp = '"+numexp+"' AND finca = '"+finca+"'";
            rs = Utilidades.Conexion.select(sConsulta,conexion);
            if (rs.next())
            {
                numReg = rs.getInt("numero");
            }
            else
            {// no hay datos registrales.                
                numReg = 0;
            }
            rs.close();
            rs = null;
        }
        catch (Exception e){numReg = 0;}      
        finally
        {
            try{
                if (rs != null) rs.close();
            }
            catch (Exception e) {}
           return numReg;
        }
        
    }//getNumdr
    
    
    public void dameValorExcel(int fila,int col,String tipo)  
    {
        String valor = null;
        try
        {    valorI = 0;
             valorD = 0.0;
             valorS = null;
             
             celda = oExcel.getCeldaFilaHoja(HojaActual,fila,col);                                 
             if (tipo.equals("I")) valorI = Utilidades.Excel.getIntegerCellValue(celda);  
             if (tipo.equals("D")) valorD = Utilidades.Excel.getDoubleCellValue(celda);
             if (tipo.equals("S")) valorS = Utilidades.Excel.getStringCellValue(celda);
        }   
        catch (FileNotFoundException fnfe)
        {
            valorI = 0;
            valorD = 0.0;
            valorS = null;
        }
        catch (IOException ioe)
        {
            valorI = 0;
            valorD = 0.0;
            valorS = null;
        }
        catch (Exception e)
        {
            valorI = 0;
            valorD = 0.0;
            valorS = null;
        }        
    }//dameValorExcel
    
    private String nombreRegistro(String nombre)
    {//0..200
        
            if (nombre != null)
            {
                String [] registro = nombre.split("Nº");
                return registro[0];
            }
            else return "";
        
    }//nombreRegistro
    
     private String numeroRegistro(String nombre)
    {//length value 2 RELLENAR CON ESPACIOS
        
       String numero = "";
     
            if (nombre != null)
            {
                String [] registro = nombre.split("Nº");        
                if (registro.length > 1) numero = Utilidades.Cadenas.TrimTotal(registro[1]);
                if (numero.length() > 0 && numero.length()< 2) numero = "0"+numero;
                else numero = "00";       
            }
            else numero = "00";
      
       return numero;
    }//numeroRegistro
     
    private void recalcularValores(String numexp)
    {
        try
        {
            conexion = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"),propiedades.getValueProperty("IfxLogin"),propiedades.getValueProperty("IfxPass"));
            conexion.setAutoCommit(false);               
            boolean actualizado = Objetos.unidades.calculos.Totales.run(numexp,conexion);
            if (actualizado) System.out.println("Actualizado");
            else System.out.println("NO Actualizado");
            conexion.close();
        }
        catch (Exception e)
        {
            System.out.println("Excepción: "+e.toString());
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
        }
        
    }
    
    public Boolean esActivoDeGrupo (String idExpediente,String numexp)
    {
        java.sql.ResultSet rs = null;
        String sConsulta = null;
        boolean esActivoDeGrupo = false;
        try
        {
            if (idExpediente != null && numexp != null)
            {
                sConsulta = "SELECT numexp FROM refer WHERE numexp = '"+numexp+"' AND idexpediente = '"+idExpediente+"'";
                rs = Utilidades.Conexion.select(sConsulta, conexion);
                if (rs.next()) esActivoDeGrupo =  true;            
                else esActivoDeGrupo =  false;
            }
            else esActivoDeGrupo =  false;
            rs.close();
            rs = null;
        }
        catch (Exception e)
        {
            esActivoDeGrupo =  false;
        }
        finally
        {
            try
            {
                if (rs != null && !rs.isClosed())
                {
                    rs.close();
                    rs = null;
                }
            }
            catch (Exception e)
            {
                
            }
            return esActivoDeGrupo;
        }
        
    }//esActivoDeGrupo
    
    public Integer numElementosByExpediente (String numexp)
    {
        java.sql.ResultSet rs = null;
        String sConsulta = null;
        Integer cuantos = null;
        try
        {
            if (numexp != null)
            {
                sConsulta = "SELECT count(*) cuantos FROM elementos WHERE numexp = '"+numexp+"'";
                rs = Utilidades.Conexion.select(sConsulta, conexion);
                if (rs.next()) cuantos = rs.getInt("cuantos");                
            }            
            rs.close();
            rs = null;
        }
        catch (Exception e)
        {
            cuantos = null;
        }
        finally
        {
            try
            {
                if (rs != null && !rs.isClosed())
                {
                    rs.close();
                    rs = null;
                }
            }
            catch (Exception e)
            {
                
            }
            return cuantos;
        }
        
    }//numElementosByExpediente
       
}
