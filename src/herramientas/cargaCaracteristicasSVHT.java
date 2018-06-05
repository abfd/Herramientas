/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package herramientas;

/**
 *
 * @author abfd
 */
public class cargaCaracteristicasSVHT 
{
    
    public static String sUrlExcel = "/data/informes/Servihabitat/CARACTERISTICAS.xls";
    public static Utilidades.Excel oExcel = null;            
    public static org.apache.poi.hssf.usermodel.HSSFCell celda = null;
    public static String HojaActual = "COMPLETO";
    
    
    private boolean decimal = false;
    
    private String codigo = null;
    private String descripcion = null;
    private String tipoinmsvht = null;
    private Integer obliga = null;
    
    public static void main(String[] args) 
    {
        cargaCaracteristicasSVHT carga = new cargaCaracteristicasSVHT();
        carga = null;
        System.gc();
    }
      
    
    public  cargaCaracteristicasSVHT ()
    {
        java.sql.Connection conexion = null;
        int finicio = 4;
        int ffin = 168;
        String valor = null;
        String sInsert = null;
        try
        {
            conexion = Utilidades.Conexion.get_bbddprod_vt2();
            conexion.setAutoCommit(false);     
            
            oExcel = new Utilidades.Excel(sUrlExcel);            
             
            for (int columna = 3; columna < 54; columna ++)
            {
                for (int fila = finicio; fila < ffin;fila ++)  
                {
                    codigo = null;
                    descripcion = null;
                    tipoinmsvht = null;
                    obliga = null;
                    valor = null;

                    //codigo = getCellValue(fila,0);
                    codigo = Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(HojaActual,fila,0));
                    //descripcion = getCellValue(fila,1);
                    descripcion = Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(HojaActual,fila,1));
                    tipoinmsvht = Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(HojaActual,3,columna));
                    //valor = getCellValue(fila,2);
                    valor = Utilidades.Excel.getValueToString(oExcel.getCeldaFilaHoja(HojaActual,fila,columna));
                    if (valor != null && valor.equals("X")) obliga = 1;
                    else obliga = 0;


                    sInsert = "INSERT INTO caractsvht VALUES ("+codigo+",'"+descripcion+"',"+tipoinmsvht+","+obliga+")";
                    if (Utilidades.Conexion.insert(sInsert, conexion) == 1) conexion.commit();
                    else
                    {
                        conexion.rollback();
                        System.out.println("ERROR INSERT: "+sInsert);
                    }                                
                }
            }
            conexion.close();
            conexion = null;
        }
        catch (Exception e)
        {
            System.out.println("Excepcion: "+e.toString());
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
            catch (Exception e){}
        }
    }
    
    
    private String getCellValue(int fila,int columna)
    {
         String valor = null;
         Double iValor = 0.0;
         
         try
         {                        
          
              celda = oExcel.getCeldaFilaHoja(HojaActual,fila,columna);                
              if (celda != null) 
              {
                   valor = celda.getStringCellValue();
              }      
         }
         catch (NumberFormatException nfe)
         {             
             celda = oExcel.getCeldaFilaHoja(HojaActual,fila,columna);                
             if (celda != null) 
             {
                 iValor = celda.getNumericCellValue();                                             
                 if (iValor != null && !decimal)  valor = Long.toString((long) Math.floor(iValor));                 
                 if (iValor != null && decimal)  valor = Double.toString((Double)(iValor));                                             
             }      
         }
         catch (Exception e)
         {
             valor = null;
             //logger.error("Fila: "+Integer.toString(fila)+"Columna: "+Integer.toString(columna)+" Imposible leer valor: "+e.toString());                    
         }
         finally
         {
             return valor;
         }
    }
    
}
