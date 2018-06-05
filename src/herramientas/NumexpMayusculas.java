/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package herramientas;

/**
 *
 * @author Administrador
 */
public class NumexpMayusculas 
{
    //********************************************************************************************************
    //********************************************************************************************************
    public static int numexpToUpper(java.util.Date fecha,java.sql.Connection conexion)
    {
        int iNumeroRegistrosAfectados = 0;
        int iContadorTotal = 0;
        java.sql.ResultSet rsDatos = null;
        Utilidades.Consultas oConsultas = null;
        try
        {
            rsDatos = Objetos.Solicitudes.getWhereFchenc(fecha, conexion);
            while(rsDatos.next())
            {
                if(rsDatos.getString("NUMEXP")!=null && !rsDatos.getString("NUMEXP").trim().equals(""))
                {
                    oConsultas = new Utilidades.Consultas(Utilidades.Consultas.UPDATE);
                    oConsultas.from("SOLICITUDES");
                    oConsultas.set("NUMEXP",rsDatos.getString("NUMEXP").trim().toUpperCase(),Utilidades.Consultas.VARCHAR);
                    oConsultas.where("NUMEXP",rsDatos.getString("NUMEXP"),Utilidades.Consultas.VARCHAR);
                    iNumeroRegistrosAfectados = Utilidades.Conexion.update(oConsultas.getSql(),conexion);
                    if(iNumeroRegistrosAfectados==1) conexion.commit();
                    else conexion.rollback();
                    oConsultas = null;
                }//if
            }//while
        }//try
        catch(Exception e)
        {
            System.out.println(e);
        }//catch
        finally
        {
            try
            {
                if(rsDatos!=null) rsDatos.close();
            }//try
            catch (Exception e){}
            rsDatos = null;
            oConsultas = null;
        }//finally
        return iContadorTotal;
    }//numexpToUpper
    //********************************************************************************************************
    //********************************************************************************************************
    public static void main(String[] args)
    {
        java.sql.Connection oConexion = null;
        try
        {
            oConexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@orabackup:1521:rvtn");
            java.util.Date oDate = Utilidades.Cadenas.getDate("26-08-2010");
        }//try
        catch(Exception e)
        {
            System.out.println(e);
        }//catch
        finally
        {
            try
            {
                oConexion.close();
                oConexion.rollback();
                oConexion = null;
            }
            catch(Exception e){}
        }
    }//main
    //********************************************************************************************************
    //********************************************************************************************************
    
}
