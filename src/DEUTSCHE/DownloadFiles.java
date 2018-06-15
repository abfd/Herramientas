/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DEUTSCHE;

/**
 *
 * @author Alberto
 */
public class DownloadFiles 
{
    
    //******************************************************************************
    //******************************************************************************
    public static int download(java.sql.Connection connection,java.io.File directory) throws Exception
    {
        int iNumeroRegistrosAfectados = 0;
        java.sql.ResultSet oResultSet = null;
        String oString = null;
        Objetos.Citas oCitas = null;
        boolean oBoolean = false;
        String referencia = null;
        try
        {
            oString = "SELECT SOLICITUDES.NUMEXP,REFER.REFERENCIA FROM SOLICITUDES,REFER WHERE SOLICITUDES.NUMEXP=REFER.NUMEXP AND SOLICITUDES.CODEST=10 AND SOLICITUDES.CODCLI=952 AND SOLICITUDES.OFICINA='REVA'"; 
            oResultSet = Utilidades.Conexion.select(oString, connection);
            while(oResultSet!=null && oResultSet.next())
            {
                referencia = oResultSet.getString("REFERENCIA");
                //if(referencia!=null && referencia.indexOf("-")!=-1) referencia = referencia.substring(referencia.indexOf("-")+1);
                //oFile = new java.io.File(directory.getAbsolutePath()+"/"++"/");
                Objetos.Docgrafica.downloadNotaSimple(oResultSet.getString("NUMEXP"),directory.getAbsolutePath(),referencia, connection);
                /*if(oBoolean)
                {
                    oCitas = new Objetos.Citas();
                    oCitas.numexp = oResultSet.getString("NUMEXP");
                    oCitas.codusua = "juan";
                    oCitas.fecha = new java.util.Date();
                    oCitas.hora = Utilidades.Cadenas.getTimestamp();
                    oCitas.tipo = Objetos.Citas.TIPO_ENVIO_PDF_AUDITORA;
                    if(oCitas.insert(connection)!=1) System.out.println("NUMEXP: "+oResultSet.getString("NUMEXP")+" - IMPOSIBLE DE INSERTAR EN CITAS.");
                    else iNumeroRegistrosAfectados++;
                }//if
                else System.out.println("NUMEXP: "+oResultSet.getString("NUMEXP")+" - IMPOSIBLE DE DESCARGAR EL PDF COMPLETO.");
*/
            }//while
        }//try
        catch(Exception exception)
        {
            throw exception;
        }//catch
        finally
        {
            if(oResultSet!=null) oResultSet.close();
        }//finally
        return iNumeroRegistrosAfectados;
    }//download
    
    //******************************************************************************
    //******************************************************************************
    public static int download(java.sql.Connection connection,java.io.File file_in,java.io.File directory_out) throws Exception
    {
        int iNumeroRegistrosAfectados = 0;
        java.sql.ResultSet oResultSet = null;
        String oString = null;
        Objetos.Citas oCitas = null;
        boolean oBoolean = false;
        String referencia_venta_rapida = null;
        String[] numexps = null;
        try
        {
            numexps = Objetos.v2.Solicitudes.getNumexpsFromFile(file_in);
            if(numexps!=null && numexps.length>0)
            {
                for(int iContador=0;iContador<numexps.length;iContador++)               
                {
                    numexps[iContador] = numexps[iContador].trim();
                    if(!new java.io.File(directory_out.getAbsolutePath()+"/"+numexps[iContador]+".pdf").exists())
                    {
                        oBoolean = Objetos.Docgrafica.downloadPDFCompleto(numexps[iContador],directory_out.getAbsolutePath(), connection);
                        if(oBoolean)
                        {
                            oCitas = new Objetos.Citas();
                            oCitas.numexp = numexps[iContador];
                            oCitas.codusua = "juan";
                            oCitas.fecha = new java.util.Date();
                            oCitas.hora = Utilidades.Cadenas.getTimestamp();
                            oCitas.tipo = Objetos.Citas.TIPO_ENVIO_PDF_AUDITORA;
                            if(oCitas.insert(connection)!=1) System.out.println("NUMEXP: "+numexps[iContador]+" - IMPOSIBLE DE INSERTAR EN CITAS.");
                            else iNumeroRegistrosAfectados++;
                        }//if
                        else System.out.println("NUMEXP: "+numexps[iContador]+" - IMPOSIBLE DE DESCARGAR EL PDF COMPLETO.");
                    }//if  
                    referencia_venta_rapida = numexps[iContador].substring(4,numexps[iContador].lastIndexOf("-"))+" "+Utilidades.Cadenas.getValorMostrarWeb(Utilidades.Valtecnic.getLetraGarantia(numexps[iContador]));
                    referencia_venta_rapida = referencia_venta_rapida.trim();
                    if(!new java.io.File(directory_out.getAbsolutePath()+"/"+referencia_venta_rapida+"_r.pdf").exists())
                    {
                        oBoolean = Objetos.Docgrafica.download_pdf_venta_rapida(numexps[iContador],directory_out,referencia_venta_rapida+"_r",connection);
                        if(!oBoolean) oBoolean = Objetos.Docgrafica.download_pdf_venta_rapida_OLD(numexps[iContador],directory_out,referencia_venta_rapida+"_r",connection);
                        if(oBoolean)
                        {
                            oCitas = new Objetos.Citas();
                            oCitas.numexp = numexps[iContador];
                            oCitas.codusua = "juan";
                            oCitas.fecha = new java.util.Date();
                            oCitas.hora = Utilidades.Cadenas.getTimestamp();
                            oCitas.tipo = Objetos.Citas.TIPO_ENVIO_PDF_AUDITORA;
                            if(oCitas.insert(connection)!=1) System.out.println("NUMEXP: "+numexps[iContador]+" - IMPOSIBLE DE INSERTAR EN CITAS.");
                            else iNumeroRegistrosAfectados++;
                        }//if
                        else System.out.println("NUMEXP: "+numexps[iContador]+" - IMPOSIBLE DE DESCARGAR EL RESUMEN.");
                    }//if          
                }//for
            }//if
        }//try
        catch(Exception exception)
        {
            throw exception;
        }//catch
        finally
        {
            if(oResultSet!=null) oResultSet.close();
        }//finally
        return iNumeroRegistrosAfectados;
    }//download
    
    //******************************************************************************
    //******************************************************************************
    public static void errores(java.sql.Connection connection,java.io.File directory) throws Exception
    {
        String oString = "";
        java.io.File[] archivos = directory.listFiles();
        for(int iContador=0;iContador<archivos.length;iContador++)
        {
            oString += Objetos.Refer.getNumexp(archivos[iContador].getName().substring(0,archivos[iContador].getName().indexOf("_r")),connection);
            oString += "\n";
        }//for
        System.out.println(oString);
    }
    //******************************************************************************
    //******************************************************************************
    public static void main(String[] args)
    {
        java.sql.Connection oConnection = null;
        java.io.File directory = null;
        int iNumeroRegistrosAfectados = 0;
        try
        {
            directory = new java.io.File("C:/8/");
            oConnection = Utilidades.Conexion.getConnectionValtecnic2();
            iNumeroRegistrosAfectados = Clientes.DEUTSCHE.DownloadFiles.download(oConnection,new java.io.File("C:/8/numexps.txt"),directory);
            oConnection.commit();
            //oConnection.rollback();
            
            //errores(oConnection, new java.io.File("C:/9/"));
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
                }//if
            }//try
            catch(Exception exception1){}
            System.out.println("REGISTROS AFECTADOS: "+iNumeroRegistrosAfectados);
        }//finally
    }//main
    //******************************************************************************
    //******************************************************************************
}//DownloadFiles