#!/bin/bash

echo "Before to continue is necessary to have installed mysql"
read -p "Do you want to continue (Y/n): " continuar

if [ $continuar != "Y" ];
then
	exit
fi 

echo "Copying files..."
sudo mkdir /opt/softlab
sudo mkdir /opt/softlab/reportes

sudo cp -R image /opt/softlab/
sudo cp -R master_report /opt/softlab/
sudo cp -R SoftLab/dist/lib /opt/softlab/
sudo cp SoftLab/dist/SoftLab1.2.jar /opt/softlab/
sudo cp softlab /opt/softlab/

#sudo chown  
sudo chmod 755 /opt/softlab/master_report
sudo chmod 755 /opt/softlab/reportes
sudo chmod 555 /opt/softlab/SoftLab1.2.jar
sudo chmod 555 /opt/softlab/lib
sudo chmod 555 /opt/softlab/softlab

sudo ln -s /opt/softlab/reportes ~/
sudo ln -s /opt/softlab/softlab /usr/bin/softlab
echo "Copy finished"

echo "Creating Database..."
read -p "name user database: " user
mysql -u $user -p$passw < laboratorio.sql
if [ $? = 0 ]; then 
	echo "installation finished"
else
	echo "failure to creating database"
fi

