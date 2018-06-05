/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servihabitat;

import java.util.List;

/**
 *
 * @author abfd
 */
public class cargaCircunstancias 
{

    /**
     *
     */
    public java.sql.Connection conexion;
    public  String sUrlExcel = "/data/informes/Servihabitat/codificacion condicionantes y advertencias v20.xls";
    public  Utilidades.Excel oExcel = null;            
    public  org.apache.poi.hssf.usermodel.HSSFCell celda = null;
    public  String HojaActual = "hoja1";
    
    public Objetos.Circuns1 oCircuns1 = new Objetos.Circuns1();
    public int insertados = 0;
    public int noInsertados = 0;
    public int enBlanco = 0;
    
    public List <Integer> clientes = new java.util.ArrayList();
    
     public static void main(String[] args) 
    {
        cargaCircunstancias carga = new cargaCircunstancias();
        carga = null;
        System.gc();
    }
    
    public cargaCircunstancias()
    {
        String svalor = null;
        
        try
        {
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@oraserver02:1521:rvtnprod");
            //conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:desarrollo/des0329@oraserver03:1521:rvtn3");
            conexion.setAutoCommit(false);   
            
            oExcel = new Utilidades.Excel(sUrlExcel);              
            celda = null;
            clientes.add(255);
            clientes.add(355);
            clientes.add(755);
            clientes.add(855);
            clientes.add(955);
            for (int fila = 0; fila < 144; fila ++)
            {//registro   
                System.out.println("fila: "+fila);
                oCircuns1.clear();
                svalor = dameValorExcel(fila,0);
                if (svalor != null && !svalor.equalsIgnoreCase(""))
                {
                    if (svalor != null) oCircuns1.fincli = svalor;
                    svalor = dameValorExcel(fila,1);
                    if (svalor != null) oCircuns1.cod_cir = Integer.parseInt(svalor);
                    svalor = dameValorExcel(fila,2);
                    if (svalor != null) oCircuns1.tipo = svalor.toUpperCase();
                    svalor = dameValorExcel(fila,3);
                    if (svalor != null) oCircuns1.cod_cir1 = Integer.parseInt(svalor);
                    svalor = dameValorExcel(fila,4);
                    if (svalor != null) oCircuns1.texto = svalor.trim();
                    oCircuns1.idioma = "e";
                    oCircuns1.incluyetxt = 0;
                    for (int cliente:clientes)
                    {
                        oCircuns1.codcli = cliente;
                        if (insertWithoutScheme(conexion) == 1) 
                        {
                            conexion.commit();
                            insertados ++;
                        } 
                        else
                        {
                            conexion.rollback();
                            noInsertados ++;
                        }
                    }
                }
                else enBlanco ++;
            }//for
            conexion.close();
            conexion = null;
            System.out.println("Insertados: "+insertados);
            System.out.println("No Insertados: "+noInsertados);
            System.out.println("En blanco: "+enBlanco);
        }
        catch (Exception e)
        {
            System.out.println("Excepción general: "+e.toString());
        }
    }
    
    public String dameValorExcel(int fila,int col)  
    {
        String valor = null;
        try
        {                 
             celda = oExcel.getCeldaFilaHoja(HojaActual,fila,col);                                 
             valor = Utilidades.Excel.getStringCellValue(celda);         
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
    
    private int insertWithoutScheme(java.sql.Connection connection) throws java.lang.Exception
    {
        int iNumeroRegistrosAfectados = 0;
        try
        {            
            Utilidades.Consultas oConsulta = null;
            if(connection!=null)
            {
           oConsulta = new Utilidades.Consultas(Utilidades.Consultas.INSERT);
           oConsulta.fromWithoutScheme("circuns1");
           oConsulta.insert("cod_cir",Utilidades.Cadenas.getValorSinBlancos(oCircuns1.cod_cir),Utilidades.Consultas.INT);
           oConsulta.insert("cod_cir1",Utilidades.Cadenas.getValorSinBlancos(oCircuns1.cod_cir1),Utilidades.Consultas.INT);
           oConsulta.insert("tipo",Utilidades.Cadenas.getValorSinBlancos(oCircuns1.tipo),Utilidades.Consultas.VARCHAR);
           oConsulta.insert("idioma",Utilidades.Cadenas.getValorSinBlancos(oCircuns1.idioma),Utilidades.Consultas.VARCHAR);
           oConsulta.insert("texto",Utilidades.Cadenas.getValorSinBlancos(oCircuns1.texto),Utilidades.Consultas.VARCHAR);
           oConsulta.insert("codcli",Utilidades.Cadenas.getValorSinBlancos(oCircuns1.codcli),Utilidades.Consultas.INT);
           oConsulta.insert("fincli",Utilidades.Cadenas.getValorSinBlancos(oCircuns1.fincli),Utilidades.Consultas.VARCHAR);
           oConsulta.insert("incluyetxt",Utilidades.Cadenas.getValorSinBlancos(oCircuns1.incluyetxt),Utilidades.Consultas.INT);
           iNumeroRegistrosAfectados = Utilidades.Conexion.insert(oConsulta.getSql(),connection);
            }//if
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
            iNumeroRegistrosAfectados = 0;
        }
        finally
        {
            return iNumeroRegistrosAfectados;
        }
    }//insert

}
