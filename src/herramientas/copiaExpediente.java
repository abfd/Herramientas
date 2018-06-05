/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package herramientas;

/**
 *
 * @author Ana Belen de Frutos
 */
public class copiaExpediente 
{
    
    public static void main(String[]args)
    {
        String numexp = "2552 0302 111604";
        String numexp_out = "";
        java.sql.Connection cConexion_in = null;
        java.sql.Connection cConexion_out = null;
        try
        {
            cConexion_in = Utilidades.Conexion.getConnectionValtecnic2();
            cConexion_out = Utilidades.Conexion.getConnectionDesarrolloValtecnic();
            //copia
            Objetos.v2.Solicitudes.copyFullExpediente(numexp,cConexion_in,cConexion_out);
            //vuelco
            //Objetos.v2.Solicitudes.copyFullExpediente(numexp,numexp_out,cConexion_in,cConexion_out);            
            cConexion_out.commit();
        }//try
        catch(Exception e)
        {
            System.out.println(e);
        }//catch
        finally
        {
            try
            {
                if(cConexion_in!=null) 
                {
                    cConexion_in.rollback();
                    cConexion_in.close();
                }//if
                if(cConexion_out!=null) 
                {
                    cConexion_out.rollback();
                    cConexion_out.close();
                }//if
            }//try
            catch(Exception e){}
            cConexion_in = null;
            cConexion_out = null;
            numexp = null;
        }//finally
    }//main

    
}
