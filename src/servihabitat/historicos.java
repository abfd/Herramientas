/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servihabitat;

import Utilidades.Fechas;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 *
 * @author abfd
 */
public class historicos 
{
    //1.- Un fichero de tasación 
    //2.- Un fichero de condicionantes/advertencias
    
    private HSSFWorkbook historicos = null;
    private HSSFSheet hojaTasacion = null;
    private HSSFSheet hojaCircuns   = null;
    private HSSFRow fila = null;
    private HSSFCell celda = null;
    
    //conexion
    private Connection conexion = null; 
    private Connection conexion2 = null; 
    private Connection conexion3 = null;
    
    //columnas en el excel
    private String numexp = null;
    private String numero_solicitud = null;
    private Integer idElemento = null;
    private String sociedad = null;
    private String version = "1";
    private String unidad_registral = null;
    private String nom_tasador = null;
    private String dni_tasador = null;
    private String metodo_valoracion = null;
    private String visita_interior = null;
    private String fecha_visita = null;
    private String gastos_comerciales = null;
    private String finalidad = null;
    private String motivo = "999";
    private String tipo_circuns = null;
    private String identificacion  = "99";
    
    public List <String> condicionatesList = new java.util.ArrayList();
    public List <String> advertenciasList = new java.util.ArrayList();
        
    private Objetos.unidades.Vtotalestas oVtotalestas = new Objetos.unidades.Vtotalestas();
    
    public static void main(String[] args) 
    {
        historicos nwExcel = new historicos();
        nwExcel = null;
        System.gc();
    }
    
    public historicos ()
    {                           
        String sconsultaTAS = null;
        String sconsultaUR = null;        
        java.sql.ResultSet rsTAS = null;
        java.sql.ResultSet rsUR = null;        
        int contadorInsertadosHojaTasacion = 0;        
        int contadorInsertadosHojaCircuns = 0;        
        
        try
        {
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@oraserver02:1521:rvtnprod");                                    
            conexion.setAutoCommit(false);                         
            historicos = new HSSFWorkbook();
            hojaTasacion =   historicos.createSheet("Tasacion");
            hojaCircuns = historicos.createSheet("Circuns");
            //1.- tasaciones
            sconsultaTAS = "select s.numexp,s.codage,s.fchvis,r.referencia,d.finalidad,r.api from solicitudes s join operclientes o on (s.numexp = o.numexp) join refer r on (s.numexp = r.numexp) join documenta d on (s.numexp = d.numexp)  where  s.codcli IN (255,355,755,855,955) and o.TIPOPERACION = 'REC' and o.TIPOMENSAJE = 'STA' and s.fchenc between '01012016' and '30092016' and s.tipoinm != 'XXC' and s.codest = 10 and s.codage is not null order by r.referencia";
            rsTAS = Utilidades.Conexion.select(sconsultaTAS, conexion);
            while (rsTAS.next())
            {
                actualizaValores();
                oVtotalestas.clear();                
                numexp = rsTAS.getString("numexp");
                numero_solicitud = rsTAS.getString("referencia");
                oVtotalestas.load(numexp,conexion);
                if (rsTAS.getString("api") != null) sociedad = Utilidades.Cadenas.completTextWithLeftCharacter(rsTAS.getString("api"), '0', 4);
                else sociedad = "";
                nom_tasador = Objetos.Personal.getNombreWhereCodage(rsTAS.getInt("codage"), conexion);
                dni_tasador = Objetos.Personal.getNif(rsTAS.getInt("codage"), conexion);                
                if (dni_tasador != null) dni_tasador = dni_tasador.replace("-", "");
                fecha_visita = new java.text.SimpleDateFormat("dd.MM.yyyy").format(rsTAS.getDate("fchvis"));
                sconsultaUR = "select * from datosreg where numexp = '"+numexp+"' and numseguim is not null order by numseguim";
                conexion2 = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@oraserver02:1521:rvtnprod");
                conexion2.setAutoCommit(false); 
                rsUR = Utilidades.Conexion.select(sconsultaUR, conexion2);
                while (rsUR.next())
                {
                    contadorInsertadosHojaTasacion ++;
                    actualizaValoresUR();
                    idElemento = getIdElemento(rsUR.getString("numero"),rsUR.getString("tipoinm"));
                    unidad_registral = rsUR.getString("numseguim");
                    metodo_valoracion = MetValoracionElemento(idElemento,rsUR.getString("tipoinm"));
                    visita_interior = getVisitaInterior(idElemento);
                    gastos_comerciales = "0";
                    fila = hojaTasacion.createRow(contadorInsertadosHojaTasacion);
                    celda = fila.createCell((short) 0); 
                    celda.setCellValue(numero_solicitud);
                    celda = fila.createCell((short) 1); 
                    celda.setCellValue(sociedad);
                    celda = fila.createCell((short) 2); 
                    celda.setCellValue(version);
                    celda = fila.createCell((short) 3); 
                    celda.setCellValue(unidad_registral);
                    celda = fila.createCell((short) 4); 
                    celda.setCellValue(nom_tasador);
                    celda = fila.createCell((short) 5); 
                    celda.setCellValue(dni_tasador);
                    celda = fila.createCell((short) 6); 
                    celda.setCellValue(metodo_valoracion);
                    celda = fila.createCell((short) 7); 
                    celda.setCellValue(visita_interior);
                    celda = fila.createCell((short) 8); 
                    celda.setCellValue(fecha_visita);
                    celda = fila.createCell((short) 9); 
                    celda.setCellValue(gastos_comerciales);
                }
                rsUR.close();
                rsUR = null;
                conexion2.close();
                conexion2 = null;
                //condicionantes y advertencias y finalidad
                //loadCondicionantesActivos();
                //loadAdvertenciasActivas();
                conexion3 = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@oraserver02:1521:rvtnprod");            
                conexion3.setAutoCommit(false); 
                finalidad = getFinalidad(rsTAS.getString("finalidad"));
                String sConsulta = "SELECT * FROM condicion1 WHERE codusua is null AND numexp = '" +numexp+"'";
                java.sql.ResultSet rs = Utilidades.Conexion.select(sConsulta, conexion3);
                //for (String s:condicionatesList)
                while (rs.next())
                {
                    contadorInsertadosHojaCircuns ++;
                    fila = hojaCircuns.createRow(contadorInsertadosHojaCircuns);
                    celda = fila.createCell((short) 0); 
                    celda.setCellValue(numero_solicitud);
                    celda = fila.createCell((short) 1); 
                    celda.setCellValue(sociedad);
                    celda = fila.createCell((short) 2); 
                    celda.setCellValue(version);
                    celda = fila.createCell((short) 3); 
                    celda.setCellValue(unidad_registral);
                    celda = fila.createCell((short) 4); 
                    celda.setCellValue(finalidad);
                    celda = fila.createCell((short) 5); 
                    if (finalidad != null && finalidad.equals("01")) celda.setCellValue(getMotivo(rs.getString("codcon")));
                    else celda.setCellValue("999");
                    celda = fila.createCell((short) 6); 
                    celda.setCellValue("C");
                    celda = fila.createCell((short) 7); 
                    celda.setCellValue(identificacion);
                    celda = fila.createCell((short) 8); 
                    celda.setCellValue(rs.getString("texco1"));
                }
                rs.close();
                rs = null;
                //for (String s:advertenciasList)
                sConsulta = "SELECT * FROM adverten1 WHERE codusua is null AND numexp = '" +numexp+"'";
                rs = Utilidades.Conexion.select(sConsulta, conexion3);
                while (rs.next())              
                {
                    contadorInsertadosHojaCircuns ++;
                    fila = hojaCircuns.createRow(contadorInsertadosHojaCircuns);
                    celda = fila.createCell((short) 0); 
                    celda.setCellValue(numero_solicitud);
                    celda = fila.createCell((short) 1); 
                    celda.setCellValue(sociedad);
                    celda = fila.createCell((short) 2); 
                    celda.setCellValue(version);
                    celda = fila.createCell((short) 3); 
                    celda.setCellValue(unidad_registral);
                    celda = fila.createCell((short) 4); 
                    celda.setCellValue(finalidad);
                    celda = fila.createCell((short) 5); 
                    if (finalidad != null && finalidad.equals("01")) celda.setCellValue(getMotivo(rs.getString("codadv")));
                    else celda.setCellValue("999");                    
                    celda = fila.createCell((short) 6); 
                    celda.setCellValue("A");
                    celda = fila.createCell((short) 7); 
                    celda.setCellValue(identificacion);
                    celda = fila.createCell((short) 8); 
                    celda.setCellValue(rs.getString("texad6"));
                }
                conexion3.close();
                conexion3 = null;                
            }
            rsTAS.close();
            rsTAS = null;
            
            //escribimos el libro
            FileOutputStream elFichero = new FileOutputStream("/data/informes/tmp/historicos.xls");
            historicos.write(elFichero);                        
            historicos = null;
            elFichero.close();
            elFichero = null;
            
            
            conexion.close();
            conexion = null;            
        }
        catch (Exception e)
        {
            System.out.println("Excepcion general: "+e.toString());
        }
        finally
        {
            try
            {
               if (conexion != null && !conexion.isClosed())
               {                
                    conexion.rollback();
                    conexion.close();
               }
            }
            catch (Exception e)
            {
                 
            }
            try
            {
               if (conexion2 != null && !conexion2.isClosed())
               {                
                    conexion2.rollback();
                    conexion2.close();
               }
            }
            catch (Exception e)
            {
                 
            }
            try
            {
               if (conexion3 != null && !conexion3.isClosed())
               {                
                    conexion3.rollback();
                    conexion3.close();
               }
            }
            catch (Exception e)
            {
                 
            }
        }
    }
    
    private void actualizaValores()
    {
        numexp = null;
        numero_solicitud = null;  
        idElemento = null;
        unidad_registral = null;
        nom_tasador = null;
        dni_tasador = null;
        metodo_valoracion = null;
        visita_interior = null;
        fecha_visita = null;
        gastos_comerciales = null;
        finalidad = null;     
        tipo_circuns = null;             
        fila = null;
        celda = null;
        condicionatesList.clear();
        advertenciasList.clear();
    }
    
     private void actualizaValoresUR()
    {        
        idElemento = null;
        motivo = null;
        unidad_registral = null;        
        metodo_valoracion = null;
        visita_interior = null;        
        gastos_comerciales = null;                    
    }
    
    private Integer getIdElemento(String numero,String tipoinm)
    {//numero en datos registrales
        String consulta = "";
        java.sql.ResultSet rs = null;
        Integer idElemento = 0;
            
        if (tipoinm == null) tipoinm = "";
        if (numero == null) numero = "0";
        try
        {//sConsulta = "SELECT r.*,s.refunidad FROM relafincas r JOIN suelo s ON (r.idslovalor = s.iden_suelo and r.numexp = s.numexp) WHERE r.numexp = '"+numexp+"' ORDER BY r.idslovalor ASC";
            if (esSuelo(tipoinm))
            {
                consulta = "SELECT idslovalor id_elmto FROM relafincas WHERE numexp = '"+numexp+"' and num_dr = "+numero;
            }
            else
            {
                if (esEdificio(tipoinm))
                {
                    consulta = "select distinct(v.id_principal) id_elmto from vtotagrupa v join elementos e on (v.numexp = e.numexp and v.id_agrupa = e.id_agrupa) where e.numexp = '"+numexp+"' and e.num_dr = "+numero;
                }
                else
                {
                    consulta = "select id_elmto from elementos e where e.numexp = '"+numexp+"' and e.num_dr = "+numero+" and e.tipoinm = '"+tipoinm+"' order by id_elmto";
                }
            }
            rs = Utilidades.Conexion.select(consulta,conexion);     
            if (rs.next()) idElemento = rs.getInt("id_elmto");
            else idElemento = null;
            rs.close();
            rs = null;
        }
        catch (Exception e)
        {
            idElemento = null;
        }
        finally
        {
            
            try
            {
                if (rs != null) rs.close();
            }
            catch (Exception e)
            {
                
            }
            return idElemento;
        }              
        
    }//getIdElemento
    
    private boolean esSuelo(String tipoinm)
    {        
        if (tipoinm.equals("TRR") || tipoinm.equals("SOL") || tipoinm.equals("FCR")) return true;
        else return false;
    }//esSuelo
    
    public Boolean esEdificio(String tipoinm)
    {
        Boolean esEdificio = false;
        java.sql.ResultSet rs = null;
        String consulta = "";                
        
        try
        {        
                consulta = "select * from tipos_aux where tipotasac like 'EDIF%' and idioma = 'e' and tipoinm = '"+tipoinm+"'";
                rs = Utilidades.Conexion.select(consulta, conexion);
                if (rs.next()) esEdificio = true;                
                rs.close();
                rs = null;                        
        }
        catch (Exception e)
        {
            esEdificio = false;
        }
        finally
        {
            try
            {
                if (rs != null) rs.close();
            }
            catch (Exception e)
            {
                
            }
            return esEdificio;
        }        
    }//esEdificio
    
    private String MetValoracionElemento(Integer idElemento,String tipoinm)
    {//
        /*
        01	Valor de reemplazamiento bruto
02	Valor de reemplazamiento neto
03	Valor residual dinámico
04	Valor residual estático
05	Valor de mercado por comparación
06	Valor de mercado por comparación ajustado
07	Valor por actualización de rentas de inmuebles ligados a una explotación económica
08	Valor por actualización de rentas de inmueble con mercado de alquileres
09	Valor por actualización de rentas de otros inmuebles en arrendamiento
10	Valor máximo legal
11	Otro criterio
12	Métodos automáticos de valoración
13	Procedimiento Muestral

        */
        
        String sconsulta = null;
        java.sql.ResultSet rs = null;
        Integer metodo = null;
        
        try
        {
            if (esSuelo(tipoinm)) 
            {
                sconsulta = "SELECT metodo FROM suelo WHERE numexp ='"+numexp+"' and iden_suelo = "+idElemento;
            }
            else
            {
                sconsulta = "SELECT metodo FROM vtotales WHERE numexp ='"+numexp+"' and idnumero = "+idElemento;
            }
            rs = Utilidades.Conexion.select(sconsulta, conexion);
            if (rs.next()) metodo = rs.getInt("metodo");             
            if (metodo == null) metodo = oVtotalestas.metodo;
            if (metodo != null)
            {
                    if (metodo == 11 || metodo ==12 ||metodo == 13) metodo  = 11;
                    else if (metodo == 14) metodo  = 5;                    
            }     
        }
        catch (Exception e)
        {
            metodo = null;
        }
        finally
        {
            if (metodo != null) return  Utilidades.Cadenas.completTextWithLeftCharacter(metodo, '0', 2);
            else return null;
        }  
    }//MetValoracionElemento
    
    private String getVisitaInterior(Integer idElemento)
    {//Indica si la finca se visitó interiormente cuando se realizó la última tasación completa (Ver tabla 14)
     //X Si //nada No 
        Boolean visitado = false;
        java.sql.ResultSet rs = null;
        String consulta = "";                
        
        try
        {        
                consulta = "select valor from exptcaract where numexp = '"+numexp+"' and codcaract = 1102 and idnumero = "+idElemento;
                rs = Utilidades.Conexion.select(consulta, conexion);
                while (rs != null && rs.next() && !visitado)
                {                    
                    if (rs.getString("valor") != null && !rs.getString("valor").equalsIgnoreCase("") && rs.getInt("valor") == 1)
                    {
                        visitado = true;
                    }
                }                
                rs.close();
                rs = null;                        
        }
        catch (Exception e)
        {
            visitado = null;
        }
        finally
        {
            try
            {
                if (rs != null) rs.close();
            }
            catch (Exception e)
            {
                
            }
            if (visitado != null && visitado) return "X"; //Si
            else if (visitado != null && !visitado) return ""; //No
            else return null;
        }
    }//getVisitaInterior
    
    public void loadCondicionantesActivos()
    {        
        String sConsulta = "";
        java.sql.ResultSet rs = null;
        String texto = null;
        
        try
        {
            if (numexp != null)
            {               
                sConsulta = "SELECT * FROM condicion1 WHERE codusua is null AND numexp = '" +numexp+"'";
                rs = Utilidades.Conexion.select(sConsulta, conexion);
                while (rs.next())
                {
                    condicionatesList.add(rs.getString("texco1"));                    
                }
                rs.close();
                rs = null;
            }
        }//try
        catch (Exception e)
        {
            
        }
        finally 
        {            
        }
    }//loadCondicionantesActivos
    
     public void loadAdvertenciasActivas()
    {        
        String sConsulta = "";
        java.sql.ResultSet rs = null;
        String texto = null;
        
        try
        {
            if (numexp != null)
            {               
                sConsulta = "SELECT * FROM adverten1 WHERE codusua is null AND numexp = '" +numexp+"'";
                rs = Utilidades.Conexion.select(sConsulta, conexion);
                while (rs.next())
                {
                    advertenciasList.add(rs.getString("texad6"));                    
                }
                rs.close();
                rs = null;
            }
        }//try
        catch (Exception e)
        {
            
        }
        finally 
        {            
        }
    }//loadCondicionantesActivos
     
    private String getFinalidad(String finalidad)
    {
        /*
        1- Mercado Hipotecario - 01
2- Fondos de Inversión - 03
3- Cobertura de seguros - 02
4- Informativa sin acceso - 07
5- Circular BE con acceso - 09
6- Peritación de bienes - 09
7- Evaluación de patrimonio - 06
8-  Determinación de Justiprecio - 09
9- Informativa con acceso - 07
0- Circular BE sin acceso - 09
A- Asesoramiento compra venta - 05
V- Valor de mercado - 05
W- Valor de mercado AQR - 09
D- Peritación de daños - 09

        */
        
        if (finalidad != null)
        {
            if (finalidad.equals("1")) return "01";
            else if (finalidad.equals("2")) return "03";
            else if (finalidad.equals("3")) return "02";
            else if (finalidad.equals("4")) return "07";
            else if (finalidad.equals("5")) return "09";
            else if (finalidad.equals("6")) return "09";
            else if (finalidad.equals("7")) return "06";
            else if (finalidad.equals("8")) return "09";
            else if (finalidad.equals("9")) return "07";
            else if (finalidad.equals("0")) return "09";
            else if (finalidad.equals("A")) return "05";
            else if (finalidad.equals("V")) return "05";
            else if (finalidad.equals("W")) return "09";
            else if (finalidad.equals("D")) return "09";            
            else return null;            
        }
        else return null;
    }
    
    private String getMotivo (String codigo)
    {
        String motivo = "999";
        if (codigo != null)
        {
            if (codigo.indexOf("I") != -1) motivo = "001";
            else if (codigo.indexOf("O") != -1) motivo = "004";
            else if (codigo.indexOf("U") != -1) motivo = "005";
            else if (codigo.indexOf("P") != -1) motivo = "006";
            else motivo = "999";
        }
        return motivo;
    }
}
