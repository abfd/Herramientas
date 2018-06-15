/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DEUTSCHE;


/**
 *
 * @author Alberto
 */
public class Load_Expedientes_Excel 
{
    public static final int COLUMNA_NUMEXP = 0; // A
    public static final int COLUMNA_TIPOINM = 1; // B
    public static final int COLUMNA_REFERENCIA_CATASTRAL = 2; // C
    public static final int COLUMNA_DIRECCION = 3; // D
    public static final int COLUMNA_NUMERO= 4; // E
    public static final int COLUMNA_BLOQUE = 5; // F
    public static final int COLUMNA_ESCALERA = 6; // G
    public static final int COLUMNA_PISO = 7; // H
    public static final int COLUMNA_PUERTA = 8; // I
        
    public static final int COLUMNA_CODPOS = 9; // J
    public static final int COLUMNA_MUNICIPIO = 10; // K
    public static final int COLUMNA_PROVINCIA = 11; // l
    public static final int COLUMNA_NOMBRE_REGISTRO =  12; // m
    public static final int COLUMNA_TOMO = 13; // n
    public static final int COLUMNA_LIBRO = 14; // o
    public static final int COLUMNA_FOLIO = 15; // p
    public static final int COLUMNA_FINCA = 16; // q
    public static final int COLUMNA_IDUFIR = 17; // r
    public static final int COLUMNA_NUMERO_REGISTRO = 18;
    
    /*public static final int COLUMNA_CODPOS = 9; // J
    public static final int COLUMNA_MUNICIPIO = 10; // K
    public static final int COLUMNA_PROVINCIA = 11; // l
    public static final int COLUMNA_NOMBRE_REGISTRO =  12; // m
    //public static final int COLUMNA_TOMO = 13; // n
    public static final int COLUMNA_TOMO = 14; // o
    public static final int COLUMNA_LIBRO = 15; // p
    public static final int COLUMNA_FOLIO = 16; // q
    public static final int COLUMNA_FINCA = 17; // r*/
        
    //***************************************************************************************************
    //***************************************************************************************************
    public static String load(java.io.File file,java.sql.Connection connection,java.sql.Connection connection_docgrafica)
    {
        Utilidades.Excel oExcel = null;
        String oString = null;
        String numexp = null;
        Objetos.Datosreg oDatosreg = null;
        Objetos.Provincias oProvincias = null;
        Objetos.Catastro oCatastro = null;
        Objetos.Refer oRefer = null;
        Objetos.Documenta oDocumenta = null;
        Objetos.Avisos oAvisos = null;
        Objetos.Txtlib oTxtlib = null;
        String codpos = null;
        Objetos.v2.Solicitudes oSolicitudes = null;
        Objetos.unidades.Suelo oSuelo = null;
        Objetos.unidades.Edificios oEdificios = null;
        Objetos.unidades.Elementos oElementos = null;
        Integer codcli = null;
        String oficina = null;
        String resultado = "";
        String encarg = "";
        String solici = "";
        String api = "";
        String lote = "";
        String nifsolici = "";  
        Integer delegado = null;
        String finalidad = null;
        int iContadorExpedientes = 0;
        int iContadorFincas = 0;
        java.util.Date fecha_limite = null;
        String aviso = null;
        Utilidades.Filtro oFiltro = null;
        java.io.File[] notas_simples = null;
        java.io.File directory_notas_simples = null;
        boolean repetido = false;
        //**********************************************************************
        //**********************************************************************
        //**********************************************************************
        directory_notas_simples = new java.io.File("X:/Deutsche bank drive by - febrero 2016");
        String nombre_hoja = "Hoja1";
        lote = "7531";
        api = "7531";
        codcli = 952;
        oficina = "REVA";
        //aviso = "VER INSTRUCCIÓN 0003/2016";
        encarg = null;
        solici = "DEUTSCHE BANK, S.A.E.";
        nifsolici = "A08000614";
        delegado = null;
        finalidad = null;
        fecha_limite = Utilidades.Cadenas.getDate(23,9,2016);
        //**********************************************************************
        //**********************************************************************
        //**********************************************************************
        try
        {
            if(file!=null && connection!=null && file.exists())
            {
                oExcel = new Utilidades.Excel(file.getAbsolutePath());
                int iFila = 1;//5001
                int j = 1;
                boolean bFin = false;
                int iNumero = 1;
                while(!bFin)
                {
                    repetido = false;
                    j = 1;
                    oString = Utilidades.Excel.getStringCellValue(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_NUMEXP));
                    if(oString!=null && !oString.trim().equals(""))
                    {
                        // - EXPEDIENTE NUEVO
                        codpos = Utilidades.Cadenas.completTextWithLeftCharacter(Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_CODPOS)),'0',5);
                        oProvincias = new Objetos.Provincias();
                        if(codpos!=null && codpos.length()>=2 && oProvincias.load(new Integer(codpos.substring(0,2)), connection))
                        {
                            numexp = "";
                            numexp+= oProvincias.prefijo;
                            numexp+= codcli.toString().substring(1,3);
                            numexp+= "."+Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_NUMEXP));
                            numexp+= "-"+Utilidades.Cadenas.getDateParse(new java.util.Date(),"yy");
                            oSolicitudes = new Objetos.v2.Solicitudes();
                            if(oSolicitudes.load(numexp, connection)==0)
                            {
                                oSolicitudes = new Objetos.v2.Solicitudes();
                                oSolicitudes.numexp = numexp;
                                oSolicitudes.tipoinm = Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_TIPOINM));
                                if(oSolicitudes.tipoinm!=null && oSolicitudes.tipoinm.equals("XXX")) resultado+="- FILA: " +iFila+" | NUMEXP: "+numexp+" | EL EXPEDIENTE CON TIPOLOGÍA XXX.\n";
                                oSolicitudes.codcli = codcli;
                                oSolicitudes.oficina = oficina;
                                Objetos.Clidir oClidir = new Objetos.Clidir();
                                if(oClidir.load(codcli,oSolicitudes.oficina, connection))
                                {
                                    if(encarg==null || encarg.trim().equals("")) oSolicitudes.encarg = oClidir.percon;
                                    else oSolicitudes.encarg = encarg;
                                    oSolicitudes.dirofi = oClidir.direcc;
                                    oSolicitudes.postal = oClidir.codpos;
                                    oSolicitudes.provclient = oClidir.provin;
                                    oSolicitudes.poblacion = oClidir.pobla;
                                    oSolicitudes.telefo = oClidir.telefo;
                                    oSolicitudes.fax = oClidir.fax;
                                }//if
                                oSolicitudes.solici = solici;
                                oSolicitudes.nifsolici = nifsolici;
                                oSolicitudes.locali = Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_MUNICIPIO));
                                oSolicitudes.munici = Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_MUNICIPIO));
                                oSolicitudes.provin = Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_PROVINCIA));
                                oSolicitudes.calle = Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_DIRECCION));
                                oSolicitudes.numero = Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_NUMERO));
                                oSolicitudes.planta = Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_PISO));
                                oSolicitudes.puerta = Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_PUERTA));
                                oSolicitudes.escalera = Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_ESCALERA));
                                try
                                {
                                    oSolicitudes.codpos = new Integer(Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_CODPOS)));
                                }//try
                                catch(Exception e)
                                {
                                    oString = null;
                                    if(oSolicitudes.locali!=null && !oSolicitudes.locali.trim().equals("")) 
                                    {
                                        oString = Objetos.Localidades.getCodpos(oSolicitudes.locali, connection);
                                        if(oString!=null && !oString.trim().equals("")) oSolicitudes.codpos = new Integer(oString);
                                    }//if
                                    if(oSolicitudes.codpos==null)
                                    {
                                        oSolicitudes.codpos = new Integer(oProvincias.codpro+"000");
                                    }//if
                                }//catch
                                oSolicitudes.fchenc = new java.util.Date();
                                if(delegado!=null) oSolicitudes.delegado = delegado;
                                else oSolicitudes.delegado = oProvincias.coddel;
                                oSolicitudes.provin = oProvincias.nompro;
                                oSolicitudes.codest = new Integer(0);
                                oSolicitudes.insert(connection);
                                iContadorExpedientes++;
                                iContadorFincas++;
                                
                                oDatosreg = new Objetos.Datosreg();
                                oDatosreg.numexp = oSolicitudes.numexp;
                                oDatosreg.tipoinm = Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_TIPOINM));
                                oDatosreg.registro = Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_NOMBRE_REGISTRO));
                                if (Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_NUMERO_REGISTRO)) != null && !Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_NUMERO_REGISTRO)).equals("0"))
                                {
                                    oDatosreg.registro += " Nº "+Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_NUMERO_REGISTRO)).trim();
                                }
                                oDatosreg.finca = Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_FINCA));
                                oDatosreg.folio = Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_FOLIO));
                                oDatosreg.libro = Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_LIBRO));
                                oDatosreg.tomo = Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_TOMO));
                                oDatosreg.insert(connection);

                                //Objetos.Catastro.delete(oSolicitudes.numexp,oDatosreg.numero,connection);
                                oCatastro = new Objetos.Catastro();
                                oCatastro.numexp = oSolicitudes.numexp;
                                oCatastro.numero = oDatosreg.numero;
                                oCatastro.refcliente = Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_NUMEXP));
                                oCatastro.dircatastral = Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_DIRECCION));
                                if(Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_NUMERO))!=null && !Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_NUMERO)).trim().equals(""))
                                oCatastro.dircatastral += " N? "+Utilidades.Cadenas.TrimAllBlanks(Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_NUMERO)));
                                if(Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_BLOQUE))!=null && !Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_BLOQUE)).trim().equals(""))
                                oCatastro.dircatastral += " BLOQUE "+Utilidades.Cadenas.TrimAllBlanks(Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_BLOQUE)));
                                if(Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_PISO))!=null && !Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_PISO)).trim().equals(""))
                                oCatastro.dircatastral += " PISO "+Utilidades.Cadenas.TrimAllBlanks(Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_PISO)));
                                if(Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_PUERTA))!=null && !Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_PUERTA)).trim().equals(""))
                                oCatastro.dircatastral += " PUERTA "+Utilidades.Cadenas.TrimAllBlanks(Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_PUERTA)));                            
                                if(Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_ESCALERA))!=null && !Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_ESCALERA)).trim().equals(""))
                                oCatastro.dircatastral += " ESCALERA "+Utilidades.Cadenas.TrimAllBlanks(Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_ESCALERA)));                            
                                oCatastro.fcatastral = Utilidades.Cadenas.TrimAllBlanks(Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_REFERENCIA_CATASTRAL)));
                                oCatastro.insert(connection);
                                
                                if(oDatosreg.finca!=null && !oDatosreg.finca.trim().equals("") && !Objetos.Notas_simples.exists(numexp, oDatosreg.finca, connection)) 
                                    {
                                        if(Objetos.Notas_simples.solicitarNotaSimple(oSolicitudes.numexp,oDatosreg.finca,"inform", connection)>0)
                                            resultado += "- NUMEXP: "+numexp+" - PEDIDAS NOTAS SIMPLES.\n";
                                    }//if
                                /*try
                                {
                                    if(oDatosreg.finca!=null && !oDatosreg.finca.trim().equals("") && !Objetos.Notas_simples.exists(numexp, oDatosreg.finca, connection)) 
                                        Objetos.Notas_simples.solicitarNotaSimple(numexp,oDatosreg.finca, "juan", connection);
                                    else resultado+="- FILA: " +iFila+" | NUMEXP: "+oSolicitudes.numexp+" | NÚMERO: "+oDatosreg.numero+" | FINCA: "+oDatosreg.finca+" | YA EXISTE LA PETICIÓNDE NOTA SIMPLE.\n";
                                }//try
                                catch(Exception e)
                                {
                                    resultado+="- FILA: " +iFila+" | NUMEXP: "+oSolicitudes.numexp+" | NÚMERO: "+oDatosreg.numero+" | FINCA: "+oDatosreg.finca+" | EXCEPCIÓN:"+e+".\n";
                                }//catch*/
                            
                                /*if(finalidad!=null && !finalidad.trim().equals(""))
                                {
                                    oDocumenta = new Objetos.Documenta();
                                    oDocumenta.numexp = oSolicitudes.numexp;
                                    oDocumenta.tipoinm = oSolicitudes.tipoinm;
                                    oDocumenta.finalidad = finalidad;
                                    oDocumenta.insertDocumenta(connection);
                                }//if*/
                                
                                oTxtlib = new Objetos.Txtlib();
                                oTxtlib.numexp = oSolicitudes.numexp;
                                oTxtlib.codigo = Objetos.Txtlib.CODIGO_AVISO;
                                oTxtlib.texto = aviso;
                                oTxtlib.insert(connection);
                                
                                oAvisos = new Objetos.Avisos();
                                oAvisos.numexp = oSolicitudes.numexp;
                                oAvisos.aviso1 = aviso;
                                oAvisos.insert(connection);
                                
                                oRefer = new Objetos.Refer();
                                oRefer.numexp = oSolicitudes.numexp;
                                oRefer.referencia = Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_NUMEXP));
                                oRefer.api = api;
                                oRefer.lote = lote;
                                oRefer.insert(connection);
                                     
                                Objetos.Citas.insert_fecha_limite(numexp, fecha_limite, connection);
                                
                                if(!Objetos.Tipos_aux.esSuelo(Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_TIPOINM))))
                                {
                                    oSuelo = new Objetos.unidades.Suelo();
                                    oSuelo.numexp = oSolicitudes.numexp;
                                    oSuelo.insert(connection);

                                    oEdificios = new Objetos.unidades.Edificios();
                                    oEdificios.numexp = oSolicitudes.numexp;
                                    oEdificios.iden_suelo = oSuelo.iden_suelo;
                                    oEdificios.via = Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_DIRECCION));
                                    oEdificios.numvia = Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_NUMERO));
                                    oEdificios.insert(connection);

                                    oElementos = new Objetos.unidades.Elementos();
                                    oElementos.numexp = oSolicitudes.numexp;
                                    oElementos.id_edificio = oEdificios.id_edificio;
                                   
                                    oElementos.num_dr = oDatosreg.numero;
                                    oElementos.tipoinm = Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_TIPOINM));
                                    oElementos.bloque = Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_BLOQUE));
                                    oElementos.escalera = Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_ESCALERA));
                                    oElementos.planta = Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_PISO));
                                    oElementos.puerta = Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_PUERTA));
                                    oElementos.refunidad = Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_NUMEXP));
                                    oElementos.nomelmto = Objetos.Tipos_aux.getFirstWordTipotasac(oElementos.tipoinm, connection)+" "+oElementos.refunidad;
                                    oElementos.insert(connection);
                                }//if
                                else
                                {
                                    oSuelo = new Objetos.unidades.Suelo();
                                    oSuelo.numexp = oSolicitudes.numexp;
                                    oSuelo.num_dr = oDatosreg.numero;
                                    oSuelo.tipoinm = Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_TIPOINM));
                                    oSuelo.insert(connection);
                                }//else
                                resultado+="- FILA: " +iFila+" | NUMEXP: "+oSolicitudes.numexp+" | INSERTADO NUEVO EXPEDIENTE.\n";
                                //resultado+="- FILA: " +iFila+" | NUMEXP: "+oSolicitudes.numexp+" | NÚMERO: "+oDatosreg.numero+" | INSERTADA NUEVA FINCA.\n";
                              
                                /*
                                oFiltro = new Utilidades.Filtro(Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_NUMEXP)), Utilidades.Filtro.NOMBRE_FICHERO_EMPIEZA_CON);
                                notas_simples = directory_notas_simples.listFiles(oFiltro);
                                for(int iContador=0;iContador<notas_simples.length;iContador++)
                                {
                                    if(Objetos.Docgrafica.insert(numexp,notas_simples[iContador].getName(),Objetos.Docgrafica.PIE_NOTA_SIMPLE, Objetos.Docgrafica.TIPO_NOTA_SIMPLE, "juan", notas_simples[iContador], connection_docgrafica)==1)
                                    {
                                        resultado+="- FILA: " +iFila+" | NUMEXP: "+oSolicitudes.numexp+" | SUBIDA NOTA SIMPLE.\n";
                                        connection_docgrafica.rollback();
                                    }
                                    else connection_docgrafica.rollback();
                                }//for
*/
                                
                            }//if
                            else
                            {//si ya se encuentra dado de alta se notifica.
                               if (oSolicitudes.numexp.equals("252.587438-16"))
                               {
                                   System.out.println("252.587438-16");
                               }
                               java.util.Date date = new java.util.Date(16, 8, 10); //año, mes,día
                               if (!oSolicitudes.fchenc.toString().equals("2016-08-10"))
                               {
                                   repetido = true;
                                   resultado+="- FILA: " +iFila+" | NUMEXP: "+oSolicitudes.numexp+" | EXPEDIENTE REPETIDO.\n"; 
                               }
                            }
                        }//if
                        else
                        {
                            resultado+="- FILA: " +iFila+" | NO SE PUEDEN CARGAR LOS DATOS POSTALES.\n";
                        }//else                            
                    }//if       
                    else
                    {
                        bFin = true;
                        resultado+="- FILA: " +iFila+" | FIN.\n";
                    }//else if
                    
                    iFila++;
                    iNumero++;
                    
                    if (!repetido) connection.commit();
                    //connection.rollback();
                    //connection_docgrafica.commit();
                    //connection_docgrafica.rollback();
                    System.out.println(resultado);
                    resultado="";
                }//while
            }//if
            
                   //connection.rollback();
            //System.out.println(resultado);
            System.out.println("TOTAL EXPEDIENTES: "+iContadorExpedientes);
            System.out.println("TOTAL FINCAS: "+iContadorFincas);
        }//try
        catch(Exception e)
        {
            System.out.println(e);
        }//catch
        finally
        {
            oExcel = null;
        }//finally
        return resultado;
    }//loadExpedientes
      
    //***************************************************************************************************
    //***************************************************************************************************
    public static String update_finca(java.io.File file,java.sql.Connection connection,java.sql.Connection connection_docgrafica)
    {
        Utilidades.Excel oExcel = null;
        String oString = null;
        String numexp = null;
        Objetos.Datosreg oDatosreg = null;
        Objetos.Provincias oProvincias = null;
        Objetos.Catastro oCatastro = null;
        Objetos.Refer oRefer = null;
        Objetos.Documenta oDocumenta = null;
        Objetos.Avisos oAvisos = null;
        Objetos.Txtlib oTxtlib = null;
        String codpos = null;
        Objetos.v2.Solicitudes oSolicitudes = null;
        Objetos.unidades.Suelo oSuelo = null;
        Objetos.unidades.Edificios oEdificios = null;
        Objetos.unidades.Elementos oElementos = null;
        Integer codcli = null;
        String oficina = null;
        String resultado = "";
        String encarg = "";
        String solici = "";
        String api = "";
        String lote = "";
        String nifsolici = "";  
        Integer delegado = null;
        String finalidad = null;
        int iContadorExpedientes = 0;
        int iContadorFincas = 0;
        java.util.Date fecha_limite = null;
        String aviso = null;
        Utilidades.Filtro oFiltro = null;
        java.io.File[] notas_simples = null;
        java.io.File directory_notas_simples = null;
        
        //**********************************************************************
        //**********************************************************************
        //**********************************************************************
        directory_notas_simples = new java.io.File("X:/Deutsche bank drive by - febrero 2016");
        String nombre_hoja = "Hoja1";
        lote = "7528";
        api = "7528";
        codcli = 952;
        oficina = "REVA";
        //aviso = "VER INSTRUCCIÓN 0003/2016";
        encarg = null;
        solici = "DEUTSCHE BANK, S.A.E.";
        nifsolici = "A08000614";
        delegado = null;
        finalidad = null;
        fecha_limite = Utilidades.Cadenas.getDate(8,4,2016);
        //**********************************************************************
        //**********************************************************************
        //**********************************************************************
        try
        {
            if(file!=null && connection!=null && file.exists())
            {
                oExcel = new Utilidades.Excel(file.getAbsolutePath());
                int iFila = 1;//5001
                int j = 1;
                boolean bFin = false;
                int iNumero = 1;
                while(!bFin)
                {
                    j = 1;
                    oString = Utilidades.Excel.getStringCellValue(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_NUMEXP));
                    if(oString!=null && !oString.trim().equals(""))
                    {
                        // - EXPEDIENTE NUEVO
                        codpos = Utilidades.Cadenas.completTextWithLeftCharacter(Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_CODPOS)),'0',5);
                        oProvincias = new Objetos.Provincias();
                        if(codpos!=null && codpos.length()>=2 && oProvincias.load(new Integer(codpos.substring(0,2)), connection))
                        {
                            numexp = "";
                            numexp+= oProvincias.prefijo;
                            numexp+= codcli.toString().substring(1,3);
                            numexp+= "."+Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_NUMEXP));
                            numexp+= "-"+Utilidades.Cadenas.getDateParse(new java.util.Date(),"yy");
                            oSolicitudes = new Objetos.v2.Solicitudes();
                            if(oSolicitudes.load(numexp, connection)==1)
                            {
                                oDatosreg = new Objetos.Datosreg();
                                oDatosreg.load(numexp,1, connection);
                                oDatosreg.finca = Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_FINCA));
                                oDatosreg.folio = Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_FOLIO));
                                oDatosreg.libro = Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_LIBRO));
                                oDatosreg.tomo = Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(nombre_hoja,iFila,COLUMNA_TOMO));
                                oDatosreg.update(connection);                                
                                try
                                {
                                    if(oDatosreg.finca!=null && !oDatosreg.finca.trim().equals("") && !Objetos.Notas_simples.exists(numexp, oDatosreg.finca, connection)) 
                                        Objetos.Notas_simples.solicitarNotaSimple(numexp,oDatosreg.finca, "juan", connection);
                                    else resultado+="- FILA: " +iFila+" | NUMEXP: "+oSolicitudes.numexp+" | NÚMERO: "+oDatosreg.numero+" | FINCA: "+oDatosreg.finca+" | YA EXISTE LA PETICIÓNDE NOTA SIMPLE.\n";
                                }//try
                                catch(Exception e)
                                {
                                    resultado+="- FILA: " +iFila+" | NUMEXP: "+oSolicitudes.numexp+" | NÚMERO: "+oDatosreg.numero+" | FINCA: "+oDatosreg.finca+" | EXCEPCIÓN:"+e+".\n";
                                }//catch
                            
                                
                            }//if
                        }//if
                        else
                        {
                            resultado+="- FILA: " +iFila+" | NO SE PUEDEN CARGAR LOS DATOS POSTALES.\n";
                        }//else                            
                    }//if       
                    else
                    {
                        bFin = true;
                        resultado+="- FILA: " +iFila+" | FIN.\n";
                    }//else if
                    
                    iFila++;
                    iNumero++;
                    
                    connection.commit();
                    //connection.rollback();
                    //connection_docgrafica.commit();
                    
                    //connection_docgrafica.rollback();
                    System.out.println(resultado);
                    resultado="";
                }//while
            }//if
            
                   //connection.rollback();
            //System.out.println(resultado);
            System.out.println("TOTAL EXPEDIENTES: "+iContadorExpedientes);
            System.out.println("TOTAL FINCAS: "+iContadorFincas);
        }//try
        catch(Exception e)
        {
            System.out.println(e);
        }//catch
        finally
        {
            oExcel = null;
        }//finally
        return resultado;
    }//loadExpedientes
    
    
    //************************************************************************************
    //************************************************************************************
    public static void main(String[] args)
    {
        java.io.File oFile = null;
        java.sql.Connection oConnection = null;
        java.sql.Connection oConnectionDocgrafica = null;
        try
        {
            //oFile = new java.io.File("C:/1/DB3.xls");
            oFile = new java.io.File("C:/1/DB4.xls");
            oConnection = Utilidades.Conexion.getConnectionValtecnic2();
            //oConnection = Utilidades.Conexion.getConnectionDesarrolloValtecnic();
            oConnectionDocgrafica = Utilidades.Conexion.getConnectionDocgraficaValtecnic2();
            load(oFile,oConnection,oConnectionDocgrafica);
            //update_finca(oFile,oConnection,oConnectionDocgrafica);
            oConnectionDocgrafica.rollback();
            oConnection.rollback();
        }//try
        catch(Exception exception)
        {
            System.out.println(exception);
        }//catch
        finally
        {
            try
            {
                if(oConnection!=null) 
                {
                    oConnection.rollback();
                    oConnection.close();
                }
                if(oConnectionDocgrafica!=null) 
                {
                    oConnectionDocgrafica.rollback();
                    oConnectionDocgrafica.close();
                }
            }//try
            catch(Exception exception){}
        }//finally
    }//main
    //************************************************************************************
    //************************************************************************************
}
