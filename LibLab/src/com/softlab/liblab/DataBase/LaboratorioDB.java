/*
 * LaboratorioDB.java
 * This file is part of products-control-Prosid
 *
 * Copyright (C) 2015 J.Patricio Hijuitl
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses/>.
 */

package com.softlab.liblab.DataBase;

import com.softlab.liblab.reports.*;
import com.softlab.liblab.tools.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

public class LaboratorioDB
{
    private Connection m_sC;
    
    public LaboratorioDB(Session s) throws SQLException
    {
       m_sC = s.getConnection();
    }

    // busca coincidencias en la tabla REGISTRO_STATIC con respecto al nombre
    //de los productos regitrados
    public ResultSet prediccion(String nombre)
    {
        try {
            return new Sentence(SentenceList.prediccion(nombre), m_sC).openExec();
        } catch(SQLException e){
            System.out.print("error en funcion buscarProductos(): "+e);
            return null;
        }
    }

    // regresa un Producto 
    // retorna el siguiente numero de analisis de la tabla REGISTRO
    public int sgtNoAnalisis()
    {
        try {
            ResultSet rs = new Sentence("SELECT MAX(ID_ANALISIS) "
                            + "+ 1 AS sgtId FROM registro", m_sC).openExec();
            return rs.getInt("sgtId");
        } catch(SQLException e) {
            System.out.print(e);
            return 0;
        }
    }
    
    // regresa el sigueinte numero de analisis al de su argumento; de la Tabla REGISTRO
    public ResultSet sgtProducto(int idAnalisis) throws SQLException
    {
        return new Sentence("SELECT NOMBRE, ID_ANALISIS, LOTE FROM registro "
                +"WHERE ID_ANALISIS > "+idAnalisis+" LIMIT 1",m_sC).openExec();
    }
    
    // regresa el sigueinte numero de analisis al de su argumento; de la Tabla REGISTRO
    public ResultSet antProducto(int idAnalisis) throws SQLException
    {
        return new Sentence(" SELECT NOMBRE, ID_ANALISIS, LOTE FROM registro "
                        +" WHERE ID_ANALISIS < "+idAnalisis+" "
                        + "ORDER BY ID_ANALISIS DESC LIMIT 1",m_sC).openExec();
    }

    // regresa el siguiente numero de certificado de la tabla REGISTRO
    public int sgtNoCertificado()
    {
        try {
            ResultSet rs = new Sentence("SELECT MAX(CERTIFICADO) "
                            + "+ 1 AS sgtC FROM registro", m_sC).openExec();
            rs.next();
            return rs.getInt("sgtC");
        } catch(SQLException e) {
            System.out.print(e);
            return 0;
        }
    }

    // delete register of table REGISTRO
    public void eliminarDato(int idAnalisis)
    {
        try {
            new Sentence(SentenceList.eliminarDato("registro", "ID_ANALISIS", 
                idAnalisis),m_sC).exec();
        } catch(Exception e) {
            System.out.println("Error en funcion eliminar producto: "+e);
        }
    }
    
    // elimina un registro
    public void eliminarEmbarque(int id)
    {
        try {
            new Sentence(SentenceList.eliminarDato("embarque", "ID", id),
                    m_sC).exec();
        } catch(Exception e) {
            System.out.println("Error en funcion eliminar embarque: "+e);
        }
    }
    
// regresa lo registrado con la fecha indicada
    public ResultSet getRegistrosFecha(String f)
    {     
        try {
            return new Sentence(SentenceList.getRegistrosFecha(f),
                    m_sC).openExec();
        }
        catch (SQLException e) {
            System.out.println("error en funcion registroMes: "+e.getMessage());
            return null;
        }
    }
    
    // regresa lo registrado con el nombre indicado
    public ResultSet getRegistrosNombre(String nombre, int n)
    {     
        try {
            return new Sentence(SentenceList.getRegistrosNombre(nombre,n),
                    m_sC).openExec();
        }
        catch (SQLException e) {
            System.out.println("error en funcion getRegistrosNombre: "
                    +e.getMessage());
            return null;
        }
    }
     
 // retorna los valores de la tabla indicada con el idAnalisis
    public ResultSet getAnalisis(int idAnalisis, String tabla,
            ArrayList<String> analisis)
    {
        String sql =  "SELECT ";
        for(String a: analisis) sql = sql+a+" ,";
        sql = sql.substring(0,sql.length()-1);
        sql = String.format(sql+" FROM %s WHERE ID_ANALISIS = %d",
                tabla,idAnalisis);
        try {
            return new Sentence(sql, m_sC).openExec();
        } catch(Exception e) {
            System.out.println("error en funcion getAnalisis: "+e);
        }
        return null;
    }

    public ResultSet getAnalisis(int año,String producto, String tabla)
    {
        Fecha i = new Fecha(año);
        Fecha f = new Fecha(año,12,31);        
        try {
            String query = "SELECT X.*, R.ID_ANALISIS, R.NOMBRE, R.FECHA FROM "
                + tabla + " X INNER JOIN registro R "
                + "ON (R.ID_ANALISIS = X.ID_ANALISIS) WHERE R.FECHA >= '"
                + i.getFormMySQL()+"' AND R.FECHA <= '"+f.getFormMySQL()+"' "
                + "AND R.NOMBRE = '"+producto+"'";
            return new Sentence(query, m_sC).openExec();       
        } catch(Exception e) {
            System.out.println("error en funcion getAnalisis: "+e);
        }
        return null;
    }

    public String setAnalisis(int idAnalisis, Producto producto, HashMap datos)
    {
        try {
            ArrayList<Grupo> grps = producto.getGrupos();
            for (Grupo g: grps) {
                String tabla = g.getTabla();
                String query = "UPDATE "+tabla+" SET ";
                ArrayList<String> analisis = g.getAnalisis();
                for(String c: analisis) 
                    query = query+c+" = "+datos.get(c)+" ,";
                query =  query.substring(0,query.length()-1);
                query = query + " WHERE ID_ANALISIS = "+idAnalisis;
                new Sentence(query,m_sC).exec();
            }
        } catch(Exception e) {
            System.out.println(e);
            return e.getMessage();
        }
        return null;
    }
    
    // regresa los embarques que coinciden con los parametros recividos
    public ResultSet getEmbarques(int limit, String producto, String cliente)
    {
        if(producto.equals("TODOS")) {
            producto = "'%'";
        } else { 
            producto = "'%"+producto+"%'";
        }
        if (cliente.equals("TODOS")){ 
            cliente = "'%'";
        } else {
            cliente = "'%"+cliente+"%'";
        }
        try {
            //SELECT E.*, R.NOMBRE, R.LOTE 
            //FROM EMBARQUE E INNER JOIN REGISTRO R ON (R.ID_ANALISIS = E.ID_ANALISIS) 
            //AND R.NOMBRE LIKE '%' AND CLIENTE LIKE '%' 
            //ORDER BY FECHA DESC, NOMBRE, ID_ANALISIS limit  20
            String query = "SELECT ID, E.FECHA AS FECHA, E.CLIENTE AS CLIENTE, "
                    + "E.ID_ANALISIS AS NO_ANALISIS, R.NOMBRE AS NOMBRE, "
                    + "R.LOTE AS LOTE, E.CANTIDAD AS CANTIDAD FROM embarque "
                    + "E INNER JOIN registro R ON (R.ID_ANALISIS = E.ID_ANALISIS)"
                    + " AND R.NOMBRE LIKE "+producto+" AND CLIENTE LIKE "+cliente 
                    + " ORDER BY FECHA DESC, NOMBRE, E.ID_ANALISIS limit  "+limit;          
            return new Sentence(query, m_sC).openExec();
        } catch(Exception e) {
            System.out.println("error en funcion getEmbarques: "+e); 
            return null;
        }
    }

    // regresa un mensaje de errro o null si no lo hay
    public String embarcar(String cliente, String producto, int requerido)
    {
        try {
        /*esto me sirve para verficar que ya no queda nada de producto del 
         lote del ultimo embarque*/
        int idAnalisis = 0;
        int enviado = 0;
        int enviar = 0;
        /*SELECT MAX(E.ID_ANALISIS) FROM EMBARQUE E
          INNER JOIN REGISTRO R ON (E.ID_ANALISIS = R.ID_ANALISIS)
          WHERE R.NOMBRE = 'ESPUMAG G'*/
        String query = "SELECT ID_ANALISIS, SUM(CANTIDAD) AS CANTIDAD "
                    + "FROM embarque "
                    + "WHERE ID_ANALISIS = "
                    + "( SELECT MAX(E.ID_ANALISIS) FROM embarque E "
                    + "INNER JOIN registro R ON (E.ID_ANALISIS = R.ID_ANALISIS )"
                    + "WHERE R.NOMBRE = '"+producto+"' )";
        //System.out.println("embarcar: "+query);
        ResultSet rs = new Sentence(query, m_sC).openExec();
        if (rs.next()) {
            idAnalisis = rs.getInt("ID_ANALISIS"); 
            enviado = rs.getInt("cantidad");
        } // cantidad enviada  
        rs = new Sentence("SELECT ID_ANALISIS, CANTIDAD "
                        + "FROM registro "
                        + "WHERE NOMBRE = '"+producto
                        + "' AND ID_ANALISIS >= "+idAnalisis,m_sC).openExec(); 
        if (rs.next()) {
            int sobrante = rs.getInt("CANTIDAD") - enviado; // cantidad restante
            if (sobrante > 0) {
                if (requerido >= sobrante) {
                    requerido = requerido - sobrante;
                    enviar = sobrante;
                } else {
                    enviar = requerido;
                    requerido = 0;
                }
                new Sentence("INSERT INTO embarque(FECHA, CLIENTE, ID_ANALISIS, CANTIDAD) "
                            + "VALUES ('"+new Fecha().getFormMySQL()+"' , '"+cliente
                            +"' , "+rs.getString("ID_ANALISIS")+" , "+enviar+")",m_sC).exec();
             //   genCertificado(idAnalisis);
                if(requerido == 0)
                    return null;
            }
        }
        //-----
        while (requerido != 0 && rs.next()) { // si aun 
            idAnalisis = rs.getInt("ID_ANALISIS");
            int fabricado = rs.getInt("CANTIDAD");
            if (requerido >= fabricado) {
                enviar = fabricado;
                requerido = requerido - fabricado;
            } else {
                enviar = requerido;
                requerido = 0;
            }
            new Sentence("INSERT INTO embarque(FECHA, CLIENTE, ID_ANALISIS, CANTIDAD) "
                            + "VALUES ('"+new Fecha().getFormMySQL()+"' , '"
                            + cliente+"' , "+rs.getString("id_analisis")+", "
                            +enviar+")",m_sC).exec();
        }
        } catch(Exception e) { 
            System.out.println("error en funcion embarcar()"+e);
        }
        if (requerido != 0) {
            return "Material Insuficiente";
        }
        else { 
            return null;
        }
    }

// genera certificados y regresa mensaje con informacion acerde de si cumple con especificaciones
    public String genCertificadosEmb(String cliente, Fecha fecha, ProductList prods)
    {
        String mensaje = "";
        try {
            String query = "SELECT embarque.ID_ANALISIS, registro.NOMBRE "
                    + "FROM embarque INNER JOIN registro "
                    + "ON(embarque.ID_ANALISIS = registro.ID_ANALISIS) "
                    + "WHERE CLIENTE = '%s' AND embarque.FECHA = '%s'";
            ResultSet rs = new Sentence(String.format(query,cliente,fecha.getFormMySQL()),
                            m_sC).openExec();
            ArrayList<JasperPrint> lista =  new ArrayList();
            JasperPrint print;
            while (rs.next()) {
                int idAnalisis = rs.getInt("ID_ANALISIS");
                Producto prod = prods.getProducto(rs.getString("NOMBRE"));
                mensaje  = mensaje + verificaAnalisis(idAnalisis, prod);
                if ((print = getCertificadoId(idAnalisis,prod)) != null) {
                    lista.add( print );
                }
            }
            if (mensaje.equals("")) 
                return "ningun embarque coincide con los datos proporcionados";
            Reportes.concatPdf(lista, Paths.dPathCertificados+cliente+"-"
                    +fecha.getFormFecha());
            String apppdf = "evince "+Paths.dPathCertificados+cliente+"-"
                    +fecha.getFormFecha()+".pdf";
            Process p = Runtime.getRuntime().exec (apppdf); 
            System.out.println(apppdf);
        } catch(Exception e){
            System.out.println("error en funcion genCertificadosEmb() : "+e);
        }
        return mensaje;
    }

// crea el certificado de calidad del numero de analisis recibido
    public JasperPrint getCertificadoId(int id_analisis, Producto producto) 
    throws SQLException, JRException, Exception
    {
        ResultSet rs = new Sentence("SELECT CERTIFICADO FROM registro "
                + "WHERE ID_ANALISIS = "+id_analisis,m_sC).openExec();
        if (rs.next()) { 
            return getCertificadoNo(rs.getInt("CERTIFICADO"),producto);
        }
        else { 
            return null;
        }
    }

    // crea el certificado de calida con el numero de certificado recibido
    public JasperPrint getCertificadoNo(int no_certificado,Producto producto) 
    throws SQLException, JRException, Exception
    {
        ResultSet rs = new Sentence("SELECT LOTE FROM registro "
                + "WHERE CERTIFICADO = "+no_certificado,m_sC).openExec();
        HashMap parameters = new HashMap();
        if(rs.next()) {
            //if(cliente == null) cliente = rs.getString("CLIENTE_PROV").trim();
            parameters.put("certificado", no_certificado);
            parameters.put("caducidad", producto.getCaducidad(rs.getString("LOTE")));
            parameters.put("fecha",new Fecha().getFecha());
        } else { 
            return null;
        } // no hay parametros
        String path = Paths.sPathCertificados;
        String fileJasper = String.format(path+"%s.jasper",producto.getFileJasper());
        JasperReport masterReport = (JasperReport)JRLoader.loadObject(fileJasper);
        return JasperFillManager.fillReport(masterReport,parameters,m_sC);
    }
    
    // metodo que verifica que el material este dentro de analisis tipico
    public String verificaAnalisis(int idAnalisis, Producto p)
    {
        String info;
        try {
            String query = "SELECT SiO2, Al2O3, FeO, CaO, MgO, C, H2O "
                    + "FROM analisis_gral WHERE ID_ANALISIS = "+idAnalisis;
            ResultSet analisis = new Sentence(query,m_sC).openExec();
            info = p.check(analisis, m_sC); 
        } catch(Exception e) {
            System.out.println("error en verificarAnalisis: "+e);
            return "error al ejecutar operacion\n";
        }
        return idAnalisis+": "+info+"\n";
    }

    /* genera reporte segun su parametro reporte
    public void genReporte(String reporte, HashMap datos, String dir, String formato)
    {
        JasperPrint jasperPrint = getJasperPrint(datos, reporte);
        try 
        {
            Reportes.runReporte(jasperPrint, dir+"/"+Reportes.corregirNombreFile(reporte), formato);
        }
        catch (JRException ex) {System.out.println("Error en funcion genReporte: "+ex);}
    }

    public JasperPrint getJasperPrint(HashMap datos, String reporte)
    {
        try {
            Statement s = conexion.createStatement();
            String fileJasper = null;
            if(reporte.equals("Reporte Producto Terminado"))
                fileJasper = "src/master_report/analisis_pt/general/analisis_pt.jasper";
            else if(reporte.equals("Reporte Materia Prima"))
                fileJasper = "src/master_report/analisis_mp/general/analisis_mp.jasper";
            else 
            {
                String tipo = null;
                String nombre = (String)datos.get("producto");
                //para conocer a que tipo pertenece el material, dato que sera utilizado
                //para saber el archivo .jasper al que se hara referencia 
                ResultSet rs = s.executeQuery("SELECT TIPO FROM REGISTRO_STATIC WHERE NOMBRE = '"+nombre+"'");
                if(rs.next())   tipo = rs.getString("TIPO");
                if(tipo.equals("PT"))
                {
                    if(nombre.equals("LUGITEC AL"))
                        fileJasper = "src/master_report/reporte_analisis_pt_individual.jasper";
                    else if(nombre.equals("SIDOX BLOCK EP"))
                        fileJasper = "src/master_report/reporte_analisis_sidox_block_ep.jasper";
                    else
                        fileJasper = "src/master_report/reporte_analisis_pt_individual.jasper";
                }
                else if(tipo.equals("MP"))
                {
                    //fileJasper = "src/master_report/reporte_analisis_mp.jasper";
                }
            }
            JasperReport masterReport = (JasperReport)JRLoader.loadObject(fileJasper);
            JasperPrint jasperPrint = JasperFillManager.fillReport(masterReport,datos,conexion);
            return jasperPrint;
            
        } catch (Exception ex) {System.out.println("Error en funcion genRAPT: "+ex); return null;}
    }

*/
    
    //public String[] getAllProds(){return prods.getNombres();}
    // regresa un ResultSet con todos lo producto terminado en registro_static
   // public ArrayList<String> getPTs(){return prods.getMp();}
  // regresa un ResultSet con todo la materia prima en la tabla REGISTRO_STATIC
  //  public ArrayList<String> getMPs(){return prods.getMp();}
    
    // inserta el idAnalisis del producto indicado en las tablas que le correspondes
    public void actTablas(Producto producto)
    {
        ResultSet r;
        try {
            //Statement s = conexion.createStatement();
            //Statement s1 = conexion.createStatement();
            String query = "SELECT ID_ANALISIS FROM registro "
                        + "WHERE NOMBRE = '"+producto.getNombre()+"'";
            r = new Sentence(query,m_sC).openExec();
            int id;
            while( r.next() ){
                id =  r.getInt("ID_ANALISIS");
                System.out.println("actualizando: "+id);
                actTablas(id,producto);
            }
            
        } catch(SQLException ex) {
            System.out.println("actTablas: "+ex);
        }
    }
    
    // inserta el idAnalisis del producto indicado en las tablas que le correspondes
    public void actTablas(int idAnalisis, Producto p)
    {
        ArrayList<Grupo> grupos = p.getGrupos();
        for (Grupo g: grupos) {
            try {
                String query1 = String.format("INSERT INTO %s(ID_ANALISIS) "
                        + "VALUES(%d)",g.getTabla(),idAnalisis);
                new Sentence(query1,m_sC).exec();
            } catch(SQLException ex) {
                System.out.println("error actTablas"+ex);
            }
        }
    }
       
    // regresa un ResultSet con la consulta 
    public ResultSet query(String consulta)
    {
        ResultSet r = null;
        try {
            //Statement s = conexion.createStatement();
            //r = new Sentence(consulta,m_sC).openExec();
            return new Sentence(consulta,m_sC).openExec();
        } catch (SQLException ex) {
            System.out.println("error enfuncion ejecutarConsulta: "+ex);
            return null;
        }
    }
    
    public int execute(String consulta)
    {
        try {
            new Sentence(consulta, m_sC).exec();
            return 0;
        } catch (SQLException ex) {
            System.out.println("error enfuncion execute: "+ex);
            return 1;
        }
    }
    
    public Connection getConexion()
    {
        return m_sC;
    }
    
    /******************* Cotrol de calidad *****************/
/*
    public HashMap calculos(String material, String caracteristica, String fechaDe, String fechaA)
    {
        HashMap resultados = new HashMap();
        ResultSet r,r1;
        try {
            Statement s = conexion.createStatement();
            r = s.executeQuery("SELECT TABLA FROM RELACION WHERE CARACTERISTICA = '"+caracteristica+"'");
            r.next();
            String tabla = r.getString("TABLA");
           // r1 = s.executeQuery("SELECT "+caracteristica+" FROM "+tabla
           //         + " WHERE ID_ANALISIS IN(SELECT ID_ANALISIS FROM REGISTRO "
           //         + " WHERE PRODUCTO = '"+material+"' AND FECHA >= "
           //         + new Fecha(fechaDe).getFormMySQL()
           //         + " AND FECHA <= "+new Fecha(fechaA).getFormMySQL());
            String q = "SELECT "+caracteristica+" FROM "+tabla
                    + " WHERE ID_ANALISIS IN(SELECT ID_ANALISIS FROM REGISTRO "
                    + " WHERE NOMBRE = '"+material+"' AND LOTE LIKE '%')";
            System.out.println(q);
            r1 = s.executeQuery(q);

           // añade un ImageIcon
            //resultados.put("histograma", calidad.CreatHistograma(r1,material,caracteristica));
            // añade promedio
            resultados.put("promedio", calidad.promedio(r1));
            // añade moda
            resultados.put("moda", calidad.moda(r1));
            // añade rango
            resultados.put("rango", calidad.rango(r1));
            // añade desviacion estandar
            resultados.put("desviacionStd", calidad.desviacionStd(r1));

            return resultados;
            
        }catch (SQLException ex) {
            System.out.println("error en funcion crearHistograma: "+ex);
            return null;
        }
        
    }*/
}
 
