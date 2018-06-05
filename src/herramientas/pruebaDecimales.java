/*
 * pruebaDecimales.java
 *
 * Created on 8 de mayo de 2007, 12:09
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package herramientas;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;

/**
 *
 * @author Administrador
 */
public class pruebaDecimales {
    
    
    private float campo1 = 0;
    private  Connection conexion = null;
    private  String sRutaPropiedades = "/data/informes/cargainecenso/propiedades/inecenso.properties";
    private Utilidades.Propiedades propiedades = null;   
    private static java.util.Hashtable tabla = new java.util.Hashtable();
    public static void main(String [] args ) 
    {
        //pruebaDecimales pd = new pruebaDecimales();                
        tabla.put(1, 1);
        tabla.put(2, 3);
        tabla.put(5, 3);
        tabla.put(2, 2);
        
        System.out.println("tamano: "+tabla.size());
        System.out.println(tabla.get(1));
        System.out.println(tabla.get(2));
        System.out.println(tabla.get(8));
        
        
        
    }//main
    
    public  pruebaDecimales()
    {
        try
        {
            //para pasar un string a un date.
            java.io.File fichero = new java.io.File(sRutaPropiedades);                        
            propiedades = new Utilidades.Propiedades(fichero.getAbsolutePath());    
            conexion = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"),propiedades.getValueProperty("IfxLogin"),propiedades.getValueProperty("IfxPass"));
            conexion.setAutoCommit(false);        
        
            java.util.Calendar gc = java.util.GregorianCalendar.getInstance();                 
            gc.set(2007,3,1); //año, mes, dia  los meses van de 0 .. 11
            java.util.Date fdesde = gc.getTime();
            gc.set(2007,3,30); //año, mes, dia
            java.util.Date fhasta = gc.getTime();
            int dias = Funciones.Fcomunes.festivos(conexion,fhasta,fdesde,3,"28");
            conexion.close();
            System.out.println(Integer.toString(dias));
        }
        catch (Exception e)
        {
            
        }
    }
    /*
    public  pruebaDecimales()
    {
        try
        {
            java.io.File fichero = new java.io.File(sRutaPropiedades);                        
            propiedades = new Utilidades.Propiedades(fichero.getAbsolutePath());    
            conexion = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"),propiedades.getValueProperty("IfxLogin"),propiedades.getValueProperty("IfxPass"));
            conexion.setAutoCommit(false);
            Tablas.facturas factura = new Tablas.facturas();
            if(factura.hayFactura(conexion,"229.46-6284-07"))
            {
                System.out.println(factura.getFchfact());
            }
            
            conexion.close();
        }
        catch (Exception e)
        {
            
        }
    }//pruebaFecha
    
    
    public pruebaDecimales() 
    {//el valor obtenido de esta prueba es 65.70
     // redondeo si >= 5 en el tercer decimal  
        try
        {
            //String valor = "65.69573";  //guarda 65.70
            //String valor = "65.69473";  //guarda 65.69
            String valor = "65.69973";  //guarda 65.70
            campo1 = Float.parseFloat(valor);
            java.io.File fichero = new java.io.File(sRutaPropiedades);                        
            propiedades = new Utilidades.Propiedades(fichero.getAbsolutePath());    
            conexion = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"),propiedades.getValueProperty("IfxLogin"),propiedades.getValueProperty("IfxPass"));
            conexion.setAutoCommit(false);
            
            String sConsulta = "INSERT INTO prueba VALUES ("+ campo1 + ")";
            if (Utilidades.Conexion.insert(sConsulta,conexion) == 0) conexion.rollback();
            else conexion.commit();
            
            sConsulta = "SELECT * from prueba";
            ResultSet rs = Utilidades.Conexion.select(sConsulta,conexion);
            if (rs.next()) campo1 = rs.getFloat("campo1");
            System.out.print(campo1);
            conexion.close();
            
        }
        catch (ClassNotFoundException cnfe)
        {
            
        }
        catch (SQLException sqle)
        {
            
        }
        catch (FileNotFoundException fnfe)
        {
            
        }  
        catch (IOException ioe)
        {
            
        }
    }
     */
    /*
    public pruebaDecimales()
    {//2, indica la escala y el redonde se genera basandose en el tercer decimal.
        //double d = 23.9049403594 ;
        double d = 23.0;
        d = new java.math.BigDecimal (d).setScale (2,java.math.BigDecimal.ROUND_HALF_EVEN).doubleValue();

    }
     */
}
