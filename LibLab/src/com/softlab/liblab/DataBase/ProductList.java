/*
 * ProductList.java
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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProductList
{
    
    private ArrayList<Producto> productos;
    //private ArrayList<String> nombres;
    
    public ProductList(){}
    
    public void load(Connection m_sC) throws SQLException
    {
        productos =  new ArrayList<Producto>();
        ResultSet rs,rs1;
        String nombre, clienteProveedor, tipo, fileJasper,nombreGrupo;
        int caducidad,id;
        String query =  String.format("SELECT ID, NOMBRE, CLIENTE_PROVEEDOR, "
                + "TIPO, CADUCIDAD, FILE_JASPER FROM producto");
        if ((rs = new Sentence(query, m_sC).openExec()) == null) 
            return;
        while (rs.next()) {
            ArrayList<String> grupos = new ArrayList<String>();
            id = rs.getInt("ID");
            nombre =  rs.getString("NOMBRE");
            clienteProveedor = rs.getString("CLIENTE_PROVEEDOR");
            tipo = rs.getString("TIPO");
            caducidad = rs.getInt("CADUCIDAD");
            fileJasper = rs.getString("FILE_JASPER");
            String query1 =  String.format("SELECT producto_grupo.NOMBRE_GRUPO "
                            +"FROM producto_grupo "
                            +"WHERE producto_grupo.NOMBRE_PRODUCTO = '%s'",nombre);
            if ((rs1 = new Sentence(query1, m_sC).openExec()) != null) {
                while(rs1.next())
                    grupos.add(rs1.getString("NOMBRE_GRUPO"));
            }
            Producto p =  new Producto(id,nombre, clienteProveedor, tipo, 
                    caducidad, grupos, fileJasper);
            p.loadGrupos(m_sC);
            productos.add(p);
        }
    }
    
    public String[] getNombres()
    {
        String []nombres =  new String[productos.size()];
        int i = 0;
        for (Producto p: productos)
            nombres[i++] = p.getNombre();
        return nombres;
    }
    
    public Producto getProducto(String nombre)
    {
        for(Producto p: productos)
            if (p.getNombre().equals(nombre.trim()))
                return p;
        return null;
    }
    
    public int size()
    {
        return productos.size();
    }
    
    public ArrayList<String>getPt(){return getPtMp("PT");} 
    public ArrayList<String>getMp(){return getPtMp("MP");}
    
    
    private ArrayList<String> getPtMp(String tipo)
    {
        ArrayList<String> pts = new ArrayList<String>();
        for(Producto p: productos)
            if(p.getTipo().equals(tipo)) pts.add(p.getNombre());
        return pts;      
    }

}
