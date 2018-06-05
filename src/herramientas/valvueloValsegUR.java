/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package herramientas;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author Ana Belen de Frutos
 */
public class valvueloValsegUR 
{

    public static String sUrlExcel = "/data/informes/Servihabitat/VALORES DEL SEGURO.xls";
    public static Utilidades.Excel oExcel = null;            
    public static org.apache.poi.hssf.usermodel.HSSFCell celda = null;
    public static String HojaActual = "VALORES DEL SEGURO";
    private  java.sql.Connection conexion = null;
    private String referencia = null;
    private String ur = null;
    private String sConsulta = null;
    private java.sql.ResultSet rs = null;
    private Objetos.v2.Refer oRefer = new Objetos.v2.Refer();
    
     public static void main(String[] args) 
    {
        valvueloValsegUR calcula = new valvueloValsegUR();
        calcula = null;
        System.gc();
    }
    
    
    
    public valvueloValsegUR()
    {
        Integer num_dr = null;
        Double vuelo = 0.0;
        Double seguro = 0.0;
        try
        {            
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@oraserver02:1521:rvtnprod");            
            conexion.setAutoCommit(false);                
            oExcel = new Utilidades.Excel(sUrlExcel);          
            for (int fila = 0; fila <2000; fila ++)
            {//registro   
                oRefer.clear();
                vuelo = 0.0;
                seguro = 0.0;
                //System.out.println("Fila: "+(fila+1));
                referencia = dameValorExcel(fila,0);
                ur = dameValorExcel(fila,1);
                if (oRefer.loadValuesFromReferencia(referencia, conexion) == 1)
                {
                    sConsulta = "SELECT numero FROM datosreg WHERE numexp = '"+oRefer.numexp+"' AND numseguim = "+ur;
                    rs = Utilidades.Conexion.select(sConsulta, conexion);
                    if (rs.next()) num_dr = rs.getInt("numero");
                    rs.close();
                    rs = null;
                    if (num_dr != null)
                    {
                        sConsulta = "SELECT sum(NVL(valvuelo,0)) valvuelo, sum (NVL(valseg,0)) valseg FROM vtotales JOIN elementos ON (vtotales.idnumero = elementos.id_elmto) WHERE elementos.numexp = '"+oRefer.numexp+"' AND elementos.num_dr = "+num_dr;
                        rs = Utilidades.Conexion.select(sConsulta, conexion);
                        if (rs.next())
                        {
                            vuelo = rs.getDouble("valvuelo");
                            seguro = rs.getDouble("valseg");                            
                            //System.out.println("referencia: "+referencia+" y UR: "+ur+ " VUELO: "+vuelo);
                            //System.out.println("referencia: "+referencia+" y UR: "+ur+ " SEGURO: "+seguro);
                        
                        }
                        //else System.out.println("referencia: "+referencia+" y UR: "+ur+ " SIN valores de vuelo y suelo");
                        rs.close();
                        rs = null;
                    }
                    //else System.out.println("referencia: "+referencia+" y UR: "+ur+ " SIN datos registrales");
                }
                //else System.out.println("referencia: "+referencia+" y UR: "+ur+ " NO encontrado");      
                System.out.println(Utilidades.Cadenas.translate(Utilidades.Cadenas.getNumberParse(seguro,"#0.00"),'.',','));
            }           
                
            conexion.close();
            conexion = null;
        }
        catch (Exception e)
        {
            
        }
        finally
        {
            try
            {   
                if (rs != null && !rs.isClosed())
                {
                    rs.close();
                    rs = null;
                }
                if (conexion != null && !conexion.isClosed())
                {
                    conexion.close();
                    conexion = null;
                }
            }
            catch (Exception e){
                
            }
        }
    }
    
     public static String dameValorExcel(int fila,int col)  
    {
        String valor = null;
        try
        {                 
             celda = oExcel.getCeldaFilaHoja(HojaActual,fila,col);                                 
             valor = Utilidades.Excel.getStringCellValue(celda);         
        }   
        catch (FileNotFoundException fnfe)
        {
            valor = null;
        }
        catch (IOException ioe)
        {
            valor = null;
        }
        catch (Exception e)
        {
            valor = null;
        }
        finally
        {
            return valor;
        }
    }//dameValorExcel
}
