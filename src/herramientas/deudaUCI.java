/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package herramientas;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author Ana Belen de Frutos
 */
public class deudaUCI 
{
    
    //private String sUrlExcel = "/data/informes/deudaUCI/DEUDA UCI HASTA 31 10 11 POR ESTADO.xls";
    private String sUrlExcel = "/data/informes/deudaUCI/mal.xls";
    private Logger logger = Logger.getLogger(deudaUCI.class);
    
    private String numfactvt = null;
    private java.sql.Connection conexion = null;
    private Objetos.Prencargos oPrencargos = null;
    private Objetos.Factuci oFactuci = null;
    private Objetos.v2.Facturas oFacturas = null;
    
    public static void main(String[] args) 
    {
        deudaUCI carga = new deudaUCI();
        //deudaUCI carga = new deudaUCI(2);
        carga = null;
        System.gc();
    }
    
    public deudaUCI()
    {//Inserción en factuci de facturas que no se llegaron a dar de alta.
       
        
         int iNumeroFilasAfectadas = 0;
         double aux_neto = 0;
        
         try
         {
            //log4j
            PropertyConfigurator.configure("/data/informes/deudaUCI/" + "Log4j.properties");   
             
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@orabackup:1521:rvtn");
            conexion.setAutoCommit(false);
            String hoja = "Hoja1";   //nombre de la hora
            Utilidades.Excel oExcel = new Utilidades.Excel(sUrlExcel);            
            org.apache.poi.hssf.usermodel.HSSFCell celda = null;
            for (int fila = 0; fila < 1;fila ++)              
            {                                                
                
                celda = oExcel.getCeldaFilaHoja(hoja,fila,0);  
                if (celda != null) 
                {
                    numfactvt = celda.getStringCellValue().trim();                    
                }                
                if (numfactvt != null)
                {
                    //1.- debe estar en prencargos. Comprobamos:
                    oPrencargos = new Objetos.Prencargos();
                    if (oPrencargos.load(numfactvt, conexion))
                    {//esta en prencargos
                        //2.- comprobamos que está facturado en facturas
                        oFacturas = new Objetos.v2.Facturas();
                        if (oFacturas.load(numfactvt, conexion) == 1)
                        {//existe en facturas
                            //3.- comprobamos que no exista ya en factuci
                            oFactuci = new Objetos.Factuci();
                            if (oFactuci.loadValuesFromNumexp(numfactvt, conexion) == 0)
                            {//no existe en factuci. DAR DE ALTA
                                aux_neto = 0;
                                if(oFacturas.impbonif<=0) aux_neto = oFacturas.impnet;
                                else aux_neto = oFacturas.impnet + oFacturas.impbonif;
                                //insertamos
                                iNumeroFilasAfectadas = 0;
                                iNumeroFilasAfectadas = f_fu(oFacturas.numfac,oFacturas.fchfact, aux_neto,oFacturas.impiva,oFacturas.imptot,oFacturas.pagcue,conexion);
                                if (iNumeroFilasAfectadas != 1) 
                                {
                                    logger.error(numfactvt+". No Insertada en FACTUCI");
                                    conexion.rollback();
                                }
                                else
                                {
                                    logger.info(numfactvt+". Insertada en FACTUCI");
                                    conexion.commit();
                                }
                            }
                            else
                            {
                                logger.info(numfactvt+". Ya existe en FACTUCI");
                            }
                            
                        }
                        else
                        {//no esta facturado.
                            logger.info(numfactvt+". No existe en FACTURAS");
                        }
                        
                    }
                    else logger.info(numfactvt+". No existe en PRENCARGOS");
                }
                
                oPrencargos = null;
                oFactuci = null;
                oFacturas = null;
            }
            conexion.close();
         }
         catch (Exception e)
         {
             logger.error(numfactvt+". Descripcion: "+e.toString());
         }
         finally
         {
            try
            {
                if (conexion != null && !conexion.isClosed()) conexion.close();                
            }
            catch (java.sql.SQLException sqlException)
            {     
                logger.error ("Imposible cerrar conexión con la B.D Informix");
            }             
         }//finally
       
    }
    
    public deudaUCI(int b)
    {//Inserción en factuci de facturas que no se llegaron a dar de alta.
       
        
         int iNumeroFilasAfectadas = 0;
         double aux_neto = 0;
        
         try
         {
            //log4j
            PropertyConfigurator.configure("/data/informes/deudaUCI/" + "Log4j.properties");   
             
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@orabackup:1521:rvtn");
            conexion.setAutoCommit(false);
            String hoja = "NO VOLCADAS";   //nombre de la hora
            Utilidades.Excel oExcel = new Utilidades.Excel(sUrlExcel);            
            org.apache.poi.hssf.usermodel.HSSFCell celda = null;
            for (int fila = 2; fila < 119;fila ++)              
            {                                                
                
                celda = oExcel.getCeldaFilaHoja(hoja,fila,0);  
                if (celda != null) 
                {
                    numfactvt = celda.getStringCellValue().trim();                    
                }                
                if (numfactvt != null)
                {
                    //1.- debe estar en prencargos. Comprobamos:
                    oPrencargos = new Objetos.Prencargos();
                    if (oPrencargos.load(numfactvt, conexion))
                    {//esta en prencargos
                        //2.- comprobamos que está facturado en facturas
                        oFacturas = new Objetos.v2.Facturas();
                        if (oFacturas.load(numfactvt, conexion) == 1)
                        {//existe en facturas
                            //3.- comprobamos que no exista ya en factuci
                            oFactuci = new Objetos.Factuci();
                            if (oFactuci.loadValuesFromNumexp(numfactvt, conexion) == 0)
                            {//no existe en factuci. DAR DE ALTA
                               logger.info(numfactvt+". NO existe en FACTUCI");
                            }
                            else
                            {
                               logger.info(numfactvt+". En FACTUCI con estodf: "+oFactuci.estadof+" y estadoe: "+oFactuci.estadoe);
                            }
                            
                        }
                        else
                        {//no esta facturado.
                            logger.info(numfactvt+". No existe en FACTURAS");
                        }
                        
                    }
                    else logger.info(numfactvt+". No existe en PRENCARGOS");
                }
                
                oPrencargos = null;
                oFactuci = null;
                oFacturas = null;
            }
            conexion.close();
         }
         catch (Exception e)
         {
             logger.error(numfactvt+". Descripcion: "+e.toString());
         }
         finally
         {
            try
            {
                if (conexion != null && !conexion.isClosed()) conexion.close();                
            }
            catch (java.sql.SQLException sqlException)
            {     
                logger.error ("Imposible cerrar conexión con la B.D Informix");
            }             
         }//finally
       
    }
    
    
    
    private int f_fu(String numfac,java.util.Date fchfact,double impnet,double impiva,double imptot,double pagcue,java.sql.Connection conexion) throws java.sql.SQLException, java.lang.Exception
    {
        String operacion = null; // - A : Alta || M : Modificación
        int iNumeroRegistrosAfectados = 0;
        // - 24-11-2011: SE AÑADE LA COMPROBACIÓN PARA HACER OBLIGATORIO QUE SEA UN ENCARGO ATUOMÁTICO.
        if(fchfact.compareTo(new java.util.Date(30,4,2002))>0 && Clientes.uci.Comunes.esEncargoAutomatico(numfac, conexion))
        {
            // - SE BUSCA LA FACTURA DE FACTUCI
            int iNumorden = Objetos.Factuci.getCountNumorden(numfac, conexion);
            if(iNumorden==0) operacion = "A";
            else operacion = "M";
            Objetos.Factuci oFactuci = new Objetos.Factuci();
            //iNumeroRegistrosAfectados = oFactuci.loadValuesFromPrencargos(numfac,conexion);
            oFactuci.loadValuesFromPrencargos(numfac,conexion);
            if(operacion.equals("A"))
            {
                oFactuci.fchfacvt = fchfact;
                oFactuci.numfacvt = numfac;
                oFactuci.numexpv = numfac;
                oFactuci.numorden = new Integer(iNumorden+1);
                oFactuci.impnetc = new Double(impnet);
                oFactuci.impivac = new Double(impiva);
                oFactuci.imptotc = new Double(imptot);
                // - CÁLCULO DE INGRESO NETO DE VALTECNIC.
                oFactuci.ingnetv = new Double(impnet - pagcue);
                // - IVA QUE INGRESA VALTECNIC.
                String[] iva = f_dame_impuesto(numfac, conexion);
                oFactuci.ingivav = new Double(f_redondea_iva(oFactuci.ingnetv.doubleValue(),Integer.parseInt(iva[1])));
                // - TOTAL QUE INGRESA VALTECNIC
                oFactuci.ingtotv = new Double(oFactuci.ingnetv.doubleValue() + oFactuci.ingivav.doubleValue());
                oFactuci.abonetu = new Double(0);
                oFactuci.aboivau = new Double(0);
                oFactuci.abototu = new Double(0);
                oFactuci.impnettas = new Double(0);
                oFactuci.impivatas = new Double(0);
                oFactuci.imptottas = new Double(0);
                oFactuci.estadof = "9";
                oFactuci.fchabono = Utilidades.Cadenas.getDate(1,1,1940);
                oFactuci.fchcierre = Utilidades.Cadenas.getDate(1,1,1940);
                oFactuci.estadoe = "4";
                oFactuci.diasvt = new Integer(Utilidades.Valtecnic.dameDiasVT(numfac, conexion));
                iNumeroRegistrosAfectados = oFactuci.insertFactuci(conexion);
            }//if
            else
            {
                // - SE BUSCA LA FACTURA EN FACTUCI
                iNumeroRegistrosAfectados = oFactuci.loadValuesFromNumexp(numfac,Utilidades.Cadenas.getDate(1,1,1940),iNumorden,conexion);
                if(iNumeroRegistrosAfectados==1)
                {
                    if(oFactuci.ingnetv.doubleValue()!=impnet-pagcue)
                    {
                        if(Utilidades.Cadenas.getValorMostrarWeb(oFactuci.estadof).equals("7"))
                        {
                            // - FACTURA YA ABONADA, SE ANULA Y SE GENERA UNA NUEVA
                            /*oFactuci.estadof = "8";
                            oFactuci.fchcierre = new java.util.Date(); 
                            oFactuci.numfacvt = numfac;
                            oFactuci.numorden = new Integer(iNumorden);
                            //oFactuci.estadoe = "3";
                            iNumeroRegistrosAfectados = oFactuci.updateFactuci("01-01-1940",conexion);*/
                            iNumeroRegistrosAfectados = Objetos.Factuci.anularFacturaAbonada(numfac,iNumorden,conexion);
                            if(iNumeroRegistrosAfectados == 1)
                            {
                                // - CREACIÓN DE UN NUEVO REGISTRO ACTIVO.
                                Objetos.Factuci oFactuciAux = new Objetos.Factuci();
                                //oFactuciAux = oFactuci;
                                oFactuciAux.oficina = oFactuci.oficina;
                                oFactuciAux.numexpc = oFactuci.numexpc;
                                oFactuciAux.ag_obj = oFactuci.ag_obj;
                                oFactuciAux.objeto = oFactuci.objeto;
                                oFactuciAux.num_tas = oFactuci.num_tas;                                
                                
                                oFactuciAux.fchfacvt = fchfact;
                                oFactuciAux.numfacvt = numfac;
                                oFactuciAux.numorden = new Integer(iNumorden+1);
                                oFactuciAux.numexpv = numfac;
                                oFactuciAux.impnetc = new Double(impnet);
                                oFactuciAux.impivac = new Double(impiva);
                                oFactuciAux.imptotc = new Double(imptot);
                                // - CÁLCULO DE INGRESO NETO DE VALTECNIC.
                                oFactuciAux.ingnetv = new Double(impnet - pagcue);
                                // - IVA QUE INGRESA VALTECNIC.
                                String[] iva = f_dame_impuesto(numfac, conexion);
                                oFactuciAux.ingivav = Utilidades.Matematicas.procesarNumeroDecimales(new Double(f_redondea_iva(oFactuciAux.ingnetv.doubleValue(),(int)Double.parseDouble(Utilidades.Cadenas.getValorDecimalBBDD(iva[1])))),2);
                                // - TOTAL QUE INGRESA VALTECNIC
                                oFactuciAux.ingtotv = Utilidades.Matematicas.procesarNumeroDecimales(new Double(oFactuciAux.ingnetv.doubleValue() + oFactuciAux.ingivav.doubleValue()),2);
                                oFactuciAux.abonetu = new Double(0);
                                oFactuciAux.aboivau = new Double(0);
                                oFactuciAux.abototu = new Double(0);
                                oFactuciAux.impnettas = new Double(0);
                                oFactuciAux.impivatas = new Double(0);
                                oFactuciAux.imptottas = new Double(0);
                                oFactuciAux.fchabono = Utilidades.Cadenas.getDate(1,1,1940);
                                oFactuciAux.estadof = "9";
                                oFactuciAux.fchcierre = Utilidades.Cadenas.getDate(1,1,1940);
                                oFactuciAux.estadoe = "4";
                                oFactuciAux.diasvt = new Integer(Utilidades.Valtecnic.dameDiasVT(numfac, conexion));
                                iNumeroRegistrosAfectados = oFactuciAux.insertFactuci(conexion);
                                oFactuciAux = null;
                            }//if
                            else Utilidades.Correo.sendMail("valtecnic.com","informatica@valtecnic.com","abfd@valtencic.com;alberto.sanz@valtecnic.com",null,null,"F_FU: EXPEDIENTE "+numfac,"IMPOSIBLE ANULAR FACTURA DE UCI.",false);
                        }//if
                        else
                        {
                            oFactuci.impnetc = new Double(impnet);
                            oFactuci.impivac = new Double(impiva);
                            oFactuci.imptotc = new Double(imptot);
                            oFactuci.diasvt = new Integer(Utilidades.Valtecnic.dameDiasVT(numfac, conexion));
                            // - CÁLCULO DE INGRESO NETO DE VALTECNIC.
                            oFactuci.ingnetv = new Double(impnet - pagcue);
                            // - IVA QUE INGRESA VALTECNIC.
                            String[] iva = f_dame_impuesto(numfac, conexion);
                            oFactuci.ingivav = new Double(f_redondea_iva(oFactuci.ingnetv.doubleValue(),Integer.parseInt(iva[1])));
                            // - TOTAL QUE INGRESA VALTECNIC
                            oFactuci.ingtotv = new Double(oFactuci.ingnetv.doubleValue() + oFactuci.ingivav.doubleValue());
                            if(oFactuci.ingtotv.doubleValue()==oFactuci.abototu.doubleValue()) oFactuci.estadof="9";
                            else oFactuci.estadof="9";
                            oFactuci.estadoe = "4";
                            oFactuci.numfacvt = numfac;
                            oFactuci.fchcierre = Utilidades.Cadenas.getDate(1,1,1940);
                            oFactuci.numorden = new Integer(iNumorden);
                            iNumeroRegistrosAfectados = oFactuci.updateFactuci(conexion);
                        }//else
                    }//if
                }//if
                else Utilidades.Correo.sendMail("valtecnic.com","informatica@valtecnic.com","abfd@valtencic.com;alberto.sanz@valtecnic.com",null,null,"F_FU: EXPEDIENTE "+numfac,"MÁS DE DOS REGISTROS CON EL MISMO NÚMERO DE ORDEN Y FECHA 01-01-1940 EN FACTUCI.",false);
            }//else
            oFactuci = null;
        }//if
        operacion = null;
        return iNumeroRegistrosAfectados;
    }//f_fu
    
    private String[] f_dame_impuesto(String numexp,java.sql.Connection conexion) throws java.sql.SQLException, java.lang.Exception
    {
        String[] arrayImpuesto = new String[2];
        java.sql.ResultSet rsDatos = Utilidades.Conexion.getRegistroTablaNumeroExpediente("SOLICITUDES", "CODPOS AS CODIGO,CODCLI AS CLIENTE",numexp, conexion);
        boolean bExiste = rsDatos.next();
        if(!bExiste)
        {
            // - HISTÓRICO
            rsDatos = Utilidades.Conexion.getRegistroTablaNumeroExpediente("HIS_SOLICITUDES", "CODPOS AS CODIGO,CODCLI AS CLIENTE",numexp, conexion);
            if(rsDatos.next()) bExiste = true;
        }//if
        else bExiste = true;
        if(bExiste)
        {
            /*if(Utilidades.Cadenas.getValorMostrarWeb(rsDatos,"CLIENTE").equals("225"))
            {
                // - PARA EL CLIENTE 225 SIEMPRE SE APLICA EL MISMO IVA.
                arrayImpuesto[0] = "I.V.A";
                arrayImpuesto[1] = "16";
            }//if
            else
            {
                String sCodpos = Utilidades.Cadenas.getValorMostrarWeb(rsDatos,"CODIGO","00000");
                sCodpos = Utilidades.Cadenas.getNumberParse(Integer.parseInt(sCodpos), "00000");
                rsDatos = Utilidades.Valtecnic.getProvincias(Integer.parseInt(sCodpos.substring(0,2)), conexion);
                if(rsDatos.next())
                {
                    arrayImpuesto[0] = Utilidades.Cadenas.getValorMostrarWeb(rsDatos,"d_imp");
                    arrayImpuesto[1] = Integer.toString(Utilidades.Cadenas.getValorEnteroMostrarWeb(rsDatos,"t_imp"));
                }//if
                sCodpos = null;
            }//else*/
            String sCodpos = Utilidades.Cadenas.getValorMostrarWeb(rsDatos,"CODIGO","00000");
            sCodpos = Utilidades.Cadenas.getNumberParse(Integer.parseInt(sCodpos), "00000");
            rsDatos = Utilidades.Valtecnic.getProvincias(Integer.parseInt(sCodpos.substring(0,2)), conexion);
            if(rsDatos.next())
            {
                arrayImpuesto[0] = Utilidades.Cadenas.getValorMostrarWeb(rsDatos,"d_imp");
                arrayImpuesto[1] = Integer.toString(Utilidades.Cadenas.getValorEnteroMostrarWeb(rsDatos,"t_imp"));
            }//if
            sCodpos = null;
        }//if
        if(rsDatos!=null) rsDatos.close();
        rsDatos = null;
        return arrayImpuesto;
    }//f_dame_impuesto
    
    private double f_redondea_iva(double valor1,double valor2)
    {
        double producto = 0;
        producto = (valor1 * valor2)/100;
        //producto = Utilidades.Matematicas.procesarNumeroDecimales(producto,3);
        producto = Utilidades.Matematicas.procesarNumeroDecimales(producto,2);
        return producto;
    }//f_redondea_iva
}
