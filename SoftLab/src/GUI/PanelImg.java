/*
 * PanelImg.java
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

package GUI;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class PanelImg extends JPanel
{   
    private ImageIcon imagen;

    public PanelImg(String img)
    {
       //String curDir = System.getProperty("user.dir");
       //imagen = new ImageIcon(curDir+"/fondo.gif");
        imagen = new ImageIcon(img);
    }
    
    public void actImg(String imagen)
    {
        this.imagen = new ImageIcon(imagen);
        this.repaint();
    }

    public void actImg(ImageIcon imagen)
    {
        this.imagen = imagen;
        this.repaint();
    }

    public void paintComponent(Graphics g) 
    {
        Dimension tamanio=getSize(); 
        if(imagen == null) {
            String curDir = System.getProperty("user.dir");
            imagen = new ImageIcon(curDir+"/fondo.gif");
        }
        g.drawImage(imagen.getImage(), 0, 0, tamanio.width,tamanio.height,null);
        setOpaque(false);
        super.paintComponent(g); 
    }
    
}
