/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package herramientas;

/**
 *
 * @author Ana Belen de Frutos
 */
public class AgrupacionesCaixa {
    
    
    
    public static void main(String[] args)
    {
           AgrupacionesCaixa nwAgrupacion = new AgrupacionesCaixa("2552 1128 133019 V3");
           nwAgrupacion = null;
           System.gc();
    }
    
    
    public AgrupacionesCaixa(String numexp)
    {
        java.sql.Connection conexion = null;
        String Consulta = null;
        String update = null;
        String insert = null;
        java.sql.ResultSet rs = null;
        int agrupaciones = 0;
        int elementos = 0;
        boolean error = false;
        
        
        try {
            conexion = Utilidades.Conexion.getConnectionValtecnic2();
            if (numexp != null)
            {
                Consulta = "SELECT * FROM elementos WHERE numexp = '"+numexp+"' ORDER BY id_elmto asc";
                rs = Utilidades.Conexion.select(Consulta,conexion);
                while (rs.next())
                {
                    elementos ++;
                    System.out.println("Elemento nº:  "+elementos+" id_elmto: "+rs.getString("id_elmto"));
                    update = "UPDATE elementos SET id_agrupa ="+elementos+", refunidad = "+elementos+" WHERE numexp = '"+numexp+"' AND id_elmto = "+rs.getString("id_elmto");                                                        
                    if (Utilidades.Conexion.update(update,conexion) != 1) 
                    {
                        System.out.println("No se puede actualizar agrupación del elemento "+rs.getString("id_elmto"));
                        error = true;
                        break;
                    }                    
                    else
                    {//insertamos nueva agruacion
                        insert = "INSERT INTO vtotagrupa (numexp,id_agrupa,id_principal,totelmtos) values ('"+numexp+"',"+elementos+","+rs.getString("id_elmto")+",1)";
                        if (Utilidades.Conexion.insert(insert,conexion) != 1) 
                        {
                            error = true;
                            System.out.println("No se puede insertar agrupación prara el elemento "+rs.getString("id_elmto"));                            
                            break;
                        }
                    }
                }//while
                if (!error) 
                {
                    //actualizamos el nº de agrupaciones del edificio
                    update = "UPDATE edificios SET nunidades ="+elementos+", naguded = "+elementos+" WHERE numexp = '"+numexp+"'";
                    if (Utilidades.Conexion.update(update,conexion) != 1) 
                    {
                        error = true;
                        System.out.println("No se puede actualizar información para el edificio");
                    }
                }                
            }                                    
            
        }//try
        catch (Exception e) {
            error = true;
            System.out.println(e);
        }//catch
        finally 
        {
            try
            {
                if (!error) conexion.commit();
                else conexion.rollback();
                conexion.close();
            }
            catch (Exception e)
            {
                
            }
        }
    }
    
}
