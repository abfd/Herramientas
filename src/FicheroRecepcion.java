/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 *
 * @author Administrador
 */
public class FicheroRecepcion 
{

    //*****************************************************************************************************************
    //*****************************************************************************************************************
    private org.apache.log4j.Logger oLogger = null;
    private java.io.File oArchivo = null;
    private java.sql.Connection oConexionValtecnic = null;
    private java.sql.Connection oConexionGecopinsa = null;
    private Objetos.Localidades oLocalidad = null;
    private Objetos.Municipios oMunicipio = null;
    private Objetos.Provincias oProvincia = null;
    private Objetos.Clidir oClidir = null;
    private String codcli_expediente = this.codcli;
    
    public static final String codcli = "103";
    public static final String codcli_bbva_blue = "903";
    private int iNumeroRegistrosAfectados = 0;
    private int iContadorTotal = 0;
    //private String emailControl = "alberto.sanz@valtecnic.com";
    private String emailControl = "sede@valtecnic.com";

    //*****************************************************************************************************************
    //*****************************************************************************************************************
    public FicheroRecepcion(String path, String nombreFichero, org.apache.log4j.Logger logger, java.sql.Connection conexionValtecnic, java.sql.Connection conexionGecopinsa) throws java.sql.SQLException, Exception
    {
        oLogger = logger;
        if ((path != null) && (!(path.equals(""))) && (nombreFichero != null) && (!(nombreFichero.equals("")))) 
        {
            this.oArchivo = new java.io.File(path, nombreFichero);
            if (!(this.oArchivo.exists())) 
            {
                if (oLogger != null) oLogger.error("NO EXISTE EL FICHERO ESPECIFICADO PARA EL PROCESAMIENTO. RUTA: " + this.oArchivo.getAbsolutePath());
                throw new java.io.IOException("NO EXISTE EL FICHERO ESPECIFICADO PARA EL PROCESAMIENTO. RUTA: " + this.oArchivo.getAbsolutePath());
            }//if
        }//if
        else 
        {
            if (oLogger != null) oLogger.error("FALTAN LOS DATOS DE ENTRADA DEL FICHERO DE PETICIONES DE BBVA.");
            throw new java.io.IOException("FALTAN LOS DATOS DE ENTRADA DEL FICHERO DE PETICIONES DE BBVA.");
        }//else
        oConexionValtecnic = conexionValtecnic;
        oConexionGecopinsa = conexionGecopinsa;
        secuenciaObjeto();
    }//FicheroRecepcion

    //*****************************************************************************************************************
    //*****************************************************************************************************************
    public int getRegistrosAfectados() 
    {
        return this.iContadorTotal;
    }//getRegistrosAfectados

    //*****************************************************************************************************************
    //*****************************************************************************************************************
    public void secuenciaObjeto() throws java.sql.SQLException, Exception 
    {
        if (oLogger != null) oLogger.info("SE VA A PROCESAR EL FICHERO: " + this.oArchivo.getAbsolutePath());
        String sTextoFichero = Utilidades.Archivos.readTextFile(oArchivo);
        String[] sEncargoParticular = sTextoFichero.split("\n");
        if (sEncargoParticular.length > 0) 
        {
            for (int iContador = 0; iContador < sEncargoParticular.length; iContador++) 
            {
                try
                {
                    this.iNumeroRegistrosAfectados = procesarFicheroEncargo(sEncargoParticular[iContador].replaceAll("\\|\\|", "\\| \\|").split("\\|"));
                    // - UN SEGUNDO DE RETARDO PARA QUE LOS NÚMEROS DE EXPEDIENTE NO SE SOLAPEN
                    Thread.sleep(Long.parseLong("1000"));
                }//try
                catch(Exception e)
                {
                    if (oLogger != null) oLogger.fatal(e);
                }
            }//for
        }//if
        else 
        {
            if (oLogger != null) oLogger.info("NO HAY ENCARGOS DENTRO DEL FICHERO SELECCIONADO.");
        }//else
        sEncargoParticular = null;
        sTextoFichero = null;
        this.oArchivo = null;
    }//secuenciaObjeto
    
    //*****************************************************************************************************************
    //*****************************************************************************************************************
    private int procesarFicheroEncargo(String[] datosEncargoParticular) throws java.sql.SQLException, Exception 
    {
        iNumeroRegistrosAfectados = 0;
        if ((datosEncargoParticular != null) && (datosEncargoParticular.length > 0)) 
        {
            if (oLogger != null) oLogger.info("PROCESAMIENTO DEL EXPEDIENTE: " + datosEncargoParticular[0]);
            java.sql.Connection oConexion = null;
            if(datosEncargoParticular[3].equals(Clientes.bbva.anida.logica.Conversiones.CODIGO_VALTECNIC)) oConexion = oConexionValtecnic;
            else if(datosEncargoParticular[3].equals(Clientes.bbva.anida.logica.Conversiones.CODIGO_GECOPINSA)) oConexion = oConexionGecopinsa;
            else oConexion = oConexionValtecnic;
            if(!Objetos.Refer.existsWhereReferenciaAndCodcli(datosEncargoParticular[0],codcli_expediente, oConexion))
            {
                oLocalidad = new Objetos.Localidades(); 
                if(datosEncargoParticular[10]!=null && !datosEncargoParticular[10].trim().equals("") && !oLocalidad.load(new Integer(datosEncargoParticular[10].trim()), oConexion))
                {
                    // - NO HAY CÓDIGO POSTAL
                    if(oLogger!=null) oLogger.info("- EXP: "+datosEncargoParticular[0]+" - NO EXISTE LA LOCALIDAD. CODPOS: "+datosEncargoParticular[10]);
                    // - SE INTENTA CARGAR LOS DATOS CON EL NOMBRE DE POBLACIÓN
                    String sCodpro = Objetos.Localidades.getCodpro(datosEncargoParticular[7].trim().toUpperCase(), oConexion); 
                    if(sCodpro!=null && !sCodpro.equals(""))
                    {
                        oLocalidad.localidad = datosEncargoParticular[7].trim().toUpperCase();
                        oMunicipio = new Objetos.Municipios();
                        oMunicipio.municipio = "A DETERMINAR";
                        oProvincia = new Objetos.Provincias();
                        if (!oProvincia.load(new Integer(sCodpro),oConexion) && oLogger != null)
                        {
                            oProvincia.nompro = "A DETERMINAR";
                            oLogger.info("- EXP: "+datosEncargoParticular[0]+" - NO EXISTE EL NOMBRE DE LA PROVINCIA CON EL CÓDIGO DE PROVINCIA: " + sCodpro);
                        }//if
                    }//if
                    else
                    {
                        // - SE INTENTA OBTENER EL CODPRO CON LOS DOS PRIMEROS DÍGITOS DEL CODPOS
                        sCodpro = datosEncargoParticular[10].substring(2);
                        if(sCodpro!=null && !sCodpro.equals(""))
                        {
                            oLocalidad.localidad = datosEncargoParticular[7].trim().toUpperCase();
                            oMunicipio = new Objetos.Municipios();
                            oMunicipio.municipio = "A DETERMINAR";
                            oProvincia = new Objetos.Provincias();
                            if (!oProvincia.load(new Integer(sCodpro),oConexion) && oLogger != null)
                            {
                                oProvincia.nompro = "A DETERMINAR";
                                oLogger.info("- EXP: "+datosEncargoParticular[0]+" - NO EXISTE EL NOMBRE DE LA PROVINCIA CON EL CÓDIGO DE PROVINCIA: " + sCodpro);
                            }//if
                        }//if
                        else if(oLogger!=null) oLogger.info(" - EXP: "+datosEncargoParticular[0]+" - NO SE PUEDEN OBTENER DATOS DE UBICACIÓN PARA EL EXPEDIENTE. PROCESAMIENTO MANUAL. LOCALIDAD NO ENCONTRADA: " + datosEncargoParticular[7]);
                    }//else
                    sCodpro = null;
                }//if
                else
                {
                    oProvincia = new Objetos.Provincias();
                    oProvincia.nompro = "A DETERMINAR";
                    oLocalidad.localidad = datosEncargoParticular[7].trim().toUpperCase();
                    if(datosEncargoParticular[3].equals(Clientes.bbva.anida.logica.Conversiones.CODIGO_GECOPINSA)) 
                    {
                        oProvincia.prefijo = "9";
                        oProvincia.coddel = new Integer(21);
                    }
                    oLocalidad.load(datosEncargoParticular[7].trim().toUpperCase(), oConexion);
                    oMunicipio = new Objetos.Municipios();
                    oMunicipio.municipio = "A DETERMINAR";
                    if (oMunicipio.load(oLocalidad.codine, oConexion)) 
                    {
                        if (!(oProvincia.load(new Integer(oMunicipio.codpro), oConexion))) 
                        {
                            if ((!(oProvincia.load(new Integer(datosEncargoParticular[10].substring(0, 2)), oConexion))) && oLogger != null)
                            {
                                oProvincia.nompro = "A DETERMINAR";
                                oLogger.info("NO EXISTE EL NOMBRE DE LA PROVINCIA CON EL CÓDIGO DE PROVINCIA: " + datosEncargoParticular[10].substring(0, 2));
                            }//if
                        }//if
                    }//if

                }//else
                //-----------------------------------------------------------------------
                //-----------------------------------------------------------------------
                if(oLocalidad.localidad!=null && oProvincia.prefijo!=null && !oProvincia.prefijo.trim().equals(""))
                {
                    // - EXISTE DATOS DE UBICACIÓN
                    String sNumeroExpediente = null;
                    if(datosEncargoParticular[3].equals(Clientes.bbva.anida.logica.Conversiones.CODIGO_VALTECNIC)) sNumeroExpediente = oProvincia.prefijo+codcli.substring(1);
                    else if(datosEncargoParticular[3].equals(Clientes.bbva.anida.logica.Conversiones.CODIGO_GECOPINSA)) sNumeroExpediente = "9"+codcli.substring(1);
                    sNumeroExpediente += ".";
                    sNumeroExpediente += Utilidades.Cadenas.getValorMayusculasSinBlancos(datosEncargoParticular[0]);
                    sNumeroExpediente += "-"+Utilidades.Cadenas.getDateParse(new java.util.Date(),"yy");
                    
                    /*if(datosEncargoParticular[3].equals(Clientes.bbva.anida.logica.Conversiones.CODIGO_VALTECNIC)) sNumeroExpediente = Utilidades.Valtecnic.numero_automatico("103", Utilidades.Constantes.baseDatosValtecnic, oProvincia.nompro, new java.text.SimpleDateFormat("dd-MM-yyyy").format(new java.util.Date()), oConexion);
                    else if(datosEncargoParticular[3].equals(Clientes.bbva.anida.logica.Conversiones.CODIGO_GECOPINSA)) sNumeroExpediente = Utilidades.Valtecnic.numero_automatico("103", Utilidades.Constantes.baseDatosGecopinsa, oProvincia.nompro, new java.text.SimpleDateFormat("dd-MM-yyyy").format(new java.util.Date()), oConexion);
                    else sNumeroExpediente = Utilidades.Valtecnic.numero_automatico("103", Utilidades.Constantes.baseDatosValtecnic, oProvincia.nompro, new java.text.SimpleDateFormat("dd-MM-yyyy").format(new java.util.Date()), oConexion);*/
                    oClidir = new Objetos.Clidir();
                    codcli_expediente = codcli;
                    if (datosEncargoParticular.length >= 55 && datosEncargoParticular[54]!=null && !datosEncargoParticular[54].trim().equals("")) 
                    {
                        if(datosEncargoParticular[54].toUpperCase().indexOf("VEN A CASA")!=-1 || datosEncargoParticular[54].toUpperCase().indexOf("BLUE JOVEN")!=-1) 
                            codcli_expediente = codcli_bbva_blue;
                    }//if
                    if (!oClidir.load(Integer.parseInt(codcli_expediente), datosEncargoParticular[48].trim(), oConexion))
                    {
                        if(!oClidir.load(Integer.parseInt(codcli_expediente), Utilidades.Cadenas.completTextWithLeftCharacter(datosEncargoParticular[48].trim(),'0',4), oConexion))
                        {
                            if (oLogger != null) oLogger.info(" - EXP: "+sNumeroExpediente+" - NO EXISTEN LOS DATOS DE LA OFICINA QUE HACE EL ENCARGO - CLIENTE: " + "103" + " - OFICINA: " + datosEncargoParticular[48].trim());
                            Utilidades.Correo.sendMail("valtecnic.com", "informatica@valtecnic.com", this.emailControl, null, null, "CREAR NUEVA OFICINA BBVA", "SE HA DADO DE ALTA EL EXPEDIENTE '" + sNumeroExpediente + "' PERO NO EXISTEN LOS DATOS DE LA OFICINA QUE HACE EL ENCARGO - CLIENTE: " + "103" + " - OFICINA: " + datosEncargoParticular[48].trim(), false);
                        }//if
                    }//if
                    if(oClidir!=null && oClidir.oficina!=null && oClidir.oficina.trim().length()<4) oClidir.oficina = Utilidades.Cadenas.completTextWithLeftCharacter(oClidir.oficina,'0',4);
                    int iNumeroGarantia = 1;
                    if(!Objetos.Solicitudes.exists(sNumeroExpediente, oConexion))
                    {
                        this.iNumeroRegistrosAfectados = procesarEncargoParticular(sNumeroExpediente + Clientes.bbva.anida.logica.Conversiones.getLetraGarantia(iNumeroGarantia), iNumeroGarantia, datosEncargoParticular, datosEncargoParticular[6], datosEncargoParticular[7], Clientes.bbva.anida.logica.Conversiones.convertCodigoToTipoinmValtecnic(datosEncargoParticular[8]), datosEncargoParticular[9], datosEncargoParticular[10],oConexion);
                        if(iNumeroRegistrosAfectados==1)
                        {
                            Objetos.v2.Operclientes oOperclientes = new Objetos.v2.Operclientes();
                            oOperclientes.cliente = new Integer(codcli_expediente);
                            oOperclientes.numexp = sNumeroExpediente + Clientes.bbva.anida.logica.Conversiones.getLetraGarantia(iNumeroGarantia);
                            oOperclientes.refcliente = Utilidades.Cadenas.getValorMayusculasSinBlancos(datosEncargoParticular[0]);
                            oOperclientes.tipoperacion = "REC";
                            oOperclientes.tipomensaje = "STA";
                            oOperclientes.control = new Integer(0);
                            oOperclientes.postventa = new Integer(0);
                            oOperclientes.setFchenvioNow();
                            oOperclientes.setHoraenvioNow();
                            oOperclientes.estado = "001";
                            iNumeroRegistrosAfectados = oOperclientes.insert(oConexion);
                            oOperclientes = null;
                        }//if
                        if ((this.iNumeroRegistrosAfectados == 1) && (oLogger != null)) oLogger.info("SE HA INSERTADO CORRECTAMENTE EL INMUEBLE PRINCIPAL.");
                        if ((this.iNumeroRegistrosAfectados == 1) && (datosEncargoParticular[11] != null) && (!(datosEncargoParticular[11].trim().equals("")))) 
                        {
                            iNumeroGarantia++;
                            this.iNumeroRegistrosAfectados = procesarEncargoParticular(sNumeroExpediente + Clientes.bbva.anida.logica.Conversiones.getLetraGarantia(iNumeroGarantia), iNumeroGarantia, datosEncargoParticular, datosEncargoParticular[11], datosEncargoParticular[12], Clientes.bbva.anida.logica.Conversiones.convertCodigoToTipoinmValtecnic(datosEncargoParticular[13]), datosEncargoParticular[14], datosEncargoParticular[15],oConexion);
                            if (this.iNumeroRegistrosAfectados == 1)
                            { 
                                Objetos.v2.Operclientes oOperclientes = new Objetos.v2.Operclientes();
                                oOperclientes.cliente = new Integer(codcli_expediente);
                                oOperclientes.numexp = sNumeroExpediente + Clientes.bbva.anida.logica.Conversiones.getLetraGarantia(iNumeroGarantia);
                                oOperclientes.refcliente = Utilidades.Cadenas.getValorMayusculasSinBlancos(datosEncargoParticular[0]);
                                oOperclientes.tipoperacion = "REC";
                                oOperclientes.tipomensaje = "STA";
                                oOperclientes.control = new Integer(0);
                                oOperclientes.postventa = new Integer(0);
                                oOperclientes.setFchenvioNow();
                                oOperclientes.setHoraenvioNow();
                                oOperclientes.estado = "001";
                                iNumeroRegistrosAfectados = oOperclientes.insert(oConexion);
                                oOperclientes = null;
                                if(iNumeroRegistrosAfectados==1 && oLogger!=null) oLogger.info(" - EXP: "+sNumeroExpediente + Clientes.bbva.anida.logica.Conversiones.getLetraGarantia(iNumeroGarantia)+" - SE HA INSERTADO CORRECTAMENTE EL INMUEBLE 2.");
                            }//if
                        }//if
                        if ((this.iNumeroRegistrosAfectados == 1) && (datosEncargoParticular[16] != null) && (!(datosEncargoParticular[16].trim().equals("")))) 
                        {
                            iNumeroGarantia++;
                            this.iNumeroRegistrosAfectados = procesarEncargoParticular(sNumeroExpediente + Clientes.bbva.anida.logica.Conversiones.getLetraGarantia(iNumeroGarantia), iNumeroGarantia, datosEncargoParticular, datosEncargoParticular[16], datosEncargoParticular[17], Clientes.bbva.anida.logica.Conversiones.convertCodigoToTipoinmValtecnic(datosEncargoParticular[18]), datosEncargoParticular[19], datosEncargoParticular[20],oConexion);
                            if (this.iNumeroRegistrosAfectados == 1)
                            { 
                                Objetos.v2.Operclientes oOperclientes = new Objetos.v2.Operclientes();
                                oOperclientes.cliente = new Integer(codcli_expediente);
                                oOperclientes.numexp = sNumeroExpediente + Clientes.bbva.anida.logica.Conversiones.getLetraGarantia(iNumeroGarantia);
                                oOperclientes.refcliente = Utilidades.Cadenas.getValorMayusculasSinBlancos(datosEncargoParticular[0]);
                                oOperclientes.tipoperacion = "REC";
                                oOperclientes.tipomensaje = "STA";
                                oOperclientes.control = new Integer(0);
                                oOperclientes.postventa = new Integer(0);
                                oOperclientes.setFchenvioNow();
                                oOperclientes.setHoraenvioNow();
                                oOperclientes.estado = "001";
                                iNumeroRegistrosAfectados = oOperclientes.insert(oConexion);
                                oOperclientes = null;
                                if(iNumeroRegistrosAfectados==1 && oLogger!=null) oLogger.info(" - EXP: "+sNumeroExpediente + Clientes.bbva.anida.logica.Conversiones.getLetraGarantia(iNumeroGarantia)+" - SE HA INSERTADO CORRECTAMENTE EL INMUEBLE 3.");
                            }//if
                        }//if
                        if ((this.iNumeroRegistrosAfectados == 1) && (datosEncargoParticular[21] != null) && (!(datosEncargoParticular[21].trim().equals("")))) 
                        {
                            iNumeroGarantia++;
                            this.iNumeroRegistrosAfectados = procesarEncargoParticular(sNumeroExpediente + Clientes.bbva.anida.logica.Conversiones.getLetraGarantia(iNumeroGarantia), iNumeroGarantia, datosEncargoParticular, datosEncargoParticular[21], datosEncargoParticular[22], Clientes.bbva.anida.logica.Conversiones.convertCodigoToTipoinmValtecnic(datosEncargoParticular[23]), datosEncargoParticular[24], datosEncargoParticular[25],oConexion);
                            if (this.iNumeroRegistrosAfectados == 1)
                            { 
                                Objetos.v2.Operclientes oOperclientes = new Objetos.v2.Operclientes();
                                oOperclientes.cliente = new Integer(codcli_expediente);
                                oOperclientes.numexp = sNumeroExpediente + Clientes.bbva.anida.logica.Conversiones.getLetraGarantia(iNumeroGarantia);
                                oOperclientes.refcliente = Utilidades.Cadenas.getValorMayusculasSinBlancos(datosEncargoParticular[0]);
                                oOperclientes.tipoperacion = "REC";
                                oOperclientes.tipomensaje = "STA";
                                oOperclientes.control = new Integer(0);
                                oOperclientes.postventa = new Integer(0);
                                oOperclientes.setFchenvioNow();
                                oOperclientes.setHoraenvioNow();
                                oOperclientes.estado = "001";
                                iNumeroRegistrosAfectados = oOperclientes.insert(oConexion);
                                oOperclientes = null;
                                if(iNumeroRegistrosAfectados==1 && oLogger!=null) oLogger.info(" - EXP: "+sNumeroExpediente + Clientes.bbva.anida.logica.Conversiones.getLetraGarantia(iNumeroGarantia)+" - SE HA INSERTADO CORRECTAMENTE EL INMUEBLE 4.");
                            }//if
                        }//if
                        if ((this.iNumeroRegistrosAfectados == 1) && (datosEncargoParticular[26] != null) && (!(datosEncargoParticular[26].trim().equals("")))) 
                        {
                            iNumeroGarantia++;
                            this.iNumeroRegistrosAfectados = procesarEncargoParticular(sNumeroExpediente + Clientes.bbva.anida.logica.Conversiones.getLetraGarantia(iNumeroGarantia), iNumeroGarantia, datosEncargoParticular, datosEncargoParticular[26], datosEncargoParticular[27], Clientes.bbva.anida.logica.Conversiones.convertCodigoToTipoinmValtecnic(datosEncargoParticular[28]), datosEncargoParticular[29], datosEncargoParticular[30],oConexion);
                            if (this.iNumeroRegistrosAfectados == 1)
                            { 
                                Objetos.v2.Operclientes oOperclientes = new Objetos.v2.Operclientes();
                                oOperclientes.cliente = new Integer(codcli_expediente);
                                oOperclientes.numexp = sNumeroExpediente + Clientes.bbva.anida.logica.Conversiones.getLetraGarantia(iNumeroGarantia);
                                oOperclientes.refcliente = Utilidades.Cadenas.getValorMayusculasSinBlancos(datosEncargoParticular[0]);
                                oOperclientes.tipoperacion = "REC";
                                oOperclientes.tipomensaje = "STA";
                                oOperclientes.control = new Integer(0);
                                oOperclientes.postventa = new Integer(0);
                                oOperclientes.setFchenvioNow();
                                oOperclientes.setHoraenvioNow();
                                oOperclientes.estado = "001";
                                iNumeroRegistrosAfectados = oOperclientes.insert(oConexion);
                                oOperclientes = null;
                                if(iNumeroRegistrosAfectados==1 && oLogger!=null) oLogger.info(" - EXP: "+sNumeroExpediente + Clientes.bbva.anida.logica.Conversiones.getLetraGarantia(iNumeroGarantia)+" - SE HA INSERTADO CORRECTAMENTE EL INMUEBLE 5.");
                            }//if
                        }//if
                        if ((this.iNumeroRegistrosAfectados == 1) && (datosEncargoParticular[31] != null) && (!(datosEncargoParticular[31].trim().equals("")))) 
                        {
                            iNumeroGarantia++;
                            this.iNumeroRegistrosAfectados = procesarEncargoParticular(sNumeroExpediente + Clientes.bbva.anida.logica.Conversiones.getLetraGarantia(iNumeroGarantia), iNumeroGarantia, datosEncargoParticular, datosEncargoParticular[31], datosEncargoParticular[32], Clientes.bbva.anida.logica.Conversiones.convertCodigoToTipoinmValtecnic(datosEncargoParticular[33]), datosEncargoParticular[34], datosEncargoParticular[35],oConexion);
                            if (this.iNumeroRegistrosAfectados == 1)
                            { 
                                Objetos.v2.Operclientes oOperclientes = new Objetos.v2.Operclientes();
                                oOperclientes.cliente = new Integer(codcli_expediente);
                                oOperclientes.numexp = sNumeroExpediente + Clientes.bbva.anida.logica.Conversiones.getLetraGarantia(iNumeroGarantia);
                                oOperclientes.refcliente = Utilidades.Cadenas.getValorMayusculasSinBlancos(datosEncargoParticular[0]);
                                oOperclientes.tipoperacion = "REC";
                                oOperclientes.tipomensaje = "STA";
                                oOperclientes.control = new Integer(0);
                                oOperclientes.postventa = new Integer(0);
                                oOperclientes.setFchenvioNow();
                                oOperclientes.setHoraenvioNow();
                                oOperclientes.estado = "001";
                                iNumeroRegistrosAfectados = oOperclientes.insert(oConexion);
                                oOperclientes = null;
                                if(iNumeroRegistrosAfectados==1 && oLogger!=null) oLogger.info(" - EXP: "+sNumeroExpediente + Clientes.bbva.anida.logica.Conversiones.getLetraGarantia(iNumeroGarantia)+" - SE HA INSERTADO CORRECTAMENTE EL INMUEBLE 6.");
                            }//if
                        }//if
                    }//if
                    else
                    {
                        if(iNumeroRegistrosAfectados==1 && oLogger!=null) oLogger.info(" - EXP: "+sNumeroExpediente+" - YA EXISTE EL EXPEDIENTE EN EL SISTEMA.");
                    }//else
                    oLocalidad = null;
                    oMunicipio = null;
                    oProvincia = null;
                    oClidir = null;
                    sNumeroExpediente = null;
                }//if
                else
                {
                    // - SIGUIENTE EXPEDIENTE Y MANDAR CORREO.
                    Clientes.bbva.anida.logica.MailNoProcesado oMailNoProcesado = new Clientes.bbva.anida.logica.MailNoProcesado(datosEncargoParticular);
                    oMailNoProcesado = null;
                }//else
                if(iNumeroRegistrosAfectados==1) 
                {
                    iContadorTotal++;
                    oConexion.commit();
                }//if
                else oConexion.rollback();
                //oConexion.close();
                //oConexion = null;
            }//if
            else
            {
                if(oLogger!=null) oLogger.info("- EXP: "+datosEncargoParticular[0]+" - EL EXPEDIENTE YA EXISTE EN EL SISTEMA.");
            }//else
        }//if
        else
        {
            if(oLogger!=null) oLogger.info("- NO HAY NINGÚN DATO A LA HORA DE PROCESAR EL ENCARGO.");
        }//else
        return this.iNumeroRegistrosAfectados;
    }//procesarFicheroEncargo

    //*****************************************************************************************************************
    //*****************************************************************************************************************
    private int procesarEncargoParticular(String numexp, int numeroGarantia, String[] datosEncargoParticular, String direccionInmueble, String PoblacionInmueble,
            String tipoInmueble, String descripcionTipoInmueble, String codposInmueble,java.sql.Connection oConexion) throws java.sql.SQLException, Exception 
    {
        // *********************************************************************
        // - SOLICITUDES
        // *********************************************************************
        Objetos.Solicitudes oSolicitudes = new Objetos.Solicitudes();
        oSolicitudes.numexp = Utilidades.Cadenas.getValorMayusculasSinBlancos(numexp);
        oSolicitudes.codcli = codcli_expediente;
        oSolicitudes.oficina = Utilidades.Cadenas.getValorMayusculasSinBlancos(datosEncargoParticular[48]);
        if(oSolicitudes.oficina!=null && oSolicitudes.oficina.length()==3) oSolicitudes.oficina = "0"+oSolicitudes.oficina;
        if ((oClidir != null) && (oClidir.codcli != null)) 
        {
            oSolicitudes.denomina = oClidir.denomina;
            oSolicitudes.dirofi = oClidir.direcc;
            oSolicitudes.postal = oClidir.codpos.toString();
            oSolicitudes.poblacion = oClidir.pobla;
            oSolicitudes.provclient = oClidir.provin;
            oSolicitudes.telefo = oClidir.telefo;
            oSolicitudes.fax = oClidir.fax;
            //(25-05-2010 | PILAR): CAMBIO EN LA PERSONA DE ENCARGO
            //oSolicitudes.encarg = oClidir.percon;
            oSolicitudes.encarg = "ANIDA";
        }//if
        oSolicitudes.tipoinm = Utilidades.Cadenas.getValorMayusculasSinBlancos(tipoInmueble);
        oSolicitudes.calle = Utilidades.Cadenas.getValorMayusculasSinBlancos(direccionInmueble);
        //oSolicitudes.poblacion = Utilidades.Cadenas.getValorMayusculasSinBlancos(PoblacionInmueble);
        oSolicitudes.codpos = codposInmueble;
        if (oProvincia != null) oSolicitudes.provin = oProvincia.nompro;
        if (oMunicipio != null) oSolicitudes.munici = oMunicipio.municipio;
        if ((PoblacionInmueble != null) && (!(PoblacionInmueble.trim().equals("")))) oSolicitudes.locali = PoblacionInmueble.toUpperCase();
        oSolicitudes.codest = "0";
        oSolicitudes.fchenc = Utilidades.Cadenas.getToday();
        oSolicitudes.contacto = Utilidades.Cadenas.getValorMayusculasSinBlancos(datosEncargoParticular[42]);
        oSolicitudes.telefonos = Utilidades.Cadenas.getValorMayusculasSinBlancos(datosEncargoParticular[43]);
        if ((datosEncargoParticular[44] != null) && (!(datosEncargoParticular[44].trim().equals("")))) oSolicitudes.telefonos = oSolicitudes.telefonos + "/" + datosEncargoParticular[44];
        oSolicitudes.solici = datosEncargoParticular[46].toUpperCase() + "," + datosEncargoParticular[45].toUpperCase();
        oSolicitudes.nifsolici = Utilidades.Cadenas.getValorMayusculasSinBlancos(datosEncargoParticular[47]);
        oSolicitudes.codcsb = Utilidades.Cadenas.getValorMayusculasSinBlancos(datosEncargoParticular[49]);
        oSolicitudes.ccoficina = Utilidades.Cadenas.getValorMayusculasSinBlancos(datosEncargoParticular[50]);
        oSolicitudes.dctrl = Utilidades.Cadenas.getValorMayusculasSinBlancos(datosEncargoParticular[51]);
        if(oSolicitudes.dctrl!=null && oSolicitudes.dctrl.length()==1) oSolicitudes.dctrl = "0"+oSolicitudes.dctrl;
        oSolicitudes.cuenta = Utilidades.Cadenas.getValorMayusculasSinBlancos(datosEncargoParticular[52]);
        oSolicitudes.delegado = oProvincia.coddel.toString();
        this.iNumeroRegistrosAfectados = oSolicitudes.insertSolicitudes(oConexion);
        oSolicitudes = null;

        // *********************************************************************
        // - ENTRADA
        // *********************************************************************
        if (this.iNumeroRegistrosAfectados == 1) {
            Objetos.Entrada oEntrada = new Objetos.Entrada();
            oEntrada.numexp = numexp;
            if ((oProvincia != null) && (oProvincia.coddel != null)) oEntrada.coddel = oProvincia.coddel.toString().toUpperCase();
            this.iNumeroRegistrosAfectados = oEntrada.insert(oConexion);
            oEntrada = null;
        }//if

        // *********************************************************************
        // - AVISOS
        // *********************************************************************
        if (this.iNumeroRegistrosAfectados == 1) 
        {
            Objetos.Avisos oAviso = new Objetos.Avisos();
            oAviso.numexp = numexp;
            oAviso.aviso1 = "TIPOLOGÍA: " + descripcionTipoInmueble + ". ";
            oAviso.aviso1+= "FECHA LÍMITE: " + datosEncargoParticular[5] + ". ";
            if ((datosEncargoParticular[53] != null) && (datosEncargoParticular[53].trim().equals("S"))) oAviso.aviso2 = "SE TRATA DE UN EMPLEADO DEL BANCO. ";
            if ((datosEncargoParticular[36] != null) && (datosEncargoParticular[36].trim().equals("S"))) oAviso.aviso2 = Utilidades.Cadenas.getValorMostrarWeb(oAviso.aviso2)+"SE NECESITA VERIFICACIÓN REGISTRAL. FACTURAR" +
                    " NOTA. ";
            if ((datosEncargoParticular[36] != null) && (datosEncargoParticular[36].trim().equals("N"))) oAviso.aviso2 = Utilidades.Cadenas.getValorMostrarWeb(oAviso.aviso2)+"IMPRESCINDIBLE PEDIR DOCUMENTACIÓN EN VISITA. ";
            this.iNumeroRegistrosAfectados = oAviso.insert(oConexion);
            oAviso = null;
        }//if

        // *********************************************************************
        // - PRODUSU
        // *********************************************************************
        if (this.iNumeroRegistrosAfectados == 1) 
        {
            this.iNumeroRegistrosAfectados = Objetos.Produsu.insertProdusu(numexp, "0", "juan", "A", "1", "RE", oConexion);
        }//if

        // *********************************************************************
        // - ESTADÍSTICAS
        // *********************************************************************
        if (this.iNumeroRegistrosAfectados == 1) 
        {
            Objetos.Estadisticas oEstadistica = new Objetos.Estadisticas();
            oEstadistica.numexp = numexp;
            oEstadistica.fchrec = new java.util.Date();
            oEstadistica.codagep = new Integer(0);
            this.iNumeroRegistrosAfectados = oEstadistica.insert(oConexion);
            oEstadistica = null;
        }//if

        // *********************************************************************
        // - REFER
        // *********************************************************************
        if (this.iNumeroRegistrosAfectados == 1) 
        {
            Objetos.Refer oRefer = new Objetos.Refer();
            oRefer.numexp = numexp;
            oRefer.referencia = Utilidades.Cadenas.getValorMayusculasSinBlancos(datosEncargoParticular[0]);
            oRefer.objeto = Integer.toString(numeroGarantia);
            if(datosEncargoParticular.length>=55) oRefer.gestor = datosEncargoParticular[55];
            if(datosEncargoParticular.length>=56) oRefer.nombstor = datosEncargoParticular[56];
            this.iNumeroRegistrosAfectados = oRefer.insert(oConexion);
            oRefer = null;
        }//if

        // *********************************************************************
        // - NOTAS_ENCARG
        // *********************************************************************
        if (this.iNumeroRegistrosAfectados == 1) 
        {
            Objetos.Notas_encarg oNota_encarg = new Objetos.Notas_encarg();
            oNota_encarg.numexp = numexp;
            if ((oProvincia != null) && (oProvincia.coddel != null)) oNota_encarg.delegado = oProvincia.coddel;
            this.iNumeroRegistrosAfectados = oNota_encarg.insert(oConexion);
            oNota_encarg = null;
        }//if

        // *********************************************************************
        // - NOTAS
        // *********************************************************************
        if (this.iNumeroRegistrosAfectados == 1) 
        {
            Objetos.Notas oNota = null;
            int iNotaActual = 1;
            if ((datosEncargoParticular.length >= 55) && (datosEncargoParticular[54] != null) && (!(datosEncargoParticular[54].trim().equals("")))) 
            {
                datosEncargoParticular[54] = datosEncargoParticular[54].toUpperCase();
                oNota = new Objetos.Notas();
                oNota.numexp = numexp;
                oNota.numnot = new Integer(iNotaActual);
                oNota.fecnot = new java.util.Date();
                if (datosEncargoParticular[54].length() <= 60) 
                {
                    oNota.texnot1 = datosEncargoParticular[54];
                }//if
                else if ((datosEncargoParticular[54].length() > 60) && (datosEncargoParticular[54].length() <= 120)) {
                    oNota.texnot1 = datosEncargoParticular[54].substring(0,60);
                    oNota.texnot2 = datosEncargoParticular[54].substring(60, datosEncargoParticular[54].length());
                }//else if
                else if ((datosEncargoParticular[54].length() > 120) && (datosEncargoParticular[54].length() <= 180)) {
                    oNota.texnot1 = datosEncargoParticular[54].substring(0,60);
                    oNota.texnot2 = datosEncargoParticular[54].substring(60, 120);
                    oNota.texnot3 = datosEncargoParticular[54].substring(120, datosEncargoParticular[54].length());
                }//else if
                else if ((datosEncargoParticular[54].length() > 180) && (datosEncargoParticular[54].length() <= 240)) {
                    oNota.texnot1 = datosEncargoParticular[54].substring(0,60);
                    oNota.texnot2 = datosEncargoParticular[54].substring(60, 120);
                    oNota.texnot3 = datosEncargoParticular[54].substring(120, 180);
                    oNota.texnot4 = datosEncargoParticular[54].substring(180, datosEncargoParticular[54].length());
                }//else if
                else if ((datosEncargoParticular[54].length() > 240) && (datosEncargoParticular[54].length() <= 300)) {
                    oNota.texnot1 = datosEncargoParticular[54].substring(0,60);
                    oNota.texnot2 = datosEncargoParticular[54].substring(60, 120);
                    oNota.texnot3 = datosEncargoParticular[54].substring(120, 180);
                    oNota.texnot4 = datosEncargoParticular[54].substring(180, 240);
                    oNota.texnot5 = datosEncargoParticular[54].substring(240, datosEncargoParticular[54].length());
                }//else if
                else if ((datosEncargoParticular[54].length() > 300) && (datosEncargoParticular[54].length() <= 360)) {
                    oNota.texnot1 = datosEncargoParticular[54].substring(0,60);
                    oNota.texnot2 = datosEncargoParticular[54].substring(60, 120);
                    oNota.texnot3 = datosEncargoParticular[54].substring(120, 180);
                    oNota.texnot4 = datosEncargoParticular[54].substring(180, 240);
                    oNota.texnot5 = datosEncargoParticular[54].substring(240, 300);
                    oNota.texnot6 = datosEncargoParticular[54].substring(300, datosEncargoParticular[54].length());
                }//else if
                this.iNumeroRegistrosAfectados = oNota.insert(oConexion);
                iNotaActual++;
                if ((this.iNumeroRegistrosAfectados == 1) && (datosEncargoParticular[54].length() > 360)) 
                {
                    oNota = new Objetos.Notas();
                    oNota.numexp = numexp;
                    oNota.numnot = new Integer(iNotaActual);
                    oNota.fecnot = new java.util.Date();
                    if ((datosEncargoParticular[54].length() > 360) && (datosEncargoParticular[54].length() <= 420)) 
                    {
                        oNota.texnot1 = datosEncargoParticular[54].substring(360);
                    }//if
                    this.iNumeroRegistrosAfectados = oNota.insert(oConexion);
                    ++iNotaActual;
                }//if
            }//if
            
            if (this.iNumeroRegistrosAfectados == 1 && this.codcli_expediente .equals(codcli_bbva_blue))
            {
                oNota = new Objetos.Notas();
                oNota.numexp = numexp;
                oNota.numnot = new Integer(iNotaActual);
                oNota.fecnot = new java.util.Date();
                oNota.texnot1 = "HIPOTECA BLUE JOVEN";
                this.iNumeroRegistrosAfectados = oNota.insert(oConexion);
                ++iNotaActual;
            }//if
            if (this.iNumeroRegistrosAfectados == 1) 
            {
                oNota = new Objetos.Notas();
                oNota.numexp = numexp;
                oNota.numnot = new Integer(iNotaActual);
                oNota.fecnot = new java.util.Date();
                oNota.texnot1 = "TIPO DE OPERACIÓN HIPOTECARIA: " + Clientes.bbva.anida.logica.Conversiones.getTipoTasacionHipotecaria(datosEncargoParticular[2]);
                this.iNumeroRegistrosAfectados = oNota.insert(oConexion);
                ++iNotaActual;
            }//if
            if (this.iNumeroRegistrosAfectados == 1) 
            {
                oNota = new Objetos.Notas();
                oNota.numexp = numexp;
                oNota.numnot = new Integer(iNotaActual);
                oNota.fecnot = new java.util.Date();
                oNota.texnot1 = "TIPO DE PATRIMONIO: " + Clientes.bbva.anida.logica.Conversiones.getTipoPatrimonio(datosEncargoParticular[1]);
                this.iNumeroRegistrosAfectados = oNota.insert(oConexion);
                ++iNotaActual;
            }//if
            if (this.iNumeroRegistrosAfectados == 1 && datosEncargoParticular[55]!=null && datosEncargoParticular[55].length() > 0) 
            {
                oNota = new Objetos.Notas();
                oNota.numexp = numexp;
                oNota.numnot = new Integer(iNotaActual);
                oNota.fecnot = new java.util.Date();
                oNota.texnot1 = "CÓDIGO GESTOR: " + datosEncargoParticular[55];
                if (datosEncargoParticular[56]!=null && datosEncargoParticular[56].length() > 0) oNota.texnot1 += "NOMBRE GESTOR: " + datosEncargoParticular[56];
                this.iNumeroRegistrosAfectados = oNota.insert(oConexion);
                ++iNotaActual;
            }//if
            oNota = null;
        }//if

        // *********************************************************************
        // - DATOSREG
        // *********************************************************************
        if (this.iNumeroRegistrosAfectados == 1) 
        {
            Objetos.Datosreg oDatosreg = new Objetos.Datosreg();
            oDatosreg.numexp = numexp;
            oDatosreg.numero = new Integer(1);
            if (datosEncargoParticular[37] != null) oDatosreg.finca = Utilidades.Cadenas.getValorMayusculasSinBlancos(datosEncargoParticular[37]);
            if (datosEncargoParticular[38] != null) oDatosreg.tomo = Utilidades.Cadenas.getValorMayusculasSinBlancos(datosEncargoParticular[38]);
            if (datosEncargoParticular[39] != null) oDatosreg.libro = Utilidades.Cadenas.getValorMayusculasSinBlancos(datosEncargoParticular[39]);
            if (datosEncargoParticular[40] != null) oDatosreg.folio = Utilidades.Cadenas.getValorMayusculasSinBlancos(datosEncargoParticular[40]);
            oDatosreg.tipoinm = tipoInmueble;
            if (datosEncargoParticular[41] != null) oDatosreg.registro = Utilidades.Cadenas.getValorMayusculasSinBlancos(datosEncargoParticular[41]);
            this.iNumeroRegistrosAfectados = oDatosreg.insertar(oConexion);
            oDatosreg = null;
        }//if
        
        // *********************************************************************
        // - NOTA_SIMPLE
        // *********************************************************************
        if ((datosEncargoParticular[36] != null) && (datosEncargoParticular[36].trim().equals("S")))
        {
            if(datosEncargoParticular[37]!=null && !datosEncargoParticular[37].trim().equals(""))
            {
                this.iNumeroRegistrosAfectados = Objetos.Notas_simples.solicitarNotaSimple(numexp,Utilidades.Cadenas.getValorMayusculasSinBlancos(datosEncargoParticular[37]),null, oConexion);
            }//if
            else
            {
                String sCuerpo = "";
                sCuerpo += " - NÚMERO DE EXPEDIENTE: "+numexp;
                sCuerpo += "\n - PARA ESTE EXPEDIENTE BBVA SOLICITA LA NOTA SIMPLE, PERO NO SE PUEDE SOLICITAR AUTOMÁTICAMENTE PORQUE FALTA LA FINCA REGISTRAL.";
                Utilidades.Correo.sendMail("valtecnic.com","informatica@valtecnic.com","sede@valtecnic.com","gestion3@valtecnic.com", null,"ERROR BBVA ANIDA",sCuerpo, false);
                sCuerpo = null;
            }//else
        }//if
        
      
        return this.iNumeroRegistrosAfectados;
    }//procesarEncargoParticular

    //*****************************************************************************************************************
    //*****************************************************************************************************************
    public static void main(String[] args) 
    {
        java.sql.Connection cConexionV = null;
        java.sql.Connection cConexionG = null;
        try 
        {
            //cConexionV = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@ (DESCRIPTION = (ADDRESS = (PROTOCOL = TCP)(HOST = oraserver01.valtecnic.com)(PORT = 1521)) (ADDRESS = (PROTOCOL = TCP)(HOST =oraserver02.valtecnic.com)(PORT =1521)) (LOAD_BALANCE = no) (CONNECT_DATA = (SERVER = DEDICATED) (SERVICE_NAME = test) (FAILOVER_MODE = (TYPE = SELECT) (METHOD = BASIC) (RETRIES = 20) (DELAY = 15))))");
            //cConexionG = Utilidades.Conexion.getConnection("jdbc:oracle:thin:gecopinsa2/gecopinsa2@ (DESCRIPTION = (ADDRESS = (PROTOCOL = TCP)(HOST = oraserver01.valtecnic.com)(PORT = 1521)) (ADDRESS = (PROTOCOL = TCP)(HOST =oraserver02.valtecnic.com)(PORT =1521)) (LOAD_BALANCE = no) (CONNECT_DATA = (SERVER = DEDICATED) (SERVICE_NAME = test) (FAILOVER_MODE = (TYPE = SELECT) (METHOD = BASIC) (RETRIES = 20) (DELAY = 15))))");
            cConexionV = Utilidades.Conexion.getConnectionValtecnic2();
            cConexionG = Utilidades.Conexion.getConnectionGecopinsa2();
            String dia = Utilidades.Cadenas.getToday();
            java.io.File fDia = new java.io.File("C:/1/ANIDA/"+dia+"/");
            if(fDia.exists())
            {
                java.io.File[] fDirectorio = fDia.listFiles();
                for(int iContador=0;iContador<fDirectorio.length;iContador++)
                {
                    FicheroRecepcion o = new FicheroRecepcion(fDia.getPath(), fDirectorio[iContador].getName(), null, cConexionV, cConexionG);
                    System.out.println("RESULTADO: " + o.getRegistrosAfectados());
                }//FOR
            }//if
        } 
        catch (Exception e) 
        {
            System.out.println(e);
        } 
        finally 
        {
            if (cConexionV != null) 
            {
                try {
                    //cConexionV.commit();
                    cConexionV.close();
                    cConexionV = null;
                }//try
                catch (Exception e) 
                {
                    System.out.println(e);
                }//catch
            }//if
            if (cConexionG != null) 
            {
                try {
                    //cConexionG.commit();
                    cConexionG.close();
                    cConexionG = null;
                }//try
                catch (Exception e) 
                {
                    System.out.println(e);
                }//catch
            }//if
        }//finally
    }//main
    
    //*****************************************************************************************************************
    //*****************************************************************************************************************
    
}//FicheroRecepcion

