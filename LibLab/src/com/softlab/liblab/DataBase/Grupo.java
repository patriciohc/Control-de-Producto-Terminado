/*
 * Grupo.java
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

public class Grupo implements Registro
{
    private String nombre = null;
    private String tabla = null;
    private ArrayList<String> analisis;
    
    public Grupo(String nombre)
    {
        this(nombre,new ArrayList<String>());
    }
    
    public Grupo(String nombre, ArrayList<String> analisis)
    {
        this.nombre = nombre;
        this.tabla = nombreTabla(nombre);
        this.analisis = analisis;
    }

    @Override
    public boolean save(Connection c) throws SQLException {
        String query = String.format("INSERT INTO grupos(NOMBRE, TABLA) "
                + "VALUES('%s','%s')",nombre,tabla);
        new Sentence(query, c).exec();
        query = "INSERT INTO analisis(NOMBRE_GRUPO, ANALISIS, PRI) VALUES ";
        int pos = 0;
        String query1 = "CREATE TABLE IF NOT EXISTS "+tabla+"(ID_ANALISIS INT(11) NOT NULL, ";
        for (String a: analisis) {
            query1 = query1+a+" float DEFAULT NULL ,";
            query = query+String.format("('%s','%s', %d) ,",nombre,a, pos++);
        }
        query1 = query1+" PRIMARY KEY (`ID_ANALISIS`), FOREIGN KEY (`ID_ANALISIS`) "
                +"REFERENCES `laboratorio`.`registro` (`ID_ANALISIS`) "
                +"ON DELETE CASCADE ON UPDATE CASCADE)";
        query = query.substring(0,query.length()-2);   
        new Sentence(query, c).exec();
        new Sentence(query1,c).exec();
        return true;
    }
    
    @Override
    public boolean load(Connection c) throws SQLException {
        String query = String.format("SELECT NOMBRE, TABLA FROM grupos WHERE NOMBRE = '%s'",nombre);
        ResultSet rs;
        if ((rs = new Sentence(query, c).openExec()) == null) return false;
        if (rs.next()) {
            nombre = rs.getString("NOMBRE");
            tabla = rs.getString("TABLA");
        }
        if(!loadAnalisis(c)) return false;
        return true;    
    }
    
    public boolean delete(Connection c) throws SQLException
    {
        ResultSet rs =  new Sentence(String.format("SELECT COUNT(*) AS N "
                                                  +"FROM  %s ",nombreTabla(nombre)), c).openExec();
        if (!rs.next()) return false;
        if(rs.getInt("N") != 0) return false;
        String query =  String.format("DELETE FROM grupos WHERE NOMBRE='%s'",nombre);
        new Sentence(query, c).exec();
        /*eliminar tabla....*/
        query =  String.format("DROP TABLE %s",nombre);
        new Sentence(query, c).exec();
        return true;
    }
    
    private boolean loadAnalisis(Connection m_sC)throws SQLException
    {
        ResultSet rs;
        String query =  String.format("SELECT ANALISIS"
                                    +" FROM analisis"
                                    +" WHERE NOMBRE_GRUPO = '%s' ORDER BY PRI",nombre);
        if ((rs = new Sentence(query, m_sC).openExec()) == null) return false;
        while (rs.next()) {
            analisis.add(rs.getString("ANALISIS"));
        }
        return true;
    }

    public String getNombre()
    {
        return nombre;
    }

    public String getTabla()
    {
        return tabla;
    }

    public ArrayList<String> getAnalisis()
    {
        return analisis;
    }

    private String nombreTabla(String nombre)
    {
        nombre = nombre.replaceAll("\\s","").toLowerCase();
        return nombre.trim();
    }
 
}
