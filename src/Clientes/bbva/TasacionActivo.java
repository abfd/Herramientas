/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Clientes.bbva;

/**
 *
 * @author Alberto
 */
public class TasacionActivo 
{
    
    private static final String esquemaDeValidacion = "osV9F1.xsd";
    private org.w3c.dom.Document xmlDoc = null;
    private String ficheroXML = "/data/informes/xml/bbva/"; //asignar el nombre que corresponda
    private boolean bDatosTecnicos = false;
    
    private java.sql.Connection connection = null;
    private String numexp = null;
    private Objetos.v2.Solicitudes solicitudes = null;
    private Objetos.Refer refer = null;
    private Objetos.Documenta documenta = null;
    private Objetos.Sancion sancion = null;
    private Objetos.Sancioncons sancioncons = null;
    private Objetos.v2.Otrastas otrastas = null;
    private Objetos.v2.Facturas facturas = null;
    private java.sql.ResultSet rsDatosreg = null;
    private java.sql.ResultSet rsCatastro = null;
    private Objetos.unidades.Elementos[] elementos = null;
    private Objetos.unidades.Superficies superficies = null;
    private Objetos.unidades.Unitarios unitarios = null;
    private Objetos.unidades.Vtotales vtotales = null;
    private Objetos.Elemento1 oElemento1 = null;
    private Objetos.Elemento2 oElemento2 = null;
    private Objetos.Elemento3 oElemento3 = null;
    private Objetos.Elemento4 oElemento4 = null;
    private Objetos.Elemento5 oElemento5 = null;
    private Objetos.Elemento6 oElemento6 = null;
    private Objetos.Elemento7 oElemento7 = null;
    private Objetos.Elemento8 oElemento8 = null;
    private Objetos.v2.Elemento9 oElemento9 = null;
    
    //*********************************************************************************************************************************
    //*********************************************************************************************************************************
    public void load_structures() throws Exception
    {
        solicitudes = new Objetos.v2.Solicitudes();
        solicitudes.load(numexp,connection);
        refer = new Objetos.Refer();
        refer.load(numexp, connection);
        rsDatosreg = Objetos.Datosreg.get(numexp, connection);
        rsCatastro = Objetos.Catastro.get(numexp, connection);
        facturas = new Objetos.v2.Facturas();
        facturas.load(numexp, connection);
        documenta = new Objetos.Documenta();
        documenta.load(numexp, connection);
        
                
        sancion = new Objetos.Sancion();
        sancioncons = new Objetos.Sancioncons();
        otrastas = new Objetos.v2.Otrastas();
        if(sancion.load(numexp, connection)) bDatosTecnicos = false;
        else if(sancioncons.load(numexp, connection)) bDatosTecnicos = false;
        else if(otrastas.load(numexp, connection)) bDatosTecnicos = false;
        else bDatosTecnicos = true;
    }//load_structures
    
    //*********************************************************************************************************************************
    //*********************************************************************************************************************************
    public TasacionActivo(String numexp,java.sql.Connection connection) throws Exception
    {
        org.w3c.dom.Element TasacionActivo = null;
        org.w3c.dom.Element Localizacion = null;
        Integer numFincas = 0;
        String oString = "";
        Boolean oBoolean = false;
        org.w3c.dom.Element FincasInmueble = null;
        org.w3c.dom.Element Finca = null;
        org.w3c.dom.Element CargasInmueble = null;
        org.w3c.dom.Element xElement = null;
        org.w3c.dom.Element Valoracion = null;
        org.w3c.dom.Element Albaran = null;
        
        try
        {
            this.numexp = numexp;
            this.connection = connection;
            load_structures();
                xmlDoc = Utilidades.xml.Documento.crearDocumentoXMLVacio();   
            //ELEMENTO RAIZ
                    TasacionActivo = Utilidades.xml.Documento.crearNodo(xmlDoc,"TasacionActivo"); 
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,TasacionActivo,"NumeroTasacion",refer.referencia);
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,TasacionActivo,"CodigoTasadora","4311");
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,TasacionActivo,"NombreTasadora","VALTECNIC");
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,TasacionActivo,"ReferenciaTasadora",solicitudes.numexp);
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,TasacionActivo,"Excepciones","");
                    Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,TasacionActivo,"FechaLimite","");
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,TasacionActivo,"VerificacionRegistral",true);
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,TasacionActivo,"NombreCliente",solicitudes.solici);
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,TasacionActivo,"ApellidosCliente",solicitudes.solici);
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,TasacionActivo,"NIFCliente",solicitudes.nifsolici);
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,TasacionActivo,"CodigoActivo",refer.api);
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,TasacionActivo,"TipoActivo","");
             //LOCALIZACION
                    Localizacion = Utilidades.xml.Documento.crearNodo(xmlDoc,"Localizacion"); 
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Localizacion,"Pais",Utilidades.Constantes.ESPANIA);
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Localizacion,"Provincia",solicitudes.provin);
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Localizacion,"Municipio",solicitudes.munici);
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Localizacion,"Poblacion",solicitudes.locali);
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Localizacion,"TipoVia",solicitudes.tipovia);
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Localizacion,"NombreVia",solicitudes.calle);
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Localizacion,"NumeroVia",solicitudes.numero);
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Localizacion,"BloquePortal",solicitudes.numero);
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Localizacion,"Planta",solicitudes.planta);
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Localizacion,"Puerta",solicitudes.puerta);
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Localizacion,"CodigoPostal",solicitudes.codpos);
                    Utilidades.xml.Documento.addNodoHijo2NodoSuperior(TasacionActivo,Localizacion); 
                    Localizacion = null;
                    
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,TasacionActivo,"InmuebleIntegradoVariasF",false);
                    
               //FINCAS INMUEBLE
                    if(bDatosTecnicos)
                    {
                        elementos = Objetos.unidades.Elementos.enumerate(numexp, connection);
                        FincasInmueble = Utilidades.xml.Documento.crearNodo(xmlDoc,"FincasInmueble");  
                        for (int i = 0;i<elementos.length;i++)
                        {                        
                            //Finca = generaNwFincaDatosTecnicos(elementos[i]);
                            if (Finca != null)
                            {
                                Utilidades.xml.Documento.addNodoHijo2NodoSuperior(FincasInmueble,Finca); 
                                Finca = null;
                            }//if
                        }//for fincas inmueble
                    }//if
                    else
                    {
                        java.sql.ResultSet oResultSet = Objetos.Datosreg.get(numexp, connection);
                        FincasInmueble = Utilidades.xml.Documento.crearNodo(xmlDoc,"FincasInmueble");  
                        while (oResultSet!=null && oResultSet.next())
                        {                        
                            Finca = generaNwFinca(oResultSet.getInt("NUMERO"));
                            if (Finca != null)
                            {
                                Utilidades.xml.Documento.addNodoHijo2NodoSuperior(FincasInmueble,Finca); 
                                Finca = null;
                            }//if
                        }//for fincas inmueble
                        if(oResultSet!=null) oResultSet.close();
                        oResultSet = null;
                    }//else
                    
                    Utilidades.xml.Documento.addNodoHijo2NodoSuperior(TasacionActivo,FincasInmueble); 
                    FincasInmueble = null;
                    
               //CARGAS INMUEBLE
                    /*CargasInmueble = Utilidades.xml.Documento.crearNodo(xmlDoc,"CargasInmueble"); 
                        xElement = Utilidades.xml.Documento.crearNodo(xmlDoc,"Carga"); 
                        Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"IdentificacionPersonaAFavorCarga","");
                        Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"Concepto","");
                        Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"ImporteCargaImueble","");
                        Utilidades.xml.Documento.addNodoHijo2NodoSuperior(CargasInmueble,xElement);
                        xElement = null;
                    Utilidades.xml.Documento.addNodoHijo2NodoSuperior(TasacionActivo,CargasInmueble); 
                    CargasInmueble = null;*/
                
                //VALORACION
                    Valoracion = Utilidades.xml.Documento.crearNodo(xmlDoc,"Valoracion"); 
                    Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,Valoracion,"FechaTasacion",solicitudes.fchvis,"yyyy-MM-dd");
                    Utilidades.xml.Documento.addHoja2NodoDefault0(xmlDoc,Valoracion,"ValorTasacion",solicitudes.tasacion);
                    if(sancion.valtotsuelo!=null) oString = sancion.valtotsuelo.toString();
                    else if(sancioncons.sueloactual!=null) oString = sancioncons.sueloactual.toString();
                    else if(otrastas.totsuelo!=null) oString = otrastas.totsuelo.toString();
                    else oString = "";
                    Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,Valoracion,"ValorDeSuelo",oString);
                    Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,Valoracion,"ValorM2","");
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Valoracion,"Divisa",Utilidades.Constantes.EUROS);
                    if(sancion.suputil!=null) oString = sancion.suputil.toString();
                    //else if(sancioncons.ssuputil!=null) oString = sancioncons.sueloactual.toString();
                    else if(otrastas.totsuperf!=null) oString = otrastas.totsuperf.toString();
                    else oString = ""; 
                    Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,Valoracion,"SuperficieTasacion",oString);
                    Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,Valoracion,"SupTasacionInicial",oString);
                    Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,Valoracion,"SupAdoptada",oString);
                    Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,Valoracion,"SupRegistro",oString);
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Valoracion,"AnioConstruccion","");
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Valoracion,"TasacionCondicionada",Objetos.Condicion1.hay_sin_cerrar(numexp, connection)); 
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Valoracion,"Observaciones",Utilidades.Cadenas.getTextWithNoSpecialCharacters(Objetos.Obsfin.getObsfin1(numexp, connection)),200);            
                    oString = Objetos.Obsfin.getObsfin1(numexp, connection);
                    if(oString==null || oString.trim().equals("")) oString = "true";
                    else oString = "false";
                    //Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Valoracion,"Reforma",oString);
                    //Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Valoracion,"IntensidadReforma",0);
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Valoracion,"CalidadMercadoActual",5);
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Valoracion,"CalidadZonaActual",5);
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Valoracion,"CalidadEdificioActual",5);
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Valoracion,"CalidadMercadoTendencia",5);
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Valoracion,"CalidadZonaTendencia",5);
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Valoracion,"CalidadEdificioTendencia",5);
                    Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,Valoracion,"PorcentajeProindiviso","");
                    Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,Valoracion,"PorcentajeReduccion","");
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Valoracion,"AjustadaOrdenECO",true);
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Valoracion,"MetodoValoracion","");
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Valoracion,"Condicionantes",Objetos.Condicion1.hay(numexp, connection));            
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Valoracion,"Advertencias",Objetos.Adverten1.hay(numexp, connection));
                    if(solicitudes!=null && solicitudes.fchvis!=null) oBoolean = true;
                    else oBoolean = false;
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Valoracion,"Visita",oBoolean);
                    //Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Valoracion,"TipoDatosUtilizadosInmueblesComparables","");
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Valoracion,"MetodoResidualDinamico",false);
                    Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,Valoracion,"TipoActualizacion","");
                    Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,Valoracion,"TasaAnualMedVariacionPM","");
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Valoracion,"PlazoMaximoFinalizarCons","");
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Valoracion,"PlazoMaximoFinalizarCome","");
                    //Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Valoracion,"MetodoResidualEstadisticoMargenPromotor",false);
                    Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,Valoracion,"ValorMercado",solicitudes.tasacion);
                    Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,Valoracion,"ValorHipotecario",solicitudes.tasacion);
                    Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,Valoracion,"ValorHipotesisEdificioTe",get_valoracion_ValorHipotesisEdificioTerminado());
                    
                    Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,Valoracion,"ValorTerreno",get_valoracion_ValorTerreno());
                    Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,Valoracion,"ValorSeguro",solicitudes.seguro);
                    Utilidades.xml.Documento.addNodoHijo2NodoSuperior(TasacionActivo,Valoracion); 
                    
                    
                //ALBARAN
                    //VALORACION
                    Albaran = Utilidades.xml.Documento.crearNodo(xmlDoc,"Albaran"); 
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Albaran,"ReferenciaAlbaran",solicitudes.numexp);
                    Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,Albaran,"FechaAlbaran",facturas.fchfact,"yyyy-MM-dd");
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Albaran,"Tarifa",facturas.honora);
                    Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,Albaran,"GastosNotaRegistral",facturas.planos);
                    Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,Albaran,"GastosCedulaUrbana","");
                    Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,Albaran,"GastosLocomocion","");
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Albaran,"DescripcionOtros",facturas.concep1);
                    Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,Albaran,"GastosOtros",facturas.impaux);
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Albaran,"TipoImpuesto",getTipoImpuesto(facturas.iva));
                    Utilidades.xml.Documento.addNodoHijo2NodoSuperior(TasacionActivo,Albaran); 
                    Albaran = null;
                    
                    
                    Utilidades.xml.Documento.addNodoRaiz2DocumentoXML(xmlDoc,TasacionActivo); 
                    TasacionActivo=null;
                                        //Escribimos el documento XML en un fichero
                    Utilidades.xml.Utilidades.printDOM(xmlDoc,ficheroXML+numexp+".xml");
                    xmlDoc = null;                                                                    
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }//TasacionActivo
    
    //************************************************************************************************************************************
    //************************************************************************************************************************************
    private Double get_valoracion_ValorHipotesisEdificioTerminado()
    {
        Double oDouble = null;
        try
        {
            if(!bDatosTecnicos)
            {
                if(oElemento9!=null && oElemento9.valtotfin!=null) oDouble = oElemento9.valtotfin;
                else if(oElemento9!=null && oElemento9.valtotact!=null) oDouble = oElemento9.valtotact;
                else if(otrastas!=null && otrastas.tasafinal!=null) oDouble = otrastas.tasafinal;
                else oDouble = null;
            }//if
            else
            {
                
            }//else
        }//try
        catch(Exception e)
        {
            oDouble = null;
        }//catch
        return oDouble;
    }//get_valoracion_ValorHipotesisEdificioTerminado
    
    //************************************************************************************************************************************
    //************************************************************************************************************************************
    private Double get_valoracion_ValorTerreno()
    {
        Double oDouble = null;
        try
        {
            if(!bDatosTecnicos)
            {
                if(sancion!=null && sancion.valtotsuelo!=null) oDouble = sancion.valtotsuelo;
                else if(sancioncons!=null && sancioncons.sueloactual!=null) oDouble = sancioncons.sueloactual;
                else if(otrastas!=null && otrastas.totsuelo!=null) oDouble = otrastas.totsuelo;
                else oDouble = null;
            }//if
            else
            {
                
            }//else
        }//try
        catch(Exception e)
        {
            oDouble = null;
        }//catch
        return oDouble;
    }//get_valoracion_ValorTerreno
    
    //************************************************************************************************************************************
    //************************************************************************************************************************************
    private org.w3c.dom.Element generaNwFinca(Integer numero)
    {
        org.w3c.dom.Element Finca = null;
        org.w3c.dom.Element Localizacion = null;
        org.w3c.dom.Element IdentificadorRegistral = null;
        org.w3c.dom.Element DatosViviendas = null;
        org.w3c.dom.Element InstalacionesComunes = null;
        org.w3c.dom.Element DatosLocalesComerciales = null;
        org.w3c.dom.Element DatosOficinas = null;
        org.w3c.dom.Element ValoracionFinca = null;
        org.w3c.dom.Element ContaminacionRelevanteValoracion = null;
        org.w3c.dom.Element DatosEdificios = null;
        org.w3c.dom.Element xElement = null;
        org.w3c.dom.Element DatosHoteles = null;
        org.w3c.dom.Element DatosNavesIndustriales = null;
        org.w3c.dom.Element DatosSueloUrbanoUrbanizable = null;
        org.w3c.dom.Element DatosFincasRusticas = null;
        org.w3c.dom.Element MetodoDelCoste = null;
        org.w3c.dom.Element MetodoResidual = null;
        org.w3c.dom.Element MetodoComparacion = null;
        org.w3c.dom.Element MetodoActRentas = null;
        org.w3c.dom.Element OtrosMetodos = null;
        org.w3c.dom.Element AprovechamientoUnidades = null;
        org.w3c.dom.Element AprovechamientoM2 = null;
        org.w3c.dom.Element CargasInmueble = null;
        String oString = null;
        Objetos.Datosreg oDatosreg = null;
        Objetos.Catastro oCatastro = null;
        
        
        try
        {
            //***********************************************************************
            oDatosreg = new Objetos.Datosreg();
            oDatosreg.load(numexp,numero, connection);
            oCatastro = new Objetos.Catastro();
            if(oDatosreg!=null && oDatosreg.numero!=null) oCatastro.load(numexp,oDatosreg.numero, connection);
            oElemento1 = new Objetos.Elemento1();
            oElemento1.load(numexp, connection);
            oElemento2 = new Objetos.Elemento2();
            oElemento2.load(numexp, connection);
            oElemento3 = new Objetos.Elemento3();
            oElemento3.load(numexp, connection);
            oElemento4 = new Objetos.Elemento4();
            oElemento4.load(numexp, connection);
            oElemento5 = new Objetos.Elemento5();
            oElemento5.load(numexp, connection);
            oElemento6 = new Objetos.Elemento6();
            oElemento6.load(numexp, connection);
            oElemento7 = new Objetos.Elemento7();
            oElemento7.load(numexp, connection);
            oElemento8 = new Objetos.Elemento8();
            oElemento8.load(numexp, connection);
            oElemento9 = new Objetos.v2.Elemento9();
            oElemento9.load(numexp, connection);
            
            //***********************************************************************
            Finca = Utilidades.xml.Documento.crearNodo(xmlDoc,"Finca"); 
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Finca,"CodigoInmueble",refer.referencia);
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Finca,"TipoInmueble",getTipoInmueble(solicitudes.tipoinm));
            //LOCALIZACION
            Localizacion = Utilidades.xml.Documento.crearNodo(xmlDoc,"Localizacion"); 
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Localizacion,"Pais",Utilidades.Constantes.ESPANIA);
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Localizacion,"Provincia",solicitudes.provin);
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Localizacion,"Municipio",solicitudes.munici);
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Localizacion,"Poblacion",solicitudes.locali);
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Localizacion,"TipoVia",solicitudes.tipovia);
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Localizacion,"NombreVia",solicitudes.calle);
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Localizacion,"NumeroVia",solicitudes.numero);
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Localizacion,"BloquePortal","");
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Localizacion,"Planta",solicitudes.planta);
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Localizacion,"Puerta",solicitudes.puerta);
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Localizacion,"CodigoPostal",solicitudes.codpos);
            Utilidades.xml.Documento.addNodoHijo2NodoSuperior(Finca,Localizacion); 
            Localizacion = null;
            
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Finca,"Posesion",false);
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Finca,"IDUFIR",oDatosreg.idufir);
            
            //IDENTIFICACION REGISTRAL
            IdentificadorRegistral = Utilidades.xml.Documento.crearNodo(xmlDoc,"IdentificadorRegistral"); 
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,IdentificadorRegistral,"ProvinciaRegistro",oDatosreg.getFirstCiudadRegistroAndNumero()[0]);
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,IdentificadorRegistral,"MunicipioRegistro",solicitudes.munici);
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,IdentificadorRegistral,"NumeroRegistro",oDatosreg.getFirstCiudadRegistroAndNumero()[1]);
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,IdentificadorRegistral,"Tomo",oDatosreg.tomo);
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,IdentificadorRegistral,"Libro",oDatosreg.libro);
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,IdentificadorRegistral,"FincaRegistral",oDatosreg.finca);
            Utilidades.xml.Documento.addNodoHijo2NodoSuperior(Finca,IdentificadorRegistral); 
            IdentificadorRegistral = null;
            
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Finca,"ReferenciaCatastral",oCatastro.fcatastral);
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Finca,"Geolocalizacion",solicitudes.getGeolocalizacion(connection),100);
            
            if(Objetos.Tipos_aux.esLocalComercial(solicitudes.tipoinm))
            {
                //PARA VIVIENDAS
                DatosViviendas = Utilidades.xml.Documento.crearNodo(xmlDoc,"DatosViviendas"); 
                Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,DatosViviendas,"SuperficiePlanta",oElemento5.suputil);
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosViviendas,"TipoVivienda","");
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosViviendas,"NumAscensoresBloque",oElemento1.numascens);
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosViviendas,"NumDormitorios",oElemento2.getSumaDormitorios());
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosViviendas,"NumBanos",oElemento2.getSumaBanios()+oElemento2.getSumaAseos());
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosViviendas,"ExteriorInterior","");
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosViviendas,"Orientacion","");
                Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,DatosViviendas,"SuperfTerrazaM2",oElemento5.suptrza);
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosViviendas,"TerrazaTendedero",false);
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosViviendas,"CocinaEquipada",false);
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosViviendas,"ArmariosEmpotrados",false);
                if(oElemento4.puerta!=null && oElemento4.puerta.equals("1")) oString = "true";
                else oString = "false";
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosViviendas,"PuertaSeguridad",oString);
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosViviendas,"SistemaAlarma",false);
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosViviendas,"CircuitoCerradoSeguridad",false);
                if(oElemento4.aguacaliet!=null && (oElemento4.aguacaliet.equals("1") || oElemento4.aguacaliet.equals("2"))) oString = "true";
                else oString = "false";
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosViviendas,"AguaCaliente",oString);
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosViviendas,"CombustibleAguaCaliente",oElemento4.getTextCombustagua_XML_BBVA());
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosViviendas,"TipoCalefaccion",oElemento4.getTextCombustcalef_XML_BBVA());
                if(oElemento4.aireacond!=null && (oElemento4.aireacond.equals("1") || oElemento4.aireacond.equals("2"))) oString = "true";
                else oString = "false";
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosViviendas,"AireAcondicionado",oString);            
                    //instalaciones comunes
                    InstalacionesComunes  = Utilidades.xml.Documento.crearNodo(xmlDoc,"InstalacionesComunes"); 
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,InstalacionesComunes,"ZonaVerdeM2","");
                    if(oElemento2.piscina!=null && oElemento2.piscina.equals("0")) oString = "false";
                    else oString = "true";
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,InstalacionesComunes,"Piscina",oString);            
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,InstalacionesComunes,"TenisPadel",false);            
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,InstalacionesComunes,"OtrasInstalacionesDeport",false);            
                    Utilidades.xml.Documento.addNodoHijo2NodoSuperior(DatosViviendas,InstalacionesComunes); 
                    InstalacionesComunes = null;            
                Utilidades.xml.Documento.addNodoHijo2NodoSuperior(Finca,DatosViviendas); 
                DatosViviendas = null;
            }//if
            
            if(Objetos.Tipos_aux.esLocalComercial(solicitudes.tipoinm))
            {
                //LOCALES COMERCIALES
                DatosLocalesComerciales = Utilidades.xml.Documento.crearNodo(xmlDoc,"DatosLocalesComerciales"); 
                Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,DatosLocalesComerciales,"FrenteM","");            
                Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,DatosLocalesComerciales,"FondoM","");            
                Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,DatosLocalesComerciales,"AlturaM","");            
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosLocalesComerciales,"Divisibilidad",false);            
                Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,DatosLocalesComerciales,"SuperficieEnAltilloM2","");            
                Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,DatosLocalesComerciales,"SuperficieEnPlantaSotanoM2","");            
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosLocalesComerciales,"TipoCalle","");            
                Utilidades.xml.Documento.addNodoHijo2NodoSuperior(Finca,DatosLocalesComerciales); 
                DatosLocalesComerciales = null;
            }//if
            
            if(Objetos.Tipos_aux.esLocalComercial(solicitudes.tipoinm))
            {
                //OFICINAS
                DatosOficinas = Utilidades.xml.Documento.crearNodo(xmlDoc,"DatosOficinas"); 
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosOficinas,"TipoEdificacion",false);             // REVISAR: No tiene sentido el tipo
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosOficinas,"SueloTecnicoFalsoTechoIn",false);            
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosOficinas,"Divisibilidad",false);            
                Utilidades.xml.Documento.addNodoHijo2NodoSuperior(Finca,DatosOficinas); 
                DatosOficinas = null;
            }//if
            
            //VALORACION FINCA
            ValoracionFinca = Utilidades.xml.Documento.crearNodo(xmlDoc,"ValoracionFinca"); 
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,ValoracionFinca,"CosteTasacionFinca",facturas.imptot);            
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,ValoracionFinca,"ValorTasacion",solicitudes.tasacion);   
            if(oElemento8.valrmerac!=null) oString = oElemento8.valrmerac.toString();
            //else if(otrastas.totsuelo!=null) oString = otrastas.totsuelo.toString();         
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,ValoracionFinca,"ValorM2",oString);           
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,ValoracionFinca,"SuperficieTasacion","");    
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,ValoracionFinca,"SupTasacionInicial","");  
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,ValoracionFinca,"SupAdoptada","");
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,ValoracionFinca,"SupRegistro","");  
            if(sancion.valtotsuelo!=null) oString = sancion.valtotsuelo.toString();
            else if(sancioncons.sueloactual!=null) oString = sancioncons.sueloactual.toString();
            else if(otrastas.totsuelo!=null) oString = otrastas.totsuelo.toString();
            else oString = "";
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,ValoracionFinca,"ValorDeSuelo",oString);            
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,ValoracionFinca,"Divisa",Utilidades.Constantes.EUROS);  
            //Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,ValoracionFinca,"TasacionCondicionada",Objetos.Condicion1.hay_sin_cerrar(numexp, connection));            
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,ValoracionFinca,"Observaciones",Objetos.Obsfin.getObsfin1(numexp, connection),200);            
            oString = Objetos.Obsfin.getObsfin1(numexp, connection);
            if(oString==null || oString.trim().equals("")) oString = "true";
            else oString = "false";
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,ValoracionFinca,"Reforma",oString);
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,ValoracionFinca,"IntensidadReforma","");            
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,ValoracionFinca,"PorcentajeReduccion","");            
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,ValoracionFinca,"MetodoValoracion","");   
            
            MetodoDelCoste = Utilidades.xml.Documento.crearNodo(xmlDoc,"MetodoDelCoste");            
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,MetodoDelCoste,"ValorReemplazoNeto","");  
            Utilidades.xml.Documento.addNodoHijo2NodoSuperior(ValoracionFinca,MetodoDelCoste); 
            
            MetodoResidual = Utilidades.xml.Documento.crearNodo(xmlDoc,"MetodoResidual");            
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,MetodoResidual,"ValorResDinamico","");   
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,MetodoResidual,"ValorResEstatico","");  
            Utilidades.xml.Documento.addNodoHijo2NodoSuperior(ValoracionFinca,MetodoResidual); 
            
            MetodoComparacion = Utilidades.xml.Documento.crearNodo(xmlDoc,"MetodoComparacion");            
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,MetodoComparacion,"ValorComparacion","");  
            Utilidades.xml.Documento.addNodoHijo2NodoSuperior(ValoracionFinca,MetodoComparacion); 
            
            MetodoActRentas = Utilidades.xml.Documento.crearNodo(xmlDoc,"MetodoActRentas");            
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,MetodoActRentas,"ValorActRentasExplotacio",""); 
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,MetodoActRentas,"ValorActRentasMercado",""); 
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,MetodoActRentas,"ValorActRentasOtros","");  
            Utilidades.xml.Documento.addNodoHijo2NodoSuperior(ValoracionFinca,MetodoActRentas); 
            
            OtrosMetodos = Utilidades.xml.Documento.crearNodo(xmlDoc,"OtrosMetodos");            
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,OtrosMetodos,"ValorMaxLegal",""); 
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,OtrosMetodos,"ValorCatastral",""); 
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,OtrosMetodos,"OtroCriterio","");  
            Utilidades.xml.Documento.addNodoHijo2NodoSuperior(ValoracionFinca,OtrosMetodos); 
            
            //Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,ValoracionFinca,"Condicionantes",Objetos.Condicion1.concatAllTexco1(numexp, connection),200);            
            //Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,ValoracionFinca,"Advertencias",Objetos.Adverten1.hay(numexp, connection));
            if(solicitudes.fchvis!=null) oString = "true";
            else oString = "false";
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,ValoracionFinca,"Visita",true);            
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,ValoracionFinca,"TipoDatosInmueblesComp","");            
            //Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,ValoracionFinca,"MetodoResidualDinamico",false);            
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,ValoracionFinca,"TipoActualizacion","");            
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,ValoracionFinca,"TasaAnualMedVariacionPM","");     
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,ValoracionFinca,"MetodoResidualEstadistic","");            
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,ValoracionFinca,"ValorMercado",solicitudes.tasacion);            
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,ValoracionFinca,"ValorHipotecario",solicitudes.tasacion);            
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,ValoracionFinca,"ValorHipotesisEdificioTe","");            
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,ValoracionFinca,"ValorTerreno","");            
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,ValoracionFinca,"ValorSeguro",solicitudes.seguro);                        
            Utilidades.xml.Documento.addNodoHijo2NodoSuperior(Finca,ValoracionFinca);             
            ValoracionFinca = null;
            
            //CONTAMINACION
            ContaminacionRelevanteValoracion = Utilidades.xml.Documento.crearNodo(xmlDoc,"ContaminacionRelevanteVa"); 
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,ContaminacionRelevanteValoracion,"Terreno",false);                        
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,ContaminacionRelevanteValoracion,"Construccion",false);                        
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,ContaminacionRelevanteValoracion,"Acustica",false);                        
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,ContaminacionRelevanteValoracion,"Ambiental",false);                        
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,ContaminacionRelevanteValoracion,"Otras",false);                        
            Utilidades.xml.Documento.addNodoHijo2NodoSuperior(Finca,ContaminacionRelevanteValoracion);             
            ContaminacionRelevanteValoracion = null;
            
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Finca,"ConserjePorteroSeguridad",false);
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,Finca,"InmuebleCaracteristicasS",false);
            
            //EDIFICIOS
            DatosEdificios = Utilidades.xml.Documento.crearNodo(xmlDoc,"DatosEdificios"); 
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosEdificios,"UbicacionRelativaEnMunic","");                        
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosEdificios,"UbicacionRelativaEnBarri","");                        
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosEdificios,"UbicacionRelativaEnInmue","");                        
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,DatosEdificios,"SupConstruidaElementosCo","");                       
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,DatosEdificios,"SupElementosComunes","");                       
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,DatosEdificios,"SupConstruida","");                       
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,DatosEdificios,"SupUtil","");                    
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,DatosEdificios,"FechaConstruccion","");                        
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,DatosEdificios,"FechaUltimaRehabilitacio","");    
                xElement =  Utilidades.xml.Documento.crearNodo(xmlDoc,"EstadoConstruccion");  
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"Estado","");      
                Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,xElement,"PorcentajeConstruido","");                        
                Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,xElement,"FechaUltimoGradoAvanceEs","");  
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"SociedadEstimacionUltGA","");  
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"CertificacionObraTermina","");  
                Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,xElement,"FechaEstimadaFinObra","");  
                Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,xElement,"CosteAdquisicionSuelo","");  
                Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,xElement,"GastosDesarrollo","");  
                Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,xElement,"CosteEstimadoParaTermina","");  
                Utilidades.xml.Documento.addNodoHijo2NodoSuperior(DatosEdificios,xElement);  
                xElement = null;
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosEdificios,"Licencia","");     
            if(oElemento2.polivalenc!=null && (oElemento2.polivalenc.equals("1") || oElemento2.polivalenc.equals("2"))) oString = "true";
            else oString = "false";                  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosEdificios,"UsoPolivalente",oString);                        
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosEdificios,"CalidadConstruccion","");                        
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosEdificios,"EstadoConservacion","");                        
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,DatosEdificios,"NumPlantasSobreRasante",""); 
            /*xElement =  Utilidades.xml.Documento.crearNodo(xmlDoc,"AprovechamientoUnidades");
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"ResidencialViviendasProtegidas","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"ResidencialViviendasLibresPrimeraResidencia","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"ResidencialViviendasLibresSegundaResidencia","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"Oficinas","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"LocalesComerciales","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"UsoIndustrial","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"UsoHosteleroHabitaciones","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"PlazasGaraje","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"Trasteros","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"OtrosUsos","");  
            Utilidades.xml.Documento.addNodoHijo2NodoSuperior(DatosEdificios,xElement);  
            xElement = null;
            xElement =  Utilidades.xml.Documento.crearNodo(xmlDoc,"AprovechamientoM2");
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"ResidencialViviendasProtegidas","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"ResidencialViviendasLibresPrimeraResidencia","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"ResidencialViviendasLibresSegundaResidencia","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"Oficinas","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"LocalesComerciales","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"UsoIndustrial","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"UsoHostelero","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"PlazasGaraje","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"Trasteros","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"ZonaVerdeInstalacionesDeportivas","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"Dotacional","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"OtrosUsos","");  
            Utilidades.xml.Documento.addNodoHijo2NodoSuperior(DatosEdificios,xElement);  
            xElement = null;                                                */
            Utilidades.xml.Documento.addNodoHijo2NodoSuperior(Finca,DatosEdificios);             
            DatosEdificios = null;            
            
            if(Objetos.Tipos_aux.esHotel(solicitudes.tipoinm))
            {
                //HOTELES
                DatosHoteles = Utilidades.xml.Documento.crearNodo(xmlDoc,"DatosHoteles"); 
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosHoteles,"Categoria","");  
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosHoteles,"TipoDestino","");  
                    xElement =  Utilidades.xml.Documento.crearNodo(xmlDoc,"Instalaciones");
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"SalasConvencionesReuniones","");  
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"Restaurante","");  
                    Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"InstalacionesDeportivas","");  
                    Utilidades.xml.Documento.addNodoHijo2NodoSuperior(DatosHoteles,xElement);  
                    xElement = null;   
                Utilidades.xml.Documento.addNodoHijo2NodoSuperior(Finca,DatosHoteles);  
                DatosHoteles = null;    
            }//if
            
            if(Objetos.Tipos_aux.esNave(solicitudes.tipoinm))
            {
                //NAVES INDUSTRIALES
                DatosNavesIndustriales = Utilidades.xml.Documento.crearNodo(xmlDoc,"DatosNavesIndustriales"); 
                Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,DatosNavesIndustriales,"AlturaLibreM","");  
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosNavesIndustriales,"EspacioDiafano",false);  
                Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,DatosNavesIndustriales,"LuzEstandarDeEstructuraM","");  
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosNavesIndustriales,"Instalaciones",false);  
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosNavesIndustriales,"Sotanos",false);  
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosNavesIndustriales,"Ubicacion","");
                Utilidades.xml.Documento.addNodoHijo2NodoSuperior(Finca,DatosNavesIndustriales);  
                DatosNavesIndustriales = null; 
            }//if
            
            //SUELOS URBANO URBANIZABLES
            DatosSueloUrbanoUrbanizable = Utilidades.xml.Documento.crearNodo(xmlDoc,"DatosSueloUrbanoUrbaniza"); 
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosSueloUrbanoUrbanizable,"TipoSuelo","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosSueloUrbanoUrbanizable,"DesarrolloPlaneamiento","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosSueloUrbanoUrbanizable,"SistemaGestion","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosSueloUrbanoUrbanizable,"FaseGestion","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosSueloUrbanoUrbanizable,"ParalizacionUrbanizacion",false);  
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,DatosSueloUrbanoUrbanizable,"PorcentajeUrbanizacionEj","");  
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,DatosSueloUrbanoUrbanizable,"PorcentajeAmbitoValorado","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosSueloUrbanoUrbanizable,"ProximidadRespectoNucleo","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosSueloUrbanoUrbanizable,"ProyectoObra",false);  
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,DatosSueloUrbanoUrbanizable,"SuperficieTerrenoM2","");  
            /*xElement =  Utilidades.xml.Documento.crearNodo(xmlDoc,"AprovechamientoM2");
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"ResidencialViviendasProtegidas","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"ResidencialViviendasLibresPrimeraResidencia","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"ResidencialViviendasLibresSegundaResidencia","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"Oficinas","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"LocalesComerciales","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"UsoIndustrial","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"UsoHostelero","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"PlazasGaraje","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"Trasteros","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"OtrosUsos","");  
            Utilidades.xml.Documento.addNodoHijo2NodoSuperior(DatosSueloUrbanoUrbanizable,xElement);  
            xElement = null;  
            xElement =  Utilidades.xml.Documento.crearNodo(xmlDoc,"AprovechamientoUnidades");
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"ResidencialViviendasProtegidas","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"ResidencialViviendasLibresPrimeraResidencia","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"ResidencialViviendasLibresSegundaResidencia","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"Oficinas","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"LocalesComerciales","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"UsoIndustrial","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"UsoHosteleroHabitaciones","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"PlazasGaraje","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"Trasteros","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"ZonaVerdeInstalacionesDeportivas","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"Dotacional","");  
            Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"OtrosUsos","");  
            Utilidades.xml.Documento.addNodoHijo2NodoSuperior(DatosSueloUrbanoUrbanizable,xElement);  
            xElement = null;  */
            Utilidades.xml.Documento.addNodoHijo2NodoSuperior(Finca,DatosSueloUrbanoUrbanizable);  
            DatosSueloUrbanoUrbanizable = null; 
            
            if(Objetos.Tipos_aux.esFincaRustica(solicitudes.tipoinm))
            {
                //FINCAS RUSTICAS
                DatosFincasRusticas = Utilidades.xml.Documento.crearNodo(xmlDoc,"DatosFincasRusticas"); 
                Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,DatosFincasRusticas,"SuperficieTotalHa","");  
                Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,DatosFincasRusticas,"SuperficieCultivosSecaHa","");  
                Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,DatosFincasRusticas,"SuperficieCultivosRegaHa","");  
                Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,DatosFincasRusticas,"SuperficieOtrosUsosAgrHa","");  
                Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,DatosFincasRusticas,"SuperficieUsosGanaderoHa","");  
                Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,DatosFincasRusticas,"SuperficieConstruidaViM2","");  
                Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,DatosFincasRusticas,"SupConstruidaUsosAgroM2","");  
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosFincasRusticas,"AguaRegadioLegalizada",false);  
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosFincasRusticas,"EnergiaElectrica",false);  
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosFincasRusticas,"AccesoInfraestructurasVi",false);  
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosFincasRusticas,"UsoActualDelSuelo","");  
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,DatosFincasRusticas,"AprovechamientoEsperado",false);  
                Utilidades.xml.Documento.addNodoHijo2NodoSuperior(Finca,DatosFincasRusticas);  
                DatosFincasRusticas = null; 
            }//if
            
            //APROVECHAMIENTO UNIDADES
            AprovechamientoUnidades = Utilidades.xml.Documento.crearNodo(xmlDoc,"AprovechamientoUnidades"); 
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,AprovechamientoUnidades,"ResViviendasProt",""); 
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,AprovechamientoUnidades,"ResiViviendasLibre",""); 
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,AprovechamientoUnidades,"ResiViviendasLibreSeg",""); 
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,AprovechamientoUnidades,"Oficinas",""); 
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,AprovechamientoUnidades,"LocalesComerciales",""); 
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,AprovechamientoUnidades,"UsoIndustrial",""); 
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,AprovechamientoUnidades,"UsoHosteleroHabitaciones",""); 
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,AprovechamientoUnidades,"PlazasGaraje",""); 
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,AprovechamientoUnidades,"Trasteros",""); 
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,AprovechamientoUnidades,"OtrosUsos",""); 
            Utilidades.xml.Documento.addNodoHijo2NodoSuperior(Finca,AprovechamientoUnidades);  
            AprovechamientoUnidades = null; 
            
            //APROVECHAMIENTO M2
            AprovechamientoM2 = Utilidades.xml.Documento.crearNodo(xmlDoc,"AprovechamientoM2");  
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,AprovechamientoM2,"ResViviendasProt",""); 
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,AprovechamientoM2,"ResiViviendasLibre",""); 
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,AprovechamientoM2,"ResiViviendasLibreSeg",""); 
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,AprovechamientoM2,"Oficinas",""); 
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,AprovechamientoM2,"LocalesComerciales",""); 
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,AprovechamientoM2,"UsoIndustrial",""); 
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,AprovechamientoM2,"UsoHostelero",""); 
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,AprovechamientoM2,"PlazasGaraje",""); 
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,AprovechamientoM2,"Trasteros",""); 
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,AprovechamientoM2,"ZonaVerdeInstalacionesDe",""); 
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,AprovechamientoM2,"Dotacional",""); 
            Utilidades.xml.Documento.addHoja2NodoIfNeitherNullNorBlank(xmlDoc,AprovechamientoM2,"OtrosUsos","");
            Utilidades.xml.Documento.addNodoHijo2NodoSuperior(Finca,AprovechamientoM2);  
            AprovechamientoM2 = null; 
            
            //CARGAS INMUEBLE 1-->N (for)
            CargasInmueble = Utilidades.xml.Documento.crearNodo(xmlDoc,"CargasInmueble");
                xElement = Utilidades.xml.Documento.crearNodo(xmlDoc,"Carga"); 
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"IdentPersonaAFavorCarga","");
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"Concepto","");
                Utilidades.xml.Documento.addHoja2Nodo(xmlDoc,xElement,"ImporteCargaImueble","");
                Utilidades.xml.Documento.addNodoHijo2NodoSuperior(CargasInmueble,xElement);
                xElement = null;
            Utilidades.xml.Documento.addNodoHijo2NodoSuperior(Finca,CargasInmueble);
            CargasInmueble = null; 
        }
        catch (Exception e)
        {
            Finca = null;
        }
        finally
        {
            return Finca;
        }
    }//generaNwFinca
    
    //************************************************************************************************************************************
    //************************************************************************************************************************************
   
    
    //*********************************************************************************************************************************
    //*********************************************************************************************************************************
    public static String getTipoImpuesto(Double iva)
    {
        String oString = "";
        if(iva!=null)
        {
            if(iva.intValue()==21) oString = "IVA 21%";
            else if(iva.intValue()==18) oString = "IVA 18%";
            else if(iva.intValue()==16) oString = "IVA 16%";
            else if(iva.intValue()==7) oString = "IPSI 7%";
            else if(iva.intValue()==9) oString = "IGIC 9%";
        }//if
        return oString;
    }//getTipoImpuesto
    
    //*********************************************************************************************************************************
    //*********************************************************************************************************************************
    public static String getTipoVivienda(String tipoinm)
    {
       String oString = "";
       if(tipoinm!=null && !tipoinm.trim().equals(""))
       {
           if(tipoinm.equals("LCB")) oString = "6";
           else if(tipoinm.equals("LCC")) oString = "Local comercial";
           else if(tipoinm.equals("LCS")) oString = "Local comercial";
           else if(tipoinm.equals("LCT")) oString = "Local comercial";
           else if(tipoinm.equals("NMB")) oString = "Nave industrial";
           else if(tipoinm.equals("NMC")) oString = "Nave industrial";
           else if(tipoinm.equals("NMS")) oString = "Nave industrial";
       }//if
       return oString;
    }//getTipoVivienda
    
    //*********************************************************************************************************************************
    //*********************************************************************************************************************************
    public static String getTipoInmueble(String tipoinm)
    {
       String oString = "";
       if(tipoinm!=null && !tipoinm.trim().equals(""))
       {
           if(tipoinm.equals("LCB")) oString = "6";
           else if(tipoinm.equals("LCC")) oString = "Local comercial";
           else if(tipoinm.equals("LCS")) oString = "Local comercial";
           else if(tipoinm.equals("LCT")) oString = "Local comercial";
           else if(tipoinm.equals("NMB")) oString = "Nave industrial";
           else if(tipoinm.equals("NMC")) oString = "Nave industrial";
           else if(tipoinm.equals("NMS")) oString = "Nave industrial";
           else if(tipoinm.equals("NMT")) oString = "Nave industrial";
           else if(tipoinm.equals("NUB")) oString = "Nave industrial";
           else if(tipoinm.equals("NUC")) oString = "Nave industrial";
           else if(tipoinm.equals("NUS")) oString = "Nave industrial";
           else if(tipoinm.equals("NUT")) oString = "Nave industrial";
           else if(tipoinm.equals("OFB")) oString = "Oficina";
           else if(tipoinm.equals("OFC")) oString = "Oficina";
           else if(tipoinm.equals("OFT")) oString = "Oficina";
           else if(tipoinm.equals("PZC")) oString = "Plaza de garaje";
           else if(tipoinm.equals("PZG")) oString = "Plaza de garaje";
           else if(tipoinm.equals("PZT")) oString = "Plaza de garaje";
           else if(tipoinm.equals("TRC")) oString = "Trastero";
           else if(tipoinm.equals("TRT")) oString = "Trastero";
           else if(tipoinm.equals("VPC")) oString = "Vivienda";
           else if(tipoinm.equals("VPS")) oString = "Vivienda";
           else if(tipoinm.equals("VPT")) oString = "Vivienda";
           else if(tipoinm.equals("VUT")) oString = "Vivienda";
           else if(tipoinm.equals("VUC")) oString = "Vivienda";  
           else if(tipoinm.equals("VUE")) oString = "Vivienda";                                      
           else if(tipoinm.equals("XXX")) oString = "";
           else if(tipoinm.equals("ACB")) oString = "";
           else if(tipoinm.equals("ACM")) oString = "";
           else if(tipoinm.equals("ADC")) oString = "Otras edificaciones";
           else if(tipoinm.equals("ADT")) oString = "Otras edificaciones";
           else if(tipoinm.equals("ANX")) oString = "Otras edificaciones";
           else if(tipoinm.equals("ART")) oString = "";
           else if(tipoinm.equals("AXV")) oString = "Otras edificaciones";
           else if(tipoinm.equals("BPA")) oString = "";
           else if(tipoinm.equals("BPB")) oString = "";
           else if(tipoinm.equals("BRM")) oString = "";
           else if(tipoinm.equals("BRV")) oString = "";
           else if(tipoinm.equals("BTM")) oString = "";
           else if(tipoinm.equals("C.D")) oString = "Otras edificaciones";
           else if(tipoinm.equals("CDC")) oString = "Otras edificaciones";
           else if(tipoinm.equals("CDT")) oString = "Otras edificaciones";
           else if(tipoinm.equals("CPC")) oString = "Otras edificaciones";
           else if(tipoinm.equals("CPG")) oString = "";
           else if(tipoinm.equals("CPT")) oString = "Otras edificaciones";
           else if(tipoinm.equals("DIN")) oString = "";
           else if(tipoinm.equals("DIO")) oString = "";
           else if(tipoinm.equals("DPE")) oString = "";
           else if(tipoinm.equals("DRV")) oString = "";
           else if(tipoinm.equals("ECC")) oString = "Resto de edificios";
           else if(tipoinm.equals("ECT")) oString = "Resto de edificios";
           else if(tipoinm.equals("EDC")) oString = "Resto de edificios";
           else if(tipoinm.equals("EDE")) oString = "Resto de edificios";
           else if(tipoinm.equals("EDT")) oString = "Resto de edificios";
           else if(tipoinm.equals("EGC")) oString = "Resto de edificios";
           else if(tipoinm.equals("EGT")) oString = "Resto de edificios";
           else if(tipoinm.equals("EIC")) oString = "Resto de edificios";
           else if(tipoinm.equals("EIE")) oString = "Resto de edificios";
           else if(tipoinm.equals("EIT")) oString = "Resto de edificios";
           else if(tipoinm.equals("EMS")) oString = "Otras edificaciones";
           else if(tipoinm.equals("EOC")) oString = "Resto de edificios";
           else if(tipoinm.equals("EOT")) oString = "Resto de edificios";
           else if(tipoinm.equals("EPA")) oString = "";
           else if(tipoinm.equals("EPB")) oString = "";
           else if(tipoinm.equals("EPC")) oString = "";
           else if(tipoinm.equals("EQI")) oString = "";
           else if(tipoinm.equals("EQO")) oString = "";
           else if(tipoinm.equals("ERC")) oString = "Resto de edificios";
           else if(tipoinm.equals("ERE")) oString = "Resto de edificios";
           else if(tipoinm.equals("ERM")) oString = "Otras edificaciones";
           else if(tipoinm.equals("ERT")) oString = "Resto de edificios";
           else if(tipoinm.equals("ERV")) oString = "Otras edificaciones";
           else if(tipoinm.equals("ETC")) oString = "Otras edificaciones";
           else if(tipoinm.equals("ETM")) oString = "Resto de edificios";
           else if(tipoinm.equals("ETR")) oString = "Resto de edificios";
           else if(tipoinm.equals("ETT")) oString = "Otras edificaciones";
           else if(tipoinm.equals("EXM")) oString = "";
           else if(tipoinm.equals("FCE")) oString = "Finca rústica";
           else if(tipoinm.equals("FCM")) oString = "";
           else if(tipoinm.equals("FCR")) oString = "Finca rústica";
           else if(tipoinm.equals("GAC")) oString = "Otras edificaciones";
           else if(tipoinm.equals("GAE")) oString = "Otras edificaciones";
           else if(tipoinm.equals("GAS")) oString = "Otras edificaciones";
           else if(tipoinm.equals("GAT")) oString = "Otras edificaciones";
           else if(tipoinm.equals("HTC")) oString = "Hotel";
           else if(tipoinm.equals("HTE")) oString = "Hotel";
           else if(tipoinm.equals("HTT")) oString = "Hotel";
           else if(tipoinm.equals("IFC")) oString = "";
           else if(tipoinm.equals("IFE")) oString = "";
           else if(tipoinm.equals("INJ")) oString = "";
           else if(tipoinm.equals("INP")) oString = "";
           else if(tipoinm.equals("IN5")) oString = "";
           else if(tipoinm.equals("LCE")) oString = "Local comercial";
           else if(tipoinm.equals("MAQ")) oString = "";
           else if(tipoinm.equals("MOB")) oString = "";
           else if(tipoinm.equals("NUE")) oString = "Nave industrial";
           else if(tipoinm.equals("OFE")) oString = "Oficina";
           else if(tipoinm.equals("PAT")) oString = "";
           else if(tipoinm.equals("PDT")) oString = "";
           else if(tipoinm.equals("PKD")) oString = "Otras edificaciones";
           else if(tipoinm.equals("PRN")) oString = "Otras edificaciones";
           else if(tipoinm.equals("PRT")) oString = "";
           else if(tipoinm.equals("PZE")) oString = "Plaza de garaje";
           else if(tipoinm.equals("PZL")) oString = "Plaza de garaje";
           else if(tipoinm.equals("PZV")) oString = "Plaza de garaje";
           else if(tipoinm.equals("RCE")) oString = "";
           else if(tipoinm.equals("SGO")) oString = "";
           else if(tipoinm.equals("SGV")) oString = "";
           else if(tipoinm.equals("SOE")) oString = "Suelo urbano o urbanizable";
           else if(tipoinm.equals("SOL")) oString = "Suelo urbano o urbanizable";
           else if(tipoinm.equals("TEE")) oString = "Terrenos";
           else if(tipoinm.equals("TRE")) oString = "Trastero";
           else if(tipoinm.equals("TRL")) oString = "Trastero";
           else if(tipoinm.equals("TRR")) oString = "Terrenos";
           else if(tipoinm.equals("TRV")) oString = "Trastero";
           else if(tipoinm.equals("TRZ")) oString = "Otras edificaciones";
           else if(tipoinm.equals("VEH")) oString = "";
           else if(tipoinm.equals("VPE")) oString = "Vivienda";
           else if(tipoinm.equals("VPI")) oString = "Vivienda";
       }//if
       return oString;
    }//getTipoInmueble
    
    //*********************************************************************************************************************************
    //*********************************************************************************************************************************
    public static void main(String[] args) 
    {
        String numexp = null;
        java.sql.Connection connection = null;
        try
        {
            numexp = "103.254562-13";
            connection = Utilidades.Conexion.getConnectionValtecnic2();
            TasacionActivo nwTasacionActivo = new TasacionActivo(numexp,connection);
            nwTasacionActivo = null;
        }//try
        catch(Exception e)
        {
            System.out.print(e);
        }//catch
        finally
        {
            try
            {
                if(connection!=null) connection.close();
            }//try
            catch(Exception e){}
            connection = null;
            numexp = null;            
        }//finally        
    }//main
    
    //************************************************************************************************************************************
    //************************************************************************************************************************************
    
}//TasacionActivo
