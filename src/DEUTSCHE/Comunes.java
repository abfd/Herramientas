    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DEUTSCHE;


/**
 *
 * @author Administrador
 */
public class Comunes 
{
    
    //*******************************************************************************************************************************************
    //*******************************************************************************************************************************************
    public static Utilidades.Resultado download_InformeCompleto_finalizadas(String url_path_out,java.sql.Connection connection,java.sql.Connection connection_documentation) throws java.lang.Exception
    {
        Utilidades.Resultado oResultado = new Utilidades.Resultado();
        String sql = null;
        String nombre = null;
        String texto = "";
        int iDownload = 0;        
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
                sql = "SELECT NUMEXP FROM SOLICITUDES WHERE CODEST=10 AND codcli = 952 AND NUMEXP NOT IN (SELECT NUMEXP FROM CITAS WHERE TIPO="+Objetos.Citas.TIPO_CERTIFICACION_ENERGETICA_ENVIADA+" AND CODUSUA='juan')"; //AND FCHREM<'15-06-2013'
                rsDatos = Utilidades.Conexion.select(sql,connection);
                while(rsDatos.next())
                {                                                        
                    nombre = rsDatos.getString("NUMEXP");
                    if(Objetos.Docgrafica.download_Certificado_energetico(rsDatos.getString("NUMEXP"), url_path_out,nombre,connection_documentation))
                    {
                        iDownload++;
                        oCitas = new Objetos.Citas();
                        oCitas.numexp = rsDatos.getString("NUMEXP");
                        oCitas.codusua = "juan";
                        oCitas.fecha = new java.util.Date();
                        oCitas.hora = Utilidades.Cadenas.getTimestamp();
                        oCitas.tipo = Objetos.Citas.TIPO_CERTIFICACION_ENERGETICA_ENVIADA;
                        if(oCitas.insert(connection)!=1) oResultado.addTextNexTrace("- NUMEXP: "+rsDatos.getString("NUMEXP")+" - IMPOSIBLE DE GUARDAR EN CITAS.");
                    }//if
                    else oResultado.addTextNexTrace("- NUMEXP: "+rsDatos.getString("NUMEXP")+" - IMPOSIBLE DE DESCARGAR EL PDF DE LA CERTIFICACIÓN ENERGÉTICA.");                                        
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
    
    

    //*******************************************************************************************************************************************
    //*******************************************************************************************************************************************
    public static void main(String[] args)
    {
        //Clientes.uci.Comunes.descargarPlanosUCI();
        java.sql.Connection oConnection = null;
        java.sql.Connection oConnectionDocumentation = null;
        int iNumeroRegistrosAfectados = 0;
        try {
            oConnection = Utilidades.Conexion.getConnectionValtecnic2();
            oConnectionDocumentation = Utilidades.Conexion.getConnectionDocgraficaProduccion();
            Utilidades.Resultado oResultado = download_InformeCompleto_finalizadas("/data/informes/DEUTSCHEBANK/",oConnection,oConnectionDocumentation);
            oConnection.commit();
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
    
    //*******************************************************************************************************************************************
    //*******************************************************************************************************************************************
}//Comunes