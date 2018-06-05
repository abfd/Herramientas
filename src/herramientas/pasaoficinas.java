/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package herramientas;

import Utilidades.Consultas;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;


/**
 *
 * @author Administrador
 */
public class pasaoficinas 
{
    
    
    private  Connection conexion = null;    
    private  Connection conexion2 = null;    
    private  String sRutaPropiedades = "/data/informes/insertapis7cpp/propiedades/insertapis7cpp.properties";
    private Utilidades.Propiedades propiedades = null;
    
    public static void main(String [] args ) 
    {        
        pasaoficinas o = new pasaoficinas();        
        o = null;
        System.gc();
        
    }//main

    
    public pasaoficinas() 
    {
        try
        {
        File fPropiedades = new File(sRutaPropiedades);                     
        propiedades = new Utilidades.Propiedades(fPropiedades.getAbsolutePath());
        
        String textoConsulta = "";
        String numexp = "";
        String sConsulta = "";
        String fecha = "";
        conexion = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"),propiedades.getValueProperty("IfxLogin"),propiedades.getValueProperty("IfxPass"));
        conexion.setAutoCommit(false);
        conexion2 = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"),propiedades.getValueProperty("IfxLogin"),propiedades.getValueProperty("IfxPass"));
        conexion2.setAutoCommit(false);
        
        Consultas consulta;        
        
        
        
        sConsulta = "select * from clidir where codcli = 129";
        ResultSet rsoficinas = Utilidades.Conexion.select(sConsulta,conexion);
        while (rsoficinas.next())            
        { 
            try
            {  
                consulta = new Utilidades.Consultas(Utilidades.Consultas.INSERT);
                consulta.from("clidir"); 
                consulta.insert("codcli","329",Utilidades.Consultas.INT);
                consulta.insert("denomina",rsoficinas.getString("denomina"),Utilidades.Consultas.VARCHAR);
                consulta.insert("oficina",rsoficinas.getString("oficina"),Utilidades.Consultas.VARCHAR);
                consulta.insert("direcc",rsoficinas.getString("direcc"),Utilidades.Consultas.VARCHAR);
                consulta.insert("codpos",rsoficinas.getString("codpos"),Utilidades.Consultas.INT);
                consulta.insert("provin",rsoficinas.getString("provin"),Utilidades.Consultas.VARCHAR);
                consulta.insert("pobla",rsoficinas.getString("pobla"),Utilidades.Consultas.VARCHAR);
                consulta.insert("percon",rsoficinas.getString("percon"),Utilidades.Consultas.VARCHAR);
                consulta.insert("agen",rsoficinas.getString("agen"),Utilidades.Consultas.VARCHAR);            
                consulta.insert("telefo",rsoficinas.getString("telefo"),Utilidades.Consultas.VARCHAR);            
                consulta.insert("fax",rsoficinas.getString("fax"),Utilidades.Consultas.VARCHAR);            
                if (rsoficinas.getString("fchact") != null) 
                {            
                   fecha = rsoficinas.getString("fchact").substring(8, 10)+"-"+rsoficinas.getString("fchact").substring(5, 7)+"-"+rsoficinas.getString("fchact").substring(0, 4);
                    consulta.insert("fchact",fecha,Utilidades.Consultas.DATE);            
                }
                textoConsulta = consulta.getSql();                        
                if (Utilidades.Conexion.insert(textoConsulta,conexion2) == 0)
                {   
                    conexion2.rollback();
                    System.out.println("ERROR - Imposible insertar oficina: "+rsoficinas.getString("oficina"));
                }   
                else 
                {
                    conexion2.commit();
                    System.out.println("INSERTADA oficina: "+rsoficinas.getString("oficina"));
                }
                consulta = null;
                System.gc();
            }//try
            catch(SQLException sqle)
            {            
                System.out.println("EXCEPCIÓN: "+sqle.toString());              
            }                        
        }//while
        rsoficinas.close();
        rsoficinas = null;
        conexion.close();
        conexion2.close();
        }//try
        catch (FileNotFoundException fnfe)
        {
            System.out.println("EXCEPCIÓN: "+fnfe.toString());              
        }
        catch (IOException ioe)
        {
            System.out.println("EXCEPCIÓN: "+ioe.toString());              
        }
        catch (ClassNotFoundException cnfe)
        {
            System.out.println("EXCEPCIÓN: "+cnfe.toString());              
        }
        catch (SQLException sqle)
        {
            System.out.println("EXCEPCIÓN: "+sqle.toString());              
        }
        finally
        {
             try
            {
                if (conexion != null && !conexion.isClosed()) conexion.close();
                if (conexion2 != null && !conexion2.isClosed()) conexion2.close();
            }
            catch (SQLException sqlException)
            {                
            }
            
        }
        
        
    }
}
