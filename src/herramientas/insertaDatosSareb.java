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
public class insertaDatosSareb 
{
    

    public static void main(String[] args) 
    {
        insertaDinamico ("ADVALGRP_20160928_00000000000000152775","5506 1004 175255",1491629);
    }
    
    /**
     * 
     * @param idExpediente, indica los expedientes sobre los que hay que cargar la tabla de dinamico
     * @param numexp, datos de dinamico a copiar.
     * @param iden_suelo, id suelo del dinamico.
     */
    public static void insertaDinamico(String idExpediente,String numexp,Integer idenSuelo)
    {
        java.sql.Connection conexion = null; 
        java.sql.ResultSet rs = null;
        String sConsulta = null;
        Objetos.unidades.Dinamico oDinamico = new Objetos.unidades.Dinamico();
        Objetos.unidades.Suelo oSuelo = new Objetos.unidades.Suelo();
        try
        {
            conexion = Utilidades.Conexion.getConnection("jdbc:oracle:thin:valtecnic2/valtecnic2@oraserver02:1521:rvtnprod");            
            conexion.setAutoCommit(false);  
            
            if (idExpediente != null && numexp != null)
            {                                    
                        sConsulta = "SELECT r.numexp,s.iden_suelo FROM refer r JOIN suelo s on r.numexp = s.numexp LEFT OUTER JOIN dinamico d on s.numexp = d.numexp WHERE r.idexpediente = '"+idExpediente+"' AND r.numexp != '"+numexp+"'";
                        rs = Utilidades.Conexion.select(sConsulta, conexion);
                        while (rs.next())
                        {
                            if (oDinamico.load(numexp, idenSuelo, conexion))  //datos del dinamico del expediente de entrada                          
                            {
                                oDinamico.numexp = rs.getString("numexp");
                                oDinamico.iden_suelo = rs.getInt("iden_suelo");
                                if (oDinamico.insert(conexion) == 1) 
                                {
                                    conexion.commit();
                                    System.out.println("Insertado DINAMICO: "+oDinamico.numexp);
                                }
                                else
                                {
                                    conexion.rollback();
                                    System.out.println("NO insertado: "+oDinamico.numexp);
                                }
                            }
                            else System.out.println("No se ha recuperado DINAMICO para el expediente: "+numexp); 
                        }
                   
                       
            }
            else System.out.println("Los parametros de entrada no son correctos.");                        
        }
        catch (Exception e)
        {
            System.out.println("Excepción general: "+e.toString());    
        }
        finally
        {            
            try
            {
                conexion.close();
            }
            catch (Exception e) {};
        }
    }
}
