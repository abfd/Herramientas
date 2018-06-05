/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package herramientas;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Ana Belen de Frutos
 */
public class pruebas {
    
    
    public static void main(String[] args)
    {
           //prueba_select_colleccion();
        //completTextWithRightCharacterFixedLength("7021",'0',3);
        //String anio_construccion = Utilidades.Cadenas.completTextWithLeftCharacterFixedLength("2015-02-25",'0',4);//new SimpleDateFormat("yyyy-MM-dd").format("2015-02-25"); // en 2015
        //System.out.println(anio_construccion);
        //elOr();
        //redondeo();
        prueba_recupera_datos_catastro();
        //prueba();
        //comparaObjetos();
        //dameFechaHora();
    }
    
    public static void prueba_select_colleccion()
    {
        java.sql.Connection conexion = null;
        String sconsulta = "";
        java.sql.ResultSet rs = null;
        
        try
        {
            Collection<Integer> elementos = new HashSet<Integer>();
            conexion = Utilidades.Conexion.getConnectionValtecnic2();
            sconsulta = "SELECT id_elmto FROM elementos WHERE numexp = '1233 1224 113009'";
            rs = Utilidades.Conexion.select(sconsulta, conexion);
            while (rs.next())
            {
                elementos.add(rs.getInt("id_elmto"));
            }
            rs.close();
            rs = null;
            if (elementos.size() > 0)
            {
                for (Integer i:elementos)
                {
                    System.out.println(i);
                }
            }
            sconsulta = "SELECT sum(supadoptada) suma FROM superficies WHERE numexp = '1233 1224 113009' AND idnumero IN "+elementos;
            sconsulta = sconsulta.replace("[", "(");
            sconsulta = sconsulta.replace("]", ")");
            rs = Utilidades.Conexion.select(sconsulta, conexion);
            if (rs.next()) System.out.println(rs.getString("suma"));
            else System.out.println("No se han recuperado datos");
        }
        catch (Exception e)
        {
            System.out.println("Excepcion: "+e.toString());
        }
        
    }
    
    public static String completTextWithRightCharacterFixedLength (String valor, char caracterCompletar,int longitud)
    {
        String sSalida = Utilidades.Cadenas.getValorMostrarWeb(valor);
        int iLongitudOriginal = sSalida.length();
        for(int i=0;i<longitud-iLongitudOriginal;i++)
        {
            sSalida = sSalida + caracterCompletar;
        }//for
        if(sSalida!=null && sSalida.length()>longitud) sSalida = sSalida.substring(0,longitud);
        return sSalida;
    }//completTextWithRightCharacterFixedLength
    
    public static void elOr()
    {
        String valor = null;
        if (valor == null || valor.trim().equals("")) System.out.println("entra en el if");
        else System.out.println("no entra en el if");
    }
    
    public static void redondeo()
    {
        //System.out.println("4567.49 = "+Math.round(4567.49));  
        //String nameFile = ",ORINOCO_APPRAISALS_DBEXCHANGE,dummy_1.xml,U,20160419A00014397759 - copia.bin";
        String nameFile = "ORINOCO_APPRAISALS_DBEXCHANGE";
        String [] dfichero = nameFile.split(",");
        
        
    }
    
    public static void prueba_recupera_datos_catastro()
    {
        recuperadatoscatastro.RecuperaDatosCatastro nwdc = new recuperadatoscatastro.RecuperaDatosCatastro();
        
        nwdc.beanRCByLocalizacion.setFCatastral("08148A003000030000JE");
        
        //nwdc.beanRCByLocalizacion.setProvincia("MADRID");
        //nwdc.beanRCByLocalizacion.setMunicipio("ALCOBENDAS");
        //nwdc.beanRCByLocalizacion.setTipovia("CL");
        //nwdc.beanRCByLocalizacion.setCalle("CONCILIO");
        //nwdc.beanRCByLocalizacion.setNumero("11");
        //nwdc.beanRCByLocalizacion.setPlanta("5");
        //nwdc.beanRCByLocalizacion.setPuerta("C");
        
        
        /*
        nwdc.beanRCByLocalizacion.setProvincia("LLEIDA");
        nwdc.beanRCByLocalizacion.setMunicipio("LLEIDA");
        nwdc.beanRCByLocalizacion.setTipovia("CL");
        nwdc.beanRCByLocalizacion.setCalle("DE MARIA AURELIA CAPMANY");
        nwdc.beanRCByLocalizacion.setNumero("2");
        nwdc.beanRCByLocalizacion.setPlanta("5");
        nwdc.beanRCByLocalizacion.setPuerta("3");
        
        nwdc.beanRCByLocalizacion.setProvincia("BARCELONA");
        nwdc.beanRCByLocalizacion.setMunicipio("L'HOSPITALET DE LLOBREGAT");
        nwdc.beanRCByLocalizacion.setTipovia("CL");
        nwdc.beanRCByLocalizacion.setCalle("MOSSEN JAUME BUSQUETS DE");
        nwdc.beanRCByLocalizacion.setNumero("50");
        nwdc.beanRCByLocalizacion.setPlanta("1");
        nwdc.beanRCByLocalizacion.setPuerta("2");
*/
        //nwdc.recupera_refCatastral_by_localizacion();
        nwdc.recupera_localizacion_by_refCatastral();

        if (nwdc.estadoOK)
        {
            if (nwdc.Informacion_Catastral_Inmuebles.size() > 0)
            {
                java.util.Iterator iterador = nwdc.Informacion_Catastral_Inmuebles.listIterator();
                while( iterador.hasNext() ) 
                {
                    nwdc.beanRCByLocalizacion = (Beans.BeanRCByLocalizacion) iterador.next();
                    System.out.println("Referencia Catastral: "+nwdc.beanRCByLocalizacion.getReferenciaCatastral());
                    System.out.println("Primer nº de policia: "+nwdc.beanRCByLocalizacion.getPnp());                    
                    System.out.println("bloque: "+nwdc.beanRCByLocalizacion.getBq());
                    System.out.println("escalera: "+nwdc.beanRCByLocalizacion.getEs());
                    System.out.println("planta: "+nwdc.beanRCByLocalizacion.getPt());
                    System.out.println("puerta: "+nwdc.beanRCByLocalizacion.getPu());
                }
            }
            else System.out.println("NO SE HAN ENCONTRADO INMUEBLES.");
        }
        else
        {
            System.out.println("ERROR: "+nwdc.descError);
        }
    }
    public static void prueba()
    {
        String observaciones = "para ti por ser tu";
        int longitud_max = 3;        
        int cuantos = (int) Math.ceil((double)observaciones.length()/(double)longitud_max);
        String observacion[] = new String[cuantos];
        int inicio = 0;
        int fin = inicio+longitud_max;
        for (int i=0;i<cuantos;i++)
        {
            if (fin > observaciones.length()) fin = observaciones.length();
            observacion[i] = observaciones.substring(inicio, fin);
            inicio = fin;
            fin = inicio+longitud_max;
        }
        for (String texto:observacion)
        {
            System.out.println(texto);
        }
    }
    
    public static void comparaObjetos()
    {
        Objetos.Dircatastro oDircatastro1 = new Objetos.Dircatastro();
        Objetos.Dircatastro oDircatastro2 = new Objetos.Dircatastro();
        java.sql.Connection conexion = null;
        
        try
        {
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:desarrollo/des0329@oraserver04:1521:rvtn4", "desarrollo", "des0329");
            oDircatastro1.load(new Long(14), conexion);
            oDircatastro2.load(new Long(14), conexion);
            if (oDircatastro1.equals(oDircatastro2)) System.out.println("Son iguales");
            else System.out.println("NO Son iguales");
            
            conexion.close();
        }
        catch (Exception e){
            
        }
        finally
        {
            
        }
    }
    
    public static void dameFechaHora()
    {
        String fechaHora = new java.text.SimpleDateFormat("_yyyyMMdd_HHmmss").format(new java.util.Date());     
        System.out.println(fechaHora);
    }
    
   



}
