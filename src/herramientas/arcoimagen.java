/*
 * arcoimagen.java
 *
 * Created on 10 de diciembre de 2007, 11:54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package herramientas;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Administrador
 */
public class arcoimagen {
    
    private String sRutaPropiedades = "/data/informes/arcoimagen/arcoimagen.properties";    
    private String sRutaLog;
    private Utilidades.Propiedades propiedades = null;    
    private Connection conexion = null;    
    private Connection conexion2 = null;    
    private String sConsulta = "";
    
    //atributos de clase
    private String numexpc;
    private String objeto;
    private String numexpv;
    private String numtas;
    private int cuantas;
    private String estado;      
    private String oficina;
    private String agobj;
    private String interno;
    private String[] partes;
    
    
    public static void main (String [] args ) 
    {               
        arcoimagen arcoi = new arcoimagen();
    }//main
    
    public arcoimagen() 
    {
        int total = 0;
        try
        {
            cargaPropiedades();
            conexion = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"),propiedades.getValueProperty("IfxLogin"),propiedades.getValueProperty("IfxPass"));
            conexion.setAutoCommit(false);    
            conexion2 = Utilidades.Conexion.getConnection(propiedades.getValueProperty("IfxConexion"),propiedades.getValueProperty("IfxLogin"),propiedades.getValueProperty("IfxPass"));
            conexion2.setAutoCommit(false);
            
            ResultSet rsTotal = null;
            ResultSet rsPrencargos = null;
            sConsulta = "SELECT * FROM arcoimagen WHERE estado = 0";
            rsTotal = Utilidades.Conexion.select(sConsulta,conexion);   
            
            Tablas.gestionOperuci operuci = new Tablas.gestionOperuci();
            int numUci;            
            while (rsTotal.next()) 
            {
                total ++;
                System.out.println("Total: "+Integer.toString(total));
                inicializaDatos();
                numexpc = rsTotal.getString("numexpc");
                objeto =  rsTotal.getString("objeto");    
                partes = numexpc.split("-");
                oficina = partes[0];
                numexpc = partes[1];
                interno = partes[2];
                partes = null;
                partes = objeto.split("-");
                agobj = partes[0];
                objeto = partes[1];     
                numUci = Integer.parseInt(numexpc);                
                sConsulta = "SELECT numexpv,num_tas FROM PRENCARGOS WHERE oficina = '"+oficina+"' AND numexpc = '"+Integer.toString(numUci)+"' AND ag_obj = '"+agobj+"' AND objeto ='"+objeto+"'";
                rsPrencargos = Utilidades.Conexion.select(sConsulta,conexion);
                while (rsPrencargos.next())
                {
                    cuantas ++;
                    numexpv = rsPrencargos.getString("numexpv");    
                    numtas = rsPrencargos.getString("num_tas");
                }//while prencargos
                System.out.println("Expediente: "+numexpv+" Ocurrencias: "+Integer.toString(cuantas));
                rsPrencargos.close();
                if (cuantas == 1)
                {
                    sConsulta = "UPDATE arcoimagen SET numexpv = '"+numexpv+"', cuantas = "+Integer.toString(cuantas)+" ,estado = 1 WHERE numexpc = '"+oficina+"-"+numexpc+"-"+interno;
                    sConsulta += "' AND objeto = '"+agobj+"-"+objeto+"'";
                }
                else
                {
                    sConsulta = "UPDATE arcoimagen SET cuantas = "+Integer.toString(cuantas)+" ,estado = 2 WHERE numexpc = '"+oficina+"-"+numexpc+"-"+interno;
                    sConsulta += "' AND objeto = '"+agobj+"-"+objeto+"'";
                }
                if (Utilidades.Conexion.update(sConsulta,conexion2) > 0) 
                {
                    if (cuantas == 1)
                    {                        
                        operuci.incializaOperacion();
                        operuci.inicializaEstado();
                        operuci.setOficina(oficina);
                        operuci.setNumexpc(numUci);
                        operuci.setAgObj(agobj);
                        operuci.setObjeto(objeto);
                        operuci.setNumTas(numtas);
                        operuci.setNumexp(numexpv);
                        operuci.setEstado("X");
                        operuci.setTipoperacion("ENV");
                        operuci.setTipomensaje("DAT");
                        operuci.setFchenvio();
                        operuci.setHoraenvio();
                        operuci.insertaEnvioDatos(conexion2);
                        if (operuci.dameEstadoGestionOperuci()) 
                        {                            
                            conexion2.commit();                
                            System.out.println("EXITO EN ESTADO 1");
                        }
                        else
                        {
                            conexion2.rollback();
                            System.out.println("ERROR 5");
                            Utilidades.Log.addText(sRutaLog,"ERROR 5: "+numexpv+" "+operuci.dameErrorGestionOperuci());
                        }
                    } else 
                    {
                        conexion2.commit();
                        System.out.println("EXITO EN ESTADO 2");
                    }
                }
                else
                {
                    conexion2.rollback();    
                    System.out.println("ERROR 6");
                    Utilidades.Log.addText(sRutaLog,"ERROR 6: Imposible actualizar ARCOIMAGEN"+oficina+"-"+numexpc+"-"+interno+"  "+agobj+"-"+objeto);
                }
                
            }//while total
            rsTotal.close();
            conexion.close();
            conexion2.close();
            operuci = null;
        }
        catch (FileNotFoundException fnfe)
        {
            Utilidades.Log.addText(sRutaLog,"ERROR 1: "+fnfe.toString());
            System.out.println("ERROR 1");
        }
        catch (IOException ioe)
        {
            Utilidades.Log.addText(sRutaLog,"ERROR 2: "+ioe.toString());
            System.out.println("ERROR 2");
        }
        catch (ClassNotFoundException cnfe)
        {
            Utilidades.Log.addText(sRutaLog,"ERROR 3: "+cnfe.toString());
            System.out.println("ERROR 3");
        }
        catch (SQLException sqle)
        {
            Utilidades.Log.addText(sRutaLog,"ERROR 4: "+sqle.toString());
            System.out.println("ERROR 4");
        }        
        finally
            {
                try
                {
                    if (conexion != null && !conexion.isClosed()) conexion.close();
                    if (conexion2 != null && !conexion2.isClosed()) conexion2.close();
                }
                catch (SQLException e)
                {
                    Utilidades.Log.addText(sRutaLog,"Imposible cerrar conexión: "+e.toString().trim());                    
                }
                conexion = null;
                conexion2 = null;
                System.gc();
            }
        
    }//cargaArcoimagen
    
    private void cargaPropiedades() throws FileNotFoundException, IOException
    {                   
       File fPropiedades = new File(sRutaPropiedades);
       if (fPropiedades.exists())
       {
             propiedades = new Utilidades.Propiedades(fPropiedades.getAbsolutePath());
             sRutaLog = propiedades.getValueProperty("RutaLog");                            
        }  
        else
        {
            throw new FileNotFoundException ("Imposible localizar fichero de propiedades en la ruta:  " + sRutaPropiedades.trim());                          
        }
    }//cargaPropiedades
    
    private void inicializaDatos()
    {
        //atributos de clase
        numexpc = "";
        objeto = "";
        numexpv = "";
        cuantas = 0;
        estado = "";
        oficina = "";
        agobj = "";
        interno = "";
        partes = null;
    }//inicializaDatos
    
}
