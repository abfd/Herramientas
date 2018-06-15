
/**
 *
 * @author Administrador
 */
public class Conversiones 
{
    //*****************************************************************************************************************
    //*****************************************************************************************************************
    public static final String CODIGO_VALTECNIC= "01384";
    public static final String CODIGO_GECOPINSA= "04888";
    public static final String EUROS= "EUR";
    
    public static final String codcli = "103";
    //*****************************************************************************************************************
    //*****************************************************************************************************************
    public static String convertTipoinmValtecnicToCodigo(String tipoinm) 
    {
        String codigo = null;
        if ((tipoinm != null) && (!(tipoinm.equals("")))) 
        {
            if(tipoinm.equals("ACB")) codigo = "41";
            else if(tipoinm.equals("ACM")) codigo = "41";
            else if(tipoinm.equals("ADC")) codigo = "06";
            else if(tipoinm.equals("ADT")) codigo = "06";
            else if(tipoinm.equals("ANX")) codigo = "41";
            else if(tipoinm.equals("ART")) codigo = "41";
            else if(tipoinm.equals("AXV")) codigo = "41";
            else if(tipoinm.equals("BPA")) codigo = "41";
            else if(tipoinm.equals("BPB")) codigo = "41";
            else if(tipoinm.equals("BRM")) codigo = "41";
            else if(tipoinm.equals("BRV")) codigo = "41";
            else if(tipoinm.equals("BTM")) codigo = "41";
            else if(tipoinm.equals("C.D")) codigo = "23";
            else if(tipoinm.equals("CDC")) codigo = "23";
            else if(tipoinm.equals("CDT")) codigo = "23";
            else if(tipoinm.equals("CPC")) codigo = "41";
            else if(tipoinm.equals("CPG")) codigo = "41";
            else if(tipoinm.equals("CPT")) codigo = "41";
            else if(tipoinm.equals("DIN")) codigo = "41";
            else if(tipoinm.equals("DIO")) codigo = "41";
            else if(tipoinm.equals("DPE")) codigo = "41";
            else if(tipoinm.equals("DRV")) codigo = "41";
            else if(tipoinm.equals("ECC")) codigo = "13";
            else if(tipoinm.equals("ECT")) codigo = "13";
            else if(tipoinm.equals("EDC")) codigo = "13";
            else if(tipoinm.equals("EDE")) codigo = "13";
            else if(tipoinm.equals("EDT")) codigo = "13";
            else if(tipoinm.equals("EGC")) codigo = "06";
            else if(tipoinm.equals("EGT")) codigo = "06";
            else if(tipoinm.equals("EIC")) codigo = "04";
            else if(tipoinm.equals("EIE")) codigo = "04";
            else if(tipoinm.equals("EIT")) codigo = "04";
            else if(tipoinm.equals("EMS")) codigo = "41";
            else if(tipoinm.equals("EOC")) codigo = "02";
            else if(tipoinm.equals("EOT")) codigo = "02";
            else if(tipoinm.equals("EPA")) codigo = "41";
            else if(tipoinm.equals("EPB")) codigo = "41";
            else if(tipoinm.equals("EPC")) codigo = "41";
            else if(tipoinm.equals("EQI")) codigo = "41";
            else if(tipoinm.equals("EQO")) codigo = "41";
            else if(tipoinm.equals("ERC")) codigo = "13";
            else if(tipoinm.equals("ERE")) codigo = "13";
            else if(tipoinm.equals("ERM")) codigo = "41";
            else if(tipoinm.equals("ERT")) codigo = "13";
            else if(tipoinm.equals("ERV")) codigo = "41";
            else if(tipoinm.equals("ETC")) codigo = "02";
            else if(tipoinm.equals("ETM")) codigo = "41";
            else if(tipoinm.equals("ETR")) codigo = "41";
            else if(tipoinm.equals("ETT")) codigo = "02";
            else if(tipoinm.equals("EXM")) codigo = "41";
            else if(tipoinm.equals("FCE")) codigo = "07";
            else if(tipoinm.equals("FCM")) codigo = "41";
            else if(tipoinm.equals("FCR")) codigo = "07";
            else if(tipoinm.equals("GAC")) codigo = "41";
            else if(tipoinm.equals("GAS")) codigo = "41";
            else if(tipoinm.equals("GAT")) codigo = "41";
            else if(tipoinm.equals("HTC")) codigo = "12";
            else if(tipoinm.equals("HTE")) codigo = "12";
            else if(tipoinm.equals("HTT")) codigo = "12";
            else if(tipoinm.equals("IFC")) codigo = "41";
            else if(tipoinm.equals("IFE")) codigo = "41";
            else if(tipoinm.equals("IN5")) codigo = "41";
            else if(tipoinm.equals("INJ")) codigo = "41";
            else if(tipoinm.equals("INP")) codigo = "41";
            else if(tipoinm.equals("LCB")) codigo = "03";
            else if(tipoinm.equals("LCC")) codigo = "03";
            else if(tipoinm.equals("LCE")) codigo = "03";
            else if(tipoinm.equals("LCS")) codigo = "03";
            else if(tipoinm.equals("LCT")) codigo = "03";
            else if(tipoinm.equals("MAQ")) codigo = "41";
            else if(tipoinm.equals("MOB")) codigo = "41";
            else if(tipoinm.equals("NMB")) codigo = "04";
            else if(tipoinm.equals("NMC")) codigo = "04";
            else if(tipoinm.equals("NMS")) codigo = "04";
            else if(tipoinm.equals("NMT")) codigo = "04";
            else if(tipoinm.equals("NUB")) codigo = "04";
            else if(tipoinm.equals("NUC")) codigo = "04";
            else if(tipoinm.equals("NUE")) codigo = "04";
            else if(tipoinm.equals("NUS")) codigo = "04";
            else if(tipoinm.equals("NUT")) codigo = "04";
            else if(tipoinm.equals("OFB")) codigo = "02";
            else if(tipoinm.equals("OFC")) codigo = "02";
            else if(tipoinm.equals("OFE")) codigo = "41";
            else if(tipoinm.equals("OFT")) codigo = "02";
            else if(tipoinm.equals("PAT")) codigo = "41";
            else if(tipoinm.equals("PDT")) codigo = "41";
            else if(tipoinm.equals("PKD")) codigo = "06";
            else if(tipoinm.equals("PRN")) codigo = "13";
            else if(tipoinm.equals("PRT")) codigo = "41";
            else if(tipoinm.equals("PZC")) codigo = "05";
            else if(tipoinm.equals("PZE")) codigo = "05";
            else if(tipoinm.equals("PZG")) codigo = "05";
            else if(tipoinm.equals("PZL")) codigo = "05";
            else if(tipoinm.equals("PZT")) codigo = "05";
            else if(tipoinm.equals("PZV")) codigo = "05";
            else if(tipoinm.equals("RCE")) codigo = "41";
            else if(tipoinm.equals("SGO")) codigo = "13";
            else if(tipoinm.equals("SGV")) codigo = "01";
            else if(tipoinm.equals("SOE")) codigo = "09";
            else if(tipoinm.equals("SOL")) codigo = "09";
            else if(tipoinm.equals("TEE")) codigo = "09";
            else if(tipoinm.equals("TRC")) codigo = "24";
            else if(tipoinm.equals("TRE")) codigo = "24";
            else if(tipoinm.equals("TRL")) codigo = "24";
            else if(tipoinm.equals("TRR")) codigo = "09";
            else if(tipoinm.equals("TRT")) codigo = "24";
            else if(tipoinm.equals("TRV")) codigo = "24";
            else if(tipoinm.equals("TRZ")) codigo = "25";
            else if(tipoinm.equals("VEH")) codigo = "41";
            else if(tipoinm.equals("VPC")) codigo = "01";
            else if(tipoinm.equals("VPE")) codigo = "01";
            else if(tipoinm.equals("VPI")) codigo = "01";
            else if(tipoinm.equals("VPS")) codigo = "01";
            else if(tipoinm.equals("VPT")) codigo = "01";
            else if(tipoinm.equals("VUC")) codigo = "42";
            else if(tipoinm.equals("VUE")) codigo = "42";
            else if(tipoinm.equals("VUI")) codigo = "42";
            else if(tipoinm.equals("VUS")) codigo = "42";
            else if(tipoinm.equals("VUT")) codigo = "42";
            else if(tipoinm.equals("XXC")) codigo = "41";
            else if(tipoinm.equals("XXX")) codigo = "41";
        }//if
        return codigo;
    }//convertTipoinmValtecnicToCodigo
    
    //*****************************************************************************************************************
    //*****************************************************************************************************************
    public static String convertCodigoToTipoinmValtecnic(String codigo) {
        String tipoinm = null;
        if ((codigo != null) && (!(codigo.equals("")))) {
            if (codigo.equals("01")) {
                tipoinm = "VPT";
            }//else if 
            else if (codigo.equals("02")) 
            {
                tipoinm = "OFT";
            }//else if 
            else if (codigo.equals("03")) 
            {
                tipoinm = "LCT";
            }//else if 
            else if (codigo.equals("04")) 
            {
                tipoinm = "NUT";
            }//else if 
            else if (codigo.equals("05")) 
            {
                tipoinm = "PZT";
            }//else if 
            else if (codigo.equals("06")) 
            {
                tipoinm = "EGT";
            }//else if 
            else if (codigo.equals("07")) 
            {
                tipoinm = "FCR";
            }//else if 
            else if (codigo.equals("08")) 
            {
                tipoinm = "FCR";
            }//else if 
            else if (codigo.equals("09")) 
            {
                tipoinm = "SOL";
            }//else if 
            else if (codigo.equals("10")) 
            {
                tipoinm = "TRR";
            }//else if 
            else if (codigo.equals("11")) 
            {
                tipoinm = "TRR";
            }//else if 
            else if (codigo.equals("12")) 
            {
                tipoinm = "HTT";
            }//else if 
            else if (codigo.equals("13")) 
            {
                tipoinm = "ERT";
            }//else if 
            else if (codigo.equals("14")) 
            {
                tipoinm = "NUT";
            }//else if 
            else if (codigo.equals("15")) 
            {
                tipoinm = "LCT";
            }//else if 
            else if (codigo.equals("17")) 
            {
                tipoinm = "VPT";
            }//else if 
            else if (codigo.equals("20")) 
            {
                tipoinm = "XXX";
            }//else if 
            else if (codigo.equals("21")) 
            {
                tipoinm = "EDT";
            }//else if 
            else if (codigo.equals("22")) 
            {
                tipoinm = "EDT";
            }//else if 
            else if (codigo.equals("23")) 
            {
                tipoinm = "EDT";
            }//else if 
            else if (codigo.equals("24")) 
            {
                tipoinm = "TRT";
            }//else if 
            else if (codigo.equals("25")) 
            {
                tipoinm = "TRZ";
            }//else if 
            else if (codigo.equals("30")) 
            {
                tipoinm = "TRR";
            }//else if 
            else if (codigo.equals("35")) 
            {
                tipoinm = "TRR";
            }//else if 
            else if (codigo.equals("40")) 
            {
                tipoinm = "NUT";
            }//else if 
            else if (codigo.equals("41")) 
            {
                tipoinm = "XXX";
            }//else if 
            else if (codigo.equals("42")) 
            {
                tipoinm = "VUT";
            }//else if
            else if (codigo.equals("45")) 
            {
                tipoinm = "SOL";
            }//else if
        }//if
        return tipoinm;
    }//convertCodigoToTipoinmValtecnic
    
    //*****************************************************************************************************************
    //*****************************************************************************************************************
    public static String convertCalidadValtecnicToBBVA(String calidadValtecnic) 
    {
        String sCalidadActual = "2";
        if(calidadValtecnic!=null && !calidadValtecnic.trim().equals("")) 
        {
            if(calidadValtecnic.equals("1")) sCalidadActual = "1";
            else if(calidadValtecnic.equals("2")) sCalidadActual = "3";
            else if(calidadValtecnic.equals("3")) sCalidadActual = "5";
        }//if
        return sCalidadActual;
    }//convertCalidadValtecnicToBBVA
    
    //*****************************************************************************************************************
    //*****************************************************************************************************************
    public static String formatearFechaValtecnicToBBVA(String fechaValtecnic)
    {
        String fechaBBVA = null;
        if(fechaValtecnic!=null && !fechaValtecnic.trim().equals(""))
        {
            fechaBBVA = fechaValtecnic.replace('-','/');
        }//if
        return fechaBBVA;
    }//formatearFechaValtecnicToBBVA
    
    //*****************************************************************************************************************
    //*****************************************************************************************************************
    public static String formatearFechaValtecnicToBBVA(java.util.Date fechaValtecnic)
    {
        String fechaBBVA = null;
        if(fechaValtecnic!=null)
        {
            fechaBBVA = Utilidades.Cadenas.getDateParse(fechaValtecnic,"dd/MM/yyyy");
        }//if
        return fechaBBVA;
    }//formatearFechaValtecnicToBBVA
    
    //*****************************************************************************************************************
    //*****************************************************************************************************************
    public static String getTipoImpuestoBBVA(double iva)
    {
        String impuesto = "A";
        if(iva==16) impuesto = "1"; // IVA 16
        else if(iva==21) impuesto = "8"; // IVA 21
        else if(iva==8) impuesto = "9"; // IVA 21
        else if(iva==5) impuesto = "B"; // IGIC 5
        else if(iva==4) impuesto = "3"; // IPSI 4
        else if(iva==0) impuesto = "4"; // IPSI 0
        else if(iva==7) impuesto = "5"; // IVA 7
        else if(Double.toString(iva).equals(Utilidades.Constantes.IGIC_POR_DEFECTO)) impuesto = "B"; // IVA 18
        else if(iva==4) impuesto = "6"; // IVA 4
        else if(iva==2) impuesto = "7"; // IGIC 2
        else if(iva==0) impuesto = "8"; // IGIC 0
        else if(iva==18) impuesto = "A"; // IVA 18
        else if(Double.toString(iva).equals(Utilidades.Constantes.IVA_POR_DEFECTO)) impuesto = "A"; // IVA 18
        return impuesto;
    }//getTipoImpuestoBBVA
    
    //*****************************************************************************************************************
    //*****************************************************************************************************************
    public static String getTextoCodigoBBVA(String codigo) 
    {
        String tipoinm = null;
        if ((codigo != null) && (!(codigo.equals("")))) 
        {
            if (codigo.equals("01")) 
            {
                tipoinm = "VIVIENDA";
            }//else if 
            else if (codigo.equals("02")) 
            {
                tipoinm = "OFICINA";
            }//else if 
            else if (codigo.equals("03")) 
            {
                tipoinm = "LOCAL COMERCIAL";
            }//else if 
            else if (codigo.equals("04")) 
            {
                tipoinm = "NAVE INDUSTRIAL";
            }//else if 
            else if (codigo.equals("05")) 
            {
                tipoinm = "PLAZA GARAGE";
            }//else if 
            else if (codigo.equals("06")) 
            {
                tipoinm = "PARKING";
            }//else if 
            else if (codigo.equals("07")) 
            {
                tipoinm = "TERRENO RÚSTICO";
            }//else if 
            else if (codigo.equals("08")) 
            {
                tipoinm = "TERRENO AGRÍCOLA";
            }//else if 
            else if (codigo.equals("09")) 
            {
                tipoinm = "SOLAR";
            }//else if 
            else if (codigo.equals("10")) 
            {
                tipoinm = "PARCELA";
            }//else if 
            else if (codigo.equals("11")) 
            {
                tipoinm = "TERRENO INDUSTRIAL";
            }//else if 
            else if (codigo.equals("12")) 
            {
                tipoinm = "HOTEL";
            }//else if 
            else if (codigo.equals("13")) 
            {
                tipoinm = "EDIFICIO";
            }//else if 
            else if (codigo.equals("14")) 
            {
                tipoinm = "BODEGA";
            }//else if 
            else if (codigo.equals("15")) 
            {
                tipoinm = "LOCAL INDUSTRIAL";
            }//else if 
            else if (codigo.equals("17")) 
            {
                tipoinm = "VIVIENDA OFICIAL";
            }//else if 
            else if (codigo.equals("20")) 
            {
                tipoinm = "PLANTA EDIFICIO";
            }//else if 
            else if (codigo.equals("21")) 
            {
                tipoinm = "CLUB SOCIAL";
            }//else if 
            else if (codigo.equals("22")) 
            {
                tipoinm = "PARTICIPACIÓN CLUB";
            }//else if 
            else if (codigo.equals("23")) 
            {
                tipoinm = "ZONA DEPORTIVA";
            }//else if 
            else if (codigo.equals("24")) 
            {
                tipoinm = "TRASTERO";
            }//else if 
            else if (codigo.equals("25")) 
            {
                tipoinm = "TERRAZA";
            }//else if 
            else if (codigo.equals("30")) 
            {
                tipoinm = "PATIO";
            }//else if 
            else if (codigo.equals("35")) 
            {
                tipoinm = "JARDÍN-HUERTO";
            }//else if 
            else if (codigo.equals("40")) 
            {
                tipoinm = "CUADRA";
            }//else if 
            else if (codigo.equals("41")) 
            {
                tipoinm = "OTROS";
            }//else if 
            else if (codigo.equals("42")) 
            {
                tipoinm = "CHALET";
            }//else if 
            else if (codigo.equals("45")) 
            {
                tipoinm = "SUELO";
            }//else if 
        }//if            
        return tipoinm;
    }//getTextCodigoBBVAToTipoinm

    //*****************************************************************************************************************
    //*****************************************************************************************************************
    public static String getTipoVivienda(Objetos.Elemento1 elemento1)
    {
        String sTipo = "06";
        if(elemento1!=null && elemento1.tipoedif!=null && !elemento1.tipoedif.equals("") && elemento1.tipoinm!=null && !elemento1.tipoinm.equals(""))
        {
            if(elemento1.tipoinm.substring(1,2).toUpperCase().equals("P"))
            {
                sTipo = "06"; // - EXTERIOR
            }//if
            else
            {
                if(elemento1.tipoedif.equals("1")) sTipo = "07"; // - AISLADA
                else if(elemento1.tipoedif.equals("2")) sTipo = "08"; // - ADOSADA
                else if(elemento1.tipoedif.equals("3")) sTipo = "08"; // - ADOSADA
            }//else
        }//if
        return sTipo;
    }//getTipoVivienda


    //*****************************************************************************************************************
    //*****************************************************************************************************************
    public static String getTipoPatrimonio(String codigo) 
    {
        String tipoinm = null;
        if ((codigo != null) && (!(codigo.equals("")))) 
        {
            codigo = codigo.trim();
            if (codigo.equals("C")) 
            {
                tipoinm = "COMPRAS";
            }//else if 
            else if (codigo.equals("P")) 
            {
                tipoinm = "PATRIMONIO";
            }//else if 
            else if (codigo.equals("S")) 
            {
                tipoinm = "SUBASTA";
            }//else if 
            else if (codigo.equals("X")) 
            {
                tipoinm = "DACIONES EN PAGO";
            }//else if 
            else if (codigo.equals("H")) 
            {
                tipoinm = "HIPOTECARIO";
            }//else if
        }//if
        return tipoinm;
    }//getTipoPatrimonio

    //*****************************************************************************************************************
    //*****************************************************************************************************************
    public static String getLetraGarantia(int iNumeroGarantia) 
    {
        String sGarantia = "";
        switch (iNumeroGarantia) {
            case 1:
                sGarantia = "";
                break;
            case 2:
                sGarantia = " B";
                break;
            case 3:
                sGarantia = " C";
                break;
            case 4:
                sGarantia = " D";
                break;
            case 5:
                sGarantia = " E";
                break;
            case 6:
                sGarantia = " F";
        }//switch
        return sGarantia;
    }//getLetraGarantia


    //*****************************************************************************************************************
    //*****************************************************************************************************************
    public static String getTextCodigoBBVAToTipoinm(String codigo) 
    {
        String tipoinm = null;
        if ((codigo != null) && (!(codigo.equals("")))) 
        {
            if (codigo.equals("01")) 
            {
                tipoinm = "VIVIENDA";
            }//else if 
            else if (codigo.equals("02")) 
            {
                tipoinm = "OFICINA";
            }//else if 
            else if (codigo.equals("03")) 
            {
                tipoinm = "LOCAL COMERCIAL";
            }//else if 
            else if (codigo.equals("04")) 
            {
                tipoinm = "NAVE INDUSTRIAL";
            }//else if 
            else if (codigo.equals("05")) 
            {
                tipoinm = "PLAZA GARAGE";
            }//else if 
            else if (codigo.equals("06")) 
            {
                tipoinm = "PARKING";
            }//else if 
            else if (codigo.equals("07")) 
            {
                tipoinm = "TERRENO RÚSTICO";
            }//else if 
            else if (codigo.equals("08")) 
            {
                tipoinm = "TERRENO AGRÍCOLA";
            }//else if 
            else if (codigo.equals("09")) 
            {
                tipoinm = "SOLAR";
            }//else if 
            else if (codigo.equals("10")) 
            {
                tipoinm = "PARCELA";
            }//else if 
            else if (codigo.equals("11")) 
            {
                tipoinm = "TERRENO INDUSTRIAL";
            }//else if 
            else if (codigo.equals("12")) 
            {
                tipoinm = "HOTEL";
            }//else if 
            else if (codigo.equals("13")) 
            {
                tipoinm = "EDIFICIO";
            }//else if 
            else if (codigo.equals("14")) 
            {
                tipoinm = "BODEGA";
            }//else if 
            else if (codigo.equals("15")) 
            {
                tipoinm = "LOCAL INDUSTRIAL";
            }//else if 
            else if (codigo.equals("17")) 
            {
                tipoinm = "VIVIENDA OFICIAL";
            }//else if 
            else if (codigo.equals("20")) 
            {
                tipoinm = "PLANTA EDIFICIO";
            }//else if 
            else if (codigo.equals("21")) 
            {
                tipoinm = "CLUB SOCIAL";
            }//else if 
            else if (codigo.equals("22")) 
            {
                tipoinm = "PARTICIPACIÓN CLUB";
            }//else if 
            else if (codigo.equals("23")) 
            {
                tipoinm = "ZONA DEPORTIVA";
            }//else if 
            else if (codigo.equals("24")) 
            {
                tipoinm = "TRASTERO";
            }//else if 
            else if (codigo.equals("25")) 
            {
                tipoinm = "TERRAZA";
            }//else if 
            else if (codigo.equals("30")) 
            {
                tipoinm = "PATIO";
            }//else if 
            else if (codigo.equals("35")) 
            {
                tipoinm = "JARDÍN-HUERTO";
            }//else if 
            else if (codigo.equals("40")) 
            {
                tipoinm = "CUADRA";
            }//else if 
            else if (codigo.equals("41")) 
            {
                tipoinm = "OTROS";
            }//else if 
            else if (codigo.equals("42")) 
            {
                tipoinm = "CHALET";
            }//else if 
            else if (codigo.equals("45")) 
            {
                tipoinm = "SUELO";
            }//else if 
        }//if            
        return tipoinm;
    }//getTextCodigoBBVAToTipoinm
    
    //*****************************************************************************************************************
    //*****************************************************************************************************************
    public static String getTipoTasacionHipotecaria(String codigo) 
    {
        String tipoinm = null;
        if ((codigo != null) && (!(codigo.equals("")))) 
        {
            codigo = codigo.trim();
            if (codigo.equals("PN")) 
            {
                tipoinm = "PRÉSTAMO NUEVO";
            }//else if 
            else if (codigo.equals("NO")) 
            {
                tipoinm = "NOVACIÓN";
            }//else if 
            else if (codigo.equals("SA")) 
            {
                tipoinm = "SUBROGACIÓN ACREEDOR";
            }//else if 
            else if (codigo.equals("PR")) 
            {
                tipoinm = "PROMOTOR";
            }//else if 
            else if (codigo.equals("VI")) 
            {
                tipoinm = "VALORACIÓN INTERMEDIA DE OBRA";
            }//else if 
            else if (codigo.equals("AT")) 
            {
                tipoinm = "ACTUALIZACIÓN TASACIÓN";
            }//else if 
            else if (codigo.equals("PP")) 
            {
                tipoinm = "PROMOCIÓN SUELO PREVIAMENTE TASADO";
            }//else if 
            else if (codigo.equals("2F")) 
            {
                tipoinm = "SEGUNDA FASE PROMOCIÓN YA TASADA";
            }//else if 
            else if (codigo.equals("IP")) 
            {
                tipoinm = "INFORME PRETASACIÓN YA REALIZADO";
            }//else if 
        }//if
        return tipoinm;
    }//getTipoTasacionHipotecaria
    
    //*****************************************************************************************************************
    //*****************************************************************************************************************
    public static String getTipoVia(String tipovia) 
    {
        
        String tipoviabbva = "CL";
        if ((tipovia != null) && (!(tipovia.equals("")))) 
        {
            tipovia = tipovia.trim().toUpperCase();
            if(tipovia.equals("AVD")) tipoviabbva = "AV";
            else if(tipovia.equals("AVDA")) tipoviabbva = "AV";
            else if(tipovia.equals("AYO.")) tipoviabbva = "AY";
            else if(tipovia.equals("BO.")) tipoviabbva = "BO";
            else if(tipovia.equals("C/")) tipoviabbva = "CL";
            else if(tipovia.equals("CARR")) tipoviabbva = "CL";
            else if(tipovia.equals("CJON")) tipoviabbva = "CJ";
            else if(tipovia.equals("CJTO")) tipoviabbva = "CU";
            else if(tipovia.equals("COL")) tipoviabbva = "CN";
            else if(tipovia.equals("Cª")) tipoviabbva = "CR";
            else if(tipovia.equals("Cº")) tipoviabbva = "CM";
            else if(tipovia.equals("FINC")) tipoviabbva = "FN";
            else if(tipovia.equals("GR.")) tipoviabbva = "GR";
            else if(tipovia.equals("GRTA")) tipoviabbva = "GL";
            else if(tipovia.equals("PAR")) tipoviabbva = "LG";
            else if(tipovia.equals("PL.")) tipoviabbva = "PZ";
            else if(tipovia.equals("POLR")) tipoviabbva = "PL";
            else if(tipovia.equals("PROL")) tipoviabbva = "PR";
            else if(tipovia.equals("PSJE")) tipoviabbva = "PJ";
            else if(tipovia.equals("PTDA")) tipoviabbva = "PD";
            else if(tipovia.equals("Pº.")) tipoviabbva = "PS";
            else if(tipovia.equals("RBLA")) tipoviabbva = "RB";
            else if(tipovia.equals("RDA.")) tipoviabbva = "RD";
            else if(tipovia.equals("RTDA")) tipoviabbva = "CL";
            else if(tipovia.equals("RUA")) tipoviabbva = "RU";
            else if(tipovia.equals("TRV.")) tipoviabbva = "TR";
            else if(tipovia.equals("VIA")) tipoviabbva = "VI";
            else tipoviabbva = "CL";
        }
        return tipoviabbva;
    }//getTipoVia
    
    //*****************************************************************************************************************
    //*****************************************************************************************************************
    
}//Conversiones
