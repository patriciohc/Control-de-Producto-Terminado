/*
 * Clientes.java
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

public class Clientes {

    public static boolean update(String nombre, String nnombre, Connection c) 
    throws SQLException
    {
        String query =  String.format("UPDATE clientes SET NOMBRE='%s' WHERE NOMBRE='%s'",
                                    nnombre,nombre);
        new Sentence(query, c).exec();
        return true;
    }

    public static boolean delete(String nombre, Connection c) 
    throws SQLException
    {
        String query =  String.format("DELETE FROM clientes WHERE NOMBRE='%s'",nombre);
        new Sentence(query, c).exec();
        return true;
    }

    // regresa un ResultSet con el nombre de todos los clientes en la tabla CLIENTES
    public static ResultSet getClientes(Connection c)
    throws SQLException
    {
        ResultSet r = null;
        r = new Sentence("SELECT NOMBRE FROM clientes ",c).openExec();
        return r;
    }
    
    // inserta el idAnalisis del producto indicado en las tablas que le correspondes
    public static void nuevoCliente(String nombre,Connection c)
    {
        try {
            String query = "INSERT INTO clientes(NOMBRE) VALUES('"+nombre+"')";
            new Sentence( query,c).exec();   
        } catch (SQLException ex) {
            System.out.println("nuevoCliente: "+ex);
        }
    }
    
}
