/*
 * Sentence.java
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

public class Sentence 
{

    private String query;
    private Connection m_sC;
    
    public Sentence(String query, Connection m_sC)
    {
        this.query  =  query;
        this.m_sC = m_sC;
    }
    
    public ResultSet openExec() throws SQLException
    {
        System.out.println("ejecutando: "+query);
        return m_sC.createStatement().executeQuery(query);
    }

    public boolean exec() throws SQLException
    {
        System.out.println("ejecutando: "+query);
        return m_sC.createStatement().execute(query);
    }
}
