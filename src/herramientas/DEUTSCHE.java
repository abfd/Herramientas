/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package herramientas;

/**
 *
 * @author Ana Belen de Frutos
 */
public class DEUTSCHE 
{
        //*******************************************************************************************************************************************
    //*******************************************************************************************************************************************
    public static Utilidades.Resultado download_InformeCompleto_finalizadas(String api,String url_path_out,java.sql.Connection connection,java.sql.Connection connection_documentation) throws java.lang.Exception
    {
        Utilidades.Resultado oResultado = new Utilidades.Resultado();
        String sql = null;
        String nombre = null;
        String texto = "";
        int iDownload = 0;      
        //String api = null;
        java.sql.ResultSet rsDatos = null;
        Objetos.Citas oCitas = null;
       java.io.File oFile = null;
        if(connection!=null)
        {
            oFile = new java.io.File(url_path_out);
            if(!oFile.exists()) oFile.mkdir();
            oResultado.setBooleanResultado(Boolean.TRUE);
            oResultado.setFile(oFile);
            try
            {                
                    //******************************************************************************************************************************
                    //******************************************************************************************************************************
                    // - INFORME COMPLETO PDF     
                    //CERTIFICACION_ENERGETICA_ENVIADA = 15
                    //REVA NORMALES sql = "SELECT NUMEXP FROM SOLICITUDES WHERE CODEST=10 AND codcli = 952 AND NUMEXP NOT IN (SELECT NUMEXP FROM CITAS WHERE TIPO="+Objetos.Citas.TIPO_CERTIFICACION_ENERGETICA_ENVIADA+" AND CODUSUA='juan')"; //AND FCHREM<'15-06-2013'
                    //REVA API 5001 
                    //api = "5001";
                    //sql = "SELECT SOLICITUDES.NUMEXP FROM SOLICITUDES JOIN REFER ON (SOLICITUDES.NUMEXP = REFER.NUMEXP) WHERE CODEST=10 AND codcli = 952 AND oficina = 'REVA' AND API = 5001 AND SOLICITUDES.NUMEXP NOT IN (SELECT NUMEXP FROM CITAS WHERE TIPO="+Objetos.Citas.TIPO_CERTIFICACION_ENERGETICA_ENVIADA+" AND CODUSUA='juan')";
                    
                    
                    sql = "SELECT SOLICITUDES.NUMEXP FROM SOLICITUDES JOIN REFER ON (SOLICITUDES.NUMEXP = REFER.NUMEXP) WHERE CODEST=10 AND codcli = 952 AND oficina = 'REVA' AND API ="+api+" AND SOLICITUDES.NUMEXP NOT IN (SELECT NUMEXP FROM CITAS WHERE TIPO="+Objetos.Citas.TIPO_CERTIFICACION_ENERGETICA_ENVIADA+" AND CODUSUA='juan')";
                    
                    //REVA API 5002 NOTAS SIMPLES
                    //api = "5002";
                    //sql = "SELECT SOLICITUDES.NUMEXP,refer.referencia FROM SOLICITUDES JOIN REFER ON (SOLICITUDES.NUMEXP = REFER.NUMEXP) WHERE CODEST=10 AND codcli = 952 AND oficina = 'REVA' AND API = 5002 AND SOLICITUDES.NUMEXP NOT IN (SELECT NUMEXP FROM CITAS WHERE TIPO="+Objetos.Citas.TIPO_CERTIFICACION_ENERGETICA_ENVIADA+" AND CODUSUA='juan')";
                    rsDatos = Utilidades.Conexion.select(sql,connection);
                    while(rsDatos.next())
                    {              
                            if (!api.equals("5002"))
                            {//PDF COMPLETO
                                if (Objetos.Docgrafica.existeInformeCompleto(rsDatos.getString("NUMEXP"), connection_documentation))
                                {
                                    nombre = rsDatos.getString("NUMEXP")+".pdf";
                                    Utilidades.Conexion.descargarFicheroOracle("SELECT CONTENIDO FROM DOCGRAFICA WHERE TIPO = 4325 AND NUMEXP='"+rsDatos.getString("NUMEXP")+"'",connection_documentation,"",url_path_out,nombre);
                                //if(Objetos.Docgrafica.download_pdf_completo(rsDatos.getString("NUMEXP"), url_path_out,nombre,connection_documentation))
                                //{
                                    iDownload++;
                                    oCitas = new Objetos.Citas();
                                    oCitas.numexp = rsDatos.getString("NUMEXP");
                                    oCitas.codusua = "juan";
                                    oCitas.fecha = new java.util.Date();
                                    oCitas.hora = Utilidades.Cadenas.getTimestamp();
                                    oCitas.tipo = Objetos.Citas.TIPO_CERTIFICACION_ENERGETICA_ENVIADA;
                                    if(oCitas.insert(connection)!=1) oResultado.addTextNexTrace("- NUMEXP: "+rsDatos.getString("NUMEXP")+" - IMPOSIBLE DE GUARDAR EN CITAS.");
                                }//if
                                else oResultado.addTextNexTrace("- NUMEXP: "+rsDatos.getString("NUMEXP")+" - IMPOSIBLE DE DESCARGAR EL PDF COMPLETO NO EXISTE.");                        
                            }
                            else
                            {//NOTAS SIMPLES
                                nombre = rsDatos.getString("referencia")+".pdf";
                                Utilidades.Conexion.descargarFicheroOracle("SELECT CONTENIDO FROM DOCGRAFICA WHERE TIPO = 4300 AND NUMEXP='"+rsDatos.getString("NUMEXP")+"'",connection_documentation,"",url_path_out,nombre);
                                iDownload++;
                                oCitas = new Objetos.Citas();
                                oCitas.numexp = rsDatos.getString("NUMEXP");
                                oCitas.codusua = "juan";
                                oCitas.fecha = new java.util.Date();
                                oCitas.hora = Utilidades.Cadenas.getTimestamp();
                                oCitas.tipo = Objetos.Citas.TIPO_CERTIFICACION_ENERGETICA_ENVIADA;
                                if(oCitas.insert(connection)!=1) oResultado.addTextNexTrace("- NUMEXP: "+rsDatos.getString("NUMEXP")+" - IMPOSIBLE DE GUARDAR EN CITAS.");                                
                            }
                                                
                    }//while                                                    
            }//try
            catch(Exception e)
            {
                oResultado.setTextoError(e.toString());
                oResultado.setBooleanResultado(Boolean.FALSE);
                throw e;
            }//catch
            finally
            {
                if(rsDatos!=null) rsDatos.close();
                rsDatos = null;
                sql = null;
                nombre = null;                
                oCitas = null;
            }//fainlly
        }//if
        oResultado.addTextNexTrace("- TOTAL DESCARGADAS: "+iDownload);
        oResultado.setNumeroRegistrosAfectados(iDownload);
        return oResultado;
    }//download_Certificado_energetico_finalizadas
    
public static void main(String[] args)
    {
        //Clientes.uci.Comunes.descargarPlanosUCI();
        java.sql.Connection oConnection = null;
        java.sql.Connection oConnectionDocumentation = null;
        int iNumeroRegistrosAfectados = 0;
        String fecha = new java.text.SimpleDateFormat("ddMMyyyy").format(new java.util.Date());
        try {
            oConnection = Utilidades.Conexion.getConnectionValtecnic2();
            oConnectionDocumentation = Utilidades.Conexion.getConnectionDocgraficaProduccion();
            //REVA API 5000 
            String api = "";
            //api = "5000";
            //api = "5004";
            api = "5005";
            //api = "5006";
            Utilidades.Resultado oResultado = download_InformeCompleto_finalizadas(api,"/data/informes/DEUTSCHEBANK/"+fecha,oConnection,oConnectionDocumentation);
            oConnection.commit();
            //oConnection.rollback();
            System.out.println(oResultado.getTraceText());
        }//try
        catch (Exception e) {
            System.out.println(e);
        }//catch
        finally 
        {
            System.out.println("- REGISTROS AFECTADOS: " + iNumeroRegistrosAfectados);
            try
            {
                if (oConnection != null) {
                    oConnection.rollback();                    
                    oConnection.close();
                    oConnection = null;
                }//if
                if (oConnectionDocumentation != null) {
                    oConnectionDocumentation.rollback();
                    oConnectionDocumentation.close();
                    oConnectionDocumentation = null;
                }//if
            }//try
            catch(Exception e){}
        }//finally
    }//main

}
