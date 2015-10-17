/*
 * Bitacora.java
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
import java.sql.SQLException;
import java.util.ArrayList;

public class Bitacora implements Registro
{
    
    private String query;
    private ArrayList<Entrada> es;
    private ProductList prods;
    
    public Bitacora(ProductList prods)
    {
        this.prods = prods;
        query =  "INSERT INTO registro(ID_ANALISIS, NOMBRE,LOTE,CANTIDAD,"
                + "CLIENTE_PROVEEDOR,CERTIFICADO,FECHA) VALUES ";
        es =  new ArrayList<Entrada>();
    }

    
    @Override
    public boolean save(Connection c) throws SQLException 
    {
        query = query.substring(0,query.length()-1);
        query = query   + "ON DUPLICATE KEY UPDATE NOMBRE=VALUES(NOMBRE), "
                        + "LOTE=VALUES(LOTE), CANTIDAD=VALUES(CANTIDAD), "
                        + "CLIENTE_PROVEEDOR=VALUES(CLIENTE_PROVEEDOR), "
                        + "CERTIFICADO=VALUES(CERTIFICADO), FECHA=(FECHA)";
        new Sentence(query,c).exec();
        String query1;
        for (Entrada e: es) {
            ArrayList<Grupo> grupos = prods.getProducto(e.nombre).getGrupos();
            for (Grupo g: grupos) {
                try {
                    query1 = String.format("INSERT INTO %s(ID_ANALISIS) VALUES(%d)",
                                        g.getTabla(),e.id);
                    new Sentence(query1,c).exec();
                } catch(SQLException ex) {}
            }
        }
        return true;
    }

    @Override
    public boolean load(Connection c) throws SQLException
    {
        //To change body of generated methods, choose Tools | Templates.
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
    public void addElement(int idAnalisis, String nombre, Fecha lote, 
                        String cantidad, String cliente, String certificado)
    {
        query = query + String.format("( %d,'%s','%s',%s,'%s',%s,'%s' ),"
                        ,idAnalisis,nombre,lote.getLote(),cantidad,cliente,
                        certificado,lote.getFormMySQL() );
        es.add(new Entrada(idAnalisis, nombre));
    }
    
    class Entrada
    {
        int id = 0;
        String nombre = "";
        
        public Entrada(int id, String nombre)
        {
            this.id = id;
            this.nombre = nombre;
        }
    }
    
}
