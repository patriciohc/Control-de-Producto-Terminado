/*
 * Producto.java
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

import com.softlab.liblab.tools.Fecha;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


public class Producto implements Registro
{
    private int id = 0;
    private String nombre = null,clienteProveedor = null,tipo = null,fileJasper = null;
    private int caducidad = 0;
    private ArrayList<Grupo> grupos = new ArrayList<Grupo>();
    private ArrayList<String> nameGrupos = new ArrayList<String>();
    private HashMap tipicosMin = new HashMap<String,Float>(); 
    private HashMap tipicosMax = new HashMap<String,Float>();
    private boolean tipicosIsLoad = false; // los analisis tipicos estan cargados ??
    
    Producto(String nombre)
    {
        this.nombre = nombre;
    }
    
    Producto(int id)
    {
        this.id = id;
    }
    
    public Producto(int id, String nombre, String  clientProv, String tipo, 
                int caducidad, ArrayList<String> grupos, String fileJasper )
    {
        this.id = id;
        this.nombre = nombre;
        this.clienteProveedor = clientProv;
        this.caducidad = caducidad;
        this.tipo = tipo;
        this.fileJasper = fileJasper;
        this.nameGrupos = grupos;
    }

    public boolean load(Connection m_sC) throws SQLException{
        ResultSet rs;
        String query =  String.format("SELECT NOMBRE, CLIENTE_PROVEEDOR, TIPO, "
                            + "CADUCIDAD, FILE_JASPER"
                            +" FROM producto"
                            +" WHERE NOMBRE = '%s' OR ID = %d",nombre,id);
        if ((rs = new Sentence(query, m_sC).openExec()) == null) 
            return false;
        if (!rs.next()) 
            return false;
        nombre =  rs.getString("NOMBRE");
        clienteProveedor = rs.getString("CLIENTE_PROVEEDOR");
        tipo = rs.getString("TIPO");
        caducidad = rs.getInt("CADUCIDAD");
        fileJasper = rs.getString("FILE_JASPER");
        return true;
    }
    
    public boolean loadGrupos(Connection m_sC)throws SQLException
    {
        for (String name: nameGrupos) {
            Grupo g = new Grupo(name);
            g.load(m_sC);
            grupos.add(g);
        }
        return true;
    }
    
    private boolean loadTipico(Connection c)throws SQLException
    {
        String query =  String.format("SELECT * FROM esp_rangos "
                + "WHERE MATERIAL = '%s' AND MIN_MAX = 0",nombre);
        String query1 =  String.format("SELECT * FROM esp_rangos "
                + "WHERE MATERIAL = '%s' AND MIN_MAX = 1",nombre);
        ResultSet rs = new Sentence(query, c).openExec();
        ResultSet rs1 = new Sentence(query1, c).openExec();
        ResultSetMetaData rsMD = rs.getMetaData();
        int nc = rsMD.getColumnCount(); // numero de columnas
        if (!rs.next()) 
            return false;
        if (!rs1.next()) 
            return false;
        for (int i = 3 ; i <= nc; i++) {
            String label = rsMD.getColumnName(i);
            tipicosMin.put(label, rs.getFloat(label));
            tipicosMax.put(label, rs1.getFloat(label));
        } 
        tipicosIsLoad = true;
        return true;
    }
    
   // metodo que verifica que el material este dentro de analisis tipico
    public String check(ResultSet analisis, Connection c)throws SQLException
    {
        if (!tipicosIsLoad) 
            loadTipico(c);
        String info = "";
        try {
            String deflt[] = {"SiO2", "Al2O3", "FeO", "CaO", "MgO", "C", "H2O"};
            analisis.next();
            //ResultSetMetaData elementos = analisis.getMetaData();
            for (int i = 0; i<deflt.length; i++) {
                float dato = analisis.getFloat(deflt[i]);
                float minE = (Float)tipicosMin.get(deflt[i]);
                float maxE = (Float)tipicosMax.get(deflt[i]);
                if (dato < minE || dato > maxE) info = info + "fuera de rango: "+deflt[i]+"\n";
            }
        } catch(Exception e) {
            System.out.println("error en verificarAnalisis: "+e);
            return "error al ejecutar operacion\n";
        }
        if(info.equals(""))
            return "ok";
        else return info;
    }
    
    // metodo que verifica que el material este dentro de analisis tipico
    public String check(ArrayList<Object[]> datos, Connection c)throws SQLException
    {
        if (!tipicosIsLoad) 
            loadTipico(c);
        String info = "";
        //try{
            //ResultSetMetaData elementos = analisis.getMetaData();
            for (Object[] par: datos) {
                String el = (String)par[0]; 
                float dato = (Float)par[1]; 
                Float minE = (Float)tipicosMin.get(el);
                Float maxE = (Float)tipicosMax.get(el);
                if (minE == null || maxE == null || (minE == 0 && maxE == 0) ) continue;
                if(dato < minE || dato > maxE) info = info + "fuera de rango: "+el+"\n";
            }
            
        //}catch(Exception e){
         //   System.out.println("error en verificarAnalisis: "+e);
          //  return "error al ejecutar operacion\n";
        //}
        if(info.equals("")) {
            return "ok";
        } else { 
            return info;
        }
    }
      
    public boolean save(Connection c) throws SQLException 
    {
        String query =  String.format("INSERT INTO producto(NOMBRE, "
                    + "CLIENTE_PROVEEDOR, TIPO, CADUCIDAD, FILE_JASPER)"
                    + "VALUES ('%s', '%s', '%s', %d, '%s')", nombre, 
                    clienteProveedor, tipo, caducidad, fileJasper);
        new Sentence(query, c).exec();
        query =  String.format("INSERT INTO esp_rangos(MATERIAL, MIN_MAX)"
                    + "VALUES ('%s', 0),('%s', 1)",nombre,nombre);
        new Sentence(query, c).exec();
        System.out.println("agregando grupos");
        for (String name: nameGrupos) {
            query = String.format(" INSERT INTO producto_grupo(NOMBRE_PRODUCTO, "
                    + "NOMBRE_GRUPO) VALUES('%s', '%s')",nombre,name);
            new Sentence(query, c).exec();
        }
        return true;
    }
    
    public boolean update(Connection c) throws SQLException
    {
        String query =  String.format("UPDATE producto SET NOMBRE='%s', "
                    + "CLIENTE_PROVEEDOR='%s', TIPO='%s', CADUCIDAD=%d, "
                    + "FILE_JASPER='%s' WHERE ID=%d",
                    nombre,clienteProveedor, tipo, caducidad, fileJasper,id);
        new Sentence(query, c).exec();
        query = String.format("DELETE FROM producto_grupo "
                + "WHERE NOMBRE_PRODUCTO='%s'",nombre);
        new Sentence(query, c).exec();
        query = "INSERT INTO producto_grupo(NOMBRE_PRODUCTO, NOMBRE_GRUPO) VALUES ";
        for (String ng: nameGrupos) {
            query =  query + String.format("('%s','%s') ,",nombre,ng);
        }
        query = query.substring(0, query.length()-2);
        new Sentence(query, c).exec();
        return true;
    }
    
    public boolean delete(Connection c) throws SQLException
    {
        ResultSet rs =  new Sentence(String.format("SELECT COUNT(*) "
                                    +"FROM registro "
                                    +"WHERE NOMBRE='%s'",nombre), c).openExec();
        if (!rs.next()) 
            return false;
        if(rs.getInt(1) != 0)
            return false;
        String query =  String.format("DELETE FROM producto WHERE NOMBRE='%s'",nombre);
        new Sentence(query, c).exec();
        return true;
    }
     
    public String getNombre()
    {
        return nombre;
    }

    public String getClienteProveedor()
    {
        return clienteProveedor;
    }

    public String getTipo()
    {
        return tipo;
    }
    
    public String getFileJasper()
    {
        return fileJasper;
    }

    public String getCaducidad(String lote)
    {
            Fecha f = new Fecha(lote);
            f.incrementarMes(caducidad);
            return f.getFormFecha();
    }
    
    public int getCaducidad()
    {
        return caducidad;
    }
    
    public int getId()
    {
        return id;
    }

    public ArrayList<Grupo> getGrupos() 
    {
        return grupos;
    }

}

