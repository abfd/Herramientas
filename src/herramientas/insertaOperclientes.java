/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package herramientas;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author Administrador
 */
public class insertaOperclientes 
{
    
    private  static Connection  conexion = null;        
    private  static String sRutaPropiedades = "/data/informes/generaOperacionesbsch/propiedades/generaOperacionesbsch.properties";
    private  static Utilidades.Propiedades propiedades = null;
    
    
    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException, SQLException, Exception 
    {
        //insertaOperclientes();     
        //insertaCatastro();
        
        //insertaSeguimiento();
        numera();
        System.gc();
    }
    
    public static void numera()
    {
        String sconsulta = "";
        java.sql.ResultSet rs = null;
        Objetos.unidades.Elementos oelementos = new Objetos.unidades.Elementos();        
        int contador = 0;
        
        rs = null;
        try
        {
            File fPropiedades = new File(sRutaPropiedades);                     
            propiedades = new Utilidades.Propiedades(fPropiedades.getAbsolutePath());        
        
            conexion = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"),propiedades.getValueProperty("IfxLogin"),propiedades.getValueProperty("IfxPass"));
            conexion.setAutoCommit(false);
            
            sconsulta = "SELECT id_elmto FROM elementos where numexp = '1553 0308 160008 V2'";
            rs = Utilidades.Conexion.select(sconsulta,conexion);
            while (rs.next())
            {
                contador ++;
                if (oelementos.load(rs.getInt("id_elmto"), conexion))
                {
                    oelementos.id_agrupa = contador;
                    if (oelementos.update(conexion) == 1)
                    {
                        conexion.commit();                        
                    }
                    else conexion.rollback();
                    oelementos.clear();
                }
            }
            rs.close();
            rs = null;
            
            
        }
        catch (Exception e)
        {
            
        }
        finally
        {
            try
            {
                conexion.close();
            }
            catch (Exception e)
            {
                
            }
        }
    }
    
    /*
    public static void insertaOperclientes() throws FileNotFoundException, IOException, ClassNotFoundException, SQLException
    {
        File fPropiedades = new File(sRutaPropiedades);                     
        propiedades = new Utilidades.Propiedades(fPropiedades.getAbsolutePath());        
        
        conexion = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"),propiedades.getValueProperty("IfxLogin"),propiedades.getValueProperty("IfxPass"));
        conexion.setAutoCommit(false);
        String cliente = "255";
        String expediente = "1550 0902 142956";
        String referencia = "0000009383";
        String tipoObjeto = "01";   //04 para promociones 01 para expediente
        Tablas.operclientes operservihabitat = new Tablas.operclientes(cliente);
        String seq = Funciones.Fcomunes.obtenerSecuencia(conexion,cliente);        
        operservihabitat.setIdmensaje(seq);
        operservihabitat.setNumexp(expediente);                              
        operservihabitat.setReferencia(referencia.trim());
        operservihabitat.setTipoperacion("REC");
        operservihabitat.setTipomensaje("STA");
        operservihabitat.setControl(tipoObjeto);
        operservihabitat.setPostventa("1");
        operservihabitat.setFchenvio();
        operservihabitat.setHoraenvio();
        operservihabitat.setEstado("101");  //confirmado sta
        operservihabitat.insertaOperacion(conexion);                                                            
        boolean estadoOK = operservihabitat.EstadoOK();                                                            
                
        if (estadoOK)  conexion.commit();
        else conexion.rollback();
        conexion.close();                 
                              
    }//insertaOperclientes
    
    public static void insertaCatastro() throws FileNotFoundException, IOException, ClassNotFoundException, SQLException, Exception
    {
        File fPropiedades = new File(sRutaPropiedades);                     
        propiedades = new Utilidades.Propiedades(fPropiedades.getAbsolutePath());        
        
        conexion = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"),propiedades.getValueProperty("IfxLogin"),propiedades.getValueProperty("IfxPass"));
        conexion.setAutoCommit(false);        
        int cuantos = 121;   //numero total de registros a insertar
        Objetos.Catastro oCatastro = new Objetos.Catastro();
        oCatastro.numexp = "1550 0827 140957";
        oCatastro.tipoinm = "ERC";
        for (int i=6; i<=cuantos; i++) 
        {
            oCatastro.numero = i;
            if (oCatastro.insert(conexion) == 1) conexion.commit();
            else conexion.rollback();            
        }        
        conexion.close();                 
                              
    }//insertaOperclientes
    */
    public static void insertaSeguimiento() throws FileNotFoundException, IOException, ClassNotFoundException, SQLException, Exception
    {
        File fPropiedades = new File(sRutaPropiedades);                     
        propiedades = new Utilidades.Propiedades(fPropiedades.getAbsolutePath());        
        
        //conexion = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"),propiedades.getValueProperty("IfxLogin"),propiedades.getValueProperty("IfxPass"));
        conexion = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"));
        conexion.setAutoCommit(false);        
        String expediente = "xxx3456";
        
        Objetos.Refer oRefer = new Objetos.Refer();
        oRefer.numexp = expediente;
        try
        {
            if (oRefer.insert(conexion) == 1) System.out.println("INSERTARIA 1");
            else System.out.println("INSERTARIA 0");
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
        }
        /*
        boolean estadoOK = false;
        
        Objetos.v2.Seguimiento oSeguim = new Objetos.v2.Seguimiento();        
        if (oSeguim.load2(expediente, conexion))
        {
            oSeguim.numexp = "548.VLT110297700753459";
            oSeguim.tipoinm = "VUS";
            if (oSeguim.insert(conexion) == 1) conexion.commit();
            else conexion.rollback();
                    
        }
          */              
        conexion.close();                 
                              
    }//insertaOperclientes
    
    /*
    public static void insertaCatastro() throws FileNotFoundException, IOException, ClassNotFoundException, SQLException, Exception
    {
        File fPropiedades = new File(sRutaPropiedades);                     
        propiedades = new Utilidades.Propiedades(fPropiedades.getAbsolutePath());        
        
        conexion = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"),propiedades.getValueProperty("IfxLogin"),propiedades.getValueProperty("IfxPass"));
        conexion.setAutoCommit(false);        
        int cuantos = 121;   //numero total de registros a insertar
        Objetos.Catastro oCatastro = new Objetos.Catastro();
        oCatastro.numexp = "1550 0827 140957";
        oCatastro.tipoinm = "ERC";
        for (int i=6; i<=cuantos; i++) 
        {
            oCatastro.numero = i;
            if (oCatastro.insert(conexion) == 1) conexion.commit();
            else conexion.rollback();            
        }        
        conexion.close();                 
                              
    }//insertaOperclientes
     * 
     */
}
